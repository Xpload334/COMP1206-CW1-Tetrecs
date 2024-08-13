package uk.ac.soton.comp1206.component.multiplayer;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.imageButtons.CandyButtonImageType;
import uk.ac.soton.comp1206.component.imageButtons.ImageButton;
import uk.ac.soton.comp1206.event.ImageButtonClickedListener;

/**
 * UI component to represent the side bar and user list of a lobby window. Contains a list of users,
 * a field/button to change your username, and a start game button.
 */
public class UserList extends VBox {

  private static final Logger logger = LogManager.getLogger(UserList.class);
  private ImageButton slideOutButton;
  double width = 200;
  private VBox users;
  private TextField usernameField;
  ImageButton usernameChangeButton;
  ImageButton startMultiplayerGameButton;
  boolean visible;
  long animationDuration = 250;
  boolean IsHost;

  /**
   * Construct a new User List, enabling the start game button if the user is the host.
   *
   * @param IsHost boolean representing if the user is the host or not.
   */
  public UserList(boolean IsHost) {
    setPrefWidth(width);
    setSpacing(20);
    setPadding(new Insets(10, 10, 10, 10));
    getStyleClass().add("gameBox");
    setAlignment(Pos.TOP_CENTER);

    visible = true;

    build(IsHost);
  }

  /**
   * Build the user list. Enable or disable the start game button if the user is the host.
   * @param IsHost boolean representing if the user is the host or not.
   */
  public void build(boolean IsHost) {
    this.IsHost = IsHost;

    //Add image button
    slideOutButton = new ImageButton(CandyButtonImageType.LEVELLIST, 64, 64);
    GentleShakeAnimation.startAnimation(slideOutButton);
    slideOutButton.setPreserveRatio(true);
    slideOutButton.setButtonClickedListener(this::toggleSidebar);
    slideOutButton.setAlignment(Pos.CENTER_LEFT);
    getChildren().add(slideOutButton);

    //Add username field
    var nameChangeHBox = new HBox(5);
    getChildren().add(nameChangeHBox);

    usernameField = new TextField();
    usernameField.setPromptText("Username...");
    usernameField.setAlignment(Pos.CENTER);
    nameChangeHBox.getChildren().add(usernameField);

    //Add change username button
    usernameChangeButton = new ImageButton(CandyButtonImageType.RELOAD, 25, 25);
    GentleShakeAnimation.startAnimation(usernameChangeButton);
    nameChangeHBox.getChildren().add(usernameChangeButton);

    //Add list of users
    users = new VBox();
    users.setSpacing(20);
    users.setPadding(new Insets(10, 10, 10, 10));

    //Add scroll pane for the list of users
    ScrollPane scroller = new ScrollPane();
    scroller.setContent(users);
    scroller.setFitToWidth(true);
    scroller.getStyleClass().add("scroller");
    getChildren().add(scroller);

    //Add start game button
    buildStartGameButton(IsHost);
  }

  /**
   * Build the start game button. Enable or disable it depending on if the user is the host or not.
   * @param IsHost boolean representing if the user is the host or not.
   */
  private void buildStartGameButton(boolean IsHost) {
    startMultiplayerGameButton = new ImageButton(CandyButtonImageType.PLAY, 64, 64);
    GentleShakeAnimation.startAnimation(startMultiplayerGameButton);
    getChildren().add(startMultiplayerGameButton);
    setStartGameActive(IsHost);
  }

  /**
   * Enable or disable the start game button depending on if the user is the host or not.
   * @param IsHost boolean representing if the user is the host or not.
   */
  private void setStartGameActive(boolean IsHost) {
    if(IsHost) {
      logger.info("Setting game button active");
      startMultiplayerGameButton.setActive(true);
    }
    else {
      logger.info("Setting game button inactive");
      startMultiplayerGameButton.setActive(false);
    }
  }

  /**
   * Return the contents of the username text field.
   *
   * @return String representing the username
   */
  public String getUsernameField() {
    return usernameField.getText();
  }

  /**
   * Set the list of users from a string of all users.
   *
   * @param allUsers String with all users separated by new lines.
   */
  public void setUsers(String allUsers) {
    logger.info("Setting list of Users: " + allUsers);

    Platform.runLater(() -> {
      clearUsers();

      String[] users = allUsers.split("\n");
      for (String user : users) {
        if (!user.isEmpty()) {
          addUser(user);
        }
      }
    });
  }

  /**
   * Add a user to the user list, with an icon and text of their username
   *
   * @param username string with the username.
   */
  public void addUser(String username) {
    logger.info("Adding new User: " + username);
    var userBox = new HBox();
    var userImage = new ImageView(new Image(
        getClass().getResource("/images/custom/candyButtons/Profile (1).png").toExternalForm()));
    userImage.setPreserveRatio(true);
    userImage.setFitHeight(16);

    userBox.getChildren().add(userImage);
    userBox.getStyleClass().add("gameBox");
    userBox.setSpacing(10);
    HBox.setHgrow(userImage, Priority.NEVER);

    var user = new Text(username);
    user.getStyleClass().add("playerBox");
    user.setFill(Color.WHITE);
    userBox.getChildren().add(user);
    HBox.setHgrow(user, Priority.ALWAYS);

    users.getChildren().add(userBox);
  }

  /**
   * Clear the children of the user list.
   */
  private void clearUsers() {
    logger.info("Clearing list of Users");
    users.getChildren().clear();
  }

  /**
   * Toggle whether the sidebar should show or not
   */
  private void toggleSidebar() {
    if (visible) {
      hideSidebar();
    } else {
      showSidebar();
    }
  }

  /**
   * Animate the sidebar sliding out
   */
  void hideSidebar() {
    visible = false;
    for (var child : getChildren()) {
      //Don't hide the top button
      if (child instanceof ImageButton) {
        ImageButton button = (ImageButton) child;
        logger.info("ImageButton has type "+button.getButtonImageType());
        if(button.getButtonImageType() == CandyButtonImageType.LEVELLIST) {
          continue;
        }
      }
      child.setVisible(false);
    }
    Duration duration = Duration.millis(animationDuration);
    Timeline timeline = new Timeline(
        new KeyFrame(duration, new KeyValue(this.prefWidthProperty(), 50, Interpolator.EASE_BOTH))
    );
    timeline.play();
  }

  /**
   * Animate the sidebar sliding in
   */
  void showSidebar() {
    visible = true;
    Duration duration = Duration.millis(animationDuration);
    Timeline timeline = new Timeline(
        new KeyFrame(duration,
            new KeyValue(this.prefWidthProperty(), width, Interpolator.EASE_BOTH))
    );
    timeline.play();
    timeline.setOnFinished(actionEvent -> {
      for (var child : getChildren()) {
        child.setVisible(true);
      }
    });
  }

  /**
   * Set the event to handle when the username change button is pressed.
   *
   * @param listener listener.
   */
  public void setOnUsernameChangeButton(ImageButtonClickedListener listener) {
    usernameChangeButton.setButtonClickedListener(listener);
  }

  /**
   * Set the event to handle when the start game button is pressed.
   *
   * @param listener listener.
   */
  public void setOnStartGameButton(ImageButtonClickedListener listener) {
    startMultiplayerGameButton.setButtonClickedListener(listener);
  }

  /**
   * Set the boolean for if user is the host of the channel.
   *
   * @param state boolean if the user is the host of the channel.
   */
  public void setIsHost(boolean state) {
    IsHost = state;
  }

  /**
   * Update the start game button for the host status.
   */
  public void updateStartGameButton() {
    logger.info("Updating StartGameButton");
    Platform.runLater(() -> {
      setStartGameActive(IsHost);
    });
  }

}