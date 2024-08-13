package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.imageButtons.CandyButtonImageType;
import uk.ac.soton.comp1206.component.imageButtons.ImageButton;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;


/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  ImageView title;
  VBox menuButtons;
  double menuButtonSpacing = 10;
  double buttonSize = 80;

  /**
   * Create a new menu scene
   *
   * @param gameWindow the Game Window this will be displayed in
   */
  public MenuScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Menu Scene");
  }

  /**
   * Build the menu layout
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);

    var mainPane = new BorderPane();
    menuPane.getChildren().add(mainPane);

    //Picture title
    var titleImage = new Image(
        Objects.requireNonNull(this.getClass().getResource("/images/TetrECS.png"))
            .toExternalForm());
    title = new ImageView();
    title.setImage(titleImage);
    title.setPreserveRatio(true);
    title.setFitWidth(gameWindow.getWidth());

    mainPane.setTop(title);

    menuButtons = new VBox(menuButtonSpacing);
    menuButtons.setAlignment(Pos.BASELINE_CENTER);
    mainPane.setCenter(menuButtons);

    //Play image button
    var playImageButton = new ImageButton(CandyButtonImageType.PLAY, buttonSize, buttonSize);
    menuButtons.getChildren().add(playImageButton);
    playImageButton.setButtonClickedListener(this::startGame);

    //Multiplayer image button
    var multiplayerImageButton = new ImageButton(CandyButtonImageType.MULTIPLAYER, buttonSize,
        buttonSize);
    menuButtons.getChildren().add(multiplayerImageButton);
    multiplayerImageButton.setButtonClickedListener(this::showMultiplayerLobby);

    //Instructions image button
    var instructionsImageButton = new ImageButton(CandyButtonImageType.HELP, buttonSize, buttonSize);
    menuButtons.getChildren().add(instructionsImageButton);
    instructionsImageButton.setButtonClickedListener(this::showInstructions);

    //Settings image button
    var settingsImageButton = new ImageButton(CandyButtonImageType.OPTIONS, buttonSize, buttonSize);
    menuButtons.getChildren().add(settingsImageButton);
    settingsImageButton.setButtonClickedListener(this::showSettings);
  }

  /**
   * Initialise the menu
   */
  @Override
  public void initialise() {
    Multimedia.stopMusic();
    Multimedia.menuMusic();

    GentleShakeAnimation.startAnimation(title);
    animateEachButton();
  }

  /**
   * Animate each button in the menu buttons with the Gentle Shake Animation.
   */
  private void animateEachButton() {
    for (var button : menuButtons.getChildren()) {
      GentleShakeAnimation.startAnimation(button);
    }
  }

  /**
   * Handle when the Start Game button is pressed.
   */
  private void startGame() {
    gameWindow.startChallenge();
  }

  /**
   * Handle when the Instructions button is pressed.
   */
  private void showInstructions() {
    gameWindow.showInstructions();
  }

  /**
   * Handle when the multiplayer button is pressed.
   */
  private void showMultiplayerLobby() {
    gameWindow.showLobby();
  }

  /**
   * Handle when the settings button is pressed
   */
  private void showSettings() {
    gameWindow.showSettings();
  }

}
