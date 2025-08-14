package DB.Types;

import Api.Types;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.sql.Timestamp;

import static Api.Utils.toTimestamp;

public class Order {
    private final int id;
    private final Timestamp date;
    private final String type;
    private final String name;
    private final String address;
    private final String phone;
    private final String description;
    private final String noteId;
    private final String status;


    Order(Builder b) {
        this.id = b.id;
        this.noteId = b.noteId;
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
        jsonOrder.addProperty("noteId", this.noteId);
        jsonOrder.addProperty("date", this.date.toString());
        jsonOrder.addProperty("type", this.type);
        jsonOrder.addProperty("name", this.name);
        jsonOrder.addProperty("address", this.address);
        jsonOrder.addProperty("status", this.status);
        jsonOrder.addProperty("phone", this.phone);
        jsonOrder.addProperty("description", this.description);

        return jsonOrder;
    }

    public static Order parseOrderFromJson(String orderStr) throws Types.ApiError {
        try {
            JsonObject orderJson = JsonParser.parseString(orderStr).getAsJsonObject();


            int id = orderJson.get("id").getAsInt();
            String noteId = orderJson.get("noteId").getAsString();
            String dateStr = orderJson.get("date").getAsString();
            String type = orderJson.get("type").getAsString();
            String name = orderJson.get("name").getAsString();
            String address = orderJson.get("address").getAsString();
            String phone = orderJson.get("phoneNum").getAsString();
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
            throw Types.ApiError.buildMsg("No se pudo convertir el formato dado a un obj. JSON",
                    e.getMessage());
        }
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

    public String getNoteId() {
        return noteId;
    }

    public static class Builder {
        int id;
        String noteId;
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

        public Builder noteId(String noteId) {
            this.noteId = noteId;
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
