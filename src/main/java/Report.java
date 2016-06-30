import ij.IJ;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import model.SegmentResult;
import model.Statistics;
import utils.LSUtil;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

/**
 * Generate result report in iText7
 * 
 * @author Vincent Liu
 * 
 */
public class Report
{
	private List<SegmentResult> resultList;

	private Statistics statistics;

	private String originFileName;

	private String gtFileName;

	private Date date;

	private String resultDir;

	public Report(List<SegmentResult> resultList, String originFileName, String gtFileName,
			String resultDir)
	{
		this.resultList = resultList;
		this.originFileName = originFileName;
		this.gtFileName = gtFileName;
		this.resultDir = resultDir;

		date = new Date();

		statistics = new Statistics(resultList);

	}

	// class MyFooter extends PdfPageEventHelper
	// {
	// final Font footerFont = new Font(Font.FontFamily.COURIER, 12, Font.NORMAL);
	//
	// final PdfFont footerFont = PdfFontFactory.createFont(FontConstants.COURIER);
	//
	// public void onEndPage(PdfWriter writer, Document document)
	// {
	//
	// ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(
	// LSConstants.AUTHORITY, footerFont), (document.right() - document.left()) / 2
	// + document.leftMargin(), document.bottom() - 10, 0);
	// }
	// }


	/**
	 * Generate segmentation result report
	 * 
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public void generate() throws FileNotFoundException
	{

		IJ.log("===== Generate report =====");

		final OutputStream outputStream =
				new FileOutputStream(resultDir + "/" + originFileName + ".pdf");
		final PdfWriter pdfWriter = new PdfWriter(outputStream);
		final PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		final Document document = new Document(pdfDocument);


		// Footer: before document.open()
		// final MyFooter event = new MyFooter();
		// pdfWriter.setPageEvent(event);

		final Paragraph emptyParagraph = new Paragraph("");

		final Table infoTable = new Table(new float[] {100, 300});

		infoTable.addCell(createInfoCell("Date:"));
		infoTable.addCell(createInfoDataCell(date.toString()));
		infoTable.addCell(createInfoCell("Input file:"));
		infoTable.addCell(createInfoDataCell(originFileName));
		infoTable.addCell(createInfoCell("Ground truth file:"));
		infoTable.addCell(createInfoDataCell(gtFileName));



		final Table table = new Table(new float[] {40, 60, 50, 80, 70, 70, 60, 110});
		table.setWidthPercent(100);

		final Cell cell = createTitleCell("No.");
		final Cell jaccardCell = createTitleCell("Jaccard");
		final Cell diceCell = createTitleCell("Dice");
		final Cell conformityCell = createTitleCell("Conformity");
		final Cell sensitivityCell = createTitleCell("Sensitivity");
		final Cell specificityCell = createTitleCell("Specificity");
		final Cell evolveTimeCell = createTitleCell("Evolve Time(ms)");
		final Cell fpRateCell = createTitleCell("FP Rate");

		table.addCell(cell);
		table.addCell(jaccardCell);
		table.addCell(diceCell);
		table.addCell(conformityCell);

		table.addCell(sensitivityCell);
		table.addCell(specificityCell);
		table.addCell(fpRateCell);
		table.addCell(evolveTimeCell);

		for (SegmentResult result : resultList)
		{
			table.addCell(createDataCell(String.valueOf(result.getSliceNo())));
			table.addCell(createDataCell(LSUtil.convertDoubleToStr(result.getJaccard())));
			table.addCell(createDataCell(LSUtil.convertDoubleToStr(result.getDice())));
			table.addCell(createDataCell(LSUtil.convertDoubleToStr(result.getConformity())));
			table.addCell(createDataCell(LSUtil.convertDoubleToStr(result.getSensitivity())));
			table.addCell(createDataCell(LSUtil.convertDoubleToStr(result.getSpecificity())));
			table.addCell(createDataCell(LSUtil.convertDoubleToStr(result.getFPRate())));
			table.addCell(createDataCell(String.valueOf(result.getEvolveTime())));

		}

		table.addCell(createAvgCell("Avg."));

		table.addCell(createAvgCell(LSUtil.convertDoubleToStr(statistics.getJaccardMean())));
		table.addCell(createAvgCell(LSUtil.convertDoubleToStr(statistics.getDiceMean())));
		table.addCell(createAvgCell(LSUtil.convertDoubleToStr(statistics.getConformityMean())));
		table.addCell(createAvgCell(LSUtil.convertDoubleToStr(statistics.getSensitivityMean())));
		table.addCell(createAvgCell(LSUtil.convertDoubleToStr(statistics.getSpecificityMean())));
		table.addCell(createAvgCell(LSUtil.convertDoubleToStr(statistics.getFPRateMean())));
		table.addCell(createAvgCell(LSUtil.convertDoubleToStr(statistics.getEvolveTimeMean())));

		table.addCell(createStdCell("Std."));

		table.addCell(createStdCell(LSUtil.convertDoubleToStr(statistics.getJaccardStd())));
		table.addCell(createStdCell(LSUtil.convertDoubleToStr(statistics.getDiceStd())));
		table.addCell(createStdCell(LSUtil.convertDoubleToStr(statistics.getConformityStd())));
		table.addCell(createStdCell(LSUtil.convertDoubleToStr(statistics.getSensitivityStd())));
		table.addCell(createStdCell(LSUtil.convertDoubleToStr(statistics.getSpecificityStd())));
		table.addCell(createStdCell(LSUtil.convertDoubleToStr(statistics.getFPRateStd())));
		table.addCell(createStdCell(LSUtil.convertDoubleToStr(statistics.getEvolveTimeStd())));


		document.add(infoTable);
		document.add(emptyParagraph);
		document.add(table);

		// Close document
		document.close();

		IJ.showMessage("Generate report complete");

	}

	/**
	 * Create cell of info titles
	 * 
	 * @param title
	 * @return
	 */
	private Cell createInfoCell(String title)
	{
		final Cell cell = new Cell();
		cell.add(new Paragraph(title));

		cell.setBorder(Border.NO_BORDER);

		cell.setPaddingLeft(10);
		cell.setPaddingRight(-10);

		return cell;

	}

	/**
	 * Create cell of info data
	 * 
	 * @param title
	 * @return
	 */
	private Cell createInfoDataCell(String title)
	{
		final Cell cell = new Cell();
		cell.add(title);

		cell.setBorder(Border.NO_BORDER);

		cell.setPaddingLeft(10);
		cell.setPaddingRight(-10);

		return cell;

	}

	/**
	 * Create cell of table titles
	 * 
	 * @param title
	 * @return
	 */
	private Cell createTitleCell(String title)
	{
		final Cell cell = new Cell();
		cell.add(new Paragraph(title));
		cell.setBackgroundColor(Color.LIGHT_GRAY);

		cell.setBorder(Border.NO_BORDER);
		cell.setBorderBottom(new SolidBorder(Color.BLACK, 1));
		// cell.setBorderTop(new SolidBorder(Color.BLACK, 0));


		cell.setTextAlignment(TextAlignment.CENTER);

		return cell;

	}

	/**
	 * Create cell of data
	 * 
	 * @param data
	 * @return
	 */
	private Cell createDataCell(String data)
	{
		final Cell cell = new Cell();
		cell.add(data);
		cell.setBackgroundColor(Color.WHITE);

		cell.setBorder(null);

		cell.setTextAlignment(TextAlignment.CENTER);

		return cell;
	}

	/**
	 * Create cell of average
	 * 
	 * @param data
	 * @return
	 */
	private Cell createAvgCell(String data)
	{
		final Cell cell = new Cell();
		cell.add(new Paragraph(data));
		cell.setBackgroundColor(Color.LIGHT_GRAY);

		cell.setBorder(Border.NO_BORDER);
		cell.setBorderTop(new SolidBorder(Color.BLACK, 1));

		cell.setTextAlignment(TextAlignment.CENTER);


		return cell;
	}

	/**
	 * Create cell of standard deviation
	 * 
	 * @param data
	 * @return
	 */
	private Cell createStdCell(String data)
	{
		final Cell cell = new Cell();
		cell.add(data);
		cell.setBackgroundColor(Color.LIGHT_GRAY);

		cell.setBorder(Border.NO_BORDER);
		cell.setBorderBottom(new SolidBorder(Color.BLACK, 2));

		cell.setTextAlignment(TextAlignment.CENTER);


		return cell;
	}

}
