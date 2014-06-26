package com.deconware.ops.phantom;

import net.imagej.ops.Op;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.phantom.Phantoms;

@Plugin(type = Op.class, name = AddSphere.NAME, priority = Priority.HIGH_PRIORITY)
public class AddSphereOp extends AddShape implements AddSphere
{
	@Parameter
	int radius;
	
	public void DrawShape()
	{
		// draw the sphere
		Phantoms.drawSphere(interval, center, (int)radius, intensity);
	}
}
