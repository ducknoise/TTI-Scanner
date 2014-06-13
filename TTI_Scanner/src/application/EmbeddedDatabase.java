package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;


public class EmbeddedDatabase {
	private static final String DRIVER = "org.h2.Driver";
	private Connection conn = null;
	//keep in order of database rows
	static int [] IN_INFO_DB = {Scannable.FIRST_NAME, Scannable.LAST_NAME,Scannable.ADDRESS, Scannable.CITY, Scannable.STATE, Scannable.ZIP,Scannable.COUNTRY, 
		Scannable.ID_TYPE, Scannable.DL_NUM, Scannable.PP_NUM, Scannable.DATE_OF_BIRTH, Scannable.ISSUE_DATE,Scannable.EXPIRE_DATE, Scannable.SEX, Scannable.CHECK_IN_DATE, Scannable.ALL_CHECK_IN_DATES};
	static int [] IN_BLACK_LIST_DB = {Scannable.DL_NUM, Scannable.PP_NUM, Scannable.BLACKLIST_REASON};
	public void establishConnection(String filePath) {
		try{
			Class.forName(DRIVER);
			conn= DriverManager.getConnection("jdbc:h2:/"+filePath+";IFEXISTS=TRUE");
		}
		catch(Exception e){
			try {
				Class.forName(DRIVER);
				conn= DriverManager.getConnection("jdbc:h2:/"+filePath+";");
				createTable();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
	}
	public void closeConnectoin(){
		try{
			conn.close();
		}catch(Exception e){}
	}

	private void createTable(){
		try{
			String executeString = "CREATE TABLE Scannables("
					+ "FirstName Text,"
					+ "LastName Text,"
					+ "Address Text,"
					+ "City Text,"
					+ "State Text,"
					+ "Zip Text,"
					+ "Counrty Text,"
					+ "IDtype Text,"
					+ "DLNum Text,"
					+ "PassportNumber Text,"
					+ "DOBISO Text,"
					+ "IssueDate Text,"
					+ "ExpireISO Text,"
					+ "Sex Text,"
					+ "CheckInDate Text,"
					+ "AllCheckInDates Text,"
					+ "ImgPath Text);";
			conn.createStatement().execute(executeString);
			executeString = "CREATE TABLE BlackList(DLNum Text, PassportNumber Text, Reason Text)";
			conn.createStatement().execute(executeString);

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void insertScannable(Scannable s){
		String [] info = s.getInfo();
		try{
			String executeString = "INSERT INTO Scannables VALUES(";
			for(int i =0; i<IN_INFO_DB.length; i++)
				executeString += "'" + info[IN_INFO_DB[i]]+"',";
			executeString+="'"+s.imgPath+"'"+")";
			conn.createStatement().execute(executeString);
			if(s.blackListed()){
				executeString = "INSERT INTO BlackList VALUES(";
				for(int i =0; i < IN_BLACK_LIST_DB.length; i++){
					executeString +="'" + info[IN_BLACK_LIST_DB[i]]+ "'";
					if(i!= IN_BLACK_LIST_DB.length -1) executeString+= ",";
				}
				executeString+= ");";
				conn.createStatement().execute(executeString);

			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public ArrayList<Scannable>  getScannables(){
		ArrayList<Scannable> scans = new ArrayList<Scannable>();
		try{

			ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM Scannables ");

			while(resultSet.next()){
				String [] info = new String[Scannable.INFO_SIZE];
				for(int i =0; i< IN_INFO_DB.length;i++){
					info[IN_INFO_DB[i]] = resultSet.getString(i+1);
				}
				String imgPath = resultSet.getString(IN_INFO_DB.length+1);
				//blacklist stuff
				ResultSet resultSet2 = conn.createStatement().executeQuery("SELECT * FROM BlackList WHERE DLNum='"+info[Scannable.DL_NUM]+ "' AND PassportNumber='"+ info[Scannable.PP_NUM] +"';");
				if(resultSet2.next()){
					info[Scannable.BLACKLIST] = "Y";
					info[Scannable.BLACKLIST_REASON] = resultSet2.getString(3);
				}else{
					info[Scannable.BLACKLIST] = "N";
					info[Scannable.BLACKLIST_REASON] = "";
				}

				scans.add(new Scannable(info,imgPath));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return scans;
	}

	public Scannable getScannable(String passPortNum, String dlNum){
		Scannable s = new Scannable();
		try{
			ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM Scannables WHERE  DLNum='"+dlNum+ "' AND PassportNumber='"+ passPortNum +"';");
			if(resultSet.next()){
				String [] info = new String[Scannable.INFO_SIZE];
				for(int i =0; i<IN_INFO_DB.length;i++){
					info[IN_INFO_DB[i]] = resultSet.getString(i+1);
				}
				
				String imgPath = resultSet.getString(IN_INFO_DB.length+1);
				
				//blacklist stuff
				ResultSet resultSet2 = conn.createStatement().executeQuery("SELECT * FROM BlackList WHERE DLNum='"+info[Scannable.DL_NUM]+ "' AND PassportNumber='"+ info[Scannable.PP_NUM] +"';");
				if(resultSet2.next()){
					info[Scannable.BLACKLIST] = "Y";
					info[Scannable.BLACKLIST_REASON] = resultSet2.getString(3);
				}else{
					info[Scannable.BLACKLIST] = "N";
					info[Scannable.BLACKLIST_REASON] = "";
				}
				s = new Scannable(info, imgPath);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return s;
	}

	public void updateScannable(Scannable s, String [] updatedInfo){
		deleteScannable(s);
		s.setInfo(updatedInfo);
		insertScannable(s);
	}
	public void deleteScannable(Scannable s){
		try{
			String [] oldInfo = s.getInfo();
			String executeString = "DELETE FROM Scannables WHERE DLNum='"+oldInfo[Scannable.DL_NUM]+ "' AND PassportNumber='"+ oldInfo[Scannable.PP_NUM] +"';";
			conn.createStatement().execute(executeString);
			executeString = "DELETE FROM BlackList WHERE DLNum='"+oldInfo[Scannable.DL_NUM]+ "' AND PassportNumber='"+ oldInfo[Scannable.PP_NUM] +"';";
			conn.createStatement().execute(executeString);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public ArrayList<Scannable> getBlackListed(){
		ArrayList<Scannable> scans = new ArrayList<Scannable>();
		try{

			ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM BlackList");

			while(resultSet.next()){
				String dlNum = resultSet.getString(1);
				String ppNum = resultSet.getString(2);
				scans.add(getScannable(ppNum, dlNum));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return scans;
	}

	public boolean isBlackListed(Scannable s){
		boolean blackListed = false;
		String [] info = s.getInfo();
		try{
			String executeString = "SELECT * FROM BlackList WHERE DLNum='"+info[Scannable.DL_NUM]+ "' AND PassportNumber='"+ info[Scannable.PP_NUM] +"';";
			ResultSet resultSet = conn.createStatement().executeQuery(executeString);
			if(resultSet.next()){
				info[Scannable.BLACKLIST] = "Y";
				info[Scannable.BLACKLIST_REASON] = resultSet.getString(3);
				s.setInfo(info);
				blackListed = true;
			}
			
		}
		catch(Exception e){
			
		}
		return blackListed;
	}


	public void consolidateScannable(Scannable s){
		String [] info = s.getInfo();
		try{
			String executeString =  "SELECT * FROM Scannables WHERE DLNum='"+info[Scannable.DL_NUM]+ "' AND PassportNumber='"+ info[Scannable.PP_NUM] +"';";
			ResultSet resultSet = conn.createStatement().executeQuery(executeString);

			String allDates = "";
			while(resultSet.next()){
				String date = resultSet.getString(16);//16 is currently the location of AllCheckInDates field
				allDates += date + "\n"; 
			}

			info[Scannable.ALL_CHECK_IN_DATES] = allDates;
			updateScannable(s, info);


		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(s.toString());
		}
	}

}
