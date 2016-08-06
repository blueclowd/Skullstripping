package texture;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.Map;

import org.junit.Test;

public class GLCMTest
{

	@Test
	public void testGLCM()
	{
		final double[][] testImg =
				new double[][] {

						{7.5419, 1.0241, 7.8531, 1.8095, 2.7510, 2.5018, 0.2688, 2.6012, 4.2171,
								5.3745},
						{3.3420, 7.9926, 1.2512, 3.0770, 4.6726, 1.2919, 0.5504, 0.8450, 3.6594,
								5.5611},
						{7.8644, 1.3690, 6.8442, 4.6639, 0.8622, 1.4301, 2.5568, 4.8877, 7.0030,
								0.5439},
						{2.4116, 0.2608, 5.1581, 2.0144, 7.2505, 3.3831, 4.2469, 6.2304, 4.1444,
								2.0383},
						{5.6088, 4.4896, 3.0102, 2.3235, 7.0372, 0.7538, 5.2356, 3.3876, 7.5490,
								1.7923},
						{5.3307, 7.0549, 1.5274, 4.9367, 6.5421, 4.7882, 3.2610, 0.7266, 5.1017,
								5.3427},
						{4.3130, 5.3534, 3.4260, 2.1222, 2.0858, 3.7674, 6.5598, 2.1318, 7.6616,
								6.7551},
						{5.5848, 1.5235, 3.8562, 6.5950, 4.7549, 5.5676, 5.7469, 1.2293, 1.9257,
								2.7557},
						{5.3322, 2.9513, 0.9649, 7.8613, 0.1801, 5.5991, 7.7492, 2.2480, 5.4090,
								6.2442},
						{1.4251, 3.6858, 4.7161, 5.8420, 3.4021, 5.1082, 4.2507, 3.5207, 2.3125,
								5.4027}};

		GLCM glcm = new GLCM(testImg, 8, 1, 0, 0, 7, 4);

		Map<Point, double[][]> glcmMap = glcm.getGLCMMap();

		double[][] glcmMat00 = glcmMap.get(new Point(0, 0));

		assertEquals(0, glcmMat00[0][0], 0.0001);
		assertEquals(1.0 / 12, glcmMat00[0][4], 0.0001);
		assertEquals(4.0 / 12, glcmMat00[7][1], 0.0001);

		final GLCMFeatures glcmFeatures = glcm.getGLCMFeatures();
		final double[][] energy = glcmFeatures.getEnergy();
		final double[][] contrast = glcmFeatures.getContrast();
		final double[][] entropy = glcmFeatures.getEntropy();
		final double[][] homogenity = glcmFeatures.getHomogenity();


		double entropyTemp = 0;
		double energyTemp = 0;
		double contrastTemp = 0;
		double homogenityTemp = 0;

		for (int y = 0; y < glcmMat00.length; y++)
		{
			for (int x = 0; x < glcmMat00[0].length; x++)
			{
				entropyTemp -= glcmMat00[y][x] * Math.log(glcmMat00[y][x]);
				energyTemp += glcmMat00[y][x] * glcmMat00[y][x];
				contrastTemp += (y - x) * (y - x) * glcmMat00[y][x];
				homogenityTemp += glcmMat00[y][x] / (1 + (y - x) * (y - x));
			}
		}

		assertEquals(energyTemp, energy[0][0], 0.0001);
		assertEquals(entropyTemp, entropy[0][0], 0.0001);
		assertEquals(contrastTemp, contrast[0][0], 0.0001);
		assertEquals(homogenityTemp, homogenity[0][0], 0.0001);

	}

}
