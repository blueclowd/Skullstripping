package model;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Ui options
 * 
 * @author liu
 * 
 */
public class UiOptions
{

  private double velocity = 0.05 * 255 * 255;

  private double threshold = 0.5;

  private int heading = 0;

  private boolean isHoleFilling = false;

  private String outputMaskFile;

  private String outputBrainOnlyFile;

  private String contourColor;

  private Map<String, Color> colorMap = new LinkedHashMap<String, Color>();

  public UiOptions()
  {
    colorMap.put("Yellow", Color.yellow);
    colorMap.put("Green", Color.green);
    colorMap.put("Magenta", Color.magenta);
  }

  public double getVelocity()
  {
    return velocity;
  }

  public void setVelocity(double velocity)
  {
    this.velocity = velocity;
  }

  public double getThreshold()
  {
    return threshold;
  }

  public void setThreshold(double threshold)
  {
    this.threshold = threshold;
  }

  public int getHeading()
  {
    return heading;
  }

  public void setHeading(int heading)
  {
    this.heading = heading;
  }

  public boolean isHoleFilling()
  {
    return isHoleFilling;
  }

  public void setHoleFilling(boolean isHoleFilling)
  {
    this.isHoleFilling = isHoleFilling;
  }

  public String getOutputMaskFile()
  {
    return outputMaskFile;
  }

  public void setOutputMaskFile(String outputMaskFile)
  {
    this.outputMaskFile = outputMaskFile;
  }

  public String getOutputBrainOnlyFile()
  {
    return outputBrainOnlyFile;
  }

  public void setOutputBrainOnlyFile(String outputBrainOnlyFile)
  {
    this.outputBrainOnlyFile = outputBrainOnlyFile;
  }

  public Color getContourColor()
  {
    return colorMap.get(contourColor);
  }

  public void setContourColor(String contourColor)
  {
    this.contourColor = contourColor;
  }
}
