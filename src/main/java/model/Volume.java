package model;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import ij.IJ;
import loci.formats.IFormatReader;
import utils.LSConstants;

public class Volume
{

	/** Width (X) of the data volume. */
	private int width;

	/** Height (Y) of the data volume. */
	private int height;

	/** Depth (Z) of the data volume. */
	private int depth;

	/** Data volume. */
	private short[][][] _volShort = null;

	/** Data volume. */
	private byte[][][] _volByte = null;

	public Volume(IFormatReader iFormatReader)
	{

		width = iFormatReader.getSizeX();
		height = iFormatReader.getSizeY();
		depth = iFormatReader.getSizeZ();

	}

	/**
	 * Gets the data volume.
	 * 
	 * @return The data volume.
	 */
	public Object getDataVolume()
	{
		if (_volByte != null)
		{
			return _volByte;
		} else
		{
			return _volShort;
		}
	}

	public int getDepth(int orient)
	{
		if (orient >= 3)
		{
			throw new IllegalArgumentException(
					"DataVolume: " + orient + " is not a valid orientation. " + "A valid orientation is 0, 1, "
							+ "or 2 which indicates axial, " + "coronal and sagittal " + "respectively.");
		}

		switch (orient)
		{
		case 0:
			return depth;
		case 1:
			return height;
		default:
			return width;
		}

	}

	public BufferedImage createGrayImage(BufferedImage buffImage, int imageType)
	{
		final int width = buffImage.getWidth();
		final int height = buffImage.getHeight();

		final BufferedImage resultImg = new BufferedImage(width, height, imageType);

		WritableRaster raster;
		raster = resultImg.getRaster();

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				// raster.setSample(x, y, 0, getPixelValue(x, y, index,
				// orient));

				if (imageType == BufferedImage.TYPE_BYTE_GRAY)
				{
					raster.setSample(x, y, 0, buffImage.getRaster().getSample(x, y, 0) & 0xff);

				} else if (imageType == BufferedImage.TYPE_USHORT_GRAY)
				{
					raster.setSample(x, y, 0, buffImage.getRaster().getSample(x, y, 0) & 0xffff);

				} else
				{
					IJ.error(LSConstants.ERROR_UNACCEPTABLE_IMAGE_TYPE + imageType);
				}

			}
		}

		return resultImg;
	}

	public BufferedImage createGrayImage(int IMAGE_TYPE, int index, int orient)
	{
		if (index >= getDepth(orient))
			index = getDepth(orient);
		if (index < 0)
			index = 0;
		int width = getWidth(orient);
		int height = getHeight(orient);
		BufferedImage image = new BufferedImage(width, height, IMAGE_TYPE);
		WritableRaster raster;
		raster = image.getRaster();
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				raster.setSample(x, y, 0, getPixelValue(x, y, index, orient));
			}
		}
		return image;
	}

	/**
	 * Gets the width of sections in a given orientation.
	 * 
	 * @param orient
	 *            Orientation of the sections to be queried. If orient is 0, it
	 *            is axial; 1 is coronal; and 2 is sagittal.
	 * 
	 * @throws IllegalArgumentException
	 *             If the orientation value is invalid.
	 * 
	 * @return The width of the sections in the given orientation.
	 */
	public int getWidth(int orient)
	{
		if (orient >= 3)
		{
			throw new IllegalArgumentException(
					"DataVolume: " + orient + " is not a valid orientation. " + "A valid orientation is 0, 1, "
							+ "or 2 which indicates axial, " + "coronal and sagittal " + "respectively.");
		}
		if (orient == 0)
			return width;
		else if (orient == 1)
			return width;
		else
			return height;
	}

	/**
	 * Gets the height of sections in a given orientation.
	 * 
	 * @param orient
	 *            Orientation of the sections to be queried. If orient is 0, it
	 *            is axial; 1 is coronal; and 2 is sagittal.
	 * 
	 * @throws IllegalArgumentException
	 *             If the orientation value is invalid.
	 * 
	 * @return The height of the sections in the given orientation.
	 */
	public int getHeight(int orient)
	{
		if (orient >= 3)
		{
			throw new IllegalArgumentException(
					"DataVolume: " + orient + " is not a valid orientation. " + "A valid orientation is 0, 1, "
							+ "or 2 which indicates axial, " + "coronal and sagittal " + "respectively.");
		}
		if (orient == 0)
			return height;
		else if (orient == 1)
			return depth;
		else
			return depth;
	}

	/**
	 * Gets the value of a pixel in a resampled 2D section from the data data
	 * volume. The 2D section is defined by its orientation and its index in the
	 * data volume; the pixel to be queried is defined by its 2D coordinate (x,
	 * y).
	 * 
	 * @param x
	 *            The x coordinate of the pixel in the 2D section.
	 * @param y
	 *            The y coordinate of the pixel in the 2D section.
	 * @param index
	 *            The index of the section in the data volume.
	 * @param orient
	 *            The orientation of the 2D section in the data volume.
	 * 
	 * @return An int number of the pixel's value.
	 */
	public int getPixelValue(int x, int y, int index, int orient)
	{
		int pixel = -1;

		if (getDataVolume() instanceof byte[][][])
		{
			byte[][][] array = (byte[][][]) getDataVolume();

			// Axial
			if (orient == 0)
				pixel = array[index][x][y] & 0xff;

			// Coronal
			else if (orient == 1)
				pixel = array[y][x][index] & 0xff;

			// Sagittal
			else
				pixel = array[y][index][x] & 0xff;
		} else
		{
			short[][][] array = (short[][][]) getDataVolume();

			// Axial
			if (orient == 0)
				pixel = array[index][x][y] & 0xffff;

			// Coronal
			else if (orient == 1)
				pixel = array[y][x][index] & 0xffff;

			// Sagittal
			else
				pixel = array[y][index][x] & 0xffff;
		}
		return pixel;
	}

}
