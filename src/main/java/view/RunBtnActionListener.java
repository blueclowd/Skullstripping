package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import controller.DataManager;

/**
 * ActionListener of JButton "Run"
 * 
 * @author vincentliu
 *
 */
public class RunBtnActionListener implements ActionListener
{

	private DataManager dataManager;

	private CustomWindow customWindow;

	private String fileName;

	private String filePath;

	private List<String> gtFileNameList;

	private List<String> gtFilePathList;

	public RunBtnActionListener(DataManager dataManager, CustomWindow customWindow, String fileName, String filePath,
			List<String> gtFileNameList, List<String> gtFilePathList)
	{
		this.dataManager = dataManager;
		this.customWindow = customWindow;

		this.fileName = fileName;
		this.filePath = filePath;
		this.gtFileNameList = gtFileNameList;
		this.gtFilePathList = gtFilePathList;

	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{

		customWindow.appendLog(" Click Run Button");

		// Disable button "Generate Report" for the second time press
		customWindow.enableGenerateReportBtn(false);

		final EvolveWorker evolveWorker = new EvolveWorker(dataManager, customWindow, fileName, filePath,
				gtFileNameList, gtFilePathList);
		evolveWorker.execute();

	}

}