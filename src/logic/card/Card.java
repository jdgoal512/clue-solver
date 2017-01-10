package src.logic.card;

/**
 * Represents a clue card. Has a value, type and a description
**/
public class Card {
	//Variables
	private TypeOfCard cardType; //The type of card (Room, weapon or suspect)
	private String description; //A string description of the card
	private boolean known; //Whether or not the card is known by a player

	//Methods

	/**
	 * Creates a new cards
	 * @param cardType The type of the card (Room, weapon, or suspect)
	 * @param description A string description of the card, this is what will show up to the user
	**/
	public Card(TypeOfCard cardType, String description) {
		this.cardType = cardType;
		this.description = description;
		known = false;
	}

	/**
	 * Creates a copy of a card
	 * @param otherCard The card to copy
	**/
	public Card(Card otherCard) {
		cardType = otherCard.cardType;
		description = otherCard.description;
		known = false;
	}

	/**
	 * Gives the type of the card (Room, weapon, or suspect)
	 * @return cardType The type of the card
	**/
	public TypeOfCard getType() {
		return cardType;
	}

	/**
	 * Gives the user description of the card
	 * @return description String containing a description of the card for the user
	**/
	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return description.hashCode();
	}

	public boolean equals(Object other) {
		if (other instanceof Card) {
			Card otherCard = (Card)other;
			return otherCard.cardType == cardType && description.equals(otherCard.description);
		}
		return false;
	}

	/**
	 * Marks a card as whether it is known to be possessed by a player or not
	 * @param newValue New boolean value of whether the card is possessed by a player or not
	**/
	public void setKnown(boolean newValue) {
		known = newValue;
	}

	/**
	 * Gives whether or not a card is known to be in a player's hand
	 * @return known Boolean value of whether or not the card is known to be in a player's hand
	**/
	public boolean isKnown() {
		return known;
	}
}
