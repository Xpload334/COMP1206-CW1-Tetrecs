package uk.ac.soton.comp1206.event;

/**
 * Slider Value Change Listener is used to handle an event when a Block Control Slider's value has
 * been updated. Passes the new value of the slider in the message.
 */
public interface SliderValueChangeListener {

  /**
   * Handle a Slider Value Change event.
   * @param value new value of the slider.
   */
  public void valueChanged(double value);

}
