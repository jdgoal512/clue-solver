package src;
import java.util.Scanner;
import src.logic.*;
import src.logic.card.*;
import java.util.InputMismatchException;
import java.util.ArrayList;

class ClueSolver {
	private static int MIN_PLAYERS = 3;
	private static int MAX_PLAYERS = 6;

	private static int get_number(String prompt, Scanner scanner) {
		System.out.print(String.format("\033[34m%s\033[39m", prompt));
		int value = Integer.MAX_VALUE;
		try {
			value = scanner.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("\033[31mPlease enter a valid number\033[39m");
			scanner.nextLine();
		}
		return value;
	}

	private static int verified_int(String prompt, String error_message, int min, int max, Scanner scanner) {
		int value = Integer.MAX_VALUE;
		while (value < min || value > max) {
			value = get_number(prompt, scanner);
			if (value != Integer.MAX_VALUE && (value < MIN_PLAYERS || value > MAX_PLAYERS)) {
				System.out.println(String.format("\033[31m%s\033[39m", error_message));
			}
		}
		// System.out.println(String.format("Got value : %d", value));
		return value;
	}

	private static String createMenuList(String title, ArrayList<String> list) {
		int i = 1;
		String output = title + "\033[39m\n\n";
		for (String s : list) {
			output += String.format("%d. %s\n", i, s);
			i++;
		}
		return output;
	}

	public static void main(String[] args) {
	System.out.println("\033[35m-----------------------------------------");
		System.out.println("| \033[1mWelcome to the Clue Solving Assistant\033[0m |");
		System.out.println("-----------------------------------------\033[39m\n");
	    Scanner scanner = new Scanner(System.in);
		//Get how many players
		String number_of_players_error = String.format("Sorry, there must be %d to %d players to play Clue.", MIN_PLAYERS, MAX_PLAYERS);
		int number_of_players = verified_int("How many players will be playing? ", number_of_players_error, MIN_PLAYERS, MAX_PLAYERS, scanner);

		//Get player names
		ArrayList<Player> player_names = new ArrayList<Player>();
    	// Get the user's name
    	System.out.print("\033[34mEnter your name:\033[39m ");
		String name = scanner.next();
		//Import the cards
		CardList c = CardList.getCardList();
		int total_cards = c.getNumberOfCards();
		int cardsAccountedFor = 3;
		int possible_cards = (total_cards - 3) / number_of_players;
		boolean can_have_extra_card = (total_cards - 3) % number_of_players != 0;
		int number_of_cards = possible_cards;
		String number_of_cards_error = String.format("\033[31mSorry, there must be %d to %d cards.\033[39m", possible_cards, possible_cards + 1);
		if (can_have_extra_card) {
			number_of_cards = verified_int("\033[34mHow many cards do you have?\033[39m ", number_of_cards_error, possible_cards, possible_cards + 1, scanner);
		}
		int player_cards = number_of_cards;
		cardsAccountedFor += player_cards;
		player_names.add(new Player(name, number_of_cards));
		System.out.println(String.format("Player 1: %s\tCards: %s", name, number_of_cards));
		for (int i = 2; i<=number_of_players; i++) {
    		System.out.print("\033[34mEnter the name of the next player (to the left):\033[39m ");
			name = scanner.next();
			if (can_have_extra_card) {
				int cardsLeft = total_cards - cardsAccountedFor;
				if (cardsLeft % number_of_players == 0) {
					can_have_extra_card = false;
					if (cardsLeft % possible_cards != 0) {
						number_of_cards = possible_cards + 1;
					} else {
						number_of_cards = possible_cards;
					}
				} else {
					number_of_cards = verified_int(String.format("\033[34mHow many cards does %s have?\033[39m ", name),
						number_of_cards_error, possible_cards, possible_cards + 1, scanner);
				}
			}
			cardsAccountedFor += number_of_cards;
			player_names.add(new Player(name, number_of_cards));
	    	System.out.println(String.format("Player %d: %s\tCards: %s", i, name, number_of_cards));
		}

		ClueLogic solver = new ClueLogic(player_names);
		//Get what cards you have
		for (int i = 0; i<player_cards; ) {
			int cardNumber = verified_int(createMenuList("Please select a card you have", solver.getAllCardNames()), "Please select a valid card", 1,
				total_cards, scanner);
			//Only continue if it is a new card
			if (solver.addStartCard(cardNumber-1)) {
				i++;
			}
		}

		//Begin the game
		String mainMenuText = "Main menu\033[39m\n\n1. Make a suggestion\n2. Add a known card\n3. View Current Status\n4. Exit\n";
		String mainMenuError = "Please choose a valid option";
		boolean continueGame = true;
		while (continueGame) {
			int mainMenuOption = verified_int(mainMenuText, mainMenuError, 1, 4, scanner);
			switch (mainMenuOption) {
				case 1: //Make a suggestion
					//Get number of the player making the suggestion
					int playerNumber = verified_int(createMenuList("Which player will make a suggestion?", solver.getPlayerList()), "Please select a valid player",
						1, number_of_players, scanner);
					//Pick a room
					int roomNumber = verified_int(createMenuList("Where did the murder occur?", solver.getRoomNames()), "Please select a valid player",
						1, c.getNumberOfRooms(), scanner);
					//Pick a suspect
					int suspectNumber = verified_int(createMenuList("Who did it?", solver.getSuspectNames()), "Please select a valid player",
						1, c.getNumberOfSuspects(), scanner);
					//Pick a weapon
					int weaponNumber = verified_int(createMenuList("What weapon did they use?", solver.getWeaponNames()), "Please select a valid player",
						1, c.getNumberOfWeapons(), scanner);
					//Get number of the player making the suggestion
					ArrayList<String> players = solver.getPlayerList();
					players.set(playerNumber-1, "Nobody");
					int endingPlayer = verified_int(createMenuList("Which player was able to disprove the suggestion?", players), "Please select a valid player",
							1, number_of_players, scanner);
					solver.makeSuggestion(playerNumber-1, c.getRoom(roomNumber-1), c.getSuspect(suspectNumber-1), c.getWeapon(weaponNumber-1), endingPlayer-1);
					break;
				case 2: //Add a known card
					int knownPlayerNumber = verified_int(createMenuList("Which player's was learned?", solver.getPlayerList()), "Please select a valid player",
						2, number_of_players, scanner);
					//Make sure card is not already known
					boolean alreadyKnown = true;
					int cardNumber = 0;
					Card knownCard = null;
					while (alreadyKnown) {
						cardNumber = verified_int(createMenuList(String.format("Please select which card %s has:",
							player_names.get(knownPlayerNumber-1).getName()), solver.getAllCardNames()), "Please select a valid card", 1,
							total_cards, scanner);
						knownCard = CardList.getCardList().getCard(cardNumber-1);
						alreadyKnown = knownCard.isKnown();
					}
					boolean added = solver.addKnownCard(knownCard, player_names.get(knownPlayerNumber-1));
					if (added) {
						System.out.println("The card was sucessfully added");
					} else {
						System.out.println("There was an error adding the card");
					}
					break;
				case 3: //View current status
					System.out.println(solver.getSolution());
					for (int i = 0; i<player_names.size(); i++) {
						System.out.println(player_names.get(i).toString());
					}
					break;
				default: //Quit
					continueGame = false;
			}
		}
	}
}
