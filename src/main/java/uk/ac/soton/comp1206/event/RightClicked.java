package uk.ac.soton.comp1206.event;

import javafx.scene.input.MouseEvent;

/**
 * Right Clicked Listener handles an event when the right mouse is clicked on the GameBoard.
 */
public interface RightClicked {

  /**
   * Handle a right clicked event.
   * @param event MouseEvent of the click.
   */
  public void rightClicked(MouseEvent event);

}
