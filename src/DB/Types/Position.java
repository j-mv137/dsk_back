package DB.Types;

import Api.Types.ApiError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Position {
    int id;
    String room;
    String key;
    int level;

    Position(Builder b) {
        this.id = b.id;
        this.room = b.room;
        this.key = b.key;
        this.level = b.level;
    }

    public static class Builder {
        private int id;
        private String room;
        private String key;
        private int level;

        public Builder id(int id) {this.id = id; return this;}
        public Builder room(String room) {this.room = room; return this;}
        public Builder key(String key) {this.key = key; return this;}
        public Builder level(int level) {this.level = level; return this;}

        public Position build() {
            return new Position(this);
        }
    }

    public JsonObject toJson() {
        JsonObject posJson = new JsonObject();

        posJson.addProperty("id", this.id);
        posJson.addProperty("room", this.room);
        posJson.addProperty("key", this.key);
        posJson.addProperty("level", this.level);

        return posJson;
    }

    public static Position parsePosFromJson(String positionQuery) throws ApiError{
        try {
            JsonObject positionJson = JsonParser.parseString(positionQuery).getAsJsonObject();

            String positionRoom = positionJson.get("room").getAsString();
            String positionKey = positionJson.get("key").getAsString();
            int positionLvl = positionJson.get("level").getAsInt();

            // I'm going to assume that the position query might not have specified an id
            // for this case we assign it a 0.
            // Useful for addPosToProd method.
            if(!positionJson.has("id")) {
                return new Builder()
                        .id(0)
                        .room(positionRoom)
                        .key(positionKey)
                        .level(positionLvl)
                        .build();
            }

            return new Builder()
                    .id(positionJson.get("id").getAsInt())
                    .room(positionRoom)
                    .key(positionKey)
                    .level(positionLvl)
                    .build();

        } catch (JsonSyntaxException e) {
            throw ApiError.buildMsg("f_parsePosFromJson cls_Position error with JSON syntax", e.getMessage());
        }

    }


    public int getLevel() {
        return level;
    }

    public String getKey() {
        return key;
    }

    public String getRoom() {
        return room;
    }

    public int getId() {
        return id;
    }
}
