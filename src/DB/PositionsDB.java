package DB;

import Api.Types.ApiError;
import DB.Types.Position;
import DB.Types.Product;
import DB.Types.ProductPosition;

import java.sql.*;
import java.util.ArrayList;

public class PositionsDB {
    Connection db;

    public PositionsDB(Connection db) {
        this.db = db;
    }

    void updatePositions(int prodID, int oldPosID, int newPosID) {
        try{
            PreparedStatement st = this.db.prepareStatement("UPDATE products_positions SET position_id=?;" +
                    "WHERE product_id=? AND positions_id=?;");
            st.setInt(1, newPosID);
            st.setInt(2, prodID);
            st.setInt(3, oldPosID);

            int err = st.executeUpdate();

            if (err != 0) {
                System.out.println("Algo salió mal combiando la posición del artículo");
                System.exit(err);
            }
        } catch (SQLException e) {
            System.out.printf("Error: %s", e.getMessage());
            System.exit(1);
        }
    }

    public void addPosition(int prodID, int posID) throws ApiError {
        try {
            if (this.repeated(prodID, posID)) {
                throw ApiError.buildMsg("f_addPosition cls_posDB the prod-pos relation already exists"
                        , "");
            }

            PreparedStatement st = this.db.prepareStatement("INSERT INTO products_positions " +
                    "(products_id, position_id) VALUES (?, ?);");
            st.setInt(1, prodID);
            st.setInt(2, posID);

            st.executeUpdate();
        } catch (SQLException e) {
               throw ApiError.buildMsg("f_addPosition cls_posDB error in the exec of th sql query", "");
        }
    }

    private boolean repeated(int prodID, int posID) {
        try {
            PreparedStatement st = this.db.prepareStatement("SELECT * FROM products_positions " +
                    "WHERE product_id = ? AND position_id = ?;");

            st.setInt(1, prodID);
            st.setInt(2, posID);

            ResultSet rs  = st.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.printf("Error: positions_repeated %s", e);
            return false;
        }
    }

    ProductPosition deletePosition(int prodID, int posID) {
        try {
            PreparedStatement st = this.db.prepareStatement("DELETE FROM products_positions " +
                    "WHERE product_id = ? AND position_id = ? RETURNING *;");
            st.setInt(1, prodID);
            st.setInt(2, posID);

            ResultSet row = st.executeQuery();

            return new ProductPosition(
                    row.getInt("product_id"),
                    row.getInt("position_id"));
        } catch (SQLException e) {
            System.out.printf("Error: %s", e.getMessage());
           return null;
        }
    }

    public  Product[] getProdsByRack(String key, String room) throws ApiError{
        try {

            PreparedStatement st = this.db.prepareStatement("SELECT pr.id, pr.main_code, pr.second_code," +
                    "pr.description, pr.sell_price, pr.cost, pr.currency, pr.art_num, " +
                    "pr.min_quantity FROM products pr " +
                    "INNER JOIN products_positions pp ON pr.id = pp.product_id " +
                    "INNER JOIN positions pos ON pp.position_id = pos.id " +
                    "WHERE pos.room = ? AND pos.key = ?;");

            st.setString(1, room);
            st.setString(2, key);


            ResultSet rows = st.executeQuery();

            ArrayList<Product> prods = new ArrayList<>(  );

            while(rows.next()) {
                prods.add(new Product.Builder()
                        .id(rows.getInt("id"))
                        .mainCode(rows.getString("main_code"))
                        .secondCode(rows.getString("secondary_code"))
                        .description(rows.getString("description"))
                        .sellPrice(rows.getDouble("selling_price"))
                        .cost(rows.getDouble("cost"))
                        .currency(rows.getString("currency"))
                        .artNum(rows.getInt("article_count"))
                        .minQuantity(rows.getInt("min_quantity"))
                        .build());
            }

            return prods.toArray(new Product[0]);
        } catch(SQLException e) {
            // did this for some reason
            throw ApiError.buildMsg("Error: f_getProdsByRack cls_posDB problem with sql query", e.getMessage());
        }
    }

    private int getPositionsId(String key, String room) throws ApiError{
        try {
            PreparedStatement st = this.db.prepareStatement("SELECT id FROM positions WHERE key = ? AND room = ?");

            ResultSet rows = st.executeQuery();

            rows.next();
            if(!rows.isLast()) {
                throw ApiError.buildMsg("Error: f_getPositionsId cls_posDB query returned more than one position id"
                        , "");
            };

            return rows.getInt("id");

        } catch(SQLException e){
            throw ApiError.buildMsg("Error: f_getPositionsId cls_posDB incorrect sql query", e.getMessage());
        }
    }

    ArrayList<Position> getPositionByProd(int prodID) {
        try {
            PreparedStatement st = this.db.prepareStatement("SELECT pos.id, pos.room, pos.space, pos.level " +
                    "FROM positions pos INNER JOIN products_positions pp ON pos.id = pp.position_id" +
                    "WHERE pp.product_id = ?;");

            st.setInt(1, prodID);

            ResultSet rows = st.executeQuery();

            ArrayList<Position> positions = new ArrayList<>();

            while(rows.next()) {
                positions.add(new Position.Builder()
                                .id(rows.getInt("id"))
                                .room(rows.getString("room"))
                                .space(rows.getString("space"))
                                .level(rows.getInt("level"))
                        .build());
            }

            return positions;

        } catch (SQLException e) {
            System.out.printf("Error: %s", e.getMessage());
            return null;
        }
    }
}
