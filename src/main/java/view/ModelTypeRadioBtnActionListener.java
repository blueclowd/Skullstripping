package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModelTypeRadioBtnActionListener implements ActionListener
{
	private CustomWindow customWindow;

	public ModelTypeRadioBtnActionListener(CustomWindow customWindow)
	{
		this.customWindow = customWindow;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{

		if (customWindow.getModelBasedBtn().isSelected())
		{
			customWindow.appendLog(" Change to model base");
			customWindow.getTextureCoefficient().setEnabled(false);
		}

		if (customWindow.getTextureBasedBtn().isSelected())
		{
			customWindow.appendLog(" Change to texture base");
			customWindow.getTextureCoefficient().setEnabled(true);
		}

	}

}
