package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import ij.ImagePlus;
import utils.LSConstants;
import utils.LSConstants.ContourColor;

/**
 * Main GUI window of level set segmentation
 * 
 * @author Vincent Liu
 *
 */
public class CustomWindow extends JFrame
{

	/** default serial version UID */
	private static final long serialVersionUID = 1L;

	// Window size
	private static final int WINDOW_WIDTH = 1000;
	private static final int WINDOW_HEIGHT = 700;

	// Titles
	private static final String WINDOW_TITLE = "Levelset - ";
	private static final String IMAGE1_TITLE = "Input Image";
	private static final String IMAGE2_TITLE = "Ground Truth";

	private static final String ARG_VELOCITY = " Velocity:";
	private static final String ARG_TEXTURECOEFFICIENT = " Coefficient(Texture):";
	private static final String ARG_THRESHOLD = " Threshold:";
	private static final String ARG_HEADING = " Heading(0-3):";

	private static final String ARG_MODELEDBASED = "Modeled Based";
	private static final String ARG_TEXTUREBASED = "Texture Based";

	private static final String ARG_FILLHOLES = "Fill Holes";
	private static final String ARG_CONTOURCOLOR = " Contour Color:";

	// Buttons
	private JButton runBtn;
	private JButton exitBtn;
	private JButton generateReportBtn;

	/**
	 * main GUI panel (containing the buttons panel on the left, the image in
	 * the center and the annotations panel on the right
	 */
	private JPanel imgPanel;
	private JPanel btnPanel;
	private JPanel argPanel;
	private JPanel logPanel;
	private JPanel copyRightPanel;

	// Labels
	private JLabel imageLabel1;
	private JLabel imageLabel2;
	private JLabel copyRight;

	private ImageIcon imageIcon1;
	private ImageIcon imageIcon2;

	// Images
	private ImagePlus imgPlus1;
	private ImagePlus imgPlus2;

	// TextFields
	private JTextField velocity;
	private JTextField textureCoefficient;
	private JTextField threshold;
	private JTextField heading;

	// RadioButtons
	private JRadioButton modelBased;
	private JRadioButton textureBased;

	// CheckBox
	private JCheckBox fillHoles;

	// ComboBox
	private JComboBox<ContourColor> contourColor;

	// TextAreas
	private JTextArea logArea;
	private JScrollPane logScrollPane;

	// Default value of TextFields
	private static final String DEFAULT_VELOCITY = "3251.25";
	private static final String DEFAULT_TEXTURECOEFFICIENT = "0.03";
	private static final String DEFAULT_THRESHOLD = "0.5";
	private static final String DEFAULT_HEADING = "0";

	private static final int CONTOUR_COLOR = Color.white.getRGB();
	private static final Color BACKGROUND_COLOR = new Color(127, 255, 212);

	private static final String IMGPANEL_TITLE = "Illustrations";
	private static final String ARGPANEL_TITLE = "Arguements";
	private static final String LOGPANEL_TITLE = "Log information";

	private Font btnFont = new Font("Arial", Font.BOLD, 12);
	private Font copyRightFont = new Font("Serif", Font.PLAIN, 14);

	/**
	 * Construct the plugin window
	 * 
	 * @param imp
	 *            input image
	 */
	public CustomWindow(ImagePlus imgPlus1, ImagePlus imgPlus2, int initSliceNo, String fileName)
	{
		this.imgPlus1 = imgPlus1;
		this.imgPlus2 = imgPlus2;

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setTitle(WINDOW_TITLE + fileName);
		setLocation(100, 100);
		setLayout(new GridBagLayout());
		setIconImage(new ImageIcon(getClass().getResource("/iconCBEL.png")).getImage());

		getContentPane().setBackground(BACKGROUND_COLOR);

		final GridBagConstraints illustrationPanelConstraints = new GridBagConstraints();
		illustrationPanelConstraints.gridx = 0;
		illustrationPanelConstraints.gridy = 0;
		illustrationPanelConstraints.gridwidth = 1;
		illustrationPanelConstraints.gridheight = 2;
		illustrationPanelConstraints.weightx = 0;
		illustrationPanelConstraints.weighty = 0;
		illustrationPanelConstraints.fill = GridBagConstraints.BOTH;
		illustrationPanelConstraints.anchor = GridBagConstraints.WEST;

		final GridBagConstraints argPanelConstraints = new GridBagConstraints();
		argPanelConstraints.gridx = 2;
		argPanelConstraints.gridy = 0;
		argPanelConstraints.gridwidth = 1;
		argPanelConstraints.gridheight = 1;
		argPanelConstraints.weightx = 0;
		argPanelConstraints.weighty = 0;
		argPanelConstraints.fill = GridBagConstraints.NONE;
		argPanelConstraints.anchor = GridBagConstraints.EAST;

		final GridBagConstraints btnPanelConstraints = new GridBagConstraints();
		btnPanelConstraints.gridx = 2;
		btnPanelConstraints.gridy = 1;
		btnPanelConstraints.gridwidth = 1;
		btnPanelConstraints.gridheight = 1;
		btnPanelConstraints.weightx = 0;
		btnPanelConstraints.weighty = 0;
		btnPanelConstraints.fill = GridBagConstraints.NONE;
		btnPanelConstraints.anchor = GridBagConstraints.EAST;

		final GridBagConstraints spaceConstraints = new GridBagConstraints();
		spaceConstraints.gridx = 1;
		spaceConstraints.gridy = 0;
		spaceConstraints.gridwidth = 1;
		spaceConstraints.gridheight = 2;
		spaceConstraints.weightx = 0;
		spaceConstraints.weighty = 0;
		spaceConstraints.fill = GridBagConstraints.NONE;
		spaceConstraints.anchor = GridBagConstraints.WEST;

		final GridBagConstraints hSpaceConstraints = new GridBagConstraints();
		hSpaceConstraints.gridx = 0;
		hSpaceConstraints.gridy = 2;
		hSpaceConstraints.gridwidth = 3;
		hSpaceConstraints.gridheight = 1;
		hSpaceConstraints.weightx = 0;
		hSpaceConstraints.weighty = 0;
		hSpaceConstraints.fill = GridBagConstraints.NONE;
		hSpaceConstraints.anchor = GridBagConstraints.WEST;

		final GridBagConstraints logConstraints = new GridBagConstraints();
		logConstraints.gridx = 0;
		logConstraints.gridy = 3;
		logConstraints.gridwidth = 3;
		logConstraints.gridheight = 1;
		logConstraints.weightx = 0;
		logConstraints.weighty = 0;
		logConstraints.fill = GridBagConstraints.BOTH;
		logConstraints.anchor = GridBagConstraints.CENTER;

		final GridBagConstraints copyRightConstraints = new GridBagConstraints();
		copyRightConstraints.gridx = 0;
		copyRightConstraints.gridy = 4;
		copyRightConstraints.gridwidth = 3;
		copyRightConstraints.gridheight = 1;
		copyRightConstraints.weightx = 0;
		copyRightConstraints.weighty = 0;
		copyRightConstraints.fill = GridBagConstraints.BOTH;
		copyRightConstraints.anchor = GridBagConstraints.CENTER;

		// Button configuration
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(2, 1, 10, 10));
		btnPanel.setBackground(BACKGROUND_COLOR);
		setJButtons();

		// Image configuration
		imgPanel = new JPanel();
		imgPanel.setLayout(new GridLayout(1, 2, 10, 10));
		imgPanel.setBorder(BorderFactory.createTitledBorder(IMGPANEL_TITLE));

		setJLabels(initSliceNo);

		// Log configuration
		logPanel = new JPanel();
		logPanel.setBorder(BorderFactory.createTitledBorder(LOGPANEL_TITLE));

		setJTextArea();

		// Argument configuration
		argPanel = new JPanel(new GridLayout(7, 2, 10, 2));
		argPanel.setBorder(BorderFactory.createTitledBorder(ARGPANEL_TITLE));

		setJRadioButtons();
		setJTextFields();
		setJCheckBox();
		setJComboBox();

		// Copy right configuration
		copyRightPanel = new JPanel();
		copyRightPanel.setBackground(BACKGROUND_COLOR);
		setCopyRight();

		add(imgPanel, illustrationPanelConstraints);
		add(argPanel, argPanelConstraints);
		add(logPanel, logConstraints);
		add(btnPanel, btnPanelConstraints);
		add(copyRightPanel, copyRightConstraints);

		add(Box.createRigidArea(new Dimension(20, 0)), spaceConstraints);
		add(Box.createRigidArea(new Dimension(0, 20)), hSpaceConstraints);

		// pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	private void setCopyRight()
	{
		copyRight = new JLabel(LSConstants.AUTHORITY);
		copyRight.setFont(copyRightFont);
		copyRight.setHorizontalAlignment(JLabel.CENTER);
		copyRight.setVerticalAlignment(JLabel.CENTER);

		copyRightPanel.add(copyRight);
	}

	private void setJComboBox()
	{
		final JLabel contourColorLabel = new JLabel(ARG_CONTOURCOLOR);

		contourColor = new JComboBox<ContourColor>();
		contourColor.addItem(ContourColor.Yellow);
		contourColor.addItem(ContourColor.White);
		contourColor.addItem(ContourColor.Green);
		contourColor.addItem(ContourColor.Magenta);

		contourColor.setSelectedIndex(0);
		contourColor.setEditable(false);

		argPanel.add(contourColorLabel);
		argPanel.add(contourColor);
	}

	private void setJCheckBox()
	{
		fillHoles = new JCheckBox(ARG_FILLHOLES);

		argPanel.add(fillHoles);
		argPanel.add(Box.createRigidArea(new Dimension()));
	}

	private void setJTextArea()
	{
		logArea = new JTextArea(10, 70);
		logArea.setText(" (Waiting...)");

		// Make caret move down automatically
		final DefaultCaret caret = (DefaultCaret) logArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		logScrollPane = new JScrollPane(logArea);
		logScrollPane.setAutoscrolls(true);

		logScrollPane.setBorder(BorderFactory.createLineBorder(Color.PINK));

		logPanel.add(logScrollPane);
	}

	private void setJTextFields()
	{
		// Argument 1: Velocity
		final JLabel velocityLabel = new JLabel(ARG_VELOCITY);
		argPanel.add(velocityLabel);
		velocity = new JTextField();
		velocity.setText(DEFAULT_VELOCITY);
		argPanel.add(velocity);

		// Argument 2:
		final JLabel textureCoefficientLabel = new JLabel(ARG_TEXTURECOEFFICIENT);
		argPanel.add(textureCoefficientLabel);
		textureCoefficient = new JTextField();
		textureCoefficient.setText(DEFAULT_TEXTURECOEFFICIENT);
		textureCoefficient.setEnabled(false);
		argPanel.add(textureCoefficient);

		// Argument 3:
		final JLabel thresholdLabel = new JLabel(ARG_THRESHOLD);
		argPanel.add(thresholdLabel);
		threshold = new JTextField();
		threshold.setText(DEFAULT_THRESHOLD);
		argPanel.add(threshold);

		// Argument 4:
		final JLabel headingLabel = new JLabel(ARG_HEADING);
		argPanel.add(headingLabel);
		heading = new JTextField();
		heading.setText(DEFAULT_HEADING);
		argPanel.add(heading);

	}

	private void setJRadioButtons()
	{
		modelBased = new JRadioButton(ARG_MODELEDBASED);
		modelBased.setSelected(true);
		modelBased.addActionListener(new ModelTypeRadioBtnActionListener(this));

		textureBased = new JRadioButton(ARG_TEXTUREBASED);
		textureBased.addActionListener(new ModelTypeRadioBtnActionListener(this));

		ButtonGroup buttonGrp = new ButtonGroup();

		buttonGrp.add(modelBased);
		buttonGrp.add(textureBased);

		argPanel.add(modelBased);
		argPanel.add(textureBased);
	}

	private void setJLabels(int initSliceNo)
	{
		imageIcon1 = new ImageIcon(imgPlus1.getBufferedImage());
		imageIcon2 = new ImageIcon(imgPlus2.getBufferedImage());

		// Set text position
		imageLabel1 = new JLabel(IMAGE1_TITLE);
		imageLabel2 = new JLabel(IMAGE2_TITLE);

		imageLabel1.setSize(300, 300);
		imageLabel1.setIcon(imageIcon1);
		imageLabel2.setIcon(imageIcon2);

		// Set font TODO
		imageLabel1.setFont(btnFont);
		imageLabel2.setFont(btnFont);

		// Set default slice
		setSliceNo(initSliceNo);

		// Set text on the top of image
		imageLabel1.setVerticalTextPosition(JLabel.TOP);
		imageLabel1.setHorizontalTextPosition(JLabel.CENTER);

		imageLabel2.setVerticalTextPosition(JLabel.TOP);
		imageLabel2.setHorizontalTextPosition(JLabel.CENTER);

		imgPanel.add(imageLabel1);
		imgPanel.add(imageLabel2);

	}

	private void setJButtons()
	{

		runBtn = new JButton("Run");
		runBtn.setToolTipText("Start segmentation");
		runBtn.setIcon(new ImageIcon(getClass().getResource("/run.png")));

		generateReportBtn = new JButton("Generate Report");
		generateReportBtn.setEnabled(false);
		generateReportBtn.setIcon(new ImageIcon(getClass().getResource("/generateReport.png")));

		btnPanel.add(runBtn);
		btnPanel.add(generateReportBtn);

	}

	public void setSliceNo(int sliceNo)
	{
		appendLog(" Slice " + sliceNo);

		imgPlus1.setSlice(sliceNo);
		imgPlus2.setSlice(sliceNo);

		BufferedImage image1 = imgPlus1.getBufferedImage();
		BufferedImage image2 = imgPlus2.getBufferedImage();

		setOriginalImage(image1);
		setGtImage(image2);

	}

	public void setOriginalImage(BufferedImage image)
	{
		imageLabel1.setIcon(new ImageIcon(image));
	}

	public void setGtImage(BufferedImage image)
	{
		imageLabel2.setIcon(new ImageIcon(image));
	}

	public void setContour(List<Point> contourPoints, BufferedImage image)
	{
		// Repaint the color where contour points locate to display the contour
		for (Point p : contourPoints)
		{
			image.setRGB(p.x, p.y, CONTOUR_COLOR);
		}

		imageLabel1.setIcon(new ImageIcon(image));
	}

	public void drawContour(List<Point> contourPoints)
	{
		Graphics g = imageIcon1.getImage().getGraphics();

		g.drawOval(100, 100, 1, 1);
	}

	public JLabel getImage1Label()
	{
		return imageLabel1;
	}

	public String getVelocity()
	{
		return velocity.getText();
	}

	public JTextField getTextureCoefficient()
	{
		return textureCoefficient;
	}

	public String getThreshold()
	{
		return threshold.getText();
	}

	public String getHeading()
	{
		return heading.getText();
	}

	public JRadioButton getModelBasedBtn()
	{
		return modelBased;
	}

	public JRadioButton getTextureBasedBtn()
	{
		return textureBased;
	}

	public JCheckBox getFillingHoles()
	{
		return fillHoles;
	}

	public JComboBox getContourColor()
	{
		return contourColor;
	}

	public void enableGenerateReportBtn(boolean enable)
	{
		generateReportBtn.setEnabled(enable);
	}

	public void addRunBtnActionListener(ActionListener listener)
	{
		runBtn.addActionListener(listener);
	}

	public void addGenerateReportBtnActionListener(ActionListener listener)
	{
		generateReportBtn.addActionListener(listener);
	}

	public void addExitBtnActionListener(ActionListener listener)
	{
		exitBtn.addActionListener(listener);
	}

	public void appendLog(String newLog)
	{
		if (logArea == null)
		{
			return;
		}

		String originalText = logArea.getText();
		String newTextToAppend = originalText + "\n" + newLog;

		logArea.setText(newTextToAppend);

	}

}
