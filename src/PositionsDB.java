import java.sql.*;
import java.util.ArrayList;

public class PositionsDB {
    Connection db;

    PositionsDB(Connection db) {
        this.db = db;
        this.CreateTable();
    }

      void CreateTable() {
        try {
            Statement st = this.db.createStatement();
            int err = st.executeUpdate("CREATE TABLE IF NOT EXISTS products_positions (" +
                    "id SERIAL PRIMARY KEY, product_id INT, position_id INT," +
                    "CONSTRAINT unique_prod_pos_pair UNIQUE (product_id, position_id)," +
                    "CONSTRAINT prod_id FOREIGN KEY(product_id)) REFERENCES products(id)" +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "CONSTRAINT pos_id FOREIGN KEY(position_id), REFERENCES positions(id)" +
                    "ON DELETE CASCADE ON UPDATE CASCADE);");

            if(err != 0) {
                System.out.println("Something failed creating the products_positions table.");
                System.exit(err);
            }
            st.close();
        } catch (SQLException e) {
            System.out.printf("Error: %s", e.getMessage());
            System.exit(1);
        }
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
                System.out.println("Salió mal algo combiando la posición del artículo");
                System.exit(err);
            }
        } catch (SQLException e) {
            System.out.printf("Error: %s", e.getMessage());
            System.exit(1);
        }
    }

    void addPosition(int prodID, int posID) {
        try {
            if (this.prodPosPairRep(prodID, posID)) {
                System.out.print("El producto ya tiene la posición asignada");
                return;
            }

            PreparedStatement st = this.db.prepareStatement("INSERT INTO products_positions " +
                    "(products_id, positions_id) VALUES (?, ?);");
            st.setInt(1, prodID);
            st.setInt(1, posID);

            int err = st.executeUpdate();

            if (err != 0) {
                System.out.println("Salió mal algo insertando la nueva posición del artículo");
                System.exit(err);
            }
        } catch (SQLException e) {
            System.out.printf("Error: %s", e.getMessage());
            System.exit(1);
        }
    }

    private boolean prodPosPairRep(int prodID, int posID) {
        try {
            PreparedStatement st = this.db.prepareStatement("SELECT * FROM products_positions " +
                    "WHERE product_id = ? AND position_id = ?;");

            st.setInt(1, prodID);
            st.setInt(2, posID);

            ResultSet rs  = st.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.printf("Error: %s", e);
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

    ArrayList<Product> getProductsByPos(int posID) {
        try {
            PreparedStatement st = this.db.prepareStatement("SELECT pr.id, pr.code, pr.secondary_code," +
                    "pr.description, pr.selling_price, pr.cost, pr.currency, pr.article_count, " +
                    "pr.provider_id, pr.min_quantity FROM products pr " +
                    "INNER JOIN products_positions pp ON pr.id = pp.product_id" +
                    "WHERE pp.position_id = ?;");

            st.setInt(1, posID);

            ResultSet rows = st.executeQuery();

            ArrayList<Product> prods = new ArrayList<>(  );

            while(rows.next()) {
                prods.add(new Product.Builder()
                                .id(rows.getInt("id"))
                                .mainCode(rows.getString("code"))
                                .secondCode(rows.getString("secondary_code"))
                                .description(rows.getString("description"))
                                .sellPrice(rows.getFloat("selling_price"))
                                .cost(rows.getFloat("cost"))
                                .currency(rows.getString("currency"))
                                .artCount(rows.getInt("article_count"))
                                .providerID(rows.getInt("provider_id"))
                                .minQuantity(rows.getInt("min_quantity"))
                        .build());
            }

            return prods;
        } catch(SQLException e) {
            System.out.printf("Error: %s", e.getMessage());
            return null;
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
