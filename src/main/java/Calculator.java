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


import java.util.Vector;

import utils.LSUtil;

/**
 * Class to support data calculations.
 * 
 * @author Audrey H. Zhuang and Daniel J. Valentino
 * @version 2 September 2005
 */
public class Calculator
{

  /** Parameter h. */
  private double H = 1.0d;

  /** Parameter delta_t. */
  private double DELTA_T = 0.1d;

  /**
   * Parameter mu. If mu is assigned a small value, small objects can be detected; otherwise, if mu
   * is assigned a large value, small objects will be skipped.
   */
  private double MU = 0.05 * 255d * 255d;

  /** The weight of the image force. */
  private double _fFactor = 0.1 * 255 * 255;

  /** Parameter epsilon. */
  private double EPSILON = H;

  /** The width of the image. */
  int X_DIM;

  /** The height of the image. */
  int Y_DIM;

  /**
   * The distance for searching minimum intensity value which is used to calculate the image-based
   * force. It is 16 by default.
   */
  private int _minD = 16;

  /**
   * The distance for searching maximum intensity value which is used to calculate the image-based
   * force. It is always the half of _minD
   */
  private int _maxD = (int) Math.floor(_minD / 2 + 0.5);

  /**
   * The resolusions of the pixels in either x and y directions, assuming the resolution in both
   * directions are the same as xyDim.
   */
  private float _xyDim = 1.0f;

  /** Threshold selector. */
  private double _bT = 0.5;

  /** Cut off boundary value of phi. */
  private double BOUNDARY_VALUE = -0.5;


  /**
   * Constructor.
   * 
   * @param width Width of the 2D image grid.
   * @param height Height of the 2D image grid.
   * @param modelType Type-1 works for normal young brains; Type-2 works for aging or Alzheimer's
   *        brains.
   * @param velocity Velocity of the level set model.
   * @param thresholdSelector A float number working as the threshold selector.
   */
  public Calculator(int width, int height, double velocity, double thresholdSelector)
  {
    X_DIM = width;
    Y_DIM = height;
    MU = velocity;
    _bT = thresholdSelector;
  }

  /**
   * Calculates the array F. Each element of the array is a double number whose value is the image
   * force.
   */
  public double[][] calculateF(double[][] phi, double[][] delta_eps, short[][] inputArray,
      double intens2, double intens10, double intensM, double intens98)
  {
    double[][] fArray1 = new double[X_DIM][Y_DIM];

    // Find out zero level set and calculate force for zero level set
    for (int y = 1; y < Y_DIM - 1; y++)
    {
      for (int x = 1; x < X_DIM - 1; x++)
      {
        // Only level set within the band are updated and that level sets outside the band remain
        // stationary
        if (delta_eps[x][y] > 0)
        {

          // Calculate array of force for zero level set
          fArray1[x][y] = _calculateF(x, y, phi, inputArray, intens2, intens10, intensM, intens98);
        }
      }
    }

    return fArray1;
  }

  /**
   * Calculate delta_epsilon. Equation 2.16
   * 
   * @param phi A double array.
   * @return A double array of delta_eps.
   */
  public double[][] calculateHEpsilonAndDeltaEpsilon3(double[][] phi)
  {
    double[][] delta_eps = new double[X_DIM][Y_DIM];

    // epsilon = h = delta_x = delta_y
    double eps = 1.5 * H;
    double temp;
    double var;

    for (int x = 0; x < X_DIM; x++)
    {
      for (int y = 0; y < Y_DIM; y++)
      {
        var = phi[x][y];
        if (var < eps && var > (-eps))
        {
          delta_eps[x][y] = (1 + Math.cos(Math.PI * var / eps)) / (2 * eps);
        }
      }
    }
    return delta_eps;
  }


  /**
   * Calculate median intensity from the inputArray.
   * 
   * @param phi A double array.
   * @param inputArray InputArray to be queried.
   * @return A double value.
   */
  public double calculateMedianIntensity(double[][] phi, short[][] inputArray)
  {
    double median = 0;

    // Find the intensity of the pixel at the middle of the intensity queue
    int count = 0;

    // Find maxI
    int max = 0;
    for (int y = 0; y < Y_DIM; y++)
    {
      for (int x = 0; x < X_DIM; x++)
      {
        if (phi[x][y] > 0)
        {
          if (max < inputArray[x][y])
            max = inputArray[x][y];
        }
      }
    }

    int[] hist = new int[max + 1];
    int intens;
    for (int y = 0; y < Y_DIM; y++)
    {
      for (int x = 0; x < X_DIM; x++)
      {
        if (phi[x][y] > 0)
        {
          count++;
          intens = inputArray[x][y];
          hist[intens]++;
        }
      }
    }


    int temp = 0;
    int half = (int) (count / 2);
    for (int i = 0; i < hist.length; i++)
    {
      temp = temp + hist[i];
      if (temp > half)
      {
        median = i;
        i = hist.length;
      }
    }
    return median;
  }

  /**
   * Calculates the new phi.
   * 
   * @param phi A double array.
   * @param delta_eps A double array.
   * @param fArray A double array of image-based force.
   * @return A double array representing the phi.
   */
  public double[][] calculatePhiNew2(double[][] phi, double[][] delta_eps, double[][] fArray)
  {
    double[][] phiNew = LSUtil.copy2DAry(phi);

    for (int x = 2; x < X_DIM - 2; x++)
    {
      for (int y = 2; y < Y_DIM - 2; y++)
      {
        if (Math.abs(fArray[x][y]) > 0)
        {
          phiNew[x][y] = calculatePhiNew(x, y, phi, delta_eps, fArray[x][y]);
        }
      }
    }
    return phiNew;
  }


  /**
   * Calculates the new phi.
   * 
   * @param x X-coord of the pixel.
   * @param y Y-coord of the pixel.
   * @param phi A double array.
   * @param delta_eps A double array.
   * @param f Image-based force.
   * @return A double value representing the phi value at the queried pixel.
   */
  public double calculatePhiNew(int x, int y, double[][] phi, double[][] delta_eps,
      double imageForce)
  {
    double d1, d2, d3, d4, d;
    double u;
    double m;
    double phiNew;

    // Calculate d1, d2, d3, d4
    d1 = _calculateD1(phi, x, y);
    d2 = _calculateD1(phi, x - 1, y);
    d3 = _calculateD3(phi, x, y);
    d4 = _calculateD3(phi, x, y - 1);

    // Calculate m = delta_t * delta_eps[x][y] * mu / h^2
    m = DELTA_T * delta_eps[x][y] * MU / (H * H);

    // Calculate d
    d = 1 + m * (d1 + d2 + d3 + d4);

    // double fFactor = 255*255;
    double fFactor = _fFactor;
    u = DELTA_T * delta_eps[x][y] * fFactor * imageForce;
    phiNew =
        (phi[x][y] + m
            * (d1 * phi[x + 1][y] + d2 * phi[x - 1][y] + d3 * phi[x][y + 1] + d4 * phi[x][y - 1]) + u)
            / d;
    return phiNew;
  }

  /**
   * Calculate the probing distance which is used for calculating the image- based force. The
   * distance for searching minimum intensity is 20mm, and the distance for searching maximum
   * intensity is 10mm.
   * 
   * @param xyDim The equal dimension in x or y direction.
   * @param age The age of the subject to whom the data belongs.
   */
  public void calculateProbingDistance(double xyDim, double age)
  {
    // A pixel's length is approximated as xyDim * (sqrt(2) + 1)/2
    double pixelLength1 = xyDim * Math.sqrt(2);
    double pixelLength2 = xyDim;
    double pixelLength = (pixelLength1 + pixelLength2) / 2;

    // Distance (with the unit of mm) searched from maxI and minI
    double maxDisDouble = 10;
    double minDisDouble = (100 - age) / 100 * 20;
    if (age > 100)
      minDisDouble = 0;

    _maxD = (int) Math.floor(maxDisDouble / pixelLength + 0.5);
    _minD = (int) Math.floor(minDisDouble / pixelLength + 0.5);
  }

  /**
   * Converts a level set array to a binary mask. All positive elements in the level set array will
   * be set as a unique positive value, and all negative elements will be set as 0.
   * 
   * @param d A double array.
   * @return A byte, binary array.
   */
  public byte[][] convertToBinaryArray(double[][] d)
  {
    byte[][] outputArray = new byte[X_DIM][Y_DIM];
    for (int x = 0; x < X_DIM; x++)
    {
      for (int y = 0; y < Y_DIM; y++)
      {

        // Set all positive level sets with a unique positive number
        if (d[x][y] >= BOUNDARY_VALUE)
          outputArray[x][y] = 40;
      }
    }
    return outputArray;
  }

  /**
   * Get velocity.
   * 
   * @return A double value.
   */
  public double getVelocity()
  {
    return MU;
  }


  /**
   * Reinitialize phi to a new signed distance function. Each element of the new phi has its value
   * equal to the distance to the zero level set curve, and has positive sign if it is within the
   * zero level set curve, and has negative sign if it is out side of the zero level set curve.
   * 
   * @param array A double array to be queried.
   * @return A double array.
   */
  public double[][] reinitialize(double[][] array)
  {
    double[][] newArray = null;

    double old;
    double dis;
    int MAX_ITER = 200;


    double[][] tempArray = array;
    double s;
    boolean isStationary = false;
    int reini = 0;
    while (!isStationary && reini < MAX_ITER)
    {

      // Allocate newArray
      newArray = new double[X_DIM][Y_DIM];
      for (int y = 1; y < (Y_DIM - 1); y++)
      {
        for (int x = 1; x < (X_DIM - 1); x++)
        {

          old = tempArray[x][y];

          // Revision of Sussman's phi0 construction done by Peng
          dis = _calculateDistance(x, y, tempArray);
          s = old / Math.sqrt(old * old + 1);
          newArray[x][y] = old - DELTA_T * s * (dis - 1);

        }
      }
      for (int x = 0; x < X_DIM; x++)
      {
        newArray[x][0] = newArray[x][1];
        newArray[x][Y_DIM - 1] = newArray[x][Y_DIM - 2];
      }
      for (int y = 0; y < Y_DIM; y++)
      {
        newArray[0][y] = newArray[1][y];
        newArray[X_DIM - 1][y] = newArray[X_DIM - 2][y];
      }

      // Check stationary
      double sum = 0;
      int M = 0;
      for (int y = 1; y < (Y_DIM - 1); y++)
      {
        for (int x = 1; x < (X_DIM - 1); x++)
        {
          if (Math.abs(tempArray[x][y]) > 1.5)
          {
            sum += Math.abs(newArray[x][y] - tempArray[x][y]);
            M++;
          }
        }
      }
      double m = sum / M;
      if (m < DELTA_T)
        isStationary = true;

      // Update tempArray
      tempArray = newArray;
      reini++;
    }
    return newArray;
  }

  /**
   * Set velocity.
   * 
   * @param velocity Velocity of the level set model.
   */
  public void setVelocity(double velocity)
  {
    MU = velocity;
  }

  /**
   * d1 = (phi[x+1][y] - phi[x][y])/ sqrt(((phi[x+1][y]-phi[x][y])/h)^2 +
   * ((phi[x][y+1]-phi[x][y-1])/2h)^2)
   */
  private double _calculateD1(double[][] phi, int x, int y)
  {
    double d1 = 0;
    double temp1 = (phi[x + 1][y] - phi[x][y]) / H;
    double temp2 = (phi[x][y + 1] - phi[x][y - 1]) / (2 * H);
    double temp3 = Math.sqrt(temp1 * temp1 + temp2 * temp2);
    if (Math.abs(temp3) > 0)
      d1 = 1 / temp3;
    return d1;
  }

  /**
   * d2 = (phi[x][y+1] - phi[x][y]) / sqrt(((phi[x+1][y]-phi[x-1][y])/2h)^2 +
   * ((phi[x][y+1]-phi[x][y])/h)^2)
   */
  private double _calculateD3(double[][] phi, int x, int y)
  {
    double d2 = 0;
    double temp1 = (phi[x + 1][y] - phi[x - 1][y]) / (2 * H);
    double temp2 = (phi[x][y + 1] - phi[x][y]) / H;
    double temp3 = Math.sqrt(temp1 * temp1 + temp2 * temp2);
    if (Math.abs(temp3) > 0)
      d2 = 1 / temp3;
    return d2;
  }

  /**
   * The distance used in reinitialization.
   */
  private double _calculateDistance(int x, int y, double[][] phi)
  {
    // The exact equation should be a = (phi[x][y] - phi[x-1][y] ) / H
    double a = phi[x][y] - phi[x - 1][y];
    double b = phi[x + 1][y] - phi[x][y];
    double c = phi[x][y] - phi[x][y - 1];
    double d = phi[x][y + 1] - phi[x][y];

    double ans = 0;
    if (phi[x][y] != 0)
    {
      if (phi[x][y] > 0)
      {
        if (a < 0)
          a = 0;
        if (b > 0)
          b = 0;
        if (c < 0)
          c = 0;
        if (d > 0)
          d = 0;
      } else
      {
        if (a > 0)
          a = 0;
        if (b < 0)
          b = 0;
        if (c > 0)
          c = 0;
        if (d < 0)
          d = 0;
      }
      double max1 = Math.max(a * a, b * b);
      double max2 = Math.max(c * c, d * d);
      ans = Math.sqrt(max1 + max2);
    }
    return ans;
  }

  /**
   * Calculate the image-based force (Fimg) at (x, y).
   * 
   */
  public double _calculateF(int x, int y, double[][] phi, short[][] inputArray, double intens2,
      double intens10, double intensM, double intens98)
  {
    double f = 0;
    double[] normal;
    int minD = _minD;
    int maxD = _maxD;

    // Calculate the direction of the normal of phi(x,y)
    normal = _calculateNorDir(x, y, phi);

    final Vector<Integer> intensities = new Vector<Integer>(1, 1);

    int tempX;
    int tempY;
    int samplingDis = maxD > minD ? maxD : minD;

    for (int i = 0; i < samplingDis; i++)
    {

      // calculate the coords of pixels
      tempX = (int) Math.floor(x + i * normal[0] + 0.5);
      tempY = (int) Math.floor(y + i * normal[1] + 0.5);

      // If inside the image
      if (tempX < X_DIM && tempY < Y_DIM && tempX > 0 && tempY > 0)
      {
        intensities.add(new Integer(inputArray[tempX][tempY]));
      }
    }

    // Equation 2.8
    double tempMin = _getMin(intensities, minD);
    double minI = Math.max(intens2, Math.min(tempMin, intensM));

    // Equation 2.9
    double tempMax = _getMax(intensities, maxD);
    double maxI = Math.max(intensM, tempMax);

    // Equation 2.7
    double tL = (maxI - intens2) * _bT + intens2;

    // Equation 2.6
    f = 2 * (minI - tL) / (maxI - intens2);

    return f;
  }

  /**
   * Calculate the direction of the normal of phi(x, y), which is phi_y / phi_x.
   * 
   * @return A two element array in the order of normal_x and normal_y.
   */
  private double[] _calculateNorDir(int x, int y, double[][] phi)
  {
    double phi_y, phi_x;

    phi_y = (phi[x][y + 1] - phi[x][y - 1]) / 2;
    phi_x = (phi[x + 1][y] - phi[x - 1][y]) / 2;
    double d = Math.sqrt(phi_y * phi_y + phi_x * phi_x);
    double[] normal = new double[2];
    if (d != 0)
    {
      normal[0] = phi_x / d;
      normal[1] = phi_y / d;
    } else
    {
      normal[0] = 0;
      normal[1] = 0;
    }
    return normal;
  }


  /**
   * Gets the max value of the array.
   * 
   * @param array to search max within.
   * @return max value of the array.
   */
  private int _getMax(Vector<Integer> vector, int depth)
  {
    int max = ((Integer) vector.elementAt(0)).intValue();
    if (vector.size() <= 1)
    {
      return max;
    }
    if (depth > vector.size())
    {
      depth = vector.size();
    }
    // int temp;
    // for (int i = 1; i < depth; i++)
    // {
    // temp = ((Integer) vector.elementAt(i)).intValue();
    // if (max < temp)
    // max = temp;
    // }

    for (Integer value : vector)
    {
      if (value > max)
      {
        max = value;
      }
    }
    return max;
  }

  /**
   * Gets the min value of the array.
   * 
   * @param array to search max within.
   * @return min value of the array.
   */
  private int _getMin(Vector<Integer> vector, int depth)
  {
    int min = ((Integer) vector.elementAt(0)).intValue();

    if (vector.size() <= 1)
    {
      return min;
    }
    if (depth > vector.size())
    {
      depth = vector.size();
    }

    // int temp;
    // for (int i = 1; i < depth; i++)
    // {
    // temp = ((Integer) vector.elementAt(i)).intValue();
    // if (min > temp)
    // {
    // min = temp;
    // }
    // }

    for (Integer value : vector)
    {
      if (value < min)
      {
        min = value;
      }
    }
    return min;
  }
}
