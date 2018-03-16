package com.etrade.framework.core.web.json;

import java.io.IOException;
import java.util.Date;

import com.etrade.framework.core.util.DateUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateTimeJsonSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        if (value != null) {
            jgen.writeString(DateUtils.formatTime(value));
        }
    }

    @Override
    public Class<Date> handledType() {
        return Date.class;
    }
}
