// 2008-2013 - Nicola Ferralis <feranick@hotmail.com>

// Released under Gnu Public License (GPL) v. 3.0.
// http://www.gnu.org/licenses/gpl-3.0.txt

//ALPHA SOFTWARE - USE at your OWN RISK!

/* DESCRIPTION & CUSTOMIZATION INSTRUCTIONS
        This plugin lets you apply filters to improve image quality
**/

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import ij.plugin.*;
import ij.plugin.filter.Duplicater;

public class MySEM_FiltersFrame implements  PlugInFilter {


        ImagePlus imp;

        public int setup(String arg, ImagePlus imp) {
                this.imp = imp;
                return DOES_ALL;
        }

        public void run(ImageProcessor ip) {
		imp.unlock();
		//IJ.run("Duplicate...");
		 //ImagePlus imp2 = WindowManager.getCurrentImage();
       		    ImageCanvas cc = new ImageCanvas(imp);
       		    new MySEM_FiltersFrame_Panel(imp, cc);               
	
		
}


class MySEM_FiltersFrame_Panel extends ImageWindow implements ActionListener {
    
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9;
 	private Button button10, button11, button12, button13, button1b, button1c;
	String units;
	int fontsize = 13;
	
        MySEM_FiltersFrame_Panel(ImagePlus imp, ImageCanvas ic) {
		super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
	    Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
            
	    button1 = new Button(" Brightness/Contrast ");
        button1.addActionListener(this);
	    panel.add(button1);

	    button1b = new Button(" Threshold Adjuster ");
        button1b.addActionListener(this);
	    panel.add(button1b);

	    button1c = new Button(" Particle Analysis ");
        button1c.addActionListener(this);
	    panel.add(button1c);
	   
	    Panel panel2 = new Panel();
        panel2.setLayout(new GridLayout(1, 4));
	    
        button2 = new Button(" Smooth ");
        button2.addActionListener(this);
        panel2.add(button2);

	    button3 = new Button(" Sharpen ");
        button3.addActionListener(this);
	    panel2.add(button3);

	    button4 = new Button(" Enhance Contrast ");
        button4.addActionListener(this);
	    panel2.add(button4);

	    button5 = new Button(" Unsharp Mask ");
        button5.addActionListener(this);
	    panel2.add(button5);

	    Panel panel3 = new Panel();
        panel3.setLayout(new GridLayout(1, 3));

	    button6 = new Button(" Remove Streaks ");
        button6.addActionListener(this);
	    panel3.add(button6);

	    button7 = new Button(" Line Correction ");
        button7.addActionListener(this);
	    panel3.add(button7);

	    button8 = new Button(" Median Filter ");
        button8.addActionListener(this);
	    panel3.add(button8);

	    Panel panel4 = new Panel();
        panel4.setLayout(new GridLayout(1, 5));

	    button10 = new Button(" Undo ");
        button10.addActionListener(this);
	    panel4.add(button10);

	    button9 = new Button(" ROI ");
        button9.addActionListener(this);
	    panel4.add(button9);

        button11 = new Button(" Show Info ");
        button11.addActionListener(this);
	    panel4.add(button11);

        button12 = new Button(" Duplicate ");
        button12.addActionListener(this);
	    panel4.add(button12);

        button13 = new Button(" Main ");
        button13.addActionListener(this);
	    panel4.add(button13);		

        add(panel);
	    add(panel2);
   	    add(panel3);
	    add(panel4);
        pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Point loc = getLocation();
        Dimension size = getSize();
        if (loc.y+size.height>screen.height)
            getCanvas().zoomOut(0, 0);
         }
      
    public void actionPerformed(ActionEvent e) {
        Object b = e.getSource();

	//IJ.run("MySEM Select Image");

        if (b==button1) {
		    IJ.run("Brightness/Contrast...");}

	    if (b==button1b) {
		    IJ.run("16-bit");
		    IJ.run("Threshold...");
		    }

	    if (b==button1c) {
		    IJ.run("Analyze Particles...");}

	    if(b==button2)
		    {IJ.run("Smooth");}

	    if(b==button3)
		    {IJ.run("Sharpen");}
	
	    if(b==button4)
		    {IJ.run("Enhance Contrast");}

	    if(b==button5)
		    {IJ.run("Unsharp Mask...");}

	    if(b==button6)
		    {IJ.run("Remove Streaks");}

	    if(b==button7)
		    {IJ.run("MySEM Line Correction");}

	    if(b==button8)
		    {IJ.run("Median...");}

	    if(b==button9)
		    {IJ.run("ROI Manager...");}

    	if(b==button10)
		    {IJ.run("Undo");}

	    if(b==button11)
		    {IJ.run("Show Info...");}

	    if(b==button12)
		    {IJ.run("Duplicate...",imp.getTitle());}

	    if(b==button13)
		    {IJ.run("MySEM MainFrame");}

        }
	}

}
