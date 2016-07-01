package texture;

import java.awt.image.BufferedImage;

public class Texture
{
	protected int height = 0;

	protected int width = 0;

	/**
	 * Original image
	 */
	protected double[][] img;

	/**
	 * First derivative of original image along x direction
	 */
	protected double[][] imgX;

	/**
	 * First derivative of original image along y direction
	 */
	protected double[][] imgY;

	/**
	 * Second derivative of original image along x direction
	 */
	protected double[][] imgXX;

	/**
	 * Second derivative of original image along y direction
	 */
	protected double[][] imgYY;

	/**
	 * First derivative of original image along x and y directions
	 */
	protected double[][] imgXY;

	public Texture(double[][] img)
	{
		this.img = img;
		this.height = img.length;
		this.width = img[0].length;

		imgX = new double[img.length][img[0].length];
		imgY = new double[img.length][img[0].length];
		imgXX = new double[img.length][img[0].length];
		imgYY = new double[img.length][img[0].length];
		imgXY = new double[img.length][img[0].length];

		difX();
		difY();
		difXX();
		difYY();
		difXY();
	}

	public Texture(BufferedImage buffImg)
	{
		this.img = convertToDouble(buffImg);

		imgX = new double[img.length][img[0].length];
		imgY = new double[img.length][img[0].length];
		imgXX = new double[img.length][img[0].length];
		imgYY = new double[img.length][img[0].length];
		imgXY = new double[img.length][img[0].length];

		difX();
		difY();
		difXX();
		difYY();
		difXY();
	}

	public double[][] evaluate()
	{
		return img;
	}

	protected static double[][] convertToDouble(BufferedImage img)
	{
		final double[][] doubleImg = new double[img.getHeight()][img.getWidth()];

		for (int y = 0; y < doubleImg.length; y++)
		{
			for (int x = 0; x < doubleImg[0].length; x++)
			{
				doubleImg[y][x] = img.getRaster().getSample(x, y, 0);
			}
		}

		return doubleImg;

	}

	protected void difX()
	{
		for (int y = 1; y < img.length; y++)
		{
			for (int x = 1; x < img[0].length; x++)
			{
				imgX[y][x] = img[y][x] - img[y][x - 1];
			}
		}
	}

	protected void difY()
	{
		for (int y = 1; y < img.length; y++)
		{
			for (int x = 1; x < img[0].length; x++)
			{
				imgY[y][x] = img[y][x] - img[y - 1][x];
			}
		}
	}

	protected void difXX()
	{
		for (int y = 1; y < img.length; y++)
		{
			for (int x = 1; x < img[0].length; x++)
			{
				imgXX[y][x] = imgX[y][x] - imgX[y][x - 1];
			}
		}
	}

	protected void difYY()
	{
		for (int y = 1; y < img.length; y++)
		{
			for (int x = 1; x < img[0].length; x++)
			{
				imgYY[y][x] = imgY[y][x] - imgY[y - 1][x];
			}
		}
	}

	protected void difXY()
	{
		for (int y = 1; y < img.length; y++)
		{
			for (int x = 1; x < img[0].length; x++)
			{
				imgXY[y][x] = imgX[y][x] - imgX[y - 1][x];
			}
		}
	}

	public double getX(int x, int y)
	{
		return imgX[y][x];
	}

	public double getY(int x, int y)
	{
		return imgY[y][x];
	}

	public double getXX(int x, int y)
	{
		return imgXX[y][x];
	}

	public double getYY(int x, int y)
	{
		return imgYY[y][x];
	}

	public double getXY(int x, int y)
	{
		return imgXY[y][x];
	}
}
