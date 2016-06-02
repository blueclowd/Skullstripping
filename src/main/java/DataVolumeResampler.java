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


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Vector;

/**
 * Class to collect images by re-sampling a 3D data volume.
 * 
 * @author Audrey H. Zhuang and Daniel J. Valentino
 * @version 2 September 2005
 */
public class DataVolumeResampler {
  /** The object of DataVolume. */
  private DataVolume _dataVolume;

  /** Constructor. */
  public DataVolumeResampler(DataVolume dataVolume) {
    _dataVolume = dataVolume;
  }

  /**
   * Resamples data volume and Creates an ARGB image.
   * 
   * @param width Width of the image to be queried.
   * @param height Height of the image to be queried.
   * @param index Index of the queried image in the data volume.
   * @param orient Orientation of the queried image.
   * @param array The data volume to resample.
   * @param scaleRatios Scale ratios for scaling the resampled image.
   * @param interpRule Interpolation rule for scaling the resampled image.
   * @param imageColors Color information of binary mask image.
   * @param rgbArray An array of ARGB values.
   * 
   * @return A buffered image.
   */
  public BufferedImage createARGBImage(int index, int orient, Vector imageColors) {
    if (index >= _dataVolume.getDepth(orient))
      index = _dataVolume.getDepth(orient);
    if (index < 0)
      index = 0;
    int width = _dataVolume.getWidth(orient);
    int height = _dataVolume.getHeight(orient);
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    int rgb = ((Integer) imageColors.get(0)).intValue();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        // if(raster.getSample(x, y, 0) != 0)
        if (getPixelValue(x, y, index, orient) != 0)
          image.setRGB(x, y, rgb);
      }
    }
    return image;
  }

  /**
   * Resamples data volume and Creates an ARGB image.
   * 
   * @param width Width of the image to be queried.
   * @param height Height of the image to be queried.
   * @param index Index of the queried image in the data volume.
   * @param orient Orient of the queried image.
   * @param array The data volume to resample.
   * @param scaleRatios Scale ratios for scaling the resampled image.
   * @param interpRule Interpolation rule for scaling the resampled image.
   * @param imageColors Color information of binary mask image.
   * @param rgbArray An array of ARGB values.
   * 
   * @return A buffered image.
   */
  public BufferedImage createARGBImage(int index, int orient, int[] rgbArray) {
    if (index >= _dataVolume.getDepth(orient))
      index = _dataVolume.getDepth(orient);
    if (index < 0)
      index = 0;
    int width = _dataVolume.getWidth(orient);
    int height = _dataVolume.getHeight(orient);

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    int pixelValue;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        pixelValue = getPixelValue(x, y, index, orient);
        if (pixelValue < 0) {
          if (_dataVolume.getBitsPP() == 8)
            pixelValue = pixelValue & 0xff;
          else if (_dataVolume.getBitsPP() == 16)
            pixelValue = pixelValue & 0xffff;
        }
        image.setRGB(x, y, rgbArray[pixelValue]);
      }
    }
    return image;
  }

  /**
   * Creates an ARGB image containing the contours of the delineated structures.
   * 
   * @param colorImage containing fullfilled delineated structures.
   * 
   * @return A bufferedImage.
   */
  public static BufferedImage createContourImage(BufferedImage colorImage) {
    int width = colorImage.getWidth();
    int height = colorImage.getHeight();
    BufferedImage contourImage = new BufferedImage(width, height, colorImage.getType());
    long tempLong;

    // Extract the contour
    for (int y = 2; y < height - 2; y++) {
      for (int x = 2; x < width - 2; x++) {
        tempLong =
            4 * colorImage.getRGB(x, y) - colorImage.getRGB(x, (y - 2))
                - colorImage.getRGB(x, (y + 2)) - colorImage.getRGB((x - 2), y)
                - colorImage.getRGB((x + 2), y);
        if (tempLong != 0) {
          contourImage.setRGB(x, y, colorImage.getRGB(x, y));
        }
      }
    }
    return contourImage;
  }

  /**
   * Resamples data volume and creates a gray.
   * 
   * @param width Width of the image to be queried.
   * @param height Height of the image to be queried.
   * @param IMAGE_TYPE Image type of the image to be queried.
   * @param index Index of the queried image in the data volume.
   * @param orient Orientation of the queried image.
   * @param array The data volume to resample.
   * @param scaleRatios Scale ratios for scaling the resampled image.
   * @param interpRule Interpolation rule for scaling the resampled image.
   * 
   * @return A bufferedImage.
   */
  public BufferedImage createGrayImage(int IMAGE_TYPE, int index, int orient) {
    if (index >= _dataVolume.getDepth(orient))
      index = _dataVolume.getDepth(orient);
    if (index < 0)
      index = 0;
    int width = _dataVolume.getWidth(orient);
    int height = _dataVolume.getHeight(orient);
    BufferedImage image = new BufferedImage(width, height, IMAGE_TYPE);
    WritableRaster raster;
    raster = image.getRaster();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        raster.setSample(x, y, 0, getPixelValue(x, y, index, orient));
      }
    }
    return image;
  }

  /**
   * Gets the value of a pixel in a resampled 2D section from the data data volume. The 2D section
   * is defined by its orientation and its index in the data volume; the pixel to be queried is
   * defined by its 2D coordinate (x, y).
   * 
   * @param x The x coordinate of the pixel in the 2D section.
   * @param y The y coordinate of the pixel in the 2D section.
   * @param index The index of the section in the data volume.
   * @param orient The orientation of the 2D section in the data volume.
   * 
   * @return An int number of the pixel's value.
   */
  public int getPixelValue(int x, int y, int index, int orient) {
    int pixel = -1;

    if (_dataVolume.getDataVolume() instanceof byte[][][]) {
      byte[][][] array = (byte[][][]) _dataVolume.getDataVolume();

      // Axial
      if (orient == 0)
        pixel = array[index][x][y] & 0xff;

      // Coronal
      else if (orient == 1)
        pixel = array[y][x][index] & 0xff;

      // Sagittal
      else
        pixel = array[y][index][x] & 0xff;
    } else {
      short[][][] array = (short[][][]) _dataVolume.getDataVolume();

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

  /**
   * Flips the BufferedImage horizontally or vertically.
   * 
   * @param BufferedImage The image to flip.
   * @param isXFlipEnabled Whether to flip the image about X axis.
   * @param isYFlipEnabled Whether to flip the image about Y axis.
   * 
   * @return A flipped bufferedImage.
   */
  public static BufferedImage flipImage(BufferedImage image, boolean isXFlipEnabled,
      boolean isYFlipEnabled) {
    if ((!isXFlipEnabled) && (!isYFlipEnabled))
      return image;
    if (image == null)
      return null;
    int width = image.getWidth();
    int height = image.getHeight();
    Raster imageRaster = image.getRaster();

    BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
    WritableRaster flippedRaster = flippedImage.getRaster();

    float pixelArray[] = null;
    int iFlip, jFlip;
    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {

        pixelArray = imageRaster.getPixel(i, j, pixelArray);
        if (isXFlipEnabled) {
          jFlip = height - j - 1;
        } else {
          jFlip = j;
        }
        if (isYFlipEnabled) {
          iFlip = width - i - 1;
        } else {
          iFlip = i;
        }

        flippedRaster.setPixel(iFlip, jFlip, pixelArray);
      }
    }
    return flippedImage;
  }

  /**
   * Histogram equalization or histogram linearization.
   * 
   * @param image Image to be queried.
   */
  public BufferedImage histEqualization(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    Raster imageRaster = image.getRaster();
    BufferedImage outputImage = new BufferedImage(width, height, image.getType());
    WritableRaster outputRaster = outputImage.getRaster();

    // Calculate maxI
    int maxI = 0;
    int tempPixel = 0;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        tempPixel = imageRaster.getSample(x, y, 0);
        if (maxI < tempPixel)
          maxI = tempPixel;
      }
    }

    // Initialize the histogram array
    int[] hist = new int[maxI + 1];

    // Constract histogram array
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        tempPixel = imageRaster.getSample(x, y, 0);
        hist[tempPixel]++;
      }
    }

    // Histogram equalization
    int newPixel;
    double totalSum = width * height;
    double sum;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        tempPixel = imageRaster.getSample(x, y, 0);
        sum = 0;
        for (int k = 0; k <= tempPixel; k++) {
          sum += hist[k];
        }
        newPixel = (int) Math.floor(sum / totalSum * maxI + 0.5);
        outputRaster.setSample(x, y, 0, newPixel);
      }
    }

    return outputImage;
  }

}
