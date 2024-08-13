package uk.ac.soton.comp1206.component;

import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to display an animated timer for the Game.
 */
public class TimerBar extends StackPane {

  private static final Logger logger = LogManager.getLogger(TimerBar.class);
  Rectangle timerBackground;
  Rectangle timerBar;
  Color TimerColourHigh = Color.GREEN;
  Color TimerColourMed = Color.ORANGE;
  Color TimerColourLow = Color.RED;
  float TIMER_MED_MULTIPLIER = 0.33f;
  float TIMER_LOW_MULTIPLIER = 0.67f;

  Timer colourChangeTimer;
  double maxWidth;
  private Timeline timeline;

  /**
   * Construct a new GameTimer from given width and height.
   *
   * @param width  width of timer to render
   * @param height height of timer to render
   */
  public TimerBar(double width, double height) {
    maxWidth = width;
    setWidth(width);
    maxWidth(width);
    setHeight(height);

    timerBackground = new Rectangle(width, height);
    timerBackground.setStroke(Color.WHITE);
    timerBackground.setFill(Color.TRANSPARENT);
    getChildren().add(timerBackground);

    timerBar = new Rectangle(width, height);
    timerBar.setStroke(Color.TRANSPARENT);
    timerBar.setFill(Color.GREEN);
    //setAlignment(timerBar, Pos.BASELINE_LEFT);
    getChildren().add(timerBar);
  }

  /**
   * Start the animation of the GameTimer for the specified duration.
   *
   * @param duration duration of the timer in milliseconds.
   */
  public void startTimer(long duration) {
    logger.info("Starting GameTimer for " + duration);
    //Make new timeline
    timeline = new Timeline();
    //Add end keyframe to set width to 0
    timeline.getKeyFrames().add(
        new KeyFrame(Duration.millis(duration),
            new KeyValue(timerBar.widthProperty(), 0, Interpolator.LINEAR))
    );
    timeline.play();
    startColourChangeTimers(duration);
  }

  /**
   * Start two other TimerTasks to change the colour of the timer bar.
   *
   * @param duration duration of the main timer in milliseconds.
   */
  private void startColourChangeTimers(long duration) {
    colourChangeTimer = new Timer();

    TimerTask medChangeTask = new TimerTask() {
      @Override
      public void run() {
        logger.info("Timer colour medium");
        timerBar.setFill(TimerColourMed);
      }
    };

    TimerTask lowChangeTask = new TimerTask() {
      @Override
      public void run() {
        logger.info("Timer colour low");
        timerBar.setFill(TimerColourLow);
      }
    };

    colourChangeTimer.schedule(medChangeTask, (long) (duration * TIMER_MED_MULTIPLIER));
    colourChangeTimer.schedule(lowChangeTask, (long) (duration * TIMER_LOW_MULTIPLIER));
  }

  /**
   * If a timer animation is currently running, stop it and reset the timer back to a full state.
   */
  public void resetTimer() {
    if (!isRunning()) {
      return;
    }
    logger.info("Stopping GameTimer");
    timeline.stop(); //Stop current timeline
    colourChangeTimer.cancel(); //Cancel colour change timer
    colourChangeTimer.purge(); //Purge colour change timer
    // Set to max width
    timerBar.widthProperty().set(maxWidth);
    timerBar.setFill(TimerColourHigh);
    //Maybe add an animation to slide the timer back to the start
  }

  /**
   * Reset, then start this timer for the given duration.
   *
   * @param duration duration of the timer in milliseconds.
   */
  public void restartTimer(long duration) {
    resetTimer();

    startTimer(duration);
  }

  /**
   * Return whether the timer is running or not.
   *
   * @return boolean.
   */
  public boolean isRunning() {
    return timeline.getStatus() == Status.RUNNING;
  }
}
