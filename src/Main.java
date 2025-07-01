import Api.Types.ApiError;
import DB.ProductsDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static Api.Api.handleQuery;


public class Main {
    public static void main(String[] args) {
//        if (args.length != 1) {
//            ApiError error = ApiError
//                    .buildMsg("Error en la llamada ala API: args = %d. args = 1"
//                                    .formatted(args.length)
//                            , "");
//
//            System.err.println(error.getMessage());
//        }

        try {
            Connection db = connect();
            ProductsDB productsDB = new ProductsDB(db);
            if (args.length < 1) {
                args = new String[1];
                args[0] = "{\"method\": \"getProdsBySearch\", args:[\"\"]}";
            }
            String res = handleQuery(args[0], productsDB);

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
        String connString = "jdbc:postgresql://localhost/aye_dsk?user=jacobo&password=jacobon137";

        try {
            return DriverManager.getConnection(connString);
        } catch (SQLException e) {
            throw ApiError.buildMsg("No se pudo establecer la conexión", e.getMessage());
        }
    }
}