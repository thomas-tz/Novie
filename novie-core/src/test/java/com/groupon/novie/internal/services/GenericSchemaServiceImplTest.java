package com.groupon.novie.internal.services;

import com.google.common.collect.Sets;
import com.groupon.novie.SchemaDefinition;
import com.groupon.novie.internal.engine.QueryOperator;
import com.groupon.novie.internal.engine.QueryParameter;
import com.groupon.novie.internal.engine.constraint.SimpleTestConstraint;
import com.groupon.novie.internal.exception.InvalidParameterException;
import com.groupon.novie.internal.exception.NovieException;
import com.groupon.novie.internal.response.Report;
import com.groupon.novie.internal.response.ReportMeasure;
import com.groupon.novie.internal.validation.QueryParameterAware;
import com.groupon.novie.internal.validation.QueryParameterEnvelope;
import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Created by thomas on 06/02/2014.
 */
public class GenericSchemaServiceImplTest {

    @Mocked
    @Injectable
    SqlQueryEngine sqlQueryEngine;

    @Mocked
    @Injectable
    ContextReaderService contextReaderService;

    @Mocked
    SchemaDefinition config;

    @Mocked
    QueryParameterEnvelope parametersEnvelope;

    @Tested
    GenericSchemaServiceImpl genericSchemaService;

    @Test
    public void emptyResult() throws NovieException {
        final QueryParameterAware voidQueryParameterAware = new QueryParameterAware() {
            @Override
            public void addToQuery(QueryParameter parameter) throws InvalidParameterException {

            }
        };
        new NonStrictExpectations() {
            {
                contextReaderService.getResultSizeLimit();
                result = 0l;
                parametersEnvelope.getTimeZoneConstraint();
                result = voidQueryParameterAware;
                parametersEnvelope.getConstraints();
                result = Collections.emptySet();
                parametersEnvelope.getAggregations();
                result = Collections.emptySet();
                config.getMandatoryDimension();
                result = Collections.emptySet();
                sqlQueryEngine.retrieveSummary(config, ReportMeasure.class, withAny(new QueryParameter()));
                result = new ReportMeasure();
                sqlQueryEngine.retrieveRecords(config, ReportMeasure.class, withAny(new QueryParameter()));
                result = Collections.emptyList();
            }
        };
        Report report = genericSchemaService.generateReport(config, parametersEnvelope);
        Assert.assertEquals("Summary object do not match", new ReportMeasure(), report.getSummary());
        Assert.assertEquals("Result size", 0 , report.getTotal());
        Assert.assertEquals("Records size", 0 , report.getRecords().size());

    }

    @Test(expected = InvalidParameterException.class)
    public void mandatoryDimensionFailed() throws NovieException {
        final QueryParameterAware voidQueryParameterAware = new QueryParameterAware() {
            @Override
            public void addToQuery(QueryParameter parameter) throws InvalidParameterException {

            }
        };
        new NonStrictExpectations() {
            {
                parametersEnvelope.getTimeZoneConstraint();
                result = voidQueryParameterAware;
                parametersEnvelope.getConstraints();
                result = Collections.emptySet();
                parametersEnvelope.getAggregations();
                result = Collections.emptySet();
                config.getMandatoryDimension();
                result = Arrays.asList("DATE");
                sqlQueryEngine.retrieveSummary(config, ReportMeasure.class, withAny(new QueryParameter()));
                result = new ReportMeasure();
                sqlQueryEngine.retrieveRecords(config, ReportMeasure.class, withAny(new QueryParameter()));
                result = Collections.emptyList();
            }
        };
        Report report = genericSchemaService.generateReport(config, parametersEnvelope);
    }

    @Test(expected = InvalidParameterException.class)
    public void mandatoryDimensionsFailed() throws NovieException {
        final QueryParameterAware voidQueryParameterAware = new QueryParameterAware() {
            @Override
            public void addToQuery(QueryParameter parameter) throws InvalidParameterException {

            }
        };
        new NonStrictExpectations() {
            {
                parametersEnvelope.getTimeZoneConstraint();
                result = voidQueryParameterAware;
                parametersEnvelope.getConstraints();
                result = Collections.emptySet();
                parametersEnvelope.getAggregations();
                result = Collections.emptySet();
                config.getMandatoryDimension();
                result = Arrays.asList("DATE", "USER");
                sqlQueryEngine.retrieveSummary(config, ReportMeasure.class, withAny(new QueryParameter()));
                result = new ReportMeasure();
                sqlQueryEngine.retrieveRecords(config, ReportMeasure.class, withAny(new QueryParameter()));
                result = Collections.emptyList();
            }
        };
        Report report = genericSchemaService.generateReport(config, parametersEnvelope);
    }

    @Test
    public void mandatoryDimensionSuccess() throws NovieException {
        final QueryParameterAware voidQueryParameterAware = new QueryParameterAware() {
            @Override
            public void addToQuery(QueryParameter parameter) throws InvalidParameterException {

            }
        };

        final SimpleTestConstraint<Date> c1 = new SimpleTestConstraint<Date>("date", QueryOperator.EQUAL, new Date());
        final SimpleTestConstraint<String> c2 = new SimpleTestConstraint<String>("user", QueryOperator.EQUAL, "me");

        new NonStrictExpectations() {
            {
                contextReaderService.getResultSizeLimit();
                result = 0l;
                parametersEnvelope.getTimeZoneConstraint();
                result = voidQueryParameterAware;
                parametersEnvelope.getConstraints();
                result = Sets.newHashSet(c1, c2);
                parametersEnvelope.getAggregations();
                result = Collections.emptySet();
                config.getMandatoryDimension();
                result = Arrays.asList("DATE");
                sqlQueryEngine.retrieveSummary(config, ReportMeasure.class, withAny(new QueryParameter()));
                result = new ReportMeasure();
                sqlQueryEngine.retrieveRecords(config, ReportMeasure.class, withAny(new QueryParameter()));
                result = Collections.emptyList();
            }
        };
        Report report = genericSchemaService.generateReport(config, parametersEnvelope);
        Assert.assertEquals("Summary object do not match", new ReportMeasure(), report.getSummary());
        Assert.assertEquals("Result size", 0, report.getTotal());
        Assert.assertEquals("Records size", 0, report.getRecords().size());
    }
}
