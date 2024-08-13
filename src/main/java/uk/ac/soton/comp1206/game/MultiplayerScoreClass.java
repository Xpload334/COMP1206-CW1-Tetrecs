package uk.ac.soton.comp1206.game;

/**
 * Class to hold the information about a multiplayer score. Holds username, score and lives. Lives
 * only used for the Leaderboard.
 */
public class MultiplayerScoreClass implements Comparable {

  String username;
  int score;
  int lives;

  /**
   * Construct a new Multiplayer Score Class.
   * @param username username.
   * @param score score.
   * @param lives lives.
   */
  public MultiplayerScoreClass(String username, int score, int lives) {
    this.username = username;
    this.score = score;
    this.lives = lives;

  }

  /**
   * Compares this object with the specified object for order.  Returns a negative integer, zero, or
   * a positive integer as this object is less than, equal to, or greater than the specified
   * object.
   */
  @Override
  public int compareTo(Object o) {
    if (!(o instanceof MultiplayerScoreClass)) {
      throw new ClassCastException("Object must be MultiplayerScoreClass");
    }
    MultiplayerScoreClass other = (MultiplayerScoreClass) o;
    //Score
    if (this.getScore() > other.getScore()) {
      return 1;
    }
    if (this.getScore() < other.getScore()) {
      return -1;
    }
    //Lives, if score the same
    if (this.getLives() > other.getLives()) {
      return 1;
    }
    if (this.getLives() < other.getLives()) {
      return -1;
    }
    //Name doesn't matter
    return 0;
  }

  /**
   * Return the username of this score class.
   * @return username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Return the score of this score class.
   * @return score
   */
  public int getScore() {
    return score;
  }

  /**
   * Return the lives of this score class.
   * @return lives
   */
  public int getLives() {
    return lives;
  }
}
