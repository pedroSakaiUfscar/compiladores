# T1 — Analisador léxico da linguagem LA

## Membros do grupo

- Rodrigo Smith Rodrigues - RA: 821172
- Vitória Hilgert Tomasel - RA: 821259
- Pedro dos Santos Sakai - RA: 824387

## Compilar

O Maven só enxerga o projeto onde está o `pom.xml` — neste repositório isso é a pasta **`T1`**, não a raiz `compiladores`.

```bash
mvn -f T1/pom.xml package
```

O JAR executável com dependências é gerado em:

`target/t1-la-lexico-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Executar (conforme especificação)

```bash
java -jar target/t1-la-lexico-1.0-SNAPSHOT-jar-with-dependencies.jar /caminho/entrada.txt /caminho/saida.txt
```

## Corretor automático (local)

```bash
# Na raiz compiladores
java -cp corretor/corretor-auto.jar \
  br.ufscar.dc.compiladores.compiladores.corretor.automatico.Principal \
  "java -jar $PWD/T1/target/t1-la-lexico-1.0-SNAPSHOT-jar-with-dependencies.jar" \
  gcc \
  "$PWD/corretor/temp" \
  "$PWD/casos-de-teste" \
  "824387, 821259, 821172" \
  "t1"
```

## Estrutura

`src/main/antlr4/.../LALexer.g4`: Gramática léxica (tokens, comentários, erros).
`src/main/java/.../Principal.java`: Lê a entrada, percorre os tokens e formata a saída ou o primeiro erro. 

