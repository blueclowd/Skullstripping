package model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.itextpdf.layout.element.Cell;

public class ReportTest
{
	@Test
	public void testCreateInfoDataCell() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException
	{
		Report report = new Report(null, "", null, "");

		Method method = report.getClass().getDeclaredMethod("createInfoDataCell", List.class);
		method.setAccessible(true);

		List<String> titleList = new ArrayList<String>();

		titleList.add("title1");
		titleList.add("title2");

		Cell resultCell = (Cell) method.invoke(report, titleList);

	}
}
