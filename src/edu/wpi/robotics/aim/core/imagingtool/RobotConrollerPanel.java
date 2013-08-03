package edu.wpi.robotics.aim.core.imagingtool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;


import edu.wpi.robotics.aim.core.math.Transform;
import edu.wpi.robotics.aim.core.robot.AbstractKinematics;
import edu.wpi.robotics.aim.core.robot.GenericKinematicsModel;
import edu.wpi.robotics.aim.core.robot.IJointSpaceUpdateListener;
import edu.wpi.robotics.aim.core.robot.ITaskSpaceUpdateListener;
import edu.wpi.robotics.aim.core.robot.ImriController;
import edu.wpi.robotics.aim.core.robot.VirtualMRIController;
import edu.wpi.robotics.aim.core.robot.xml.MRIXmlFactory;
import edu.wpi.robotics.aim.sample.gui.MRIControllerExtendedDisplay;
import edu.wpi.robotics.aim.sample.gui.SampleGui;
//import net.miginfocom.swing.MigLayout;
public class RobotConrollerPanel extends JPanel
{

	/**
	 * 
	 */
	private JPanel leftPanel;
	private JPanel rightPanel;
	JSplitPane splitPane ;
	private ImriController mcon;
	private SampleGui gui = new SampleGui();
	private JPanel starter = new JPanel();//new MigLayout());
	private JTabbedPane tabs = new JTabbedPane();

	private static final long serialVersionUID = 1L;

	private DisplayWidget displayWidget;

	public RobotConrollerPanel()
	{
		this.createContentPane();
		
	}
	public void SetDisplayWidget(DisplayWidget w)
	{
		this.displayWidget =w;
		//this.createContentPane();
	}
	public void destroyObject()
	{
		displayWidget=null;
	}
	public void createContentPane()
	{
		this.setOpaque(true);

		//Create a scrolled text area.

		leftPanel = new JPanel();
	

		addElementsToLeftPanel();

	this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.6;
		c.weighty =0.4;
		c.gridwidth = 1;
		c.gridheight = 1;
		this.add(leftPanel,c);	
	}
	
	public void addElementsToLeftPanel()
	{
		mcon = new VirtualMRIController();
		gui.setKinematicsModel(new GenericKinematicsModel(
				MRIXmlFactory.getDefaultConfigurationStream("GenericKinematics.xml"),
				mcon)
				);//Nothing from here on should interact with the raw controller
		tabs.addTab("Control", gui);

		//tabs.addTab("MRI Controller Extended", new MRIControllerExtendedDisplay(mcon));

		leftPanel.add(tabs);

	}
	
	public AbstractKinematics getKinematicsModel(){
		return gui.getModel();
	}
	public void SetTransform(Transform transform)
	{
		try {
			gui.getModel().setDesiredTaskSpaceTransform(transform, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//(taskSpaceTransform, seconds, update)(transform);
		gui.repaint();
	}
	public void setITaskUpdateListener(ITaskSpaceUpdateListener l) 
	{
	gui.getModel().addPoseUpdateListener(l);	
	}
	public void setIJointUpdateListener(IJointSpaceUpdateListener l) 
	{
	gui.getModel().addJointSpaceListener(l);	
	}
	

}
