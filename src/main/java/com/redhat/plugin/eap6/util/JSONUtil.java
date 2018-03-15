package com.redhat.plugin.eap6.util;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;

public class JSONUtil {

    private static final String JSON_SCHEMA_FILE = "dict.schema.json";

    private JSONUtil() {
    }

    public static void validateJSON(Object jsonObject) throws IOException, ValidationException {

        InputStream is = null;

        try {

            is = JSONUtil.class.getResourceAsStream(JSON_SCHEMA_FILE);
            JSONObject schemaObj = new JSONObject(new JSONTokener(is));
            Schema schema = SchemaLoader.builder().schemaJson(schemaObj).draftV7Support().build().load().build();

            schema.validate(jsonObject);

        } finally {
            if (is != null) {
                is.close();
            }
        }

    }
}
