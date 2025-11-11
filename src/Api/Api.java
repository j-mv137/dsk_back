package Api;

import DB.NotesDB;
import DB.OrdersDB;
import DB.PositionsDB;
import DB.ProductsDB;
import Api.Types.ApiQuery;
import Api.Types.ApiError;
import DB.Types.Note;
import DB.Types.Order;
import DB.Types.Position;
import DB.Types.Product;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;

import java.sql.Timestamp;

import static Api.Utils.*;


// TODO: 1. change the form of the args attribute to contain a map

/*
* Receives the request (query) in JSON formatted string with the form
* {
*   "direction": "Prods" | "Orders" | "etc",
*   "method": "nameOfTheMethod",
*   "args": Object[]
* }
*
* Sure.
* */
public class Api {
    public static String handleQuery(String query, ProductsDB productsDB, OrdersDB ordersDB, PositionsDB positionsDB, NotesDB notesDB) throws  ApiError{
        try {
            ApiQuery apiQuery = parseQuery(query);


            String direction = apiQuery.getDirection();

            return switch (direction) {
                case "Products" -> handleProdsQuery(apiQuery, productsDB);
                case "Orders" -> handleOrdersQuery(apiQuery, ordersDB);
                case "Positions" -> handlePositionsQuery(apiQuery, positionsDB);
                case "Notes" -> handleNotesQuery(apiQuery, notesDB);
                default -> "";
            };

        } catch (ApiError e) {
            throw e;
        }
    }

    private static String handleProdsQuery(ApiQuery apiQuery, ProductsDB prodsDB) throws ApiError{
        String apiRes;

        String method = apiQuery.getMethod();

        try {
            switch (method) {
                case "getProdsBySearch":
                    Object[] searchQueryObj = apiQuery.getArgs();
                    validateArgs(searchQueryObj, 1, String.class);

                    String searchQuery = (String) searchQueryObj[0];

                    Product[] prods = prodsDB.getProducts(searchQuery);
                    JsonArray prodsJson = new JsonArray();

                    for (Product prod : prods) {
                        prodsJson.add(prod.toJson());
                    }

                    apiRes = prodsJson.toString();
                    break;
                default:
                    throw ApiError.buildMsg("f_handleProdsQuery cls_api unknown method", "");
            }
            return apiRes;
        } catch (ApiError e) {
            throw e;
        }
    }

    private static String handleOrdersQuery(ApiQuery apiQuery, OrdersDB ordersDB) throws ApiError{
        String apiRes;

        String method = apiQuery.getMethod();
        try {
            switch (method) {
                case "addOrder":
                    // only one arg, a JSON formatted string that has the object of the new order
                    //TODO: change the func. to receive and actual Order object
                    Object[] ordersQueryObj = apiQuery.getArgs();
                    validateArgs(ordersQueryObj, 1, String.class);

                    String ordersQuery = (String) ordersQueryObj[0];

                    ordersDB.addOrder(ordersQuery);

                    apiRes = "0";
                    break;

                case "getOrdersByDate":
                    // args: init. and final datetime
                    Object[] ordersDatesObj =  apiQuery.getArgs();
                    validateArgs(ordersDatesObj, 2, String.class);

                    String initDateStr = (String) ordersDatesObj[0];
                    String finalDateStr = (String) ordersDatesObj[1];

                    Timestamp initialDate = toTimestamp(initDateStr);
                    Timestamp finalDate = toTimestamp(finalDateStr);

                    Order[] orders = ordersDB.getOrdersByDate(initialDate, finalDate);

                    JsonArray ordersJson = new JsonArray();

                    for (Order order : orders) {
                        ordersJson.add(order.toJson());
                    }

                    apiRes = ordersJson.toString();

                    break;
                default:
                    throw ApiError.buildMsg("f_handleOrdersQuery cls_api unknown method", "");
            }

            return apiRes;
        } catch (ApiError e) {
            throw e;
        }
    }

    private static String handlePositionsQuery(ApiQuery apiQuery, PositionsDB positionsDB) throws ApiError{
        String method = apiQuery.getMethod();
        try {
            String apiRes;

            switch (method) {
                case "getProdsByRack":
                    // args: key, room
                    Object[] posCredObj =  apiQuery.getArgs();
                    validateArgs(posCredObj, 2, String.class);

                    String key = (String) posCredObj[0];
                    String room = (String) posCredObj[1];

                    Product[] prods = positionsDB.getProdsByRack(key, room);

                    JsonArray prodsJson = new JsonArray();

                    for(Product prod : prods) {
                        prodsJson.add(prod.toJson());
                    }

                    apiRes = prodsJson.toString();
                    break;

                case "addPosToProd":
                    // args: prodId, positionJson (JSON formatted position object)
                    Object[] posProdCredObj = apiQuery.getArgs();

                    int prodId = ((LazilyParsedNumber) posProdCredObj[0]).intValue();
                    String posJson = (String) posProdCredObj[1];

                    positionsDB.addPosToProd(prodId, posJson);
                    apiRes = "";
                    break;

                case "getPosForProd":
                    // args: prodID
                    Object[] prodIDObj = apiQuery.getArgs();
                    validateArgs(prodIDObj, 1, int.class);

                    int prodID = ((LazilyParsedNumber) prodIDObj[0]).intValue();

                    Position[] positions = positionsDB.getPosForProd(prodID);

                    JsonArray positionsJson = new JsonArray();

                    for(Position pos : positions) {
                        positionsJson.add(pos.toJson());
                    }

                    apiRes = positionsJson.toString();
                    break;

                case "getPosLevels":
                    // args: key, room
                    Object[] posCredArgs = apiQuery.getArgs();
                    validateArgs(posCredArgs, 2, String.class);

                    // Stupid to have them put in an array. should've been a map
                    String keyGPL = (String) posCredArgs[0];
                    String roomGPL = (String) posCredArgs[1];

                    Integer[] posLevels = positionsDB.getPosLevels(keyGPL, roomGPL);

                    JsonArray posLevelsJson = new JsonArray();

                    for(int posLvl : posLevels) {
                        posLevelsJson.add(posLvl);
                    }

                    apiRes = posLevelsJson.toString();
                    break;

                default:
                    throw ApiError.buildMsg("f_handlePositionsQuery cls_api unknown method", "");
            }
            return apiRes;
        } catch (ApiError e) {
            throw e;
        }
    }

    public static String handleNotesQuery(ApiQuery apiQuery, NotesDB notesDB) throws ApiError {
        String method = apiQuery.getMethod();
        try {
            String apiRes;

            switch (method) {
                case "getNotesByDate":
                    // args: initDate, finalDate
                    Object[] datesObj = apiQuery.getArgs();
                    validateArgs(datesObj, 2, String.class);

                    String initStr = (String) datesObj[0];
                    String finalStr = (String) datesObj[1];

                    Timestamp initDate = toTimestamp(initStr);
                    Timestamp finalDate = toTimestamp(finalStr);

                    Note[] notes = notesDB.getNotesByDate(initDate, finalDate);

                    JsonArray notesJson = new JsonArray();

                    for (Note note : notes) {
                        notesJson.add(note.toJson());
                    }

                    apiRes = notesJson.toString();
                    break;

                case "getProdsInNote":
                    // args: String type, int num
                    Object[] noteCreds = apiQuery.getArgs();

                    // NOTE: no validating args for now
                    String noteType = (String) noteCreds[0];
                    int noteNum = ((LazilyParsedNumber) noteCreds[1]).intValue();

                    Product[] prods = notesDB.getProdsInNote(noteType, noteNum);

                    JsonArray prodsJson = new JsonArray();

                    for (Product prod : prods) {
                        prodsJson.add(prod.toJson());
                    }

                    apiRes = prodsJson.toString();
                    break;

                case "addNote":
                    // args: String note, String prods
                    // prods is a json formated string with the prods obj.
                    // same with note
                    Object[] prodsNNoteObj = apiQuery.getArgs();
                    validateArgs(prodsNNoteObj, 2, String.class);

                    String noteJsonStr = (String) prodsNNoteObj[0];
                    String prodsJsonStr = (String) prodsNNoteObj[1];

                    Product[] prodsToNote = Product.parseProdsJsonArr(prodsJsonStr);
                    Note note = Note.parseFromJson(noteJsonStr);

                    notesDB.addNote(note, prodsToNote);

                    apiRes = "";
                    break;

                default:
                    throw ApiError.buildMsg("f_handleNotesQuery cls_api unknown method", "");
            }
            return apiRes;
        } catch (ApiError e) {
            throw e;
        }

    }
}
