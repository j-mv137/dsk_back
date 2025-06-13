package DB.Types;

public class Product {
    int id;
    String mainCode;
    String secondCode;
    String description;
    float sellPrice;
    float cost;
    String currency;
    int artCount;
    int providerID;
    int minQuantity;


    Product(Builder b) {
        this.id = b.id;
        this.mainCode = b.mainCode;
        this.secondCode = b.secondCode;
        this.description = b.description;
        this.sellPrice = b.sellPrice;
        this.cost = b.cost;
        this.currency = b.currency;
        this.artCount =b.artCount;
        this.providerID = b.providerID;
        this.minQuantity = b.minQuantity;
    }

    public static class Builder {
        private int id;
        private String mainCode;
        private String secondCode;
        private String description;
        private float sellPrice;
        private float cost;
        private String currency;
        private int artCount;
        private int providerID;
        private int minQuantity;

        public Builder id(int id) {this.id = id; return this;}
        public Builder mainCode(String mainCode) {this.mainCode = mainCode; return this;}
        public Builder secondCode(String secondCode) {this.secondCode = secondCode; return this;}
        public Builder description(String description) {this.description = description; return this;}
        public Builder sellPrice(float sellPrice) {this.sellPrice = sellPrice; return this;}
        public Builder cost(float cost) {this.cost = cost; return this;}
        public Builder currency(String currency) {this.currency = currency; return this;}
        public Builder artCount(int artCount) {this.artCount = artCount; return this;}
        public Builder providerID(int providerID) {this.providerID = providerID; return this;}
        public Builder minQuantity(int minQuantity) {this.minQuantity = minQuantity; return this;}

        public Product build() {
            return new Product(this);
        }
    }
}
