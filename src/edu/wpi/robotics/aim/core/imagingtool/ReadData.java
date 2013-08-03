package edu.wpi.robotics.aim.core.imagingtool;

import gdcm.FilenamesType;
import gdcm.IPPSorter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import vtk.vtkGDCMImageReader;
import vtk.vtkImageData;
import vtk.vtkMedicalImageProperties;
import vtk.vtkStringArray;


public class ReadData {


	static
	{
		System.loadLibrary("vtkCommonJava");
		System.loadLibrary("vtkFilteringJava");
		System.loadLibrary("vtkIOJava");
		System.loadLibrary("vtkImagingJava");
		System.loadLibrary("vtkGraphicsJava");
		System.loadLibrary("vtkRenderingJava");
		System.loadLibrary("vtkgdcmJava");
		System.loadLibrary("gdcmjni");
	}
	private vtkGDCMImageReader reader = null;
	vtkMedicalImageProperties prop = null;
	private String volumeLable = null;
	private vtkImageData imageData = null;
	private Map<String, String> headerInfo = new HashMap<String,String>();

	/*
	 *  For now only implemented for Directories. It should be extended to handles any path.
	 */
	public ReadData()
	{

	}

	FilenamesType fns;

	public  void process(String path)
	{
		fns.add( path );
	}

	// Process only files under dir
	public  void visitAllFiles(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i=0; i<children.length; i++)
			{
				visitAllFiles(new File(dir, children[i]));
			}
		}
		else
		{
			process(dir.getPath());
		}
	}

	public boolean updateData(String path)
	{

		fns = new FilenamesType();
		File dir = new File(path);
		visitAllFiles(dir);

		//if(reader == null)
		reader = new vtkGDCMImageReader();


		IPPSorter ipp = new IPPSorter();
		ipp.SetComputeZSpacing( true );
		ipp.SetZSpacingTolerance( 1e-3 );
		boolean b = ipp.Sort( fns );
		if(!b)
		{
			//throw new Exception("Could not scan");
		}
		double ippzspacing = ipp.GetZSpacing();


		FilenamesType sorted = ipp.GetFilenames();

		fns.clear();
		vtkStringArray files = new vtkStringArray();
		long nfiles = sorted.size();
		//for( String f : sorted )
		System.out.println("Number of Files in Path ::" + path + " are  " +nfiles );
		for (int i = 0; i < nfiles; i++)
		{
			String f = sorted.get(i);

			files.InsertNextValue( f );
		}

		reader.SetFileNames( files );
		reader.Update(); 
		imageData = reader.GetOutput();
		//	prop = new vtkMedicalImageProperties();
		prop = reader.GetMedicalImageProperties();

		//System.out.println(prop);

		//	System.out.println(imageData);

		double window =0,level =0;

		double[] w = prop.GetNthWindowLevelPreset(0);

		headerInfo.put("window", String.valueOf(w[0]));
		headerInfo.put("level", String.valueOf(w[1]));	
		//	System.out.println(w.toString());
		prop.GetWindowLevelPresetIndex(window, level);

		System.out.println(w[0] + " " + w[1]);


		volumeLable = "volume" + path;

		return true;

	}



	/*
	 *  Returns the image data
	 */
	public vtkImageData getImageData()
	{
		return imageData;
	}
	/*
	 * returns the unique volume label;
	 */
	public String getVolumeLabel()
	{
		return volumeLable;
	}

	public Map<String, String> getHeaderInfo()
	{
		return headerInfo;
	}
	public void destroyObject()
	{
		headerInfo.clear();
		imageData = null;
		reader.Delete();
	}
}