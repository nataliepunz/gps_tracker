package at.jku.se.gps_tracker.controller;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public interface ErrorPopUpController {
	default void showErrorPopUp(String textToBeDisplayed) {
		getErrorStage(textToBeDisplayed).showAndWait();
	}

	default void showErrorPopUpNoWait(String textToBeDisplayed) {
		getErrorStage(textToBeDisplayed).show();
	}

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