package src.logic;

import java.util.HashSet;
import java.util.ArrayList;
import src.logic.card.*;
import java.util.Iterator;

/**
 * Represents a player or solution in the game of clue
 * Keeps a record of which cards it has, does not have, and suggestions it has said yes to
**/
public class Player {
	//Variables
	private String name; //The player's name
	private HashSet<Card> knownCards; //Cards the player is known to have
	private HashSet<Card> possibleCards; //Cards that are still not known whether the player has it or not
	private HashSet<Card> cardsNotPossible; //Cards a player is known not to have
	private ArrayList<HashSet<Card>> possibleSuggestions; //Suggestions the player has said yes to
	private int numberOfCards; //How many cards the player has
	private boolean solved; //Whether or not all cards the player can have are known or not

	//List of what status a card may have relative to a player
	public enum CardStatus {
		KNOWN, NOT_POSSIBLE, POSSIBLE, UNKNOWN
	}

	//Methods
	/**
	 * Creates a new player
	 * @param name The player's name
	 * @param numberOfCards The maximum number of cards the player can have
	**/
	public Player(String name, int numberOfCards) {
		this.name = name;
		this.numberOfCards = numberOfCards;
		knownCards = new HashSet<Card>();
		possibleCards = new HashSet<Card>();
		cardsNotPossible = new HashSet<Card>();
		possibleSuggestions = new ArrayList<HashSet<Card>>();
		solved = false;
		ArrayList<Card> allCards = CardList.getCardList().getCards();
		for (Card c : allCards) {
			possibleCards.add(c);
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object other) {
		if (other instanceof Player) {
			Player otherPlayer = (Player)other;
			return name == otherPlayer.name;
		}
		return false;
	}

	/**
	 * Adds a known card to the player's hand
	 * @param name The player's name
	 * @param numberOfCards The maximum number of cards the player can have
	**/
	public void addCard(Card c) {
		if (possibleCards.contains(c)) {
			possibleCards.remove(c);
		}
		knownCards.add(c);
		if (knownCards.size() == numberOfCards) { //Check if the user has all the cards they can
			while (possibleCards.size() != 0) { //Remove all other cards as possiblities if all cards are known
				Card cardToRemove = possibleCards.iterator().next();
				addCardNotPossible(cardToRemove);
			}
			solved = true;
		}
		//Remove suggestions that have this card
		for (int i=0; i<possibleSuggestions.size(); i++) {
			if (possibleSuggestions.get(i).contains(c)) {
				possibleSuggestions.remove(i);
				i--;
			}
		}
	}

	/**
	 * Gives the player's name
	 * @return name A string containing the player's name
	**/
	public String getName() {
		return name;
	}

	/**
	 * Marks a card as not possible for the player to possess and removes the card from all suggestions
	 * the player has said yes to
	 * @param card The card to mark as impossible for the player to have
	**/
	public void addCardNotPossible(Card card) {
		if (possibleCards.contains(card)) {
			possibleCards.remove(card);
			cardsNotPossible.add(card);
			removeFromSuggestions(card);
			ClueLogic.getClueLogic().checkIfCardIsUnowned(card); //Check if no one owns that card
		}
		ArrayList<Card> otherCards = null;
		switch (card.getType()) {
			case ROOM:
				otherCards = CardList.getCardList().getRooms();
				break;
			case SUSPECT:
				otherCards = CardList.getCardList().getSuspects();
				break;
			case WEAPON:
				otherCards = CardList.getCardList().getWeapons();
				break;
			default:
				System.out.println("Error");
		}
		//Make sure that all the cards of a type have not been taken - possibly implemented somewhere else
		// HashSet<Card> unknownCards = new HashSet<Card>();
		// for (Card otherCard : otherCards) {
		// 	if (!otherCard.isKnown()) {
		// 		unknownCards.add(otherCard);
		// 	}
		// }

	}

	/**
	 * Removes all blank suggestions from the list of suggestions they player has said yes to
	**/
	private void cleanUpSuggestions() {
		for (int i=0; i<possibleSuggestions.size(); i++) {
			if (possibleSuggestions.get(i).size() == 0) {
				possibleSuggestions.remove(i);
				i--;
			}
		}
	}

	/**
	 * Checks a suggestion against the cards a player is known to have or not have
	 * and adds it to the list of suggestions if it is valid
	 * @param suggestion The suggestion to check and add
	**/
	private void checkSuggestion(HashSet<Card> suggestion) {
		//Filter the suggestion
		//Check sugestion against cards the player couldn't have
		HashSet<Card> cardsToRemove = new HashSet<Card>();
		for (Card c : suggestion) { //Precheck cards if any contain existing information
			if (getStatusOfCard(c) == CardStatus.KNOWN) {
				suggestion.clear();
				return; //Nothing new is learned, throw it out
			} else if (getStatusOfCard(c) == CardStatus.NOT_POSSIBLE) {
				cardsToRemove.add(c);
			}
		}
		for (Card c : cardsToRemove) { //Remove impossible cards from the suggestion
			suggestion.remove(c);
		}
		if (suggestion.size() == 0) {
			return; //Nothing new is learned, the suggestion did not provide any new information
		} else if (suggestion.size() == 1) { //Learned something!
			Card learnedCard = suggestion.iterator().next();
			ClueLogic.getClueLogic().addKnownCard(learnedCard, this);
			suggestion.clear();
		} else { //Add the suggestion to the possible candidates
			possibleSuggestions.add(suggestion);
		}
		cleanUpSuggestions();
	}

	/**
	* Removes a card from all the suggestions
	* (The card is not a card owned by the player)
	**/
	public void removeFromSuggestions(Card cardToRemove) {
		HashSet<Card> newSolutions = new HashSet<Card>();
		for (int i=0; i<possibleSuggestions.size(); i++) {
			if (possibleSuggestions.get(i).contains(cardToRemove)) {
				possibleSuggestions.get(i).remove(cardToRemove);
				if (possibleSuggestions.get(i).size() == 1) {
					newSolutions.add(possibleSuggestions.get(i).iterator().next());
				}
			}
		}
		for (Card c : newSolutions) {
			ClueLogic.getClueLogic().addKnownCard(c, this);
		}
	}

	/**
	 * Adds a suggetion to the list of suggestions the player has said yes to if it may contains
	 * any new information (the player doesn't have any of the cards)
	 * @param suggestion The suggestion to add to the list of suggestions
	**/
	public void addPossibleSuggestion(Suggestion suggestion) {
		HashSet<Card> newPossibleSuggestion = new HashSet<Card>();
		//Check if any of the cards are already known to be possesed by the players
		if (knownCards.contains(suggestion.getSuspect()) || knownCards.contains(suggestion.getWeapon())
				|| knownCards.contains(suggestion.getRoom())) {
			return; //Nothing new is learned, throw out the suggestion
		}
		newPossibleSuggestion.add(suggestion.getRoom());
		newPossibleSuggestion.add(suggestion.getSuspect());
		newPossibleSuggestion.add(suggestion.getWeapon());
		checkSuggestion(newPossibleSuggestion);
	}

	/**
	 * Adds a set of cars as a suggestion to the list of suggestions the player has said yes to if it may contains
	 * any new information (the player doesn't have any of the cards)
	 * @param suggestion The set of cards to add as a suggestion
	**/
	public void addPossibleSuggestion(HashSet<Card> suggestion) {
		HashSet<Card> extraInfo = new HashSet<Card>();
		//Check and clean up extra infomation from the suggestion
		for (Card c : suggestion) {
			if (knownCards.contains(c)) {
				return; //Nothing new is learned, the player already has one of the cards
			} else if (c.isKnown() || cardsNotPossible.contains(c)) {
				extraInfo.add(c);
			}
		}
		//Remove extra information
		for (Card c : extraInfo) {
			suggestion.remove(c);
		}
		if (suggestion.size() == 0) {
			return; //Nothing new is learned, the suggestion did not provide any new information
		} else if (suggestion.size() == 1) { //Learned something!
			Card learnedCard = suggestion.iterator().next();
			ClueLogic.getClueLogic().addKnownCard(learnedCard, this);
			suggestion.clear();
		} else { //Add the suggestion to the possible candidates
			possibleSuggestions.add(suggestion);
		}
		cleanUpSuggestions(); //Check other suggestions just in case something else is learned
	}

	@Override
	public String toString() {
		String output = String.format("∨∨∨∨vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv\n"+
			"\t\033[34m\033[1m%s\033[0m\033[39m\n\n\033[32mKnown cards:\033[39m\n", name);
		int index = 1;
		//Give a list of all known cards
		for (Card c : knownCards) {
			output += String.format("%d. %s\t", index, c.getDescription());
			index++;
		}
		for (int i = knownCards.size(); i<numberOfCards; i++) {
			output += String.format("%d. Unknown\t", index);
			index++;
		}
		//Give a list of cards known not to be in the player's hand if all their hand is not known
		if (!isSolved()) {
			output += "\n\n\033[31mCards not owned:\033[39m\n";
			index = 1;
			for (Card c : cardsNotPossible) {
				output += String.format("%d. %s\t", index, c.getDescription());
				index++;
			}
		}
		//Display other information if it is available
		if (possibleSuggestions.size() != 0) {
			output += "\n\n\033[33mOther information:\033[39m\n";
			index = 1;
			for (HashSet<Card> possibleSuggestion : possibleSuggestions) {
				String suggestion = String.format("%d. ", index);
				Iterator<Card> iter = possibleSuggestion.iterator();
				while (iter.hasNext()) {
					Card c = iter.next();
					if (iter.hasNext()) {
						suggestion += c.getDescription() + " or ";
					} else {
						suggestion += c.getDescription();
					}
				}
				for (Card c : possibleSuggestion) {
				}
				suggestion += "\n";
				index++;
				output += suggestion;
			}
			output += "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n";
		} else {
			output += "\n\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n";
		}
		return output;
	}

	/**
	 * Whether or not the player is known to possess a specified cardNumber
	 * @param card The card to check if the player has it
	 * @return hasCard Boolean value if the player definately has the card
	**/
	public boolean hasCard(Card card) {
		return knownCards.contains(card);
	}

	/**
	 * Gives a set of all the cards the player is known to possess in their hand
	 * @return knownCards Set of all cards known to be in the player's hand
	**/
	public HashSet<Card> getKnownCards() {
		return knownCards;
	}

	/**
	 * Gives the status of a card relative to a player. It may be known to be in the player's hand, known not to be in the player's hand,
	 * be in a suggestion the player has said yes to, or no information about the card being in the player's hand may be available.
	 * @return status The status of the card relative to the player
	**/
	public CardStatus getStatusOfCard(Card c) {
		if (hasCard(c)) {
			return CardStatus.KNOWN;
		} else if (cardsNotPossible.contains(c)) {
			return CardStatus.NOT_POSSIBLE;
		} else {
			for (HashSet<Card> suggestion : possibleSuggestions) {
				if (suggestion.contains(c)) {
					return CardStatus.POSSIBLE;
				}
			}
			return CardStatus.UNKNOWN;
		}
	}

	/**
	 * Whether or not all cards the player can have are known or not
	 * @return solved Boolean value of whether all the player's cards are known or not
	**/
	public boolean isSolved() {
		return solved;
	}

	public void clear() {
		knownCards.clear();
		possibleCards.clear();
		cardsNotPossible.clear();
		possibleSuggestions.clear();
		solved = false;
		ArrayList<Card> allCards = CardList.getCardList().getCards();
		for (Card c : allCards) {
			possibleCards.add(c);
		}
	}
}
