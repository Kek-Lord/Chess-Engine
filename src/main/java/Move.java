public class Move {
    private char piece;

    private int fromRank;
    private int toRank;

    private int fromFile;
    private int toFile;
    private char capturedPiece;
    private boolean castleMove;




    private char promotionPiece;


    public Move(
            char piece,
            int fromRank,
            int fromFile,
            int toRank,
            int toFile,
            char capturedPiece) {

        this(
                piece,
                fromRank,
                fromFile,
                toRank,
                toFile,
                capturedPiece,
                '\0'
        );
    }

    public Move(
            char piece,
            int fromRank,
            int fromFile,
            int toRank,
            int toFile,
            char capturedPiece,
            char promotionPiece) {

        this.piece = piece;
        this.fromRank = fromRank;
        this.fromFile = fromFile;
        this.toRank = toRank;
        this.toFile = toFile;
        this.capturedPiece = capturedPiece;
        this.promotionPiece = promotionPiece;
    }

    @Override
    public String toString() {
        return piece + ": (" +
                fromRank + "," + fromFile +
                ") -> (" +
                toRank + "," + toFile + ")";
    }

    public int getFromRank() {
        return fromRank;
    }

    public int getFromFile() {
        return fromFile;
    }

    public int getToRank() {
        return toRank;
    }

    public int getToFile() {
        return toFile;
    }

    public char getCapturedPiece() {
        return capturedPiece;
    }

    public char getPromotionPiece() { return promotionPiece;}

    public boolean isPromotionMove() {
        return promotionPiece != '\0';
    }

    public boolean isCastleMove() {
        return castleMove;
    }

    public void setCastleMove(boolean castleMove) {
        this.castleMove = castleMove;
    }

}
