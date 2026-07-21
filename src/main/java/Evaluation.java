import java.util.List;

public class Evaluation {
    MoveGenerator moveGenerator = new MoveGenerator();
    MoveMaker moveMaker = new MoveMaker();
    MoveList moveList = new MoveList();

    public double calculateEvaluation(Position position) {
        double eval = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                char piece = position.getPiece(row, col);

                switch (Character.toLowerCase(piece)) {
                    case 'p':
                        eval += Character.isUpperCase(piece) ? 1 : -1;
                        break;

                    case 'n':
                        eval += Character.isUpperCase(piece) ? 3 : -3;
                        break;

                    case 'b':
                        eval += Character.isUpperCase(piece) ? 3 : -3;
                        break;

                    case 'r':
                        eval += Character.isUpperCase(piece) ? 5 : -5;
                        break;

                    case 'q':
                        eval += Character.isUpperCase(piece) ? 9 : -9;
                        break;

                    case 'k':
                        break;

                    default:
                        break;
                }
            }
        }

        return eval;
    }

    public double search(Position position, int depth) {
        if (depth == 0) {
            return calculateEvaluation(position);
        }

        List<Move> moves = moveGenerator.generateLegalMove(position);
        if (moves.isEmpty()) {
            if (moveGenerator.isKingUnderAttack(position, position.isWhiteToMove())) {
                return Double.NEGATIVE_INFINITY;
            }
            return 0;
        }

        double bestEvaluation = Double.NEGATIVE_INFINITY;

        for (Move move : moves) {
            moveMaker.makeMove(position, move);
            double evaluation = -search(position, depth - 1);
            if (evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
            }
            moveList.getLastPosition();
        }

        return bestEvaluation;
    }
}