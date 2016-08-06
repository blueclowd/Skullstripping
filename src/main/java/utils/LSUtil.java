package utils;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.gui.BufferedImageReader;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

import org.apache.commons.lang.ArrayUtils;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;

public class LSUtil
{

	/**
	 * Get cummulative histogram by histogram
	 * 
	 * @param hist
	 * @return
	 */
	public static int[] getCumHistByHist(int[] hist)
	{

		int cumHist[] = new int[hist.length];

		cumHist[0] = hist[0];

		for (int i = 1; i < hist.length; i++)
		{

			cumHist[i] = cumHist[i - 1] + hist[i];
		}

		return cumHist;
	}

	public static Roi getImageBound(ImagePlus imp)
	{

		final ImageProcessor img = imp.getProcessor();
		final short[] vals = (short[]) img.getPixels();

		final int height = img.getHeight();
		final int width = img.getWidth();

		final int cenRow = img.getHeight() / 2;
		final int cenCol = img.getWidth() / 2;

		final int roiX, roiY, roiW, roiH;

		IJ.log("Background color = " + img.getBackgroundValue());
		int col = 0;
		while (vals[cenRow * width + col] <= -32760)
		{

			col++;
		}

		roiX = col;

		col = width - 1;
		while (vals[cenRow * width + col] <= -32760)
		{
			col--;
		}

		roiW = col - roiX;

		int row = 0;
		while (vals[cenCol + row] <= -32760)
		{
			row += width;
		}

		roiY = row;

		row = height - 1;
		while (vals[cenCol + row] <= -32760)
		{
			IJ.log(String.valueOf(vals[cenRow * width + col]));
			row -= width;
		}

		roiH = row - roiY;

		final Roi roi = new Roi(roiX, roiY, roiW, roiH);

		IJ.log("Image bound = " + roiX + "/" + roiY + "/" + roiW + "/" + roiH);

		return roi;

	}

	public static ImagePlus createImgPlus(double[][] img, String title)
	{

		if (ArrayUtils.isEmpty(img))
		{
			IJ.log("createImgPlus: " + img);

			return null;
		}

		ImageProcessor imgProcessor = new FloatProcessor(img.length, img[0].length);

		for (int i = 0; i < img.length; i++)
		{
			for (int j = 0; j < img[0].length; j++)
			{
				imgProcessor.putPixelValue(i, j, img[i][j]);
			}
		}

		return new ImagePlus(title, imgProcessor);
	}

	public static boolean exportAry(final double[][] img, final String fileName)
	{
		final ImagePlus imgPlus = createImgPlus(img, "");

		return new FileSaver(imgPlus).saveAsBmp(fileName + ".bmp");
	}

	public static boolean exportAry(final byte[][] array, final String fileName)
	{

		final int height = array.length;
		final int width = array[0].length;

		final byte[] oneDimArray = new byte[array.length * array[0].length];

		// Transform: 2-D to 1-D
		for (int x = 0; x < array.length; x++)
		{
			for (int y = 0; y < array[0].length; y++)
			{
				oneDimArray[x + y * array.length] = array[x][y];
			}
		}

		// Create ImagePlus instance
		final ImagePlus imgPlus =
				new ImagePlus("ByteImage", new ByteProcessor(width, height, oneDimArray));

		// Save ImagePlus instance by FileSaver
		return new FileSaver(imgPlus).saveAsBmp(fileName + ".bmp");

	}

	public static double[][] binarizeDouble(double[][] img, double target)
	{
		if (ArrayUtils.isEmpty(img))
		{
			IJ.log("binarizeDouble: " + img);

			return null;
		}

		double[][] result = new double[img.length][img[0].length];

		for (int i = 0; i < img.length; i++)
		{
			for (int j = 0; j < img[0].length; j++)
			{
				result[i][j] = (img[i][j] == target) ? 255 : 0;

				if (result[i][j] == 255)
				{
					IJ.log("Meet = " + i + " " + j);
				}

			}
		}

		return result;

	}

	public static Overlay createTempPoint(byte[][] mask, Color overlayColor)
	{
		final Overlay overlay = new Overlay();

		final Roi roi = new Line(10, 10, 10, 10);
		roi.setStrokeWidth(0);
		roi.setStrokeColor(overlayColor);

		overlay.add(roi);

		return overlay;
	}

	public static Overlay createContourByMask(byte[][] mask, Color overlayColor)
	{
		final Overlay overlay = new Overlay();

		final List<Integer> contourX = new ArrayList<Integer>();

		final List<Integer> contourY = new ArrayList<Integer>();

		for (int x = 10; x < mask.length - 10; x++)
		{
			for (int y = 10; y < mask[0].length - 10; y++)
			{
				if (4 * mask[x][y] != mask[x - 1][y] + mask[x + 1][y] + mask[x][y - 1]
						+ mask[x][y + 1])
				{
					contourX.add(x);
					contourY.add(y);
				}
			}
		}

		for (int i = 0; i < contourX.size(); i++)
		{
			final Roi roi =
					new Line(contourX.get(i), contourY.get(i), contourX.get(i), contourY.get(i));

			roi.setStrokeWidth(0);
			roi.setStrokeColor(overlayColor);

			overlay.add(roi);

		}

		return overlay;

	}

	public static List<Point> createContourByMask(byte[][] mask)
	{
		final List<Point> pointList = new ArrayList<Point>();

		for (int x = 10; x < mask.length - 10; x++)
		{
			for (int y = 10; y < mask[0].length - 10; y++)
			{
				if (4 * mask[x][y] != mask[x - 1][y] + mask[x + 1][y] + mask[x][y - 1]
						+ mask[x][y + 1])
				{
					pointList.add(new Point(x, y));
				}
			}
		}

		return pointList;
	}

	public static BufferedImage layContourOnImage(BufferedImage image, byte[][] mask,
			Color contourColor)
	{
		BufferedImage contouredImage =
				new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

		if (image == null || mask == null)
		{
			return contouredImage;
		}

		for (int x = 10; x < mask.length - 10; x++)
		{
			for (int y = 10; y < mask[0].length - 10; y++)
			{
				if (4 * mask[x][y] != mask[x - 1][y] + mask[x + 1][y] + mask[x][y - 1]
						+ mask[x][y + 1])
				{
					contouredImage.setRGB(x, y, contourColor.getRGB());
				} else
				{
					contouredImage.setRGB(x, y, image.getRGB(x, y));
				}
			}
		}

		return contouredImage;
	}

	public static BufferedImage convertGrayToRGB(BufferedImage grayImage)
	{
		final BufferedImage colorImage =
				new BufferedImage(grayImage.getWidth(), grayImage.getHeight(),
						BufferedImage.TYPE_INT_RGB);

		final Graphics g = colorImage.getGraphics();
		g.drawImage(grayImage, 0, 0, null);
		g.dispose();

		return colorImage;
	}

	public static Overlay getContourOverlay(byte[][] initial, Color initialColor, byte[][] current,
			Color currentColor)
	{
		final Overlay overlay = new Overlay();

		final Overlay initOverlay = createContourByMask(initial, initialColor);
		final Overlay currentOverlay = createContourByMask(current, currentColor);

		for (int i = 0; i < initOverlay.size(); i++)
		{
			overlay.add(initOverlay.get(i));
		}

		for (int i = 0; i < currentOverlay.size(); i++)
		{
			overlay.add(currentOverlay.get(i));
		}

		return overlay;
	}

	public static void exportMatFile(double[][] array, String fileName)
	{
		// 2. write arrays to file
		final List list = new ArrayList();
		list.add(new MLDouble("double_arr", array));

		try
		{
			new MatFileWriter(fileName + ".mat", list);
		} catch (IOException e)
		{
			IJ.log("Export mat file fails");
		}
	}

	public static boolean[][] convertBoolAry(double[][] result, double value)
	{
		final boolean[][] resultBool = new boolean[result.length][result[0].length];

		// Temp
		double sum = 0;
		for (int x = 0; x < result.length; x++)
		{
			for (int y = 0; y < result[0].length; y++)
			{
				resultBool[x][y] = result[x][y] >= value;

				if (result[x][y] >= value)
				{
					sum++;
				}
			}
		}

		// IJ.log("[Temp] A = " + sum);

		return resultBool;
	}

	public static List<boolean[][]> convertBoolAry(List<double[][]> resultList, double value)
	{
		IJ.log("Convert Boolean Array: " + value);

		final List<boolean[][]> boolList = new ArrayList<boolean[][]>();

		for (double[][] result : resultList)
		{
			boolList.add(convertBoolAry(result, value));
		}

		return boolList;
	}

	public static double[][] copy2DAry(double[][] ary)
	{
		final double[][] newAry = new double[ary.length][ary[0].length];

		for (int i = 0; i < ary.length; i++)
		{
			newAry[i] = Arrays.copyOf(ary[i], ary[0].length);
		}

		return newAry;
	}

	public static int add(int a, int b)
	{
		return a + b;
	}

	/**
	 * Calculate mask area of img based on the specified threshold
	 * 
	 * @param img
	 * @param threshold
	 * @return
	 */
	public static int calculateMaskArea(double[][] img, double threshold)
	{
		int maskArea = 0;

		for (int y = 0; y < img.length; y++)
		{
			for (int x = 0; x < img[0].length; x++)
			{
				if (img[y][x] >= threshold)
				{
					maskArea++;
				}
			}
		}

		return maskArea;
	}

	/**
	 * Converts a level set array to a binary mask. All positive elements in the level set array
	 * will be set as a unique positive value, and all negative elements will be set as 0.
	 * 
	 * @param d A double array.
	 * @return A byte, binary array.
	 */
	public static byte[][] convertToBinaryAry(double[][] img, double threshold, byte white)
	{
		final byte[][] outputArray = new byte[img.length][img[0].length];

		for (int y = 0; y < img.length; y++)
		{
			for (int x = 0; x < img[0].length; x++)
			{

				// Set all positive level sets with a unique positive number
				if (img[y][x] >= threshold)
				{
					outputArray[y][x] = white;
				}
			}
		}

		return outputArray;

	}

	/**
	 * Calculate the length of the zero level set.
	 * 
	 * @param array An array of binary values, whose elements with positive values correspond to the
	 *        brain tissue.
	 * @return The number of positive pixels of the given array.
	 */
	public static int calculateContourLength(double[][] img, double threshold, byte white)
	{
		int length = 0;
		byte[][] byteArray = convertToBinaryAry(img, threshold, white);
		long tempLong;

		for (int y = 1; y < img.length - 1; y++)
		{
			for (int x = 1; x < img[0].length - 1; x++)
			{
				tempLong =
						4 * byteArray[y][x] - byteArray[y - 1][x] - byteArray[y + 1][x]
								- byteArray[y][x - 1] - byteArray[y][x + 1];
				if (tempLong > 0)
				{
					length++;
				}
			}
		}
		return length;
	}

	public static int getMax(BufferedImage image)
	{
		int max = Integer.MIN_VALUE;

		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				if (image.getRaster().getSample(x, y, 0) > max)
				{
					max = image.getRaster().getSample(x, y, 0);
				}
			}
		}

		return max;
	}

	public static int getMin(BufferedImage image)
	{
		int min = Integer.MAX_VALUE;

		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				if (image.getRaster().getSample(x, y, 0) < min)
				{
					min = image.getRaster().getSample(x, y, 0);
				}
			}
		}

		return min;
	}

	public static List<BufferedImage> readImage(String filePath)
	{

		final List<BufferedImage> bufferImageList = new ArrayList<BufferedImage>();

		try
		{
			final IFormatReader reader = new ImageReader();
			reader.setId(filePath);

			final BufferedImageReader buffReader = new BufferedImageReader(reader);

			for (int i = 0; i < buffReader.getSizeZ(); i++)
			{
				bufferImageList.add(buffReader.openImage(i));
			}

			buffReader.close();

		} catch (FormatException | IOException e)
		{
			IJ.log("Read image fails.");
			e.printStackTrace();
		}

		IJ.log("readImage = " + bufferImageList.size());

		return bufferImageList;
	}

	public static List<double[][]> convertBuffToDouble(List<BufferedImage> bufferedImage)
	{
		final List<double[][]> doubleList = new ArrayList<double[][]>();

		for (BufferedImage buffImage : bufferedImage)
		{
			double[][] doubleImg = new double[buffImage.getWidth()][buffImage.getHeight()];

			for (int x = 0; x < buffImage.getWidth(); x++)
			{
				for (int y = 0; y < buffImage.getHeight(); y++)
				{
					doubleImg[x][y] = buffImage.getRaster().getSample(x, y, 0);
				}
			}

			doubleList.add(doubleImg);
		}

		return doubleList;
	}

	public static List<double[][]> convertImgPlusToDouble(ImagePlus imgPlus)
	{
		final List<BufferedImage> buffList = new ArrayList<BufferedImage>();

		if (imgPlus == null)
		{
			return new ArrayList<double[][]>();
		}

		for (int i = 1; i <= imgPlus.getStackSize(); i++)
		{
			imgPlus.setSlice(i);

			buffList.add(imgPlus.getBufferedImage());
		}

		return convertBuffToDouble(buffList);
	}

	public static String convertDoubleToStr(double number)
	{

		String convertedStr = "";

		if (Double.isNaN(number))
		{
			convertedStr = "NaN";
		} else if (Double.isInfinite(number))
		{
			convertedStr = "Inf";
		} else
		{
			convertedStr = new DecimalFormat("#.####").format(number);
		}

		return convertedStr;
	}

	public static List<BufferedImage> vflipImages(List<BufferedImage> buffImgList)
	{
		IJ.log("Vertically flip image: " + buffImgList.size());

		final List<BufferedImage> flipBuffImgList = new ArrayList<BufferedImage>();

		for (BufferedImage img : buffImgList)
		{
			final BufferedImage flipImg =
					new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

			flipImageV(img, flipImg);

			flipBuffImgList.add(flipImg);

		}

		return flipBuffImgList;
	}

	public static void flipImageV(BufferedImage img, BufferedImage flipImg)
	{
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);

		tx.translate(0, -img.getHeight(null));

		AffineTransformOp tr = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		tr.filter(img, flipImg);
	}

	public static double[][] addDouble(double[][] img1, double[][] img2, double ratio1,
			double ratio2)
	{
		final double[][] result = new double[img1[0].length][img1.length];

		for (int y = 0; y < img1.length; y++)
		{
			for (int x = 0; x < img1[0].length; x++)
			{
				result[y][x] = ratio1 * img1[y][x] + ratio2 * img2[y][x];
			}
		}

		return result;
	}

	/**
	 * Create a 2D array to store the image data.
	 */
	public static short[][] createShortAry(BufferedImage bImg)
	{
		// Get the raster of the bImg
		Raster raster = bImg.getRaster();
		int xOffset = 0;
		int yOffset = 0;
		short[][] array = new short[bImg.getWidth()][bImg.getHeight()];

		// Offset to get the interesting region, set manually
		int tempInt;
		for (int y = 0; y < bImg.getHeight(); y++)
		{
			for (int x = 0; x < bImg.getWidth(); x++)
			{
				tempInt = raster.getSample(x + xOffset, y + yOffset, 0);

				tempInt &= 0xffff;
				array[x][y] = (short) tempInt;

			}
		}
		return array;
	}

	public static double[] getMaxSeries(List<BufferedImage> buffImages)
	{
		double[] maxSeries = new double[buffImages.size()];

		for (int i = 0; i < buffImages.size(); i++)
		{
			BufferedImage currentImg = buffImages.get(i);
			Raster raster = currentImg.getRaster();
			double max = 0;

			for (int y = 0; y < currentImg.getHeight(); y++)
			{
				for (int x = 0; x < currentImg.getWidth(); x++)
				{
					if (raster.getSample(x, y, 0) > max)
					{
						max = raster.getSample(x, y, 0);
					}
				}
			}

			maxSeries[i] = max;
		}

		return maxSeries;
	}

	public static List<BufferedImage> readImage2(String filePath)
	{
		final List<BufferedImage> bufferedImageList = new ArrayList<BufferedImage>();

		try
		{

			final ImporterOptions importerOpts = new ImporterOptions();
			importerOpts.setAutoscale(false);
			importerOpts.setId(filePath);

			final ImagePlus imgPlus = BF.openImagePlus(importerOpts)[0];

			for (int i = 0; i < imgPlus.getNSlices(); i++)
			{
				imgPlus.setSlice(i);

				BufferedImage buffImage = imgPlus.getBufferedImage();

				bufferedImageList.add(buffImage);
			}

		} catch (FormatException | IOException e)
		{
			IJ.log(e.getMessage());
		}

		return bufferedImageList;
	}

	public static boolean exportBufferedImage(BufferedImage bufferedImage, String fileName)
	{
		File file = new File(fileName + ".png");

		boolean result = false;
		try
		{
			result = ImageIO.write(bufferedImage, "PNG", file);
			if (!result)
			{
				System.out.println("wrong");
			}

		} catch (IOException e)
		{
			IJ.log(e.getMessage());
		}

		return result;
	}

	/**
	 * Rescale the image to the range [min, max]
	 * 
	 * @param bufferedImage
	 * @param min
	 * @param max
	 * @return
	 */
	public static BufferedImage rescaleImage(BufferedImage bufferedImage, int min, int max)
	{
		final int height = bufferedImage.getHeight();
		final int width = bufferedImage.getWidth();

		final BufferedImage scaledImage = new BufferedImage(width, height, bufferedImage.getType());
		final WritableRaster outRaster = scaledImage.getRaster();
		final WritableRaster inRaster = bufferedImage.getRaster();

		int imageMin = 0;
		int imageMax = 0;

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int value = inRaster.getSample(x, y, 0);

				if (value < imageMin)
				{
					imageMin = value;
				}

				if (value > imageMax)
				{
					imageMax = value;
				}
			}
		}

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				outRaster.setSample(x, y, 0, min
						+ (double) (inRaster.getSample(x, y, 0) - imageMin) / (imageMax - imageMin)
						* (max - min));
			}
		}

		return scaledImage;

	}

	public static boolean checkWithinRange(BufferedImage image, int lowerBound, int upperBound)
	{
		boolean withinRange = true;

		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int val = image.getRaster().getSample(x, y, 0);

				if (val < lowerBound || val > upperBound)
				{
					withinRange = false;
				}
			}
		}

		return withinRange;
	}

	/**
	 * Check and rescale image if it contains pixel out of the range [lowerBound, upperBound]
	 * 
	 * @param imageList
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	public static List<BufferedImage> checkAndRescaleRange(List<BufferedImage> imageList,
			int lowerBound, int upperBound)
	{
		final List<BufferedImage> rescaledList = new ArrayList<BufferedImage>();

		for (BufferedImage buffImage : imageList)
		{
			// Rescale the image if it contains pixel out of bound
			if (checkWithinRange(buffImage, lowerBound, upperBound))
			{
				rescaledList.add(buffImage);
			} else
			{
				rescaledList.add(rescaleImage(buffImage, lowerBound, upperBound));
			}
		}

		return rescaledList;

	}

	/**
	 * Find the index of the place where maximal difference occurs.
	 * 
	 * @param doubleList
	 * @return
	 */
	public static int findMaxDiff(List<Double> doubleList)
	{
		int maxDiff = 0;
		int maxDiffIdx = 0;

		for (int idx = 0; idx < doubleList.size(); idx++)
		{
			double diff = Math.abs(doubleList.get(idx) - doubleList.get(idx + 1));

			maxDiffIdx = diff > maxDiff ? idx : maxDiffIdx;

		}

		return maxDiffIdx;
	}

	public static ImagePlus readImage(String filePath, boolean verticalFlip)
	{
		ImagePlus imagePlus = new ImagePlus();
		try
		{
			imagePlus = BF.openImagePlus(filePath)[0];

			if (verticalFlip)
			{
				for (int i = 1; i < imagePlus.getStackSize(); i++)
				{
					imagePlus.setSlice(i);
					imagePlus.getProcessor().flipVertical();
				}
			}

		} catch (FormatException | IOException e)
		{
			IJ.log(LSConstants.ERROR_OPEN_IMAGE);
		}

		return imagePlus;
	}

	public static ImagePlus combineGroundTruth(List<String> gtPathList)
	{
		IJ.log("Ground truth amount: " + gtPathList.size());

		final ImagePlus firstImgPlus = readImage(gtPathList.get(0), true);

		final ImagePlus resultImgPlus =
				IJ.createImage("", firstImgPlus.getWidth(), firstImgPlus.getHeight(),
						firstImgPlus.getStackSize(), 8);

		// For each gound truth
		for (String gtPath : gtPathList)
		{
			final ImagePlus imgPlus = readImage(gtPath, true);

			// For each slice
			for (int i = 1; i <= imgPlus.getStackSize(); i++)
			{
				resultImgPlus.setSlice(i);
				final ImageProcessor oriImgProcessor = resultImgPlus.getProcessor();

				imgPlus.setSlice(i);

				// For each pixel, do OR operation
				for (int x = 0; x < imgPlus.getWidth(); x++)
				{
					for (int y = 0; y < imgPlus.getHeight(); y++)
					{
						if (imgPlus.getPixel(x, y)[0] != 0)
						{
							oriImgProcessor.set(x, y, 255);
						}

					}
				}

				resultImgPlus.setProcessor(oriImgProcessor);

			}
		}

		return resultImgPlus;

	}

}
