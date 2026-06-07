# T5 — Gerador de código C para a linguagem LA

Este projeto implementa o gerador de código C da linguagem LA usando **ANTLR 4** e **Java**. Reúne em um único executável as fases construídas nos trabalhos anteriores (léxico, sintático e semântico) e adiciona, ao final, a tradução do programa LA para um arquivo `.c` equivalente.

## Membros do grupo

- Rodrigo Smith Rodrigues — RA: 821172
- Vitória Hilgert Tomasel — RA: 821259
- Pedro dos Santos Sakai — RA: 824387

## Requisitos

Para compilar e executar este trabalho, é necessário ter instalado:

- **Java JDK 17**
- **Apache Maven 3.8+**
- **ANTLR 4 Runtime 4.13.1**  
  Esta dependência é baixada automaticamente pelo Maven durante a compilação.
- **GCC** (apenas para compilar o `.c` gerado)

> O projeto foi configurado para compilar com `source` e `target` em Java 17.

## Estrutura do projeto

O módulo do trabalho está dentro da pasta `T5/`.  
A raiz do repositório não possui `pom.xml`, então os comandos Maven devem ser executados apontando para `T5/pom.xml` ou dentro da pasta `T5`.

Arquivos principais:

- `src/main/antlr4/br/ufscar/dc/compiladores/la/gerador/LA.g4`  
  Gramática léxica e sintática da linguagem LA (reaproveitada do T4).
- `src/main/java/br/ufscar/dc/compiladores/la/gerador/Principal.java`  
  Classe principal: orquestra léxico → sintático → semântico → gerador e escreve a saída.
- `src/main/java/br/ufscar/dc/compiladores/la/gerador/AnalisadorSemantico.java`  
  Regras semânticas (reaproveitadas do T4).
- `src/main/java/br/ufscar/dc/compiladores/la/gerador/GeradorCodigo.java`  
  Tradução da árvore sintática para C: typedefs, `#define`, sub-rotinas, declarações, atribuições, leitura/escrita, controle de fluxo, ponteiros e registros.

## Como compilar

Na raiz do repositório, execute:

```bash
cd T5
mvn package -q
```

Isso irá:
1. Gerar `LALexer.java` e `LAParser.java` a partir de `LA.g4` (plugin ANTLR4)
2. Compilar todos os arquivos Java
3. Empacotar o JAR executável com dependências em `target/t5-la-gerador-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Como executar

```bash
java -jar T5/target/t5-la-gerador-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo_entrada> <arquivo_saida>
```

- `<arquivo_entrada>`: arquivo-fonte em linguagem LA
- `<arquivo_saida>`: arquivo onde será gravado o relatório de erros **ou** o código C gerado

## Corretor automático (local)

```bash
# Na raiz compiladores
java -cp corretor/corretor-auto.jar \
  br.ufscar.dc.compiladores.compiladores.corretor.automatico.Principal \
  "java -jar $PWD/T5/target/t5-la-gerador-1.0-SNAPSHOT-jar-with-dependencies.jar" \
  gcc \
  "$PWD/corretor/temp" \
  "$PWD/casos-de-teste" \
  "824387, 821259, 821172" \
  "t5"
```

## Saída esperada

| Situação | Saída |
|---|---|
| Programa correto | Código C equivalente (sem `Fim da compilacao`) |
| Erro léxico | `Linha N: <mensagem>` + `Fim da compilacao` |
| Erro sintático | `Linha N: erro sintatico proximo a <token>` + `Fim da compilacao` |
| Erro semântico | `Linha N: <mensagem>` (uma por erro, ordenadas por linha) + `Fim da compilacao` |
