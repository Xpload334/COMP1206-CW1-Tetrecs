package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.animations.GentleShakeAnimation;
import uk.ac.soton.comp1206.component.imageButtons.CandyButtonImageType;
import uk.ac.soton.comp1206.component.settings.BlockControlSlider;
import uk.ac.soton.comp1206.component.imageButtons.ImageButton;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Settings Scene of the game. Allows the user to change music/sound effect volumes, or disable them
 * completely.
 */
public class SettingsScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
  double menuButtonSpacing = 10;
  double buttonSize = 80;
  double controlSpacing = 10;
  /**
   * Sounds
   */
  private BlockControlSlider soundsSlider;
  double maxSoundVolume = 0.5;
  /**
   * Music
   */
  private BlockControlSlider musicSlider;
  double maxMusicVolume = 0.5;

  /**
   * Create a new settings scene, passing in the GameWindow the scene will be displayed in.
   *
   * @param gameWindow the game window.
   */
  public SettingsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating settings scene");
  }

  /**
   * Build the layout of the scene
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    //Stack pane
    var settingsPane = new StackPane();
    settingsPane.setMaxWidth(gameWindow.getWidth());
    settingsPane.setMaxHeight(gameWindow.getHeight());
    settingsPane.getStyleClass().add("instructions-background");
    root.getChildren().add(settingsPane);

    //Main pane
    var mainPane = new BorderPane();
    mainPane.setMaxSize(gameWindow.getWidth(), gameWindow.getHeight());
    settingsPane.getChildren().add(mainPane);

    //Title
    var titleHBox = new HBox();
    titleHBox.setAlignment(Pos.CENTER);
    mainPane.setTop(titleHBox);

    var titleText = new Text("Settings");
    titleText.getStyleClass().add("title");
    titleText.setTextAlignment(TextAlignment.CENTER);
    titleHBox.getChildren().add(titleText);

    //Menu Buttons
    VBox menuButtons = new VBox(menuButtonSpacing);
    menuButtons.setAlignment(Pos.CENTER);
    mainPane.setCenter(menuButtons);

    //Sounds HBox
    HBox soundsHBox = buildSoundsHBox();
    menuButtons.getChildren().add(soundsHBox);

    //Music HBox
    HBox musicHBox = buildMusicHBox();
    menuButtons.getChildren().add(musicHBox);
  }

  /**
   * Initialise this scene. Called after creation
   */
  @Override
  public void initialise() {
    logger.info("Initialising Settings");

    scene.setOnKeyPressed((e) -> {
      if (e.getCode() == KeyCode.ESCAPE) {
        gameWindow.startMenu();
      }
    });
  }

  /**
   * Build the sounds HBox, with a ImageButton to toggle sounds and a Block Control Slider to change
   * the sound volume. The ImageButtons of the Slider won't play a sound effect when pressed, but
   * will play an updated sound effect to show the new sound volume.
   *
   * @return sounds HBox.
   */
  private HBox buildSoundsHBox() {
    HBox soundsHBox = new HBox(controlSpacing);
    soundsHBox.setAlignment(Pos.CENTER);

    //Sounds image button
    ImageButton soundsImageButton = new ImageButton(CandyButtonImageType.SOUND, buttonSize, buttonSize);
    soundsHBox.getChildren().add(soundsImageButton);
    GentleShakeAnimation.startAnimation(soundsImageButton);

    //Sounds slider
    soundsSlider = new BlockControlSlider(5, 0, maxSoundVolume, 5);
    soundsSlider.setCurrentValue(Multimedia.getSoundVolume());
    soundsSlider.setActive(Multimedia.isSoundsEnabled());
    soundsHBox.getChildren().add(soundsSlider);

    //Plus and minus buttons don't have default press sound, changed to play once the volume is updated
    soundsSlider.getPlusButton().setHasPressSound(false);
    soundsSlider.getMinusButton().setHasPressSound(false);

    soundsSlider.setOnSliderValueChanged(value -> {
      Multimedia.setSoundVolume(value);
      Multimedia.buttonPress(); //Play changed volume sound effect
    });

    //On button clicked
    soundsImageButton.setButtonClickedListener(() -> {
      toggleSounds();
      soundsSlider.toggleActive();
    });

    return soundsHBox;
  }

  /**
   * Build the music HBox, with a ImageButton to toggle music and a Block Control Slider to change
   * the music volume.
   *
   * @return music HBox.
   */
  private HBox buildMusicHBox() {
    HBox musicHBox = new HBox(controlSpacing);
    musicHBox.setAlignment(Pos.CENTER);

    //Music image button
    ImageButton musicImageButton = new ImageButton(CandyButtonImageType.MUSIC, buttonSize, buttonSize);
    musicHBox.getChildren().add(musicImageButton);

    GentleShakeAnimation.startAnimation(musicImageButton);

    //Sounds slider
    musicSlider = new BlockControlSlider(5, 0, maxMusicVolume, 5);
    musicSlider.setCurrentValue(Multimedia.getMusicVolume());
    musicSlider.setActive(Multimedia.isMusicEnabled());
    musicHBox.getChildren().add(musicSlider);
    //Multimedia.getMusicProperty().bind(musicSlider.getDoubleProperty());
    musicSlider.setOnSliderValueChanged(Multimedia::setMusicVolume);

    //On button clicked
    musicImageButton.setButtonClickedListener(() -> {
      toggleMusic();
      musicSlider.toggleActive();
    });

    return musicHBox;
  }

  /**
   * Toggle whether music is enabled in the Multimedia class.
   */
  private void toggleMusic() {
    if (Multimedia.isMusicEnabled()) {
      //Stop music
      Multimedia.setMusicEnabled(false);
      Multimedia.stopMusic();

    } else {
      //Start music
      Multimedia.setMusicEnabled(true);
      Multimedia.menuMusic();
    }
  }

  /**
   * Toggle whether sounds are enabled in the Multimedia class.
   */
  private void toggleSounds() {
    if (Multimedia.isSoundsEnabled()) {
      Multimedia.setSoundsEnabled(false);
    } else {
      Multimedia.setSoundsEnabled(true);
      Multimedia.buttonPress(); //Play button sound anyway
    }

  }
}
