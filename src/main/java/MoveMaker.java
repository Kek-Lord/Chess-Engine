import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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

        // clear from-square
        copy.setPiece(
                move.getFromRank(),
                move.getFromFile(),
                '.'
        );

        char pieceToPlace = piece;

        if (move.isPromotionMove()) {
            pieceToPlace = move.getPromotionPiece();
        }

        //en passant capture handling
        if (move.isEnPassant()) {

            int toRank = move.getToRank();
            int toFile = move.getToFile();

            // white pawn captures black pawn
            if (piece == 'P') {
                copy.setPiece(toRank + 1, toFile, '.');
            }

            // black pawn captures white pawn
            if (piece == 'p') {
                copy.setPiece(toRank - 1, toFile, '.');
            }
        }

        // place piece
        copy.setPiece(
                move.getToRank(),
                move.getToFile(),
                pieceToPlace
        );

        // castling
        if (piece == 'K') {

            if (move.getFromFile() == 4 && move.getToFile() == 6) {
                copy.setPiece(7, 7, '.');
                copy.setPiece(7, 5, 'R');
            }

            if (move.getFromFile() == 4 && move.getToFile() == 2) {
                copy.setPiece(7, 0, '.');
                copy.setPiece(7, 3, 'R');
            }
        }

        if (piece == 'k') {

            if (move.getFromFile() == 4 && move.getToFile() == 6) {
                copy.setPiece(0, 7, '.');
                copy.setPiece(0, 5, 'r');
            }

            if (move.getFromFile() == 4 && move.getToFile() == 2) {
                copy.setPiece(0, 0, '.');
                copy.setPiece(0, 3, 'r');
            }
        }

        updateCastlingRights(copy, piece, move, captured);

        // en passant square
        copy.setEnPassantSquare(-1);

        if (piece == 'P' || piece == 'p') {

            int fromRank = move.getFromRank();
            int toRank = move.getToRank();

            // double pawn push
            if (Math.abs(fromRank - toRank) == 2) {

                int epRank = (fromRank + toRank) / 2;
                int epFile = move.getFromFile();

                copy.setEnPassantSquare(epRank * 8 + epFile);
            }
        }

        // switch side
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

        // king moves
        if (piece == 'K') {
            position.disableWhiteKingSideCastling();
            position.disableWhiteQueenSideCastling();
        }

        if (piece == 'k') {
            position.disableBlackKingSideCastling();
            position.disableBlackQueenSideCastling();
        }

        // white rook moves or gets captured
        if (piece == 'R' || capturedPiece == 'R') {

            if ((fromRank == 7 && fromFile == 0) || (toRank == 7 && toFile == 0)) {
                position.disableWhiteQueenSideCastling();
            }

            if ((fromRank == 7 && fromFile == 7) || (toRank == 7 && toFile == 7)) {
                position.disableWhiteKingSideCastling();
            }
        }

        // black rook moves or gets captured
        if (piece == 'r' || capturedPiece == 'r') {

            if ((fromRank == 0 && fromFile == 0) || (toRank == 0 && toFile == 0)) {
                position.disableBlackQueenSideCastling();
            }

            if ((fromRank == 0 && fromFile == 7) || (toRank == 0 && toFile == 7)) {
                position.disableBlackKingSideCastling();
            }
        }
    }
}