import java.util.ArrayDeque;
import java.util.Deque;

public class MoveList {
    private final Deque<Move> moveList = new ArrayDeque<>();
    private final Deque<Position> positionList = new ArrayDeque<>();

    public void addMove(Move move) {
        moveList.add(move);
    }

    public Deque<Move> getMoveList() {
        return moveList;
    }

    public void addPosition(Position position) {
        positionList.add(position);
    }

    public Deque<Position> getPositionList() {
        return positionList;
    }

    public void printPositionList() {
        for (Position position : positionList) {
            position.printBoard();
        }
    }

    public Position getLastPosition() {
        // the last position is actually the current position, so to move back
        // we have to get the second to last position

        if (positionList.size() == 1) {
            positionList.removeLast();
            return new Position();
        }
        positionList.removeLast();
        return positionList.getLast();
    }
}
