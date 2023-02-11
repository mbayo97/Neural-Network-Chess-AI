import java.util.HashMap;
import java.util.List;

public class OpeningBook {
  private HashMap<String, List<ChessMove>> book;

  public OpeningBook() {
    book = new HashMap<>();
    // Code to populate the opening book with pre-computed moves for various starting positions
  }

  public List<ChessMove> lookup(String fen) {
    return book.getOrDefault(fen, null);
  }
}

public void load() {
    // Code to load the weights from a file or initialize the weights randomly
  }

public class Minimax {
  private OpeningBook openingBook;

  // ... existing code for minimax algorithm ...

  public Minimax(OpeningBook openingBook) {
    this.openingBook = openingBook;
  }

  public ChessMove getBestMove(ChessBoard board) {
    String fen = board.getFEN();
    List<ChessMove> moves = openingBook.lookup(fen);
    if (moves != null && !moves.isEmpty()) {
      return moves.get(0);
    }

    ChessMove bestMove = null;
    int bestValue = Integer.MIN_VALUE;
    for (ChessMove move : board.getValidMoves()) {
      ChessBoard newBoard = board.makeMove(move);
      int newValue = minimax(newBoard, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
      if (newValue > bestValue) {
        bestValue = newValue;
        bestMove = move;
      }
    }

    return bestMove;
  }
}
