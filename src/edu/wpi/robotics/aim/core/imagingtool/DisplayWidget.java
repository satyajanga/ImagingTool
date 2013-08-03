package edu.wpi.robotics.aim.core.imagingtool;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vtk.vtkImageData;
import vtk.vtkImageFlip;
import vtk.vtkMatrix4x4;
import edu.wpi.robotics.aim.core.robot.AbstractKinematics;
import edu.wpi.robotics.aim.sample.gui.MatrixDisplay;

public class DisplayWidget extends JPanel implements ActionListener,ChangeListener,ItemListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static boolean shouldFill = true;
	final static boolean shouldWeightX = true;
	final static boolean RIGHT_TO_LEFT = false;

	private boolean disableVolume = false;
	int mode =0;

	private JSlider axialSlider;
	private JSlider coronalSlider;
	private JSlider sagittalSlider;
	private JCheckBox axialCheckBox;
	private JCheckBox coronalCheckBox;
	private JCheckBox sagittalCheckBox;
	private vtkImageData imageData = null;
	Map<String,String> headerInfo;
	private ImagingPanel planarViewsPanel = new ImagingPanel();
	private JLabel[] sliderLabels = new JLabel[3];
	private Map<vtkMatrix4x4,vtkImageData> imageDataMap;
	double[] center ={0,0,0} ;
	JPanel interactorPanel;
	JPanel imagingPanel;
	Map<String, vtkImageData> displayed2DObjects = new HashMap<String, vtkImageData>();
	InteractorWidget widget;
	public DisplayWidget()
	{
		addEmptyComponentsToPane();		
	}
	public void addEmptyComponentsToPane()
	{
		//	this.setBackground(Color.black);

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		/*gridx is for column, gridy is for row, gridWidth is for number of columns and gridHeight is for number of rows;*/


		widget = new InteractorWidget();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 6;
		c.gridheight = 10;
		interactorPanel = new JPanel();
		interactorPanel.setLayout(new BorderLayout());
		interactorPanel.add(widget,BorderLayout.CENTER);
		this.add(interactorPanel, c);		

		//c.ipady = 200;
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridheight = 1;
		axialCheckBox = new JCheckBox("Axial");
		//	c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		checkBoxPanel.add(axialCheckBox, c);

		coronalCheckBox = new JCheckBox("Coronal");
		c.gridx = 1;
		c.gridy = 0;
		checkBoxPanel.add(coronalCheckBox, c);

		sagittalCheckBox = new JCheckBox("Sagittal");
		c.gridx = 2;
		c.gridy = 0;
		checkBoxPanel.add(sagittalCheckBox, c);


		c.gridx = 0;
		c.gridy =10;
		c.gridheight =1;
		c.gridwidth =6;
		c.weightx = 0.10;
		c.weighty = 0.10;
		this.add(checkBoxPanel,c);


		JPanel sliderPanel1 = new JPanel();
		sliderPanel1.setLayout(new GridBagLayout());

		sliderLabels[0] = new JLabel();
		sliderLabels[0].setText("50");
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		sliderPanel1.add(sliderLabels[0],c);


		axialSlider = new JSlider();
		axialSlider.setMaximum(100);
		axialSlider.setMinimum(0);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;
		sliderPanel1.add(axialSlider, c);



		sliderLabels[1] = new JLabel();
		sliderLabels[1].setText("50");
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 3;
		c.gridy = 0;
		sliderPanel1.add(sliderLabels[1],c);
		c.fill = GridBagConstraints.HORIZONTAL;

		coronalSlider = new JSlider();
		coronalSlider.setMaximum(100);
		coronalSlider.setMinimum(0);
		c.gridx = 2;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;

		sliderPanel1.add(coronalSlider, c);



		sliderLabels[2] = new JLabel();
		sliderLabels[2].setText("50");
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 5;
		c.gridy = 0;
		sliderPanel1.add(sliderLabels[2],c);

		sagittalSlider = new JSlider();
		sagittalSlider.setMaximum(100);
		sagittalSlider.setMinimum(0);
		c.gridx = 4;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;

		sliderPanel1.add(sagittalSlider, c);
		c.gridx = 0;
		c.gridy =11;
		c.gridheight =1;
		c.gridwidth =6;

		this.add(sliderPanel1,c);


		c.gridx = 0;
		c.gridy =0;
		c.gridheight = 1;
		c.gridwidth =1;
		c.weightx = 0.6;
		c.weighty = 0.35;
		c.fill = GridBagConstraints.BOTH;
		imagingPanel = new JPanel();
		imagingPanel.add(planarViewsPanel,c);

		c.gridx = 0;
		c.gridy =12;
		c.gridheight = 5;
		c.gridwidth =6;
		c.weightx = 1;
		c.weighty = 1;
		this.add(planarViewsPanel,c);
		planarViewsPanel.setWidget(widget);
		widget.SetPlanarView(planarViewsPanel);
	}
	public void setData(vtkImageData data,Map<String,String> header) 
	{
		if(imageData == data)
			return;
		headerInfo = header;
		imageData = data;
		processImageDataForBounds();
		addComponentsToPane();
	}

	public void processImageDataForBounds()
	{

		int extent[] = imageData.GetWholeExtent();
		System.out.println("Extent ::" + extent[0] + " " + extent[1] + " "+ extent[2]+" "+extent[3]+" "+extent[4]+ " "+ extent[5]);
		double[] spacing = imageData.GetSpacing();
		//double[] spacing = imageData.GetSpacing();
		double[] origin = imageData.GetOrigin();


		center[0] = origin[0] + spacing[0] * 0.5 * (extent[0] + extent[1]);
		center[1] = origin[1] + spacing[1] * 0.5 * (extent[2] + extent[3]);
		center[2] = origin[2] + spacing[2] * 0.5 * (extent[4] + extent[5]);

	}
	public  void addComponentsToPane()
	{
		widget.setBounds(imageData.GetBounds());
		//widget.UpdateSliderValueRanges();

		axialCheckBox.addItemListener(this);
		coronalCheckBox.addItemListener(this);
		sagittalCheckBox.addItemListener(this);


		axialSlider.setMaximum((int)(center[2]*2*100));
		axialSlider.setValue((int)(center[2]*100));
		axialSlider.setMinimum(0);
		axialSlider.addChangeListener(this);
		sliderLabels[0].setText(Double.toString(center[2]));


		coronalSlider.setMaximum((int)(center[1]*2*100));
		coronalSlider.setMinimum(0);
		coronalSlider.setValue((int)(center[1]*100));
		coronalSlider.addChangeListener(this);
		sliderLabels[1].setText(Double.toString(center[1]));

		sagittalSlider.setMaximum((int)(center[0]*2*100));
		sagittalSlider.setValue((int)(center[0]*100));
		sagittalSlider.setMinimum(0);
		sagittalSlider.addChangeListener(this);
		sliderLabels[2].setText(Double.toString(center[0]));
		planarViewsPanel.setImageData(imageData, headerInfo);

		this.repaint();
	}

	void addObjectToActor(vtkMatrix4x4 transform,vtkImageData data)
	{
		double[]  wl = new double[2];
		if(headerInfo!=null)
		{
			wl[0] = Double.valueOf(headerInfo.get("window"));
			wl[1] = Double.valueOf(headerInfo.get("level"));
		}
		else
		{
			wl[0] = 300;
			wl[1] =40;
		}
		widget.addImageToInteractor(transform, data,wl);	
	}

	void set2DObjectsTobeDisplayed(Map<String,vtkImageData> objects)
	{
		Set<String> keysToadded = objects.keySet();
		keysToadded.removeAll( displayed2DObjects.keySet());
		Set<String> keysToRemoved = displayed2DObjects.keySet();
		keysToRemoved.removeAll( displayed2DObjects.keySet());
	}

	void SetMode(int m)
	{
		mode = m;
		this.widget.SetMode(m);
		planarViewsPanel.setMode(m);
	}
	void SetTargetPointSliders(JSlider[] sliders)
	{
		widget.SetTargetPointSliders(sliders);
	}

	void SetStartingPointSliders(JSlider[] sliders)
	{
		widget.SetStartingPointSliders(sliders);
	}

	void SetTargetPointLabels(JLabel[] labels)
	{
		widget.SetTargetPointLabels(labels);
	}

	void SetStartingPointLabels(JLabel[] labels)
	{
		widget.SetStartingPointLabels(labels);
		//	widget.UpdateSliderValueRanges();

	}
	void SetTransformPanel(MatrixDisplay panel)
	{
		widget.SetTransformPanel(panel);
	}
	@Override
	public void actionPerformed(ActionEvent e) {


	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if(disableVolume || imageData==null)
			return;
		double[]  wl = new double[2];
		wl[0] = Double.valueOf(headerInfo.get("window"));
		wl[1] = Double.valueOf(headerInfo.get("level"));
		double val=0;
		if (e.getSource().equals(axialSlider))
		{
			widget.removeImageFromInteractor(planarViewsPanel.getAxis(0));
			val = ((double)axialSlider.getValue())/100;
			val = val*100;
			planarViewsPanel.setSliderValue(axialSlider.getValue(),0);
			sliderLabels[0].setText(Double.toString((((double)((int)val)))/100));
			if(axialCheckBox.isSelected())
			{
				widget.addImageToInteractor(planarViewsPanel.getAxis(0), planarViewsPanel.getImageData(0),wl);	
			}
		}
		else if (e.getSource().equals(coronalSlider))
		{
			widget.removeImageFromInteractor(planarViewsPanel.getAxis(1));
			planarViewsPanel.setSliderValue(coronalSlider.getValue(),1);
			val = ((double)coronalSlider.getValue())/100;
			val = val*100;	
			sliderLabels[1].setText(Double.toString((((double)((int)val)))/100));

			if(coronalCheckBox.isSelected())
			{
				widget.addImageToInteractor(planarViewsPanel.getAxis(1), planarViewsPanel.getImageData(1),wl);	
			}
		}
		else if (e.getSource().equals(sagittalSlider))
		{
			widget.removeImageFromInteractor(planarViewsPanel.getAxis(2));
			planarViewsPanel.setSliderValue(sagittalSlider.getValue(),2);
			val = ((double)sagittalSlider.getValue())/100;
			val = val*100;
			sliderLabels[2].setText(Double.toString((((double)((int)val)))/100));

			if(sagittalCheckBox.isSelected())
			{
				widget.addImageToInteractor(planarViewsPanel.getAxis(2), planarViewsPanel.getImageData(2),wl);	
			}
		}
		this.repaint();
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if(disableVolume || imageData==null)
			return;

		double[]  wl = new double[2];
		wl[0] = Double.valueOf(headerInfo.get("window"));
		wl[1] = Double.valueOf(headerInfo.get("level"));
		if(e.getSource().equals(axialCheckBox))
		{
			if(e.getStateChange() ==ItemEvent.SELECTED)
			{
				vtkImageFlip flip = new vtkImageFlip();
				flip.SetInput(planarViewsPanel.getImageData(0));
				flip.SetFilteredAxis(0);
				flip.Update();
				widget.addImageToInteractor(planarViewsPanel.getAxis(0),flip.GetOutput(),wl );
				flip.Delete();
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				widget.removeImageFromInteractor(planarViewsPanel.getAxis(0));
			}
		}
		else if(e.getSource().equals(coronalCheckBox))
		{
			if(e.getStateChange() ==ItemEvent.SELECTED)
			{
				widget.addImageToInteractor(planarViewsPanel.getAxis(1), planarViewsPanel.getImageData(1),wl);
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				widget.removeImageFromInteractor(planarViewsPanel.getAxis(1));
			}
		}
		else if(e.getSource().equals(sagittalCheckBox))
		{
			if(e.getStateChange() ==ItemEvent.SELECTED)
			{
				widget.addImageToInteractor(planarViewsPanel.getAxis(2), planarViewsPanel.getImageData(2),wl);

			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				widget.removeImageFromInteractor(planarViewsPanel.getAxis(2));

			}
		}
		this.repaint();
	}
	/*
	public void tabChanged()
	{
		//	widget.destroyTheObject();
		interactorPanel.removeAll();
		interactorPanel.revalidate();
		widget.destroyTheObject();
		widget.Delete();

		widget= new InteractorWidget();
		interactorPanel.add(widget,BorderLayout.CENTER);

		if(imageData!=null )
		{
			widget.setBounds(imageData.GetBounds());
		}
		planarViewsPanel.setWidget(widget);
		planarViewsPanel.tabChanged();

		planarViewsPanel.setMode(mode);

		if(axialCheckBox.isSelected())
		{
			widget.addImageToInteractor(planarViewsPanel.getAxis(0), planarViewsPanel.getImageData(0));

		}
		if(sagittalCheckBox.isSelected())
		{
			widget.addImageToInteractor(planarViewsPanel.getAxis(1), planarViewsPanel.getImageData(1));

		}
		if(coronalCheckBox.isSelected())
		{
			widget.addImageToInteractor(planarViewsPanel.getAxis(2), planarViewsPanel.getImageData(2));

		}
		planarViewsPanel.setSliderValue(axialSlider.getValue(), 0);
		planarViewsPanel.setSliderValue(coronalSlider.getValue(), 1);
		planarViewsPanel.setSliderValue(sagittalSlider.getValue(), 2);
		widget.SetPlanarView(planarViewsPanel);

	}
	public void destroyObject()
	{
		if(imageData!=null)
			imageData.Delete();



		widget.destroyTheObject();
		widget.Delete();
	}
	public void tabIsChanging()
	{
		interactorPanel.removeAll();
		imagingPanel.removeAll();
		this.revalidate();
	//	this.repaint();
	}

	public void tabChange()
	{
		interactorPanel.add(widget);
		imagingPanel.add(planarViewsPanel);
		this.revalidate();
	//	this.repaint();
	}
	 */
	public void removeData(vtkImageData data) 
	{
		if(imageData==null)
			return;
		removeFromInteractor();
		planarViewsPanel.removeImageData();
		imageData = null;
		setSize(getWidth()-1,getHeight()-1);
		setSize(getWidth()+1,getHeight()+1);
		validate();
		repaint();

	}
	public void removeFromInteractor()
	{
		if(axialCheckBox.isSelected())
		{
			widget.removeImageFromInteractor(planarViewsPanel.getAxis(0));
			axialCheckBox.setSelected(false);
		}
		if(coronalCheckBox.isSelected())
		{
			widget.removeImageFromInteractor(planarViewsPanel.getAxis(1));
			coronalCheckBox.setSelected(false);
		}
		if(sagittalCheckBox.isSelected())
		{
			widget.removeImageFromInteractor(planarViewsPanel.getAxis(2));
			sagittalCheckBox.setSelected(false);
		}

	}
	private void addToInteractor() 
	{

		double[]  wl = new double[2];
		wl[0] = Double.valueOf(headerInfo.get("window"));
		wl[1] = Double.valueOf(headerInfo.get("level"));
		if(axialCheckBox.isSelected())
		{
			widget.addImageToInteractor(planarViewsPanel.getAxis(0), planarViewsPanel.getImageData(0),wl);
		}
		if(coronalCheckBox.isSelected())
		{
			widget.addImageToInteractor(planarViewsPanel.getAxis(1), planarViewsPanel.getImageData(1),wl);
		}
		if(sagittalCheckBox.isSelected())
		{
			widget.addImageToInteractor(planarViewsPanel.getAxis(2), planarViewsPanel.getImageData(2),wl);
		}
	}

	public void setVolumeDisabled(boolean selected) 
	{

		if(imageData == null)
			return;
		disableVolume = selected;
		if(selected)
		{
			removeFromInteractor();
			planarViewsPanel.setImageEnabled(0);
		}
		else
		{
			addToInteractor();
			planarViewsPanel.setImageEnabled(1);
		}
		setSize(this.getWidth()-1,this.getHeight()-1);

		setSize(this.getWidth()+1,this.getHeight()+1);

		this.validate();
		this.repaint();
	}
	public void removeImageFromInteractor(vtkMatrix4x4 pos)
	{
		widget.removeImageFromInteractor(pos);
	}
	public void setKinematicsModel(AbstractKinematics kinematicsModel)
	{
		widget.setKinematicsModel(kinematicsModel);
	}
	public void showEntryPointInSlices()
	{
		double[] entryPoint = widget.getEntryPoint();
		if(entryPoint==null)
			return;

		axialSlider.setValue((int)(entryPoint[2]*100));
		coronalSlider.setValue((int)(entryPoint[1]*100));
		sagittalSlider.setValue((int)(entryPoint[0]*100));

	}
	
	public void showTargetPointInSlices()
	{
		double[] targetPoint = widget.getTargetPoint();
		if(targetPoint==null)
			return;

		axialSlider.setValue((int)(targetPoint[2]*100));
		coronalSlider.setValue((int)(targetPoint[1]*100));
		sagittalSlider.setValue((int)(targetPoint[0]*100));

	}

}
