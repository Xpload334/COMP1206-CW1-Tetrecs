package uk.ac.soton.comp1206.component;

import javafx.scene.paint.Color;

/**
 * Class containing various colours used in the game, notably for GameBlocks and ScoreList text
 */
public class GameColours {
  /**
   * The set of colours for different pieces
   */
  public static final Color[] COLOURS = {
      Color.TRANSPARENT,
      Color.DEEPPINK,
      Color.RED,
      Color.ORANGE,
      Color.YELLOW,
      Color.YELLOWGREEN,
      Color.LIME,
      Color.GREEN,
      Color.DARKGREEN,
      Color.DARKTURQUOISE,
      Color.DEEPSKYBLUE,
      Color.AQUA,
      Color.AQUAMARINE,
      Color.BLUE,
      Color.MEDIUMPURPLE,
      Color.PURPLE
  };
  /**
   * Colour for an empty GameBlock
   */
  public static final Color EmptyColour = Color.color(0.2, 0.2, 0.2, 0.5);
  /**
   * Colour for a highlighted GameBlock
   */
  public static final Color HighlightColour = Color.color(1, 1, 1, 0.5);
}
