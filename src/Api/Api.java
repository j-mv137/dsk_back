package Api;

import DB.OrdersDB;
import DB.ProductsDB;
import Api.Types.ApiQuery;
import Api.Types.ApiError;
import DB.Types.Order;
import DB.Types.Product;

import com.google.gson.*;

import java.sql.Timestamp;
import java.util.ArrayList;


public class Api {
    public static String handleQuery(String query, ProductsDB prodsDB, OrdersDB ordersDB) {
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

                    apiRes = prodsJson.getAsString();
                    break;


                case "addOrder":
                    // only one argument (a json formatted string of an order object)
                    // no response

                    String ordersQuery = apiQuery.getArgs()[0].toString();
                    ordersDB.addOrder(ordersQuery);

                    apiRes = "";
                    break;

                case "getOrdersbyDate":

                    // two args init. and final datetime, check if the args are a string array
                    Object[] getOrdersQuery = apiQuery.getArgs();
                    if (getOrdersQuery.getClass() != String[].class) {
                        // I think it will be caught within this func and after that everything goes smooth.
                        throw ApiError.buildMsg("Error: argumentos inválidos para la función getOrdersByDate", "");
                    };

                    Timestamp initialDate = OrdersDB.toTimestamp(getOrdersQuery[0].toString());
                    Timestamp finalDate = OrdersDB.toTimestamp(getOrdersQuery[1].toString());

                    Order[] orders = ordersDB.getOrdersByDate(initialDate, finalDate);

                    JsonArray ordersJson = new JsonArray();

                    for (Order order : orders) {
                        ordersJson.add(order.toJson());
                    }

                    apiRes = ordersJson.getAsString();
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
