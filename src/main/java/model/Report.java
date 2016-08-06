package model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

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

import ij.IJ;
import utils.LSUtil;

/**
 * Generate result report in iText7
 * 
 * @author Vincent Liu
 * 
 */
public class Report
{
	private SegmentResult segmentResult;

	private String originFileName;

	private List<String> gtFileNameList;

	private String resultDir;

	public Report(SegmentResult segmentResult, String originFileName, List<String> gtFileNameList, String resultDir)
	{
		this.segmentResult = segmentResult;
		this.originFileName = originFileName;
		this.gtFileNameList = gtFileNameList;
		this.resultDir = resultDir;
	}

	/**
	 * Generate segmentation result report
	 * 
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public void generate() throws FileNotFoundException
	{

		IJ.log("===== Generate report: " + resultDir + "/" + originFileName + ".pdf" + "=====");

		final OutputStream outputStream = new FileOutputStream(resultDir + "/" + originFileName + ".pdf");
		final PdfWriter pdfWriter = new PdfWriter(outputStream);
		final PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		final Document document = new Document(pdfDocument);
		final Paragraph emptyParagraph = new Paragraph("");

		final Table infoTable = new Table(new float[]
		{ 100, 300 });

		infoTable.addCell(createInfoCell("Date:"));
		infoTable.addCell(createInfoDataCell(new Date().toString()));
		infoTable.addCell(createInfoCell("Input file:"));
		infoTable.addCell(createInfoDataCell(originFileName));
		infoTable.addCell(createInfoCell("Ground truth file:"));
		infoTable.addCell(createInfoDataCell(gtFileNameList));

		final Table table = new Table(new float[]
		{ 60, 50, 80, 70, 70, 60, 110 });
		table.setWidthPercent(100);

		final Cell jaccardCell = createTitleCell("Jaccard");
		final Cell diceCell = createTitleCell("Dice");
		final Cell conformityCell = createTitleCell("Conformity");
		final Cell sensitivityCell = createTitleCell("Sensitivity");
		final Cell specificityCell = createTitleCell("Specificity");
		final Cell evolveTimeCell = createTitleCell("Evolve Time(ms)");
		final Cell fpRateCell = createTitleCell("FP Rate");

		table.addCell(jaccardCell);
		table.addCell(diceCell);
		table.addCell(conformityCell);
		table.addCell(sensitivityCell);
		table.addCell(specificityCell);
		table.addCell(fpRateCell);
		table.addCell(evolveTimeCell);

		table.addCell(createDataCell(LSUtil.convertDoubleToStr(segmentResult.getJaccard())));
		table.addCell(createDataCell(LSUtil.convertDoubleToStr(segmentResult.getDice())));
		table.addCell(createDataCell(LSUtil.convertDoubleToStr(segmentResult.getConformity())));
		table.addCell(createDataCell(LSUtil.convertDoubleToStr(segmentResult.getSensitivity())));
		table.addCell(createDataCell(LSUtil.convertDoubleToStr(segmentResult.getSpecificity())));
		table.addCell(createDataCell(LSUtil.convertDoubleToStr(segmentResult.getFPRate())));
		table.addCell(createDataCell(String.valueOf(segmentResult.getEvolveTime())));

		document.add(infoTable);
		document.add(emptyParagraph);
		document.add(table);

		// Close document
		document.close();

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
	 * Create cell of info data
	 * 
	 * @param title
	 * @return
	 */
	private Cell createInfoDataCell(List<String> titleList)
	{

		final StringBuilder concatedTitle = new StringBuilder();
		for (String title : titleList)
		{
			concatedTitle.append(title);
			concatedTitle.append("\n");
		}

		final Cell cell = new Cell();
		cell.add(concatedTitle.toString());

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
		// cell.setBorderTop(new SolidBorder(Color.BLACK, 1));

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

}
