import org.aeliamdi.MDIFrame;
import org.aeliamdi.MDIFrameButton;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
/*
 * Created On: Jan 19, 2005
 * Created By: Pritam G. Barhate
 */

/**
 * @author Pritam G. Barhate
 */
public class CustomMDIButton extends MDIFrameButton
{
	
	Icon customIcon = null;
	Icon customMouseOverIcon = null;
	/**
	 * Overriden constructor.
	 * @param type
	 * @param frame
	 */
	public CustomMDIButton(int type, MDIFrame frame) {
		
		//Call the super class constructor
		super(type, frame);		
		
 		URL imageURL = null;
 		//load all the necessary images required by your
 		//class and store them in variables.
 		switch(this.buttonType){
	 		case MDIFrameButton.ICONIFY_BUTTON:
	 			imageURL= this.getClass().getResource("/images/aqua-orange.gif");
	 			customIcon = new ImageIcon(imageURL);	 		
				imageURL= this.getClass().getResource("/images/aqua-orange-mouseover.gif");
				customMouseOverIcon = new ImageIcon(imageURL);
				break;
			case MDIFrameButton.RESTORE_BUTTON:
				imageURL= this.getClass().getResource("/images/aqua-green.gif");
				customIcon = new ImageIcon(imageURL);
				imageURL= this.getClass().getResource("/images/aqua-green-mouseover.gif");
				customMouseOverIcon = new ImageIcon(imageURL);
				break;				
			case MDIFrameButton.CLOSE_BUTTON:
				imageURL= this.getClass().getResource("/images/aqua-red.gif");
				customIcon = new ImageIcon(imageURL);	
				imageURL= this.getClass().getResource("/images/aqua-red-mouseover.gif");
				customMouseOverIcon = new ImageIcon(imageURL);
				break;					
 		}
	}
	
	//Override this method and set the properties for the 
	//buttons as you want.
	public void setupLookAndFeel(String ID){
		if(ID.equals("Motif")){
			this.setButtonIcon(customIcon);
			this.setBorderPainted(false);
			this.setMargin(new Insets(0,0,0,0));
			this.setPreferredSize(new Dimension(30, 30));
			//Use this method if you want to want to 
			//provide rollover icons.
			this.setMouseOverIcon(customMouseOverIcon);
			//Note that following statement must be 
			//called explicitely in order to enable
			//rollover icon feature.
			this.setMouseOverIconEnabled(true);
			this.setBackground(UIManager.getColor("Button.background"));
	 	}else {	 	//In all other cases use superclass	functionality.	
	 		super.setupLookAndFeel(ID);
	 	}
	}
}
