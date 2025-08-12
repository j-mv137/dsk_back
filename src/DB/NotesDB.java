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
                    "id SERIAL PRIMARY KEY, note_id INTEGER, product_id INTEGER);");

            st2.executeUpdate();

        } catch (SQLException e) {
            throw ApiError.buildMsg("f_constructor cls_notesDB failed to exec. SQL", e.getMessage());
        }
    }


    public Note[] getNotesbyDate(Timestamp initDate, Timestamp finalDate) throws ApiError{
        ArrayList<Note> notesList = new ArrayList<>();
        try {
            PreparedStatement st = this.db.prepareStatement("SELECT * FROM notes WHERE date > ? AND date < ?;");

            st.setTimestamp(1, initDate);
            st.setTimestamp(2, finalDate);

            ResultSet rows = st.executeQuery();

            while(rows.next()) {
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
            throw  ApiError.buildMsg("f_getNotesByDate cls_notesDB failed to exec. SQL", e.getMessage());
        }
    }

    public Product[] getProdsInNote(String type, int num) throws ApiError {
        ArrayList<Product> productsList = new ArrayList<>();

        try {
            PreparedStatement st = this.db.prepareStatement("SELECT * FROM products ");


        } catch (SQLException e) {
            throw ApiError.buildMsg("f_getProdsInNote cls_notesDB failed to exec. SQL", e.getMessage());
        }

    }

}
