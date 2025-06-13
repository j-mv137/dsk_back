import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Connection db = connect();
        PositionsDB positions = new PositionsDB(db);
    }

    static Connection connect() {
        String connString = "jdbc:postgresql://localhost/aye_dsk?user=jacobo&password=jacobon137";

        try {
            return DriverManager.getConnection(connString);
        } catch (SQLException e) {
            System.out.println("No se pudo conectar la base de datos.");
            System.out.printf("Error: %s", e);
            System.exit(1);
        }
        return null;
    }
}