package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Piece Played Listener handles an event when a GamePiece is played in the Game. Passes through the
 * GamePiece in the message.
 */
public interface PiecePlayedListener {

  /**
   * Handle a piece played event.
   * @param gamePiece GamePiece that was played
   */
  public void piecePlayed(GamePiece gamePiece);

}
