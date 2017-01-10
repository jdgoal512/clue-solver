package src.logic.card;

import java.util.ArrayList;

/**
 * Singleton representing deck of all the Clue cards
**/
public class CardList {
	//Variables
	private static CardList cards = null; //The singleton
	private ArrayList<Card> suspects; //Suspect cards
	private ArrayList<Card> weapons; //Weapon cards
	private ArrayList<Card> rooms; //Room cards
	private ArrayList<Card> allCards; //All the cards

	//Methods

	/**
	 * Gives the singleton group of cards
	 * @return cards The group of cards
	**/
	public static CardList getCardList() {
		if (cards == null) {
			cards = new CardList();
		}
		return cards;
	}

	/**
	 * Creates a new card list
	**/
	private CardList() {
		//Define member variables
		suspects = new ArrayList<Card>();
		weapons = new ArrayList<Card>();
		rooms = new ArrayList<Card>();
		allCards = new ArrayList<Card>();
		//Import the suspects
		suspects.add(new Card(TypeOfCard.SUSPECT, "Professor Plum")); //1 X
		suspects.add(new Card(TypeOfCard.SUSPECT, "Colonel Mustard")); //2 X
		suspects.add(new Card(TypeOfCard.SUSPECT, "Mr. Green")); //3 X
		suspects.add(new Card(TypeOfCard.SUSPECT, "Miss Scarlet")); //4 X
		suspects.add(new Card(TypeOfCard.SUSPECT, "Ms. White")); //5 X
		suspects.add(new Card(TypeOfCard.SUSPECT, "Mrs. Peacock")); //6
		//Import the weapons
		weapons.add(new Card(TypeOfCard.WEAPON, "Bat")); //7 X
		weapons.add(new Card(TypeOfCard.WEAPON, "Pistol")); //8 X
		weapons.add(new Card(TypeOfCard.WEAPON, "Knife")); //9 X
		weapons.add(new Card(TypeOfCard.WEAPON, "Candlestick")); //10 X
		weapons.add(new Card(TypeOfCard.WEAPON, "Rope")); //11 X
		weapons.add(new Card(TypeOfCard.WEAPON, "Ax")); //12 X
		weapons.add(new Card(TypeOfCard.WEAPON, "Dumbbell")); //13 X
		weapons.add(new Card(TypeOfCard.WEAPON, "Poison")); //14
		weapons.add(new Card(TypeOfCard.WEAPON, "Trophy")); //15 X
		//Import the rooms
		rooms.add(new Card(TypeOfCard.ROOM, "Guest House")); //16 X
		rooms.add(new Card(TypeOfCard.ROOM, "Dining Room")); //17 X
		rooms.add(new Card(TypeOfCard.ROOM, "Kitchen")); //18 X
		rooms.add(new Card(TypeOfCard.ROOM, "Patio")); //19 X
		rooms.add(new Card(TypeOfCard.ROOM, "Spa")); //20 X
		rooms.add(new Card(TypeOfCard.ROOM, "Theater")); //21 X
		rooms.add(new Card(TypeOfCard.ROOM, "Living Room")); //22
		rooms.add(new Card(TypeOfCard.ROOM, "Observatory")); //23 X
		rooms.add(new Card(TypeOfCard.ROOM, "Hall")); //24 X
		//Add all cards to the list of all the cardsLounge
		for (Card c : suspects) {
			allCards.add(c);
		}
		for (Card c : weapons) {
			allCards.add(c);
		}
		for (Card c : rooms) {
			allCards.add(c);
		}
	}

	/**
	 * Gives the integer value of how many clue cards are in the deck.
	 * The sum of suspects, weapons, and rooms
	 * @return totalCards The number of cards in the deck
	**/
	public int getNumberOfCards() {
		return allCards.size();
	}

	/**
	 * Gives the integer value of how many room cards there are
	 * @return numberOfRooms The number of room clue cards
	**/
	public int getNumberOfRooms() {
		return rooms.size();
	}

	/**
	 * Gives the integer value of the how many suspect cards there are
	 * @return numberOfSuspects The number of suspect cards
	**/
	public int getNumberOfSuspects() {
		return suspects.size();
	}

	/**
	 * Gives the integer value of how many weapon cards there are
	 * @return numberOfWeapons The number of weapon cards
	**/
	public int getNumberOfWeapons() {
		return weapons.size();
	}

	/**
	 * Gives the specified card from the list of all the clue cards
	 * @param cardNumber The index of the card in the list of all the cards
	 * @return card The card from the list of cards at the specified index
	**/
	public Card getCard(int cardNumber) {
		return allCards.get(cardNumber);
	}

	/**
	 * Gives the specified suspect card from the list of suspect clue cards
	 * @param cardNumber The index of the suspect card in the list of suspect cards
	 * @return card The suspect card from the suspect card list at the specified index
	**/
	public Card getSuspect(int cardNumber) {
		return suspects.get(cardNumber);
	}

	/**
	 * Gives the specified room card from the list room clue cards
	 * @param cardNumber The index of the room card in the list of room cards
	 * @return card The room card from the room card list at the specified index
	**/
	public Card getRoom(int cardNumber) {
		return rooms.get(cardNumber);
	}

	/**
	 * Gives the specified weapon card from the list of room clue cards
	 * @return card The weapon card from the weapon card list at the specified index
	**/
	public Card getWeapon(int cardNumber) {
		return weapons.get(cardNumber);
	}

	/**
	 * Gives an ArrayList containing all clue cards
	 * @return allCards The list of all the clue cards
	**/
	public ArrayList<Card> getCards() {
		return allCards;
	}

	/**
	 * Gives an ArrayList containing all the room clue cards
	 * @return rooms The list of all the room clue cards
	**/
	public ArrayList<Card> getRooms() {
		return rooms;
	}

	/**
	 * Gives an ArrayList containing all the weapon clue cards
	 * @return weapons The list of all the weapon clue cards
	**/
	public ArrayList<Card> getWeapons() {
		return weapons;
	}

	/**
	 * Gives an ArrayList containing all the suspect clue cards
	**/
	public ArrayList<Card> getSuspects() {
		return suspects;
	}

	/**
	 * Clears the known flag on all cards
	**/
	public void resetCards() {
		for (Card c : allCards) {
			c.setKnown(false);
		}
	}
}
