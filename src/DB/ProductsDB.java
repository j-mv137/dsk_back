package DB;
import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
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

    String getSimilar(String word) {
        try {
            PreparedStatement st =  this.db.prepareStatement("SELECT word FROM wds_description " +
                    "WHERE similarity(word, ?) > 0.5;");

            st.setString(1, word);

            ResultSet rows = st.executeQuery();

            word += " |";

            while(rows.next()) {
                word = word.concat(rows.getString("word") + " |") ;

                // If it's the last rows don't add " |" at the end
                if(!rows.next()) {
                    rows.previous();
                    word = word.concat(rows.getString("word"));
                }
            }

            return word;
        } catch (SQLException e) {
            System.err.printf("Error: %s", e.getMessage());
            return word;
        }
    }

    public int getProductsByDesc(String query) {
        //
        String parsedQuery = query.replaceAll("\\s+", " | ");

        String allSimWords = "";
        String[] words = query.split("\\s+");


        for(int i = 0; i < words.length; i++) {
            allSimWords = allSimWords.concat(getSimilar(words[i]) + " |");

            if (i == words.length - 1 ) {
                allSimWords = allSimWords.concat(getSimilar(words[i]));
            }
        }

        try {
            PreparedStatement st = this.db.prepareStatement("SELECT * FROM products " +
                                "WHERE to_tsvector(description) @@ to_tsquery(?);");

            st.setString(1, allSimWords);

            return 1;
        } catch (SQLException e) {
            System.exit(1);
            return 0;
        }
    }


}
