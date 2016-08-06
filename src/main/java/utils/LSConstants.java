package utils;

/**
 * Constants of levelset
 * 
 * @author Vincent Liu
 * 
 */
public class LSConstants
{

	public enum NSWE
	{
		North, South, West, East;
	}

	public enum LevelsetType
	{
		Model("Model"), Texture("Texture");

		String typeName;

		private LevelsetType(String typeName)
		{
			this.typeName = typeName;
		}
	}

	public enum MedImageFormat
	{
		MincMRI("MINC MRI"), Analyze75("Analyze 7.5");

		String formatName;

		private MedImageFormat(String formatName)
		{
			this.formatName = formatName;
		}
	}

	public enum ContourColor
	{
		Yellow("Yellow"), White("White"), Green("Green"), Magenta("Magenta");

		String colorName;

		private ContourColor(String colorName)
		{
			this.colorName = colorName;
		}
	}

	public static final String ERROR_TRANSFER_TO_IMAGEREADER = "Transfer to ImageReader error.";

	public static final String ERROR_GROUND_TRUTH_NOT_FOUND = "Ground truth not found error";

	public static final String ERROR_OPEN_IMAGE = "Open image error";

	public static final String ERROR_CREATE_RESULT_DIR = "Create result directory error";

	public static final String ERROR_UNACCEPTABLE_IMAGE_TYPE = "Unacceptable image type error";

	public static final String AUTHORITY =
			"Copyright © 2016 Computational Biomedical Engineering Laboratory (CBEL)";

	public static final String GROUND_TRUTH_SELECTION_TITLE = "Ground Truth Selection";
	public static final String GROUND_TRUTH_SELECTION_QUES = "Finish loading ground truth ?";

	public static final String RESULT_DIRECTORY = "LsResult";

	public static final String ERROR_RESULT_EMPTY = "Result list is empty";
	public static final String WARN_OVERFLOW = "The evaluation might be incorrect due to overflow";
}
