/**
 * Abstract class from which all chess pieces will inherit
 * All pieces use Object's equals and hashcode methods, as two pieces are only equal if they have the same memory location
 * @author Stephen S
 *
 */
public abstract class Piece {
	
	public final boolean isWhite;
	
	/**
	 * Superclass constructor
	 * @param isWhite color of the piece
	 */
	public Piece(boolean isWhite) {
		this.isWhite = isWhite;
	}
	
	/**
	 * Pieces are only equal if they are the exact same piece
	 */	
}
