import static org.junit.Assert.assertEquals;
import ij.ImagePlus;
import ij.gui.Overlay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import utils.LSUtil;



public class LSUtilTest
{


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

	@Test
	public void testReadImage()
	{
		String filePath = "/home/liu/Documents/MedicalImg/IBSR01.mnc";

		List<BufferedImage> bufferImageList = LSUtil.readImage(filePath);

		assertEquals("", 128, bufferImageList.size());

	}

	@Test
	public void testConvertDoubleToStr()
	{
		assertEquals("", "NaN", LSUtil.convertDoubleToStr(Double.NaN));

		assertEquals("", "0", LSUtil.convertDoubleToStr(0.0));

		assertEquals("", "2", LSUtil.convertDoubleToStr(2));

	}

	@Test
	public void testFlipImageV()
	{
		final BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);
		final BufferedImage flipImg = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);

		img.getRaster().setSample(0, 0, 0, 1);
		img.getRaster().setSample(0, 1, 0, 2);

		img.getRaster().setSample(1, 1, 0, 3);
		img.getRaster().setSample(1, 0, 0, 4);

		LSUtil.flipImageV(img, flipImg);

		// Check no change on original image
		assertEquals("", 1, img.getRaster().getSample(0, 0, 0));
		assertEquals("", 2, img.getRaster().getSample(0, 1, 0));
		assertEquals("", 3, img.getRaster().getSample(1, 1, 0));
		assertEquals("", 4, img.getRaster().getSample(1, 0, 0));

		// Check flip on result image
		assertEquals("", 2, flipImg.getRaster().getSample(0, 0, 0));
		assertEquals("", 1, flipImg.getRaster().getSample(0, 1, 0));
		assertEquals("", 4, flipImg.getRaster().getSample(1, 1, 0));
		assertEquals("", 3, flipImg.getRaster().getSample(1, 0, 0));

	}

	@Test
	public void testAddDouble()
	{
		double[][] img1 = { {2, -1}, {0, 9}};
		double[][] img2 = { {0, 4}, {-5, 9}};

		double[][] sum = LSUtil.addDouble(img1, img2, 1, 2);

		assertEquals("", 2, sum[0][0], 0.00001);
		assertEquals("", 7, sum[0][1], 0.00001);
		assertEquals("", -10, sum[1][0], 0.00001);
		assertEquals("", 27, sum[1][1], 0.00001);
	}

	@Test
	public void testCreateShortAry()
	{
		final BufferedImage bufferImage = new BufferedImage(2, 2, BufferedImage.TYPE_USHORT_GRAY);
		final WritableRaster raster = bufferImage.getRaster();

		raster.setSample(0, 0, 0, 300);
		raster.setSample(0, 1, 0, 32904);
		raster.setSample(1, 0, 0, 10);
		raster.setSample(1, 1, 0, 0);



		short[][] ary = LSUtil.createShortAry(bufferImage);

		assertEquals("", 300, ary[0][0]);
		assertEquals("", -32632, ary[0][1]);
	}

	@Test
	public void testGetMaxSeries()
	{
		List<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();

		BufferedImage img1 = new BufferedImage(2, 2, BufferedImage.TYPE_USHORT_GRAY);
		WritableRaster raster1 = img1.getRaster();

		raster1.setSample(0, 0, 0, 5);
		raster1.setSample(0, 1, 0, 785);
		raster1.setSample(1, 0, 0, 5);
		raster1.setSample(1, 1, 0, 2005);

		BufferedImage img2 = new BufferedImage(2, 2, BufferedImage.TYPE_USHORT_GRAY);
		WritableRaster raster2 = img2.getRaster();

		raster2.setSample(0, 0, 0, 5);
		raster2.setSample(0, 1, 0, 40);
		raster2.setSample(1, 0, 0, 665);
		raster2.setSample(1, 1, 0, 25);

		bufferedImages.add(img1);
		bufferedImages.add(img2);


		double[] maxSeries = LSUtil.getMaxSeries(bufferedImages);

		assertEquals("", 2, maxSeries.length);
		assertEquals("", 2005, maxSeries[0], 0.0001);
		assertEquals("", 665, maxSeries[1], 0.0001);
	}

	@Test
	public void testExportBufferedImage()
	{
		BufferedImage bufferedImage = new BufferedImage(2, 2, BufferedImage.TYPE_USHORT_GRAY);

		LSUtil.exportBufferedImage(bufferedImage, "test");

	}

	@Test
	public void testRescaleImage()
	{

		BufferedImage bufferedImage = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster raster = bufferedImage.getRaster();

		raster.setSample(0, 0, 0, 20);
		raster.setSample(0, 1, 0, 10);
		raster.setSample(1, 0, 0, 15);
		raster.setSample(1, 1, 0, 5);

		BufferedImage rescaledImage = LSUtil.rescaleImage(bufferedImage, 0, 3);

		assertEquals("", 3, rescaledImage.getRaster().getSample(0, 0, 0));
		assertEquals("", 1, rescaledImage.getRaster().getSample(0, 1, 0));
		assertEquals("", 2, rescaledImage.getRaster().getSample(1, 0, 0));
		assertEquals("", 0, rescaledImage.getRaster().getSample(1, 1, 0));

	}

}
