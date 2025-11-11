package DB.Types;

import Api.Types.ApiError;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private final int id;
    private final String mainCode;
    private final String secondCode;
    private final String description;
    private final String department;
    private final String category;
    private final double sellPrice;
    private final double cost;
    private final String currency;
    private final int artNum;
    private final int minQuantity;

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

    public int getId() {
        return id;
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

    public int getMinQuantity() { return minQuantity; }


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
        jsonProd.addProperty("mainCode", this.mainCode);
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

    public static Product parseProdFromJson(JsonObject prodJson) {
        return new Builder()
                .id(prodJson.get("id").getAsInt())
                .mainCode(prodJson.get("mainCode").getAsString())
                .secondCode(prodJson.get("secondCode").getAsString())
                .description(prodJson.get("description").getAsString())
                .department(prodJson.get("department").getAsString())
                .category(prodJson.get("category").getAsString())
                .sellPrice(prodJson.get("sellPrice").getAsFloat())
                .cost(prodJson.get(("cost")).getAsFloat())
                .currency(prodJson.get("artNum").getAsString())
                .minQuantity(prodJson.get("minQuantity").getAsInt())
                .build();
    }

    public static Product[] parseProdsJsonArr(String prodsJsonStr) throws ApiError {
        try {
            JsonArray prodsJsonArr = JsonParser.parseString(prodsJsonStr).getAsJsonArray();
            List<Product> prodsList = new ArrayList<>();

            for (JsonElement prodJsonEl : prodsJsonArr) {
                Product prod = Product.parseProdFromJson(prodJsonEl.getAsJsonObject());
                prodsList.add(prod);
            }

            return prodsList.toArray(new Product[0]);

        } catch (JsonSyntaxException e) {
            throw ApiError.buildMsg("f_parseProdsJsonArr cls_product failed to parse json syntax"
                , e.getMessage());
        }
    }
}
