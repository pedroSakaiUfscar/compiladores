package br.ufscar.dc.compiladores.la.semantico;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Ponto de entrada do compilador T3 — análise semântica da linguagem LA.
 *
 * Fluxo de compilação:
 *   1. Pré-análise léxica: detecta comentário não fechado, cadeia não fechada
 *      ou símbolo não identificado antes de acionar o parser.
 *   2. Análise sintática: LALexer + LAParser (gerados pelo ANTLR4 a partir de
 *      LA.g4) constroem a árvore de derivação via parser.programa().
 *   3. Erros sintáticos são capturados por ErroSintatico (apenas o primeiro).
 *   4. Verifica se a palavra-chave "algoritmo" foi omitida na seção principal
 *      (alternativa PrincipalSemPalavraAlgoritmo) e gera erro adequado.
 *   5. Análise semântica: AnalisadorSemantico percorre a árvore validando
 *      tipos, escopo e usos de identificadores. Os erros são impressos
 *      ordenados por linha.
 *
 * Em qualquer cenário (sucesso ou erro) a saída termina com "Fim da compilacao".
 *
 * Uso: java -jar t3.jar <arquivo_entrada> <arquivo_saida>
 */
public final class Principal {

    /** Mensagem de encerramento bem-sucedido da compilação. */
    private static final String FIM = "Fim da compilacao";

    private Principal() {}

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java -jar ... <arquivo_entrada> <arquivo_saida>");
            System.exit(1);
        }
        Path entrada = Path.of(args[0]);
        Path saida = Path.of(args[1]);

        try {
            String texto = Files.readString(entrada, StandardCharsets.UTF_8);

            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(saida, StandardCharsets.UTF_8))) {

                // Etapa 1: pré-análise léxica
                if (emitirErroLexicoSeHouver(texto, pw)) {
                    pw.println(FIM);
                    return;
                }

                // Etapa 2: análise sintática
                CharStream cs = CharStreams.fromString(texto);
                LALexer lexer = new LALexer(cs);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                LAParser parser = new LAParser(tokens);

                // Substitui o listener padrão do ANTLR (que imprime no stderr)
                // pelo nosso, que captura o primeiro erro sintático
                ErroSintatico sint = new ErroSintatico();
                parser.removeErrorListeners();
                parser.addErrorListener(sint);

                ParseTree tree;
                try {
                    tree = parser.programa();
                } catch (Exception e) {
                    if (sint.linha > 0) {
                        pw.println("Linha " + sint.linha + ": erro sintatico proximo a " + sint.proximo);
                        pw.println(FIM);
                        return;
                    }
                    throw e;
                }

                if (sint.linha > 0) {
                    pw.println("Linha " + sint.linha + ": erro sintatico proximo a " + sint.proximo);
                    pw.println(FIM);
                    return;
                }

                if (tree instanceof LAParser.ProgramaContext pc) {
                    // Etapa 3: detecta omissão da palavra-chave "algoritmo"
                    // A gramática aceita essa forma via PrincipalSemPalavraAlgoritmo,
                    // mas ela é sintaticamente incorreta e deve gerar erro
                    if (pc.secao_principal() instanceof LAParser.PrincipalSemPalavraAlgoritmoContext sem) {
                        int linha = primeiraLinhaDeComando(sem.corpo());
                        String prox = textoProximoAoFaltaAlgoritmo(sem.corpo());
                        pw.println("Linha " + linha + ": erro sintatico proximo a " + prox);
                        pw.println(FIM);
                        return;
                    }

                    // Etapa 4: análise semântica
                    // Os erros encontrados são ordenados por linha antes de serem impressos
                    AnalisadorSemantico sem = new AnalisadorSemantico();
                    sem.analisar(pc);
                    sem.getErros().stream()
                            .sorted(Comparator.comparingInt(AnalisadorSemantico.Erro::linha))
                            .forEach(e -> pw.println("Linha " + e.linha() + ": " + e.mensagem()));
                }

                pw.println(FIM);
            }
        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Percorre os tokens do texto-fonte procurando erros léxicos.
     * Retorna true (e emite a mensagem) se algum for encontrado.
     */
    private static boolean emitirErroLexicoSeHouver(String texto, PrintWriter pw) {
        CharStream cs = CharStreams.fromString(texto);
        LALexer lexer = new LALexer(cs);
        while (true) {
            Token t = lexer.nextToken();
            if (t.getType() == Token.EOF) break;
            if (t.getType() == LALexer.COMENTARIO_NAO_FECHADO) {
                pw.println("Linha " + t.getLine() + ": comentario nao fechado");
                return true;
            }
            if (t.getType() == LALexer.CADEIA_NAO_FECHADA) {
                pw.println("Linha " + t.getLine() + ": cadeia literal nao fechada");
                return true;
            }
            if (t.getType() == LALexer.SIMBOLO_NAO_IDENTIFICADO) {
                pw.println(
                        "Linha " + t.getLine() + ": " + t.getText() + " - simbolo nao identificado");
                return true;
            }
        }
        return false;
    }

    /** Retorna a linha do primeiro comando do corpo (usado na mensagem de erro). */
    private static int primeiraLinhaDeComando(LAParser.CorpoContext corpo) {
        for (int i = 0; i < corpo.getChildCount(); i++) {
            if (corpo.getChild(i) instanceof LAParser.ComandoContext c) {
                return c.getStart().getLine();
            }
        }
        return corpo.getStop() != null ? corpo.getStop().getLine() : 1;
    }

    /** Retorna o texto do primeiro token do primeiro comando do corpo. */
    private static String textoProximoAoFaltaAlgoritmo(LAParser.CorpoContext corpo) {
        for (int i = 0; i < corpo.getChildCount(); i++) {
            if (corpo.getChild(i) instanceof LAParser.ComandoContext c) {
                TerminalNode tn = primeiroTerminal(c);
                return tn != null ? humanizarToken(tn.getSymbol()) : "EOF";
            }
        }
        return "EOF";
    }

    /** Busca recursivamente o primeiro nó terminal (folha) na subárvore. */
    private static TerminalNode primeiroTerminal(ParseTree t) {
        if (t instanceof TerminalNode tn) return tn;
        for (int i = 0; i < t.getChildCount(); i++) {
            TerminalNode r = primeiroTerminal(t.getChild(i));
            if (r != null) return r;
        }
        return null;
    }

    /** Retorna o texto do token, ou "EOF" para fim de arquivo. */
    private static String humanizarToken(Token t) {
        if (t == null || t.getType() == Token.EOF) return "EOF";
        return t.getText();
    }

    /**
     * Listener de erro sintático: substitui o listener padrão do ANTLR para
     * capturar apenas o primeiro erro (linha e token infrator).
     */
    private static final class ErroSintatico extends BaseErrorListener {

        int linha;      // linha do primeiro erro (0 = sem erro)
        String proximo; // texto do token que causou o erro

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            if (this.linha > 0) return; // ignora erros subsequentes
            this.linha = line;
            if (offendingSymbol instanceof Token t) {
                this.proximo = t.getType() == Token.EOF ? "EOF" : t.getText();
            } else {
                this.proximo = "EOF";
            }
        }
    }
}
