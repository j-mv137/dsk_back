package DB.Types;

import com.google.gson.JsonObject;

public class Product {
    int id;
    String mainCode;
    String secondCode;
    String description;
    String department;
    String category;
    double sellPrice;
    double cost;
    String currency;
    int artNum;
    int minQuantity;

    Product(Builder b) {
        this.id = b.id;
        this.mainCode = b.mainCode;
        this.secondCode = b.secondCode;
        this.description = b.description;
        this.department = b.department;
        this.category = b.category;
        this.sellPrice = b.sellPrice;
        this.cost = b.cost;
        this.currency = b.currency;
        this.artNum = b.artNum;
        this.minQuantity = b.minQuantity;
    }

    public String getMainCode() {
        return this.mainCode;
    }

    public String getSecondCode() {
        return this.secondCode;
    }

    public String getDescription() {
        return description;
    }

    public String getDepartment() {
        return department;
    }

    public String getCategory() {
        return category;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getCost() {
        return cost;
    }

    public String getCurrency() {
        return currency;
    }

    public int getArtCount() {
        return artNum;
    }

    public int getMinQuantity() {
        return minQuantity;
    }


    public static class Builder {
        private int id;
        private String mainCode;
        private String secondCode;
        private String description;
        private String department;
        private String category;
        private double sellPrice;
        private double cost;
        private String currency;
        private int artNum;
        private int minQuantity;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder mainCode(String mainCode) {
            this.mainCode = mainCode;
            return this;
        }

        public Builder secondCode(String secondCode) {
            this.secondCode = secondCode;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder sellPrice(double sellPrice) {
            this.sellPrice = sellPrice;
            return this;
        }

        public Builder cost(double cost) {
            this.cost = cost;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder artNum(int artNum) {
            this.artNum = artNum;
            return this;
        }

        public Builder minQuantity(int minQuantity) {
            this.minQuantity = minQuantity;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }


    public JsonObject toJson() {
        JsonObject jsonProd = new JsonObject();

        jsonProd.addProperty("id", this.id);
        jsonProd.addProperty("code", this.mainCode);
        jsonProd.addProperty("secondCode", this.secondCode);
        jsonProd.addProperty("description", this.description);
        jsonProd.addProperty("department", this.department);
        jsonProd.addProperty("category", this.category);
        jsonProd.addProperty("sellPrice", this.sellPrice);
        jsonProd.addProperty("cost", this.cost);
        jsonProd.addProperty("currency", this.currency);
        jsonProd.addProperty("artNum", this.artNum);
        jsonProd.addProperty("minQuantity", this.minQuantity);

        return jsonProd;
    }
}
