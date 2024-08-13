package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.Game;

/**
 * Game Parting Listener used to handle an event when the user has parted from a multiplayer game.
 */
public interface GamePartingListener {

  /**
   * Handle a game parted event.
   * @param game game that was parted.
   */
  public void gameParted(Game game);

}
