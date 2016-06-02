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
import ij.io.FileSaver;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import utils.LSUtil;

/**
 * Class to apply level set algorithm to a 2D image.
 * 
 * @author Audrey H. Zhuang and Daniel J. Valentino
 * @version 2 September 2005
 */
public class SkullStripper
{

  /** The array containing the data of the final image. */
  private byte[][] _outputArray = null;

  /** The width of the image. */
  int X_DIM = 160;

  /** The height of the image. */
  int Y_DIM = 160;

  /** Consider byte image. */
  private final int IMAGE_TYPE = BufferedImage.TYPE_BYTE_GRAY;

  /** The given inputImage. */
  private BufferedImage _inputImage = null;

  /**
   * The intensity minimum below which lies 2% of the cumulative histogram.
   */
  private double _intens2 = 0;

  /**
   * The intensity maxmum below which lies 98% of the cumulative histogram.
   */
  private double _intens98 = 0;

  /**
   * The intensity maxmum below which lies 10% of the cumulative histogram.
   */
  private double _intens10 = 0;

  /**
   * The intensity maxmum below which lies 40% of the cumulative histogram.
   */
  private double _intens40 = 0;

  /** The Last Mask Area is used to compare with current mask area. */
  private int _lastMaskArea = -1;
  private int _lastLastMaskArea = -1;

  /**
   * The Stationary Counter indicates if this activeContour is stable: IF _statnCounter < STATN_MAX
   * ==> unstable
   */
  private int _statnCounter = -1;

  /** Maximum oscillations to reach stability */
  private int STATN_MAX = 4;

  /** Calculates level set functions */
  private Calculator _calculator;

  /** The number of bits per pixel in the skull-stripped image. */
  private int _bitsPP = 16;

  /** Intensity of brain mask. */
  private int _intensityOfMask = 1;

  /** X-coord of the center of the initial zero level set circle. */
  private double _centerX;

  /** Y-coord of the center of the initial zero level set circle. */
  private double _centerY;

  /** Radius of the initial zero level set circle. */
  private double _radius;

  /** Cut off boundary value of phi. */
  private double BOUNDARY_VALUE = -0.5;

  private ImagePlus imgPlus;

  private Color contourColor;


  /**
   * Class to apply level set algorithm to a 2D image.
   * 
   * @param bImg The image to be skull-stripped.
   * @param v The weight of the curvature part.
   * @param thresholdSelector A float number working as the threshold selector.
   */
  public SkullStripper(BufferedImage bImg, double v, double thresholdSelector, ImagePlus imgPlus,
      Color contourColor)
  {
    // Store the width and height of the image
    X_DIM = bImg.getWidth();
    Y_DIM = bImg.getHeight();
    _inputImage = bImg;
    _calculator = new Calculator(X_DIM, Y_DIM, v, thresholdSelector);

    this.imgPlus = imgPlus;
    this.contourColor = contourColor;

  }

  /**
   * Calculate the probing distance which is used for calculating the image- based force. The
   * distance for searching minimum intensity is dependent upon the subject's age, and the distance
   * for searching maximum intensity is 10mm.
   * 
   * @param xyDim The resolution of the pixels in both x and y directions, and assume the resolution
   *        in x and y direction is the same.
   * @param age The age of the subject to whom the data belongs.
   */
  public void calculateProbingDistance(float xyDim, double age)
  {
    _calculator.calculateProbingDistance(xyDim, age);
  }

  /**
   * Create a 2D array to store the image data.
   */
  public short[][] create2DArray(BufferedImage bImg)
  {
    // Get the raster of the bImg
    Raster raster = bImg.getRaster();
    int xOffset = 0;
    int yOffset = 0;
    short[][] array = new short[bImg.getWidth()][bImg.getHeight()];

    // Offset to get the interesting region, set manually
    int tempInt;
    for (int y = 0; y < Y_DIM; y++)
    {
      for (int x = 0; x < X_DIM; x++)
      {
        tempInt = raster.getSample(x + xOffset, y + yOffset, 0);
        tempInt &= 0xffff;
        array[x][y] = (short) tempInt;
      }
    }
    return array;
  }

  /**
   * Gets the initial parameters of the zero level set.
   * 
   * @return A double array in the order of x_coord of the center, y_coord of the center, and the
   *         radius.
   */
  public double[] getInitialParameters()
  {
    double[] temp = new double[3];
    temp[0] = _centerX;
    temp[1] = _centerY;
    temp[2] = _radius;
    return temp;
  }

  /**
   * Gets the input image.
   * 
   * @return An bufferedImage object.
   */
  public BufferedImage getInputImage()
  {
    return _inputImage;
  }

  /**
   * Gets intensity value of the non-zero pixels in the mask.
   * 
   * @return An int value.
   */
  public int getIntensityOfMask()
  {
    return _intensityOfMask;
  }

  /**
   * Gets the outputArray.
   * 
   * @return A byte array.
   */
  public byte[][] getOutputArray()
  {
    return _outputArray;
  }

  /**
   * Get the skull-stripped image.
   * 
   * @return A BufferedImage
   */
  public BufferedImage getOutputImage()
  {
    return _createOutputImage(_inputImage, _outputArray);
  }

  /*
   * Get a binary images in which 0 represents background and non-zero represents brain tissues. The
   * metadata to save with the skull-stripped results are obtained by revising the metadata of the
   * original image.
   * 
   * @return A BufferedImage.
   */
  public BufferedImage getOutputMaskImage()
  {
    return _createMaskImage(_outputArray, _bitsPP);
  }

  /**
   * Get velocity.
   * 
   * @return A double value.
   */
  public double getVelocity()
  {
    return _calculator.getVelocity();
  }

  /**
   * Gets the zero level set contour.
   * 
   * @return A bufferedImage object.
   */
  public BufferedImage getZeroLevelSetImage()
  {
    return _createContourImage(_outputArray);
  }

  /**
   * Sets bits per pixel.
   * 
   * @param bitsPP The number of bits per pixel.
   */
  public void setBitsPP(int bitsPP)
  {
    _bitsPP = bitsPP;
  }

  /**
   * Sets the initial parameters for initializing the zero level set. The zero level set is
   * initialized as a signed distance function, which is a circle defined by a center and radius.
   * 
   * @param x X-coord of the circle's center..
   * @param y Y-coord of the circle's center.
   * @param r Radius of the circle.
   */
  public void setInitialParameters(double x, double y, double r)
  {
    _centerX = x;
    _centerY = y;
    _radius = r;
  }

  /**
   * Set stripping enabled so that the activeContour can be ready to do skull-stripping.
   * 
   * @param isStrippingEnabled True it is enabled; false otherwise.
   */
  public void setStrippingEnabled(boolean isStrippingEnabled)
  {
    if (isStrippingEnabled)
    {
      _statnCounter = -1;
    }
  }

  /**
   * Set intensity values at 2%, 10%, 40% and 98% histogram.
   * 
   * @param intens2 The intensity minimum below which lies 2% of the cumulative histogram.
   * @param intens10 The intensity minimum below which lies 10% of the cumulative histogram.
   * @param intens40 The intensity minimum below which lies 40% of the cumulative histogram.
   * @param intens98 The intensity minimum below which lies 98% of the cumulative histogram.
   */
  public void setThresholdIntensities(double intens2, double intens10, double intens40,
      double intens98)
  {
    _intens2 = intens2;
    _intens10 = intens10;
    _intens40 = intens40;
    _intens98 = intens98;
  }


  /**
   * Sets the outputArray.
   * 
   * @param array A byte array to set to the _outputArray.
   */
  public void setOutputArray(byte[][] array)
  {
    _outputArray = array;
  }

  /**
   * Set velocity.
   * 
   * @param velocity Velocity of the level set model.
   */
  public void setVelocity(double velocity)
  {
    _calculator.setVelocity(velocity);
  }



  /**
   * Shrink phi.
   * 
   * @param phi The array of phi.
   * @param DIST Distance to shrink zero level set.
   */
  public double[][] shrinkPhiZero(double[][] phi, int DIST)
  {
    for (int k = 0; k < DIST; k++)
    {

      // Move zero contour inward
      for (int y = 0; y < phi[0].length; y++)
      {
        for (int x = 0; x < phi.length; x++)
        {
          phi[x][y] = phi[x][y] - 1;
        }
      }

      // reinialize phi
      phi = _calculator.reinitialize(phi);
    }
    return phi;
  }

  /**
   * Skull strips a 2D brain image with the given initial phi.
   * 
   * @param masIter Maximum interations to run.
   * @param phi0 The phi array.
   * @return A phi array.
   */
  public double[][] skullStrip(int maxIter, double[][] phi0, int sliceNo)
  {
    if (phi0 == null)
    {
      phi0 = initializePhi();
    }

    // When the initial phi doesn't contain a circle, that is, the area
    // inside the zero level set is 0, just return the empty phi
    if (LSUtil.calculateMaskArea(phi0, -0.5d) <= 0)
    {
      IJ.log("Mask area < 0");
      _outputArray = _calculator.convertToBinaryArray(phi0);
      return phi0;
    }

    int maskArea = 0;
    short[][] inputArray = create2DArray(_inputImage);
    int timer = 0;
    double[][] delta_eps;
    double[][] f;
    double[][] phi = null;
    double intensM = 0;
    boolean isStationary = false;

    double[][] phiNew = phi0;
    _outputArray = _calculator.convertToBinaryArray(phiNew);

    // If initial mask area is too small, use _intens10 as intensM
    intensM =
        (LSUtil.calculateMaskArea(phi0, -0.5d) <= 200) ? _intens10 : _calculator
            .calculateMedianIntensity(phiNew, inputArray);

    imgPlus.setSlice(sliceNo);



    // when the zero level set is stationary, stop iterating
    // When the interation exceeds the max iteration numbers, stop iterating
    while ((!isStationary) && timer < maxIter)
    {

      // update phi
      phi = phiNew;

      // Narrow band
      delta_eps = _calculator.calculateHEpsilonAndDeltaEpsilon3(phi);


      // Calculate array of force
      f =
          _calculator.calculateF(phi, delta_eps, inputArray, _intens2, _intens10, intensM,
              _intens98);

      // Calculate phi1new
      phiNew = _calculator.calculatePhiNew2(phi, delta_eps, f);

      // Reinitialization
      phiNew = _calculator.reinitialize(phiNew);

      imgPlus.setOverlay(LSUtil.getContourOverlay(_calculator.convertToBinaryArray(phi0),
          Color.red, _calculator.convertToBinaryArray(phiNew), contourColor));
      imgPlus.updateAndDraw();
      imgPlus.show();


      // calculate the area of brain mask
      if (timer % 8 == 0)
      {
        maskArea = LSUtil.calculateMaskArea(phiNew, -0.5d);
        isStationary = _isPhiStationary(maskArea);
      }

      timer++;

    }

    if (maxIter > 5)
    {
      _outputArray = _calculator.convertToBinaryArray(phiNew);
    }

    // In order to save overlay, save current slice in jpeg format.
    new FileSaver(imgPlus).saveAsJpeg(sliceNo + ".jpg");

    IJ.log("Timer: " + timer);

    return phiNew;
  }

  /**
   * Creates a contour image from the given array.
   * 
   * @param array A byte, binary array.
   * @return A BufferedImage object.
   */
  private BufferedImage _createContourImage(byte[][] array)
  {
    BufferedImage contourImage = new BufferedImage(X_DIM, Y_DIM, BufferedImage.TYPE_INT_ARGB);
    int rgb = (0xff << 24) | (0xff << 16) | (0xf << 8); // red
    // int rgb = (0xff << 24) |(0xff << 16) | (0xff << 8) | 0xff;//white
    long tempLong;
    for (int y = 1; y < Y_DIM - 1; y++)
    {
      for (int x = 1; x < X_DIM - 1; x++)
      {
        tempLong =
            4 * array[x][y] - array[x][y - 1] - array[x][y + 1] - array[x - 1][y] - array[x + 1][y];
        if (tempLong != 0)
        {
          contourImage.setRGB(x, y, rgb);
        }
      }
    }
    return contourImage;
  }

  /**
   * Create a brain mask image.
   * 
   * @param outputArray A byte, binary array.
   * @param bitsPP Bits per pixel.
   * @return A BufferedImage object.
   */
  private BufferedImage _createMaskImage(byte[][] outputArray, int bitsPP)
  {
    BufferedImage outputImage = null;

    if (outputArray == null)
    {
      return outputImage;
    }

    int imageType;
    if (bitsPP <= 8)
    {
      imageType = BufferedImage.TYPE_BYTE_GRAY;
      byte tempByte = (byte) _intensityOfMask;
      outputImage = new BufferedImage(X_DIM, Y_DIM, imageType);
      WritableRaster outputRaster = outputImage.getRaster();
      for (int y = 0; y < Y_DIM; y++)
      {
        for (int x = 0; x < X_DIM; x++)
        {
          if (outputArray[x][y] > 0)
          {
            outputRaster.setSample(x, y, 0, tempByte);
          }
        }
      }
    } else
    {
      imageType = BufferedImage.TYPE_USHORT_GRAY;
      short tempShort = (short) _intensityOfMask;
      outputImage = new BufferedImage(X_DIM, Y_DIM, imageType);
      WritableRaster outputRaster = outputImage.getRaster();
      for (int y = 0; y < Y_DIM; y++)
      {
        for (int x = 0; x < X_DIM; x++)
        {
          if (outputArray[x][y] > 0)
          {
            outputRaster.setSample(x, y, 0, tempShort);
          }
        }
      }
    }
    return outputImage;
  }

  /**
   * Create the skull-stripped brain image.
   * 
   * @param inputImage The inputImage to be skull-stripped.
   * @param outputArray An array of the same size as that of the inputImage, and contains the level
   *        set information.
   */
  private BufferedImage _createOutputImage(BufferedImage inputImage, byte[][] outputArray)
  {
    // Create an empty bufferedImage
    int imageType = 0;
    if (_bitsPP <= 8)
      imageType = BufferedImage.TYPE_BYTE_GRAY;
    else
      imageType = BufferedImage.TYPE_USHORT_GRAY;
    BufferedImage outputImage =
        new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), imageType);

    Raster inputRaster = inputImage.getRaster();
    WritableRaster outputRaster = outputImage.getRaster();
    int width = inputImage.getWidth();
    int height = inputImage.getHeight();
    int tempInt;
    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        if (outputArray[x][y] > 0)
        {

          tempInt = inputRaster.getSample(x, y, 0);
          outputRaster.setSample(x, y, 0, tempInt);
        }
      }
    }
    return outputImage;
  }


  /**
   * Get the max value of the array.
   * 
   * @param array to search max within.
   * @return max value of the array.
   */
  private int _getMax(int[] array, int depth)
  {
    int max = array[0];
    if (array.length < 2)
      return max;
    for (int i = 1; i < depth && i < array.length; i++)
    {
      if (max < array[i])
        max = array[i];
    }
    return max;
  }

  private int _getMax(short[] array, int depth)
  {
    int max = array[0];
    if (array.length < 2)
      return max;
    for (int i = 1; i < depth && i < array.length; i++)
    {
      if (max < array[i])
        max = array[i];
    }
    return max;
  }


  /**
   * Initialize phi
   * 
   * @return A double array.
   */
  public double[][] initializePhi()
  {
    final double[][] phi = new double[X_DIM][Y_DIM];

    double temp1, temp2, temp3;

    // phi1_0 = -sqrt((x-centerX)^2 + (y-centerY)^2) + radius
    for (int y = 0; y < Y_DIM; y++)
    {
      for (int x = 0; x < X_DIM; x++)
      {
        temp1 = (x - _centerX) * (x - _centerX);
        temp2 = (y - _centerY) * (y - _centerY);

        // Zero level set: the circle centered at (_centerX, _centerY) with radius _radius
        temp3 = -Math.sqrt(temp1 + temp2) + _radius;

        phi[x][y] = temp3;
      }
    }

    return phi;
  }

  /**
   * Check if the result is stationary.
   */
  private boolean _isPhiStationary(int maskArea)
  {
    if (_lastLastMaskArea < 0)
    {
      _lastLastMaskArea = maskArea;
      return false;
    }
    if (_lastMaskArea < 0)
    {
      _lastMaskArea = maskArea;
      return false;
    }

    int GROWTH_RANGE = (int) ((double) _lastLastMaskArea * 0.0003);

    // Check if the mask are stops growing
    // if maskArea is 14000 pixels, then 2 pixel growth is insignificant, which
    // is often occur at spinal cord
    if ((_lastMaskArea - _lastLastMaskArea) <= GROWTH_RANGE
        && (maskArea - _lastMaskArea) <= GROWTH_RANGE)
    {
      _statnCounter++;
    }

    // Check if the mask area is oscillating
    else if ((_lastMaskArea - _lastLastMaskArea) < 0 && (maskArea - _lastMaskArea) > 0)
    {
      _statnCounter++;
    } else if ((_lastMaskArea - _lastLastMaskArea) > 0 && (maskArea - _lastMaskArea) < 0)
    {
      _statnCounter++;
    }

    // update lastmaskArea and _lastLastMaskArea
    _lastLastMaskArea = _lastMaskArea;
    _lastMaskArea = maskArea;

    // if (_statnCounter > STATN_MAX)
    // return true;
    // else
    // return false;

    return _statnCounter > STATN_MAX;
  }

  public void clearLastMaskArea()
  {
    _lastMaskArea = -1;
    _lastLastMaskArea = -1;
    _statnCounter = -1;
  }

}
