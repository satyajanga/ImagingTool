package edu.wpi.robotics.aim.core.imagingtool;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import edu.wpi.robotics.aim.sample.gui.MatrixDisplay;

import vtk.vtkActor;
import vtk.vtkImageData;
import vtk.vtkMatrix4x4;

public class InteractorPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	private Map<vtkMatrix4x4,vtkImageData> imageDataMap;
	
	private ImageView[] planarViews;
	int numberOfPoints =0;
	double[] startingPoint;
	double[] targetPoint;
	
	JSlider[] targetPointSliders;
	JSlider[] startingPointSliders;
	JLabel[] targetPointLabels;
	JLabel[] startingPointLabels;
	MatrixDisplay transformPanel;

	public InteractorPanel()
	{
		imageDataMap = new HashMap<vtkMatrix4x4, vtkImageData>();
	}
	public void addImageToInteractor(vtkMatrix4x4 transform,vtkImageData imageData)
	{
		imageDataMap.put(transform, imageData);
	}
	public void removeImageFromInteractor(vtkMatrix4x4 transform)
	{
		imageDataMap.remove(transform);
	}

}
