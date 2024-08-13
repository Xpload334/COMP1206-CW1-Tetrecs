package uk.ac.soton.comp1206.component.multiplayer;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.imageButtons.CandyButtonImageType;
import uk.ac.soton.comp1206.component.imageButtons.ImageButton;
import uk.ac.soton.comp1206.event.ImageButtonClickedListener;

/**
 * UI component representing the sidebar of the LobbyScene, to show buttons for creating a new
 * channel.
 */
public class NewLobbyMenu extends VBox {

  /**
   * Creating New Lobbies
   */
  private final TextField newLobbyTextField;
  private final ImageButton createNewLobbyButton;
  boolean CanMakeLobby = false;

  /**
   * Construct a New Lobby Menu.
   */
  public NewLobbyMenu() {
    getStyleClass().add("gameBox");
    setSpacing(30);
    setPadding(new Insets(20, 20, 20, 20));

    var newLobbyTitle = new Text("Create Game");
    newLobbyTitle.getStyleClass().add("title");
    getChildren().add(newLobbyTitle);

    newLobbyTextField = new TextField();
    newLobbyTextField.setPromptText("Set Game Name...");
    getChildren().add(newLobbyTextField);

    createNewLobbyButton = new ImageButton(CandyButtonImageType.UPLOAD, 100, 100);
    GentleShakeAnimation.startAnimation(createNewLobbyButton);
    getChildren().add(createNewLobbyButton);
  }

  /**
   * Set the event to handle when the create channel button is pressed.
   *
   * @param listener listener
   */
  public void setOnCreatePressed(ImageButtonClickedListener listener) {
    createNewLobbyButton.setButtonClickedListener(listener);
  }

  /**
   * Return the contents of the text field.
   *
   * @return String representing the channel name.
   */
  public String getChannelName() {
    return newLobbyTextField.getText();
  }

  /**
   * Clear the contents of the lobby name text field.
   */
  public void clearTextField() {
    newLobbyTextField.clear();
  }

}
