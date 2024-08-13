package uk.ac.soton.comp1206.component;

import java.util.Set;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.BlockHoveredListener;
import uk.ac.soton.comp1206.event.RightClicked;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard. It extends a GridPane to
 * hold a grid of GameBlocks.
 * <p>
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming
 * block. It also be linked to an external grid, for the main game board.
 * <p>
 * The GameBoard is only a visual representation and should not contain game logic or model logic in
 * it, which should take place in the Grid.
 */
public class GameBoard extends GridPane {

  /**
   * Logger
   */

  protected static final Logger logger = LogManager.getLogger(GameBoard.class);

  /**
   * Number of columns in the board
   */
  protected final int cols;

  /**
   * Number of rows in the board
   */
  protected final int rows;

  /**
   * The visual width of the board - has to be specified due to being a Canvas
   */
  protected final double width;

  /**
   * The visual height of the board - has to be specified due to being a Canvas
   */
  protected final double height;

  /**
   * The grid this GameBoard represents
   */
  final Grid grid;

  /**
   * The blocks inside the grid
   */
  GameBlock[][] blocks;

  /**
   * The listener to call when a specific block is clicked
   */
  private BlockClickedListener blockClickedListener;

  /**
   * Listener to call when a block is right clicked
   */
  private RightClicked rightClickedlistener;

  /**
   * Listener to call when a block is hovered.
   */
  private BlockHoveredListener blockHoveredListener;

  /**
   * GameBlock currently being hovered.
   */
  private GameBlock hoveredBlock;


  /**
   * Create a new GameBoard, based off a given grid, with a visual width and height.
   *
   * @param grid   linked grid
   * @param width  the visual width
   * @param height the visual height
   */
  public GameBoard(Grid grid, double width, double height) {
    this.cols = grid.getCols();
    this.rows = grid.getRows();
    this.width = width;
    this.height = height;
    this.grid = grid;

    //Build the GameBoard
    build();
  }

  /**
   * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows,
   * along with the visual width and height.
   *
   * @param cols   number of columns for internal grid
   * @param rows   number of rows for internal grid
   * @param width  the visual width
   * @param height the visual height
   */
  public GameBoard(int cols, int rows, double width, double height) {
    this.cols = cols;
    this.rows = rows;
    this.width = width;
    this.height = height;
    this.grid = new Grid(cols, rows);

    //Build the GameBoard
    build();
  }

  /**
   * Get a specific block from the GameBoard, specified by it's row and column
   *
   * @param x column
   * @param y row
   * @return game block at the given column and row
   */
  public GameBlock getBlock(int x, int y) {
    return blocks[x][y];
  }

  /**
   * Build the GameBoard by creating a block at every x and y column and row
   */
  protected void build() {
    logger.info("Building grid: {} x {}", cols, rows);

    setMaxWidth(width);
    setMaxHeight(height);

    setGridLinesVisible(true);

    blocks = new GameBlock[cols][rows];

    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        createBlock(x, y);
      }
    }
  }

  /**
   * Create a block at the given x and y position in the GameBoard
   *
   * @param x column
   * @param y row
   * @return GameBlock created.
   */
  protected GameBlock createBlock(int x, int y) {
    var blockWidth = width / cols;
    var blockHeight = height / rows;

    //Create a new GameBlock UI component
    GameBlock block = new GameBlock(x, y, blockWidth, blockHeight);

    //Add to the GridPane
    add(block, x, y);

    //Add to our block directory
    blocks[x][y] = block;

    //Link the GameBlock component to the corresponding value in the Grid
    block.bind(grid.getGridProperty(x, y));

    //Add a mouse click handler to the block to trigger GameBoard blockClicked method
    block.setOnMouseClicked((e) -> blockClicked(e, block));

    block.setOnMouseEntered(event -> {
      //logger.info("Hovered");
      blockHovered(true, block);
      block.setCursor(Cursor.HAND);
    });
    block.setOnMouseExited(event -> {
      blockHovered(false, block);
      block.setCursor(Cursor.DEFAULT);
    });

    return block;
  }

  /**
   * Remove highlights from other blocks, and highlight/remove highlight from the given GameBlock.
   *
   * @param IsHovered if the block has been hovered
   * @param block     GameBlock to affect
   */
  public void blockHovered(boolean IsHovered, GameBlock block) {
    removeAllHighlights();
    if (IsHovered) {
      block.paintHighlight();
    } else {
      block.removePaintHighlight();
    }
  }

  /**
   * Set the currently hovered block to this GameBlock
   *
   * @param hoveredBlock GameBlock to set as currently hovered
   */
  public void setHoveredBlock(GameBlock hoveredBlock) {
    this.hoveredBlock = hoveredBlock;
  }

  /**
   * Get the GameBlock currently being hovered.
   *
   * @return GameBlock currently being hovered.
   */
  public GameBlock getHoveredBlock() {
    return hoveredBlock;
  }

  /**
   * Remove highlights from all GameBlocks in this board.
   */
  public void removeAllHighlights() {
    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        getBlock(x, y).removePaintHighlight();
      }
    }
  }

  /**
   * Call the fade transition on all GameBlocks from the given GameBlockCoordinate set.
   *
   * @param gameBlockCoordinateSet set of coordinates cleared
   */
  public void fadeOut(Set<GameBlockCoordinate> gameBlockCoordinateSet) {
    for (GameBlockCoordinate blockCoordinate : gameBlockCoordinateSet) {
      GameBlock block = getBlock(blockCoordinate.getX(), blockCoordinate.getY());
      block.fadeOut();
    }

  }

  /**
   * Set the listener to handle an event when a block is clicked
   *
   * @param listener listener to add
   */
  public void setOnBlockClick(BlockClickedListener listener) {
    this.blockClickedListener = listener;
  }

  /**
   * Set the listener to handle an event when a block is right clicked
   *
   * @param listener listener to add
   */
  public void setOnRightClicked(RightClicked listener) {
    this.rightClickedlistener = listener;
  }

  /**
   * Set the listener to handle an event when a block is hovered
   *
   * @param listener listener to add
   */
  public void setBlockHoveredListener(BlockHoveredListener listener) {
    this.blockHoveredListener = listener;
  }

  /**
   * Triggered when a block is clicked. Call the attached listener.
   *
   * @param event mouse event
   * @param block block clicked on
   */
  protected void blockClicked(MouseEvent event, GameBlock block) {
    logger.info("Block clicked: {" + block + "}" + " with " + event.getButton());

    //Right click
    if (event.getButton() == MouseButton.SECONDARY) {
      if (rightClickedlistener != null) {
        logger.info("Right mouse clicked");
        rightClickedlistener.rightClicked(event);
      }
    } else if (event.getButton() == MouseButton.PRIMARY) {
      if (blockClickedListener != null) {
        blockClickedListener.blockClicked(block);
      }
    }
  }

}
