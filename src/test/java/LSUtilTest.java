import static org.junit.Assert.assertEquals;
import ij.ImagePlus;
import ij.gui.Overlay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import utils.LSUtil;



public class LSUtilTest
{

  @Test
  public void testGetFile()
  {
    File f = new File("src/main/resources/16.jpg");

    System.out.println(f.exists());
  }

  @Test
  public void testCopyArray()
  {
    double[][] ary1 = { {1, 2}, {3, 4}};

    double[][] ary2 = Arrays.copyOf(ary1, 4);

    System.out.println(ary2[0][0] + " " + ary2[0][1] + " " + ary2[1][0] + " " + ary2[1][1]);
  }

  @Test
  public void testCreateImgPlus()
  {
    double[][] vals = { {0, 2, 3, 4, 5, 6}, {0, 3, 2, 1, 5, 6}};

    ImagePlus imgPlus = LSUtil.createImgPlus(vals, "Test Title");

    assertEquals("", 0, imgPlus.getPixel(0, 0)[0]);
    assertEquals("", 2, imgPlus.getPixel(0, 1)[0]);
    assertEquals("", 3, imgPlus.getPixel(0, 2)[0]);
    assertEquals("", 4, imgPlus.getPixel(0, 3)[0]);

  }

  @Test
  public void testBinarizeDouble()
  {
    double[][] input = { {4, 3}, {0, 1}};

    double[][] result = LSUtil.binarizeDouble(input, 1);

    System.out.println(result[0][0]);
    System.out.println(result[0][1]);
    System.out.println(result[1][0]);

    System.out.println(result[1][1]);
  }

  @Test
  public void testExportMatFile()
  {

    double[][] src2D = { {-1.0, 2.0, 3.0}, {1.0, -2.0, 3.0}, {99.1, 92.3, 40.1}};

    LSUtil.exportMatFile(src2D, "test");


  }

  @Test
  public void testCreateContourByMask()
  {
    byte[][] mask = { {0, 40, 40, 0}, {0, 40, 40, 0}, {0, 40, 40, 40}, {0, 40, 40, 40}};

    Overlay overlay = LSUtil.createContourByMask(mask, Color.yellow);

    byte b = 11;

    System.out.println(b == 11);

  }

  @Test
  public void testExportByteArray()
  {
    byte[][] array =
        { {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {40, 40, 40, 40},
            {40, 40, 40, 40}, {40, 40, 40, 40}};

    LSUtil.exportAry(array, "Test_ExportByteArray");

    double[][] doubleAry = { {0.0, 0.0, 10.0, 10.0}, {100.0, 100.0, 10.0, 10.0}};

    LSUtil.exportAry(doubleAry, "Test_ExportDoubleAry");

  }

  @Test
  public void testCopy2DAry()
  {
    double[][] input = { {1.0, 3.0}, {5.0, 7.0}};

    double[][] copied = LSUtil.copy2DAry(input);

    assertEquals(input[0][0], copied[0][0], 0.0000001);
    assertEquals(input[0][1], copied[0][1], 0.0000001);
    assertEquals(input[1][0], copied[1][0], 0.0000001);
    assertEquals(input[1][1], copied[1][1], 0.0000001);

    // Test if it input and copied are distinct.
    input[0][0] = 9.0;

    assertEquals(1.0, copied[0][0], 0.0000001);
  }

  @Test
  public void testConvertToBinaryAry()
  {
    double[][] img = { {2.0, 3.0, -0.2}, {-0.5, -1.2, -33}};

    byte[][] result = LSUtil.convertToBinaryAry(img, -0.5d, (byte) 40);

    assertEquals((byte) 40, result[0][0]);
    assertEquals((byte) 40, result[0][1]);
    assertEquals((byte) 40, result[0][2]);
    assertEquals((byte) 40, result[1][0]);
    assertEquals((byte) 0, result[1][1]);
    assertEquals((byte) 0, result[1][2]);

  }

  @Test
  public void testGetMax()
  {
    BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);

    image.getRaster().setSample(0, 0, 0, 1);
    image.getRaster().setSample(0, 1, 0, 2);
    image.getRaster().setSample(1, 0, 0, 9);
    image.getRaster().setSample(1, 1, 0, 0);

    assertEquals("", 9, LSUtil.getMax(image));

  }

  @Test
  public void testGetMin()
  {
    BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);

    image.getRaster().setSample(0, 0, 0, 1);
    image.getRaster().setSample(0, 1, 0, 2);
    image.getRaster().setSample(1, 0, 0, 9);
    image.getRaster().setSample(1, 1, 0, 0);

    assertEquals("", 0, LSUtil.getMin(image));

  }


}
