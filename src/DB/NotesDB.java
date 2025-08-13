package DB;

import Api.Types.ApiError;
import DB.Types.Note;
import DB.Types.Product;

import java.sql.*;
import java.util.ArrayList;


public class NotesDB {
    Connection db;

    NotesDB(Connection db) throws ApiError {
        this.db = db;

        // TEMPORAL
        try {
            PreparedStatement st = db.prepareStatement("CREATE TABLE IF NOT EXISTS notes (" +
                    "id SERIAL PRIMARY KEY, type VARCHAR, num INTEGER, total REAL);");

            st.executeUpdate();

            PreparedStatement st2 = db.prepareStatement("CREATE TABLE IF NOT EXISTS notes_products (" +
                    "id SERIAL PRIMARY KEY, note_id INTEGER, product_id INTEGER" +
                    "CONSTRAINT unique_note_prod UNIQUE (note_id, product_id)," +
                    "CONSTRAINT FK_note_id FOREIGN KEY(note_id) REFERENCES notes(id) " +
                    "ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "CONSTRAINT FK_product_id FOREIGN KEY(product_id) REFERENCES products(id)) " +
                    "ON UPDATE CASCADE ON DELETE CASCADE;");

            st2.executeUpdate();

        } catch (SQLException e) {
            throw ApiError.buildMsg("f_constructor cls_notesDB failed to exec. SQL", e.getMessage());
        }
    }


    public Note[] getNotesbyDate(Timestamp initDate, Timestamp finalDate) throws ApiError {
        ArrayList<Note> notesList = new ArrayList<>();
        try {
            PreparedStatement st = this.db.prepareStatement("SELECT * FROM notes WHERE date > ? AND date < ?;");

            st.setTimestamp(1, initDate);
            st.setTimestamp(2, finalDate);

            ResultSet rows = st.executeQuery();

            while (rows.next()) {
                notesList.add(new Note.Builder()
                        .id(rows.getInt("id"))
                        .type(rows.getString("type"))
                        .num(rows.getInt("num"))
                        .date(rows.getTimestamp("date"))
                        .total(rows.getFloat("total"))
                        .build());
            }

            return notesList.toArray(new Note[0]);
        } catch (SQLException e) {
            throw ApiError.buildMsg("f_getNotesByDate cls_notesDB failed to exec. SQL", e.getMessage());
        }
    }

    public Product[] getProdsInNote(String type, int num) throws ApiError {
        ArrayList<Product> productsList = new ArrayList<>();

        try {
            PreparedStatement st = this.db.prepareStatement("SELECT pr.* FROM products pr " +
                    "INNER JOIN note_product np ON np.product_id = pr.id " +
                    "INNER JOIN notes nt ON np.note_id = nt.id" +
                    "WHERE nt.type = ? AND nt.num = ?;");

            st.setString(1, type);
            st.setInt(2, num);

            ResultSet rows = st.executeQuery();

            while (rows.next()) {
                productsList.add(new Product.Builder()
                        .mainCode(rows.getString("main_code"))
                        .secondCode(rows.getString("second_code"))
                        .description(rows.getString("description"))
                        .department(rows.getString("department"))
                        .category(rows.getString("category"))
                        .sellPrice(rows.getFloat("sell_price"))
                        .cost(rows.getFloat("cost"))
                        .currency(rows.getString("currency"))
                        .artNum(rows.getInt("art_num"))
                        .minQuantity(rows.getInt("min_quantity"))
                        .build());
            }

            return productsList.toArray(new Product[0]);

        } catch (SQLException e) {
            throw ApiError.buildMsg("f_getProdsInNote cls_notesDB failed to exec. SQL", e.getMessage());
        }

    }

    public void addProdsToNote(Product[] prods, int noteID) throws ApiError {
        try {
            PreparedStatement st = this.db.prepareStatement("INSERT INTO note_product (note_id, product_id) " +
                    "VALUES (?, ?)");


            for (Product prod : prods) {
                st.setInt(1, noteID);
                st.setInt(2, );
            }

        } catch (SQLException e) {
            throw ApiError.buildMsg("f_addProdsToNote cls_notesDB failed to exec. SQL", e.getMessage());
        }

        }
}