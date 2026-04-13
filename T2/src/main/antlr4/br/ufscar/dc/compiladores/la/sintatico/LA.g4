/**
 * Gramática ANTLR4 para a linguagem LA (Linguagem Algorítmica) — T2 (análise sintática).
 *
 * O ANTLR4 gera LALexer.java e LAParser.java a partir deste arquivo.
 * Principal.java faz uma pré-varredura léxica, depois chama parser.programa()
 * e reporta o primeiro erro encontrado no arquivo de saída.
 */
grammar LA;

/* ===================== Regras Sintáticas (Parser) ===================== */

// Regra inicial: declarações globais seguidas da seção principal
programa
    : declaracoes_globais secao_principal EOF
    ;

declaracoes_globais
    : (declaracao_global)*
    ;

// Declarações fora do bloco principal: tipos, constantes, procedimentos e funções
declaracao_global
    : declaracao_tipo
    | declaracao_constante_global
    | declaracao_procedimento
    | declaracao_funcao
    ;

// tipo <ident> : registro ... fim_registro
declaracao_tipo
    : TIPO ident DOIS_PONTOS REGISTRO lista_campos_registro FIM_REGISTRO
    ;

lista_campos_registro
    : (lista_ident_dim DOIS_PONTOS tipo_estendido)+
    ;

// constante <ident> : <tipo_basico> = <valor>
declaracao_constante_global
    : CONSTANTE ident DOIS_PONTOS tipo_basico IGUAL valor_constante
    ;

// Procedimento: subrotina sem retorno
declaracao_procedimento
    : PROCEDIMENTO ident ABRE_PAR lista_parametros? FECHA_PAR corpo FIM_PROCEDIMENTO
    ;

// Função: subrotina com retorno tipado
declaracao_funcao
    : FUNCAO ident ABRE_PAR lista_parametros? FECHA_PAR DOIS_PONTOS tipo_estendido corpo FIM_FUNCAO
    ;

lista_parametros
    : parametro (VIRGULA parametro)*
    ;

// "var" indica passagem por referência; sem ele, por valor
parametro
    : (VAR)? lista_ident_dim DOIS_PONTOS tipo_estendido
    ;

lista_ident_dim
    : ident_dim (VIRGULA ident_dim)*
    ;

// Identificador com zero ou mais dimensões de array: nome[expr]...
ident_dim
    : ident (ABRE_COL expressao FECHA_COL)*
    ;

// Seção principal: com ou sem a palavra-chave "algoritmo".
// PrincipalSemPalavraAlgoritmo é capturada em Principal.java como erro sintático.
secao_principal
    : ALGORITMO corpo FIM_ALGORITMO               #PrincipalComCabecalho
    | corpo FIM_ALGORITMO                         #PrincipalSemPalavraAlgoritmo
    ;

// Corpo de qualquer bloco: declarações locais seguidas de comandos
corpo
    : (declaracao_local)* (comando)*
    ;

declaracao_local
    : DECLARE (variavel_decl)+
    | CONSTANTE ident DOIS_PONTOS tipo_basico IGUAL valor_constante
    | TIPO ident DOIS_PONTOS tipo_corpo_tipo
    ;

tipo_corpo_tipo
    : REGISTRO lista_campos_registro FIM_REGISTRO
    | tipo_estendido_simples
    ;

variavel_decl
    : lista_ident_dim DOIS_PONTOS tipo_estendido
    ;

tipo_estendido
    : REGISTRO lista_campos_registro FIM_REGISTRO
    | tipo_estendido_simples
    ;

// "^" antes do tipo indica ponteiro
tipo_estendido_simples
    : CIRCUNFLEXO? (tipo_basico | ident)
    ;

tipo_basico
    : LITERAL
    | INTEIRO
    | REAL
    | LOGICO
    ;

valor_constante
    : CADEIA
    | NUM_INT
    | NUM_REAL
    | VERDADEIRO
    | FALSO
    ;

comando
    : comando_atribuicao
    | comando_leia
    | comando_escreva
    | comando_se
    | comando_caso
    | comando_para
    | comando_enquanto
    | comando_faca_ate
    | comando_chamada
    | RETORNE expressao?
    ;

// lvalue <- expressao
comando_atribuicao
    : lvalue ATRIB expressao
    ;

// Destino de atribuição: variável, campo de registro (reg.campo) ou array (v[i]), com suporte a ponteiro (^ptr)
lvalue
    : CIRCUNFLEXO? ident ( ('.' ident) | ('[' expressao ']') )*
    ;

comando_leia
    : LEIA ABRE_PAR lista_lvalue FECHA_PAR
    ;

lista_lvalue
    : lvalue (VIRGULA lvalue)*
    ;

comando_escreva
    : ESCREVA ABRE_PAR lista_expressoes FECHA_PAR
    ;

lista_expressoes
    : expressao (VIRGULA expressao)*
    ;

// Chamada de procedimento (sem valor de retorno)
comando_chamada
    : ident ABRE_PAR lista_expressoes? FECHA_PAR
    ;

comando_se
    : SE expressao ENTAO corpo (SENAO corpo)? FIM_SE
    ;

// switch/case: valor ou intervalo mapeado para um corpo
comando_caso
    : CASO expressao SEJA item_caso+ (SENAO corpo)? FIM_CASO
    ;

item_caso
    : selecao_caso DOIS_PONTOS corpo
    ;

// Seletor: inteiro, intervalo (1..5) ou cadeia literal
selecao_caso
    : NUM_INT (PONTOS2 NUM_INT)?
    | CADEIA
    ;

// Laço for: variável de controle incrementada de 1 a cada iteração
comando_para
    : PARA ident ATRIB expressao ATE expressao FACA corpo FIM_PARA
    ;

// Laço while (testa antes)
comando_enquanto
    : ENQUANTO expressao FACA corpo FIM_ENQUANTO
    ;

// Laço do-while (testa depois)
comando_faca_ate
    : FACA corpo ATE expressao
    ;

/* Hierarquia de precedência (menor → maior):
   expressao > expr_e > expr_rel > expr_arit > termo > unario > fator */

expressao  : expressao OU expr_e | expr_e ;
expr_e     : expr_e E_LOGICO expr_rel | expr_rel ;
expr_rel   : expr_rel op=(IGUAL|DIFERENTE|MENOR|MENOR_IGUAL|MAIOR|MAIOR_IGUAL) expr_arit | expr_arit ;
expr_arit  : expr_arit op=(MAIS|MENOS) termo | termo ;
termo      : termo op=(VEZES|DIV|MOD) unario | unario ;
unario     : MENOS unario | NAO unario | fator ;

fator
    : ABRE_PAR expressao FECHA_PAR
    | NUM_INT
    | NUM_REAL
    | CADEIA
    | VERDADEIRO
    | FALSO
    | E_COMERCIAL ident        // &ident — endereço de variável
    | CIRCUNFLEXO ident        // ^ident — desreferência de ponteiro
    | ident ABRE_PAR lista_expressoes? FECHA_PAR   // chamada de função
    | ident ( ('.' ident) | ('[' expressao ']') )*
    ;

ident
    : IDENT
    ;

/* ===================== Regras Léxicas (Lexer) ===================== */

WS : [ \t\r\n]+ -> skip ;

// Comentário válido entre chaves (sem quebra de linha interna)
COMENTARIO : '{' ~[}\r\n]* '}' -> skip ;

// Comentário aberto sem fechar — gera token de erro tratado em Principal.java
COMENTARIO_NAO_FECHADO : '{' ~[}\r\n]* ( '\r'? '\n' | EOF ) ;

// Cadeia válida: permite \" e \\ como escapes
CADEIA : '"' ( '\\"' | '\\\\' | ~["\\\r\n] )* '"' ;

// Cadeia aberta sem fechar — gera token de erro tratado em Principal.java
CADEIA_NAO_FECHADA : '"' ( '\\"' | '\\\\' | ~["\\\r\n] )* ( '\r'? '\n' | EOF ) ;

fragment DIGITO : [0-9] ;

// NUM_REAL antes de NUM_INT para que "3.14" não seja tokenizado como "3" + "." + "14"
NUM_REAL : DIGITO+ '.' DIGITO+ ;
NUM_INT  : DIGITO+ ;

/* Palavras-chave — declaradas antes de IDENT para terem prioridade */
FIM_ALGORITMO    : 'fim_algoritmo' ;
FIM_PROCEDIMENTO : 'fim_procedimento' ;
FIM_FUNCAO       : 'fim_funcao' ;
FIM_REGISTRO     : 'fim_registro' ;
FIM_ENQUANTO     : 'fim_enquanto' ;
FIM_PARA         : 'fim_para' ;
FIM_CASO         : 'fim_caso' ;
FIM_SE           : 'fim_se' ;
PROCEDIMENTO     : 'procedimento' ;
CONSTANTE        : 'constante' ;
REGISTRO         : 'registro' ;
ENQUANTO         : 'enquanto' ;
DECLARE          : 'declare' ;
ESCREVA          : 'escreva' ;
VERDADEIRO       : 'verdadeiro' ;
RETORNE          : 'retorne' ;
LITERAL          : 'literal' ;
INTEIRO          : 'inteiro' ;
LOGICO           : 'logico' ;
ALGORITMO        : 'algoritmo' ;
FUNCAO           : 'funcao' ;
SENAO            : 'senao' ;
ENTAO            : 'entao' ;
SEJA             : 'seja' ;
CASO             : 'caso' ;
PARA             : 'para' ;
FACA             : 'faca' ;
TIPO             : 'tipo' ;
REAL             : 'real' ;
LEIA             : 'leia' ;
NAO              : 'nao' ;
VAR              : 'var' ;
ATE              : 'ate' ;
FALSO            : 'falso' ;
SE               : 'se' ;

/* Operadores e pontuação */
PONTOS2    : '..' ;
ATRIB      : '<-' ;
DIFERENTE  : '<>' ;
MENOR_IGUAL: '<=' ;
MAIOR_IGUAL: '>=' ;

DOIS_PONTOS: ':' ;
PONTO      : '.' ;
VIRGULA    : ',' ;
MENOR      : '<' ;
MAIOR      : '>' ;
IGUAL      : '=' ;
MAIS       : '+' ;
MENOS      : '-' ;
VEZES      : '*' ;
DIV        : '/' ;
MOD        : '%' ;
CIRCUNFLEXO: '^' ;
E_COMERCIAL: '&' ;
ABRE_PAR   : '(' ;
FECHA_PAR  : ')' ;
ABRE_COL   : '[' ;
FECHA_COL  : ']' ;

E_LOGICO : 'e' ;
OU       : 'ou' ;

// Identificadores — declarados após palavras-chave para não sobrepô-las
IDENT : [a-zA-Z_] [a-zA-Z0-9_]* ;

// Qualquer caractere não reconhecido — gera token de erro tratado em Principal.java
SIMBOLO_NAO_IDENTIFICADO : . ;
