package uk.ac.soton.comp1206.event;

/**
 * Game Loop Listener is used to handle an event when a Game Loop is completed, upon a timer
 * completion.
 */
public interface GameLoopListener {

  /**
   * Handle a game loop event.
   * @param lives int representing the number of lives left.
   */
  public void gameLoop(int lives);

}
