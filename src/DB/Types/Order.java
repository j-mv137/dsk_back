package DB.Types;

import com.google.gson.JsonObject;

import java.sql.Timestamp;

public class Order {
    private final int id;
    private final Timestamp date;
    private final String type;
    private final String name;
    private final String address;
    private final String phone;
    private final String description;
    private final String status;


    Order(Builder b) {
        this.id = b.id;
        this.date = b.date;
        this.type = b.type;
        this.name = b.name;
        this.address = b.address;
        this.phone = b.phone;
        this.description = b.description;
        this.status = b.status;
    }

    public JsonObject toJson() {
        JsonObject jsonOrder = new JsonObject();

        jsonOrder.addProperty("id", this.id);
        jsonOrder.addProperty("timestamp", this.date.toString());
        jsonOrder.addProperty("type", this.type);
        jsonOrder.addProperty("name", this.name);
        jsonOrder.addProperty("address", this.address);
        jsonOrder.addProperty("phone", this.phone);
        jsonOrder.addProperty("description", this.description);

        return jsonOrder;
    }

    public int getId() {
        return this.id;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public static class Builder {
        int id;
        Timestamp date;
        String type;
        String name;
        String address;
        String phone;
        String description;
        String status;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder date(Timestamp date) {
            this.date = date;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }


        public Order build() {return new Order(this);}
    }
}
