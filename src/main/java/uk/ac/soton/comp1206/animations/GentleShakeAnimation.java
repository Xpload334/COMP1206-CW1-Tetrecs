package uk.ac.soton.comp1206.animations;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Animation for UI components, especially ImageButtons, to rotate and expand gently.
 */
public class GentleShakeAnimation {

  private static final double scaleTransitionDuration = 2000;
  private static final double rotateTransitionDuration = 5000;

  /**
   * Start the gentle shake animation for the given node. Loops indefinitely.
   *
   * @param node node to animate.
   */
  public static void startAnimation(Node node) {
    ParallelTransition pt = getAnimation();
    pt.setNode(node);
    pt.play();
  }

  /**
   * Helper method to get the animation used.
   *
   * @return ParallelTransition for a gentle shake effect.
   */
  private static ParallelTransition getAnimation() {
    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(scaleTransitionDuration));
    scaleTransition.setByX(-0.08);
    scaleTransition.setByY(-0.08);
    scaleTransition.setCycleCount(Animation.INDEFINITE); //Loop forever!
    scaleTransition.setAutoReverse(true);

    RotateTransition rotateTransition = new RotateTransition(
        Duration.millis(rotateTransitionDuration));
    rotateTransition.setFromAngle(-1);
    rotateTransition.setToAngle(1);
    rotateTransition.setCycleCount(Animation.INDEFINITE); //Loop forever as well
    rotateTransition.setAutoReverse(true);

    return new ParallelTransition(scaleTransition, rotateTransition);
  }

}
