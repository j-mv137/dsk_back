package DB;
import Api.Types.ApiError;
import DB.Types.Product;

import java.sql.*;
import java.util.*;

public class ProductsDB {
    Connection db;

    public ProductsDB(Connection db) {
        this.db = db;
    }

    public void updateWordsTable(String description) {
        try {
            PreparedStatement st =  this.db.prepareStatement("INSERT INTO wds_description (word) " +
                    "VALUES (ts_stat('SELECT to_tsvector(''simple'', ?)) FROM products');");
            st.setString(1, description);

        } catch (SQLException e) {
            System.err.printf("Error. createWordsTable %s" , e.getMessage());
            System.exit(1);
        }
    }

    private String getSimilar(String word) {
        // Creates a string of the form "wd | simWd1 | simW2 | ..."
        try {
            // From our lexicon obtain those words whose similarity is above 0.5.
            PreparedStatement st =  this.db.prepareStatement("SELECT word FROM wds_description " +
                    "WHERE similarity(word, ?) >= 0.5;");


            st.setString(1, word);

            ResultSet rows = st.executeQuery();

            ArrayList<String> wordList = new ArrayList<>();

            wordList.add(word);

            while(rows.next()) {
                String simWord = rows.getString("word");
                if(!wordList.contains(simWord)) {
                    wordList.add(simWord);
                }
            }

            int listLength = wordList.toArray().length;

            word = "( " + word;

            if(listLength > 1) {
                word = word + " | ";
            }

            for(int i=1; i < wordList.toArray().length; i++) {
                if(i != listLength-1) {
                    word = word.concat(wordList.get(i) + " | ");
                } else {
                    word = word.concat(wordList.get(i));
                }
            }

            return word + " )";
        } catch (SQLException e) {
            System.err.printf("Error getSimilar: %s", e.getMessage());
            return word;
        }
    }

    private String getAllSimilar(String query) {
        // Creates a string of the form "wd1 | simWd1 | simWd1 | wd2 | ... "
        String allSimWords = "";
        // Split the query to separate each word. The regex makes it so we don't consider
        // the blank spaces before the first word
        //
        query = query.strip();

        if (query.isEmpty()) return "";

        String[] words = query.split("\\s+");

        for(int i = 0; i < words.length; i++) {
            if (i == words.length - 1 ) {
                allSimWords = allSimWords.concat(this.getSimilar(words[i]));
            } else {
                allSimWords = allSimWords.concat(this.getSimilar(words[i]) + " & ");
            }
        }


        return allSimWords;
    }

    public Product[] getProducts(String query) throws ApiError {
        ArrayList<Product> prods = new ArrayList<>();
        try {
            // We match the tsvector of the description column to the tsvector of all words in the query plus
            // al other similar words in our lexicon. This to get results even if the query has misspellings.
            PreparedStatement st = this.db.prepareStatement("SELECT * FROM products " +
                                "WHERE to_tsvector(description) @@ to_tsquery(?);");

            st.setString(1, getAllSimilar(query));

            ResultSet rows = st.executeQuery();

            while(rows.next()) {
                Product prod = new Product.Builder()
                        .id(rows.getInt("id"))
                        .mainCode(rows.getString("code"))
                        .secondCode(rows.getString("second_code"))
                        .description(rows.getString("description"))
                        .department(rows.getString("department"))
                        .category(rows.getString("category"))
                        .cost(rows.getDouble("cost"))
                        .sellPrice(rows.getDouble("sell_price"))
                        .currency(rows.getString("currency"))
                        .artNum(rows.getInt("art_num"))
                        .minQuantity(rows.getInt("min_quantity"))
                        .build();

                prods.add(prod);
            }

            Product[] prodsByCode = getProductsByCode(query);

            Product[] prodsArr = new Product[prods.toArray().length];
            Product[] prodsByDesc = prods.toArray(prodsArr);


            return sortProducts(prodsByDesc, prodsByCode);

        } catch (SQLException e) {
            throw ApiError.buildMsg("Error en la búsqueda de artículos", e.getMessage());
        }
    }

    public Product[] getProductsByCode(String query) throws ApiError {
        ArrayList<Product> prods = new ArrayList<>();
        try {
            PreparedStatement st = this.db.prepareStatement("SELECT * FROM products " +
                                "WHERE to_tsvector(code) @@ websearch_to_tsquery(?) " +
                                "OR to_tsvector(second_code) @@ websearch_to_tsquery(?);");

            st.setString(1, query);
            st.setString(2, query);

            ResultSet rows = st.executeQuery();

            while(rows.next()) {
                Product prod = new Product.Builder()
                        .id(rows.getInt("id"))
                        .mainCode(rows.getString("code"))
                        .secondCode(rows.getString("second_code"))
                        .description(rows.getString("description"))
                        .department(rows.getString("department"))
                        .category(rows.getString("category"))
                        .cost(rows.getDouble("cost"))
                        .sellPrice(rows.getDouble("sell_price"))
                        .currency(rows.getString("currency"))
                        .artNum(rows.getInt("art_num"))
                        .minQuantity(rows.getInt("min_quantity"))
                        .build();

                prods.add(prod);
            }

            Product[] prodsArr = new Product[prods.toArray().length];

            return prods.toArray(prodsArr);

        } catch (SQLException e) {
            throw ApiError.buildMsg("Error en la búsqueda de artículos", e.getMessage());
        }
    }

    private Product[] sortProducts(Product[] prodsByDesc, Product[] prodsByCode) {
        final int numProducts = 15;


        Map<String, ArrayList<Product>> prodsByDescMap = Utils.getMapForProds(prodsByDesc);
        Map<String, ArrayList<Product>> prodsByCodeMap = Utils.getMapForProds(prodsByCode);

        List<String> sortedDptsDesc = Utils.orderBySize(prodsByDescMap);
        List<String> sortedDptsCode = Utils.orderBySize(prodsByCodeMap);

        List<Product> sortedProds = new ArrayList<>();

        if (prodsByDesc.length > prodsByCode.length ) {
            for (String dpt : sortedDptsDesc) {
                ArrayList<Product> prodsInDpt = prodsByDescMap.get(dpt);
                if (prodsInDpt.size() > numProducts - sortedProds.size()) {
                    sortedProds.addAll(prodsInDpt.subList(0, numProducts - sortedProds.size()));
                    break;
                }
                sortedProds.addAll(prodsInDpt);
            }

            if (sortedProds.size() < numProducts) {
                for (String dpt : sortedDptsCode) {
                    ArrayList<Product> prodsInDpt = prodsByDescMap.get(dpt);
                    if (prodsInDpt.size() > numProducts - prodsInDpt.size()) {
                        sortedProds.addAll(prodsInDpt.subList(0, numProducts - sortedProds.size()));
                        break;
                    }
                    sortedProds.addAll(prodsInDpt);
                }
            }
        } else {
            for (String dpt : sortedDptsCode) {
                ArrayList<Product> prodsInDpt = prodsByCodeMap.get(dpt);
                if (prodsInDpt.size() > numProducts - prodsInDpt.size()) {
                    sortedProds.addAll(prodsInDpt.subList(0, numProducts - sortedProds.size()));
                    break;
                }
                sortedProds.addAll(prodsInDpt);
            }


            if (sortedProds.size() < numProducts) {
                for (String dpt : sortedDptsDesc) {
                    ArrayList<Product> prodsInDpt = prodsByDescMap.get(dpt);
                    if (prodsInDpt.size() > numProducts - prodsInDpt.size()) {
                        sortedProds.addAll(prodsInDpt.subList(0, numProducts - sortedProds.size()));
                        break;
                    }
                    sortedProds.addAll(prodsInDpt);
                }

            }
        }
        return sortedProds.toArray(new Product[0]);
    }


    public int addProduct(Product product) {
        try {
            PreparedStatement st = this.db.prepareStatement("INSERT INTO products " +
                    "(main_code, second_code, description, department, category, sell_price, cost, currency, " +
                    "artNum, min_quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");


            st.setString(1, product.getMainCode());
            st.setString(2, product.getSecondCode());
            st.setString(3, product.getDescription());
            st.setString(4, product.getDepartment());
            st.setString(5, product.getCategory());
            st.setDouble(6, product.getSellPrice());
            st.setDouble(7, product.getCost());
            st.setString(8, product.getCurrency());
            st.setInt(9, product.getArtCount());
            st.setInt(10, product.getMinQuantity());

            updateWordsTable(product.getDescription());

            return 0;
        } catch(SQLException e) {
            System.err.printf("Error: %s", e.getMessage());
            return e.getErrorCode();
        }
    }

    public static abstract class Utils {
        public static Map<String, ArrayList<Product>> getMapForProds(Product[] products) {

            Map<String, ArrayList<Product>> prodsMap = new HashMap<>();

            for (Product prod : products) {
                String department = prod.getDepartment().toLowerCase();


                if (department.contains("vde")) {
                    if(!prodsMap.containsKey("vde")) {
                        ArrayList<Product> prodsVDE = new ArrayList<>();
                        prodsMap.put("vde", prodsVDE);
                    }
                    prodsMap.get("vde").add(prod);
                } else if (department.contains("novem")) {
                    if(!prodsMap.containsKey("novem")) {
                        ArrayList<Product> prodsNovem = new ArrayList<>();
                        prodsMap.put("novem", prodsNovem);
                    }
                    prodsMap.get("novem").add(prod);
                } else if
                (department.contains("truper") || department.contains("foset") || department. contains("volteck")) {
                    if(!prodsMap.containsKey("truper")) {
                        ArrayList<Product> prodsTruper = new ArrayList<>();
                        prodsMap.put("truper", prodsTruper);
                    }
                    prodsMap.get("truper").add(prod);
                } else if (department.contains("wt")) {
                    if(!prodsMap.containsKey("wt")) {
                        ArrayList<Product> prodsWT = new ArrayList<>();
                        prodsMap.put("wt", prodsWT);
                    }
                }
            }

            return prodsMap;
        }

        public static List<String> orderBySize(Map<String, ArrayList<Product>> prodsMap) {
            Map<Integer, String> dptMap = new HashMap<>();

            for (String k : prodsMap.keySet()) {
                dptMap.put(prodsMap.get(k).size(), k);
            }

            List<Integer> artCount = dptMap.keySet().stream().toList();
            List<String> sortedDpt = new ArrayList<>();

            for (int i : artCount) {
                sortedDpt.add(dptMap.get(i));
            }

            return sortedDpt;
        }
    }


}


