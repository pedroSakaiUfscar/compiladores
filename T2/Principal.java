package br.ufscar.dc.compiladores.la.sintatico;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class Principal {

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
                if (emitirErroLexicoSeHouver(texto, pw)) {
                    pw.println(FIM);
                    return;
                }

                CharStream cs = CharStreams.fromString(texto);
                LALexer lexer = new LALexer(cs);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                LAParser parser = new LAParser(tokens);

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
                    if (pc.secao_principal() instanceof LAParser.PrincipalSemPalavraAlgoritmoContext sem) {
                        int linha = primeiraLinhaDeComando(sem.corpo());
                        String prox = textoProximoAoFaltaAlgoritmo(sem.corpo());
                        pw.println("Linha " + linha + ": erro sintatico proximo a " + prox);
                        pw.println(FIM);
                        return;
                    }
                }

                pw.println(FIM);
            }
        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
            System.exit(1);
        }
    }

    /** Pré-análise léxica (mensagens conforme casos T2). */
    private static boolean emitirErroLexicoSeHouver(String texto, PrintWriter pw) {
        CharStream cs = CharStreams.fromString(texto);
        LALexer lexer = new LALexer(cs);
        while (true) {
            Token t = lexer.nextToken();
            if (t.getType() == Token.EOF) {
                break;
            }
            if (t.getType() == LALexer.COMENTARIO_NAO_FECHADO) {
                pw.println("Linha " + t.getLine() + ": comentario nao fechado");
                return true;
            }
            if (t.getType() == LALexer.CADEIA_NAO_FECHADA) {
                pw.println("Linha " + t.getLine() + ": cadeia literal nao fechada");
                return true;
            }
            if (t.getType() == LALexer.SIMBOLO_NAO_IDENTIFICADO) {
                String c = t.getText();
                pw.println("Linha " + t.getLine() + ": " + c + " - simbolo nao identificado");
                return true;
            }
        }
        return false;
    }

    private static int primeiraLinhaDeComando(LAParser.CorpoContext corpo) {
        for (int i = 0; i < corpo.getChildCount(); i++) {
            if (corpo.getChild(i) instanceof LAParser.ComandoContext c) {
                return c.getStart().getLine();
            }
        }
        return corpo.getStop() != null ? corpo.getStop().getLine() : 1;
    }

    /**
     * Lexema amigável do primeiro comando quando falta {@code algoritmo}
     * (caso 1 do LEIA_ME: geralmente {@code leia}, {@code escreva}, etc.).
     */
    private static String textoProximoAoFaltaAlgoritmo(LAParser.CorpoContext corpo) {
        for (int i = 0; i < corpo.getChildCount(); i++) {
            if (corpo.getChild(i) instanceof LAParser.ComandoContext c) {
                return lexemaAmigavelPrimeiroTokenComando(c);
            }
        }
        return "EOF";
    }

    private static String lexemaAmigavelPrimeiroTokenComando(LAParser.ComandoContext c) {
        TerminalNode tn = primeiroTerminal(c);
        if (tn == null) {
            return "EOF";
        }
        return humanizarToken(tn.getSymbol());
    }

    private static TerminalNode primeiroTerminal(ParseTree t) {
        if (t instanceof TerminalNode tn) {
            return tn;
        }
        for (int i = 0; i < t.getChildCount(); i++) {
            TerminalNode r = primeiroTerminal(t.getChild(i));
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    private static String humanizarToken(Token t) {
        if (t == null || t.getType() == Token.EOF) {
            return "EOF";
        }
        return t.getText();
    }

    private static final class ErroSintatico extends BaseErrorListener {
        int linha;
        String proximo;

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            if (this.linha > 0) {
                return;
            }
            this.linha = line;
            if (offendingSymbol instanceof Token t) {
                this.proximo = humanizarSintatico(t, (Parser) recognizer);
            } else {
                this.proximo = "EOF";
            }
        }

        private static String humanizarSintatico(Token t, Parser parser) {
            if (t.getType() == Token.EOF) {
                return "EOF";
            }
            return t.getText();
        }
    }
}
