# Entrega 1
# Analisador Léxico - Linguagem Algorítmica (LA)

Este projeto é a implementação do **Trabalho 1** da disciplina de **Construção de Compiladores** (DC/UFSCar). O objetivo é realizar a análise léxica de programas escritos na linguagem LA, gerando uma lista de tokens ou reportando erros.

## Membros do Grupo
* **Rodrigo Smith Rodrigues** - RA: 821172
* **Vitória Hilgert Tomasel** - RA: 821259
* **Pedro dos Santos Sakai** - RA: 824387

## Pré-requisitos
Para compilar e executar este projeto, você precisará de:
* **Python 3.8** ou superior instalado no sistema.
* Não são necessárias bibliotecas externas (utiliza apenas a biblioteca padrão `re` e `sys`).

## Como Executar
O analisador funciona via linha de comando e exige dois argumentos: o caminho do arquivo fonte (entrada) e o caminho onde o resultado será salvo (saída)

### Passo a Passo:
1. Abra o terminal ou prompt de comando.
2. Navegue até a pasta onde o arquivo `analisador_lexico.py` está localizado.
3. Execute o comando seguindo o modelo abaixo:

```bash
python analisador_lexico.py "C:\caminho\arquivo_entrada.txt" "C:\caminho\saida.txt"