lexer grammar LALexer;

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
FECHA_COL  : ']' ;

E_LOGICO : 'e' ;
OU       : 'ou' ;

IDENT : [a-zA-Z_] [a-zA-Z0-9_]* ;

SIMBOLO_NAO_IDENTIFICADO : . ;
