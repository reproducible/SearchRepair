package Experiment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import antlr.preprocess.SnippetLexer;
import antlr.preprocess.SnippetParser;
import antlr.preprocess.SnippetParser.Add_expressionContext;
import antlr.preprocess.SnippetParser.And_expressionContext;
import antlr.preprocess.SnippetParser.ArgumentsContext;
import antlr.preprocess.SnippetParser.Arith_expressionContext;
import antlr.preprocess.SnippetParser.ArrayExprContext;
import antlr.preprocess.SnippetParser.AssignStatContext;
import antlr.preprocess.SnippetParser.Assign_expressionContext;
import antlr.preprocess.SnippetParser.AtomContext;
import antlr.preprocess.SnippetParser.BlockContext;
import antlr.preprocess.SnippetParser.CallExprContext;
import antlr.preprocess.SnippetParser.CallStatContext;
import antlr.preprocess.SnippetParser.CondExprContext;
import antlr.preprocess.SnippetParser.ConvertExprContext;
import antlr.preprocess.SnippetParser.DeclarationStatContext;
import antlr.preprocess.SnippetParser.ElseifblockContext;
import antlr.preprocess.SnippetParser.ElseifpartContext;
import antlr.preprocess.SnippetParser.ElsepartContext;
import antlr.preprocess.SnippetParser.ExprContext;
import antlr.preprocess.SnippetParser.If_statContext;
import antlr.preprocess.SnippetParser.IfblockContext;
import antlr.preprocess.SnippetParser.IfpartContext;
import antlr.preprocess.SnippetParser.Multi_expressionContext;
import antlr.preprocess.SnippetParser.Or_expressionContext;
import antlr.preprocess.SnippetParser.ReturnStatContext;
import antlr.preprocess.SnippetParser.StatContext;
import antlr.preprocess.SnippetParser.TermContext;

public class Restore {
	
	public static String getMappingString(String source, Map<String, String> map){
		String output = null;
		try{
			InputStream stream = new ByteArrayInputStream(source.getBytes());
			ANTLRInputStream input = new ANTLRInputStream(stream);
			SnippetLexer lexer = new SnippetLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			SnippetParser parser = new SnippetParser(tokens);
			output = getMappingString(parser, map);
		}catch(Exception e){
			e.printStackTrace();
		}
		return output;
	}

	private static String getMappingString(SnippetParser parser,
			Map<String, String> map) {
		
		StringBuilder output = new StringBuilder();
		List<StatContext> stats = parser.prog().stat();
		for(StatContext stat : stats){
			output.append(getMappingString(stat, map));
			output.append("\n");
		}
		
		return output.toString();
	}

	private static String getMappingString(StatContext stat,
			Map<String, String> map) {
			ParseTree child = stat.getChild(0);
			StringBuilder sb = new StringBuilder();
			if(child instanceof DeclarationStatContext){
				DeclarationStatContext decl = (DeclarationStatContext) child;
				sb.append(getMappingString(decl, map));
				sb.append(";\n");
			}
			else if(child instanceof AssignStatContext){
				AssignStatContext assign = (AssignStatContext) child;
				sb.append(getMappingString(assign, map));
				sb.append(";\n");
			}
			else if(child instanceof If_statContext) {
				If_statContext ifstat = (If_statContext) child;
				sb.append(getMappingString(ifstat, map));
				sb.append("\n");
			}
			else if(child instanceof ReturnStatContext){
				ReturnStatContext returnStat = (ReturnStatContext) child;
				sb.append(getMappingString(returnStat, map));
				sb.append("\n");
			}
			else if(child instanceof CallStatContext){
				CallStatContext call = (CallStatContext) child;
				sb.append(getMappingString(call, map));
			}
			return sb.toString();
	}
	
	

	private static Object getMappingString(CallStatContext call,
			Map<String, String> map) {
		return getMappingString(call.callExpr(), map);
	}

	private static String getMappingString(ReturnStatContext returnStat,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append("return ");
		sb.append(getMappingString(returnStat.arith_expression(), map));
		sb.append(";");
		return sb.toString();
	}

	private static String getMappingString(AssignStatContext assign,
			Map<String, String> map) {
		int i = 0;
		StringBuilder sb = new StringBuilder();
		String right = getMappingString(assign.assign_expression(), map);
		if(assign.type() != null){
			sb.append(assign.type().getText());
			i++;
		}
		if(assign.POINTER() != null){
			sb.append(assign.POINTER().getText());
			i++;
		}
		sb.append(" ");
		String id = assign.ID().getText();
		if(map.containsKey(id)){
			sb.append(map.get(id));
			
		}
		else{
			sb.append(id);
		}
		sb.append(" ");
		i++;
		sb.append(assign.getChild(i).getText() + " ");
		sb.append(right);
		return sb.toString();
	}

	private static String getMappingString(
			Assign_expressionContext assign_expression, Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if(assign_expression.arith_expression() != null){
			sb.append(getMappingString(assign_expression.arith_expression(), map));
		}
		else if(assign_expression.convertExpr() != null){
			sb.append(getMappingString(assign_expression.convertExpr(), map));
		}
		else{
			sb.append(assign_expression.getText());
		}
		return sb.toString();
	}

	private static String getMappingString(ConvertExprContext convertExpr,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(convertExpr.type().getText());
		sb.append(')');
		sb.append(getMappingString(convertExpr.assign_expression(), map));
		return sb.toString();
	}

	private static String getMappingString(
			Arith_expressionContext arith_expression, Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(getMappingString(arith_expression.add_expression(), map));
		return sb.toString();
	}

	private static String getMappingString(
			Add_expressionContext add_expression, Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(getMappingString(add_expression.multi_expression(), map) + " ");
		for(int i = 0; i < add_expression.addOperator().size(); i++){
			sb.append(add_expression.addOperator(i).getText() + " ");
			sb.append(getMappingString(add_expression.add_expression(i), map));
		}
		return sb.toString();
	}

	private static String getMappingString(
			Multi_expressionContext multi_expression, Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(getMappingString(multi_expression.expr(0), map) + " ");
		for(int i = 0; i < multi_expression.multiOperator().size(); i++){
			sb.append(multi_expression.multiOperator(i).getText() + " ");
			sb.append(getMappingString(multi_expression.expr(i + 1), map));
		}
		return sb.toString();
	}



	private static String getMappingString(ExprContext expr,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if(expr.atom() != null){
			sb.append(getMappingString(expr.atom(), map));
		}
		else{
			sb.append("(");
			sb.append(getMappingString(expr.add_expression(), map));
			sb.append(")");
		}
		return sb.toString();
	}

	

	private static String getMappingString(If_statContext ifstat,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(getMappingString(ifstat.ifpart(), map));
		sb.append("\n");
		if(ifstat.elseifpart() != null){
			sb.append(getMappingString(ifstat.elseifpart(), map));
			sb.append("\n");
		}
		if(ifstat.elsepart() != null){
			sb.append(getMappingString(ifstat.elsepart(), map));
			sb.append("\n");
		}
		return sb.toString();
	}

	private static Object getMappingString(ElsepartContext elsepart,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append("else ");
		sb.append(getMappingString(elsepart.ifblock(), map));
		return sb.toString();
	}

	private static String getMappingString(ElseifpartContext elseifpart, Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for(ElseifblockContext con : elseifpart.elseifblock()){
			sb.append(getMappingString(con, map));
			sb.append("\n");
		}
		return sb.toString();
	}

	private static String getMappingString(ElseifblockContext con,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(con.getChild(0).getText() + " ");
		sb.append(con.getChild(1).getText());
		sb.append(getMappingString((CondExprContext) con.getChild(2), map));
		sb.append(getMappingString((IfblockContext) con.getChild(3), map));
		return sb.toString();
	}

	private static String getMappingString(IfblockContext child,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if(child.stat() != null){
			sb.append(getMappingString(child.stat(), map));
		}
		else if(child.block() != null){
			sb.append(getMappingString(child.block(), map));
		}
		return sb.toString();
	}

	private static Object getMappingString(BlockContext block,
			Map<String, String> map) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(StatContext stat : block.stat()){
			sb.append(getMappingString(stat, map));
		}
		sb.append("}");
		return sb.toString();
	}

	private static String getMappingString(CondExprContext child,
			Map<String, String> map) {
		return getMappingString(child.or_expression(), map);
	}

	private static String getMappingString(Or_expressionContext or_expression,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(getMappingString(or_expression.and_expression(), map));
		sb.append(" ");
		for(Or_expressionContext con : or_expression.or_expression()){
			sb.append( "||" );
			sb.append(getMappingString(con, map));
		}
		return sb.toString();
	}

	private static String getMappingString(And_expressionContext and_expression, Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(getMappingString(and_expression.term(0), map));
		sb.append(" ");
		for(int i = 1; i < and_expression.term().size(); i++){
			sb.append( "&&" );
			sb.append(getMappingString(and_expression.term(i), map));
		}
		return sb.toString();
	}

	private static String getMappingString(TermContext term,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if(term.or_expression() != null){
			sb.append("(");
			sb.append(getMappingString(term.or_expression(), map));
			sb.append(")");
		}
		else{
			sb.append(getMappingString(term.atom(0), map));
			if(term.operator() != null){
				sb.append(" " + term.operator().getText() + " ");
				sb.append(getMappingString(term.atom(1), map));
			}			
		}
		return sb.toString();
	}

	private static String getMappingString(AtomContext atom,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if(atom.ID() != null){
			if(map.containsKey(atom.ID().getText()))
			{
				sb.append(map.get(atom.ID().getText()));
			}
			else{
				sb.append(atom.ID().getText());
			}
		}
		else if(atom.callExpr() != null){
			sb.append(getMappingString(atom.callExpr(), map));
		}
		else if(atom.arrayExpr() != null){
			sb.append(getMappingString(atom.arrayExpr(), map));
		}
		else sb.append(atom.getText());
		return sb.toString();
	}

	private static String getMappingString(ArrayExprContext arrayExpr,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if(map.containsKey(arrayExpr.getChild(0).getText())){
			sb.append(map.get(arrayExpr.getChild(0).getText()));
		}
		else{
			sb.append(arrayExpr.getChild(0).getText());
		}
		
		sb.append('[');
		if(map.containsKey(arrayExpr.getChild(2).getText())){
			sb.append(map.get(arrayExpr.getChild(2).getText()));
		}
		else{
			sb.append(arrayExpr.getChild(2).getText());
		}
		sb.append(']');
		return sb.toString();
	}

	private static String getMappingString(CallExprContext callExpr,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(callExpr.ID().getText());
		sb.append(getMappingString(callExpr.arguments(), map));
		return sb.toString();
	}



	private static String getMappingString(ArgumentsContext arguments,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		String s = "";
		for(Assign_expressionContext con : arguments.assign_expression()){
			s += getMappingString(con, map);
			s += ", ";
		}
		if(s.endsWith(", ")) s = s.substring(0, s.length() - 2);
		sb.append(s);
		sb.append(")");
		return sb.toString();
	}

	private static String getMappingString(IfpartContext ifpart, Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(ifpart.getChild(0).getText());
		sb.append(getMappingString((CondExprContext) ifpart.getChild(1), map));
		sb.append(getMappingString((IfblockContext) ifpart.getChild(2), map));
		return sb.toString();
	}

	private static String getMappingString(DeclarationStatContext decl,
			Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		sb.append(decl.type().getText());
		i++;
		
		if(decl.POINTER() != null){
			sb.append("*");
			i++;
		}
		sb.append(" ");
		String id = decl.ID().getText();
		if(map.containsKey(id)){
			sb.append(map.get(id));
		}
		else{
			sb.append(id);
		}
		i++;
		
		sb.append(" ");
		while(i < decl.getChildCount()){
			sb.append(decl.getChild(i).getText());
		}
		return sb.toString();
	}

}
