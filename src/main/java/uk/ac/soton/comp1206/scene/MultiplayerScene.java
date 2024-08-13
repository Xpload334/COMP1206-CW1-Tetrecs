package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import uk.ac.soton.comp1206.component.scores.Leaderboard;
import uk.ac.soton.comp1206.game.GameAim;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.game.MultiplayerScoreClass;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Multiplayer Challenge Scene. Holds the UI for the multiplayer challenge mode in the game.
 */
public class MultiplayerScene extends ChallengeScene {

  private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
  private MultiplayerGame game;
  Communicator communicator;
  Leaderboard leaderboard;

  /**
   * Lobby Scores
   */
  public List<MultiplayerScoreClass> lobbyScoreArrayList = new ArrayList<>();
  /**
   * Lobby Scores observable list.
   */
  public ObservableList<MultiplayerScoreClass> lobbyScoreObservableList = FXCollections.observableArrayList(
      lobbyScoreArrayList);
  /**
   * Lobby Scores list property.
   */
  public SimpleListProperty lobbyScores = new SimpleListProperty(lobbyScoreObservableList);

  /**
   * Create a new Multiplayer challenge scene.
   *
   * @param gameWindow the Game Window.
   * @param communicator communicator.
   */
  public MultiplayerScene(GameWindow gameWindow, Communicator communicator) {
    super(gameWindow);
    this.communicator = communicator;
  }

  /**
   * Build the Multiplayer challenge scene.
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();
    //game = new MultiplayerGame(5,5);

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
    game.setGameFinishedListener(game -> {
      logger.info("Received GameFinishedListener, showing scores");
      sendLives(game.getLives());
      gameTimer.resetTimer();
      gameWindow.showScores(game);
      sendDieMessage(); //Send die message
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

    var pieceDisplay = new HBox(10);
    //Build current piece display
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

    ///////////////////////
    //Build the game timer
    gameTimer = new TimerBar(gameWindow.getWidth(), GAMETIMER_WIDTH);
    game.setOnGameLoop(lives -> {
      Platform.runLater(this::restartGameTimer);

      sendLives(lives); //Send lives messages
    });
    game.addPiecePlayedListener(gamePiece -> Platform.runLater(this::restartGameTimer));
    baseGrid.add(gameTimer, 0, 1);
    ///////////////////////

    var titleTextStack = new StackPane();
    mainPane.setTop(titleTextStack);

    var titleText = new Text("Multiplayer");
    titleText.getStyleClass().add("title");
    titleText.setTextAlignment(TextAlignment.CENTER);
    titleTextStack.getChildren().add(titleText);

    IsBuilt = true;
    //////////////////////////
    //END OF CHALLENGE SCENE BUILD
    //////////////////////////
    leaderboard = new Leaderboard();
    leaderboard.setPrefWidth(100);
    leaderboard.setPrefHeight(400);
    leaderboard.lobbyScoreClassListProperty.bind(lobbyScores);
    leaderboard.setAlignment(Pos.CENTER);
    mainPane.setLeft(leaderboard);

    game.addPiecePlayedListener((gamePiece) -> {
      requestNewPiece();

      sendBoardString(game.getBoardStateString()); //Send board string to server
      sendScore(game.getScore()); //Send score to server
    });

    game.setOnPieceRequest(this::requestNewPiece);

    game.setOnGameParted(thisGame -> {
      sendDieMessage(); //Send die message
    });


  }

  /**
   * Setup the game object and model
   */
  @Override
  public void setupGame() {
    logger.info("Starting a new challenge");

    //Start new multiplayer game
    game = new MultiplayerGame(5, 5);
  }

  /**
   * Assign the various keybinds used to control the Game.
   */
  protected void assignControls() {
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
   * Initialise the multiplayer challenge scene.
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");
    //PieceBoard logic
    game.setNextPieceListener((nextPiece, followingPiece) -> {
      nextPieceBoard.displayPiece(nextPiece);
      followingPieceBoard.displayPiece(followingPiece);
    });

    scene.setOnKeyPressed(keyEvent -> game.keyPressed(keyEvent)); //Enable controls
    gameTimer.startTimer(game.getTimerDelay());

    Multimedia.stopMusic(); //Stop current music
    Multimedia.gameMusic(); //Set to game music

    communicator.addListener(this::recieveMessage);

    leaderboard.updateList();
    leaderboard.setVisible(true);

    this.game.start();

  }

  /**
   * Restart the GameTimer with the timer delay calculated by the Game.
   */
  protected void restartGameTimer() {
    gameTimer.restartTimer(game.getTimerDelay());
  }

  /**
   * Receive and process messages from the communicator.
   *
   * @param message String representing a server message.
   */
  private void recieveMessage(String message) {
    if (message.startsWith("PIECE")) {
      logger.info("Received PIECE");
      String pieceString = message.replaceFirst("PIECE", "");
      pieceString = pieceString.trim();
      int pieceIndex = Integer.parseInt(pieceString);
      game.enqueuePiece(pieceIndex);

    } else if (message.startsWith("SCORES")) {
      logger.info("Received SCORES");
      String scoresString = message.replaceFirst("SCORES", "");
      scoresString = scoresString.trim();
      setLobbyScores(scoresString);

    } else if (message.startsWith("DIE")) {
      logger.info("Received DIE");
    }
  }

  /**
   * Set the list of lobby scores from a String containing all lobby names, scores and lives. Name,
   * score and live are separated by a colon. Each lobby score is separated by a new line.
   *
   * @param allScores
   */
  private void setLobbyScores(String allScores) {
    logger.info("Setting lobby scores from String");
    lobbyScores.clear(); //Clear lobby scores
    String[] scoresStrings = allScores.split("\n"); //Split into name:score strings

    //For each split string
    for (String scoreString : scoresStrings) {
      String name = scoreString.split(":")[0]; //Get name
      int score = Integer.parseInt(scoreString.split(":")[1]); //Get score
      String lives = scoreString.split(":")[2]; //Get Lives
      int livesInteger;
      if (lives.equals("DEAD")) {
        livesInteger = -1;
      } else {
        livesInteger = Integer.parseInt(lives);
      }

      //Add score to lobby scores
      lobbyScores.add(new MultiplayerScoreClass(name, score, livesInteger));
      logger.info("Added new lobby score " + name + ":" + score + ":" + lives);
    }
    game.setLobbyScores(lobbyScores);
    Platform.runLater(() -> {
      leaderboard.updateList(lobbyScores);
    });
  }

  /**
   * Request a new GamePiece index from the server.
   */
  public void requestNewPiece() {
    communicator.send("PIECE");
  }

  /**
   * Send the current board state to the server.
   *
   * @param boardString String representing the board state.
   */
  public void sendBoardString(String boardString) {
    communicator.send(boardString);
  }

  /**
   * Send the current score to the server.
   *
   * @param score int representing the game score.
   */
  public void sendScore(int score) {
    communicator.send("SCORE " + score);
  }

  /**
   * Send the current lives to the server.
   *
   * @param lives int representing the game lives.
   */
  public void sendLives(int lives) {
    communicator.send("LIVES " + lives);
  }

  /**
   * Send a die message to the server.
   */
  public void sendDieMessage() {
    sendLives(-1);
    communicator.send("DIE");
  }

  /////////////////////////

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

  /////////////////////////
  //Scores
  /////////////////////////

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
    gameStats.setSpacing(20);

    VBox scoreBox = buildScoreBox();
    VBox levelBox = buildLevelBox();
    VBox livesBox = buildLivesBox();
    VBox multiplierBox = buildMultiplierBox();
    gameStats.getChildren().addAll(scoreBox, levelBox, livesBox, multiplierBox);

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

}
