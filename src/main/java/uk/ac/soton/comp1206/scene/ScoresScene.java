package uk.ac.soton.comp1206.scene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.FileUtilities;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.scores.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.game.MultiplayerScoreClass;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.component.scores.UserPromptBox;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Scores Scene of the game. Displayed when a game over is achieved, and holds local/lobby high
 * scores as well as global high scores.
 */
public class ScoresScene extends BaseScene {

  Game game;
  Communicator communicator;
  boolean IsFromMultiplayer;
  private static final Logger logger = LogManager.getLogger(ScoresScene.class);
  /**
   * Local Scores array list
   */
  public List<Pair<String, Integer>> localScoreArrayList = new ArrayList<>();
  /**
   * Local Score observable list
   */
  public ObservableList<Pair<String, Integer>> localScoreObservableList = FXCollections.observableArrayList(
      localScoreArrayList);
  /**
   * Local scores list property
   */
  public SimpleListProperty localScores = new SimpleListProperty(localScoreObservableList);
  ScoresList localScoresList;
  Text localScoreText;
  /**
   * Remote Scores array list
   */
  public List<Pair<String, Integer>> remoteScoreArrayList = new ArrayList<>();
  /**
   * Remote Scores observable list
   */
  public ObservableList<Pair<String, Integer>> remoteScoreObservableList = FXCollections.observableArrayList(
      remoteScoreArrayList);
  /**
   * Remote Scores list property
   */
  public SimpleListProperty remoteScores = new SimpleListProperty(remoteScoreObservableList);
  ScoresList remoteScoresList;
  Text globalScoreText;
  /**
   * Lobby Scores array list
   */
  public List<Pair<String, Integer>> lobbyScoreArrayList = new ArrayList<>();
  /**
   * Lobby Scores observable list
   */
  public ObservableList<Pair<String, Integer>> lobbyScoreObservableList = FXCollections.observableArrayList(
      lobbyScoreArrayList);
  /**
   * Lobby Scores list property
   */
  public SimpleListProperty lobbyScores = new SimpleListProperty(lobbyScoreObservableList);
  ScoresList lobbyScoreList;
  Text lobbyScoreText;
  /**
   * Panes.
   */
  StackPane scoresPane;
  BorderPane mainPane;

  private final String localScoresFilePath = "src/main/resources/localScores.txt";
  private final String defaultScoresFilePath = "src/main/resources/defaultScores.txt";

  /**
   * User Prompt Box.
   */
  UserPromptBox userPromptBox;

  /**
   * Construct a new Scores Scene for a given GameWindow, Game and Communicator.
   *
   * @param gameWindow   game window.
   * @param game         game, can be either a single-player or multiplayer game
   * @param communicator communicator.
   */
  public ScoresScene(GameWindow gameWindow, Game game, Communicator communicator) {
    super(gameWindow);
    this.game = game;
    if (game instanceof MultiplayerGame) {
      logger.info("Multiplayer game, using lobby scores instead of local scores");
      IsFromMultiplayer = true;
    }

    this.communicator = communicator;
    logger.info("Creating Scores Scene");
  }

  /**
   * Build the layout of the scene
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    scoresPane = new StackPane();
    scoresPane.setMaxWidth(gameWindow.getWidth());
    scoresPane.setMaxHeight(gameWindow.getHeight());
    scoresPane.getStyleClass().add("instructions-background");
    scoresPane.setAlignment(Pos.CENTER);
    root.getChildren().add(scoresPane);

    mainPane = new BorderPane();
    scoresPane.getChildren().add(mainPane);

    //Picture title
    var titleImage = new Image(
        Objects.requireNonNull(this.getClass().getResource("/images/TetrECS.png"))
            .toExternalForm());
    var title = new ImageView();
    title.setImage(titleImage);
    title.setPreserveRatio(true);
    title.setFitWidth(gameWindow.getWidth());
    GentleShakeAnimation.startAnimation(title);
    mainPane.setTop(title);

    //Center Display
    var centerDisplay = new VBox();
    centerDisplay.setMaxWidth(gameWindow.getWidth() * 2 / 3);
    centerDisplay.setAlignment(Pos.CENTER);
    mainPane.setCenter(centerDisplay);

    //Game Over Text
    var gameOverText = new Text("Game Over...");
    gameOverText.getStyleClass().add("title");
    gameOverText.setTextAlignment(TextAlignment.CENTER);
    GentleShakeAnimation.startAnimation(gameOverText);
    centerDisplay.getChildren().add(gameOverText);

    //Accompanying Text
    var scoreDisplayPane = new GridPane();
    scoreDisplayPane.setAlignment(Pos.CENTER);
    centerDisplay.getChildren().add(scoreDisplayPane);

    if (IsFromMultiplayer) {
      //Lobby Score Text
      lobbyScoreText = new Text("Lobby");
      lobbyScoreText.getStyleClass().add("title");
      lobbyScoreText.setTextAlignment(TextAlignment.CENTER);
      scoreDisplayPane.add(lobbyScoreText, 0, 0);

      //Create Lobby Score List
      lobbyScoreList = buildScoresList(lobbyScores);
      lobbyScoreList.setAlignment(Pos.CENTER);
      scoreDisplayPane.add(lobbyScoreList, 0, 1);
    } else {
      //Local Score Text
      localScoreText = new Text("Local");
      localScoreText.getStyleClass().add("title");
      localScoreText.setTextAlignment(TextAlignment.CENTER);
      scoreDisplayPane.add(localScoreText, 0, 0);

      //Create Local Score List
      localScoresList = buildScoresList(localScores);
      localScoresList.setAlignment(Pos.CENTER);
      scoreDisplayPane.add(localScoresList, 0, 1);
    }

    //Global Score List
    globalScoreText = new Text("Global");
    globalScoreText.getStyleClass().add("title");
    globalScoreText.setTextAlignment(TextAlignment.CENTER);
    scoreDisplayPane.add(globalScoreText, 1, 0);

    //Create Global Score List
    remoteScoresList = buildScoresList(remoteScores);
    remoteScoresList.setAlignment(Pos.CENTER);
    scoreDisplayPane.add(remoteScoresList, 1, 1);

    //Create user prompt box, hide for now
    userPromptBox = new UserPromptBox(50, 50);
    userPromptBox.setAlignment(Pos.TOP_CENTER);
    userPromptBox.setActive(false);
    centerDisplay.getChildren().add(userPromptBox);

    showScoreListText(false);
  }

  /**
   * Return a new ScoreList bound to the specified score list property.
   *
   * @param listProperty score list property.
   * @return Scores List to hold the scores of this list property.
   */
  private ScoresList buildScoresList(SimpleListProperty listProperty) {
    ScoresList scoresList = new ScoresList();
    scoresList.scoresListProperty.bind(listProperty);
    scoresList.setAlignment(Pos.CENTER);
    return scoresList;
  }

  /**
   * Initialise this scene. Called after creation
   */
  @Override
  public void initialise() {
    logger.info("Initialising ScoreScene");

    scene.setOnKeyPressed((e) -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        gameWindow.startMenu();
      }
    });

    //Link the message listener
    communicator.addListener((message) -> Platform.runLater(() -> this.receiveMessage(message)));

    loadLocalScores();
    loadOnlineScores();
    //Load and check the global game scores once the communicator recieves global scores
  }

  /**
   * Check if the game score beats the local or global scores. If it beats either, prompt the user
   * prompt box.
   */
  private void checkGameScores() {
    //Check if game score beats local/global scores
    boolean beatsLocalScores = false;
    boolean beatsGlobalScores = false;
    int gameScore = getGameScore();

    //Check if beats local scores
    int localIndex = GameScoreBeatsLocalScores(gameScore);
    logger.info("Local Index = " + localIndex);
    if (localIndex != -1) {
      logger.info("Score beats local scores");
      beatsLocalScores = true;
    } else {
      logger.info("Score does not beat local scores");
    }
    //Check if beats global scores
    int globalIndex = GameScoreBeatsOnlineScores(gameScore);
    logger.info("Global Index = " + globalIndex);
    if (globalIndex != -1) {
      logger.info("Score beats global scores");
      beatsGlobalScores = true;
    } else {
      logger.info("Score does not beat global scores");
    }

    //If beats local or global scores
    if (beatsLocalScores || beatsGlobalScores) {
      //Prompt Username
      promptUserName(localIndex, globalIndex);
      //On being submitted, display the scores as normal
      //Write scores to file
    } else {
      Platform.runLater(() -> {
        showScoreLists(); //Update the ScoreList displays
        showScoreListText(true); //Show text
        userPromptBox.setActive(false); //Hide user prompt box
      });
    }
  }

  /**
   * Load the local scores into the local scores list property. If the local scores file doesn't
   * exist, load the default scores into the local scores file.
   */
  private void loadLocalScores() {
    if (game instanceof MultiplayerGame) {
      logger.info("Loading multiplayer game scores");
      ObservableList<MultiplayerScoreClass> multiplayerScoreClasses = ((MultiplayerGame) game).getLobbyScores();
      addLobbyScores(multiplayerScoreClasses);
    } else {
      logger.info("Loading local scores");
      File localScoresFile = new File(localScoresFilePath);
      if (!localScoresFile.exists()) {
        loadDefaultScores();
      }
      FileUtilities.loadScores(localScoresFile, localScores);
    }

  }

  /**
   * Request the lobby scores from the server.
   */
  private void loadLobbyScores() {
    communicator.send("SCORES");
  }

  /**
   * Request the online high scores from the server
   */
  private void loadOnlineScores() {
    communicator.send("HISCORES");
    //For testing, get the default scores
    //communicator.send("HISCORES DEFAULT");
  }

  /**
   * Load the default scores from the default scores file into the local scores file.
   */
  private void loadDefaultScores() {
    File defaultScoresFile = new File(defaultScoresFilePath);
    File localScoresFile = new File(localScoresFilePath);

    try {
      FileUtilities.copyContent(defaultScoresFile, localScoresFile);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Receive and process messages from the communicator.
   *
   * @param message String representing a server message.
   */
  private void receiveMessage(String message) {
    if (message.startsWith("HISCORES")) {
      //If message is exactly HISCORES, ignore
      if (message.equals("HISCORES")) {
        return;
      }
      addRemoteScores(message);
      // Check game score against the local and global lists
      checkGameScores();

    } else if (message.startsWith("SCORES")) {
      logger.info("Received SCORES");
      if (message.equals("SCORES")) {
        return;
      }
      String scoresString = message.replaceFirst("SCORES", "");
      scoresString = scoresString.trim();
      addLobbyScores(scoresString);
    }

  }

  /**
   * Add the remote scores to the remote scores list property from a String containing all names and
   * scores. Name and score separated by a colon, remote scores separated by a new line.
   *
   * @param allScoresMessage String containing all remote scores.
   */
  private void addRemoteScores(String allScoresMessage) {
    String allScores = allScoresMessage.split("HISCORES ")[1]; //Remove beginning
    String[] scoresStrings = allScores.split("\n"); //Split into name:score strings
    //For each split string
    for (String scoreString : scoresStrings) {
      String name = scoreString.split(":")[0]; //Get name
      Integer score = Integer.parseInt(scoreString.split(":")[1]); //Get score
      //Add score to remote scores
      remoteScores.add(new Pair<>(name, score));
    }
  }

  /**
   * Add the lobby scores to the lobby scores list property from an Observable List containing
   * Multiplayer Score Classes. Lives are unimportant and are ignored.
   *
   * @param scoreClasses observable list containing multiplayer score classes.
   */
  private void addLobbyScores(ObservableList<MultiplayerScoreClass> scoreClasses) {
    logger.info("Adding lobby scores to list property");
    for (MultiplayerScoreClass scoreClass : scoreClasses) {
      String name = scoreClass.getUsername();
      Integer score = scoreClass.getScore();
      //Add score to lobby scores
      lobbyScores.add(new Pair<>(name, score));
    }
  }

  /**
   * Add the lobby scores to the lobby scores list property from a String containing all names,
   * scores and lives. Lives are discarded. Name and score separated by a colon, lobby scores
   * separated by a new line.
   *
   * @param allScores String containing all lobby scores.
   */
  private void addLobbyScores(String allScores) {
    logger.info("Setting lobby scores from String");
    lobbyScores.clear(); //Clear lobby scores
    String[] scoresStrings = allScores.split("\n"); //Split into name:score strings

    //For each split string
    for (String scoreString : scoresStrings) {
      String name = scoreString.split(":")[0]; //Get name
      Integer score = Integer.parseInt(scoreString.split(":")[1]); //Get score

      //Add score to lobby scores
      lobbyScores.add(new Pair<>(name, score));
    }
  }

  /**
   * Get the final game score.
   *
   * @return int representing game score.
   */
  private int getGameScore() {
    return game.getScore();
  }

  /**
   * Check if the game's score beats any of the local scores in the list. Return correct index to
   * insert the score into. Return -1 if score does not beat any local score.
   *
   * @param gameScore int representing this game's score
   * @return index to insert this game's score into. Returns -1 if score doesn't beat any local
   * score.
   */
  private int GameScoreBeatsLocalScores(int gameScore) {
    //Given that the local scores are already in order,
    //will return index of the largest value less than gameScore
    for (int i = 0; i < localScoreObservableList.size(); i++) {
      Pair<String, Integer> entry = localScoreObservableList.get(i);
      int score = entry.getValue();
      if (gameScore > score) {
        return i;
      }
    }
    //If doesn't beat any score, return -1
    return -1;
  }

  /**
   * Check if the game's score beats any of the remote scores in the list. Return correct index to
   * insert the score into. Return -1 if score does not beat any remote score.
   *
   * @param gameScore int representing this game's score
   * @return index to insert this game's score into. Returns -1 if score doesn't beat any remote
   * score.
   */
  private int GameScoreBeatsOnlineScores(int gameScore) {
    //Given that the online scores are already in order
    for (int i = 0; i < remoteScoreObservableList.size(); i++) {
      Pair<String, Integer> entry = remoteScoreObservableList.get(i);
      int score = entry.getValue();
      if (gameScore > score) {
        return i;
      }
    }
    //If doesn't beat any score, return -1
    return -1;
  }

  /**
   * Show the user prompt box. Upon the user's name being submitted, submit the score to the correct
   * indices in the local and remote scores lists. Write the new local score to the file, and send
   * the score to the server for the remote scores. Then, show the Score Lists and text and hide the
   * user prompt box.
   *
   * @param localScoreIndex  index to insert this game's score into. Is -1 if score doesn't beat any
   *                         local score.
   * @param globalScoreIndex index to insert this game's score into. Is -1 if score doesn't beat any
   *                         remote score.
   */
  private void promptUserName(int localScoreIndex, int globalScoreIndex) {
    userPromptBox.setActive(true);

    userPromptBox.setKeyPressedListener(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        userPromptBox.submitName();
      }
    });

    userPromptBox.setOnNameSubmitted(username -> {
      //Upon submitting
      logger.info("Submitting Score, " + username + ":" + game.getScore());
      int gameScore = getGameScore();

      //Local Scores
      if (localScoreIndex != -1) {
        insertLocalScore(username, gameScore,
            localScoreIndex); //Insert score into correct position for local scores
        FileUtilities.writeScores(new File(localScoresFilePath),
            localScoreObservableList); //Write local scores to file
      }
      //Global Scores
      if (globalScoreIndex != -1) {
        insertRemoteScore(username, gameScore, globalScoreIndex);
        //Submit new score to remote scores
        communicator.send("HISCORE " + username + ":" + gameScore); //HISCORE <Name>:<Score>
      }

      showScoreLists(); //Update the ScoreList displays
      showScoreListText(true); //Show text
      userPromptBox.setActive(false); //Hide user prompt box

    });
  }

  /**
   * Show or hide the correct text for the tops of each Score List. Local/Lobby text shown depending
   * on if the game is single/multiplayer.
   *
   * @param state boolean.
   */
  private void showScoreListText(boolean state) {
    if (IsFromMultiplayer) {
      lobbyScoreText.setVisible(state);
    } else {
      localScoreText.setVisible(state);
    }
    globalScoreText.setVisible(state);
  }

  /**
   * Show the correct Score Lists. Local/Lobby lists shown depending on if the game is
   * single/multiplayer.
   */
  private void showScoreLists() {
    if (IsFromMultiplayer) {
      lobbyScoreList.updateList(); //Build the lobby score list
    } else {
      localScoresList.updateList(); //Build the local score list
    }
    remoteScoresList.updateList(); //Build the remote score list
  }

  /**
   * Insert a game score, with a username and correct index, into the local scores list.
   *
   * @param username   String representing username.
   * @param gameScore  int representing game score.
   * @param scoreIndex int representing index to insert score into.
   */
  private void insertLocalScore(String username, int gameScore, int scoreIndex) {
    localScores.add(scoreIndex, new Pair<>(username, gameScore));
  }

  /**
   * Insert a game score, with a username and correct index, into the remote scores list.
   *
   * @param username   String representing username.
   * @param gameScore  int representing game score.
   * @param scoreIndex int representing index to insert score into.
   */
  private void insertRemoteScore(String username, int gameScore, int scoreIndex) {
    remoteScores.add(scoreIndex, new Pair<>(username, gameScore));
  }
}
