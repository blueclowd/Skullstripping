import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import model.Metrics;
import model.SegmentResult;

import org.junit.Test;

import com.itextpdf.text.DocumentException;


public class ReportTest
{



  @Test
  public void testGenerate()
  {
    List<SegmentResult> segmentResult = new ArrayList<SegmentResult>();

    SegmentResult result1 = new SegmentResult(1);
    result1.setEvolveTime(56);

    boolean[][] result = { {true, false}, {false, false}};
    boolean[][] groundTruth = { {true, true}, {true, false}};

    SegmentResult result2 = new SegmentResult(2);
    result2.setEvolveTime(3546);

    Metrics metric2 = new Metrics(result, groundTruth);
    metric2.calculate();

    result2.setMetrics(metric2);

    SegmentResult result3 = new SegmentResult(3);
    result3.setEvolveTime(3546999);

    SegmentResult result4 = new SegmentResult(4);
    result4.setEvolveTime(46);

    segmentResult.add(result1);
    segmentResult.add(result2);
    segmentResult.add(result3);
    segmentResult.add(result4);

    // Report report = new Report(segmentResult, "UnitTest");

    // report.generate();
  }

  @Test
  public void testCreatePDF() throws FileNotFoundException, DocumentException
  {
    List<SegmentResult> segmentResult = new ArrayList<SegmentResult>();

    SegmentResult result1 = new SegmentResult(1);
    result1.setEvolveTime(56);

    boolean[][] result = { {true, false}, {false, false}};
    boolean[][] groundTruth = { {true, true}, {true, false}};

    SegmentResult result2 = new SegmentResult(2);
    result2.setEvolveTime(3546);

    Metrics metric2 = new Metrics(result, groundTruth);
    metric2.calculate();

    result2.setMetrics(metric2);

    SegmentResult result3 = new SegmentResult(3);
    result3.setEvolveTime(3546999);

    SegmentResult result4 = new SegmentResult(4);
    result4.setEvolveTime(46);

    segmentResult.add(result1);
    segmentResult.add(result2);
    segmentResult.add(result3);
    segmentResult.add(result4);

    Report report = new Report(segmentResult, "in.mnc", "out.mnc");

    report.generate();
  }
}
