package br.ufscar.dc.compiladores.culina;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.antlr.v4.runtime.Token;

public final class RelatorioErros {

    public record Erro(int linha, int coluna, String mensagem) {}

    private final List<Erro> erros = new ArrayList<>();

    public void adicionar(Token token, String mensagem) {
        erros.add(new Erro(token.getLine(), token.getCharPositionInLine(), mensagem));
    }

    public void adicionar(int linha, int coluna, String mensagem) {
        erros.add(new Erro(linha, coluna, mensagem));
    }

    public boolean vazio() {
        return erros.isEmpty();
    }

    public List<Erro> listar() {
        erros.sort(Comparator.comparingInt(Erro::linha).thenComparingInt(Erro::coluna));
        return Collections.unmodifiableList(erros);
    }

    public void limpar() {
        erros.clear();
    }
}
