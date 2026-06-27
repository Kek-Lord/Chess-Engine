import java.util.List;

public class Main {
    public static void main (String[] args) throws InterruptedException {
        Position position = new Position();

        MoveGenerator generator = new MoveGenerator();
        MoveMaker maker = new MoveMaker();

        for (int i = 0; i < 200; i++) {

            List<Move> moves =
                    generator.generateMove(position);

            Move move =
                    moves.get(0); // naive choice

            Thread.sleep(1000);
            System.out.println(move);

            position = maker.makeMove(position, move);

            position.printBoard();
        }
    }
}
