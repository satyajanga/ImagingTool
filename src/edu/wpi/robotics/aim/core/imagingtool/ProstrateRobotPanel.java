package edu.wpi.robotics.aim.core.imagingtool;

import javax.swing.JPanel;

import vtk.DisplaySTL;

import edu.wpi.robotics.aim.core.math.Transform;
import edu.wpi.robotics.aim.core.robot.AbstractKinematics;
import edu.wpi.robotics.aim.core.robot.IJointSpaceUpdateListener;
import edu.wpi.robotics.aim.core.robot.ITaskSpaceUpdateListener;
import edu.wpi.robotics.aim.core.robot.JointLimit;
import gui.RobotArmGUI;

public class ProstrateRobotPanel extends JPanel implements ITaskSpaceUpdateListener, IJointSpaceUpdateListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public RobotArmGUI robotArmGUI;
int count =0;
	public ProstrateRobotPanel()
	{
		robotArmGUI = new RobotArmGUI();
		RobotArmGUI.window = robotArmGUI;

		//window.dispSTL.renWin.Render();
		//window.dispSTL.renWin.GetRenderer().GetActiveCamera().Dolly(1);
		//window.dispSTL.renWin.GetRenderer().ResetCamera();

		//robotArmGUI.GetContentPanel();

		this.add(robotArmGUI.GetContentPanel());
	}

	public void intializeRobotGUI()
	{

		robotArmGUI.dispSTL.translateStageToPosX(0+DisplaySTL.STAGE_MAX_TRANS_X);
		robotArmGUI.dispSTL.translateStageToPosY(0);
		robotArmGUI.dispSTL.translateStageToPosZ(0);
	}

	@Override
	public void onTaskSpaceUpdate(AbstractKinematics source, Transform pose)
	{
		System.out.println("Pose On Task Update::" +pose);
		parseTransformAndSetToSliders(pose);
	}
	public void parseTransformAndSetToSliders(Transform pose)
	{
		double stageX =0;
		double stageY =0;
		double stageZ =0;

		double needleRot =0;
		double ONeedle =0;
		double INeedle=0;

		System.out.println("parse Tansform");
		robotArmGUI.updateSlides(10, 10, 10, 10, 10, 10);


	}

	@Override
	public void onTargetTaskSpaceUpdate(AbstractKinematics source,
			Transform pose)
	{
		System.out.println("Pose on Task Space Update::" +pose);

	}

	@Override
	public void onJointSpaceUpdate(AbstractKinematics source, double[] joints) {
		
		count++;
		System.out.println("Count :: " + count + "  In Prostrate Robot Panel :: " + joints[0]+ " "+ joints[1] + " "+joints[2]+ " " + joints[3]+ " "+ joints[4] +" "+ joints[5]);
		//robotArmGUI.updateSlides(joints[0], joints[1], joints[2], joints[3], joints[4],joints[5]);
	}

	@Override
	public void onJointSpaceTargetUpdate(AbstractKinematics source,
			double[] joints) {
	}

	@Override
	public void onJointSpaceLimit(AbstractKinematics source, int axis,
			JointLimit event) {
	
	}

}
