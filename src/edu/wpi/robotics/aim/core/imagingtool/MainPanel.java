package edu.wpi.robotics.aim.core.imagingtool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import vtk.vtkImageData;



public class MainPanel extends JPanel implements ActionListener
{

	private JPanel leftPanel;
	private JPanel rightPanel;

	private JPanel panel3DObjects;
	private Map<String,vtkImageData> loaded3DObjects;


	private Map<String,JCheckBox> checkBoxesFor2DObjects;

	private Map<String,Map<String,String> > loaded3DHeaderInfo;
	private JComboBox comboBoxFor3DObjects;


	private DisplayWidget displayWidget;
	private static final long serialVersionUID = 1L;
	private JPanel openIGTPanel;


	private JButton display3DObjects;
	private JButton unload3DObjects;

	private JCheckBox connect = new JCheckBox("Connect");

	private JTextField host;
	private JTextField port;

	private OpenIGTPanel panel2DObjects;
	Vector<String> keys3d;

	Box buttonBox2D;
	JSplitPane splitPane ;
	JCheckBox disableVolume;
	public void SetDisplayWidget(DisplayWidget w)
	{
		this.setBorder(BorderFactory.createTitledBorder("Main Panel"));//(Color.black));

		this.displayWidget =w;
		createContentPane();
		panel2DObjects.SetDisplayWidget(displayWidget);
	}
	public MainPanel()
	{
		keys3d = new Vector<String>();
		loaded3DHeaderInfo = new HashMap<String,Map<String,String> >();
		loaded3DObjects = new HashMap<String, vtkImageData>();
		checkBoxesFor2DObjects = new HashMap<String, JCheckBox>();
		comboBoxFor3DObjects = new JComboBox(keys3d);


		display3DObjects = new JButton("Apply");
		unload3DObjects = new JButton("Unload");
		display3DObjects.addActionListener(this);
		unload3DObjects.addActionListener(this);



		port = new JTextField("18944",10);
		host = new JTextField("127.0.0.1",10);


	}
	public void createContentPane() 
	{
		//Create the content-pane-to-be.

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
		GridBagLayout layout = new GridBagLayout();

		leftPanel.setLayout(layout);


		panel2DObjects = new OpenIGTPanel();

		panel2DObjects.setName("2D Objects Panel");
		panel3DObjects = new JPanel(new GridBagLayout());
		panel3DObjects.setName("3D Objects Panel");
		openIGTPanel = new JPanel(new GridBagLayout());
		openIGTPanel.setName("OpenIGT Panel");


		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		leftPanel.add(new JScrollPane(panel2DObjects),c);
		panel2DObjects.setBorder(BorderFactory.createTitledBorder("2D Objects"));//(Color.black));

		c.gridy = 1;
		leftPanel.add(panel3DObjects,c);
		panel3DObjects.setBorder(BorderFactory.createTitledBorder("3D Objects"));//;(Color.black));

		c.gridy = 2;
		leftPanel.add(openIGTPanel,c);
		openIGTPanel.setBorder(BorderFactory.createTitledBorder("OpenIGT Link"));//(Color.black));




		c.gridx = 1;
		c.gridy =0;

		c.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = new JLabel("3D Objects");
		panel3DObjects.add(label,c);
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth =2;
		//c.gridwidth = GridBagConstraints.REMAINDER;
		//	c.gridheight  =2;
		panel3DObjects.add(comboBoxFor3DObjects,c);
		disableVolume = new JCheckBox("disable volume");



		disableVolume.addActionListener(this);
		c.gridwidth =2;
		c.gridheight =1;
		c.gridx = 2;
		c.gridy =1;
		panel3DObjects.add(disableVolume,c);
		c.gridheight = 1;
		c.gridx = 2;
		c.gridy = 4;


		c.anchor = GridBagConstraints.NORTH;


		Box buttonBox3D = Box.createHorizontalBox();
		buttonBox3D.add(display3DObjects);
		buttonBox3D.add(Box.createHorizontalStrut(20));
		buttonBox3D.add(unload3DObjects);
		panel3DObjects.add(buttonBox3D,c);

		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.gridy = 4;
		c.gridx = 2;
		c.anchor = GridBagConstraints.NORTH;


		Box openIGTBox = Box.createHorizontalBox();
		openIGTBox.add(connect);
		openIGTPanel.add(openIGTBox,c);
		connect.addActionListener(this);

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		openIGTPanel.add(new JLabel(" Host Address ::"),c);

		c.gridx =1;
		c.gridwidth = 2;
		openIGTPanel.add(host,c);

		c.gridx =0;
		c.gridy = 1;
		c.gridwidth = 1;
		openIGTPanel.add(new JLabel("Port Num ::"),c);
		c.gridx =1;
		c.gridwidth = 2;
		openIGTPanel.add(port,c);
		
		

	}



	public void Load3DVolume(String path)
	{
		ReadData imageReader = new ReadData();
		if(imageReader.updateData(path))
		{
			System.out.println("File Path ::" + path);
			String volumeLabel = imageReader.getVolumeLabel();
			vtkImageData pixelData = imageReader.getImageData();
			Map<String, String> header = imageReader.getHeaderInfo();

			loaded3DObjects.put(volumeLabel, pixelData);
			keys3d.addElement(volumeLabel);
			comboBoxFor3DObjects.setSelectedItem(volumeLabel);
			loaded3DHeaderInfo.put(volumeLabel, header);
		}
		else
		{
			System.out.println("Not Able to read Data");
		}

	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == display3DObjects)
		{
			String key =(String)comboBoxFor3DObjects.getSelectedItem();

			if(loaded3DObjects.containsKey(key))
			{
				System.out.println("Printing Image Data ::");
				loaded3DObjects.get(key).Print();
			System.out.println("Printing Image Data ::" + loaded3DObjects.get(key));
				displayWidget.setData(loaded3DObjects.get(key),loaded3DHeaderInfo.get(key));
				
				this.revalidate();
				this.repaint();
			}
		}
		else if(e.getSource() == unload3DObjects)
		{
			String key =(String)comboBoxFor3DObjects.getSelectedItem();
			displayWidget.removeData(loaded3DObjects.get(key));
			loaded3DHeaderInfo.remove(key);
			loaded3DObjects.remove(key);
			comboBoxFor3DObjects.removeItem(key);
			keys3d.removeElement(key);

			this.revalidate();
			this.repaint();
		}
		else if(e.getSource() == disableVolume)
		{
			displayWidget.setVolumeDisabled(disableVolume.isSelected());
			this.revalidate();
			this.repaint();
		}
		else if(e.getSource() == connect)
		{
			if(connect.isSelected())
			{
				int p = Integer.valueOf(port.getText());
				String hostName = host.getText();
				System.out.println("Coming to connect " + hostName + " Port: " + p);
				this.panel2DObjects.connectToServer(hostName, p);
			}	
			else 
			{
				this.panel2DObjects.disconnetFromServer();
			}
		}
	}
}
