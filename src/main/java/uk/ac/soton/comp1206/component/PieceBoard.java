package uk.ac.soton.comp1206.component;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Class to represent a PieceBoard, which displays a given piece in a grid.
 */
public class PieceBoard extends GameBoard {

  /**
   * Game Piece to display.
   */
  public static GamePiece currentPiece;

  /**
   * Create a new 3x3 PieceBoard.
   *
   * @param width  the visual width
   * @param height the visual height
   */
  public PieceBoard(double width, double height) {
    super(3, 3, width, height);
  }


  /**
   * Display the specified GamePiece inside this PieceBoard.
   *
   * @param gamePiece GamePiece to display.
   */
  public void displayPiece(GamePiece gamePiece) {
    //Clear grid
    grid.clearGrid();

    //Set display to the piece
    currentPiece = gamePiece;
    grid.playPiece(currentPiece, 0, 0);
  }

}
