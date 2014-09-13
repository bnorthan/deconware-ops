package com.deconware.ops.phantom;

import net.imagej.ops.Op;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;

import com.deconware.algorithms.StaticFunctions;

@Plugin(type = Op.class, name = AddConstant.NAME, priority = Priority.HIGH_PRIORITY)
public class AddConstantOp implements Op
{
	@Parameter
	RandomAccessibleInterval interval;
	
	@Parameter
	double background;
	
	@Override
	public void run()
	{
		// create a blank phantom
		StaticFunctions.set(Views.iterable(interval), background);
	}
}
