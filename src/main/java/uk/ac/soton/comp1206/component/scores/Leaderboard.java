package uk.ac.soton.comp1206.component.scores;

import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameColours;
import uk.ac.soton.comp1206.game.MultiplayerScoreClass;

/**
 * Construct a new Leaderboard for displaying scores
 */
public class Leaderboard extends VBox {

  private static final Logger logger = LogManager.getLogger(Leaderboard.class);
  /**
   * Array of colours for the scores.
   */
  protected static final Color[] COLOURS = GameColours.COLOURS;
  /**
   * Maximum number of scores to display.
   */
  protected int numScores = 10;
  /**
   * Name of this leaderboard.
   */
  protected String name;
  /**
   * Lobby Score List Property.
   */
  public SimpleListProperty<MultiplayerScoreClass> lobbyScoreClassListProperty = new SimpleListProperty<>();

  /**
   * Construct a new Leaderboard.
   */
  public Leaderboard() {
    //Not building at the start as the leaderboard may change later, so
    //will call updateList() later on
    name = lobbyScoreClassListProperty.getName();
  }

  /**
   * Build the score list by creating text for up to some maximum number of entries.
   */
  private void build() {
    logger.info("Building Leaderboard");
    this.getChildren().clear();
    getStyleClass().add("scorelist");

    buildScores();
    logger.info("Leaderboard built");
  }

  /**
   * Build the score displays of the Leaderboard. If the lives of the display is -1, (DEAD), set the
   * lives text to X and set the colours of the text to gray.
   */
  private void buildScores() {
    int i = 0;
    logger.info("Building Leaderboard Scores");

    while (i < numScores && i < lobbyScoreClassListProperty.size()) {
      String name = lobbyScoreClassListProperty.get(i).getUsername();
      int score = lobbyScoreClassListProperty.get(i).getScore();
      int lives = lobbyScoreClassListProperty.get(i).getLives();

      boolean IsOut = false;

      var scoreDisplay = new HBox(5);
      scoreDisplay.setAlignment(Pos.CENTER);

      //Name text
      var nameText = new Text(name);
      nameText.getStyleClass().add("scorer");
      nameText.setFill(COLOURS[i + 1]);
      scoreDisplay.getChildren().add(nameText);

      //Score text
      var scoreText = new Text(Integer.toString(score));
      scoreText.getStyleClass().add("scoreitem");
      scoreDisplay.getChildren().add(scoreText);

      //Lives text
      String livesString;
      //If out, replace with X
      if (lives < 0) {
        livesString = "X";
        IsOut = true;
      } else {
        livesString = Integer.toString(lives);
      }
      var livesText = new Text(livesString);
      livesText.getStyleClass().add("scoreitem");
      livesText.setFill(Color.YELLOW);
      scoreDisplay.getChildren().add(livesText);

      if (IsOut) {
        nameText.setFill(Color.GRAY);
        scoreText.setFill(Color.GRAY);
        livesText.setFill(Color.GRAY);
      }

      logger.info("Added " + name + ":" + score + ":" + lives);
      getChildren().add(scoreDisplay);

      i++;
    }
    logLeaderboardChildren();
  }

  /**
   * Log each of the scores : lives of each score class.
   */
  private void logLeaderboardChildren() {
    logger.info("Logging each score class");
    for (MultiplayerScoreClass scoreClass : lobbyScoreClassListProperty) {
      logger.info(scoreClass.getScore() + ":" + scoreClass.getLives());
    }
  }

  /**
   * Rebuild the leaderboard.
   */
  public void updateList() {
    logger.info("Updating Leaderboard");
    build();
  }

  /**
   * Update the leaderboard list property.
   *
   * @param listProperty list property of MultiplayerScoreClasses
   */
  public void updateList(SimpleListProperty listProperty) {
    logger.info("Updating Leaderboard with list property");
    lobbyScoreClassListProperty = listProperty;
    build();
  }

}
