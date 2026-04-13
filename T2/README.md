# T2 — Analisador Sintático da linguagem LA

Este projeto implementa o analisador sintático da linguagem LA usando **ANTLR 4** e **Java**.

## Requisitos

Para compilar e executar este trabalho, é necessário ter instalado:

- **Java JDK 17**
- **Apache Maven 3.8+**
- **ANTLR 4 Runtime 4.13.1**  
  Esta dependência é baixada automaticamente pelo Maven durante a compilação.

> O projeto foi configurado para compilar com `source` e `target` em Java 17.

## Estrutura do projeto

O módulo do trabalho está dentro da pasta `T2/`.  
A raiz do repositório não possui `pom.xml`, então os comandos Maven devem ser executados apontando para `T2/pom.xml` ou dentro da pasta `T2`.

Arquivos principais:

- `src/main/antlr4/br/ufscar/dc/compiladores/la/sintatico/LA.g4`  
  Gramática léxica e sintática da linguagem LA.
- `src/main/java/br/ufscar/dc/compiladores/la/sintatico/Principal.java`  
  Classe principal que lê a entrada, executa a análise sintática e gera a saída formatada.

## Como compilar

Na raiz do repositório, execute:

```bash
cd T2
mvn package -q
```

Isso irá:
1. Gerar `LALexer.java` e `LAParser.java` a partir de `LA.g4` (plugin ANTLR4)
2. Compilar todos os arquivos Java
3. Empacotar o JAR executável com dependências em `target/t2-la-sintatico-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Como executar

```bash
java -jar T2/target/t2-la-sintatico-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo_entrada> <arquivo_saida>
```

- `<arquivo_entrada>`: arquivo-fonte em linguagem LA
- `<arquivo_saida>`: arquivo onde as mensagens de erro (ou `Fim da compilacao`) serão escritas

## Saída esperada

| Situação | Saída |
|---|---|
| Programa correto | `Fim da compilacao` |
| Erro léxico | `Linha N: <mensagem>` + `Fim da compilacao` |
| Erro sintático | `Linha N: erro sintatico proximo a <token>` + `Fim da compilacao` |
