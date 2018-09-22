/**
 * King piece
 * Can move one tile in any direction
 * @author Stephen S
 *
 */
public class King extends Piece implements Castleable {
	
	private boolean canCastle;

	/**
	 * Calls superclass constructor
	 * @param isWhite color of the piece
	 */
	public King(boolean isWhite) {
		super(isWhite);
		canCastle = true;
	}

	/**
	 * Tells the king that he cannot castle
	 */
	@Override
	public void cannotCastle() {
		canCastle = false;
	}

	/**
	 * Asks if the king can castle
	 */
	@Override
	public boolean canCastle() {
		return canCastle;
	}
}
