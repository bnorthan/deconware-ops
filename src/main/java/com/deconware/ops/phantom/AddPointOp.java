package com.deconware.ops.phantom;

import net.imagej.ops.Op;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Point;

import com.deconware.algorithms.phantom.Phantoms;

@Plugin(type = Op.class, name = AddPoint.NAME, priority = Priority.HIGH_PRIORITY)
public class AddPointOp extends AddShape implements AddPoint
{
	protected void DrawShape()
	{
		Phantoms.drawPoint(interval, center, intensity);
	}
}
