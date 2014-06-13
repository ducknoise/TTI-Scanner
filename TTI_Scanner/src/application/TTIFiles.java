package application;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class TTIFiles {
	static String filePath="";
	private static String getPath(){
		if(filePath.equals("")){
			try {
				File prop = new File("C:/tti/idscan/prop.tti");
				byte[] bytes = Files.readAllBytes(prop.toPath());
				String contents = new String(bytes);
				int begIndex = contents.indexOf("LogPath|")+"LogPath|".length();
				int endIndex = contents.indexOf("MaxIdimgSize|");
				filePath = contents.substring(begIndex,endIndex).trim();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return filePath;
	}

	public static  Scannable [] getScannables(){
		if(Scannable.SORT_BY == Scannable.SORT_BY_NAME || Scannable.SORT_BY == Scannable.SORT_BY_CHECK_IN_DATE){
			ArrayList<Scannable> scans = getScannablesFromDB();
			scans.addAll(getScannablesFromTemp());
			Scannable [] scannables = ((Scannable[])scans.toArray(new Scannable[scans.size()]));
			Arrays.sort(scannables);
			return scannables;
		}
		else{
			getScannablesFromTemp();
			ArrayList<Scannable> scans = getBlacklistedFromDB();
			Scannable [] scannables = ((Scannable[])scans.toArray(new Scannable[scans.size()]));
			Arrays.sort(scannables);
			return scannables;
		}
	}
	private static ArrayList<Scannable>getScannablesFromTemp(){
		EmbeddedDatabase db = new EmbeddedDatabase();
		db.establishConnection(getPath() + "/db");
		ArrayList<Scannable> scans = new ArrayList<Scannable>();
		File directory = new File(getPath()).getAbsoluteFile();

		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			String filePath = files[i].getAbsolutePath();
			if (filePath.substring(filePath.length() - 18).equals("_scannedXMLlog.tti"))
			{
				Scannable s = new Scannable(files[i]);
				scans.add(s);
				db.insertScannable(s);
				if(db.isBlackListed(s)){
					Global.newBlackListed =s;
					Global.blackListedCheck = false;
				}
				db.consolidateScannable(s);
				archiveFile(files[i]);

			}
		}
		db.closeConnectoin();
		return scans;
	}

	private static void archiveFile(File file){
		File archived = new File(getPath()+ "/archived");
		if(!archived.exists()){
			archived.mkdir();
		}
		Calendar cal = Calendar.getInstance(); 
		String date = String.valueOf(cal.getTimeInMillis());
		String newPath = getPath()+"archived/"+date+".tti";
		file.renameTo(new File(newPath));
	}

	private static ArrayList<Scannable> getScannablesFromDB(){
		ArrayList<Scannable> scans = new ArrayList<Scannable>();
		EmbeddedDatabase db = new EmbeddedDatabase();
		db.establishConnection(getPath() + "/db");
		scans = db.getScannables();
		db.closeConnectoin();
		return scans;
	}
	private static ArrayList<Scannable> getBlacklistedFromDB(){
		ArrayList<Scannable> scans = new ArrayList<Scannable>();
		EmbeddedDatabase db = new EmbeddedDatabase();
		db.establishConnection(getPath() + "/db");
		scans = db.getBlackListed();
		db.closeConnectoin();
		return scans;
	}
	
	public static void updateEntry(Scannable s, String [] updatedInfo){
		EmbeddedDatabase db = new EmbeddedDatabase();
		db.establishConnection(getPath() + "/db");
		
		//for black list checking that doesnt popup if blacklist has just been change
		Scannable s2 = new Scannable();
		String [] info = new String[Scannable.INFO_SIZE];
		info[Scannable.DL_NUM] = updatedInfo[Scannable.DL_NUM];
		info[Scannable.PP_NUM] = updatedInfo[Scannable.PP_NUM];
		s2.setInfo(info);
		if(db.isBlackListed(s2)){
			Global.newBlackListed =s;
			Global.blackListedCheck = false;
		}
		//update
		db.updateScannable(s, updatedInfo);
		db.consolidateScannable(s);
		
		db.closeConnectoin();
	}
}
