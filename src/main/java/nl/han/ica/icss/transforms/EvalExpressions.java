package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.add(new HashMap<>());
        iterateThroughAST(ast.root);
    }

    private void iterateThroughAST(ASTNode node) {
        if (node instanceof Stylerule && ((Stylerule)node).selectors.size() >= 1) {
            variableValues.add(new HashMap<>());
        } else if (node instanceof Declaration) {
            calculateDeclaration((Declaration)node);
        } else if (node instanceof VariableAssignment) {
            calculateVariableAssignment((VariableAssignment)node);
        }

        for (ASTNode child : node.getChildren()) {
            iterateThroughAST(child);
        }

        if (node instanceof Stylerule && ((Stylerule)node).selectors.size() >= 1) {
            variableValues.removeLast();
        }
    }

    private void calculateDeclaration(Declaration declaration) {
        declaration.expression = calculateExpression(declaration.expression);
    }

    private void calculateVariableAssignment(VariableAssignment assignment) {
        String variableName = assignment.name.name;
        Literal value = calculateExpression(assignment.expression);
        HashMap<String, Literal> currentBlock = variableValues.getLast();
        currentBlock.put(variableName, value);
        assignment.expression = value;
    }

    private Literal calculateExpression(Expression expression) {
        if (expression instanceof Operation) {
            return calculateOperation((Operation)expression);
        } else if (expression instanceof Literal) {
            return (Literal)expression;
        } else {
            VariableReference reference = (VariableReference)expression;
            Literal value = null;
            for (HashMap<String, Literal> map : variableValues) {
                if (map.containsKey(reference.name)) {
                    value = map.get(reference.name);
                }
            }

            return value;
        }
    }

    private Literal calculateOperation(Operation operation) {
        Literal leftSide = calculateExpression(operation.lhs);
        Literal rightSide = calculateExpression(operation.rhs);

        if (operation instanceof AddOperation) {
            return calculateAddOperation(leftSide, rightSide);
        } else if (operation instanceof SubtractOperation) {
            return calculateSubtractOperation(leftSide, rightSide);
        } else {
            return calculateMultiplyOperation(leftSide, rightSide);
        }
    }

    private Literal calculateAddOperation(Literal leftSide, Literal rightSide) {
        int calculatedValue;

        if (leftSide instanceof PercentageLiteral) {
            calculatedValue = ((PercentageLiteral) leftSide).value + ((PercentageLiteral) rightSide).value;
            return new PercentageLiteral(calculatedValue);
        } else if (leftSide instanceof PixelLiteral) {
            calculatedValue = ((PixelLiteral) leftSide).value + ((PixelLiteral) rightSide).value;
            return new PixelLiteral(calculatedValue);
        } else {
            calculatedValue = ((ScalarLiteral) leftSide).value + ((ScalarLiteral) rightSide).value;
            return new ScalarLiteral(calculatedValue);
        }
    }

    private Literal calculateSubtractOperation(Literal leftSide, Literal rightSide) {
        int calculatedValue;

        if (leftSide instanceof PercentageLiteral) {
            calculatedValue = ((PercentageLiteral) leftSide).value - ((PercentageLiteral) rightSide).value;
            return new PercentageLiteral(calculatedValue);
        } else if (leftSide instanceof PixelLiteral) {
            calculatedValue = ((PixelLiteral) leftSide).value - ((PixelLiteral) rightSide).value;
            return new PixelLiteral(calculatedValue);
        } else {
            calculatedValue = ((ScalarLiteral) leftSide).value - ((ScalarLiteral) rightSide).value;
            return new ScalarLiteral(calculatedValue);
        }
    }

    private Literal calculateMultiplyOperation(Literal leftSide, Literal rightSide) {
        int calculatedValue;
        Literal nonScalarSide;
        Literal scalarSide;
        if (leftSide instanceof ScalarLiteral) {
            nonScalarSide = rightSide;
            scalarSide = leftSide;
        } else {
            nonScalarSide = leftSide;
            scalarSide = rightSide;
        }

        if (nonScalarSide instanceof PercentageLiteral) {
            calculatedValue = ((PercentageLiteral) nonScalarSide).value * ((ScalarLiteral) scalarSide).value;
            return new PercentageLiteral(calculatedValue);
        } else if (nonScalarSide instanceof PixelLiteral) {
            calculatedValue = ((PixelLiteral) nonScalarSide).value * ((ScalarLiteral) scalarSide).value;
            return new PixelLiteral(calculatedValue);
        } else {
            calculatedValue = ((ScalarLiteral) nonScalarSide).value * ((ScalarLiteral) scalarSide).value;
            return new ScalarLiteral(calculatedValue);
        }
    }
}
