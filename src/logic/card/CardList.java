package src.logic.card;

// import java.util.HashSet;
import java.util.ArrayList;

public class CardList {
	private static CardList cards = null;

	public static CardList getCardList() {
		if (cards == null) {
			cards = new CardList();
		}
		return cards;
	}

	private ArrayList<Card> suspects;
	private ArrayList<Card> weapons;
	private ArrayList<Card> rooms;
	private ArrayList<Card> allCards;
	private CardList() {
		suspects = new ArrayList<Card>();
		weapons = new ArrayList<Card>();
		rooms = new ArrayList<Card>();
		allCards = new ArrayList<Card>();

		suspects.add(new Card(CardValue.PLUM, TypeOfCard.SUSPECT, "Professor Plum"));
		suspects.add(new Card(CardValue.MUSTARD, TypeOfCard.SUSPECT, "Colonel Mustard"));
		suspects.add(new Card(CardValue.GREEN, TypeOfCard.SUSPECT, "Mr. Green"));
		suspects.add(new Card(CardValue.SCARLET, TypeOfCard.SUSPECT, "Miss Scarlet"));
		suspects.add(new Card(CardValue.WHITE, TypeOfCard.SUSPECT, "Ms. White"));
		suspects.add(new Card(CardValue.PEACOCK, TypeOfCard.SUSPECT, "Mrs. Peacock"));

		weapons.add(new Card(CardValue.KNIFE, TypeOfCard.WEAPON, "Knife"));
		weapons.add(new Card(CardValue.CANDLESTICK, TypeOfCard.WEAPON, "Candlestick"));
		weapons.add(new Card(CardValue.REVOLVER, TypeOfCard.WEAPON, "Revolver"));
		weapons.add(new Card(CardValue.PIPE, TypeOfCard.WEAPON, "Lead Pipe"));
		weapons.add(new Card(CardValue.ROPE, TypeOfCard.WEAPON, "Rope"));
		weapons.add(new Card(CardValue.WRENCH, TypeOfCard.WEAPON, "Wrench"));

		rooms.add(new Card(CardValue.HALL, TypeOfCard.ROOM, "Hall"));
		rooms.add(new Card(CardValue.CONSERVATORY, TypeOfCard.ROOM, "Conservatory"));
		rooms.add(new Card(CardValue.DINING_ROOM, TypeOfCard.ROOM, "Dining Room"));
		rooms.add(new Card(CardValue.KITCHEN, TypeOfCard.ROOM, "Kitchen"));
		rooms.add(new Card(CardValue.STUDY, TypeOfCard.ROOM, "Study"));
		rooms.add(new Card(CardValue.LIBRARY, TypeOfCard.ROOM, "Library"));
		rooms.add(new Card(CardValue.BALLROOM, TypeOfCard.ROOM, "Ballroom"));
		rooms.add(new Card(CardValue.LOUNGE, TypeOfCard.ROOM, "Lounge"));
		rooms.add(new Card(CardValue.BILLIARD_ROOM, TypeOfCard.ROOM, "Billiards Room"));

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

	public int getNumberOfCards() {
		return allCards.size();
	}

	public int getNumberOfRooms() {
		return rooms.size();
	}

	public int getNumberOfSuspects() {
		return suspects.size();
	}

	public int getNumberOfWeapons() {
		return weapons.size();
	}
	public Card getCard(int cardNumber) {
		return allCards.get(cardNumber);
	}
	public Card getSuspect(int cardNumber) {
		return suspects.get(cardNumber);
	}
	public Card getRoom(int cardNumber) {
		return rooms.get(cardNumber);
	}
	public Card getWeapon(int cardNumber) {
		return weapons.get(cardNumber);
	}
	public ArrayList<Card> getCards() {
		return allCards;
	}
	public ArrayList<Card> getRooms() {
		return rooms;
	}
	public ArrayList<Card> getWeapons() {
		return weapons;
	}
	public ArrayList<Card> getSuspects() {
		return suspects;
	}
}
