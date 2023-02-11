import java.util.Scanner;

public class ChessAI {
  private static final NeuralNetwork neuralNetwork = new NeuralNetwork();
  private static final MiniMax minMax = new MiniMax();
  private static final EndgameTablebase endgameTablebase = new EndgameTablebase();
  private static final OpeningBook openingBook = new OpeningBook();
  private ChessAI board;
  private boolean gameOver;

  public static void main(String[] args) {
    // Load the neural network
    neuralNetwork.load();

    // Load the MinMax algorithm
    minMax.load();

    // Load the endgame tablebase
    endgameTablebase.load();

    // Load the opening book
    openingBook.load();

    // Initialize the chess board
    String[][] board = new String[8][8];
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        board[i][j] = ".";
      }
    }

    Scanner scanner = new Scanner(System.in);
    while (true) {
      // Display the chess board
      displayBoard(board);

      // Check if the game is over
      if (isGameOver(board)) {
        System.out.println("The game is over.");
        break;
      }

      System.out.print("Enter your move (e.g. e2 e4): ");
      String userMove = scanner.nextLine();
      // Validate the user's move
      if (!isValidMove(userMove)) {
        System.out.println("Invalid move.");
        continue;
      }

      // Make the user's move on the chess board
      makeMove(board, userMove);

      // Check if the game is over
      if (isGameOver(board)) {
        System.out.println("The game is over.");
        break;
      }

      // Get the AI's move
      String aiMove = getAIMove(board);
      System.out.println("AI move: " + aiMove);

      // Make the AI's move on the chess board
      makeMove(board, aiMove);
    }

    scanner.close();
  }

  public void display() {
    System.out.println("  a b c d e f g h");
    for (int i = 0; i < 8; i++) {
      System.out.print((8 - i) + " ");
      for (int j = 0; j < 8; j++) {
        Piece piece = board[i][j];
        if (piece == null) {
          System.out.print("- ");
        } else {
          System.out.print(piece.getSymbol() + " ");
        }
      }
      System.out.println();
    }
  }

  public boolean isValidMove(String move) {
    // Check if the move is in the correct format (e.g. "e2-e4")
    if (!move.matches("^[a-h][1-8]-[a-h][1-8]$")) {
      System.out.println("Invalid move format. Please enter your move as 'a2-a4'.");
      return false;
    }

    // Extract the source and destination squares from the move
    int sourceFile = move.charAt(0) - 'a';
    int sourceRank = 8 - (move.charAt(1) - '0');
    int destFile = move.charAt(3) - 'a';
    int destRank = 8 - (move.charAt(4) - '0');

    // Check if the source square contains a piece belonging to the user
    Piece sourcePiece = board.getPiece(sourceFile, sourceRank);
    if (sourcePiece == null || sourcePiece.getColor() != Color.WHITE) {
      System.out.println("Invalid move. There is no piece to move or the piece does not belong to you.");
      return false;
    }

    // Check if the move is legal for the source piece
    if (!sourcePiece.canMove(board, sourceFile, sourceRank, destFile, destRank)) {
      System.out.println("Invalid move. The piece cannot move to the destination square.");
      return false;
    }

    // Check if the destination square is occupied by a piece of the same color
    Piece destPiece = board.getPiece(destFile, destRank);
    if (destPiece != null && destPiece.getColor() == Color.WHITE) {
      System.out.println("Invalid move. The destination square is occupied by a piece of your own color.");
      return false;
    }

    // All checks passed, the move is valid
    return true;
  }

  public void makeMove(String move) {
    int sourceFile = move.charAt(0) - 'a';
    int sourceRank = 8 - (move.charAt(1) - '0');
    int destFile = move.charAt(3) - 'a';
    int destRank = 8 - (move.charAt(4) - '0');

    Piece sourcePiece = board.getPiece(sourceFile, sourceRank);
    Piece destPiece = board.getPiece(destFile, destRank);

    board.setPiece(destFile, destRank, sourcePiece);
    board.setPiece(sourceFile, sourceRank, null);

    sourcePiece.setHasMoved(true);
  }

  public void checkGameOver() {
    boolean whiteKingCaptured = true;
    boolean blackKingCaptured = true;

    for (int file = 0; file < 8; file++) {
      for (int rank = 0; rank < 8; rank++) {
        Piece piece = board.getPiece(file, rank);

        if (piece instanceof King) {
          if (piece.getColor() == Color.WHITE) {
            whiteKingCaptured = false;
          } else {
            blackKingCaptured = false;
          }
        }
      }
    }

    gameOver = whiteKingCaptured || blackKingCaptured;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public Move getAIMove() {
    // First, check if the current position is in the endgame tablebase
    Move endgameMove = endgameTablebase.getBestMove(board);
    if (endgameMove != null) {
      return endgameMove;
    }

    // Next, check if the current position is in the opening book
    Move openingMove = openingBook.getBestMove(board);
    if (openingMove != null) {
      return openingMove;
    }

    // If the current position is not in the endgame tablebase or opening book,
    // use the MiniMax algorithm to search for the best move
    Move minimaxMove = miniMax(board, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

    // Finally, use the neural network to evaluate the resulting position and
    // potentially make further adjustments to the MiniMax move
    Move finalMove = neuralNetwork.evaluateMove(board, minimaxMove);

    return finalMove;
  }

  private Move miniMax(ChessBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
    if (depth == 0) {
      return neuralNetwork.evaluatePosition(board);
    }

    List<Move> moves = board.getAllMoves();
    if (maximizingPlayer) {
      Move bestMove = null;
      int bestValue = Integer.MIN_VALUE;

      for (Move move : moves) {
        board.makeMove(move);
        int value = miniMax(board, depth - 1, alpha, beta, false).getValue();
        board.undoMove(move);

        if (value > bestValue) {
          bestValue = value;
          bestMove = move;
        }

        alpha = Math.max(alpha, value);
        if (beta <= alpha) {
          break;
        }
      }

      return new Move(bestMove.getFromFile(), bestMove.getFromRank(), bestMove.getToFile(), bestMove.getToRank(), bestValue);
    } else {
      Move bestMove = null;
      int bestValue = Integer.MAX_VALUE;

      for (Move move : moves) {
        board.makeMove(move);
        int value = miniMax(board, depth - 1, alpha, beta, true).getValue();
        board.undoMove(move);

        if (value < bestValue) {
          bestValue = value;
          bestMove = move;
        }

        beta = Math.min(beta, value);
        if (beta <= alpha) {
          break;
        }
      }

      return new Move(bestMove.getFromFile(), bestMove.getFromRank(), bestMove.getToFile(), bestMove.getToRank(), bestValue);
    }
  }
}
