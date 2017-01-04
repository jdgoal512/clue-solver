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
	private ArrayList<Card> all_cards;
	private CardList() {
		suspects = new ArrayList<Card>();
		weapons = new ArrayList<Card>();
		rooms = new ArrayList<Card>();
		all_cards = new ArrayList<Card>();

		suspects.add(new Card(CardValue.PLUM, TypeOfCard.SUSPECT, "Professor Plum"));
		suspects.add(new Card(CardValue.MUSTARD, TypeOfCard.SUSPECT, "Colonel Musard"));
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
			all_cards.add(c);
		}
		for (Card c : weapons) {
			all_cards.add(c);
		}
		for (Card c : rooms) {
			all_cards.add(c);
		}
	}

	public int getNumberOfCards() {
		return all_cards.size();
	}

	public ArrayList<String> getAllCardNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (Card c: all_cards) {
			if (c.isKnown()) {
				list.add("(" + c.getDescription() + ")");

			} else {
				list.add(c.getDescription());
			}
		}
		return list;
	}

	public ArrayList<String> getRoomNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (Card c: rooms) {
			if (c.isKnown()) {
				list.add("(" + c.getDescription() + ")");

			} else {
				list.add(c.getDescription());
			}
		}
		return list;
	}
	public int getNumberOfRooms() {
		return rooms.size();
	}
	public ArrayList<String> getSuspectNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (Card c: suspects) {
			if (c.isKnown()) {
				list.add("(" + c.getDescription() + ")");
			} else {
				list.add(c.getDescription());
			}
		}
		return list;
	}

	public int getNumberOfSuspects() {
		return suspects.size();
	}
	public ArrayList<String> getWeaponNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (Card c: weapons) {
			if (c.isKnown()) {
				list.add("(" + c.getDescription() + ")");
			} else {
				list.add(c.getDescription());
			}
		}
		return list;
	}

	public int getNumberOfWeapons() {
		return weapons.size();
	}
	public Card getCard(int cardNumber) {
		return all_cards.get(cardNumber);
	}
}
