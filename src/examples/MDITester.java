/*
AceMDI - Easy, yet powerful MDI at your fingertips.
Copyright (C) 2004 Pritam G. Barhate.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information, contact pritam_g_barhate@rediffmail.com
 */


import org.aeliamdi.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Vector;


/*
 * Created on Sep 16, 2004
 *
 * Pritam G. Barhate
 */

/**
 *This is the test class that is used to test AceMDI while coding.
 * @author Pritam G. Barhate
 */
public class MDITester {
	static MDIFrame editorFrame;
	static JTextArea logger;
	
	public static void main(String[] args) {
		String landfClassName;
		if(args.length == 0 ){
			landfClassName = "javax.swing.plaf.metal.MetalLookAndFeel";
		}else{
			landfClassName = args[0];
		}
		try {
	        UIManager.setLookAndFeel(landfClassName);
	    } catch (Exception e) {
	    	System.out.println(e);
	    }
	    //System.out.println(UIManager.getLookAndFeel().getID());
		editorFrame = new MDIFrame("Editor", true);
		editorFrame.setJMenuBar(createMenuBar());
		logger = new JTextArea(5,5);
		logger.setFocusable(false);
		logger.setEditable(false);
		JScrollPane loggerScrollPane = new JScrollPane(logger);
		JPanel viewContainer = editorFrame.getViewContainer();
		editorFrame.getContentPane().remove(viewContainer);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,viewContainer, loggerScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);
		loggerScrollPane.setMinimumSize(new Dimension(300, 100));
		viewContainer.setMinimumSize(new Dimension(300,200));
		//editorFrame.add(loggerScrollPane, BorderLayout.WEST);
		editorFrame.getContentPane().setLayout(new BorderLayout());
		editorFrame.getContentPane().add(splitPane, BorderLayout.CENTER);
		editorFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
		
		editorFrame.addMDIFrameListener(new MDIFrameAdapter(){
				public void viewPaneChanged(MDIFrameEvent e){
					logger.append("\nView Pane changed from " + e.getOldViewPane() + " to " + e.getNewViewPane());
				}
			});
				
		editorFrame.setSize(500,300);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension desktopSize = toolkit.getScreenSize();
		
		int x = (desktopSize.width - editorFrame.getWidth()) / 2;
		int y = (desktopSize.height - editorFrame.getHeight() - 20) / 2;
		
		editorFrame.setLocation(x, y);
		editorFrame.setVisible(true);
	}
	
	static JMenuBar createMenuBar(){
		JMenuBar menubar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenuItem newMenu = new JMenuItem("New");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JTextPane textPane= new JTextPane();
				JScrollPane scrollPane = new JScrollPane(textPane);
								
				URL imageURL= this.getClass().getResource("/images/new.gif");
				
				MDIIcon icon = new MDIIcon(imageURL);
				MDIView text = new MDIView(editorFrame, textPane, null, icon);
				text.addMDIViewListener(new TheMDIViewListener());
				text.setLayout(new BorderLayout());
				text.add(scrollPane, BorderLayout.CENTER);
				editorFrame.add(text);
				
			}
		});
		file.add(newMenu);
		
		newMenu = new JMenuItem("Close View...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				Vector views = editorFrame.getViews();
				Object input = JOptionPane.showInputDialog(editorFrame, "Select view to close", "Close view", JOptionPane.QUESTION_MESSAGE, null, views.toArray(), views.get(0));
				if(input != null){
					((MDIView)input).closeView();
				}
			}
		});
		file.add(newMenu);
		
		newMenu = new JMenuItem("Dispose View...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				Vector views = editorFrame.getViews();
				Object input = JOptionPane.showInputDialog(editorFrame, "Select view to dispose", "Dispose view", JOptionPane.QUESTION_MESSAGE, null, views.toArray(), views.get(0));
				if(input != null){
					((MDIView)input).setDefaultCloseOperation(MDIFrame.DISPOSE_ON_CLOSE);
					((MDIView)input).disposeView();
				}
			}
		});
		file.add(newMenu);
		
		menubar.add(file);
		
		newMenu = new JMenuItem("Change Desktop Background...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JDialog dialog = createSetDesktopBackgroundDialog();
				dialog.pack();
				int x = (editorFrame.getX() + (editorFrame.getWidth() / 2)) - (dialog.getWidth() / 2);
				int y = (editorFrame.getY() + (editorFrame.getHeight() / 2)) - (dialog.getHeight() / 2);
				dialog.setLocation(x,y);
				dialog.setVisible(true);
			}
		});
		file.add(newMenu);
		
		menubar.add(file);
		
		JMenu view = new JMenu("View");
		newMenu = new JMenuItem("Change View");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				editorFrame.changeView();
			}
		});
		view.add(newMenu);
		
		newMenu = new JMenuItem("Change Tab placement");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String[] choices = {"Bottom", "Left", "Right", "Top"};
				Object input = JOptionPane.showInputDialog(editorFrame, "Select placement", "Close view", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(input != null){
					if("Bottom".equals(input)){
						editorFrame.getTabbedPane().setTabPlacement(JTabbedPane.BOTTOM);
					}else if("Left".equals(input)){
						editorFrame.getTabbedPane().setTabPlacement(JTabbedPane.LEFT);
					}else if("Right".equals(input)){
						editorFrame.getTabbedPane().setTabPlacement(JTabbedPane.RIGHT);
					}else if("Top".equals(input)){
						editorFrame.getTabbedPane().setTabPlacement(JTabbedPane.TOP);
					}
				}
				
			}
		});
		view.add(newMenu);
		
		newMenu = new JMenuItem("Get Active view title");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				MDIView activeView = editorFrame.getActiveView();
				JOptionPane.showMessageDialog(editorFrame, "The title of the active view is: " + activeView.getTitle(), "Active View Title", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		view.add(newMenu);
		
		newMenu = new JMenuItem("Get View State...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//JOptionPane.showMessageDialog(editorFrame, "The window state is " + editorFrame.getActiveView().getState());
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				JDialog dialog = createGetViewStateDialog();
				dialog.pack();
				int x = (editorFrame.getX() + (editorFrame.getWidth() / 2)) - (dialog.getWidth() / 2);
				int y = (editorFrame.getY() + (editorFrame.getHeight() / 2)) - (dialog.getHeight() / 2);
				dialog.setLocation(x,y);
				dialog.setVisible(true);
			}
		});
		
		view.add(newMenu);
		
		newMenu = new JMenuItem("Set View Title...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				JDialog dialog = createSetTitleDialog();
				dialog.pack();
				int x = (editorFrame.getX() + (editorFrame.getWidth() / 2)) - (dialog.getWidth() / 2);
				int y = (editorFrame.getY() + (editorFrame.getHeight() / 2)) - (dialog.getHeight() / 2);
				dialog.setLocation(x,y);
				dialog.setVisible(true);
			}
		});		
		view.add(newMenu);
		
		newMenu = new JMenuItem("Set View Icon...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				JDialog dialog = createSetIconDialog();
				dialog.pack();
				int x = (editorFrame.getX() + (editorFrame.getWidth() / 2)) - (dialog.getWidth() / 2);
				int y = (editorFrame.getY() + (editorFrame.getHeight() / 2)) - (dialog.getHeight() / 2);
				dialog.setLocation(x,y);
				dialog.setVisible(true);
			}
		});
		view.add(newMenu);
		
		newMenu = new JMenuItem("Set Active View...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				Vector views = editorFrame.getViews();
				Object input = JOptionPane.showInputDialog(editorFrame, "Select view to set active", "Select view", JOptionPane.QUESTION_MESSAGE, null, views.toArray(), views.get(0));
				if(input != null){
					((MDIView)input).setSelected(true);
				}
			}
		});
		
		view.add(newMenu);
		
		newMenu = new JMenuItem("Set deselected view...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				Vector views = editorFrame.getViews();
				Object input = JOptionPane.showInputDialog(editorFrame, "Select view to deselect", "Deselect view", JOptionPane.QUESTION_MESSAGE, null, views.toArray(), views.get(0));
				if(input != null){
					((MDIView)input).setSelected(false);
				}
			}
		});
		
		view.add(newMenu);
		
		newMenu = new JMenuItem("Set Maximized...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				Vector views = editorFrame.getViews();
				Object input = JOptionPane.showInputDialog(editorFrame, "Select view to maximize", "Maximize view", JOptionPane.QUESTION_MESSAGE, null, views.toArray(), views.get(0));
				if(input != null){
					((MDIView)input).setMaximized();
				}
			}
		});
		
		view.add(newMenu);
		
		newMenu = new JMenuItem("Set Iconified...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				Vector views = editorFrame.getViews();
				Object input = JOptionPane.showInputDialog(editorFrame, "Select view to iconify", "Iconify view", JOptionPane.QUESTION_MESSAGE, null, views.toArray(), views.get(0));
				if(input != null){
					((MDIView)input).setIconified();
				}
			}
		});
		
		view.add(newMenu);
		
		newMenu = new JMenuItem("Set Restored...");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.getViews().size() == 0){
					showNoViewsPresentDialog();
					return;
				}
				Vector views = editorFrame.getViews();
				Object input = JOptionPane.showInputDialog(editorFrame, "Select view to restore", "Restore view", JOptionPane.QUESTION_MESSAGE, null, views.toArray(), views.get(0));
				if(input != null){
					((MDIView)input).setRestored();
				}
			}
		});
		
		view.add(newMenu);
		
		menubar.add(view);
		
		JMenu frameButtons = new JMenu("Frame Buttons");
		
		JMenu lookAndFeel = new JMenu("Change Look and Feel");
		
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
				
		for(int i=0; i<info.length; i++){
			newMenu = new JMenuItem(new ChangeLookAndFeelAction(editorFrame, info, i));
			lookAndFeel.add(newMenu);
		}	
		
		frameButtons.add(lookAndFeel);
		
		newMenu = new JMenuItem("Change Close Button");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String ID = UIManager.getLookAndFeel().getID();
			 	if(ID.equals("Motif")){
			 		CustomMDIButton closeButton = new CustomMDIButton(MDIFrameButton.CLOSE_BUTTON, editorFrame);
					editorFrame.setCloseButton(closeButton);
			 	}else{
			 		JOptionPane.showMessageDialog(editorFrame, "This test requires motif look and feel.\nPlease change look and feel to CDE/Motif first.", "MDITester", JOptionPane.INFORMATION_MESSAGE);
			 	}				
			}
		});
		frameButtons.add(newMenu);
		
		
		newMenu = new JMenuItem("Change Restore Button");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String ID = UIManager.getLookAndFeel().getID();
				if(ID.equals("Motif")){
					CustomMDIButton restoreButton = new CustomMDIButton(MDIFrameButton.RESTORE_BUTTON, editorFrame);
					editorFrame.setRestoreButton(restoreButton);
				}else{
			 		JOptionPane.showMessageDialog(editorFrame, "This test requires motif look and feel.\nPlease change look and feel to CDE/Motif first.", "MDITester", JOptionPane.INFORMATION_MESSAGE);
			 	}	
			}
		});
		frameButtons.add(newMenu);
		
		newMenu = new JMenuItem("Change Iconify Button");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String ID = UIManager.getLookAndFeel().getID();
				if(ID.equals("Motif")){
					CustomMDIButton iconifyButton = new CustomMDIButton(MDIFrameButton.ICONIFY_BUTTON, editorFrame);
					editorFrame.setIconifyButton(iconifyButton);
				}else{
			 		JOptionPane.showMessageDialog(editorFrame, "This test requires motif look and feel.\nPlease change look and feel to CDE/Motif first.", "MDITester", JOptionPane.INFORMATION_MESSAGE);
			 	}	
			}
		});
		frameButtons.add(newMenu);
		
		newMenu = new JMenuItem("Enable window buttons");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!editorFrame.isButtonsEnabled()){
					editorFrame.setButtonsEnabled(true);
				}
			}
		});
		frameButtons.add(newMenu);
		
		newMenu = new JMenuItem("Disable window buttons");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(editorFrame.isButtonsEnabled()){
					editorFrame.setButtonsEnabled(false);					
				}
			}
		});
		frameButtons.add(newMenu);
		
		
		menubar.add(frameButtons);
		
		JMenu systemMenuFunctions = new JMenu("System Menu");
		
		newMenu = new JMenuItem("Change Minimize menu text");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JDialog dialog = createChangeSystemMenuTextDialog("Minimize");
				int x = (editorFrame.getX() + (editorFrame.getWidth() / 2)) - (dialog.getWidth() / 2);
				int y = (editorFrame.getY() + (editorFrame.getHeight() / 2)) - (dialog.getHeight() / 2);
				dialog.setLocation(x,y);
				dialog.setVisible(true);
			}
		});
		systemMenuFunctions.add(newMenu);
		
		newMenu = new JMenuItem("Change Restore menu text");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JDialog dialog = createChangeSystemMenuTextDialog("Restore");
				int x = (editorFrame.getX() + (editorFrame.getWidth() / 2)) - (dialog.getWidth() / 2);
				int y = (editorFrame.getY() + (editorFrame.getHeight() / 2)) - (dialog.getHeight() / 2);
				dialog.setLocation(x,y);
				dialog.setVisible(true);
			}
		});
		systemMenuFunctions.add(newMenu);
		
		newMenu = new JMenuItem("Change Close menu text");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JDialog dialog = createChangeSystemMenuTextDialog("Close");
				int x = (editorFrame.getX() + (editorFrame.getWidth() / 2)) - (dialog.getWidth() / 2);
				int y = (editorFrame.getY() + (editorFrame.getHeight() / 2)) - (dialog.getHeight() / 2);
				dialog.setLocation(x,y);
				dialog.setVisible(true);
			}
		});
		systemMenuFunctions.add(newMenu);
		
		menubar.add(systemMenuFunctions);
		
		JMenu log = new JMenu("log");
		newMenu = new JMenuItem("Clear log");
		newMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				logger.setText("");
			}
		});
		log.add(newMenu);
		menubar.add(log);
		return menubar;
	}
	
	static JDialog createChangeSystemMenuTextDialog(final String menu){
		final JDialog dialog = new JDialog(editorFrame, "Change System Menu Text", true);
		JLabel informationLabel = new JLabel("Enter new Text and new Mnemonic for " + menu + ".");
		JLabel textLabel = new JLabel("Text");
		JLabel mnemonicLabel = new JLabel("Mnemonic(int)");
		final JTextField menuTextField = new JTextField(10);
		final JTextField mnemonicTextField = new JTextField(3);
		JButton okButton = new JButton("Change " + menu + " Text");
		
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(menu.equals("Minimize")){
					editorFrame.setMinimizeMenuText(menuTextField.getText());
					try{
						int mnemonicValue = Integer.parseInt(mnemonicTextField.getText());
						editorFrame.setMinimizeMenuMnemonic(mnemonicValue);
					}catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(editorFrame, "The mnemonic should be a number.", "Not a valid number", JOptionPane.ERROR_MESSAGE);
						mnemonicTextField.selectAll();
						mnemonicTextField.requestFocusInWindow();
						return;
					}
				}else if(menu.equals("Restore")){
					editorFrame.setRestoreMenuText(menuTextField.getText());
					try{
						int mnemonicValue = Integer.parseInt(mnemonicTextField.getText());
						editorFrame.setRestoreMenuMnemonic(mnemonicValue);
					}catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(editorFrame, "The mnemonic should be a number.", "Not a valid number", JOptionPane.ERROR_MESSAGE);
						mnemonicTextField.selectAll();
						mnemonicTextField.requestFocusInWindow();
					}
				}else if(menu.equals("Close")){
					editorFrame.setCloseMenuText(menuTextField.getText());
					try{
						int mnemonicValue = Integer.parseInt(mnemonicTextField.getText());
						editorFrame.setCloseMenuMnemonic(mnemonicValue);
					}catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(editorFrame, "The mnemonic should be a number.", "Not a valid number", JOptionPane.ERROR_MESSAGE);
						mnemonicTextField.selectAll();
						mnemonicTextField.requestFocusInWindow();
					}
				}
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel itemsPanel = new JPanel(new GridLayout(2,2));
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		
		itemsPanel.add(textLabel);
		itemsPanel.add(menuTextField);
		itemsPanel.add(mnemonicLabel);
		itemsPanel.add(mnemonicTextField);
		
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		mainPanel.add(informationLabel, BorderLayout.NORTH);
		mainPanel.add(itemsPanel, BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(7,  7, 7, 7));
		dialog.setContentPane(mainPanel);
		dialog.pack();		
		return dialog;
	}
	
	static JDialog createGetViewStateDialog(){
		final JDialog dialog = new JDialog(editorFrame, "Get View Sate", true);
		JLabel information = new JLabel("Select a view to show its state");
		JLabel viewsLabel = new JLabel("Select View");
		final JComboBox viewsComboBox = new JComboBox(editorFrame.getViews());
		JPanel mainPanel = new JPanel(new GridLayout(2,1));
		JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		comboBoxPanel.add(viewsLabel);
		comboBoxPanel.add(viewsComboBox);
		mainPanel.add(information);
		mainPanel.add(comboBoxPanel);
		
		JButton showStateButton = new JButton("Show State");
		showStateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				MDIView theView = (MDIView)viewsComboBox.getSelectedItem();
				dialog.setVisible(false);
				dialog.dispose();
				int viewState = theView.getState();
				String viewStateString = null;
				switch(viewState){
					case 30:
						viewStateString = "MDIView.MAXIMIZED";
						break;
					case 20:
						viewStateString = "MDIView.RESTORED";
						break;
					case 10:
						viewStateString = "MDIView.ICONIFIED";
						break;
				}				
				JOptionPane.showMessageDialog(editorFrame, "The state of " + theView.getTitle() + " is: " + viewStateString, "View State", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonsPanel.add(showStateButton);
		buttonsPanel.add(cancelButton);
		
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(mainPanel, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		
		return dialog;		
	}
	
	static JDialog createSetIconDialog(){
		final JDialog dialog = new JDialog(editorFrame, "Set Icon for a view", true);
		JLabel viewsLabel = new JLabel("Select View");
		final JComboBox viewsComboBox = new JComboBox(editorFrame.getViews());
		JLabel iconLabel = new JLabel("New Icon");
		final JTextField iconTextField = new JTextField("file path");
		iconTextField.setEditable(false);
		JButton browseButton = new JButton("Browse");
		
		browseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
			    int returnVal = fc.showOpenDialog(dialog);

			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			        File  file =  fc.getSelectedFile();
			        iconTextField.setText(file.getPath());
			    } 
			}
		});
		JPanel container = new JPanel(new GridLayout(2, 3, 5,5));
		container.add(viewsLabel);
		container.add(viewsComboBox);
		container.add(Box.createHorizontalGlue());
		container.add(iconLabel);
		container.add(iconTextField);
		container.add(browseButton);
				
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				MDIView theView = (MDIView)viewsComboBox.getSelectedItem();
				if(!iconTextField.getText().equals("file path")){
					theView.setIcon(new MDIIcon(iconTextField.getText()));
					dialog.setVisible(false);
					dialog.dispose();
				}else{
					JOptionPane.showMessageDialog(dialog, "Please select an icon file", "Select icon", JOptionPane.OK_OPTION);
				}
			}
		});
				
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		container.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(container, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		
		return dialog;
	}
	
	static JDialog createSetTitleDialog(){
		final JDialog dialog = new JDialog(editorFrame, "Set title for a view", true);
		JLabel viewsLabel = new JLabel("Select View");
		final JComboBox viewsComboBox = new JComboBox(editorFrame.getViews());
		JLabel titleLabel = new JLabel("New Title");
		final JTextField titleTextField = new JTextField();
		JPanel container = new JPanel(new GridLayout(2, 2, 5,5));
		container.add(viewsLabel);
		container.add(viewsComboBox);
		container.add(titleLabel);
		container.add(titleTextField);
		
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				MDIView theView = (MDIView)viewsComboBox.getSelectedItem();
				theView.setTitle(titleTextField.getText());
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
				
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		container.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(container, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		return dialog;
	}
	
	static JDialog createSetDesktopBackgroundDialog(){
		final JDialog dialog = new JDialog(editorFrame, "Set Desktop Background", true);
		JLabel oldColorLabel = new JLabel("Old Background");
		JLabel oldColorSwatch = new JLabel("    ");
		oldColorSwatch.setBackground(editorFrame.getDesktopBackground());
		oldColorSwatch.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		oldColorSwatch.setOpaque(true);
		
		JLabel newColorLabel = new JLabel("New Background");
		final JLabel newColorSwatch = new JLabel("    ");
		newColorSwatch.setBackground(Color.GRAY);
		newColorSwatch.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		newColorSwatch.setOpaque(true);
		JButton chooseColorButton = new JButton("Choose color");
		chooseColorButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Color color = JColorChooser.showDialog(dialog, "Choose Color", Color.GRAY);
				newColorSwatch.setBackground(color);
			}
		});
		
		JPanel container = new JPanel(new GridLayout(2, 3, 5,5));
		container.add(oldColorLabel);
		container.add(oldColorSwatch);
		container.add(Box.createHorizontalGlue());
		container.add(newColorLabel);
		container.add(newColorSwatch);
		container.add(chooseColorButton);
		
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				editorFrame.setDesktopBackground(newColorSwatch.getBackground());
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
				
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		container.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(container, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		return dialog;
	}
	
	static void showNoViewsPresentDialog(){
		JOptionPane.showMessageDialog(editorFrame, "There are no views present. \nPlease create some views using\n File > new and Test again", "No Views Present", JOptionPane.ERROR_MESSAGE);
	}
	
	static class TheMDIViewListener implements MDIViewListener
	{
		
		public void MDIViewActiviated(MDIViewEvent e) {
			logger.append("\nView name: " + ((MDIView)e.getSource()).getTitle() + " Event Type: " + e.paramString());

		}
	
		public void MDIViewClosed(MDIViewEvent e) {
			logger.append("\nView name: " + ((MDIView)e.getSource()).getTitle() + " Event Type: " + e.paramString());

		}
	
	
		public void MDIViewClosing(MDIViewEvent e) {
			logger.append("\nView name: " + ((MDIView)e.getSource()).getTitle() + " Event Type: " + e.paramString());
			int result = JOptionPane.showConfirmDialog(editorFrame, "Do you really want to \nclose the view?", "Close View", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				((MDIView)e.getSource()).setDefaultCloseOperation(MDIView.DISPOSE_ON_CLOSE);				
			}else{
				((MDIView)e.getSource()).setDefaultCloseOperation(MDIView.DO_NOTHING_ON_CLOSE);
			}
		}
		
		
		public void MDIViewDeactivited(MDIViewEvent e) {
			logger.append("\nView name: " + ((MDIView)e.getSource()).getTitle() + " Event Type: " + e.paramString());

		}
		
		
		public void MDIViewIconified(MDIViewEvent e) {
			logger.append("\nView name: " + ((MDIView)e.getSource()).getTitle() + " Event Type: " + e.paramString());

		}

		public void MDIViewMaximized(MDIViewEvent e) {
			logger.append("\nView name: " + ((MDIView)e.getSource()).getTitle() + " Event Type: " + e.paramString());

		}

		public void MDIViewActivated( MDIViewEvent e )
		{

		}

		public void MDIViewDeactivated( MDIViewEvent e )
		{

		}

		public void MDIViewOpened(MDIViewEvent e) {
			logger.append("\nView name: " + ((MDIView)e.getSource()).getTitle() + " Event Type: " + e.paramString());

		}

		
		public void MDIViewRestored(MDIViewEvent e) {
			logger.append("\nView name: " + ((MDIView)e.getSource()).getTitle() + " Event Type: " + e.paramString());

		}
	}
}

class ChangeLookAndFeelAction extends AbstractAction{
	String lookAndFeelClassName;
	MDIFrame frame;
	public ChangeLookAndFeelAction(MDIFrame frame, UIManager.LookAndFeelInfo[] info, int i){
		super(info[i].getName());
		lookAndFeelClassName = info[i].getClassName();
		this.frame = frame;
	}
	
	public void actionPerformed(ActionEvent e){
		try{
			UIManager.setLookAndFeel(lookAndFeelClassName);
		}catch(Exception iae){
			//shouldn't occur ever
		}
		SwingUtilities.updateComponentTreeUI(frame);		
	}
}
