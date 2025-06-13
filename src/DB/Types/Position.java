package DB.Types;

public class Position {
    int id;
    String room;
    String space;
    int level;

    Position(Builder b) {
        this.id = b.id;
        this.room = b.room;
        this.space = b.space;
        this.level = b.level;
    }

    public static class Builder {
        private int id;
        private String room;
        private String space;
        private int level;

        public Builder id(int id) {this.id = id; return this;}
        public Builder room(String room) {this.room = room; return this;}
        public Builder space(String space) {this.space = space; return this;}
        public Builder level(int level) {this.level = level; return this;}

        public Position build() {
            return new Position(this);
        }
    }
}
