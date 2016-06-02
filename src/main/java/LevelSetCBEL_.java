import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.util.List;

import model.SegmentResult;
import model.UiOptions;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.DocumentException;

public class LevelSetCBEL_ implements PlugInFilter
{

  private UiOptions uiOpts;

  private DataManager dataManager;

  // Configuration
  private static final Font font = new Font("Arial", Font.BOLD, 12);
  private static final Color bgColor = new Color(140, 240, 160);

  private String filePath;
  private String fileName;

  private String gtFileName;
  private String gtFilePath;

  public int setup(String arg, ImagePlus imgPlus)
  {

    // Open parameter dialog
    final GenericDialog gd = new GenericDialog("Parameters");

    setDialogConfig(gd);

    gd.showDialog();

    if (gd.wasCanceled())
    {
      IJ.error("PlugIn canceled!");
      return 0;
    }

    fileName = imgPlus.getOriginalFileInfo().fileName;
    filePath = imgPlus.getOriginalFileInfo().directory + fileName;

    IJ.log("Input Image = " + filePath);

    final OpenDialog gtDialog = new OpenDialog("Open ground truth image...");

    gtFileName = gtDialog.getFileName();
    gtFilePath = gtDialog.getDirectory() + gtFileName;

    IJ.log("Ground Truth = " + gtFilePath);


    // UI options from GenericDialog
    uiOpts = getUiOptions(gd);

    dataManager = new DataManager(uiOpts, imgPlus);

    dataManager.loadMetadata(imgPlus);

    dataManager.createSkullStripper();

    // The plugin filter handles all types of images
    return DOES_ALL;
  }

  /**
   * The run method receives the image processor of the image and performs the actual function of
   * the plugin
   */
  public void run(ImageProcessor ip)
  {

    IJ.log("run()...");

    dataManager.initZeroLS();

    dataManager.evolveVolume();

    if (uiOpts.isHoleFilling())
    {
      dataManager.fillHoles();
    }

    if (StringUtils.isNotEmpty(uiOpts.getOutputMaskFile()))
    {
      dataManager.saveMask(uiOpts.getOutputMaskFile());
    }
    if (StringUtils.isNotEmpty(uiOpts.getOutputBrainOnlyFile()))
    {
      dataManager.saveBrainOnlyFile(uiOpts.getOutputBrainOnlyFile());
    }

    IJ.log("Evaluate segmentation result");
    final List<SegmentResult> segmentResults = dataManager.evaluateResult();

    try
    {
      new Report(segmentResults, fileName, gtFileName).generate();
    } catch (FileNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (DocumentException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    dataManager.closeReader();

  }

  /**
   * Common configuration of dialog
   */
  private void setDialogConfig(GenericDialog gd)
  {

    IJ.log("setDialogConfig()...");

    gd.setFont(font);

    gd.setBackground(bgColor);

    gd.addNumericField("Velocity", 0.05 * 255 * 255, 2);

    gd.addNumericField("Threshold", 0.2, 2);

    gd.addNumericField("Heading(0-3)", 0, 0);

    gd.addCheckbox("Fill holes", false);

    gd.addStringField("Save mask file (jpg)", "mask");

    gd.addStringField("Save brain-only file", "brain");

    gd.addChoice("Contour Color", new String[] {"Yellow", "Green", "Magenta"}, "Yelow");

  }

  private UiOptions getUiOptions(final GenericDialog dialog)
  {

    final UiOptions uiOpts = new UiOptions();

    uiOpts.setVelocity(dialog.getNextNumber());
    uiOpts.setThreshold(dialog.getNextNumber());
    uiOpts.setHeading((int) dialog.getNextNumber());

    uiOpts.setHoleFilling(dialog.getNextBoolean());
    uiOpts.setOutputMaskFile(dialog.getNextString());
    uiOpts.setOutputBrainOnlyFile(dialog.getNextString());

    uiOpts.setContourColor(dialog.getNextChoice());

    IJ.log("Ui Options:");
    IJ.log("Velocity = " + uiOpts.getVelocity());
    IJ.log("Threshold = " + uiOpts.getThreshold());
    IJ.log("Heading = " + uiOpts.getHeading());

    IJ.log("Heading = " + uiOpts.getContourColor());

    return uiOpts;
  }
}
