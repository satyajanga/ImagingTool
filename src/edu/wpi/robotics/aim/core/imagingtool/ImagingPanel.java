package edu.wpi.robotics.aim.core.imagingtool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Map;

import javax.swing.JPanel;

import vtk.vtkDataObject;
import vtk.vtkImageData;
import vtk.vtkMatrix4x4;

public class ImagingPanel extends JPanel implements DisplayChangeListener
{

	/**
	 * 
	 */
	private vtkImageData imageData = null;
	private int mode; 
	private int numberOfPoints =0;
	private double[] startingPoint;
	private double[] targetPoint;
	private static final long serialVersionUID = 1L;
	private InteractorWidget widget;
	private ImageView[] planarViews = new ImageView[3];
	private Map<String,String> headerInfo;
	public ImagingPanel()
	{
		setUpViews();
	}
	public void setUpViews()
	{
		for(int i=0;i<3;i++)
		{
			planarViews[i] = new ImageView();
		}
		GridBagConstraints c = new GridBagConstraints();
		setLayout(new GridBagLayout());

		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight =1;
		c.gridwidth=1;
		planarViews[0] = new ImageView();
		c.gridx = 0;
		c.gridy = 0;
		setBackground(Color.BLACK);
		add(planarViews[0], c);

		planarViews[1] = new ImageView();
		planarViews[1].setSize(new Dimension(100,100));
		c.gridx = 1;
		c.gridy = 0;
		add(planarViews[1], c);


		planarViews[2] = new ImageView();

		c.gridx = 2;
		c.gridy = 0;
		add( planarViews[2], c);
	}
	public void setMode(int m)
	{
		mode = m;
		for(int i=0;i<3;i++)
			planarViews[i].SetMode(m);
	}

	public void setImageData(vtkImageData data,Map<String, String> header)
	{
		imageData = data;
		headerInfo = header;
		for(int i=0;i<3;i++)
		{
			planarViews[i].setData(i, data, mode, headerInfo);
			planarViews[i].addListener(this);
		}
	}


	public void setWidget(InteractorWidget w)
	{
		widget = w;

	}

	public void tabChanged()
	{
		this.removeAll();
		this.planarViews[0].Delete();
		this.planarViews[1].Delete();
		this.planarViews[2].Delete();
		this.revalidate();
		for(int i=0;i<3;i++)
		{
			planarViews[i].Delete();
		}
		setUpViews();
		if(imageData!=null)
			setImageData(imageData, headerInfo);
		int temp = numberOfPoints;
		numberOfPoints =0;
		if(temp>1)
		{
			SetTargetPoint(targetPoint);
			widget.SetTargetPoint(targetPoint);
			if(temp ==2)
			{
				SetStartingPoint(startingPoint);
				widget.SetStartingPoint(startingPoint);
			}
		}
	}
	public void SetTargetPoint(double[] point)
	{
		targetPoint = point;
		numberOfPoints+=1;
		for(int i=0;i<3;i++)
		{
			planarViews[i].SetTargetPoint(point);
		}
	}

	public void SetStartingPoint(double[] point)
	{
		startingPoint = point;
		numberOfPoints+=1;
		for(int i=0;i<3;i++)
		{
			planarViews[i].SetStartingPoint(point);
		}
	}

	public void UpdateTargetPoint(double[] point)
	{
		targetPoint = point;
		for(int i=0;i<3;i++)
		{
			planarViews[i].UpdateTargetPoint(point);
		}
	}

	public void UpdateStartingPoint(double[] point)
	{
		startingPoint = point;
		for(int i=0;i<3;i++)
		{
			planarViews[i].UpdateStartingPoint(point);
		}
	}
	@Override
	public void targetPointAdded(double[] point, int orientation) 
	{
		targetPoint = point;
		numberOfPoints+=1;
		widget.SetTargetPoint(point);
		switch(orientation)
		{
		case 0:
		{
			planarViews[1].SetTargetPoint(point);
			planarViews[2].SetTargetPoint(point);
			break;
		}
		case 1:
		{
			planarViews[0].SetTargetPoint(point);
			planarViews[2].SetTargetPoint(point);

			break;
		}
		case 2:
		{
			planarViews[0].SetTargetPoint(point);
			planarViews[1].SetTargetPoint(point);
			break;
		}
		}

	}

	@Override
	public void startingPointAdded(double[] point, int orientation)
	{
		startingPoint = point;
		numberOfPoints+=1;
		widget.SetStartingPoint(point);
		switch(orientation)
		{
		case 0:
		{
			planarViews[1].SetStartingPoint(point);
			planarViews[2].SetStartingPoint(point);
			break;
		}
		case 1:
		{
			planarViews[0].SetStartingPoint(point);
			planarViews[2].SetStartingPoint(point);

			break;
		}
		case 2:
		{
			planarViews[0].SetStartingPoint(point);
			planarViews[1].SetStartingPoint(point);

			break;
		}
		}
	}

	@Override
	public void targetPointUpdated(double[] point, int orientation)
	{
		targetPoint = point;
		widget.UpdateStartingPoint(point);

		switch(orientation)
		{
		case 0:
		{
			planarViews[1].UpdateTargetPoint(point);
			planarViews[2].UpdateTargetPoint(point);
			break;
		}
		case 1:
		{
			planarViews[0].UpdateTargetPoint(point);
			planarViews[2].UpdateTargetPoint(point);
			break;
		}
		case 2:
		{
			planarViews[0].UpdateTargetPoint(point);
			planarViews[1].UpdateTargetPoint(point);

			break;
		}
		}
	}

	@Override
	public void startingPointUpdated(double[] point, int orientation)
	{
		startingPoint =point;
		widget.UpdateStartingPoint(point);
		switch(orientation)
		{
		case 0:
		{
			planarViews[1].UpdateStartingPoint(point);
			planarViews[2].UpdateStartingPoint(point);
			break;
		}
		case 1:
		{
			planarViews[0].UpdateStartingPoint(point);
			planarViews[2].UpdateStartingPoint(point);
			break;
		}
		case 2:
		{
			planarViews[0].UpdateStartingPoint(point);
			planarViews[1].UpdateStartingPoint(point);

			break;
		}
		}
	}

	public vtkImageData getImageData(int orientation) 
	{

		return planarViews[orientation].getImageData();
	}

	public vtkMatrix4x4 getAxis(int orientation) 
	{

		return planarViews[orientation].getAxis();
	}

	public void setSliderValue(int value, int orientation) 
	{
		planarViews[orientation].setSliderValue(value);

	}
	public void removeImageData() 
	{
		for(int i=0;i<3;i++)
		{
			planarViews[i].removeImageData();
		}
		this.revalidate();
		this.repaint();
	}
	public void setImageEnabled(int b)
	{
		for(int i=0;i<3;i++)
			planarViews[i].SetImageEnabled(b);
		
		this.revalidate();
		this.repaint();
	}
	
}
