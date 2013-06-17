// 2008-2013 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

/* DESCRIPTION & CUSTOMIZATION INSTRUCTIONS
        This plugin lets you plot line profiles over linear selections.
**/

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.awt.*;
import ij.plugin.*;

public class MySEM_Measure implements  PlugInFilter {

        ImagePlus imp;
	String units;
	int fontsize = 13;
    int decPlaces = 1;
	
        public int setup(String arg, ImagePlus imp) {
                this.imp = imp;
                return DOES_ALL;
        }

        public void run(ImageProcessor ip) {

		if(ip.getWidth()==512)
			{fontsize=13;}
		else if(ip.getWidth()==1024)
			{fontsize=26;}
		else if(ip.getWidth()==2048)
			{fontsize=42;}	
        //else {IJ.error("Cannot apply to this image");
            //    return;}
        else{}

		Roi roi = imp.getRoi();
		if (roi==null || roi.isLine()==false) {
			IJ.error("Line selection required.");
			return;
		}
          
		Calibration oc = imp.getCalibration().copy();
		units=oc.getUnit();
            
        if(units=="nm")
            {decPlaces=1;}
        else {
            decPlaces=2;}
            
		Line line = (Line)roi;
		String wText = ""+IJ.d2s(roi.getLength(),decPlaces)+" "+units;
		int x = ((line.x1+line.x2)/2)+fontsize/2;

		if(x+ip.getStringWidth(wText)>ip.getWidth()) 
			{x=x-ip.getStringWidth(wText)-fontsize;}

		
		int y = (line.y1+line.y2)/2;
	
		if((line.x1>line.x2 && line.y1<line.y2) || ( line.x1<line.x2 && line.y1>line.y2) || (line.y1<=fontsize && line.y2<=fontsize))
			{y=y+fontsize;}

		if(line.y1+fontsize >= ip.getHeight() && line.y2+fontsize >= ip.getHeight())
			{y=y-fontsize;}
		
			
		writetext(ip, wText, x, y);
		ip.setLineWidth(2);
		ip.drawLine(line.x1, line.y1, line.x2, line.y2);
		imp.getWindow().repaint();
        }

        

	private void writetext(ImageProcessor ip, String wText, int x, int y) {
		ip.setFont(new Font("Arial", Font.BOLD, fontsize));			
		ip.setRoi(x-5, y-ip.getFontMetrics().getHeight()-5, ip.getStringWidth(wText) + 10, ip.getFontMetrics().getHeight() + 10);
		ip.add(150);
		ip.drawString(wText, x, y);
		}
}
