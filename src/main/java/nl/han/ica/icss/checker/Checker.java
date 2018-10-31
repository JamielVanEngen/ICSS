package nl.han.ica.icss.checker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.*;

public class Checker {

    private LinkedList<HashMap<String,ExpressionType>> variableTypes;
    private HashMap<String, ExpressionType[]> allowedProperties;

    public Checker() {
        variableTypes = new LinkedList<>();
        initializeAllowedProperties();
    }

    private void initializeAllowedProperties() {
        allowedProperties = new HashMap<>();
        allowedProperties.put("color", new ExpressionType[] {
                ExpressionType.COLOR
        });
        allowedProperties.put("background-color", new ExpressionType[] {
                ExpressionType.COLOR
        });
        allowedProperties.put("width", new ExpressionType[] {
                ExpressionType.PERCENTAGE,
                ExpressionType.PIXEL
        });
        allowedProperties.put("height", new ExpressionType[] {
                ExpressionType.PIXEL
        });
    }

    public void check(AST ast) {
        variableTypes.add(new HashMap<>());
        checkSemantics(ast.root);
    }

    /**
     * This method recursively goes through the ASTTree.
     * @param node The current node of the tree.
     */
    private void checkSemantics(ASTNode node) {
        if (node instanceof Stylerule && ((Stylerule)node).selectors.size() >= 1) {
            variableTypes.add(new HashMap<>());
        }else if (node instanceof VariableAssignment) {
            setVariableTypes((VariableAssignment)node);
        } else if (node instanceof VariableReference) {
            checkIfUndeclaredVariablesAreUsed((VariableReference)node);
        } else if (node instanceof Operation) {
            checkExpressionOperationSemantics((Operation)node);
        } else if (node instanceof Declaration) {
            checkDeclarationSemantics((Declaration)node);
        }

        for (ASTNode child : node.getChildren()) {
            checkSemantics(child);
        }
        if (node instanceof Stylerule && ((Stylerule)node).selectors.size() >= 1) {
            variableTypes.removeLast();
        }
    }



    private void setVariableTypes(VariableAssignment assignment) {
        HashMap<String, ExpressionType> variables = variableTypes.getLast();
        variables.put(assignment.name.name, getExpressionType(assignment.expression));
    }

    private ExpressionType getExpressionType(Expression expression) {
        if (expression instanceof Literal) {
            return convertLiteralToExpressionType((Literal)expression);
        } else if (expression instanceof Operation) {
            return getExpressionTypeFromOperation((Operation) expression);
        } else {
            ExpressionType type = ExpressionType.UNDEFINED;
            for (HashMap<String, ExpressionType> map : variableTypes) {
                if (map.containsKey(((VariableReference)expression).name)) {
                    type = map.get(((VariableReference)expression).name);
                }
            }
            return type;
        }
    }

    private void checkIfUndeclaredVariablesAreUsed(VariableReference reference) {
        if (!isVariableFoundInVariableTypes(reference.name)) {
            reference.setError("Variable '" + reference.name + "' not defined!");
        }
    }

    private boolean isVariableFoundInVariableTypes(String variableName) {
        for (HashMap<String, ExpressionType> map : variableTypes) {
            if (map.containsKey(variableName)) {
                return true;
            }
        }
        return false;
    }

    private void checkExpressionOperationSemantics(Operation expression) {
        ExpressionType lhsType = getExpressionType(expression.lhs);
        ExpressionType rhsType = getExpressionType(expression.rhs);
        if (lhsType == ExpressionType.COLOR || rhsType == ExpressionType.COLOR) {
            expression.setError("An equation cannot contain a color.");
        } else {
            if (expression instanceof AddOperation || expression instanceof SubtractOperation) {

                if (lhsType != rhsType) {
                    expression.setError("The types have to be equal to each other when adding or subtracting.");
                }
            } else if (expression instanceof MultiplyOperation) {
                if (lhsType != ExpressionType.SCALAR & rhsType != ExpressionType.SCALAR) {
                    expression.setError("Multiplying requires one scalar type in the equation.");
                }
            }
        }
    }

    private ExpressionType convertLiteralToExpressionType(Literal literal) {
        if (literal instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (literal instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (literal instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else {
            return ExpressionType.SCALAR;
        }
    }

    private void checkDeclarationSemantics(Declaration declaration) {
        ExpressionType expressionType;
        if (declaration.expression instanceof Operation) {
            expressionType = getExpressionTypeFromOperation((Operation) declaration.expression);
        } else {
            expressionType = getExpressionType(declaration.expression);
        }
        ExpressionType[] allowedTypes = allowedProperties.get(declaration.property.name);
        if (!(Arrays.asList(allowedTypes).contains(expressionType))) {
            declaration.setError("An expression with the type of '" + expressionType
             + "' is not allowed on the " + declaration.property.name + " property");
        }
    }

    private ExpressionType getExpressionTypeFromOperation(Operation expression) {
        ExpressionType lhsType;
        ExpressionType rhsType;
        if (expression.lhs instanceof Operation) {
            lhsType = getExpressionTypeFromOperation((Operation) expression.lhs);
        } else {
            lhsType = getExpressionType(expression.lhs);
        }
        if (expression.rhs instanceof Operation) {
            rhsType = getExpressionTypeFromOperation(((Operation) expression.rhs));
        }
        else {
            rhsType = getExpressionType(expression.rhs);
        }
        if (lhsType == ExpressionType.SCALAR) {
            return rhsType;
        } else {
            return lhsType;
        }
    }
}
