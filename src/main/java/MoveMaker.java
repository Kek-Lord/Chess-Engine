public class MoveMaker {
    public Position makeMove(Position position, Move move) {

        Position copy = new Position(position);

        char piece = copy.getPiece(
                move.getFromRank(),
                move.getFromFile()
        );

        char captured = copy.getPiece(
                move.getToRank(),
                move.getToFile()
        );


        copy.setPiece(
                move.getFromRank(),
                move.getFromFile(),
                '.'
        );

        char pieceToPlace = piece;

        if (move.isPromotionMove()) {
            pieceToPlace = move.getPromotionPiece();
        }

        copy.setPiece(
                move.getToRank(),
                move.getToFile(),
                pieceToPlace
        );

        if (piece == 'K') {

            if(move.getFromFile() == 4 && move.getToFile() == 6) {
                copy.setPiece(7,7,'.');
                copy.setPiece(7,5,'R');
            }

            if(move.getFromFile() == 4 && move.getToFile() == 2) {
                copy.setPiece(7, 0, '.');
                copy.setPiece(7,3,'R');
            }
        }

        if (piece == 'k') {

            if(move.getFromFile() == 4 && move.getToFile() == 6) {
                copy.setPiece(0, 7, '.');
                copy.setPiece(0, 5, 'r');
            }

            if(move.getFromFile() == 4 && move.getToFile() == 2) {
                copy.setPiece(0, 0, '.');
                copy.setPiece(0,3,'r');
            }
        }

        updateCastlingRights(copy, piece, move, captured);

        copy.setWhiteToMove(!copy.isWhiteToMove());


        return copy;
    }

    private void updateCastlingRights(
            Position position,
            char piece,
            Move move,
            char capturedPiece
    ) {
        int fromRank = move.getFromRank();
        int fromFile = move.getFromFile();
        int toRank = move.getToRank();
        int toFile = move.getToFile();

        if (piece == 'K') {
            position.disableWhiteKingSideCastling();
            position.disableWhiteQueenSideCastling();
        }

        if (piece == 'k') {
            position.disableBlackKingSideCastling();
            position.disableBlackQueenSideCastling();
        }
        // white rook
        if (piece == 'R' || capturedPiece == 'R') {

            if((fromRank == 7 && fromFile == 0) || (toRank == 7 && toFile == 0)) {
                position.disableWhiteQueenSideCastling();
            }

            if ((fromRank == 7 && fromFile == 7) || (toRank == 7 && toFile == 7)) {
                position.disableWhiteKingSideCastling();
            }
        }

        //black rook
        if (piece == 'r' || capturedPiece == 'r') {

            if((fromRank == 0 && fromFile == 0) || (toRank == 0 && toFile == 0)) {
                position.disableBlackQueenSideCastling();
            }

            if ((fromRank == 0 && fromFile == 7) || (toRank == 0 && toFile == 7)) {
                position.disableBlackKingSideCastling();
            }
        }
    }

}
