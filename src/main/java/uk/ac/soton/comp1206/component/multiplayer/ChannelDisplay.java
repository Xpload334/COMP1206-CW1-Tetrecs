package uk.ac.soton.comp1206.component.multiplayer;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.imageButtons.CandyButtonImageType;
import uk.ac.soton.comp1206.component.imageButtons.ImageButton;
import uk.ac.soton.comp1206.event.ImageButtonClickedListener;

/**
 * UI to show a channel's name in a box, with a button to join this channel.
 */
public class ChannelDisplay extends HBox {

  private static final Logger logger = LogManager.getLogger(ChannelDisplay.class);
  private final String channelName;
  private final ImageButton joinChannelButton;

  /**
   * Construct a new Channel Display for the given channel name, with a given width.
   *
   * @param channelName String representing the name of the channel.
   * @param width       double representing the channel display width
   */
  public ChannelDisplay(String channelName, double width) {
    this.channelName = channelName;

    setWidth(width);
    setMaxWidth(width);
    double height = 50;
    setHeight(height);
    setSpacing(30);
    getStyleClass().add("gameBox");

    //Join image button
    joinChannelButton = new ImageButton(CandyButtonImageType.PLUS, 50, 50);
    GentleShakeAnimation.startAnimation(joinChannelButton);
    getChildren().add(joinChannelButton);
    joinChannelButton.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(joinChannelButton, Priority.ALWAYS);

    //Name of channel on right
    Text nameText = new Text();
    SimpleStringProperty nameProperty = new SimpleStringProperty();
    nameText.textProperty().bind(nameProperty);
    nameProperty.set(channelName);
    nameText.getStyleClass().add("channelItem");
    nameText.setTextAlignment(TextAlignment.CENTER);
    //HBox.setHgrow(joinChannelButton, Priority.NEVER);
    getChildren().add(nameText);

  }

  /**
   * Set the event to handle when the join button is pressed.
   *
   * @param listener listener.
   */
  public void setOnButtonPressed(ImageButtonClickedListener listener) {
    joinChannelButton.setButtonClickedListener(listener);
  }

  /**
   * Return the name of the displayed channel.
   *
   * @return String representing the name of the channel.
   */
  public String getChannelName() {
    return channelName;
  }
}
