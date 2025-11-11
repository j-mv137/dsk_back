package DB;

import Api.Types.ApiError;
import DB.Types.Note;
import DB.Types.Product;

import java.sql.*;
import java.util.ArrayList;


public class NotesDB {
    Connection db;

    public NotesDB(Connection db) throws ApiError {
        this.db = db;

        // TEMPORAL
        try {
            PreparedStatement st = db.prepareStatement("CREATE TABLE IF NOT EXISTS notes (" +
                    "id SERIAL PRIMARY KEY, type VARCHAR, num INTEGER, total REAL, " +
                    "CONSTRAINT unique_note_type_num UNIQUE (type, num)" +
                    ");");

            st.executeUpdate();

            PreparedStatement st2 = db.prepareStatement("CREATE TABLE IF NOT EXISTS notes_products (" +
                    "id SERIAL PRIMARY KEY, note_id INTEGER, product_id INTEGER, " +
                    "CONSTRAINT unique_note_prod UNIQUE (note_id, product_id)," +
                    "CONSTRAINT FK_note_id FOREIGN KEY(note_id) REFERENCES notes(id) " +
                    "ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "CONSTRAINT FK_product_id FOREIGN KEY(product_id) REFERENCES products(id) " +
                    "ON UPDATE CASCADE ON DELETE CASCADE);");

            st2.executeUpdate();

        } catch (SQLException e) {
            throw ApiError.buildMsg("f_constructor cls_notesDB failed to exec. SQL", e.getMessage());
        }
    }


    public Note[] getNotesByDate(Timestamp initDate, Timestamp finalDate) throws ApiError {
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

    public void addNote(Note noteCred, Product[] prods) throws ApiError {
        try {
            PreparedStatement st = this.db.prepareStatement("INSERT INTO notes (type, num, date, total) " +
                    "VALUES (?, ?, ?, ?);");

            st.setString(1, noteCred.getType());
            st.setInt(2, noteCred.getNum());
            st.setTimestamp(3, noteCred.getDate());
            st.setFloat(4, noteCred.getTotal());

            st.executeUpdate();

            // elite. get the id of the note we just added
            PreparedStatement st2 = this.db.prepareStatement("SELECT id FROM notes ORDER BY id DESC LIMIT 1;");
            ResultSet row = st2.executeQuery();

            row.next();
            int noteId = row.getInt("id");

            addProdsToNote(prods, noteId);

        } catch (SQLException e) {
            throw ApiError.buildMsg("f_addNote cls_notesDB failed to exec. SQL query", e.getMessage());
        }
    }

    private void addProdsToNote(Product[] prods, int noteID) throws ApiError {
        try {
            PreparedStatement st = this.db.prepareStatement("INSERT INTO note_product (note_id, product_id) " +
                    "VALUES (?, ?);");

            for (Product prod : prods) {
                st.setInt(1, noteID);
                st.setInt(2, prod.getId());

                st.executeUpdate();
            }

        } catch (SQLException e) {
            throw ApiError.buildMsg("f_addProdsToNote cls_notesDB failed to exec. SQL", e.getMessage());
        }

    }

}