# T6 — Compilador CULINA (receitas → HTML)

Trabalho final da disciplina **Construção de Compiladores** (UFSCar).  
Implementa uma linguagem declarativa de domínio específico (**CULINA**) para descrever receitas culinárias e gerar páginas HTML prontas para visualização.

## Membros do grupo

- Rodrigo Smith Rodrigues — RA: 821172
- Vitória Hilgert Tomasel — RA: 821259
- Pedro dos Santos Sakai — RA: 824387

## Demonstração em vídeo

📺 [Assistir no YouTube](https://youtu.be/dBltNnmCqDo)

## O que é a linguagem CULINA?

**CULINA** é uma DSL (Domain-Specific Language) inspirada na linguagem **RECIPE** de [Linneu Augusto Mendo Zanco (Compiladores-T6, UFSCar)](https://github.com/linneu1997/Compiladores-T6), adaptada e estendida por este grupo.

| Aspecto | Descrição |
|---------|-----------|
| **Propósito** | Escrever receitas de forma estruturada |
| **Paradigma** | Declarativo (seções fixas + comandos de preparo) |
| **Saída** | Página HTML estilizada com ingredientes e passos numerados |
| **Tecnologia** | Java 17, ANTLR 4.13.1, Maven |

### Diferenças em relação à RECIPE original

1. Nome do projeto: **CULINA** (compilador e documentação)
2. Campo opcional `Porcoes: N` (número de porções)
3. Relatório de erros gravado no **arquivo de saída** (não apenas no console)
4. Regras semânticas adicionais (temperatura do forno, método vazio, etc.)
5. HTML com CSS embutido e layout responsivo

## Exemplo de programa

```text
receita
Nome: "Brigadeiro"
Tempo: 20 min
Porcoes: 30

Ingredientes:
  400 g de LeiteCondensado
  1 colher sopa de Margarina
  sal a_gosto
fim_ingredientes

Metodo:
  cozinhe(LeiteCondensado, 20 min)
  misture(LeiteCondensado, Margarina)
  passo("Enrole em bolinhas.")
fim_metodo
fim_receita
```

Comentários começam com `$` e vão até o fim da linha.

### Convenções léxicas

- **Ingredientes**: identificadores com inicial **maiúscula** (`LeiteCondensado`, `Farinha`)
- **Temperos**: identificadores com inicial **minúscula** (`sal`, `pimenta`)
- **Cadeias**: entre aspas duplas (`"texto"`)

### Comandos do método

| Comando | Descrição |
|---------|-----------|
| `asse(Ing, tempo, tempC)` | Assar ingrediente a temperatura em °C |
| `cozinhe(Ing, tempo)` | Cozinhar ingrediente |
| `misture(Ing1, Ing2, ...)` | Misturar ingredientes (mín. 2) |
| `corte(Ing, picar\|ralar\|cortar)` | Cortar ingrediente |
| `bata(Ing1, Ing2, ...)` | Bater ingredientes (mín. 2) |
| `descanse(tempo)` | Tempo de descanso |
| `passo("texto")` | Passo livre em texto |
| `Pao_de_Lo()` | Macro: receita de pão de ló |
| `cobertura()` | Macro: cobertura de chocolate |
| `arroz()` | Macro: arroz básico |
| `risotto()` | Macro: base de risotto |

## Análise implementada

### Léxica + sintática (gramática `CULINA.g4`)

A gramática define a estrutura do documento `receita … fim_receita`, seções, medidas e comandos. O ANTLR gera lexer e parser automaticamente.

### Semântica (mínimo de 6 verificações)

| # | Regra |
|---|-------|
| 1 | Ingrediente ou tempero **duplicado** na declaração |
| 2 | Ingrediente **usado sem declaração** em comando |
| 3 | Uso de **tempero** onde se espera ingrediente |
| 4 | Temperatura de `asse` fora do intervalo **50–300 °C** |
| 5 | Receita sem **nenhum** ingrediente/tempero declarado |
| 6 | Seção `Metodo` **sem comandos** |
| 7 | `misture` / `bata` com **menos de dois** ingredientes |

### Geração de código

O `GeradorHTML` percorre a árvore sintática (Visitor ANTLR) e produz HTML5 completo com estilos embutidos.

## Compilar

```bash
mvn -f T6/pom.xml package
```

JAR executável:

`T6/target/t6-culina-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Executar

```bash
java -jar T6/target/t6-culina-1.0-SNAPSHOT-jar-with-dependencies.jar entrada.txt saida.html
```

- **Sucesso**: `saida.html` contém a página da receita
- **Erro**: o arquivo de saída contém linhas no formato `Erro L:C - mensagem`

## Casos de teste

Ver [`casos-de-teste/6.casos_teste_t6/`](../casos-de-teste/6.casos_teste_t6/): 2 receitas válidas + 4 casos de erro (semântico e sintático), com as saídas esperadas em `saida_esperada/`.

## Estrutura do código

```
T6/
├── pom.xml
├── README.md
└── src/main/
    ├── antlr4/.../CULINA.g4       # Gramática
    └── java/.../
        ├── Principal.java          # Orquestração
        ├── AnalisadorSemantico.java
        ├── GeradorHTML.java
        └── RelatorioErros.java
```

## Entrega (requisitos do professor)

- [x] Repositório com código-fonte e gramática documentada
- [x] README.md com descrição da linguagem e instruções de compilação
- [x] Casos de teste — [`casos-de-teste/6.casos_teste_t6/`](../casos-de-teste/6.casos_teste_t6/)
- [x] Vídeo demonstrativo — [YouTube](https://youtu.be/dBltNnmCqDo)

## Referências

- Lucrédio, D. *T6 — Especificação e Critérios* (UFSCar, 2026)
- Zanco, L. A. M. [Compiladores-T6 / RECIPE](https://github.com/linneu1997/Compiladores-T6)
- [ANTLR 4 Documentation](https://github.com/antlr/antlr4)
