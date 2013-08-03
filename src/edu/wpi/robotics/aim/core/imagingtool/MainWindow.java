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




public class MainWindow extends JFrame implements ActionListener,ChangeListener
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
	JTabbedPane tabbedPane =new JTabbedPane(JTabbedPane.TOP);
	private static final long serialVersionUID = 1L;
	JPanel displayPanel;
	public MainWindow()
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
		JPanel contentPane = new JPanel(new BorderLayout());
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


		tabbedPane.addTab("       Main       ",mainPanel);

		tabbedPane.addTab("      Navigation     ",navigationPanel);
		tabbedPane.addTab("   Robot Controller    ",robotControllerPanel);	
		//tabbedPane.addTab("    Simulated Robot    ",virtualRobotPanel.robotArmGUI.GetContentPanel());


		//tabbedPane.addComponentListener(this);
		tabbedPane.addChangeListener(this);
		contentPane.add(tabbedPane);

		//virtualRobotPanel.intializeRobotGUI();
		return contentPane;
	}

	private static void createAndShowGUI()
	{
		MainWindow mainWindow = new MainWindow();
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
	}
	public void setUPGarbageCollector()
	{
		vtkObject.JAVA_OBJECT_MANAGER.getAutoGarbageCollector().SetAutoGarbageCollection(true);
		vtkObject.JAVA_OBJECT_MANAGER.getAutoGarbageCollector().SetScheduleTime(10, TimeUnit.SECONDS);


	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		int Index =this.tabbedPane.getSelectedIndex();
		JTabbedPane tabPane = (JTabbedPane)e.getSource();
		String message = tabPane.getTitleAt(tabPane.getSelectedIndex());
		switch(Index)
		{
		case 0:
		{
//			mainPanel.tabActivated();

			break;
		}
		case 1:
		{


	//		navigationPanel.tabActivated();
			break;
		}
		case 2:
		{

		//	robotControllerPanel.tabActivated();
			break;
		}
		case 3:
		{
			
			break;
		}
		case 4:
		{

		}
		}
	}
}





