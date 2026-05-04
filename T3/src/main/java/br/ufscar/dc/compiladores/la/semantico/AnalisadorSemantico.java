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

/**
 * Analisador semântico da linguagem LA.
 *
 * Recebe a árvore de derivação produzida pelo parser do T2 e percorre suas
 * declarações e comandos coletando uma lista de erros semânticos:
 *   - Identificadores não declarados ou redeclarados
 *   - Tipos não declarados / atribuição com tipos incompatíveis
 *   - Acesso a campos inexistentes em registros
 *
 * Os erros não interrompem a análise: a lista pode ser obtida por
 * {@link #getErros()} ao final, já ordenada por linha.
 *
 * Modelo interno de tipos (string canônica):
 *   - Tipos básicos: "literal", "inteiro", "real", "logico"
 *   - Registros:    "@reg:<id>" (id = nome do tipo ou anônimo "__anon_N")
 *   - Ponteiros:    prefixo "^" repetido (ex.: "^^inteiro")
 */
public final class AnalisadorSemantico {

    /** Erro semântico: linha e mensagem como devem aparecer na saída. */
    public record Erro(int linha, String mensagem) {}

    /** Tipos primitivos da linguagem LA (lowercase). */
    private static final List<String> TIPOS_BASICOS =
            List.of("literal", "inteiro", "real", "logico");

    /** Lista acumulada de erros encontrados durante a análise. */
    private final List<Erro> erros = new ArrayList<>();
    /** Nome declarado pelo programador para alias / registro -> tipo interno canonical. */
    private final Map<String, String> nomesTipoUsuario = new HashMap<>();

    /** id de registro (nome do tipo ou __anon__) -> campo -> tipo. */
    private final Map<String, Map<String, String>> camposDoRegistro = new HashMap<>();

    /** Nomes de procedimentos declarados globalmente. */
    private final Set<String> procedimentos = new HashSet<>();
    /** Funções declaradas: nome -> tipo de retorno. */
    private final Map<String, String> retornoFuncoes = new HashMap<>();
    /** Contador para gerar identificadores únicos para registros anônimos. */
    private int idRegistroAnonimo = 0;

    /** Pilha de escopos (cada nível é um mapa nome -> tipo). O topo é o escopo atual. */
    private Deque<Map<String, String>> escopos = new ArrayDeque<>();
    /**
     * Nomes declarados cuja lista de variáveis/parâmetros teve erro de tipo: não devem gerar
     * segunda mensagem "nao declarado" nos pontos de uso (padrão dos casos de teste T3).
     */
    private final Set<String> nomesDeclaracaoComTipoInvalido = new HashSet<>();

    /** Devolve a lista (imutável) de erros coletados pela última chamada de {@link #analisar}. */
    public List<Erro> getErros() {
        return Collections.unmodifiableList(erros);
    }

    /** Registra um erro semântico na lista. */
    private void erro(int linha, String msg) {
        erros.add(new Erro(linha, msg));
    }

    /**
     * Ponto de entrada da análise semântica.
     *
     * Reinicializa todo o estado interno, processa primeiro as declarações
     * globais (tipos, constantes, procedimentos, funções) e depois o corpo
     * da seção principal num escopo próprio. Ao final, ordena os erros por linha.
     */
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

    /** Ordena os erros pela linha em que ocorreram, preservando ordem de inserção quando empatam. */
    private void ordenarErros() {
        erros.sort((a, b) -> Integer.compare(a.linha, b.linha));
    }

    // --- programa global -------------------------------------------------

    /** Despacha cada tipo de declaração global para o tratamento correspondente. */
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

    /**
     * Registra uma declaração global do tipo "tipo X: registro ... fim_registro".
     * Detecta redeclaração e armazena os campos do registro para uso posterior.
     */
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

    /** Registra uma constante global no escopo atual com seu tipo declarado. */
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

    /**
     * Declara um procedimento ou função: registra a assinatura, abre um novo escopo,
     * declara os parâmetros e analisa o corpo. Detecta redeclaração e parâmetros
     * com tipo inválido.
     */
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

    /**
     * Percorre um corpo (declarações locais + comandos) em duas passadas:
     * primeiro registra as declarações no escopo atual e depois analisa os comandos.
     * Isso permite usar variáveis declaradas mais abaixo no mesmo bloco.
     */
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

    /**
     * Trata uma declaração local: declare (variáveis), constante (constantes locais)
     * ou tipo (alias / registro local).
     */
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

    /**
     * Expande o corpo de um "tipo X: ..." (registro inline ou alias para outro tipo)
     * e devolve a representação canônica do tipo.
     */
    private String expandirTipoCorpoTipo(LAParser.Tipo_corpo_tipoContext ctx) {
        if (ctx.REGISTRO() != null) {
            String idReg = proximoRegistroAnonimo();
            nomesTipoUsuario.put(idReg, chaveRegistro(idReg));
            registrarCamposEmRegistro(idReg, ctx.lista_campos_registro());
            return chaveRegistro(idReg);
        }
        return expandirTipoEstendidoSimples(ctx.tipo_estendido_simples());
    }

    /** Despacha cada variante de comando para o método específico. */
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

    /**
     * Verifica compatibilidade de tipos em "lvalue <- expressao" e emite
     * "atribuicao nao compativel para X" quando não há compatibilidade.
     */
    private void comandoAtribuicao(LAParser.Comando_atribuicaoContext ctx) {
        String tAlvo = tipoParaAtribuicaoLvalor(ctx.lvalue());
        String tExpr = tipoExpressao(ctx.expressao());
        if (!compativelAtribuicao(tAlvo, tExpr)) {
            String nome = textoIdent(ctx.lvalue().ident(0));
            erro(ctx.start.getLine(), "atribuicao nao compativel para " + nome);
        }
    }

    /** "leia": apenas valida que cada lvalue está declarado (efeito colateral em tipoParaAtribuicaoLvalor). */
    private void comandoLeia(LAParser.Comando_leiaContext ctx) {
        for (LAParser.LvalueContext lv : ctx.lista_lvalue().lvalue()) {
            tipoParaAtribuicaoLvalor(lv);
        }
    }

    /** "escreva": tipa cada expressão (efeito colateral: marca usos). */
    private void comandoEscreva(LAParser.Comando_escrevaContext ctx) {
        for (LAParser.ExpressaoContext ex :
                ctx.lista_expressoes().expressao()) {
            tipoExpressao(ex);
        }
    }

    /**
     * Chamada de procedimento: valida que o nome existe como procedimento.
     * Se for variável ou função, ignora silenciosamente (não é a forma esperada).
     */
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

    /** "se ... entao ... [senao ...]": tipa a condição e processa cada bloco em escopo próprio. */
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

    /** "caso ... seja ... [senao ...]": tipa a expressão e cada item em escopo próprio. */
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

    /** "para ... ate ... faca": tipa as expressões dos limites e processa o corpo em escopo próprio. */
    private void comandoPara(LAParser.Comando_paraContext ctx) {
        tipoExpressao(ctx.expressao(0));
        tipoExpressao(ctx.expressao(1));
        escopos.push(new HashMap<>());
        processarConteudoCorpo(ctx.corpo());
        escopos.pop();
    }

    /** "enquanto ... faca": tipa a condição e processa o corpo em escopo próprio. */
    private void comandoEnquanto(LAParser.Comando_enquantoContext ctx) {
        tipoExpressao(ctx.expressao());
        escopos.push(new HashMap<>());
        processarConteudoCorpo(ctx.corpo());
        escopos.pop();
    }

    /** "faca ... ate": processa o corpo em escopo próprio e depois tipa a condição. */
    private void comandoFacaAte(LAParser.Comando_faca_ateContext ctx) {
        escopos.push(new HashMap<>());
        processarConteudoCorpo(ctx.corpo());
        escopos.pop();
        tipoExpressao(ctx.expressao());
    }

    // --- Tipos ------------------------------------------------------------

    /** Constrói a chave canônica de um tipo registro a partir de seu id interno. */
    private static String chaveRegistro(String idInterno) {
        return "@reg:" + idInterno;
    }

    /** Gera um id único para um registro anônimo (registro inline em declaração de variável). */
    private String proximoRegistroAnonimo() {
        return "__anon_" + (++idRegistroAnonimo);
    }

    /**
     * Indexa os campos de um registro num mapa nome -> tipo. Detecta campos
     * duplicados e expande o tipo de cada campo (que pode ser básico, registro
     * ou ponteiro).
     */
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

    /** Valida (tipa) cada expressão usada como dimensão de array em uma declaração. */
    private void validarExpressoesDimensoes(LAParser.Ident_dimContext idc) {
        for (LAParser.ExpressaoContext ex : idc.expressao()) {
            tipoExpressao(ex);
        }
    }

    /** Devolve o nome canônico (lowercase) de um tipo básico textualmente declarado. */
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

    /**
     * Expande tipo_estendido_simples: tipo básico ou nome de tipo declarado,
     * possivelmente prefixado por '^' (ponteiro). Em falha emite erro e retorna null.
     */
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

    /**
     * Declara um símbolo no escopo do topo da pilha. Retorna false e emite
     * erro de redeclaração se o nome já existir no mesmo escopo.
     */
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

    /**
     * Procura um símbolo subindo a pilha de escopos. Emite "nao declarado" se não encontrar
     * (a menos que o nome esteja na lista de declarações com tipo inválido — caso em que
     * o erro de tipo já foi emitido na declaração e não duplicamos a mensagem aqui).
     */
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

    /** Versão de busca que NÃO emite erro caso o nome não exista — usada para descobrir o papel do identificador. */
    private String buscarTipoVariavelSilenciosa(String nome) {
        for (Map<String, String> m : escopos) {
            if (m.containsKey(nome)) {
                return m.get(nome);
            }
        }
        return null;
    }

    /**
     * Tipo do lado esquerdo de uma atribuição / leia.
     *
     * Suporta sufixos encadeados: acesso a campo (.nome), indexação ([expr])
     * e desreferenciamento (^). Pode emitir erros como "nao declarado" para
     * o identificador base ou para campos inexistentes.
     */
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

    /** Remove um único '^' do prefixo do tipo (ex.: "^^inteiro" -> "^inteiro"). */
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

    /**
     * Acessa um campo de registro a partir do tipo da expressão base.
     * Emite "identificador X nao declarado" se o tipo não for um registro
     * ou se o campo não existir.
     */
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

    /**
     * Tipa uma expressão completa (operador "ou" lógico no nível mais alto).
     * Devolve o tipo canônico resultante ou null caso haja incompatibilidade.
     */
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

    /** Tipa uma expressão com operador "e" lógico. */
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

    /**
     * Tipa uma expressão relacional. Operadores relacionais sempre produzem "logico"
     * quando os operandos são comparáveis; caso contrário, devolvem null.
     */
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

    /**
     * Decide se dois tipos são comparáveis pelo operador relacional dado.
     * Numéricos (inteiro/real) são sempre comparáveis entre si;
     * "literal" só compara com "literal"; "logico" só admite = e <>.
     */
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

    /** Remove todos os '^' do prefixo do tipo (útil para comparações de "tipo subjacente"). */
    private static String tirarPonteirosRecursivo(String tipo) {
        String t = tipo;
        while (t != null && t.startsWith("^")) {
            t = t.substring(1);
        }
        return t;
    }

    /** Tipa uma expressão aritmética com operadores de menor precedência (+, -). */
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

    /** Soma: numérico + numérico (resultado real se algum for real); literal + literal = literal. */
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

    /** Subtração: aceita apenas operandos numéricos. */
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

    /** True se o tipo for numérico (inteiro/real) eventualmente sob qualquer número de '^'. */
    private boolean ehNumericoOuPonteirosNumericos(String t) {
        String u = tirarPonteirosRecursivo(t);
        return "inteiro".equals(u) || "real".equals(u);
    }

    /** Tipa um termo (operadores *, /, %). */
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

    /**
     * Multiplicação/divisão/módulo. MOD exige inteiros; demais aceitam numéricos
     * com promoção a real quando algum operando for real.
     */
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

    /**
     * Tipa um operador unário. "-" exige numérico (preserva o tipo);
     * "nao" exige logico (resulta em logico).
     */
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

    /**
     * Tipa um fator, que pode ser:
     *   - Literal (número, cadeia, verdadeiro/falso) ou expressão entre parênteses
     *   - Operador de endereço (&id) ou desreferência (^id)
     *   - Chamada de função (id(...))
     *   - Acesso a variável, possivelmente com sufixos .campo / [expr] encadeados
     */
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
            // &id : operador de endereço (devolve ponteiro para o tipo da variável)
            if (sym == LAParser.E_COMERCIAL) {
                String nome = textoIdent(ctx.ident(0));
                Token t = ctx.ident(0).IDENT().getSymbol();
                String vt = buscarTipoVariavel(nome, t);
                if (vt == null) {
                    return null;
                }
                return "^" + vt;
            }
            // ^id : operador de desreferência (exige tipo ponteiro)
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
            // Chamada de função: id(arg, arg, ...)
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

    /**
     * Aplica os sufixos .campo e [expr] encadeados sobre o tipo de uma variável
     * (na ordem em que aparecem no texto). Cada acesso pode reduzir um nível
     * de ponteiro/array ou trocar para o tipo de um campo de registro.
     */
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

    /** Devolve o tipo do literal usado em uma declaração de constante. */
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

    /** Igualdade estrita entre tipos canônicos. */
    private static boolean tiposIguais(String a, String b) {
        return Objects.equals(a, b);
    }

    /**
     * Compatibilidade para atribuição "alvo <- expr": tipos iguais ou promoção
     * implícita de inteiro para real (real := inteiro é aceito; o inverso não).
     */
    private static boolean compativelAtribuicao(String alvo, String expr) {
        if (alvo == null || expr == null) {
            return false;
        }
        if (alvo.equals(expr)) {
            return true;
        }
        return "real".equals(alvo) && "inteiro".equals(expr);
    }

    /** Atalho para extrair o lexema de um nó IdentContext da árvore. */
    private static String textoIdent(LAParser.IdentContext id) {
        return id.IDENT().getText();
    }
}
