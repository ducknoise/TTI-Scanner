package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class BLPopupController implements Initializable  {

	@FXML TextArea reason;
	@FXML Text isBlacklisted;
	@FXML ImageView faceView;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		isBlacklisted.setText(Global.newBlackListed.fullName + " is Black Listed!");
		reason.setText(Global.newBlackListed.getInfo()[Scannable.BLACKLIST_REASON]);
		faceView.setImage(Global.newBlackListed.getFace());
		Global.blackListedCheck = true;
	}

}
