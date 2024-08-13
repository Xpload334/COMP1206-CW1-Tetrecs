package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.Game;

/**
 * Game Finished Listener is used to handle an event when the Game has finished due to losing all
 * lives.
 */
public interface GameFinishedListener {

  /**
   * Handle a game finished event.
   * @param game game that has finished.
   */
  public void gameFinished(Game game);

}
