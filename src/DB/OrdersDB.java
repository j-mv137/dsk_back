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


    public void addOrder(String query) throws ApiError{
        try {
            Order order = Order.parseOrderFromJson(query);

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
            st.setString(9, order.getNoteId());

            st.executeUpdate();
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

            PreparedStatement st = this.db.prepareStatement("SELECT * FROM orders" +
                    " WHERE date >= ? AND date <= ?;");

            st.setTimestamp(1, initialDate);
            st.setTimestamp(2, finalDate);

            ResultSet rows =  st.executeQuery();

            while(rows.next()) {
                Order order = new Order.Builder()
                        .id(rows.getInt("id"))
                        .noteId(rows.getString("note_id"))
                        .date(rows.getTimestamp("date"))
                        .name(rows.getString("name"))
                        .type(rows.getString("type"))
                        .address(rows.getString("address"))
                        .phone(rows.getString("phone"))
                        .description(rows.getString("description"))
                        .status(rows.getString("status"))
                        .build();

                ordersList.add(order);
            }

            return ordersList.toArray(new Order[0]);

        } catch (SQLException e) {
            throw ApiError.buildMsg("Error al filtrar por fecha", e.getMessage());
        }
    }
}
