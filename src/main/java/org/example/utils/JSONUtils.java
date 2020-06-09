package org.example.utils;


import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author lcb
 * @date 2020/4/28
 */
public class JSONUtils {
    public static String bean2Json(Object obj) throws IOException {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
}
