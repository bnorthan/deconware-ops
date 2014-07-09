package com.deconware.ops.fft;

import net.imagej.ops.Contingent;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

import net.imglib2.img.Img;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.meta.Axes;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.RealType;
import net.imagej.ops.Op;  
import net.imagej.ops.slicer.CroppedIterableInterval;

import com.deconware.algorithms.fft.filters.FrequencyFilter;
import com.deconware.ops.SpatialIterableInterval;

import net.imagej.ops.OpService;

/**
 * 
 * @author bnorthan
 * base class for frequency filter operations
 * @param <T>
 */
public abstract class FrequencyFilterOp<T extends RealType<T>, S extends RealType<S>> implements Op, Contingent
{
	@Parameter
	OpService ops;
	
	@Parameter
	ImgPlus<T> input;
	
	@Parameter
	ImgPlus<S> kernel;
	
	@Parameter(type = ItemIO.BOTH, required=false)
	ImgPlus<T> output;
	
	/**
	 * abstract function used to create the algorithm that will be applied
	 * @param region
	 * @return
	 */
	abstract protected FrequencyFilter<T,S> createAlgorithm(RandomAccessibleInterval<T> input, 
			RandomAccessibleInterval<T> kernel,
			RandomAccessibleInterval<T> output);
	
	@Override
	public void run()
	{
		// do we have an image plus? 
		
		Cursor<RandomAccessibleInterval<?>> inCursor=SpatialIterableInterval.getSpatialIterableInterval(ops, input);
		Cursor<RandomAccessibleInterval<?>> kernelCursor=SpatialIterableInterval.SpatialIterableInterval(ops, kernel);
		
		// do input and kernel have same dimensions?
		
		Img<T> outputImg=input.factory().create(input, input.firstElement());
		output=new ImgPlus(outputImg, input);
		
		Cursor<RandomAccessibleInterval<?>> outCursor=SpatialIterableInterval.SpatialIterableInterval(ops, output);
		
		while (inCursor.hasNext())
		{
			inCursor.fwd();
			kernelCursor.fwd();
			outCursor.fwd();
			// TODO: not sure about this cast... maybe there is a better way to get from RandomAccessibleInterval<?> to
			// RandomAccessibleInterval<T>
			FrequencyFilter<T,S> filter=createAlgorithm((RandomAccessibleInterval<T>)inCursor.get(), 
					(RandomAccessibleInterval<T>)kernelCursor.get(), 
					(RandomAccessibleInterval<T>)outCursor.get());
			
			filter.process();
		}
	}
	
	@Override
	public boolean conforms() 
	{
		// check to see if input is an ImgPlus
		if (input instanceof ImgPlus)
		{
			int numDim=input.numDimensions();
			// check axis type -- if unknown don't conform
			for (int d=0;d<numDim;d++)
			{
				if (input.axis(d).type().getLabel().equals(Axes.UNKNOWN_LABEL))
				{
					return false;
				}
			}
			
			//return true;
		}
		
		// we need the input to be an ImgPlus, otherwise we don't have the axis type
		return false;
	}
}
