package edu.wpi.robotics.aim.core.imagingtool;

import static com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.awt.Point;
import java.util.Vector;

import javax.swing.plaf.multi.MultiOptionPaneUI;


import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

public class NeedleDetection
{



	static void displayImage(String NameOfWindow,IplImage ImageToDisplay )//displayImageFunction
	{
		CvRect rect = cvGetImageROI(ImageToDisplay);
		cvResetImageROI(ImageToDisplay);
		// cvResizeWindow(NameOfWindow,600,600);
		cvNamedWindow( NameOfWindow, CV_WINDOW_NORMAL );

		//    cvSetWindowProperty(NameOfWindow,CV_WND_PROP_FULLSCREEN,CV_WINDOW_FULLSCREEN);
		// ;cvResizeWindow(NameOfWindow,600,600);
		cvShowImage(NameOfWindow, ImageToDisplay);

		cvWaitKey(0);
		//cvWaitKey(200);
		cvSetImageROI(ImageToDisplay,rect);
		cvDestroyWindow(NameOfWindow);
	}
	static void HoughTransform(IplImage img)
	{
		IplImage	canny = cvCreateImage(cvGetSize(img), img.depth(), 1);

		CvMemStorage storage = cvCreateMemStorage(0);
		int method = CV_HOUGH_STANDARD;
		int distanceResolutionInPixels = 1;
		double angleResolutionInRadians = 3.14/180;
		int minimumVotes = 10;
		int unused = 0;
		CvSeq lines = cvHoughLines2( canny, storage,
				method,
				distanceResolutionInPixels,
				angleResolutionInRadians,
				minimumVotes,
				unused, unused);

		// Draw lines on the canny contour image
		IplImage colorDst = cvCreateImage(cvGetSize(img), img.depth(), 3);
		cvCvtColor(canny, colorDst, CV_GRAY2BGR);
		for (int i=0;i<lines.total();i++)
		{
			CvPoint2D32f point = new CvPoint2D32f(cvGetSeqElem(lines, i));
			double rho = point.x();
			double theta = point.y();
			double a = Math.cos(theta);
			double b = Math.sin(theta);
			double x0 = a * rho;
			double y0 = b * rho;
			CvPoint pt1 = new CvPoint((int)Math.round(x0 + 1000 * (-b)), (int)Math.round(y0 + 1000 * (a)));
			CvPoint pt2 = new CvPoint((int)Math.round(x0 - 1000 * (-b)), (int)Math.round(y0 - 1000 * (a)));

			cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 1, CV_AA, 0);
		}
		displayImage("Hough ", colorDst);
	}
	static IplImage Skletonization(IplImage img)
	{
		//	clock_t startTime = clock();

		//clock_t endTime = clock();
		displayImage("img", img);

		IplImage skel = cvCreateImage(cvGetSize(img), img.depth(), 1);

		IplImage temp = cvCreateImage(cvGetSize(img), img.depth(), 1);
		IplImage canny = cvCreateImage(cvGetSize(img), img.depth(), 1);


		IplConvKernel element = cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_CROSS,null);
		//cvCanny(src, canny, threshold1, threshold2, apertureSize)
		cvDistTransform(img, skel, CV_DIST_L2,3,null,null,1);

		displayImage("Skeleton", skel);

		//		displayImage("In skel", img);
		//		CvMat skel = CvMat.create(img.rows(),img.cols(), img.depth());
		//		CvMat temp = CvMat.create(img.rows(),img.cols(), img.depth());
		//		IplConvKernel element = cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_CROSS,null);

		boolean done =false ; 
		do
		{
			cvMorphologyEx(img, temp,null,element,MORPH_CLOSE,1);

			displayImage("Skeleton", skel);

			cvNot(temp,temp);
			cvAnd(img,temp,temp,null);
			cvOr(skel, temp,skel, null);
			//			cvbitwise_not(temp, temp);
			//			cvbitwise_and(img, temp, temp);
			//			cvbitwise_or(skel, temp, skel);
			cvErode(img, img, element,1);

			double[] max = new double[2];
			double[] min = new double[2];
			CvPoint minLoc = new CvPoint();
			CvPoint maxLoc = new CvPoint();

			cvMinMaxLoc(img, min, max,minLoc,maxLoc,null);
			//		cvminMaxLoc(img, 0, max);
			done = (max[0] == 0);
		} while (!done);


		IplImage skeleton = cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,1);



		/*
		Vector<Vector<Point> > contours;
		Vector<Vec4i> hierarchy;

		findContours(skel, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );

		vector<Point> maxSizeContour;
		int size =0;
		for(int i=0;i<contours.size();i++)
		{
			if(contours.at(i).size() > size)
			{
				maxSizeContour = contours.at(i);
				size = maxSizeContour.size();
			}
		}

		CvMat dst = CvMat.create(img.rows(), img.cols(), CV_8UC1);

		contours.clear();
		CvScalar color= new CvScalar( 255);

		contours.push_back(maxSizeContour);
		drawContours(dst,contours,0,color,CV_FILLED,8);
		//    if( !contours.empty() && !hierarchy.empty() )
		//    {
		//        // iterate through all the top-level contours,
		//        // draw each connected component with its own random color
		//        int idx = 0;
		//        for( ; idx >= 0; idx = hierarchy[idx][0] )
		//        {

		//            Scalar color( (rand()&255), (rand()&255), (rand()&255) );
		//            drawContours( dst, contours, idx, color, CV_FILLED, 8, hierarchy );
		//        }
		//    }

		displayImage( "Connected Components", dst.asIplImage() );



		cvSetImageData(skeleton,dst.data_ptr(),dst.step());
cvSetI
	//	cv::waitKey(0);

		// std::cout<<"Skeleton running time: "<<(double)(endTime-startTime)/CLOCKS_PER_SEC<<std::endl;


		 */
		return skeleton;
	}
	public static void Thinning(IplImage img)
	{

	}
	public static void main(String[] args)
	{

		// Check that the input file exists


		IplImage inputImage = cvLoadImage("C:/AimLab/14.bmp");//this is the image you want to process



		displayImage("InputImage",inputImage);

		IplImage input_gray = cvCreateImage(cvSize(inputImage.width(),inputImage.height()),IPL_DEPTH_8U,1);
		cvCvtColor(inputImage,input_gray,CV_BGR2GRAY);

		//  Setting the ROI
		CvRect rect = cvRect(110, 110,60, 40);


		cvSetImageROI(input_gray, rect);
		cvSetImageROI(inputImage,rect);

		IplImage NegatedInput = cvCloneImage(inputImage);

		cvNot(inputImage,NegatedInput);

		IplImage imgGaussian =  cvCloneImage(input_gray);
		displayImage("InputImage",input_gray);


		CvMat src = new CvMat(input_gray);
		//	CvMat src = cv::cvarrToMat(input_gray);

		// cout << "Type :: " << src.depth() << "\n";
		//cvSmooth(src, dst, smoothtype, size1)

		cvSmooth(input_gray,imgGaussian,CV_GAUSSIAN,3);
		displayImage("Gaussian Smoothed",imgGaussian);

		cvNot(imgGaussian,imgGaussian);
		displayImage("After Negated",imgGaussian);


		//IplImage* imgGaussian_gray = cvCreateImage(cvSize(imgGaussian->width, imgGaussian->height), IPL_DEPTH_8U, 1);
		//  cvCvtColor(imgGaussian, imgGaussian_gray, CV_BGR2GRAY);

		displayImage("Image Gaussian Grey",imgGaussian);
		IplImage imgDilated =    cvCloneImage(imgGaussian);
		IplImage imgEroded = cvCloneImage(imgGaussian);

		cvDilate(imgGaussian,imgDilated,null,2);

		displayImage("After Dilation",imgDilated);


		cvErode(imgDilated,imgEroded,null,1);

		displayImage("After Erosion",imgEroded);
		IplImage	canny = cvCreateImage(cvGetSize(imgEroded), imgEroded.depth(), 1);
		//IplImage	convexHull = cvCreateImage(cvGetSize(imgEroded), canny.depth(), 1);

		//cvCanny(imgEroded, canny, 120, 255, 3);

		displayImage("Canny Image", canny);

		//cvConvexHull2(canny, convexHull,CV_CLOCKWISE , 10);
		//double mean = cvAvg( imgEroded ).val[0];

		//qDebug() << "Mean ::" << mean;
		IplImage imgThreshold = cvCloneImage(imgEroded);
		cvThreshold(imgEroded,imgThreshold,120,255,CV_THRESH_BINARY);


		displayImage("After Threshold",imgThreshold);
		HoughTransform(imgEroded);

		CvMat input = new CvMat(imgThreshold);

		IplImage temp_Skeleton= cvCreateImage(cvGetSize(imgThreshold), imgThreshold.depth(), imgThreshold.nChannels());

		IplImage temp = Skletonization(imgThreshold);


		//temp_Skeleton = temp;
		cvCopy(temp,temp_Skeleton);
		//cvCopy(Skletonization(input),temp_Skeleton);

		IplImage skeleton = cvCloneImage(imgThreshold);

		cvCopy(temp_Skeleton,skeleton);
		displayImage("Skeleton Full",skeleton);

	}
}
/*
		double XCord[100];
		double YCord[100];

		int numberOfPoints =0;

		unsigned char* data = (unsigned char*)temp_Skeleton->imageData;

		IplImage* imgHough =cvCreateImage(cvSize(temp_Skeleton->width,temp_Skeleton->height),IPL_DEPTH_8U,3);

		IplImage* imgRansac =cvCreateImage(cvSize(temp_Skeleton->width,temp_Skeleton->height),IPL_DEPTH_8U,3);

		IplImage* imgLm =cvCreateImage(cvSize(temp_Skeleton->width,temp_Skeleton->height),IPL_DEPTH_8U,3);



		cvCopy(NegatedInput,imgHough);

		cvCopy(NegatedInput,imgRansac);
		cvCopy(NegatedInput,imgLm);

		//    cvCvtColor(temp_Skeleton,imgColor,CV_GRAY2BGR);
		CvScalar s;
		s.val[0]=1;//blue
		s.val[1]=255;//green
		s.val[2]=1;//red
		for(int y=0;y<temp_Skeleton->height;y++)
		{
			for(int x=0;x<temp_Skeleton->width;x++)
			{
				if(*data == 1 || *data == 255)
				{
					XCord[numberOfPoints] = x;
					YCord[numberOfPoints] = y;
					numberOfPoints++;
					int intensity= *data;
					qDebug() << x << " " << y;
					//cvCircle(imgColor, cvPoint(x,y),1, s);
				}
				data++;
			}
		}

		double p[2]={-100,100};
		lineFit(XCord,YCord,p,numberOfPoints);



		//   qDebug() << "Line Parameters::" << p[0] << " "<<p[1];

		// qDebug() << "Number of Points ::" << numberOfPoints;



		//qDebug() << "origin ::" << temp_Skeleton->origin;
		executeRansac(XCord,YCord,numberOfPoints,imgRansac);
		for(int x=0;x<numberOfPoints;x++)
		{

			if(lineSatisfies(p,XCord[x],YCord[x]))
			{
				//     qDebug() << "Satifies :: "<< XCord[x] << " " << YCord[x];
				cvCircle(imgLm, cvPoint(XCord[x],YCord[x]),1, s);
			}
		}

		cvCopy(imgRansac,NegatedInput);
		displayImage("Ransac Applied Image",NegatedInput);

		cvCopy(imgLm,NegatedInput);
		displayImage("LM Applied Image",NegatedInput);


		vector<Vec4i> lines;
		cv::Mat mat=    cv::cvarrToMat(imgHough);

		cv::HoughLinesP(cv::cvarrToMat(temp_Skeleton), lines, 0.5, CV_PI/180, 5 );
		for( size_t i = 0; i < lines.size(); i++ )
		{
			Vec4i l = lines[i];
			cv::line( mat, Point(l[0], l[1]), Point(l[2], l[3]), Scalar(0,0,255), 3, CV_AA);
		}

		//cv::imshow("Hough Colour Image",mat);
		cvCopy(imgHough,NegatedInput);
		displayImage(" Hough  Image",NegatedInput);


		Scalar colorTab[] =
			{
				Scalar(0, 0, 255),
				Scalar(0,255,0),
				Scalar(255,100,100),
				Scalar(255,0,255),
				Scalar(0,255,255)
			};
 */

