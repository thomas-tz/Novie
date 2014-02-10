/*
Copyright (c) 2013, Groupon, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

Neither the name of GROUPON nor the names of its contributors may be
used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.groupon.novie.internal.engine.builder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;

import com.groupon.novie.internal.exception.NovieRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.google.common.base.Optional;
import com.groupon.novie.SchemaDefinition;
import com.groupon.novie.internal.engine.QueryParameter;
import com.groupon.novie.internal.engine.constraint.Constraint;
import com.groupon.novie.internal.engine.constraint.ConstraintPair;
import com.groupon.novie.internal.engine.constraint.ConstraintPair.BinaryOperator;
import com.groupon.novie.internal.engine.schema.AbstractSqlColumn;
import com.groupon.novie.internal.engine.schema.DimensionTable;
import com.groupon.novie.internal.response.GroupDisplayingRecord;
import com.groupon.novie.internal.response.MeasureAppender;
import com.groupon.novie.internal.response.ReportRecord;

public class MeasuresSqlQueryBuilder<T extends MeasureAppender> extends AbstractSqlQueryBuilder<T> {

    private static final Logger LOG = LoggerFactory.getLogger(MeasuresSqlQueryBuilder.class);


    public MeasuresSqlQueryBuilder(SchemaDefinition schemaDefinition, Class<T> resultType, QueryParameter queryParameter) {
        super(schemaDefinition,resultType,queryParameter);
    }


    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {

        try {
            T returnValue = resultType.newInstance();
            if (getGroupByElement().isPresent() && ReportRecord.class.isAssignableFrom(resultType)) {
                ReportRecord rr = (ReportRecord) returnValue;
                GroupDisplayingRecord currentGroup = null;
                for (Pair<AbstractSqlColumn, List<AbstractSqlColumn>> v : groupByElement.get().getGroupingColumns()) {
                    if (v.getLeft().getSqlTable() instanceof DimensionTable) {
                        DimensionTable dimTable = (DimensionTable) v.getLeft().getSqlTable();
                        currentGroup = rr.addOrRetrieveGroup(dimTable.getDimensionName());
                        for (AbstractSqlColumn col : v.getRight()) {
                            String groupValueStr = col.getColumnType().mapResult(col.getAlias(), rs, tz);
                            currentGroup.addInformation(col.getBusinessName(), groupValueStr);
                            if (v.getRight().size() == 1) {
                                // Only one information => selected by the user
                                currentGroup.setDisplay(groupValueStr);
                            }
                        }

                        if (currentGroup.getDisplay() == null) {
                            // In case of there is multiple returned
                            // informations
                            if (StringUtils.isBlank(dimTable.getDisplayTemplate())) {
                                StringBuilder buff = new StringBuilder();
                                for (Entry<String, String> e : currentGroup.getInformations().entrySet()) {
                                    if (buff.length() > 0) {
                                        buff.append(" ");
                                    }
                                    buff.append(e.getValue());
                                }
                                currentGroup.setDisplay(buff.toString());
                            } else {
                                String display = new String(dimTable.getDisplayTemplate());
                                for (Entry<String, String> e : currentGroup.getInformations().entrySet()) {
                                    display = display.replace("%" + e.getKey() + "%", e.getValue() == null ? "null" : e.getValue());
                                }
                                currentGroup.setDisplay(display);
                            }
                        }
                    }
                }
            }
            // **
            // Process the measure

            for (AbstractSqlColumn col : getSelectElement().getMeasures()) {

                switch (col.getColumnType()) {
                    case DECIMAL:
                        returnValue.addMeasure(col.getBusinessName(), rs.getDouble(col.getAlias()));
                        break;
                    case INTEGER:
                        returnValue.addMeasure(col.getBusinessName(), rs.getLong(col.getAlias()));
                        break;
                    default:
                        throw new SQLException("Col " + col.getBusinessName() + " has a non supported format for a Measure.");

                }

            }

            return returnValue;
        } catch (InstantiationException e) {
            throw new SQLException("Can instanciate result object.", e);
        } catch (IllegalAccessException e) {

            throw new SQLException("Can instanciate result object.", e);
        }
    }
}
