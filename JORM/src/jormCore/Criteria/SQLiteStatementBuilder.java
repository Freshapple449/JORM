package jormCore.Criteria;

import jormCore.JormApplication;
import jormCore.Wrapping.ClassWrapper;

public class SQLiteStatementBuilder extends StatementBuilder {
    /**
     * this is shit
     * 
     * @param clause
     * @return
     */
    protected String calculateWhereClause(WhereClause clause) {

        if (clause == null)
            return "";

        if (clause.getLeftClause() == null && clause.getRightClause() == null) {
            return evaluateBasicWhereClause(clause);
        }

        if (clause.getLeftClause() == null && clause.getRightClause() != null) {
            return " ( " + evaluateBasicWhereClause(clause) + calculateLogicOperator(clause.getLogicOperator())
                    + calculateWhereClause(clause.getRightClause()) + " ) ";
        }

        if (clause.getLeftClause() != null && clause.getRightClause() == null) {
            return " ( " + calculateWhereClause(clause.getLeftClause())
                    + calculateLogicOperator(clause.getLogicOperator()) + evaluateBasicWhereClause(clause) + " ) ";
        }

        if (clause.getLeftClause() != null && clause.getRightClause() != null) {
            return " ( " + calculateWhereClause(clause.getLeftClause())
                    + calculateLogicOperator(clause.getLogicOperator()) + calculateWhereClause(clause.getRightClause())
                    + " ) ";
        }

        return "";
    }

    private String evaluateBasicWhereClause(WhereClause clause) {
        return " ( " + clause.getPropertyName() + calculateComparisonOperator(clause.getComparisonOperator())
                + JormApplication.getApplication().getCurrentFieldTypeParser()
                        .normalizeValueForInsertStatement(clause.getValue())
                + " ) ";
    }

    public String createSelect(ClassWrapper type, WhereClause whereClause, boolean loadDeleted) {
        // SELECT * FROM [TYPE] WHERE [WHERE]
        String result = "SELECT * FROM " + type.getName();
        WhereClause resultingClause = new WhereClause("DELETED", 0, ComparisonOperator.Equal);
        
        // normal case
        if(!loadDeleted)
        {
            resultingClause = resultingClause.And(whereClause);
        }
        else if (whereClause != null)
        {
            resultingClause = whereClause;
        }
        else
        {
            return result;
        }

        result += " WHERE " + calculateWhereClause(resultingClause);

        System.out.println(result);
        System.out.println();

        return result;
    }

    @Override
    public String createSelect(ClassWrapper type, WhereClause whereClause) {
        return createSelect(type, whereClause, false);
    }

    @Override
    protected String EQUAL() {
        return " = ";
    }

    @Override
    protected String NOTEQUAL() {
        return " <> ";
    }

    @Override
    protected String LESS() {
        return " < ";
    }

    @Override
    protected String LESSOREQUAL() {
        return " <= ";
    }

    @Override
    protected String GREATER() {
        return " > ";
    }

    @Override
    protected String GREATEROREQUAL() {
        return " >= ";
    }

    @Override
    protected String AND() {
        return " AND ";
    }

    @Override
    protected String OR() {
        return " OR ";
    }

    @Override
    protected String NOT() {
        return " NOT ";
    }
}