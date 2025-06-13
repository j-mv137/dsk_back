package DB.Types;

public class ProductPosition {
    private final int prodID;
    private final int posID;

    public ProductPosition(int prodID, int posID) {
        this.prodID = prodID;
        this.posID = posID;
    }
    int getProdID() {
        return  this.prodID;
    }

    int getPosID() {
        return  this.posID;
    }
}
