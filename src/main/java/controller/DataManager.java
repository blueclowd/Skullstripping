package controller;

/**
 * Model-based Level Set (MLS) Algorithm
 * 
 * COPYRIGHT NOTICE Copyright (c) 2003-2005 Audrey H. Zhuang and Daniel J. Valentino
 * 
 * Please read LICENSE.TXT for the license covering this software
 * 
 * For more information, please contact the authors at: haihongz@seas.ucla.edu
 * dvalentino@mednet.ucla.edu
 */

import ij.IJ;
import ij.ImagePlus;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import model.InitialParams;
import model.Metrics;
import model.SegmentResult;
import model.UiOptions;
import model.Volume;
import utils.LSConstants;
import utils.LSConstants.LevelsetType;
import utils.LSUtil;
import view.CustomWindow;
import view.GenerateReportBtnActionListener;

/**
 * Class to read images from the file names provided on the command line as arguments and execute
 * skull-stripping task on them.
 * 
 * @author Audrey H. Zhuang and Daniel J. Valentino
 * @version 2 September 2005
 */
public class DataManager
{

	private List<SkullStripper> skullStrippers;

	/** Maximum iterations. */
	private int MAX_ITER = 255 * 16;

	/** The function phi. */
	private double[][] _phi = null;

	/** The slice index with which the skull stripping starts with. */
	private int initialIndex = 0;

	/**
	 * The intensity minimum below which lies 2% of the cumulative histogram.
	 */
	private double intens2 = 0;

	/**
	 * The intensity maxmum below which lies 10% of the cumulative histogram.
	 */
	private double intens10 = 0;

	/**
	 * The intensity maxmum below which lies 40% of the cumulative histogram.
	 */
	private double intens40 = 0;

	/**
	 * The intensity maxmum below which lies 98% of the cumulative histogram.
	 */
	private double intens98 = 0;

	/** Interslice distance (/pixel size). */
	private float INTER_SLICE_DIST = 3;

	/**
	 * The orientation of 2D slices resampled from the data volume and stored in the skullStrippers.
	 */
	private int resamplingOrient = 0;

	/** Orientation of the 2D image in the loaded file. */
	private int originalOrient = 0;

	/** Bits per pixel. */
	private int _bitsPP = 16;

	/** The way where the head locates: 0-north, 1-east, 2-south, 3-west. */
	private int heading = 0;

	/** Whether to fill holes generated by skull stripping algorithm. */
	private boolean isFillingHolesEnabled = false;

	/** Cut off boundary value of phi. */
	private double BOUNDARY_VALUE = -0.5;

	private float xyDim = 0;

	private double age = 0;

	private double textureCoefficient = 0;
	private double velocity = 0;
	private double threshold = 0;

	private LevelsetType levelsetType;

	private Color contourColor;

	private ImagePlus imgPlus;
	private ImagePlus gtImgPlus;

	private String resultDir;

	private CustomWindow customWindow;


	public DataManager()
	{

	}

	public DataManager(String filePath, ImagePlus gtImgPlus, String resultDir)
	{
		// Read in the images(with vertical flip)
		this.imgPlus = LSUtil.readImage(filePath, true);
		this.gtImgPlus = gtImgPlus;

		this.resultDir = resultDir;

		// Create a directory for result files
		final File file = new File(resultDir);

		if (!file.exists())
		{
			if (!file.mkdir())
			{
				IJ.error(LSConstants.ERROR_CREATE_RESULT_DIR);
			}
		}

	}

	public void setCustomWindow(CustomWindow customWindow)
	{
		this.customWindow = customWindow;
	}

	public void setUiOptions(UiOptions uiOpts)
	{
		this.textureCoefficient = uiOpts.getTextureCoefficient();
		this.velocity = uiOpts.getVelocity();
		this.threshold = uiOpts.getThreshold();
		this.isFillingHolesEnabled = uiOpts.isHoleFilling();
		this.heading = uiOpts.getHeading() > 3 ? 0 : uiOpts.getHeading();
		this.contourColor = uiOpts.getContourColor();
		this.levelsetType = uiOpts.getLevelsetType();
	}

	/**
	 * Gets the slice index of the image with which the skull stripping starts with.
	 * 
	 * @return An integer number.
	 */
	public int getInitialIndex()
	{
		return initialIndex;
	}

	public void fillHoles()
	{
		IJ.log("Fill Holes start.");

		for (SkullStripper skullStripper : skullStrippers)
		{
			skullStripper.setOutputArray(new Filler(skullStripper.getOutputArray())
					.getFilledArray());
		}

		IJ.log("Fill holes complete.");
	}

	/**
	 * Fill the holes in a brain mask stored in a given skullStripper object.
	 */
	public void fillHoles(SkullStripper skullStripper)
	{
		byte[][] outputArray1 = skullStripper.getOutputArray();
		Filler filler = new Filler(outputArray1);

		byte[][] outputArray2 = filler.getFilledArray();
		skullStripper.setOutputArray(outputArray2);
	}

	/**
	 * Initialize a zero level set circle in a 2D slice resampled from the input file.
	 */
	public void initZeroLS()
	{

		IJ.log(" ===== Initialize Zero Levelset ===== ");

		ZeroLSInitializer initializer =
				new ZeroLSInitializer(skullStrippers, intens2, intens98, resamplingOrient, xyDim);

		InitialParams initialParams = initializer.getInitParams();
		initialIndex = initialParams.getInitialIdx();

		// Get an SkullStripper object at the given index
		SkullStripper initSkullStripper = skullStrippers.get(initialIndex);

		// Set initialization parameters and initialize
		initSkullStripper.setInitialParameters(initialParams.getCenterX(),
				initialParams.getCenterY(), initialParams.getRadius());

		_phi = initSkullStripper.initializePhi();

	}

	/**
	 * Load metadata from image
	 * 
	 * @param filePath
	 */
	public void loadMetadata(String filePath)
	{

		IJ.log(" ===== Load meta data ===== ");

		try
		{
			final IFormatReader imgReader = new ImageReader();

			imgReader.setMetadataStore(MetadataTools.createOMEXMLMetadata());

			imgReader.setId(filePath);

			IJ.log("Width = " + imgReader.getSizeX());
			IJ.log("Height = " + imgReader.getSizeY());

			IJ.log("Format = " + imgReader.getFormat());

			resamplingOrient = getResamplingOrient(imgReader);
			IJ.log("resamplingOrient = " + resamplingOrient);

			_bitsPP = imgReader.getBitsPerPixel();
			IJ.log("Bits per pixel = " + imgReader.getBitsPerPixel());

			IJ.log("Global Meatdata = " + imgReader.getGlobalMetadata().keySet());

			xyDim =
					Float.valueOf(String.valueOf(imgReader.getGlobalMetadata().get("/xspace step")));

			// TODO
			age = 40.0;

			// TODO
			originalOrient = 0;
			IJ.log("originalOrient = " + originalOrient);


			INTER_SLICE_DIST =
					Float.valueOf(String.valueOf(imgReader.getGlobalMetadata().get("/zspace step")));;

			IJ.log("INTER_SLICE_DIST = " + INTER_SLICE_DIST);

		} catch (FormatException e)
		{
			IJ.log(e.getMessage());
		} catch (IOException e)
		{
			IJ.log(e.getMessage());
		}

	}

	/**
	 * Create skullstrippers for each slice in the volume
	 * 
	 * @param filePath
	 */
	public void createSkullStripper(String filePath)
	{

		IJ.log("===== Create Skullstrippers =====");

		skullStrippers = new ArrayList<SkullStripper>();

		// Input volume will be converted to a short 2D array for evolvement
		final List<BufferedImage> buffImgList =
				LSUtil.checkAndRescaleRange(LSUtil.vflipImages(LSUtil.readImage(filePath)), 0,
						32767);

		BufferedImage[] imgs = null;

		if (resamplingOrient == originalOrient)
		{

			imgs = new BufferedImage[buffImgList.size()];

			for (int i = 0; i < imgs.length; i++)
			{
				imgs[i] = buffImgList.get(i);
			}

		} else
		{

			int IMAGE_TYPE = 0;
			if (_bitsPP <= 8)
			{
				IMAGE_TYPE = BufferedImage.TYPE_BYTE_GRAY;
			} else
			{
				IMAGE_TYPE = BufferedImage.TYPE_USHORT_GRAY;
			}

			final IFormatReader iFormatReader = new ImageReader();

			iFormatReader.setMetadataStore(MetadataTools.createOMEXMLMetadata());

			try
			{
				iFormatReader.setId(filePath);
			} catch (FormatException | IOException e)
			{
				IJ.log(e.getMessage());
			}

			final Volume volume = new Volume(iFormatReader);

			int size = volume.getDepth(resamplingOrient);

			imgs = new BufferedImage[size];
			for (int i = 0; i < imgs.length; i++)
			{
				imgs[i] = volume.createGrayImage(buffImgList.get(i), IMAGE_TYPE);
			}

		}

		for (BufferedImage img : imgs)
		{

			final SkullStripper skullStripper =
					new SkullStripper(img, textureCoefficient, velocity, threshold, imgPlus,
							contourColor, levelsetType);

			skullStripper.setBitsPP(_bitsPP);

			skullStripper.calculateProbingDistance(xyDim, age);

			skullStrippers.add(skullStripper);

		}

		// Calculate histogram
		calculateBound(skullStrippers);

		// Set intens2, intens10, intens98 to skullStripper
		for (SkullStripper ss : skullStrippers)
		{
			ss.setThresholdIntensities(intens2, intens10, intens40, intens98);
		}

		// Set once since customWindow is static
		skullStrippers.get(0).setCustomWindow(customWindow);

	}

	/**
	 * Evolvement of the volume
	 * 
	 * @return
	 */
	public SegmentResult evolveVolume()
	{

		final SegmentResult segmentResult = new SegmentResult();

		int SHRINK_DIST = (int) Math.floor(Math.abs(INTER_SLICE_DIST) + 0.5);

		// The slices, that are around mid-sagittal slices, are estimated to be
		// within SLICE_1 and SLICE_2
		double dataSize = skullStrippers.size();
		int SLICE_1 = (int) (dataSize * 0.45);
		int SLICE_2 = (int) (dataSize * 0.55);

		// Define the length of a small contour
		int SMALL_CIRCLE = 100;

		// Keep the _phi for slides before "index"
		double[][] initPhi = LSUtil.copy2DAry(_phi);

		customWindow.appendLog(" ===== Start evolvement ===== ");

		final Date tic = new Date();

		// Skull-strip slices after center slice
		final List<double[][]> phiListAfter =
				evolve(_phi, SHRINK_DIST, SLICE_1, SLICE_2, SMALL_CIRCLE, 1);
		IJ.log("Convolution after center finished.");

		// Skull-strip slides before center slice
		final List<double[][]> phiListBefore =
				evolve(initPhi, SHRINK_DIST, SLICE_1, SLICE_2, SMALL_CIRCLE, -1);
		IJ.log("Convolution before center finished.");

		final Date tac = new Date();

		segmentResult.setEvolveTime(tac.getTime() - tic.getTime());

		// Remove the duplicated center slice
		phiListAfter.remove(0);

		if (phiListBefore.addAll(phiListAfter))
		{
			segmentResult.setPhiList(phiListBefore);
		}

		return segmentResult;
	}

	/**
	 * Evolution on half volume
	 * 
	 * @param _phi
	 * @param SHRINK_DIST
	 * @param SLICE_1
	 * @param SLICE_2
	 * @param SMALL_CIRCLE
	 * @param direction
	 */
	private List<double[][]> evolve(double[][] _phi, int SHRINK_DIST, int SLICE_1, int SLICE_2,
			int SMALL_CIRCLE, int direction)
	{

		final List<double[][]> phiList = new ArrayList<double[][]>();

		int contourLength = 0;
		double[][] phiTemp = null;
		boolean isPhiAcceptable = true;
		double velocity = 0;
		int sliceIdx = initialIndex;

		SkullStripper skullStripper;

		while (sliceIdx >= 0 && sliceIdx < skullStrippers.size())
		{

			IJ.log("======= Evolve: " + sliceIdx + " =======");

			skullStripper = skullStrippers.get(sliceIdx);

			customWindow.setSliceNo(sliceIdx);

			// The last mask area should be reset at the beginning of the
			// forward evolvement
			if (sliceIdx == initialIndex)
			{
				skullStripper.clearLastMaskArea();
			}

			phiTemp = LSUtil.copy2DAry(_phi);

			// Shrink the phi zero contour before applying it as an initial
			// contour Using reiniailization method
			_phi = skullStripper.skullStrip(MAX_ITER, _phi, sliceIdx);

			// Do not check acceptance if the images are around mid-sagittal
			// slices where the CSF change is big and segmentation between
			// slices could be very different
			if ((resamplingOrient == 2 && sliceIdx > SLICE_1 && sliceIdx < SLICE_2)
					|| sliceIdx == initialIndex)
			{

				isPhiAcceptable = true;

			} else
			{
				// Check if the skull-stripped result is acceptable
				isPhiAcceptable = _isPhiAcceptable(_phi, phiTemp);
			}

			IJ.log("1:" + isPhiAcceptable);

			if (!isPhiAcceptable)
			{
				// Increase velocity by 5 times
				velocity = skullStripper.getVelocity();

				skullStripper.setVelocity(velocity * 5);
				skullStripper.setStrippingEnabled(true);
				_phi = skullStripper.shrinkPhiZero(phiTemp, SHRINK_DIST + 10);
				_phi = skullStripper.skullStrip(MAX_ITER, _phi, sliceIdx);

				// Check if the skull-stripped result is acceptable
				isPhiAcceptable = _isPhiAcceptable(_phi, phiTemp);

				IJ.log("2:" + isPhiAcceptable);
				if (!isPhiAcceptable)
				{

					// Increase velocity by 10 times
					skullStripper.setVelocity(velocity * 10);
					skullStripper.setStrippingEnabled(true);
					_phi = skullStripper.shrinkPhiZero(phiTemp, SHRINK_DIST + 5);
					_phi = skullStripper.skullStrip(MAX_ITER, _phi, sliceIdx);

					// Check if the skull-stripped result is acceptable
					isPhiAcceptable = _isPhiAcceptable(_phi, phiTemp);

					IJ.log("3:" + isPhiAcceptable);
				}

				if (!isPhiAcceptable)
				{
					contourLength = LSUtil.calculateContourLength(phiTemp, -0.5d, (byte) 40);

					if (contourLength > SMALL_CIRCLE)
					{

						// Use mask in last slide for current slide

						// MaxIter = 0, no operation => _phi = phiTemp
						_phi = skullStripper.skullStrip(0, phiTemp, sliceIdx);
					} else
					{

						// Shrink the circle to zero
						while (LSUtil.calculateMaskArea(_phi, -0.5d) > 0)
						{
							int shrinkDistTemp = 4;
							_phi = skullStripper.shrinkPhiZero(_phi, shrinkDistTemp);
						}
						_phi = skullStripper.skullStrip(0, _phi, sliceIdx);
					}
				}
			}

			if (isFillingHolesEnabled)
			{
				fillHoles(skullStripper);
			}

			LSUtil.exportBufferedImage(LSUtil.layContourOnImage(
					LSUtil.convertGrayToRGB(imgPlus.getBufferedImage()),
					LSUtil.convertToBinaryAry(_phi, -0.5d, (byte) 40), contourColor), resultDir
					+ "/" + (sliceIdx + 1));

			phiList.add(_phi);

			// Update slice index
			sliceIdx += direction;
			_phi = skullStripper.shrinkPhiZero(_phi, SHRINK_DIST);

		}

		return phiList;

	}

	/**
	 * New getResamplingOrient for IFormatReader. Get resampling orient that is determined by the
	 * orientation giving the shortest depth. For example, if resampling in x direction gives the
	 * shortest depth, then the resampling orientation is assigned as axial.
	 * 
	 * @param imageReader
	 * @return
	 */
	private int getResamplingOrient(IFormatReader imageReader)
	{
		int resamplingOrient = 0;

		int xSize = imageReader.getSizeX();
		int ySize = imageReader.getSizeY();
		int zSize = imageReader.getSizeZ();

		if (zSize < ySize && zSize < xSize)
		{
			resamplingOrient = 0;
		} else if (ySize < xSize)
		{
			resamplingOrient = 1;
		} else
		{
			resamplingOrient = 2;
		}

		return resamplingOrient;
	}

	/**
	 * Calculate the histogram of an image and initialize the class members of _intens2, _intens10,
	 * _intens40 and _intens98.
	 */
	private void calculateBound(List<SkullStripper> skullStrippers)
	{
		// Find the maximum intensity
		int maxI = 0;
		int minI = 0;
		short[][] array = null;
		BufferedImage image = null;

		for (SkullStripper skullStripper : skullStrippers)
		{

			image = skullStripper.getInputImage();

			array = skullStripper.create2DArray(image);

			for (int x = 0; x < array.length; x++)
			{
				for (int y = 0; y < array[0].length; y++)
				{
					if (maxI < array[x][y])
						maxI = array[x][y];

					if (minI > array[x][y])
						minI = array[x][y];

				}
			}
		}

		IJ.log("Max intensity = " + maxI);
		IJ.log("Min intensity = " + minI);

		// Initialize the histogram array
		int[] hist = new int[maxI + 1];

		// Construct histogram array
		int intens;
		for (SkullStripper skullStripper : skullStrippers)
		{
			image = skullStripper.getInputImage();
			array = skullStripper.create2DArray(image);
			for (int x = 0; x < array.length; x++)
			{
				for (int y = 0; y < array[0].length; y++)
				{
					intens = array[x][y];
					hist[intens]++;
				}
			}
		}

		// Calculate _intens2
		double sum = 0;
		SkullStripper skullStripper = skullStrippers.get(0);
		image = skullStripper.getInputImage();
		int width = image.getWidth();
		int height = image.getHeight();
		double totalSum = width * height * skullStrippers.size();
		for (int k = 0; k < hist.length; k++)
		{
			sum += hist[k];
			if (sum / totalSum > 0.02)
			{
				intens2 = k;
				k = hist.length;
			}
		}

		IJ.log("Intensity(2%) = " + intens2);

		// Calculate _intens98
		sum = 0;
		for (int k = 0; k < hist.length; k++)
		{
			sum += hist[k];
			if (sum / totalSum > 0.98)
			{
				intens98 = k;
				k = hist.length;
			}
		}

		IJ.log("Intensity(98%) = " + intens98);

		// Calculate _intense10
		intens10 = Math.floor(((intens98 - intens2) * 0.1) + intens2 + 0.5);

		// Calculate _intens40
		intens40 = Math.floor(((intens98 - intens2) * 0.4) + intens2 + 0.5);

		IJ.log("10%/40% = " + intens10 + "/" + intens40);
	}

	/**
	 * If the segmentation results, represented by phi, is acceptable.
	 * 
	 * @param phi to check on.
	 * @param lastPhi Phi in last (or previous) slide.
	 * @return True is phi is acceptable; or false if it is unacceptable.
	 */
	private boolean _isPhiAcceptable(double[][] phi, double[][] lastPhi)
	{
		boolean isAcceptable = true;
		double LIMIT_JACCARD = 0.85;

		// =======
		final Metrics metrics =
				new Metrics(LSUtil.convertBoolAry(lastPhi, BOUNDARY_VALUE), LSUtil.convertBoolAry(
						phi, BOUNDARY_VALUE));

		double jaccard = metrics.getJaccard();

		IJ.log("Jaccard:" + jaccard);

		int maskArea = LSUtil.calculateMaskArea(phi, -0.5d);
		if (maskArea > 10000 && jaccard < LIMIT_JACCARD)
		{
			isAcceptable = false;

		} else
		{

			int lastMaskArea = 0;
			int contourLength = 0;
			lastMaskArea = LSUtil.calculateMaskArea(lastPhi, -0.5d);
			double difference = maskArea - lastMaskArea;

			double MAX_AREA_DIF = 0;
			contourLength = LSUtil.calculateContourLength(lastPhi, -0.5d, (byte) 40);

			if (contourLength > 0)
			{
				MAX_AREA_DIF = contourLength * (Math.abs(INTER_SLICE_DIST) + 1) * 2;
			} else
			{
				MAX_AREA_DIF = difference;
			}
			// If the difference between lastMaskArea and maskArea is within
			// 15% of lastMaskArea, then phi is acceptable
			if (difference > MAX_AREA_DIF)
			{
				isAcceptable = false;
			}

		}

		return isAcceptable;
	}

	/**
	 * Evaluate the result by calculating metrics (Jaccard, Dice, Conformity,...)
	 * 
	 * @return
	 */
	public SegmentResult evaluateMetrics(SegmentResult segmentResult)
	{
		IJ.log(" ===== Evaluate Metrics ===== ");

		if (segmentResult == null)
		{
			IJ.log(LSConstants.ERROR_RESULT_EMPTY);
			return new SegmentResult();
		}

		final List<double[][]> gtDoubleImgs = LSUtil.convertImgPlusToDouble(gtImgPlus);

		final Metrics metrics =
				new Metrics(LSUtil.convertBoolAry(segmentResult.getPhiList(), BOUNDARY_VALUE),
						LSUtil.convertBoolAry(gtDoubleImgs, 1));

		segmentResult.setMetrics(metrics);

		return segmentResult;
	}

	public void enableGenerateReportBtn(SegmentResult segmentResultWithMetrics, String fileName,
			List<String> gtFileNameList)
	{

		customWindow.addGenerateReportBtnActionListener(new GenerateReportBtnActionListener(
				customWindow, fileName, gtFileNameList, segmentResultWithMetrics));

		customWindow.enableGenerateReportBtn(true);

	}

}