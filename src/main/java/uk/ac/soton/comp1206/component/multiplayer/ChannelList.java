package uk.ac.soton.comp1206.component.multiplayer;

import java.util.ArrayList;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.ChannelListClearedListener;

/**
 * UI component to hold a list of Channel Displays.
 */
public class ChannelList extends VBox {

  private static final Logger logger = LogManager.getLogger(ChannelList.class);
  ArrayList<ChannelDisplay> channelDisplayArrayList = new ArrayList<>();
  ChannelListClearedListener clearedListener;

  /**
   * Construct a new Channel List with a given width.
   *
   * @param width double representing the channel list width.
   */
  public ChannelList(double width) {
    setWidth(width);
    setSpacing(10);
    //setPadding(new Insets(20, 20, 20, 20));

    getStyleClass().add("channelList");
  }

  /**
   * Add a new channel display to the array list and to the channel list's children.
   *
   * @param channelDisplay channel display to add.
   */
  public void addChannelDisplay(ChannelDisplay channelDisplay) {
    logger.info("Adding new display for " + channelDisplay.getChannelName());
    if (channelDisplayArrayList.contains(channelDisplay)) {
      logger.info("Display already exists for " + channelDisplay.getChannelName());
      return;
    }
    getChildren().add(channelDisplay);
    channelDisplayArrayList.add(channelDisplay);
  }

  /**
   * Clear the channel display array list and the channel list's children.
   */
  public void clearChannelDisplays() {
    logger.info("Clearing all ChannelDisplays");
    for (ChannelDisplay channelDisplay : channelDisplayArrayList) {
      logger.info("Removing ChannelDisplay for " + channelDisplay.getChannelName());
      getChildren().remove(channelDisplay);
    }
    channelDisplayArrayList.clear();

    clearedListener.channelListCleared();
  }

  /**
   * Handle an event when the channel list is cleared.
   *
   * @param listener channel cleared listener.
   */
  public void setOnChannelCleared(ChannelListClearedListener listener) {
    clearedListener = listener;
  }

  /**
   * Return if a channel name exists in the channel display array list.
   *
   * @param newChannelName String representing the channel name.
   * @return if a channel name exists.
   */
  public boolean ChannelNameExists(String newChannelName) {
    for (ChannelDisplay channelDisplay : channelDisplayArrayList) {
      String channelName = channelDisplay.getChannelName();
      if (channelName.equals(newChannelName)) {
        logger.info("Channel name already exists.");
        return true;
      }
    }
    logger.info("Channel name does not exist.");
    return false;
  }

}
