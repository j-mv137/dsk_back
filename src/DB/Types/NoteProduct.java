package DB.Types;

public class NoteProduct {
    private final int noteID;
    private final int prodID;

    NoteProduct(int noteID, int prodID) {
        this.noteID = noteID;
        this.prodID = prodID;
    }

    public int getNoteID() {
        return noteID;
    }

    public int getProdID() {
        return prodID;
    }
}
