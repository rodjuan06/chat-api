package com.rodjuan.chatapi.chat.model.objectid;

import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

@JacksonComponent
public class ObjectIdDeserializer extends ValueDeserializer<ObjectId> {

    @Override
    public ObjectId deserialize(JsonParser parser, DeserializationContext context) {
        String value = parser.getString();

        if (value == null || !ObjectId.isValid(value)) {
            throw new IllegalArgumentException("Invalid ObjectId: " + value);
        }

        return new ObjectId(value);
    }
}