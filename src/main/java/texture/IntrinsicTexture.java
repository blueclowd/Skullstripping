package texture;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IntrinsicTexture extends Texture
{
	private static final Logger LOGGER = LoggerFactory.getLogger(IntrinsicTexture.class);

	private double[][] Z;

	public IntrinsicTexture(double[][] img)
	{
		super(img);

		calculateZ();
	}

	public IntrinsicTexture(BufferedImage img)
	{
		super(convertToDouble(img));

		calculateZ();
	}

	/**
	 * Evaluate intrinsic texture
	 * 
	 * @return
	 */
	@Override
	public double[][] evaluate()
	{
		final double[][] intrinsic = new double[img.length][img[0].length];

		for (int x = 0; x < img.length; x++)
		{
			for (int y = 0; y < img[0].length; y++)
			{
				final double alpha = getAlpha(x, y);

				final double beta = getBeta(x, y);

				final double gamma = getGamma(x, y);

				final double kappa1 =
						((-1) * beta + Math.sqrt(beta * beta - 4 * alpha * gamma)) / (2 * alpha);

				final double kappa2 =
						((-1) * beta - Math.sqrt(beta * beta - 4 * alpha * gamma)) / (2 * alpha);

				intrinsic[x][y] = Math.sqrt(kappa1 * kappa1 + kappa2 * kappa2);

			}
		}

		return intrinsic;
	}

	private void calculateZ()
	{
		Z = new double[img.length][img[0].length];

		for (int y = 0; y < Z.length; y++)
		{
			for (int x = 0; x < Z[0].length; x++)
			{
				Z[y][x] = Math.sqrt(1 + getX(x, y) * getX(x, y) + getY(x, y) * getY(x, y));
			}
		}

	}

	private double getZ(int x, int y)
	{

		return Z[x][y];
	}

	private double getAlpha(int x, int y)
	{
		return (1 + getX(x, y) * getX(x, y)) * (1 + getX(x, y) * getX(x, y)) - getX(x, y)
				* getY(x, y);
	}

	private double getBeta(int x, int y)
	{

		return (-1)
				* (getXX(x, y) * (1 + getY(x, y) * getY(x, y)) + getYY(x, y)
						* (1 + getX(x, y) * getX(x, y)) - getXY(x, y) * (getX(x, y) * getY(x, y)))
				/ getZ(x, y);

	}

	private double getGamma(int x, int y)
	{
		return (getXX(x, y) * getYY(x, y) - getXY(x, y) * getXY(x, y)) / (getZ(x, y) * getZ(x, y));
	}

}
