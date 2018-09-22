import java.awt.Point;
import java.util.Arrays;

/**
 * Board class that uses a 2D array to hold the current chess board
 * @author Stephen S
 *
 */
public class Board {
	
	public Piece[][] board;
	private boolean whiteTurn;
	public static final int SIDES = 8;
	
	/**
	 * Constructor
	 * Creates the starting configuration of chess
	 */
	public Board() {
		whiteTurn = true;
		
		board = new Piece[SIDES][];
		
		board[0] = homeRow(true);
		
		board[1] = new Piece[SIDES];
		for (int i = 0; i < SIDES; i++) {
			board[1][i] = new Pawn(true);
		}
		
		for (int i = 2; i < SIDES - 2; i++) {
			board[i] = new Piece[SIDES];
		}
		
		board[SIDES - 2] = new Piece[SIDES];
		for (int i = 0; i < SIDES; i++) {
			board[SIDES - 2][i] = new Pawn(false);
		}
		
		board[SIDES - 1] = homeRow(false);
	}
	
	/**
	 * Returns a starting row for the given color
	 * @param white
	 * @return
	 */
	private Piece[] homeRow(boolean white) {
		return new Piece[] {new Rook(white),
							new Knight(white),
							new Bishop(white),
							new Queen(white),
							new King(white),
							new Bishop(white),
							new Knight(white),
							new Rook(white) };
	}
	
	/**
	 * Copy constructor
	 * @param other board instance
	 */
	public Board(Board other) {
		this.whiteTurn = other.whiteTurn;
		
		this.board = new Piece[other.board.length][];
		
		for (int i = 0; i < other.board.length; i++) {
			Piece[] row = other.board[i];
			this.board[i] = new Piece[row.length];
			
			for (int j = 0; j < row.length; j++) {
				// note that this does not copy pieces, but uses the original pieces
				// this is necessary because piece equality is memory location
				// Board never mutates pieces - only Game mutates pieces
				// Board copies are only used to check if a possible move will result in check
				this.board[i][j] = row[j];
			}
		}
	}
	
	/**
	 * Executes a given (valid) move
	 * @param m move
	 */
	public void move(Move m) {
		
		//mostly used for en passant capturing
		if (m.captures && m.otherPiece() != null) {
			remove(getLocation(m.otherPiece()));
		}
		
		//remove the piece from its current location
		Point current = getLocation(m.getPiece());
		remove(current);
		
		//perform a castle
		if (m.castles) {
			Piece rook = m.otherPiece();
			Point rookLoc = getLocation(rook);
			
			int diff = current.x - rookLoc.x;
			int deltaX = diff > 0 ? -1 : 1;
			
			//king
			board[m.y][m.x] = m.getPiece();
			
			board[m.y][m.x - deltaX] = rook;
			
			remove(rookLoc);
			
		//promote a pawn
		} else if (m.promotes) {
			board[m.y][m.x] = new Queen(m.getPiece().isWhite);
			
		//otherwise, simply move a piece (all other captures taken care of, as it replaces previous piece)
		} else {
			board[m.y][m.x] = m.getPiece();
		}
	}
	
	/**
	 * Tells if an int is in bounds of the board
	 * Assumes side lengths are the same, so same method works for x and y
	 * @param i x or y value
	 * @return true if 0 <= i <= sidelength - 1
	 */
	public boolean isInBounds(int i) {
		return i >= 0 && i <= SIDES - 1;
	}
	
	/**
	 * Gives the location of the piece
	 * @param piece
	 * @return returns the (x,y) coordinates of the piece
	 */
	public Point getLocation(Piece piece) {
		for (int y = 0; y < board.length; y++) { 
			for (int x = 0; x < board[y].length; x++) {
				if (board[y][x] != null && board[y][x].equals(piece)) {
					return new Point(x, y);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the piece at (x, y)
	 * @param x
	 * @param y
	 * @return the value at (x, y)
	 */
	public Piece get(int x, int y) {
		
		return board[y][x];
	}
	
	/**
	 * Remove a piece from the p given
	 * @param p
	 */
	public void remove(Point p) {
		board[p.y][p.x] = null;
	}
	
	/**
	 * 
	 * @return if it is white's turn
	 */
	public boolean isWhiteTurn() {
		return whiteTurn;
	}
	
	public void switchTurns() {
		whiteTurn = !whiteTurn;
	}
	
	/**
	 * Checks if two boards are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o instanceof Board) {
			Board b = (Board) o;
			
			return this.whiteTurn == b.whiteTurn && Arrays.deepEquals(this.board, b.board);
		}
		return false;
	}
	
	/**
	 * int cannot hold all possible chess boards, but we'll just use the builtin array hash code and call it a day
	 */
	@Override
	public int hashCode() {
		return Arrays.deepHashCode(board) + (whiteTurn ? 1 : 0);
	}

}
