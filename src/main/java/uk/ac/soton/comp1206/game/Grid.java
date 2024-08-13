package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MultiplayerScene;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer
 * values arranged in a 2D arrow, with rows and columns.
 * <p>
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display
 * of the contents of the grid.
 * <p>
 * The Grid contains functions related to modifying the model, for example, placing a piece inside
 * the grid.
 * <p>
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {
  private static final Logger logger = LogManager.getLogger(Grid.class);

  /**
   * The number of columns in this grid
   */
  private final int cols;

  /**
   * The number of rows in this grid
   */
  private final int rows;

  /**
   * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
   */
  private final SimpleIntegerProperty[][] grid;

  /**
   * Create a new Grid with the specified number of columns and rows and initialise them
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Grid(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create the grid itself
    grid = new SimpleIntegerProperty[cols][rows];

    //Add a SimpleIntegerProperty to every block in the grid
    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        grid[x][y] = new SimpleIntegerProperty(0);
      }
    }
  }

  /**
   * Get the Integer property contained inside the grid at a given row and column index. Can be used
   * for binding.
   *
   * @param x column
   * @param y row
   * @return the IntegerProperty at the given x and y in this grid
   */
  public IntegerProperty getGridProperty(int x, int y) {
    return grid[x][y];
  }

  /**
   * Update the value at the given x and y index within the grid
   *
   * @param x     column
   * @param y     row
   * @param value the new value
   */
  public void set(int x, int y, int value) {
    grid[x][y].set(value);
  }

  /**
   * Get the value represented at the given x and y index within the grid
   *
   * @param x column
   * @param y row
   * @return the value
   */
  public int get(int x, int y) {
    try {
      //Get the value held in the property at the x and y index provided
      return grid[x][y].get();
    } catch (ArrayIndexOutOfBoundsException e) {
      //No such index
      return -1;
    }
  }

  /**
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  //End of skeleton

  /**
   * Given a specified piece and (x,y) of the grid, return if the piece can be played in this
   * space.
   *
   * @param gamePiece GamePiece to play
   * @param x         x-coordinate
   * @param y         y-coordinate
   * @return if the given GamePiece can be played at this coordinate
   */
  public boolean canPlayPiece(GamePiece gamePiece, int x, int y) {
    int[][] blocks = gamePiece.getBlocks();
    //Blocks define a 3x3 array of blocks
    //For each block, if it occupies an invalid space, return false
    //Else, return true
    for (int i = 0; i < blocks.length; i++) {
      for (int j = 0; j < blocks.length; j++) {
        //If has a block here
        try {
          if (blocks[i][j] > 0) {
            int blockX = x + i;
            int blockY = y + j;

            //Check if passes borders of grid
            if (blockX < 0 || blockX >= getCols()) {
              return false;
            }
            if (blockY < 0 || blockY >= getRows()) {
              return false;
            }

            if (get(blockX, blockY) > 0) {
              return false;
            }
          }
        } catch (ArrayIndexOutOfBoundsException e) {
          return false;
        }
      }
    }
    //If there are no problems, piece can be played
    return true;
  }

  /**
   * Play a specified game piece on to the board, with the centre being at the given (x,y)
   *
   * @param gamePiece game piece to play
   * @param x         x-coordinate
   * @param y         y-coordinate
   */
  public void playPiece(GamePiece gamePiece, int x, int y) {
    if(gamePiece == null) {
      logger.info("Piece is null");
      return;
    }

    int[][] blocks = gamePiece.getBlocks();
    //Blocks define a 3x3 array of blocks
    for (int i = 0; i < blocks.length; i++) {
      for (int j = 0; j < blocks.length; j++) {
        //Block value will be 1 if a block is present
        if (blocks[i][j] > 0) {
          int blockX = x + i;
          int blockY = y + j;

          set(blockX, blockY, gamePiece.getValue());
        }
      }
    }
  }

  /**
   * Return if a horizontal row is full.
   * Returns true if every space in this row has a non-zero value.
   *
   * @param row y-coordinate of this row
   * @return if this row is full
   */
  public boolean rowIsFull(int row) {
    //For each block on this row
    for (int j = 0; j < getCols(); j++) {
      //If value is 0, return false (not full)
      if (get(row, j) == 0) {
        return false;
      }
    }
    //Else, if all blocks full, return true
    return true;
  }

  /**
   * Return if a vertical column is full.
   * Returns true if every space in this column has a non-zero value.
   *
   * @param col x-coordinate of this column
   * @return if this column is full
   */
  public boolean colIsFull(int col) {
    //For each block on this column
    for (int i = 0; i < getRows(); i++) {
      //If value is 0, return false (not full)
      if (get(i, col) == 0) {
        return false;
      }
    }
    //Else, if all blocks full, return true
    return true;
  }

  /**
   * Clear this row.
   * Set every space on this row to 0.
   * @param row y-coordinate of this row
   */
  public void clearRow(int row) {
    //For each block on this row
    for (int j = 0; j < getCols(); j++) {
      set(row, j, 0); //Set value to 0
    }
  }

  /**
   * Clear this column.
   * Set every space on this column to 0.
   * @param col x-coordinate of this column
   */
  public void clearCol(int col) {
    //For each block on this column
    for (int i = 0; i < getRows(); i++) {
      set(i, col, 0); //Set value to 0
    }
  }

  /**
   * Set the value of the given (x,y) coordinate to 0.
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public void clear(int x, int y) {
    set(x, y, 0);
  }

  /**
   * Clear the entire grid.
   */
  public void clearGrid() {
    for (int row = 0; row < getRows(); row++) {
      clearRow(row);
    }
    for (int col = 0; col < getCols(); col++) {
      clearCol(col);
    }
  }


}
