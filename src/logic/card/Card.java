package src.logic.card;

public class Card {
	private TypeOfCard cardType;
	private CardValue cardValue;
	private String description;
	private boolean known;
	public Card(CardValue cardValue, TypeOfCard cardType, String description) {
		this.cardType = cardType;
		this.cardValue = cardValue;
		this.description = description;
		known = false;
	}
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
			return otherCard.cardType == cardType && otherCard.cardValue == cardValue && description == otherCard.description;
		}
		return false;
	}
	public void setKnown(boolean newValue) {
		known = newValue;
	}
	public boolean isKnown() {
		return known;
	}
}
