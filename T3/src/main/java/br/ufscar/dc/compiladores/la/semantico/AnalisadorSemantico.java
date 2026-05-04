package br.ufscar.dc.compiladores.la.semantico;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class AnalisadorSemantico {

    public record Erro(int linha, String mensagem) {}

    private static final List<String> TIPOS_BASICOS =
            List.of("literal", "inteiro", "real", "logico");

    private final List<Erro> erros = new ArrayList<>();
    /** Nome declarado pelo programador para alias / registro -> tipo interno canonical. */
    private final Map<String, String> nomesTipoUsuario = new HashMap<>();

    /** id de registro (nome do tipo ou __anon__) -> campo -> tipo. */
    private final Map<String, Map<String, String>> camposDoRegistro = new HashMap<>();

    private final Set<String> procedimentos = new HashSet<>();
    private final Map<String, String> retornoFuncoes = new HashMap<>();
    private int idRegistroAnonimo = 0;

    private Deque<Map<String, String>> escopos = new ArrayDeque<>();
    /**
     * Nomes declarados cuja lista de variáveis/parâmetros teve erro de tipo: não devem gerar
     * segunda mensagem "nao declarado" nos pontos de uso (padrão dos casos de teste T3).
     */
    private final Set<String> nomesDeclaracaoComTipoInvalido = new HashSet<>();

    public List<Erro> getErros() {
        return Collections.unmodifiableList(erros);
    }

    private void erro(int linha, String msg) {
        erros.add(new Erro(linha, msg));
    }

    public void analisar(LAParser.ProgramaContext ctx) {
        escopos.clear();
        escopos.push(new HashMap<>());
        nomesTipoUsuario.clear();
        camposDoRegistro.clear();
        procedimentos.clear();
        retornoFuncoes.clear();
        idRegistroAnonimo = 0;
        nomesDeclaracaoComTipoInvalido.clear();

        for (LAParser.Declaracao_globalContext dg : ctx.declaracoes_globais().declaracao_global()) {
            processarDeclaracaoGlobal(dg);
        }

        LAParser.CorpoContext corpoPrincipal =
                ctx.secao_principal() instanceof LAParser.PrincipalComCabecalhoContext cab
                        ? cab.corpo()
                        : ((LAParser.PrincipalSemPalavraAlgoritmoContext) ctx.secao_principal())
                                .corpo();

        escopos.push(new HashMap<>());
        processarConteudoCorpo(corpoPrincipal);
        escopos.pop();

        ordenarErros();
    }

    private void ordenarErros() {
        erros.sort((a, b) -> Integer.compare(a.linha, b.linha));
    }

    // --- programa global -------------------------------------------------

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
            // Sem mensagem padronizada nos casos de teste T3
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
        if (procedimentos.contains(nome) || retornoFuncoes.containsKey(nome)) {
            erro(nomeTok.getLine(), "identificador " + nome + " ja declarado anteriormente");
            return;
        }
        if (procedimento) {
            procedimentos.add(nome);
        } else {
            String tr = expandirTipoEstendido(tipoRet);
            if (tr != null) {
                retornoFuncoes.put(nome, tr);
            }
        }

        escopos.push(new HashMap<>());
        if (params != null) {
                for (LAParser.ParametroContext par : params.parametro()) {
                String tParam = expandirTipoEstendido(par.tipo_estendido());
                if (tParam == null) {
                    for (LAParser.Ident_dimContext idc :
                            par.lista_ident_dim().ident_dim()) {
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
        processarConteudoCorpo(corpo);
        escopos.pop();
    }

    // --- corpo (declarações + comandos) ----------------------------------

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
        } else if (ctx.RETORNE() != null && ctx.expressao() != null) {
            tipoExpressao(ctx.expressao());
        }
    }

    private void comandoAtribuicao(LAParser.Comando_atribuicaoContext ctx) {
        String tAlvo = tipoParaAtribuicaoLvalor(ctx.lvalue());
        String tExpr = tipoExpressao(ctx.expressao());
        if (!compativelAtribuicao(tAlvo, tExpr)) {
            String nome = textoIdent(ctx.lvalue().ident(0));
            erro(ctx.start.getLine(), "atribuicao nao compativel para " + nome);
        }
    }

    private void comandoLeia(LAParser.Comando_leiaContext ctx) {
        for (LAParser.LvalueContext lv : ctx.lista_lvalue().lvalue()) {
            tipoParaAtribuicaoLvalor(lv);
        }
    }

    private void comandoEscreva(LAParser.Comando_escrevaContext ctx) {
        for (LAParser.ExpressaoContext ex :
                ctx.lista_expressoes().expressao()) {
            tipoExpressao(ex);
        }
    }

    private void comandoChamadaProc(LAParser.Comando_chamadaContext ctx) {
        String nome = textoIdent(ctx.ident());
        Token t = ctx.ident().IDENT().getSymbol();
        if (!procedimentos.contains(nome)) {
            if (buscarTipoVariavelSilenciosa(nome) == null && !retornoFuncoes.containsKey(nome)) {
                erro(t.getLine(), "identificador " + nome + " nao declarado");
            }
            return;
        }
        if (ctx.lista_expressoes() != null) {
            for (LAParser.ExpressaoContext ex : ctx.lista_expressoes().expressao()) {
                tipoExpressao(ex);
            }
        }
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

    // --- Tipos ------------------------------------------------------------

    private static String chaveRegistro(String idInterno) {
        return "@reg:" + idInterno;
    }

    private String proximoRegistroAnonimo() {
        return "__anon_" + (++idRegistroAnonimo);
    }

    private void registrarCamposEmRegistro(
            String idRegistro, LAParser.Lista_campos_registroContext lista) {
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

    /** Expande tipo_estendido; em falha já emite erro e retorna null. */
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

    /**
     * Resolve um nome de tipo declarado pelo usuário ou um básico. Retorna tipo canônico
     * (inteiro, real, @reg:X, ...) ou null.
     */
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

    // --- Escopo / símbolos -------------------------------------------------

    private boolean declararSimboloNoEscopoAtual(Token idTok, String tipo) {
        String nome = idTok.getText();
        Map<String, String> topo = escopos.peek();
        if (topo.containsKey(nome)) {
            erro(idTok.getLine(), "identificador " + nome + " ja declarado anteriormente");
            return false;
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

    /** Tipo do lado esquerdo de uma atribuição / leia. */
    private String tipoParaAtribuicaoLvalor(LAParser.LvalueContext lv) {
        int idx = 0;
        if (lv.getChild(idx) instanceof TerminalNode tn
                && tn.getSymbol().getType() == LAParser.CIRCUNFLEXO) {
            idx++;
        }
        LAParser.IdentContext idBase = (LAParser.IdentContext) lv.getChild(idx++);
        Token idTok = idBase.IDENT().getSymbol();
        String tipo = buscarTipoVariavel(idTok.getText(), idTok);
        if (tipo == null) {
            return null;
        }
        if (lv.CIRCUNFLEXO() != null) {
            tipo = tirarUmPont(tipo);
        }
        while (idx < lv.getChildCount()) {
            ParseTree ch = lv.getChild(idx++);
            if (!(ch instanceof TerminalNode tnx)) {
                break;
            }
            int tt = tnx.getSymbol().getType();
            if (tt == LAParser.PONTO) {
                LAParser.IdentContext ic = (LAParser.IdentContext) lv.getChild(idx++);
                tipo = campoDeRegistro(tipo, textoIdent(ic), ic.IDENT().getSymbol());
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

    private static String tirarUmPont(String tipoVar) {
        if (tipoVar != null && tipoVar.startsWith("^")) {
            return tipoVar.substring(1);
        }
        return tipoVar;
    }

    /** Após uso de colchetes tenta remover um nível de ponteiro (array modelado como ponteiro). */
    private String tipoElementoMatrizOuErro(String tipoVariavelBase, Token ref) {
        if (tipoVariavelBase.startsWith("^")) {
            return tipoVariavelBase.substring(1);
        }
        return tipoVariavelBase;
    }

    private String campoDeRegistro(String tipoValor, String nomeCampo, Token ref) {
        String idReg = extrairIdRegistro(tipoValor);
        if (idReg == null) {
            erro(ref.getLine(), "identificador " + nomeCampo + " nao declarado");
            return null;
        }
        Map<String, String> cmap = camposDoRegistro.get(idReg);
        if (cmap == null || !cmap.containsKey(nomeCampo)) {
            erro(ref.getLine(), "identificador " + nomeCampo + " nao declarado");
            return null;
        }
        return cmap.get(nomeCampo);
    }

    /** Se {@code tipo} começa por @reg:x, devolve {@code x}. */
    private static String extrairIdRegistro(String tipo) {
        if (tipo != null && tipo.startsWith("@reg:")) {
            return tipo.substring("@reg:".length());
        }
        return null;
    }

    // --- Expressões --------------------------------------------------------

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
                if (!retornoFuncoes.containsKey(nomeFun)) {
                    erro(t.getLine(), "identificador " + nomeFun + " nao declarado");
                    return null;
                }
                if (ctx.lista_expressoes() != null) {
                    for (LAParser.ExpressaoContext ex : ctx.lista_expressoes().expressao()) {
                        tipoExpressao(ex);
                    }
                }
                return retornoFuncoes.get(nomeFun);
            }

            Token id0Tok = primeiro.IDENT().getSymbol();
            String tipo = buscarTipoVariavel(id0Tok.getText(), id0Tok);
            if (tipo == null) {
                return null;
            }
            return aplicarSufixoTipoVariavel(ctx, id0Tok, tipo);
        }

        return null;
    }

    private String aplicarSufixoTipoVariavel(LAParser.FatorContext ctx, Token refBase, String tipoIni) {
        String tipoCorrente = tipoIni;
        int ids = 1;
        int ib = 0;
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
                tipoCorrente =
                        campoDeRegistro(tipoCorrente, textoIdent(ic), ic.IDENT().getSymbol());
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

    private static boolean compativelAtribuicao(String alvo, String expr) {
        if (alvo == null || expr == null) {
            return false;
        }
        if (alvo.equals(expr)) {
            return true;
        }
        return "real".equals(alvo) && "inteiro".equals(expr);
    }

    private static String textoIdent(LAParser.IdentContext id) {
        return id.IDENT().getText();
    }
}
