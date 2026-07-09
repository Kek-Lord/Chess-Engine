public class FenConvertor {
    public Position fenToPosition(String fen) {
        Position position = new Position();
        int rank = 7;
        int file = 0;

        for (int i = 0; i < fen.length(); i++) {
            char c = fen.charAt(i);

            if (c == ' ') {
                break;      // End of board description
            }

            if (c == '/') {
                rank--;
                file = 0;
                continue;
            }

            if (Character.isDigit(c)) {
                int emptySquares = c - '0';

                for (int j = 0; j < emptySquares; j++) {
                    position.setPiece(rank, file, '.');
                    file++;
                }

                continue;
            }

            // Must be a piece
            position.setPiece(rank, file, c);
            file++;
        }
        return position;
    }
}
