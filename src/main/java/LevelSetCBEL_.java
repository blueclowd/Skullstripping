import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.OpenDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import utils.LSConstants;
import utils.LSUtil;
import view.CustomWindow;
import view.RunBtnActionListener;
import controller.DataManager;

/**
 * Entry class for ImageJ plugin
 * 
 * @author Vincent Liu
 * 
 */
public class LevelSetCBEL_ implements PlugInFilter
{

	private String filePath;
	private String fileName;

	private List<String> gtFileNameList;
	private List<String> gtFilePathList;

	private ImagePlus imgPlus;
	private ImagePlus gtImgPlus;

	@Override
	public int setup(String arg, ImagePlus imgPlus)
	{

		this.imgPlus = imgPlus;

		fileName = imgPlus.getOriginalFileInfo().fileName;
		filePath = imgPlus.getOriginalFileInfo().directory + fileName;

		IJ.log("File Path = " + filePath);

		// Open dialog for ground truth selection, there might be multiple
		// ground truths for different anatomical structure.
		gtFileNameList = new ArrayList<String>();
		gtFilePathList = new ArrayList<String>();

		while (true)
		{
			final OpenDialog gtDialog = new OpenDialog("Open ground truth image...");
			gtFileNameList.add(gtDialog.getFileName());
			gtFilePathList.add(gtDialog.getDirectory() + gtDialog.getFileName());
			IJ.log("GT File Path = " + gtDialog.getDirectory() + gtDialog.getFileName());

			int selected =
					JOptionPane.showConfirmDialog(null, LSConstants.GROUND_TRUTH_SELECTION_QUES,
							LSConstants.GROUND_TRUTH_SELECTION_TITLE, JOptionPane.YES_NO_OPTION);

			if (JOptionPane.YES_OPTION == selected)
			{
				IJ.log("Finish loading ground truth");
				break;
			}

		}

		gtImgPlus = LSUtil.combineGroundTruth(gtFilePathList);

		return DOES_ALL;
	}

	/**
	 * The run method receives the image processor of the image and performs the actual function of
	 * the plugin
	 */
	@Override
	public void run(ImageProcessor ip)
	{

		IJ.log("run()...");

		// Close default window by ImageJ
		WindowManager.removeWindow(WindowManager.getCurrentWindow());

		final DataManager dataManager =
				new DataManager(filePath, gtImgPlus, LSConstants.RESULT_DIRECTORY);

		final CustomWindow customWindow =
				new CustomWindow(LSUtil.readImage(filePath, true), gtImgPlus,
						imgPlus.getStackSize() / 3, fileName);

		// Set action listeners
		customWindow.addRunBtnActionListener(new RunBtnActionListener(dataManager, customWindow,
				fileName, filePath, gtFileNameList, gtFilePathList));

		dataManager.setCustomWindow(customWindow);

	}

}
