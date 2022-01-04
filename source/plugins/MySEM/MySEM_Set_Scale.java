// 2008-2022 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

/* DESCRIPTION & CUSTOMIZATION INSTRUCTIONS
        This plugin lets you calibrate images spatially using user input magnifications,
        calibration values and length units. The calibration can optionally be set as global. After
        spatial calibration the plugin will also optionally run the scale-bar plugin.
**/

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.Prefs;
import ij.measure.Calibration;
import java.awt.*;
import ij.plugin.*;
import java.awt.event.*;
import ij.gui.DialogListener;


public class MySEM_Set_Scale implements  PlugInFilter,DialogListener {

    ImagePlus imp;
    private static boolean addScaleBar = Prefs.get("MySEMSetScaleAddScale.boolean", false);  //  if true run scalebar plugin - available in version 1.28h
    private static boolean isGlobalCal = Prefs.get("MySEMSetScaleGlobal.boolean", true); // if true, set selected calibration as the global calibration
	private static boolean remBar = Prefs.get("MySEMSetScaleRemBar.boolean", false); // if true, remove NovelX information bar
	private static boolean filters = Prefs.get("MySEMSetScaleFilters.boolean", false); // if true, it enhance contrast in the image
	private static boolean zcal = Prefs.get("MySEMSetScaleZCal.boolean", false); // if true, it runs the Z-Calibration
    private static int unitsIndex = (int) Prefs.get("MySEMSetScaleUnits.int", 1);
	private static int calIndex = (int) Prefs.get("MySEMSetScaleCalIndex.int", 0);
    private static boolean calcust = Prefs.get("MySEMSetScaleCalCust.boolean", false); // if true, use the custom calibration factor

    // nominal magnification settings of the microscope
	private static double mag = Prefs.get("MySEMSetScaleMag.double", 5000);
    
    // nominal size scale bar
    private static double sbd = Prefs.get("MySEMSetScaleSbd.double", 1.00);
    
    // Additional correction factor (if needed)
    private static double addCF = Prefs.get("MySEMSetScaleAddCF.double", 1.00);
	
	// calibration factor in formula: [Scale bar (in nm)]*(Mag)/(2000*cal) = #pixels for a scalebar=1000 nm    	
	private static double cal = Prefs.get("MySEMSetScaleCal.double", 100.89);
	private static double xscale;
    private static int pixl = (int) Prefs.get("MySEMSetScalePixl.int", 50);

    //Scaling factors for differences in calibration between different image sizes
    public static int refWidth=512;
    public static double actualWidthCoeff=1.00;
    public static int imWidth = 512;

    
    // units for the spacial calibrations given in xscales array above
	private static String[] microscope =  {"Hitachi Regulus 8100", "FEI/Philips XL30","FEI Helios 600 Nanolab", "Zeiss LEO 1550", "Agilent 8500 FE-SEM", "NovelX MySEM", "ORNL STEM", "Custom"};
	private static String[] units =  {"nm", "um", "mm"};
	private static String Units = "";


        public int setup(String arg, ImagePlus imp) {
                this.imp = imp;
                return DOES_ALL;
        }

        public void run(ImageProcessor ip) {
            
            imWidth=ip.getWidth();            
            
                if (doDialog()) {
			 Calibration oc = imp.getCalibration().copy();
                        oc.setUnit(Units);
                        oc.pixelWidth=xscale;
                        oc.pixelHeight=oc.pixelWidth;

                        if (isGlobalCal) {
                                imp.setGlobalCalibration(oc);
                                int[] list = WindowManager.getIDList();
                                if (list==null) return;
                                for (int i=0; i<list.length; i++) {
                                        ImagePlus imp2 = WindowManager.getImage(list[i]);
                                        if (imp2!=null) imp2.getWindow().repaint();
                                }
                                } else {
                                imp.setGlobalCalibration(null);
                                imp.setCalibration(oc);
                                imp.getWindow().repaint();
                        }
				imp.unlock();
                    
			if(zcal==true)
				{IJ.run("16-bit");
				IJ.run("Calibrate...");
				zcal = false;}

			if(filters==true)
				{IJ.run("MySEM Filters");
				filters = false;}


			if (remBar==true){
				int frameHeight = (int) (ip.getHeight()*0.925);
                
                if(ip.getWidth()==512)
                {frameHeight = frameHeight+1;}
                else if(ip.getWidth()==1024)
                {frameHeight = frameHeight+5;}
                else if(ip.getWidth()==2048)
                {frameHeight = frameHeight+10;}
                else if(ip.getWidth()==712)
                {frameHeight = (int) (frameHeight*0.95);}   //FEI XL30
                else {IJ.error("Cannot apply cropping to this image");
                    return;}
               
                // Eventually add ability to customize settings per microscope

				imp.setRoi(0, 0, ip.getWidth(), frameHeight);
			      new ImagePlus("duplicate",ip.crop()).show();
				isGlobalCal = true;                    
				}  

			if (addScaleBar==true){
                                IJ.run("Scale Bar...");
                        }
                }
        }
    
       public boolean doDialog() {
        GenericDialog gd = new GenericDialog("Calibrate SEM image...");
		gd.addChoice("Microscope: ", microscope, microscope[calIndex]);
		gd.addNumericField("Custom magnification: ", mag, 0);	
		//gd.addNumericField("Nominal scale bar [nm]: ", Nsb, 0);
        gd.addChoice("Units in scale bar:", units, units[unitsIndex]);
		gd.addCheckbox(" Global calibration", isGlobalCal);
		gd.addCheckbox(" Z-calibration", zcal);
        gd.addCheckbox(" Add scale bar", addScaleBar);
		gd.addCheckbox(" Remove info bar at the bottom", remBar);		
		gd.addCheckbox(" Additional filters", filters);
           
		gd.showDialog();
            if (gd.wasCanceled()) {return false;}
		calIndex=gd.getNextChoiceIndex();		
		mag = gd.getNextNumber();
		//Nsb = gd.getNextNumber();
        unitsIndex=gd.getNextChoiceIndex();
		Units=units[unitsIndex]; 
		isGlobalCal = gd.getNextBoolean();
		zcal = gd.getNextBoolean();
        addScaleBar = gd.getNextBoolean();
		remBar =  gd.getNextBoolean();
		filters = gd.getNextBoolean();
           
        actualWidthCoeff=(double) imWidth/refWidth;      // set up the image size correction
           
		if(gd.invalidNumber()==true)
			{IJ.error("Not a valid number: calibration not performed");
			return false;}   

        if(calIndex==0) {
                cal=247.88;}    // FEI XL30

        if(calIndex==1) {
                cal=242;}    // FEI XL30
        
        if(calIndex==2) {
               cal=249.1;}    // FEI Helios
           
        if(calIndex==3)
			{cal=113.556;}       // Zeiss Leo 1550
            
        if(calIndex==4) {
			cal=201.78;}      // Agilent 8500
		
		if(calIndex==5)
			{cal=201.78;}     // NovelX MySEM

        if(calIndex==6)
			{
            GenericDialog gd2 = new GenericDialog("ORNL STEM");
            
            //gd2.addNumericField(" Magnification: ", mag, 0);
            //gd2.addNumericField("Calibration factor [C]: ", cal, 2);
            gd2.addNumericField(" Width in pixels of the image: ", pixl, 0);
            gd2.addNumericField(" Nominal distance for the full width of the image [nm]: ", sbd, 3);
            gd2.addNumericField(" Additional correction factor (if needed): ", addCF, 3);
                
            gd2.addCheckbox(" Use calibration factor:", calcust);
            gd2.addNumericField(" Calibration factor: ", cal, 4);
            gd2.addDialogListener(this);
                
            gd2.showDialog();
        
            //mag = gd2.getNextNumber();
			pixl = (int) gd2.getNextNumber();
            sbd = gd2.getNextNumber();
            addCF = gd2.getNextNumber();
            calcust =  gd2.getNextBoolean();
            
            if(calcust==true)
                {cal = gd2.getNextNumber();}

            if(calcust==false)
                {cal=(sbd/pixl)/addCF;}
            
            if(gd2.invalidNumber()==true)
				{IJ.error("Not a valid number: calibration not performed");
				return false;}
            if (gd2.wasCanceled()) {
				return false;}
                
			}

		else
		if(calIndex==7)
			{
            GenericDialog gd2 = new GenericDialog("Custom microscope...");
            
            gd2.addNumericField(" Magnification: ", mag, 0);
            //gd2.addNumericField("Calibration factor [C]: ", cal, 2);
            gd2.addNumericField(" Number of pixels of the scale bar in image: ", pixl, 0);
           
            gd2.addNumericField(" Nominal distance in scale bar [um]: ", sbd, 3);
                
            gd2.addCheckbox(" Use calibration factor:", calcust);
            gd2.addNumericField(" Calibration factor: ", cal, 2);
            gd2.addDialogListener(this);
                
            gd2.showDialog();
        
            mag = gd2.getNextNumber();
			pixl = (int) gd2.getNextNumber();
            sbd = gd2.getNextNumber();
            calcust =  gd2.getNextBoolean();
            
            if(calcust==true)
                {cal = gd2.getNextNumber();}

            if(calcust==false)
                {cal=mag*sbd*actualWidthCoeff/pixl;}
            
            if(gd2.invalidNumber()==true)
				{IJ.error("Not a valid number: calibration not performed");
				return false;}
            if (gd2.wasCanceled()) {								
				return false;}
                
			}
			        
		xscale=(1000*cal)/(mag*actualWidthCoeff);
        
            
		if(unitsIndex==1)
			xscale=xscale/1E3;
		else
		if(unitsIndex==2)
		 	xscale=xscale/1E6;
        
		Prefs.set("MySEMSetScaleUnits.int", unitsIndex);
		Prefs.set("MySEMSetScaleCalIndex.int", calIndex);
		Prefs.set("MySEMSetScaleCal.double", cal);
		Prefs.set("MySEMSetScaleMag.double", mag);      
		Prefs.set("MySEMSetScaleAddScale.boolean", addScaleBar);  
        Prefs.set("MySEMSetScaleGlobal.boolean", isGlobalCal); 
		Prefs.set("MySEMSetScaleZCal.boolean", zcal); 
		Prefs.set("MySEMSetScaleRemBar.boolean", remBar);
        Prefs.set("MySEMSetScaleCalCust.boolean", calcust);
		Prefs.set("MySEMSetScaleFilters.boolean", filters);
        Prefs.set("MySEMSetScalePixl.int", pixl);
        Prefs.set("MySEMSetScaleSbd.double", sbd);
        Prefs.set("MySEMSetScaleAddCF.double", addCF);
           
		return true;
        }
    
    // Called after modifications to the dialog. Returns true if valid input.
    public boolean dialogItemChanged(GenericDialog gd2, AWTEvent e) {
        //IJ.log("OK");
        
        mag = gd2.getNextNumber();
        pixl = (int) gd2.getNextNumber();
        sbd = gd2.getNextNumber();
        cal=mag*sbd*actualWidthCoeff/pixl;
    
        IJ.showStatus("Calibration factor = "+IJ.d2s(cal,2));
        return true;
    }
}


