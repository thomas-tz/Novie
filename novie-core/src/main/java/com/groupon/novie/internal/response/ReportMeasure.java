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
package com.groupon.novie.internal.response;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.groupon.novie.internal.serialize.MapXmlMeasureAdapter;

public class ReportMeasure implements MeasureAppender {

    private Map<String, Number> measures = new LinkedHashMap<String, Number>();

    @Override
    public void addMeasure(String measureName, Number value) {
        measures.put(measureName, value);
    }

    public String toString() {
        StringBuilder returnValue = new StringBuilder();
        for (Map.Entry<String, Number> e : measures.entrySet()) {
            if (returnValue.length() > 0) {
                returnValue.append(", ");
            }
            returnValue.append(e.getKey());
            returnValue.append("=");
            returnValue.append(e.getValue());
        }
        return returnValue.toString();
    }

    @XmlJavaTypeAdapter(MapXmlMeasureAdapter.class)
    public Map<String, Number> getMeasures() {
        return measures;
    }

    public void setMeasures(Map<String, Number> measures) {
        this.measures = measures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportMeasure that = (ReportMeasure) o;

        if (measures != null ? !measures.equals(that.measures) : that.measures != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return measures != null ? measures.hashCode() : 0;
    }
}
