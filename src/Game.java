 import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controls an instance of a Game of chess
 * Has one board, on which it moves
 * Determines whether moves are legal
 * Does not have custom hashcode or equals because it does not keep track of whether two games that look the same are different
 * @author Stephen S
 *
 */
public class Game {
	
	public static final int NOT_OVER = 0;
	public static final int DRAW = 1;
	public static final int WHITE_WIN = 2;
	public static final int BLACK_WIN = 3;
	
	private Board board;
	private int turnsUntilDraw = 0;
	private final int TURNS_UNTIL_DRAW = 50;
	public HashMap<Piece, ArrayList<Move>> precalculatedMoves;
	
	//stored to check en passant
	private Pawn justDoubleMoved;

	/**
	 * Constructor
	 * @param board
	 */
	public Game(Board board) {
		this.board = board;
		precalculatedMoves = new HashMap<>();
		justDoubleMoved = null;
	}
	
	/**
	 * Checks if the game is over and stores the Moves lists it generates to save time later
	 * @return constant of board state
	 */
	public int isGameOver() {
		//50-move draw
		if (turnsUntilDraw >= TURNS_UNTIL_DRAW) {
			return DRAW;
		}
		
		boolean inCheck = inCheck(board, board.isWhiteTurn());
		
		if (inCheck) {
			//tell king he cannot castle
			for (int j = 0; j < board.board.length; j++) {
				Piece[] row = board.board[j];
				for (int i = 0; i < row.length; i++) {
					Piece p = row[i];
					if (p instanceof King && p.isWhite == board.isWhiteTurn()) {
						((King) p).cannotCastle();
					}
				}
			}
		}
		
		//see if any piece has a legal move
		for (Piece[] row : board.board) {
			for (Piece p : row) {
				if (p != null && p.isWhite == board.isWhiteTurn()) {
					ArrayList<Move> moves = getMoves(p);
					precalculatedMoves.put(p, moves);
					
					for (Move m : moves) {
						if (m.legal) {
							return NOT_OVER;
						}
					}
				}
			}
		}
		
		//if no legal moves and in check, checkmate
		if (inCheck) {
			if (board.isWhiteTurn()) {
				return BLACK_WIN;
			} else {
				return WHITE_WIN;
			}
		//else, no legal moves and not in check, stalemate
		} else {
			return DRAW;
		}
	}
	
	/**
	 * Finds moves for a given piece (valid and invalid)
	 * @param piece
	 * @return list of moves
	 */
	public ArrayList<Move> getMoves(Piece piece) {
		if (precalculatedMoves.containsKey(piece)) {
			return precalculatedMoves.get(piece);
		}
		
		ArrayList<Move> moves = new ArrayList<>();
		
		Point current = board.getLocation(piece);
		
		if (piece instanceof Pawn) {
			Pawn pawn = (Pawn) piece;
			
			int sign = pawn.isWhite ? 1 : -1;
			
			int nextY = current.y + sign;
			//should not need to check if in board since it should promotes before then
			boolean promotes = nextY == 0 || nextY == Board.SIDES - 1;
			
			//pawn moving in front of self
			int counter = 0;
			int tempNextY = nextY;
			do {
				tempNextY += counter * sign;
				
				if (board.get(current.x, tempNextY) != null) {
					break;
				}
				
				moves.add(new Move(pawn, current.x, tempNextY, !inCheck(board, new Move(pawn, current.x, tempNextY)), false, promotes, false, null));
				
				counter++;
				
				//for moving twice from the start
			} while (!pawn.hasMoved() && counter < 2);
			
			//pawn capturing diagonally
			for (int i = -1; i <= 1; i += 2) {
				int nextX = current.x + i;
				
				//position must be inside the board
				if (!board.isInBounds(nextX)) {
					continue;
				}
				
				Piece toKill = board.get(nextX, nextY);
				
				//must be a piece of the other team's color at that position
				Piece enPassant = null;
				if (toKill == null || toKill.isWhite == pawn.isWhite) {
					//pawn capturing with en passant
					enPassant = board.get(nextX, current.y);
					
					if (!(enPassant != null && enPassant.equals(justDoubleMoved))) {
						continue;
					}
				}
				
				//we only get here if we have a valid capture in this iteration, either regular or en passant
				
				moves.add(new Move(pawn, nextX, nextY, !inCheck(board, new Move(pawn, nextX, nextY)), true, promotes, false, enPassant));
				
			}
		} else if (piece instanceof Knight) {
			
			//generate all points an L away, then make sure they are in bounds, then make sure they are good
			
			int[] possibles = {-2, -1, 1, 2};
			
			final int CAP = 3;
			
			//loop through x values
			for (int deltaX : possibles) {
				//get two y values for each x value
				for (int deltaY = -1; deltaY <= 1; deltaY += 2) {
					
					//get which value it is from x, get the sign of y from i
					int y = current.y + deltaY * (CAP - Math.abs(deltaX));
					int x = current.x + deltaX;
					
					//if both are valid positions
					if (board.isInBounds(x) && board.isInBounds(y)) {
						Piece other = board.get(x, y);
						
						//if the place is empty or the other is of the opposing color
						if (other == null || other.isWhite != piece.isWhite) {
							
							moves.add(new Move(piece, x, y, !inCheck(board, new Move(piece, x, y)), other != null, false, false, null));
						}
					}
				}
			}
		} else {
			
			//diagonal moving
			if (piece instanceof Bishop || piece instanceof Queen) {
				
				//do the same thing for each of the four diagonal directions
				for (int x = -1; x <= 1; x += 2) {
					for (int y = -1; y <= 1; y += 2) {
						
						//start moving in the diagonal once
						int curX = current.x + x;
						int curY = current.y + y;
						
						//keep moving diagonally until we go out of bounds or a break;
						while (board.isInBounds(curX) && board.isInBounds(curY)) {
							Piece target = board.get(curX, curY);
							
							//if same color as target piece, break out before adding as a move
							if (target != null && target.isWhite == piece.isWhite) {
								break;
							}
							
							moves.add(new Move(piece, curX, curY, !inCheck(board, new Move(piece, curX, curY)), target != null, false, false, null));
							
							//if different color as target piece, break out before add as move
							if (target != null && target.isWhite != piece.isWhite) {
								break;
							}
							
							curX += x;
							curY += y;
						}
					}
				}
			}
			
			//horizontal/vertical moving
			if (piece instanceof Rook || piece instanceof Queen) {
				
				//all four cardinal directions
				int possibles[][] = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
				
				for (int[] points : possibles) {
					int x = points[0];
					int y = points[1];
					
					//start moving once
					int curX = current.x + x;
					int curY = current.y + y;
					
					//keep moving until we go out of bounds or a break;
					while (board.isInBounds(curX) && board.isInBounds(curY)) {
						Piece target = board.get(curX, curY);
						
						//if same color as target piece, break out before adding as a move
						if (target != null && target.isWhite == piece.isWhite) {
							break;
						}
						
						moves.add(new Move(piece, curX, curY, !inCheck(board, new Move(piece, curX, curY)), target != null, false, false, null));
						
						//if different color as target piece, break out before add as move
						if (target != null && target.isWhite != piece.isWhite) {
							break;
						}
						
						curX += x;
						curY += y;
					}
				}
			}
		}
		
		if (piece instanceof King) {
			
			//possible moves for a king from center
			int possibles[][] = { {1, 1},
								  {1, 0},
								  {1, -1},
								  {0, 1},
								  {0, -1},
								  {-1, 1},
								  {-1, 0},
								  {-1, -1} };
			
			for (int[] diff : possibles) {
				int x = current.x + diff[0];
				int y = current.y + diff[1];
			
				//if position is in bounds
				if (board.isInBounds(x) && board.isInBounds(y)) {
					Piece other = board.get(x, y);
					
					//if king can move to the other location
					if (other == null || other.isWhite != piece.isWhite) {

						moves.add(new Move(piece, x, y, !inCheck(board, new Move(piece, x, y)), other != null, false, false, null));
						
					}
				}
			}
			
			King king = (King) piece;
			
			if (king.canCastle()) {
				
				//get rooks to castle with
				for (Piece[] row : board.board) {
					for (Piece toTest : row) {
						if (toTest instanceof Rook && toTest.isWhite == piece.isWhite) {
							Rook r = (Rook) toTest;
							
							if (r.canCastle()) {
								
								Point kingLoc = board.getLocation(king);
								
								int diff = kingLoc.x - board.getLocation(r).x;
								
								// decide which direction to move to check empty locations
								int deltaX = diff > 0 ? -1 : 1;
								
								boolean blocked = false;
								for (int i = 1; i <= 2; i++) {
									int curX = kingLoc.x + deltaX * i;
									if (board.get(curX, kingLoc.y) != null || inCheck(board, king.isWhite, curX, kingLoc.y)) {
										blocked = true;
										break;
									}
								}
								
								moves.add(new Move(king, kingLoc.x + deltaX * 2, kingLoc.y, !blocked, false, false, true, r));
								
							}
						}
					}
				}
			}
		}
		
		precalculatedMoves.put(piece, moves);
		return moves;
	}
	
	/**
	 * Executes a given move on the game board
	 * @param m move
	 */
	public void move(Move m) {
		precalculatedMoves.clear();
		
		//used to check if previous move was a pawn double move
		int previousY = board.getLocation(m.getPiece()).y;
		
		board.move(m);
		
		//reset 50-move draw counter if pawn move or capture; otherwise, increment
		if (m.captures || m.getPiece() instanceof Pawn) {
			turnsUntilDraw = 0;
		} else {
			turnsUntilDraw += 1;
		}
		
		//reset en passant
		justDoubleMoved = null;
		
		//tell pawns they have moved
		if (m.getPiece() instanceof Pawn && !((Pawn) m.getPiece()).hasMoved()) {
			
			Pawn p = (Pawn) m.getPiece();
			
			p.didMove();
			
			int diff = board.getLocation(p).y - previousY;
			
			if (diff == 2 || diff == -2) {
				justDoubleMoved = p;
			}
		
		//tells castleables they cannot castle
		} else if (m.getPiece() instanceof Castleable) {
			
			((Castleable) m.getPiece()).cannotCastle();
		}
		
		board.switchTurns();
	}
	
	/**
	 * For a given board, after moving, determines if the king of the team who moved is now in check
	 * @param original the board before the move ( needs to be copied )
	 * @param m the move to test
	 * @return
	 */
	private static boolean inCheck(Board original, Move m) {
		//make a copy of the board, run the test move on the board, and see if the king is in check
		Board b = new Board(original);
		boolean isWhiteTurn = b.isWhiteTurn();
		b.move(m);
		return inCheck(b, isWhiteTurn);
	}
	
	/**
	 * For a given board, determines if the given team's king is in check
	 * @param b the board
	 * @param isWhiteKing whose king it is
	 * @return if the given team's king is in check
	 */
	private static boolean inCheck(Board b, boolean isWhiteKing) {
		//find the king and call inCheck at the king's position
		for (int j = 0; j < b.board.length; j++) {
			Piece[] row = b.board[j];
			for (int i = 0; i < row.length; i++) {
				Piece p = row[i];
				if (p instanceof King && p.isWhite == isWhiteKing) {
					return inCheck(b, isWhiteKing, i, j);
				}
			}
		}
		throw new IllegalArgumentException("No king found... ???");
	}
	
	/**
	 * For a given board, determines if (x, y) is in check from the player whose turn it is
	 * @param b the board
	 * @param isWhiteOfDefendingPlayer if the defending player is white
	 * @param x position of place to check
	 * @param y position of place to check
	 * @return if the given position is in check
	 */
	private static boolean inCheck(Board b, boolean isWhiteOfDefendingPlayer, int x, int y) {
		//loop through board and see if opposing pieces can attack (x,y)
		for (int j = 0; j < b.board.length; j++) {
			Piece[] row = b.board[j];
			
			for (int i = 0; i < row.length; i++) {
				Piece p = row[i];
				
				if (p != null && p.isWhite != isWhiteOfDefendingPlayer) {
					
					int diffX = Math.abs(x - i);
					int diffY = Math.abs(y - j);
					
					if (p instanceof Pawn) {
						
						//if directly diagonally from each other
						if (diffX == 1 && diffY == 1) {
							//if pawn can attack location
							int sign = p.isWhite ? 1 : -1;
							
							if (j + sign == y) {
								return true;
							}
						}
					} else if (p instanceof Knight) {
						
						//if knight can attack (x, y)
						if ( (diffX == 2 && diffY == 1) || (diffY == 2 && diffX == 1) ) {
							return true;
						}
					} else if (p instanceof King) {
						
						//if king can attack (x, y)
						if (diffX <= 1 && diffY <= 1) {
							return true;
						}
					} else {
						if (p instanceof Bishop || p instanceof Queen) {
							//if in same diagonal
							if (diffX == diffY) {
								
								int deltaX = x > i ? -1 : 1;
								int deltaY = y > j ? -1 : 1;
								
								//if nothing is blocking the diagonal, we are in check
								boolean blocked = false;
								
								for (int counterX = x + deltaX, counterY = y + deltaY; !( Math.abs(counterX - i) < 1 && Math.abs(counterY - j) < 1 ); counterX += deltaX, counterY += deltaY) {
									if (b.get(counterX, counterY) != null) {
										blocked = true;
										break;
									}
								}
								
								if (!blocked) {
									return true;
								}
							}
						}
						if (p instanceof Rook || p instanceof Queen) {
							//if in same horizontal
							if (diffY == 0) {
								int minX = Math.min(x, i);
								int maxX = Math.max(x, i);
								
								//if nothing is blocking the horizontal, we are in check
								boolean blocked = false;
								for (int counter = minX + 1; counter < maxX; counter++) {
									if (b.get(counter, y) != null) {
										blocked = true;
										break;
									}
								}
								
								if (!blocked) {
									return true;
								}
								
							//if in same vertical
							} else if (diffX == 0) {
								int minY = Math.min(y, j);
								int maxY = Math.max(y, j);
								
								//if nothing is blocking the vertical, we are in check
								boolean blocked = false;
								for (int counter = minY + 1; counter < maxY; counter++) {
									if (b.get(x, counter) != null) {
										blocked = true;
										break;
									}
								}
								
								if (!blocked) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
}
