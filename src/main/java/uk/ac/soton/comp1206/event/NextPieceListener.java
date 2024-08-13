package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Next Piece Listener handles an event when the next GamePiece is generated for the Game. Passes
 * the next GamePiece and the following GamePiece in the message.
 */
public interface NextPieceListener {

  /**
   * Handle an event when the next piece is generated
   *
   * @param nextPiece      next GamePiece to be placed
   * @param followingPiece GamePiece next in queue after nextPiece
   */
  public void nextPieceGenerated(GamePiece nextPiece, GamePiece followingPiece);

}
