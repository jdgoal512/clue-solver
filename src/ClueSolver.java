package src;
import java.util.Scanner;
import src.logic.*;
import src.logic.card.*;
import java.util.InputMismatchException;
import java.util.ArrayList;

/**
 * Command line interface for the clue solving logic
 * Makes sure input is valid
**/
class ClueSolver {
	//Variables
	//The least amount of people that can play clue
	private static int MIN_PLAYERS = 3;
	//The maximum number of players for clue
	private static int MAX_PLAYERS = 6;
	//The options for the play menu
	enum MenuOption { SUGGESTION, ADD_KNOWN, VIEW_STATUS, UNDO, EXIT };

	//Methods

	/**
	 * Safely requests that the user input a string value given a prompt/title
	 * Does not throw errors for input of the wrong type
	 * @param prompt The prompt for the user
	 * @param scanner the scanner that should be used to get the input
	 * @return value The value given by the users
	**/
	private static int promptForInt(String prompt, Scanner scanner) {
		System.out.print(String.format("\033[34m%s\033[39m", prompt));
		int value = Integer.MAX_VALUE; //Default value for an incorrect input
		try {
			value = scanner.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("\033[31mPlease enter a valid number\033[39m");
			scanner.nextLine();
		}
		return value;
	}

	/**
	 * Requests the user to input an integer given a minimum and maximum value. Repeats the
	 * request until a valid value is given
	 * @param prompt The prompt for the user
	 * @param errorMessage The error message to show if the user inputs something that is not in the given bounds
	 * @param min The minimum bound for input
	 * @param max The maximum bound for input
	 * @param scanner The scanner to get the input
	**/
	private static int verifiedInt(String prompt, String errorMessage, int min, int max, Scanner scanner) {
		int value = Integer.MAX_VALUE; //Default value for an incorrect input
		while (value < min || value > max) {
			value = promptForInt(prompt, scanner);
			if (value != Integer.MAX_VALUE && (value < MIN_PLAYERS || value > MAX_PLAYERS)) {
				System.out.println(String.format("\033[31m%s\033[39m", errorMessage));
			}
		}
		return value;
	}

	/**
	 * Generates a numbered menu prompt from a list of strings (items on the list)
	 * @param title The title for the menu
	 * @param list The titles for the choices for the user
	 * @return output The formatted string for the menu prompt
	**/
	private static String createMenuList(String title, ArrayList<String> list) {
		int i = 1;
		String output = title + "\033[39m\n\n";
		for (String s : list) {
			output += String.format("%d. %s\n", i, s);
			i++;
		}
		return output;
	}

	/**
	 * Converts an integer value to its value in the menu
	 * @param itemNumber The number of the desired menu itemNumber
	 * @return menuItem The enum associated with specified menu item
	**/
	private static MenuOption intToMenuItem(int itemNumber) {
		if (itemNumber == 1) {
			return MenuOption.SUGGESTION;
		} else if (itemNumber == 2) {
			return MenuOption.ADD_KNOWN;
		} else if (itemNumber == 3) {
			return MenuOption.VIEW_STATUS;
		} else if (itemNumber == 4) {
			return MenuOption.UNDO;
		} else {
			return MenuOption.EXIT;
		}
	}

	/**
	 * Starts the clue solver
	 * @param args The command line arguments (should be none)
	**/
	public static void main(String[] args) {
		//Display a welcome message
		System.out.println("\033[35m-----------------------------------------");
		System.out.println("| \033[1mWelcome to the Clue Solving Assistant\033[0m |");
		System.out.println("-----------------------------------------\033[39m\n");
		// System.out.println("\033[31m*Warning* The solver will generate incorrect information if bad infomation is given to it\033[39m\n");
	    Scanner scanner = new Scanner(System.in);

		//Get how many players
		String numberOfPlayersError = String.format("Sorry, there must be %d to %d players to play Clue.", MIN_PLAYERS, MAX_PLAYERS);
		int numberOfPlayers = verifiedInt("How many players will be playing? ", numberOfPlayersError, MIN_PLAYERS, MAX_PLAYERS, scanner);

		//Get player names

		ArrayList<Player> playerNames = new ArrayList<Player>();
    	// Get the user's name
    	System.out.print("\033[34mEnter your name:\033[39m ");
		String name = scanner.next();
		//Import the cards
		CardList c = CardList.getCardList();
		int totalCards = c.getNumberOfCards(); //Number of all the weapons, suspects, and rooms
		// int cardsAccountedFor = 3; //Cards in the envelope and in people's hands
		int CARDS_IN_ENVELOPE = 3;
		int possibleCards = (totalCards - CARDS_IN_ENVELOPE) / numberOfPlayers; //Minimum number of cards in each players hand
		// boolean canHaveExtraCard = (totalCards - CARDS_IN_ENVELOPE) % numberOfPlayers != 0; //Whether players can have different numbers of cards
		int numberOfCards = possibleCards; //How many cards the player actually has
		String numberOfCardsError = String.format("\033[31mSorry, there must be %d to %d cards.\033[39m", possibleCards, possibleCards + 1);
		if ((totalCards - CARDS_IN_ENVELOPE) % numberOfPlayers != 0) {
			numberOfCards = verifiedInt("\033[34mHow many cards do you have?\033[39m ", numberOfCardsError, possibleCards, possibleCards + 1, scanner);
		}
		int playerCards = numberOfCards; //How many cards the user has
		int cardsAccountedFor = CARDS_IN_ENVELOPE + playerCards;
		playerNames.add(new Player(name, numberOfCards)); //Add the user to the list of players
		//Get the names and amount cards for the other players
		for (int i = 2; i<=numberOfPlayers; i++) {
    		System.out.print("\033[34mEnter the name of the next player (to the left):\033[39m ");
			name = scanner.next();
			if ((totalCards - cardsAccountedFor)%(numberOfPlayers-i+1) != 0) {
				numberOfCards = verifiedInt(String.format("\033[34mHow many cards does %s have?\033[39m ", name),
					numberOfCardsError, possibleCards, possibleCards + 1, scanner);
			} else {
				numberOfCards = (totalCards - cardsAccountedFor)/ (numberOfPlayers-i+1);
			}
			cardsAccountedFor += numberOfCards;
			playerNames.add(new Player(name, numberOfCards));
	    	System.out.println(String.format("Player %d: %s\tCards: %s", i, name, numberOfCards));
		}

		ClueLogic solver = new ClueLogic(playerNames);
		//Get what cards you have
		for (int i = 0; i<playerCards; ) {
			int cardNumber = verifiedInt(createMenuList("Please select a card you have", solver.getAllCardNames()), "Please select a valid card", 1,
				totalCards, scanner);
			//Only continue if it is a new card
			if (solver.doMove(Move.startCard(cardNumber-1))) {
				i++;
			}
		}

		//Begin the game
		String mainMenuText = "Main menu\033[39m\n\n1. Make a suggestion\n2. Add a known card\n3. View Current Status\n4. Undo a move\n5. Exit\n";
		String mainMenuError = "Please choose a valid option";
		int NUMBER_OF_MENU_ITEMS = 5;
		boolean continueGame = true; //Whether or not to continue prompting the user
		while (continueGame) {
			MenuOption mainMenuOption = intToMenuItem(verifiedInt(mainMenuText, mainMenuError, 1, NUMBER_OF_MENU_ITEMS, scanner));
			switch (mainMenuOption) {
				case SUGGESTION: //Make a suggestion
					//Get number of the player making the suggestion
					int playerNumber = verifiedInt(createMenuList("Which player will make a suggestion? (Press 0 to go back)", solver.getPlayerList()),
							"Please select a valid player", 0, numberOfPlayers, scanner);
					if (playerNumber == 0) {
						break;
					}
					//Pick a room
					int roomNumber = verifiedInt(createMenuList("Where did the murder occur? (Press 0 to go back)", solver.getRoomNames()),
							"Please select a valid player", 0, c.getNumberOfRooms(), scanner);
					if (roomNumber == 0) {
						break;
					}
					//Pick a suspect
					int suspectNumber = verifiedInt(createMenuList("Who did it? (Press 0 to go back)", solver.getSuspectNames()),
							"Please select a valid player", 0, c.getNumberOfSuspects(), scanner);
					if (suspectNumber == 0) {
						break;
					}
					//Pick a weapon
					int weaponNumber = verifiedInt(createMenuList("What weapon did they use? (Press 0 to go back)", solver.getWeaponNames()),
							"Please select a valid player", 0, c.getNumberOfWeapons(), scanner);
					if (weaponNumber == 0) {
						break;
					}
					//Get number of the player making the suggestion
					ArrayList<String> players = solver.getPlayerList();
					players.set(playerNumber-1, "Nobody");
					int endingPlayer = verifiedInt(createMenuList("Which player was able to disprove the suggestion? (Press 0 to go back)", players),
							"Please select a valid player", 1, numberOfPlayers, scanner);
					if (endingPlayer == 0) {
						break;
					}
					Suggestion suggestion = new Suggestion(c.getRoom(roomNumber-1), c.getSuspect(suspectNumber-1), c.getWeapon(weaponNumber-1));
					solver.doMove(Move.suggestion(playerNumber-1, suggestion, endingPlayer-1));
					break;
				case ADD_KNOWN: //Add a known card
					int knownPlayerNumber = verifiedInt(createMenuList("Which player's was learned? (Press 1 to go back)", solver.getPlayerList()),
							"Please select a valid player", 1, numberOfPlayers, scanner);
					if (knownPlayerNumber == 1) {
						break;
					}
					//Make sure card is not already known
					boolean alreadyKnown = true;
					int cardNumber = 0;
					Card knownCard = null;
					while (alreadyKnown) {
						cardNumber = verifiedInt(createMenuList(String.format("Please select which card %s has: (Press 0 to go back)",
							playerNames.get(knownPlayerNumber-1).getName()), solver.getAllCardNames()), "Please select a valid card", 0,
							totalCards, scanner);
						if (cardNumber != 0) {
							knownCard = CardList.getCardList().getCard(cardNumber-1);
							alreadyKnown = knownCard.isKnown();
						}
					}
					if (cardNumber != 0) {
						boolean added = solver.doMove(Move.addCard(knownCard, playerNames.get(knownPlayerNumber-1)));
						if (added) {
							System.out.println("The card was sucessfully added");
						} else {
							System.out.println("There was an error adding the card");
						}
					}
					break;
				case VIEW_STATUS: //View current status
					System.out.println(solver.getSolution());
					for (int i = 0; i<playerNames.size(); i++) {
						System.out.println(playerNames.get(i).toString());
					}
					break;
				case UNDO:
					ArrayList<String> moves = solver.getMoves();
					if (moves.size() > 0) {
						int moveNumber = verifiedInt(createMenuList("Which move would you like to remove? (Press 0 to go back)", moves),
							"Please select a valid move", 0, moves.size(), scanner);
							if (moveNumber != 0) {
								solver.doMove(Move.undo(moveNumber-1));
							}
					} else {
						System.out.println("\033[31mSorry, there are no moves possible to undo\033[39m");
					}
					break;
				default: //Quit
					continueGame = false;
			}
		}
	}
}
