import Api.Types.ApiError;
import DB.NotesDB;
import DB.OrdersDB;
import DB.PositionsDB;
import DB.ProductsDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static Api.Api.handleQuery;


public class Main {
    public static void main(String[] args) {
        // There must only be one arg. cont. the req.
//        if (args.length != 1) {
//            ApiError error = ApiError
//                    .buildMsg("Error en la llamada a la API: args = %d. args = 1".formatted(args.length), "");
//
//            System.err.println(error.getMessage());
//            return;
//        }


        try {
            Connection db = connect();
            String res;

            ProductsDB productsDB = new ProductsDB(db);
            OrdersDB ordersDB = new OrdersDB(db);
            PositionsDB positionsDB = new PositionsDB(db);
            NotesDB notesDB = new NotesDB(db);

            if (args.length == 1) {
                res = handleQuery(args[0], productsDB, ordersDB, positionsDB, notesDB);
            } else {
                String tempQuery = "{\"direction\":\"Products\",\"method\":\"getProdsBySearch\",\"args\":[\"bomba dab \"]}";
                res = handleQuery(tempQuery, productsDB, ordersDB, positionsDB, notesDB);
            }

            System.out.println(res);
            db.close();
        } catch (ApiError e) {
            System.err.println(e.getMessage());
        } catch (SQLException e) {
            String error = ApiError.buildMsg("No se pudo cerrar el servidor",
                    e.getMessage()).getMessage();
            System.err.println(error);
        }

    }


    static Connection connect() throws ApiError{
        String connString = "jdbc:postgresql://localhost/aye?user=jacobo";

        try {
            return DriverManager.getConnection(connString);
        } catch (SQLException e) {
            throw ApiError.buildMsg("No se pudo establecer la conexión", e.getMessage());
        }
    }
}