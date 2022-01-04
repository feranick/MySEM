// 2008-2022 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

/* DESCRIPTION & CUSTOMIZATION INSTRUCTIONS
        This plugin lets you crop the image to remove the standard NovelX information bar at the bottom. Optionally, it allows you to add a custom scale bar (this requires a previous calibration). 
**/

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.awt.*;
import ij.plugin.*;

public class MySEM_Select_Image implements  PlugInFilter {


        ImagePlus imp;
	String units;
	
        public int setup(String arg, ImagePlus imp) {
                this.imp = imp;
                return DOES_ALL;
        }

        public void run(ImageProcessor ip) {
		Roi roi = imp.getRoi();   

		int frameHeight = (int) (ip.getHeight()*0.925);

            if(ip.getWidth()==512)
			{frameHeight = frameHeight+1;}
            else if(ip.getWidth()==1024)
			{frameHeight = frameHeight+5;}
            else if(ip.getWidth()==2048)
			{frameHeight = frameHeight+10;}
            else if(ip.getWidth()==712)
            {frameHeight = (int) (frameHeight*0.95);}   //FEI XL30
            else {IJ.error("Cannot apply to this image");
                return;}
            // Eventually add ability to customize settings per microscope
       
		imp.setRoi(0, 0, ip.getWidth(), frameHeight);
      }

        
}
