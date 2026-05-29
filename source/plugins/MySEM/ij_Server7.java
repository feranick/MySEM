/* ij_Server5.java
 * for use with ImageJ
 * 
 * plugin opens desired file (from string input) and calls on MySEM_Set_Scale.
 * then, plugin can perform various operations (e.g. invert, binary, threshold)
 * when called from string input from the server, ijServer.java
 *
 * different way to close
 * 
 * things to fix:
 * -need to change binary so that it does not call new? sometimes other commands
 * do not work after calling on binary
 * -need server to send client semMag
 * -if image is cropped, the old image is still active. need to change
 *
 * jason kawasaki
 * 10:48am, June 19, 2009
 * Nicola Ferralis
 * May 29, 2026
 */

import java.net.*;
import java.io.*;
import java.awt.*;

import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.*;
import ij.io.*;
import ij.plugin.filter.*;
import ij.measure.*;
import ij.Prefs;
import ij.plugin.frame.*;

public class ij_Server7 implements PlugIn {

    public void run(String arg) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            IJ.showMessage("Could not listen on port: 4444.");
            return;
        }

        try (
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String fromClient;
            ImagePlus imp = null;
            ImageProcessor ip = null;

            while ((fromClient = in.readLine()) != null) {
                if (fromClient.equals("Bye.")) {
                    out.println(fromClient);
                    IJ.showMessage("server closed");
                    break;
                }

                if (fromClient.startsWith("open ")) {
                    String path = fromClient.substring(5);
                    Opener op = new Opener();
                    imp = op.openImage(path);
                    if (imp != null) {
                        imp.show();
                        ip = imp.getProcessor();

                        MySEM_Set_Scale ms = new MySEM_Set_Scale(); 
                        ms.setup("", imp);
                        ms.run(ip);
                    } else {
                        IJ.log("Error: Image could not be opened at path: " + path);
                        }
                } 
                else {
                    // Safety check: verify an image window is actually open
                    imp = WindowManager.getCurrentImage();
                    if (imp == null) {
                        out.println("Error: No image open.");
                        continue;
                    }
                    ip = imp.getProcessor();

                    if (fromClient.equals("invert")) {
                        ip.invert();
                        imp.updateAndDraw();
                    }
                    // Fix: Sync the processor pointer right after changing the image matrix structure
                    else if (fromClient.equals("binary")) {
                        new ImageConverter(imp).convertToGray8(); 
                        ip = imp.getProcessor(); 
                        imp.updateAndDraw();
                    }
                    else if (fromClient.equals("smooth")) {
                        ip.smooth();
                        imp.updateAndDraw();
                    }
                    // Fix: Sync the processor pointer here as well
                    else if (fromClient.equals("threshold")) {
                        new ImageConverter(imp).convertToGray8();
                        ip = imp.getProcessor(); 
                        ThresholdAdjuster ta = new ThresholdAdjuster();
                        imp.unlock(); 
                    }
                    else if (fromClient.equals("particle")) {
                        ParticleAnalyzer pa = new ParticleAnalyzer();
                        pa.setup("", imp);
                        pa.run(ip);
                    }
                    else if (fromClient.equals("addcomment")) {
                        MySEM_Add_Comment ms = new MySEM_Add_Comment();
                        ms.setup("", imp);
                        ms.run(ip);
                    }
                    // Fix: Catch the freshly active image context from the WindowManager after crop closure
                    else if (fromClient.equals("crop")) {
                        MySEM_Crop ms = new MySEM_Crop();
                        ms.setup("", imp);
                        ms.run(ip);
                        
                        // Re-sync server tracking references to the new window layout
                        imp = WindowManager.getCurrentImage();
                        if (imp != null) {
                            ip = imp.getProcessor();
                        }
                    }
                    else if (fromClient.equals("measure")) {
                        MySEM_Measure ms = new MySEM_Measure();
                        ms.setup("", imp);
                        ms.run(ip);
                    }
                    else if (fromClient.equals("filters")) {
                        MySEM_Filters ms = new MySEM_Filters();
                        ms.setup("", imp);
                        ms.run(ip);
                    }
                    else if (fromClient.equals("background")) {
                        BackgroundSubtracter bs = new BackgroundSubtracter();
                        bs.setup("", imp);
                        bs.run(ip);
                        imp.updateAndDraw();
                    }
                }
                out.println(fromClient); // handshake loop
            }
        } catch (IOException e) {
            IJ.log("Server socket error: " + e.getMessage());
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try { serverSocket.close(); } catch (IOException ignored) {}
            }
        }
    }
}
