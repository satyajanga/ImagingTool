package edu.wpi.robotics.aim.core.imagingtool;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.neuronrobotics.loader.NRNativeLoader;
import com.neuronrobotics.loader.NativeResourceException;


public class TabShare extends JFrame implements ActionListener {

	/**
	 * 
	 */

	static
	{


		try {
			if(NRNativeLoader.OSUtil.isWindows()){
				NRNativeLoader.loadFromJar("msvcr110");
				NRNativeLoader.loadFromJar("msvcp110");
			}
			NRNativeLoader.loadFromJar("vtksys");
			NRNativeLoader.loadFromJar("vtkCommon");
			NRNativeLoader.loadFromJar("vtkCommonJava");
			NRNativeLoader.loadFromJar("vtkFiltering");
			NRNativeLoader.loadFromJar("vtkFilteringJava");
			NRNativeLoader.loadFromJar("vtkDICOMParser");
			NRNativeLoader.loadFromJar("vtkzlib");
			NRNativeLoader.loadFromJar("vtkhdf5");
			NRNativeLoader.loadFromJar("vtkhdf5_hl");

			NRNativeLoader.loadFromJar("vtkNetCDF");
			NRNativeLoader.loadFromJar("vtkNetCDF_cxx");
			NRNativeLoader.loadFromJar("vtkmetaio");
			NRNativeLoader.loadFromJar("vtkjpeg");
			NRNativeLoader.loadFromJar("vtktiff");
			NRNativeLoader.loadFromJar("vtkexpat");
			NRNativeLoader.loadFromJar("vtkpng");
			NRNativeLoader.loadFromJar("LSDyna");
			NRNativeLoader.loadFromJar("vtkIO");
			NRNativeLoader.loadFromJar("vtkIOJava");
			NRNativeLoader.loadFromJar("vtkImaging");
			NRNativeLoader.loadFromJar("vtkImagingJava");
			NRNativeLoader.loadFromJar("vtkverdict");
			NRNativeLoader.loadFromJar("vtkGraphics");
			NRNativeLoader.loadFromJar("vtkGraphicsJava");
			NRNativeLoader.loadFromJar("vtkfreetype");
			NRNativeLoader.loadFromJar("vtkftgl");
			NRNativeLoader.loadFromJar("vtkRendering");
			NRNativeLoader.loadFromJar("vtkRenderingJava");
			NRNativeLoader.loadFromJar("mpistubs");
			NRNativeLoader.loadFromJar("MapReduceMPI");
			NRNativeLoader.loadFromJar("vtklibxml2");
			NRNativeLoader.loadFromJar("vtkalglib");
			NRNativeLoader.loadFromJar("vtkInfovis");
			NRNativeLoader.loadFromJar("vtkexoIIc");
			NRNativeLoader.loadFromJar("vtkHybrid");

			NRNativeLoader.loadFromJar("vtkVolumeRendering");

			NRNativeLoader.loadFromJar("vtkVolumeRenderingJava");

			NRNativeLoader.loadFromJar("vtkWidgets");
			NRNativeLoader.loadFromJar("vtkViews");
			NRNativeLoader.loadFromJar("vtkViewsJava");
			NRNativeLoader.loadFromJar("vtkCharts");
			NRNativeLoader.loadFromJar("vtkChartsJava");
			NRNativeLoader.loadFromJar("vtkGenericFiltering");
			NRNativeLoader.loadFromJar("vtkGenericFilteringJava");
			NRNativeLoader.loadFromJar("vtkInfovisJava");
			NRNativeLoader.loadFromJar("vtkproj4");
			NRNativeLoader.loadFromJar("vtkGeovis");
			NRNativeLoader.loadFromJar("vtkWidgetsJava");
			NRNativeLoader.loadFromJar("vtkGeovisJava");
			NRNativeLoader.loadFromJar("vtkHybridJava");
			NRNativeLoader.loadFromJar("gdcmCommon");
			NRNativeLoader.loadFromJar("gdcmzlib");

			NRNativeLoader.loadFromJar("gdcmDSED");
			NRNativeLoader.loadFromJar("gdcmexpat");

			NRNativeLoader.loadFromJar("gdcmIOD");
			NRNativeLoader.loadFromJar("gdcmDICT");

			NRNativeLoader.loadFromJar("gdcmjpeg8");
			NRNativeLoader.loadFromJar("gdcmjpeg12");
			NRNativeLoader.loadFromJar("gdcmjpeg16");
			NRNativeLoader.loadFromJar("gdcmopenjpeg");
			NRNativeLoader.loadFromJar("gdcmcharls");
			NRNativeLoader.loadFromJar("gdcmMSFF");

			NRNativeLoader.loadFromJar("gdcmjni");
			NRNativeLoader.loadFromJar("vtkgdcm");
			NRNativeLoader.loadFromJar("vtkgdcmJava");
			NRNativeLoader.addDir();

			System.out.println(System.getProperty("java.library.path"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NativeResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static final long serialVersionUID = 1L;

	//private RobotConrollerPanel robotController = new RobotConrollerPanel();
	AlwaysShowingPanel panel;
	public TabShare() {
		super("AimLab Navigation Tool");

		JTabbedPane tabPane = new JTabbedPane();

		// Make the "always visible" panel the first child so
		// that it always remains "on top" of the z-order
		panel = new AlwaysShowingPanel();

		tabPane.add("Main", panel);

		panel.setParent(this);
		tabPane.add("Navigation", new JLabel("dummy"));
		tabPane.add("RobotContorller", new JLabel("dummy"));
		tabPane.add(" Simulated", new JLabel("dummy"));

		getContentPane().add(tabPane, BorderLayout.CENTER);
		pack();
		tabPane.addChangeListener(panel);
		tabPane.addComponentListener(panel);
	}

	public JMenuBar createMenuBar() 
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);

		menuItem = new JMenuItem("Open File/Folder");
		menuItem.setActionCommand("Open");

		menu.add(menuItem);
		menuItem.addActionListener(this);

		menu = new JMenu("Help");
		menu.getAccessibleContext().setAccessibleDescription(
				"This menu does nothing");

		menuBar.add(menu);

		return menuBar;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			//			
			@Override
			public void run() {

				TabShare test = new TabShare();
				test.setJMenuBar(test.createMenuBar());
				test.setVisible(true);
				test.setExtendedState(Frame.MAXIMIZED_BOTH);  
				test.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			}
		});
	}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand().equals("Open"))
		{
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				String path = null ;
				try {
					path = fc.getSelectedFile().getCanonicalPath();
					panel.Load3DVolume(path);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}
}

class AlwaysShowingPanel extends JPanel implements ChangeListener, ComponentListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message = "";
	private boolean red = true;
	DisplayWidget displayed3DWidget ;
	private MainPanel mainPanel;
	private NavigationPanel navigationPanel;
	private RobotConrollerPanel robotControllerPanel;
	private ProstrateRobotPanel virtualRobotPanel;
	private JPanel simulatedRobotPanel;
	JSplitPane splitpane;
	private JButton sendToController = new JButton("Send To Controller");
	private int count =0;
	JSplitPane splitpane2;
	JFrame tabShare;
	JPanel tempPanel,tempPanel1;
	public Dimension getPreferredSize() {
		return new Dimension(200,100);
	}

	public void Load3DVolume(String path)
	{
		mainPanel.Load3DVolume(path);
	}

	public AlwaysShowingPanel()
	{

		this.setVisible(true);
		displayed3DWidget = new DisplayWidget();

		navigationPanel = new NavigationPanel();	
		navigationPanel.SetControllerButton(sendToController);
		sendToController.addActionListener(this);
		navigationPanel.SetDisplayWidget(displayed3DWidget);

		robotControllerPanel = new RobotConrollerPanel();	
		robotControllerPanel.SetDisplayWidget(displayed3DWidget);

		virtualRobotPanel = new ProstrateRobotPanel();
		simulatedRobotPanel = virtualRobotPanel.robotArmGUI.GetContentPanel();
		mainPanel = new MainPanel();	
		mainPanel.SetDisplayWidget(displayed3DWidget);

		robotControllerPanel.setITaskUpdateListener(virtualRobotPanel);
	robotControllerPanel.setIJointUpdateListener(virtualRobotPanel);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty =	1;
		c.gridwidth = 1;
		c.gridheight = 1;
		tempPanel = new JPanel(new GridBagLayout());

		tempPanel.add(mainPanel,c);
		c.gridx =1;
		tempPanel.add(navigationPanel,c);

		tempPanel1 = new JPanel(new GridBagLayout());

		c.gridx= 0;
		tempPanel1.add(tempPanel,c);
		c.gridx=1;
		tempPanel1.add(displayed3DWidget,c);
		c.gridx=0;
		add(tempPanel1,c);
		c.gridx=1;
		add(robotControllerPanel,c);
		c.gridy=2;
		add(simulatedRobotPanel,c);

		navigationPanel.setVisible(false);
		robotControllerPanel.setVisible(false);
		simulatedRobotPanel.setVisible(false);


		Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
		tempPanel.setPreferredSize(new Dimension((int)(screenSize.width*0.3),screenSize.height));
		tempPanel.setMinimumSize(new Dimension((int)(screenSize.width*0.3),screenSize.height));
		displayed3DWidget.setKinematicsModel(robotControllerPanel.getKinematicsModel());
	}


	public void setVisible(boolean visible) {
		// do nothing - we want to always be visible!
	}


	public void setParent(JFrame parent)
	{
		tabShare = parent;
	}
	/* Component Listener */
	public void componentResized(ComponentEvent e) {

		JTabbedPane tabPane = (JTabbedPane)e.getSource();
		Component selected = tabPane.getComponentAt(tabPane.getSelectedIndex());
		setSize(selected.getSize());
		validate();
	}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabpane = (JTabbedPane)e.getSource();
		int Index =tabpane.getSelectedIndex();

		switch(Index)
		{
		case 0:
		{
			robotControllerPanel.setVisible(false);
			simulatedRobotPanel.setVisible(false);
			tempPanel1.setVisible(true);
			displayed3DWidget.SetMode(0);
			mainPanel.setVisible(true);
			navigationPanel.setVisible(false);
			break;
		}
		case 1:
		{

			robotControllerPanel.setVisible(false);
			simulatedRobotPanel.setVisible(false);
			tempPanel1.setVisible(true);
			navigationPanel.setVisible(true);
			mainPanel.setVisible(false);
			displayed3DWidget.SetMode(1);
			break;
		}
		case 2:
		{

			tempPanel1.setVisible(false);
			simulatedRobotPanel.setVisible(false);

			robotControllerPanel.setVisible(true);
			displayed3DWidget.SetMode(0);

			break;
		}
		case 3:
		{
			tempPanel1.setVisible(false);

			robotControllerPanel.setVisible(false);

			simulatedRobotPanel.setVisible(true);
			if(count ==0)
			{
				virtualRobotPanel.intializeRobotGUI();
				count=1;
			}


			break;
		}
		}

		this.validate();
		this.repaint();
		this.updateUI();

	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == sendToController)
		{
			robotControllerPanel.SetTransform(navigationPanel.getTransform());
			JTabbedPane tabbedPane = (JTabbedPane)this.getParent();
			tabbedPane.setSelectedIndex(2);
		}
	}

}
