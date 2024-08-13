package uk.ac.soton.comp1206.event;

import javafx.scene.input.KeyEvent;

/**
 * Key Pressed Listener handles an event when a key is pressed.
 */
public interface KeyPressedListener {

  /**
   * Handle a key pressed event.
   * @param event KeyEvent of this event.
   */
  public void keyPressed(KeyEvent event);

}
