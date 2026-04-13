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
