package src.logic;

import java.util.ArrayList;
import src.logic.card.*;
// import src.logic.Player;

public class ClueLogic {
	ArrayList<Player> players;
	CardList cards;
	public ClueLogic(ArrayList<Player> players) {
		this.players = players;
		cards = CardList.getCardList();
	}
	public void addKnownCard(Card knownCard, int playerNumber) {
		players.get(playerNumber).addCard(knownCard);
	}
	public boolean addStartCard(int cardNumber) {
		Card card = cards.getCard(cardNumber);
		if (card.isKnown()) {
			return false;
		}
		card.setKnown(true);
		players.get(1).addCard(card);
		return true;
	}
	public ArrayList<String> getPlayerList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Player p : players) {
			list.add(p.getName());
		}
		return list;
	}
}
