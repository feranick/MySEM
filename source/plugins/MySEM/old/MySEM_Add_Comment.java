// 2008-2009 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

/* DESCRIPTION & CUSTOMIZATION INSTRUCTIONS
        This plugin lets you add a custom tag to the NovelX standard image, right above the technical details line
**/

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.awt.*;
import ij.plugin.*;

public class MySEM_Add_Comment implements  PlugInFilter {


        ImagePlus imp;
	String tag;
	int fontsize = 13;
	
        public int setup(String arg, ImagePlus imp) {
                this.imp = imp;
                return DOES_ALL;
        }

        public void run(ImageProcessor ip) {
		
		if(ip.getHeight()==512 && ip.getWidth()==512)
			{fontsize=13;}
		else if(ip.getHeight()==1024 && ip.getWidth()==1024)
			{fontsize=26;}
		else if(ip.getHeight()==2048 && ip.getWidth()==2048)
			{fontsize=52;}	
		else {IJ.error("Not a NovelX MySEM image");
			return;}		 
		
		if (doDialog()) {
			writetext(ip,tag, ip.getWidth()*32/100, ip.getHeight()*96/100);}
		
		
        }

        

	private void writetext(ImageProcessor ip, String wText, int x, int y) {
		ip.setColor(new Color(0,0,250));
	
		ip.setFont(new Font("Arial", Font.PLAIN, fontsize));			
		ip.add(0);
		ip.drawString(wText, x, y);
		}

	private boolean doDialog() {
                GenericDialog gd = new GenericDialog("Add Custom Tag");
		gd.addStringField("Tag: ", tag, 30);		 
		gd.showDialog();
                if (gd.wasCanceled()) {return false;}
		tag = gd.getNextString();              
		return true;
        }

}
