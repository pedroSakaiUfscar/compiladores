package br.ufscar.dc.compiladores.culina;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class AnalisadorSemantico extends CULINABaseVisitor<Void> {

    private static final int TEMP_MIN_ASSE = 50;
    private static final int TEMP_MAX_ASSE = 300;

    private final RelatorioErros relatorio;
    private final Map<String, TipoSimbolo> tabela = new HashMap<>();

    private enum TipoSimbolo {
        INGREDIENTE,
        TEMPERO
    }

    public AnalisadorSemantico(RelatorioErros relatorio) {
        this.relatorio = relatorio;
    }

    @Override
    public Void visitReceita(CULINAParser.ReceitaContext ctx) {
        tabela.clear();
        super.visitReceita(ctx);

        int totalDeclarados = ctx.ingredientes().lista_ingredientes().size()
                + ctx.ingredientes().lista_tempero().size();
        if (totalDeclarados == 0) {
            relatorio.adicionar(
                    ctx.ingredientes().getStart(),
                    "receita deve declarar pelo menos um ingrediente ou tempero");
        }

        if (ctx.metodo().cmd().isEmpty()) {
            relatorio.adicionar(ctx.metodo().getStart(), "secao Metodo deve conter pelo menos um comando");
        }

        return null;
    }

    @Override
    public Void visitLista_ingredientes(CULINAParser.Lista_ingredientesContext ctx) {
        String nome = ctx.INGREDIENTE().getText();
        registrarSimbolo(ctx.INGREDIENTE(), nome, TipoSimbolo.INGREDIENTE);
        return null;
    }

    @Override
    public Void visitLista_tempero(CULINAParser.Lista_temperoContext ctx) {
        String nome = ctx.TEMPERO().getText();
        registrarSimbolo(ctx.TEMPERO(), nome, TipoSimbolo.TEMPERO);
        return null;
    }

    @Override
    public Void visitCmdAsse(CULINAParser.CmdAsseContext ctx) {
        verificarIngredienteDeclarado(ctx.INGREDIENTE());
        int temperatura = Integer.parseInt(ctx.NUMERO().getText());
        if (temperatura < TEMP_MIN_ASSE || temperatura > TEMP_MAX_ASSE) {
            relatorio.adicionar(
                    ctx.NUMERO().getSymbol(),
                    "temperatura de forno deve estar entre "
                            + TEMP_MIN_ASSE
                            + " e "
                            + TEMP_MAX_ASSE
                            + " graus Celsius");
        }
        return null;
    }

    @Override
    public Void visitCmdCozinhe(CULINAParser.CmdCozinheContext ctx) {
        verificarIngredienteDeclarado(ctx.INGREDIENTE());
        return null;
    }

    @Override
    public Void visitCmdCorte(CULINAParser.CmdCorteContext ctx) {
        verificarIngredienteDeclarado(ctx.INGREDIENTE());
        return null;
    }

    @Override
    public Void visitCmdMisture(CULINAParser.CmdMistureContext ctx) {
        if (ctx.INGREDIENTE().size() < 2) {
            relatorio.adicionar(ctx.MISTURE().getSymbol(), "comando misture exige pelo menos dois ingredientes");
        }
        for (TerminalNode ing : ctx.INGREDIENTE()) {
            verificarIngredienteDeclarado(ing);
        }
        return null;
    }

    @Override
    public Void visitCmdBata(CULINAParser.CmdBataContext ctx) {
        if (ctx.INGREDIENTE().size() < 2) {
            relatorio.adicionar(ctx.BATA().getSymbol(), "comando bata exige pelo menos dois ingredientes");
        }
        for (TerminalNode ing : ctx.INGREDIENTE()) {
            verificarIngredienteDeclarado(ing);
        }
        return null;
    }

    private void registrarSimbolo(TerminalNode no, String nome, TipoSimbolo tipo) {
        if (tabela.containsKey(nome)) {
            relatorio.adicionar(no.getSymbol(), tipo == TipoSimbolo.INGREDIENTE
                    ? "ingrediente " + nome + " ja declarado"
                    : "tempero " + nome + " ja declarado");
            return;
        }
        tabela.put(nome, tipo);
    }

    private void verificarIngredienteDeclarado(TerminalNode ing) {
        String nome = ing.getText();
        if (!tabela.containsKey(nome)) {
            relatorio.adicionar(ing.getSymbol(), "ingrediente " + nome + " nao declarado");
            return;
        }
        if (tabela.get(nome) != TipoSimbolo.INGREDIENTE) {
            relatorio.adicionar(ing.getSymbol(), nome + " e um tempero, nao um ingrediente");
        }
    }
}
