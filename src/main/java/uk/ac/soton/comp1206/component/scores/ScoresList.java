package uk.ac.soton.comp1206.component.scores;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameColours;

/**
 * User Interface component to display a list of game scores.
 */
public class ScoresList extends VBox {

  private static final Logger logger = LogManager.getLogger(ScoresList.class);
  /**
   * Array of colours for the scores.
   */
  protected static final Color[] COLOURS = GameColours.COLOURS;
  /**
   * Maximum number of scores to display.
   */
  protected int numScores = 10;
  /**
   * Name of this score list.
   */
  protected String name;
  /**
   * Score list property
   */
  public SimpleListProperty<Pair<String, Integer>> scoresListProperty = new SimpleListProperty<>();


  /**
   * Construct a new Score List.
   */
  public ScoresList() {
    //Not building at the start as the scorelist may change later, so
    //will call updateList() later on
    name = scoresListProperty.getName();
  }

  /**
   * Build the score list by creating text for up to some maximum number of entries.
   */
  private void build() {
    logger.info("Building Score List");
    this.getChildren().clear();
    getStyleClass().add("scorelist");

    buildScores();
    logger.info("Score List Built");
  }

  /**
   * Build the score displays of the score list.
   */
  private void buildScores() {
    int i = 0;
    while (i < numScores && i < scoresListProperty.size()) {
      var name = scoresListProperty.get(i).getKey();
      var score = scoresListProperty.get(i).getValue();

      var scoreDisplay = new HBox();
      scoreDisplay.setAlignment(Pos.CENTER);

      var nameText = new Text(name);
      nameText.getStyleClass().add("scorer");
      nameText.setFill(COLOURS[i + 1]);
      scoreDisplay.getChildren().add(nameText);

      var scoreText = new Text(score.toString());
      scoreText.getStyleClass().add("scoreitem");
      scoreDisplay.getChildren().add(scoreText);

      logger.info("Added " + name + ":" + score.toString());
      getChildren().add(scoreDisplay);

      i++;
    }
  }

  /**
   * Animate each of the score displays in turn.
   */
  public void reveal() {
    SequentialTransition sequentialTransition = new SequentialTransition();

    for (var child : getChildren()) {
      if (child instanceof HBox) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), child);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        sequentialTransition.getChildren().add(fadeTransition);
      }
    }
    sequentialTransition.play();
  }

  /**
   * Rebuild the score list.
   */
  public void updateList() {
    logger.info("Updating Score List");
    build();
  }
}
