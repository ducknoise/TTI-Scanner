package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;








import java.util.Timer;
import java.util.TimerTask;



import application.Global;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Controller
implements Initializable
{
	Scannable[] scannables;
	Scannable[] shownScannables;
	@FXML ImageView imageView;
	@FXML ListView<Scannable> namesList;
	@FXML TextField searchBox;
	@FXML TextField firstNameField;
	@FXML TextField lastNameField;
	@FXML TextField addressField;
	@FXML TextField cityField;
	@FXML TextField stateField;
	@FXML TextField zipField;
	@FXML TextField countryField;
	@FXML TextField idTypeField;
	@FXML TextField idField;
	@FXML TextField passportNumberField;
	@FXML TextField DOBField;
	@FXML TextField issueDateField;
	@FXML TextField expireField;
	@FXML TextField sexField;
	@FXML TextField blackListField;
	@FXML TextArea reasonArea;
	@FXML TextField checkInDateField;
	@FXML Button historyButton;
	ObservableList<Scannable> items;
	Timer timer;
	int index  =-1;
	public void initialize(URL location, ResourceBundle resources)
	{
		getScannables();
		setScannables();
		Global.mainContoller = this;
		refresh();
		
	}

	public void getScannables()
	{
		scannables = TTIFiles.getScannables();
		shownScannables = scannables;
	}

	public void setScannables()
	{
		items = FXCollections.observableArrayList();
		for (int i = 0; i < shownScannables.length; i++) {
			items.add(shownScannables[i]);
		}
		namesList.setItems(items);
	}

	public void refresh()  {
		getScannables();
		setScannables();
		searchBox.setText("");
		if(!Global.blackListedCheck){
			try{
				Parent root = (Parent)FXMLLoader.load(getClass().getResource("BlackListedPopup.fxml"));
				Scene scene = new Scene(root);
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.show();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

	}

	public void listClicked()
	{
		index = namesList.getSelectionModel().getSelectedIndex();
		if (index >= 0)
		{
			Scannable s = shownScannables[index];
			Image image = s.getImage();
			imageView.setImage(image);
			setInfo(shownScannables[index]);
			if(s.info[Scannable.ALL_CHECK_IN_DATES].length() < s.info[Scannable.CHECK_IN_DATE].length() + 15){
				if(!historyButton.isDisabled())
					historyButton.setDisable(true);
			}else {
				if(historyButton.isDisabled())
					historyButton.setDisable(false);
			}
		}
	}

	public void search()
	{
		ArrayList<Scannable> tempScannables = new ArrayList<Scannable>();
		String searchText = searchBox.getText();
		for (int i = 0; i < scannables.length; i++) {
			if (scannables[i].sortBy().toLowerCase().contains(searchText.toLowerCase())) {
				tempScannables.add(scannables[i]);
			}
		}
		shownScannables = ((Scannable[])tempScannables.toArray(new Scannable[tempScannables.size()]));
		setScannables();
	}

	public void open()
	{
		if (index >= 0) {
			try
			{
				String path = shownScannables[index].imgPath;
				File file = new File(path);
				Desktop.getDesktop().print(file);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	private void setInfo(Scannable s){
		String [] info = s.getInfo();
		firstNameField.setText(info[Scannable.FIRST_NAME]);
		lastNameField.setText(info[Scannable.LAST_NAME]);
		addressField.setText(info[Scannable.ADDRESS]);
		cityField.setText(info[Scannable.CITY]);
		stateField.setText(info[Scannable.STATE]);
		zipField.setText(info[Scannable.ZIP]);
		countryField.setText(info[Scannable.COUNTRY]);
		idTypeField.setText(info[Scannable.ID_TYPE]);
		idField.setText(info[Scannable.DL_NUM]);
		passportNumberField.setText(info[Scannable.PP_NUM]);
		DOBField.setText(info[Scannable.DATE_OF_BIRTH]);
		issueDateField.setText(info[Scannable.ISSUE_DATE]);
		expireField.setText(info[Scannable.EXPIRE_DATE]);
		sexField.setText(info[Scannable.SEX]);
		blackListField.setText(info[Scannable.BLACKLIST]);
		reasonArea.setText(info[Scannable.BLACKLIST_REASON]);
		checkInDateField.setText(info[Scannable.CHECK_IN_DATE]);
	}

	private String [] getInfo(){
		String [] info = new String[Scannable.INFO_SIZE];
		info[Scannable.FIRST_NAME] = firstNameField.getText();
		info[Scannable.LAST_NAME] = lastNameField.getText();
		info[Scannable.ADDRESS] = addressField.getText();
		info[Scannable.CITY] = cityField.getText();
		info[Scannable.STATE] = stateField.getText();
		info[Scannable.ZIP] = zipField.getText();
		info[Scannable.COUNTRY] = countryField.getText();
		info[Scannable.ID_TYPE] = idTypeField.getText();
		info[Scannable.DL_NUM] = idField.getText();
		info[Scannable.PP_NUM] = passportNumberField.getText();
		info[Scannable.DATE_OF_BIRTH] = DOBField.getText();
		info[Scannable.ISSUE_DATE] = issueDateField.getText();
		info[Scannable.EXPIRE_DATE] = expireField.getText();
		info[Scannable.SEX] = sexField.getText();
		info[Scannable.BLACKLIST] = blackListField.getText();
		info[Scannable.BLACKLIST_REASON] = reasonArea.getText();
		info[Scannable.CHECK_IN_DATE] = checkInDateField.getText();

		return info;
	}

	public void saveInfo(ActionEvent event) throws IOException, InterruptedException {

		if (index >= 0)
		{
			Scannable s = shownScannables[index];
			String[] updatedInfo = getInfo();
			updatedInfo[Scannable.ALL_CHECK_IN_DATES] = s.getInfo()[Scannable.ALL_CHECK_IN_DATES];
			TTIFiles.updateEntry(s, updatedInfo);
			setInfo(s);
			refresh();
		}

	}

	public void ynCheck()  {
		String fieldText = blackListField.getText();
		if(fieldText.length()>0){
			String text = fieldText.substring(0,1);
			if(!(text.equalsIgnoreCase("N") ||text.equalsIgnoreCase("Y")))
				text = "N";
			text=text.toUpperCase();
			blackListField.setText(text);
		}
	}

	public void searchAndSortBy(ActionEvent event) throws IOException, InterruptedException{
		MenuItem item = (MenuItem)event.getSource();
		String title = item.getId();
		if(title.equals("fullNameSearch"))
			Scannable.setSort(Scannable.SORT_BY_NAME);
		if(title.equals("blackListSearch"))
			Scannable.setSort(Scannable.SORT_BY_BLACK_LIST);
		if(title.equals("checkInDateSearch")){
			Scannable.setSort(Scannable.SORT_BY_CHECK_IN_DATE);
			Parent root = (Parent)FXMLLoader.load(getClass().getResource("CalendarGUI.fxml"));
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			Global.calanderStage = stage;
			stage.show();
		}
		refresh();
	}
	public void	checkins(){

		if (index >= 0)
		{
			try{
				Global.prevCheckinsScannable = shownScannables[index];
				Parent root = (Parent)FXMLLoader.load(getClass().getResource("PrevCheckins.fxml"));
				Scene scene = new Scene(root);
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.show();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void autoRefresh(ActionEvent event) throws IOException, InterruptedException{
		ToggleButton autoRefresh = (ToggleButton)event.getSource();
		if(autoRefresh.isSelected()){
		 timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							Global.mainContoller.refresh();

						}
					});

				}
			}, 1000, 3000);
		}
		else{
			timer.cancel();
		}
	}
}










