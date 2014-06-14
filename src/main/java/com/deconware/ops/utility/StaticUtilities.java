package com.deconware.ops.utility;

import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.meta.AxisType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;

public class StaticUtilities 
{
	/**
	 * 
	 * checks if a RandomAccessibleInterval is instance of Img and if so uses it
	 * to create a new image
	 * 
	 * @param interval
	 * @param type
	 * @param dim
	 * @return
	 */
	public static<T extends RealType<T>, S extends RealType<S>> Img<S> CreateImg(RandomAccessibleInterval<T> interval, long[] dim, S type)
	{
		// check if the interval is an image
		if (interval instanceof Img)
		{
			Img<T> img=(Img<T>)(interval);
			
			Img<S> out=null;
			
			try
			{
				out=img.factory().imgFactory(type).create(dim, type);
			}
			catch (IncompatibleTypeException e)
			{
				return null;
			}
			
			return out;
		}
		else
		{
			return null;
		}
	}
	
	public static<T extends RealType<T>, S extends RealType<S>> ImgPlus<S> WrapAsImgPlus(ImgPlus<T> in, Img<S> imgToWrap, String name)
	{
		AxisType[] axes=new AxisType[in.numDimensions()];
		
		for (int d=0;d<in.numDimensions();d++)
		{
			axes[d]=in.axis(d).type();
		}
		
		return new ImgPlus<S>(imgToWrap, name, axes);
	}
}
