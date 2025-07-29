package DB;

import Api.Types.ApiError;
import DB.Types.Order;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.sql.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrdersDB {
    Connection db;

    public OrdersDB(Connection db) {
        this.db = db;
    }

    private Order parseOrder(String orderStr) throws ApiError {
        try {
            JsonObject orderJson = JsonParser.parseString(orderStr).getAsJsonObject();


            int id = orderJson.get("id").getAsInt();
            int noteId = orderJson.get("noteId").getAsInt();
            String dateStr = orderJson.get("date").getAsString();
            String type = orderJson.get("type").getAsString();
            String name = orderJson.get("name").getAsString();
            String address = orderJson.get("address").getAsString();
            String phone = orderJson.get("phone").getAsString();
            String description = orderJson.get("description").getAsString();
            String status = orderJson.get("status").getAsString();


            Timestamp dateTime = toTimestamp(dateStr);

            return new Order.Builder()
                    .id(id)
                    .noteId(noteId)
                    .date(dateTime)
                    .type(type)
                    .name(name)
                    .address(address)
                    .phone(phone)
                    .description(description)
                    .status(status)
                    .build();

        } catch (JsonSyntaxException e) {
            throw ApiError.buildMsg("No se pudo convertir el formato dado a un obj. JSON",
                    e.getMessage());
        }
    }

    public void addOrder(String query) throws ApiError{
        try {
            Order order = parseOrder(query);

            PreparedStatement st = this.db.prepareStatement("INSERT INTO orders " +
                    "(id, date, type, name, address, phone, description, status, note_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            st.setInt(1, order.getId());
            st.setTimestamp(2, order.getDate());
            st.setString(3, order.getType());
            st.setString(4, order.getName());
            st.setString(5, order.getAddress());
            st.setString(6, order.getPhone());
            st.setString(7, order.getDescription());
            st.setString(8, order.getStatus());
            st.setInt(9, order.getNoteId());

            st.executeQuery();
        } catch (SQLException e) {
            throw ApiError.buildMsg("Error al insertar orden", e.getMessage());
        }
    }

    public Order[] getOrdersByDate(Timestamp initialDate, Timestamp finalDate) throws ApiError{
        if (initialDate.compareTo(finalDate) > 0) {
            return new Order[0];
        }

        List<Order> ordersList = new ArrayList<>();

        try {

            PreparedStatement st = this.db.prepareStatement("SELECT * FROM orders or " +
                    "WHERE or.timestamp >= ? and or.timestamp <= ?;");

            st.setTimestamp(1, initialDate);
            st.setTimestamp(2, finalDate);

            ResultSet rows =  st.executeQuery();

            while(rows.next()) {
                ordersList.add(new Order.Builder()
                        .id(rows.getInt("id"))
                        .noteId(rows.getInt("note_id"))
                        .date(rows.getTimestamp("date"))
                        .name(rows.getString("name"))
                        .type(rows.getString("type"))
                        .address(rows.getString("address"))
                        .phone(rows.getString("phone"))
                        .description(rows.getString("description"))
                        .status(rows.getString("status"))
                        .build());
            }

            return ordersList.toArray(new Order[0]);

        } catch (SQLException e) {
            throw ApiError.buildMsg("Error al filtrar por fecha", e.getMessage());
        }
    }

    public static Timestamp toTimestamp(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

        return Timestamp.valueOf(dateTime);
    }
}
