package edu.wpi.robotics.aim.core.imagingtool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vtk.vtkJavaGarbageCollector;
import vtk.vtkObject;
import vtk.vtkReferenceInformation;
import edu.wpi.robotics.aim.core.math.Transform;
import edu.wpi.robotics.aim.core.robot.AbstractKinematics;




public class MainWindow1 extends JFrame implements ActionListener
{

	public enum State
	{
		Main, Navigation, RobotController, VirtualRobot
	}


	private MainPanel mainPanel;
	private NavigationPanel navigationPanel;
	private RobotConrollerPanel robotControllerPanel;
	private ProstrateRobotPanel virtualRobotPanel;

	private DisplayWidget displayed3DWidget;
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private static final long serialVersionUID = 1L;

	private JButton main = new JButton(" Main ");
	private JButton navigation = new JButton(" Navigation  ");
	private JButton control = new JButton("  Robot Controller ");
	public MainWindow1()
	{


		displayed3DWidget = new DisplayWidget();
		setUPGarbageCollector();
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

	public Container createContentPane() 
	{
		//Create the content-pane-to-be.
		JPanel contentPane = new JPanel();
		contentPane.setOpaque(true);



		navigationPanel = new NavigationPanel();	
		navigationPanel.SetDisplayWidget(displayed3DWidget);
		robotControllerPanel = new RobotConrollerPanel();	
		robotControllerPanel.SetDisplayWidget(displayed3DWidget);

		AbstractKinematics kin = robotControllerPanel.getKinematicsModel();
		Transform t = kin.getCurrentTaskSpaceTransform();//reads current pose from joints through forward kinematics



		virtualRobotPanel = new ProstrateRobotPanel();

		mainPanel = new MainPanel();	
		mainPanel.SetDisplayWidget(displayed3DWidget);

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(main);
main.addActionListener(this);
		buttonBox.add(Box.createHorizontalStrut(20));
		buttonBox.add(navigation);
navigation.addActionListener(this);
		buttonBox.add(Box.createHorizontalStrut(20));
		buttonBox.add(control);
control.addActionListener(this);

		splitPane.setDividerLocation(400);
		splitPane.add(mainPanel,0);
		splitPane.add(displayed3DWidget,1);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.9;
		c.weighty =0.1;
		c.gridwidth = 1;
		c.gridheight = 1;
		contentPane.add(buttonBox,c);
		c.fill = GridBagConstraints.BOTH;
		c.gridx =0;
		c.gridy =GridBagConstraints.RELATIVE;
		c.weightx = 0.6;
		c.weighty =0.4;
		contentPane.add(splitPane,c);
		return contentPane;
	}

	private static void createAndShowGUI()
	{
		MainWindow1 mainWindow = new MainWindow1();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainWindow.setTitle("Imaging Tool");
		//Create and set up the content pane.

		mainWindow.setJMenuBar(mainWindow.createMenuBar());
		mainWindow.setContentPane(mainWindow.createContentPane());

		//Display the window.
		mainWindow.setExtendedState(Frame.MAXIMIZED_BOTH);  ;
		mainWindow.setVisible(true);	
		mainWindow.pack();
	}
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
				//				  vtkJavaGarbageCollector gc =
				//						  vtkGlobalJavaHash.GarbageCollector;
				//						                  gc.SetScheduleTime(1, TimeUnit.SECONDS);
				//						                  gc.SetAutoGarbageCollection(true);
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
					mainPanel.Load3DVolume(path);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
		if(e.getSource() == main)
		{
splitPane.remove(0);
splitPane.add(mainPanel,0);
		}
		else if(e.getSource() == navigation)
		{
			splitPane.remove(0);
			splitPane.add(navigationPanel,0);
		}
		else if(e.getSource() == control)
		{
			splitPane.remove(0);
			splitPane.add(robotControllerPanel,0);
		}
		
		this.validate();
		this.pack();


	}
	public void setUPGarbageCollector()
	{
		vtkObject.JAVA_OBJECT_MANAGER.getAutoGarbageCollector().SetAutoGarbageCollection(true);
		vtkObject.JAVA_OBJECT_MANAGER.getAutoGarbageCollector().SetScheduleTime(10, TimeUnit.SECONDS);


	}
}



