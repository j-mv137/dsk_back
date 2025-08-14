package DB.Types;

import Api.Types.ApiError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.sql.Timestamp;

import static Api.Utils.toTimestamp;

public class Note {
    private  final int id;
    private final String type ;
    private final int num;
    private final Timestamp date;
    private final float total;


    Note(Builder b){
        this.id = b.id;
        this.type = b.type;
        this.num = b.num;
        this.date = b.date;
        this.total = b.total;
    }

    public float getTotal() {
        return total;
    }

    public int getId() {
        return id;
    }

    public int getNum() {
        return num;
    }

    public String getType() {
        return type;
    }

    public Timestamp getDate() {
        return date;
    }

    public static class Builder {
        private  int id;
        private String type ;
        private int num;
        private float total;
        private Timestamp date;

        public Builder id(int id) { this.id = id; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder num(int num) { this.num = num; return this;}
        public Builder total(float total) {this.total = total; return this;}
        public Builder date(Timestamp date) {this.date = date; return this;}

        public Note build() {return new Note(this);}
    }

    public JsonObject toJson() {
        JsonObject jsonNote = new JsonObject();

        jsonNote.addProperty("id", this.getId());
        jsonNote.addProperty("type", this.getType());
        jsonNote.addProperty("num", this.getNum());
        jsonNote.addProperty("total", this.getTotal());

        return jsonNote;
    }

    public static Note parseFromJson(String noteStr) throws ApiError {
        try {
            JsonObject noteJson = JsonParser.parseString(noteStr).getAsJsonObject();
            int noteId;

            Timestamp noteDate = toTimestamp(noteJson.get("date").getAsString());

            if(noteJson.has("id")) {
                noteId = noteJson.get("id").getAsInt();
            } else {
                noteId = 0;
            }

            return new Builder()
                    .id(noteId)
                    .type(noteJson.get("type").getAsString())
                    .num(noteJson.get("num").getAsInt())
                    .date(noteDate)
                    .total(noteJson.get("total").getAsFloat())
                    .build();

        } catch (JsonSyntaxException e) {
            throw ApiError.buildMsg("f_parseFromJson cls_note failed to parse Json syntax", e.getMessage());
        }
    }
}
