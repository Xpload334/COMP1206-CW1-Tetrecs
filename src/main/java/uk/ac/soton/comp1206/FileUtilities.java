package uk.ac.soton.comp1206;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * File Utilities class is used to handle reading and writing of game files, notably the local
 * scores.
 */
public class FileUtilities {

  private static final Logger logger = LogManager.getLogger(FileUtilities.class);
  private static final String localScoresPath = "src/main/resources/localScores.txt";
  private static final String defaultScoresPath = "src/main/resources/defaultScores.txt";

  /**
   * Copy content of file A to file B
   *
   * @param a File A
   * @param b File B
   */
  public static void copyContent(File a, File b) {
    logger.info("Contents of file '" + a.getName() + "' written to '" + b.getName() + "'");

    try {
      BufferedReader fileIn = new BufferedReader(new FileReader(a));
      BufferedWriter fileOut = new BufferedWriter(new FileWriter(b));

      String line;
      while ((line = fileIn.readLine()) != null) {
        fileOut.write(line);
        fileOut.newLine();
      }
      fileIn.close();
      fileOut.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the highest score of the local scores file. If the local scores file cannot be found,
   * return the highest score of the default scores file instead.
   *
   * @return int representing the highest score of the local/default scores.
   */
  public static int getLocalHighScore() {
    logger.info("Getting highest local score from local scores file");

    try {
      //Get first line of the default scores file
      File localScores = new File(localScoresPath);
      BufferedReader reader = new BufferedReader(new FileReader(localScores));

      String firstLine = reader.readLine();
      if (firstLine.isEmpty()) {
        throw new FileNotFoundException();
      }
      var score = firstLine.split(":")[1];
      return Integer.parseInt(score);

    } catch (FileNotFoundException e) {
      //Try default scores instead
      File defaultScores = new File(defaultScoresPath);

      try {
        BufferedReader reader = new BufferedReader(new FileReader(defaultScores));
        String firstLine = reader.readLine();
        var score = firstLine.split(":")[1];
        return Integer.parseInt(score);

      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * Load the contents of a scores File into a score List Property. Username and score are separated
   * by a colon, and each score entry is separated by a new line.
   *
   * @param scoresFile file containing scores.
   * @param scoresList list property to hold the scores.
   */
  public static void loadScores(File scoresFile, ListProperty scoresList) {
    logger.info("Loading Scores");
    //List<Pair<String, Integer>> displayList = new ArrayList<>();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(scoresFile));

      String line;
      while ((line = reader.readLine()) != null) {
        var user = line.split(":")[0];
        var score = line.split(":")[1];

        Integer scoreInt = Integer.parseInt(score);

        scoresList.add(new Pair<>(user, scoreInt));
        logger.info("Score Added, " + user + ":" + score);
      }

      reader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);

    }
  }

  /**
   * Write the list of local scores to the local scores file.
   *
   * @param scoresFile           local scores file.
   * @param observableScoresList observable list to hold the local scores.
   */
  public static void writeScores(File scoresFile,
      ObservableList<Pair<String, Integer>> observableScoresList) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(scoresFile));
      for (Pair<String, Integer> scorePair : observableScoresList) {
        var name = scorePair.getKey();
        var score = scorePair.getValue();

        writer.write(name + ":" + score);
        writer.newLine();
        logger.info("Score written, " + name + ":" + score);
      }

      writer.close();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
