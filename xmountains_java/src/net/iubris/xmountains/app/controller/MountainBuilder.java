package net.iubris.xmountains.app.controller;

import java.lang.reflect.Field;

import net.iubris.xmountains.app.controller.MountAppController.Parameter;
import uk.ac.ed.epcc.mountain.model.Mountain;

public class MountainBuilder {

	int levels = 10;
	int stop = 2;
	double fdim = 0.65;
//	boolelan frozen = false;
	long little_sleep = 100;
	long long_sleep = 5000;
	
	private final Parameter parameter;

	public MountainBuilder(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public Mountain build() {
		setLevels();
		setStop();
		setFdim();
		setSleep();
		setSnooze();
		
		Mountain mountain = new Mountain();
		mountain.set_fdim(fdim);
		mountain.set_size(levels, stop);
		mountain.init();
		
		return mountain;
	}
	private void setLevels() {
		String s = getParameter("levels");
		int i;
		if (s != null) {
			i = Integer.parseInt(s);
			if (i > 1) {
			levels = i;
			}
		}
	}
	private void setStop() {
		String s = getParameter("stop");
		int i;
		if (s != null) {
			i = Integer.parseInt(s);
			if (i > 1) {
			stop = i;
			}
		}
	}
	private void setFdim() {
		String s = getParameter("fdim");
		double tmp;
		Double dble;
		if (s != null) {
			dble = new Double(s);
			tmp = dble.doubleValue();
			if (tmp > 0.5 && tmp <= 1.0) {
			fdim = tmp;
			}
		}
	}
	private void setSleep() {
		String s = getParameter("sleep");
		int i;
		if (s != null) {
			i = Integer.parseInt(s);
			long_sleep = (long) i;
		}
	}
	private void setSnooze() {
		String s = getParameter("snooze");
		int i;
		if (s != null) {
			i = Integer.parseInt(s);
			little_sleep = (long) i;
		}
	}
//	@Override
	private String getParameter(String parameterName) {
		try {
			Field field = parameter.getClass().getDeclaredField(parameterName);
			field.setAccessible(true);
			return (String) field.get(parameter);
		} catch(NoSuchFieldException e) {
//			e.printStackTrace();
			System.err.println("no field «"+parameterName+"»");
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

}
