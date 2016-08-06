package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MetricsTest
{
	@Test
	public void testCalculate_Single()
	{
		boolean[][] result = { {true, true}, {false, false}, {true, false}};
		boolean[][] groundTruth = { {true, false}, {false, true}, {false, false}};

		Metrics metrics = new Metrics(result, groundTruth);

		assertEquals("", 1, metrics.getTP(), 0.001);
		assertEquals("", 1, metrics.getFN(), 0.001);
		assertEquals("", 2, metrics.getTN(), 0.001);
		assertEquals("", 2, metrics.getFP(), 0.001);
	}

	@Test
	public void testGetConformity()
	{
		Metrics metrics = new Metrics(new boolean[][] {}, new boolean[][] {});
		metrics.setTP(0);
		metrics.setTN(1);
		metrics.setFP(1);
		metrics.setFN(0);

		assertEquals("", 0, metrics.getJaccard(), 000001);
		assertEquals("", 0, metrics.getDice(), 000001);
		assertTrue("", Double.isInfinite(metrics.getConformity()));
		assertTrue("", Double.isNaN(metrics.getSensitivity()));
		assertTrue("", Double.isInfinite(metrics.getFPRate()));

	}

	@Test
	public void testCalculate_Multi()
	{
		final List<boolean[][]> resultList = new ArrayList<boolean[][]>();
		boolean[][] result1 =
				new boolean[][] { {false, true, false}, {false, true, true}, {false, false, false}};
		boolean[][] result2 =
				new boolean[][] { {false, true, false}, {true, true, false}, {false, false, false}};
		boolean[][] result3 =
				new boolean[][] { {false, true, true}, {false, true, true}, {false, true, true}};

		resultList.add(result1);
		resultList.add(result2);
		resultList.add(result3);


		final List<boolean[][]> groundTruthList = new ArrayList<boolean[][]>();

		boolean[][] groundTruth1 =
				new boolean[][] { {true, true, false}, {true, true, false}, {false, false, false}};
		boolean[][] groundTruth2 =
				new boolean[][] { {true, true, true}, {true, false, true}, {false, false, false}};
		boolean[][] groundTruth3 =
				new boolean[][] { {true, true, false}, {true, true, false}, {false, false, false}};

		groundTruthList.add(groundTruth1);
		groundTruthList.add(groundTruth2);
		groundTruthList.add(groundTruth3);

		final Metrics metrics = new Metrics(resultList, groundTruthList);

		assertEquals("", 6, metrics.getTP(), 0.0001);
		assertEquals("", 8, metrics.getTN(), 0.0001);
		assertEquals("", 6, metrics.getFP(), 0.0001);
		assertEquals("", 7, metrics.getFN(), 0.0001);

		assertEquals("", 6.0 / (6 + 6 + 7), metrics.getJaccard(), 0.0001);
		assertEquals("", 1 - (6 + 7) / 6.0, metrics.getConformity(), 0.0001);

	}

}
