package uk.ac.soton.comp1206.animations;

import javafx.animation.AnimationTimer;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * AnimationTimer to fade GameBlocks when they are cleared.
 */
public class GameBlockFadeAnimation extends AnimationTimer {

  private final GameBlock gameBlock;
  private double opacity = 1;

  /**
   * Construct a new fade animation for a given GameBlock
   *
   * @param gameBlock game block to fade out
   */
  public GameBlockFadeAnimation(GameBlock gameBlock) {
    this.gameBlock = gameBlock;
  }

  @Override
  public void handle(long l) {
    doHandle();
  }

  /**
   * Gradually decrease the opacity of the GameBlock
   */
  private void doHandle() {
    opacity -= 0.01;
    if (opacity <= 0) {
      stop();
    } else {
      gameBlock.paintFade(opacity);
    }
  }
}
