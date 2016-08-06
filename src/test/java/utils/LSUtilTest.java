package utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class LSUtilTest
{

	@Test
	public void testConvertDoubleToStr()
	{
		double number = 6.59976827116368E-4;

		String result = LSUtil.convertDoubleToStr(number);

		assertEquals("", "0.0007", result);

	}

	@Test
	public void testCombineGroundTruth()
	{
		List<String> pathList = new ArrayList<String>();
		pathList.add("/home/vincentliu/Desktop/Images/IBSR/10New/IBSR_06/segmentation/MINC/IBSR_06_seg_ana.hdr.mnc");

		ImagePlus gtImgPlus = LSUtil.combineGroundTruth(pathList);
		gtImgPlus.setSlice(40);

		ImageProcessor imgProcessor = gtImgPlus.getProcessor();

		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;

		for (int x = 0; x < imgProcessor.getWidth(); x++)
		{
			for (int y = 0; y < imgProcessor.getHeight(); y++)
			{
				int value = imgProcessor.get(x, y);
				if (value > max)
				{
					max = value;
				}

				if (value < min)
				{
					min = value;
				}
			}
		}

		assertEquals("", 65535, max);
		assertEquals("", 0, min);
	}
}
