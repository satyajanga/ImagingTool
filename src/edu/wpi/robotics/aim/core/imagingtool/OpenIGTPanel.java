package edu.wpi.robotics.aim.core.imagingtool;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.ClientInfoStatus;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.network.GenericIGTLinkClient;
import org.medcare.igtl.network.IOpenIgtPacketListener;

import vtk.vtkImageData;
import vtk.vtkMatrix4x4;
import Jama.Matrix;

import com.googlecode.javacv.cpp.opencv_features2d.MSER;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

public class OpenIGTPanel extends JPanel implements IOpenIgtPacketListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String,vtkMatrix4x4> transforms2DObjects;
	private Map<String,vtkImageData> loaded2DObjects;
	private ArrayList<String> displayed2DObjects; /* Any number of 2D objected can be displayed*/
	private Semaphore mutex = new Semaphore(1);
	Box buttonBox2D;

	public DisplayWidget displayWidget;

	Vector<Vector<String> > tableData = new Vector< Vector<String> >();
	JTable MesgTable;
	GenericIGTLinkClient client;
	public OpenIGTPanel()
	{
		setLayout(new BorderLayout());
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Device");
		columnNames.add("Mesg Type");
		columnNames.add("TimeStamp");
		tableData.add(columnNames);
		MesgTable = new JTable(tableData, columnNames);
		transforms2DObjects = new HashMap<String, vtkMatrix4x4>();
		loaded2DObjects = new HashMap<String, vtkImageData>();
		displayed2DObjects = new ArrayList<String>();
		this.add(MesgTable,BorderLayout.CENTER);
	}
	public void connectToServer(String hostName,int port)
	{
		try
		{
			client = new GenericIGTLinkClient(hostName, port);
			client.addIOpenIgtOnPacket(this);
		}
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}		
	}

	public void SetDisplayWidget(DisplayWidget w)
	{
		displayWidget = w;
	}
	public void destroyObject()
	{
		displayWidget=null;
	}

	@Override
	public void onRxTransform(String name, TransformNR t) {

	}
	@Override
	public TransformNR getTxTransform(String name) {

		return null;
	}
	@Override
	public void onRxString(String name, String body) {
		// TODO Auto-generated method stub

	}
	@Override
	public String onTxString(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onRxDataArray(String name, Matrix data) {
		// TODO Auto-generated method stub

	}
	@Override
	public double[] onTxDataArray(String name) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRxImage(String name, ImageMessage image) 
	{

		try {
			mutex.acquire(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\nComing into On Receive Image\n");

		vtkImageData imageData = new vtkImageData();

		long dimensions[] = image.getDimensions();
		System.out.println("Width :" + dimensions[0] + " Length: " + dimensions[1]);

		byte[] bytes = image.getImageData();
		imageData.SetScalarType((int)image.getScalarType());
		imageData.SetNumberOfScalarComponents((int)image.getImageType());

		imageData.SetExtent(0,(int)dimensions[0]-1,0,(int)dimensions[1]-1,0,0);

		imageData.SetSpacing(1,1,1);
		imageData.AllocateScalars();
		short pixel;
		switch((int)image.getScalarType())
		{
		case 2:
		{

		}
		case 3:
		{
			/* do nothing*/
			System.out.println("Type of Image: " + image.getScalarType());

			for (int i = 0; i < dimensions[0]; ++i)
			{
				for (int j=0;j<dimensions[1];j++)
				{
					pixel = (short) (128 + bytes[(int) (i*dimensions[0] + j)]);
					imageData.SetScalarComponentFromDouble(i, j, 0, 0, (double)pixel);				
				}

			}

			System.out.println("Created VTK ImageData");

			break;
		}
		case 4:
		{

		}
		case 5:
		{
			/* do nothing*/
			System.out.println("Type of Image: " + image.getScalarType());

			for(int i=0;i<dimensions[0];i++)
			{
				for(int j=0;j<dimensions[1];j++)
				{
					pixel = bytes[(int)(i*dimensions[0] + 2*j)];pixel = (short) (pixel*256); pixel = (short) (pixel + bytes[(int)(i*dimensions[0] + 2*j+1)]);
					imageData.SetScalarComponentFromFloat(i, j, 0, 0, (double)pixel);								
				}
			}
			System.out.println("Created VTK ImageData");

			break;

		}
		default:
		{
			/* do nothing*/
			System.out.println("Type of Image: " + image.getScalarType());
			System.out.println("Failed to Create VTK ImageData");

		}

		}
		if(loaded2DObjects.containsKey(name))
		{

			displayWidget.removeImageFromInteractor(transforms2DObjects.get(name));

			loaded2DObjects.remove(name);
			transforms2DObjects.remove(name);
		}

		loaded2DObjects.put(name,imageData);

		vtkMatrix4x4 transform = new vtkMatrix4x4();
		double[][] matrix = image.getMatrix();
		double origin[] = image.getOrigin();
		System.out.println ("Origin ::" + origin[0] + " " + origin[1] + " "+origin[2] );
		origin[0] = origin[0] + dimensions[0]/2;
		origin[1] = origin[1] + dimensions[1]/2;
		origin[2] = origin[2] + dimensions[2]/2;

		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				transform.SetElement(i,j,matrix[i][j]);	
			}
		}
		displayWidget.addObjectToActor(transform, imageData);

		transforms2DObjects.put(name, transform);
		mutex.release(1);
		Vector<String> row = new Vector<String>();
		row.add(name);
		row.add("IMAGE");
		row.add("time");
		tableData.add(row);
		MesgTable.repaint();
		//Date d = new Date();

		//System.out.println(new Timestamp(d.getTime()));		
	}
	public void disconnetFromServer() 
	{
		if(client.isAlive())
			client.interrupt();

	}
}
