package texture;

public class GLCMFeatures
{
	private double[][] contrast;
	private double[][] homogenity;
	private double[][] entropy;
	private double[][] energy;

	public GLCMFeatures()
	{
		// TODO Auto-generated constructor stub
	}

	public double[][] getContrast()
	{
		return contrast;
	}

	public void setContrast(double[][] contrast)
	{
		this.contrast = contrast;
	}

	public double[][] getHomogenity()
	{
		return homogenity;
	}

	public void setHomogenity(double[][] homogenity)
	{
		this.homogenity = homogenity;
	}

	public double[][] getEntropy()
	{
		return entropy;
	}

	public void setEntropy(double[][] entropy)
	{
		this.entropy = entropy;
	}

	public double[][] getEnergy()
	{
		return energy;
	}

	public void setEnergy(double[][] energy)
	{
		this.energy = energy;
	}

}
