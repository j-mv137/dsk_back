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
        if (args.length != 1) {
            ApiError error = ApiError
                    .buildMsg("Error en la llamada ala API: args = %d. args = 1".formatted(args.length), "");

            System.err.println(error.getMessage());
        }

        try {
            // Throws APIError. For some reason decided was a good idea.
            Connection db = connect();

            // Temporal solution.
            ProductsDB productsDB = new ProductsDB(db);
            OrdersDB ordersDB = new OrdersDB(db);

            // handle request.
            String res = handleQuery(args[0], productsDB, ordersDB);

            // Print to stdout
            System.out.println(res);

            // Throw
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
        String connString = "jdbc:postgresql://localhost/aye_dsk?user=jacobo&password=jacobon137";

        try {
            return DriverManager.getConnection(connString);
        } catch (SQLException e) {
            throw ApiError.buildMsg("No se pudo establecer la conexión", e.getMessage());
        }
    }
}