package br.ufscar.dc.compiladores.culina;

public final class GeradorHTML extends CULINABaseVisitor<Void> {

    private final StringBuilder saida = new StringBuilder();
    private int passo = 0;

    public String gerar(CULINAParser.ReceitaContext ctx) {
        saida.setLength(0);
        passo = 0;
        visitReceita(ctx);
        return saida.toString();
    }

    @Override
    public Void visitReceita(CULINAParser.ReceitaContext ctx) {
        String titulo = desquote(ctx.nome().CADEIA().getText());

        saida.append("<!DOCTYPE html>\n<html lang=\"pt-BR\">\n<head>\n");
        saida.append("<meta charset=\"UTF-8\">\n");
        saida.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        saida.append("<title>").append(escape(titulo)).append("</title>\n");
        saida.append("<style>\n");
        saida.append("body{font-family:Georgia,serif;max-width:760px;margin:2rem auto;padding:0 1rem;");
        saida.append("color:#2c1810;background:#fffaf5;line-height:1.6}\n");
        saida.append("h1{color:#8b2500;border-bottom:3px solid #d4a574;padding-bottom:.5rem}\n");
        saida.append("h2{color:#5c3d2e;margin-top:2rem}\n");
        saida.append(".meta{background:#f5e6d3;padding:1rem;border-radius:8px;margin:1rem 0}\n");
        saida.append(".capa{display:block;max-width:100%;border-radius:8px;margin:1rem auto}\n");
        saida.append("ul.ingredientes{list-style:square;padding-left:1.5rem}\n");
        saida.append("ol.passos{padding-left:1.5rem}\n");
        saida.append("ol.passos li{margin-bottom:.75rem}\n");
        saida.append(".intro{color:#555;font-style:italic}\n");
        saida.append("footer{margin-top:3rem;font-size:.85rem;color:#888;text-align:center}\n");
        saida.append("</style>\n</head>\n<body>\n");

        saida.append("<h1>").append(escape(titulo)).append("</h1>\n");

        if (ctx.capa() != null) {
            String img = desquote(ctx.capa().CADEIA().getText());
            saida.append("<img class=\"capa\" src=\"").append(escape(img)).append("\" alt=\"")
                    .append(escape(titulo))
                    .append("\">\n");
        }

        saida.append("<div class=\"meta\">\n<p><strong>Tempo de preparo:</strong> ");
        for (int i = 0; i < ctx.tempo_prep().tempo().size(); i++) {
            if (i > 0) {
                saida.append(" + ");
            }
            CULINAParser.TempoContext t = ctx.tempo_prep().tempo(i);
            saida.append(t.NUMERO().getText()).append(' ').append(t.unidade_tempo().getText());
        }
        saida.append("</p>\n");

        if (ctx.porcoes() != null) {
            saida.append("<p><strong>Porções:</strong> ")
                    .append(ctx.porcoes().NUMERO().getText())
                    .append("</p>\n");
        }
        saida.append("</div>\n");

        for (CULINAParser.ParagrafoContext par : ctx.paragrafo()) {
            saida.append("<p class=\"intro\">")
                    .append(escape(desquote(par.CADEIA().getText())))
                    .append("</p>\n");
        }

        saida.append("<h2>Ingredientes</h2>\n<ul class=\"ingredientes\">\n");
        visitIngredientes(ctx.ingredientes());
        saida.append("</ul>\n");

        saida.append("<h2>Modo de preparo</h2>\n<ol class=\"passos\">\n");
        visitMetodo(ctx.metodo());
        saida.append("</ol>\n");

        saida.append("<footer>Gerado pelo compilador CULINA — UFSCar Compiladores T6</footer>\n");
        saida.append("</body>\n</html>\n");
        return null;
    }

    @Override
    public Void visitIngredientes(CULINAParser.IngredientesContext ctx) {
        for (CULINAParser.Lista_ingredientesContext ing : ctx.lista_ingredientes()) {
            visitLista_ingredientes(ing);
        }
        for (CULINAParser.Lista_temperoContext temp : ctx.lista_tempero()) {
            visitLista_tempero(temp);
        }
        return null;
    }

    @Override
    public Void visitLista_ingredientes(CULINAParser.Lista_ingredientesContext ctx) {
        saida.append("<li>");
        if (ctx.medida_liq() != null) {
            saida.append(formatarMedidaLiq(ctx.medida_liq()));
        } else {
            saida.append(formatarMedidaSolido(ctx.medida_solido()));
        }
        saida.append(" de ").append(ctx.INGREDIENTE().getText()).append("</li>\n");
        return null;
    }

    @Override
    public Void visitLista_tempero(CULINAParser.Lista_temperoContext ctx) {
        saida.append("<li>");
        if (ctx.PITADA() != null) {
            saida.append("Uma pitada de ").append(ctx.TEMPERO().getText());
        } else if (ctx.A_GOSTO() != null) {
            saida.append(ctx.TEMPERO().getText()).append(" a gosto");
        } else {
            saida.append(ctx.NUMERO().getText());
            if (ctx.getChild(1).getText().equals("g")) {
                saida.append("g de ");
            } else if (ctx.tipo_colher() != null) {
                saida.append(" colher(es) de ")
                        .append(ctx.tipo_colher().getText())
                        .append(" de ");
            } else {
                saida.append(" xícara(s) de ");
            }
            saida.append(ctx.TEMPERO().getText());
        }
        saida.append("</li>\n");
        return null;
    }

    @Override
    public Void visitMetodo(CULINAParser.MetodoContext ctx) {
        for (CULINAParser.CmdContext cmd : ctx.cmd()) {
            visitCmd(cmd);
        }
        return null;
    }

    @Override
    public Void visitCmdAsse(CULINAParser.CmdAsseContext ctx) {
        emitirPasso("Asse "
                + ctx.INGREDIENTE().getText()
                + " por "
                + formatarTempo(ctx.tempo())
                + " a "
                + ctx.NUMERO().getText()
                + " °C.");
        return null;
    }

    @Override
    public Void visitCmdCozinhe(CULINAParser.CmdCozinheContext ctx) {
        emitirPasso("Cozinhe " + ctx.INGREDIENTE().getText() + " por " + formatarTempo(ctx.tempo()) + ".");
        return null;
    }

    @Override
    public Void visitCmdMisture(CULINAParser.CmdMistureContext ctx) {
        StringBuilder sb = new StringBuilder("Misture ");
        for (int i = 0; i < ctx.INGREDIENTE().size(); i++) {
            if (i > 0) {
                sb.append(i == ctx.INGREDIENTE().size() - 1 ? " e " : ", ");
            }
            sb.append(ctx.INGREDIENTE(i).getText());
        }
        sb.append('.');
        emitirPasso(sb.toString());
        return null;
    }

    @Override
    public Void visitCmdCorte(CULINAParser.CmdCorteContext ctx) {
        emitirPasso(capitalize(ctx.tipo_corte().getText())
                + " o ingrediente "
                + ctx.INGREDIENTE().getText()
                + ".");
        return null;
    }

    @Override
    public Void visitCmdBata(CULINAParser.CmdBataContext ctx) {
        StringBuilder sb = new StringBuilder("Bata ");
        for (int i = 0; i < ctx.INGREDIENTE().size(); i++) {
            if (i > 0) {
                sb.append(i == ctx.INGREDIENTE().size() - 1 ? " e " : ", ");
            }
            sb.append(ctx.INGREDIENTE(i).getText());
        }
        sb.append('.');
        emitirPasso(sb.toString());
        return null;
    }

    @Override
    public Void visitCmdDescanse(CULINAParser.CmdDescanseContext ctx) {
        emitirPasso("Deixe descansar por " + formatarTempo(ctx.tempo()) + ".");
        return null;
    }

    @Override
    public Void visitCmdPasso(CULINAParser.CmdPassoContext ctx) {
        emitirPasso(desquote(ctx.CADEIA().getText()));
        return null;
    }

    @Override
    public Void visitCmdPaoDeLo(CULINAParser.CmdPaoDeLoContext ctx) {
        emitirPasso("Quebre 4 ovos, separe as claras das gemas.");
        emitirPasso("Bata as claras em neve e reserve.");
        emitirPasso("Adicione as gemas com uma xícara de água morna e bata até espumar.");
        emitirPasso("Acrescente 2 xícaras de chá de açúcar e bata até misturar.");
        emitirPasso("Adicione 2 xícaras de chá de farinha de trigo, 1 colher de sopa de fermento em pó e misture.");
        emitirPasso("Incorpore as claras em neve, despeje em forma untada e asse por 30 min a 180 °C.");
        return null;
    }

    @Override
    public Void visitCmdCobertura(CULINAParser.CmdCoberturaContext ctx) {
        emitirPasso("Em uma panela, misture leite, chocolate em pó e margarina.");
        emitirPasso("Leve ao fogo médio até ferver, acrescente leite condensado e mexa até engrossar.");
        return null;
    }

    @Override
    public Void visitCmdArroz(CULINAParser.CmdArrozContext ctx) {
        emitirPasso("Refogue cebola e alho, adicione o arroz e refogue até ficar semi-transparente.");
        emitirPasso("Acrescente água e temperos, cozinhe até secar; adicione mais água se necessário.");
        return null;
    }

    @Override
    public Void visitCmdRisotto(CULINAParser.CmdRisottoContext ctx) {
        emitirPasso("Refogue cebola e alho, adicione arroz e refogue.");
        emitirPasso("Adicione sal, temperos e vinho; misture até secar.");
        emitirPasso("Adicione caldo concha a concha até o arroz ficar al dente.");
        emitirPasso("Finalize com manteiga e parmesão.");
        return null;
    }

    private void emitirPasso(String texto) {
        passo++;
        saida.append("<li>").append(escape(texto)).append("</li>\n");
    }

    private static String formatarMedidaSolido(CULINAParser.Medida_solidoContext ctx) {
        if (ctx.tipo_colher() != null) {
            return ctx.NUMERO().getText() + " colher(es) de " + ctx.tipo_colher().getText();
        }
        return ctx.getText().replace("xicara", "xícara(s)");
    }

    private static String formatarMedidaLiq(CULINAParser.Medida_liqContext ctx) {
        return ctx.getText().replace("xicara", "xícara(s)");
    }

    private static String formatarTempo(CULINAParser.TempoContext ctx) {
        return ctx.NUMERO().getText() + " " + ctx.unidade_tempo().getText();
    }

    private static String desquote(String cadeia) {
        if (cadeia.length() >= 2 && cadeia.charAt(0) == '"' && cadeia.charAt(cadeia.length() - 1) == '"') {
            return cadeia.substring(1, cadeia.length() - 1);
        }
        return cadeia;
    }

    private static String capitalize(String s) {
        if (s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
