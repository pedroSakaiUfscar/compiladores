package br.ufscar.dc.compiladores.la.lexico;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
public class Principal {

    /** Tipos cujo segundo campo na saída é o nome simbólico (não o lexema repetido). */
    private static boolean segundoCampoEhNomeSimbolico(String nomeRegra) {
        return "IDENT".equals(nomeRegra)
                || "NUM_INT".equals(nomeRegra)
                || "NUM_REAL".equals(nomeRegra)
                || "CADEIA".equals(nomeRegra);
    }

    /** Coloca o texto entre aspas simples, como nos exemplos */
    private static String aspasSimples(String s) {
        return "'" + s + "'";
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java -jar ... <arquivo_entrada> <arquivo_saida>");
            System.exit(1);
        }
        String arquivoEntrada = args[0];
        String arquivoSaida = args[1];

        try (PrintWriter pw = new PrintWriter(arquivoSaida, StandardCharsets.UTF_8)) {
            CharStream cs = CharStreams.fromFileName(arquivoEntrada, StandardCharsets.UTF_8);
            LALexer lexer = new LALexer(cs);

            while (true) {
                Token t = lexer.nextToken();
                if (t.getType() == Token.EOF) {
                    break;
                }

                String nome = LALexer.VOCABULARY.getSymbolicName(t.getType());
                if (nome == null) {
                    nome = LALexer.VOCABULARY.getLiteralName(t.getType());
                }
                if (nome == null) {
                    nome = String.valueOf(t.getType());
                }

                // --- Erros léxicos especiais ---
                if ("COMENTARIO_NAO_FECHADO".equals(nome)) {
                    pw.println("Linha " + t.getLine() + ": comentario nao fechado");
                    break;
                }
                if ("CADEIA_NAO_FECHADA".equals(nome)) {
                    pw.println("Linha " + t.getLine() + ": cadeia literal nao fechada");
                    break;
                }
                if ("SIMBOLO_NAO_IDENTIFICADO".equals(nome)) {
                    String c = t.getText();
                    pw.println("Linha " + t.getLine() + ": " + c + " - simbolo nao identificado");
                    break;
                }

                // --- Saída ---
                String lexema = t.getText();
                String primeiro = aspasSimples(lexema);
                String segundo;
                if (segundoCampoEhNomeSimbolico(nome)) {
                    segundo = nome;
                } else {
                    segundo = aspasSimples(lexema);
                }
                pw.println("<" + primeiro + "," + segundo + ">");
            }
        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
            System.exit(1);
        }
    }
}
