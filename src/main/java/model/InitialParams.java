package model;

/**
 * Information about initial slice
 * 
 * @author Vincent Liu
 *
 */
public class InitialParams
{

	private double centerX;

	private double centerY;

	private int initialIdx;

	private double radius;

	public double getCenterX()
	{
		return centerX;
	}

	public void setCenterX(double centerX)
	{
		this.centerX = centerX;
	}

	public double getCenterY()
	{
		return centerY;
	}

	public void setCenterY(double centerY)
	{
		this.centerY = centerY;
	}

	public int getInitialIdx()
	{
		return initialIdx;
	}

	public void setInitialIdx(int initialIdx)
	{
		this.initialIdx = initialIdx;
	}

	public double getRadius()
	{
		return radius;
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	@Override
	public String toString()
	{
		return "InitialParams [centerX=" + centerX + ", centerY=" + centerY + ", initialIdx=" + initialIdx + ", radius="
				+ radius + "]";
	}

}
