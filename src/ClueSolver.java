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
		System.out.print(prompt);
		int value = Integer.MAX_VALUE;
		try {
			value = scanner.nextInt();
		} catch (InputMismatchException e) {
			System.out.println("Please enter a valid number");
			scanner.nextLine();
		}
		return value;
	}

	private static int verified_int(String prompt, String error_message, int min, int max, Scanner scanner) {
		int value = Integer.MAX_VALUE;
		while (value < min || value > max) {
			value = get_number(prompt, scanner);
			if (value != Integer.MAX_VALUE && (value < MIN_PLAYERS || value > MAX_PLAYERS)) {
				System.out.println(error_message);
			}
		}
		// System.out.println(String.format("Got value : %d", value));
		return value;
	}

	private static String createMenuList(String title, ArrayList<String> list) {
		int i = 1;
		String output = title + "\n\n";
		for (String s : list) {
			output += String.format("%d. %s\n", i, s);
			i++;
		}
		return output;
	}

	public static void main(String[] args) {
		System.out.println("Welcome to the Clue Solving Assistant");
		System.out.println("-------------------------------------\n");
	    Scanner scanner = new Scanner(System.in);
		//Get how many players
		String number_of_players_error = String.format("Sorry, there must be %d to %d players to play Clue.", MIN_PLAYERS, MAX_PLAYERS);
		int number_of_players = verified_int("How many players will be playing? ", number_of_players_error, MIN_PLAYERS, MAX_PLAYERS, scanner);

		//Get player names
		ArrayList<Player> player_names = new ArrayList<Player>();
    	// Get the user's name
    	System.out.print("Enter your name: ");
		String name = scanner.next();
		//Import the cards
		CardList c = CardList.getCardList();
		int total_cards = c.getNumberOfCards();
		int possible_cards = total_cards / number_of_players;
		boolean can_have_extra_card = (total_cards - 3) % number_of_players != 0;
		int number_of_cards = possible_cards;
		String number_of_cards_error = String.format("Sorry, there must be %d to %d cards.", possible_cards, possible_cards + 1);
		if (can_have_extra_card) {
			number_of_cards = verified_int("How many cards do you have? ", number_of_cards_error, possible_cards, possible_cards + 1, scanner);
		}
		int player_cards = number_of_cards;
		player_names.add(new Player(name, number_of_cards));
		System.out.println(String.format("Player 1: %s", name));
		for (int i = 2; i<=number_of_players; i++) {
    		System.out.print("Enter the name of the next player (to the left): ");
			name = scanner.next();
			if (can_have_extra_card) {
				number_of_cards = verified_int(String.format("How many cards does %s have? ", name), number_of_cards_error, possible_cards, possible_cards + 1, scanner);
			}
			player_names.add(new Player(name, number_of_cards));
	    	System.out.println(String.format("Player %d: %s", i, name));
		}

		ClueLogic solver = new ClueLogic(player_names);
		//Get what cards you have
		for (int i = 1; i<player_cards; ) {
			int cardNumber = verified_int(createMenuList("Please select a card you have", c.getAllCardNames()), "Please select a valid card", 1,
				total_cards, scanner);
			//Only continue if it is a new card
			if (solver.addStartCard(cardNumber-1)) {
				i++;
			}
		}

		//Begin the game
		String mainMenuText = "Main menu\n\n1. Make a suggestion\n2. View Current Status\n3. Exit\n";
		String mainMenuError = "Please choose a valid option";
		boolean continueGame = true;
		while (continueGame) {
			int mainMenuOption = verified_int(mainMenuText, mainMenuError, 1, 3, scanner);
			switch (mainMenuOption) {
				case 1: //Make a suggestion
					//Get number of the player making the suggestion
					int playerNumber = verified_int(createMenuList("Which player will make a suggestion?", solver.getPlayerList()), "Please select a valid player",
						1, number_of_players, scanner);
					//Pick a room
					int roomNumber = verified_int(createMenuList("Where did the murder occur?", c.getRoomNames()), "Please select a valid player",
						1, c.getNumberOfRooms(), scanner);
					//Pick a suspect
					int suspectNumber = verified_int(createMenuList("Who did it?", c.getSuspectNames()), "Please select a valid player",
						1, c.getNumberOfSuspects(), scanner);
					//Pick a weapon
					int weaponNumber = verified_int(createMenuList("What weapon did they use?", c.getWeaponNames()), "Please select a valid player",
						1, c.getNumberOfWeapons(), scanner);
					//Get number of the player making the suggestion
					ArrayList<String> players = solver.getPlayerList();
					players.set(playerNumber-1, "Nobody");
					int endingPlayer = verified_int(createMenuList("Which player was able to disprove the suggestion?", players), "Please select a valid player",
							1, number_of_players, scanner);
					break;
				case 2: //View current status
					System.out.println("TODO");
					break;
				default:
					continueGame = false;
			}
		}
	}
}
