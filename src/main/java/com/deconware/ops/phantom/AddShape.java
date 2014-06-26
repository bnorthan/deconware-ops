package com.deconware.ops.phantom;

import net.imagej.ops.Op;

import org.scijava.plugin.Parameter;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Point;

public abstract class AddShape implements Op
{
	@Parameter
	RandomAccessibleInterval interval;
	
	@Parameter
	long[] location;
	
	@Parameter
	double intensity;
	
	Point center;
	
	@Override
	public void run()
	{
		// a bit hackish but assume first three dimensions are spa
		center = new Point(interval.numDimensions());
		
		center.setPosition(location);
			
		// draw the sphere
		DrawShape();
	}
	
	protected abstract void DrawShape();
}
