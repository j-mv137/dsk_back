package DB.Types;

import com.google.gson.JsonObject;

import java.sql.Timestamp;

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
}
