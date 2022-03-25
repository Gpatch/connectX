
public final class TextView
{

	public TextView()
	{
	}

	// Displaying appropriate messages for specific events
	public final void displayNewGameMessage()
	{
		System.out.println("---- NEW GAME STARTED ----");
	}
	public final void displayEndGameMessage(){ System.out.println ("---- GAME OVER ----"); }
	public final void displayFullBoardMessage() {System.out.println ("---- THE BOARD IS FULL ----"); }
	public final void displayGiveUpMessage(String player)
	{ System.out.println ("---- " + player + " GAVE UP ----"); }
	public final void displayWinMessage(String player) { System.out.println("---- " + player + " WON THE GAME ---"); }

	public final void displayEnterNameMessage(String name) { System.out.println ("---- " + name + " ENTER YOUR NAME ----"); }
	public final void displayEnterSymbol(String name) { System.out.println ("---- " + name + " ENTER YOUR CHIP SYMBOL ----"); }
	public final void displayEnterSymbolNPC() { System.out.println ("---- ENTER NPC CHIP SYMBOL ----"); }

	public final void displayEnterBoardRows() { System.out.println("---- ENTER THE NUMBER OF ROWS (MIN 4; MAX 20) ----");}
	public final void displayEnterBoardCols() { System.out.println("---- ENTER THE NUMBER OF COLUMNS (MIN 4; MAX 20) ----");}

	//Calls inputs for player
	public String inputPlayerName (){ return InputUtil.readStringFromUser(); }
	public char inputChipSymbol (){ return InputUtil.readCharFromUser(); }
	public int inputBoardDim() { return InputUtil.readIntFromUser(); }

	///Ask if wants to play with the NPC
	public boolean askToPlayWithNPC(){
		char input;
		boolean result = false;

		do{
			System.out.println("Do you want to play with the NPC? (Type y/n)");
			input = InputUtil.readCharFromUser();

			if(input == 'y'){result = true;}
			else if (input == 'n') {result = false;}

		}while(input != 'y' && input != 'n');

		return result;
	}

	//User choose between default and custom sized board
	public boolean askForBoardType(){
		boolean classic = true;
		char input;

		do{
			System.out.println("Do you want to play with the default board size? (Type y/n)");
			input = InputUtil.readCharFromUser();

			if(input == 'y'){
				classic = true;
			}
			else if(input == 'n'){
				classic = false;
			}
		}while(input != 'y' && input != 'n');
		return classic;
	}

	//Get input for custom rows
	public int inputNrRows(int rows){
		while(rows < 4 || rows > 20){
			System.out.println("Number of rows has to be between 4 and 20!");
			rows = InputUtil.readIntFromUser();
		}
		return rows;
	}

	//Get input for custom columns
	public int inputNrCols(int cols){
		while(cols < 4 || cols > 20){
			System.out.println("Number of columns has to be between 4 and 20!");
			cols = InputUtil.readIntFromUser();
		}
		return cols;
	}

	public int askForWinChips(int rows, int cols){
		char input;
		boolean defaultChip = true;
		int nChips;

		do{
			System.out.println("Do you want the default amount of chips required to win? (Type y/n)");
			input = InputUtil.readCharFromUser();

			if(input == 'y'){
				defaultChip = true;
			}
			else if(input == 'n'){
				defaultChip = false;
			}
		}while(input != 'y' && input != 'n');

		if(defaultChip){
			return  4;
		}else{
			System.out.println("Enter the amount of chips required to win: ");
			nChips = InputUtil.readIntFromUser();

			while(nChips < 3 || nChips > Math.max(rows, cols)){
				System.out.println("Number of winning chips has to be between 3 and " + Math.max(rows,cols) + "!");
				nChips = InputUtil.readIntFromUser();
			}
		}
		return nChips;
	}


	//Check that players don't have the same chip symbols
	public char validSecondSymbol(char symbol, char symbol2){
		while (symbol2 == symbol){
			System.out.println("Please choose a different symbol!");
			symbol2 = InputUtil.readCharFromUser();
		}
		return symbol2;
	}


	// Asks in which column to perform a move
	public int askForMove(String player, int cols) {
		int input;
		do {
			System.out.print(player + " select a free column: ");
			input = InputUtil.readIntFromUser();
		}while(input <= 0 || input > cols);
		return input;
	}


	//Asks the user for input to restart
	public boolean askToRestart()
	{
		char input;
		boolean restart = false;
		do {
			System.out.println("Do you want to restart? (Type y/n)");
			input = InputUtil.readCharFromUser();

			if(input == 'y'){
				restart = true;
			}
			else if(input == 'n'){
				restart = false;
			}

		}while(input != 'y' && input != 'n');

		return restart;
	}

	public int optionsToChoose(){
		int input;
		do{
			System.out.println("---- CHOOSE THE OPTION 1-4 ----");
			System.out.println("	   	CONTINUE - 1");
			System.out.println("	   	GIVE UP - 2");
			System.out.println("	  	 SAVE - 3");
			System.out.println("	  	 EXIT - 4");
			System.out.println("-------------------------------");

			input = InputUtil.readIntFromUser();
		}while(input != 1 && input != 2 && input != 3 && input != 4);

		return input;
	}

	public boolean askToLoadData(){
		char input;
		boolean toLoad = false;
		do{
			System.out.println("Do you want to load the saved game? (y/n)");
			input = InputUtil.readCharFromUser();

			if(input == 'y'){
				toLoad = true;
			}
			else if(input == 'n'){
				toLoad = false;
			}

		}while(input != 'y' && input != 'n');

		return toLoad;
	}


	//Displays the current state of the board
	public final void displayBoard(int rows, int cols, char[][] positions, char chip, char chip2) {
		// Visual elements needed to represent some parts of the board
		String horizontalLine = (" " + "-".repeat(3)).repeat(cols);
		String empty = ("|" + " ".repeat(3));
		String player1 = ("| " + chip + " ");
		String player2 = ("| " + chip2 + " ");

		// A StringBuilder is used to assemble longer Strings more efficiently.
		StringBuilder sb = new StringBuilder();

		//Here the appropriate string is appended based on the player symbol stored in the array
		sb.append(horizontalLine);
		sb.append("\n");
		//Each iteration draws a new row for the board
		for(int i = 0; i < rows; i++){	//Looping through the rows
			for(int j = 0; j< cols; j++){	//Looping through the columns
				if(positions[j][i] == '\u0000'){
					sb.append(empty);
				}
				else if(positions[j][i] == chip){
					sb.append(player1);
				}
				else{
					sb.append(player2);
				}
			}

			// Now print remaining elements needed to complete the look of the board
			sb.append("|");
			sb.append("\n");
			sb.append(horizontalLine);
			sb.append("\n");
		}
		// Then print out the assembled String.
		System.out.println(sb);
	}
}
