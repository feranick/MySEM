// 2008-2026 - Nicola Ferralis <feranick@hotmail.com>

import ij.plugin.*;
import ij.*;
import ij.gui.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;

/** This plugin implements the Plugins/MySEM/MySEM_Update command. */
public class MySEM_Updater implements PlugIn {

    private static boolean Startup = false; 
    private static boolean Cancel = false; 
    private static boolean FirstRun = false; 
    private static boolean Uninstall = false; 
    public String currentVersion = "5.8";
    public String url = "https://raw.githubusercontent.com/feranick/MySEM/master/source/";

    @Override
    public void run(String arg) {    
        Cancel = false;
        FirstRun = false;
        Uninstall = false;
        
        CheckFile("MySEM" + File.separator + "MySEM_Set_Scale.class");

        if ("about".equals(arg)) {
            showAbout();
            return;
        }

        if (IJ.getApplet() != null) return;        
        String upgradeVersion = getUpgradeVersion();
        if (upgradeVersion == null) return;

        showDialog(upgradeVersion);
        if (Uninstall) {
            showDialogUninst();
        }
        
        if (Cancel) {
            return;
        }

        String pluginsPath = IJ.getDirectory("plugins");
        String macrosPath = IJ.getDirectory("macros");

        if (pluginsPath == null || macrosPath == null) {
            IJ.error("MySEM Updater", "Could not locate valid ImageJ system directories.");
            return;
        }

        if (!Uninstall) { // Installation Mode
            if (Startup && FirstRun) {        
                File f0_orig = new File(macrosPath + "StartupMacros.txt");    
                if (f0_orig.exists()) {
                    f0_orig.renameTo(new File(macrosPath + "StartupMacros_backup.txt"));
                }
            }

            // Standardized targeted file down-loader map
            GetFile(pluginsPath + "MySEM/MySEM_Updater.jar", url + "MySEM_Updater.jar");    
            GetFile(pluginsPath + "MySEM/MySEM_Crop.class", url + "plugins/MySEM/MySEM_Crop.class");
            GetFile(pluginsPath + "MySEM/MySEM_Crop.java", url + "plugins/MySEM/MySEM_Crop.java");
            GetFile(pluginsPath + "MySEM/MySEM_Measure.class", url + "plugins/MySEM/MySEM_Measure.class");
            GetFile(pluginsPath + "MySEM/MySEM_Measure.java", url + "plugins/MySEM/MySEM_Measure.java");
            GetFile(pluginsPath + "MySEM/MySEM_Set_Scale.class", url + "plugins/MySEM/MySEM_Set_Scale.class");
            GetFile(pluginsPath + "MySEM/MySEM_Set_Scale.java", url + "plugins/MySEM/MySEM_Set_Scale.java");
            GetFile(pluginsPath + "MySEM/MySEM_Filters.class", url + "plugins/MySEM/MySEM_Filters.class");
            GetFile(pluginsPath + "MySEM/MySEM_Filters.java", url + "plugins/MySEM/MySEM_Filters.java");
            GetFile(pluginsPath + "MySEM/MySEM_Line_Correction.java", url + "plugins/MySEM/MySEM_Line_Correction.java");
            GetFile(pluginsPath + "MySEM/MySEM_Line_Correction.class", url + "plugins/MySEM/MySEM_Line_Correction.class");
            GetFile(pluginsPath + "MySEM/MySEM_MainFrame.class", url + "plugins/MySEM/MySEM_MainFrame.class");
            GetFile(pluginsPath + "MySEM/MySEM_MainFrame.java", url + "plugins/MySEM/MySEM_MainFrame.java");
            GetFile(pluginsPath + "MySEM/MySEM_MainFrame$MySEM_MainFrame_Panel.class", url + "plugins/MySEM/MySEM_MainFrame$MySEM_MainFrame_Panel.class");
            GetFile(pluginsPath + "MySEM/MySEM_FiltersFrame.class", url + "plugins/MySEM/MySEM_FiltersFrame.class");
            GetFile(pluginsPath + "MySEM/MySEM_FiltersFrame.java", url + "plugins/MySEM/MySEM_FiltersFrame.java");
            GetFile(pluginsPath + "MySEM/MySEM_FiltersFrame$MySEM_FiltersFrame_Panel.class", url + "plugins/MySEM/MySEM_FiltersFrame$MySEM_FiltersFrame_Panel.class");
            GetFile(pluginsPath + "MySEM/MySEM_Select_Image.class", url + "plugins/MySEM/MySEM_Select_Image.class");
            GetFile(pluginsPath + "MySEM/MySEM_Select_Image.java", url + "plugins/MySEM/MySEM_Select_Image.java");

            GetFile(macrosPath + "toolsets/MySEM.txt", url + "macros/toolsets/MySEM.txt");
            GetFile(macrosPath + "toolsets/FFT.txt", url + "macros/toolsets/FFT.txt");
            GetFile(pluginsPath + "MySEM/Remove_Streaks.txt", url + "plugins/MySEM/Remove_Streaks.txt");
            
            if (Startup) {        
                GetFile(macrosPath + "StartupMacros.txt", url + "macros/StartupMacros.txt");
            }
            
            IJ.showMessage("MySEM Setup Complete", "Installation updated successfully.\nPlease restart ImageJ now.");
        } 
        else { // Uninstall Mode
            String[] targetFiles = {
                pluginsPath + "MySEM/MySEM_Updater.jar",
                pluginsPath + "MySEM/MySEM_Add_Comment.class", pluginsPath + "MySEM/MySEM_Add_Comment.java",
                pluginsPath + "MySEM/MySEM_Crop.class", pluginsPath + "MySEM/MySEM_Crop.java",
                pluginsPath + "MySEM/MySEM_Measure.class", pluginsPath + "MySEM/MySEM_Measure.java",
                pluginsPath + "MySEM/MySEM_Set_Scale.class", pluginsPath + "MySEM/MySEM_Set_Scale.java",
                pluginsPath + "MySEM/MySEM_Filters.class", pluginsPath + "MySEM/MySEM_Filters.java",
                pluginsPath + "MySEM/MySEM_Line_Correction.class", pluginsPath + "MySEM/MySEM_Line_Correction.java",
                pluginsPath + "MySEM/MySEM_MainFrame.class", pluginsPath + "MySEM/MySEM_MainFrame.java",
                pluginsPath + "MySEM/MySEM_MainFrame$MySEM_MainFrame_Panel.class",
                pluginsPath + "MySEM/MySEM_FiltersFrame.class", pluginsPath + "MySEM/MySEM_FiltersFrame.java",
                pluginsPath + "MySEM/MySEM_FiltersFrame$MySEM_FiltersFrame_Panel.class",
                pluginsPath + "MySEM/MySEM_Select_Image.class", pluginsPath + "MySEM/MySEM_Select_Image.java",
                macrosPath + "toolsets/MySEM.txt", macrosPath + "toolsets/FFT.txt",
                pluginsPath + "MySEM/Remove_Streaks.txt", macrosPath + "StartupMacros.txt"
            };

            for (String path : targetFiles) {
                File f = new File(path);
                if (f.exists()) f.delete();
            }
            
            File f0_orig = new File(macrosPath + "StartupMacros_backup.txt");
            if (f0_orig.exists()) {
                f0_orig.renameTo(new File(macrosPath + "StartupMacros.txt"));
            } else {
                GetFile(macrosPath + "StartupMacros.txt", url + "StartupMacrosOrig.txt");
            }
            
            IJ.showMessage("MySEM Uninstalled", "All components removed successfully.\nPlease restart ImageJ.");
        }
        
        // Clean close interaction sequence
        if (IJ.getInstance() != null) {
            IJ.getInstance().quit();
        }
    }

    private void showDialog(String versions) {
        GenericDialog gd = new GenericDialog("MySEM Plugins");
        if (FirstRun) {
            gd.addMessage("Thank you for your interest in the MySEM plugins.\n");  
            gd.addMessage("This installer will install v. " + versions);
            gd.addMessage("ImageJ will close after the installation. \n");
            gd.addCheckbox(" Install customized Startup macro", Startup);    
        } else {
            gd.addMessage("Current MySEM plugins version: " + currentVersion + "\n");  
            gd.addMessage("It will be upgraded to v. " + versions + "\n");    
            gd.addCheckbox(" Update customized Startup macro", Startup);    
            
            String msg = "\nIf you click \"OK\", ImageJ will: \n \n"+
            "  1. install the new updater \n"+
            "  2. update your existing files \n"+
            "  3. close ImageJ cleanly \n \n"+
            "After you restart ImageJ, components will be active.\n";
            gd.addCheckbox(" Uninstall?", Uninstall);
            gd.addMessage(msg);
        }

        gd.showDialog();
        Startup = gd.getNextBoolean();
        if (!FirstRun) {
            Uninstall = gd.getNextBoolean();
        }
        
        if (gd.wasCanceled()) {
            Cancel = true;
        }
    }

    private void showDialogUninst() {
        GenericDialog gdU = new GenericDialog("Uninstall MySEM Plugins");
        gdU.addMessage("Are you sure you want to uninstall all MySEM plugins?\n");
        gdU.showDialog();
        if (gdU.wasCanceled()) {
            Cancel = true;
        }
    }
    
    private void GetFile(String localPath, String urlAddress) {
        byte[] jarData = getJar(urlAddress);
        if (jarData == null) return;
        
        File targetFile = new File(localPath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Generate child folder structures safely
        }
        
        try (FileOutputStream out = new FileOutputStream(targetFile)) {
            out.write(jarData, 0, jarData.length);
        } catch (IOException e) {
            IJ.log("Failed to write file locally: " + localPath + " - " + e.getMessage());
        }
    }

    private void CheckFile(String local) {
        String pluginsPath = IJ.getDirectory("plugins");
        if (pluginsPath == null) {
            FirstRun = true;
            return;
        }
        File file = new File(pluginsPath + File.separator + local);
        if (!file.exists()) {
            FirstRun = true;
        }
    }
    
    private String getUpgradeVersion() {
        String versionUrl = "https://raw.githubusercontent.com/feranick/MySEM/master/source/version.txt";
        String notes = openUrlAsString(versionUrl, 20);
        if (notes == null || notes.trim().isEmpty()) {
            error("Unable to retrieve updated files.\nCheck your Internet connection.");
            return null;
        }
        return notes.trim();
    }

    private String openUrlAsString(String address, int maxLines) {
        StringBuilder sb = new StringBuilder();
        try {
            URL urlObj = new URL(address);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlObj.openStream()))) {
                int count = 0;
                String line;
                while ((line = br.readLine()) != null && count++ < maxLines) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private byte[] getJar(String address) {
        // Robust byte collector handles dynamic sizes (no fixed dependency on Content-Length headers)
        try {
            URL urlObj = new URL(address);
            URLConnection uc = urlObj.openConnection();
            IJ.showStatus("Downloading updated MySEM plugins...");
            
            try (InputStream in = uc.getInputStream(); 
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                return out.toByteArray();
            }
        } catch (IOException e) {
            IJ.log("Download failed: " + address + " - " + e.getMessage());
            return null;
        }
    }

    private void error(String msg) {
        IJ.error("MySEM plugins updater", msg);
    }

    private void showAbout() {
        IJ.showMessage("About MySEM plugins", "Image processing plugins for various SEMs \n\nversion: "
            + currentVersion + "\n \n"
            + "2008-2026 Nicola Ferralis - <feranick@hotmail.com> \n \n"
            + "MySEM plugins are released under GPL version 3.0 \n \n"
            + "For more information: \n" + url + "\n");
    }
}
