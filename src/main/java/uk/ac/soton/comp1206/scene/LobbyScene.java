package uk.ac.soton.comp1206.scene;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.TimerBar;
import uk.ac.soton.comp1206.component.multiplayer.ChannelDisplay;
import uk.ac.soton.comp1206.component.multiplayer.ChannelList;
import uk.ac.soton.comp1206.component.multiplayer.ChannelPane;
import uk.ac.soton.comp1206.component.multiplayer.NewLobbyMenu;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Multiplayer lobby scene. Holds the UI for seeing, joining and creating multiplayer game lobbies.
 * UI for lobby chat messages and starting a game is handled inside ChannelPane.
 */
public class LobbyScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(LobbyScene.class);
  Communicator communicator;
  /**
   * Timer bar reused from challenge scene to indicate channel requests
   */
  private TimerBar channelRequestTimerBar;
  private Timer timer;
  private static final long CHANNEL_REQUEST_DELAY = 4000;

  /**
   * Channel List
   */
  private ChannelList channelList;
  /**
   * New Lobby Menu
   */
  private NewLobbyMenu newLobbyMenu;
  /**
   * Channel pane. Keep the LobbyScene open such that the communication of channels can still
   * occur.
   */
  private boolean InLobby = false;
  private String currentChannel;
  private ChannelPane channelPane;
  private BorderPane channelCenterPane;

  /**
   * Construct a new LobbyScene for a specified GameWindow and using a specified Communicator.
   *
   * @param gameWindow   game window.
   * @param communicator communicator class.
   */
  public LobbyScene(GameWindow gameWindow, Communicator communicator) {
    super(gameWindow);
    this.communicator = communicator;
    logger.info("Creating Instructions Scene");
  }

  /**
   * Build the layout of the scene
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var lobbyPane = new StackPane();
    lobbyPane.setMaxWidth(gameWindow.getWidth());
    lobbyPane.setMaxHeight(gameWindow.getHeight());
    lobbyPane.getStyleClass().add("instructions-background");
    root.getChildren().add(lobbyPane);

    var mainPane = new BorderPane();
    lobbyPane.getChildren().add(mainPane);

    //Build channel list pane
    var channelListPane = new VBox();
    channelListPane.setSpacing(30);
    channelListPane.setPadding(new Insets(10, 10, 10, 10));
    channelListPane.setPrefWidth(gameWindow.getWidth() / (2));
    mainPane.setLeft(channelListPane);

    //Build Title Text
    var titleText = new Text("Multiplayer Lobbies");
    titleText.getStyleClass().add("title");
    titleText.setTextAlignment(TextAlignment.CENTER);
    channelListPane.getChildren().add(titleText);

    //Channel list
    channelList = new ChannelList(channelListPane.getWidth());
    var scroller = new ScrollPane();
    scroller.setPrefWidth(channelListPane.getWidth());
    scroller.setPrefHeight(gameWindow.getHeight());
    scroller.setContent(channelList);
    scroller.setFitToWidth(true);
    scroller.getStyleClass().add("scroller");
    scroller.setPadding(new Insets(20, 20, 20, 20));
    channelListPane.getChildren().add(scroller);

    //Build side panel for creating own game
    var sidePanel = new VBox();
    sidePanel.setAlignment(Pos.BOTTOM_CENTER);
    mainPane.setRight(sidePanel);
    newLobbyMenu = new NewLobbyMenu();
    sidePanel.getChildren().add(newLobbyMenu);

    ///////////////////////
    //Build the game timer
    channelRequestTimerBar = new TimerBar(gameWindow.getWidth(), 15);
    mainPane.setBottom(channelRequestTimerBar);
    ///////////////////////

    //Channel pane
    channelCenterPane = new BorderPane();
    channelCenterPane.setVisible(false);
    lobbyPane.getChildren().add(channelCenterPane);

    channelPane = new ChannelPane(communicator);
    double channelPanePadding = 50;
    channelPane.setMaxWidth(gameWindow.getWidth() - channelPanePadding);
    channelPane.setMaxHeight(gameWindow.getHeight() - channelPanePadding);
    channelPane.setVisible(false); //Initially set visibility to false, enable when needed
    channelCenterPane.setCenter(channelPane);
  }

  /**
   * Initialise this scene. Called after creation
   */
  @Override
  public void initialise() {
    logger.info("Initialising Instructions");

    scene.setOnKeyPressed((e) -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        if (InLobby) {
          leaveChannel();
        } else {
          channelRequestTimerBar.resetTimer();
          cancelTimer();
          disconnectFromServer();
          gameWindow.startMenu();
        }
      }
    });

    //Receive communicator messages
    communicator.addListener(this::recieveMessage);

    //On create new lobby button pressed
    newLobbyMenu.setOnCreatePressed(() -> {
      //On Creating a new channel
      String channelName = newLobbyMenu.getChannelName();
      //If is empty
      if (channelName.isEmpty()) {
        logger.info("Channel name submission is empty");
        Alert invalidChannelAlert = new Alert(AlertType.ERROR);
        invalidChannelAlert.setHeaderText("Lobby Creation Error");
        invalidChannelAlert.setContentText("Please enter a name for this lobby.");
        invalidChannelAlert.show();
      }
      //If name already exists
      else if (channelList.ChannelNameExists(channelName)) {
        Alert invalidChannelAlert = new Alert(AlertType.ERROR);
        invalidChannelAlert.setHeaderText("Lobby Creation Error");
        invalidChannelAlert.setContentText("A lobby already exists with this name.");
        invalidChannelAlert.show();
      }
      //If already in a lobby
      else if (InLobby) {
        Alert invalidChannelAlert = new Alert(AlertType.ERROR);
        invalidChannelAlert.setHeaderText("Lobby Creation Error");
        invalidChannelAlert.setContentText("Cannot join lobby whilst in a lobby.");
        invalidChannelAlert.show();
      } else {
        createChannel(channelName);//Create the channel
        newLobbyMenu.clearTextField();
        //Join channel happens after creation anyway
      }
    });

    channelList.setOnChannelCleared(() -> logger.info("Channel list cleared"));

    //Initially request channels from communicator
    requestChannels();
    //Start the channel request timer
    startTimer();
    //Start the channel request animated timer
    startTimerBar();
  }

  //////////////////////////////////////////////////////////

  /**
   * Send a message to the Communicator to create a new channel
   *
   * @param channelName string representing the name of the channel
   */
  private void createChannel(String channelName) {
    logger.info("Creating new channel " + channelName);
    communicator.send("CREATE " + channelName);
    //createChannelDisplay(channelName);
  }

  /**
   * Request current channels, using the Communicator, from the server.
   */
  private void requestChannels() {
    logger.info("Requesting channels from Communicator");
    communicator.send("LIST");
  }

  /**
   * Make the given channelName the current channel, then show the channel pane for that given
   * channel
   *
   * @param channelName String representing the channel name.
   */
  private void joinChannel(String channelName) {
    logger.info("Joining channel " + channelName);
    currentChannel = channelName;
    showChannelPane(true);
    InLobby = true;
    requestUsers(); //Request users when joining

    Multimedia.lobbyStart(); //Lobby start sound effect
  }

  /**
   * Leave a channel.
   */
  private void leaveChannel() {
    channelPane.leaveChannel();
  }

  /**
   * Disconnect from the multiplayer server.
   */
  private void disconnectFromServer() {
    communicator.send("QUIT");
  }

  /**
   * Receive and process messages from the communicator.
   *
   * @param message String representing a server message.
   */
  private void recieveMessage(String message) {
    logger.info("Recieved message: " + message);

    /////////////////////////
    //Server messages
    ////////////////////////
    // For list of channels
    if (message.startsWith("CHANNELS")) {
      logger.info("Recieved CHANNELS");
      //Use a listener to start adding channels once the channel list is cleared
      channelList.setOnChannelCleared(() -> {
        String channelNames = message.replaceFirst("CHANNELS", "");
        channelNames = channelNames.trim();
        if (!channelNames.isEmpty()) {
          createListOfChannels(channelNames);
        }
      });
      clearChannelDisplays();

    } //Join channel
    else if (message.startsWith("JOIN")) {
      logger.info("Recieved JOIN");
      String channelName = message.replaceFirst("JOIN", "");
      channelName = channelName.trim();
      joinChannel(channelName);

    } //If an action was not possible
    else if (message.startsWith("ERROR")) {
      Alert serverErrorAlert = new Alert(AlertType.ERROR);
      serverErrorAlert.setHeaderText("Server Error");
      String errorContent = message.replaceFirst("ERROR", "");
      errorContent = errorContent.trim();
      serverErrorAlert.setContentText(errorContent);
      serverErrorAlert.show();

    }
    ///////////////////////////
    //Channel messages
    ///////////////////////////
    //Chat Messages
    else if (message.startsWith("MSG")) {
      //Send chat message to the ChannelPane
      logger.info("Recieved MSG");
      String messageContent = message.split("MSG ")[1];
      channelPane.receiveChatMessage(messageContent);
    } //User changed name to another username
    else if (message.startsWith("NICK")) {
      logger.info("Received NICK");
      //Instead of tackling each username, just refresh the userlist

    } //Start multiplayer game
    else if (message.startsWith("START")) {
      logger.info("Received START");
      if (InLobby) {
        Platform.runLater(this::startMultiplayerGame);
      }

    } //User left channel
    else if (message.startsWith("PARTED")) {
      logger.info("Received PARTED");
      Platform.runLater(() -> {
        InLobby = false;
        channelPane.setInLobby(false);
        showChannelPane(false);
      });

    } //List of users
    else if (message.startsWith("USERS")) {
      logger.info("Received USERS");
      setListOfUsers(message);

    } //You are the host of this lobby
    else if (message.startsWith("HOST")) {
      logger.info("Received HOST");
      channelPane.setIsHost(true);
    }
  }

  /**
   * Start the multiplayer game.
   */
  private void startMultiplayerGame() {
    gameWindow.startMultiplayerGame();

  }

  /**
   * Create a list of Channel Displays from a String containing all the channel names, separated by
   * new lines.
   *
   * @param channelNames String of all channel names.
   */
  private void createListOfChannels(String channelNames) {
    logger.info("Creating list of channel displays from " + channelNames);

    String[] channelNameArray = channelNames.split("\n");
    logger.info("Number of channels: " + channelNameArray.length);

    for (String channelName : channelNameArray) {
      logger.info("Channel: " + channelName);
      if (!channelName.isBlank()) {
        createChannelDisplay(channelName);
      }
    }
  }

  /**
   * Create a Channel Display for a given channel name.
   *
   * @param channelName String representing the channel name.
   */
  private void createChannelDisplay(String channelName) {
    ChannelDisplay channelDisplay = new ChannelDisplay(channelName, channelList.getWidth());
    channelDisplay.setOnButtonPressed(() -> communicator.send("JOIN " + channelName));

    Platform.runLater(() -> channelList.addChannelDisplay(channelDisplay));
  }

  /**
   * Clear all Channel Displays from the Channel List.
   */
  private void clearChannelDisplays() {
    channelList.clearChannelDisplays();
  }

  /**
   * Request a list of users from the server.
   */
  private void requestUsers() {
    communicator.send("USERS");
  }

  /**
   * Set the list of users for the Channel Pane from a message String.
   *
   * @param message message String.
   */
  private void setListOfUsers(String message) {
    String usersString = message.replaceFirst("USERS", "");
    usersString = usersString.trim();
    channelPane.setListOfUsers(usersString);
  }

  /**
   * Start the animated timer bar.
   */
  private void startTimerBar() {
    channelRequestTimerBar.startTimer(CHANNEL_REQUEST_DELAY);
  }

  /**
   * Restart the animated timer bar.
   */
  private void restartTimerBar() {
    channelRequestTimerBar.restartTimer(CHANNEL_REQUEST_DELAY);
  }

  /**
   * Start the timer for the calculated delay which, on completion, requests channels and restarts
   * the timer.
   */
  private void startTimer() {
    logger.info("Started new Timer for " + CHANNEL_REQUEST_DELAY + " milliseconds");
    timer = new Timer("ChannelRequestTimer");

    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {
          clearChannelDisplays();
          requestChannels(); //Request channels
          resetTimer(); //Restart timer
          restartTimerBar(); //Restart animated timer
        });
      }
    };
    timer.schedule(timerTask, CHANNEL_REQUEST_DELAY);
  }

  /**
   * Cancel, then start the timer.
   */
  private void resetTimer() {
    cancelTimer();
    if (InLobby) {
      return;
    }
    startTimer();
  }

  /**
   * Cancel the channel request timer.
   */
  private void cancelTimer() {
    logger.info("Timer cancelled");
    timer.cancel();
    timer.purge();
  }

  //////////////////////////////////////

  /**
   * Show or hide the Channel Pane. If showing, set the InLobby boolean to true and build the
   * Channel Pane for the current channel. If hiding, set the InLobby boolean to false, hide the
   * channel pane and restart the channel request timer.
   *
   * @param state boolean.
   */
  private void showChannelPane(boolean state) {
    if (state) {
      logger.info("Showing channel pane");
      Platform.runLater(() -> {
        channelPane.build(currentChannel);
        channelCenterPane.setVisible(true);
        channelPane.setVisible(true);
        channelPane.setInLobby(true);
      });

    } else {
      logger.info("Hiding channel pane");
      Platform.runLater(() -> {
        channelCenterPane.setVisible(false);
        channelPane.setVisible(false);
        channelPane.setInLobby(false);

        requestChannels();
        startTimer();
        startTimerBar();
      });

    }
  }

}
