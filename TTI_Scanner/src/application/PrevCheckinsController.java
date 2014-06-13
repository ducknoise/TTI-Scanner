package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class PrevCheckinsController implements Initializable {
	@FXML Text titleText;
	@FXML TextArea historyArea;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		titleText.setText(Global.prevCheckinsScannable.fullName + " has checked in on the following dates:");
		historyArea.setText(Global.prevCheckinsScannable.getInfo()[Scannable.ALL_CHECK_IN_DATES]);
		
	}

}
