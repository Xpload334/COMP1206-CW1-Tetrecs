package uk.ac.soton.comp1206.event;

import java.util.Set;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * Line Cleared Listener handles an event when a line of blocks are cleared in the Game. It passes a
 * Set of GameBlockCoordinates of all affected GameBlocks by the line clear in the message.
 */
public interface LineClearedListener {

  /**
   * Handle a line cleared event.
   * @param gameBlockCoordinateSet set of GameBlockCoordinates affected by the line clear.
   */
  public void lineCleared(Set<GameBlockCoordinate> gameBlockCoordinateSet);

}
