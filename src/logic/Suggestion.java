package src.logic;

import src.logic.card.Card;

/**
 * Represents a suggestion. Used to transfer a suggestion from the client to the ClueSolver
**/
public class Suggestion {
	//Variables
	private Card room;
	private Card suspect;
	private Card weapon;

	//Methods

	/**
	 * Creates a new Suggestion
	 * @param room Card of where the murder was suggested to have occured
	 * @param suspect Card of who was suggested to have performed the murder
	 * @param weapon Card of what was suggested to have been used to perform the murder
	**/
	public Suggestion(Card room, Card suspect, Card weapon) {
		this.room = room;
		this.suspect = suspect;
		this.weapon = weapon;
	}

	/**
	 * Gives the room specified in the suggetsion
	 * @return room Room specified in the suggestion
	**/
	public Card getRoom() {
		return room;
	}

	/**
	 * Gives the suspect specified in the suggetsion
	 * @return suspect Suspect specified in the suggestion
	**/
	public Card getSuspect() {
		return suspect;
	}

	/**
	 * Gives the weapon specified in the suggetsion
	 * @return weapon Weapon specified in the suggestion
	**/
	public Card getWeapon() {
		return weapon;
	}

	@Override
	public String toString() {
		return String.format("%s and %s and %s", room.getDescription(), suspect.getDescription(), weapon.getDescription());
	}
}
