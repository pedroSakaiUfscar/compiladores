grammar LA;

/* ========== Parser ========== */

programa
    : declaracoes_globais secao_principal EOF
    ;

declaracoes_globais
    : (declaracao_global)*
    ;

declaracao_global
    : declaracao_tipo
    | declaracao_constante_global
    | declaracao_procedimento
    | declaracao_funcao
    ;

declaracao_tipo
    : TIPO ident DOIS_PONTOS REGISTRO lista_campos_registro FIM_REGISTRO
    ;

lista_campos_registro
    : (lista_ident_dim DOIS_PONTOS tipo_estendido)+
    ;

declaracao_constante_global
    : CONSTANTE ident DOIS_PONTOS tipo_basico IGUAL valor_constante
    ;

declaracao_procedimento
    : PROCEDIMENTO ident ABRE_PAR lista_parametros? FECHA_PAR corpo FIM_PROCEDIMENTO
    ;

declaracao_funcao
    : FUNCAO ident ABRE_PAR lista_parametros? FECHA_PAR DOIS_PONTOS tipo_estendido corpo FIM_FUNCAO
    ;

lista_parametros
    : parametro (VIRGULA parametro)*
    ;

parametro
    : (VAR)? lista_ident_dim DOIS_PONTOS tipo_estendido
    ;

lista_ident_dim
    : ident_dim (VIRGULA ident_dim)*
    ;

ident_dim
    : ident (ABRE_COL expressao FECHA_COL)*
    ;

secao_principal
    : ALGORITMO corpo FIM_ALGORITMO               #PrincipalComCabecalho
    | corpo FIM_ALGORITMO                         #PrincipalSemPalavraAlgoritmo
    ;

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

comando_atribuicao
    : lvalue ATRIB expressao
    ;

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

comando_chamada
    : ident ABRE_PAR lista_expressoes? FECHA_PAR
    ;

comando_se
    : SE expressao ENTAO corpo (SENAO corpo)? FIM_SE
    ;

comando_caso
    : CASO expressao SEJA item_caso+ (SENAO corpo)? FIM_CASO
    ;

item_caso
    : selecao_caso DOIS_PONTOS corpo
    ;

selecao_caso
    : NUM_INT (PONTOS2 NUM_INT)?
    | CADEIA
    ;

comando_para
    : PARA ident ATRIB expressao ATE expressao FACA corpo FIM_PARA
    ;

comando_enquanto
    : ENQUANTO expressao FACA corpo FIM_ENQUANTO
    ;

comando_faca_ate
    : FACA corpo ATE expressao
    ;

expressao
    : expressao OU expr_e
    | expr_e
    ;

expr_e
    : expr_e E_LOGICO expr_rel
    | expr_rel
    ;

expr_rel
    : expr_rel op=(IGUAL|DIFERENTE|MENOR|MENOR_IGUAL|MAIOR|MAIOR_IGUAL) expr_arit
    | expr_arit
    ;

expr_arit
    : expr_arit op=(MAIS|MENOS) termo
    | termo
    ;

termo
    : termo op=(VEZES|DIV|MOD) unario
    | unario
    ;

unario
    : MENOS unario
    | NAO unario
    | fator
    ;

fator
    : ABRE_PAR expressao FECHA_PAR
    | NUM_INT
    | NUM_REAL
    | CADEIA
    | VERDADEIRO
    | FALSO
    | E_COMERCIAL ident
    | CIRCUNFLEXO ident
    | ident ABRE_PAR lista_expressoes? FECHA_PAR
    | ident ( ('.' ident) | ('[' expressao ']') )*
    ;

ident
    : IDENT
    ;

/* ========== Lexer (igual ao T1) ========== */

WS : [ \t\r\n]+ -> skip ;

COMENTARIO : '{' ~[}\r\n]* '}' -> skip ;

COMENTARIO_NAO_FECHADO : '{' ~[}\r\n]* ( '\r'? '\n' | EOF ) ;

CADEIA : '"' ( '\\"' | '\\\\' | ~["\\\r\n] )* '"' ;

CADEIA_NAO_FECHADA : '"' ( '\\"' | '\\\\' | ~["\\\r\n] )* ( '\r'? '\n' | EOF ) ;

fragment DIGITO : [0-9] ;

NUM_REAL : DIGITO+ '.' DIGITO+ ;

NUM_INT : DIGITO+ ;

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

PONTOS2   : '..' ;
ATRIB     : '<-' ;
DIFERENTE : '<>' ;
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
FECHA_COL   : ']' ;

E_LOGICO : 'e' ;
OU       : 'ou' ;

IDENT : [a-zA-Z_] [a-zA-Z0-9_]* ;

SIMBOLO_NAO_IDENTIFICADO : . ;
