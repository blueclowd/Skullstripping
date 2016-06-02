package model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MetricsTest
{
  @Test
  public void testCalculate()
  {
    boolean[][] result = { {true, true}, {false, false}, {true, false}};
    boolean[][] groundTruth = { {true, false}, {false, true}, {false, false}};

    Metrics metrics = new Metrics(result, groundTruth);
    metrics.calculate();

    assertEquals("", 1, metrics.getTP(), 0.001);
    assertEquals("", 1, metrics.getFN(), 0.001);
    assertEquals("", 2, metrics.getTN(), 0.001);
    assertEquals("", 2, metrics.getFP(), 0.001);
  }
}
