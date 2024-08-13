package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.TimerBar;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GameAim;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the
 * game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  /**
   * Game to run in this Challenge Scene.
   */
  protected Game game;
  /**
   * Boolean to show if this Scene is built or not.
   */
  public boolean IsBuilt = false;
  /**
   * Main Pane to hold the Challenge Scene UI
   */
  protected BorderPane mainPane;

  /**
   * Main Game Board
   */
  protected GameBoard board;

  /**
   * Radius of the circle centered on the Current Piece Board.
   */
  protected static final double CENTER_CIRCLE_RADIUS = 8;
  /**
   * Next Piece Board
   */
  protected PieceBoard nextPieceBoard;
  /**
   * Size of the Next Piece Board.
   */
  protected static final double NEXTPIECEBOARD_SIZE = 120;
  /**
   * Following Piece Board.
   */
  protected PieceBoard followingPieceBoard;
  /**
   * Size of the Following Piece Board.
   */
  protected static final double FOLLOWINGPIECEBOARD_SIZE = 80;
  /**
   * Keyboard-Controlled Game Aim
   */
  protected GameAim gameAim;
  /**
   * Game Timer.
   */
  protected TimerBar gameTimer;
  /**
   * Width of the Game Timer.
   */
  protected static final double GAMETIMER_WIDTH = 20;

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Challenge Scene");
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("challenge-background");
    root.getChildren().add(challengePane);

    mainPane = new BorderPane();
    challengePane.getChildren().add(mainPane);

    var centerBoardStack = new StackPane();
    centerBoardStack.setPrefSize(gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    mainPane.setCenter(centerBoardStack);

    ///////////////////////
    //Build the game board
    board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    //Handle block on gameboard grid being clicked
    board.setOnBlockClick(this::blockClicked);
    //Handle block on gameboard grid being right clicked
    board.setOnRightClicked((event) -> {
      //Default rotate on click is rotate right
      logger.info("Right click");
      game.rotateRight();
    });
    //Handle block being hovered
    board.setBlockHoveredListener(this::blockHovered);
    mainPane.setCenter(board);

    //Key Listeners
    gameAim = new GameAim(game.getGrid());
    assignControls(); //Assign rest of controls

    //Game Line Cleared
    game.setLineClearedListener(gameBlockCoordinateSet -> board.fadeOut(gameBlockCoordinateSet));
    //Game Finished
    game.setGameFinishedListener(thisGame -> {
      logger.info("Received GameFinishedListener, showing scores");
      gameTimer.resetTimer();
      gameWindow.showScores(game);
    });

    ///////////////////////
    //Build the game stats
    var gameStats = buildGameStats();
    //sideBar.getChildren().add(gameStats);
    mainPane.setRight(gameStats);

    ///////////////////////
    //Build the piece queue
    //Bottom display
    var baseGrid = new GridPane();
    mainPane.setBottom(baseGrid);
    baseGrid.setPrefWidth(gameWindow.getWidth());

    //Build current piece display
    var pieceDisplay = new HBox(10);
    pieceDisplay.setAlignment(Pos.BASELINE_RIGHT);
    pieceDisplay.setPrefWidth(gameWindow.getWidth() / 2);
    pieceDisplay.setPadding(new Insets(0, 10, 10, 10));
    baseGrid.add(pieceDisplay, 0, 0);

    //Create following piece PieceBoard
    //(Making this one first such that it is positioned to the left)
    followingPieceBoard = new PieceBoard(FOLLOWINGPIECEBOARD_SIZE, FOLLOWINGPIECEBOARD_SIZE);
    followingPieceBoard.setOnBlockClick(block -> game.swapCurrentPiece());
    pieceDisplay.getChildren().add(followingPieceBoard);

    //Create next piece PieceBoard (with center circle)
    var nextPieceStack = new StackPane(); //Create stackpane to hold the pieceboard and the circle
    nextPieceStack.setPrefSize(NEXTPIECEBOARD_SIZE, NEXTPIECEBOARD_SIZE);
    nextPieceBoard = new PieceBoard(NEXTPIECEBOARD_SIZE, NEXTPIECEBOARD_SIZE);
    nextPieceBoard.setOnBlockClick(block -> {
      //Default rotate on click is rotate right
      game.rotateRight();
    });
    nextPieceStack.getChildren().add(nextPieceBoard);
    nextPieceStack.getChildren().add(getPieceCenterCircle(nextPieceStack));
    pieceDisplay.getChildren().add(nextPieceStack);

    //PieceBoard logic
    game.setNextPieceListener((nextPiece, followingPiece) -> {
      nextPieceBoard.displayPiece(nextPiece);
      followingPieceBoard.displayPiece(followingPiece);
    });

    ///////////////////////
    //Build the game timer
    gameTimer = new TimerBar(gameWindow.getWidth(), GAMETIMER_WIDTH);
    game.setOnGameLoop(lives -> Platform.runLater(this::restartGameTimer));
    game.addPiecePlayedListener(gamePiece -> {
      Platform.runLater(this::restartGameTimer);
      //restartGameTimer();
    });
    gameTimer.setAlignment(Pos.TOP_CENTER);
    baseGrid.add(gameTimer, 0, 1);
    ///////////////////////

    //Title
    var titleTextStack = new StackPane();
    mainPane.setTop(titleTextStack);

    var titleText = new Text("Challenge");
    titleText.getStyleClass().add("title");
    titleText.setTextAlignment(TextAlignment.CENTER);
    GentleShakeAnimation.startAnimation(titleText);
    titleTextStack.getChildren().add(titleText);

    IsBuilt = true;
  }

  /**
   * Return a small circle to apply to a piece display StackPane
   *
   * @param pieceStack StackPane with a PieceBoard on it
   * @return circle centered on the piecestack
   */
  public Circle getPieceCenterCircle(StackPane pieceStack) {
    var circle = new Circle();
    circle.setCenterX(pieceStack.getTranslateX());
    circle.setCenterY(pieceStack.getTranslateY());
    circle.setRadius(CENTER_CIRCLE_RADIUS);
    circle.setFill(Color.color(1, 1, 1, 0.8));

    return circle;
  }

  /**
   * Assign the various keybinds used to control the Game.
   */
  private void assignControls() {
    game.addKeyListener((event -> {
      if (event.getCode() == KeyCode.ESCAPE) {
        game.shutdownGame();
        gameWindow.startMenu();
      }
    }));

    //Game Aim
    /////////////////
    //Move Left
    game.addKeyListener((event -> {
      if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A) {
        gameAim.moveLeft();
        gameAimBlockHovered();
      }
    }));
    //Move Right
    game.addKeyListener((event -> {
      if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D) {
        gameAim.moveRight();
        gameAimBlockHovered();
      }
    }));
    //Move Up
    game.addKeyListener((event -> {
      if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
        gameAim.moveUp();
        gameAimBlockHovered();
      }
    }));
    //Move Down
    game.addKeyListener((event -> {
      if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
        gameAim.moveDown();
        gameAimBlockHovered();
      }
    }));
    //Place Block
    game.addKeyListener(event -> {
      if (!game.IsRunning) {
        logger.info("Cannot play piece when game not running");
        return;
      }

      if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.X) {
        //Get block at coords
        GameBlock block = board.getBlock(gameAim.getX(), gameAim.getY());
        //Click block at coords
        blockClicked(block);
      }
    });

    //Rotate Piece Left
    game.addKeyListener(event -> {
      if (!game.IsRunning) {
        logger.info("Cannot rotate piece when game not running");
        return;
      }
      if (event.getCode() == KeyCode.Q || event.getCode() == KeyCode.Z
          || event.getCode() == KeyCode.BRACELEFT) {
        game.rotateLeft();
      }
    });
    //Rotate Piece Right
    game.addKeyListener(event -> {
      if (!game.IsRunning) {
        logger.info("Cannot rotate piece when game not running");
        return;
      }
      if (event.getCode() == KeyCode.E || event.getCode() == KeyCode.C
          || event.getCode() == KeyCode.BRACERIGHT) {
        game.rotateRight();
      }
    });
    //Swap Piece
    game.addKeyListener(event -> {
      if (!game.IsRunning) {
        logger.info("Cannot swap piece when game not running");
        return;
      }
      if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.R) {
        game.swapCurrentPiece();
      }
    });
  }

  /**
   * Handle when a block is clicked
   *
   * @param gameBlock the Game Block that was clocked
   */
  private void blockClicked(GameBlock gameBlock) {
    this.game.blockClicked(gameBlock);
  }

  /**
   * Highlight the block that is currently targeted by the Game Aim.
   */
  protected void gameAimBlockHovered() {
    GameBlock block = board.getBlock(gameAim.getX(), gameAim.getY());
    blockHovered(block);
  }

  /**
   * Handle when a block is hovered (by the keyboard controlled GameAim)
   *
   * @param gameBlock the GameBlock that has been hovered
   */
  protected void blockHovered(GameBlock gameBlock) {
    board.setHoveredBlock(gameBlock);
    gameAim.setAimPosition(gameBlock.getX(), gameBlock.getY());
    board.blockHovered(true, gameBlock);
  }

  /**
   * Setup the game object and model
   */
  public void setupGame() {
    logger.info("Starting a new challenge");

    //Start new game
    game = new Game(5, 5);
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");

    game.start();

    scene.setOnKeyPressed(keyEvent -> game.keyPressed(keyEvent)); //Enable controls
    gameTimer.startTimer(game.getTimerDelay());

    Multimedia.stopMusic(); //Stop current music
    Multimedia.gameStart(); //Play game start sound effect
    Multimedia.gameMusic(); //Set to game music
  }

  /**
   * Restart the GameTimer with the timer delay calculated by the Game.
   */
  protected void restartGameTimer() {
    gameTimer.restartTimer(game.getTimerDelay());
  }

  /**
   * Build the stats display of the user interface. Keeps track of score, level, lives and score
   * multiplier.
   *
   * @return VBox containing additional VBoxes for game statistics
   */
  public VBox buildGameStats() {
    //Single Vbox and set that on to the right of the borderpane
    VBox gameStats = new VBox();
    gameStats.setPrefWidth(200);
    gameStats.setAlignment(Pos.TOP_CENTER);
    gameStats.setSpacing(10);

    VBox scoreBox = buildScoreBox();
    VBox hiScoreBox = buildHiScoreBox();
    VBox levelBox = buildLevelBox();
    VBox livesBox = buildLivesBox();
    VBox multiplierBox = buildMultiplierBox();

    gameStats.getChildren().addAll(scoreBox, hiScoreBox, levelBox, livesBox, multiplierBox);

    return gameStats;
  }

  /**
   * Return a VBox containing a title and text property for displaying score.
   *
   * @return score VBox
   */
  public VBox buildScoreBox() {
    var box = new VBox(10);
    var label = new Text("Score");
    label.getStyleClass().add("title");
    box.getChildren().add(label);
    var stat = new Text();
    stat.textProperty().bind(getScoreProperty().asString());
    stat.getStyleClass().add("score");

    GentleShakeAnimation.startAnimation(stat);
    box.getChildren().add(stat);

    return box;
  }

  /**
   * Return a VBox containing a title and text property for displaying level.
   *
   * @return level VBox
   */
  public VBox buildLevelBox() {
    var box = new VBox(10);
    var label = new Text("Level");
    label.getStyleClass().add("heading");
    box.getChildren().add(label);
    var stat = new Text();
    stat.getStyleClass().add("level");
    stat.textProperty().bind(getLevelProperty().asString());

    GentleShakeAnimation.startAnimation(stat);
    box.getChildren().add(stat);

    return box;
  }

  /**
   * Return a VBox containing a title and text property for displaying lives.
   *
   * @return lives VBox
   */
  public VBox buildLivesBox() {
    var box = new VBox(10);
    var label = new Text("Lives");
    label.getStyleClass().add("heading");
    box.getChildren().add(label);
    var stat = new Text();
    stat.getStyleClass().add("lives");
    stat.textProperty().bind(getLivesProperty().asString());

    GentleShakeAnimation.startAnimation(stat);
    box.getChildren().add(stat);

    return box;
  }

  /**
   * Return a VBox containing a title and text property for displaying score multiplier.
   *
   * @return score multiplier VBox
   */
  public VBox buildMultiplierBox() {
    var box = new VBox(10);
    var label = new Text("Multiplier");
    label.getStyleClass().add("heading");
    box.getChildren().add(label);
    var stat = new Text();
    stat.getStyleClass().add("level");
    stat.textProperty().bind(getMultiplierProperty().asString());

    GentleShakeAnimation.startAnimation(stat);
    box.getChildren().add(stat);

    return box;
  }

  /**
   * Return a VBox containing a title and text property for display the highest local score.
   *
   * @return high score VBox
   */
  public VBox buildHiScoreBox() {
    var box = new VBox(10);
    var label = new Text("HiScore");
    label.getStyleClass().add("heading");
    box.getChildren().add(label);

    game.setHiScore();
    int hiScore = game.getHiScore();
    logger.info("Hi score from get = " + hiScore);
    var stat = new Text();
    stat.getStyleClass().add("level");
    stat.textProperty().bind(getHiScoreProperty().asString());
    logger.info("Hi score set to " + getHiScoreProperty().toString());

    GentleShakeAnimation.startAnimation(stat);
    box.getChildren().add(stat);

    return box;
  }
  ///////////////////////////////////

  /**
   * Return the score property.
   *
   * @return score property.
   */
  private SimpleIntegerProperty getScoreProperty() {
    return game.getScoreProperty();
  }

  /**
   * Return the level property.
   *
   * @return level property.
   */
  private SimpleIntegerProperty getLevelProperty() {
    return game.getLevelProperty();
  }

  /**
   * Return the lives property.
   *
   * @return lives property.
   */
  private SimpleIntegerProperty getLivesProperty() {
    return game.getLivesProperty();
  }

  /**
   * Return the multiplier property.
   *
   * @return multiplier property.
   */
  private SimpleIntegerProperty getMultiplierProperty() {
    return game.getMultiplierProperty();
  }

  /**
   * Return the high score property.
   *
   * @return high score property.
   */
  private SimpleIntegerProperty getHiScoreProperty() {
    return game.getHiScoreProperty();
  }
}
