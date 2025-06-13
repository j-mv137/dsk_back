package DB;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductsDB {
    Connection db;

    public ProductsDB(Connection db) {
        this.db = db;
    }

    public void createWordsTable() {
        try {
            Statement st =  this.db.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS wds_description " +
                    "AS SELECT word FROM " +
                    "ts_stat('SELECT to_tsvector(''simple'', description) FROM products');");
            st.executeUpdate("CREATE INDEX desc_wds_idx ON wds_description USING GIN(word gin_trgm_ops);");

            

        } catch (SQLException e) {
            System.out.printf("Error. %s" , e.getMessage());
            System.exit(1);
        }
    }

    static String parseQuary(String query) {
        return query.replace(" ", " | ");
    }

    public int getProductID(String query) {
        try {
            Statement st = this.db.prepareStatement("SELECT *, wetsearch_to_tsquery('spansih', replace(?, ' ', ' OR '))");
            return 1;
        } catch (SQLException e) {
            System.exit(1);
            return 0;
        }
    }


}
