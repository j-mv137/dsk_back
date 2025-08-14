package Api;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import Api.Types.ApiQuery;
import Api.Types.ApiError;


public class Utils {
    public static Timestamp toTimestamp(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

        return Timestamp.valueOf(dateTime);
    }

    // Only works when args has elements of a unique type. For now targetClass only a number or string
    public static <T> void validateArgs(Object[] args, int targetLength, Class<T> targetClass) throws ApiError {
        if(args.length != targetLength) {
            throw ApiError.buildMsg("f_validateArgs cls_Api length of args should be %s and is %s"
                    .formatted(targetLength, args.length),"");
        }

        // if expected a number the parseQuery func. should've returned "LazilyParsedNumber"
        Class<?> validateClass = (targetClass == int.class || targetClass == float.class)
                ? LazilyParsedNumber.class : targetClass;

        for (Object arg: args) {
            if(arg.getClass() != validateClass) {
                throw ApiError.buildMsg("f_validateArgs cls_Api type of args should've been %s and it's %s"
                                .formatted(validateClass, arg.getClass())
                        ,"");
            }
        }
    }

    public static ApiQuery parseQuery(String query) throws ApiError {
        try {
            JsonObject jsonQuery = JsonParser.parseString(query).getAsJsonObject();

            String direction = jsonQuery.get("direction").getAsString();
            String method = jsonQuery.get("method").getAsString();

            ArrayList<Object> args = new ArrayList<>();
            JsonArray jsonArgs = jsonQuery.getAsJsonArray("args");

            // For every element in the args array identify the type and added to the list
            for(JsonElement el : jsonArgs ) {
                if(!el.isJsonPrimitive()) {
                    args.add(el.getAsString());
                }
                JsonPrimitive e = el.getAsJsonPrimitive();
                if(e.isNumber()) {
                    args.add(e.getAsNumber());
                } else if (e.isString()) {
                    args.add(e.getAsString());
                } else if (e.isBoolean()) {
                    args.add(e.getAsBoolean());
                }
            }
            Object[] argsArr = new Object[args.toArray().length];
            argsArr = args.toArray(argsArr);

            return new ApiQuery(direction, method, argsArr);
        } catch (JsonSyntaxException e) {
            throw ApiError.buildMsg("Error en el formato del argumento",
                    "Error en f_parseQuery: %s".formatted(e.getMessage()));
        }
    }
}
