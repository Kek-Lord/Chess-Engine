import java.util.List;

public class Perft {

    private final MoveGenerator moveGenerator;
    private final MoveMaker moveMaker;

    public Perft(MoveGenerator moveGenerator, MoveMaker moveMaker) {
        this.moveGenerator = moveGenerator;
        this.moveMaker = moveMaker;
    }

    public long perft(Position position, int depth) {

        if (depth == 0) {
            return 1;
        }

        long nodes = 0;

        List<Move> moves = moveGenerator.generateLegalMove(position);

        for (Move move : moves) {
            Position next = moveMaker.makeMove(position, move);
            nodes += perft(next, depth - 1);
        }

        return nodes;
    }

    public void perftDivide(Position position, int depth) {

        List<Move> moves = moveGenerator.generateLegalMove(position);

        long total = 0;

        for (Move move : moves) {

            Position next = moveMaker.makeMove(position, move);

            long count = perft(next, depth - 1);

            System.out.println(move + " : " + count);

            total += count;
        }

        System.out.println("TOTAL: " + total);
    }
}