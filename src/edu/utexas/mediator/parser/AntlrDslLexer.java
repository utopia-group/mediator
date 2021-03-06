// Generated from AntlrDsl.g4 by ANTLR 4.6

package edu.utexas.mediator.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AntlrDslLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, MEM=6, LNOT=7, LAND=8, LOR=9, 
		JOIN=10, SEL=11, PROJ=12, CUP=13, DIFF=14, INS=15, DEL=16, UPD=17, LOP=18, 
		ID=19, WS=20;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "MEM", "LNOT", "LAND", "LOR", 
		"JOIN", "SEL", "PROJ", "CUP", "DIFF", "INS", "DEL", "UPD", "LOP", "ID", 
		"WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "','", "')'", "'['", "']'", "'in'", "'not'", "'and'", "'or'", 
		"'join'", "'sigma'", "'pi'", "'cup'", "'minus'", "'ins'", "'del'", "'upd'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "MEM", "LNOT", "LAND", "LOR", "JOIN", 
		"SEL", "PROJ", "CUP", "DIFF", "INS", "DEL", "UPD", "LOP", "ID", "WS"
	};
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


	public AntlrDslLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "AntlrDsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\26\u0080\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3"+
		"\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\5\23q\n\23\3\24\3\24\7\24u\n\24\f\24\16\24x\13\24\3\25\6\25{\n\25\r\25"+
		"\16\25|\3\25\3\25\2\2\26\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
		"\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26\3\2\5\5\2C\\aac|\7"+
		"\2/\60\62;C\\aac|\5\2\13\f\17\17\"\"\u0086\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\3+\3\2\2\2\5-\3\2\2\2\7/\3\2\2\2\t\61\3\2\2\2\13\63\3\2\2"+
		"\2\r\65\3\2\2\2\178\3\2\2\2\21<\3\2\2\2\23@\3\2\2\2\25C\3\2\2\2\27H\3"+
		"\2\2\2\31N\3\2\2\2\33Q\3\2\2\2\35U\3\2\2\2\37[\3\2\2\2!_\3\2\2\2#c\3\2"+
		"\2\2%p\3\2\2\2\'r\3\2\2\2)z\3\2\2\2+,\7*\2\2,\4\3\2\2\2-.\7.\2\2.\6\3"+
		"\2\2\2/\60\7+\2\2\60\b\3\2\2\2\61\62\7]\2\2\62\n\3\2\2\2\63\64\7_\2\2"+
		"\64\f\3\2\2\2\65\66\7k\2\2\66\67\7p\2\2\67\16\3\2\2\289\7p\2\29:\7q\2"+
		"\2:;\7v\2\2;\20\3\2\2\2<=\7c\2\2=>\7p\2\2>?\7f\2\2?\22\3\2\2\2@A\7q\2"+
		"\2AB\7t\2\2B\24\3\2\2\2CD\7l\2\2DE\7q\2\2EF\7k\2\2FG\7p\2\2G\26\3\2\2"+
		"\2HI\7u\2\2IJ\7k\2\2JK\7i\2\2KL\7o\2\2LM\7c\2\2M\30\3\2\2\2NO\7r\2\2O"+
		"P\7k\2\2P\32\3\2\2\2QR\7e\2\2RS\7w\2\2ST\7r\2\2T\34\3\2\2\2UV\7o\2\2V"+
		"W\7k\2\2WX\7p\2\2XY\7w\2\2YZ\7u\2\2Z\36\3\2\2\2[\\\7k\2\2\\]\7p\2\2]^"+
		"\7u\2\2^ \3\2\2\2_`\7f\2\2`a\7g\2\2ab\7n\2\2b\"\3\2\2\2cd\7w\2\2de\7r"+
		"\2\2ef\7f\2\2f$\3\2\2\2gq\7?\2\2hi\7#\2\2iq\7?\2\2jq\7>\2\2kl\7>\2\2l"+
		"q\7?\2\2mq\7@\2\2no\7@\2\2oq\7?\2\2pg\3\2\2\2ph\3\2\2\2pj\3\2\2\2pk\3"+
		"\2\2\2pm\3\2\2\2pn\3\2\2\2q&\3\2\2\2rv\t\2\2\2su\t\3\2\2ts\3\2\2\2ux\3"+
		"\2\2\2vt\3\2\2\2vw\3\2\2\2w(\3\2\2\2xv\3\2\2\2y{\t\4\2\2zy\3\2\2\2{|\3"+
		"\2\2\2|z\3\2\2\2|}\3\2\2\2}~\3\2\2\2~\177\b\25\2\2\177*\3\2\2\2\6\2pv"+
		"|\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}