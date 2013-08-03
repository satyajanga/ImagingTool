package edu.wpi.robotics.aim.core.imagingtool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.network.GenericIGTLinkServer;
import org.medcare.igtl.network.IOpenIgtPacketListener;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

import edu.wpi.robotics.aim.core.math.Transform;
import edu.wpi.robotics.aim.sample.gui.MatrixDisplay;

public class NavigationPanel extends JPanel implements ActionListener,IOpenIgtPacketListener
{

	/**
	 * 
	 */
	private JPanel openIGTPanel;

	private JPanel leftPanel;
	private DisplayWidget displayWidget;
	private JPanel panelTargetPointSliders;
	private JPanel panelStartingPointSliders;
	private MatrixDisplay transformMatrixPanel;

	private JSlider[] targetPointSliders = new JSlider[3];
	private JSlider[] startingPointSliders = new JSlider[3];

	private JLabel[] targetPointLabels = new JLabel[3];
	private JLabel[] startingPointLabels = new JLabel[3];

	private static final long serialVersionUID = 1L;

	JSplitPane splitPane ;
	private JButton sendToController;
	private JCheckBox connect = new JCheckBox("Connect");
	private JTextField host;
	private JTextField port;

	GenericIGTLinkServer server = null;

	JCheckBox displayEntry = new JCheckBox("Diplay in Slices");
	JCheckBox displayTarget = new JCheckBox("Diplay in Slices");
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == connect)
		{
			if(connect.isSelected())
			{
				int p = Integer.valueOf(port.getText());
				String hostName = host.getText();
				System.out.println("Coming to connect " + hostName + " Port: " + p);


				try {
					server = new GenericIGTLinkServer(p);
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(server==null)
				{
					System.out.println("Server Not Started in port ::" + Integer.valueOf(port.toString()));
				}
				else
					server.addIOpenIgtOnPacket(this);	

			}
			else
			{
				server.stopServer();
			}
			
		}else if(e.getSource() == displayEntry)
		{
			if(displayEntry.isSelected())
			{
				displayWidget.showEntryPointInSlices();
			}
		}
		else if(e.getSource() == displayTarget)
		{
			if(displayTarget.isSelected())
			{
				displayWidget.showTargetPointInSlices();
			}
		}

	}

	public void SetDisplayWidget(DisplayWidget w)
	{
		this.displayWidget =w;
		this.createContentPane();

		displayWidget.SetTargetPointSliders(targetPointSliders);
		displayWidget.SetStartingPointSliders(startingPointSliders);
		displayWidget.SetTargetPointLabels(targetPointLabels);
		displayWidget.SetStartingPointLabels(startingPointLabels);
		displayWidget.SetTransformPanel(transformMatrixPanel);

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
		c.weightx = 0.3;
		c.weighty =0.6;
		c.gridwidth = 1;
		c.gridheight = 1;

		this.add(leftPanel,c);

	}
	private void addElementsToLeftPanel()
	{
		GridBagLayout layout = new GridBagLayout();

		leftPanel.setLayout(layout);

		panelTargetPointSliders = new JPanel(new GridBagLayout());
		panelStartingPointSliders = new JPanel(new GridBagLayout());

		openIGTPanel = new JPanel(new GridBagLayout());
		openIGTPanel.setName("OpenIGT Panel");

		GridBagConstraints c = new GridBagConstraints();



		// Started Adding Objects To TargetPoint Panel
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;

		panelTargetPointSliders.add(new JLabel("R"),c);

		c.fill = GridBagConstraints.BOTH;

		c.gridx = 1;
		c.gridy = 0;
		targetPointSliders[0] = new JSlider(SwingConstants.HORIZONTAL);
		panelTargetPointSliders.add(targetPointSliders[0], c);	
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 0;
		targetPointLabels[0] = new JLabel();
		targetPointLabels[0].setText("100");
		panelTargetPointSliders.add(targetPointLabels[0],c);


		c.gridx = 0;
		c.gridy = 1;
		panelTargetPointSliders.add(new JLabel("A"),c);


		c.fill = GridBagConstraints.BOTH;

		c.gridy = 1;
		c.gridx = 1;
		targetPointSliders[1] = new JSlider(SwingConstants.HORIZONTAL);
		panelTargetPointSliders.add(targetPointSliders[1], c);	

		c.fill = GridBagConstraints.NONE;
		targetPointLabels[1] = new JLabel();
		targetPointLabels[1].setText("100");
		c.gridx =2;
		c.gridy = 1;

		panelTargetPointSliders.add(targetPointLabels[1],c);

		c.gridx = 0;
		c.gridy = 2;
		panelTargetPointSliders.add(new JLabel("S"),c);


		c.fill = GridBagConstraints.BOTH;

		c.gridy = 2;
		c.gridx = 1;
		targetPointSliders[2] = new JSlider(SwingConstants.HORIZONTAL);
		panelTargetPointSliders.add(targetPointSliders[2], c);	
		targetPointLabels[2] = new JLabel();
		targetPointLabels[2].setText("100");
		c.gridy = 2;
		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		panelTargetPointSliders.add(targetPointLabels[2],c);
		panelTargetPointSliders.setName("Target Point Sliders");

		c.gridx =1;
		c.gridy =3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panelTargetPointSliders.add(displayTarget,c);
		displayTarget.addActionListener(this);
		// Done Adding Objects To TargetPoint Panel

		// Started Adding Objects To Starting Panel
		c.gridx = 0;
		c.gridy = 0;
		panelStartingPointSliders.add(new JLabel("R"),c);

		c.gridx = 1;
		c.gridy = 0;
		startingPointSliders[0] = new JSlider(SwingConstants.HORIZONTAL);
		panelStartingPointSliders.add(startingPointSliders[0], c);	

		c.gridx = 2;
		c.gridy = 0;
		startingPointLabels[0] = new JLabel();
		startingPointLabels[0].setText("100");
		panelStartingPointSliders.add(startingPointLabels[0],c);

		c.gridx = 0;
		c.gridy = 1;
		panelStartingPointSliders.add(new JLabel("A"),c);



		//		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 1;
		c.gridy = 1;
		startingPointSliders[1] = new JSlider(SwingConstants.HORIZONTAL);
		panelStartingPointSliders.add(startingPointSliders[1], c);	

		startingPointLabels[1] = new JLabel();
		startingPointLabels[1].setText("100");
		c.gridx = 2;
		c.gridy = 1;

		panelStartingPointSliders.add(startingPointLabels[1],c);

		c.gridx = 0;
		c.gridy = 2;
		panelStartingPointSliders.add(new JLabel("S"),c);





		//c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 1;
		c.gridy = 2;
		startingPointSliders[2] = new JSlider(SwingConstants.HORIZONTAL);
		panelStartingPointSliders.add(startingPointSliders[2], c);	

		startingPointLabels[2] = new JLabel();
		startingPointLabels[2].setText("100");
		c.gridx = 2;
		c.gridy = 2;
		panelStartingPointSliders.add(startingPointLabels[2],c);
		// Done Adding Objects To Starting Point Panel

		c.gridx =1;
		c.gridy =3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panelStartingPointSliders.add(displayEntry,c);
		displayEntry.addActionListener(this);


		sendToController.addActionListener(this);
		transformMatrixPanel = new MatrixDisplay("Transform");
		transformMatrixPanel.setName("Transform Matrix");

		JPanel transfromPanel = new JPanel(new GridBagLayout());

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridheight = 3;
		c.gridwidth =3;
		c.fill = GridBagConstraints.BOTH;
		transfromPanel.add(transformMatrixPanel, c);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth =2;

		transfromPanel.add(sendToController,c);


		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridheight =1;
		c.gridwidth =1;
		leftPanel.add(panelTargetPointSliders,c);
		panelTargetPointSliders.setBorder(BorderFactory.createTitledBorder("Target Point Sliders"));//(Color.black));

		c.gridy =1;
		leftPanel.add(panelStartingPointSliders,c);
		panelStartingPointSliders.setBorder(BorderFactory.createTitledBorder("Starting Point Sliders"));//(Color.black));



		c.gridy = 2;
		leftPanel.add(openIGTPanel,c);
		openIGTPanel.setBorder(BorderFactory.createTitledBorder("Open IGT Panel"));//(Color.black));


		c.gridy = 3;
		leftPanel.add(transfromPanel,c);
		transformMatrixPanel.setBorder(BorderFactory.createTitledBorder("Transform Matrix"));//(Color.black));

		port = new JTextField("18944",10);
		host = new JTextField("127.0.0.1",10);

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


		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.gridy = 4;
		c.gridx = 2;
		c.anchor = GridBagConstraints.NORTH;


		Box openIGTBox = Box.createHorizontalBox();
		openIGTBox.add(connect);
		connect.addActionListener(this);
		openIGTPanel.add(openIGTBox,c);
	}

	public void SetControllerButton(JButton button) {
		this.sendToController = button;

	}

	public Transform getTransform() {

		return new Transform(transformMatrixPanel.getTableDataMatrix());
	}

	@Override
	public void onRxTransform(String name, TransformNR t) {

		System.out.println("On RxTransform Name :: " + name + " TransformNR ::" + t);
	}

	@Override
	public TransformNR getTxTransform(String name) {

		return null;
	}

	@Override
	public void onRxString(String name, String body) {

		System.out.println("Name  Received ::" + name);
	}

	@Override
	public String onTxString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRxDataArray(String name, Matrix data) {
		// TODO Auto-generated method stub

	}

	@Override
	public double[] onTxDataArray(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRxImage(String name, ImageMessage image) {
		// TODO Auto-generated method stub

	}

}
