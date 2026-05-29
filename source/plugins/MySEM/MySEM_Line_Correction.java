// 2009-2026 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

/* DESCRIPTION: 
   This plugin corrects line scans by subtracting horizontal line averages */

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.awt.*;
import ij.plugin.*;

public class MySEM_Line_Correction implements PlugInFilter {

    ImagePlus imp;
    String units;
    int fontsize = 13;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        IJ.run("8-bit");
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
        imp.killRoi();
        imp.unlock();
        
        int frameHeight = (int) (ip.getHeight() * 0.925);
        int width = ip.getWidth();
            
        if (width == 512)
            {frameHeight = frameHeight + 1;}
        else if (width == 1024)
            {frameHeight = frameHeight + 5;}
        else if (width == 2048)
            {frameHeight = frameHeight + 10;}
        else {
            IJ.error("Cannot apply to this image");
            return;
        }
		
        // 1. Calculate the true mathematical average across all operational horizontal lines
        double totalLineAveragesSum = 0;
        for (int h = 0; h < frameHeight; h++) {	
            double rowSum = 0;
            for (int x = 0; x < width; x++) {
                rowSum += ip.getPixelValue(x, h);
            }
            totalLineAveragesSum += (rowSum / width); // Divided by row width, not frameHeight
        }	
        
        // Global baseline average of the scanned area
        double globalAverage = totalLineAveragesSum / frameHeight;
		
        // 2. Subtract row-specific line shifts relative to the global baseline average
        for (int h = 0; h < frameHeight; h++) {
            double rowSum = 0;
            for (int x = 0; x < width; x++) {
                rowSum += ip.getPixelValue(x, h);
            }
            
            // Calculate delta exactly once per row line
            double lineAverage = rowSum / width;
            double delta = lineAverage - globalAverage;

            // Apply line shift correction across the row
            for (int x = 0; x < width; x++) {
                int newvalue = (int) ip.getPixelValue(x, h) - (int) delta;		
                ip.set(x, h, newvalue);
            }	
        }	
		
        imp.killRoi();
        imp.updateAndDraw(); // Refresh screen rendering layout context
    }
}
