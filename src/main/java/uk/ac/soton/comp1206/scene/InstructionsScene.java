package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Game instructions scene. Holds the UI for showing game controls and available pieces.
 */
public class InstructionsScene extends BaseScene {
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  /**
   * Construct a new Instructions Scene
   * @param gameWindow the Game Window this will be displayed in
   */
  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Instructions Scene");
  }

  /**
   * Build the layout of the scene
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    var instructionsPane = new StackPane();
    instructionsPane.setMaxWidth(gameWindow.getWidth());
    instructionsPane.setMaxHeight(gameWindow.getHeight());
    instructionsPane.getStyleClass().add("instructions-background");
    root.getChildren().add(instructionsPane);

    var mainPane = new BorderPane();
    instructionsPane.getChildren().add(mainPane);

    var instructions = new Image(
        Objects.requireNonNull(this.getClass().getResource("/images/Instructions.png")).toExternalForm());
    var instructionsView = new ImageView();
    instructionsView.setImage(instructions);
    instructionsView.setPreserveRatio(true);
    instructionsView.setFitWidth(gameWindow.getWidth());

    mainPane.setCenter(instructionsView);

    var pieceDisplay = buildPieceDisplay();
    pieceDisplay.setAlignment(Pos.TOP_CENTER);
    mainPane.setBottom(pieceDisplay);
  }

  /**
   * Initialise this scene. Called after creation
   */
  @Override
  public void initialise() {
    logger.info("Initialising Instructions");

    scene.setOnKeyPressed((e) -> {
      if(e.getCode() == KeyCode.ESCAPE) {
        gameWindow.startMenu();
      }
    });
  }

  /**
   * Return a VBox containing a title and a GridPane of all available GamePieces.
   * @return piece display VBox
   */
  public VBox buildPieceDisplay() {
    var pieceDisplayBox = new VBox();
    pieceDisplayBox.setPrefWidth(gameWindow.getWidth());

    var pieceDisplay = new GridPane();

    pieceDisplay.setHgap(10);
    for (int i = 0; i < 15; i++) {
      var piece = new PieceBoard(40, 40);
      piece.displayPiece(GamePiece.createPiece(i));

      pieceDisplay.addRow(0, piece);

    }
    pieceDisplayBox.getChildren().add(pieceDisplay);
    pieceDisplay.setAlignment(Pos.TOP_CENTER);

    var pieceDisplayTitle = new Label("Meet the Pieces!");
    pieceDisplayTitle.getStyleClass().add("instructions");
    pieceDisplayBox.getChildren().add(pieceDisplayTitle);
    pieceDisplayTitle.setAlignment(Pos.TOP_CENTER);

    return pieceDisplayBox;
  }
}
