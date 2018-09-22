/**
 * Pawn piece
 * Can move forward
 * Can move twice in one turn if still in starting position
 * Can capture diagonally
 * Can en passant capture
 * Can promote to a queen
 * @author Stephen S
 *
 */
public class Pawn extends Piece {

	private boolean moved;

	/**
	 * Calls superclass constructor
	 * @param isWhite color of the piece
	 */
	public Pawn(boolean isWhite) {
		super(isWhite);
		moved = false;
	}
	
	/**
	 * 
	 * @return whether the pawn has moved
	 */
	public boolean hasMoved() {
		return moved;
	}
	
	/**
	 * Tells this class that the pawn has moved
	 */
	public void didMove() {
		moved = true;
	}
	
}
