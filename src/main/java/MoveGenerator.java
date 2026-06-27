import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    private static final int[][] KNIGHT_OFFSETS = {
            {-2, -1}, {-2, 1},
            {-1, -2}, {1, -2},

            {-1, 2}, {1, 2},
            {2, -1}, {2, 1}
    };

    private static final int[][] BISHOP_OFFSETS = {
            {-1, -1}, {-1, 1},

            {1, -1}, {1, 1},
    };

    private static final int[][] ROOK_OFFSETS = {
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1}
    };

    private static final int[][] QUEEN_OFFSETS = {
            // rook directions
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1},

            // bishop directions
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
    };

    private static final int[][] KING_OFFSETS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    private int[] findKing(Position position) {
        char king = position.isWhiteToMove() ? 'K' : 'k';

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                if (position.getPiece(rank, file) == king) {
                    return new int[]{rank, file};
                }
            }
        }
        return null;
    }


    // so, we want to know if the king is under attack.
    public boolean isKingUnderAttack(Position position) {

        int[] kingPos = findKing(position);

        return isSquareAttacked(
                position,
                kingPos[0],
                kingPos[1],
                !position.isWhiteToMove()
        );
    }


    public List<Move> generateMove(Position position) {
        List<Move> moves = new ArrayList<>();
        int rank = 0;
        int file = 0;
        if (isKingUnderAttack(position)) {
            System.out.println("King under attack!");
            int[] kingPos = findKing(position);

            moves.addAll(generateKingMove(position,
                    kingPos[0],
                    kingPos[1]));

            return moves;
        }
        for (rank = 0; rank < 8; rank++) {
            for (file = 0; file < 8; file++) {

                char piece = position.getPiece(rank, file);

                if (!belongsToSideToMove(position, piece)) {
                    continue;
                }



                switch (Character.toUpperCase(piece)) {

                    case 'P':
                        moves.addAll(generatePawnMove(position, rank, file));
                        break;

                    case 'N':
                        moves.addAll(generateKnightMove(position, rank, file));
                        break;

                    case 'B':
                        moves.addAll(generateBishopMove(position, rank, file));
                        break;

                    case 'R':
                        moves.addAll(generateRookMove(position, rank, file));
                        break;

                    case 'Q':
                        moves.addAll(generateQueenMove(position, rank, file));
                        break;

                    case 'K':
                        moves.addAll(generateKingMove(position, rank, file));
                        break;
                }
            }
        }

        return moves;
    }

    // moves for Bishop, Rook, and Queen
    private List<Move> generateSlidingMoves(
            Position position,
            int rank,
            int file,
            int[][] directions) {
        List<Move> moves = new ArrayList<>();

        char movingPiece = position.getPiece(rank, file);

        for (int[] direction : directions) {
            int targetRank = rank + direction[0];
            int targetFile = file + direction[1];

            while (targetRank >= 0 &&
                    targetRank < 8 &&
                    targetFile >= 0 &&
                    targetFile < 8) {
                char targetPiece = position.getPiece(targetRank, targetFile);
                if (targetPiece == '.') {
                    moves.add(new Move(
                            movingPiece,
                            rank, file,
                            targetRank,
                            targetFile,
                            targetPiece
                    ));
                } else if (sameColour(
                        movingPiece,
                        targetPiece
                )) {
                    break;
                } else {
                    moves.add(new Move(movingPiece,
                            rank, file,
                            targetRank,
                            targetFile,
                            targetPiece));
                    break;
                }

                targetRank += direction[0];
                targetFile += direction[1];
            }
        }

        return moves;
    }

    private List<Move> generateBishopMove(
            Position position,
            int rank,
            int file) {

        return generateSlidingMoves(
                position,
                rank,
                file,
                BISHOP_OFFSETS
        );
    }

    private List<Move> generatePawnMove(
            Position position,
            int rank,
            int file) {

        List<Move> pawnMoves = new ArrayList<>();

        char movingPiece = position.getPiece(rank, file);

        int direction =
                isWhitePiece(movingPiece)
                        ? -1
                        : 1;

        int startingRank =
                isWhitePiece(movingPiece)
                        ? 6
                        : 1;

        // single push

        int oneForward = rank + direction;

        if (oneForward >= 0 &&
                oneForward < 8 &&
                position.getPiece(oneForward, file) == '.') {

            if (isPromotionSquare(movingPiece, oneForward)) {

                addPromotionMoves(
                        pawnMoves,
                        movingPiece,
                        rank,
                        file,
                        oneForward,
                        file,
                        '.');

            } else {

                pawnMoves.add(
                        new Move(
                                movingPiece,
                                rank,
                                file,
                                oneForward,
                                file,
                                '.'
                        )
                );
            }

            // double push

            int twoForward = rank + (2 * direction);

            if (rank == startingRank &&
                    twoForward >= 0 &&
                    twoForward < 8 &&
                    position.getPiece(twoForward, file) == '.') {

                pawnMoves.add(
                        new Move(
                                movingPiece,
                                rank,
                                file,
                                twoForward,
                                file,
                                '.'
                        )
                );
            }
        }

        //capture left

        int captureRank = rank + direction;
        int captureLeftFile = file - 1;

        if (captureRank >= 0 &&
                captureRank < 8 &&
                captureLeftFile >= 0) {

            char targetPiece =
                    position.getPiece(
                            captureRank,
                            captureLeftFile);

            if (isEnemyPiece(
                    movingPiece,
                    targetPiece)) {

                if (isPromotionSquare(movingPiece, captureRank)) {

                    addPromotionMoves(
                            pawnMoves,
                            movingPiece,
                            rank,
                            file,
                            captureRank,
                            captureLeftFile,
                            targetPiece);

                } else {

                    pawnMoves.add(
                            new Move(
                                    movingPiece,
                                    rank,
                                    file,
                                    captureRank,
                                    captureLeftFile,
                                    targetPiece
                            )
                    );
                }
            }
        }

        // capture right

        int captureRightFile = file + 1;

        if (captureRank >= 0 &&
                captureRank < 8 &&
                captureRightFile < 8) {

            char targetPiece =
                    position.getPiece(
                            captureRank,
                            captureRightFile);

            if (isEnemyPiece(
                    movingPiece,
                    targetPiece)) {

                if (isPromotionSquare(movingPiece, captureRank)) {

                    addPromotionMoves(
                            pawnMoves,
                            movingPiece,
                            rank,
                            file,
                            captureRank,
                            captureRightFile,
                            targetPiece);

                } else {

                    pawnMoves.add(
                            new Move(
                                    movingPiece,
                                    rank,
                                    file,
                                    captureRank,
                                    captureRightFile,
                                    targetPiece
                            )
                    );
                }
            }
        }

        return pawnMoves;
    }

    private void addPromotionMoves(
            List<Move> moves,
            char pawn,
            int fromRank,
            int fromFile,
            int toRank,
            int toFile,
            char capturedPiece) {

        char[] promotionPieces =
                isWhitePiece(pawn)
                        ? new char[]{'Q', 'R', 'B', 'N'}
                        : new char[]{'q', 'r', 'b', 'n'};

        for (char promotionPiece : promotionPieces) {
            moves.add(
                    new Move(
                            pawn,
                            fromRank,
                            fromFile,
                            toRank,
                            toFile,
                            capturedPiece,
                            promotionPiece

                    )
            );
        }
    }

    private boolean isPromotionSquare(char pawn, int targetRank) {
        return (isWhitePiece(pawn) && targetRank == 0) ||
                (!isWhitePiece(pawn) && targetRank == 7);
    }

    public List<Move> generateKnightMove(Position position, int rank, int file) {
        List<Move> knightMoves = new ArrayList<>();
        char movingPiece = position.getPiece(rank, file);

        for (int[] offsets : KNIGHT_OFFSETS) {
            int targetRank = rank + offsets[0];
            int targetFile = file + offsets[1];
            if (targetRank < 0 || targetRank >= 8 ||
                    targetFile < 0 || targetFile >= 8) {
                continue;
            }

            char targetPiece = position.getPiece(targetRank, targetFile);

            if (sameColour(movingPiece, targetPiece)) {
                continue;
            }
            Move knightMove = new Move(movingPiece, rank, file, targetRank, targetFile, targetPiece,'\0');
            knightMoves.add(knightMove);
        }
        return knightMoves;
    }

    private List<Move> generateRookMove(
            Position position,
            int rank,
            int file
    ) {
        return generateSlidingMoves(
                position,
                rank,
                file,
                ROOK_OFFSETS
        );
    }

    private List<Move> generateQueenMove(
            Position position,
            int rank,
            int file
    ) {

        return generateSlidingMoves(
                position,
                rank,
                file,
                QUEEN_OFFSETS
        );
    }

    private List<Move> generateKingMove(
            Position position,
            int rank,
            int file) {

        List<Move> kingMoves = new ArrayList<>();

        char movingPiece = position.getPiece(rank, file);

        for (int[] offset : KING_OFFSETS) {

            int targetRank = rank + offset[0];
            int targetFile = file + offset[1];

            if (targetRank < 0 ||
                    targetRank >= 8 ||
                    targetFile < 0 ||
                    targetFile >= 8) {
                continue;
            }

            char targetPiece =
                    position.getPiece(
                            targetRank,
                            targetFile);

            if (sameColour(
                    movingPiece,
                    targetPiece)) {
                continue;
            }

            kingMoves.add(
                    new Move(
                            movingPiece,
                            rank,
                            file,
                            targetRank,
                            targetFile,
                            targetPiece,
                            '\0'
                    )
            );
        }

        kingMoves.addAll(
                generateCastlingMoves(
                        position,
                        rank,
                        file
                )
        );

        return kingMoves;
    }

    private List<Move> generateCastlingMoves(
            Position position,
            int rank,
            int file) {

        List<Move> moves = new ArrayList<>();

        char king = position.getPiece(rank, file);

        //white

        if (king == 'K') {

            // Kingside

            if (position.isWhiteKingSideCastling()) {

                if (position.getPiece(7, 5) == '.' &&
                        position.getPiece(7, 6) == '.' &&
                        position.getPiece(7, 7) == 'R') {

                    if (!isSquareAttacked(position, 7, 4, false) &&
                            !isSquareAttacked(position, 7, 5, false) &&
                            !isSquareAttacked(position, 7, 6, false)) {

                        Move castleMove = new Move(
                                'K',
                                7, 4,
                                7, 6,
                                '.',
                                '\0'
                        );

                        castleMove.setCastleMove(true);
                        moves.add(castleMove);
                    }
                }
            }

            // Queenside

            if (position.isWhiteQueenSideCastling()) {

                if (position.getPiece(7, 1) == '.' &&
                        position.getPiece(7, 2) == '.' &&
                        position.getPiece(7, 3) == '.' &&
                        position.getPiece(7, 0) == 'R') {

                    if (!isSquareAttacked(position, 7, 4, false) &&
                            !isSquareAttacked(position, 7, 3, false) &&
                            !isSquareAttacked(position, 7, 2, false)) {

                        Move castleMove = new Move(
                                'K',
                                7, 4,
                                7, 2,
                                '.',
                                '\0'
                        );

                        castleMove.setCastleMove(true);
                        moves.add(castleMove);
                    }
                }
            }
        }

        //black

        if (king == 'k') {

            // Kingside

            if (position.isBlackKingSideCastling()) {

                if (position.getPiece(0, 5) == '.' &&
                        position.getPiece(0, 6) == '.' &&
                        position.getPiece(0, 7) == 'r') {

                    if (!isSquareAttacked(position, 0, 4, true) &&
                            !isSquareAttacked(position, 0, 5, true) &&
                            !isSquareAttacked(position, 0, 6, true)) {

                        moves.add(
                                new Move(
                                        'k',
                                        0, 4,
                                        0, 6,
                                        '.',
                                        '\0'
                                )
                        );
                    }
                }
            }

            // Queenside

            if (position.isBlackQueenSideCastling()) {

                if (position.getPiece(0, 1) == '.' &&
                        position.getPiece(0, 2) == '.' &&
                        position.getPiece(0, 3) == '.' &&
                        position.getPiece(0, 0) == 'r') {

                    if (!isSquareAttacked(position, 0, 4, true) &&
                            !isSquareAttacked(position, 0, 3, true) &&
                            !isSquareAttacked(position, 0, 2, true)) {

                        moves.add(
                                new Move(
                                        'k',
                                        0, 4,
                                        0, 2,
                                        '.',
                                        '\0'
                                )
                        );
                    }
                }
            }
        }

        return moves;
    }

    private boolean isSquareAttacked(Position position, int rank, int file, boolean byWhite) {

        int pawnDir = byWhite ? -1 : 1;

        int pawnRank = rank + pawnDir;

        if (pawnRank >= 0 && pawnRank < 8) {

            if (file - 1 >= 0) {
                char p = position.getPiece(pawnRank, file - 1);
                if (byWhite && p == 'P') return true;
                if (!byWhite && p == 'p') return true;
            }

            if (file + 1 < 8) {
                char p = position.getPiece(pawnRank, file + 1);
                if (byWhite && p == 'P') return true;
                if (!byWhite && p == 'p') return true;
            }
        }


        for (int[] o : KNIGHT_OFFSETS) {
            int r = rank + o[0];
            int f = file + o[1];

            if (r < 0 || r >= 8 || f < 0 || f >= 8) continue;
            char p = position.getPiece(r, f);
            if (byWhite && p == 'N') return true;
            if (!byWhite && p == 'n') return true;
        }

        for (int[] o : KING_OFFSETS) {
            int r = rank + o[0];
            int f = file + o[1];

            if (r < 0 || r >= 8 || f < 0 || f >= 8) continue;
            char p = position.getPiece(r, f);

            if (byWhite && p == 'K') return true;
            if (!byWhite && p == 'k') return true;
        }

        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},   // rook lines
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}   // bishop lines
        };

        for (int[] d : directions) {

            int r = rank + d[0];
            int f = file + d[1];

            while (r >= 0 && r < 8 && f >= 0 && f < 8) {

                char p = position.getPiece(r, f);

                if (p != '.') {

                    // rook/queen lines
                    if ((d[0] == 0 || d[1] == 0)) {
                        if (byWhite && (p == 'R' || p == 'Q')) return true;
                        if (!byWhite && (p == 'r' || p == 'q')) return true;
                    }

                    // bishop/queen lines
                    if ((d[0] != 0 && d[1] != 0)) {
                        if (byWhite && (p == 'B' || p == 'Q')) return true;
                        if (!byWhite && (p == 'b' || p == 'q')) return true;
                    }

                    break;
                }

                r += d[0];
                f += d[1];
            }
        }
        return false;
    }

    private boolean sameColour(char piece1, char piece2) {
        if (piece1 == '.' || piece2 == '.') {
            return false;
        }

        return Character.isUpperCase(piece1)
                == Character.isUpperCase(piece2);
    }

    private boolean isEnemyPiece(
            char piece1,
            char piece2) {

        if (piece1 == '.' || piece2 == '.') {
            return false;
        }

        return Character.isUpperCase(piece1)
                != Character.isUpperCase(piece2);
    }

    // takes the position and the piece,
    // if it's an empty square, early return.
    // if it's white's turn to move, return true if the piece is a white piece
    // else it must be black's turn
    private boolean belongsToSideToMove(
            Position position,
            char piece
    ) {
        if (piece == '.') {
            return false;
        }

        if (position.isWhiteToMove()) {
            return isWhitePiece(piece);
        }

        return isBlackPiece(piece);
    }

    private boolean isWhitePiece(char piece) {
        return Character.isUpperCase(piece);
    }

    private boolean isBlackPiece(char piece) {
        return Character.isLowerCase(piece);
    }
}
