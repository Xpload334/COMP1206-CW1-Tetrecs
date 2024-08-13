package uk.ac.soton.comp1206;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Multimedia class is used to store and play various sound effects and music. It contains two
 * MediaPlayers to play sounds and music respectively.
 * <p>
 * All custom sound effects and music are taken from https://freesound.org/ and all sounds there are
 * Creative Commons Licensed.
 */
public class Multimedia {

  private static final Logger logger = LogManager.getLogger(Multimedia.class);

  /**
   * Sound Media Player
   */
  public static MediaPlayer soundPlayer;
  /**
   * Music Media Player
   */
  public static MediaPlayer musicPlayer;

  /**
   * Boolean for whether music is enabled.
   */
  private static boolean musicEnabled = true;
  /**
   * Boolean for whether sounds are enabled.
   */
  private static boolean soundsEnabled = true;
  /**
   * Double to represent sound volume
   */
  public static double soundVolume = 0.5;
  /**
   * Double to represent music volume
   */
  public static double musicVolume = 0.5;

  /**
   * Construct a new Multimedia class.
   */
  public Multimedia() {
    musicPlayer.setVolume(soundVolume);
    soundPlayer.setVolume(musicVolume);
  }

  /**
   * Play a music file from the audio player.
   *
   * @param file string representing the relative path (from project root) of the file
   */
  public static void playMusic(String file) {
    playMusic(file, file);
  }

  /**
   * Play a music file from the audio player. PLay a second music file after the first finishes.
   *
   * @param file     string representing the relative path (from project root) of the file
   * @param afterEnd string representing the relative path (from project root) of the music file to
   *                 play after the first is finished.
   */
  public static void playMusic(String file, String afterEnd) {
    if (!musicEnabled) {
      return;
    }

    String toPlay = Multimedia.class.getResource("/" + file).toExternalForm();
    logger.info("Playing music: " + toPlay);

    try {
      Media play = new Media(toPlay);
      musicPlayer = new MediaPlayer(play);
      musicPlayer.setVolume(musicVolume);
      musicPlayer.play();

      musicPlayer.setOnEndOfMedia(new Runnable() {
        @Override
        public void run() {
          playMusic(afterEnd);
        }
      });


    } catch (Exception e) {
      musicEnabled = false;
      e.printStackTrace();
      logger.error("Unable to play music file, disabling music");
    }
  }

  /**
   * Play a sound file from the audio player
   *
   * @param file string representing the relative path (from project root) of the file
   */
  public static void playSound(String file) {
    if (!soundsEnabled) {
      return;
    }

    String toPlay = Multimedia.class.getResource("/" + file).toExternalForm();
    logger.info("Playing sound: " + toPlay);

    try {
      Media play = new Media(toPlay);
      soundPlayer = new MediaPlayer(play);
      soundPlayer.setVolume(soundVolume);
      soundPlayer.play();
    } catch (Exception e) {
      soundsEnabled = false;
      e.printStackTrace();
      logger.error("Unable to play sound file, disabling sounds");
    }
  }

  /**
   * Stop any currently playing music file.
   */
  public static void stopMusic() {
    if (musicPlayer == null || musicPlayer.getStatus() != Status.PLAYING) {
      return;
    }
    musicPlayer.stop();
  }

  /**
   * Return if sounds are enabled.
   *
   * @return boolean.
   */
  public static boolean isSoundsEnabled() {
    return soundsEnabled;
  }

  /**
   * Set whether sounds are enabled.
   *
   * @param soundsEnabled boolean.
   */
  public static void setSoundsEnabled(boolean soundsEnabled) {
    Multimedia.soundsEnabled = soundsEnabled;
  }

  /**
   * Return if music is enabled.
   *
   * @return boolean.
   */
  public static boolean isMusicEnabled() {
    return musicEnabled;
  }

  /**
   * Set if music is enabled.
   *
   * @param musicEnabled boolean.
   */
  public static void setMusicEnabled(boolean musicEnabled) {
    Multimedia.musicEnabled = musicEnabled;
  }

  /**
   * Set the volume to play sounds at.
   *
   * @param soundVolume double representing sound volume.
   */
  public static void setSoundVolume(double soundVolume) {
    logger.info("Sound volume set to " + soundVolume);
    Multimedia.soundVolume = soundVolume;
    soundPlayer.setVolume(soundVolume);
  }

  /**
   * Set the volume to play music at.
   *
   * @param musicVolume double representing music volume.
   */
  public static void setMusicVolume(double musicVolume) {
    Multimedia.musicVolume = musicVolume;
    musicPlayer.setVolume(musicVolume);
  }

  /**
   * Return the sound volume.
   *
   * @return double representing sound volume.
   */
  public static double getSoundVolume() {
    return soundVolume;
  }

  /**
   * Return the music volume.
   *
   * @return double representing music volume.
   */
  public static double getMusicVolume() {
    return musicVolume;
  }

  //Sounds
  //////////////////////

  /**
   * Playing a piece.
   */
  public static void place() {
    playSound("sounds/place.wav");
  }

  /**
   * Clearing a line.
   */
  public static void clearLine() {
    playSound("sounds/custom/clearLine.wav");
  }

  /**
   * Swapping the current game piece.
   */
  public static void swap() {
    playSound("sounds/custom/swap.wav");
  }

  /**
   * Invalid position to play current piece.
   */
  public static void placeError() {
    playSound("sounds/custom/error.wav");
  }

  /**
   * Rotating the current game piece.
   */
  public static void rotate() {
    playSound("sounds/rotate.wav");
  }

  /**
   * Losing a life.
   */
  public static void lifeLose() {
    playSound("sounds/custom/loseLifeCustom.wav");
  }

  /**
   * Reaching a new level
   */
  public static void level() {
    playSound("sounds/level.wav");
  }

  /**
   * Starting a game.
   */
  public static void gameStart() {
    playSound("sounds/custom/gameStartCustom.wav");
  }

  /**
   * Game over!
   */
  public static void gameOver() {
    playSound("sounds/custom/gameOverCustom.wav");
  }

  /**
   * Joining a multiplayer lobby.
   */
  public static void lobbyStart() {
    playSound("sounds/custom/lobbyStartCustom.wav");
  }

  /**
   * Receiving a chat message in a lobby.
   */
  public static void message() {
    playSound("sounds/custom/messageCustom.mp3");
  }

  /**
   * Highlighting an ImageButton.
   */
  public static void buttonHighlight() {
    playSound("sounds/custom/buttonHighlightCustom.wav");
  }

  /**
   * Clicking on an ImageButton.
   */
  public static void buttonPress() {
    playSound("sounds/custom/buttonSelectCustom.wav");
  }

  //Music
  /////////////////////

  /**
   * Play the menu music.
   */
  public static void menuMusic() {
    playMusic("music/custom/menuMusicCustom.mp3");
  }

  /**
   * Play the game start music, followed by the normal game music.
   */
  public static void gameMusic() {
    playMusic("music/game_start.wav", "music/game.wav");
  }

  /**
   * Play the ending music.
   */
  public static void endMusic() {
    playMusic("music/end.wav");
  }
}
