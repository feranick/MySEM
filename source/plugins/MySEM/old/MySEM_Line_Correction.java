// 2009 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

/* DESCRIPTION: 
   This plugin correct line scans by subtracting horizontal line averages */

// version 1.0: initial release.

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.awt.*;
import ij.plugin.*;

public class MySEM_Line_Correction implements  PlugInFilter {
	 private static double linesize = Prefs.get("MySEMLineCorrectionSize.double", 1);  //  if true run scalebar plugin - available in version 1.28h
        ImagePlus imp;
	String units;
	int fontsize = 13;
	//Line line;
	//Rectangle rct;

        public int setup(String arg, ImagePlus imp) {
                this.imp = imp;
		IJ.run("8-bit");
                return DOES_ALL;
        }

        public void run(ImageProcessor ip) {
		if (doDialog()) {
			imp.killRoi();
			imp.unlock();
			int frameHeight = (int) (ip.getWidth()*0.92);

			if(ip.getHeight()==512 && ip.getWidth()==512)
				{frameHeight = frameHeight+2;}
			else if(ip.getHeight()==1024 && ip.getWidth()==1024)
				{frameHeight = frameHeight+5;}
			else if(ip.getHeight()==2048 && ip.getWidth()==2048)
				{frameHeight = frameHeight+10;}	
			else {IJ.error("Not a NovelX MySEM image");
				return;}
			
			//Collect an average for each horizontal line.
			double average = 0;
			for (int h=0; h<frameHeight; h++)
				{	
				imp.setRoi(0,h, ip.getWidth(), 1);
				double sum = 0;
			
				for (int x=0; x<ip.getWidth(); x++) {
					sum += ip.getPixelValue(x, h)/linesize;
					}
				average= average+sum;
				}	
			//collective average of all horizontal lines
			average= average/linesize;
		
			//subtract collective average from every horizontal line.
			for (int h=0; h<frameHeight; h++)
				{imp.setRoi(0,h, ip.getWidth(), 1);
				double sum = 0;
				double delta = 0;
				for (int x=0; x<ip.getWidth(); x++) {
					sum += ip.getPixelValue(x, h)/linesize;
					delta=sum-average;
					}

				for (int x=0; x<ip.getWidth(); x++) {
					int newvalue=(int)ip.getPixelValue(x, h)-(int) delta;		
					ip.set(x, h, newvalue);
					
					}	
				
				}	
			
 			imp.killRoi();
			
		}	        
	}


private boolean doDialog() {
                GenericDialog gd = new GenericDialog("Options...");
		gd.addNumericField("Line thickness: ", linesize, 0);	
		
		gd.showDialog();

                if (gd.wasCanceled()) {return false;}		
		linesize = gd.getNextNumber();
		
		if(gd.invalidNumber()==true)
			{IJ.error("Not a valid number: calibration not performed");
			return false;}   


		Prefs.set("MySEMLineCorrectionSize.double", linesize);
		return true;
        }
        
}
