/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.reactions;

import java.util.logging.Logger;

/**
 *
 * @author nicoletti
 */
public class GreaterThanExpression extends BooleanExpression {

    private static final String OPERATOR = Statement.GREATER_THAN;
    private static final Logger LOG = Logger.getLogger(GreaterThanExpression.class.getName());

    @Override
    public String getOperand() {
        return OPERATOR;
    }

    public GreaterThanExpression(String left, String right) {
        super(left, right);
    }

    @Override
    public Boolean evaluate() {
        try {
            Integer intRightValue = new Integer(getRight());
            Integer intLeftValue = new Integer(getLeft());
            return intLeftValue > intRightValue;
        } catch (NumberFormatException nfe) {
            LOG.warning(Statement.GREATER_THAN  + " operator can be applied only to integer values");
            return false;
        }
    }

}
