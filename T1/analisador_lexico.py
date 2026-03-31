import re
import sys

# --- CONFIGURAÇÃO DA GRAMÁTICA LÉXICA (LA) ---
# Cada tupla contém o nome do token e sua Expressão Regular correspondente.
# A ordem é importante: padrões mais específicos vêm antes de padrões gerais.
TOKENS_SPEC = [
    # Tratamento de Erros: Identifica aberturas de blocos que não fecham na mesma linha
    ('COMENTARIO_NAO_FECHADO', r'\{[^}]*$'),
    ('CADEIA_NAO_FECHADA', r'"[^"]*$'),

    # Tokens Válidos
    ('CADEIA', r'"[^"]*"'),                         # Texto entre aspas duplas
    ('PALAVRA_CHAVE', r'\b(algoritmo|declare|fim_algoritmo|leia|escreva|inteiro|real|logico|literal|se|entao|senao|fim_se|enquanto|faca|fim_enquanto|para|ate|faca|fim_para|retorne|procedimento|funcao|tipo|registro|constante|verdadeiro|falso)\b'),
    ('IDENT', r'[a-zA-Z][a-zA-Z0-9]*'),             # Identificadores (começam com letra)
    ('NUM_REAL', r'\d+\.\d+'),                      # Números com ponto flutuante
    ('NUM_INT', r'\d+'),                            # Sequência de dígitos
    ('OPERADOR', r'<=|>=|<>|<-|[\+\-\*/=<>|]'),      # Operadores lógicos e aritméticos
    ('SIMBOLO', r'[.,:()\[\]]'),                    # Símbolos de pontuação e delimitação

    # Elementos a serem ignorados
    ('ESPACO', r'\s+'),                             # Espaços, tabs e quebras de linha
    ('COMENTARIO', r'\{.*?\}'),                     # Comentários entre chaves

    # Erro de Símbolo: Qualquer caractere que não casou com as regras anteriores
    ('ERRO', r'.'),
]

def analisar(caminho_entrada, caminho_saida):
    """
    Lê o arquivo de entrada, identifica os tokens e salva o resultado ou erro no arquivo de saída.
    """
    try:
        # Carrega todas as linhas do arquivo fonte
        with open(caminho_entrada, 'r') as f_in:
            linhas = f_in.readlines()

        resultado = []

        for i, linha_texto in enumerate(linhas):
            n_linha = i + 1
            pos = 0
            # Percorre a linha caractere por caractere usando as Regex
            while pos < len(linha_texto):
                match = None
                for nome_token, padrao in TOKENS_SPEC:
                    regex = re.compile(padrao)
                    match = regex.match(linha_texto, pos)
                    if match:
                        texto = match.group(0)

                        # Ignora espaços e comentários (não geram saída de token)
                        if nome_token == 'ESPACO' or nome_token == 'COMENTARIO':
                            pass

                            # Verificação de Erros Léxicos Críticos [cite: 70]
                        elif nome_token == 'COMENTARIO_NAO_FECHADO':
                            resultado.append(f"Linha {n_linha}: comentario nao fechado")
                            salvar_e_encerrar(resultado, caminho_saida)
                        elif nome_token == 'CADEIA_NAO_FECHADA':
                            resultado.append(f"Linha {n_linha}: cadeia nao fechada")
                            salvar_e_encerrar(resultado, caminho_saida)
                        elif nome_token == 'ERRO':
                            # Símbolo não identificado (ex: ~, @) [cite: 69]
                            resultado.append(f"Linha {n_linha}: {texto} - simbolo nao identificado")
                            salvar_e_encerrar(resultado, caminho_saida)

                        else:
                            # Formatação de saída conforme especificação: <'lexema',tipo>
                            tipo = nome_token
                            # Para palavras-chave e símbolos, o tipo é o próprio texto [cite: 21, 24]
                            if nome_token in ['PALAVRA_CHAVE', 'OPERADOR', 'SIMBOLO']:
                                tipo = texto
                            resultado.append(f"<{repr(texto).replace(chr(39), chr(39))},{tipo}>")

                        pos = match.end() # Avança o ponteiro para o fim do token casado
                        break
                if not match:
                    pos += 1

        # Se terminar o arquivo sem erros, salva a lista de tokens identificados
        salvar_e_encerrar(resultado, caminho_saida)

    except Exception as e:
        print(f"Erro inesperado no processamento: {e}")

def salvar_e_encerrar(lista_tokens, caminho_saida):
    """Grava o conteúdo no arquivo de saída e encerra o script conforme exigido."""
    with open(caminho_saida, 'w') as f_out:
        for item in lista_tokens:
            f_out.write(item + '\n')
    sys.exit(0) # Interrompe a execução após encontrar o primeiro erro ou finalizar [cite: 50]

if __name__ == "__main__":
    # Validação dos argumentos de linha de comando [cite: 71]
    if len(sys.argv) != 3:
        print("Erro: São necessários dois argumentos (entrada e saída).")
    else:
        analisar(sys.argv[1], sys.argv[2])