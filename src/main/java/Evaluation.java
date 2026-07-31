import java.util.List;

public class Evaluation {

    MoveGenerator moveGenerator = new MoveGenerator();
    MoveMaker moveMaker = new MoveMaker();

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
                }
            }
        }

        return eval;
    }

    public double search(Position position, int depth, double alpha, double beta) {

        List<Move> moves = moveGenerator.generateLegalMove(position);

        if (moves.isEmpty()) {
            if (moveGenerator.isKingUnderAttack(
                    position,
                    position.isWhiteToMove()
            )) {
                return -1000000 - depth;
            }

            return 0;
        }

        if (depth == 0) {
            return evaluate(position);
        }

        double bestEvaluation = -1000000;

        for (Move move : moves) {
            Position childPosition = moveMaker.makeMove(position, move);

            double evaluation =
                    -search(childPosition, depth - 1, -beta, -alpha);

            bestEvaluation = Math.max(bestEvaluation, evaluation);
            alpha = Math.max(alpha, evaluation);

            if (alpha >= beta) {
                break;
            }
        }

        return bestEvaluation;
    }

    public double evaluate(Position position) {

        double eval = calculateEvaluation(position);

        if (position.isWhiteToMove()) {
            return eval;
        } else {
            return -eval;
        }
    }

    public Move findBestMove(Position position, int depth) {

        List<Move> moves =
                moveGenerator.generateLegalMove(position);

        if (moves.isEmpty()) {
            return null;
        }

        Move bestMove = null;
        double bestEvaluation = -1000000;

        double alpha = -1000000;
        double beta = 1000000;
        boolean firstMove = true;

        for (Move move : moves) {

            // Make the candidate move
            Position childPosition =
                    moveMaker.makeMove(position, move);

            // Search from the opponent's position
            double evaluation =
                    -search(childPosition, depth - 1, -beta, -alpha);

            if(firstMove || evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
                bestMove = move;
                firstMove = false;
            }

            alpha = Math.max(alpha, evaluation);
        }

        return bestMove;
    }
}