import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;

import model.SegmentResult;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;


public class Report
{
  private List<SegmentResult> resultList;

  private String originFileName;

  private String gtFileName;

  public static final DecimalFormat df = new DecimalFormat("#.##");

  public Report(List<SegmentResult> resultList, String originFileName, String gtFileName)
  {
    this.resultList = resultList;
    this.originFileName = originFileName;
    this.gtFileName = gtFileName;
  }

  public void generate() throws DocumentException, FileNotFoundException
  {

    Document document = new Document();
    com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(
        "/home/liu/Desktop/eclipse/" + originFileName + ".pdf"));
    document.open();

    Paragraph oriParagraph =
        new Paragraph("Input file: " + originFileName, new Font(FontFamily.HELVETICA, 12));

    Paragraph gtParagraph =
        new Paragraph("Ground truth file: " + gtFileName, new Font(FontFamily.HELVETICA, 12));

    Paragraph emptyParagraph = new Paragraph(" ", new Font(FontFamily.COURIER, 12));


    PdfPTable table = new PdfPTable(8);
    table.setWidthPercentage(100);
    table.setTotalWidth(new float[] {30, 70, 70, 70, 70, 70, 70, 100});

    PdfPCell cell = createTitleCell("No.");

    PdfPCell jaccardCell = createTitleCell("Jaccard");

    PdfPCell diceCell = createTitleCell("Dice");

    PdfPCell conformityCell = createTitleCell("Conformity");

    PdfPCell sensitivityCell = createTitleCell("Sensitivity");

    PdfPCell specificityCell = createTitleCell("Specificity");

    PdfPCell evolveTimeCell = createTitleCell("Evolve Time");

    PdfPCell fpRateCell = createTitleCell("FP Rate");

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
      table.addCell(createDataCell(df.format(result.getJaccard())));
      table.addCell(createDataCell(df.format(result.getDice())));
      table.addCell(createDataCell(df.format(result.getConformity())));
      table.addCell(createDataCell(df.format(result.getSensitivity())));
      table.addCell(createDataCell(df.format(result.getSpecificity())));
      table.addCell(createDataCell(df.format(result.getFPRate())));
      table.addCell(createDataCell(String.valueOf(result.getEvolveTime())));

    }


    document.add(oriParagraph);
    document.add(gtParagraph);
    document.add(emptyParagraph);
    document.add(table);

    document.close();

  }

  private PdfPCell createTitleCell(String title)
  {
    final PdfPCell cell = new PdfPCell();
    cell.addElement(new Phrase(title));
    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
    cell.setBorder(0);
    cell.setBorderWidthTop(1);

    // TODO alignment

    return cell;

  }

  private PdfPCell createDataCell(String data)
  {
    final PdfPCell cell = new PdfPCell();
    cell.addElement(new Phrase(data));
    cell.setBackgroundColor(BaseColor.WHITE);
    cell.setBorder(0);
    cell.setBorderWidthTop(1);

    // TODO alignment

    return cell;
  }



}
