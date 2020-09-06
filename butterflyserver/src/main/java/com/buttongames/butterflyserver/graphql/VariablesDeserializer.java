package com.buttongames.butterflyserver.graphql;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;

import java.io.IOException;
import java.util.Map;

class VariablesDeserializer extends JsonDeserializer<Map<String, Object>> {
    @Override
    public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final Object o = p.readValueAs(Object.class);
        if (o instanceof Map) {
            return (Map<String, Object>) o;
        } else if (o instanceof String) {
            return mapper.readValue((String) o, new TypeReference<Map<String, Object>>() {
            });
        } else {
            throw new RuntimeJsonMappingException("variables should be either an object or a string");
        }
    }
}
