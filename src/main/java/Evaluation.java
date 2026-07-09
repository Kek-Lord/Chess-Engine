public class Evaluation {

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
}