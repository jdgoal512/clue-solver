package src.logic;

import java.util.ArrayList;
import src.logic.card.*;
import java.util.HashSet;

/**
 * Stores a list of players, cards, suggestions, and known cards in a clue game.
 * Performs deductions and resolves what cards may be owned by other players in the envelope.
 * Represented by a psuedo-singleton class because it must receive a list of players to be created
 * so it cannot be a pure singleton.
**/
public class ClueLogic {
	//Variables
	ArrayList<Player> players; //List of players in the game
	CardList cards; //List of clue cards
	private Player solution; //Player represting the envelope/solution
	private static ClueLogic solver = null; //The singleton

	//Methods

	/**
	 * Gives the singleton for the ClueLogic class
	 * @return solver Returns the singleton clue logic class
	**/
	public static ClueLogic getClueLogic() {
		return solver;
	}

	/**
	 * Creates an instance of the ClueLogic solver. Updates the singleton for the class.
	**/
	public ClueLogic(ArrayList<Player> players) {
		this.players = players;
		cards = CardList.getCardList();
		solution = new Player("########### Solution ###########", 3);
		solver = this;
	}

	/**
	 * Checks if a card isn't owned by anyone (and thus is in the envelope) and adds it to the
	 * solution if it is unowned.
	 * @param card The card to check if it is unowned
	**/
	public void checkIfCardIsUnowned(Card card) {
		//Make sure the card could not possibly be possesed by any player
		for (Player p : players) {
			if (p.getStatusOfCard(card) != Player.CardStatus.NOT_POSSIBLE) {
				return;
			}
		}
		//Add the card to the solution
		addKnownCard(card, solution);ArrayList<Card> otherCardsOfSameType;
		//Remove all other cards of the same type (room, suspect, or weapon) as possiblities from the solution
		switch (card.getType()) {
			case ROOM:
				otherCardsOfSameType = CardList.getCardList().getRooms();
				break;
			case WEAPON:
				otherCardsOfSameType = CardList.getCardList().getWeapons();
				break;
			case SUSPECT:
				otherCardsOfSameType = CardList.getCardList().getSuspects();
				break;
			default:
				System.out.println("Error: unknown type of card");
				return;
		}
		for (Card otherCard : otherCardsOfSameType) {
			if (!otherCard.equals(card)) {
				solution.addCardNotPossible(otherCard);
			}
		}
	}

	/**
	 * Checks if all the cards of a type are possesed by someone other than the envelope.
	 * If so, the card is added to the envelope.
	 * @param card The card to check if it is the last unowned card of its type
	**/
	private void checkIfLastCard(Card card) {
		ArrayList<Card> otherCards;
		//Get the list of all other cards of the same type
		switch (card.getType()) {
			case SUSPECT:
			otherCards = CardList.getCardList().getSuspects();
			break;
			case WEAPON:
			otherCards = CardList.getCardList().getWeapons();
			break;
			case ROOM:
			otherCards = CardList.getCardList().getRooms();
			break;
			default: //Error
			System.out.println("Error: Invalid Card");
			return;
		}
		//Create a list of all the unowned cards of the specified type
		HashSet<Card> unknownCards = new HashSet<Card>();
		for (Card other : otherCards) {
			if (!other.isKnown()) {
				unknownCards.add(other);
			}
		}
		if (unknownCards.size() == 1) { //Card is part of the solution
			Card solutionCard = unknownCards.iterator().next();
			addKnownCard(solutionCard, solution);
			ArrayList<Card> otherCardsOfSameType;
			switch (solutionCard.getType()) {
				case ROOM:
					otherCardsOfSameType = CardList.getCardList().getRooms();
					break;
				case WEAPON:
					otherCardsOfSameType = CardList.getCardList().getWeapons();
					break;
				case SUSPECT:
					otherCardsOfSameType = CardList.getCardList().getSuspects();
					break;
				default:
					System.out.println("Error: unknown type of card");
					return;
			}
			//Add all other cards of the same type as not possible for the solution, should be redundant
			for (Card otherCard : otherCardsOfSameType) {
				if (!otherCard.equals(solutionCard)) {
					solution.addCardNotPossible(otherCard);
				}
			}
		}
	}

	/**
	 * Marks a card as impossible for all players to possess (besides the owner)
	 * and removes it from all suggestions the other players have.
	 * @param c The card to remove as a possiblity for
	**/
	private void removeCardFromOtherPlayers(Card c) {
		for (Player p : players) {
			if (!p.hasCard(c)) {
				p.addCardNotPossible(c);
			}
		}
		if (!solution.hasCard(c)) {
			solution.addCardNotPossible(c);
		}
	}

	/**
	 * Adds a known card to a specified player's hand.
	 * Removes the card as a possiblity from all other players.
	 * Fails if the card is already known.
	 * @param knownCard The card add to the player's hand
	 * @param player The player to add the card's hand to
	 * @return wasAdded Whether or not the card was successfully added to the player's hand
	**/
	public boolean addKnownCard(Card knownCard, Player player) {
		if (knownCard.isKnown()) {
			return false;
		}
		knownCard.setKnown(true);
		player.addCard(knownCard);
		removeCardFromOtherPlayers(knownCard);
		checkIfLastCard(knownCard);
		return true;
	}

	/**
	 * Adds a known card to the user's hand
	 * Removes the card as a possiblity from all other players.
	 * Fails if the card is already known.
	 * @param knownCard The card add to the user's hand
	 * @param player The player to add the card's hand to
	 * @return wasAdded Whether or not the card was successfully added to the user's hand
	**/
	public boolean addStartCard(int cardNumber) {
		Card card = cards.getCard(cardNumber);
		return addKnownCard(card, players.get(0));
	}

	/**
	 * Gives a list of string with the names of all the players in the game in order starting with the user
	 * @return list The ArrayList of strings with all the player's names
	**/
	public ArrayList<String> getPlayerList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Player p : players) {
			list.add(p.getName());
		}
		return list;
	}

	/**
	 * Adds a suggestion to the databases and automatically performs deductions for all players
	 * @param suggestingPlayer index of the player who suggested a solution
	 * @param suggestedRoom Where the murder was suggested to have occured
	 * @param suggestedSuspect Who was suggested to have performed the murder
	 * @param suggestedWeapon What was suggested to be the weapon used in the murder
	 * @param resolvingPlayer index of the player who was able to disprove the murder. If nobody was able to prove it,
	 * then the index of the player who made the suggestion
	**/
	public void makeSuggestion(int suggestingPlayer, Card suggestedRoom, Card suggestedSuspect,
					Card suggestedWeapon, int resolvingPlayer) {
		for (int i = (suggestingPlayer + 1)%players.size(); i != suggestingPlayer; i = (i + 1)%players.size()) {
			System.out.println(String.format("Checking %s", players.get(i).getName()));
			if (i == resolvingPlayer) { //Add the suggestion to the possible cards the player has
				if (suggestingPlayer == resolvingPlayer) { //No one resolved the suggestion, cards suggested are either owned by that player or are the solution
					return;
				}
				players.get(resolvingPlayer).addPossibleSuggestion(new Suggestion(suggestedRoom, suggestedSuspect, suggestedWeapon));
				return;
			} else { //Remove all the cards in the suggestion from all players who were unable to disprove the suggestion
				players.get(i).addCardNotPossible(suggestedRoom);
				players.get(i).addCardNotPossible(suggestedSuspect);
				players.get(i).addCardNotPossible(suggestedWeapon);
			}
		}
	}

	/**
	 * Gives a list of strings of all possible suspects
	 * @return ArrayList of strings with all the suspect's names
	**/
	public ArrayList<String> getSuspectNames() {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Card> suspects = CardList.getCardList().getSuspects();
		for (Card c: suspects) {
			if (c.isKnown()) {
				if (solution.hasCard(c)) {
					list.add("*[\033[32m\033[1m" + c.getDescription() + "\033[0m\033[39m]*");
				} else {
					if (players.get(0).hasCard(c)) {
						list.add("(\033[36m" + c.getDescription() + "\033[39m)");
					} else {
						list.add("(\033[31m" + c.getDescription() + "\033[39m)");
					}
				}
			} else {
				list.add(c.getDescription());
			}
		}
		return list;
	}

	/**
	 * Gives a list of strings of all possible weapons
	 * @return ArrayList of strings with all the weapons
	**/
	public ArrayList<String> getWeaponNames() {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Card> weapons = CardList.getCardList().getWeapons();
		for (Card c: weapons) {
			if (c.isKnown()) {
				if (solution.hasCard(c)) {
					list.add("*[\033[32m\033[1m" + c.getDescription() + "\033[0m\033[39m]*");
				} else {
					if (players.get(0).hasCard(c)) {
						list.add("(\033[36m" + c.getDescription() + "\033[39m)");
					} else {
						list.add("(\033[31m" + c.getDescription() + "\033[39m)");
					}
				}
			} else {
				list.add(c.getDescription());
			}
		}
		return list;
	}

	/**
	 * Gives a list of strings of all possible rooms
	 * @return ArrayList of strings with all the rooms
	**/
	public ArrayList<String> getRoomNames() {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Card> rooms = CardList.getCardList().getRooms();
		for (Card c: rooms) {
			if (c.isKnown()) {
				if (solution.hasCard(c)) {
					list.add("*[\033[32m\033[1m" + c.getDescription() + "\033[0m\033[39m]*");
				} else {
					if (players.get(0).hasCard(c)) {
						list.add("(\033[36m" + c.getDescription() + "\033[39m)");
					} else {
						list.add("(\033[31m" + c.getDescription() + "\033[39m)");
					}
				}
			} else {
				list.add(c.getDescription());
			}
		}
		return list;
	}

	/**
	 * Gives a list of strings of all possible clue cards
	 * @return ArrayList of strings with all the clue cards
	**/
	public ArrayList<String> getAllCardNames() {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Card> allCards = CardList.getCardList().getCards();
		for (Card c: allCards) {
			if (c.isKnown()) {
				if (solution.hasCard(c)) {
					list.add("*[\033[32m\033[1m" + c.getDescription() + "\033[0m\033[39m]*");
				} else {
					if (players.get(0).hasCard(c)) {
						list.add("(\033[36m" + c.getDescription() + "\033[39m)");
					} else {
						list.add("(\033[31m" + c.getDescription() + "\033[39m)");
					}
				}
			} else {
				list.add(c.getDescription());
			}
		}
		return list;
	}

	/**
	 * Gives a player represeting the solution/envelope
	 * @return solution Player representing the solution/envelope
	**/
	public Player getSolution() {
		return solution;
	}
}
