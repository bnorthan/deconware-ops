package com.deconware.ops;



import net.imagej.ops.AbstractFunction;
import net.imagej.ops.Function;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.slicer.CroppedIterableInterval;

import net.imglib2.meta.ImgPlus;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 */
@Plugin(type = Op.class, name = SpatialWise.NAME,
	priority = Priority.VERY_HIGH_PRIORITY)
public class SpatialWiseImgPlusImgPlus<I, O> extends
	AbstractFunction<ImgPlus<I>, ImgPlus<O>>
	implements
	SpatialWise<ImgPlus<I>, ImgPlus<O>>
{

	@Parameter
	private OpService opService;

	@Parameter
	private Function<I, O> func;

	
	@Override
	public ImgPlus<O> compute(ImgPlus<I> input,
		ImgPlus<O> output)
	{
		 CroppedIterableInterval outputInterval=SpatialIterableInterval.getSpatialIterableInterval(opService, output);
		 CroppedIterableInterval inputInterval=SpatialIterableInterval.getSpatialIterableInterval(opService, input);
		 
		 opService.run("map", outputInterval, inputInterval,
				func);

	/*	opService.run("map", new CroppedIterableInterval(opService, output,
			axisIndices), new CroppedIterableInterval(opService, input, axisIndices),
			func);*/

		return output;
	}

}

