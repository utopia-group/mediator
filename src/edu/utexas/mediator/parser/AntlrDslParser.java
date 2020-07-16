// Generated from AntlrDsl.g4 by ANTLR 4.6

package edu.utexas.mediator.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AntlrDslParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, MEM=6, LNOT=7, LAND=8, LOR=9, 
		JOIN=10, SEL=11, PROJ=12, CUP=13, DIFF=14, INS=15, DEL=16, UPD=17, LOP=18, 
		ID=19, WS=20;
	public static final int
		RULE_stmtRoot = 0, RULE_stmt = 1, RULE_queryExpr = 2, RULE_insStmt = 3, 
		RULE_delStmt = 4, RULE_updStmt = 5, RULE_attrList = 6, RULE_tuple = 7, 
		RULE_pred = 8;
	public static final String[] ruleNames = {
		"stmtRoot", "stmt", "queryExpr", "insStmt", "delStmt", "updStmt", "attrList", 
		"tuple", "pred"
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

	@Override
	public String getGrammarFileName() { return "AntlrDsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public AntlrDslParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class StmtRootContext extends ParserRuleContext {
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public TerminalNode EOF() { return getToken(AntlrDslParser.EOF, 0); }
		public StmtRootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmtRoot; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitStmtRoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtRootContext stmtRoot() throws RecognitionException {
		StmtRootContext _localctx = new StmtRootContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_stmtRoot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			stmt();
			setState(19);
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

	public static class StmtContext extends ParserRuleContext {
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
	 
		public StmtContext() { }
		public void copyFrom(StmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ToInsStmtContext extends StmtContext {
		public InsStmtContext insStmt() {
			return getRuleContext(InsStmtContext.class,0);
		}
		public ToInsStmtContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToInsStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ToQueryExprContext extends StmtContext {
		public QueryExprContext queryExpr() {
			return getRuleContext(QueryExprContext.class,0);
		}
		public ToQueryExprContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToQueryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ToDelStmtContext extends StmtContext {
		public DelStmtContext delStmt() {
			return getRuleContext(DelStmtContext.class,0);
		}
		public ToDelStmtContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToDelStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ToUpdStmtContext extends StmtContext {
		public UpdStmtContext updStmt() {
			return getRuleContext(UpdStmtContext.class,0);
		}
		public ToUpdStmtContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToUpdStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_stmt);
		try {
			setState(25);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JOIN:
			case SEL:
			case PROJ:
			case CUP:
			case DIFF:
			case ID:
				_localctx = new ToQueryExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(21);
				queryExpr();
				}
				break;
			case INS:
				_localctx = new ToInsStmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(22);
				insStmt();
				}
				break;
			case DEL:
				_localctx = new ToDelStmtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(23);
				delStmt();
				}
				break;
			case UPD:
				_localctx = new ToUpdStmtContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(24);
				updStmt();
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

	public static class QueryExprContext extends ParserRuleContext {
		public QueryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryExpr; }
	 
		public QueryExprContext() { }
		public void copyFrom(QueryExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ToIdContext extends QueryExprContext {
		public TerminalNode ID() { return getToken(AntlrDslParser.ID, 0); }
		public ToIdContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitToId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnionExprContext extends QueryExprContext {
		public TerminalNode CUP() { return getToken(AntlrDslParser.CUP, 0); }
		public List<QueryExprContext> queryExpr() {
			return getRuleContexts(QueryExprContext.class);
		}
		public QueryExprContext queryExpr(int i) {
			return getRuleContext(QueryExprContext.class,i);
		}
		public UnionExprContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitUnionExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ProjExprContext extends QueryExprContext {
		public TerminalNode PROJ() { return getToken(AntlrDslParser.PROJ, 0); }
		public AttrListContext attrList() {
			return getRuleContext(AttrListContext.class,0);
		}
		public QueryExprContext queryExpr() {
			return getRuleContext(QueryExprContext.class,0);
		}
		public ProjExprContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitProjExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class JoinExprContext extends QueryExprContext {
		public TerminalNode JOIN() { return getToken(AntlrDslParser.JOIN, 0); }
		public List<QueryExprContext> queryExpr() {
			return getRuleContexts(QueryExprContext.class);
		}
		public QueryExprContext queryExpr(int i) {
			return getRuleContext(QueryExprContext.class,i);
		}
		public JoinExprContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitJoinExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SelExprContext extends QueryExprContext {
		public TerminalNode SEL() { return getToken(AntlrDslParser.SEL, 0); }
		public PredContext pred() {
			return getRuleContext(PredContext.class,0);
		}
		public QueryExprContext queryExpr() {
			return getRuleContext(QueryExprContext.class,0);
		}
		public SelExprContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitSelExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MinusExprContext extends QueryExprContext {
		public TerminalNode DIFF() { return getToken(AntlrDslParser.DIFF, 0); }
		public List<QueryExprContext> queryExpr() {
			return getRuleContexts(QueryExprContext.class);
		}
		public QueryExprContext queryExpr(int i) {
			return getRuleContext(QueryExprContext.class,i);
		}
		public MinusExprContext(QueryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitMinusExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryExprContext queryExpr() throws RecognitionException {
		QueryExprContext _localctx = new QueryExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_queryExpr);
		try {
			setState(63);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case JOIN:
				_localctx = new JoinExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(27);
				match(JOIN);
				setState(28);
				match(T__0);
				setState(29);
				queryExpr();
				setState(30);
				match(T__1);
				setState(31);
				queryExpr();
				setState(32);
				match(T__2);
				}
				break;
			case SEL:
				_localctx = new SelExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(34);
				match(SEL);
				setState(35);
				match(T__0);
				setState(36);
				pred();
				setState(37);
				match(T__1);
				setState(38);
				queryExpr();
				setState(39);
				match(T__2);
				}
				break;
			case PROJ:
				_localctx = new ProjExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(41);
				match(PROJ);
				setState(42);
				match(T__0);
				setState(43);
				attrList();
				setState(44);
				match(T__1);
				setState(45);
				queryExpr();
				setState(46);
				match(T__2);
				}
				break;
			case CUP:
				_localctx = new UnionExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(48);
				match(CUP);
				setState(49);
				match(T__0);
				setState(50);
				queryExpr();
				setState(51);
				match(T__1);
				setState(52);
				queryExpr();
				setState(53);
				match(T__2);
				}
				break;
			case DIFF:
				_localctx = new MinusExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(55);
				match(DIFF);
				setState(56);
				match(T__0);
				setState(57);
				queryExpr();
				setState(58);
				match(T__1);
				setState(59);
				queryExpr();
				setState(60);
				match(T__2);
				}
				break;
			case ID:
				_localctx = new ToIdContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(62);
				match(ID);
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

	public static class InsStmtContext extends ParserRuleContext {
		public TerminalNode INS() { return getToken(AntlrDslParser.INS, 0); }
		public TerminalNode ID() { return getToken(AntlrDslParser.ID, 0); }
		public TupleContext tuple() {
			return getRuleContext(TupleContext.class,0);
		}
		public InsStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitInsStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InsStmtContext insStmt() throws RecognitionException {
		InsStmtContext _localctx = new InsStmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_insStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(INS);
			setState(66);
			match(T__0);
			setState(67);
			match(ID);
			setState(68);
			match(T__1);
			setState(69);
			tuple();
			setState(70);
			match(T__2);
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

	public static class DelStmtContext extends ParserRuleContext {
		public TerminalNode DEL() { return getToken(AntlrDslParser.DEL, 0); }
		public TerminalNode ID() { return getToken(AntlrDslParser.ID, 0); }
		public PredContext pred() {
			return getRuleContext(PredContext.class,0);
		}
		public DelStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitDelStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DelStmtContext delStmt() throws RecognitionException {
		DelStmtContext _localctx = new DelStmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_delStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(DEL);
			setState(73);
			match(T__0);
			setState(74);
			match(ID);
			setState(75);
			match(T__1);
			setState(76);
			pred();
			setState(77);
			match(T__2);
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

	public static class UpdStmtContext extends ParserRuleContext {
		public TerminalNode UPD() { return getToken(AntlrDslParser.UPD, 0); }
		public List<TerminalNode> ID() { return getTokens(AntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(AntlrDslParser.ID, i);
		}
		public PredContext pred() {
			return getRuleContext(PredContext.class,0);
		}
		public UpdStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_updStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitUpdStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UpdStmtContext updStmt() throws RecognitionException {
		UpdStmtContext _localctx = new UpdStmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_updStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			match(UPD);
			setState(80);
			match(T__0);
			setState(81);
			match(ID);
			setState(82);
			match(T__1);
			setState(83);
			pred();
			setState(84);
			match(T__1);
			setState(85);
			match(ID);
			setState(86);
			match(T__1);
			setState(87);
			match(ID);
			setState(88);
			match(T__2);
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

	public static class AttrListContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(AntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(AntlrDslParser.ID, i);
		}
		public AttrListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attrList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitAttrList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttrListContext attrList() throws RecognitionException {
		AttrListContext _localctx = new AttrListContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_attrList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(T__3);
			setState(91);
			match(ID);
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(92);
				match(T__1);
				setState(93);
				match(ID);
				}
				}
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(99);
			match(T__4);
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

	public static class TupleContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(AntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(AntlrDslParser.ID, i);
		}
		public TupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tuple; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitTuple(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TupleContext tuple() throws RecognitionException {
		TupleContext _localctx = new TupleContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_tuple);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(T__0);
			setState(102);
			match(ID);
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(103);
				match(T__1);
				setState(104);
				match(ID);
				}
				}
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(110);
			match(T__2);
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

	public static class PredContext extends ParserRuleContext {
		public PredContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pred; }
	 
		public PredContext() { }
		public void copyFrom(PredContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class InPredContext extends PredContext {
		public TerminalNode MEM() { return getToken(AntlrDslParser.MEM, 0); }
		public TerminalNode ID() { return getToken(AntlrDslParser.ID, 0); }
		public QueryExprContext queryExpr() {
			return getRuleContext(QueryExprContext.class,0);
		}
		public InPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitInPred(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AndPredContext extends PredContext {
		public TerminalNode LAND() { return getToken(AntlrDslParser.LAND, 0); }
		public List<PredContext> pred() {
			return getRuleContexts(PredContext.class);
		}
		public PredContext pred(int i) {
			return getRuleContext(PredContext.class,i);
		}
		public AndPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitAndPred(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotPredContext extends PredContext {
		public TerminalNode LNOT() { return getToken(AntlrDslParser.LNOT, 0); }
		public PredContext pred() {
			return getRuleContext(PredContext.class,0);
		}
		public NotPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitNotPred(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LopPredContext extends PredContext {
		public List<TerminalNode> ID() { return getTokens(AntlrDslParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(AntlrDslParser.ID, i);
		}
		public TerminalNode LOP() { return getToken(AntlrDslParser.LOP, 0); }
		public LopPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitLopPred(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OrPredContext extends PredContext {
		public TerminalNode LOR() { return getToken(AntlrDslParser.LOR, 0); }
		public List<PredContext> pred() {
			return getRuleContexts(PredContext.class);
		}
		public PredContext pred(int i) {
			return getRuleContext(PredContext.class,i);
		}
		public OrPredContext(PredContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AntlrDslVisitor ) return ((AntlrDslVisitor<? extends T>)visitor).visitOrPred(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredContext pred() throws RecognitionException {
		PredContext _localctx = new PredContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_pred);
		try {
			setState(141);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LNOT:
				_localctx = new NotPredContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(112);
				match(LNOT);
				setState(113);
				match(T__0);
				setState(114);
				pred();
				setState(115);
				match(T__2);
				}
				break;
			case LAND:
				_localctx = new AndPredContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(117);
				match(LAND);
				setState(118);
				match(T__0);
				setState(119);
				pred();
				setState(120);
				match(T__1);
				setState(121);
				pred();
				setState(122);
				match(T__2);
				}
				break;
			case LOR:
				_localctx = new OrPredContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(124);
				match(LOR);
				setState(125);
				match(T__0);
				setState(126);
				pred();
				setState(127);
				match(T__1);
				setState(128);
				pred();
				setState(129);
				match(T__2);
				}
				break;
			case MEM:
				_localctx = new InPredContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(131);
				match(MEM);
				setState(132);
				match(T__0);
				setState(133);
				match(ID);
				setState(134);
				match(T__1);
				setState(135);
				queryExpr();
				setState(136);
				match(T__2);
				}
				break;
			case ID:
				_localctx = new LopPredContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(138);
				match(ID);
				setState(139);
				match(LOP);
				setState(140);
				match(ID);
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\26\u0092\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\5\3\34\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4B\n\4\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\b\3\b\3\b\3\b\7\ba\n\b\f\b\16\bd\13\b\3\b\3\b\3\t\3\t\3\t\3"+
		"\t\7\tl\n\t\f\t\16\to\13\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\5\n\u0090\n\n\3\n\2\2\13\2\4\6\b\n\f\16\20\22\2\2\u0096\2\24"+
		"\3\2\2\2\4\33\3\2\2\2\6A\3\2\2\2\bC\3\2\2\2\nJ\3\2\2\2\fQ\3\2\2\2\16\\"+
		"\3\2\2\2\20g\3\2\2\2\22\u008f\3\2\2\2\24\25\5\4\3\2\25\26\7\2\2\3\26\3"+
		"\3\2\2\2\27\34\5\6\4\2\30\34\5\b\5\2\31\34\5\n\6\2\32\34\5\f\7\2\33\27"+
		"\3\2\2\2\33\30\3\2\2\2\33\31\3\2\2\2\33\32\3\2\2\2\34\5\3\2\2\2\35\36"+
		"\7\f\2\2\36\37\7\3\2\2\37 \5\6\4\2 !\7\4\2\2!\"\5\6\4\2\"#\7\5\2\2#B\3"+
		"\2\2\2$%\7\r\2\2%&\7\3\2\2&\'\5\22\n\2\'(\7\4\2\2()\5\6\4\2)*\7\5\2\2"+
		"*B\3\2\2\2+,\7\16\2\2,-\7\3\2\2-.\5\16\b\2./\7\4\2\2/\60\5\6\4\2\60\61"+
		"\7\5\2\2\61B\3\2\2\2\62\63\7\17\2\2\63\64\7\3\2\2\64\65\5\6\4\2\65\66"+
		"\7\4\2\2\66\67\5\6\4\2\678\7\5\2\28B\3\2\2\29:\7\20\2\2:;\7\3\2\2;<\5"+
		"\6\4\2<=\7\4\2\2=>\5\6\4\2>?\7\5\2\2?B\3\2\2\2@B\7\25\2\2A\35\3\2\2\2"+
		"A$\3\2\2\2A+\3\2\2\2A\62\3\2\2\2A9\3\2\2\2A@\3\2\2\2B\7\3\2\2\2CD\7\21"+
		"\2\2DE\7\3\2\2EF\7\25\2\2FG\7\4\2\2GH\5\20\t\2HI\7\5\2\2I\t\3\2\2\2JK"+
		"\7\22\2\2KL\7\3\2\2LM\7\25\2\2MN\7\4\2\2NO\5\22\n\2OP\7\5\2\2P\13\3\2"+
		"\2\2QR\7\23\2\2RS\7\3\2\2ST\7\25\2\2TU\7\4\2\2UV\5\22\n\2VW\7\4\2\2WX"+
		"\7\25\2\2XY\7\4\2\2YZ\7\25\2\2Z[\7\5\2\2[\r\3\2\2\2\\]\7\6\2\2]b\7\25"+
		"\2\2^_\7\4\2\2_a\7\25\2\2`^\3\2\2\2ad\3\2\2\2b`\3\2\2\2bc\3\2\2\2ce\3"+
		"\2\2\2db\3\2\2\2ef\7\7\2\2f\17\3\2\2\2gh\7\3\2\2hm\7\25\2\2ij\7\4\2\2"+
		"jl\7\25\2\2ki\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2\2np\3\2\2\2om\3\2\2"+
		"\2pq\7\5\2\2q\21\3\2\2\2rs\7\t\2\2st\7\3\2\2tu\5\22\n\2uv\7\5\2\2v\u0090"+
		"\3\2\2\2wx\7\n\2\2xy\7\3\2\2yz\5\22\n\2z{\7\4\2\2{|\5\22\n\2|}\7\5\2\2"+
		"}\u0090\3\2\2\2~\177\7\13\2\2\177\u0080\7\3\2\2\u0080\u0081\5\22\n\2\u0081"+
		"\u0082\7\4\2\2\u0082\u0083\5\22\n\2\u0083\u0084\7\5\2\2\u0084\u0090\3"+
		"\2\2\2\u0085\u0086\7\b\2\2\u0086\u0087\7\3\2\2\u0087\u0088\7\25\2\2\u0088"+
		"\u0089\7\4\2\2\u0089\u008a\5\6\4\2\u008a\u008b\7\5\2\2\u008b\u0090\3\2"+
		"\2\2\u008c\u008d\7\25\2\2\u008d\u008e\7\24\2\2\u008e\u0090\7\25\2\2\u008f"+
		"r\3\2\2\2\u008fw\3\2\2\2\u008f~\3\2\2\2\u008f\u0085\3\2\2\2\u008f\u008c"+
		"\3\2\2\2\u0090\23\3\2\2\2\7\33Abm\u008f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}