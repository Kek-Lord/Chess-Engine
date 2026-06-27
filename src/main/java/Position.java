public class Position {
    private char[][] board;
    private boolean whiteToMove;
    private boolean whiteKingSideCastling;
    private boolean whiteQueenSideCastling;

    private boolean blackKingSideCastling;
    private boolean blackQueenSideCastling;

    private int enPassantSquare;
    private int halfMoveClock;
    private int fullMoveNumber;

    public Position() {
        board = new char[][]{
                {'r','n','b','q','k','b','n','r'},
                {'p','p','p','p','p','p','p','p'},
                {'.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.'},
                {'.','.','.','.','.','.','.','.'},
                {'P','P','P','P','P','P','P','P'},
                {'R','N','B','Q','K','B','N','R'}
        };

        whiteToMove = true;

        whiteKingSideCastling = true;
        whiteQueenSideCastling = true;
        blackKingSideCastling = true;
        blackQueenSideCastling = true;

        enPassantSquare = -1;

        halfMoveClock = 0;
        fullMoveNumber = 1;
    }

    public void printBoard() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                System.out.print(board[rank][file] + " ");
            }
            System.out.print("| ");
            System.out.println(rank);
        }
        System.out.println("-----------------");
        System.out.println("0 1 2 3 4 5 6 7\n");
    }

    public Position(Position other) {
        this.board = new char[8][8];

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                this.board[rank][file] = other.board[rank][file];
            }
        }

        this.whiteToMove = other.whiteToMove;

        this.whiteKingSideCastling =
                other.whiteKingSideCastling;
        this.whiteQueenSideCastling =
                other.whiteQueenSideCastling;

        this.blackKingSideCastling =
                other.blackKingSideCastling;
        this.blackQueenSideCastling =
                other.blackQueenSideCastling;

        this.enPassantSquare = other.enPassantSquare;

        this.halfMoveClock = other.halfMoveClock;
        this.fullMoveNumber = other.fullMoveNumber;
    }

    public boolean isWhiteToMove(){
        return whiteToMove;
    }

    public void setWhiteToMove(boolean whiteToMove) {
        this.whiteToMove = whiteToMove;
    }

    public char getPiece(int rank, int file) {
        return board[rank][file];
    }


    public void setPiece(int rank, int file, char piece) {
        board[rank][file] = piece;
    }

    public boolean isWhiteKingSideCastling() {
        return whiteKingSideCastling;
    }

    public boolean isWhiteQueenSideCastling() {
        return whiteQueenSideCastling;
    }

    public boolean isBlackKingSideCastling() {
        return blackKingSideCastling;
    }

    public boolean isBlackQueenSideCastling() {
        return blackQueenSideCastling;
    }

    public void disableWhiteKingSideCastling() {
        whiteKingSideCastling = false;
    }
    public void disableWhiteQueenSideCastling() {
        whiteQueenSideCastling = false;
    }
    public void disableBlackKingSideCastling() {
        blackKingSideCastling = false;
    }
    public void disableBlackQueenSideCastling() {
        blackQueenSideCastling = false;
    }
}
