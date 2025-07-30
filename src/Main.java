import Api.Types.ApiError;
import DB.OrdersDB;
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
            // Throws APIError. For some reason decided was a good idea.
            Connection db = connect();
            String res;

            ProductsDB productsDB = new ProductsDB(db);
            OrdersDB ordersDB = new OrdersDB(db);

            if (args.length != 0) {
                // Handle req. and print to stdout
                res = handleQuery(args[0], productsDB, ordersDB);
            } else {
                String tryQuery ="{\"direction\":\"Orders\",\"method\":\"addOrder\",\"args\":[\"{\\\"date\\\":\\\"15/07/2025, 00:00:00\\\",\\\"noteId\\\":\\\"412\\\",\\\"status\\\":\\\"pendiente\\\",\\\"type\\\":\\\"revisión\\\",\\\"name\\\":\\\"Jacovanana Morales\\\",\\\"description\\\":\\\"Jacobo Morales\\\",\\\"address\\\":\\\"Holas\\\",\\\"phoneNum\\\":\\\"622 143 3432\\\",\\\"id\\\":\\\"413424\\\"}\"]}";
                res = handleQuery(tryQuery, productsDB, ordersDB);
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