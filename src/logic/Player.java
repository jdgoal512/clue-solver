package src.logic;

import java.util.HashSet;
import java.util.ArrayList;
import src.logic.card.*;
import java.util.Iterator;

public class Player {
	public enum CardStatus {
		KNOWN, NOT_POSSIBLE, POSSIBLE, UNKNOWN
	}
	private String name;
	private HashSet<Card> knownCards;
	private HashSet<Card> possibleCards;
	private HashSet<Card> cardsNotPossible;
	private ArrayList<HashSet<Card>> possibleSuggestions;
	private int numberOfCards;
	private boolean solved;

	public Player(String name, int numberOfCards) {
		this.name = name;
		this.numberOfCards = numberOfCards;
		knownCards = new HashSet<Card>();
		possibleCards = new HashSet<Card>();
		cardsNotPossible = new HashSet<Card>();
		possibleSuggestions = new ArrayList<HashSet<Card>>();
		solved = false;
		ArrayList<Card> all_cards = CardList.getCardList().getCards();
		for (Card c : all_cards) {
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

	public void addCard(Card c) {
		if (possibleCards.contains(c)) {
			possibleCards.remove(c);
		}
		knownCards.add(c);
		if (knownCards.size() == numberOfCards) { //Check if the user has all the cards they can_have_extra_card
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

	public String getName() {
		return name;
	}

	public void addCardNotPossible(Card c) {
		if (possibleCards.contains(c)) {
			possibleCards.remove(c);
			cardsNotPossible.add(c);
			removeFromSuggestions(c);
			ClueLogic.getClueLogic().checkIfCardIsUnowned(c); //Check if no one owns that card
		}
		//TODO: Make sure all cards except one of a type have not been found
		ArrayList<Card> otherCards = null;
		switch (c.getType()) {
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
		//
		HashSet<Card> unknownCards = new HashSet<Card>();
		for (Card otherCard : otherCards) {
			if (!otherCard.isKnown()) {
				unknownCards.add(otherCard);
			}
		}

	}

	public void cleanUpSuggestions() {
		for (int i=0; i<possibleSuggestions.size(); i++) {
			if (possibleSuggestions.get(i).size() == 0) {
				possibleSuggestions.remove(i);
				i--;
			}
		}
	}

	public void checkSuggestion(HashSet<Card> suggestion) {
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

	public void addPossibleSuggestion(Suggestion s) {
		HashSet<Card> newPossibleSuggestion = new HashSet<Card>();
		//Check if any of the cards are already known to be possesed by the players
		if (knownCards.contains(s.getSuspect()) || knownCards.contains(s.getWeapon()) || knownCards.contains(s.getRoom())) {
			return; //Nothing new is learned, throw out the suggestion
		}
		newPossibleSuggestion.add(s.getRoom());
		newPossibleSuggestion.add(s.getSuspect());
		newPossibleSuggestion.add(s.getWeapon());
		checkSuggestion(newPossibleSuggestion);

	}
	@Override
	public String toString() {
		String output = String.format("∨∨∨∨vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv\n\t\033[34m\033[1m%s\033[0m\033[39m\n\n\033[32mKnown cards:\033[39m\n", name);
		int index = 1;
		for (Card c : knownCards) {
			output += String.format("%d. %s\t", index, c.getDescription());
			index++;
		}
		for (int i = knownCards.size(); i<numberOfCards; i++) {
			output += String.format("%d. Unknown\t", index);
			index++;
		}
		output += "\n\n\033[31mCards not owned:\033[39m\n";
		index = 1;
		for (Card c : cardsNotPossible) {
			output += String.format("%d. %s\t", index, c.getDescription());
			index++;
		}
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
	public boolean hasCard(Card c) {
		return knownCards.contains(c);
	}
	public HashSet<Card> getKnownCards() {
		return knownCards;
	}
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
	public boolean isSolved() {
		return solved;
	}
}
