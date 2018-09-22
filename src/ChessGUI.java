import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

/**
 * Graphical User Interface Class
 * @author Spencer G
 *
 * This class incorporates the Game and Board class found within the package.
 * A JFrame is constructed and a chess board is made using Boards list of Pieces
 */

public class ChessGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private final int LENGTH;
	private final int WIDTH;
	private Board myBoard;
	private Game myGame;
	private JPanel panel;
	private JButton[][] buttons;
	private HashMap<String, ImageIcon> imageMap;
	private ArrayList<Move> moves;

	/**
	 * Constructs local variables and calls startUp to display the chess board before playing
	 */
	public ChessGUI() {

		myBoard = new Board();
		myGame = new Game(myBoard);
		LENGTH = myBoard.board.length;
		WIDTH = myBoard.board[0].length;
		buttons = new JButton[LENGTH][WIDTH];
		setupImageMap();
		moves = null;
		this.startUp();
	}

	/**
	 * Fills imageMap with the ImageIcons for all chess Pieces and assigns a String to each for easy access
	 */
	private void setupImageMap() {

		imageMap = new HashMap<String, ImageIcon>();

		imageMap.put("wPawn", new ImageIcon("images/WPawn.png"));
		imageMap.put("bPawn", new ImageIcon("images/BPawn.png"));
		imageMap.put("wKing", new ImageIcon("images/WKing.png"));
		imageMap.put("bKing", new ImageIcon("images/BKing.png"));
		imageMap.put("wQueen", new ImageIcon("images/WQueen.png"));
		imageMap.put("bQueen", new ImageIcon("images/BQueen.png"));
		imageMap.put("wBishop", new ImageIcon("images/WBishop.png"));
		imageMap.put("bBishop", new ImageIcon("images/BBishop.png"));
		imageMap.put("wRook", new ImageIcon("images/WRook.png"));
		imageMap.put("bRook", new ImageIcon("images/BRook.png"));
		imageMap.put("wKnight", new ImageIcon("images/WKnight.png"));
		imageMap.put("bKnight", new ImageIcon("images/BKnight.png"));
		imageMap.put("blankPiece", null);
	}

	/**
	 * Called every time a JButton is clicked
	 * If a button at location p is clicked and contains a Piece, then moves will contain all the available Move class instances (both legal and illegal) for that Piece at location p
	 *    all buttons which share a location with a Move in moves for that Piece at location p are then highlighted (blue for legal, red for illegal)
	 * If a Piece is selected and a blue highlighted button at location p is clicked, then that Piece will be moved to location p
	 * If a Piece is selected at location p and then another Piece at location k is selected, then onClick is called again for Point k
	 * Turns are visually displayed to the player through window text via this method
	 * Every time a Piece is moved, then moves gets reset to null
	 * 
	 * @param p is the location of a space on the chess board that was clicked
	 */
	private void onClick(Point p) {

		boolean isWhiteTurn = myBoard.isWhiteTurn();
		Piece dummyPiece = myBoard.get(p.y, p.x);
		
		//if no moves generated for a previous piece
		if(moves == null) {

			//if we clicked on a piece whose turn it is
			if(dummyPiece != null && (isWhiteTurn == dummyPiece.isWhite)) {

				//get moves for that piece
				moves = myGame.getMoves(dummyPiece);

				//show the moves on the UI
				for(Move m : moves) {

					if(m.legal) {

						buttons[m.x][m.y].setBackground(Color.BLUE);
					} else {

						buttons[m.x][m.y].setBackground(Color.RED);
					}
				}

			} 

		//if there are moves generated for a previous piece
		} else  {

			boolean moved = false;

			//see if this click on one of those moves
			for(Move m : moves) {

				//if the user clicked on a legal move
				if(m.legal && m.x == p.y && m.y == p.x) {

					//perform the move
					myGame.move(m);
					moved = true;
					createGrid();
					moves = null;

					if(isWhiteTurn) {

						this.setTitle("Black Turn");
					} else {

						this.setTitle("White Turn");
					}

					break;
				}
			}

			//if the user clicked elsewhere
			if(!moved) {

				//if the user clicked on another piece of the same color
				if(dummyPiece != null && (isWhiteTurn == dummyPiece.isWhite)) {

					//reset and recursively call this method to trigger the initial highlighting
					createGrid();
					moves = null;
					onClick(p);
				}
			}
		}
	}

	/**
	 * Disables all JButtons within JButton[][] buttons
	 */
	private void disableButtons() {

		for(int y = 0; y < buttons.length; y++) {
			for(int x = 0; x < buttons[y].length; x++) {

				buttons[y][x].setEnabled(false);
			}
		}
	}

	/**
	 * Visually sets up the board before any play happens
	 */
	private void startUp() {

		panel = new JPanel(new GridLayout(LENGTH,WIDTH));
		createGrid();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("White Turn");
		this.add(panel);
		this.pack();
		this.setVisible(true);

	}

	/**
	 * JButton[][] buttons is reset and then filled with ImageIcons corresponding with Pieces in myBoard.board;
	 * The buttons are then given an ActionListener that calls onClick(Point p) with its location, p, as a new Point(x,y)
	 * Then each buttons is added to panel in proper order, then the panel is added to the JFrame, then the frame is displayed
	 * Lastly, if there is any game over conditions, then they are handled accordingly and that game over condition is displayed in a new frame
	 */
	private void createGrid() {

		buttons = new JButton[LENGTH][WIDTH];

		//alternates for each row
		boolean blackspace = true;
		
		for(int y = 0; y < buttons.length; y++) {
			for(int x = 0; x < buttons[y].length; x++) {

				String mapID;
				Piece myPiece = myBoard.get(y, x);

				//set the button's image
				if(myPiece == null) {

					buttons[y][x] = new JButton(imageMap.get("blankPiece"));
				} else {

					if(myPiece.isWhite) {

						mapID = "w" + myPiece.getClass().getSimpleName();
					} else {

						mapID = "b" + myPiece.getClass().getSimpleName();
					}

					buttons[y][x] = new JButton(imageMap.get(mapID));
				}

				//set the button's background color
				if(blackspace == (x%2 == 0)) {
					
					buttons[y][x].setBackground(Color.DARK_GRAY);
				} else {

					buttons[y][x].setBackground(Color.WHITE);
				}

				buttons[y][x].setOpaque(true);

				Point p = new Point(x, y);

				//set the button's event handler
				buttons[y][x].addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						onClick(p);
					}
				});
			}
			blackspace = !blackspace;
		}

		//build the panel
		panel.removeAll();

		for(int y = 0; y < buttons.length; y++) {
			for(int x = 0; x < buttons[y].length; x++) {

				panel.add(buttons[x][y]);
			}
		}

		this.pack();

		int gameOver = myGame.isGameOver();

		//build game over frame
		if(gameOver > Game.NOT_OVER) {

			JFrame endGameFrame = new JFrame();
			endGameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			endGameFrame.setSize(450, 300);
			endGameFrame.setTitle("GAMEOVER");
			JLabel label = new JLabel("THIS TEXT SHOULD NEVER APPEAR, BUT IF IT DOES, HBOX IS CLEARLY THE WORST MELEE PLAYER TO WATCH");

			this.disableButtons();

			if(gameOver == Game.DRAW) {

				label.setText("Draw");
			} else if(gameOver == Game.WHITE_WIN) {

				label.setText("White Wins");
			} else if(gameOver == Game.BLACK_WIN){

				label.setText("Black Wins");
			}

			label.setFont(new Font("Courier",Font.BOLD,250));

			endGameFrame.add(label);
			endGameFrame.pack();
			endGameFrame.toFront();
			endGameFrame.setVisible(true);
		}
	}

	/**
	 * Hashcode
	 */
	@Override
	public int hashCode() {

		int total = 17;

		total += (serialVersionUID * 37);
		total += (LENGTH * 37);
		total += (WIDTH * 37);
		total += myBoard.hashCode();
		total += myGame.hashCode();
		total += panel.hashCode();
		total += buttons.hashCode();
		total += imageMap.hashCode();

		if(moves != null) {
			
			total += moves.hashCode();
		}

		return total;
	} 
	
	/**
	 * Equals
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof ChessGUI) {
			
			ChessGUI o = (ChessGUI)obj;
			
			return (this.myBoard.equals(o.myBoard) && this.myGame.equals(o.myGame)
					&& this.panel.equals(o.panel) && this.buttons.equals(o.buttons)
					&& this.imageMap.equals(o.imageMap));
		}
		
		return false;
	}
}
