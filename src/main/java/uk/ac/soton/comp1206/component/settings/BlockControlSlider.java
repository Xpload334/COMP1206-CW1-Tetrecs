package uk.ac.soton.comp1206.component.settings;

import java.util.ArrayList;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.GameColours;
import uk.ac.soton.comp1206.component.imageButtons.CandyButtonImageType;
import uk.ac.soton.comp1206.component.imageButtons.ImageButton;
import uk.ac.soton.comp1206.event.SliderValueChangeListener;

/**
 * UI component to represent a slider that increment/decrement a number of times between two values.
 * Controlled by two ImageButtons.
 */
public class BlockControlSlider extends HBox {

  private static final Logger logger = LogManager.getLogger(BlockControlSlider.class);
  boolean IsActive = true;

  private ImageButton minusButton;
  private ImageButton plusButton;
  double buttonSize = 50;
  double rectangleWidth = 50;
  double rectangleHeight = 30;

  ArrayList<Rectangle> controlRectangles = new ArrayList<>();

  double minValue = 0;
  SimpleDoubleProperty currentValue;
  double maxValue = 0.2;
  int currentInt = 3;
  int increments = 5;
  SliderValueChangeListener sliderValueChangeListener;

  /**
   * Construct a new Block Control Slider with an initial, minimum and maximum value, as well as the
   * number of increments between the min and max.
   *
   * @param initialValue initial increment of the block control slider.
   * @param minValue     minimum value of the slider.
   * @param maxValue     maximum value of the slider.
   * @param increments   number of increments between min and max.
   */
  public BlockControlSlider(int initialValue, double minValue, double maxValue, int increments) {
    currentInt = initialValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.increments = increments;

    setAlignment(Pos.CENTER);
    setSpacing(5);

    build();
    initialise();
  }

  /**
   * Build the block control slider.
   */
  private void build() {
    //Minus on Left
    minusButton = new ImageButton(CandyButtonImageType.MINUS, buttonSize, buttonSize);
    getChildren().add(minusButton);
    minusButton.setButtonClickedListener(this::decreaseOne);
    GentleShakeAnimation.startAnimation(minusButton);

    //Array of rectangles
    for (int i = 0; i < increments; i++) {
      Rectangle rectangle = new Rectangle(rectangleWidth, rectangleHeight);
      rectangle.setFill(GameColours.HighlightColour);
      controlRectangles.add(rectangle);
      getChildren().add(rectangle);
    }

    //Plus on Right
    plusButton = new ImageButton(CandyButtonImageType.PLUS, buttonSize, buttonSize);
    getChildren().add(plusButton);
    plusButton.setButtonClickedListener(this::increaseOne);
    GentleShakeAnimation.startAnimation(plusButton);
  }

  /**
   * Initialise the block control slider.
   */
  public void initialise() {
    currentValue = new SimpleDoubleProperty();
    setOnSliderValueChanged(value -> logger.info("Initialising"));
    setCurrentValue(currentInt);
  }

  /**
   * Increase the current step by 1, if possible.
   */
  private void increaseOne() {
    if (!IsActive) {
      return;
    }

    logger.info("Increased by one");
    currentInt++;
    if (currentInt > increments) {
      currentInt = increments;
    }
    setCurrentValue(currentInt);
  }

  /**
   * Decrease the current step by 1, if possible.
   */
  private void decreaseOne() {
    if (!IsActive) {
      return;
    }

    logger.info("Decreased by one");
    currentInt--;
    if (currentInt < 0) {
      currentInt = 0;
    }
    setCurrentValue(currentInt);
  }

  /**
   * Set the current value of the slider from the current increment.
   *
   * @param currentInt current increment of the slider.
   */
  public void setCurrentValue(int currentInt) {
    double range = maxValue - minValue;
    double incrementValue = range / increments;
    double newValue = currentInt * incrementValue;
    this.currentValue.set(newValue);

    logger.info(currentInt + "/" + increments + ", value = " + newValue);

    updateRectangles(currentInt);
    sliderValueChangeListener.valueChanged(newValue);
  }

  /**
   * Set the current value of the slider to a new value.
   *
   * @param newValue new value of the slider.
   */
  public void setCurrentValue(double newValue) {
    double range = maxValue - minValue;
    double incrementValue = range / increments;
    currentInt = Math.toIntExact(Math.round(newValue / incrementValue));

    double newValueActual = currentInt * incrementValue;

    logger.info(currentInt + "/" + increments + ", value = " + newValueActual);

    updateRectangles(currentInt);
    sliderValueChangeListener.valueChanged(newValueActual);
  }

  /**
   * Update the rectangles of the slider based on the current increment.
   *
   * @param currentInt current increment of the slider.
   */
  private void updateRectangles(int currentInt) {
    if (!IsActive) {
      return;
    }

    //Fill up to current int
    for (int i = 0; i < currentInt; i++) {
      controlRectangles.get(i).setFill(Color.GREEN);
    }
    //Fill the rest with empty
    for (int i = currentInt; i < increments; i++) {
      controlRectangles.get(i).setFill(GameColours.HighlightColour);
    }
  }

  /**
   * Toggle whether the slider is active or not.
   */
  public void toggleActive() {
    setActive(!IsActive);
  }

  /**
   * Set if the slider is active or not.
   *
   * @param isActive boolean representing if the slider is active or not.
   */
  public void setActive(boolean isActive) {
    this.IsActive = isActive;

    if (isActive) {
      updateRectangles(currentInt);
    } else {
      fillAllRectangles(GameColours.EmptyColour);
    }
  }

  /**
   * Fill all the rectangles of the slider with a given Color.
   *
   * @param color Color to fill rectangles with.
   */
  private void fillAllRectangles(Color color) {
    for (Rectangle rectangle : controlRectangles) {
      rectangle.setFill(color);
    }
  }

  /**
   * Return the current value of the slider.
   *
   * @return current value of the slider.
   */
  public double getCurrentValue() {
    return currentValue.get();
  }

  /**
   * Return the value property of the slider.
   *
   * @return value property of the slider.
   */
  public SimpleDoubleProperty getDoubleProperty() {
    return currentValue;
  }

  /**
   * Handle an event when the value of the slider changes.
   *
   * @param listener listener.
   */
  public void setOnSliderValueChanged(SliderValueChangeListener listener) {
    sliderValueChangeListener = listener;
  }

  /**
   * Return the plus ImageButton.
   *
   * @return plus button.
   */
  public ImageButton getPlusButton() {
    return plusButton;
  }

  /**
   * Return the minus ImageButton.
   *
   * @return minus button.
   */
  public ImageButton getMinusButton() {
    return minusButton;
  }
}
