grammar CULINA;

receita
    : RECEITA nome capa? tempo_prep porcoes? paragrafo* ingredientes metodo FIM_RECEITA EOF
    ;

nome
    : NOME DOIS_PONTOS CADEIA
    ;

capa
    : CAPA DOIS_PONTOS CADEIA
    ;

tempo_prep
    : TEMPO DOIS_PONTOS tempo+
    ;

porcoes
    : PORCOES DOIS_PONTOS NUMERO
    ;

paragrafo
    : PARAGRAFO DOIS_PONTOS CADEIA
    ;

ingredientes
    : INGREDIENTES DOIS_PONTOS lista_ingredientes* lista_tempero* FIM_INGREDIENTES
    ;

lista_ingredientes
    : (medida_solido | medida_liq) DE INGREDIENTE
    ;

medida_solido
    : NUMERO ('g' | COLHER tipo_colher | XICARA)
    ;

medida_liq
    : NUMERO ('ml' | 'l' | XICARA | COPO)
    ;

lista_tempero
    : NUMERO ('g' | COLHER tipo_colher | XICARA) DE TEMPERO
    | PITADA TEMPERO
    | TEMPERO A_GOSTO
    ;

tipo_colher
    : CHA | SOBREMESA | SOPA
    ;

metodo
    : METODO DOIS_PONTOS cmd* FIM_METODO
    ;

cmd
    : cmdAsse
    | cmdCozinhe
    | cmdMisture
    | cmdCorte
    | cmdBata
    | cmdDescanse
    | cmdPasso
    | cmdPaoDeLo
    | cmdCobertura
    | cmdArroz
    | cmdRisotto
    ;

cmdAsse
    : ASSE ABRE_PAR INGREDIENTE VIRGULA tempo VIRGULA NUMERO FECHA_PAR
    ;

cmdCozinhe
    : COZINHE ABRE_PAR INGREDIENTE VIRGULA tempo FECHA_PAR
    ;

cmdMisture
    : MISTURE ABRE_PAR INGREDIENTE (VIRGULA INGREDIENTE)+ FECHA_PAR
    ;

cmdCorte
    : CORTE ABRE_PAR INGREDIENTE VIRGULA tipo_corte FECHA_PAR
    ;

cmdBata
    : BATA ABRE_PAR INGREDIENTE (VIRGULA INGREDIENTE)+ FECHA_PAR
    ;

cmdDescanse
    : DESCANSE ABRE_PAR tempo FECHA_PAR
    ;

cmdPasso
    : PASSO ABRE_PAR CADEIA FECHA_PAR
    ;

cmdPaoDeLo
    : PAO_DE_LO ABRE_PAR FECHA_PAR
    ;

cmdCobertura
    : COBERTURA ABRE_PAR FECHA_PAR
    ;

cmdArroz
    : ARROZ ABRE_PAR FECHA_PAR
    ;

cmdRisotto
    : RISOTTO ABRE_PAR FECHA_PAR
    ;

tipo_corte
    : PICAR | RALAR | CORTAR
    ;

tempo
    : NUMERO unidade_tempo
    ;

unidade_tempo
    : HORA | MIN | SEG
    ;

/* Palavras reservadas antes de tempero para evitar que 'receita', 'de', etc. virem tempero. */

WS : [ \t\r\n]+ -> skip ;

COMENTARIO : '$' ~[\r\n]* '\r'? '\n' -> skip ;

RECEITA           : 'receita' ;
FIM_RECEITA       : 'fim_receita' ;
FIM_INGREDIENTES  : 'fim_ingredientes' ;
FIM_METODO        : 'fim_metodo' ;
NOME              : 'Nome' ;
CAPA              : 'Capa' ;
TEMPO             : 'Tempo' ;
PORCOES           : 'Porcoes' ;
PARAGRAFO         : 'Paragrafo' ;
INGREDIENTES      : 'Ingredientes' ;
METODO            : 'Metodo' ;
PAO_DE_LO         : 'Pao_de_Lo' ;
A_GOSTO           : 'a_gosto' ;
DE                : 'de' ;
COLHER            : 'colher' ;
XICARA            : 'xicara' ;
COPO              : 'copo' ;
PITADA            : 'pitada' ;
CHA               : 'cha' ;
SOBREMESA         : 'sobremesa' ;
SOPA              : 'sopa' ;
ASSE              : 'asse' ;
COZINHE           : 'cozinhe' ;
MISTURE           : 'misture' ;
CORTE             : 'corte' ;
BATA              : 'bata' ;
DESCANSE          : 'descanse' ;
PASSO             : 'passo' ;
COBERTURA         : 'cobertura' ;
ARROZ             : 'arroz' ;
RISOTTO           : 'risotto' ;
PICAR             : 'picar' ;
RALAR             : 'ralar' ;
CORTAR            : 'cortar' ;
HORA              : 'hora' ;
MIN               : 'min' ;
SEG               : 'seg' ;

CADEIA : '"' ( ESC_SEQ | ~["\\\r\n] )* '"' ;

fragment ESC_SEQ : '\\"' | '\\\\' ;

NUMERO : [0-9]+ ;

INGREDIENTE : [A-Z] [a-zA-Z0-9]* ;

TEMPERO : [a-z] [a-z0-9]* ;

DOIS_PONTOS : ':' ;
VIRGULA     : ',' ;
ABRE_PAR    : '(' ;
FECHA_PAR   : ')' ;

SIMBOLO_NAO_IDENTIFICADO : . ;
