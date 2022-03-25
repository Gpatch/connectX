import java.io.*;


public final class Model
{
	// ===========================================================================
	// ================================ CONSTANTS ================================
	// ===========================================================================
	// The most common version of Connect Four has 6 rows and 7 columns.
	public static final int DEFAULT_NR_ROWS = 6;
	public static final int DEFAULT_NR_COLS = 7;
	
	// ========================================================================
	// ================================ FIELDS ================================
	// ========================================================================
	// The size of the board.
	private int nrRows;
	private int nrCols;

	//Coordinate where NPC makes a simulated move, which then needs to be cleared
	private int[] coordinateToClear;

	//Store each entry in a 2D array. [column][row]
	private char[][] pos;

	// Store whose turn it is (TRUE - Player1; FALSE - Player2/AI)
	private boolean playerTurn;
	private boolean NPC_active;

	private boolean isSessionForceTerminated;
	private boolean validMove;
	private boolean exitOptionMenu;

	private boolean isBoardNotEmpty;
	private boolean isGameInSetupMode;
	private boolean isGameLooping;

	private boolean gameWon;
	private String winnerPName;

	// Store information about players: names and chosen chip symbols
	private final String[] pNames;
	private final char[] chipSymbols;
	//Number of chips required to win
	private int winChips;

	// =============================================================================
	// ================================ CONSTRUCTOR ================================
	// =============================================================================
	public Model() {
		nrRows = DEFAULT_NR_ROWS;
		nrCols = DEFAULT_NR_COLS;

		coordinateToClear = new int[2];

		NPC_active = false;
		playerTurn = randomBoolean();
		validMove = true;

		isBoardNotEmpty = false;
		isGameInSetupMode = true;
		isGameLooping = true;
		isSessionForceTerminated = false;

		pNames = new String[2];
		pNames[0] = "PLAYER 1";
		pNames[1] = "PLAYER 2";

		chipSymbols = new char[2];
		chipSymbols[0] = '1';
		chipSymbols[1] = '2';

		gameWon = false;
		winChips = 4;
		winnerPName = null;

		isSessionForceTerminated = false;
	}
	
	// ====================================================================================
	// ================================ MODEL INTERACTIONS ================================
	// ====================================================================================

	private boolean randomBoolean(){
		int rndBinary = (int) (Math.random() + 1);
		return rndBinary != 0;
	}

	//Check who is the current player in terms of their chip symbol, based on the boolean value of the player's turn
	private char currentPlayerSymbol(boolean playerTurn){
		if(playerTurn){
			return chipSymbols[0];
		}else{
			return chipSymbols[1];
		}
	}

	//Check the current player in terms of their name, based on the boolean value of the player's turn
	public String currentPlayerName(){
		if(playerTurn){
			return pNames[0];
		}else{
			return pNames[1];
		}
	}

	// Checks if the board is full, by counting how many array elements are filled
	public boolean isBoardFull(char[][] board) {
		int counter = 0;

		for(int col = 0; col <= nrCols-1; col++){
			for(int row = 0; row <= nrRows-1; row++){
				if(board[col][row] != '\u0000'){
					counter ++;
				}
			}
		}
		return counter == nrCols * nrRows;
	}


	//Check if move is valid, by checking if there are any free spaces in the column
	//and also checking if the column number is valid
	private boolean isMoveValid(int move, char[][] board) {
		int emptyColCounter = 0;
		if (isBoardNotEmpty) {
			for (int j = nrRows-1; j >= 0; j--) {
				if (board[move-1][j] != '\u0000') {
					emptyColCounter++;
				}
			}
		}
		return !(emptyColCounter==nrRows);
	}


	//If the move is valid in the current column, then the chip is dropped in the column on the lowest available row
	public void makeMove(char[][] board, int move) {
		char player;
		player = currentPlayerSymbol(playerTurn);

		if(isMoveValid(move, board)){
			validMove = true;
			isBoardNotEmpty = true;
			for(int j = nrRows-1; j >= 0; j--){
				if(board[move-1][j] == '\u0000'){
					board[move-1][j] = player;
					//'CoordinateToClear' makes effect only in the AI algorithm
					coordinateToClear[0] = move-1;
					coordinateToClear[1] = j;
					break;
				}
			}
		}
		else{ validMove = false; }
	}


	public void bestNPCMove(){
		makeMove(pos, pickBestMove());
	}



	public void saveGame(){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("./src/save.txt"));
			bw.write(nrRows + "\n");
			bw.write(nrCols + "\n");
			bw.write(NPC_active + "\n");
			bw.write(playerTurn + "\n");
			bw.write(validMove + "\n");
			bw.write(isBoardNotEmpty + "\n");
			bw.write(isGameInSetupMode + "\n");
			bw.write(isGameLooping + "\n");
			bw.write(pNames[0] + "\n");
			bw.write(pNames[1] + "\n");
			bw.write(chipSymbols[0] + "\n");
			bw.write(chipSymbols[1] + "\n");
			bw.write(gameWon + "\n");
			bw.write(winChips + "\n");
			bw.write(winnerPName + "\n");
			//Saves each element of board array into a separate line
			for(int i = 0; i < nrCols; i++){
				for(int j = 0; j < nrRows; j++){
					bw.write(pos[i][j]+"\n");
				}
			}
			bw.close();
		}catch (Exception ex){
			System.out.println("ERROR WHILE WRITING TO FILE!");
		}

	}

	public void loadGame(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("./src/save.txt"));
			String s;
			int lineNum = 0;
			int i = -1;
			while ((s = br.readLine())  != null){
				lineNum++;

				switch (lineNum){
					case 1:
						nrRows = Integer.parseInt(s);
						break;
					case 2:
						nrCols = Integer.parseInt(s);
						setBoardSize(nrCols,nrRows);
						break;
					case 3:
						NPC_active = Boolean.parseBoolean(s);
						break;
					case 4:
						playerTurn = Boolean.parseBoolean(s);
						break;
					case 5:
						validMove = Boolean.parseBoolean(s);
						break;
					case 6:
						isBoardNotEmpty = Boolean.parseBoolean(s);
						break;
					case 7:
						isGameInSetupMode = Boolean.parseBoolean(s);
						break;
					case 8:
						isGameLooping = Boolean.parseBoolean(s);
						break;
					case 9:
						pNames[0] = s;
						break;
					case 10:
						pNames[1] = s;
						break;
					case 11:
						chipSymbols[0] = s.charAt(0);
						break;
					case 12:
						chipSymbols[1] = s.charAt(0);
						break;
					case 13:
						gameWon = Boolean.parseBoolean(s);
						break;
					case 14:
						winChips = Integer.parseInt(s);
						break;
					case 15:
						winnerPName = s;
						break;
				}

				if(lineNum >= 16){
					i++;
					int col = i / (nrCols-1);
					int row = i % (nrCols-1);
					pos[col][row] = s.charAt(0);
				}
			}
			br.close();
		}catch (Exception ex){
			System.out.println("ERROR WHILE READING FROM FILE!");
		}
	}

	// Resets the game settings when the game was restarted
	public void gameRestart() {
		nrRows = DEFAULT_NR_ROWS;
		nrCols = DEFAULT_NR_COLS;

		NPC_active = false;
		playerTurn = randomBoolean();
		validMove = true;

		isBoardNotEmpty = false;
		isGameInSetupMode = true;
		isGameLooping = true;

		pNames[0] = "PLAYER 1";
		pNames[1] = "PLAYER 2";

		chipSymbols[0] = '1';
		chipSymbols[1] = '2';

		gameWon = false;
		winChips = 4;
		winnerPName = null;

		terminateSession(true);

	}

	//Gets the score for a window of chips after a simulated NPC move
	private int scoreDirections(char[][] board) {
		int score = 0;
		int counter;
		char[] currentWindow = new char[winChips];

		// ------------------------HORIZONTAL----------------
		for (int j = nrRows - 1; j >= 0; j--) {
			for (int i = 0; i < nrCols-(winChips-1); i++) {
				counter=-1;
				for(int k = i; k < i+winChips; k++){
					counter++;
					currentWindow[counter] = board[k][j];
				}
				score +=  evaluateScoreAtMove(currentWindow,chipSymbols[1], chipSymbols[0]);
			}
		}
		//------------------------VERTICAL--------------------
		for (int i = 0; i < nrCols; i++) {
			for (int j = 0; j < nrRows-(winChips-1); j++) {
				counter=-1;
				for(int k = j; k < j+winChips; k++){
					counter++;
					currentWindow[counter] = board[i][k];
				}
				score +=  evaluateScoreAtMove(currentWindow,chipSymbols[1], chipSymbols[0]);
			}
		}
		//-------------POSITIVE DIAGONAL
		for(int i = nrRows-1; i >= nrRows-(winChips-1); i--){
			for(int j = 0; j < nrCols-(winChips-1); j++){
				counter = -1;
				for(int w = 0; w < winChips; w++){
					counter++;
					currentWindow[counter] = board[j+w][i-w];
				}
				score +=  evaluateScoreAtMove(currentWindow,chipSymbols[1], chipSymbols[0]);

			}
		}
		//-------------NEGATIVE DIAGONAL
		for(int i = 0; i < nrRows-(winChips-1); i++){
			for(int j = 0; j < nrCols-(winChips-1); j++){
				counter = -1;
				for(int w = 0; w < winChips; w++){
					counter++;
					currentWindow[counter] = board[j+w][i+w];
				}
				score +=  evaluateScoreAtMove(currentWindow,chipSymbols[1], chipSymbols[0]);
			}
		}
		return score;
	}


	private int evaluateScoreAtMove(char[] window, char AIChip, char pChip){
		int score = 0;
		int AICounter = 0;
		int PCounter = 0;
		int emptyCounter = 0;

		//Loops through the window, identifies which chips are there
		for (char c : window) {
			if (c == AIChip) {
				AICounter++;
			}
			else if(c == pChip){
				PCounter++;
			}
			else if (c == '\u0000'){
				emptyCounter++;
			}
		}

		//If NPC's winning move, then return a very high score
		if(AICounter == winChips){
			score += 1000000;
			return score;
		}
		//Else add appropriate score
		else if(AICounter > 1 && AICounter < winChips){
			for(int i = 2; i < winChips; i++){
				if(AICounter == i && emptyCounter == window.length-AICounter){
					score += i;
					break;
				}
			}
		}
		//Check if opponent has 1 chip left to win, then add very low score
		if(PCounter == winChips-1 && emptyCounter == winChips-PCounter){
			score -= 100000;
			return score;
		}
		//Else add appropriate negative score
		else if(PCounter > 2 && AICounter < winChips){
			for (int i = 3; i < winChips; i++) {
				if (PCounter == i && emptyCounter == window.length-AICounter) {
					score -= i*2;
					break;
				}
			}
		}
		return score;
	}

	private int[] getValidLocations(char[][] board){
		int counter = 0;
		int[] valid_locations;
		//Identifying correct size for the array
		for(int i = 1; i <= nrCols; i++){
			if(isMoveValid(i, board)){
				counter++;
			}
		}
		valid_locations = new int[counter];

		//Now array receiving the values
		counter = -1;
		for(int i = 1; i <= nrCols; i++){
			if(isMoveValid(i, board)){
				counter++;
				valid_locations[counter] = i;
			}
		}
		return valid_locations;
	}

	private int pickBestMove(){
		char[][] boardCopy = new char[pos.length][pos[0].length];

		for(int i = 0; i < pos.length; i++){
			System.arraycopy(pos[i], 0, boardCopy[i], 0, pos[0].length);
		}
		int[] validLoc = getValidLocations(boardCopy);

		int bestScore = 0;
		int bestLoc = validLoc[validLoc.length/2];
		for(int loc : validLoc){
			//NPC makes a move in the simulated board, to see how effective the move is. Does not affect the original board
			makeMove(boardCopy,loc);
			int score = scoreDirections(boardCopy);
			if(score > bestScore){
				bestScore = score;
				bestLoc = loc;
			}
			//Deletes the previous move
			boardCopy[coordinateToClear[0]][coordinateToClear[1]] = '\u0000';
		}
		return bestLoc;
	}


	private boolean verticalWinnerDetection(char[][] board){
		boolean player = true;
		boolean winnerDetected = false;
		int counter;
		int counter2;

			for(int i = 0; i < nrCols; i++){
				counter = 0;
				counter2 = 0;
				for(int j = nrRows-1; j >= 0; j--){
					if(board[i][j] == chipSymbols[0]){
						player = true;
						counter ++;
						counter2 = 0;
						if(counter >= winChips){break;}

					}else if(board[i][j] == chipSymbols[1]){
						player = false;
						counter2 ++;
						counter = 0;
						if(counter2 >= winChips){break;}

					}else{ counter = 0; counter2 = 0;}
				}

				if(counter >= winChips || counter2 >= winChips) {
					winnerDetected = true;
					break;
				}
			}

			gameWon = winnerDetected;

			if(player) winnerPName = pNames[0];
			else winnerPName = pNames[1];

			return winnerDetected;
		}

		//Checks if player won horizontally
		private boolean horizontalWinnerDetection(char[][] board) {

			boolean player = true;
			boolean winnerDetected = false;
			int counter;
			int counter2;

			for (int j = nrRows - 1; j >= 0; j--) {
				counter = 0;
				counter2 = 0;
				for (int i = 0; i < nrCols; i++) {
					if (board[i][j] == chipSymbols[0]) {
						player = true;
						counter++;
						counter2 = 0;
						if(counter >= winChips){break;}

					} else if (board[i][j] == chipSymbols[1]) {
						player = false;
						counter2++;
						counter = 0;
						if(counter2 >= winChips){break;}

					}else{ counter = 0; counter2 = 0;}
				}

				if(counter >= winChips || counter2 >= winChips) {
					winnerDetected = true;
					break;
				}
			}
			gameWon = winnerDetected;

			if(player) winnerPName = pNames[0];
			else winnerPName = pNames[1];

			return winnerDetected;
		}


		//Checks if player won diagonally. Checks all positive slope gradients where each diagonal starts from 0'th column
		private boolean diagonalUpHalf_WinnerDetection(char[][] board) {

			boolean player = true;
			boolean winnerDetected = false;
			int counter;
			int counter2;

			for(int k = 0; k <= (nrRows-1)+(nrCols-1); k++){
				counter = 0;
				counter2 = 0;
				//Diagonals total
				for(int j = 0; j <=k; j++){ // column
					int i = k-j; //Row
					if(i <= nrRows-1 && j <= nrCols-1){ //Check if col and row are valid

						if (board[j][i] == chipSymbols[0]) {
							player = true;
							counter++;
							counter2 = 0;
							if (counter >= winChips) {
								break;
							}

						} else if (board[j][i] == chipSymbols[1]) {
							player = false;
							counter2++;
							counter = 0;
							if (counter2 >= winChips) {
								break;
							}

						} else {
							counter = 0;
							counter2 = 0;
						}
					}
				}
				if (counter >= winChips || counter2 >= winChips) {
					winnerDetected = true;
					break;
				}
			}
			gameWon = winnerDetected;

			if(player) winnerPName = pNames[0];
			else winnerPName = pNames[1];

			return winnerDetected;
		}


	private char[] rowReverse(char[] row){
		char[] reverseRow = new char[row.length];

		for(int i = 0 ; i < row.length; i++) {
			reverseRow[i] = row[(row.length-1)-i];
		}
		return reverseRow;
	}

	private char[][] matrixRowReverse(char[][] matrix, int cols, int rows){

		char[][] reversedMatrix = new char[matrix.length][matrix[0].length];
		char[] extractedRow = new char [matrix.length];
		char[] reversedRow;

		int counter;

		for (int i= 0; i < rows; i ++){
		//Row extraction
			counter = -1;
			for(int j = 0; j< cols; j++){
				counter++;
				extractedRow[counter] = matrix[j][i];
			}
			//Reversing the row
			reversedRow = rowReverse(extractedRow);

			//Sending the reversed row to the new matrix
			for (int j = 0; j < cols; j++){
				reversedMatrix[j][i] = reversedRow[j];
			}
		}
		return reversedMatrix;
	}


	//Same algorithm as for positive gradient diagonals, but with a matrix with reversed rows
	private boolean diagonalDownHalf_WinnerDetection(char[][] reversePos) {

		boolean player = true;
		boolean winnerDetected = false;
		int counter;
		int counter2;

		for(int k = 0; k <= (nrRows-1)+(nrCols-1); k++){
			counter = 0;
			counter2 = 0;
			//Diagonals total
			for(int j = 0; j <=k; j++){ // column
				int i = k-j; //Row
				if(i <= nrRows-1 && j <= nrCols-1){ //Check if col and row are valid

					if (reversePos[j][i] == chipSymbols[0]) {
						player = true;
						counter++;
						counter2 = 0;
						if (counter >= winChips) {
							break;
						}

					} else if (reversePos[j][i] == chipSymbols[1]) {
						player = false;
						counter2++;
						counter = 0;
						if (counter2 >= winChips) {
							break;
						}
					} else {
						counter = 0;
						counter2 = 0;
					}
				}
			}
			if (counter >= winChips || counter2 >= winChips) {
				winnerDetected = true;
				break;
			}
		}
		gameWon = winnerDetected;

		if(player) winnerPName = pNames[0];
		else winnerPName = pNames[1];

		return winnerDetected;
	}

	public void totalWinDetection(char[][] board){
		if(!verticalWinnerDetection(board)){
			if(!horizontalWinnerDetection(board)){
				if(!diagonalUpHalf_WinnerDetection(board)){
					if(diagonalDownHalf_WinnerDetection(matrixRowReverse(board,nrCols,nrRows))){
						exitOptionMenu(true);
					}
				}
			}
		}
	}

	// =========================================================================
	// ================================ GETTERS ================================
	// =========================================================================
	public int getDimension(String dimension) {
		if(dimension.equals("column")){
			return nrCols;
		}
		else if(dimension.equals("row")){
			return nrRows;
		}
		return 0;
	}
	public char[][] getBoard() { return pos; }
	public char[] getChipSymbols(){ return chipSymbols; }

	public boolean getCurrentPlayerTurn(){ return playerTurn; }
	public boolean getMoveState() { return validMove; }
	public boolean getGameSetupStatus() { return isGameInSetupMode; }
	public boolean getGameWonStatus() { return gameWon; }
	public boolean getGameLooping() { return isGameLooping; }
	public boolean getNPCActive() {return NPC_active;}
	public boolean getSessionTerminationStatus() { return isSessionForceTerminated;}
	public boolean getOptionMenuState() {return exitOptionMenu; }

	public String[] getNames(){ return pNames; }
	public String getWinnerName() { return winnerPName; }

	// =========================================================================
	// ================================ SETTERS ================================
	// =========================================================================
	public void setCurrentPlayerTurn(boolean player){ playerTurn = player; }
	public void setNPCActive(boolean active) { NPC_active = active;}
	public void setGameSetupStatus(boolean newGameStarted) {  isGameInSetupMode = newGameStarted; }
	public void setGameLooping(boolean state) { isGameLooping = state;}
	public void terminateSession(boolean terminated){isSessionForceTerminated = terminated;}

	public void setDimension(int size, String dimension) {
		if(dimension.equals("column")){
			nrCols = size;
		}
		else if(dimension.equals("row")){
			nrRows = size;
		}
	}
	public void setBoardSize(int col, int row) { pos = new char[col][row] ;}

	public void setName(String newName, int player) {
		if(player == 1){
			pNames[0] = newName;
		}
		else if(player == 2){
			pNames[1] = newName;
		}
	}

	public void setChipSymbol(char chip, int player) {
		if(player == 1){
			chipSymbols[0] = chip ;
		}
		else if(player == 2){
			chipSymbols[1] = chip;
		}
	}
	public void setWinChips(int chips){ winChips = chips; }

	public void exitOptionMenu(boolean exit) {exitOptionMenu = exit; }

}
