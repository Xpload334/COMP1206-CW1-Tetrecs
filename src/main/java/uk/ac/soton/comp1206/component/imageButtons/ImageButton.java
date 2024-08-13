package uk.ac.soton.comp1206.component.imageButtons;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.event.ImageButtonClickedListener;

/**
 * UI component for an Image Button, an ImageView with changing images that has the same
 * functionality as a button.
 */
public class ImageButton extends HBox {

  private static final Logger logger = LogManager.getLogger(ImageButton.class);
  boolean HasPressSound = true;
  boolean HasHighlightSound = true;
  CandyButtonImages candyButtonImages = new CandyButtonImages();
  private final CandyButtonImageType candyButtonImageType;
  private ArrayList<Image> images = new ArrayList<>();
  private final ImageView imageView = new ImageView();
  private ImageButtonClickedListener buttonClickedListener;

  /**
   * State of this Image Button.
   */
  private enum ImageButtonState {
    NORMAL,
    HIGHLIGHTED,
    CLICKED,
    INACTIVE
  }

  private ImageButtonState currentState;
  private static final int NORMAL_INDEX = 0;
  private static final int HIGHLIGHTED_INDEX = 1;
  private static final int PRESSED_INDEX = 2;
  private static final int INACTIVE_INDEX = 3;


  /**
   * Construct a new Image Button from a given ButtonImageType, width and height.
   *
   * @param candyButtonImageType image type of this Image Button
   * @param width           width
   * @param height          height
   */
  public ImageButton(CandyButtonImageType candyButtonImageType, double width, double height) {
    logger.info("Building new image button " + candyButtonImageType);
    setWidth(width);
    setHeight(height);
    setAlignment(Pos.CENTER);

    imageView.setPreserveRatio(true);
    imageView.setFitWidth(width);
    getChildren().add(imageView);

    this.candyButtonImageType = candyButtonImageType;
    build(candyButtonImageType);
  }

  /**
   * Build this Image Button and assign the correct image set from the ButtonImageType.
   *
   * @param candyButtonImageType image type of this Image Button
   */
  private void build(CandyButtonImageType candyButtonImageType) {
    setImages(candyButtonImageType); //Set image set to correct set

    setButtonPressedEvent();

    //Set mouse entered event
    imageView.setOnMouseEntered(event -> {
      logger.info("Mouse entered");
      if (currentState == ImageButtonState.NORMAL) {
        setCurrentState(ImageButtonState.HIGHLIGHTED);
        setImage();
        getScene().setCursor(Cursor.HAND); //Set cursor
        if (HasHighlightSound) {
          Multimedia.buttonHighlight(); //Play sound
        }

      }
    });
    //Set mouse exited event
    imageView.setOnMouseExited(event -> {
      logger.info("Mouse exited");
      if (currentState == ImageButtonState.HIGHLIGHTED) {
        setCurrentState(ImageButtonState.NORMAL);
        setImage();
        getScene().setCursor(Cursor.DEFAULT); //Set cursor
      }
    });

    //Set mouse pressed event
    imageView.setOnMousePressed(event -> {
      logger.info("Mouse pressed");
      if (currentState == ImageButtonState.HIGHLIGHTED) {
        setCurrentState(ImageButtonState.CLICKED);
        setImage();
        if (HasPressSound) {
          Multimedia.buttonPress(); //Play sound
        }

      }
    });
    //Set mouse released event
    imageView.setOnMouseReleased(event -> {
      logger.info("Mouse released");
      if (currentState == ImageButtonState.CLICKED) {
        setCurrentState(ImageButtonState.HIGHLIGHTED);
        setImage();
        //getScene().setCursor(Cursor.DEFAULT);
      }
    });

    initialise();
  }

  /**
   * Initialise this Image Button
   */
  public void initialise() {
    setCurrentState(ImageButtonState.NORMAL);
    setImage(NORMAL_INDEX); //Set to normal image initially
  }

  /**
   * Handle an event when this Image Button is clicked.
   */
  private void setButtonPressedEvent() {
    imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
      if (currentState != ImageButtonState.INACTIVE) {
        buttonClickedListener.buttonClicked();
      }
    });

  }

  /**
   * Set the button clicked listener.
   *
   * @param listener listener.
   */
  public void setButtonClickedListener(ImageButtonClickedListener listener) {
    buttonClickedListener = listener;
  }

  /**
   * Set whether to preserve the image ratio of this Image Button
   *
   * @param b boolean
   */
  public void setPreserveRatio(boolean b) {
    imageView.setPreserveRatio(b);
  }

  /**
   * Set whether this Image Button is active or not. If inactive, disable this button.
   *
   * @param active boolean.
   */
  public void setActive(boolean active) {
    if (!active) {
      setCurrentState(ImageButtonState.INACTIVE);
      setImage();
    } else {
      setCurrentState(ImageButtonState.NORMAL);
      setImage();
    }
  }

  /**
   * Set the image of this Image Button based on the current state.
   */
  private void setImage() {
    switch (currentState) {
      case NORMAL -> setImage(NORMAL_INDEX);
      case HIGHLIGHTED -> setImage(HIGHLIGHTED_INDEX);
      case CLICKED -> setImage(PRESSED_INDEX);
      case INACTIVE -> setImage(INACTIVE_INDEX);
    }
  }

  /**
   * Set the image of this Image Button from a given index.
   *
   * @param index index of the image in the images ArrayList.
   */
  private void setImage(int index) {
    imageView.setImage(images.get(index));
  }

  /**
   * Set the image set of this Image Button from its ButtonImageType.
   *
   * @param candyButtonImageType image type of this ImageButton.
   */
  private void setImages(CandyButtonImageType candyButtonImageType) {
    switch (candyButtonImageType) {
      case HELP -> images = CandyButtonImages.Help;
      case LEVELLIST -> images = CandyButtonImages.LevelList;
      case MINUS -> images = CandyButtonImages.Minus;
      case MUSIC -> images = CandyButtonImages.Music;
      case MULTIPLAYER -> images = CandyButtonImages.Multiplayer;
      case PLAY -> images = CandyButtonImages.Play;
      case PLUS -> images = CandyButtonImages.Plus;
      case PROFILE -> images = CandyButtonImages.Profile;
      case OPTIONS -> images = CandyButtonImages.Options;
      case RIGHTARROW -> images = CandyButtonImages.RightArrow;
      case RELOAD -> images = CandyButtonImages.Reload;
      case SOUND -> images = CandyButtonImages.Sound;
      case UPLOAD -> images = CandyButtonImages.Upload;
    }
  }

  /**
   * Set the current state of this Image Button.
   *
   * @param currentState current ImageButtonState
   */
  public void setCurrentState(
      ImageButtonState currentState) {
    this.currentState = currentState;
  }

  /**
   * Set whether or not this Image Button plays a sound when clicked.
   *
   * @param hasPressSound boolean.
   */
  public void setHasPressSound(boolean hasPressSound) {
    HasPressSound = hasPressSound;
  }

  /**
   * Set whether or not this Image Button plays a sound when highlighted.
   *
   * @param hasHighlightSound boolean.
   */
  public void setHasHighlightSound(boolean hasHighlightSound) {
    HasHighlightSound = hasHighlightSound;
  }

  /**
   * Return the ButtonImageType of this Image Button.
   *
   * @return ButtonImageType.
   */
  public CandyButtonImageType getButtonImageType() {
    return candyButtonImageType;
  }
}
