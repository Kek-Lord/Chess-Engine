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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ChessUI extends Application {

    private static final int TILE_SIZE = 80;

    private final MoveGenerator generator = new MoveGenerator();
    private Side boardPerspective = Side.WHITE;
    private final MoveMaker maker = new MoveMaker();
    private Position position = new Position();
    private GameMode gameMode;
    private Side playerSide;

    private final GridPane boardGrid = new GridPane();
    private MoveList moveList = new MoveList();
    private List<Move> selectedMoves = new ArrayList<>();

    private static final double BAR_HEIGHT = TILE_SIZE * 8;
    private final Evaluation evaluation = new Evaluation();

    private final StackPane evalBar = new StackPane();
    private Stage stage;
    private final Rectangle blackBar = new Rectangle(30, BAR_HEIGHT);
    private final Rectangle whiteBar = new Rectangle(30, BAR_HEIGHT);

    private final Rectangle background = new Rectangle(30, BAR_HEIGHT);
    private final Rectangle fill = new Rectangle(30, 0);


    private final Label evalLabel = new Label();
    private List<Move> moves;

    private int selectedRank = -1;
    private int selectedFile = -1;




    @Override
    public void start(Stage stage) {

        this.stage = stage;

        stage.setTitle("Chess Engine Viewer");

        whiteBar.setFill(Color.BLUE);
        blackBar.setFill(Color.BLACK);
        evalBar.getChildren().addAll(blackBar, whiteBar);

        showMainMenu();

        stage.show();
    }

    private void showMainMenu() {
        Button watch = new Button("Watch AI");
        Button play = new Button("Play AI");

        watch.setOnAction(e -> showSideSelection(GameMode.WATCH_AI));
        play.setOnAction(e -> showSideSelection(GameMode.PLAY_AI));

        VBox root = new VBox(20, watch, play);
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 800, 800));
    }

    private void showSideSelection(GameMode mode) {
        Button white = new Button("White");
        Button black = new Button("Black");
        Button random = new Button("Random");

        white.setOnAction(e -> startGame(mode, Side.WHITE));
        black.setOnAction(e -> startGame(mode, Side.BLACK));
        random.setOnAction(e -> startGame(mode, Side.RANDOM));

        VBox root = new VBox(15, white, black, random);
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 800, 800));
    }

    private void startGame(GameMode mode, Side side) {

        if (side == Side.RANDOM) {
            side = Math.random() < 0.5 ? Side.WHITE : Side.BLACK;
        }

        this.gameMode = mode;
        this.playerSide = side;

        this.boardPerspective = side;
        initialiseGame();
        showGameScene();

    }

    private void initialiseGame() {

        position = new Position();
        moveList = new MoveList();
        moves = generator.generateLegalMove(position);
    }

    private void showGameScene() {

        Button nextMove = new Button("Next Move");
        Button undoMove = new Button("Undo Move");
        Button flipBoard = new Button("Flip Board");
        Button menu = new Button("Main Menu");

        nextMove.setOnAction(e -> playNextMove());
        undoMove.setOnAction(e -> undoLastMove());
        menu.setOnAction(e -> showMainMenu());
        flipBoard.setOnAction(e -> {
            boardPerspective = (boardPerspective == Side.WHITE) ? Side.BLACK : Side.WHITE;
            drawBoard(position);
        });

        HBox boardArea = new HBox(15, boardGrid, evalBar);
        VBox root = new VBox(15, boardArea, evalLabel,
                new HBox(10, nextMove, undoMove, menu, flipBoard));

        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 800, 800));

        drawBoard(position);
        updateEvaluation();
        updateEvaluationBar();
    }


    private int generateRandomMove(int moveIndex, List<Move> moves) {
        int randomNum;
        return (int)(Math.random() * moves.size()); // 0 to move list
    }

//    private int getBestMove(List<Move> moves) {
//        for (Move move : moves) {
//
//        }
//    }

//    private int search(int depth, MoveMaker moveMaker, Position position) {
//        List<Move> moves = generator.generateLegalMove(position);
//
//        for (Move move : moves) {
//            moveMaker.makeMove(position, move);
//            search(depth - 1, moveMaker, position);
//            position
//        }
//    }


    private void updateEvaluationBar() {

        double eval = evaluation.calculateEvaluation(position);

        eval = Math.max(-10, Math.min(10, eval));

        double percentage = (eval + 10) / 20.0;

        whiteBar.setHeight(BAR_HEIGHT * percentage);
        StackPane.setAlignment(blackBar, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(whiteBar, Pos.BOTTOM_CENTER);
    }

    private void drawBoard(Position position) {

        boardGrid.getChildren().clear();

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {

                StackPane tile = new StackPane();

                int boardRank = rank;
                int boardFile = file;

                if (boardPerspective == Side.BLACK) {
                    boardRank = 7 - rank;
                    boardFile = 7 - file;
                }

                // Check whether this square is a legal destination
                boolean isLegalDestination = false;

                for (Move move : selectedMoves) {
                    if (move.getToRank() == boardRank &&
                            move.getToFile() == boardFile) {

                        isLegalDestination = true;
                        break;
                    }
                }

                // Normal board colour
                boolean light = (rank + file) % 2 == 0;

                Rectangle square = new Rectangle(TILE_SIZE, TILE_SIZE);

                if (isLegalDestination) {
                    square.setFill(Color.GREEN);
                } else {
                    square.setFill(light ? Color.BEIGE : Color.BROWN);
                }

                square.setViewOrder(2);
                tile.getChildren().add(square);

                int selectedRank = boardRank;
                int selectedFile = boardFile;

                tile.setOnMouseClicked(e -> {
                    handleSquareClicked(selectedRank, selectedFile);
                });

                char piece = position.getPiece(boardRank, boardFile);

                if (piece != '.') {

                    String path = getImagePath(piece);
                    var stream = getClass().getResourceAsStream("/" + path);

                    if (stream == null) {
                        System.out.println("Missing image: " + path);
                    } else {

                        ImageView img = new ImageView(new Image(stream));

                        img.setFitWidth(TILE_SIZE);
                        img.setFitHeight(TILE_SIZE);
                        img.setPreserveRatio(true);
                        img.setViewOrder(1);

                        tile.getChildren().add(img);
                    }
                }

                tile.setPrefSize(TILE_SIZE, TILE_SIZE);

                boardGrid.add(tile, file, rank);
            }
        }
    }

    private void handleSquareClicked(int rank, int file) {

        if (gameMode == GameMode.WATCH_AI) {
            return;
        }

        char selectedPiece = position.getPiece(rank, file);

        if (selectedPiece == '.') {
            return;
        }

        if (!isPlayerPiece(selectedPiece)) {
            return;
        }

        selectedRank = rank;
        selectedFile = file;

        selectedMoves = getMovesAtTile(rank, file);

        System.out.println("Legal moves: " + selectedMoves.size());

        drawBoard(position);
    }

    private boolean isPlayerPiece(char piece) {
        if (playerSide == Side.WHITE) {
            return Character.isUpperCase(piece);
        }

        if (playerSide == Side.BLACK) {
            return Character.isLowerCase(piece);
        }

        return false;
    }

    private List<Move> getMovesAtTile(int rank, int file) {
        List<Move> movesAtTile = new ArrayList<>();

        for (Move move : moves) {
            if (move.getFromRank() == rank && move.getFromFile() == file){
                movesAtTile.add(move);
            }
        }

        return movesAtTile;
    }

    private void setGridTileColour(int rank, int file) {
        for (int i = 0; i < rank; i++) {
            for(int j = 0; i < file; j++) {

            }
        }
    }

    private void playNextMove() {
        System.out.println("Button Clicked");
        int moveIndex = 0;
        if (moveIndex >= moves.size()){
            System.out.println("No More Moves :)");
            return;
        }

        Move move = moves.get(generateRandomMove(moveIndex, moves));
        position = maker.makeMove(position, move);
        moveList.addMove(move);
        moveList.addPosition(position);
        moveList.printPositionList();

        moves = generator.generateLegalMove(position);

        drawBoard(position);
        updateEvaluation();
        updateEvaluationBar();
    }


    private void undoLastMove() {
        System.out.println("Undo Last Move Button Clicked!");
        // get the last position, then set the current position to that one
        position = moveList.getLastPosition();
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