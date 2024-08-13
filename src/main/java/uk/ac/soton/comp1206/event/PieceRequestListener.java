package uk.ac.soton.comp1206.event;

/**
 * Piece Request Listener is used to handle an event when a GamePiece is requested from the server by a Multiplayer Game.
 */
public interface PieceRequestListener {

  /**
   * Handle a Piece Request event.
   */
  public void requestPiece();

}
