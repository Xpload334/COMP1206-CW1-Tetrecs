package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * Block Hovered Listener is used to handle the event when a GameBlock in the GameBoard is hovered by
 * the mouse, or is the active GameBlock of the GameAim.
 * Passes the GameBlock that was hovered in the message.
 */
public interface BlockHoveredListener {

  /**
   * Handle a block hovered event
   * @param block the block that has been hovered
   */
  public void blockHovered(GameBlock block);

}
