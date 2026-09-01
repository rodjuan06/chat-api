package com.rodjuan.chatapi.chat.model.objectid;

import org.bson.types.ObjectId;
import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

@JacksonComponent
public class ObjectIdSerializer extends ValueSerializer<ObjectId> {

    @Override
    public void serialize(ObjectId value, JsonGenerator gen, SerializationContext context) {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.toHexString());
        }
    }
}