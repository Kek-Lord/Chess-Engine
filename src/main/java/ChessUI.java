import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;

public class ChessUI extends Application {

    private static final int TILE_SIZE = 80;

    private final MoveGenerator generator = new MoveGenerator();
    private final MoveMaker maker = new MoveMaker();

    private final GridPane boardGrid = new GridPane();
    private Position position = new Position();

    private static final double BAR_HEIGHT = TILE_SIZE * 8;
    private final Evaluation evaluation = new Evaluation();

    private final StackPane evalBar = new StackPane();
    private final Rectangle blackBar = new Rectangle(30, BAR_HEIGHT);
    private final Rectangle whiteBar = new Rectangle(30, BAR_HEIGHT);

    private final Rectangle background = new Rectangle(30, BAR_HEIGHT);
    private final Rectangle fill = new Rectangle(30, 0);


    private final Label evalLabel = new Label();
    private List<Move> moves;
    private int moveIndex = 0;

    @Override
    public void start(Stage stage) {
        whiteBar.setFill(Color.BLUE);
        blackBar.setFill(Color.BLACK);
        evalBar.getChildren().addAll(blackBar, whiteBar);
        drawBoard(position);

        Button nextMove = new Button("Next Move");
        nextMove.setOnAction(e -> playNextMove());
        HBox boardArea = new HBox(15, boardGrid, evalBar);
        VBox root = new VBox(boardArea, evalLabel, nextMove);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Chess Engine Viewer");
        stage.show();
        updateEvaluation();
        updateEvaluationBar();
        MoveGenerator generator = new MoveGenerator();
        moves = generator.generateLegalMove(position);
    }

    private int generateRandomMove(int moveIndex, List<Move> moves) {
        int randomNum;
        return (int)(Math.random() * moves.size()); // 0 to move list
    }

    private int getBestMove(List<Move> moves) {
        for (Move move : moves) {

        }
    }

    private int search(int depth, MoveMaker moveMaker, Position position) {
        List<Move> moves = generator.generateLegalMove(position);

        for (Move move : moves) {
            moveMaker.makeMove(position, move);
            search(depth - 1, moveMaker, position);
            position
        }
    }

    private void updateEvaluationBar() {

        double eval = evaluation.calculateEvaluation(position);

        // Clamp between -10 and +10
        eval = Math.max(-10, Math.min(10, eval));

        // Convert [-10,+10] to [0,1]
        double percentage = (eval + 10) / 20.0;

        whiteBar.setHeight(BAR_HEIGHT * percentage);
        // Keep the white section attached to the bottom
        StackPane.setAlignment(blackBar, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(whiteBar, Pos.BOTTOM_CENTER);
    }

    private void drawBoard(Position position) {

        boardGrid.getChildren().clear();

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {

                StackPane tile = new StackPane();

                boolean light = (rank + file) % 2 == 0;

                Rectangle square = new Rectangle(TILE_SIZE, TILE_SIZE);
                square.setFill(light ? Color.BEIGE : Color.BROWN);


                square.setViewOrder(1);

                tile.getChildren().add(square);

                char piece = position.getPiece(rank, file);

                if (piece != '.') {

                    String path = getImagePath(piece);
                    var stream = getClass().getResourceAsStream("/" + path);

                    if (stream == null) {
                        System.out.println("Missing image: " + path);
                        continue;
                    }

                    ImageView img = new ImageView(new Image(stream));

                    img.setFitWidth(TILE_SIZE);
                    img.setFitHeight(TILE_SIZE);
                    img.setPreserveRatio(true);

                    img.setViewOrder(0);

                    tile.getChildren().add(img);
                }

                tile.setPrefSize(TILE_SIZE, TILE_SIZE);

                boardGrid.add(tile, file, rank);
            }
        }
    }

    private void playNextMove() {
        System.out.println("Button Clicked");
        if (moveIndex >= moves.size()){
            System.out.println("No More Moves :)");
            return;
        }

        Move move = moves.get(generateRandomMove(moveIndex, moves));
        System.out.println(move);
        position = maker.makeMove(position, move);

        // IMPORTANT: regenerate moves for new position
        moves = generator.generateLegalMove(position);

        drawBoard(position);
        updateEvaluation();
        updateEvaluationBar();
    }

    private String getImagePath(char piece) {

        String type = switch (Character.toLowerCase(piece)) {
            case 'k' -> "king";
            case 'q' -> "queen";
            case 'r' -> "rook";
            case 'b' -> "bishop";
            case 'n' -> "knight";
            case 'p' -> "pawn";
            default -> null;
        };

        if (type == null) return null;

        String color = Character.isUpperCase(piece) ? "white" : "black";

        return "pieces/" + color + "-" + type + ".png";
    }

    private void updateEvaluation() {
        double eval = evaluation.calculateEvaluation(position);
        evalLabel.setText(String.format("Evaluation: %.1f", eval));
    }
}