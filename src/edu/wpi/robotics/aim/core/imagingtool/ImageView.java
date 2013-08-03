package edu.wpi.robotics.aim.core.imagingtool;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vtk.vtkActor;
import vtk.vtkActorCollection;
import vtk.vtkCanvas;
import vtk.vtkImageActor;
import vtk.vtkImageData;
import vtk.vtkImageMapToWindowLevelColors;
import vtk.vtkImageReslice;
import vtk.vtkInteractorStyleImage;
import vtk.vtkLookupTable;
import vtk.vtkMatrix4x4;
import vtk.vtkPointPicker;
import vtk.vtkPolyDataMapper;
import vtk.vtkPropCollection;
import vtk.vtkPropPicker;
import vtk.vtkRenderWindow;
import vtk.vtkRenderer;
import vtk.vtkSphereSource;

public class ImageView extends vtkCanvas implements ActionListener,ChangeListener {

	/**
	 * 
	 */

	private vtkImageData imageData;

	private DisplayChangeListener listener;

	private int mode; /* 2D or 3D */
	private int orientation;
	private static final long serialVersionUID = 1L;
	private vtkImageReslice imageReslice = null;

	vtkImageMapToWindowLevelColors color =null;
	vtkImageActor imageActor = null;
	boolean actorSelected = false;
	vtkActor selectedActor = null;
	int numberOfPoints =0;
	double[] startingPoint;
	double[] targetPoint;
	vtkActor targetSphereActor = null;
	vtkActor startingSphereActor = null;
	Map<String, String> headerInfo = null;
	public ImageView()
	{
		this.setBackground(Color.black);
		this.iren.SetInteractorStyle(new vtkInteractorStyleImage());
		//this.setBackground(Color.black);
		//	this.rw.SetWindowName("AXIAL");
	}

	public void destroyTheObject()
	{
		imageData = null;

		selectedActor = null;
		if(numberOfPoints >0)
		{
			targetSphereActor.Delete();
			if(numberOfPoints>1)
			{
				startingSphereActor.Delete();
			}
		}
		if(color!=null)
		{
			color.Delete();

			imageActor.Delete();
			imageReslice.Delete();
		}


	}
	public void setData(int orient, vtkImageData data, int m,
			Map<String, String> header) 
	{

		orientation = orient;
		imageData = data;
		mode = m;
		headerInfo = header;

		int extent[] = imageData.GetWholeExtent();
		double[] spacing = imageData.GetSpacing();
		double[] origin = imageData.GetOrigin();

		double[] center = { 0, 0, 0 };

		center[0] = origin[0] + spacing[0] * 0.5 * (extent[0] + extent[1]);
		center[1] = origin[1] + spacing[1] * 0.5 * (extent[2] + extent[3]);
		center[2] = origin[2] + spacing[2] * 0.5 * (extent[4] + extent[5]);


		imageReslice = new vtkImageReslice();
		imageReslice.SetInput(imageData);
		imageReslice.SetOutputDimensionality(2);

		vtkMatrix4x4 axial = new vtkMatrix4x4();
		axial.Identity();
		axial.SetElement(0, 3, center[0]);
		axial.SetElement(1, 3, center[1]);
		axial.SetElement(2, 3, center[2]);

		vtkMatrix4x4 sagittal = new vtkMatrix4x4();
		sagittal.DeepCopy(axial);

		sagittal.SetElement(0, 0, 0);
		sagittal.SetElement(0, 2, -1);
		sagittal.SetElement(1, 0, 1);
		sagittal.SetElement(1, 1, 0);
		sagittal.SetElement(2, 1, -1);
		sagittal.SetElement(2, 2, 0);

		// 177 static double sagittalElements[16] = {
		// 178 0, 0,-1, 0,
		// 179 1, 0, 0, 0,
		// 180 0,-1, 0, 0,
		// 181 0, 0, 0, 1 };
		// 182

		vtkMatrix4x4 coronal = new vtkMatrix4x4();
		coronal.DeepCopy(axial);
		coronal.SetElement(1, 1, 0);
		coronal.SetElement(1, 2, 1);
		coronal.SetElement(2, 1, -1);
		coronal.SetElement(2, 2, 0);

		if (orient == 0)
			imageReslice.SetResliceAxes(axial);
		else if (orient == 1)
			imageReslice.SetResliceAxes(coronal);
		else if (orient == 2)
			imageReslice.SetResliceAxes(sagittal);

		imageReslice.SetInterpolationModeToLinear();

		imageActor = new vtkImageActor();

		this.GetRenderer().AddActor(imageActor);

		imageReslice.Update();
		vtkLookupTable table = new vtkLookupTable();
		//System.out.println(imageData.GetScalarRange());
		double window = Double.valueOf(headerInfo.get("window"));
		double level = Double.valueOf(headerInfo.get("level"));
		table.SetRange((level-window)/2,(level+window)/2); // image intensity range
		table.SetValueRange(0.0, 1.0); // from black to white
		table.SetSaturationRange(0.0, 0.0); // no color saturation
		table.SetRampToLinear();
		table.Build();
		color= new vtkImageMapToWindowLevelColors();
		color.SetOutputFormatToLuminance();
		color.SetInputConnection(imageReslice.GetOutputPort());
		color.SetWindow(window);
		color.SetLevel(level);
		color.UpdateWholeExtent();

		imageActor.SetInput(color.GetOutput());
		this.ren.ResetCamera();

		this.Render();

		axial.Delete();
		sagittal.Delete();
		coronal.Delete();
		table.Delete();

	}


	public void deActivateWidget()
	{


		//	this.rw.Delete();
	}
	public void activateWidget()
	{
		this.ren = new vtkRenderer();
		this.rw = new vtkRenderWindow();
		//		
		//		
		this.rw.AddRenderer(ren);
		imageActor = new vtkImageActor();
		imageActor.SetInput(color.GetOutput());
		ren.AddActor(imageActor);
		this.Render();

	}

	public void checkTheVisibilityOfPoints()
	{
		if(mode == 0)
			return;
		int pos;
		vtkMatrix4x4 m1;
		// m1.Identity();
		m1 = imageReslice.GetResliceAxes();
		//	System.out.println(imageReslice.GetOutput().toString());
		pos =(int) m1.GetElement(2-orientation, 3);
		if(numberOfPoints >0)
		{
			if(pos == (int)targetPoint[2-orientation])
			{
				targetSphereActor.SetVisibility(1);
			}
			else
			{
				targetSphereActor.SetVisibility(0);
			}

			if(numberOfPoints >1)
			{
				if(pos ==(int)startingPoint[2-orientation])
				{
					startingSphereActor.SetVisibility(1);
				}
				else
				{
					startingSphereActor.SetVisibility(0);
				}
			}
		}

	}
	public void setSliderValue(int pos) 
	{

		if(imageReslice==null)
			return;

		vtkMatrix4x4 m1;
		// m1.Identity();
		double val = pos;
		val = val/100;
		m1 = imageReslice.GetResliceAxes();
		m1.SetElement(2-orientation, 3, val);
		imageReslice.SetResliceAxes(m1);
		imageReslice.Update();
		imageActor.SetInput(color.GetOutput());
		checkTheVisibilityOfPoints();
		this.Render();

	}

	public vtkImageData getImageData()
	{
		return imageReslice.GetOutput();
	}
	public vtkMatrix4x4 getAxis()
	{
		return imageReslice.GetResliceAxes();
	}
	public void UpdateTargetPoint(double[] point)
	{
		targetPoint = point;
		targetSphereActor.SetPosition(convertToPointOnPlane(point));
		checkTheVisibilityOfPoints();
		this.Render();
	}
	public void UpdateStartingPoint(double[] point)
	{
		startingPoint = point;
		startingSphereActor.SetPosition(convertToPointOnPlane(point));
		checkTheVisibilityOfPoints();
		this.Render();
	}
	public void SetTargetPoint(double[] point)
	{
		targetPoint = point;
		numberOfPoints+=1;
		addSphereActorAtPoint(convertToPointOnPlane(point));
		checkTheVisibilityOfPoints();
		this.Render();		
	}
	public void SetStartingPoint(double[] point)
	{
		startingPoint = point;
		numberOfPoints+=1;
		addSphereActorAtPoint(convertToPointOnPlane(point));
		checkTheVisibilityOfPoints();
		this.Render();		
	}
	public void SetMode(int m)
	{
		mode = m;

	}
	public void addSphereActorAtPoint(double[] point)
	{
		vtkSphereSource sphere = new vtkSphereSource();
		sphere.SetRadius(4);

		vtkPolyDataMapper sphereMapper = new vtkPolyDataMapper();
		sphereMapper.SetInputConnection(sphere.GetOutputPort());

		vtkActor sphereActor;
		if(numberOfPoints ==1)
		{
			targetSphereActor = new vtkActor();
			sphereActor = targetSphereActor;

		}
		else
		{
			startingSphereActor= new vtkActor();
			sphereActor = startingSphereActor;
		}

		sphereActor.SetMapper(sphereMapper);
		sphereActor.GetProperty().SetColor(0,0,1);  
		sphereActor.SetPosition(point);
		sphereActor.SetDragable(1);
		sphereActor.DragableOn();
		sphereActor.SetPickable(1);
		sphereActor.PickableOn();

		this.GetRenderer().AddActor(sphereActor);
		this.Render();
		sphere.Delete();
		sphereMapper.Delete();
		sphereActor = null;
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void mousePressed(MouseEvent e)
	{

		actorSelected = false;
		super.mousePressed(e);
		if(mode == 0)
			return;
		if(e.getButton() == MouseEvent.BUTTON3 && numberOfPoints<2)
		{
			numberOfPoints+=1;
			int[] eventPos;
			eventPos = this.getRenderWindowInteractor().GetEventPosition();
			vtkPointPicker picker = new vtkPointPicker();
			//clickCoords contains the coordinates of the click, return by vtkWindowInteractor

			picker.SetTolerance(0.01);
			picker.Pick(eventPos[0], eventPos[1], 0.0, this.GetRenderer());
			System.out.println("Pick ::" + eventPos[0] + " "+eventPos[1] );
			double[] pick;
			//if(result!=0)
			//pick = picker.GetPickPosition();

			pick = picker.GetPickPosition();
			System.out.println("Pick ::" + pick[0] + " "+pick[1] +" "+ pick[2]);
			addSphereActorAtPoint(pick);

			if(numberOfPoints==1)
			{
				targetPoint = convertToPointInVolume(pick);
				System.out.println("Target Point :"+targetPoint[0] +" " + targetPoint[1] + " "+targetPoint[2]);
				listener.targetPointAdded(targetPoint, orientation);
			}
			else
			{
				startingPoint = convertToPointInVolume(pick);
				System.out.println("startingPoint :"+startingPoint[0] +" " + startingPoint[1] + " "+startingPoint[2]);
				listener.startingPointAdded(startingPoint, orientation);
			}
			picker.Delete();
		}
		else
			if(e.getButton() == MouseEvent.BUTTON1 )
			{
				vtkPropPicker picker = new vtkPropPicker();
				int[] eventPos;
				eventPos = iren.GetEventPosition();
				picker.PickProp(eventPos[0],eventPos[1], this.GetRenderer());
				vtkActor actor = null;
				actor= picker.GetActor();

				if((actor!=null) && (actor == startingSphereActor || actor == targetSphereActor))
				{				
					selectedActor = actor;
					actorSelected = true;
					if(iren.GetEnabled() ==1)
						iren.Disable();
				}
				else
				{
					actorSelected = false;
					if(iren.GetEnabled() ==0)
						iren.Enable();
				}
				picker.Delete();
			}

	} 
	public double[] convertToPointOnPlane(double[] point)
	{
		double[] p = new double[3];
		p[2] = 0;
		double[] origin = imageReslice.GetOutput().GetOrigin();
		switch(orientation)
		{
		case 0:
		{
			p[0] = point[0];
			p[1] = point[1];
			break;
		}
		case 1:
		{
			p[0] = point[0];
			p[1] = point[2];
			break;
		}
		case 2:
		{
			p[0] = point[1];
			p[1] = point[2];
			break;
		}
		}

		p[0] = p[0] + origin[0];
		p[1] = p[1] + origin[1];
		return p;
	}
	public double[] convertToPointInVolume(double[] pickedPoint)
	{
		double[] p = new double[3];
		vtkMatrix4x4 m =imageReslice.GetResliceAxes();
		p[2-orientation] =  m.GetElement(2-orientation,3);
		double[] origin = imageReslice.GetOutput().GetOrigin();
		pickedPoint[0] = pickedPoint[0] - origin[0];
		pickedPoint[1] = pickedPoint[1] - origin[1];

		switch(orientation)
		{
		case 0:
		{
			p[0] = pickedPoint[0];
			p[1] = pickedPoint[1];
			break;
		}
		case 1:
		{
			p[0] = pickedPoint[0];
			p[2] = pickedPoint[1];
			break;
		}
		case 2:
		{
			p[1] = pickedPoint[0];
			p[2] = pickedPoint[1];
			break;
		}
		}
		return p;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{

		super.mouseMoved(e);

		if(mode==0)
			return;
		if(actorSelected == true)
		{
			System.out.println("In Mouse Moved ::" + e.getX() + " " + e.getY());
			//	iren.Disable();
			//	iren.SetEventwInformationFlipY(e.getX(), e.getY(), 0, 0, '0', 0, "0");
			int[] eventPos;
			eventPos = this.getRenderWindowInteractor().GetEventPosition();
			vtkPointPicker picker = new vtkPointPicker();
			//clickCoords contains the coordinates of the click, return by vtkWindowInteractor

			picker.SetTolerance(0.01);
			picker.Pick(eventPos[0], eventPos[1], 0.0, this.GetRenderer());

			double[] pick;
			pick = picker.GetPickPosition();

			selectedActor.SetPosition(pick);
			if(selectedActor==targetSphereActor)
			{
				targetPoint = convertToPointInVolume(pick);
				System.out.println("Target Point :"+targetPoint[0] +" " + targetPoint[1] + " "+targetPoint[2]);
				listener.targetPointUpdated(targetPoint, orientation);
			}
			else
			{
				startingPoint = convertToPointInVolume(pick);
				System.out.println("startingPoint :"+startingPoint[0] +" " + startingPoint[1] + " "+startingPoint[2]);
				listener.startingPointUpdated(startingPoint, orientation);
			}
			this.Render();
			picker.Delete();
		}
		else
		{
			actorSelected=false;
			if(iren.GetEnabled()==0)
				iren.Enable();

		}

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(iren.GetEnabled() == 0)
			iren.Enable();
		actorSelected = false;
		super.mouseReleased(e);
	}


	@Override
	public void stateChanged(ChangeEvent e) 
	{

	}

	public void addListener(DisplayChangeListener l) 
	{
		listener = l;
	}

	public void removeImageData() 
	{
		imageData = null;
		imageReslice = null;
		imageActor.SetVisibility(0);
		ren.RemoveActor(imageActor);
		imageActor = null;
		color = null;
		this.Render();
	}
	public void SetImageEnabled(int b)
	{
		if(imageActor==null)
			return;
		imageActor.SetVisibility(b);
		if(b==0)
		{
			ren.RemoveActor(imageActor);
		}
		else
		{
			ren.AddActor(imageActor);
		}
		
		this.Render();
	}
}
