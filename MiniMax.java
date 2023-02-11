import java.util.List;

public class MiniMax {
  private static final int MAX_DEPTH = 3;

  private int evaluate(ChessBoard board) {
    // Code to evaluate the current state of the chess board and return a score
    // This score can be based on material balance, mobility, etc.
    return 0;
  }

  public void load() {
    // Code to load the weights from a file or initialize the weights randomly
  }

  public ChessMove getBestMove(ChessBoard board) {
    int bestScore = Integer.MIN_VALUE;
    ChessMove bestMove = null;

    List<ChessMove> moves = board.getValidMoves();
    for (ChessMove move : moves) {
      board.makeMove(move);
      int score = minValue(board, MAX_DEPTH - 1);
      board.undoMove(move);
      if (score > bestScore) {
        bestScore = score;
        bestMove = move;
      }
    }

    return bestMove;
  }

  private int maxValue(ChessBoard board, int depth) {
    if (board.isGameOver() || depth == 0) {
      return evaluate(board);
    }

    int bestScore = Integer.MIN_VALUE;

    List<ChessMove> moves = board.getValidMoves();
    for (ChessMove move : moves) {
      board.makeMove(move);
      bestScore = Math.max(bestScore, minValue(board, depth - 1));
      board.undoMove(move);
    }

    return bestScore;
  }

  private int minValue(ChessBoard board, int depth) {
    if (board.isGameOver() || depth == 0) {
      return evaluate(board);
    }

    int bestScore = Integer.MAX_VALUE;

    List<ChessMove> moves = board.getValidMoves();
    for (ChessMove move : moves) {
      board.makeMove(move);
      bestScore = Math.min(bestScore, maxValue(board, depth - 1));
      board.undoMove(move);
    }

    return bestScore;
  }
}
