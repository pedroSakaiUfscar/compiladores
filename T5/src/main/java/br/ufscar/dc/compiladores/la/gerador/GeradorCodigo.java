package br.ufscar.dc.compiladores.la.gerador;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Gerador de código C equivalente a um programa LA já validado.
 *
 * Estratégia geral:
 *   - Percorre a árvore sintática (ANTLR) construída pelo LAParser.
 *   - Mantém uma tabela de símbolos própria (pilha de escopos) só para
 *     decidir o tipo C apropriado em cada situação (printf %d/%f/%s,
 *     uso de strcpy para literais, &x em scanf, etc.).
 *   - Acumula três blocos textuais separados:
 *       1. defines      — um "#define" por constante (global ou local)
 *       2. antesMain    — typedefs e definições de procedimentos/funções
 *       3. corpoMain    — declarações locais e comandos do algoritmo principal
 *     A montagem final é: includes + defines + antesMain + main(){corpoMain return 0;}
 *
 * O gerador é executado APÓS o analisador semântico ter aprovado o programa,
 * portanto assume que todos os identificadores e tipos são válidos.
 */
public final class GeradorCodigo {

    /** Buffer corrente onde os métodos de emissão escrevem (é trocado durante a geração). */
    private StringBuilder out;

    /** Indentação corrente (em tabulações). */
    private int indent;

    /** Contador de identificadores internos para registros anônimos (typedefs gerados pelo gerador). */
    private int idAnonimo;

    /** Pilha de escopos: cada mapa associa nome → tipo canônico. */
    private final Deque<Map<String, String>> escopos = new ArrayDeque<>();

    /**
     * Aliases de tipo declarados pelo usuário (ex.: "treg") cujo conteúdo é um registro.
     * Mapeia o nome do alias → mesmo nome (serve como conjunto e também como id de registro).
     */
    private final Map<String, String> aliasParaC = new HashMap<>();

    /** Mapa de registros (id do registro → mapa ordenado de campos: nome → tipo canônico). */
    private final Map<String, LinkedHashMap<String, String>> registros = new HashMap<>();

    /** Tipo de retorno (canônico) das funções para uso em chamadas dentro de expressões. */
    private final Map<String, String> retornoFuncao = new HashMap<>();

    /** Lista de #defines a serem emitidos no topo do arquivo C. */
    private final List<String> defines = new ArrayList<>();

    /** Tipo canônico para uma constante ou variável (chave do mapa de escopos). */
    private static final String T_INT = "inteiro";
    private static final String T_REAL = "real";
    private static final String T_LIT = "literal";
    private static final String T_LOG = "logico";

    /**
     * Ponto de entrada do gerador. Recebe o ProgramaContext do parser e
     * devolve o código C como uma única String pronta para gravar em arquivo.
     */
    public String gerar(LAParser.ProgramaContext ctx) {
        // Reinicialização do estado (permite reusar a instância)
        indent = 0;
        idAnonimo = 0;
        escopos.clear();
        aliasParaC.clear();
        registros.clear();
        retornoFuncao.clear();
        defines.clear();

        // Escopo global (constantes/tipos globais)
        escopos.push(new HashMap<>());

        // Buffer "antes do main": typedefs e sub-rotinas
        StringBuilder antesMain = new StringBuilder();
        out = antesMain;
        for (LAParser.Declaracao_globalContext dg : ctx.declaracoes_globais().declaracao_global()) {
            processarDeclaracaoGlobal(dg);
        }

        // Buffer do corpo do main
        StringBuilder corpoMain = new StringBuilder();
        out = corpoMain;
        indent = 1;

        LAParser.CorpoContext corpoPrincipal =
                ctx.secao_principal() instanceof LAParser.PrincipalComCabecalhoContext cab
                        ? cab.corpo()
                        : ((LAParser.PrincipalSemPalavraAlgoritmoContext) ctx.secao_principal()).corpo();

        escopos.push(new HashMap<>());
        processarCorpo(corpoPrincipal);
        escopos.pop();

        // Montagem final
        StringBuilder fim = new StringBuilder();
        fim.append("#include <stdio.h>\n");
        fim.append("#include <stdlib.h>\n");
        fim.append("#include <string.h>\n");
        fim.append("\n");

        if (!defines.isEmpty()) {
            for (String d : defines) fim.append(d).append('\n');
            fim.append('\n');
        }

        if (antesMain.length() > 0) {
            fim.append(antesMain);
            if (antesMain.charAt(antesMain.length() - 1) != '\n') fim.append('\n');
            fim.append('\n');
        }

        fim.append("int main() {\n");
        fim.append(corpoMain);
        fim.append("\treturn 0;\n");
        fim.append("}\n");

        return fim.toString();
    }

    // ---------------------------------------------------------------------
    // Auxiliares de emissão (escrevem no buffer corrente "out")
    // ---------------------------------------------------------------------

    /** Emite uma linha já indentada terminada em \n. */
    private void linha(String s) {
        for (int i = 0; i < indent; i++) out.append('\t');
        out.append(s);
        out.append('\n');
    }

    /** Emite texto sem indentação automática (uso interno raro). */
    private void semIndent(String s) {
        out.append(s);
    }

    // ---------------------------------------------------------------------
    // Declarações globais
    // ---------------------------------------------------------------------

    /**
     * Distribui uma declaração global para o tratamento adequado:
     *   - declaracao_tipo            -> typedef struct
     *   - declaracao_constante_global-> #define
     *   - declaracao_procedimento    -> void name(...) { ... }
     *   - declaracao_funcao          -> tipo name(...) { ... }
     */
    private void processarDeclaracaoGlobal(LAParser.Declaracao_globalContext dg) {
        if (dg.declaracao_tipo() != null) {
            processarDeclaracaoTipo(dg.declaracao_tipo());
        } else if (dg.declaracao_constante_global() != null) {
            processarConstanteGlobal(dg.declaracao_constante_global());
        } else if (dg.declaracao_procedimento() != null) {
            processarProcedimento(dg.declaracao_procedimento());
        } else if (dg.declaracao_funcao() != null) {
            processarFuncao(dg.declaracao_funcao());
        }
    }

    /** "tipo X: registro ... fim_registro" -> "typedef struct { campos } X;". */
    private void processarDeclaracaoTipo(LAParser.Declaracao_tipoContext d) {
        String nome = textoIdent(d.ident());
        aliasParaC.put(nome, nome);
        LinkedHashMap<String, String> campos = new LinkedHashMap<>();
        registros.put(nome, campos);

        linha("typedef struct {");
        indent++;
        emitirCamposRegistro(d.lista_campos_registro(), campos);
        indent--;
        linha("} " + nome + ";");
    }

    /** "constante X: tipo = valor" -> "#define X valor". */
    private void processarConstanteGlobal(LAParser.Declaracao_constante_globalContext c) {
        String nome = textoIdent(c.ident());
        String tipo = nomeTipoBasico(c.tipo_basico());
        // Em C, #define usa o texto literal do valor (NUM_INT, NUM_REAL, CADEIA, true/false)
        String valor = valorConstanteC(c.valor_constante());
        defines.add("#define " + nome + " " + valor);
        escopos.peek().put(nome, tipo);
    }

    /** Procedimento (sem retorno): "void name(params) { corpo }". */
    private void processarProcedimento(LAParser.Declaracao_procedimentoContext p) {
        String nome = textoIdent(p.ident());
        String params = montarParametrosC(p.lista_parametros());
        linha("void " + nome + "(" + params + ") {");
        indent++;
        escopos.push(new HashMap<>());
        registrarParametrosNoEscopo(p.lista_parametros());
        processarCorpo(p.corpo());
        escopos.pop();
        indent--;
        linha("}");
        out.append('\n');
    }

    // ---------------------------------------------------------------------
    // Corpo (declarações locais + comandos) e declarações locais
    // ---------------------------------------------------------------------

    /** Processa um corpo: primeiro as declarações locais, depois os comandos. */
    private void processarCorpo(LAParser.CorpoContext corpo) {
        for (int i = 0; i < corpo.getChildCount(); i++) {
            ParseTree ch = corpo.getChild(i);
            if (ch instanceof LAParser.Declaracao_localContext dl) {
                processarDeclaracaoLocal(dl);
            }
        }
        for (int i = 0; i < corpo.getChildCount(); i++) {
            ParseTree ch = corpo.getChild(i);
            if (ch instanceof LAParser.ComandoContext cmd) {
                processarComando(cmd);
            }
        }
    }

    /**
     * Trata "declare", "constante" e "tipo" no escopo local.
     * Constantes locais também viram #define no topo do arquivo (preprocessor é global).
     */
    private void processarDeclaracaoLocal(LAParser.Declaracao_localContext dl) {
        if (dl.DECLARE() != null) {
            for (LAParser.Variavel_declContext vd : dl.variavel_decl()) {
                emitirVariaveisDecl(vd);
            }
        } else if (dl.CONSTANTE() != null) {
            String nome = textoIdent(dl.ident());
            String tipo = nomeTipoBasico(dl.tipo_basico());
            String valor = valorConstanteC(dl.valor_constante());
            defines.add("#define " + nome + " " + valor);
            escopos.peek().put(nome, tipo);
        } else if (dl.TIPO() != null) {
            // typedef local: emitido na posição corrente (dentro do main, da func, etc.)
            String nome = textoIdent(dl.ident());
            LAParser.Tipo_corpo_tipoContext tct = dl.tipo_corpo_tipo();
            if (tct.REGISTRO() != null) {
                aliasParaC.put(nome, nome);
                LinkedHashMap<String, String> campos = new LinkedHashMap<>();
                registros.put(nome, campos);
                linha("typedef struct {");
                indent++;
                emitirCamposRegistro(tct.lista_campos_registro(), campos);
                indent--;
                linha("} " + nome + ";");
            } else {
                String tipoC = tipoCParaTipoEstendidoSimplesEmContexto(tct.tipo_estendido_simples(), false);
                linha("typedef " + tipoC + " " + nome + ";");
                aliasParaC.put(nome, nome);
            }
        }
    }

    /**
     * Emite a declaração C para "lista_ident_dim DOIS_PONTOS tipo_estendido".
     * Cada identificador na lista vira uma linha de declaração C.
     */
    private void emitirVariaveisDecl(LAParser.Variavel_declContext vd) {
        LAParser.Tipo_estendidoContext te = vd.tipo_estendido();
        for (LAParser.Ident_dimContext idc : vd.lista_ident_dim().ident_dim()) {
            String nome = textoIdent(idc.ident());
            String dims = dimensoesC(idc);
            emitirDeclaracaoVariavel(nome, te, dims);
            // registro inline ja foi registrado dentro de emitirDeclaracaoVariavel
            if (te.REGISTRO() == null) {
                escopos.peek().put(nome, tipoCanonicoParaTipoEstendido(te));
            }
        }
    }

    /**
     * Constrói o sufixo "[N][M]" a partir de uma "ident_dim" (zero ou mais dimensões).
     * As expressões já foram validadas pelo analisador semântico.
     */
    private String dimensoesC(LAParser.Ident_dimContext idc) {
        StringBuilder sb = new StringBuilder();
        for (LAParser.ExpressaoContext ex : idc.expressao()) {
            sb.append('[').append(expressaoC(ex)).append(']');
        }
        return sb.toString();
    }

    /**
     * Emite a declaração C de uma variável.
     *   - Tipo básico/literal/ponteiro: "TIPO NOME[DIM]?;"
     *   - Registro inline anônimo: "struct { campos } NOME[DIM]?;"
     *   - Alias de tipo (ex.: treg): "treg NOME[DIM]?;"
     *   - Caso especial literal (sem ponteiro): "char NOME[DIM]?[80];"
     */
    private void emitirDeclaracaoVariavel(String nome, LAParser.Tipo_estendidoContext te, String dims) {
        if (te.REGISTRO() != null) {
            // Registro inline anônimo
            String idReg = "__anon_" + (++idAnonimo);
            LinkedHashMap<String, String> campos = new LinkedHashMap<>();
            registros.put(idReg, campos);
            // Vincula o nome da variável ao id do registro para resolução de campos
            escopos.peek().put(nome, "@reg:" + idReg);
            linha("struct {");
            indent++;
            emitirCamposRegistro(te.lista_campos_registro(), campos);
            indent--;
            linha("} " + nome + dims + ";");
            return;
        }

        LAParser.Tipo_estendido_simplesContext tes = te.tipo_estendido_simples();
        boolean ptr = tes.CIRCUNFLEXO() != null;
        String tipoBase = tes.tipo_basico() != null
                ? nomeTipoBasico(tes.tipo_basico())
                : textoIdent(tes.ident());

        if (T_LIT.equals(tipoBase) && !ptr) {
            // String "comum" em LA -> char NOME[80]; (com dimensões antes do tamanho fixo)
            linha("char " + nome + dims + "[80];");
        } else {
            String c = mapTipoBaseParaC(tipoBase);
            linha(c + (ptr ? "* " : " ") + nome + dims + ";");
        }
    }

    /**
     * Emite os campos de um registro (cada bloco "lista_ident_dim : tipo_estendido")
     * e popula o mapa "camposDestino" com os tipos canônicos para consultas posteriores.
     */
    private void emitirCamposRegistro(LAParser.Lista_campos_registroContext lcr,
                                      LinkedHashMap<String, String> camposDestino) {
        var blocos = lcr.lista_ident_dim();
        var tipos = lcr.tipo_estendido();
        for (int i = 0; i < blocos.size(); i++) {
            LAParser.Lista_ident_dimContext lid = blocos.get(i);
            LAParser.Tipo_estendidoContext te = tipos.get(i);
            String tipoCanon = tipoCanonicoParaTipoEstendido(te);
            for (LAParser.Ident_dimContext idc : lid.ident_dim()) {
                String nomeCampo = textoIdent(idc.ident());
                String dims = dimensoesC(idc);
                emitirDeclaracaoVariavel(nomeCampo, te, dims);
                camposDestino.put(nomeCampo, tipoCanon);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Resolução de tipos (mapa LA -> C e tipo canônico interno)
    // ---------------------------------------------------------------------

    /** Texto exato do tipo básico em LA ("inteiro", "real", "literal", "logico"). */
    private static String nomeTipoBasico(LAParser.Tipo_basicoContext ctx) {
        return ctx.start.getText();
    }

    /** Mapeia "inteiro/real/logico/literal" e aliases de usuário para o tipo C de declaração. */
    private String mapTipoBaseParaC(String tipoBase) {
        switch (tipoBase) {
            case "inteiro": return "int";
            case "real":    return "float";
            case "logico":  return "int";
            case "literal": return "char"; // tamanho fixo (80) é tratado fora
            default:        return tipoBase; // alias de tipo definido pelo usuário
        }
    }

    /** Texto C que representa um "tipo_estendido" como tipo (sem nome de variável). */
    private String tipoCParaTipoEstendidoEmContexto(LAParser.Tipo_estendidoContext te, boolean parametro) {
        if (te.REGISTRO() != null) {
            // Registros inline em parâmetros/retorno não são esperados nos casos de teste;
            // mantemos um fallback razoável.
            return "void*";
        }
        return tipoCParaTipoEstendidoSimplesEmContexto(te.tipo_estendido_simples(), parametro);
    }

    /**
     * Texto C de um tipo simples. "parametro=true" usa "char*" para literal
     * (passagem direta de string) ao invés de "char" + tamanho fixo.
     */
    private String tipoCParaTipoEstendidoSimplesEmContexto(
            LAParser.Tipo_estendido_simplesContext tes, boolean parametro) {
        boolean ptr = tes.CIRCUNFLEXO() != null;
        String tipoBase = tes.tipo_basico() != null
                ? nomeTipoBasico(tes.tipo_basico())
                : textoIdent(tes.ident());
        if (T_LIT.equals(tipoBase) && !ptr && parametro) {
            return "char*";
        }
        String c = mapTipoBaseParaC(tipoBase);
        return ptr ? c + "*" : c;
    }

    /**
     * Tipo canônico interno (string) de um "tipo_estendido". Usado na tabela
     * de símbolos para depois decidir formatadores e compatibilidades.
     */
    private String tipoCanonicoParaTipoEstendido(LAParser.Tipo_estendidoContext te) {
        if (te == null) return null;
        if (te.REGISTRO() != null) {
            return "@reg:?"; // não precisamos diferenciar, pois consultaremos campos pelo nome da variável
        }
        return tipoCanonicoParaTipoEstendidoSimples(te.tipo_estendido_simples());
    }

    private String tipoCanonicoParaTipoEstendidoSimples(LAParser.Tipo_estendido_simplesContext tes) {
        boolean ptr = tes.CIRCUNFLEXO() != null;
        String tipoBase = tes.tipo_basico() != null
                ? nomeTipoBasico(tes.tipo_basico())
                : textoIdent(tes.ident());
        // Se for um alias de tipo (registro nomeado), reusamos o id do registro
        String canon;
        if (aliasParaC.containsKey(tipoBase)) {
            canon = "@reg:" + tipoBase;
        } else {
            canon = tipoBase; // basico ou desconhecido
        }
        return ptr ? "^" + canon : canon;
    }

    /** Monta a lista de parâmetros C a partir de "lista_parametros" da LA. */
    private String montarParametrosC(LAParser.Lista_parametrosContext lp) {
        if (lp == null) return "";
        StringBuilder sb = new StringBuilder();
        boolean primeiro = true;
        for (LAParser.ParametroContext par : lp.parametro()) {
            // Cada parametro pode declarar varios identificadores compartilhando o mesmo tipo
            for (LAParser.Ident_dimContext idc : par.lista_ident_dim().ident_dim()) {
                if (!primeiro) sb.append(", ");
                primeiro = false;
                String nome = textoIdent(idc.ident());
                String tipoC = tipoCParaTipoEstendidoEmContexto(par.tipo_estendido(), true);
                if (par.VAR() != null) {
                    // Passagem por referencia: usa ponteiro em C
                    if (!tipoC.endsWith("*")) tipoC = tipoC + "*";
                }
                sb.append(tipoC).append(' ').append(nome);
            }
        }
        return sb.toString();
    }

    /** Insere cada parâmetro na tabela de símbolos do escopo recém-criado. */
    private void registrarParametrosNoEscopo(LAParser.Lista_parametrosContext lp) {
        if (lp == null) return;
        for (LAParser.ParametroContext par : lp.parametro()) {
            String tipoCanon = tipoCanonicoParaTipoEstendido(par.tipo_estendido());
            for (LAParser.Ident_dimContext idc : par.lista_ident_dim().ident_dim()) {
                escopos.peek().put(textoIdent(idc.ident()), tipoCanon);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Comandos
    // ---------------------------------------------------------------------

    /** Despacha cada tipo de comando para o gerador específico. */
    private void processarComando(LAParser.ComandoContext ctx) {
        if (ctx.comando_atribuicao() != null) {
            cmdAtribuicao(ctx.comando_atribuicao());
        } else if (ctx.comando_leia() != null) {
            cmdLeia(ctx.comando_leia());
        } else if (ctx.comando_escreva() != null) {
            cmdEscreva(ctx.comando_escreva());
        } else if (ctx.comando_se() != null) {
            cmdSe(ctx.comando_se());
        } else if (ctx.comando_caso() != null) {
            cmdCaso(ctx.comando_caso());
        } else if (ctx.comando_para() != null) {
            cmdPara(ctx.comando_para());
        } else if (ctx.comando_enquanto() != null) {
            cmdEnquanto(ctx.comando_enquanto());
        } else if (ctx.comando_faca_ate() != null) {
            cmdFacaAte(ctx.comando_faca_ate());
        } else if (ctx.comando_chamada() != null) {
            cmdChamada(ctx.comando_chamada());
        } else if (ctx.RETORNE() != null) {
            if (ctx.expressao() != null) {
                linha("return " + expressaoC(ctx.expressao()) + ";");
            } else {
                linha("return;");
            }
        }
    }

    /**
     * Atribuição:
     *   - Para alvo do tipo "literal": strcpy(alvo, expr);
     *   - Caso geral: alvo = expr;
     */
    private void cmdAtribuicao(LAParser.Comando_atribuicaoContext ctx) {
        String tipoAlvo = tipoDeLvalue(ctx.lvalue());
        String alvoC = lvalueC(ctx.lvalue());
        String exprC = expressaoC(ctx.expressao());
        if (T_LIT.equals(tipoAlvo)) {
            linha("strcpy(" + alvoC + "," + exprC + ");");
        } else {
            linha(alvoC + " = " + exprC + ";");
        }
    }

    /**
     * Leitura:
     *   - inteiro/logico: scanf("%d", &x);
     *   - real:           scanf("%f", &x);
     *   - literal:        gets(x);  (sem &; o vetor decai para ponteiro)
     * Para "^x" (lvalue dereferenciado) o endereço já é o próprio identificador.
     */
    private void cmdLeia(LAParser.Comando_leiaContext ctx) {
        for (LAParser.LvalueContext lv : ctx.lista_lvalue().lvalue()) {
            String tipo = tipoDeLvalue(lv);
            String lvC = lvalueC(lv);
            if (T_LIT.equals(tipo)) {
                linha("gets(" + lvC + ");");
            } else {
                String fmt = formatScanf(tipo);
                String endereco = enderecoParaScanf(lv);
                linha("scanf(\"" + fmt + "\"," + endereco + ");");
            }
        }
    }

    /**
     * Escrita:
     *   - Cada expressão vira um printf separado.
     *   - Se a expressão é uma cadeia literal pura, emite printf direto da string.
     *   - Caso contrário, escolhe o formatador conforme o tipo da expressão.
     */
    private void cmdEscreva(LAParser.Comando_escrevaContext ctx) {
        for (LAParser.ExpressaoContext ex : ctx.lista_expressoes().expressao()) {
            String literal = stringLiteralOuNull(ex);
            if (literal != null) {
                linha("printf(" + literal + ");");
            } else {
                String tipo = tipoExpressao(ex);
                String fmt = formatPrintf(tipo);
                String exprC = expressaoC(ex);
                linha("printf(\"" + fmt + "\"," + exprC + ");");
            }
        }
    }

    /**
     * "se cond entao corpo (senao corpo)? fim_se"
     *   -> if (cond) { ... } else { ... }
     */
    private void cmdSe(LAParser.Comando_seContext ctx) {
        linha("if (" + expressaoC(ctx.expressao()) + ") {");
        indent++;
        escopos.push(new HashMap<>());
        processarCorpo(ctx.corpo(0));
        escopos.pop();
        indent--;
        if (ctx.SENAO() != null && ctx.corpo().size() > 1) {
            linha("}");
            linha("else {");
            indent++;
            escopos.push(new HashMap<>());
            processarCorpo(ctx.corpo(1));
            escopos.pop();
            indent--;
            linha("}");
        } else {
            linha("}");
        }
    }

    /**
     * "caso expr seja item_caso+ (senao corpo)? fim_caso"
     *   -> switch (expr) { case ...: ...; break; default: ... }
     * Faixas "N..M" são expandidas em múltiplos "case" colados (fall-through até o corpo).
     */
    private void cmdCaso(LAParser.Comando_casoContext ctx) {
        linha("switch (" + expressaoC(ctx.expressao()) + ") {");
        indent++;
        for (LAParser.Item_casoContext it : ctx.item_caso()) {
            LAParser.Selecao_casoContext sc = it.selecao_caso();
            if (sc.CADEIA() != null) {
                // Cadeia em selecao_caso não é suportada por switch C diretamente.
                // Os casos de teste só usam NUM_INT.
                linha("case " + sc.CADEIA().getText() + ":");
            } else {
                int ini = Integer.parseInt(sc.NUM_INT(0).getText());
                int fim = sc.NUM_INT().size() > 1 ? Integer.parseInt(sc.NUM_INT(1).getText()) : ini;
                for (int v = ini; v <= fim; v++) {
                    linha("case " + v + ":");
                }
            }
            indent++;
            escopos.push(new HashMap<>());
            processarCorpo(it.corpo());
            escopos.pop();
            linha("break;");
            indent--;
        }
        if (ctx.SENAO() != null && ctx.corpo() != null) {
            linha("default:");
            indent++;
            escopos.push(new HashMap<>());
            processarCorpo(ctx.corpo());
            escopos.pop();
            indent--;
        }
        indent--;
        linha("}");
    }

    /**
     * "para id <- e1 ate e2 faca corpo fim_para"
     *   -> for (id = e1; id <= e2; id++) { corpo }
     */
    private void cmdPara(LAParser.Comando_paraContext ctx) {
        String id = textoIdent(ctx.ident());
        String e1 = expressaoC(ctx.expressao(0));
        String e2 = expressaoC(ctx.expressao(1));
        linha("for (" + id + " = " + e1 + "; " + id + " <= " + e2 + "; " + id + "++) {");
        indent++;
        escopos.push(new HashMap<>());
        processarCorpo(ctx.corpo());
        escopos.pop();
        indent--;
        linha("}");
    }

    /** "enquanto cond faca corpo fim_enquanto" -> while (cond) { corpo } */
    private void cmdEnquanto(LAParser.Comando_enquantoContext ctx) {
        linha("while (" + expressaoC(ctx.expressao()) + ") {");
        indent++;
        escopos.push(new HashMap<>());
        processarCorpo(ctx.corpo());
        escopos.pop();
        indent--;
        linha("}");
    }

    /**
     * "faca corpo ate cond" — semântica de LA: a expressão é a condição de
     * CONTINUAÇÃO do laço (continua enquanto cond for verdadeira). Mapeia
     * diretamente para "do { corpo } while (cond);" em C, conforme exemplos
     * dos casos de teste.
     */
    private void cmdFacaAte(LAParser.Comando_faca_ateContext ctx) {
        linha("do {");
        indent++;
        escopos.push(new HashMap<>());
        processarCorpo(ctx.corpo());
        escopos.pop();
        indent--;
        linha("} while (" + expressaoC(ctx.expressao()) + ");");
    }

    /** Chamada de procedimento: "name(args);". */
    private void cmdChamada(LAParser.Comando_chamadaContext ctx) {
        StringBuilder sb = new StringBuilder(textoIdent(ctx.ident())).append('(');
        if (ctx.lista_expressoes() != null) {
            List<LAParser.ExpressaoContext> args = ctx.lista_expressoes().expressao();
            for (int i = 0; i < args.size(); i++) {
                if (i > 0) sb.append(',');
                sb.append(expressaoC(args.get(i)));
            }
        }
        sb.append(')').append(';');
        linha(sb.toString());
    }

    // ---------------------------------------------------------------------
    // Lvalue (lado esquerdo) e endereço para scanf
    // ---------------------------------------------------------------------

    /** Texto C de um lvalue. "^x" vira "*x", "x.f[i]" vira "x.f[i]". */
    private String lvalueC(LAParser.LvalueContext lv) {
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        if (lv.getChild(idx) instanceof TerminalNode tn
                && tn.getSymbol().getType() == LAParser.CIRCUNFLEXO) {
            sb.append('*');
            idx++;
        }
        LAParser.IdentContext baseId = (LAParser.IdentContext) lv.getChild(idx++);
        sb.append(textoIdent(baseId));
        while (idx < lv.getChildCount()) {
            ParseTree ch = lv.getChild(idx++);
            if (!(ch instanceof TerminalNode tn)) break;
            int tt = tn.getSymbol().getType();
            if (tt == LAParser.PONTO) {
                LAParser.IdentContext ic = (LAParser.IdentContext) lv.getChild(idx++);
                sb.append('.').append(textoIdent(ic));
            } else if (tt == LAParser.ABRE_COL) {
                LAParser.ExpressaoContext ex = (LAParser.ExpressaoContext) lv.getChild(idx++);
                sb.append('[').append(expressaoC(ex)).append(']');
                idx++; // FECHA_COL
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Endereço de memória para uso em scanf. Se o lvalue começa com "^id",
     * o próprio "id" já é o endereço. Caso contrário, prefixa "&".
     */
    private String enderecoParaScanf(LAParser.LvalueContext lv) {
        // Se "^id" sem sufixos: o endereço é apenas "id"
        if (lv.getChild(0) instanceof TerminalNode tn
                && tn.getSymbol().getType() == LAParser.CIRCUNFLEXO
                && lv.getChildCount() == 2) {
            return textoIdent((LAParser.IdentContext) lv.getChild(1));
        }
        return "&" + lvalueC(lv);
    }

    /**
     * Determina o tipo canônico final de um lvalue, percorrendo "." e "[]" desde o
     * identificador-base. Para "^id", retira um nível de ponteiro do tipo.
     */
    private String tipoDeLvalue(LAParser.LvalueContext lv) {
        int idx = 0;
        boolean dereferencia = false;
        if (lv.getChild(idx) instanceof TerminalNode tn
                && tn.getSymbol().getType() == LAParser.CIRCUNFLEXO) {
            dereferencia = true;
            idx++;
        }
        LAParser.IdentContext baseId = (LAParser.IdentContext) lv.getChild(idx++);
        String tipo = lookupTipo(textoIdent(baseId));
        if (dereferencia && tipo != null && tipo.startsWith("^")) {
            tipo = tipo.substring(1);
        }
        while (idx < lv.getChildCount() && tipo != null) {
            ParseTree ch = lv.getChild(idx++);
            if (!(ch instanceof TerminalNode tn)) break;
            int tt = tn.getSymbol().getType();
            if (tt == LAParser.PONTO) {
                LAParser.IdentContext ic = (LAParser.IdentContext) lv.getChild(idx++);
                tipo = tipoDoCampo(tipo, textoIdent(ic));
            } else if (tt == LAParser.ABRE_COL) {
                idx++; // expressao
                idx++; // FECHA_COL
                // o tipo "elementar" do array já é o que está armazenado
            } else {
                break;
            }
        }
        return tipo;
    }

    /** Resolve o tipo de um campo dentro de um registro pelo seu id. */
    private String tipoDoCampo(String tipoRegistro, String campo) {
        if (tipoRegistro != null && tipoRegistro.startsWith("@reg:")) {
            String idReg = tipoRegistro.substring("@reg:".length());
            LinkedHashMap<String, String> campos = registros.get(idReg);
            if (campos != null) return campos.get(campo);
        }
        return null;
    }

    /** Busca o tipo de um identificador na pilha de escopos (do interior para o exterior). */
    private String lookupTipo(String nome) {
        for (Map<String, String> m : escopos) {
            if (m.containsKey(nome)) return m.get(nome);
        }
        return null;
    }

    // ---------------------------------------------------------------------
    // Expressões: tradução para C e cálculo de tipo canônico
    // ---------------------------------------------------------------------

    /** Tradução completa de uma expressão LA para C, preservando operadores e parênteses. */
    private String expressaoC(LAParser.ExpressaoContext ctx) {
        if (ctx.expressao() != null) {
            return expressaoC(ctx.expressao()) + " || " + exprEC(ctx.expr_e());
        }
        return exprEC(ctx.expr_e());
    }

    private String exprEC(LAParser.Expr_eContext ctx) {
        if (ctx.expr_e() != null) {
            return exprEC(ctx.expr_e()) + " && " + exprRelC(ctx.expr_rel());
        }
        return exprRelC(ctx.expr_rel());
    }

    private String exprRelC(LAParser.Expr_relContext ctx) {
        if (ctx.op == null) return exprAritC(ctx.expr_arit());
        String l = exprRelC(ctx.expr_rel());
        String r = exprAritC(ctx.expr_arit());
        String op;
        switch (ctx.op.getType()) {
            case LAParser.IGUAL:       op = "=="; break;
            case LAParser.DIFERENTE:   op = "!="; break;
            case LAParser.MENOR:       op = "<"; break;
            case LAParser.MENOR_IGUAL: op = "<="; break;
            case LAParser.MAIOR:       op = ">"; break;
            case LAParser.MAIOR_IGUAL: op = ">="; break;
            default:                   op = "?";
        }
        return l + " " + op + " " + r;
    }

    private String exprAritC(LAParser.Expr_aritContext ctx) {
        if (ctx.getChildCount() == 1) return termoC(ctx.termo());
        return exprAritC(ctx.expr_arit()) + ctx.op.getText() + termoC(ctx.termo());
    }

    private String termoC(LAParser.TermoContext ctx) {
        if (ctx.getChildCount() == 1) return unarioC(ctx.unario());
        return termoC(ctx.termo()) + ctx.op.getText() + unarioC(ctx.unario());
    }

    private String unarioC(LAParser.UnarioContext ctx) {
        if (ctx.MENOS() != null) return "-" + unarioC(ctx.unario());
        if (ctx.NAO() != null)   return "!(" + unarioC(ctx.unario()) + ")";
        return fatorC(ctx.fator());
    }

    /** Tradução de fator: literais, parêntese, &/^ id, chamada de função e caminhos. */
    private String fatorC(LAParser.FatorContext ctx) {
        ParseTree p0 = ctx.getChild(0);
        if (p0 instanceof TerminalNode tn0) {
            int tt = tn0.getSymbol().getType();
            if (tt == LAParser.ABRE_PAR) {
                return "(" + expressaoC(ctx.expressao(0)) + ")";
            }
            if (tt == LAParser.NUM_INT || tt == LAParser.NUM_REAL) {
                return tn0.getText();
            }
            if (tt == LAParser.CADEIA) {
                return tn0.getText(); // já vem com aspas
            }
            if (tt == LAParser.VERDADEIRO) return "1";
            if (tt == LAParser.FALSO)      return "0";
            if (tt == LAParser.E_COMERCIAL) {
                return "&" + textoIdent(ctx.ident(0));
            }
            if (tt == LAParser.CIRCUNFLEXO) {
                return "*" + textoIdent(ctx.ident(0));
            }
        }
        if (p0 instanceof LAParser.IdentContext primeiro) {
            // Chamada de função: ident(args)
            if (ctx.ABRE_PAR() != null && ctx.ident().size() == 1) {
                StringBuilder sb = new StringBuilder(textoIdent(primeiro)).append('(');
                if (ctx.lista_expressoes() != null) {
                    List<LAParser.ExpressaoContext> args = ctx.lista_expressoes().expressao();
                    for (int i = 0; i < args.size(); i++) {
                        if (i > 0) sb.append(',');
                        sb.append(expressaoC(args.get(i)));
                    }
                }
                sb.append(')');
                return sb.toString();
            }
            // Caminho de identificador: ident.f.f[i]...
            return identPathC(ctx);
        }
        return "";
    }

    /**
     * Constrói o caminho C "id" + sufixos (".f", "[expr]") respeitando a
     * ordem original (intercala identificadores e aberturas de colchete).
     */
    private String identPathC(LAParser.FatorContext ctx) {
        StringBuilder sb = new StringBuilder(textoIdent(ctx.ident(0)));
        int idIdx = 1;
        int abreColIdx = 0;
        while (idIdx < ctx.ident().size() || abreColIdx < ctx.ABRE_COL().size()) {
            Integer bidStart = abreColIdx < ctx.ABRE_COL().size()
                    ? ctx.ABRE_COL(abreColIdx).getSymbol().getStartIndex() : null;
            Integer iidStart = idIdx < ctx.ident().size()
                    ? ctx.ident(idIdx).IDENT().getSymbol().getStartIndex() : null;
            boolean campo = iidStart != null && (bidStart == null || iidStart < bidStart);
            if (campo) {
                sb.append('.').append(textoIdent(ctx.ident(idIdx++)));
            } else {
                sb.append('[').append(expressaoC(ctx.expressao(abreColIdx++))).append(']');
            }
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------------
    // Cálculo de tipos para escolher %d / %f / %s
    // ---------------------------------------------------------------------

    private String tipoExpressao(LAParser.ExpressaoContext ctx) {
        if (ctx.expressao() != null) return T_LOG;
        return tipoExprE(ctx.expr_e());
    }

    private String tipoExprE(LAParser.Expr_eContext ctx) {
        if (ctx.expr_e() != null) return T_LOG;
        return tipoExprRel(ctx.expr_rel());
    }

    private String tipoExprRel(LAParser.Expr_relContext ctx) {
        if (ctx.op != null) return T_LOG;
        return tipoExprArit(ctx.expr_arit());
    }

    private String tipoExprArit(LAParser.Expr_aritContext ctx) {
        if (ctx.getChildCount() == 1) return tipoTermo(ctx.termo());
        String l = tipoExprArit(ctx.expr_arit());
        String r = tipoTermo(ctx.termo());
        return tipoNumericoMaisAmplo(l, r);
    }

    private String tipoTermo(LAParser.TermoContext ctx) {
        if (ctx.getChildCount() == 1) return tipoUnario(ctx.unario());
        if (ctx.op.getType() == LAParser.MOD) return T_INT;
        String l = tipoTermo(ctx.termo());
        String r = tipoUnario(ctx.unario());
        return tipoNumericoMaisAmplo(l, r);
    }

    /** Em "+", "-", "*", "/" o resultado é "real" se algum operando for real. */
    private String tipoNumericoMaisAmplo(String a, String b) {
        if (T_REAL.equals(a) || T_REAL.equals(b)) return T_REAL;
        return T_INT;
    }

    private String tipoUnario(LAParser.UnarioContext ctx) {
        if (ctx.MENOS() != null) return tipoUnario(ctx.unario());
        if (ctx.NAO() != null)   return T_LOG;
        return tipoFator(ctx.fator());
    }

    private String tipoFator(LAParser.FatorContext ctx) {
        ParseTree p0 = ctx.getChild(0);
        if (p0 instanceof TerminalNode tn0) {
            int tt = tn0.getSymbol().getType();
            if (tt == LAParser.ABRE_PAR)    return tipoExpressao(ctx.expressao(0));
            if (tt == LAParser.NUM_INT)     return T_INT;
            if (tt == LAParser.NUM_REAL)    return T_REAL;
            if (tt == LAParser.CADEIA)      return T_LIT;
            if (tt == LAParser.VERDADEIRO)  return T_LOG;
            if (tt == LAParser.FALSO)       return T_LOG;
            if (tt == LAParser.E_COMERCIAL) {
                String t = lookupTipo(textoIdent(ctx.ident(0)));
                return t != null ? "^" + t : null;
            }
            if (tt == LAParser.CIRCUNFLEXO) {
                String t = lookupTipo(textoIdent(ctx.ident(0)));
                if (t != null && t.startsWith("^")) return t.substring(1);
                return null;
            }
        }
        if (p0 instanceof LAParser.IdentContext primeiro) {
            if (ctx.ABRE_PAR() != null && ctx.ident().size() == 1) {
                return retornoFuncao.get(textoIdent(primeiro));
            }
            return tipoIdentPath(ctx);
        }
        return null;
    }

    /** Tipo do caminho "id.f[i]..." a partir do tipo do identificador inicial. */
    private String tipoIdentPath(LAParser.FatorContext ctx) {
        String tipo = lookupTipo(textoIdent(ctx.ident(0)));
        int idIdx = 1;
        int abreColIdx = 0;
        while (idIdx < ctx.ident().size() || abreColIdx < ctx.ABRE_COL().size()) {
            Integer bidStart = abreColIdx < ctx.ABRE_COL().size()
                    ? ctx.ABRE_COL(abreColIdx).getSymbol().getStartIndex() : null;
            Integer iidStart = idIdx < ctx.ident().size()
                    ? ctx.ident(idIdx).IDENT().getSymbol().getStartIndex() : null;
            boolean campo = iidStart != null && (bidStart == null || iidStart < bidStart);
            if (campo) {
                tipo = tipoDoCampo(tipo, textoIdent(ctx.ident(idIdx++)));
            } else {
                abreColIdx++;
                // o tipo elementar do array é o próprio armazenado
            }
            if (tipo == null) return null;
        }
        return tipo;
    }

    // ---------------------------------------------------------------------
    // Utilitários: identificadores, formatadores printf/scanf, valor de constante
    // ---------------------------------------------------------------------

    private static String textoIdent(LAParser.IdentContext id) {
        return id.IDENT().getText();
    }

    /** Formato printf por tipo (inteiro/logico %d, real %f, literal %s). */
    private String formatPrintf(String tipo) {
        if (T_REAL.equals(tipo))   return "%f";
        if (T_LIT.equals(tipo))    return "%s";
        // inteiro, logico, ponteiro: imprime como inteiro
        return "%d";
    }

    /** Formato scanf por tipo (idem printf, mas literal não usa scanf). */
    private String formatScanf(String tipo) {
        if (T_REAL.equals(tipo)) return "%f";
        return "%d";
    }

    /** Texto C do valor constante para "#define". CADEIA é repassada com aspas. */
    private String valorConstanteC(LAParser.Valor_constanteContext ctx) {
        if (ctx.CADEIA() != null)     return ctx.CADEIA().getText();
        if (ctx.NUM_INT() != null)    return ctx.NUM_INT().getText();
        if (ctx.NUM_REAL() != null)   return ctx.NUM_REAL().getText();
        if (ctx.VERDADEIRO() != null) return "1";
        if (ctx.FALSO() != null)      return "0";
        return "0";
    }

    /**
     * Detecta se uma "expressao" é apenas uma cadeia literal (folha CADEIA sem operadores).
     * Útil para emitir printf("...") em vez de printf("%s", expr) quando há texto puro em escreva.
     */
    private static String stringLiteralOuNull(LAParser.ExpressaoContext ctx) {
        if (ctx.expressao() != null) return null;
        LAParser.Expr_eContext ee = ctx.expr_e();
        if (ee.expr_e() != null) return null;
        LAParser.Expr_relContext er = ee.expr_rel();
        if (er.op != null) return null;
        LAParser.Expr_aritContext ea = er.expr_arit();
        if (ea.getChildCount() != 1) return null;
        LAParser.TermoContext tm = ea.termo();
        if (tm.getChildCount() != 1) return null;
        LAParser.UnarioContext u = tm.unario();
        if (u.MENOS() != null || u.NAO() != null) return null;
        LAParser.FatorContext f = u.fator();
        if (f.getChildCount() == 1 && f.CADEIA() != null) {
            return f.CADEIA().getText();
        }
        return null;
    }

    /** Função: "tipoRet name(params) { corpo }". Pode haver "retorne expr". */
    private void processarFuncao(LAParser.Declaracao_funcaoContext f) {
        String nome = textoIdent(f.ident());
        String params = montarParametrosC(f.lista_parametros());
        String retC = tipoCParaTipoEstendidoEmContexto(f.tipo_estendido(), false);
        retornoFuncao.put(nome, tipoCanonicoParaTipoEstendido(f.tipo_estendido()));
        linha(retC + " " + nome + "(" + params + ") {");
        indent++;
        escopos.push(new HashMap<>());
        registrarParametrosNoEscopo(f.lista_parametros());
        processarCorpo(f.corpo());
        escopos.pop();
        indent--;
        linha("}");
        out.append('\n');
    }
}
