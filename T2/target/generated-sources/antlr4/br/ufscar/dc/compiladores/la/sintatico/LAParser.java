// Generated from br/ufscar/dc/compiladores/la/sintatico/LA.g4 by ANTLR 4.13.1
package br.ufscar.dc.compiladores.la.sintatico;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class LAParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, COMENTARIO=2, COMENTARIO_NAO_FECHADO=3, CADEIA=4, CADEIA_NAO_FECHADA=5, 
		NUM_REAL=6, NUM_INT=7, FIM_ALGORITMO=8, FIM_PROCEDIMENTO=9, FIM_FUNCAO=10, 
		FIM_REGISTRO=11, FIM_ENQUANTO=12, FIM_PARA=13, FIM_CASO=14, FIM_SE=15, 
		PROCEDIMENTO=16, CONSTANTE=17, REGISTRO=18, ENQUANTO=19, DECLARE=20, ESCREVA=21, 
		VERDADEIRO=22, RETORNE=23, LITERAL=24, INTEIRO=25, LOGICO=26, ALGORITMO=27, 
		FUNCAO=28, SENAO=29, ENTAO=30, SEJA=31, CASO=32, PARA=33, FACA=34, TIPO=35, 
		REAL=36, LEIA=37, NAO=38, VAR=39, ATE=40, FALSO=41, SE=42, PONTOS2=43, 
		ATRIB=44, DIFERENTE=45, MENOR_IGUAL=46, MAIOR_IGUAL=47, DOIS_PONTOS=48, 
		PONTO=49, VIRGULA=50, MENOR=51, MAIOR=52, IGUAL=53, MAIS=54, MENOS=55, 
		VEZES=56, DIV=57, MOD=58, CIRCUNFLEXO=59, E_COMERCIAL=60, ABRE_PAR=61, 
		FECHA_PAR=62, ABRE_COL=63, FECHA_COL=64, E_LOGICO=65, OU=66, IDENT=67, 
		SIMBOLO_NAO_IDENTIFICADO=68;
	public static final int
		RULE_programa = 0, RULE_declaracoes_globais = 1, RULE_declaracao_global = 2, 
		RULE_declaracao_tipo = 3, RULE_lista_campos_registro = 4, RULE_declaracao_constante_global = 5, 
		RULE_declaracao_procedimento = 6, RULE_declaracao_funcao = 7, RULE_lista_parametros = 8, 
		RULE_parametro = 9, RULE_lista_ident_dim = 10, RULE_ident_dim = 11, RULE_secao_principal = 12, 
		RULE_corpo = 13, RULE_declaracao_local = 14, RULE_tipo_corpo_tipo = 15, 
		RULE_variavel_decl = 16, RULE_tipo_estendido = 17, RULE_tipo_estendido_simples = 18, 
		RULE_tipo_basico = 19, RULE_valor_constante = 20, RULE_comando = 21, RULE_comando_atribuicao = 22, 
		RULE_lvalue = 23, RULE_comando_leia = 24, RULE_lista_lvalue = 25, RULE_comando_escreva = 26, 
		RULE_lista_expressoes = 27, RULE_comando_chamada = 28, RULE_comando_se = 29, 
		RULE_comando_caso = 30, RULE_item_caso = 31, RULE_selecao_caso = 32, RULE_comando_para = 33, 
		RULE_comando_enquanto = 34, RULE_comando_faca_ate = 35, RULE_expressao = 36, 
		RULE_expr_e = 37, RULE_expr_rel = 38, RULE_expr_arit = 39, RULE_termo = 40, 
		RULE_unario = 41, RULE_fator = 42, RULE_ident = 43;
	private static String[] makeRuleNames() {
		return new String[] {
			"programa", "declaracoes_globais", "declaracao_global", "declaracao_tipo", 
			"lista_campos_registro", "declaracao_constante_global", "declaracao_procedimento", 
			"declaracao_funcao", "lista_parametros", "parametro", "lista_ident_dim", 
			"ident_dim", "secao_principal", "corpo", "declaracao_local", "tipo_corpo_tipo", 
			"variavel_decl", "tipo_estendido", "tipo_estendido_simples", "tipo_basico", 
			"valor_constante", "comando", "comando_atribuicao", "lvalue", "comando_leia", 
			"lista_lvalue", "comando_escreva", "lista_expressoes", "comando_chamada", 
			"comando_se", "comando_caso", "item_caso", "selecao_caso", "comando_para", 
			"comando_enquanto", "comando_faca_ate", "expressao", "expr_e", "expr_rel", 
			"expr_arit", "termo", "unario", "fator", "ident"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "'fim_algoritmo'", "'fim_procedimento'", 
			"'fim_funcao'", "'fim_registro'", "'fim_enquanto'", "'fim_para'", "'fim_caso'", 
			"'fim_se'", "'procedimento'", "'constante'", "'registro'", "'enquanto'", 
			"'declare'", "'escreva'", "'verdadeiro'", "'retorne'", "'literal'", "'inteiro'", 
			"'logico'", "'algoritmo'", "'funcao'", "'senao'", "'entao'", "'seja'", 
			"'caso'", "'para'", "'faca'", "'tipo'", "'real'", "'leia'", "'nao'", 
			"'var'", "'ate'", "'falso'", "'se'", "'..'", "'<-'", "'<>'", "'<='", 
			"'>='", "':'", "'.'", "','", "'<'", "'>'", "'='", "'+'", "'-'", "'*'", 
			"'/'", "'%'", "'^'", "'&'", "'('", "')'", "'['", "']'", "'e'", "'ou'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "COMENTARIO", "COMENTARIO_NAO_FECHADO", "CADEIA", "CADEIA_NAO_FECHADA", 
			"NUM_REAL", "NUM_INT", "FIM_ALGORITMO", "FIM_PROCEDIMENTO", "FIM_FUNCAO", 
			"FIM_REGISTRO", "FIM_ENQUANTO", "FIM_PARA", "FIM_CASO", "FIM_SE", "PROCEDIMENTO", 
			"CONSTANTE", "REGISTRO", "ENQUANTO", "DECLARE", "ESCREVA", "VERDADEIRO", 
			"RETORNE", "LITERAL", "INTEIRO", "LOGICO", "ALGORITMO", "FUNCAO", "SENAO", 
			"ENTAO", "SEJA", "CASO", "PARA", "FACA", "TIPO", "REAL", "LEIA", "NAO", 
			"VAR", "ATE", "FALSO", "SE", "PONTOS2", "ATRIB", "DIFERENTE", "MENOR_IGUAL", 
			"MAIOR_IGUAL", "DOIS_PONTOS", "PONTO", "VIRGULA", "MENOR", "MAIOR", "IGUAL", 
			"MAIS", "MENOS", "VEZES", "DIV", "MOD", "CIRCUNFLEXO", "E_COMERCIAL", 
			"ABRE_PAR", "FECHA_PAR", "ABRE_COL", "FECHA_COL", "E_LOGICO", "OU", "IDENT", 
			"SIMBOLO_NAO_IDENTIFICADO"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "LA.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LAParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramaContext extends ParserRuleContext {
		public Declaracoes_globaisContext declaracoes_globais() {
			return getRuleContext(Declaracoes_globaisContext.class,0);
		}
		public Secao_principalContext secao_principal() {
			return getRuleContext(Secao_principalContext.class,0);
		}
		public TerminalNode EOF() { return getToken(LAParser.EOF, 0); }
		public ProgramaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_programa; }
	}

	public final ProgramaContext programa() throws RecognitionException {
		ProgramaContext _localctx = new ProgramaContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_programa);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			declaracoes_globais();
			setState(89);
			secao_principal();
			setState(90);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Declaracoes_globaisContext extends ParserRuleContext {
		public List<Declaracao_globalContext> declaracao_global() {
			return getRuleContexts(Declaracao_globalContext.class);
		}
		public Declaracao_globalContext declaracao_global(int i) {
			return getRuleContext(Declaracao_globalContext.class,i);
		}
		public Declaracoes_globaisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracoes_globais; }
	}

	public final Declaracoes_globaisContext declaracoes_globais() throws RecognitionException {
		Declaracoes_globaisContext _localctx = new Declaracoes_globaisContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_declaracoes_globais);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(92);
					declaracao_global();
					}
					} 
				}
				setState(97);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Declaracao_globalContext extends ParserRuleContext {
		public Declaracao_tipoContext declaracao_tipo() {
			return getRuleContext(Declaracao_tipoContext.class,0);
		}
		public Declaracao_constante_globalContext declaracao_constante_global() {
			return getRuleContext(Declaracao_constante_globalContext.class,0);
		}
		public Declaracao_procedimentoContext declaracao_procedimento() {
			return getRuleContext(Declaracao_procedimentoContext.class,0);
		}
		public Declaracao_funcaoContext declaracao_funcao() {
			return getRuleContext(Declaracao_funcaoContext.class,0);
		}
		public Declaracao_globalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracao_global; }
	}

	public final Declaracao_globalContext declaracao_global() throws RecognitionException {
		Declaracao_globalContext _localctx = new Declaracao_globalContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declaracao_global);
		try {
			setState(102);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TIPO:
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				declaracao_tipo();
				}
				break;
			case CONSTANTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(99);
				declaracao_constante_global();
				}
				break;
			case PROCEDIMENTO:
				enterOuterAlt(_localctx, 3);
				{
				setState(100);
				declaracao_procedimento();
				}
				break;
			case FUNCAO:
				enterOuterAlt(_localctx, 4);
				{
				setState(101);
				declaracao_funcao();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Declaracao_tipoContext extends ParserRuleContext {
		public TerminalNode TIPO() { return getToken(LAParser.TIPO, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode DOIS_PONTOS() { return getToken(LAParser.DOIS_PONTOS, 0); }
		public TerminalNode REGISTRO() { return getToken(LAParser.REGISTRO, 0); }
		public Lista_campos_registroContext lista_campos_registro() {
			return getRuleContext(Lista_campos_registroContext.class,0);
		}
		public TerminalNode FIM_REGISTRO() { return getToken(LAParser.FIM_REGISTRO, 0); }
		public Declaracao_tipoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracao_tipo; }
	}

	public final Declaracao_tipoContext declaracao_tipo() throws RecognitionException {
		Declaracao_tipoContext _localctx = new Declaracao_tipoContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_declaracao_tipo);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			match(TIPO);
			setState(105);
			ident();
			setState(106);
			match(DOIS_PONTOS);
			setState(107);
			match(REGISTRO);
			setState(108);
			lista_campos_registro();
			setState(109);
			match(FIM_REGISTRO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Lista_campos_registroContext extends ParserRuleContext {
		public List<Lista_ident_dimContext> lista_ident_dim() {
			return getRuleContexts(Lista_ident_dimContext.class);
		}
		public Lista_ident_dimContext lista_ident_dim(int i) {
			return getRuleContext(Lista_ident_dimContext.class,i);
		}
		public List<TerminalNode> DOIS_PONTOS() { return getTokens(LAParser.DOIS_PONTOS); }
		public TerminalNode DOIS_PONTOS(int i) {
			return getToken(LAParser.DOIS_PONTOS, i);
		}
		public List<Tipo_estendidoContext> tipo_estendido() {
			return getRuleContexts(Tipo_estendidoContext.class);
		}
		public Tipo_estendidoContext tipo_estendido(int i) {
			return getRuleContext(Tipo_estendidoContext.class,i);
		}
		public Lista_campos_registroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lista_campos_registro; }
	}

	public final Lista_campos_registroContext lista_campos_registro() throws RecognitionException {
		Lista_campos_registroContext _localctx = new Lista_campos_registroContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_lista_campos_registro);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(111);
				lista_ident_dim();
				setState(112);
				match(DOIS_PONTOS);
				setState(113);
				tipo_estendido();
				}
				}
				setState(117); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Declaracao_constante_globalContext extends ParserRuleContext {
		public TerminalNode CONSTANTE() { return getToken(LAParser.CONSTANTE, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode DOIS_PONTOS() { return getToken(LAParser.DOIS_PONTOS, 0); }
		public Tipo_basicoContext tipo_basico() {
			return getRuleContext(Tipo_basicoContext.class,0);
		}
		public TerminalNode IGUAL() { return getToken(LAParser.IGUAL, 0); }
		public Valor_constanteContext valor_constante() {
			return getRuleContext(Valor_constanteContext.class,0);
		}
		public Declaracao_constante_globalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracao_constante_global; }
	}

	public final Declaracao_constante_globalContext declaracao_constante_global() throws RecognitionException {
		Declaracao_constante_globalContext _localctx = new Declaracao_constante_globalContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_declaracao_constante_global);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(CONSTANTE);
			setState(120);
			ident();
			setState(121);
			match(DOIS_PONTOS);
			setState(122);
			tipo_basico();
			setState(123);
			match(IGUAL);
			setState(124);
			valor_constante();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Declaracao_procedimentoContext extends ParserRuleContext {
		public TerminalNode PROCEDIMENTO() { return getToken(LAParser.PROCEDIMENTO, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode ABRE_PAR() { return getToken(LAParser.ABRE_PAR, 0); }
		public TerminalNode FECHA_PAR() { return getToken(LAParser.FECHA_PAR, 0); }
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode FIM_PROCEDIMENTO() { return getToken(LAParser.FIM_PROCEDIMENTO, 0); }
		public Lista_parametrosContext lista_parametros() {
			return getRuleContext(Lista_parametrosContext.class,0);
		}
		public Declaracao_procedimentoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracao_procedimento; }
	}

	public final Declaracao_procedimentoContext declaracao_procedimento() throws RecognitionException {
		Declaracao_procedimentoContext _localctx = new Declaracao_procedimentoContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_declaracao_procedimento);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(PROCEDIMENTO);
			setState(127);
			ident();
			setState(128);
			match(ABRE_PAR);
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==IDENT) {
				{
				setState(129);
				lista_parametros();
				}
			}

			setState(132);
			match(FECHA_PAR);
			setState(133);
			corpo();
			setState(134);
			match(FIM_PROCEDIMENTO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Declaracao_funcaoContext extends ParserRuleContext {
		public TerminalNode FUNCAO() { return getToken(LAParser.FUNCAO, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode ABRE_PAR() { return getToken(LAParser.ABRE_PAR, 0); }
		public TerminalNode FECHA_PAR() { return getToken(LAParser.FECHA_PAR, 0); }
		public TerminalNode DOIS_PONTOS() { return getToken(LAParser.DOIS_PONTOS, 0); }
		public Tipo_estendidoContext tipo_estendido() {
			return getRuleContext(Tipo_estendidoContext.class,0);
		}
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode FIM_FUNCAO() { return getToken(LAParser.FIM_FUNCAO, 0); }
		public Lista_parametrosContext lista_parametros() {
			return getRuleContext(Lista_parametrosContext.class,0);
		}
		public Declaracao_funcaoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracao_funcao; }
	}

	public final Declaracao_funcaoContext declaracao_funcao() throws RecognitionException {
		Declaracao_funcaoContext _localctx = new Declaracao_funcaoContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_declaracao_funcao);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(FUNCAO);
			setState(137);
			ident();
			setState(138);
			match(ABRE_PAR);
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==IDENT) {
				{
				setState(139);
				lista_parametros();
				}
			}

			setState(142);
			match(FECHA_PAR);
			setState(143);
			match(DOIS_PONTOS);
			setState(144);
			tipo_estendido();
			setState(145);
			corpo();
			setState(146);
			match(FIM_FUNCAO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Lista_parametrosContext extends ParserRuleContext {
		public List<ParametroContext> parametro() {
			return getRuleContexts(ParametroContext.class);
		}
		public ParametroContext parametro(int i) {
			return getRuleContext(ParametroContext.class,i);
		}
		public List<TerminalNode> VIRGULA() { return getTokens(LAParser.VIRGULA); }
		public TerminalNode VIRGULA(int i) {
			return getToken(LAParser.VIRGULA, i);
		}
		public Lista_parametrosContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lista_parametros; }
	}

	public final Lista_parametrosContext lista_parametros() throws RecognitionException {
		Lista_parametrosContext _localctx = new Lista_parametrosContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_lista_parametros);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			parametro();
			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VIRGULA) {
				{
				{
				setState(149);
				match(VIRGULA);
				setState(150);
				parametro();
				}
				}
				setState(155);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParametroContext extends ParserRuleContext {
		public Lista_ident_dimContext lista_ident_dim() {
			return getRuleContext(Lista_ident_dimContext.class,0);
		}
		public TerminalNode DOIS_PONTOS() { return getToken(LAParser.DOIS_PONTOS, 0); }
		public Tipo_estendidoContext tipo_estendido() {
			return getRuleContext(Tipo_estendidoContext.class,0);
		}
		public TerminalNode VAR() { return getToken(LAParser.VAR, 0); }
		public ParametroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parametro; }
	}

	public final ParametroContext parametro() throws RecognitionException {
		ParametroContext _localctx = new ParametroContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_parametro);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR) {
				{
				setState(156);
				match(VAR);
				}
			}

			setState(159);
			lista_ident_dim();
			setState(160);
			match(DOIS_PONTOS);
			setState(161);
			tipo_estendido();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Lista_ident_dimContext extends ParserRuleContext {
		public List<Ident_dimContext> ident_dim() {
			return getRuleContexts(Ident_dimContext.class);
		}
		public Ident_dimContext ident_dim(int i) {
			return getRuleContext(Ident_dimContext.class,i);
		}
		public List<TerminalNode> VIRGULA() { return getTokens(LAParser.VIRGULA); }
		public TerminalNode VIRGULA(int i) {
			return getToken(LAParser.VIRGULA, i);
		}
		public Lista_ident_dimContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lista_ident_dim; }
	}

	public final Lista_ident_dimContext lista_ident_dim() throws RecognitionException {
		Lista_ident_dimContext _localctx = new Lista_ident_dimContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_lista_ident_dim);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			ident_dim();
			setState(168);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VIRGULA) {
				{
				{
				setState(164);
				match(VIRGULA);
				setState(165);
				ident_dim();
				}
				}
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Ident_dimContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public List<TerminalNode> ABRE_COL() { return getTokens(LAParser.ABRE_COL); }
		public TerminalNode ABRE_COL(int i) {
			return getToken(LAParser.ABRE_COL, i);
		}
		public List<ExpressaoContext> expressao() {
			return getRuleContexts(ExpressaoContext.class);
		}
		public ExpressaoContext expressao(int i) {
			return getRuleContext(ExpressaoContext.class,i);
		}
		public List<TerminalNode> FECHA_COL() { return getTokens(LAParser.FECHA_COL); }
		public TerminalNode FECHA_COL(int i) {
			return getToken(LAParser.FECHA_COL, i);
		}
		public Ident_dimContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ident_dim; }
	}

	public final Ident_dimContext ident_dim() throws RecognitionException {
		Ident_dimContext _localctx = new Ident_dimContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ident_dim);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			ident();
			setState(178);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ABRE_COL) {
				{
				{
				setState(172);
				match(ABRE_COL);
				setState(173);
				expressao(0);
				setState(174);
				match(FECHA_COL);
				}
				}
				setState(180);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Secao_principalContext extends ParserRuleContext {
		public Secao_principalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_secao_principal; }
	 
		public Secao_principalContext() { }
		public void copyFrom(Secao_principalContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrincipalSemPalavraAlgoritmoContext extends Secao_principalContext {
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode FIM_ALGORITMO() { return getToken(LAParser.FIM_ALGORITMO, 0); }
		public PrincipalSemPalavraAlgoritmoContext(Secao_principalContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrincipalComCabecalhoContext extends Secao_principalContext {
		public TerminalNode ALGORITMO() { return getToken(LAParser.ALGORITMO, 0); }
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode FIM_ALGORITMO() { return getToken(LAParser.FIM_ALGORITMO, 0); }
		public PrincipalComCabecalhoContext(Secao_principalContext ctx) { copyFrom(ctx); }
	}

	public final Secao_principalContext secao_principal() throws RecognitionException {
		Secao_principalContext _localctx = new Secao_principalContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_secao_principal);
		try {
			setState(188);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ALGORITMO:
				_localctx = new PrincipalComCabecalhoContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(181);
				match(ALGORITMO);
				setState(182);
				corpo();
				setState(183);
				match(FIM_ALGORITMO);
				}
				break;
			case FIM_ALGORITMO:
			case CONSTANTE:
			case ENQUANTO:
			case DECLARE:
			case ESCREVA:
			case RETORNE:
			case CASO:
			case PARA:
			case FACA:
			case TIPO:
			case LEIA:
			case SE:
			case CIRCUNFLEXO:
			case IDENT:
				_localctx = new PrincipalSemPalavraAlgoritmoContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(185);
				corpo();
				setState(186);
				match(FIM_ALGORITMO);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CorpoContext extends ParserRuleContext {
		public List<Declaracao_localContext> declaracao_local() {
			return getRuleContexts(Declaracao_localContext.class);
		}
		public Declaracao_localContext declaracao_local(int i) {
			return getRuleContext(Declaracao_localContext.class,i);
		}
		public List<ComandoContext> comando() {
			return getRuleContexts(ComandoContext.class);
		}
		public ComandoContext comando(int i) {
			return getRuleContext(ComandoContext.class,i);
		}
		public CorpoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_corpo; }
	}

	public final CorpoContext corpo() throws RecognitionException {
		CorpoContext _localctx = new CorpoContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_corpo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 34360918016L) != 0)) {
				{
				{
				setState(190);
				declaracao_local();
				}
				}
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 19)) & ~0x3f) == 0 && ((1L << (_la - 19)) & 282574497046549L) != 0)) {
				{
				{
				setState(196);
				comando();
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Declaracao_localContext extends ParserRuleContext {
		public TerminalNode DECLARE() { return getToken(LAParser.DECLARE, 0); }
		public List<Variavel_declContext> variavel_decl() {
			return getRuleContexts(Variavel_declContext.class);
		}
		public Variavel_declContext variavel_decl(int i) {
			return getRuleContext(Variavel_declContext.class,i);
		}
		public TerminalNode CONSTANTE() { return getToken(LAParser.CONSTANTE, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode DOIS_PONTOS() { return getToken(LAParser.DOIS_PONTOS, 0); }
		public Tipo_basicoContext tipo_basico() {
			return getRuleContext(Tipo_basicoContext.class,0);
		}
		public TerminalNode IGUAL() { return getToken(LAParser.IGUAL, 0); }
		public Valor_constanteContext valor_constante() {
			return getRuleContext(Valor_constanteContext.class,0);
		}
		public TerminalNode TIPO() { return getToken(LAParser.TIPO, 0); }
		public Tipo_corpo_tipoContext tipo_corpo_tipo() {
			return getRuleContext(Tipo_corpo_tipoContext.class,0);
		}
		public Declaracao_localContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracao_local; }
	}

	public final Declaracao_localContext declaracao_local() throws RecognitionException {
		Declaracao_localContext _localctx = new Declaracao_localContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_declaracao_local);
		try {
			int _alt;
			setState(220);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DECLARE:
				enterOuterAlt(_localctx, 1);
				{
				setState(202);
				match(DECLARE);
				setState(204); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(203);
						variavel_decl();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(206); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case CONSTANTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(208);
				match(CONSTANTE);
				setState(209);
				ident();
				setState(210);
				match(DOIS_PONTOS);
				setState(211);
				tipo_basico();
				setState(212);
				match(IGUAL);
				setState(213);
				valor_constante();
				}
				break;
			case TIPO:
				enterOuterAlt(_localctx, 3);
				{
				setState(215);
				match(TIPO);
				setState(216);
				ident();
				setState(217);
				match(DOIS_PONTOS);
				setState(218);
				tipo_corpo_tipo();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Tipo_corpo_tipoContext extends ParserRuleContext {
		public TerminalNode REGISTRO() { return getToken(LAParser.REGISTRO, 0); }
		public Lista_campos_registroContext lista_campos_registro() {
			return getRuleContext(Lista_campos_registroContext.class,0);
		}
		public TerminalNode FIM_REGISTRO() { return getToken(LAParser.FIM_REGISTRO, 0); }
		public Tipo_estendido_simplesContext tipo_estendido_simples() {
			return getRuleContext(Tipo_estendido_simplesContext.class,0);
		}
		public Tipo_corpo_tipoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipo_corpo_tipo; }
	}

	public final Tipo_corpo_tipoContext tipo_corpo_tipo() throws RecognitionException {
		Tipo_corpo_tipoContext _localctx = new Tipo_corpo_tipoContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_tipo_corpo_tipo);
		try {
			setState(227);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REGISTRO:
				enterOuterAlt(_localctx, 1);
				{
				setState(222);
				match(REGISTRO);
				setState(223);
				lista_campos_registro();
				setState(224);
				match(FIM_REGISTRO);
				}
				break;
			case LITERAL:
			case INTEIRO:
			case LOGICO:
			case REAL:
			case CIRCUNFLEXO:
			case IDENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(226);
				tipo_estendido_simples();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Variavel_declContext extends ParserRuleContext {
		public Lista_ident_dimContext lista_ident_dim() {
			return getRuleContext(Lista_ident_dimContext.class,0);
		}
		public TerminalNode DOIS_PONTOS() { return getToken(LAParser.DOIS_PONTOS, 0); }
		public Tipo_estendidoContext tipo_estendido() {
			return getRuleContext(Tipo_estendidoContext.class,0);
		}
		public Variavel_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variavel_decl; }
	}

	public final Variavel_declContext variavel_decl() throws RecognitionException {
		Variavel_declContext _localctx = new Variavel_declContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_variavel_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			lista_ident_dim();
			setState(230);
			match(DOIS_PONTOS);
			setState(231);
			tipo_estendido();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Tipo_estendidoContext extends ParserRuleContext {
		public TerminalNode REGISTRO() { return getToken(LAParser.REGISTRO, 0); }
		public Lista_campos_registroContext lista_campos_registro() {
			return getRuleContext(Lista_campos_registroContext.class,0);
		}
		public TerminalNode FIM_REGISTRO() { return getToken(LAParser.FIM_REGISTRO, 0); }
		public Tipo_estendido_simplesContext tipo_estendido_simples() {
			return getRuleContext(Tipo_estendido_simplesContext.class,0);
		}
		public Tipo_estendidoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipo_estendido; }
	}

	public final Tipo_estendidoContext tipo_estendido() throws RecognitionException {
		Tipo_estendidoContext _localctx = new Tipo_estendidoContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_tipo_estendido);
		try {
			setState(238);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REGISTRO:
				enterOuterAlt(_localctx, 1);
				{
				setState(233);
				match(REGISTRO);
				setState(234);
				lista_campos_registro();
				setState(235);
				match(FIM_REGISTRO);
				}
				break;
			case LITERAL:
			case INTEIRO:
			case LOGICO:
			case REAL:
			case CIRCUNFLEXO:
			case IDENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(237);
				tipo_estendido_simples();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Tipo_estendido_simplesContext extends ParserRuleContext {
		public Tipo_basicoContext tipo_basico() {
			return getRuleContext(Tipo_basicoContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode CIRCUNFLEXO() { return getToken(LAParser.CIRCUNFLEXO, 0); }
		public Tipo_estendido_simplesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipo_estendido_simples; }
	}

	public final Tipo_estendido_simplesContext tipo_estendido_simples() throws RecognitionException {
		Tipo_estendido_simplesContext _localctx = new Tipo_estendido_simplesContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_tipo_estendido_simples);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CIRCUNFLEXO) {
				{
				setState(240);
				match(CIRCUNFLEXO);
				}
			}

			setState(245);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LITERAL:
			case INTEIRO:
			case LOGICO:
			case REAL:
				{
				setState(243);
				tipo_basico();
				}
				break;
			case IDENT:
				{
				setState(244);
				ident();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Tipo_basicoContext extends ParserRuleContext {
		public TerminalNode LITERAL() { return getToken(LAParser.LITERAL, 0); }
		public TerminalNode INTEIRO() { return getToken(LAParser.INTEIRO, 0); }
		public TerminalNode REAL() { return getToken(LAParser.REAL, 0); }
		public TerminalNode LOGICO() { return getToken(LAParser.LOGICO, 0); }
		public Tipo_basicoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipo_basico; }
	}

	public final Tipo_basicoContext tipo_basico() throws RecognitionException {
		Tipo_basicoContext _localctx = new Tipo_basicoContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_tipo_basico);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 68836917248L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Valor_constanteContext extends ParserRuleContext {
		public TerminalNode CADEIA() { return getToken(LAParser.CADEIA, 0); }
		public TerminalNode NUM_INT() { return getToken(LAParser.NUM_INT, 0); }
		public TerminalNode NUM_REAL() { return getToken(LAParser.NUM_REAL, 0); }
		public TerminalNode VERDADEIRO() { return getToken(LAParser.VERDADEIRO, 0); }
		public TerminalNode FALSO() { return getToken(LAParser.FALSO, 0); }
		public Valor_constanteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_valor_constante; }
	}

	public final Valor_constanteContext valor_constante() throws RecognitionException {
		Valor_constanteContext _localctx = new Valor_constanteContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_valor_constante);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 2199027450064L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComandoContext extends ParserRuleContext {
		public Comando_atribuicaoContext comando_atribuicao() {
			return getRuleContext(Comando_atribuicaoContext.class,0);
		}
		public Comando_leiaContext comando_leia() {
			return getRuleContext(Comando_leiaContext.class,0);
		}
		public Comando_escrevaContext comando_escreva() {
			return getRuleContext(Comando_escrevaContext.class,0);
		}
		public Comando_seContext comando_se() {
			return getRuleContext(Comando_seContext.class,0);
		}
		public Comando_casoContext comando_caso() {
			return getRuleContext(Comando_casoContext.class,0);
		}
		public Comando_paraContext comando_para() {
			return getRuleContext(Comando_paraContext.class,0);
		}
		public Comando_enquantoContext comando_enquanto() {
			return getRuleContext(Comando_enquantoContext.class,0);
		}
		public Comando_faca_ateContext comando_faca_ate() {
			return getRuleContext(Comando_faca_ateContext.class,0);
		}
		public Comando_chamadaContext comando_chamada() {
			return getRuleContext(Comando_chamadaContext.class,0);
		}
		public TerminalNode RETORNE() { return getToken(LAParser.RETORNE, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public ComandoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando; }
	}

	public final ComandoContext comando() throws RecognitionException {
		ComandoContext _localctx = new ComandoContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_comando);
		try {
			setState(264);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(251);
				comando_atribuicao();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(252);
				comando_leia();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(253);
				comando_escreva();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(254);
				comando_se();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(255);
				comando_caso();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(256);
				comando_para();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(257);
				comando_enquanto();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(258);
				comando_faca_ate();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(259);
				comando_chamada();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(260);
				match(RETORNE);
				setState(262);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(261);
					expressao(0);
					}
					break;
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_atribuicaoContext extends ParserRuleContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode ATRIB() { return getToken(LAParser.ATRIB, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public Comando_atribuicaoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_atribuicao; }
	}

	public final Comando_atribuicaoContext comando_atribuicao() throws RecognitionException {
		Comando_atribuicaoContext _localctx = new Comando_atribuicaoContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_comando_atribuicao);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			lvalue();
			setState(267);
			match(ATRIB);
			setState(268);
			expressao(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LvalueContext extends ParserRuleContext {
		public List<IdentContext> ident() {
			return getRuleContexts(IdentContext.class);
		}
		public IdentContext ident(int i) {
			return getRuleContext(IdentContext.class,i);
		}
		public TerminalNode CIRCUNFLEXO() { return getToken(LAParser.CIRCUNFLEXO, 0); }
		public List<TerminalNode> PONTO() { return getTokens(LAParser.PONTO); }
		public TerminalNode PONTO(int i) {
			return getToken(LAParser.PONTO, i);
		}
		public List<TerminalNode> ABRE_COL() { return getTokens(LAParser.ABRE_COL); }
		public TerminalNode ABRE_COL(int i) {
			return getToken(LAParser.ABRE_COL, i);
		}
		public List<ExpressaoContext> expressao() {
			return getRuleContexts(ExpressaoContext.class);
		}
		public ExpressaoContext expressao(int i) {
			return getRuleContext(ExpressaoContext.class,i);
		}
		public List<TerminalNode> FECHA_COL() { return getTokens(LAParser.FECHA_COL); }
		public TerminalNode FECHA_COL(int i) {
			return getToken(LAParser.FECHA_COL, i);
		}
		public LvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvalue; }
	}

	public final LvalueContext lvalue() throws RecognitionException {
		LvalueContext _localctx = new LvalueContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_lvalue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CIRCUNFLEXO) {
				{
				setState(270);
				match(CIRCUNFLEXO);
				}
			}

			setState(273);
			ident();
			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PONTO || _la==ABRE_COL) {
				{
				setState(280);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case PONTO:
					{
					{
					setState(274);
					match(PONTO);
					setState(275);
					ident();
					}
					}
					break;
				case ABRE_COL:
					{
					{
					setState(276);
					match(ABRE_COL);
					setState(277);
					expressao(0);
					setState(278);
					match(FECHA_COL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(284);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_leiaContext extends ParserRuleContext {
		public TerminalNode LEIA() { return getToken(LAParser.LEIA, 0); }
		public TerminalNode ABRE_PAR() { return getToken(LAParser.ABRE_PAR, 0); }
		public Lista_lvalueContext lista_lvalue() {
			return getRuleContext(Lista_lvalueContext.class,0);
		}
		public TerminalNode FECHA_PAR() { return getToken(LAParser.FECHA_PAR, 0); }
		public Comando_leiaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_leia; }
	}

	public final Comando_leiaContext comando_leia() throws RecognitionException {
		Comando_leiaContext _localctx = new Comando_leiaContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_comando_leia);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(LEIA);
			setState(286);
			match(ABRE_PAR);
			setState(287);
			lista_lvalue();
			setState(288);
			match(FECHA_PAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Lista_lvalueContext extends ParserRuleContext {
		public List<LvalueContext> lvalue() {
			return getRuleContexts(LvalueContext.class);
		}
		public LvalueContext lvalue(int i) {
			return getRuleContext(LvalueContext.class,i);
		}
		public List<TerminalNode> VIRGULA() { return getTokens(LAParser.VIRGULA); }
		public TerminalNode VIRGULA(int i) {
			return getToken(LAParser.VIRGULA, i);
		}
		public Lista_lvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lista_lvalue; }
	}

	public final Lista_lvalueContext lista_lvalue() throws RecognitionException {
		Lista_lvalueContext _localctx = new Lista_lvalueContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_lista_lvalue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			lvalue();
			setState(295);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VIRGULA) {
				{
				{
				setState(291);
				match(VIRGULA);
				setState(292);
				lvalue();
				}
				}
				setState(297);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_escrevaContext extends ParserRuleContext {
		public TerminalNode ESCREVA() { return getToken(LAParser.ESCREVA, 0); }
		public TerminalNode ABRE_PAR() { return getToken(LAParser.ABRE_PAR, 0); }
		public Lista_expressoesContext lista_expressoes() {
			return getRuleContext(Lista_expressoesContext.class,0);
		}
		public TerminalNode FECHA_PAR() { return getToken(LAParser.FECHA_PAR, 0); }
		public Comando_escrevaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_escreva; }
	}

	public final Comando_escrevaContext comando_escreva() throws RecognitionException {
		Comando_escrevaContext _localctx = new Comando_escrevaContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_comando_escreva);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			match(ESCREVA);
			setState(299);
			match(ABRE_PAR);
			setState(300);
			lista_expressoes();
			setState(301);
			match(FECHA_PAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Lista_expressoesContext extends ParserRuleContext {
		public List<ExpressaoContext> expressao() {
			return getRuleContexts(ExpressaoContext.class);
		}
		public ExpressaoContext expressao(int i) {
			return getRuleContext(ExpressaoContext.class,i);
		}
		public List<TerminalNode> VIRGULA() { return getTokens(LAParser.VIRGULA); }
		public TerminalNode VIRGULA(int i) {
			return getToken(LAParser.VIRGULA, i);
		}
		public Lista_expressoesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lista_expressoes; }
	}

	public final Lista_expressoesContext lista_expressoes() throws RecognitionException {
		Lista_expressoesContext _localctx = new Lista_expressoesContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_lista_expressoes);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			expressao(0);
			setState(308);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VIRGULA) {
				{
				{
				setState(304);
				match(VIRGULA);
				setState(305);
				expressao(0);
				}
				}
				setState(310);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_chamadaContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode ABRE_PAR() { return getToken(LAParser.ABRE_PAR, 0); }
		public TerminalNode FECHA_PAR() { return getToken(LAParser.FECHA_PAR, 0); }
		public Lista_expressoesContext lista_expressoes() {
			return getRuleContext(Lista_expressoesContext.class,0);
		}
		public Comando_chamadaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_chamada; }
	}

	public final Comando_chamadaContext comando_chamada() throws RecognitionException {
		Comando_chamadaContext _localctx = new Comando_chamadaContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_comando_chamada);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(311);
			ident();
			setState(312);
			match(ABRE_PAR);
			setState(314);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 4)) & ~0x3f) == 0 && ((1L << (_la - 4)) & -8968918503289257971L) != 0)) {
				{
				setState(313);
				lista_expressoes();
				}
			}

			setState(316);
			match(FECHA_PAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_seContext extends ParserRuleContext {
		public TerminalNode SE() { return getToken(LAParser.SE, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public TerminalNode ENTAO() { return getToken(LAParser.ENTAO, 0); }
		public List<CorpoContext> corpo() {
			return getRuleContexts(CorpoContext.class);
		}
		public CorpoContext corpo(int i) {
			return getRuleContext(CorpoContext.class,i);
		}
		public TerminalNode FIM_SE() { return getToken(LAParser.FIM_SE, 0); }
		public TerminalNode SENAO() { return getToken(LAParser.SENAO, 0); }
		public Comando_seContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_se; }
	}

	public final Comando_seContext comando_se() throws RecognitionException {
		Comando_seContext _localctx = new Comando_seContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_comando_se);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
			match(SE);
			setState(319);
			expressao(0);
			setState(320);
			match(ENTAO);
			setState(321);
			corpo();
			setState(324);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SENAO) {
				{
				setState(322);
				match(SENAO);
				setState(323);
				corpo();
				}
			}

			setState(326);
			match(FIM_SE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_casoContext extends ParserRuleContext {
		public TerminalNode CASO() { return getToken(LAParser.CASO, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public TerminalNode SEJA() { return getToken(LAParser.SEJA, 0); }
		public TerminalNode FIM_CASO() { return getToken(LAParser.FIM_CASO, 0); }
		public List<Item_casoContext> item_caso() {
			return getRuleContexts(Item_casoContext.class);
		}
		public Item_casoContext item_caso(int i) {
			return getRuleContext(Item_casoContext.class,i);
		}
		public TerminalNode SENAO() { return getToken(LAParser.SENAO, 0); }
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public Comando_casoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_caso; }
	}

	public final Comando_casoContext comando_caso() throws RecognitionException {
		Comando_casoContext _localctx = new Comando_casoContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_comando_caso);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			match(CASO);
			setState(329);
			expressao(0);
			setState(330);
			match(SEJA);
			setState(332); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(331);
				item_caso();
				}
				}
				setState(334); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CADEIA || _la==NUM_INT );
			setState(338);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SENAO) {
				{
				setState(336);
				match(SENAO);
				setState(337);
				corpo();
				}
			}

			setState(340);
			match(FIM_CASO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Item_casoContext extends ParserRuleContext {
		public Selecao_casoContext selecao_caso() {
			return getRuleContext(Selecao_casoContext.class,0);
		}
		public TerminalNode DOIS_PONTOS() { return getToken(LAParser.DOIS_PONTOS, 0); }
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public Item_casoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_item_caso; }
	}

	public final Item_casoContext item_caso() throws RecognitionException {
		Item_casoContext _localctx = new Item_casoContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_item_caso);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			selecao_caso();
			setState(343);
			match(DOIS_PONTOS);
			setState(344);
			corpo();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Selecao_casoContext extends ParserRuleContext {
		public List<TerminalNode> NUM_INT() { return getTokens(LAParser.NUM_INT); }
		public TerminalNode NUM_INT(int i) {
			return getToken(LAParser.NUM_INT, i);
		}
		public TerminalNode PONTOS2() { return getToken(LAParser.PONTOS2, 0); }
		public TerminalNode CADEIA() { return getToken(LAParser.CADEIA, 0); }
		public Selecao_casoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selecao_caso; }
	}

	public final Selecao_casoContext selecao_caso() throws RecognitionException {
		Selecao_casoContext _localctx = new Selecao_casoContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_selecao_caso);
		int _la;
		try {
			setState(352);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUM_INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(346);
				match(NUM_INT);
				setState(349);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==PONTOS2) {
					{
					setState(347);
					match(PONTOS2);
					setState(348);
					match(NUM_INT);
					}
				}

				}
				break;
			case CADEIA:
				enterOuterAlt(_localctx, 2);
				{
				setState(351);
				match(CADEIA);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_paraContext extends ParserRuleContext {
		public TerminalNode PARA() { return getToken(LAParser.PARA, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode ATRIB() { return getToken(LAParser.ATRIB, 0); }
		public List<ExpressaoContext> expressao() {
			return getRuleContexts(ExpressaoContext.class);
		}
		public ExpressaoContext expressao(int i) {
			return getRuleContext(ExpressaoContext.class,i);
		}
		public TerminalNode ATE() { return getToken(LAParser.ATE, 0); }
		public TerminalNode FACA() { return getToken(LAParser.FACA, 0); }
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode FIM_PARA() { return getToken(LAParser.FIM_PARA, 0); }
		public Comando_paraContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_para; }
	}

	public final Comando_paraContext comando_para() throws RecognitionException {
		Comando_paraContext _localctx = new Comando_paraContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_comando_para);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(354);
			match(PARA);
			setState(355);
			ident();
			setState(356);
			match(ATRIB);
			setState(357);
			expressao(0);
			setState(358);
			match(ATE);
			setState(359);
			expressao(0);
			setState(360);
			match(FACA);
			setState(361);
			corpo();
			setState(362);
			match(FIM_PARA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_enquantoContext extends ParserRuleContext {
		public TerminalNode ENQUANTO() { return getToken(LAParser.ENQUANTO, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public TerminalNode FACA() { return getToken(LAParser.FACA, 0); }
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode FIM_ENQUANTO() { return getToken(LAParser.FIM_ENQUANTO, 0); }
		public Comando_enquantoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_enquanto; }
	}

	public final Comando_enquantoContext comando_enquanto() throws RecognitionException {
		Comando_enquantoContext _localctx = new Comando_enquantoContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_comando_enquanto);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(364);
			match(ENQUANTO);
			setState(365);
			expressao(0);
			setState(366);
			match(FACA);
			setState(367);
			corpo();
			setState(368);
			match(FIM_ENQUANTO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comando_faca_ateContext extends ParserRuleContext {
		public TerminalNode FACA() { return getToken(LAParser.FACA, 0); }
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode ATE() { return getToken(LAParser.ATE, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public Comando_faca_ateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando_faca_ate; }
	}

	public final Comando_faca_ateContext comando_faca_ate() throws RecognitionException {
		Comando_faca_ateContext _localctx = new Comando_faca_ateContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_comando_faca_ate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(370);
			match(FACA);
			setState(371);
			corpo();
			setState(372);
			match(ATE);
			setState(373);
			expressao(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressaoContext extends ParserRuleContext {
		public Expr_eContext expr_e() {
			return getRuleContext(Expr_eContext.class,0);
		}
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public TerminalNode OU() { return getToken(LAParser.OU, 0); }
		public ExpressaoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressao; }
	}

	public final ExpressaoContext expressao() throws RecognitionException {
		return expressao(0);
	}

	private ExpressaoContext expressao(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressaoContext _localctx = new ExpressaoContext(_ctx, _parentState);
		ExpressaoContext _prevctx = _localctx;
		int _startState = 72;
		enterRecursionRule(_localctx, 72, RULE_expressao, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(376);
			expr_e(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(383);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExpressaoContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expressao);
					setState(378);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(379);
					match(OU);
					setState(380);
					expr_e(0);
					}
					} 
				}
				setState(385);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Expr_eContext extends ParserRuleContext {
		public Expr_relContext expr_rel() {
			return getRuleContext(Expr_relContext.class,0);
		}
		public Expr_eContext expr_e() {
			return getRuleContext(Expr_eContext.class,0);
		}
		public TerminalNode E_LOGICO() { return getToken(LAParser.E_LOGICO, 0); }
		public Expr_eContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr_e; }
	}

	public final Expr_eContext expr_e() throws RecognitionException {
		return expr_e(0);
	}

	private Expr_eContext expr_e(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Expr_eContext _localctx = new Expr_eContext(_ctx, _parentState);
		Expr_eContext _prevctx = _localctx;
		int _startState = 74;
		enterRecursionRule(_localctx, 74, RULE_expr_e, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(387);
			expr_rel(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(394);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Expr_eContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expr_e);
					setState(389);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(390);
					match(E_LOGICO);
					setState(391);
					expr_rel(0);
					}
					} 
				}
				setState(396);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Expr_relContext extends ParserRuleContext {
		public Token op;
		public Expr_aritContext expr_arit() {
			return getRuleContext(Expr_aritContext.class,0);
		}
		public Expr_relContext expr_rel() {
			return getRuleContext(Expr_relContext.class,0);
		}
		public TerminalNode IGUAL() { return getToken(LAParser.IGUAL, 0); }
		public TerminalNode DIFERENTE() { return getToken(LAParser.DIFERENTE, 0); }
		public TerminalNode MENOR() { return getToken(LAParser.MENOR, 0); }
		public TerminalNode MENOR_IGUAL() { return getToken(LAParser.MENOR_IGUAL, 0); }
		public TerminalNode MAIOR() { return getToken(LAParser.MAIOR, 0); }
		public TerminalNode MAIOR_IGUAL() { return getToken(LAParser.MAIOR_IGUAL, 0); }
		public Expr_relContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr_rel; }
	}

	public final Expr_relContext expr_rel() throws RecognitionException {
		return expr_rel(0);
	}

	private Expr_relContext expr_rel(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Expr_relContext _localctx = new Expr_relContext(_ctx, _parentState);
		Expr_relContext _prevctx = _localctx;
		int _startState = 76;
		enterRecursionRule(_localctx, 76, RULE_expr_rel, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(398);
			expr_arit(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(405);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Expr_relContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expr_rel);
					setState(400);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(401);
					((Expr_relContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 16008889300418560L) != 0)) ) {
						((Expr_relContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(402);
					expr_arit(0);
					}
					} 
				}
				setState(407);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Expr_aritContext extends ParserRuleContext {
		public Token op;
		public TermoContext termo() {
			return getRuleContext(TermoContext.class,0);
		}
		public Expr_aritContext expr_arit() {
			return getRuleContext(Expr_aritContext.class,0);
		}
		public TerminalNode MAIS() { return getToken(LAParser.MAIS, 0); }
		public TerminalNode MENOS() { return getToken(LAParser.MENOS, 0); }
		public Expr_aritContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr_arit; }
	}

	public final Expr_aritContext expr_arit() throws RecognitionException {
		return expr_arit(0);
	}

	private Expr_aritContext expr_arit(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Expr_aritContext _localctx = new Expr_aritContext(_ctx, _parentState);
		Expr_aritContext _prevctx = _localctx;
		int _startState = 78;
		enterRecursionRule(_localctx, 78, RULE_expr_arit, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(409);
			termo(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(416);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Expr_aritContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expr_arit);
					setState(411);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(412);
					((Expr_aritContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==MAIS || _la==MENOS) ) {
						((Expr_aritContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(413);
					termo(0);
					}
					} 
				}
				setState(418);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TermoContext extends ParserRuleContext {
		public Token op;
		public UnarioContext unario() {
			return getRuleContext(UnarioContext.class,0);
		}
		public TermoContext termo() {
			return getRuleContext(TermoContext.class,0);
		}
		public TerminalNode VEZES() { return getToken(LAParser.VEZES, 0); }
		public TerminalNode DIV() { return getToken(LAParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(LAParser.MOD, 0); }
		public TermoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termo; }
	}

	public final TermoContext termo() throws RecognitionException {
		return termo(0);
	}

	private TermoContext termo(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TermoContext _localctx = new TermoContext(_ctx, _parentState);
		TermoContext _prevctx = _localctx;
		int _startState = 80;
		enterRecursionRule(_localctx, 80, RULE_termo, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(420);
			unario();
			}
			_ctx.stop = _input.LT(-1);
			setState(427);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TermoContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_termo);
					setState(422);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(423);
					((TermoContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 504403158265495552L) != 0)) ) {
						((TermoContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(424);
					unario();
					}
					} 
				}
				setState(429);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnarioContext extends ParserRuleContext {
		public TerminalNode MENOS() { return getToken(LAParser.MENOS, 0); }
		public UnarioContext unario() {
			return getRuleContext(UnarioContext.class,0);
		}
		public TerminalNode NAO() { return getToken(LAParser.NAO, 0); }
		public FatorContext fator() {
			return getRuleContext(FatorContext.class,0);
		}
		public UnarioContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unario; }
	}

	public final UnarioContext unario() throws RecognitionException {
		UnarioContext _localctx = new UnarioContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_unario);
		try {
			setState(435);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MENOS:
				enterOuterAlt(_localctx, 1);
				{
				setState(430);
				match(MENOS);
				setState(431);
				unario();
				}
				break;
			case NAO:
				enterOuterAlt(_localctx, 2);
				{
				setState(432);
				match(NAO);
				setState(433);
				unario();
				}
				break;
			case CADEIA:
			case NUM_REAL:
			case NUM_INT:
			case VERDADEIRO:
			case FALSO:
			case CIRCUNFLEXO:
			case E_COMERCIAL:
			case ABRE_PAR:
			case IDENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(434);
				fator();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FatorContext extends ParserRuleContext {
		public TerminalNode ABRE_PAR() { return getToken(LAParser.ABRE_PAR, 0); }
		public List<ExpressaoContext> expressao() {
			return getRuleContexts(ExpressaoContext.class);
		}
		public ExpressaoContext expressao(int i) {
			return getRuleContext(ExpressaoContext.class,i);
		}
		public TerminalNode FECHA_PAR() { return getToken(LAParser.FECHA_PAR, 0); }
		public TerminalNode NUM_INT() { return getToken(LAParser.NUM_INT, 0); }
		public TerminalNode NUM_REAL() { return getToken(LAParser.NUM_REAL, 0); }
		public TerminalNode CADEIA() { return getToken(LAParser.CADEIA, 0); }
		public TerminalNode VERDADEIRO() { return getToken(LAParser.VERDADEIRO, 0); }
		public TerminalNode FALSO() { return getToken(LAParser.FALSO, 0); }
		public TerminalNode E_COMERCIAL() { return getToken(LAParser.E_COMERCIAL, 0); }
		public List<IdentContext> ident() {
			return getRuleContexts(IdentContext.class);
		}
		public IdentContext ident(int i) {
			return getRuleContext(IdentContext.class,i);
		}
		public TerminalNode CIRCUNFLEXO() { return getToken(LAParser.CIRCUNFLEXO, 0); }
		public Lista_expressoesContext lista_expressoes() {
			return getRuleContext(Lista_expressoesContext.class,0);
		}
		public List<TerminalNode> PONTO() { return getTokens(LAParser.PONTO); }
		public TerminalNode PONTO(int i) {
			return getToken(LAParser.PONTO, i);
		}
		public List<TerminalNode> ABRE_COL() { return getTokens(LAParser.ABRE_COL); }
		public TerminalNode ABRE_COL(int i) {
			return getToken(LAParser.ABRE_COL, i);
		}
		public List<TerminalNode> FECHA_COL() { return getTokens(LAParser.FECHA_COL); }
		public TerminalNode FECHA_COL(int i) {
			return getToken(LAParser.FECHA_COL, i);
		}
		public FatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fator; }
	}

	public final FatorContext fator() throws RecognitionException {
		FatorContext _localctx = new FatorContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_fator);
		int _la;
		try {
			int _alt;
			setState(469);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(437);
				match(ABRE_PAR);
				setState(438);
				expressao(0);
				setState(439);
				match(FECHA_PAR);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(441);
				match(NUM_INT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(442);
				match(NUM_REAL);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(443);
				match(CADEIA);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(444);
				match(VERDADEIRO);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(445);
				match(FALSO);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(446);
				match(E_COMERCIAL);
				setState(447);
				ident();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(448);
				match(CIRCUNFLEXO);
				setState(449);
				ident();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(450);
				ident();
				setState(451);
				match(ABRE_PAR);
				setState(453);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 4)) & ~0x3f) == 0 && ((1L << (_la - 4)) & -8968918503289257971L) != 0)) {
					{
					setState(452);
					lista_expressoes();
					}
				}

				setState(455);
				match(FECHA_PAR);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(457);
				ident();
				setState(466);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						setState(464);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case PONTO:
							{
							{
							setState(458);
							match(PONTO);
							setState(459);
							ident();
							}
							}
							break;
						case ABRE_COL:
							{
							{
							setState(460);
							match(ABRE_COL);
							setState(461);
							expressao(0);
							setState(462);
							match(FECHA_COL);
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(468);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,39,_ctx);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdentContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(LAParser.IDENT, 0); }
		public IdentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ident; }
	}

	public final IdentContext ident() throws RecognitionException {
		IdentContext _localctx = new IdentContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_ident);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(471);
			match(IDENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 36:
			return expressao_sempred((ExpressaoContext)_localctx, predIndex);
		case 37:
			return expr_e_sempred((Expr_eContext)_localctx, predIndex);
		case 38:
			return expr_rel_sempred((Expr_relContext)_localctx, predIndex);
		case 39:
			return expr_arit_sempred((Expr_aritContext)_localctx, predIndex);
		case 40:
			return termo_sempred((TermoContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expressao_sempred(ExpressaoContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_e_sempred(Expr_eContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_rel_sempred(Expr_relContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_arit_sempred(Expr_aritContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean termo_sempred(TermoContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001D\u01da\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001^\b\u0001\n\u0001\f\u0001"+
		"a\t\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		"g\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0004\u0004t\b\u0004\u000b\u0004\f\u0004u\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u0083\b\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0003\u0007\u008d\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0005\b\u0098\b"+
		"\b\n\b\f\b\u009b\t\b\u0001\t\u0003\t\u009e\b\t\u0001\t\u0001\t\u0001\t"+
		"\u0001\t\u0001\n\u0001\n\u0001\n\u0005\n\u00a7\b\n\n\n\f\n\u00aa\t\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u00b1"+
		"\b\u000b\n\u000b\f\u000b\u00b4\t\u000b\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0003\f\u00bd\b\f\u0001\r\u0005\r\u00c0\b\r\n"+
		"\r\f\r\u00c3\t\r\u0001\r\u0005\r\u00c6\b\r\n\r\f\r\u00c9\t\r\u0001\u000e"+
		"\u0001\u000e\u0004\u000e\u00cd\b\u000e\u000b\u000e\f\u000e\u00ce\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003"+
		"\u000e\u00dd\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0003\u000f\u00e4\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003"+
		"\u0011\u00ef\b\u0011\u0001\u0012\u0003\u0012\u00f2\b\u0012\u0001\u0012"+
		"\u0001\u0012\u0003\u0012\u00f6\b\u0012\u0001\u0013\u0001\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0003\u0015\u0107\b\u0015\u0003\u0015\u0109\b\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0003\u0017\u0110\b\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0005\u0017\u0119\b\u0017\n\u0017\f\u0017\u011c\t\u0017\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0005\u0019\u0126\b\u0019\n\u0019\f\u0019\u0129\t\u0019\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0005\u001b\u0133\b\u001b\n\u001b\f\u001b\u0136\t\u001b"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u013b\b\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0001\u001d\u0003\u001d\u0145\b\u001d\u0001\u001d\u0001\u001d\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0004\u001e\u014d\b\u001e\u000b\u001e"+
		"\f\u001e\u014e\u0001\u001e\u0001\u001e\u0003\u001e\u0153\b\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001"+
		" \u0001 \u0001 \u0003 \u015e\b \u0001 \u0003 \u0161\b \u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001\"\u0001\"\u0001"+
		"\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0001#\u0001$\u0001"+
		"$\u0001$\u0001$\u0001$\u0001$\u0005$\u017e\b$\n$\f$\u0181\t$\u0001%\u0001"+
		"%\u0001%\u0001%\u0001%\u0001%\u0005%\u0189\b%\n%\f%\u018c\t%\u0001&\u0001"+
		"&\u0001&\u0001&\u0001&\u0001&\u0005&\u0194\b&\n&\f&\u0197\t&\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0005\'\u019f\b\'\n\'\f\'\u01a2\t\'"+
		"\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0005(\u01aa\b(\n(\f(\u01ad"+
		"\t(\u0001)\u0001)\u0001)\u0001)\u0001)\u0003)\u01b4\b)\u0001*\u0001*\u0001"+
		"*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001"+
		"*\u0001*\u0001*\u0001*\u0003*\u01c6\b*\u0001*\u0001*\u0001*\u0001*\u0001"+
		"*\u0001*\u0001*\u0001*\u0001*\u0005*\u01d1\b*\n*\f*\u01d4\t*\u0003*\u01d6"+
		"\b*\u0001+\u0001+\u0001+\u0000\u0005HJLNP,\u0000\u0002\u0004\u0006\b\n"+
		"\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0246"+
		"8:<>@BDFHJLNPRTV\u0000\u0005\u0002\u0000\u0018\u001a$$\u0004\u0000\u0004"+
		"\u0004\u0006\u0007\u0016\u0016))\u0002\u0000-/35\u0001\u000067\u0001\u0000"+
		"8:\u01ea\u0000X\u0001\u0000\u0000\u0000\u0002_\u0001\u0000\u0000\u0000"+
		"\u0004f\u0001\u0000\u0000\u0000\u0006h\u0001\u0000\u0000\u0000\bs\u0001"+
		"\u0000\u0000\u0000\nw\u0001\u0000\u0000\u0000\f~\u0001\u0000\u0000\u0000"+
		"\u000e\u0088\u0001\u0000\u0000\u0000\u0010\u0094\u0001\u0000\u0000\u0000"+
		"\u0012\u009d\u0001\u0000\u0000\u0000\u0014\u00a3\u0001\u0000\u0000\u0000"+
		"\u0016\u00ab\u0001\u0000\u0000\u0000\u0018\u00bc\u0001\u0000\u0000\u0000"+
		"\u001a\u00c1\u0001\u0000\u0000\u0000\u001c\u00dc\u0001\u0000\u0000\u0000"+
		"\u001e\u00e3\u0001\u0000\u0000\u0000 \u00e5\u0001\u0000\u0000\u0000\""+
		"\u00ee\u0001\u0000\u0000\u0000$\u00f1\u0001\u0000\u0000\u0000&\u00f7\u0001"+
		"\u0000\u0000\u0000(\u00f9\u0001\u0000\u0000\u0000*\u0108\u0001\u0000\u0000"+
		"\u0000,\u010a\u0001\u0000\u0000\u0000.\u010f\u0001\u0000\u0000\u00000"+
		"\u011d\u0001\u0000\u0000\u00002\u0122\u0001\u0000\u0000\u00004\u012a\u0001"+
		"\u0000\u0000\u00006\u012f\u0001\u0000\u0000\u00008\u0137\u0001\u0000\u0000"+
		"\u0000:\u013e\u0001\u0000\u0000\u0000<\u0148\u0001\u0000\u0000\u0000>"+
		"\u0156\u0001\u0000\u0000\u0000@\u0160\u0001\u0000\u0000\u0000B\u0162\u0001"+
		"\u0000\u0000\u0000D\u016c\u0001\u0000\u0000\u0000F\u0172\u0001\u0000\u0000"+
		"\u0000H\u0177\u0001\u0000\u0000\u0000J\u0182\u0001\u0000\u0000\u0000L"+
		"\u018d\u0001\u0000\u0000\u0000N\u0198\u0001\u0000\u0000\u0000P\u01a3\u0001"+
		"\u0000\u0000\u0000R\u01b3\u0001\u0000\u0000\u0000T\u01d5\u0001\u0000\u0000"+
		"\u0000V\u01d7\u0001\u0000\u0000\u0000XY\u0003\u0002\u0001\u0000YZ\u0003"+
		"\u0018\f\u0000Z[\u0005\u0000\u0000\u0001[\u0001\u0001\u0000\u0000\u0000"+
		"\\^\u0003\u0004\u0002\u0000]\\\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000"+
		"\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`\u0003\u0001"+
		"\u0000\u0000\u0000a_\u0001\u0000\u0000\u0000bg\u0003\u0006\u0003\u0000"+
		"cg\u0003\n\u0005\u0000dg\u0003\f\u0006\u0000eg\u0003\u000e\u0007\u0000"+
		"fb\u0001\u0000\u0000\u0000fc\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000"+
		"\u0000fe\u0001\u0000\u0000\u0000g\u0005\u0001\u0000\u0000\u0000hi\u0005"+
		"#\u0000\u0000ij\u0003V+\u0000jk\u00050\u0000\u0000kl\u0005\u0012\u0000"+
		"\u0000lm\u0003\b\u0004\u0000mn\u0005\u000b\u0000\u0000n\u0007\u0001\u0000"+
		"\u0000\u0000op\u0003\u0014\n\u0000pq\u00050\u0000\u0000qr\u0003\"\u0011"+
		"\u0000rt\u0001\u0000\u0000\u0000so\u0001\u0000\u0000\u0000tu\u0001\u0000"+
		"\u0000\u0000us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000v\t\u0001"+
		"\u0000\u0000\u0000wx\u0005\u0011\u0000\u0000xy\u0003V+\u0000yz\u00050"+
		"\u0000\u0000z{\u0003&\u0013\u0000{|\u00055\u0000\u0000|}\u0003(\u0014"+
		"\u0000}\u000b\u0001\u0000\u0000\u0000~\u007f\u0005\u0010\u0000\u0000\u007f"+
		"\u0080\u0003V+\u0000\u0080\u0082\u0005=\u0000\u0000\u0081\u0083\u0003"+
		"\u0010\b\u0000\u0082\u0081\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000"+
		"\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0085\u0005>\u0000"+
		"\u0000\u0085\u0086\u0003\u001a\r\u0000\u0086\u0087\u0005\t\u0000\u0000"+
		"\u0087\r\u0001\u0000\u0000\u0000\u0088\u0089\u0005\u001c\u0000\u0000\u0089"+
		"\u008a\u0003V+\u0000\u008a\u008c\u0005=\u0000\u0000\u008b\u008d\u0003"+
		"\u0010\b\u0000\u008c\u008b\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000"+
		"\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008f\u0005>\u0000"+
		"\u0000\u008f\u0090\u00050\u0000\u0000\u0090\u0091\u0003\"\u0011\u0000"+
		"\u0091\u0092\u0003\u001a\r\u0000\u0092\u0093\u0005\n\u0000\u0000\u0093"+
		"\u000f\u0001\u0000\u0000\u0000\u0094\u0099\u0003\u0012\t\u0000\u0095\u0096"+
		"\u00052\u0000\u0000\u0096\u0098\u0003\u0012\t\u0000\u0097\u0095\u0001"+
		"\u0000\u0000\u0000\u0098\u009b\u0001\u0000\u0000\u0000\u0099\u0097\u0001"+
		"\u0000\u0000\u0000\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u0011\u0001"+
		"\u0000\u0000\u0000\u009b\u0099\u0001\u0000\u0000\u0000\u009c\u009e\u0005"+
		"\'\u0000\u0000\u009d\u009c\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000"+
		"\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0003\u0014"+
		"\n\u0000\u00a0\u00a1\u00050\u0000\u0000\u00a1\u00a2\u0003\"\u0011\u0000"+
		"\u00a2\u0013\u0001\u0000\u0000\u0000\u00a3\u00a8\u0003\u0016\u000b\u0000"+
		"\u00a4\u00a5\u00052\u0000\u0000\u00a5\u00a7\u0003\u0016\u000b\u0000\u00a6"+
		"\u00a4\u0001\u0000\u0000\u0000\u00a7\u00aa\u0001\u0000\u0000\u0000\u00a8"+
		"\u00a6\u0001\u0000\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9"+
		"\u0015\u0001\u0000\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00ab"+
		"\u00b2\u0003V+\u0000\u00ac\u00ad\u0005?\u0000\u0000\u00ad\u00ae\u0003"+
		"H$\u0000\u00ae\u00af\u0005@\u0000\u0000\u00af\u00b1\u0001\u0000\u0000"+
		"\u0000\u00b0\u00ac\u0001\u0000\u0000\u0000\u00b1\u00b4\u0001\u0000\u0000"+
		"\u0000\u00b2\u00b0\u0001\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000\u0000"+
		"\u0000\u00b3\u0017\u0001\u0000\u0000\u0000\u00b4\u00b2\u0001\u0000\u0000"+
		"\u0000\u00b5\u00b6\u0005\u001b\u0000\u0000\u00b6\u00b7\u0003\u001a\r\u0000"+
		"\u00b7\u00b8\u0005\b\u0000\u0000\u00b8\u00bd\u0001\u0000\u0000\u0000\u00b9"+
		"\u00ba\u0003\u001a\r\u0000\u00ba\u00bb\u0005\b\u0000\u0000\u00bb\u00bd"+
		"\u0001\u0000\u0000\u0000\u00bc\u00b5\u0001\u0000\u0000\u0000\u00bc\u00b9"+
		"\u0001\u0000\u0000\u0000\u00bd\u0019\u0001\u0000\u0000\u0000\u00be\u00c0"+
		"\u0003\u001c\u000e\u0000\u00bf\u00be\u0001\u0000\u0000\u0000\u00c0\u00c3"+
		"\u0001\u0000\u0000\u0000\u00c1\u00bf\u0001\u0000\u0000\u0000\u00c1\u00c2"+
		"\u0001\u0000\u0000\u0000\u00c2\u00c7\u0001\u0000\u0000\u0000\u00c3\u00c1"+
		"\u0001\u0000\u0000\u0000\u00c4\u00c6\u0003*\u0015\u0000\u00c5\u00c4\u0001"+
		"\u0000\u0000\u0000\u00c6\u00c9\u0001\u0000\u0000\u0000\u00c7\u00c5\u0001"+
		"\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000\u00c8\u001b\u0001"+
		"\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00ca\u00cc\u0005"+
		"\u0014\u0000\u0000\u00cb\u00cd\u0003 \u0010\u0000\u00cc\u00cb\u0001\u0000"+
		"\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\u00cc\u0001\u0000"+
		"\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000\u00cf\u00dd\u0001\u0000"+
		"\u0000\u0000\u00d0\u00d1\u0005\u0011\u0000\u0000\u00d1\u00d2\u0003V+\u0000"+
		"\u00d2\u00d3\u00050\u0000\u0000\u00d3\u00d4\u0003&\u0013\u0000\u00d4\u00d5"+
		"\u00055\u0000\u0000\u00d5\u00d6\u0003(\u0014\u0000\u00d6\u00dd\u0001\u0000"+
		"\u0000\u0000\u00d7\u00d8\u0005#\u0000\u0000\u00d8\u00d9\u0003V+\u0000"+
		"\u00d9\u00da\u00050\u0000\u0000\u00da\u00db\u0003\u001e\u000f\u0000\u00db"+
		"\u00dd\u0001\u0000\u0000\u0000\u00dc\u00ca\u0001\u0000\u0000\u0000\u00dc"+
		"\u00d0\u0001\u0000\u0000\u0000\u00dc\u00d7\u0001\u0000\u0000\u0000\u00dd"+
		"\u001d\u0001\u0000\u0000\u0000\u00de\u00df\u0005\u0012\u0000\u0000\u00df"+
		"\u00e0\u0003\b\u0004\u0000\u00e0\u00e1\u0005\u000b\u0000\u0000\u00e1\u00e4"+
		"\u0001\u0000\u0000\u0000\u00e2\u00e4\u0003$\u0012\u0000\u00e3\u00de\u0001"+
		"\u0000\u0000\u0000\u00e3\u00e2\u0001\u0000\u0000\u0000\u00e4\u001f\u0001"+
		"\u0000\u0000\u0000\u00e5\u00e6\u0003\u0014\n\u0000\u00e6\u00e7\u00050"+
		"\u0000\u0000\u00e7\u00e8\u0003\"\u0011\u0000\u00e8!\u0001\u0000\u0000"+
		"\u0000\u00e9\u00ea\u0005\u0012\u0000\u0000\u00ea\u00eb\u0003\b\u0004\u0000"+
		"\u00eb\u00ec\u0005\u000b\u0000\u0000\u00ec\u00ef\u0001\u0000\u0000\u0000"+
		"\u00ed\u00ef\u0003$\u0012\u0000\u00ee\u00e9\u0001\u0000\u0000\u0000\u00ee"+
		"\u00ed\u0001\u0000\u0000\u0000\u00ef#\u0001\u0000\u0000\u0000\u00f0\u00f2"+
		"\u0005;\u0000\u0000\u00f1\u00f0\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001"+
		"\u0000\u0000\u0000\u00f2\u00f5\u0001\u0000\u0000\u0000\u00f3\u00f6\u0003"+
		"&\u0013\u0000\u00f4\u00f6\u0003V+\u0000\u00f5\u00f3\u0001\u0000\u0000"+
		"\u0000\u00f5\u00f4\u0001\u0000\u0000\u0000\u00f6%\u0001\u0000\u0000\u0000"+
		"\u00f7\u00f8\u0007\u0000\u0000\u0000\u00f8\'\u0001\u0000\u0000\u0000\u00f9"+
		"\u00fa\u0007\u0001\u0000\u0000\u00fa)\u0001\u0000\u0000\u0000\u00fb\u0109"+
		"\u0003,\u0016\u0000\u00fc\u0109\u00030\u0018\u0000\u00fd\u0109\u00034"+
		"\u001a\u0000\u00fe\u0109\u0003:\u001d\u0000\u00ff\u0109\u0003<\u001e\u0000"+
		"\u0100\u0109\u0003B!\u0000\u0101\u0109\u0003D\"\u0000\u0102\u0109\u0003"+
		"F#\u0000\u0103\u0109\u00038\u001c\u0000\u0104\u0106\u0005\u0017\u0000"+
		"\u0000\u0105\u0107\u0003H$\u0000\u0106\u0105\u0001\u0000\u0000\u0000\u0106"+
		"\u0107\u0001\u0000\u0000\u0000\u0107\u0109\u0001\u0000\u0000\u0000\u0108"+
		"\u00fb\u0001\u0000\u0000\u0000\u0108\u00fc\u0001\u0000\u0000\u0000\u0108"+
		"\u00fd\u0001\u0000\u0000\u0000\u0108\u00fe\u0001\u0000\u0000\u0000\u0108"+
		"\u00ff\u0001\u0000\u0000\u0000\u0108\u0100\u0001\u0000\u0000\u0000\u0108"+
		"\u0101\u0001\u0000\u0000\u0000\u0108\u0102\u0001\u0000\u0000\u0000\u0108"+
		"\u0103\u0001\u0000\u0000\u0000\u0108\u0104\u0001\u0000\u0000\u0000\u0109"+
		"+\u0001\u0000\u0000\u0000\u010a\u010b\u0003.\u0017\u0000\u010b\u010c\u0005"+
		",\u0000\u0000\u010c\u010d\u0003H$\u0000\u010d-\u0001\u0000\u0000\u0000"+
		"\u010e\u0110\u0005;\u0000\u0000\u010f\u010e\u0001\u0000\u0000\u0000\u010f"+
		"\u0110\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111"+
		"\u011a\u0003V+\u0000\u0112\u0113\u00051\u0000\u0000\u0113\u0119\u0003"+
		"V+\u0000\u0114\u0115\u0005?\u0000\u0000\u0115\u0116\u0003H$\u0000\u0116"+
		"\u0117\u0005@\u0000\u0000\u0117\u0119\u0001\u0000\u0000\u0000\u0118\u0112"+
		"\u0001\u0000\u0000\u0000\u0118\u0114\u0001\u0000\u0000\u0000\u0119\u011c"+
		"\u0001\u0000\u0000\u0000\u011a\u0118\u0001\u0000\u0000\u0000\u011a\u011b"+
		"\u0001\u0000\u0000\u0000\u011b/\u0001\u0000\u0000\u0000\u011c\u011a\u0001"+
		"\u0000\u0000\u0000\u011d\u011e\u0005%\u0000\u0000\u011e\u011f\u0005=\u0000"+
		"\u0000\u011f\u0120\u00032\u0019\u0000\u0120\u0121\u0005>\u0000\u0000\u0121"+
		"1\u0001\u0000\u0000\u0000\u0122\u0127\u0003.\u0017\u0000\u0123\u0124\u0005"+
		"2\u0000\u0000\u0124\u0126\u0003.\u0017\u0000\u0125\u0123\u0001\u0000\u0000"+
		"\u0000\u0126\u0129\u0001\u0000\u0000\u0000\u0127\u0125\u0001\u0000\u0000"+
		"\u0000\u0127\u0128\u0001\u0000\u0000\u0000\u01283\u0001\u0000\u0000\u0000"+
		"\u0129\u0127\u0001\u0000\u0000\u0000\u012a\u012b\u0005\u0015\u0000\u0000"+
		"\u012b\u012c\u0005=\u0000\u0000\u012c\u012d\u00036\u001b\u0000\u012d\u012e"+
		"\u0005>\u0000\u0000\u012e5\u0001\u0000\u0000\u0000\u012f\u0134\u0003H"+
		"$\u0000\u0130\u0131\u00052\u0000\u0000\u0131\u0133\u0003H$\u0000\u0132"+
		"\u0130\u0001\u0000\u0000\u0000\u0133\u0136\u0001\u0000\u0000\u0000\u0134"+
		"\u0132\u0001\u0000\u0000\u0000\u0134\u0135\u0001\u0000\u0000\u0000\u0135"+
		"7\u0001\u0000\u0000\u0000\u0136\u0134\u0001\u0000\u0000\u0000\u0137\u0138"+
		"\u0003V+\u0000\u0138\u013a\u0005=\u0000\u0000\u0139\u013b\u00036\u001b"+
		"\u0000\u013a\u0139\u0001\u0000\u0000\u0000\u013a\u013b\u0001\u0000\u0000"+
		"\u0000\u013b\u013c\u0001\u0000\u0000\u0000\u013c\u013d\u0005>\u0000\u0000"+
		"\u013d9\u0001\u0000\u0000\u0000\u013e\u013f\u0005*\u0000\u0000\u013f\u0140"+
		"\u0003H$\u0000\u0140\u0141\u0005\u001e\u0000\u0000\u0141\u0144\u0003\u001a"+
		"\r\u0000\u0142\u0143\u0005\u001d\u0000\u0000\u0143\u0145\u0003\u001a\r"+
		"\u0000\u0144\u0142\u0001\u0000\u0000\u0000\u0144\u0145\u0001\u0000\u0000"+
		"\u0000\u0145\u0146\u0001\u0000\u0000\u0000\u0146\u0147\u0005\u000f\u0000"+
		"\u0000\u0147;\u0001\u0000\u0000\u0000\u0148\u0149\u0005 \u0000\u0000\u0149"+
		"\u014a\u0003H$\u0000\u014a\u014c\u0005\u001f\u0000\u0000\u014b\u014d\u0003"+
		">\u001f\u0000\u014c\u014b\u0001\u0000\u0000\u0000\u014d\u014e\u0001\u0000"+
		"\u0000\u0000\u014e\u014c\u0001\u0000\u0000\u0000\u014e\u014f\u0001\u0000"+
		"\u0000\u0000\u014f\u0152\u0001\u0000\u0000\u0000\u0150\u0151\u0005\u001d"+
		"\u0000\u0000\u0151\u0153\u0003\u001a\r\u0000\u0152\u0150\u0001\u0000\u0000"+
		"\u0000\u0152\u0153\u0001\u0000\u0000\u0000\u0153\u0154\u0001\u0000\u0000"+
		"\u0000\u0154\u0155\u0005\u000e\u0000\u0000\u0155=\u0001\u0000\u0000\u0000"+
		"\u0156\u0157\u0003@ \u0000\u0157\u0158\u00050\u0000\u0000\u0158\u0159"+
		"\u0003\u001a\r\u0000\u0159?\u0001\u0000\u0000\u0000\u015a\u015d\u0005"+
		"\u0007\u0000\u0000\u015b\u015c\u0005+\u0000\u0000\u015c\u015e\u0005\u0007"+
		"\u0000\u0000\u015d\u015b\u0001\u0000\u0000\u0000\u015d\u015e\u0001\u0000"+
		"\u0000\u0000\u015e\u0161\u0001\u0000\u0000\u0000\u015f\u0161\u0005\u0004"+
		"\u0000\u0000\u0160\u015a\u0001\u0000\u0000\u0000\u0160\u015f\u0001\u0000"+
		"\u0000\u0000\u0161A\u0001\u0000\u0000\u0000\u0162\u0163\u0005!\u0000\u0000"+
		"\u0163\u0164\u0003V+\u0000\u0164\u0165\u0005,\u0000\u0000\u0165\u0166"+
		"\u0003H$\u0000\u0166\u0167\u0005(\u0000\u0000\u0167\u0168\u0003H$\u0000"+
		"\u0168\u0169\u0005\"\u0000\u0000\u0169\u016a\u0003\u001a\r\u0000\u016a"+
		"\u016b\u0005\r\u0000\u0000\u016bC\u0001\u0000\u0000\u0000\u016c\u016d"+
		"\u0005\u0013\u0000\u0000\u016d\u016e\u0003H$\u0000\u016e\u016f\u0005\""+
		"\u0000\u0000\u016f\u0170\u0003\u001a\r\u0000\u0170\u0171\u0005\f\u0000"+
		"\u0000\u0171E\u0001\u0000\u0000\u0000\u0172\u0173\u0005\"\u0000\u0000"+
		"\u0173\u0174\u0003\u001a\r\u0000\u0174\u0175\u0005(\u0000\u0000\u0175"+
		"\u0176\u0003H$\u0000\u0176G\u0001\u0000\u0000\u0000\u0177\u0178\u0006"+
		"$\uffff\uffff\u0000\u0178\u0179\u0003J%\u0000\u0179\u017f\u0001\u0000"+
		"\u0000\u0000\u017a\u017b\n\u0002\u0000\u0000\u017b\u017c\u0005B\u0000"+
		"\u0000\u017c\u017e\u0003J%\u0000\u017d\u017a\u0001\u0000\u0000\u0000\u017e"+
		"\u0181\u0001\u0000\u0000\u0000\u017f\u017d\u0001\u0000\u0000\u0000\u017f"+
		"\u0180\u0001\u0000\u0000\u0000\u0180I\u0001\u0000\u0000\u0000\u0181\u017f"+
		"\u0001\u0000\u0000\u0000\u0182\u0183\u0006%\uffff\uffff\u0000\u0183\u0184"+
		"\u0003L&\u0000\u0184\u018a\u0001\u0000\u0000\u0000\u0185\u0186\n\u0002"+
		"\u0000\u0000\u0186\u0187\u0005A\u0000\u0000\u0187\u0189\u0003L&\u0000"+
		"\u0188\u0185\u0001\u0000\u0000\u0000\u0189\u018c\u0001\u0000\u0000\u0000"+
		"\u018a\u0188\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000"+
		"\u018bK\u0001\u0000\u0000\u0000\u018c\u018a\u0001\u0000\u0000\u0000\u018d"+
		"\u018e\u0006&\uffff\uffff\u0000\u018e\u018f\u0003N\'\u0000\u018f\u0195"+
		"\u0001\u0000\u0000\u0000\u0190\u0191\n\u0002\u0000\u0000\u0191\u0192\u0007"+
		"\u0002\u0000\u0000\u0192\u0194\u0003N\'\u0000\u0193\u0190\u0001\u0000"+
		"\u0000\u0000\u0194\u0197\u0001\u0000\u0000\u0000\u0195\u0193\u0001\u0000"+
		"\u0000\u0000\u0195\u0196\u0001\u0000\u0000\u0000\u0196M\u0001\u0000\u0000"+
		"\u0000\u0197\u0195\u0001\u0000\u0000\u0000\u0198\u0199\u0006\'\uffff\uffff"+
		"\u0000\u0199\u019a\u0003P(\u0000\u019a\u01a0\u0001\u0000\u0000\u0000\u019b"+
		"\u019c\n\u0002\u0000\u0000\u019c\u019d\u0007\u0003\u0000\u0000\u019d\u019f"+
		"\u0003P(\u0000\u019e\u019b\u0001\u0000\u0000\u0000\u019f\u01a2\u0001\u0000"+
		"\u0000\u0000\u01a0\u019e\u0001\u0000\u0000\u0000\u01a0\u01a1\u0001\u0000"+
		"\u0000\u0000\u01a1O\u0001\u0000\u0000\u0000\u01a2\u01a0\u0001\u0000\u0000"+
		"\u0000\u01a3\u01a4\u0006(\uffff\uffff\u0000\u01a4\u01a5\u0003R)\u0000"+
		"\u01a5\u01ab\u0001\u0000\u0000\u0000\u01a6\u01a7\n\u0002\u0000\u0000\u01a7"+
		"\u01a8\u0007\u0004\u0000\u0000\u01a8\u01aa\u0003R)\u0000\u01a9\u01a6\u0001"+
		"\u0000\u0000\u0000\u01aa\u01ad\u0001\u0000\u0000\u0000\u01ab\u01a9\u0001"+
		"\u0000\u0000\u0000\u01ab\u01ac\u0001\u0000\u0000\u0000\u01acQ\u0001\u0000"+
		"\u0000\u0000\u01ad\u01ab\u0001\u0000\u0000\u0000\u01ae\u01af\u00057\u0000"+
		"\u0000\u01af\u01b4\u0003R)\u0000\u01b0\u01b1\u0005&\u0000\u0000\u01b1"+
		"\u01b4\u0003R)\u0000\u01b2\u01b4\u0003T*\u0000\u01b3\u01ae\u0001\u0000"+
		"\u0000\u0000\u01b3\u01b0\u0001\u0000\u0000\u0000\u01b3\u01b2\u0001\u0000"+
		"\u0000\u0000\u01b4S\u0001\u0000\u0000\u0000\u01b5\u01b6\u0005=\u0000\u0000"+
		"\u01b6\u01b7\u0003H$\u0000\u01b7\u01b8\u0005>\u0000\u0000\u01b8\u01d6"+
		"\u0001\u0000\u0000\u0000\u01b9\u01d6\u0005\u0007\u0000\u0000\u01ba\u01d6"+
		"\u0005\u0006\u0000\u0000\u01bb\u01d6\u0005\u0004\u0000\u0000\u01bc\u01d6"+
		"\u0005\u0016\u0000\u0000\u01bd\u01d6\u0005)\u0000\u0000\u01be\u01bf\u0005"+
		"<\u0000\u0000\u01bf\u01d6\u0003V+\u0000\u01c0\u01c1\u0005;\u0000\u0000"+
		"\u01c1\u01d6\u0003V+\u0000\u01c2\u01c3\u0003V+\u0000\u01c3\u01c5\u0005"+
		"=\u0000\u0000\u01c4\u01c6\u00036\u001b\u0000\u01c5\u01c4\u0001\u0000\u0000"+
		"\u0000\u01c5\u01c6\u0001\u0000\u0000\u0000\u01c6\u01c7\u0001\u0000\u0000"+
		"\u0000\u01c7\u01c8\u0005>\u0000\u0000\u01c8\u01d6\u0001\u0000\u0000\u0000"+
		"\u01c9\u01d2\u0003V+\u0000\u01ca\u01cb\u00051\u0000\u0000\u01cb\u01d1"+
		"\u0003V+\u0000\u01cc\u01cd\u0005?\u0000\u0000\u01cd\u01ce\u0003H$\u0000"+
		"\u01ce\u01cf\u0005@\u0000\u0000\u01cf\u01d1\u0001\u0000\u0000\u0000\u01d0"+
		"\u01ca\u0001\u0000\u0000\u0000\u01d0\u01cc\u0001\u0000\u0000\u0000\u01d1"+
		"\u01d4\u0001\u0000\u0000\u0000\u01d2\u01d0\u0001\u0000\u0000\u0000\u01d2"+
		"\u01d3\u0001\u0000\u0000\u0000\u01d3\u01d6\u0001\u0000\u0000\u0000\u01d4"+
		"\u01d2\u0001\u0000\u0000\u0000\u01d5\u01b5\u0001\u0000\u0000\u0000\u01d5"+
		"\u01b9\u0001\u0000\u0000\u0000\u01d5\u01ba\u0001\u0000\u0000\u0000\u01d5"+
		"\u01bb\u0001\u0000\u0000\u0000\u01d5\u01bc\u0001\u0000\u0000\u0000\u01d5"+
		"\u01bd\u0001\u0000\u0000\u0000\u01d5\u01be\u0001\u0000\u0000\u0000\u01d5"+
		"\u01c0\u0001\u0000\u0000\u0000\u01d5\u01c2\u0001\u0000\u0000\u0000\u01d5"+
		"\u01c9\u0001\u0000\u0000\u0000\u01d6U\u0001\u0000\u0000\u0000\u01d7\u01d8"+
		"\u0005C\u0000\u0000\u01d8W\u0001\u0000\u0000\u0000)_fu\u0082\u008c\u0099"+
		"\u009d\u00a8\u00b2\u00bc\u00c1\u00c7\u00ce\u00dc\u00e3\u00ee\u00f1\u00f5"+
		"\u0106\u0108\u010f\u0118\u011a\u0127\u0134\u013a\u0144\u014e\u0152\u015d"+
		"\u0160\u017f\u018a\u0195\u01a0\u01ab\u01b3\u01c5\u01d0\u01d2\u01d5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}