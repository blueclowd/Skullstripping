package model;

import java.awt.image.BufferedImage;

public class SegmentResult
{

  private int sliceNo;

  private Metrics metrics;

  private long evolveTime;

  private int iterCnt;

  private BufferedImage originImg;

  private double[][] phi;

  private BufferedImage resultImg;


  public SegmentResult(int sliceNo)
  {
    this.sliceNo = sliceNo;
  }

  public int getSliceNo()
  {
    return sliceNo;
  }

  public void setSliceNo(int sliceNo)
  {
    this.sliceNo = sliceNo;
  }

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

  public int getIterCnt()
  {
    return iterCnt;
  }

  public void setIterCnt(int iterCnt)
  {
    this.iterCnt = iterCnt;
  }

  public BufferedImage getOriginImg()
  {
    return originImg;
  }

  public void setOriginImg(BufferedImage originImg)
  {
    this.originImg = originImg;
  }

  public double[][] getPhi()
  {
    return phi;
  }

  public void setPhi(double[][] phi)
  {
    this.phi = phi;
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
