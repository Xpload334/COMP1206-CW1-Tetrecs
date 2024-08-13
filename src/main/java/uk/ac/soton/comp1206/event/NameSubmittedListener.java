package uk.ac.soton.comp1206.event;

/**
 * Name Submitted Listener is used to handle an event when the username of the user is submitted for
 * the scores. Passes the username that was submitted in the message.
 */
public interface NameSubmittedListener {

  /**
   * Handle a Name Submitted event.
   * @param username String representing the username.
   */
  public void nameSubmitted(String username);

}
