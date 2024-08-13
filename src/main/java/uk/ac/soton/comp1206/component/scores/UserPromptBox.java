package uk.ac.soton.comp1206.component.scores;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.imageButtons.CandyButtonImageType;
import uk.ac.soton.comp1206.component.imageButtons.ImageButton;
import uk.ac.soton.comp1206.event.KeyPressedListener;
import uk.ac.soton.comp1206.event.NameSubmittedListener;

/**
 * User Interface for a prompt for users to enter their name and submit their game score.
 */
public class UserPromptBox extends StackPane {

  private static final Logger logger = LogManager.getLogger(UserPromptBox.class);
  double width;
  double height;
  TextField usernameField;
  boolean IsActive;
  /**
   * Name Submitted Listener
   */
  public NameSubmittedListener nameSubmittedListener;
  /**
   * Key Pressed Listener
   */
  public KeyPressedListener keyPressedListener;

  /**
   * Construct a User Prompt Box with a given width and height.
   * <p>
   * The box has a TextField for entering username and a button to submit.
   *
   * @param width  prompt box width
   * @param height prompt box height
   */
  public UserPromptBox(double width, double height) {
    this.width = width;
    this.height = height;
    setActive(true);

    build();
  }

  /**
   * Build the user prompt box with a title, username field and submit score button.
   */
  public void build() {
    logger.info("Building new User Prompt Box");
    setPrefSize(width, height);
    setAlignment(Pos.CENTER);

    //Create VBox for contents of the prompt box
    var userBoxContents = new VBox();
    userBoxContents.setPrefSize(width, height);
    userBoxContents.getStyleClass().add("gameBox");
    userBoxContents.setAlignment(Pos.CENTER);
    getChildren().add(userBoxContents);

    //Create text title
    var title = new Text("Submit Your Score!");
    title.getStyleClass().add("channelItem");
    userBoxContents.getChildren().add(title);

    //Create a horizontal bar with a text box and send button
    usernameField = new TextField();
    usernameField.setPromptText("Your Name...");
    ;
    ImageButton submitImageButton = new ImageButton(CandyButtonImageType.UPLOAD, 50, 50);
    GentleShakeAnimation.startAnimation(submitImageButton);

    HBox sendUsername = new HBox();
    sendUsername.setAlignment(Pos.CENTER);
    sendUsername.getChildren().add(usernameField);
    sendUsername.getChildren().add(submitImageButton);
    userBoxContents.getChildren().add(sendUsername);

    //Add button event
    //submit.setOnAction(actionEvent -> submitName());
    submitImageButton.setButtonClickedListener(this::submitName);
  }

  /**
   * Set the name submitted listener.
   *
   * @param listener name submitted listener
   */
  public void setOnNameSubmitted(NameSubmittedListener listener) {
    this.nameSubmittedListener = listener;
  }

  /**
   * Set if the user prompt box is active or not.
   *
   * @param active boolean representing whether the prompt box is active or not
   */
  public void setActive(boolean active) {
    if (active) {
      logger.info("User Prompt Box enabled");
      setVisible(true);
      IsActive = true;
    } else {
      logger.info("User Prompt Box disabled");
      setVisible(false);
      IsActive = false;
    }
  }

  /**
   * Submit the username (contents of the username field) to the name submitted listener.
   */
  public void submitName() {
    String username = usernameField.textProperty().getValue();
    if (username.isEmpty() || !IsActive) {
      return;
    }

    username.replaceAll(":", " "); //Replace all colons with spaces, to prevent score reading errors

    //If all is good
    nameSubmittedListener.nameSubmitted(username);
  }

  /**
   * Set the key pressed listener.
   *
   * @param listener key pressed listener.
   */
  public void setKeyPressedListener(KeyPressedListener listener) {
    keyPressedListener = listener;
  }
}
