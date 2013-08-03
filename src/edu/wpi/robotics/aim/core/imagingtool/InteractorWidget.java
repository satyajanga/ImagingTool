package edu.wpi.robotics.aim.core.imagingtool;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vtk.vtkActor;
import vtk.vtkActorCollection;
import vtk.vtkCanvas;
import vtk.vtkCoordinate;
import vtk.vtkFollower;
import vtk.vtkImageActor;
import vtk.vtkImageData;
import vtk.vtkImageMapToColors;
import vtk.vtkLineSource;
import vtk.vtkLookupTable;
import vtk.vtkMatrix4x4;
import vtk.vtkOutlineSource;
import vtk.vtkPointPicker;
import vtk.vtkPolyDataMapper;
import vtk.vtkPropCollection;
import vtk.vtkPropPicker;
import vtk.vtkSphereSource;
import vtk.vtkVectorText;
import vtk.vtkViewport;
import Jama.Matrix;
import edu.wpi.robotics.aim.core.math.Transform;
import edu.wpi.robotics.aim.core.robot.AbstractKinematics;
import edu.wpi.robotics.aim.sample.gui.MatrixDisplay;

public class InteractorWidget extends vtkCanvas implements MouseMotionListener,ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//	vtkMatrix4x4
	private Map<vtkMatrix4x4,vtkImageActor> actorsMap;


	//	vtkCanvas panel;
	double[] bounds = {0,200,0,200,0,200};
	vtkOutlineSource boxSource;
	vtkLineSource lineSource;
	vtkActor lineActor;
	vtkPolyDataMapper boxMapper;
	vtkActor BoxAxisActor;
	Vector<vtkFollower> AxisLabelActors ;
	private boolean actorSelected = false;
	private vtkActor selectedActor;
	private ImagingPanel planarView;
	int numberOfPoints =0;
	double[] startingPoint;
	double[] targetPoint;
	vtkActor targetSphereActor;
	vtkActor startingSphereActor;
	JSlider[] targetPointSliders;
	JSlider[] startingPointSliders;
	JLabel[] targetPointLabels;
	JLabel[] startingPointLabels;
	MatrixDisplay transformPanel;
	int mode =0;
	vtkImageMapToColors color ;
	private AbstractKinematics kinematicsModel;
	vtkImageActor actorTobeRemoved = new vtkImageActor();
	vtkImageActor actorTobeAdded = new vtkImageActor();
	public void destroyTheObject()
	{
		if(mode ==0)
			return;
		for(int i=0;i<3;i++)
		{
			//	planarViews[i]=null;
			targetPointSliders[i] = null;
			targetPointLabels[i] = null;
			startingPointLabels[i] = null;
			startingPointSliders[i] = null;
			transformPanel = null;
		}/*
		vtkFollower axisActor;
		for(int i=0;i<AxisLabelActors.size();i++)
		{
			axisActor = AxisLabelActors.elementAt(i);
			AxisLabelActors.remove(i);
			axisActor.Delete();
		}
		BoxAxisActor.Delete();
		boxSource.Delete();
		boxMapper.Delete();

		lineActor.Delete();
		selectedActor = null;
		if(numberOfPoints >0)
		{
			targetSphereActor.Delete();
			if(numberOfPoints>1)
			{
				startingSphereActor.Delete();
			}
		}
		actorsMap.clear();
		color.Delete();*/
	}
	public InteractorWidget()
	{
		color = new vtkImageMapToColors();

		actorsMap = new HashMap<vtkMatrix4x4,vtkImageActor>();

		boxSource = new vtkOutlineSource();
		boxSource.SetBounds(bounds);
		boxMapper = new vtkPolyDataMapper();
		boxMapper.SetInputConnection(boxSource.GetOutputPort());

		// vtkFollower
		BoxAxisActor = new vtkActor();
		BoxAxisActor.SetMapper(boxMapper);
		BoxAxisActor.SetScale(1.0, 1.0, 1.0);
		BoxAxisActor.GetProperty().SetColor(1.0, 0.0, 1.0);
		BoxAxisActor.SetPickable(0);
		AxisLabelActors = new Vector<vtkFollower>();
		AxisLabelActors.clear();

		String[] labels = {"R", "A", "S", "L", "P", "I"};
		vtkVectorText axisText ;
		vtkPolyDataMapper axisMapper ;
		vtkFollower axisActor;


		for(int i = 0; i < 6; ++i)
		{

			axisText = new vtkVectorText();
			axisText.SetText(labels[i]);
			axisMapper = new vtkPolyDataMapper();
			axisMapper.SetInput(axisText.GetOutput());

			axisActor = new vtkFollower();
			axisActor.SetMapper(axisMapper);
			axisActor.SetPickable(0);
			AxisLabelActors.add(axisActor);

			axisActor.GetProperty().SetColor(1, 1, 1); // White
			axisActor.GetProperty().SetDiffuse(0.0);
			axisActor.GetProperty().SetAmbient(1.0);
			axisActor.GetProperty().SetSpecular(0.0);
			axisActor.SetScale(512*0.05);
			axisActor.SetOrigin(0.5,0.5,0.5);
			this.GetRenderer().AddActor(axisActor);
			//axisActor.Delete();
			axisText.Delete();
			axisMapper.Delete();
		}

		lineSource = new vtkLineSource();
		vtkPolyDataMapper lineMapper = new vtkPolyDataMapper();
		lineMapper.SetInputConnection(lineSource.GetOutputPort());
		
		lineActor= new vtkActor();

		lineActor.SetMapper(lineMapper);
		lineActor.GetProperty().SetColor(0,1,0);
		lineActor.SetVisibility(0);
		
		
		this.GetRenderer().AddActor(lineActor);

		this.setBounds(bounds);

		this.GetRenderer().AddActor(BoxAxisActor);

		this.GetRenderer().GetActiveCamera().SetParallelProjection(1);
		this.GetRenderer().ResetCamera();
	}

	public void deActivateWidget()
	{
		//	super.Delete();
		this.ren.RemoveAllViewProps();
		this.rw.RemoveRenderer(ren);

	}
	public void activateWidget()
	{

	}

	public void tabChanged()
	{
		this.ren.RemoveAllViewProps();
		this.rw.RemoveRenderer(ren);
		//Iterator it = imageDataMap.entrySet().iterator();
		//		vtkImageActor actor; 
		//		while (it.hasNext()) {
		//		        Map.Entry pairs = (Map.Entry)it.next();
		//		        actor = actorsMap.get(pairs.getKey());
		//		        this.ren.RemoveActor(actor);
		//		        color.SetInput(imageDataMap.get(pairs.getKey()));
		//		        actor.SetInput(color.GetOutput());
		//		  //      this.ren.AddActor(actor);
		//		        it.remove(); // avoids a ConcurrentModificationException
		//		    }
		//this.Render();
	}
	public void setBounds(double b[])
	{
		bounds = b;
		double offset = 0.05*1.5*512;
		double[] center = {(b[0]+b[1])/2,(b[2]+b[3])/2,(b[4]+b[5])/2};
		AxisLabelActors.elementAt(0).SetPosition(bounds[1] + offset, center[1], center[2]);
		AxisLabelActors.elementAt(1).SetPosition(center[0], bounds[3] + offset,  center[2]);
		AxisLabelActors.elementAt(2).SetPosition(center[0], center[1], bounds[5] + offset);

		AxisLabelActors.elementAt(3).SetPosition(bounds[0] - offset, center[1], center[2]);
		AxisLabelActors.elementAt(4).SetPosition(center[0], bounds[2] - offset, center[2]);
		AxisLabelActors.elementAt(5).SetPosition(center[0],center[1], bounds[4] - offset);
		boxSource.SetBounds(b);
	}

	public void UpdateSliderValueRanges()
	{
		if(mode == 0)
			return;
		double val;
		for(int i=0;i<3;i++)
		{

			targetPointSliders[i].setMinimum((int)((bounds[2*i]-10)*100));
			targetPointSliders[i].setMaximum((int) ((bounds[2*i +1]+10)*100));
			val = (bounds[2*i] +  bounds[2*i +1])/2;
			val = val*100;
			targetPointSliders[i].setValue((int)val);
			targetPointSliders[i].addChangeListener(this);
			targetPointLabels[i].setText(Double.toString(((double)((int)val))/100));
			startingPointSliders[i].setMinimum((int)((bounds[2*i]-10)*100));
			startingPointSliders[i].setMaximum((int) ((bounds[2*i +1]+10)*100));
			startingPointSliders[i].setValue((int)val);
			startingPointLabels[i].setText(Double.toString(((double)((int)val))/100));
			startingPointSliders[i].addChangeListener(this);
		}
		System.out.println(" Updating Slider With Bounds::" + bounds[0] + " " + bounds[1] + " "+bounds[2] + " "+bounds[3]);
	}
	public void SetTargetPointSliders(JSlider[] sliders)
	{
		System.out.println("Target Point Sliders are Set");
		targetPointSliders = sliders;
	}
	public void SetStartingPointSliders(JSlider[] sliders)
	{
		startingPointSliders = sliders;
	}
	public void SetTargetPointLabels(JLabel[] labels)
	{
		targetPointLabels = labels;
	}
	public void SetStartingPointLabels(JLabel[] labels)
	{
		startingPointLabels = labels;
	}
	public void addImageToInteractor(vtkMatrix4x4 transform,vtkImageData imageData,double[] wl)
	{
		vtkImageActor imageActor = new vtkImageActor();

		
		actorsMap.put(transform,imageActor);

		vtkLookupTable table = new vtkLookupTable();
		//System.out.println(imageData.GetScalarRange());
		table.SetRange((wl[1]-wl[0])/2,(wl[1]+wl[0])/2); // image intensity range
		table.SetValueRange(0.0, 1.0); // from black to white
		table.SetSaturationRange(0.0, 0.0); // no color saturation
		table.SetRampToLinear();
		table.Build();
		vtkImageMapToColors color = new vtkImageMapToColors();
		color.SetLookupTable(table);
		color.SetInput(imageData);

		// Display the image

		imageActor.SetInput(color.GetOutput());
		imageActor.SetUserMatrix(transform);


		color.Delete();
		table.Delete();

		actorTobeAdded = imageActor;
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				GetRenderer().AddActor(actorTobeAdded);
				Render();	
			}
		};
		SwingUtilities.invokeLater(runnable);
				//	color.Delete();
		//table.Delete();

	}
	public void removeImageFromInteractor(vtkMatrix4x4 transform)
	{
		if(actorsMap.containsKey(transform))
		{
			vtkImageActor imageActor = actorsMap.get(transform);
			
			actorTobeRemoved = imageActor;
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					actorTobeRemoved.SetVisibility(0);
					GetRenderer().RemoveActor(actorTobeRemoved);
					Render();	
				}
			};
			SwingUtilities.invokeLater(runnable);
			
			
			actorsMap.remove(transform);
			
		}
		return;
	}




	public void UpdateTransformPanel()
	{
		if(mode ==0)
			return;
		if(numberOfPoints<2)
			return;

		Matrix transform = new Matrix(4, 4);
		double distance = 0;
		for(int i=0;i<3;i++)
		{
			distance += ((targetPoint[i] - startingPoint[i])*(targetPoint[i] - startingPoint[i]));
		}
		distance =	Math.sqrt(distance);

		for(int i=0;i<3;i++)
		{
			transform.set(i, 3, startingPoint[i]);
			transform.set(i, 2,(targetPoint[i] - startingPoint[i])/distance);
			transform.set(3,i,1);
			transform.set(i, 0, 0);
			transform.set(i, 1, 0);
		}
		transform.set(0, 0, 1);
		transform.set(1, 1, 1);
		
		try {
			kinematicsModel.setDesiredTaskSpaceTransform(new Transform(transform), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		transformPanel.setTransform(kinematicsModel.getCurrentPoseTarget());

	}

	public void UpdateTargetPoint(double[] point)
	{
		targetPoint = point;
		lineSource.SetPoint1(targetPoint);
		targetSphereActor.SetPosition(point);

		UpdateTargetPointSliders();
		UpdateTransformPanel();
		this.Render();

	}
	public void UpdateStartingPoint(double[] point)
	{
		startingPoint =point;
		lineSource.SetPoint2(startingPoint);
		startingSphereActor.SetPosition(point);
		UpdateStartingPointSliders();

		UpdateTransformPanel();
		this.Render();
	}
	public void UpdateTargetPointSliders()
	{
		if(mode ==0 && numberOfPoints<1)
			return;
		double val;
		for(int i=0;i<3;i++)
		{
			targetPointSliders[i].removeChangeListener(this);
			targetPointSliders[i].removeChangeListener(this);
			val = targetPoint[i]*100;
			targetPointSliders[i].setValue((int)val);
			targetPointLabels[i].setText(Double.toString( ((double)((int)val))/100));
			targetPointSliders[i].addChangeListener(this);
			//		pointLabels[i].setText(Double.toString(pick[i]));
		}
	}
	public void UpdateStartingPointSliders()
	{
		if(mode ==0 && numberOfPoints <2)
			return;
		double val;
		for(int i=0;i<3;i++)
		{
			startingPointSliders[i].removeChangeListener(this);
			startingPointSliders[i].removeChangeListener(this);

			val = startingPoint[i]*100;
			startingPointSliders[i].setValue((int)val);
			startingPointLabels[i].setText(Double.toString( ((double)((int)val))/100));
			startingPointSliders[i].addChangeListener(this);
			//		pointLabels[i].setText(Double.toString(pick[i]));
		}
	}
	public void SetTargetPoint(double[] point)
	{
		targetPoint = point;
		numberOfPoints+=1;

		addSphereActorAtPoint(point);

		lineSource.SetPoint1(targetPoint);
		UpdateTargetPointSliders();
		UpdateTransformPanel();

	}
	public void SetStartingPoint(double[] point)
	{
		startingPoint = point;
		numberOfPoints+=1;

		lineSource.SetPoint2(startingPoint);
		lineActor.SetVisibility(1);

		addSphereActorAtPoint(point);
		UpdateStartingPointSliders();
		UpdateTransformPanel();
	}
	public void addSphereActorAtPoint(double[] point)
	{
		vtkSphereSource sphere = new vtkSphereSource();
		sphere.SetRadius(10);

		vtkPolyDataMapper sphereMapper = new vtkPolyDataMapper();
		sphereMapper.SetInputConnection(sphere.GetOutputPort());


		vtkActor sphereActor;
		if(numberOfPoints ==2)
		{
			startingSphereActor= new vtkActor();
			sphereActor = startingSphereActor;
		}
		else
		{
			targetSphereActor = new vtkActor();
			sphereActor = targetSphereActor;
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
		sphereActor = null;
		sphereMapper.Delete();
		sphere.Delete();
	}

	void SetMode(int m)
	{
		mode =m;
		if(m==0)
			return;

		if(numberOfPoints==0)
			UpdateSliderValueRanges();
	}
	void SetTransformPanel(MatrixDisplay panel)
	{
		this.transformPanel = panel ;
	}
	double[] GetDisplayToWorldCoordinates(double x, double y)
	{
		double[] coordinates = new double[3];
		coordinates[0]=(double)x;
		coordinates[1]=(double)y;
		coordinates[2]=1;
		double[] viewCord = new double[3];
		double[] worldCord = new double[3];

		this.GetRenderer().SetDisplayPoint(coordinates);


		this.GetRenderer().SetDisplayPoint(coordinates);
		//		this.GetRenderer().DisplayToView();
		//		viewCord = this.GetRenderer().GetViewPoint();
		this.GetRenderer().DisplayToWorld();
		worldCord=	this.GetRenderer().GetWorldPoint();

		double[] focalPoint;
		focalPoint =this.GetRenderer().GetActiveCamera().GetPosition();
		worldCord[0] =  focalPoint[0] -worldCord[0] ;
		worldCord[1] =focalPoint[1] -worldCord[1] ;
		worldCord[2] =focalPoint[2] -worldCord[2] ;
		return worldCord;

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		actorSelected = false;
		super.mousePressed(e);

		if(mode ==0)
			return;
		if(e.getButton() == MouseEvent.BUTTON3 && numberOfPoints<2)
		{
			int[] eventPos;
			eventPos = this.getRenderWindowInteractor().GetEventPosition();
			vtkPointPicker picker = new vtkPointPicker();
			//clickCoords contains the coordinates of the click, return by vtkWindowInteractor

			picker.SetTolerance(0.01);
			int result = picker.Pick(eventPos[0], eventPos[1], 0.0, this.GetRenderer());

			double[] pick;
			pick = picker.GetPickPosition();
			numberOfPoints++;
			addSphereActorAtPoint(pick);	

			if(numberOfPoints ==1)
			{
				targetPoint = pick;
				planarView.SetTargetPoint(targetPoint);
				UpdateTargetPointSliders();
				lineSource.SetPoint1(targetPoint);
			}
			else
			{
				startingPoint = pick;

				planarView.SetStartingPoint(startingPoint);

				lineSource.SetPoint2(startingPoint);

				UpdateStartingPointSliders();
				lineActor.SetVisibility(1);
				UpdateTransformPanel();
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

				if((actor!=null ) &&(actor == targetSphereActor || actor == startingSphereActor))
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

	@Override
	public void mouseDragged(MouseEvent e)
	{
		//super.mouseDragged(e);
		super.mouseMoved(e);
		if(mode == 0)
			return;
		if(actorSelected == true)
		{
			int[] eventPos;
			eventPos = this.getRenderWindowInteractor().GetEventPosition();
			vtkPointPicker picker = new vtkPointPicker();
			//clickCoords contains the coordinates of the click, return by vtkWindowInteractor

			picker.SetTolerance(0.01);
			int result = picker.Pick(eventPos[0], eventPos[1], 0.0, this.GetRenderer());

			double[] pick;
			pick = picker.GetPickPosition();

			selectedActor.SetPosition(pick);
			if(selectedActor == targetSphereActor)
			{
				targetPoint = pick;
				planarView.UpdateTargetPoint(targetPoint);


				UpdateTargetPointSliders();
				lineSource.SetPoint1(targetPoint);
			}
			else 
			{
				startingPoint = pick;
				planarView.UpdateStartingPoint(startingPoint);
				lineSource.SetPoint2(startingPoint);
				UpdateStartingPointSliders();
			}
			if(numberOfPoints==2)
				UpdateTransformPanel();


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

	public void SetPlanarView(ImagingPanel panel)
	{
		this.planarView = panel;
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
	public void mouseMoved(MouseEvent e) 
	{

		super.mouseMoved(e);
	}
	//	super.mouseDragged(e);
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if(mode == 0)
			return;
		double val;
		for(int i=0;i<3;i++)
		{
			if(e.getSource().equals(targetPointSliders[i]) && numberOfPoints >0)
			{
				targetPoint[i] = ((double)targetPointSliders[i].getValue())/100;
				targetSphereActor.SetPosition(targetPoint);
				val = targetPoint[i]*100;
				targetPointLabels[i].setText(Double.toString((((double)((int)val)))/100));
				planarView.UpdateTargetPoint(targetPoint);
				lineSource.SetPoint1(targetPoint);
				this.Render();
				break;
			}
			if(e.getSource().equals(startingPointSliders[i]) && numberOfPoints > 1)
			{
				UpdateTransformPanel();
				startingPoint[i] = ((double)startingPointSliders[i].getValue())/100;
				startingSphereActor.SetPosition(startingPoint);
				val = startingPoint[i]*100;
				startingPointLabels[i].setText(Double.toString((((double)((int)val)))/100));
				planarView.UpdateStartingPoint(startingPoint);
				lineSource.SetPoint2(startingPoint);
				this.Render();
				break;
			}
		}
		if(numberOfPoints>1)
			this.UpdateTransformPanel();
	}
	public void setKinematicsModel(AbstractKinematics model) {
		this.kinematicsModel = model;
	}
	public double[] getEntryPoint() {
		
		if(numberOfPoints==2)
		return startingPoint;
		
		return null;
	}
	public double[] getTargetPoint() {
		if(numberOfPoints>=1)
			return targetPoint;
		return null;
	}
}
