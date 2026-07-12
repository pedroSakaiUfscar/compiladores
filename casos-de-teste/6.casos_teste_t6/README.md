# Casos de teste — T6 (CULINA)

- `entrada/` — programas `.culina`
- `saida_esperada/` — HTML (receitas válidas) ou relatório de erro (`.txt`)

| Arquivo | Cobre |
|---------|-------|
| `1_brigadeiro.culina` | Receita simples (ingredientes, tempero, `misture`, `cozinhe`, `passo`) |
| `2_bolo_pao_de_lo.culina` | Receita com `Paragrafo`, macros `Pao_de_Lo`, `cobertura`, `descanse` |
| `3_ingrediente_nao_declarado.culina` | Erro semântico: ingrediente usado sem declaração |
| `4_temperatura_invalida.culina` | Erro semântico: `asse` com temperatura fora de 50–300 °C |
| `5_ingrediente_duplicado.culina` | Erro semântico: mesmo ingrediente declarado duas vezes |
| `6_erro_sintatico.culina` | Erro sintático: seção `Tempo` ausente |

## Como rodar

```bash
mvn -f T6/pom.xml package
JAR=T6/target/t6-culina-1.0-SNAPSHOT-jar-with-dependencies.jar
for f in casos-de-teste/6.casos_teste_t6/entrada/*.culina; do
  base=$(basename "$f" .culina)
  java -jar $JAR "$f" "/tmp/${base}.out"
  diff -q "/tmp/${base}.out" casos-de-teste/6.casos_teste_t6/saida_esperada/${base}.*
done
```
