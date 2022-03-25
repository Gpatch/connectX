public final class Controller {
	private final Model model;
	private final TextView view;

	public Controller(Model model, TextView view) {
		this.model = model;
		this.view = view;
	}

	private void setBoardSize(boolean isClassic){
		if(isClassic){
			model.setDimension(Model.DEFAULT_NR_ROWS, "row");
			model.setDimension(Model.DEFAULT_NR_COLS, "column");
		}
		else{
			view.displayEnterBoardRows();
			model.setDimension(view.inputNrRows(view.inputBoardDim()), "row");
			view.displayEnterBoardCols();
			model.setDimension(view.inputNrCols(view.inputBoardDim()), "column");
		}
		model.setBoardSize(model.getDimension("column"), model.getDimension("row"));
	}

	private void setChipsWin(int chips) {
		model.setWinChips(chips);
	}

	private void setPlayerInfo(){
		view.displayEnterNameMessage(model.getNames()[0]);
		model.setName(view.inputPlayerName(),1);
		System.out.println(model.getNames()[0]);

		view.displayEnterSymbol(model.getNames()[0]);
		model.setChipSymbol(view.inputChipSymbol(),1);
		System.out.println(model.getChipSymbols()[0]);
	}
	private void setPlayerInfo2(){
		view.displayEnterNameMessage(model.getNames()[1]);
		model.setName(view.inputPlayerName(),2);
		System.out.println(model.getNames()[1]);

		view.displayEnterSymbol(model.getNames()[1]);
		model.setChipSymbol(view.validSecondSymbol(model.getChipSymbols()[0], view.inputChipSymbol()),2);
		System.out.println(model.getChipSymbols()[1]);
	}

	private void setNPCInfo(){
		model.setNPCActive(true);
		model.setName("NPC",2);
		view.displayEnterSymbolNPC();
		model.setChipSymbol(view.validSecondSymbol(model.getChipSymbols()[0], view.inputChipSymbol()),2);
		System.out.println(model.getChipSymbols()[1]);
	}

	private void startGamePlay(){
		view.displayNewGameMessage();
		view.displayBoard(model.getDimension("row"), model.getDimension("column"), model.getBoard(),
				model.getChipSymbols()[0], model.getChipSymbols()[1]);

		model.setGameSetupStatus(false);
		model.terminateSession(false);
		model.exitOptionMenu(false);
	}

	private void makeMove(){
		model.exitOptionMenu(false);
		view.displayBoard(model.getDimension("row"), model.getDimension("column"), model.getBoard(),
				model.getChipSymbols()[0],model.getChipSymbols()[1]);
		model.makeMove(model.getBoard(),view.askForMove(model.currentPlayerName(), model.getDimension("column")));
		//If the column is invalid, then keep asking for valid move
		while (!model.getMoveState()) {
			model.makeMove(model.getBoard(),view.askForMove(model.currentPlayerName(), model.getDimension("column")));
		}
		//Display the board after the move
		view.displayBoard(model.getDimension("row"), model.getDimension("column"), model.getBoard(),
				model.getChipSymbols()[0],model.getChipSymbols()[1]);
		//Check if the game has a winner
		model.totalWinDetection(model.getBoard());
		model.setCurrentPlayerTurn(!model.getCurrentPlayerTurn());

		while(!model.getOptionMenuState()){
			choosingOption(view.optionsToChoose());
		}
	}


	private void makeMoveWithNPC(){
		model.exitOptionMenu(false);
		if(model.getCurrentPlayerTurn()){
			view.displayBoard(model.getDimension("row"), model.getDimension("column"), model.getBoard(),
					model.getChipSymbols()[0],model.getChipSymbols()[1]);
			model.makeMove(model.getBoard(),view.askForMove(model.getNames()[0], model.getDimension("column")));
			while (!model.getMoveState()) {
				model.makeMove(model.getBoard(),view.askForMove(model.currentPlayerName(), model.getDimension("column")));
			}
			view.displayBoard(model.getDimension("row"), model.getDimension("column"), model.getBoard(),
					model.getChipSymbols()[0],model.getChipSymbols()[1]);
			model.totalWinDetection(model.getBoard());
			model.setCurrentPlayerTurn(!model.getCurrentPlayerTurn());
		}
		else {
			model.bestNPCMove();
			view.displayBoard(model.getDimension("row"), model.getDimension("column"), model.getBoard(),
					model.getChipSymbols()[0],model.getChipSymbols()[1]);
			model.totalWinDetection(model.getBoard());
			model.setCurrentPlayerTurn(!model.getCurrentPlayerTurn());

			if(!model.getGameWonStatus()) {
				while (!model.getOptionMenuState()) {
					choosingOption(view.optionsToChoose());
				}
			}
		}
	}

	private void gameRestart(boolean restartDecision){
		if(restartDecision){
			model.gameRestart();
		}else {
			model.setGameLooping(false);
			model.terminateSession(true);
		}
	}

	private void giveUp(){
		view.displayGiveUpMessage(model.currentPlayerName());
		view.displayEndGameMessage();
	}

	private void saveGame(){
		model.saveGame();
	}

	private void choosingOption(int choice){
		switch (choice) {
			case 1:
				model.exitOptionMenu(true);
				break;
			case 2:
				model.exitOptionMenu(true);
				giveUp();
				model.terminateSession(true);
				gameRestart(view.askToRestart());
				break;

			case 3:
				model.exitOptionMenu(false);
				saveGame();
				break;

			case 4:
				System.exit(0);
				break;
		}
	}

	public void startSession() {
		while(model.getGameLooping()) {
			if(view.askToLoadData()){
				model.loadGame();
			}
			while (model.getGameSetupStatus()) {
				if (view.askToPlayWithNPC()) {
					model.setNPCActive(true);
				}

				if (model.getNPCActive()) {
					setBoardSize(view.askForBoardType());
					setChipsWin(view.askForWinChips(model.getDimension("row"),model.getDimension("column")));
					setPlayerInfo();
					setNPCInfo();
				} else {
					setBoardSize(view.askForBoardType());
					setChipsWin(view.askForWinChips(model.getDimension("row"), model.getDimension("column")));
					setPlayerInfo();
					setPlayerInfo2();
				}
				startGamePlay();
			}
			model.terminateSession(false);

			if (model.getNPCActive()) {
				//NPC MODE
				while (!model.getSessionTerminationStatus()) {
					//Ending the game if the board is full
					if (model.isBoardFull(model.getBoard())) {
						view.displayFullBoardMessage();
						view.displayEndGameMessage();
						//Asking the user to restart the game, takes in users input as an argument
						//based on the input decided whether to restart the game or exit
						gameRestart(view.askToRestart());
					}
					else if (model.getGameWonStatus()) {
							view.displayWinMessage(model.getWinnerName());
							gameRestart(view.askToRestart());

					} else {
						makeMoveWithNPC();
						if (model.getGameWonStatus()) {
							view.displayWinMessage(model.getWinnerName());
							gameRestart(view.askToRestart());
						}
					}
				}
			} else {
				//PVP MODE
				while (!model.getSessionTerminationStatus()) {
					if (model.isBoardFull(model.getBoard())) {
						view.displayFullBoardMessage();
						view.displayEndGameMessage();
						gameRestart(view.askToRestart());
					}
					else if (model.getGameWonStatus()) {
						view.displayWinMessage(model.getWinnerName());
						gameRestart(view.askToRestart());

					}
					else {
						//Ask for move if the user didn't give up
						makeMove();
						if (model.getGameWonStatus()) {
							view.displayWinMessage(model.getWinnerName());
							gameRestart(view.askToRestart());
						}
					}
				}
			}
		}
	}
}
