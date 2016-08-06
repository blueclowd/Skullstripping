package model;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Information about the contour evolvement
 * 
 * @author Vincent Liu
 * 
 */
public class SegmentResult
{

	private Metrics metrics;

	private long evolveTime;

	private BufferedImage originImg;

	private List<double[][]> phiList;

	private BufferedImage resultImg;

	public Metrics getMetrics()
	{
		return metrics;
	}

	public void setMetrics(Metrics metrics)
	{
		this.metrics = metrics;
	}

	public long getEvolveTime()
	{
		return evolveTime;
	}

	public void setEvolveTime(long evolveTime)
	{
		this.evolveTime = evolveTime;
	}

	public BufferedImage getOriginImg()
	{
		return originImg;
	}

	public void setOriginImg(BufferedImage originImg)
	{
		this.originImg = originImg;
	}

	public List<double[][]> getPhiList()
	{
		return phiList;
	}

	public void setPhiList(List<double[][]> phiList)
	{
		this.phiList = phiList;
	}

	public BufferedImage getResultImg()
	{
		return resultImg;
	}

	public void setResultImg(BufferedImage resultImg)
	{
		this.resultImg = resultImg;
	}

	/**
	 * Only for ReportMill reflection
	 * 
	 * @return
	 */
	public double getJaccard()
	{
		if (metrics == null)
		{
			return Double.NaN;
		} else
		{
			return metrics.getJaccard();
		}

	}

	/**
	 * Only for ReportMill reflection
	 * 
	 * @return
	 */
	public double getDice()
	{

		if (metrics == null)
		{
			return Double.NaN;
		} else
		{
			return metrics.getDice();
		}

	}

	/**
	 * Only for ReportMill reflection
	 * 
	 * @return
	 */
	public double getConformity()
	{

		if (metrics == null)
		{
			return Double.NaN;
		} else
		{
			return metrics.getConformity();
		}

	}

	/**
	 * Only for ReportMill reflection
	 * 
	 * @return
	 */
	public double getSensitivity()
	{

		if (metrics == null)
		{
			return Double.NaN;
		} else
		{
			return metrics.getSensitivity();
		}

	}

	/**
	 * Only for ReportMill reflection
	 * 
	 * @return
	 */
	public double getSpecificity()
	{

		if (metrics == null)
		{
			return Double.NaN;
		} else
		{
			return metrics.getSpecificity();
		}

	}

	/**
	 * Only for ReportMill reflection
	 * 
	 * @return
	 */
	public double getFPRate()
	{

		if (metrics == null)
		{
			return Double.NaN;
		} else
		{
			return metrics.getFPRate();
		}
	}

}
