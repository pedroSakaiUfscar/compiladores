# T3 — Analisador Semântico da linguagem LA

Este projeto implementa o analisador semântico da linguagem LA usando **ANTLR 4** e **Java**. Ele complementa a análise léxica (T1) e a análise sintática (T2) verificando regras de tipos, escopos, declarações e usos de identificadores.

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

> O projeto foi configurado para compilar com `source` e `target` em Java 17.

## Estrutura do projeto

O módulo do trabalho está dentro da pasta `T3/`.  
A raiz do repositório não possui `pom.xml`, então os comandos Maven devem ser executados apontando para `T3/pom.xml` ou dentro da pasta `T3`.

Arquivos principais:

- `src/main/antlr4/br/ufscar/dc/compiladores/la/semantico/LA.g4`  
  Gramática léxica e sintática da linguagem LA (reaproveitada do T2).
- `src/main/java/br/ufscar/dc/compiladores/la/semantico/Principal.java`  
  Classe principal: orquestra léxico → sintático → semântico e escreve a saída.
- `src/main/java/br/ufscar/dc/compiladores/la/semantico/AnalisadorSemantico.java`  
  Implementação das regras semânticas (tabela de símbolos, tipagem, registros, escopo, etc.).

## Como compilar

Na raiz do repositório, execute:

```bash
cd T3
mvn package -q
```

Isso irá:
1. Gerar `LALexer.java` e `LAParser.java` a partir de `LA.g4` (plugin ANTLR4)
2. Compilar todos os arquivos Java
3. Empacotar o JAR executável com dependências em `target/t3-la-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Como executar

```bash
java -jar T3/target/t3-la-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo_entrada> <arquivo_saida>
```

- `<arquivo_entrada>`: arquivo-fonte em linguagem LA
- `<arquivo_saida>`: arquivo onde as mensagens de erro (ou `Fim da compilacao`) serão escritas

## Corretor automático (local)

```bash
# Na raiz compiladores
java -cp corretor/corretor-auto.jar \
  br.ufscar.dc.compiladores.compiladores.corretor.automatico.Principal \
  "java -jar $PWD/T3/target/t3-la-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar" \
  gcc \
  "$PWD/corretor/temp" \
  "$PWD/casos-de-teste" \
  "824387, 821259, 821172" \
  "t3"
```

## Saída esperada

| Situação | Saída |
|---|---|
| Programa correto | `Fim da compilacao` |
| Erro léxico | `Linha N: <mensagem>` + `Fim da compilacao` |
| Erro sintático | `Linha N: erro sintatico proximo a <token>` + `Fim da compilacao` |
| Erro semântico | `Linha N: <mensagem>` (uma por erro, ordenadas por linha) + `Fim da compilacao` |

### Exemplos de erros semânticos detectados

- `identificador <nome> nao declarado`
- `identificador <nome> ja declarado anteriormente`
- `tipo <nome> nao declarado`
- `atribuicao nao compativel para <nome>`
