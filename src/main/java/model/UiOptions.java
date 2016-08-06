package model;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import utils.LSConstants.ContourColor;
import utils.LSConstants.LevelsetType;

/**
 * UI options
 * 
 * @author Vincent Liu
 * 
 */
public class UiOptions
{
	private double textureCoefficient = 0.1;

	private double velocity = 0.05 * 255 * 255;

	private double threshold = 0.5;

	private int heading = 0;

	private boolean isHoleFilling = false;

	private String outputMaskFile;

	private String outputBrainOnlyFile;

	private ContourColor contourColor;

	private LevelsetType levelsetType = LevelsetType.Model;

	private Map<ContourColor, Color> colorMap = new LinkedHashMap<ContourColor, Color>();

	public UiOptions()
	{
		colorMap.put(ContourColor.Yellow, Color.yellow);
		colorMap.put(ContourColor.Green, Color.green);
		colorMap.put(ContourColor.Magenta, Color.magenta);
		colorMap.put(ContourColor.White, Color.white);
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

	public void setContourColor(ContourColor contourColor)
	{
		this.contourColor = contourColor;
	}

	public LevelsetType getLevelsetType()
	{
		return levelsetType;
	}

	public void setLevelsetType(LevelsetType levelsetType)
	{
		this.levelsetType = levelsetType;
	}

	public double getTextureCoefficient()
	{
		return textureCoefficient;
	}

	public void setTextureCoefficient(double textureCoefficient)
	{
		this.textureCoefficient = textureCoefficient;
	}
}
