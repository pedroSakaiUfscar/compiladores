# T4 — Analisador semântico LA

Extensão do T3: parâmetros de sub-rotinas, ponteiros/registros em atribuição, colisão de nomes entre categorias e `retorne` só em função.

## Requisitos

- **JDK 17**
- **Apache Maven 3.8+**

## Compilar

Na raiz do repositório:

```bash
cd T4
mvn package -q
```

O JAR executável com dependências é gerado em:

`target/t4-la-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Executar

```bash
java -jar T4/target/t4-la-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo_entrada> <arquivo_saida>
```

A saída deve ser gravada **somente** no arquivo indicado (não use `System.out` para o relatório de erros).

## Saída

- Cada erro semântico: `Linha N: <mensagem>`
- Encerramento: `Fim da compilacao`

Casos de teste em `casos-de-teste/4.casos_teste_t4/`. O corretor pede pasta raiz `casos-de-teste` e um executável no 1º argumento (ex.: script que chama o JAR).

## Estrutura

- `src/main/antlr4/.../LA.g4` — gramática léxica/sintática (igual à do T2/T3)
- `src/main/java/.../Principal.java` — entrada, léxico, sintático, semântico
- `src/main/java/.../AnalisadorSemantico.java` — regras semânticas do T4
