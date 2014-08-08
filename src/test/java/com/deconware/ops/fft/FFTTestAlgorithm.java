package com.deconware.ops.fft;

import com.deconware.algorithms.StaticFunctions;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.IterableInterval;

import net.imglib2.exception.IncompatibleTypeException;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.view.Views;

import com.deconware.algorithms.fft.filters.LinearFilter;

/**
 * 
 * @author bnorthan
 *
 * Convolution class based loosely on Stephan Preibisch's imglib2 FFT convolution code.  
 * @param <T>
 * @param <S>
 */
public class FFTTestAlgorithm<T extends RealType<T>, S extends RealType<S>> extends LinearFilter<T,S>
{
		

	/**
	 * @param image
	 * @param kernel
	 * @param imgFactory
	 * @param kernelImgFactory
	 * @param fftImgFactory
	 */
	public FFTTestAlgorithm( final RandomAccessibleInterval<T> image, final RandomAccessibleInterval<S> kernel,
			   final ImgFactory<T> imgFactory, final ImgFactory<S> kernelImgFactory,
			   final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, imgFactory, kernelImgFactory, fftImgFactory );
	}

	/**
	 * @param image
	 * @param kernel
	 * @param imgFactory
	 * @param kernelImgFactory
	 * @throws IncompatibleTypeException
	 */
	public FFTTestAlgorithm(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final ImgFactory<T> imgFactory,
			final ImgFactory<S> kernelImgFactory) throws IncompatibleTypeException
	{
		super(image, kernel, imgFactory, kernelImgFactory);
	}
	
	
	public FFTTestAlgorithm(final RandomAccessibleInterval<T> image, 
			final RandomAccessibleInterval<S> kernel,
			final RandomAccessibleInterval<T> output) throws IncompatibleTypeException
	{
		super(image, kernel, output);
	}

	/**
	 * @param image
	 * @param kernel
	 * @param fftImgFactory
	 */
	public FFTTestAlgorithm( final Img<T> image, final Img<S> kernel, final ImgFactory<ComplexFloatType> fftImgFactory )
	{
		super( image, kernel, fftImgFactory );
	}

	/**
	 * @param image
	 * @param kernel
	 * @throws IncompatibleTypeException
	 */
	public FFTTestAlgorithm( final Img< T > image, final Img< S > kernel ) throws IncompatibleTypeException
	{
		super( image, kernel );
	}
	
	/**
	 * Override frequency operation to implement a simple test algorithm, 
	 * it just multiplies image fft by two
	 * 
	 * @param a - input signal  
	 * @param b - input signal
	 */
	@Override
	protected void frequencyOperation( final Img< ComplexFloatType > a, final Img< ComplexFloatType > b ) 
	{
		final Cursor<ComplexFloatType> cursorA = a.cursor();
		final Cursor<ComplexFloatType> cursorB = b.cursor();
		
		while ( cursorA.hasNext() )
		{
			cursorA.fwd();
			cursorB.fwd();
			
			cursorA.get().mul(2.0);
		}
	}
}
