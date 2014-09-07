package application;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

public class CalendarController implements Initializable {

	@FXML ChoiceBox<String> monthBox;
	@FXML ChoiceBox<String> dayBox;
	@FXML ChoiceBox<String> yearBox;
	@FXML Text dateText;
	ObservableList<String> months;
	ObservableList<String> days;
	ObservableList<String> years;
	String text;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		months = FXCollections.observableArrayList();
		days = FXCollections.observableArrayList();
		years = FXCollections.observableArrayList();
		months.add("Jan");
		months.add("Feb");
		months.add("Mar");
		months.add("Apr");
		months.add("May");
		months.add("June");
		months.add("July");
		months.add("Aug");
		months.add("Sep");
		months.add("Oct");
		months.add("Nov");
		months.add("Dec");
		for(int i =1; i <=31; i++){
			days.add(String.valueOf(i));
		}
		for(int i=Calendar.getInstance().get(Calendar.YEAR); i >2000; i--){
			years.add(String.valueOf(i));
		}
		monthBox.setItems(months);
		dayBox.setItems(days);
		yearBox.setItems(years);
		text = "";
		

	}

	public void updateText(){
		text = getMonth()+""+getDay()+""+getYear();
		dateText.setText(text);
	}

	private String getMonth(){
		String month = monthBox.getSelectionModel().getSelectedItem();
		if(month!=null){
			if(month.equals("Jan"))
				return "01";
			if(month.equals("Feb"))
				return "02";
			if(month.equals("Mar"))
				return "03";
			if(month.equals("Apr"))
				return "04";
			if(month.equals("May"))
				return "05";
			if(month.equals("June"))
				return "06";
			if(month.equals("July"))
				return "07";
			if(month.equals("Aug"))
				return "08";
			if(month.equals("Sep"))
				return "09";
			if(month.equals("Oct"))
				return "10";
			if(month.equals("Nov"))
				return "11";
			if(month.equals("Dec"))
				return "12";
		}
		return "";
	}
	private String getDay(){
		String day = dayBox.getSelectionModel().getSelectedItem();
		if(day!= null && Integer.valueOf(day) < 10)
			day = "0"+day;
		if(day!=null)
			return "/"+day;
		else 
			return "";
	}
	private String getYear(){
		String year = yearBox.getSelectionModel().getSelectedItem();
		if(year!=null)
			return "/"+year;
		else {
			return "";
		}
	}
	public void search(){
		Global.mainContoller.searchBox.setText(text);
		Global.mainContoller.search();
		Global.calanderStage.close();
	}
}
