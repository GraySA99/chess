/**
 * Contains all information needed by the GUI about a particular move.
 * The board will generate a list of possible moves which the GUI will show.
 * The GUI will tell the board which move was selected.
 * @author Stephen S
 *
 */

public class Move {

	private Piece piece;
	public final int x;
	public final int y;
	public final boolean legal;
	public final boolean promotes;
	public final boolean castles;
	
	public final boolean captures;
	private Piece otherPiece;
	
	/**
	 * 
	 * @param piece that is moving
	 * @param x coordinate of the piece
	 * @param y coordinate of the piece
	 * @param legal if the move is legal
	 * @param captures if the move captures a piece
	 * @param promotes if the piece is a move and it promotes
	 * @param castles if the piece is a king/rook and it will castle
	 * @param otherPiece the piece that is captured (mostly for en passant)
	 */
	public Move(Piece piece, int x, int y, boolean legal, boolean captures, boolean promotes, boolean castles, Piece otherPiece) {
		this.piece = piece;
		this.x = x;
		this.y = y;
		this.legal = legal;
		this.promotes = promotes;
		this.castles = castles;
		
		this.captures = captures;
		this.otherPiece = otherPiece;
	}
	
	/**
	 * Constructor used for fake moves to check for in check
	 * @param piece
	 * @param x
	 * @param y
	 */
	public Move(Piece piece, int x, int y) {
		this.piece = piece;
		this.x = x;
		this.y = y;
		
		this.legal = false;
		this.promotes = false;
		this.castles = false;
		
		this.captures = false;
		this.otherPiece = null;
	}
	
	/**
	 * 
	 * @return the piece that is moving
	 */
	public Piece getPiece() {
		return piece;
	}
	
	/**
	 * 
	 * @return if en passant, the piece to capture; if castling, the other piece
	 */
	public Piece otherPiece() {
		return otherPiece;
	}

	/**
	 * Equals method for Moves
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Move)) {
			return false;
		}
		
		Move m = (Move) o;
		
		return m.piece.equals(this.piece) && m.x == this.x && m.y == this.y && m.legal == this.legal &&
				m.captures == this.captures && m.promotes == this.promotes && m.castles == this.castles &&
				m.otherPiece.equals(this.otherPiece);
	}
	
	/**
	 * Hashcode method for Moves
	 */
	@Override
	public int hashCode() {
		int res = 17;
		res = (res * 37) + piece.hashCode();
		res = (res * 37) + x;
		res = (res * 37) + y;
		res = (res * 37) + (legal ? 2 : 1);
		res = (res * 37) + (captures ? 2 : 1);
		res = (res * 37) + (promotes ? 2 : 1);
		res = (res * 37) + (castles ? 2 : 1);
		
		if (otherPiece != null) {
			res = (res * 37) + otherPiece.hashCode();
		}
		
		return res;
	}
}
