package model;

import ij.IJ;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.collections.CollectionUtils;

import utils.LSConstants;

/**
 * The class Measurement provides the value of metrics.
 * 
 * @author Vincent Liu
 * 
 */
public class Metrics
{

	/**
	 * Mask of segmentation result
	 */
	private List<boolean[][]> resultList;

	/**
	 * Mask of ground truth
	 */
	private List<boolean[][]> groundTruthList;

	// Double for metrics calculation
	private double TP;
	private double TN;
	private double FP;
	private double FN;

	/**
	 * Multiple slices
	 * 
	 * @param resultList
	 * @param groundTruthList
	 */
	public Metrics(List<boolean[][]> resultList, List<boolean[][]> groundTruthList)
	{
		this.resultList = resultList;
		this.groundTruthList = groundTruthList;

		checkOverflow(resultList);

		calculate();
	}

	/**
	 * Single slice
	 * 
	 * @param result
	 * @param groundTruth
	 */
	public Metrics(boolean[][] result, boolean[][] groundTruth)
	{
		final List<boolean[][]> resultList = new ArrayList<boolean[][]>();
		resultList.add(result);

		final List<boolean[][]> groundTruthList = new ArrayList<boolean[][]>();
		groundTruthList.add(groundTruth);

		this.resultList = resultList;
		this.groundTruthList = groundTruthList;

		checkOverflow(resultList);

		calculate();
	}

	/**
	 * Check if overflow is possible based on the number of pixels.
	 * 
	 * @param boolList
	 */
	private void checkOverflow(List<boolean[][]> boolList)
	{
		if (CollectionUtils.isEmpty(boolList))
		{
			throw new IllegalArgumentException(LSConstants.ERROR_RESULT_EMPTY);
		} else
		{
			final boolean[][] firstResult = boolList.get(0);

			if (Double.MAX_VALUE / boolList.size() < firstResult.length * firstResult[0].length)
			{
				JOptionPane.showMessageDialog(null, LSConstants.WARN_OVERFLOW);
			}
		}
	}

	/**
	 * Calculate TP, TN, FP and FN from result and groundTruth
	 */
	public void calculate()
	{
		this.setTP(0);
		this.setTN(0);
		this.setFP(0);
		this.setFN(0);

		for (int sliceNo = 0; sliceNo < resultList.size(); sliceNo++)
		{
			final boolean[][] result = resultList.get(sliceNo);
			final boolean[][] groundTruth = groundTruthList.get(sliceNo);

			for (int x = 0; x < result.length; x++)
			{
				for (int y = 0; y < result[0].length; y++)
				{
					boolean valid1 = result[x][y];
					boolean valid2 = groundTruth[x][y];

					if (valid1 && valid2)
					{
						TP++;
					} else if (valid1 && !valid2)
					{
						FP++;
					} else if (!valid1 && valid2)
					{
						FN++;
					} else
					{
						TN++;
					}

				}
			}

		}

		IJ.log("TP = " + getTP());
		IJ.log("TN = " + getTN());
		IJ.log("FP = " + getFP());
		IJ.log("FN = " + getFN());

	}

	public double getTP()
	{
		return TP;
	}

	public void setTP(double tP)
	{
		TP = tP;
	}

	public double getTN()
	{
		return TN;
	}

	public void setTN(double tN)
	{
		TN = tN;
	}

	public double getFP()
	{
		return FP;
	}

	public void setFP(double fP)
	{
		FP = fP;
	}

	public double getFN()
	{
		return FN;
	}

	public void setFN(double fN)
	{
		FN = fN;
	}

	public double getJaccard()
	{
		return TP / (TP + FP + FN);
	}

	public double getDice()
	{
		return 2 * TP / (2 * TP + FP + FN);
	}

	public double getConformity()
	{
		return 1 - (FP + FN) / TP;
	}

	public double getFPRate()
	{
		return FP / (TP + FN);
	}

	public double getSensitivity()
	{
		return TP / (TP + FN);
	}

	public double getSpecificity()
	{
		return TN / (TN + FP);
	}

	public List<boolean[][]> getResultList()
	{
		return resultList;
	}

	public void setResultList(List<boolean[][]> resultList)
	{
		this.resultList = resultList;
	}

	public List<boolean[][]> getGroundTruthList()
	{
		return groundTruthList;
	}

	public void setGroundTruthList(List<boolean[][]> groundTruthList)
	{
		this.groundTruthList = groundTruthList;
	}
}
