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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    // Check

    for (int x = 10; x < mask.length - 10; x++)
    {
      for (int y = 10; y < mask[0].length - 10; y++)
      {
        if (4 * mask[x][y] != mask[x - 1][y] + mask[x + 1][y] + mask[x][y - 1] + mask[x][y + 1])
        {
          contourX.add(x);
          contourY.add(y);
        }

        // Check only
        if (mask[x][y] != 0 && mask[x][y] != 40)
        {
          IJ.error("Unexpected value: " + mask[x][y]);
        }
      }
    }

    // IJ.log("Contour point number: " + contourX.size());

    for (int i = 0; i < contourX.size(); i++)
    {
      final Roi roi = new Line(contourX.get(i), contourY.get(i), contourX.get(i), contourY.get(i));

      roi.setStrokeWidth(0);
      roi.setStrokeColor(overlayColor);

      overlay.add(roi);

    }



    return overlay;

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

    for (int x = 0; x < result.length; x++)
    {
      for (int y = 0; y < result[0].length; y++)
      {
        resultBool[x][y] = result[x][y] >= value;
      }
    }

    return resultBool;
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
        if (img[x][y] >= threshold)
        {
          maskArea++;
        }
      }
    }

    return maskArea;
  }


  /**
   * Converts a level set array to a binary mask. All positive elements in the level set array will
   * be set as a unique positive value, and all negative elements will be set as 0.
   * 
   * @param d A double array.
   * @return A byte, binary array.
   */
  public static byte[][] convertToBinaryAry(double[][] img, double threshold, byte white)
  {
    byte[][] outputArray = new byte[img.length][img[0].length];

    for (int x = 0; x < img.length; x++)
    {
      for (int y = 0; y < img[0].length; y++)
      {

        // Set all positive level sets with a unique positive number
        if (img[x][y] >= threshold)
          outputArray[x][y] = white;
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
            4 * byteArray[x][y] - byteArray[x][y - 1] - byteArray[x][y + 1] - byteArray[x - 1][y]
                - byteArray[x + 1][y];
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

}
