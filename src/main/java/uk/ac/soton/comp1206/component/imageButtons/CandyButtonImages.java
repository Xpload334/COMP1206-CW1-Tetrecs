package uk.ac.soton.comp1206.component.imageButtons;

import java.util.ArrayList;
import javafx.scene.image.Image;

/**
 * Static class to hold candy-themed button images in ArrayLists from a certain type.
 * <p>
 * These assets were taken from https://opengameart.org/content/candy-button-pack and are in the
 * public domain.
 */
public class CandyButtonImages {

  String path = this.getClass().getResource("/images/custom/candyButtons").toExternalForm();
  /**
   * Help Button Images.
   */
  public static ArrayList<Image> Help = new ArrayList<>();
  /**
   * Level List Button Images.
   */
  public static ArrayList<Image> LevelList = new ArrayList<>();
  /**
   * Minus Button Images.
   */
  public static ArrayList<Image> Minus = new ArrayList<>();
  /**
   * Music Button Images.
   */
  public static ArrayList<Image> Music = new ArrayList<>();
  /**
   * Multiplayer Button Images.
   */
  public static ArrayList<Image> Multiplayer = new ArrayList<>();
  /**
   * Play Button Images.
   */
  public static ArrayList<Image> Play = new ArrayList<>();
  /**
   * Plus Button Images.
   */
  public static ArrayList<Image> Plus = new ArrayList<>();
  /**
   * Profile Button Images.
   */
  public static ArrayList<Image> Profile = new ArrayList<>();
  /**
   * Options Button Images.
   */
  public static ArrayList<Image> Options = new ArrayList<>();
  /**
   * Right Arrow Button Images.
   */
  public static ArrayList<Image> RightArrow = new ArrayList<>();
  /**
   * Reload Button Images.
   */
  public static ArrayList<Image> Reload = new ArrayList<>();
  /**
   * Sound Button Images.
   */
  public static ArrayList<Image> Sound = new ArrayList<>();
  /**
   * Upload Button Images.
   */
  public static ArrayList<Image> Upload = new ArrayList<>();


  /**
   * Set the image lists.
   */
  public CandyButtonImages() {
    setImageLists();
  }

  /**
   * Add the image sets for each button type to the corresponding ArrayList.
   */
  private void setImageLists() {
    Help.add(new Image(path + "/Help (1).png"));
    Help.add(new Image(path + "/Help (2).png"));
    Help.add(new Image(path + "/Help (3).png"));
    Help.add(new Image(path + "/Help (4).png"));

    LevelList.add(new Image(path + "/LevelList (1).png"));
    LevelList.add(new Image(path + "/LevelList (2).png"));
    LevelList.add(new Image(path + "/LevelList (3).png"));
    LevelList.add(new Image(path + "/LevelList (4).png"));

    Minus.add(new Image(path + "/Minus (1).png"));
    Minus.add(new Image(path + "/Minus (2).png"));
    Minus.add(new Image(path + "/Minus (3).png"));
    Minus.add(new Image(path + "/Minus (4).png"));

    Music.add(new Image(path + "/Music (1).png"));
    Music.add(new Image(path + "/Music (2).png"));
    Music.add(new Image(path + "/Music (3).png"));
    Music.add(new Image(path + "/Music (4).png"));

    Multiplayer.add(new Image(path + "/Multiplayer (1).png"));
    Multiplayer.add(new Image(path + "/Multiplayer (2).png"));
    Multiplayer.add(new Image(path + "/Multiplayer (3).png"));
    Multiplayer.add(new Image(path + "/Multiplayer (4).png"));

    Play.add(new Image(path + "/Play (1).png"));
    Play.add(new Image(path + "/Play (2).png"));
    Play.add(new Image(path + "/Play (3).png"));
    Play.add(new Image(path + "/Play (4).png"));

    Plus.add(new Image(path + "/Plus (1).png"));
    Plus.add(new Image(path + "/Plus (2).png"));
    Plus.add(new Image(path + "/Plus (3).png"));
    Plus.add(new Image(path + "/Plus (4).png"));

    Profile.add(new Image(path + "/Profile (1).png"));
    Profile.add(new Image(path + "/Profile (2).png"));
    Profile.add(new Image(path + "/Profile (3).png"));
    Profile.add(new Image(path + "/Profile (4).png"));

    Options.add(new Image(path + "/Options (1).png"));
    Options.add(new Image(path + "/Options (2).png"));
    Options.add(new Image(path + "/Options (3).png"));
    Options.add(new Image(path + "/Options (4).png"));

    RightArrow.add(new Image(path + "/RightArrow (1).png"));
    RightArrow.add(new Image(path + "/RightArrow (2).png"));
    RightArrow.add(new Image(path + "/RightArrow (3).png"));
    RightArrow.add(new Image(path + "/RightArrow (4).png"));

    Reload.add(new Image(path + "/Reload (1).png"));
    Reload.add(new Image(path + "/Reload (2).png"));
    Reload.add(new Image(path + "/Reload (3).png"));
    Reload.add(new Image(path + "/Reload (4).png"));

    Sound.add(new Image(path + "/Sound (1).png"));
    Sound.add(new Image(path + "/Sound (2).png"));
    Sound.add(new Image(path + "/Sound (3).png"));
    Sound.add(new Image(path + "/Sound (4).png"));

    Upload.add(new Image(path + "/Upload (1).png"));
    Upload.add(new Image(path + "/Upload (2).png"));
    Upload.add(new Image(path + "/Upload (3).png"));
    Upload.add(new Image(path + "/Upload (4).png"));
  }
}
