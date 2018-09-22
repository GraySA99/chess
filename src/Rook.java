/**
 * Rook piece
 * Can move linearly
 * @author Stephen S
 *
 */
public class Rook extends Piece implements Castleable {
	
	private boolean canCastle;

	/**
	 * Calls superclass constructor
	 * @param isWhite color of the piece
	 */
	public Rook(boolean isWhite) {
		super(isWhite);
		canCastle = true;
	}

	/**
	 * Tells the rook that he cannot castle
	 */
	@Override
	public void cannotCastle() {
		canCastle = false;
	}

	/**
	 * Asks if the rook can castle
	 */
	@Override
	public boolean canCastle() {
		return canCastle;
	}
}
