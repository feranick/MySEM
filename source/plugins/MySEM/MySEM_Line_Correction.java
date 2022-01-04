// 2009-2022 - Nicola Ferralis <feranick@hotmail.com>

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
		imp.killRoi();
		imp.unlock();
            int frameHeight = (int) (ip.getHeight()*0.925);
            
            if(ip.getWidth()==512)
			{frameHeight = frameHeight+1;}
            else if(ip.getWidth()==1024)
			{frameHeight = frameHeight+5;}
            else if(ip.getWidth()==2048)
			{frameHeight = frameHeight+10;}
            else {IJ.error("Cannot apply to this image");
                return;}
            // Eventually add ability to customize settings per microscope
		
		//Collect an average for each horizontal line.
		double average = 0;
		for (int h=0; h<frameHeight; h++)
			{	
			imp.setRoi(0,h, ip.getWidth(), 1);
			double sum = 0;
			
			for (int x=0; x<ip.getWidth(); x++) {
				sum += ip.getPixelValue(x, h)/frameHeight;
				}
			average= average+sum;
			}	
		//collective average of all horizontal lines
		average= average/frameHeight;
		
		//subtract collective average from every horizontal line.
		for (int h=0; h<frameHeight; h++)
			{imp.setRoi(0,h, ip.getWidth(), 1);
			double sum = 0;
			double delta = 0;
			for (int x=0; x<ip.getWidth(); x++) {
				sum += ip.getPixelValue(x, h)/frameHeight;
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
