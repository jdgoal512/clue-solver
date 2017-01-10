package src.logic;

import src.logic.card.*;

public class Move {
	public enum MoveType { START_CARD, SUGGESTION, ADD_CARD, UNDO };
	private MoveType moveType;
	private Card card;
	private Suggestion suggestion;
	private Player player;
	int number;
	int resolvingPlayer;

	private Move(MoveType moveType, Card card, Suggestion suggestion, Player player, int number, int resolvingPlayer) {
		this.moveType = moveType;
		this.card = card;
		this.suggestion = suggestion;
		this.player = player;
		this.number = number;
		this.resolvingPlayer = resolvingPlayer;
	}

	public static Move startCard(int cardNumber) {
		return new Move(MoveType.START_CARD, null, null, null, cardNumber, 0);
	}

	public static Move suggestion(int suggestingPlayer, Suggestion suggestion, int resolvingPlayer) {
		return new Move(MoveType.SUGGESTION, null, suggestion, null, suggestingPlayer, resolvingPlayer);
	}

	public static Move addCard(Card card, Player player) {
		return new Move(MoveType.ADD_CARD, card, null, player, 0, 0);
	}

	public static Move undo(int moveNumber) {
		return new Move(MoveType.UNDO, null, null, null, moveNumber, 0);
	}

	public MoveType getType() {
		return moveType;
	}

	public Card getCard() {
		return card;
	}

	public Suggestion getSugestion() {
		return suggestion;
	}

	public Player getPlayer() {
		return player;
	}

	public int getNumber() {
		return number;
	}

	public int getResolvingPlayer() {
		return resolvingPlayer;
	}

	@Override
	public String toString() {
		switch (moveType) {
			case START_CARD:
				return String.format("Added start card [%s]", card.getDescription());
			case SUGGESTION:
				return String.format("Suggestion by [%s] of [%s] resolved by [%s]",
						ClueLogic.getClueLogic().getPlayerList().get(number), suggestion.toString(),
						ClueLogic.getClueLogic().getPlayerList().get(resolvingPlayer));
			case ADD_CARD:
				return String.format("[%s] has card [%s]", player.getName(), card.getDescription());
			case UNDO:
				return String.format("Undid move %d", number);
			default:
				return "Error: Invalid type of move";
		}
	}
}
