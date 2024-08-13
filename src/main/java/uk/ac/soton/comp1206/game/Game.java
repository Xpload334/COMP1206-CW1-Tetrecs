package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.FileUtilities;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameFinishedListener;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.KeyPressedListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.PiecePlayedListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to
 * manipulate the game state and to handle actions made by the player should take place inside this
 * class.
 */
public class Game {

  /**
   * Boolean to show if this Game is running.
   */
  public boolean IsRunning;
  private static final Logger logger = LogManager.getLogger(Game.class);
  /**
   * Current piece to play in this Game.
   */
  public static GamePiece currentPiece;
  /**
   * Piece following the Current Piece, can be swapped to.
   */
  public static GamePiece followingPiece;

  //Listeners
  /**
   * Next Piece Listener.
   */
  public NextPieceListener nextPieceListener;
  /**
   * ArrayList of Key Listeners.
   */
  public List<KeyPressedListener> keyListeners = new ArrayList<>();
  /**
   * Line Cleared Listener.
   */
  public LineClearedListener lineClearedListener;
  /**
   * Game Loop Listener.
   */
  public GameLoopListener gameLoopListener;
  /**
   * ArrayList of Piece Played Listeners
   */
  public List<PiecePlayedListener> piecePlayedListeners = new ArrayList<>();
  /**
   * Game Finished Listener
   */
  public GameFinishedListener gameFinishedListener;

  //Game Stat Properties
  /**
   * Score Property.
   */
  protected final SimpleIntegerProperty scoreProperty = new SimpleIntegerProperty(SCORE_START);
  /**
   * Lives Property.
   */
  protected final SimpleIntegerProperty livesProperty = new SimpleIntegerProperty(LIVES_START);
  /**
   * Level Property.
   */
  protected final SimpleIntegerProperty levelProperty = new SimpleIntegerProperty(LEVEL_START);
  /**
   * Multiplier Property.
   */
  protected final SimpleIntegerProperty multiplierProperty = new SimpleIntegerProperty(
      MULTIPLIER_START);
  /**
   * High Score Property.
   */
  private final SimpleIntegerProperty hiScoreProperty = new SimpleIntegerProperty(69);
  /**
   * Initial score value.
   */
  protected static final int SCORE_START = 0;
  /**
   * Initial lives value.
   */
  protected static final int LIVES_START = 3;
  /**
   * Inital level value.
   */
  protected static final int LEVEL_START = 1;
  /**
   * Initial multiplier value.
   */
  protected static final int MULTIPLIER_START = 1;

  //Score and level calculations
  /**
   * Score to increase per block cleared.
   */
  protected static final int SCORE_INCREASE_PER_BLOCK = 10;
  /**
   * Score requirement, per level, to reach a new level.
   */
  protected static final int SCORE_INCREASE_FOR_NEXT_LEVEL = 1000;
  //Game Timer
  /**
   * Maximum time in milliseconds.
   */
  protected static final int TIMER_MAXTIME = 12500;
  /**
   * Minimum time in milliseconds.
   */
  protected static final int TIMER_MINTIME = 2500;
  /**
   * Timer decrease in milliseconds, per level.
   */
  protected static final int TIMER_DECREASEPERLEVEL = 500;
  /**
   * Timer that triggers a Game Loop on completion.
   */
  protected Timer timer;

  /**
   * Number of rows
   */
  protected final int rows;

  /**
   * Number of columns
   */
  protected final int cols;

  /**
   * The grid model linked to the game
   */
  protected final Grid grid;

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);
  }

  /**
   * Start the game
   */
  public void start() {
    logger.info("Starting game");
    setHiScore(); //Set hi score
    initialiseGame();
  }

  /**
   * Initialise a new game and set up anything that needs to be done at the start
   */
  public void initialiseGame() {
    logger.info("Initialising game");
    IsRunning = true;

    currentPiece = spawnPiece(); //Set current piece to a new random piece
    followingPiece = spawnPiece(); //Set following piece to the next random piece

    updatePieceDisplays(); //Send message to listener

    startTimer(); //Start timer
  }

  /**
   * Called whenever the timer runs out, lose a life and discard current piece. Reset multiplier and
   * timer.
   */
  private void gameLoop() {
    logger.info("Life lost, resetting CurrentPiece, Multiplier and Timer.");
    Platform.runLater(() -> {
      setLives(getLives() - 1);
      //If out of lives, end the game
      if (getLives() < 0) {
        endGame();
        return;
      }

      gameLoopListener.gameLoop(getLives());
      Multimedia.lifeLose(); //Life lose sound effect

      replaceCurrentPiece(); //Replace current piece with a new, random piece
      updatePieceDisplays(); //Update PieceBoards
      resetMultiplier(); //Reset score multiplier

      resetTimer(); //Reset game timer
    });

  }

  /**
   * Replace the current piece with a new, random piece.
   */
  protected void replaceCurrentPiece() {
    setCurrentPiece(spawnPiece());
  }

  /**
   * End the current game by calling the GameFinishedListener.
   */
  public void endGame() {
    logger.info("Ending game");
    IsRunning = false;
    cancelTimer();

    Multimedia.gameOver(); //Game over sound effect

    gameFinishedListener.gameFinished(this);
  }

  /**
   * Forcibly end the current game.
   */
  public void shutdownGame() {
    logger.info("Forcibly ended game");
    IsRunning = false;
    cancelTimer();

    Multimedia.gameOver(); //Game over sound effect
  }

  /**
   * Handle what should happen when a particular block is clicked
   *
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock) {
    if (!IsRunning) {
      logger.info("Cannot play piece when game not running");
      return;
    }
    if (currentPiece == null) {
      logger.info("Requires currentPiece to exist");
      return;
    }

    //Get the position of this block
    int x = gameBlock.getX() - 1;
    int y = gameBlock.getY() - 1;

    // I think this handles the base colour changing function of the skeleton grid

    if (grid.canPlayPiece(currentPiece, x, y)) {
      logger.info("Played GamePiece " + currentPiece.getValue() + " at (" + x + ", " + y + ")");
      grid.playPiece(currentPiece, x, y);

      //piecePlayedListener.piecePlayed(currentPiece);
      triggerPiecePlayedListeners(currentPiece);

      nextPiece();
      afterPiece();
    } else {
      logger.info(
          "Cannot play GamePiece " + currentPiece.getValue() + " at (" + x + ", " + y + ")");

      //Play error place sound effect
      Multimedia.placeError();
    }
  }

  /**
   * Trigger all Piece Played Listeners with the given GamePiece.
   *
   * @param piece GamePiece to pass through.
   */
  private void triggerPiecePlayedListeners(GamePiece piece) {
    for (PiecePlayedListener listener : piecePlayedListeners) {
      listener.piecePlayed(piece);
    }

  }

  /**
   * Get the grid model inside this game representing the game state of the board
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
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

  /**
   * Create a new random GamePiece
   *
   * @return random GamePiece
   */
  protected GamePiece spawnPiece() {
    Random random = new Random();
    int piece = random.nextInt(GamePiece.PIECES);
    return GamePiece.createPiece(piece);
  }

  /**
   * Replace the current GamePiece with a new, random GamePiece. Also updates the PieceBoard
   * display.
   */
  protected void nextPiece() {
    logger.info("Spawning next GamePiece");

    setCurrentPiece(followingPiece);
    setFollowingPiece(spawnPiece());

    //Send message to listener
    updatePieceDisplays();
  }

  /**
   * Set the current GamePiece to the specified GamePiece.
   *
   * @param currentPiece GamePiece to set the current GamePiece to.
   */
  public static void setCurrentPiece(GamePiece currentPiece) {
    Game.currentPiece = currentPiece;
  }

  /**
   * Set the following GamePiece to the specified GamePiece.
   *
   * @param followingPiece GamePiece to set the following GamePiece to.
   */
  public static void setFollowingPiece(GamePiece followingPiece) {
    Game.followingPiece = followingPiece;
  }

  /**
   * Return the current GamePiece.
   *
   * @return current GamePiece.
   */
  public static GamePiece getCurrentPiece() {
    return currentPiece;
  }

  /**
   * Rotate the current piece right. Also updates the PieceBoard display.
   */
  public void rotateRight() {
    if (!IsRunning) {
      logger.info("Cannot rotate piece when game not running");
      return;
    }

    logger.info("Rotating GamePiece right");
    currentPiece.rotate(1);
    Multimedia.rotate();

    //Send message to listener
    updatePieceDisplays();
  }

  /**
   * Rotate the current piece left. Also updates the PieceBoard display.
   */
  public void rotateLeft() {
    if (!IsRunning) {
      logger.info("Cannot rotate piece when game not running");
      return;
    }

    logger.info("Rotating GamePiece left");
    currentPiece.rotate(3);
    Multimedia.rotate();

    //Send message to listener
    updatePieceDisplays();
  }

  /**
   * Swap the current GamePiece with the following GamePiece.
   */
  public void swapCurrentPiece() {
    if (!IsRunning) {
      logger.info("Cannot swap piece when game not running");
      return;
    }
    logger.info("Swapping current GamePiece with following GamePiece");

    GamePiece temp = currentPiece;
    setCurrentPiece(followingPiece);
    setFollowingPiece(temp);
    updatePieceDisplays();
    Multimedia.swap();
  }

  /**
   * Call the NextPieceListener with the current and following GamePieces.
   */
  public void updatePieceDisplays() {
    nextPieceListener.nextPieceGenerated(currentPiece, followingPiece);
  }

  /**
   * Handles logic after placing a piece. Clears any full rows or columns that have been made.
   * Multiple lines may be cleared at once, including intersecting lines.
   * <p>
   * If any lines are cleared, clear the affected coordinates, increase the score and multiplier
   * appropriately and play the necessary sound effect. Else, reset the score multiplier.
   * <p>
   * If a new level is reached, update the level.
   * <p>
   * Reset the game loop timer.
   */
  protected void afterPiece() {
    //Play normal place sound effect
    Multimedia.place();

    //Set of coordinates to clear
    Set<GameBlockCoordinate> clearedCoordinates = new HashSet<>();
    //New counter for number of cleared columns and rows
    int clearedLines = 0;

    //Check all rows
    for (int x = 0; x < getRows(); x++) {
      //If row full
      if (grid.rowIsFull(x)) {
        clearedLines++; //Increase number of cleared lines
        //Get all coordinates from that row
        for (int y = 0; y < grid.getCols(); y++) {
          //Add coordinate to set
          clearedCoordinates.add(new GameBlockCoordinate(x, y));
        }
      }
    }
    //Check all columns
    for (int y = 0; y < getCols(); y++) {
      //If column full
      if (grid.colIsFull(y)) {
        clearedLines++; //Increase number of cleared lines
        //Get all coordinates from that column
        for (int x = 0; x < grid.getRows(); x++) {
          //Add coordinate to set
          clearedCoordinates.add(new GameBlockCoordinate(x, y));
        }
      }
    }
    //Get blocks removed
    //Better than the previous formula, which relied on a square grid
    int blocksRemoved = clearedCoordinates.size();
    //If any removed
    if (blocksRemoved != 0) {
      //Clear all affected coordinates
      //For each coordinate in Set
      for (GameBlockCoordinate coordinate : clearedCoordinates) {
        grid.clear(coordinate.getX(), coordinate.getY()); //grid.clear()
      }
      //Calculate and increase score
      int scoreIncrease = calculateScore(blocksRemoved, clearedLines, getMultiplier());
      score(scoreIncrease);
      //Increase multiplier
      increaseMultiplier();
      //Trigger listener
      lineClearedListener.lineCleared(clearedCoordinates);
      //Set hi score, if the score beats the hi score
      setHiScore();
      //Play sound
      Multimedia.clearLine();
    }
    //Else, reset multiplier
    else {
      resetMultiplier();
    }
    //If reached a new level
    if (ReachedNewLevel()) {
      setNewLevel();
      Multimedia.level(); //Play sound effect
    }

    //Reset timer
    resetTimer();
  }


  /**
   * Calculate the score according to the following formula: number of lines * number of grid blocks
   * cleared * 10 * the current multiplier
   *
   * @param blocks     number of blocks cleared
   * @param lines      number of lines cleared
   * @param multiplier current score multiplier
   * @return int representing the calculated score.
   */
  private int calculateScore(int blocks, int lines, int multiplier) {
    return (lines * blocks * SCORE_INCREASE_PER_BLOCK * multiplier);
  }

  /**
   * Add the score from cleared lines to the current score property.
   *
   * @param score int representing the score to increase the score property by.
   */
  private void score(int score) {
    int newScore = getScore() + score;
    logger.info("Scored " + score + " (" + newScore + ")");
    setScore(newScore);
  }

  /**
   * Increase the multiplier by 1.
   */
  private void increaseMultiplier() {
    setMultiplier(getMultiplier() + 1);
    logger.info("Multiplier increased (" + getMultiplier() + ")");
  }

  /**
   * Reset the multiplier
   */
  private void resetMultiplier() {
    setMultiplier(1);
    logger.info("Multiplier reset (" + getMultiplier() + ")");
  }

  /**
   * Return if the total score exceeds the boundary to reach a new level.
   * <p>
   * If the current score is greater than the next level * 1000, return true.
   *
   * @return if a new level can be reached with the current score
   */
  private boolean ReachedNewLevel() {
    int levelThreshold = (getLevel() + 1) * SCORE_INCREASE_FOR_NEXT_LEVEL;
    return getScore() >= levelThreshold;
  }

  /**
   * Set the level property to the highest available level.
   */
  private void setNewLevel() {
    int newLevel = (int) Math.floor(getScore() / SCORE_INCREASE_FOR_NEXT_LEVEL);
    logger.info("Reached level " + newLevel);
    setLevel(newLevel);
  }

  /**
   * Return the time, in milliseconds, that the current game timer should use. Timer is the maximum
   * of either 2500 milliseconds or 12000 - 500 * the current level.
   *
   * @return timer length, in milliseconds
   */
  public long getTimerDelay() {
    long timeForThisLevel = TIMER_MAXTIME - ((long) TIMER_DECREASEPERLEVEL * getLevel());
    return Math.max(timeForThisLevel, TIMER_MINTIME);
  }

  /**
   * Start the game timer for the calculated delay which, on completion, calls the game loop.
   */
  protected void startTimer() {
    logger.info("Started new Timer for " + getTimerDelay() + " milliseconds");

    timer = new Timer();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        gameLoop();
      }
    };
    timer.schedule(timerTask, getTimerDelay());
  }

  /**
   * Cancel, then start the game timer.
   */
  private void resetTimer() {
    cancelTimer();
    startTimer();
  }

  /**
   * Cancel the game timer.
   */
  protected void cancelTimer() {
    logger.info("Timer cancelled");
    timer.cancel();
    timer.purge();
  }

  //Score accessors
  /////////////////

  /**
   * Return the value of the score property.
   *
   * @return int representing the game score.
   */
  public int getScore() {
    return scoreProperty.get();
  }

  /**
   * Set the value of the score property.
   *
   * @param scoreProperty int representing the game score.
   */
  public void setScore(int scoreProperty) {
    this.scoreProperty.set(scoreProperty);
  }

  /**
   * Return the score property.
   *
   * @return score property.
   */
  public SimpleIntegerProperty getScoreProperty() {
    return scoreProperty;
  }

  //Level accessors
  /////////////////

  /**
   * Return the game level.
   *
   * @return game level.
   */
  public int getLevel() {
    return levelProperty.get();
  }

  /**
   * Set the game level.
   *
   * @param levelProperty game level.
   */
  public void setLevel(int levelProperty) {
    this.levelProperty.set(levelProperty);
  }

  /**
   * Return the level property
   *
   * @return level integer property
   */
  public SimpleIntegerProperty getLevelProperty() {
    return levelProperty;
  }

  //Lives accessors
  /////////////////

  /**
   * Return the game lives.
   *
   * @return game lives
   */
  public int getLives() {
    return livesProperty.get();
  }

  /**
   * Set the game lives.
   *
   * @param livesProperty game lives
   */
  public void setLives(int livesProperty) {
    this.livesProperty.set(livesProperty);
  }

  /**
   * Return the lives property.
   *
   * @return lives integer property
   */
  public SimpleIntegerProperty getLivesProperty() {
    return livesProperty;
  }

  //Multiplier accessors
  //////////////////////

  /**
   * Return the game multiplier.
   *
   * @return game multiplier
   */
  public int getMultiplier() {
    return multiplierProperty.get();
  }

  /**
   * Set the game multiplier.
   *
   * @param multiplierProperty game multiplier
   */
  public void setMultiplier(int multiplierProperty) {
    this.multiplierProperty.set(multiplierProperty);
  }

  /**
   * Return the multiplier property
   *
   * @return multiplier integer property
   */
  public SimpleIntegerProperty getMultiplierProperty() {
    return multiplierProperty;
  }

  //Hi Score
  //////////////////////////

  /**
   * Return the game high score.
   *
   * @return high score.
   */
  public int getHiScore() {
    return hiScoreProperty.get();
  }

  /**
   * Set the game high score to be the maximum of the current score or the local scores' highest
   * score.
   */
  public void setHiScore() {
    logger.info("Setting hi score");
    int hiScore = Math.max(getScore(), FileUtilities.getLocalHighScore());
    logger.info("Hi score = " + hiScore);
    this.hiScoreProperty.set(hiScore);
  }

  /**
   * Return the high score property.
   *
   * @return high score property.
   */
  public SimpleIntegerProperty getHiScoreProperty() {
    return hiScoreProperty;
  }

  //Listeners
  ///////////

  /**
   * Set the Next Piece Listener.
   *
   * @param listener Next Piece Listener
   */
  public void setNextPieceListener(NextPieceListener listener) {
    nextPieceListener = listener;
  }

  /**
   * Add a Key Pressed Listener to the keyListeners ArrayList
   *
   * @param listener Key Pressed Listener
   */
  public void addKeyListener(KeyPressedListener listener) {
    keyListeners.add(listener);
  }

  /**
   * When a key is pressed, call each Key Pressed Listener in the keyListeners ArrayList.
   *
   * @param keyEvent KeyEvent called
   */
  public void keyPressed(KeyEvent keyEvent) {
    for (KeyPressedListener listener : keyListeners) {
      listener.keyPressed(keyEvent);
    }
  }

  /**
   * Set the Line Cleared Listener.
   *
   * @param listener Line Cleared Listener.
   */
  public void setLineClearedListener(LineClearedListener listener) {
    lineClearedListener = listener;
  }

  /**
   * Set the Game Loop Listener.
   *
   * @param listener Game Loop Listener.
   */
  public void setOnGameLoop(GameLoopListener listener) {
    gameLoopListener = listener;
  }

  /**
   * Add a Piece Played Listener
   *
   * @param listener Piece Played Listener
   */
  public void addPiecePlayedListener(PiecePlayedListener listener) {
    piecePlayedListeners.add(listener);
    //piecePlayedListener = listener;
  }

  /**
   * Set Game Finished Listener
   *
   * @param listener Game Finished Listener
   */
  public void setGameFinishedListener(GameFinishedListener listener) {
    gameFinishedListener = listener;
  }
}
