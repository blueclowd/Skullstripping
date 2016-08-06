package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.Report;
import model.SegmentResult;

public class GenerateReportBtnActionListener implements ActionListener
{
	private CustomWindow customWindow;

	private List<String> gtFileNameList;

	private String originFileName;

	private SegmentResult segmentResult;

	public GenerateReportBtnActionListener()
	{

	}

	public GenerateReportBtnActionListener(CustomWindow customWindow, String originFileName,
			List<String> gtFileNameList, SegmentResult segmentResult)
	{
		this.customWindow = customWindow;
		this.gtFileNameList = gtFileNameList;
		this.originFileName = originFileName;
		this.segmentResult = segmentResult;
	}

	public void setSegmentResult(SegmentResult segmentResult)
	{
		this.segmentResult = segmentResult;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		customWindow.appendLog(" Click Run Button");

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int rVal = fileChooser.showSaveDialog(customWindow);

		if (rVal == JFileChooser.APPROVE_OPTION)
		{

			String savePath = fileChooser.getSelectedFile().getAbsolutePath();

			customWindow.appendLog(savePath);

			try
			{
				new Report(segmentResult, originFileName, gtFileNameList, savePath).generate();

				customWindow.appendLog(" Report generated.");
				JOptionPane.showMessageDialog(new JPanel(), "Report generated.", "Note",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(new JPanel(), "Could not generate report", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			;

		}

		if (rVal == JFileChooser.CANCEL_OPTION)
		{
			customWindow.appendLog("Clicked Canel");
		}

	}

}
