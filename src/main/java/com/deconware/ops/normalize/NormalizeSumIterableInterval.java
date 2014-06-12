package com.deconware.ops.normalize;

import net.imagej.ops.Op;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.StaticFunctions;

import com.deconware.ops.normalize.NormalizeSum;

@Plugin(type = Op.class, name = NormalizeSum.NAME, priority = Priority.HIGH_PRIORITY)
public class NormalizeSumIterableInterval<T extends RealType<T>> implements Op
{
	@Parameter
	IterableInterval<T> input;
	
	@Override
	public void run()
	{
		StaticFunctions.norm(input);
	}	
}