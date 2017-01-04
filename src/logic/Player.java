package src.logic;

import java.util.HashSet;
import src.logic.card.*;

public class Player {
	private String name;
	private HashSet<Card> cards;
	private int number_of_cards;
	public Player(String name, int number_of_cards) {
		this.name = name;
		this.number_of_cards = number_of_cards;
		cards = new HashSet<Card>();
	}

	public String toString() {
		return name;
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
		cards.add(c);
	}

	public String getName() {
		return name;
	}
}
