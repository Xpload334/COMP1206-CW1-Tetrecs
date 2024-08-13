package uk.ac.soton.comp1206.game;

import java.util.LinkedList;
import java.util.Queue;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.GamePartingListener;
import uk.ac.soton.comp1206.event.PieceRequestListener;

/**
 * The Multiplayer Game class handles the main logic, state and properties of the TetrECS
 * multiplayer game. Methods are similar to the Game class but communicate with a server to receive
 * GamePieces and submit game statistics.
 */
public class MultiplayerGame extends Game {
  private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
  /**
   * Queue of Game Pieces.
   */
  public Queue<GamePiece> pieceQueue;
  /**
   * Length of the Piece Queue.
   */
  private static final int PIECEQUEUE_LENGTH = 5;
  private int initialiseIndex = 2;

  private PieceRequestListener pieceRequestListener;
  private GamePartingListener gamePartingListener;

  private SimpleListProperty lobbyScores;

  /**
   * Create a new Multiplayer Game with the specified rows and columns. Creates a corresponding grid
   * model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public MultiplayerGame(int cols, int rows) {
    super(cols, rows);

    pieceQueue = new LinkedList<>();
  }

  /**
   * Initialise a new Multiplayer Game and set up anything that needs to be done at the start
   */
  @Override
  public void initialiseGame() {
    logger.info("Initialising multiplayer game");
    IsRunning = true;

    initialisePieceQueue();
    startTimer(); //Start timer
  }

  /**
   * End the current game by calling the GameFinishedListener.
   */
  public void endGame() {
    logger.info("Ending game");
    IsRunning = false;
    cancelTimer();

    gameFinishedListener.gameFinished(this);
  }

  /**
   * Forcibly end the current game.
   */
  public void shutdownGame() {
    logger.info("Forcibly ended game");
    IsRunning = false;
    cancelTimer();

    gamePartingListener.gameParted(this);
  }

  /**
   * Initialise the piece queue with several GamePieces. Updates the piece displays.
   */
  private void initialisePieceQueue() {
    for (int i = 0; i < (PIECEQUEUE_LENGTH + 2); i++) {
      requestPiece();
    }

    updatePieceDisplays(); //Send message to listener
  }

  /**
   * Request a new GamePiece from the server.
   */
  private void requestPiece() {
    pieceRequestListener.requestPiece();
  }

  /**
   * Enqueue a GamePiece of given index to the piece queue. For the first two GamePieces enqueued,
   * they are instead set to the CurrentPiece and FollowingPiece. Updates the piece displays.
   *
   * @param pieceIndex index of the GamePiece.
   */
  public void enqueuePiece(int pieceIndex) {
    logger.info("Queuing new piece " + pieceIndex);

    if (initialiseIndex == 2) {
      setCurrentPiece(GamePiece.createPiece(pieceIndex));
      initialiseIndex--;
    } else if (initialiseIndex == 1) {
      setFollowingPiece(GamePiece.createPiece(pieceIndex));
      initialiseIndex--;
    } else {
      pieceQueue.add(GamePiece.createPiece(pieceIndex));
    }
    logger.info(pieceQueue.toString());

    updatePieceDisplays(); //Send message to listener
  }

  /**
   * Replace the current GamePiece with a new, random GamePiece. Also updates the PieceBoard
   * display.
   */
  @Override
  protected void nextPiece() {
    logger.info("Spawning next GamePiece");

    setCurrentPiece(followingPiece);
    //Instead of spawning, dequeue piece
    GamePiece piece = pieceQueue.remove();
    setFollowingPiece(piece);
    //Requesting piece is handled by listener, no need to call here

    //Send message to listener
    updatePieceDisplays();
  }

  /**
   * Replace the current piece with a piece dequeued from the piece queue.
   */
  @Override
  protected void replaceCurrentPiece() {
    logger.info("Replacing with piece from queue");
    //Dequeue piece
    GamePiece piece = pieceQueue.remove();
    setCurrentPiece(piece);
    //Send message to listeners
    updatePieceDisplays();
  }

  /**
   * Return the state of this grid as a String of ints, rows left to right and columns top to
   * bottom, separated by spaces.
   *
   * @return String of the current grid state.
   */
  public String getBoardStateString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("BOARD ");
    for (int y = 0; y < getCols(); y++) {
      for (int x = 0; x < getRows(); x++) {
        int value = grid.get(x, y);
        stringBuilder.append(value);
        stringBuilder.append(" ");
      }
    }
    return stringBuilder.toString().trim();

  }

  /**
   * Set the piece request listener.
   * @param listener listener.
   */
  public void setOnPieceRequest(PieceRequestListener listener) {
    pieceRequestListener = listener;
  }

  /**
   * Set the game parted listener.
   * @param listener listener.
   */
  public void setOnGameParted(GamePartingListener listener) {
    gamePartingListener = listener;
  }

  /**
   * Set the lobby scores listener.
   * @param listProperty listener
   */
  public void setLobbyScores(SimpleListProperty listProperty) {
    lobbyScores = listProperty;
  }

  /**
   * Return the Observable List containing the lobby scores.
   * @return lobby scores.
   */
  public ObservableList getLobbyScores() {
    return lobbyScores.get();
  }
}
