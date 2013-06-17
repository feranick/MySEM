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
 */ 

import java.net.*;
import java.io.*;

import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.io.*;

// these libraries only needed if we add image processing features
import ij.plugin.filter.*;
import ij.measure.*;
import ij.Prefs;
import ij.plugin.frame.*;
import ij.ImagePlus.*;




public class ij_Server7 implements PlugIn{

	public void run(String arg) {

		// initialize socket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
			IJ.showMessage("Could not listen on port: 4444.");
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
			IJ.showMessage("Accept failed.");
            System.exit(1);
        }

		PrintWriter out = null;
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			IJ.showMessage("Could not initialize PrintWriter out.");
			System.exit(1);
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		}catch (IOException e) {
			IJ.showMessage("Could not initialize PrintWriter in.");
			System.exit(1);
		}

		String fromClient = null;
        out.println(fromClient);

        ImagePlus imp = null;
        ImageProcessor ip = null;

		//define action variables, or keep as string commands

		//begin program loop
        while (true) {
            try{
				fromClient = in.readLine();
                if (fromClient.equals("Bye.")) {
                    out.println(fromClient);
                    IJ.showMessage("server closed");
                    break;
                }
			} catch (IOException e){
				IJ.showMessage("Could not initialize inputLine.");
				System.exit(1);
			}
            if (fromClient.regionMatches(0,"open ",0,5)) {
                
                String path = fromClient.replace("open ", "");

                Opener op = new Opener();
                imp = op.openImage(path);
                imp.show();
                ip = imp.getProcessor();

                MySEM_Set_Scale ms = new MySEM_Set_Scale(); //old image window is still active after crop and scaling
                ms.setup("",imp);
                ms.run(ip);
            }
            else{ //put this in a try/catch, just in case no image is open
                if (fromClient.equals("invert")) {
				ip.invert();
				imp.updateAndDraw();
                }
                //binary
                else if (fromClient.equals("binary")) {
                    new ImageConverter(imp).convertToGray8(); //need to do this without saying new
                }
                //smooth
                else if (fromClient.equals("smooth")) {
                    ip.smooth();
                    imp.updateAndDraw();
                }
                //threshold
                else if (fromClient.equals("threshold")) {
                    new ImageConverter(imp).convertToGray8();
                    ThresholdAdjuster ta = new ThresholdAdjuster();
                    imp.unlock(); //unlocks the image
                }
                //particle
                else if (fromClient.equals("particle"))
                {
                    ip = imp.getProcessor(); //reinitialize image
                    ParticleAnalyzer pa = new ParticleAnalyzer();
                    pa.setup("", imp);
                    pa.run(ip);
                }
                //plugins from nicola
                //addcomment
                else if (fromClient.equals("addcomment"))
                {
                    MySEM_Add_Comment ms = new MySEM_Add_Comment();//only adds comment after client closes
                    ms.setup("", imp);
                    ms.run(ip);
                }
                //crop
                else if (fromClient.equals("crop")) // old image is still active, not cropped copy
                {
                    MySEM_Crop ms = new MySEM_Crop();
                    ms.setup("", imp);
                    ms.run(ip);
                }
                //measure
                else if (fromClient.equals("measure"))
                {
                    MySEM_Measure ms = new MySEM_Measure();
                    ms.setup("", imp);
                    ms.run(ip);
                }
                //filters
                else if (fromClient.equals("filters")) {
                    MySEM_Filters ms = new MySEM_Filters();
                    ms.setup("",imp);
                    ms.run(ip);
                }
                //filters
                else if (fromClient.equals("background")) {
                    BackgroundSubtracter bs = new BackgroundSubtracter();
                    bs.setup("",imp); //may not be necessary
                    bs.run(ip);
                    imp.updateAndDraw();
                    //bs.showDialog(imp,"mySEM subtract background",PluginFilterRunner pfr);
                }

            }

			out.println(fromClient); //handshake
            
        }

		// close
        out.close();
		try {
			in.close();
			clientSocket.close();
			serverSocket.close();
		} catch (IOException e){
			IJ.showMessage("Could not close.");
			System.exit(1);
		}
    }
}
