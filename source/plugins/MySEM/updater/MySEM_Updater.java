// 2008-2022 - Nicola Ferralis <feranick@hotmail.com>

import ij.plugin.*;
import ij.*;
import ij.gui.*;
import ij.util.Tools;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;


/** This plugin implements the Plugins/MySEM/MySEM_Update command. */

public class MySEM_Updater implements PlugIn {

	private static boolean Startup = false; // if true, it changes also the Startup macro	
	private static boolean Cancel = false; // if true, it will stop the upgrade
	private static boolean FirstRun = false; // false for upgrade, true for first run
	private static boolean Uninstall = false; // false for upgrade, true for first run
	public String currentVersion = "5.6";
    public String url="https://raw.githubusercontent.com/feranick/MySEM/master/source/";

	public void run(String arg) {	
		Cancel = false;
		FirstRun = false;
		Uninstall = false;
		CheckFile("plugins/MySEM/MySEM_Set_Scale.class");

		if (arg.equals("about"))
			{showAbout();
			return;}

		if (IJ.getApplet()!=null) return;		
		String upgradeVersion = getUpgradeVersion();
		if (upgradeVersion==null) return;

		showDialog(upgradeVersion);
		if(Uninstall==true)
			{showDialogUninst();}
		
		if(Cancel==true)
			{return;}


		if(Uninstall==false)   //Condition when installing the plugins
			{if(Startup==true & FirstRun==true)		
				{File f0_orig = new File("macros/StartupMacros.txt");	
				f0_orig.renameTo(new File("macros/StartupMacros_backup.txt"));}

		
			GetFile("plugins/MySEM/MySEM_Updater.jar", url+"MySEM_Updater.jar");	

			//GetFile("plugins/MySEM/MySEM_Add_Comment.class", url+"MySEM_Add_Comment.class");
			//GetFile("plugins/MySEM/MySEM_Add_Comment.java", url+"MySEM_Add_Comment.java");
			GetFile("plugins/MySEM/MySEM_Crop.class", url+"plugins/MySEM/MySEM_Crop.class");
			GetFile("plugins/MySEM/MySEM_Crop.java", url+"plugins/MySEM/MySEM_Crop.java");
			GetFile("plugins/MySEM/MySEM_Measure.class", url+"plugins/MySEM/MySEM_Measure.class");
			GetFile("plugins/MySEM/MySEM_Measure.java", url+"plugins/MySEM/MySEM_Measure.java");
			GetFile("plugins/MySEM/MySEM_Set_Scale.class", url+"plugins/MySEM/MySEM_Set_Scale.class");
			GetFile("plugins/MySEM/MySEM_Set_Scale.java", url+"plugins/MySEM/MySEM_Set_Scale.java");
			GetFile("plugins/MySEM/MySEM_Filters.class", url+"plugins/MySEM/MySEM_Filters.class");
			GetFile("plugins/MySEM/MySEM_Filters.java", url+"plugins/MySEM/MySEM_Filters.java");
			GetFile("plugins/MySEM/MySEM_Line_Correction.java", url+"plugins/MySEM/MySEM_Line_Correction.java");
			GetFile("plugins/MySEM/MySEM_Line_Correction.class", url+"plugins/MySEM/MySEM_Line_Correction.class");
			GetFile("plugins/MySEM/MySEM_MainFrame.class", url+"plugins/MySEM/MySEM_MainFrame.class");
			GetFile("plugins/MySEM/MySEM_MainFrame.java", url+"plugins/MySEM/MySEM_MainFrame.java");
			GetFile("plugins/MySEM/MySEM_MainFrame$MySEM_MainFrame_Panel.class", url+"plugins/MySEM/MySEM_MainFrame$MySEM_MainFrame_Panel.class");
			GetFile("plugins/MySEM/MySEM_FiltersFrame.class", url+"plugins/MySEM/MySEM_FiltersFrame.class");
			GetFile("plugins/MySEM/MySEM_FiltersFrame.java", url+"plugins/MySEM/MySEM_FiltersFrame.java");
			GetFile("plugins/MySEM/MySEM_FiltersFrame$MySEM_FiltersFrame_Panel.class", url+"plugins/MySEM/MySEM_FiltersFrame$MySEM_FiltersFrame_Panel.class");
			GetFile("plugins/MySEM/MySEM_Select_Image.class", url+"plugins/MySEM/MySEM_Select_Image.class");
			GetFile("plugins/MySEM/MySEM_Select_Image.java", url+"plugins/MySEM/MySEM_Select_Image.java");


			GetFile("macros/toolsets/MySEM.txt", url+"macros/toolsets/MySEM.txt");
			GetFile("macros/toolsets/FFT.txt", url+"macros/toolsets/FFT.txt");
			GetFile("plugins/MySEM/Remove_Streaks.txt", url+"plugins/MySEM/Remove_Streaks.txt");
			
		
			if(Startup==true)		
				{GetFile("macros/StartupMacros.txt", url+"macros/StartupMacros.txt");}
			}
		else
			{File f1 = new File("plugins/MySEM/MySEM_Updater.jar");
			File f2 = new File("plugins/MySEM/MySEM_Add_Comment.class");
			File f2s = new File("plugins/MySEM/MySEM_Add_Comment.java");
			File f3 = new File("plugins/MySEM/MySEM_Crop.class");
			File f3s = new File("plugins/MySEM/MySEM_Crop.java");
			File f4 = new File("plugins/MySEM/MySEM_Measure.class");
			File f4s = new File("plugins/MySEM/MySEM_Measure.java");
			File f5 = new File("plugins/MySEM/MySEM_Set_Scale.class");
			File f5s = new File("plugins/MySEM/MySEM_Set_Scale.java");
			File f6 = new File("plugins/MySEM/MySEM_Filters.class");		
			File f6s = new File("plugins/MySEM/MySEM_Filters.java");
			File f6b = new File("plugins/MySEM/MySEM_Line_Correction.class");		
			File f6bs = new File("plugins/MySEM/MySEM_Line_Correction.java");
			File f10a = new File("plugins/MySEM/MySEM_MainFrame.class");
			File f10b = new File("plugins/MySEM/MySEM_MainFrame$MySEM_MainFrame_Panel.class");
			File f10s = new File("plugins/MySEM/MySEM_MainFrame.java");
			File f11a = new File("plugins/MySEM/MySEM_FiltersFrame.class");
			File f11b = new File("plugins/MySEM/MySEM_FiltersFrame$MySEM_FiltersFrame_Panel.class");
			File f11s = new File("plugins/MySEM/MySEM_FiltersFrame.java");
			File f12a = new File("plugins/MySEM/MySEM_Select_Image.class");
			File f12b = new File("plugins/MySEM/MySEM_Select_Image.java");


			File f7 = new File("macros/toolsets/MySEM.txt");
			File f8 = new File("macros/toolsets/FFT.txt");
			File f0 = new File("macros/StartupMacros.txt");	
			File f9 = new File("plugins/MySEM/Remove_Streaks.txt");
		
			f1.delete();
			f2.delete();
			f2s.delete();
			f3.delete();
			f3s.delete();
			f4.delete();
			f4s.delete();
			f5.delete();
			f5s.delete();
			f6.delete();
			f6s.delete();
			f6b.delete();
			f6bs.delete();
			f10a.delete();
			f10b.delete();
			f10s.delete();
			f11a.delete();
			f11b.delete();
			f11s.delete();
			f12a.delete();
			f12b.delete();

			f7.delete();
			f8.delete();
			f0.delete();
			f9.delete();
			
			File f0_orig = new File("macros/StartupMacros_backup.txt");
			if(f0_orig.exists())
				{f0_orig.renameTo(new File("macros/StartupMacros.txt"));}
			else
				{GetFile("macros/StartupMacrosOrig.txt", url+"StartupMacrosOrig.txt");
				File f0_stock = new File("macros/StartupMacrosOrig.txt");	
				f0_stock.renameTo(new File("macros/StartupMacros.txt"));}
			}
	
			System.exit(0);
	}

	int showDialog(String versions) {
		GenericDialog gd = new GenericDialog("MySEM Plugins");
		if(FirstRun==true)		
			{gd.addMessage("Thank you for your interest in the MySEM plugins.\n");  
			gd.addMessage("This installer will install v. "+versions+"");
			gd.addMessage("ImageJ will close after the installation. \n");
			gd.addCheckbox(" Install customized Startup macro", Startup);	
			}
		else
			{gd.addMessage("Current MySEM plugins version: "+currentVersion+"\n");  
			gd.addMessage("It will be upgraded to v. "+versions+"\n");	
			
			gd.addCheckbox(" Update customized Startup macro", Startup);	
			String msg = "\nIf you click \"OK\", ImageJ will: \n \n"+
			"  1. install the new updater \n"+
			"  2. update your existing files \n"+
			"  3. close ImageJ \n \n"+
			"After you restart ImageJ, re-run the plugin updater: \n \n"+
			"  Plugins -> MySEM -> MySEM Updater \n \n"+
			"This will ensure that ImageJ will have all the \n"+
			"required plugins.\n";
			gd.addCheckbox(" Uninstall?", Uninstall);
			gd.addMessage(msg);}

		gd.showDialog();
		Startup = gd.getNextBoolean();
		if(FirstRun==false)			
			{Uninstall = gd.getNextBoolean();}
		
		if (gd.wasCanceled())
			{Cancel=true;
			return -1;}
		else
			{return 0;}
	}

	int showDialogUninst() {
		GenericDialog gdU = new GenericDialog("Uninstall MySEM Plugins");
		
			String msg = "Are you sure you want to uninstall all MySEM plugins?\n";
			gdU.addMessage(msg);

		gdU.showDialog();
		if (gdU.wasCanceled())
			{Cancel=true;
			return -1;}
		else
			{return 0;}
	}
	
	void GetFile(String local, String url) {
		File file = new File(Prefs.getHomeDir() + File.separator + local);
		byte[] jar = getJar(url);
		if (jar==null) return;
		saveJar(file, jar);
		}

	void CheckFile(String local) {
		File file = new File(Prefs.getHomeDir() + File.separator + local);
		try {
			FileInputStream in = new FileInputStream(file);
			in.close();
		} catch (IOException e) {
			FirstRun=true;}			
		}
	
	String getUpgradeVersion() {
		String url = "https://raw.githubusercontent.com/feranick/MySEM/master/source/version.txt";
		String notes = openUrlAsString(url, 20);
		if (notes==null) {
			error("Unable to retrieve updated files.\n Check your Internet connection.");
			return null;
		}
		int index = notes.indexOf("");
		if (index==-1) {
			error("Version file are not in the expected format");
			return null;
		}
		//String version = notes.substring(index, index+7);
		String version = notes;
		return version;
	}

	
	String openUrlAsString(String address, int maxLines) {
		StringBuffer sb;
		try {
			URL url = new URL(address);
			InputStream in = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			sb = new StringBuffer();
			int count = 0;
			String line;
			while ((line=br.readLine())!=null && count++<maxLines)
				sb.append (line + "\n");
			in.close ();
		} catch (IOException e) {sb = null;}
			return sb!=null?new String(sb):null;
	}

	byte[] getJar(String address) {
		byte[] data;
		boolean gte133 = version().compareTo("1.33u")>=0;
		try {
			URL url = new URL(address);
			URLConnection uc = url.openConnection();
			int len = uc.getContentLength();
			IJ.showStatus("Downloading updated MySEM plugins");
			InputStream in = uc.getInputStream();
			data = new byte[len];
			int n = 0;
			while (n < len) {
				int count = in.read(data, n, len - n);
				if (count<0)
					throw new EOFException();
	   			 n += count;
				if (gte133) IJ.showProgress(n, len);
			}
			in.close();
		} catch (IOException e) {
            e.printStackTrace(System.out);
			return null;
		}
		return data;
	}

	void saveJar(File f, byte[] data) {
		try {
			FileOutputStream out = new FileOutputStream(f);
			out.write(data, 0, data.length);
			out.close();
		} catch (IOException e) {
		}
	}


	// Use reflection to get version since early versions
	// of ImageJ do not have the IJ.getVersion() method.
	String version() {
		String version = "";
		try {
			Class ijClass = ImageJ.class;
			Field field = ijClass.getField("VERSION");
			version = (String)field.get(ijClass);
		}catch (Exception ex) {}
		return version;
	}

	//boolean isMac() {
	//	String osname = System.getProperty("os.name");
	//	return osname.startsWith("Mac");
	//}

	void error(String msg) {
		IJ.error("MySEM plugins updater", msg);
	}

	void showAbout()			
		{IJ.showMessage("About MySEM plugins", "Image processing plugins for various SEMs \n\nversion: "
 			+currentVersion+"\n \n"
			+ "2008-2022 Nicola Ferralis - <feranick@hotmail.com> \n \n"
			+ "MySEM plugins are released under GPL version 3.0 \n \n"
			+ "For more information: \n"+url+"\n");
        }


}
