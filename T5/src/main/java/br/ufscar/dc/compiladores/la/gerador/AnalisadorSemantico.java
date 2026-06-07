package br.ufscar.dc.compiladores.la.gerador;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Analisador semântico da linguagem LA (T4).
 *
 * Recebe a árvore de derivação do parser e percorre declarações e comandos coletando erros:
 *   - Identificadores não declarados ou redeclarados (inclui colisão com tipo/sub-rotina)
 *   - Tipos, atribuições, ponteiros, registros e campos
 *   - Parâmetros em chamadas (quantidade, tipo, {@code var} com lvalue do mesmo tipo)
 *   - {@code retorne} só no corpo de função
 *
 * Os erros não interrompem a análise: a lista pode ser obtida por {@link #getErros()} ao final,
 * já ordenada por linha.
 *
 * Modelo interno de tipos (string canônica):
 *   - Tipos básicos: "literal", "inteiro", "real", "logico"
 *   - Registros:    "@reg:<id>" (id = nome do tipo ou anônimo "__anon_N")
 *   - Ponteiros:    prefixo "^" repetido (ex.: "^^inteiro")
 */
public final class AnalisadorSemantico {

    /** Erro semântico: linha e mensagem como devem aparecer na saída. */
    public record Erro(int linha, String mensagem) {}

    private static final List<String> TIPOS_BASICOS =
            List.of("literal", "inteiro", "real", "logico");

    private final List<Erro> erros = new ArrayList<>();
    private final Map<String, String> nomesTipoUsuario = new HashMap<>();
    private final Map<String, Map<String, String>> camposDoRegistro = new HashMap<>();
    /** Nome da sub-rotina → assinatura (parâmetros e tipo de retorno, se for função). */
    private final Map<String, AssinaturaSubrotina> subrotinas = new HashMap<>();

    private int idRegistroAnonimo = 0;
    private Deque<Map<String, String>> escopos = new ArrayDeque<>();
    private final Set<String> nomesDeclaracaoComTipoInvalido = new HashSet<>();
    /** Contexto atual para saber se {@code retorne} é válido. */
    private final Deque<ContextoRetorne> contextoRetorne = new ArrayDeque<>();

    private enum ContextoRetorne {
        PRINCIPAL,
        PROCEDIMENTO,
        FUNCAO
    }

    private static final class ParamInfo {
        final String tipoCanon;
        final boolean porReferencia;

        ParamInfo(String tipoCanon, boolean porReferencia) {
            this.tipoCanon = tipoCanon;
            this.porReferencia = porReferencia;
        }
    }

    private static final class AssinaturaSubrotina {
        final boolean procedimento;
        final String tipoRetorno;
        final List<ParamInfo> parametros;

        AssinaturaSubrotina(boolean procedimento, String tipoRetorno, List<ParamInfo> parametros) {
            this.procedimento = procedimento;
            this.tipoRetorno = tipoRetorno;
            this.parametros = parametros;
        }
    }

    public List<Erro> getErros() {
        return Collections.unmodifiableList(erros);
    }

    private void erro(int linha, String msg) {
        erros.add(new Erro(linha, msg));
    }

    public void analisar(LAParser.ProgramaContext ctx) {
        escopos.clear();
        nomesTipoUsuario.clear();
        camposDoRegistro.clear();
        subrotinas.clear();
        idRegistroAnonimo = 0;
        nomesDeclaracaoComTipoInvalido.clear();
        contextoRetorne.clear();

        escopos.push(new HashMap<>());
        for (LAParser.Declaracao_globalContext dg : ctx.declaracoes_globais().declaracao_global()) {
            processarDeclaracaoGlobal(dg);
        }

        LAParser.CorpoContext corpoPrincipal =
                ctx.secao_principal() instanceof LAParser.PrincipalComCabecalhoContext cab
                        ? cab.corpo()
                        : ((LAParser.PrincipalSemPalavraAlgoritmoContext) ctx.secao_principal())
                                .corpo();

        escopos.push(new HashMap<>());
        contextoRetorne.push(ContextoRetorne.PRINCIPAL);
        processarConteudoCorpo(corpoPrincipal);
        contextoRetorne.pop();
        escopos.pop();

        ordenarErros();
    }

    private void ordenarErros() {
        erros.sort((a, b) -> Integer.compare(a.linha, b.linha));
    }

    private void processarDeclaracaoGlobal(LAParser.Declaracao_globalContext dg) {
        if (dg.declaracao_tipo() != null) {
            registrarDeclaracaoTipo(dg.declaracao_tipo());
        } else if (dg.declaracao_constante_global() != null) {
            registrarConstanteGlobal(dg.declaracao_constante_global());
        } else if (dg.declaracao_procedimento() != null) {
            declararSubrotina(dg.declaracao_procedimento(), true);
        } else if (dg.declaracao_funcao() != null) {
            declararSubrotina(dg.declaracao_funcao(), false);
        }
    }

    private void registrarDeclaracaoTipo(LAParser.Declaracao_tipoContext d) {
        String nomeTipo = textoIdent(d.ident());
        Token tok = d.ident().IDENT().getSymbol();
        if (nomesTipoUsuario.containsKey(nomeTipo)) {
            erro(tok.getLine(), "identificador " + nomeTipo + " ja declarado anteriormente");
            return;
        }
        if (subrotinas.containsKey(nomeTipo) || nomeOcupadoEmQualquerEscopoVariavel(nomeTipo)) {
            erro(tok.getLine(), "identificador " + nomeTipo + " ja declarado anteriormente");
            return;
        }
        String idReg = nomeTipo;
        nomesTipoUsuario.put(nomeTipo, chaveRegistro(idReg));
        registrarCamposEmRegistro(idReg, d.lista_campos_registro());
    }

    private void registrarConstanteGlobal(LAParser.Declaracao_constante_globalContext c) {
        String nome = textoIdent(c.ident());
        Token idTok = c.ident().IDENT().getSymbol();
        String tb = nomeTipoBasico(c.tipo_basico());
        String tv = tipoValorConstante(c.valor_constante());
        if (!declararSimboloNoEscopoAtual(idTok, tb)) {
            return;
        }
        if (!tiposIguais(tb, tv)) {
            // Sem mensagem padronizada nos casos de teste
        }
    }

    private void declararSubrotina(ParserRuleContext decl, boolean procedimento) {
        final Token nomeTok;
        final LAParser.Lista_parametrosContext params;
        final LAParser.CorpoContext corpo;
        final LAParser.Tipo_estendidoContext tipoRet;
        if (procedimento) {
            var p = (LAParser.Declaracao_procedimentoContext) decl;
            nomeTok = p.ident().IDENT().getSymbol();
            params = p.lista_parametros();
            corpo = p.corpo();
            tipoRet = null;
        } else {
            var f = (LAParser.Declaracao_funcaoContext) decl;
            nomeTok = f.ident().IDENT().getSymbol();
            params = f.lista_parametros();
            corpo = f.corpo();
            tipoRet = f.tipo_estendido();
        }
        String nome = nomeTok.getText();
        if (subrotinas.containsKey(nome)
                || nomesTipoUsuario.containsKey(nome)
                || nomeOcupadoEmQualquerEscopoVariavel(nome)) {
            erro(nomeTok.getLine(), "identificador " + nome + " ja declarado anteriormente");
            return;
        }

        List<ParamInfo> listaParam = extrairParametrosComEstado(params);
        String tr = null;
        if (!procedimento) {
            tr = expandirTipoEstendido(tipoRet);
        }
        subrotinas.put(nome, new AssinaturaSubrotina(procedimento, tr, listaParam));

        escopos.push(new HashMap<>());
        if (params != null) {
            for (LAParser.ParametroContext par : params.parametro()) {
                boolean porRef = par.VAR() != null;
                String tParam = expandirTipoEstendido(par.tipo_estendido());
                if (tParam == null) {
                    for (LAParser.Ident_dimContext idc : par.lista_ident_dim().ident_dim()) {
                        nomesDeclaracaoComTipoInvalido.add(textoIdent(idc.ident()));
                    }
                    continue;
                }
                for (LAParser.Ident_dimContext idc : par.lista_ident_dim().ident_dim()) {
                    validarExpressoesDimensoes(idc);
                    Token id = idc.ident().IDENT().getSymbol();
                    declararSimboloNoEscopoAtual(id, tParam);
                }
            }
        }
        contextoRetorne.push(procedimento ? ContextoRetorne.PROCEDIMENTO : ContextoRetorne.FUNCAO);
        processarConteudoCorpo(corpo);
        contextoRetorne.pop();
        escopos.pop();
    }

    private List<ParamInfo> extrairParametrosComEstado(LAParser.Lista_parametrosContext params) {
        List<ParamInfo> lista = new ArrayList<>();
        if (params == null) {
            return lista;
        }
        for (LAParser.ParametroContext par : params.parametro()) {
            boolean porRef = par.VAR() != null;
            String tParam = expandirTipoEstendido(par.tipo_estendido());
            if (tParam == null) {
                continue;
            }
            for (int i = 0; i < par.lista_ident_dim().ident_dim().size(); i++) {
                lista.add(new ParamInfo(tParam, porRef));
            }
        }
        return lista;
    }

    private void processarConteudoCorpo(LAParser.CorpoContext corpo) {
        for (int i = 0; i < corpo.getChildCount(); i++) {
            if (corpo.getChild(i) instanceof LAParser.Declaracao_localContext dl) {
                processarDeclaracaoLocal(dl);
            }
        }
        for (int i = 0; i < corpo.getChildCount(); i++) {
            if (corpo.getChild(i) instanceof LAParser.ComandoContext cmd) {
                processarComando(cmd);
            }
        }
    }

    private void processarDeclaracaoLocal(LAParser.Declaracao_localContext dl) {
        if (dl.DECLARE() != null) {
            for (LAParser.Variavel_declContext vd : dl.variavel_decl()) {
                String t = expandirTipoEstendido(vd.tipo_estendido());
                if (t == null) {
                    for (LAParser.Ident_dimContext idc : vd.lista_ident_dim().ident_dim()) {
                        nomesDeclaracaoComTipoInvalido.add(textoIdent(idc.ident()));
                    }
                    continue;
                }
                for (LAParser.Ident_dimContext idc : vd.lista_ident_dim().ident_dim()) {
                    validarExpressoesDimensoes(idc);
                    Token id = idc.ident().IDENT().getSymbol();
                    declararSimboloNoEscopoAtual(id, t);
                }
            }
        } else if (dl.CONSTANTE() != null) {
            String nome = textoIdent(dl.ident());
            Token idTok = dl.ident().IDENT().getSymbol();
            String tb = nomeTipoBasico(dl.tipo_basico());
            if (declararSimboloNoEscopoAtual(idTok, tb)) {
                tipoValorConstante(dl.valor_constante());
            }
        } else if (dl.TIPO() != null) {
            String nomeAlias = textoIdent(dl.ident());
            Token idTok = dl.ident().IDENT().getSymbol();
            if (nomesTipoUsuario.containsKey(nomeAlias)) {
                erro(idTok.getLine(), "identificador " + nomeAlias + " ja declarado anteriormente");
                return;
            }
            if (subrotinas.containsKey(nomeAlias) || nomeOcupadoEmQualquerEscopoVariavel(nomeAlias)) {
                erro(idTok.getLine(), "identificador " + nomeAlias + " ja declarado anteriormente");
                return;
            }
            String expandido = expandirTipoCorpoTipo(dl.tipo_corpo_tipo());
            if (expandido != null) {
                nomesTipoUsuario.put(nomeAlias, expandido);
            }
        }
    }

    private String expandirTipoCorpoTipo(LAParser.Tipo_corpo_tipoContext ctx) {
        if (ctx.REGISTRO() != null) {
            String idReg = proximoRegistroAnonimo();
            nomesTipoUsuario.put(idReg, chaveRegistro(idReg));
            registrarCamposEmRegistro(idReg, ctx.lista_campos_registro());
            return chaveRegistro(idReg);
        }
        return expandirTipoEstendidoSimples(ctx.tipo_estendido_simples());
    }

    private void processarComando(LAParser.ComandoContext ctx) {
        if (ctx.comando_atribuicao() != null) {
            comandoAtribuicao(ctx.comando_atribuicao());
        } else if (ctx.comando_leia() != null) {
            comandoLeia(ctx.comando_leia());
        } else if (ctx.comando_escreva() != null) {
            comandoEscreva(ctx.comando_escreva());
        } else if (ctx.comando_chamada() != null) {
            comandoChamadaProc(ctx.comando_chamada());
        } else if (ctx.comando_se() != null) {
            comandoSe(ctx.comando_se());
        } else if (ctx.comando_caso() != null) {
            comandoCaso(ctx.comando_caso());
        } else if (ctx.comando_para() != null) {
            comandoPara(ctx.comando_para());
        } else if (ctx.comando_enquanto() != null) {
            comandoEnquanto(ctx.comando_enquanto());
        } else if (ctx.comando_faca_ate() != null) {
            comandoFacaAte(ctx.comando_faca_ate());
        } else if (ctx.RETORNE() != null) {
            if (ctx.expressao() != null) {
                tipoExpressao(ctx.expressao());
            }
            ContextoRetorne cr = contextoRetorne.peek();
            if (cr != ContextoRetorne.FUNCAO) {
                erro(ctx.RETORNE().getSymbol().getLine(), "comando retorne nao permitido nesse escopo");
            }
        }
    }

    /**
     * Verifica compatibilidade de tipos em "lvalue <- expressao" e emite
     * "atribuicao nao compativel para X" quando não há compatibilidade.
     */
    private void comandoAtribuicao(LAParser.Comando_atribuicaoContext ctx) {
        String tAlvo = tipoParaAtribuicaoLvalor(ctx.lvalue());
        String tExpr = tipoExpressao(ctx.expressao());
        if (tAlvo != null && !compativelAtribuicao(tAlvo, tExpr)) {
            erro(ctx.start.getLine(), "atribuicao nao compativel para " + textoLvalue(ctx.lvalue()));
        }
    }

    private void comandoLeia(LAParser.Comando_leiaContext ctx) {
        for (LAParser.LvalueContext lv : ctx.lista_lvalue().lvalue()) {
            tipoParaAtribuicaoLvalor(lv);
        }
    }

    private void comandoEscreva(LAParser.Comando_escrevaContext ctx) {
        for (LAParser.ExpressaoContext ex : ctx.lista_expressoes().expressao()) {
            tipoExpressao(ex);
        }
    }

    private void comandoChamadaProc(LAParser.Comando_chamadaContext ctx) {
        String nome = textoIdent(ctx.ident());
        Token t = ctx.ident().IDENT().getSymbol();
        AssinaturaSubrotina ass = subrotinas.get(nome);
        if (ass == null) {
            if (buscarTipoVariavelSilenciosa(nome) == null) {
                erro(t.getLine(), "identificador " + nome + " nao declarado");
            }
            return;
        }
        validarChamadaSubrotina(t, nome, ass, ctx.lista_expressoes());
    }

    private void comandoSe(LAParser.Comando_seContext ctx) {
        tipoExpressao(ctx.expressao());
        LAParser.CorpoContext entao = ctx.corpo(0);
        escopos.push(new HashMap<>());
        processarConteudoCorpo(entao);
        escopos.pop();
        if (ctx.SENAO() != null && ctx.corpo().size() > 1) {
            escopos.push(new HashMap<>());
            processarConteudoCorpo(ctx.corpo(1));
            escopos.pop();
        }
    }

    private void comandoCaso(LAParser.Comando_casoContext ctx) {
        tipoExpressao(ctx.expressao());
        for (LAParser.Item_casoContext it : ctx.item_caso()) {
            escopos.push(new HashMap<>());
            processarConteudoCorpo(it.corpo());
            escopos.pop();
        }
        if (ctx.SENAO() != null && ctx.corpo() != null) {
            escopos.push(new HashMap<>());
            processarConteudoCorpo(ctx.corpo());
            escopos.pop();
        }
    }

    private void comandoPara(LAParser.Comando_paraContext ctx) {
        tipoExpressao(ctx.expressao(0));
        tipoExpressao(ctx.expressao(1));
        escopos.push(new HashMap<>());
        processarConteudoCorpo(ctx.corpo());
        escopos.pop();
    }

    private void comandoEnquanto(LAParser.Comando_enquantoContext ctx) {
        tipoExpressao(ctx.expressao());
        escopos.push(new HashMap<>());
        processarConteudoCorpo(ctx.corpo());
        escopos.pop();
    }

    private void comandoFacaAte(LAParser.Comando_faca_ateContext ctx) {
        escopos.push(new HashMap<>());
        processarConteudoCorpo(ctx.corpo());
        escopos.pop();
        tipoExpressao(ctx.expressao());
    }

    private void validarChamadaSubrotina(
            Token nomeTok, String nome, AssinaturaSubrotina ass, LAParser.Lista_expressoesContext listaArgs) {
        List<LAParser.ExpressaoContext> args =
                listaArgs == null ? List.of() : listaArgs.expressao();
        if (args.size() != ass.parametros.size()) {
            erro(nomeTok.getLine(), "incompatibilidade de parametros na chamada de " + nome);
            for (LAParser.ExpressaoContext ex : args) {
                tipoExpressao(ex);
            }
            return;
        }
        String[] tipos = new String[args.size()];
        for (int i = 0; i < args.size(); i++) {
            tipos[i] = tipoExpressao(args.get(i));
        }
        boolean ok = true;
        for (int i = 0; i < args.size(); i++) {
            if (!parametroCompativel(ass.parametros.get(i), tipos[i], args.get(i))) {
                ok = false;
                break;
            }
        }
        if (!ok) {
            erro(nomeTok.getLine(), "incompatibilidade de parametros na chamada de " + nome);
        }
    }

    private boolean parametroCompativel(ParamInfo formal, String tipoExpr, LAParser.ExpressaoContext expr) {
        if (formal.porReferencia) {
            if (!expressaoEhLvalue(expr)) {
                return false;
            }
            String tLv = tipoDeExpressaoLvalue(expr);
            return tLv != null && tiposIguais(formal.tipoCanon, tLv);
        }
        return tiposCompativeisParametroPorValor(formal.tipoCanon, tipoExpr);
    }

    /** Parâmetro por valor: tipos iguais (sem promoção inteiro → real). */
    private static boolean tiposCompativeisParametroPorValor(String formal, String atual) {
        if (formal == null || atual == null) {
            return false;
        }
        return formal.equals(atual);
    }

    private static boolean expressaoEhLvalue(LAParser.ExpressaoContext ctx) {
        if (ctx.expressao() != null) {
            return false;
        }
        return exprEEhLvalue(ctx.expr_e());
    }

    private static boolean exprEEhLvalue(LAParser.Expr_eContext ctx) {
        if (ctx.expr_e() != null) {
            return false;
        }
        return exprRelEhLvalue(ctx.expr_rel());
    }

    private static boolean exprRelEhLvalue(LAParser.Expr_relContext ctx) {
        if (ctx.op != null) {
            return false;
        }
        return exprAritEhLvalue(ctx.expr_arit());
    }

    private static boolean exprAritEhLvalue(LAParser.Expr_aritContext ctx) {
        if (ctx.getChildCount() != 1) {
            return false;
        }
        return termoEhLvalue(ctx.termo());
    }

    private static boolean termoEhLvalue(LAParser.TermoContext ctx) {
        if (ctx.getChildCount() != 1) {
            return false;
        }
        return unarioEhLvalue(ctx.unario());
    }

    private static boolean unarioEhLvalue(LAParser.UnarioContext ctx) {
        if (ctx.MENOS() != null || ctx.NAO() != null) {
            return false;
        }
        return fatorEhLvalueVariavel(ctx.fator());
    }

    /** Indica fator que é só um lvalue (caminho de identificador, sem chamada). */
    private static boolean fatorEhLvalueVariavel(LAParser.FatorContext ctx) {
        if (ctx.ident().isEmpty()) {
            return false;
        }
        if (ctx.ABRE_PAR() != null) {
            return false;
        }
        ParseTree p0 = ctx.getChild(0);
        if (!(p0 instanceof LAParser.IdentContext)) {
            return false;
        }
        return true;
    }

    private String tipoDeExpressaoLvalue(LAParser.ExpressaoContext ctx) {
        LAParser.FatorContext f = fatorUnicoDaExpressao(ctx);
        if (f == null) {
            return null;
        }
        return tipoFatorIdentComSufixos(f);
    }

    private LAParser.FatorContext fatorUnicoDaExpressao(LAParser.ExpressaoContext ctx) {
        if (ctx.expressao() != null) {
            return null;
        }
        LAParser.Expr_eContext ee = ctx.expr_e();
        if (ee.expr_e() != null) {
            return null;
        }
        LAParser.Expr_relContext er = ee.expr_rel();
        if (er.op != null) {
            return null;
        }
        LAParser.Expr_aritContext ea = er.expr_arit();
        if (ea.getChildCount() != 1) {
            return null;
        }
        LAParser.TermoContext tm = ea.termo();
        if (tm.getChildCount() != 1) {
            return null;
        }
        LAParser.UnarioContext u = tm.unario();
        if (u.MENOS() != null || u.NAO() != null) {
            return null;
        }
        return u.fator();
    }

    private String tipoFatorIdentComSufixos(LAParser.FatorContext ctx) {
        LAParser.IdentContext primeiro = ctx.ident(0);
        Token id0Tok = primeiro.IDENT().getSymbol();
        String tipo = buscarTipoVariavel(id0Tok.getText(), id0Tok);
        if (tipo == null) {
            return null;
        }
        return aplicarSufixoTipoVariavel(ctx, id0Tok, tipo);
    }

    private static String chaveRegistro(String idInterno) {
        return "@reg:" + idInterno;
    }

    private String proximoRegistroAnonimo() {
        return "__anon_" + (++idRegistroAnonimo);
    }

    private void registrarCamposEmRegistro(String idRegistro, LAParser.Lista_campos_registroContext lista) {
        Map<String, String> cmap =
                camposDoRegistro.computeIfAbsent(idRegistro, k -> new LinkedHashMap<>());
        var blocos = lista.lista_ident_dim();
        var tipos = lista.tipo_estendido();
        for (int i = 0; i < blocos.size(); i++) {
            LAParser.Lista_ident_dimContext lid = blocos.get(i);
            LAParser.Tipo_estendidoContext te = tipos.get(i);
            String tCampo = expandirTipoEstendido(te);
            if (tCampo == null) {
                continue;
            }
            for (LAParser.Ident_dimContext idc : lid.ident_dim()) {
                Token idCampo = idc.ident().IDENT().getSymbol();
                String nc = idCampo.getText();
                if (cmap.containsKey(nc)) {
                    erro(idCampo.getLine(), "identificador " + nc + " ja declarado anteriormente");
                } else {
                    cmap.put(nc, tCampo);
                }
                validarExpressoesDimensoes(idc);
            }
        }
    }

    private void validarExpressoesDimensoes(LAParser.Ident_dimContext idc) {
        for (LAParser.ExpressaoContext ex : idc.expressao()) {
            tipoExpressao(ex);
        }
    }

    private String nomeTipoBasico(LAParser.Tipo_basicoContext ctx) {
        return ctx.start.getText().toLowerCase();
    }

    private String expandirTipoEstendido(LAParser.Tipo_estendidoContext ctx) {
        if (ctx.REGISTRO() != null) {
            String id = proximoRegistroAnonimo();
            nomesTipoUsuario.put(id, chaveRegistro(id));
            registrarCamposEmRegistro(id, ctx.lista_campos_registro());
            return chaveRegistro(id);
        }
        return expandirTipoEstendidoSimples(ctx.tipo_estendido_simples());
    }

    private String expandirTipoEstendidoSimples(LAParser.Tipo_estendido_simplesContext ctx) {
        boolean ptr = ctx.CIRCUNFLEXO() != null;
        final String canon;
        if (ctx.tipo_basico() != null) {
            canon = nomeTipoBasico(ctx.tipo_basico());
        } else {
            String nomeTipo = textoIdent(ctx.ident());
            Token sym = ctx.ident().IDENT().getSymbol();
            String r = resolverNomeDeclaradoOuErro(nomeTipo, sym);
            if (r == null) {
                return null;
            }
            canon = r;
        }
        return ptr ? ("^" + canon) : canon;
    }

    private String resolverNomeDeclaradoOuErro(String nome, Token onde) {
        if (TIPOS_BASICOS.contains(nome)) {
            return nome;
        }
        if (!nomesTipoUsuario.containsKey(nome)) {
            erro(onde.getLine(), "tipo " + nome + " nao declarado");
            return null;
        }
        return nomesTipoUsuario.get(nome);
    }

    private boolean nomeOcupadoEmQualquerEscopoVariavel(String nome) {
        for (Map<String, String> m : escopos) {
            if (m.containsKey(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Declara variável ou constante no escopo atual. Impede redeclaração local, sombreamento
     * de escopo externo e nome igual a tipo ou sub-rotina.
     */
    private boolean declararSimboloNoEscopoAtual(Token idTok, String tipo) {
        String nome = idTok.getText();
        if (nomesTipoUsuario.containsKey(nome) || subrotinas.containsKey(nome)) {
            erro(idTok.getLine(), "identificador " + nome + " ja declarado anteriormente");
            return false;
        }
        Iterator<Map<String, String>> it = escopos.iterator();
        Map<String, String> topo = it.next();
        if (topo.containsKey(nome)) {
            erro(idTok.getLine(), "identificador " + nome + " ja declarado anteriormente");
            return false;
        }
        while (it.hasNext()) {
            if (it.next().containsKey(nome)) {
                erro(idTok.getLine(), "identificador " + nome + " ja declarado anteriormente");
                return false;
            }
        }
        topo.put(nome, tipo);
        return true;
    }

    private String buscarTipoVariavel(String nome, Token uso) {
        for (Map<String, String> m : escopos) {
            if (m.containsKey(nome)) {
                return m.get(nome);
            }
        }
        if (nomesDeclaracaoComTipoInvalido.contains(nome)) {
            return null;
        }
        erro(uso.getLine(), "identificador " + nome + " nao declarado");
        return null;
    }

    private String buscarTipoVariavelSilenciosa(String nome) {
        for (Map<String, String> m : escopos) {
            if (m.containsKey(nome)) {
                return m.get(nome);
            }
        }
        return null;
    }

    private String tipoParaAtribuicaoLvalor(LAParser.LvalueContext lv) {
        int idx = 0;
        if (lv.getChild(idx) instanceof TerminalNode tn
                && tn.getSymbol().getType() == LAParser.CIRCUNFLEXO) {
            idx++;
        }
        LAParser.IdentContext idBase = (LAParser.IdentContext) lv.getChild(idx++);
        Token idTok = idBase.IDENT().getSymbol();
        String nomeBase = idTok.getText();
        String tipo = buscarTipoVariavelParaLvalue(lv, nomeBase, idTok);
        if (tipo == null) {
            return null;
        }
        if (lv.CIRCUNFLEXO() != null) {
            tipo = tirarUmPont(tipo);
        }
        String prefixo = nomeBase;
        while (idx < lv.getChildCount()) {
            ParseTree ch = lv.getChild(idx++);
            if (!(ch instanceof TerminalNode tnx)) {
                break;
            }
            int tt = tnx.getSymbol().getType();
            if (tt == LAParser.PONTO) {
                LAParser.IdentContext ic = (LAParser.IdentContext) lv.getChild(idx++);
                String nomeCampo = textoIdent(ic);
                tipo = campoDeRegistro(tipo, nomeCampo, ic.IDENT().getSymbol(), prefixo);
                prefixo = prefixo + "." + nomeCampo;
            } else if (tt == LAParser.ABRE_COL) {
                LAParser.ExpressaoContext ex = (LAParser.ExpressaoContext) lv.getChild(idx++);
                tipoExpressao(ex);
                idx++;
                tipo = tipoElementoMatrizOuErro(tipo, idTok);
            } else {
                break;
            }
            if (tipo == null) {
                return null;
            }
        }
        return tipo;
    }

    private String buscarTipoVariavelParaLvalue(LAParser.LvalueContext lv, String nomeBase, Token idTok) {
        for (Map<String, String> m : escopos) {
            if (m.containsKey(nomeBase)) {
                return m.get(nomeBase);
            }
        }
        if (nomesDeclaracaoComTipoInvalido.contains(nomeBase)) {
            return null;
        }
        erro(idTok.getLine(), "identificador " + textoLvalue(lv) + " nao declarado");
        return null;
    }

    private static String tirarUmPont(String tipoVar) {
        if (tipoVar != null && tipoVar.startsWith("^")) {
            return tipoVar.substring(1);
        }
        return tipoVar;
    }

    private String tipoElementoMatrizOuErro(String tipoVariavelBase, Token ref) {
        if (tipoVariavelBase.startsWith("^")) {
            return tipoVariavelBase.substring(1);
        }
        return tipoVariavelBase;
    }

    private String campoDeRegistro(String tipoValor, String nomeCampo, Token ref, String prefixo) {
        String idReg = extrairIdRegistro(tipoValor);
        String caminhoErro = prefixo + "." + nomeCampo;
        if (idReg == null) {
            erro(ref.getLine(), "identificador " + caminhoErro + " nao declarado");
            return null;
        }
        Map<String, String> cmap = camposDoRegistro.get(idReg);
        if (cmap == null || !cmap.containsKey(nomeCampo)) {
            erro(ref.getLine(), "identificador " + caminhoErro + " nao declarado");
            return null;
        }
        return cmap.get(nomeCampo);
    }

    private static String extrairIdRegistro(String tipo) {
        if (tipo != null && tipo.startsWith("@reg:")) {
            return tipo.substring("@reg:".length());
        }
        return null;
    }

    private String tipoExpressao(LAParser.ExpressaoContext ctx) {
        if (ctx.expressao() != null) {
            String t1 = tipoExpressao(ctx.expressao());
            String t2 = tipoExprE(ctx.expr_e());
            return combinarOu(t1, t2);
        }
        return tipoExprE(ctx.expr_e());
    }

    private static String combinarOu(String a, String b) {
        if (algumInvalido(a, b)) {
            return null;
        }
        if ("logico".equals(a) && "logico".equals(b)) {
            return "logico";
        }
        return null;
    }

    private static boolean algumInvalido(String a, String b) {
        return a == null || b == null;
    }

    private String tipoExprE(LAParser.Expr_eContext ctx) {
        if (ctx.expr_e() != null) {
            String t1 = tipoExprE(ctx.expr_e());
            String t2 = tipoExprRel(ctx.expr_rel());
            return combinarE(t1, t2);
        }
        return tipoExprRel(ctx.expr_rel());
    }

    private static String combinarE(String a, String b) {
        if (algumInvalido(a, b)) {
            return null;
        }
        if ("logico".equals(a) && "logico".equals(b)) {
            return "logico";
        }
        return null;
    }

    private String tipoExprRel(LAParser.Expr_relContext ctx) {
        if (ctx.op == null) {
            return tipoExprArit(ctx.expr_arit());
        }
        String tl = tipoExprRel(ctx.expr_rel());
        String tr = tipoExprArit(ctx.expr_arit());
        Token op = ctx.op;
        if (tl == null || tr == null) {
            return null;
        }
        if (!comparavelRelacional(tl, tr, op.getType())) {
            return null;
        }
        return "logico";
    }

    private boolean comparavelRelacional(String a, String b, int opTipo) {
        if (TIPOS_BASICOS.contains(a)) {
            a = tirarPonteirosRecursivo(a);
        }
        if (TIPOS_BASICOS.contains(b)) {
            b = tirarPonteirosRecursivo(b);
        }
        boolean numA = "inteiro".equals(a) || "real".equals(a);
        boolean numB = "inteiro".equals(b) || "real".equals(b);
        if (numA && numB) {
            return true;
        }
        if ("literal".equals(a) && "literal".equals(b)) {
            return true;
        }
        if ("logico".equals(a) && "logico".equals(b)) {
            return opTipo == LAParser.IGUAL || opTipo == LAParser.DIFERENTE;
        }
        return false;
    }

    private static String tirarPonteirosRecursivo(String tipo) {
        String t = tipo;
        while (t != null && t.startsWith("^")) {
            t = t.substring(1);
        }
        return t;
    }

    private String tipoExprArit(LAParser.Expr_aritContext ctx) {
        if (ctx.getChildCount() == 1) {
            return tipoTermo(ctx.termo());
        }
        String tl = tipoExprArit(ctx.expr_arit());
        String tr = tipoTermo(ctx.termo());
        if (tl == null || tr == null) {
            return null;
        }
        return combinarMaisMenos(ctx.op.getText(), tl, tr);
    }

    private String combinarMaisMenos(String op, String a, String b) {
        if ("+".equals(op)) {
            return somaTipos(a, b);
        }
        return menosTipos(a, b);
    }

    private String somaTipos(String a, String b) {
        if ("literal".equals(a) && "literal".equals(b)) {
            return "literal";
        }
        boolean na = ehNumericoOuPonteirosNumericos(a);
        boolean nb = ehNumericoOuPonteirosNumericos(b);
        if (na && nb) {
            if ("real".equals(a) || "real".equals(b)) {
                return "real";
            }
            return "inteiro";
        }
        return null;
    }

    private String menosTipos(String a, String b) {
        boolean na = ehNumericoOuPonteirosNumericos(a);
        boolean nb = ehNumericoOuPonteirosNumericos(b);
        if (na && nb) {
            if ("real".equals(a) || "real".equals(b)) {
                return "real";
            }
            return "inteiro";
        }
        return null;
    }

    private boolean ehNumericoOuPonteirosNumericos(String t) {
        String u = tirarPonteirosRecursivo(t);
        return "inteiro".equals(u) || "real".equals(u);
    }

    private String tipoTermo(LAParser.TermoContext ctx) {
        if (ctx.getChildCount() == 1) {
            return tipoUnario(ctx.unario());
        }
        String tl = tipoTermo(ctx.termo());
        String tr = tipoUnario(ctx.unario());
        if (tl == null || tr == null) {
            return null;
        }
        return combinarMulDivMod(ctx, tl, tr);
    }

    private String combinarMulDivMod(LAParser.TermoContext ctx, String a, String b) {
        int op = ctx.op.getType();
        boolean na = ehNumericoOuPonteirosNumericos(a);
        boolean nb = ehNumericoOuPonteirosNumericos(b);
        if (!na || !nb) {
            return null;
        }
        if (op == LAParser.MOD) {
            String ua = tirarPonteirosRecursivo(a);
            String ub = tirarPonteirosRecursivo(b);
            if ("inteiro".equals(ua) && "inteiro".equals(ub)) {
                return "inteiro";
            }
            return null;
        }
        if ("real".equals(tirarPonteirosRecursivo(a))
                || "real".equals(tirarPonteirosRecursivo(b))) {
            return "real";
        }
        return "inteiro";
    }

    private String tipoUnario(LAParser.UnarioContext ctx) {
        if (ctx.MENOS() != null) {
            String ti = tipoUnario(ctx.unario());
            if (ti != null && ehNumericoOuPonteirosNumericos(ti)) {
                return ti;
            }
            return null;
        }
        if (ctx.NAO() != null) {
            String ti = tipoUnario(ctx.unario());
            return "logico".equals(ti) ? "logico" : null;
        }
        return tipoFator(ctx.fator());
    }

    private String tipoFator(LAParser.FatorContext ctx) {
        ParseTree p0 = ctx.getChild(0);
        if (p0 instanceof TerminalNode tn0) {
            int sym = tn0.getSymbol().getType();
            if (sym == LAParser.ABRE_PAR) {
                return tipoExpressao(ctx.expressao(0));
            }
            if (sym == LAParser.NUM_INT) {
                return "inteiro";
            }
            if (sym == LAParser.NUM_REAL) {
                return "real";
            }
            if (sym == LAParser.CADEIA) {
                return "literal";
            }
            if (sym == LAParser.VERDADEIRO || sym == LAParser.FALSO) {
                return "logico";
            }
            if (sym == LAParser.E_COMERCIAL) {
                String nome = textoIdent(ctx.ident(0));
                Token t = ctx.ident(0).IDENT().getSymbol();
                String vt = buscarTipoVariavel(nome, t);
                if (vt == null) {
                    return null;
                }
                return "^" + vt;
            }
            if (sym == LAParser.CIRCUNFLEXO) {
                String nome = textoIdent(ctx.ident(0));
                Token t = ctx.ident(0).IDENT().getSymbol();
                String vt = buscarTipoVariavel(nome, t);
                if (vt == null) {
                    return null;
                }
                if (!vt.startsWith("^")) {
                    return null;
                }
                return vt.substring(1);
            }
        }

        if (p0 instanceof LAParser.IdentContext primeiro) {
            if (ctx.ABRE_PAR() != null && ctx.ident().size() == 1) {
                Token t = primeiro.IDENT().getSymbol();
                String nomeFun = textoIdent(primeiro);
                AssinaturaSubrotina ass = subrotinas.get(nomeFun);
                if (ass == null || ass.procedimento) {
                    erro(t.getLine(), "identificador " + nomeFun + " nao declarado");
                    if (ctx.lista_expressoes() != null) {
                        for (LAParser.ExpressaoContext ex : ctx.lista_expressoes().expressao()) {
                            tipoExpressao(ex);
                        }
                    }
                    return null;
                }
                validarChamadaSubrotina(t, nomeFun, ass, ctx.lista_expressoes());
                return ass.tipoRetorno;
            }

            Token id0Tok = primeiro.IDENT().getSymbol();
            String tipo = buscarTipoVariavelParaFator(ctx, textoIdent(primeiro), id0Tok);
            if (tipo == null) {
                return null;
            }
            return aplicarSufixoTipoVariavel(ctx, id0Tok, tipo);
        }

        return null;
    }

    private String buscarTipoVariavelParaFator(LAParser.FatorContext ctx, String nomeBase, Token idTok) {
        for (Map<String, String> m : escopos) {
            if (m.containsKey(nomeBase)) {
                return m.get(nomeBase);
            }
        }
        if (nomesDeclaracaoComTipoInvalido.contains(nomeBase)) {
            return null;
        }
        erro(idTok.getLine(), "identificador " + textoFatorCaminho(ctx) + " nao declarado");
        return null;
    }

    private String aplicarSufixoTipoVariavel(LAParser.FatorContext ctx, Token refBase, String tipoIni) {
        String tipoCorrente = tipoIni;
        int ids = 1;
        int ib = 0;
        String prefixo = refBase.getText();
        while (ib < ctx.ABRE_COL().size() || ids < ctx.ident().size()) {
            Integer bid =
                    ib < ctx.ABRE_COL().size() ? ctx.ABRE_COL(ib).getSymbol().getStartIndex() : null;
            Integer iid =
                    ids < ctx.ident().size()
                            ? ctx.ident(ids).IDENT().getSymbol().getStartIndex()
                            : null;
            boolean fieldNow = iid != null && (bid == null || iid < bid);
            if (fieldNow) {
                LAParser.IdentContext ic = ctx.ident(ids++);
                String nomeCampo = textoIdent(ic);
                tipoCorrente =
                        campoDeRegistro(tipoCorrente, nomeCampo, ic.IDENT().getSymbol(), prefixo);
                prefixo = prefixo + "." + nomeCampo;
            } else if (bid != null) {
                tipoExpressao(ctx.expressao(ib++));
                tipoCorrente = tipoElementoMatrizOuErro(tipoCorrente, refBase);
            } else {
                break;
            }
            if (tipoCorrente == null) {
                return null;
            }
        }
        return tipoCorrente;
    }

    private static String tipoValorConstante(LAParser.Valor_constanteContext ctx) {
        if (ctx.CADEIA() != null) {
            return "literal";
        }
        if (ctx.NUM_INT() != null) {
            return "inteiro";
        }
        if (ctx.NUM_REAL() != null) {
            return "real";
        }
        if (ctx.VERDADEIRO() != null || ctx.FALSO() != null) {
            return "logico";
        }
        return null;
    }

    private static boolean tiposIguais(String a, String b) {
        return Objects.equals(a, b);
    }

    /**
     * Compatibilidade para "alvo <- expr": mesmo tipo; real := inteiro; ponteiros e registros só
     * com tipo canônico idêntico.
     */
    private static boolean compativelAtribuicao(String alvo, String expr) {
        if (alvo == null || expr == null) {
            return false;
        }
        if (alvo.equals(expr)) {
            return true;
        }
        if ("real".equals(alvo) && "inteiro".equals(expr)) {
            return true;
        }
        if (alvo.startsWith("^") && expr.startsWith("^")) {
            return alvo.equals(expr);
        }
        if (alvo.startsWith("@reg:") && expr.startsWith("@reg:")) {
            return alvo.equals(expr);
        }
        return false;
    }

    private static String textoIdent(LAParser.IdentContext id) {
        return id.IDENT().getText();
    }

    /** Texto do lvalue na fonte ({@code getText()}). */
    private static String textoLvalue(LAParser.LvalueContext lv) {
        return lv.getText();
    }

    private static String textoFatorCaminho(LAParser.FatorContext ctx) {
        return ctx.getText();
    }
}
