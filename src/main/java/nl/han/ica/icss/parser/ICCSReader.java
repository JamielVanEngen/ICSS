package nl.han.ica.icss.parser;

import java.util.Stack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ICCSReader implements ICSSListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private Stack<ASTNode> currentContainer;

	public ICCSReader() {
		ast = new AST();
		currentContainer = new Stack<>();
	}
    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
	    Stylesheet stylesheet = new Stylesheet();
		if (ctx.children.size() > 0) {
			currentContainer.push(stylesheet);
		}
		ast.setRoot(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
	    if (currentContainer.peek() instanceof Stylesheet) {
	        currentContainer.pop();
        }
	}


    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        ASTNode parent = currentContainer.peek();
        if (parent instanceof Declaration) {
            currentContainer.pop();
            parent = currentContainer.peek();
        }
        ASTNode style = new Stylerule();
        parent.addChild(style);
        currentContainer.push(style);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        ASTNode top = currentContainer.peek();
        if (top instanceof Stylerule) {
            currentContainer.pop();
        }
    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
        ASTNode parent = currentContainer.peek();
        if (parent instanceof Stylerule) {
            ASTNode style;
            if (ctx.getText().startsWith("#")) {
                style = new ClassSelector(ctx.getText());
            } else if (ctx.getText().startsWith(".")) {
                style = new IdSelector(ctx.getText());
            } else {
                style = new TagSelector(ctx.getText());
            }

            currentContainer.push(style);
            ((Stylerule) parent).selectors.add((Selector)style);
        }
    }

    @Override
    public void exitSelector(ICSSParser.SelectorContext ctx) {
        if (currentContainer.peek() instanceof Selector) {
            currentContainer.pop();
        }
    }

    @Override
    public void enterDecleration(ICSSParser.DeclerationContext ctx) {
        currentContainer.push(new Declaration());
    }

    @Override
    public void exitDecleration(ICSSParser.DeclerationContext ctx) {
	    ASTNode currentTop = currentContainer.peek();
	    // This if else-if structure has not been cleaned up to make it more readable in terms of variable names.
	    if (currentTop instanceof Expression) {
	        // When a variable or literal it means that it needs to be added to the decleration.
            ASTNode expression = currentContainer.pop();
            ASTNode declaration = currentContainer.pop();
            declaration.addChild(expression);
            ASTNode stylerule = currentContainer.peek();
            stylerule.addChild(declaration);
        }else if (currentTop instanceof Declaration) {
            ASTNode declaration = currentContainer.pop();
            ASTNode stylerule = currentContainer.peek();
            stylerule.addChild(declaration);
        }
    }

    @Override
    public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
        ASTNode parent = currentContainer.peek();

        if (parent instanceof Declaration) {
            ((Declaration)parent).property = new PropertyName(ctx.getText());
        }
    }

    @Override
    public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {

    }

	@Override
	public void enterExpression(ICSSParser.ExpressionContext ctx) {
	    // Handles operators here.
        // The reason for this is to deal with the operator being second in the expression.
        // This way there can be a nesting of operators.
	    if (ctx.children.size() == 3) {
            Operation currentOperation;
            String operator = ctx.children.get(1).getText();
            switch (operator) {
                case "+":
                    currentOperation = new AddOperation();
                    break;
                case "*":
                    currentOperation = new MultiplyOperation();
                    break;
                default:
                    currentOperation = new SubtractOperation();
                    break;
            }

            currentContainer.push(currentOperation);
        }
	}

	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
	    ASTNode top = currentContainer.peek();
	    if (top instanceof Operation) {
	        currentContainer.pop();
	        ASTNode parent = currentContainer.peek();
	        parent.addChild(top);
	        // The line underneath had to be added because a declaration is the only exception.
        } else if (!(top instanceof Declaration)) {
	        currentContainer.pop();
        }
	}

    @Override
    public void enterLiteral(ICSSParser.LiteralContext ctx) {
        ASTNode parent = currentContainer.peek();
        String value = ctx.getText();
        Literal literal;
	    if (value.startsWith("#")) {
            literal = new ColorLiteral(value);
        } else if (value.endsWith("%")) {
            literal = new PercentageLiteral(value);
        } else if (value.endsWith("px")) {
            literal = new PixelLiteral(value);
        } else {
            literal = new ScalarLiteral(value);
        }
        parent.addChild(literal);
        currentContainer.push(literal);

    }

    @Override
    public void exitLiteral(ICSSParser.LiteralContext ctx) {

    }

    @Override
    public void enterVariableName(ICSSParser.VariableNameContext ctx) {
        ASTNode parent = currentContainer.peek();
        ASTNode variableRef = new VariableReference(ctx.getText());
        parent.addChild(variableRef);
        if (parent instanceof Expression) {
            currentContainer.push(variableRef);
        }
    }

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode test = new VariableAssignment();
        currentContainer.push(test);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode variableAssignment =  currentContainer.pop();
        ASTNode parent = currentContainer.peek();
        parent.addChild(variableAssignment);
    }

    @Override
    public void enterBody(ICSSParser.BodyContext ctx) {
    }

    @Override
    public void exitBody(ICSSParser.BodyContext ctx) {

    }

    @Override
    public void exitVariableName(ICSSParser.VariableNameContext ctx) {

    }

	@Override
	public void enterOperator(ICSSParser.OperatorContext ctx) {
	}

	@Override
	public void exitOperator(ICSSParser.OperatorContext ctx) {
	}

    @Override
    public void enterMultiply(ICSSParser.MultiplyContext ctx) {
    }

    @Override
    public void exitMultiply(ICSSParser.MultiplyContext ctx) {
    }

    @Override
	public void visitTerminal(TerminalNode terminalNode) {

	}

	@Override
	public void visitErrorNode(ErrorNode errorNode) {

	}

	@Override
	public void enterEveryRule(ParserRuleContext parserRuleContext) {

	}

	@Override
	public void exitEveryRule(ParserRuleContext parserRuleContext) {

	}
}
