package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
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
        iterateThroughAST(ast.root);
    }

    private void iterateThroughAST(ASTNode node) {
        if (node instanceof Declaration) {
            calculateDeclaration((Declaration)node);
        } else if (node instanceof VariableAssignment) {
            calculateVariableAssignment((VariableAssignment)node);
        }
    }

    private void calculateDeclaration(Declaration declaration) {

    }

    private void calculateVariableAssignment(VariableAssignment assignment) {
        String variableName = assignment.name.name;
        Literal value = calculateExpression(assignment.expression);
    }

    private Literal calculateExpression(Expression expression) {

    }

    private Literal calculateOperation(Operation operation) {

    }
}
