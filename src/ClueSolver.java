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
	enum MenuOption { SUGGESTION, ADD_KNOWN, VIEW_STATUS, EXIT };

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
		int cardsAccountedFor = 3; //Cards in the envelope and in people's hands
		int possibleCards = (totalCards - 3) / numberOfPlayers; //Minimum number of cards in each players hand
		boolean canHaveExtraCard = (totalCards - 3) % numberOfPlayers != 0; //Whether players can have different numbers of cards
		int numberOfCards = possibleCards; //How many cards the player actually has
		String numberOfCardsError = String.format("\033[31mSorry, there must be %d to %d cards.\033[39m", possibleCards, possibleCards + 1);
		if (canHaveExtraCard) {
			numberOfCards = verifiedInt("\033[34mHow many cards do you have?\033[39m ", numberOfCardsError, possibleCards, possibleCards + 1, scanner);
		}
		int playerCards = numberOfCards; //How many cards the user has
		cardsAccountedFor += playerCards;
		playerNames.add(new Player(name, numberOfCards)); //Add the user to the list of players
		System.out.println(String.format("Player 1: %s\tCards: %s", name, numberOfCards));
		//Get the names and amount cards for the other players
		for (int i = 2; i<=numberOfPlayers; i++) {
    		System.out.print("\033[34mEnter the name of the next player (to the left):\033[39m ");
			name = scanner.next();
			if (canHaveExtraCard) {
				int cardsLeft = totalCards - cardsAccountedFor; //How many cards are not already in people's hands
				if (cardsLeft % numberOfPlayers == 0) {
					canHaveExtraCard = false;
					if (cardsLeft % possibleCards != 0) {
						numberOfCards = possibleCards + 1;
					} else {
						numberOfCards = possibleCards;
					}
				} else {
					numberOfCards = verifiedInt(String.format("\033[34mHow many cards does %s have?\033[39m ", name),
						numberOfCardsError, possibleCards, possibleCards + 1, scanner);
				}
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
			if (solver.addStartCard(cardNumber-1)) {
				i++;
			}
		}

		//Begin the game
		String mainMenuText = "Main menu\033[39m\n\n1. Make a suggestion\n2. Add a known card\n3. View Current Status\n4. Exit\n";
		String mainMenuError = "Please choose a valid option";
		boolean continueGame = true; //Whether or not to continue prompting the user
		while (continueGame) {
			MenuOption mainMenuOption = intToMenuItem(verifiedInt(mainMenuText, mainMenuError, 1, 4, scanner));
			switch (mainMenuOption) {
				case SUGGESTION: //Make a suggestion
					//Get number of the player making the suggestion
					int playerNumber = verifiedInt(createMenuList("Which player will make a suggestion?", solver.getPlayerList()), "Please select a valid player",
						1, numberOfPlayers, scanner);
					//Pick a room
					int roomNumber = verifiedInt(createMenuList("Where did the murder occur?", solver.getRoomNames()), "Please select a valid player",
						1, c.getNumberOfRooms(), scanner);
					//Pick a suspect
					int suspectNumber = verifiedInt(createMenuList("Who did it?", solver.getSuspectNames()), "Please select a valid player",
						1, c.getNumberOfSuspects(), scanner);
					//Pick a weapon
					int weaponNumber = verifiedInt(createMenuList("What weapon did they use?", solver.getWeaponNames()), "Please select a valid player",
						1, c.getNumberOfWeapons(), scanner);
					//Get number of the player making the suggestion
					ArrayList<String> players = solver.getPlayerList();
					players.set(playerNumber-1, "Nobody");
					int endingPlayer = verifiedInt(createMenuList("Which player was able to disprove the suggestion?", players), "Please select a valid player",
							1, numberOfPlayers, scanner);
					solver.makeSuggestion(playerNumber-1, c.getRoom(roomNumber-1), c.getSuspect(suspectNumber-1), c.getWeapon(weaponNumber-1), endingPlayer-1);
					break;
				case ADD_KNOWN: //Add a known card
					int knownPlayerNumber = verifiedInt(createMenuList("Which player's was learned?", solver.getPlayerList()), "Please select a valid player",
						2, numberOfPlayers, scanner);
					//Make sure card is not already known
					boolean alreadyKnown = true;
					int cardNumber = 0;
					Card knownCard = null;
					while (alreadyKnown) {
						cardNumber = verifiedInt(createMenuList(String.format("Please select which card %s has:",
							playerNames.get(knownPlayerNumber-1).getName()), solver.getAllCardNames()), "Please select a valid card", 1,
							totalCards, scanner);
						knownCard = CardList.getCardList().getCard(cardNumber-1);
						alreadyKnown = knownCard.isKnown();
					}
					boolean added = solver.addKnownCard(knownCard, playerNames.get(knownPlayerNumber-1));
					if (added) {
						System.out.println("The card was sucessfully added");
					} else {
						System.out.println("There was an error adding the card");
					}
					break;
				case VIEW_STATUS: //View current status
					System.out.println(solver.getSolution());
					for (int i = 0; i<playerNames.size(); i++) {
						System.out.println(playerNames.get(i).toString());
					}
					break;
				default: //Quit
					continueGame = false;
			}
		}
	}
}
