package at.jku.se.gps_tracker.controller;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
/**
 * interface for any error-popups
 * @author Ozan
 *
 */
public interface ErrorPopUpController {
	/**
	 * opens a new popup message in a window and halts the rest of the program execution until the message is acknowledged
	 * @author Ozan
	 * @param textToBeDisplayed the text to be displayed in the popup message
	 */
	default void showErrorPopUp(String textToBeDisplayed) {
		getErrorStage(textToBeDisplayed).showAndWait();
	}
	
	/**
	 * opens a new popup message in a window and does not halt the rest of the program execution
	 * @author Ozan
	 * @param textToBeDisplayed the text to be displayed in the popup message
	 */
	default void showErrorPopUpNoWait(String textToBeDisplayed) {
		getErrorStage(textToBeDisplayed).show();
	}
	
	/**
	 * returns the stage with the message
	 * @author Ozan
	 * @param textToBeDisplayed the text to be displayed in the popup message
	 * @return the stage that can then be shown
	 */
	private Stage getErrorStage(String textToBeDisplayed) {
		Text text = new Text(textToBeDisplayed);
		Button button = new Button("Ok, close");
		VBox vBox = new VBox(text,button);
		Scene popupScene = new Scene(vBox, 600,250);
		Stage popup = new Stage();
		popup.setScene(popupScene);
		popup.setTitle("Fehler!");
		popup.getIcons().add(new Image(getClass().getResourceAsStream("/icon/errorIcon.png")));
		popup.setAlwaysOnTop(true);
		popup.setScene(popupScene);
		button.setOnAction(e -> popup.close());
		return popup;
	}
}
