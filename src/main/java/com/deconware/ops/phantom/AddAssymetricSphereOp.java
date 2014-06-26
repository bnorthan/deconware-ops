package com.deconware.ops.phantom;

import net.imagej.ops.Op;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.phantom.Phantoms;

@Plugin(type = Op.class, name = AddAssymetricSphere.NAME, priority = Priority.HIGH_PRIORITY)
public class AddAssymetricSphereOp extends AddShape implements AddSphere
{
	@Parameter
	long radius[];
	
	public void DrawShape()
	{
		// draw the sphere
		Phantoms.drawAsymetricSphere(interval, center, radius, intensity);
	}
}
