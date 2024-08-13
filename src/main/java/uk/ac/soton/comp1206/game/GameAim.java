package uk.ac.soton.comp1206.game;

import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controllable Game Aim controlled by the keyboard, so the user can place GamePieces on to the
 * Grid using keyboard controls.
 */
public class GameAim {
  private static final Logger logger = LogManager.getLogger(GameAim.class);
  int gridX;
  int gridY;
  /**
   * Current x coordinate of the game aim.
   */
  public SimpleIntegerProperty x = new SimpleIntegerProperty(0);
  /**
   * Current y coordinate of the game aim.
   */
  public SimpleIntegerProperty y = new SimpleIntegerProperty(0);

  /**
   * Construct a new Game Aim for a specified game Grid.
   * @param grid Grid to confine the GameAim to.
   */
  public GameAim(Grid grid) {
    gridX = grid.getRows();
    gridY = grid.getCols();

    int midX = (int) Math.floor(gridX / 2);
    int midY = (int) Math.floor(gridY / 2);
    x.set(midX);
    y.set(midY);

    logAimPosition();
  }

  /**
   * Move the aim left (x - 1)
   */
  public void moveLeft() {
    if (x.get() <= 0) {
      return;
    }
    x.set(getX() - 1);

    logAimPosition();
  }

  /**
   * Move the aim right (x + 1)
   */
  public void moveRight() {
    if (x.get() >= gridX - 1) {
      return;
    }
    x.set(getX() + 1);

    logAimPosition();
  }

  /**
   * Move the aim up (y - 1)
   */
  public void moveUp() {
    if (y.get() <= 0) {
      return;
    }
    y.set(getY() - 1);

    logAimPosition();
  }

  /**
   * Move the aim down (y + 1)
   */
  public void moveDown() {
    if (y.get() >= gridY - 1) {
      return;
    }
    y.set(getY() + 1);

    logAimPosition();
  }

  /**
   * Set the aim position to given coordinates
   * @param posX x position
   * @param posY y position
   */
  public void setAimPosition(int posX, int posY) {
    if (x.get() <= 0 || x.get() > gridX) {
      return;
    }
    if (y.get() <= 0 || y.get() > gridY) {
      return;
    }

    logAimPosition();
  }

  /**
   * Send a message to the Logger about the current aim's position
   */
  void logAimPosition() {
    logger.info("Aim: ({},{})", x.get(), y.get());
  }

  /**
   * Get the aim's x position
   * @return int representing x position
   */
  public int getX() {
    return x.get();
  }

  /**
   * Get the aim's y position
   * @return int representing y position
   */
  public int getY() {
    return y.get();
  }
}
