// 2008-2022 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

/* DESCRIPTION & CUSTOMIZATION INSTRUCTIONS
        This plugin lets you apply filters to improve image quality
**/

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.Prefs;
import ij.measure.Calibration;
import java.awt.*;
import ij.plugin.*;

public class MySEM_Filters implements  PlugInFilter {

        ImagePlus imp;
	private static boolean brcontr = Prefs.get("MySEMFiltersBrContr.boolean", false); // if true, it smooths the image
	private static boolean smooth = Prefs.get("MySEMFiltersSmooth.boolean", false); // if true, it smooths the image
	private static boolean sharpen = Prefs.get("MySEMFiltersSharpen.boolean", false); // if true, it sharpen the image
	private static boolean encontr = Prefs.get("MySEMFiltersEnContr.boolean", false); // if true, it enhance contrast in the image
	private static boolean unsharp = Prefs.get("MySEMFiltersUnsharp.boolean", false); // if true, it runs the unsharp mask filter in the image
	private static boolean streaks = Prefs.get("MySEMFiltersRemoveStreaks.boolean", false); // if true, it runs the FFTRemoveStreaks algorithm to the image
	private static boolean linecorr = Prefs.get("MySEMFiltersLineCorrection.boolean", false); // if true, it runs the line correction algorithm to the image
	private static boolean median = Prefs.get("MySEMFiltersMedian.boolean", false); // if true, it runs the median filter algorithm to the image


        public int setup(String arg, ImagePlus imp) {
                this.imp = imp;
                return DOES_ALL;
        }

        public void run(ImageProcessor ip) {
                if (doDialog()) {
		
				imp.unlock();

			IJ.run("MySEM Select Image");
                        
			if(brcontr==true)
				{IJ.run("Brightness/Contrast...");
				brcontr = false;}	

			if(smooth==true)
				{IJ.run("Smooth");
				smooth = false;}

			if(sharpen==true)
				{IJ.run("Sharpen");
				sharpen = false;}

			if(encontr==true)
				{IJ.run("Enhance Contrast");
				encontr = false;}
			
			if(unsharp==true)
				{IJ.run("Unsharp Mask...");
				unsharp = false;}
			
			if(streaks==true)
				{IJ.run("Remove Streaks");
				streaks = false;}

			if(linecorr==true)
				{IJ.run("MySEM Line Correction");
				linecorr = false;}

			if(median==true)
				{IJ.run("Median...");
				median = false;}
				
                }
        }

        private boolean doDialog() {
                GenericDialog gd = new GenericDialog("Image Filters");
		gd.addCheckbox(" Brightness/Contrast", brcontr);
		gd.addCheckbox(" Smooth image", smooth);
		gd.addCheckbox(" Sharpen image", sharpen);
		gd.addCheckbox(" Enhance contrast ", encontr);
		gd.addCheckbox(" Unsharp mask ", unsharp);
		gd.addCheckbox(" Remove streaks ", streaks);
		gd.addCheckbox(" Line correction ", linecorr);
		gd.addCheckbox(" Median filter ", median);
		
		
		gd.showDialog();
                if (gd.wasCanceled()) {return false;}
		
		brcontr = gd.getNextBoolean();

		smooth = gd.getNextBoolean();
		sharpen = gd.getNextBoolean();
		encontr = gd.getNextBoolean();
		unsharp = gd.getNextBoolean();
		streaks = gd.getNextBoolean();
		linecorr = gd.getNextBoolean();
		median = gd.getNextBoolean();

		Prefs.set("MySEMFiltersBrContr.boolean", brcontr);		
		Prefs.set("MySEMFiltersSmooth.boolean", smooth);
		Prefs.set("MySEMFiltersSharpen.boolean", sharpen);
		Prefs.set("MySEMFiltersEnContr.boolean", encontr);
		Prefs.set("MySEMFiltersUnsharp.boolean", unsharp);
		Prefs.set("MySEMFiltersRemoveStreaks.boolean", streaks);
		Prefs.set("MySEMFiltersLineCorrection.boolean", linecorr);
		Prefs.set("MySEMFiltersMedian.boolean", median);

		return true;
        }
}


