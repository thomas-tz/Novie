package com.groupon.novie.internal.engine.constraint;

import com.groupon.novie.internal.engine.QueryOperator;
import com.groupon.novie.internal.engine.QueryParameter;
import com.groupon.novie.internal.exception.InvalidParameterException;
import com.groupon.novie.internal.validation.QueryParameterAware;

/**
 * This class is a QueryParamterAware test class which simulate a simple constraint.
 *
 * @author thomas
 */
public class SimpleTestConstraint<T> implements QueryParameterAware{

    private String dimension;
    private String information;
    private QueryOperator operator;
    private T value;

    public SimpleTestConstraint(String dimension, String information, QueryOperator operator, T value) {
        this.dimension = dimension;
        this.information = information;
        this.operator = operator;
        this.value = value;
    }

    public SimpleTestConstraint(String dimension,QueryOperator operator, T value) {
        this.dimension = dimension;
        this.information = null;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public void addToQuery(QueryParameter parameter) throws InvalidParameterException {
        parameter.addConstraint(dimension,information,operator,value);
    }
}
