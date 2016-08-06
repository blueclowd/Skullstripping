package view;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import controller.DataManager;
import ij.IJ;
import model.SegmentResult;
import model.UiOptions;
import utils.LSConstants.ContourColor;
import utils.LSConstants.LevelsetType;

/**
 * The worker class aims to run the levelset evolvement in another thread.
 * 
 * @author Vincent Liu
 *
 */
public class EvolveWorker extends SwingWorker
{
	private DataManager dataManager;

	private CustomWindow customWindow;

	private String fileName;

	private String filePath;

	private List<String> gtFileNameList;

	public EvolveWorker(DataManager dataManager, CustomWindow customWindow, String fileName, String filePath,
			List<String> gtFileNameList, List<String> gtFilePathList)
	{
		this.dataManager = dataManager;
		this.customWindow = customWindow;
		this.fileName = fileName;
		this.filePath = filePath;
		this.gtFileNameList = gtFileNameList;
	}

	@Override
	protected SegmentResult doInBackground() throws Exception
	{

		final UiOptions uiOpts = new UiOptions();
		uiOpts.setVelocity(Double.parseDouble(customWindow.getVelocity()));
		uiOpts.setTextureCoefficient(Double.parseDouble(customWindow.getTextureCoefficient().getText()));
		uiOpts.setThreshold(Double.parseDouble(customWindow.getThreshold()));
		uiOpts.setHeading(Integer.parseInt(customWindow.getHeading()));
		uiOpts.setLevelsetType(
				customWindow.getModelBasedBtn().isSelected() ? LevelsetType.Model : LevelsetType.Texture);
		uiOpts.setHoleFilling(customWindow.getFillingHoles().isSelected());
		uiOpts.setContourColor((ContourColor) customWindow.getContourColor().getSelectedItem());

		customWindow.appendLog(" - Velocity = " + uiOpts.getVelocity() + "\n - TextureCoefficient = "
				+ uiOpts.getTextureCoefficient() + "\n - Threhold = " + uiOpts.getThreshold() + "\n - Heading = "
				+ uiOpts.getHeading() + "\n - Contour color = " + uiOpts.getContourColor());

		dataManager.setUiOptions(uiOpts);

		dataManager.loadMetadata(filePath);

		dataManager.createSkullStripper(filePath);

		dataManager.initZeroLS();

		final SegmentResult segmentResult = dataManager.evolveVolume();

		final SegmentResult segmentResultWithMetrics = dataManager.evaluateMetrics(segmentResult);

		return segmentResultWithMetrics;
	}

	@Override
	protected void done()
	{
		try
		{
			final SegmentResult segmentResultWithMetrics = (SegmentResult) get();

			// After result evaluation, enable button "Generate Report"
			customWindow.appendLog(" Enable button \"Generate Report\"");
			dataManager.enableGenerateReportBtn(segmentResultWithMetrics, fileName, gtFileNameList);

		} catch (InterruptedException e)
		{
			IJ.log(e.getMessage());
		} catch (ExecutionException e)
		{
			IJ.log(e.getMessage());
		}
	}

}
