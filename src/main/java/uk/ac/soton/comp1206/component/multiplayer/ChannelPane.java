package uk.ac.soton.comp1206.component.multiplayer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.imageButtons.CandyButtonImageType;
import uk.ac.soton.comp1206.component.imageButtons.ImageButton;
import uk.ac.soton.comp1206.network.Communicator;

/**
 * UI to represent the pane to open for the channel chat.
 */
public class ChannelPane extends BorderPane {

  private static final Logger logger = LogManager.getLogger(ChannelPane.class);
  boolean InLobby = false;
  boolean IsHost = false;
  boolean scrollToBottom;
  Communicator communicator;
  TextField messageToSend;
  ScrollPane scrollPane;
  VBox messages;
  UserList userList;

  /**
   * Construct a new Channel Pane, with a given communicator.
   *
   * @param communicator communicator class to use.
   */
  public ChannelPane(Communicator communicator) {
    this.communicator = communicator;

    getStyleClass().add("gameBox");
  }

  /**
   * Build the channel pane for the given channel name.
   *
   * @param channelName String representing the channel name.
   */
  public void build(String channelName) {
    buildTitleText(channelName);

    //Create a horizontal bar with a text box and send button
    ImageButton sendMessage = new ImageButton(CandyButtonImageType.UPLOAD, 35, 35);
    GentleShakeAnimation.startAnimation(sendMessage);

    HBox sendMessageBar = new HBox();
    messageToSend = new TextField();
    messageToSend.setPromptText("Send a message...");
    sendMessageBar.getChildren().add(messageToSend);
    HBox.setHgrow(messageToSend, Priority.ALWAYS);
    messageToSend.setPrefHeight(sendMessage.getHeight());

    sendMessageBar.setAlignment(Pos.CENTER_LEFT);
    sendMessageBar.getChildren().add(sendMessage);
    setBottom(sendMessageBar);

    //Send message when button pressed
    sendMessage.setButtonClickedListener(() -> this.sendCurrentMessage(messageToSend.getText()));
    //Send message when enter pressed
    messageToSend.setOnKeyPressed((event) -> {
      if (event.getCode() != KeyCode.ENTER) {
        return;
      }
      sendCurrentMessage(messageToSend.getText());
    });

    //Create a textflow to hold all messages
    messages = new VBox(5);
    messages.setPadding(new Insets(20, 20, 20, 20));
    //messages.getStyleClass().add("messages");

    //Add a scrollpane
    scrollPane = new ScrollPane();
    scrollPane.setFitToWidth(true);
    scrollPane.setContent(messages);
    scrollPane.getStyleClass().add("scroller");
    setCenter(scrollPane);

    buildUserList();
  }

  /**
   * Helper method to build a user list, and set the actions of the username change and start game
   * buttons
   */
  private void buildUserList() {
    //Add side bar
    userList = new UserList(IsHost);
    userList.setOnUsernameChangeButton(() -> {
      String newUsername = userList.getUsernameField();
      if (!newUsername.isEmpty()) {
        logger.info("Sending change username message to " + newUsername);
        communicator.send("NICK " + newUsername);
        receiveChatMessage("### Name successfully changed to " + newUsername + "! ###");
      }
    });
    userList.setOnStartGameButton(() -> {
      if (IsHost) {
        logger.info("Sending start multiplayer game message");
        communicator.send("START");
      }
    });

    setRight(userList);
  }

  /**
   * Helper method to build the title text for the channel pane.
   *
   * @param channelName String representing the channel name.
   */
  private void buildTitleText(String channelName) {
    var titleText = new Text(channelName);
    titleText.setTextAlignment(TextAlignment.CENTER);
    titleText.getStyleClass().add("heading");

    setTop(titleText);
  }

  /**
   * Send an outgoing message from the channel pane chat.
   *
   * @param text String representing text of the message to send to the Communicator.
   */
  private void sendCurrentMessage(String text) {
    //Ignore empty message
    if (text.isEmpty()) {
      return;
    }

    //Send message to communicator
    String message = "MSG " + text;
    communicator.send(message);

    //Clear the text input box
    messageToSend.clear();
  }

  /**
   * Receive a chat message and add it to the list of messages. Scroll to the bottom of the scroller
   * if necessary, and play the message sound effect.
   *
   * @param messageContent String with the content of the message.
   */
  public void receiveChatMessage(String messageContent) {
    Platform.runLater(() -> {
      addMessage(messageContent);
      //Scroll to bottom if needed
      if (scrollPane.getVvalue() == 0.0f || scrollPane.getVvalue() > 0.9f) {
        scrollToBottom = true;
      }
      //Jump to bottom, if able
      jumpToBottom();

      Multimedia.message(); //Play sound effect
    });
  }

  /**
   * Add a message to the list of messages.
   *
   * @param messageContent String with the content of the message.
   */
  private void addMessage(String messageContent) {
    var text = new Text(messageContent);
    text.getStyleClass().add("messages");
    text.setFill(Color.WHITE);
    text.setWrappingWidth(scrollPane.getWidth() - 50);

    messages.getChildren().add(text);
  }

  /**
   * Move the scroller to the bottom
   */
  private void jumpToBottom() {
    if (!scrollToBottom) {
      return;
    }
    scrollPane.setVvalue(1.0f);
    scrollToBottom = false;
  }

  /**
   * Send a message to the communicator to leave the channel.
   */
  public void leaveChannel() {
    communicator.send("PART");
  }

  ////////////////////////

  /**
   * Set the list of users from a string of all users, separated by new lines.
   *
   * @param usersString String of all users in the channel.
   */
  public void setListOfUsers(String usersString) {
    userList.setUsers(usersString);
  }

  /**
   * Set the boolean if the user is in a lobby.
   *
   * @param inLobby boolean representing if the user is in a lobby.
   */
  public void setInLobby(boolean inLobby) {
    InLobby = inLobby;
  }

  /**
   * Return if the user is in a lobby.
   *
   * @return boolean representing if the user is in a lobby.
   */
  public boolean isInLobby() {
    return InLobby;
  }

  /**
   * Set the boolean if the user is the host of this lobby. If true, update the user list to allow
   * game starting.
   *
   * @param isHost boolean representing if the user is the host of this lobby.
   */
  public void setIsHost(boolean isHost) {
    logger.info("Setting is host = " + isHost);
    IsHost = isHost;
    userList.setIsHost(true);
    userList.updateStartGameButton(); //Update start game button
  }

  /**
   * Return if the user is the host of this lobby.
   *
   * @return boolean representing if the user is the host of this lobby.
   */
  public boolean isHost() {
    return IsHost;
  }
}
