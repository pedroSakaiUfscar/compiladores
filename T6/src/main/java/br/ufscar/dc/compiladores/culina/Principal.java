package br.ufscar.dc.compiladores.culina;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public final class Principal {

    private Principal() {}

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java -jar ... <arquivo_entrada> <arquivo_saida>");
            System.exit(1);
        }

        Path entrada = Path.of(args[0]);
        Path saida = Path.of(args[1]);
        RelatorioErros relatorio = new RelatorioErros();

        try {
            String texto = Files.readString(entrada, StandardCharsets.UTF_8);

            if (emitirErroLexicoSeHouver(texto, relatorio)) {
                gravarRelatorio(saida, relatorio);
                return;
            }

            CharStream cs = CharStreams.fromString(texto);
            CULINALexer lexer = new CULINALexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CULINAParser parser = new CULINAParser(tokens);

            ErroSintatico erroSintatico = new ErroSintatico(relatorio);
            parser.removeErrorListeners();
            parser.addErrorListener(erroSintatico);

            CULINAParser.ReceitaContext arvore = parser.receita();

            if (!relatorio.vazio()) {
                gravarRelatorio(saida, relatorio);
                return;
            }

            AnalisadorSemantico semantico = new AnalisadorSemantico(relatorio);
            semantico.visitReceita(arvore);

            if (!relatorio.vazio()) {
                gravarRelatorio(saida, relatorio);
                return;
            }

            GeradorHTML gerador = new GeradorHTML();
            String html = gerador.gerar(arvore);
            Files.writeString(saida, html, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
            System.exit(1);
        }
    }

    private static boolean emitirErroLexicoSeHouver(String texto, RelatorioErros relatorio) {
        CharStream cs = CharStreams.fromString(texto);
        CULINALexer lexer = new CULINALexer(cs);
        while (true) {
            Token t = lexer.nextToken();
            if (t.getType() == Token.EOF) {
                break;
            }
            if (t.getType() == CULINALexer.SIMBOLO_NAO_IDENTIFICADO) {
                relatorio.adicionar(t, t.getText() + " - simbolo nao identificado");
                return true;
            }
        }
        return false;
    }

    private static void gravarRelatorio(Path saida, RelatorioErros relatorio) throws IOException {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(saida, StandardCharsets.UTF_8))) {
            for (RelatorioErros.Erro e : relatorio.listar()) {
                pw.printf("Erro %d:%d - %s%n", e.linha(), e.coluna(), e.mensagem());
            }
        }
    }

    private static final class ErroSintatico extends BaseErrorListener {

        private final RelatorioErros relatorio;
        private boolean registrado;

        ErroSintatico(RelatorioErros relatorio) {
            this.relatorio = relatorio;
        }

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            if (registrado) {
                return;
            }
            registrado = true;
            String proximo;
            if (offendingSymbol instanceof Token t) {
                proximo = t.getType() == Token.EOF ? "EOF" : t.getText();
            } else {
                proximo = "EOF";
            }
            relatorio.adicionar(line, charPositionInLine, "erro sintatico proximo a " + proximo);
        }
    }
}
