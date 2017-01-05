package src.logic;

import src.logic.card.Card;

class Suggestion {
	private Card room;
	private Card suspect;
	private Card weapon;
	public Suggestion(Card room, Card suspect, Card weapon) {
		this.room = room;
		this.suspect = suspect;
		this.weapon = weapon;
	}
	public Card getRoom() {
		return room;
	}
	public Card getSuspect() {
		return suspect;
	}
	public Card getWeapon() {
		return weapon;
	}
}
