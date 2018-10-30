// Generated from /home/jamiel/Documents/projects/school/icss/src/main/antlr4/nl/han/ica/icss/parser/ICSS.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ICSSParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ICSSVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ICSSParser#stylesheet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStylesheet(ICSSParser.StylesheetContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#stylerule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStylerule(ICSSParser.StyleruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelector(ICSSParser.SelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBody(ICSSParser.BodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#decleration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecleration(ICSSParser.DeclerationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ICSSParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(ICSSParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#multiply}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiply(ICSSParser.MultiplyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator(ICSSParser.OperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#variableAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableAssignment(ICSSParser.VariableAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#variableName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableName(ICSSParser.VariableNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ICSSParser#propertyName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyName(ICSSParser.PropertyNameContext ctx);
}