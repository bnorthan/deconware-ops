package com.deconware.ops.dimensions;

import net.imagej.ops.AbstractFunction;
import net.imagej.ops.Op;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.dim.ExtendImageUtility;
import com.deconware.algorithms.dim.ExtendImageUtility.ExtensionType;
import com.deconware.algorithms.dim.ExtendImageUtility.BoundaryType;
import com.deconware.algorithms.fft.SimpleFFTFactory.FFTTarget;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;

/**
 * Extends an RAI over all dimensions
 * 
 * @author bnorthan
 *
 * @param <T>
 */
@Plugin(type = Op.class, name = Extend.NAME, priority=Priority.LOW_PRIORITY)
public class ExtendToDimensionsOpRaiRai <T extends RealType<T>> extends
AbstractFunction<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>>  {
	
	@Parameter
	int[] newDimensions;
	
	@Parameter(required = false)
	BoundaryType boundaryType;
	
	@Parameter(required = false)
	FFTTarget fftTarget;
	
	@Override
	public RandomAccessibleInterval<T> compute(RandomAccessibleInterval<T> input,
		RandomAccessibleInterval<T> output)
	{
		
		ExtendImageUtility<T> utility=new ExtendImageUtility<T>(newDimensions, input, ExtensionType.DIMENSION, boundaryType, fftTarget);
		
		OutOfBoundsFactory< T, RandomAccessibleInterval<T> > outOfBoundsFactory= utility.getOutOfBoundsFactory(); 
	
		final RandomAccessible< T > temp = Views.extend( input, outOfBoundsFactory );
		
		long[] offset=utility.getOffset();
		long[] newDimensions=utility.getNewDimensions();
		
		final RandomAccessibleInterval<T> extendedInput = Views.offsetInterval(temp, offset, newDimensions);
		
		final IterableInterval<T> iterableInput = Views.iterable(extendedInput);
		final IterableInterval<T> iterableOutput = Views.iterable(output);
		
		final Cursor<T> cursorIn = iterableInput.localizingCursor();
		final Cursor<T> cursorOut = iterableOutput.cursor();
		
		while (cursorOut.hasNext())
		{
			cursorIn.fwd();
			cursorOut.fwd();
			
			cursorOut.get().set(cursorIn.get());
		}
		
		return output;
	}
	
	void setExtension(int[] extension)
	{
		this.newDimensions=newDimensions;
	}
	
	void setBoundaryType(BoundaryType boundaryType)
	{
		this.boundaryType=boundaryType;
	}
	
	void setFFTTarget(FFTTarget fftTarget)
	{
		this.fftTarget=fftTarget;
	}
}
