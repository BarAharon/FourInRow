import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Stack;
import java.util.*;


public class Game extends JFrame implements ActionListener{

	private static final int  human = 1;    
	private static final int  computer = 2;  
	private static int startPlayer = 1; 
	private int turn;
	
	
	private static final int  humanVshuman = 1;
	private static final int  humanVscomputer = 2;
	private static final int easy = 1;
	private static final int medium = 3;
	private static final int hard = 5;	
	private static final int regular = 1;
	private static final int opposite = 2;
	private static final int startNewGame = 1;	
	private static final int loadOldGame = 2;
	private static int count1 = 0;
	private static int count2 = 0;
	private static int type;
	private static int level;
	private static int gameType;
	private static int newOrOldGame;
	
		
	private Button [][] gBoard;
	private int [][] lBoard;
	private int [] counter;
	int rows = 0;
    int columns = 0;
	int rezef = 0;
	int bestCol;
	
	JMenuBar jmb = new JMenuBar();
	//JMenuItem save = new JMenuItem("save");
	//JMenuItem load = new JMenuItem("load");
	JMenuItem undo = new JMenuItem("undo");
	JMenuItem redo = new JMenuItem("redo");	
	JMenuItem newGame = new JMenuItem("new game");
	JMenuItem save = new JMenuItem("save");
	
	Stack st = new Stack();
	Stack stRedo = new Stack();	
			
	public Game()	         
	{	
		menu0();
		if (newOrOldGame == loadOldGame)
		{
			LoadGame();
		}
		else
		{
			menu1();
			menu2();
			menu3();
			if(type == humanVscomputer)
			{
			    menu4();
			}
			initBoard();
		}
		
		setTitle("FourInRow");		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500,450);
		setVisible(true);
		
		setJMenuBar(jmb);
		jmb.add(newGame);
		jmb.add(save);		
		jmb.add(undo);
		jmb.add(redo);	
		
		
		newGame.addActionListener(this);
		save.addActionListener(this);		
		undo.addActionListener(this);	
		redo.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == newGame)
		{
			//setVisible(false); // Clears game frame
			// 26-1-2014 MS
		    dispose();// Frees JFrame and subcomponents
			// making then undisplayable...
			//Change start player every game
			initBoard();
			turn = human;
			new Game();
		}
		else if (e.getSource() == save)
		{
			SaveGame();
		}		
		else if (e.getSource() == undo)
		{
			if (type == humanVscomputer)
			{
				Undo();
			}
			Undo();
		}
		else if (e.getSource() == redo)
		{
			if (type == humanVscomputer)
			{
				Redo();
			}
			Redo();
		}
	}
	
	public void LoadGame() 
	{
		// TODO Auto-generated method stub
		try
		{
	       	// Open file to read from, named SavedObj.sav.
	       	JFileChooser c = new JFileChooser();
	      	int r = c.showDialog(Game.this, "Open file to set object");
	    	if(r == JFileChooser.APPROVE_OPTION)
	    	{
	         	File f = c.getSelectedFile();
	           	FileInputStream loadFile = new FileInputStream(f);

	           	// Create an ObjectInputStream to get objects from save file.
	        	ObjectInputStream load = new ObjectInputStream(loadFile);

	         	// Now we do the restore.
	           	// readObject() returns a generic Object, we cast those back
		        // into their original class type.
		        // For primitive types, use the corresponding reference class.	        	
	        	rows = (int) load.readObject();
	        	columns = (int) load.readObject();
	        	initBoard();
	        	rezef = (int) load.readObject();	        	
           		lBoard = (int[][]) load.readObject();
           		counter = (int[]) load.readObject();	        	
           		startPlayer = (int) load.readObject();	
	         	gameType =  (int) load.readObject();
	        	type =  (int) load.readObject();
	        	if (load.read() != -1)
	        	{
	        		level = (int) load.readObject();
	        	}	        	
	         	updateVisualBoard();

	           	// Close the file.
	        	load.close(); // This also closes saveFile.
	    	}
		} 
		catch(Exception ex)
		{
	    	ex.printStackTrace(); // If there was an error, print the info.
		}
	}
	
	public void SaveGame() 
	{
		// TODO Auto-generated method stub
		try
		{  // Catch errors in I/O if necessary.
	    	JFileChooser c = new JFileChooser();
	    	int r = c.showDialog(Game.this, "Create file to save object");
    		if(r == JFileChooser.APPROVE_OPTION)
	    	{
	        	File f = c.getSelectedFile();
	        	// Open a file to write to, named line.sav.
	        	FileOutputStream saveFile=new FileOutputStream(f);

	        	// Create an ObjectOutputStream to put objects into save file.
         		ObjectOutputStream save = new ObjectOutputStream(saveFile);

	          	// Now we do the save.
	        	save.writeObject(rows);
	        	save.writeObject(columns);
	        	save.writeObject(rezef);
	        	save.writeObject(lBoard);
	        	save.writeObject(counter);
	        	save.writeObject(turn);
	        	save.writeObject(gameType);
	        	save.writeObject(type);
	        	if (type == humanVscomputer)
	        	{
	            	save.writeObject(level);
	        	}

	        	// Close the file.
	        	save.close(); // This also closes saveFile.	        	
	     	} 
		}
		catch(Exception ex)
		{
	      	ex.printStackTrace(); // If there was an error, print the info.
		}
	}	
	
	public void Redo()
	{
		int row, col;
	    if (!stRedo.isEmpty())
	    {	    	
	        col = (int) stRedo.pop();
	        row = (int) stRedo.pop();
	        st.push(row);
	        st.push(col);
	        lBoard[row][col] = turn;
	        counter[col]--;
	        updateVisualBoard();
	        turn = 3 - turn;
	    }
	}
	
	public void updateVisualBoard()
	{
		ImageIcon red, yellow, white;
		Image img;
		white = new ImageIcon("white.png");
		red = new ImageIcon("red.jpg");
		yellow = new ImageIcon("yellow.jpg");
      	for (int i = 0 ; i < rows ; i++)
      	{
	        for (int j = 0;  j < columns ; j++)
	        {
            	if(this.lBoard[i][j] == 0)
            	{
            		img = white.getImage();
                	this.gBoard[i][j].setImg(img);                	
            	}
	            else
	            {
                	if(this.lBoard[i][j] == 1)
                	{
                		img = red.getImage();
                    	this.gBoard[i][j].setImg(img);
                	}
	                else
	                {
	                	img = yellow.getImage();
	                    this.gBoard[i][j].setImg(img);
	                }	               
              	}
            	this.gBoard[i][j].repaint();
	        }
      	}
	}
	
	public void Undo()
	{
	    int row, col;
	    if (!st.isEmpty())
	    {	    	
	        col = (int) st.pop();
	        row = (int) st.pop();
	        stRedo.push(row);
	        stRedo.push(col);
	        lBoard[row][col] = 0;
	        counter[col]++;
	        updateVisualBoard();
	        turn = 3 - turn;
	    }
	}

	public void menu0()
	{
		String[] options =  {"new game", "load game", "Exit"};
		int response = JOptionPane.showOptionDialog(null, "Choose new or load game", 
				"Starting Game Options",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);
		switch(response)
		{		    
			case -1:
				System.out.println("Option Dialog Window Was Closed");
				System.exit(0);
			
			case 0: 
				newOrOldGame = startNewGame;
				break;
				
			case 1:
				newOrOldGame = loadOldGame;
                break;
                
			case 2:
				System.exit(0);
                
			default:
				break;				
		}
	}
	
	public void menu1()
	{
		boolean done;
		String s;
		do
		{
			try
			{
				s = JOptionPane.showInputDialog(null, "enter number of rows");
				rows = Integer.parseInt(s);				
				done = true;
			}
			catch(NumberFormatException ex)
			{
				done = false;
				JOptionPane.showInputDialog(null, "wrong input");
			}
		}while(!done || rows < 2);
		done = false;
		do
		{
			try
			{
				s = JOptionPane.showInputDialog(null, "enter number of columns");
				columns = Integer.parseInt(s);
				done = true;
			}
			catch(NumberFormatException ex)
			{
				done = false;
				JOptionPane.showInputDialog(null, "wrong input");
			}
		}while(!done || columns < 2);
		done = false;
		do
		{
			try
			{
				s = JOptionPane.showInputDialog(null, "enter number of rezef");
				rezef = Integer.parseInt(s);
				done = true;
			}
			catch(NumberFormatException ex)
			{
				done = false;
				JOptionPane.showInputDialog(null, "wrong input");
			}
		}while(!done || rezef < 2 ||(rezef > rows && rezef > columns));
	}
	
	public void menu2()
	{
		String[] options =  {"regular", "opposite", "Exit"};
		int response = JOptionPane.showOptionDialog(null, "Choose Type Game", 
				"Starting Game Options",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);
		switch(response)
		{		    
			case -1:
				System.out.println("Option Dialog Window Was Closed");
				System.exit(0);
			
			case 0: 
				gameType = regular;
				break;
				
			case 1:
				gameType = opposite;
                break;
                
			case 2:
				System.exit(0);
                
			default:
				break;				
		}
	}
	
	public void menu3()
	{		
		String[] options =  {"humanVsHuman", "humanVscomputer", "Exit"};
		int response = JOptionPane.showOptionDialog(null, "Choose Type Game", 
				"Starting Game Options",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);
		int num = type;
		switch(response)
		{		    
			case -1:
				System.out.println("Option Dialog Window Was Closed");
				System.exit(0);
			
			case 0:
				type = humanVshuman;
				if (num == humanVscomputer)
				{
					count1 = 0;
					count2 = 0;
				}
				break;
			case 1:
				type = humanVscomputer;
				if (num == humanVshuman)
				{
					count1 = 0;
					count2 = 0;
				}
				break;
			
			case  2:
				System.exit(0);
				
			default:
				break;		
		}
	}
	
	public void menu4()
	{
		String[] options =  {"easy", "medium", "hard"};
		int response = JOptionPane.showOptionDialog(null, "Choose Difficulty", 
				"Difficulty Options",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);
		switch(response)
		{	
	    	case -1:
		    	System.out.println("Option Dialog Window Was Closed");
		    	System.exit(0);
		
			case 0:
				level = easy;				
				break;
				
			case 1:
			    level = medium;
				break;
			
			case 2:
                level = hard;
                break;
                
			default:
				break;		
		}
	}
	
	public void initBoard()
	{
		gBoard = new Button[rows][columns];
		lBoard = new int[rows][columns];
		counter = new int[columns];
		
		setLayout(new GridLayout(rows, columns));
		
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < columns; j++)
			{
				lBoard[i][j] = 0;	
				ImageIcon icon = new ImageIcon("white.png");
				Image img = icon.getImage();
				gBoard[i][j] = new Button(img);
				gBoard[i][j].addActionListener(new AL(i,j));
				add(gBoard[i][j]);	
			}	
		}
		for (int i = 0; i < columns; i++)
		{
		    counter[i] = rows - 1;
		}
		turn=startPlayer;
		
		// 30-1-2016
		if(startPlayer==computer && type==humanVscomputer)
		{			    	   
			  ComputerMove();
		}
	}	
	
	public boolean isBoardFull()
	{
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				if(lBoard[i][j] == 0)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	class AL implements ActionListener
	{
		private int row;
		private int col;
		
		public AL(int row, int col)
		{			
			this.row = row;			
			this.col = col;			
			
		}
		
		public void actionPerformed(ActionEvent e)
		{	
			if(type == humanVscomputer)
			{				
				   if(counter[col] > -1 && lBoard[counter[col]][col] == 0)
				   { // free cell
					   lBoard[counter[col]][col] = human;
					   st.push(counter[col]);
					   st.push(col);
					   while(!stRedo.isEmpty())
					   {
						   stRedo.pop();
					   }
					   new Thread(new Runnable() 
					   {
	    				      public void run()
		    			      {
			    		         try
			    		         {
			    		        	 ImageIcon icon, white;
		    		     		  	 Image img ;		    		     		  	
		    						 icon = new ImageIcon("red.jpg");		    						 	
		    		     		  	 white = new ImageIcon("white.png");
			    		        	 for (int i = 0; i <= counter[col] + 1; i++)
			    		        	 {		   		        		 
			    		        		
			    						 img = icon.getImage();
			    		            	 gBoard[i][col].setImg(img);					
			    	    				 gBoard[i][col].repaint();
			        		             Thread.sleep(50);		
			        		             if (i != counter[col] + 1)
			        		             {
			        		                 img = white.getImage();
			        		                 gBoard[i][col].setImg(img);					
			    	    			    	 gBoard[i][col].repaint();
			    	    				 }
			    	    				 
			    		        	 }	
			    		        	 	    		        	
				    	         }
					             catch(InterruptedException ex)
			    		         {
					        	  
			    		         }
					          }
					      }).start();
					   
					      turn = 3 - turn;
					      counter[col]--;					      
					      CheckWin(counter[col] + 1, col);					      				      
						  if(isBoardFull())
						  {
							  endStartGame(0);
						  }	
						  //ComputerMoveStratagy();	
						  if (gameType == regular)
						  {
					    	  ComputerMove();
						  }
						  else
						  {
							  ComputerMoveOp();
						  }
	  		       
				     }
					
				  
		    }
			else //  type==humanVshuman
			{
				 if(counter[col] > -1 && lBoard[counter[col]][col] == 0)
				 { // free cell
						
					  lBoard[counter[col]][col] = turn;		
					  st.push(counter[col]);
					  st.push(col);						
					  while(!stRedo.isEmpty())
					  {
						   stRedo.pop();
					  }
					  new Thread(new Runnable() 
					  {
    				      public void run()
	    			      {
		    		         try
		    		         {
		    		        	 ImageIcon icon, white;
	    		     		  	 Image img ;
	    		     		  	 if(turn == 1)
	    						 {
	    							 icon = new ImageIcon("yellow.jpg");
	    						 }
	    					     else
	    					     {
	    							 icon = new ImageIcon("red.jpg");
	    					     }	    	
	    		     		  	 white = new ImageIcon("white.png");
		    		        	 for (int i = 0; i <= counter[col] + 1; i++)
		    		        	 {		   		        		 
		    		        		
		    						 img = icon.getImage();
		    		            	 gBoard[i][col].setImg(img);					
		    	    				 gBoard[i][col].repaint();
		        		             Thread.sleep(50);		
		        		             if (i != counter[col] + 1)
		        		             {
		        		                 img = white.getImage();
		        		                 gBoard[i][col].setImg(img);					
		    	    			    	 gBoard[i][col].repaint();
		    	    				 }
		    	    				 
		    		        	 }
			    	         }
				             catch(InterruptedException ex)
		    		         {
				        	  
		    		         }
				          }
				      }).start();
					
					 
				 }	
				  turn = 3 - turn;
				  counter[col]--;				  
			      CheckWin(counter[col] + 1, col);			      	
				  if(isBoardFull())
				  {
					  endStartGame(0);
				  }	
				  
			}
			
	    }
	}
	
	public void ComputerMove()
	{		  		   
		   new Thread(new Runnable() 
		   {
			      public void run()
			      {
  		             try
  		             {  		            	
  		            	int num;
  		        	    Thread.sleep(80 * rows);	
  		        		final int col;  		        		
  		        		num = CheckOneForWin();
  		        		if (num == -1)
  		        		{  	 		        	 	    	 		        			    		
  		        		    negaMax(level, 2);
  		        			col = bestCol;  
  		        			st.push(counter[col]);
  						    st.push(col);  						   
  					  	    while(!stRedo.isEmpty())
  						    {
  							   stRedo.pop();
  						    }
  		        			PutCircle(col);  			        		
  		                 }
  		        		 else
  		        		 {  		        			
  		        			col = num;  		        			
  		        			st.push(counter[col]);
  						    st.push(col);  						  
  						    while(!stRedo.isEmpty())
  						    {
  							   stRedo.pop();
  						    }
  		        			PutCircle(col);
  		        		 }
  			    		 turn = 3 - turn;	
  			    		 counter[col]--;   			 
  			    		 CheckWin(counter[col] + 1, col);
  						 if(isBoardFull())
  						 {
  							 endStartGame(0);
  						 }  						 
  		         }
  		         catch(InterruptedException ex)
  		         {
		        	  
  		         }
		      }
		   }).start();
	}
	
	public void ComputerMoveOp()
	{		  		   
		   new Thread(new Runnable() 
		   {
			      public void run()
			      {
  		             try
  		             {  
  		        	    Thread.sleep(80 * rows);	
  		        		final int col;   		        			    		
  		        		negaMaxOp(level, 2);
  		        		col = bestCol;  
  		        		PutCircle(col);  
  			    	    turn = 3 - turn;	
  			    		counter[col]--;   			 
  			    		CheckWin(counter[col] + 1, col);
  						if(isBoardFull())
  						{
  							endStartGame(0);
  						}  						 
  		         }
  		         catch(InterruptedException ex)
  		         {
		        	  
  		         }
		      }
		   }).start();
	}
	
	void PutCircle(int col)
	{
		lBoard[counter[col]][col] = computer;
   		
  		new Thread(new Runnable() 
   		{
	    		public void run()
	    		{
  	    	    try
  		        {    		        
  		                 ImageIcon icon, white;
		         		 Image img ;		    		     		  	
			    		 icon = new ImageIcon("yellow.jpg");		    						 	
		         		 white = new ImageIcon("white.png");
      		         for (int i = 0; i <= counter[col] + 1; i++)
      		         { 	  	
  	    			 	 img = icon.getImage();
      		             gBoard[i][col].setImg(img);					
      	    			 gBoard[i][col].repaint();
           		         Thread.sleep(50);		
           		         if (i != counter[col] + 1)
            		         {
             		             img = white.getImage();
          		             gBoard[i][col].setImg(img);					
  	          			     gBoard[i][col].repaint();
  	         			 }			    	    				 
  	      	         }	
	        	     }
		             catch(InterruptedException ex)
  		         {
	 	        	  
      		     }
	     	      }
  		  }).start();
	}
	
	boolean IsLegal(int i, int j)
	{
	    return (i >= 0 && i < rows && j >= 0 && j < columns);  
	}

	public boolean CheckRow(int r, int c)
	{
		int seed = lBoard[r][c];
		int counter = 0;
		for (int j = c - rezef; j < c + rezef; j++) 
		{	    	
			if (IsLegal(r, j))
			{
				if (lBoard[r][j] == seed)
				{
					counter++;
				}
				else
				{
					counter = 0;	
				}
				if(counter == rezef)
				{
					return true;
				}
			}
		}
		return false;
	}
		
	public boolean CheckColumn(int r, int c)
	{		
		int seed = lBoard[r][c];
		int counter = 0;
		for (int i = r - rezef; i < r + rezef; i++) 
		{	    	
			if (IsLegal(i, c))
			{
				if (lBoard[i][c] == seed)
				{
					counter++;
				}
				else
				{
					counter = 0;	
				}
				if(counter == rezef)
				{
					return true;
				}
			}
		}
		return false;	
		
		
	}
	
	public boolean CheckMainDiagonal(int r, int c)
	{
		int seed = lBoard[r][c];
		int counter = 0;
		for (int i = r - rezef, j = c - rezef; i < r + rezef && j < c + rezef; i++, j++)	
		{					
			if(IsLegal(i, j))					
		    {
	        	if (lBoard[i][j] == seed)
	            {
	              	counter++;
	            }
	        	else
		        {
		           	counter = 0;	
		        }
		       	if (counter == rezef)
		        {		         		
		    	  	return true;
		        }	
		    }
		}		
		return false;
	
	}
	
	public boolean CheckSecondaryDiagonal(int r, int c)
	{
		int seed = lBoard[r][c];
		int counter = 0;
		for (int i = r + rezef, j = c - rezef; i > r - rezef && j < c + rezef; i--, j++)
		{			
			if(IsLegal(i, j))					
		    {
	        	if (lBoard[i][j] == seed)
                 {
                  	counter++;
                }
	        	else
		        {
		         	counter = 0;	
		        }
		        if (counter == rezef)
		        {	         		
		    	   	return true;
		        }	
		    }			
		}
		return false;		
	}
	
	public void CheckWin(int r, int c)
	{
		int seed;
		if (r > -1 && c > -1)
		{			
	        seed = lBoard[r][c];
      		if (CheckRow(r, c) || CheckColumn(r, c) || CheckMainDiagonal(r, c) || CheckSecondaryDiagonal(r, c))
	     	{	
	     		if (gameType == regular)
	     		{
	         		endStartGame(seed);
		     	}
		     	else
		     	{
		      		endStartGame(3 - seed);
		      	}
	     	}
		}
	}		
	
	
	public int CheckOneForWinRow(int c)
	{
		int j;
		int [] monim = new int[3];
		int r = counter[c];
		
		//right		
		monim[0] = monim[1] = monim[2] = 0;		
		for(j = 0; c + j < columns && j < rezef; j++)
		{
			monim[lBoard[r][c + j]]++;
		}
		
		if(j == rezef)
		{
			if (monim[1] >= rezef - 1 && monim[2] == 0)
			{
				return 1;
			}
			if (monim[2] >= rezef - 1 && monim[1] == 0)
			{
			    return 2;
			}
		}
		
		//left		
		monim[0] = monim[1] = monim[2] = 0;
		for(j = 0; c - j >= 0 && j < rezef; j++)
		{
			monim[lBoard[r][c - j]]++;
		}
		
		if(j == rezef)
		{
			if (monim[1] >= rezef - 1 && monim[2] == 0)
			{
				return 1;
			}
			if (monim[2] >= rezef - 1 && monim[1] == 0)
			{
			    return 2;
			}
		}
		return 0;
	}
		
	public int CheckOneForWinColumn(int c)
	{		
		int  j;
		int [] monim = new int[3];
		int r = counter[c];		
		monim[0] = monim[1] = monim[2] = 0;
		
		for(j = 0; r + j < rows && j < rezef; j++)
		{
			monim[lBoard[r + j][c]]++;
		}
		
		if(j == rezef)
		{
			if (monim[1] >= rezef - 1 && monim[2] == 0)
			{
				return 1;
			}
			if (monim[2] >= rezef - 1 && monim[1] == 0)
			{
			    return 2;
			}
		}
		return 0;
	}
	
	public int CheckOneForWinMainDiagonal(int c)
	{
		int j, i;
		int [] monim = new int[3];
		int r = counter[c];		
		
		//right
		monim[0] = monim[1] = monim[2] = 0;
		for(i = 0, j = 0; r + i < rows && c + j < columns && j < rezef && i < rezef; i++, j++)
		{
			monim[lBoard[r + i][c + j]]++;
		}
		
		if(j == rezef && i == rezef)
		{
			if (monim[1] >= rezef - 1 && monim[2] == 0)
			{
				return 1;
			}
			if (monim[2] >= rezef - 1 && monim[1] == 0)
			{
			    return 2;
			}
		}
				
		monim[0] = monim[1] = monim[2] = 0;
		for(i = 0, j = 0; r - i >= 0 && c - j >= 0 && j < rezef && i < rezef; i++, j++)
		{
			monim[lBoard[r - i][c - j]]++;
		}
		
		if(j == rezef && i == rezef)
		{
			if (monim[1] >= rezef - 1 && monim[2] == 0)
			{
				return 1;
			}
			if (monim[2] >= rezef - 1 && monim[1] == 0)
			{
				return 2;
			}
		}
		
		return 0;
	}
	
	public int CheckOneForWinSecondaryDiagonal(int c)
	{
		int j, i;
		int [] monim = new int[3];
		int r = counter[c];		
		
		//right
		monim[0] = monim[1] = monim[2] = 0;
		for(i = 0, j = 0; r + i < rows && c - j >= 0 && j < rezef && i < rezef; i++, j++)
		{
			monim[lBoard[r + i][c - j]]++;
		}
		
		if(j == rezef && i == rezef)
		{

			if (monim[1] >= rezef - 1 && monim[2] == 0)
			{
				return 1;
			}
			if (monim[2] >= rezef - 1 && monim[1] == 0)
			{
			    return 2;
			}
		}		
		
		monim[0] = monim[1] = monim[2] = 0;
		for(i = 0, j = 0; r - i >= 0 && c + j < columns && j < rezef && i < rezef; i++, j++)
		{
			monim[lBoard[r - i][c + j]]++;
		}
		if(j == rezef && i == rezef)
		{
			if (monim[1] >= rezef - 1 && monim[2] == 0)
			{
				return 1;
			}
			if (monim[2] >= rezef - 1 && monim[1] == 0)
			{
				return 2;
			}
		}
		
		return 0;
	}
	
	public int CheckOneForWin()
	{	
		int num1;
		int num2;
		int num3;
		int num4;
		int flag = -1;
		
		for (int j = 0; j < columns; j++)
		{
			if(counter[j] != -1 && lBoard[counter[j]][j] == 0)
			{
				num1 = CheckOneForWinRow(j);
				num2 = CheckOneForWinColumn(j);
				num3 = CheckOneForWinMainDiagonal(j);
				num4 = CheckOneForWinSecondaryDiagonal(j);
				
		    	if (num1 == 2 || num2 == 2 || num3 == 2 || num4 == 2)
		    	{
			    	return j;
			    }
		    	else
		    	{
		    		if (num1 == 1 || num2 == 1 || num3 == 1 || num4 == 1)
		    		{
		    			flag = j;
		    		}
		    	}
			}
		}
		
		return flag;
	}
		
	public int negaMaxOp(int depth, int turn)
	{
		int best = Integer.MAX_VALUE;
		int val, col;
		
		if (depth == 0)
			return -GradeBoard();
		 
		for (col = 0; col < columns; col++)
		{
			if (counter[col] >= 0)
			{
				lBoard[counter[col]][col] = turn;
				counter[col]--;
				val = -negaMax(depth - 1 ,3 - turn);	
				counter[col]++;
				lBoard[counter[col]][col] = 0;
				if (val < best)
				{
					best = val;				
		    		if (depth == level)
		    		{
		     			bestCol = col;
		    		}
				}
			}
		}
		return best;
	}
	
	public int negaMax(int depth, int turn)
	{
		int best = Integer.MIN_VALUE;
		int val, col;		
		
		if (depth == 0)
			return -GradeBoard();
		 
		for (col = 0; col < columns; col++)
		{
			if (counter[col] >= 0)
			{
				lBoard[counter[col]][col] = turn;
				counter[col]--;
				val = -negaMax(depth - 1 ,3 - turn);	
				counter[col]++;
				lBoard[counter[col]][col] = 0;
				if (val > best)
				{
					best = val;				
		    		if (depth == level)
		    		{
		     			bestCol = col;
		    		}
				}
			}
		}
		return best;
	}
	
	public int GradeBoard()
	{
		int mark = 0;
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < columns; j++)
			{
				mark += GradeCell(i, j);				
			}
		}		
		
		return mark;
	}
	
	public int GradeCell(int row, int col)
	{			
		int mark = 0;			
		mark += GradeCellRows(row, col);
		mark += GradeCellColumns(row, col);
		mark += GradeCellMainDiagonal(row, col);
		mark += GradeCellSecondaryDiagonal(row, col);			
		return mark;
	}
	
	public int GradeCellRows(int r, int c)
	{		
		int mark1 = 0, mark2 = 0, j;
		int [] monim = new int[3];
		
		//right
		monim[0] = monim[1] = monim[2] = 0;
		for(j = 0; c + j < columns && j < rezef; j++)
		{
			monim[lBoard[r][c + j]]++;
		}
		
		if(j == rezef)
		{
			if (monim[1] > 0 && monim[2] == 0)
			{
				mark1 += (int)Math.pow(10, monim[1]);
			}
			if (monim[2] > 0 && monim[1] == 0)
			{
				mark2 += (int)Math.pow(10, monim[2]);
			}
			
			//like _11__
			if (monim[1] == rezef - 2)
			{
				if (lBoard[r][c] == 0 && c + j < columns && lBoard[r][c+j-1] == 0 && lBoard[r][c+j] == 0)
				{
					mark1 += (int)Math.pow(10, monim[1] + 1);
				}
			}
			
			//like _22__
			if (monim[2] == rezef - 2)
			{
				if (lBoard[r][c] == 0 && c + j < columns && lBoard[r][c+j-1] == 0 && lBoard[r][c+j] == 0)
				{
					mark2 += (int)Math.pow(10, monim[2] + 1);
					mark2 = mark2 / 2;
				}
			}		
		}
		
		//left
		monim[0] = monim[1] = monim[2] = 0;
		for(j = 0; c - j >= 0 && j < rezef; j++)
		{
			monim[lBoard[r][c - j]]++;
		}
		
		if(j == rezef)
		{
			if (monim[1] > 0 && monim[2] == 0)
			{
				mark1 += (int)Math.pow(10, monim[1]);
			}
			if (monim[2] > 0 && monim[1] == 0)
			{
				mark2 += (int)Math.pow(10, monim[2]);
			}
			
			//like _11__
			if (monim[1] == rezef - 2)
			{
				if (lBoard[r][c] == 0 && c - j >= 0 && lBoard[r][c-j+1] == 0 && lBoard[r][c-j] == 0)
				{
					mark1 += (int)Math.pow(10, monim[1] + 1);
				}
			}
			
			//like _22__
			if (monim[2] == rezef - 2)
			{
				if (lBoard[r][c] == 0 && c - j >= 0 && lBoard[r][c-j+1] == 0 && lBoard[r][c-j] == 0)
				{
					mark2 += (int)Math.pow(10, monim[2] + 1);
					mark2 = mark2 / 2;
				}
			}	
		}		
		
		return mark2 - mark1;
	}
	
	public int GradeCellColumns(int r, int c)
	{			
		int mark1 = 0, mark2 = 0, j;
		int [] monim = new int[3];
				
		monim[0] = monim[1] = monim[2] = 0;
		
		for(j = 0; r + j < rows && j < rezef; j++)
		{
			monim[lBoard[r + j][c]]++;
		}
		
		if(j == rezef)
		{
			if (monim[1] > 0 && monim[2] == 0)
			{
				mark1 += (int)Math.pow(10, monim[1]);
			}
			if (monim[2] > 0 && monim[1] == 0)
			{
				mark2 += (int)Math.pow(10, monim[2]);
			}	
		}
				
		return mark2 - mark1;		
	}
	
	public int GradeCellMainDiagonal(int r, int c)
	{			
		int mark1 = 0, mark2 = 0, j, i;
		int [] monim = new int[3];
		
		//right
		monim[0] = monim[1] = monim[2] = 0;
		for(i = 0, j = 0; r + i < rows && c + j < columns && j < rezef && i < rezef; i++, j++)
		{
			monim[lBoard[r + i][c + j]]++;
		}
		
		if(j == rezef && i == rezef)
		{
			if (monim[1] > 0 && monim[2] == 0)
			{
				mark1 += (int)Math.pow(10, monim[1]);
			}
			if (monim[2] > 0 && monim[1] == 0)
			{
				mark2 += (int)Math.pow(10, monim[2]);
			}
			
			/*like _
			 *      1			       
			 *       1
			 *        _
			 *         _
			 */
			if (monim[1] == rezef - 2)
			{
				if (lBoard[r][c] == 0 && c + j < columns && r + i < rows && lBoard[r+i-1][c+j-1] == 0 && lBoard[r+i][c+j] == 0)
				{
					mark1 += (int)Math.pow(10, monim[1] + 1);
				}
			}
			
			/*like _
			 *      2			       
			 *       2
			 *        _
			 *         _
			 */
			if (monim[2] == rezef - 2)
			{
				if (lBoard[r][c] == 0 && c + j < columns && r + i < rows && lBoard[r+i-1][c+j-1] == 0 && lBoard[r+i][c+j] == 0)
				{
					mark2 += (int)Math.pow(10, monim[2] + 1);
					mark2 = mark2 / 2;
				}
			}	
			
		}	
				
		return mark2 - mark1;
	}
	
	public int GradeCellSecondaryDiagonal(int r, int c)
	{			
		
		int mark1 = 0, mark2 = 0, j, i;
		int [] monim = new int[3];
		
		//left
		monim[0] = monim[1] = monim[2] = 0;
		for(i = 0, j = 0; r + i < rows && c - j >= 0 && j < rezef && i < rezef; i++, j++)
		{
			monim[lBoard[r + i][c - j]]++;
		}
		
		if(j == rezef && i == rezef)
		{
			if (monim[1] > 0 && monim[2] == 0)
			{
				mark1 += (int)Math.pow(10, monim[1]);
			}
			if (monim[2] > 0 && monim[1] == 0)
			{
				mark2 += (int)Math.pow(10, monim[2]);
			}
			
			/*like     _
			 *        1			       
			 *       1
			 *      _
			 *     _
			 */
			if (monim[1] == rezef - 2)
			{
				if (lBoard[r][c] == 0 && c - j >= 0 && r - i >= 0 && lBoard[r-i+1][c-j+1] == 0 && lBoard[r-i][c-j] == 0)
				{
					mark1 += (int)Math.pow(10, monim[1] + 1);
				}
			}
			
			/*like     _
			 *        2			       
			 *       2
			 *      _
			 *     _
			 */
			if (monim[2] == rezef - 2)
			{
				if (lBoard[r][c] == 0 && c - j >= 0 && r - i >= 0 && lBoard[r-i+1][c-j+1] == 0 && lBoard[r-i][c-j] == 0)
				{
					mark2 += (int)Math.pow(10, monim[2] + 1);
					mark2 = mark2 / 2;
				}
			}
		}		
			
		return mark2 - mark1;
	}
			
	public void endStartGame (int play)
	{
		  if(play==0)
		  {
		      JOptionPane.showMessageDialog(Game.this,"It's a tie");
		  }
		  else
		  {
			  if (type == humanVshuman)
			  {
				  JOptionPane.showMessageDialog(Game.this,"player "+(play==1?"red":"yellow")+" win");
			  }
			  else
			  {
				  JOptionPane.showMessageDialog(Game.this, (play==1?"player":"     computer")+" win");
			  }		
			  if (play == 1)
			  {
				  count1++;
			  }
			  else
			  {
				  count2++;
			  }
		  }
		  
		  if (type == humanVshuman)
		  {
			  JOptionPane.showMessageDialog(Game.this,"      red: "+count1+"     yellow: "+count2);
		  }
		  else
		  {
			  JOptionPane.showMessageDialog(Game.this,"      player: "+count1+"     com: "+count2);
		  }			
		  
		  //setVisible(false); // Clears game frame
		  // 26-1-2014 MS
		  dispose();// Frees JFrame and subcomponents
		  // making then undisplayable...
		  //Change start player every game
		  initBoard();
		  startPlayer=3-startPlayer;		  
		  new Game();// Creates new game		  
	}
	
    public static void main(String[] args) 
    {		
		new Game();
	}
}

