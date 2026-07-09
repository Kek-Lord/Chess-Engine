import java.util.List;

public class Main {
    public static void main (String[] args) throws InterruptedException {
        Position position = new Position();
        MoveGenerator generator = new MoveGenerator();
        MoveMaker moveMaker = new MoveMaker();

        Perft perft = new Perft(generator, moveMaker);

        FenConvertor convertor = new FenConvertor();

        System.out.println(perft.perft(position, 4));
    }
}
