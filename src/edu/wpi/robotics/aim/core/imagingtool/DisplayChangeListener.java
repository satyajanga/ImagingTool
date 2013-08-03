package edu.wpi.robotics.aim.core.imagingtool;

public interface DisplayChangeListener
{
	public void targetPointAdded(double[] point,int orientation);
	
	public void startingPointAdded(double[] point,int orientation);
	
	public void targetPointUpdated(double[] point,int orientation);
	
	public void startingPointUpdated(double[] point,int orientation);
	
}
