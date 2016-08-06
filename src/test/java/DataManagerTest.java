import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.esotericsoftware.minlog.Log;


public class DataManagerTest
{
  @Test
  public void testSaveMask()
  {
    File file = new File("new.jpg");

    BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);

    try
    {
      if (!ImageIO.write(bufferedImage, "JPEG", file))
      {
        Log.error("Create new mask fails");
      }
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  @Test
  public void testEvaluateResult()
  {
    // TODO
  }
}
