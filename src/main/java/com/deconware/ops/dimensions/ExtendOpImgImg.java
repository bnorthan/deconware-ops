package com.deconware.ops.dimensions;

import net.imagej.ops.AbstractFunction;
import net.imagej.ops.Op;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.outofbounds.OutOfBoundsFactory;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.dim.ExtendImageUtility;
import com.deconware.algorithms.dim.ExtendImageUtility.BoundaryType;
import com.deconware.algorithms.fft.SimpleFFTFactory.FFTTarget;

import com.deconware.ops.utility.StaticUtilities;

@Plugin(type = Op.class, name = Extend.NAME, priority=Priority.HIGH_PRIORITY)
public class ExtendOpImgImg <T extends RealType<T>> extends
	AbstractFunction<Img<T>, Img<T>> implements Extend
{
	@Parameter
	int[] axisIndices;
	
	@Parameter
	int[] extension;
	
	@Parameter(required = false)
	BoundaryType boundaryType;
	
	@Parameter(required = false)
	FFTTarget fftTarget;
	
	@Override
	public Img<T> compute(Img<T> input,
		Img<T> output)
	{
		
		if (axisIndices.length!=extension.length)
		{
			return null;
		}
		
		ExtendImageUtility utility=new ExtendImageUtility(axisIndices, extension, input, boundaryType, fftTarget);
		
		OutOfBoundsFactory< T, RandomAccessibleInterval<T> > outOfBoundsFactory= utility.getOutOfBoundsFactory(); 
	
		final RandomAccessible< T > temp = Views.extend( input, outOfBoundsFactory );
		
		final RandomAccessibleInterval<T> extendedInput = Views.offsetInterval(temp, utility.getOffset(), utility.getNewDimensions());
		
		// if the output is null create it based on the input
		if (output==null)
		{
			output=input.factory().create(utility.getNewDimensions(), input.firstElement());
		}
		
		final IterableInterval<T> iterableInput = Views.iterable(extendedInput);

		final Cursor<T> cursorIn = iterableInput.localizingCursor();
		final Cursor<T> cursorOut = output.cursor();
		
		while (cursorOut.hasNext())
		{
			cursorIn.fwd();
			cursorOut.fwd();
			
			
			cursorOut.get().set(cursorIn.get());
		}
		
		// if the input is an ImgPlus wrap the output as an imgplus
		if (input instanceof ImgPlus )
		{    
			return StaticUtilities.WrapAsImgPlus((ImgPlus)input, output, "extended");
		}
		
		return output;
	}
}
