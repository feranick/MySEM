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

    @Override
    public void run(String arg) {
        // Spawn a background thread to keep the ImageJ UI responsive in Java 6
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                executeServerLoop();
            }
        });
        serverThread.start();
        IJ.showStatus("mySEM Server listening on port 4444...");
    }

    private void executeServerLoop() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            IJ.error("Server Error", "Could not listen on port: 4444.");
            return; 
        }

        // Declare stream resources externally for manual Java 6 lifecycle scoping
        Socket clientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            IJ.log("Client connected successfully.");
            out.println("Server Ready"); 

            String fromClient;
            ImagePlus imp = null;
            ImageProcessor ip = null;

            while (true) {
                fromClient = in.readLine();

                if (fromClient == null || "Bye.".equals(fromClient)) {
                    if (fromClient != null) {
                        out.println(fromClient);
                    }
                    IJ.log("Server closing connection sequence.");
                    IJ.showMessage("Server closed");
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
                        IJ.log("Failed to open image at path: " + path);
                    }
                } 
                else {
                    imp = WindowManager.getCurrentImage();
                    if (imp == null) {
                        out.println("Error: No open image window found.");
                        continue;
                    }
                    ip = imp.getProcessor();

                    if (fromClient.equals("invert")) {
                        ip.invert();
                        imp.updateAndDraw();
                    }
                    else if (fromClient.equals("binary")) {
                        new ImageConverter(imp).convertToGray8();
                        ip = imp.getProcessor(); 
                        imp.updateAndDraw();
                    }
                    else if (fromClient.equals("smooth")) {
                        ip.smooth();
                        imp.updateAndDraw();
                    }
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
                    // Fix: Commented out to resolve the missing dependency compilation block error
                    /* else if (fromClient.equals("addcomment")) {
                        MySEM_Add_Comment ms = new MySEM_Add_Comment();
                        ms.setup("", imp);
                        ms.run(ip);
                    }
                    */
                    else if (fromClient.equals("crop")) {
                        MySEM_Crop ms = new MySEM_Crop();
                        ms.setup("", imp);
                        ms.run(ip);
                        
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

                out.println(fromClient); 
            }

        } catch (IOException e) {
            IJ.log("Connection runtime error: " + e.getMessage());
        } finally {
            // Safe, explicit sequential resource teardown for older JVM targets
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try { in.close(); } catch (IOException ignored) {}
            }
            if (clientSocket != null) {
                try { clientSocket.close(); } catch (IOException ignored) {}
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                try { serverSocket.close(); } catch (IOException ignored) {}
            }
            IJ.log("Server execution loop terminated cleanly.");
        }
    }
}
