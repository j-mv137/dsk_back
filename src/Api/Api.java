package Api;

import DB.ProductsDB;
import Api.Types.ApiQuery;
import Api.Types.ApiError;
import DB.Types.Product;
import com.google.gson.*;

import java.util.ArrayList;


public class Api {
    public static String handleQuery(String query, ProductsDB prodsDB) {
        try {
            ApiQuery apiQuery = parseQuery(query);

            String apiRes;

            String method = apiQuery.getMethod();

            switch (method) {
                case "getProdsBySearch":
                    String searchQuery = apiQuery.getArgs()[0].toString();

                    Product[] prods = prodsDB.getProducts(searchQuery);
                    JsonArray prodsJson = new JsonArray();

                    for (Product prod : prods) {
                        prodsJson.add(prod.toJson());
                    }

                    apiRes = prodsJson.toString();
                    break;
                default:
                    apiRes = "";
            }

            return apiRes;
        } catch (ApiError e) {
            return e.getMessage();
        }
    }
    private static ApiQuery parseQuery(String query) throws ApiError{
        try {

            JsonObject jsonQuery = JsonParser.parseString(query).getAsJsonObject();

            String method = jsonQuery.get("method").getAsString();

            ArrayList<Object> args = new ArrayList<>();
            JsonArray jsonArgs = jsonQuery.getAsJsonArray("args");

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

            return new ApiQuery(method, argsArr);
        } catch (JsonSyntaxException e) {

            throw ApiError.buildMsg("Error en el formato del Argumento",
                    "Error en f_parseQuery: %s".formatted(e.getMessage()));
        }
    }
}
