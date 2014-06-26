package com.deconware.ops.phantom;

import net.imagej.ops.Op;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.phantom.Phantoms;

@Plugin(type = Op.class, name = AddShell.NAME, priority = Priority.HIGH_PRIORITY)
public class AddShellOp extends AddShape implements AddSphere
{
	
	@Parameter
	int outerRadius;
	
	@Parameter
	int innerRadius;
	
	@Override
	public void DrawShape()
	{
		// draw the shell
		Phantoms.drawSphere(interval, center, (int)outerRadius, intensity);
		Phantoms.drawSphere(interval, center, (int)innerRadius, 0.0);
	}
}

