# Trabalhos Práticos de Compiladores

Repositório para armazenar os trabalhos da disciplina "Compiladores", cada diretório T# contém os arquivos do respectivo trabalho.

O **Maven** (`pom.xml`) fica dentro de cada `T#` (por exemplo `T1/`). Na raiz não há `pom.xml`: para compilar o T1 a partir da raiz use `mvn -f T1/pom.xml package`, ou `cd T1` e então `mvn package`.

Exemplos: `mvn -f T4/pom.xml package` para o T4; o JAR fica em `T4/target/t4-la-semantico-1.0-SNAPSHOT-jar-with-dependencies.jar`. Para o T5, `mvn -f T5/pom.xml package` gera `T5/target/t5-la-gerador-1.0-SNAPSHOT-jar-with-dependencies.jar`.

Cada `T#/` tem seu próprio `README.md` com instruções específicas de compilação e execução.

## Membros do Grupo
* **Rodrigo Smith Rodrigues** - RA: 821172
* **Vitória Hilgert Tomasel** - RA: 821259
* **Pedro dos Santos Sakai** - RA: 824387
