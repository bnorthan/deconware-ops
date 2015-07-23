package com.deconware.ops;

import net.imagej.ops.OpService;
import net.imagej.ops.slicewise.Hyperslice;

import java.util.ArrayList;

import net.imglib2.meta.Axes;
import net.imglib2.meta.ImgPlus;

/**
 * 
 * Iterates through non-spatial dimensions, return spatial hyperslice
 * 
 * If data is x,y,z,c,t iteraties through c and t returns x,y,z
 * 
 * If data is x,y,t iterates through t and returns x,y
 * 
 * @author bnorthan
 *
 */
public class SpatialIterableInterval 
{
	/**
	 * @param opService
	 * @param source
	 * @return - a cursor set up to loop through spatial dimensions
	 * 					 null if the cursor couldn't be created
	 */
	public static<T> Hyperslice getSpatialIterableInterval(final OpService opService,
			final ImgPlus<T> source)
	{
		ArrayList<Integer> spatialList=new ArrayList<Integer>();
		
		for (int d=0;d<source.numDimensions();d++)
		{
			if ( (source.axis(d).type()==Axes.X) ||
					(source.axis(d).type()==Axes.Y) ||
					(source.axis(d).type()==Axes.Z) )
			{
				spatialList.add(d);
			}
		}
		
		if (spatialList.size()==0)
		{
			return null;
		}
		
		int[] axesOfInterest=new int[spatialList.size()];
		
		for (int d=0;d<spatialList.size();d++)
		{
			axesOfInterest[d]=spatialList.get(d);
		}
		
		return new Hyperslice(opService, source, axesOfInterest);
	}
}
