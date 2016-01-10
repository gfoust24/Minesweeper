import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Main extends JFrame {
	//TODO very slow with large numbers
	//TODO allow window resize via scrollpanel
	//TODO win condition
	//TODO smily
	//TODO clock
	//TODO mine count
	//TODO cancel left click on right click
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new Main();
	}

	JTextArea txt;//debug
	public Main() {
		super("Minesweeper");
		createMenuBar();
		//setLayout(new GridLayout(2, 1));
		setLayout(new BorderLayout());
		buildTopPanel();
		boardPanel = new JPanel();
		add(boardPanel, BorderLayout.PAGE_END);
		newGame();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//while the options window is open and main window gains focus, the window listener shifts focus to options window
		this.addWindowFocusListener(new MyWindowFocusListener());
		this.setVisible(true);
	}

	JMenuBar menuBar;
	JMenu file;
	JMenuItem file_new, file_options, file_quit;

	int difficulty = 2,	//modes: 0 for beginner, 1 for intermediate, 2 for expert, 3 for custom
			height, width, mines, flagsRemaining,
		gameState //0 = game running, 1 = won, -1 = lost
			;
	
	final int[] beginner = {9, 9, 10}, intermediate = {16, 16, 40}, expert = {16, 30, 99};
	int custom[] = {20, 30, 145};
	int[][] values = {beginner, intermediate, expert, custom};
	JPanel boardPanel;
	JLabel[][] labels;
	JLabel smile;
	String[][] board;
	Options options;
	ImageIcon imgTile = new ImageIcon("tile.png"), imgFlag = new ImageIcon("flag.png"),
			imgPressed = new ImageIcon("pressed.png"), imgMine = new ImageIcon("mine.png"),
			img0 = new ImageIcon("0.png"), img1 = new ImageIcon("1.png"), img2 = new ImageIcon("2.png"),
			img3 = new ImageIcon("3.png"), img4 = new ImageIcon("4.png"), img5 = new ImageIcon("5.png"),
			img6 = new ImageIcon("6.png"), img7 = new ImageIcon("7.png"), img8 = new ImageIcon("8.png"),
			img9 = new ImageIcon("9.png"), imgClock0 = new ImageIcon("clock0.png"),
			imgClock1 = new ImageIcon("clock1.png"), imgClock2 = new ImageIcon("clock2.png"),
			imgClock3 = new ImageIcon("clock3.png"), imgClock4 = new ImageIcon("clock4.png"),
			imgClock5 = new ImageIcon("clock5.png"), imgClock6 = new ImageIcon("clock6.png"),
			imgClock7 = new ImageIcon("clock7.png"), imgClock8 = new ImageIcon("clock8.png"),
			imgClock9 = new ImageIcon("clock9.png"), imgBoom = new ImageIcon("boom.png"),
			imgGlasses = new ImageIcon("glasses.png"), imgOmg = new ImageIcon("omg.png"),
			imgSmile = new ImageIcon("smile.png"), imgSmilePressed = new ImageIcon("smilePressed.png"),
			imgWrongFlag = new ImageIcon("wrongFlag.png"), imgDead = new ImageIcon("dead.png"),
			imgPressing = new ImageIcon("pressing.png");
	
	/**
	 * Create the menu bar for the main window
	 */
	private void createMenuBar() {
		//main menu bar object
		menuBar = new JMenuBar();
		
		//file submenu
		file = new JMenu("File");
		
		//"new" button for file menu 
		file_new = new JMenuItem("New");
		file_new.addActionListener(new MyMenuListener());
		file.add(file_new);
		
		//"options" button for file menu
		file_options = new JMenuItem("Options");
		file_options.addActionListener(new MyMenuListener());
		file.add(file_options);
		
		//"quit" button for file menu
		file_quit = new JMenuItem("Quit");
		file_quit.addActionListener(new MyMenuListener());
		file.add(file_quit);
		
		//add the file menu to the menu bar
		menuBar.add(file);
		//add the menu bar to the main window
		setJMenuBar(menuBar);

		setResizable(false);
	}
	
	/**
	 * Builds the top panel of the main window containing mine count, smily button, and timer
	 */
	JPanel mineCounter, smilie, clock;
	private void buildTopPanel() {
		FlowLayout leftAlign = new FlowLayout(FlowLayout.LEFT);
		leftAlign.setHgap(0);
		FlowLayout rightAlign = new FlowLayout(FlowLayout.RIGHT);
		rightAlign.setHgap(0);
		
		mineCounter = new JPanel(leftAlign);
		mineCounter.add(new JLabel(imgClock0));
		mineCounter.add(new JLabel(imgClock0));
		mineCounter.add(new JLabel(imgClock0));
		add(mineCounter, BorderLayout.LINE_START);
		
		smilie = new JPanel();
		smile = new JLabel(imgSmile);
		smile.addMouseListener(new MySmileListener());
		smilie.add(smile);
		add(smilie, BorderLayout.CENTER);
		
		clock = new JPanel(rightAlign);
		clock.add(new JLabel(imgClock0));
		clock.add(new JLabel(imgClock0));
		clock.add(new JLabel(imgClock0));
		
		add(clock, BorderLayout.LINE_END);
	}
	
	
	private class MyWindowFocusListener implements WindowFocusListener {

		@Override
		public void windowGainedFocus(WindowEvent arg0) {
			if (!isEnabled()) {
				Toolkit.getDefaultToolkit().beep();
				options.requestFocus();
			}
		}

		@Override
		public void windowLostFocus(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private void updateMineCounter() {
		//set number for mine counter
		mineCounter.removeAll();
		String strMines = "" + flagsRemaining;
		if (flagsRemaining < 0) {
			strMines = "0" + strMines.substring(strMines.lastIndexOf("-") + 1);
			strMines = "-" + strMines.substring(strMines.length() - 2);
		}
		else {
			strMines = "00" + flagsRemaining;
		}
		for (int i = strMines.length() - 3; i < strMines.length(); i++) {
			mineCounter.add(new JLabel(new ImageIcon("clock" + strMines.charAt(i) + ".png")));
		}
		mineCounter.validate();
	}
	
	/**
	 * Sets up the main window for a new game
	 */
	public void newGame() {
		//get game values and store them separately in case options are changed in the middle of a game 
		height = values[difficulty][0];
		width = values[difficulty][1];
		mines = values[difficulty][2];
		flagsRemaining = mines;
		
		updateMineCounter();
		//setSmile(imgSmile);
		//TODO smile doesnt stay dead after losing
		//flag set to check for firstClick
		firstClick = true;
		
		//array of labels that will be used to display the board
		labels = new JLabel[height][width];
		//remove existing pieces
		boardPanel.removeAll();
		//define spaces for the board
		boardPanel.setLayout(new GridLayout(height, width));
		JLabel currLabel;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//build each label
				currLabel = new JLabel(imgTile);
				currLabel.setName(i + "-" + j);
				currLabel.addMouseListener(new MyTileListener());
				//add the label to the array
				labels[i][j] = currLabel;
				//add the label to the window
				boardPanel.add(currLabel);
			}
		}
		gameState = 0;
		pack();
	}
	
	private class MyMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			
			if (source.equals(file_new)) {
				newGame();
			}
			else if (source.equals(file_options)) {
				setEnabled(false);
				
				options = new Options(Main.this);
				//txt.setText("Options " + arg0.getSource().hashCode() + "\n" + txt.getText());
			}
			else if (source.equals(file_quit)) {
				
			}
		}
		
	}
	/**
	 * 
	 * @param img = Image icon to set smile to
	 */
	public void setSmile(ImageIcon img) {
		/*
		 * TODO standardize setSmile vs smile.seticon calls
		Default: smile
		Ongoing
			- mouse down on tile - omg
			- mouse up on tile - smile
			- mouse down on smile - press smile
			- mouse up no smile - smile
		Dead
			- mouse down on tile - nothing
			- mouse up on tile - nothing
			- mouse down on smile - pressed smile
			- mouse up on smile - dead
		Won
			- glasses
		 */
		if (!smile.getIcon().equals(imgDead)) {
			smile.setIcon(img);
		}
	}
	
	private class MySmileListener implements MouseListener {
		boolean in = false, left = false;
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			Main.this.setTitle(Main.this.getTitle() + " cl");//debug
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			in = true;
			if (left) {
				smile.setIcon(imgSmilePressed);
			}
			Main.this.setTitle(Main.this.getTitle() + " en");//debug
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			in = false;
			setSmile(imgSmile);
			Main.this.setTitle(Main.this.getTitle() + " ex");//debug
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			left = true;
			smile.setIcon(imgSmilePressed);
			Main.this.setTitle(Main.this.getTitle() + " pr");//debug
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
			if (in) {
				newGame();
			}
			left = false;
			Main.this.setTitle(Main.this.getTitle() + " re");//debug
		}
		
	}
	
	JLabel lastLabel;
	private boolean left = false, right = false, firstClick = true;  
	public class MyTileListener implements MouseListener {
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			//If the game is won or lost, disable tile interactions
			if (gameState != 0)
				return;
			JLabel source = (JLabel) arg0.getSource();
			lastLabel = source;
			if (left && !right) {
				if (source.getIcon().equals(imgTile)) {
					source.setIcon(imgPressing);
				}
			}
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			//If the game is won or lost, disable tile interactions
			if (gameState != 0)
				return;
			JLabel source = (JLabel) arg0.getSource();
			//if (left && !right) {
				if (source.getIcon().equals(imgPressing)) {
					source.setIcon(imgTile);
				}
			//}
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			//If the game is won or lost, disable tile interactions
			if (gameState != 0)
				return;
			int button = arg0.getButton();
			JLabel source = (JLabel) arg0.getSource();
			//left pressed
			if (button == MouseEvent.BUTTON1) {
				left = true;
				if (left && !right) {
					setSmile(imgOmg);
					if (source.getIcon().equals(imgTile)) {
						source.setIcon(imgPressing);
					}
				}
			}
			//right pressed
			else if (button == MouseEvent.BUTTON3) {
				right = true;
				if (!left && right) {
					//flag placement
					if (source.getIcon().equals(imgTile)) {
						source.setIcon(imgFlag);
						flagsRemaining--;
						updateMineCounter();
						//win condition
						checkWin();
					}
					//flag removal
					else if (source.getIcon().equals(imgFlag)) {
						source.setIcon(imgTile);
						flagsRemaining++;
						updateMineCounter();
					}
						
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			//If the game is won or lost, disable button interactions
			if (gameState != 0)
				return;
			int button = arg0.getButton();
			//left release
			if (button == MouseEvent.BUTTON1) {
				left = false;
				if (!right) {
					setSmile(imgSmile);
					tileClick(lastLabel);
					
				}
			}
			//right release
			else if (button == MouseEvent.BUTTON3) {
				right = false;
					
			}
		}
		
		private void tileClick(JLabel source) {
			if (firstClick)
			{
				firstClick(source);
				firstClick = false;
			}
			//TODO standardize tile/button terminology
			
			//get x and y coordinate of tile clicked that are stored in the name of the label
			String[] name = source.getName().split("-");
			final int x = Integer.parseInt(name[0]), y = Integer.parseInt(name[1]);
			
			
			if (labels[x][y].getIcon().equals(imgTile) || labels[x][y].getIcon().equals(imgPressing))
			{
				switch (board[x][y])
				{
				//indicator for no bomb in current or surrounding tiles
				case "-":
					//make tile look like it was clicked
					labels[x][y].setIcon(imgPressed);
					//look at the surrounding tiles and click any that haven't been clicked
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							try {
								if (labels[x + i][y + j].getIcon().equals(imgTile))
									tileClick(labels[x + i][y + j]);
							}
							catch (ArrayIndexOutOfBoundsException e) {
								
							}
							catch (Exception al) {
								JOptionPane.showMessageDialog(null, "tileClick error");
							}
						}
					}
					break;
					
				//inidicator for a bomb
				case "*":
					//lose condition
					//display all mines on the board
					for (int i = 0; i < height; i++) {
						for (int j = 0; j < width; j++) {
							if (board[i][j].equals("*") && !labels[i][j].getIcon().equals(imgFlag)) {
								labels[i][j].setIcon(imgMine);
							}
							else if (!board[i][j].equals("*") && labels[i][j].getIcon().equals(imgFlag)) {
								labels[i][j].setIcon(imgWrongFlag);
							}
						}
					}
					//set the one that was clicked to the red background mine
					labels[x][y].setIcon(imgBoom);
					//'kill' the smilie
					setSmile(imgDead);
					//change game state to lose value
					gameState = -1;
					break;
				//catch all for numbered tiles
				default:
					//display the number corresponding to the number of mines around it
					try {
						Integer.parseInt(board[x][y]);
						labels[x][y].setIcon(new ImageIcon(board[x][y] + ".png"));
					}
					catch (Exception al) {
						JOptionPane.showConfirmDialog(null, "Error: 1");//TODO ?
					}
				}
				
			}
			//if not tile or pressing (flag or number tile)
			else {
				try {
					//is the current spot a number?
					int mineCount = Integer.parseInt(board[x][y]);
					
					//if it is, check the surrounding spots for flags
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							try {
								if (labels[x + i][y + j].getIcon().equals(imgFlag)) {
									mineCount--;
								}
							}
							catch (Exception al) {
								
							}
						}
					}
					//if the number of flags and mines is the same, click the surrounding unclicked blocks
					if (mineCount == 0) {
						for (int i = -1; i < 2; i++) {
							for (int j = -1; j < 2; j++) {
								try {
									if (labels[x + i][y + j].getIcon().equals(imgTile)) {
										tileClick(labels[x + i][y + j]);
									}
								}
								catch (Exception al) {
									
								}
							}
						}
					}
				}
				catch (Exception al) {
					JOptionPane.showConfirmDialog(null, "Error: 2");//TODO ?
				}
			}
			
			checkWin();
		}

		public void checkWin() {
			if (flagsRemaining == 0) {
				boolean win = true;
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						if (labels[i][j].getIcon().equals(imgTile)) {
							//JOptionPane.showMessageDialog(null, i + " " + j);//debug freezes tiles if used?
							win = false;
							break;
							
						}
					}
					if (!win)
						break;
				}
				if (win) {
					//JOptionPane.showMessageDialog(null, "you win");//debug
					setSmile(imgGlasses);
					gameState = 1;
				}
			}
		}
		
		private void firstClick(JLabel source) {
			
			board = new String[height][width];
			
			//x and y of location clicked
			String[] name = source.getName().split("-");
			final int x = Integer.parseInt(name[0]), y = Integer.parseInt(name[1]);
			
			//build a list of empty spaces
			ArrayList<Integer[]> openSpaces = new ArrayList<Integer[]>();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					board[i][j] = "-";
					//if there are 9 less mines than available spaces, the area surrounding the first click is not used
					if (mines + 9 <= height * width && (Math.abs(x - i) > 1 || Math.abs(y - j) > 1)) {
						openSpaces.add(new Integer[]{i, j});
					}
					//otherwise, the area around the first click can be used but not the first click tile
					else if (mines + 9 > height * width && (x != i || y != j)) {
						openSpaces.add(new Integer[]{i, j});
					}
				}
			}
			//using the list of open spaces, randomly pick from them and assign the rest of the mine locations
			while (mines > 0) {
				Integer[] spot = openSpaces.get((int)(Math.random() * openSpaces.size()));
				int i = spot[0], j = spot[1];
				board[i][j] = "*";
				mines--;
				openSpaces.remove(spot);
				//labels[i][j].setIcon(imgMine);//debug
			}
			//assign numbers
			int nearbyMines;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					//JOptionPane.showMessageDialog(null, i + " " + j);
					//if current spot is not empty, move to next spot
					if (board[i][j].equals("-")) {
						nearbyMines = 0;
						//check spaces around current space
						for (int k = -1; k < 2; k++) {
							for (int l = -1; l < 2; l++) {
								//if current spot is a mine, make it a blank location and increase mine count
								try {
									if (board[i + k][j + l].equals("*")) {
										nearbyMines += 1;
									}
								}
								catch (ArrayIndexOutOfBoundsException e) {
									
								}
								catch (Exception al) {
									JOptionPane.showMessageDialog(null, "assign numbers error");
								}
							}
						}
						if (nearbyMines > 0) {
							board[i][j] = nearbyMines + "";
						}
					}
				}
			}
		}
	}
}
