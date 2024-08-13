package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import uk.ac.soton.comp1206.animations.GameBlockFadeAnimation;

/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

  /**
   * The set of colours for different pieces
   * <p>
   * 05/04/2022 - Made separate class GameColours to store the base colours
   */
  public static final Color[] COLOURS = GameColours.COLOURS;
  /**
   * Colour for an empty GameBlock
   */
  public static final Color EmptyColour = Color.color(0.2, 0.2, 0.2, 0.5);
  /**
   * Colour for a highlighted GameBlock
   */
  public static final Color HighlightColour = Color.color(1, 1, 1, 0.5);
  //////////
  /**
   * Boolean to show if this GameBlock is highlighted or not.
   */
  public boolean IsHighlighted = false;
  private final double width;
  private final double height;
  private final double arcWidth;
  private static final double GAMEBLOCK_ARCWIDTH_RATIO = 3;
  private final double arcHeight;
  private static final double GAMEBLOCK_ARCHEIGHT_RATIO = 3;

  ////////////
  private final Image blockImage = new Image(
      this.getClass().getResource("/images/custom/star.png").toExternalForm());

  /**
   * The column this block exists as in the grid
   */
  private final int x;

  /**
   * The row this block exists as in the grid
   */
  private final int y;

  /**
   * The value of this block (0 = empty, otherwise specifies the colour to render as)
   */
  private final IntegerProperty value = new SimpleIntegerProperty(0);

  /**
   * Create a new single Game Block
   *
   * @param x      the column the block exists in
   * @param y      the row the block exists in
   * @param width  the width of the canvas to render
   * @param height the height of the canvas to render
   */
  public GameBlock(int x, int y, double width, double height) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;

    //A canvas needs a fixed width and height
    setWidth(width);
    setHeight(height);

    arcWidth = width / GAMEBLOCK_ARCWIDTH_RATIO;
    arcHeight = height / GAMEBLOCK_ARCHEIGHT_RATIO;

    //Do an initial paint
    paint();

    //When the value property is updated, call the internal updateValue method
    value.addListener(this::updateValue);
  }

  /**
   * When the value of this block is updated,
   *
   * @param observable what was updated
   * @param oldValue   the old value
   * @param newValue   the new value
   */
  private void updateValue(ObservableValue<? extends Number> observable, Number oldValue,
      Number newValue) {
    paint();
  }

  /**
   * Handle painting of the block canvas
   */
  public void paint() {
    //If the block is empty, paint as empty
    if (value.get() == 0) {
      paintEmpty();
    } else {
      //If the block is not empty, paint with the colour represented by the value
      paintColor(COLOURS[value.get()]);
    }
  }


  /**
   * If the GameBlock is not highlighted, paint the highlight colour.
   */
  public void paintHighlight() {
    if (!IsHighlighted) {
      paintColor(HighlightColour);
      IsHighlighted = true;
    }
  }

  /**
   * If the GameBlock is highlighted, remove the highlight colour
   */
  public void removePaintHighlight() {
    if (IsHighlighted) {
      paint();
      IsHighlighted = false;
    }
  }

  /**
   * Create a GameBlockFadeAnimation to play on this GameBlock.
   */
  public void fadeOut() {
    AnimationTimer fadeAnimation = new GameBlockFadeAnimation(this);
    fadeAnimation.start();
  }

  /**
   * Paint a colour for this GameBlock based on the given opacity. Used with fadeOut()
   *
   * @param opacity double representing the opacity of this highlight.
   */
  public void paintFade(double opacity) {
    var gc = getGraphicsContext2D();
    //Paint normally first
    //Instead of paintEmpty(), due to being able to place blocks during the animation
    paint();
    //Then paint the fade
    gc.setFill(new Color(1, 1, 1, opacity));
    gc.fillRect(0, 0, width, height);
  }

  /**
   * Paint this canvas empty
   */
  private void paintEmpty() {
    var gc = getGraphicsContext2D();

    //Clear
    gc.clearRect(0, 0, width, height);

    //Fill
    gc.setFill(EmptyColour);
    gc.fillRect(0, 0, width, height);

    //Border
    //gc.setStroke(Color.BLACK);
    gc.setStroke(Color.WHITE);
    gc.strokeRect(0, 0, width, height);
  }

  /**
   * Paint this canvas with the given colour
   *
   * @param colour the colour to paint
   */
  private void paintColor(Paint colour) {
    var gc = getGraphicsContext2D();

    //Clear
    gc.clearRect(0, 0, width, height);

    //Colour fill
    //Changed to fill background with transparent first
    gc.setFill(EmptyColour);
    gc.fillRect(0, 0, width, height);

    //Insert a custom block design here
    gc.setFill(colour);
    gc.fillRoundRect(0, 0, width, height, arcWidth, arcHeight);

    //Paint a cool image on to the block
    gc.drawImage(blockImage, 0, 0, width, height);

    //Border
    //gc.setStroke(Color.BLACK);
    gc.setStroke(Color.WHITE);
    gc.strokeRoundRect(0, 0, width, height, arcWidth, arcHeight);
  }

  /**
   * Get the column of this block
   *
   * @return column number
   */
  public int getX() {
    return x;
  }

  /**
   * Get the row of this block
   *
   * @return row number
   */
  public int getY() {
    return y;
  }

  /**
   * Bind the value of this block to another property. Used to link the visual block to a
   * corresponding block in the Grid.
   *
   * @param input property to bind the value to
   */
  public void bind(ObservableValue<? extends Number> input) {
    value.bind(input);
  }
}
