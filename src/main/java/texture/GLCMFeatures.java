package texture;

import java.awt.Point;
import java.util.Map;

/**
 * Features of Gray Level Co-ocurrence Matrix
 * 
 * @author Vincent Liu
 * 
 */
public class GLCMFeatures
{
	private double[][] contrast;
	private double[][] homogenity;
	private double[][] entropy;
	private double[][] energy;

	public GLCMFeatures(Map<Point, double[][]> glcmMap)
	{
		calculateFeatures(glcmMap);
	}

	/**
	 * Calculate features based on GLCM map
	 * 
	 * @param glcmMap
	 */
	private void calculateFeatures(Map<Point, double[][]> glcmMap)
	{
		int height = 0;
		int width = 0;

		if (glcmMap.containsKey(new Point(0, 0)))
		{
			height = glcmMap.get(new Point(0, 0)).length;
			width = glcmMap.get(new Point(0, 0))[0].length;
		} else
		{
			throw new IllegalArgumentException();
		}


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

		setContrast(contrast);
		setEnergy(energy);
		setEntropy(entropy);
		setHomogenity(homogenity);

	}

	public double[][] getContrast()
	{
		return contrast;
	}

	public void setContrast(double[][] contrast)
	{
		this.contrast = contrast;
	}

	public double[][] getHomogenity()
	{
		return homogenity;
	}

	public void setHomogenity(double[][] homogenity)
	{
		this.homogenity = homogenity;
	}

	public double[][] getEntropy()
	{
		return entropy;
	}

	public void setEntropy(double[][] entropy)
	{
		this.entropy = entropy;
	}

	public double[][] getEnergy()
	{
		return energy;
	}

	public void setEnergy(double[][] energy)
	{
		this.energy = energy;
	}

}
