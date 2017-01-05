package src.logic;

import java.util.ArrayList;
import src.logic.card.*;
import java.util.HashSet;
// import src.logic.Player;

public class ClueLogic {
	public static Card noCard = new Card(CardValue.UNKNOWN, TypeOfCard.UNKNOWN, "None");
	ArrayList<Player> players;
	CardList cards;
	private Player solution;
	private static ClueLogic solver = null;

	public static ClueLogic getClueLogic() {
		return solver;
	}

	public ClueLogic(ArrayList<Player> players) {
		this.players = players;
		cards = CardList.getCardList();
		solution = new Player("########### Solution ###########", 3);
		solver = this;
	}

	public void checkIfCardIsUnowned(Card card) {
		for (Player p : players) {
			if (p.getStatusOfCard(card) != Player.CardStatus.NOT_POSSIBLE) {
				return;
			}
		}
		addKnownCard(card, solution);ArrayList<Card> otherCardsOfSameType;
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

	private void checkIfLastCard(Card card) {
		ArrayList<Card> otherCards;
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
		HashSet<Card> unknownCards = new HashSet<Card>();
		for (Card other : otherCards) {
			if (!other.isKnown()) {
				unknownCards.add(other);
			}
		}
		if (unknownCards.size() == 1) { //Card is part of the solution
			Card solutionCard = unknownCards.iterator().next();
			addKnownCard(solutionCard, solution);
			// removeCardFromOtherPlayers(solutionCard);
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
			for (Card otherCard : otherCardsOfSameType) {
				if (!otherCard.equals(solutionCard)) {
					solution.addCardNotPossible(otherCard);
				}
			}
		}
	}

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

	public boolean addKnownCard(Card knownCard, Player p) {
		if (knownCard.isKnown()) {
			return false;
		}
		knownCard.setKnown(true);
		p.addCard(knownCard);
		removeCardFromOtherPlayers(knownCard);
		checkIfLastCard(knownCard);
		return true;
		// players.get(playerNumber).addCard(knownCard);
	}

	public boolean addStartCard(int cardNumber) {
		Card card = cards.getCard(cardNumber);
		return addKnownCard(card, players.get(0));
	}

	public ArrayList<String> getPlayerList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Player p : players) {
			list.add(p.getName());
		}
		return list;
	}
	public void makeSuggestion(int suggestingPlayer, Card suggestedRoom, Card suggestedSuspect, Card suggestedWeapon, int resolvingPlayer) {
		// int playerNumber = suggestingPlayer
		for (int i = (suggestingPlayer + 1)%players.size(); i != suggestingPlayer; i = (i + 1)%players.size()) {
			System.out.println(String.format("Checking %s", players.get(i).getName()));
			if (i == resolvingPlayer) {
				System.out.println("Found player");
				if (suggestingPlayer == resolvingPlayer) { //No one resolved the suggestion, cards suggested are either owned by that player or are the solution
					return;
				}
				players.get(resolvingPlayer).addPossibleSuggestion(new Suggestion(suggestedRoom, suggestedSuspect, suggestedWeapon));
				return;
			} else {
				System.out.println("Adding bad suggestion");
				players.get(i).addCardNotPossible(suggestedRoom);
				players.get(i).addCardNotPossible(suggestedSuspect);
				players.get(i).addCardNotPossible(suggestedWeapon);
			}
		}
	}

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

	public Player getSolution() {
		return solution;
	}
}
