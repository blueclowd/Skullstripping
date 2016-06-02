package model;


/*
 * The class Measurement provides the value of metrics.
 */
public class Metrics
{

  private boolean[][] result;
  private boolean[][] groundTruth;

  private double TP;
  private double TN;
  private double FP;
  private double FN;

  public Metrics(boolean[][] result, boolean[][] groundTruth)
  {
    this.result = result;
    this.groundTruth = groundTruth;
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

  public boolean[][] getResult()
  {
    return result;
  }

  public void setResult(boolean[][] result)
  {
    this.result = result;
  }

  public boolean[][] getGroundTruth()
  {
    return groundTruth;
  }

  public void setGroundTruth(boolean[][] groundTruth)
  {
    this.groundTruth = groundTruth;
  }
}
