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


/*
* Receives the request (query) in tbe form of a JSON with the form
* {
*   "direction": "Prods" | "Orders" | "etc",
*   "method": "nameOfTheMethod",
*   "args": Object[]
* }
*
* Sure.
* */
public class Api {
    public static String handleQuery(String query, ProductsDB productsDB, OrdersDB ordersDB) {
        try {
            ApiQuery apiQuery = parseQuery(query);

            String apiRes;

            String direction = apiQuery.getDirection();

            apiRes = switch (direction) {
                case "Products" -> handleProdsQuery(apiQuery, productsDB);
                case "Orders" -> handleOrdersQuery(apiQuery, ordersDB);
                default -> "";
            };

            return apiRes;
        } catch (ApiError e) {
            return e.getMessage();
        }
    }

    private static String handleProdsQuery(ApiQuery apiQuery, ProductsDB prodsDB) {
        String apiRes;

        String method = apiQuery.getMethod();

        try {
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
                default:
                    apiRes="";
            }
            return apiRes;
        } catch (ApiError e) {
            return e.getMessage();
        }
    }

    private static String handleOrdersQuery(ApiQuery apiQuery, OrdersDB ordersDB) {
        String apiRes;

        String method = apiQuery.getMethod();
        try {
            switch (method) {
                case "addOrder":
                    // Expecting a JSON formatted string in args.
                    // this JSON should contain the structure of a row in the Orders DB.
                    String ordersQuery = apiQuery.getArgs()[0].toString();
                    ordersDB.addOrder(ordersQuery);

                    apiRes = "";
                    break;

                case "getOrdersbyDate":

                    // two args init. and final datetime, check if the args are a string array
                    Object[] getOrdersQuery = apiQuery.getArgs();
                    if (getOrdersQuery.getClass() != String[].class) {
                        // I think it will be caught within this func and after that everything goes smooth.
                        throw ApiError
                                .buildMsg("Error: argumentos inválidos para la función getOrdersByDate"
                                        , "");
                    }
                    ;

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
            throw ApiError.buildMsg("Error en el formato del Argumento",
                    "Error en f_parseQuery: %s".formatted(e.getMessage()));
        }
    }
}
