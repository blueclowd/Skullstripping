package texture;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gray Level Co-occurrence Matrix
 * 
 * @author Vincent Liu
 * 
 */
public class GLCM extends Texture
{

	private static final Logger LOGGER = LoggerFactory.getLogger(GLCM.class);

	private GLCMFeatures glcmFeatures;

	private Map<Point, double[][]> glcmMap = new HashMap<Point, double[][]>();


	public GLCM(double[][] img, int level, int xDis, int yDis, int min, int max, int winSize)
	{
		super(img);

		LOGGER.debug("GLCM: level = {}, distance(x,y) = {},{}", level, xDis, yDis);

		final int[][] scaled = doQuantization(img, level, min, max);

		glcmMap = evaluate(scaled, level, xDis, yDis, winSize);

		glcmFeatures = calculateFeatures(glcmMap);

	}

	public GLCM(BufferedImage img, int level, int xDis, int yDis, int min, int max, int winSize)
	{
		super(img);

		LOGGER.debug("GLCM: level = {}, distance(x,y) = {},{}", level, xDis, yDis);

		final int[][] scaled = doQuantization(convertToDouble(img), level, min, max);

		glcmMap = evaluate(scaled, level, xDis, yDis, winSize);

		glcmFeatures = calculateFeatures(glcmMap);

	}

	private Map<Point, double[][]> evaluate(int[][] scaled, int level, int xDis, int yDis,
			int winSize)
	{
		final int height = scaled.length;
		final int width = scaled[0].length;

		final Map<Point, double[][]> glcmMap = new HashMap<Point, double[][]>();

		for (int y = 0; y < height - level; y++)
		{
			for (int x = 0; x < width - level; x++)
			{
				int[][] window = new int[winSize][winSize];

				for (int dy = 0; dy < winSize; dy++)
				{
					for (int dx = 0; dx < winSize; dx++)
					{
						window[dy][dx] = scaled[y + dy][x + dx];
					}
				}

				double[][] glcm = calculateSingleGLCM(window, level, xDis, yDis);

				glcmMap.put(new Point(x, y), glcm);

			}
		}

		return glcmMap;

	}

	/**
	 * Sampling the image by level
	 * 
	 * @param img
	 * @param level
	 * @return
	 */
	private int[][] doQuantization(double[][] img, int level, int min, int max)
	{
		final int[][] scaled = new int[img.length][img[0].length];

		double oriMin = Double.MAX_VALUE;
		double oriMax = Double.MIN_VALUE;

		// Calculate the max and min intensity of image
		for (int y = 0; y < img.length; y++)
		{
			for (int x = 0; x < img[0].length; x++)
			{
				if (img[y][x] < oriMin)
				{
					oriMin = img[y][x];
				}

				if (img[y][x] > oriMax)
				{
					oriMax = img[y][x];
				}
			}
		}

		for (int y = 0; y < img.length; y++)
		{
			for (int x = 0; x < img[0].length; x++)
			{
				scaled[y][x] =
						(int) Math.round(min + (img[y][x] - oriMin) / (oriMax - oriMin)
								* (max - min));
			}
		}

		return scaled;
	}

	/**
	 * Calculate GLCM
	 * 
	 * @param scaled
	 * @param xDis
	 * @param yDis
	 */
	private double[][] calculateSingleGLCM(int[][] window, int level, int xDis, int yDis)
	{

		final int height = window.length;
		final int width = window[0].length;

		final double[][] glcm = new double[level][level];

		int sum = 0;

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (withinImg(x + xDis, y + yDis, width, height))
				{
					glcm[window[y][x]][window[y + yDis][x + xDis]]++;
					sum++;
				}
			}
		}

		// Normalization: divided by the frequency
		for (int y = 0; y < glcm.length; y++)
		{
			for (int x = 0; x < glcm[0].length; x++)
			{
				glcm[y][x] /= sum;
			}
		}

		return glcm;

	}

	/**
	 * Check if (x,y) is out of bound
	 * 
	 * @param x
	 * @param y
	 * @param xBound
	 * @param yBound
	 * @return
	 */
	private boolean withinImg(int x, int y, int xBound, int yBound)
	{
		boolean isWithin = true;

		if (x < 0 || x >= xBound)
		{
			isWithin = false;
			LOGGER.debug("x out of bound: {}", x);
		}

		if (y < 0 || y >= yBound)
		{
			isWithin = false;
			LOGGER.debug("y out of bound: {}", y);
		}

		return isWithin;

	}

	private GLCMFeatures calculateFeatures(Map<Point, double[][]> glcmMap)
	{
		final double[][] contrast = new double[height][width];
		final double[][] homogenity = new double[height][width];
		final double[][] entropy = new double[height][width];
		final double[][] energy = new double[height][width];

		for (Map.Entry<Point, double[][]> glcm : glcmMap.entrySet())
		{
			double entropyTemp = 0;
			double energyTemp = 0;
			double contrastTemp = 0;
			double homogenityTemp = 0;

			double[][] glcmMat = glcm.getValue();
			Point loc = glcm.getKey();
			int y = (int) loc.getY();
			int x = (int) loc.getX();

			for (int i = 0; i < glcmMat.length; i++)
			{
				for (int j = 0; j < glcmMat[0].length; j++)
				{
					entropyTemp -= glcmMat[i][j] * Math.log(glcmMat[i][j]);
					energyTemp += glcmMat[i][j] * glcmMat[i][j];
					contrastTemp += (i - j) * (i - j) * glcmMat[i][j];
					homogenityTemp += glcmMat[i][j] / (1 + (i - j) * (i - j));
				}
			}

			contrast[y][x] = contrastTemp;
			homogenity[y][x] = homogenityTemp;
			entropy[y][x] = entropyTemp;
			energy[y][x] = energyTemp;

		}

		final GLCMFeatures glcmFeatures = new GLCMFeatures();
		glcmFeatures.setContrast(contrast);
		glcmFeatures.setEnergy(energy);
		glcmFeatures.setEntropy(entropy);
		glcmFeatures.setHomogenity(homogenity);

		return glcmFeatures;
	}


	public GLCMFeatures getGLCMFeatures()
	{
		return glcmFeatures;
	}

	public Map<Point, double[][]> getGLCMMap()
	{
		return glcmMap;
	}

}
