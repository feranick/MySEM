// 2008-2026 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

/* DESCRIPTION & CUSTOMIZATION INSTRUCTIONS
        This plugin lets you crop the image to remove the standard SEM information bar at the bottom. Optionally, it allows you to add a custom scale bar (this requires a previous calibration).
**/

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.awt.*;
import ij.plugin.*;

public class MySEM_Crop implements PlugInFilter {

    ImagePlus imp;
    String units;
    
    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
        Roi roi = imp.getRoi();   

        int frameHeight = (int) (ip.getHeight() * 0.925);
            
        if (ip.getWidth() == 512)
            {frameHeight = frameHeight + 1;}
        else if (ip.getWidth() == 1024)
            {frameHeight = frameHeight + 5;}
        else if (ip.getWidth() == 2048)
            {frameHeight = frameHeight + 10;}    
        else if (ip.getWidth() == 712)
            {frameHeight = (int) (frameHeight * 0.95);}   //FEI XL30
        else if (ip.getWidth() == 1280)
            {frameHeight = (int) (frameHeight * 0.99);}   //Regulus 8100
        else if (ip.getWidth() == 640)
            {frameHeight = (int) (frameHeight * 0.99);}   //Regulus 8100
        else {
            IJ.error("Cannot apply cropping to this image");
            return;
        }
       
        // Define the target crop selection
        imp.setRoi(0, 0, ip.getWidth(), frameHeight);
        ImagePlus imp2 = new ImagePlus("cropped_" + imp.getTitle(), ip.crop());
        imp.killRoi();
        imp.unlock();		
        
        // Fix: Close the original uncropped window without prompting to save changes
        imp.changes = false; 
        imp.close();

        // Show the new cropped window, automatically setting it as the active window
        imp2.show();        
        imp2.getWindow().repaint();

        // Fix: Scale Bar now executes flawlessly on the newly focused cropped window
        IJ.run("Scale Bar...");
    }
}
