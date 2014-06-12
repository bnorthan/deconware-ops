package com.deconware.ops.metadata;

import net.imagej.ops.Op;
import net.imagej.ops.OpService;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

import net.imglib2.img.Img;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;

import net.imagej.ops.slicer.CroppedIterableInterval;

@Plugin(type = Op.class, name = "metadata", priority = Priority.HIGH_PRIORITY)
public class PrintMetaDataOpSliceWise <T extends RealType<T>> implements Op 
{
	@Parameter
	private OpService opService;

	@Parameter
	Img<T> input;
	
	@Parameter
	int[] axisIndices;
	
	@Override
	public void run()
	{
		CroppedIterableInterval hyperSlices= new CroppedIterableInterval(opService, input,
				axisIndices);
		
		Cursor<RandomAccessibleInterval<?>> c=hyperSlices.cursor();
			
		while(c.hasNext())
		{
			c.fwd();
			try
			{
				RandomAccessibleInterval<?> slice=c.get();
			
				opService.run("metadata", slice);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}
		
	
	}
}
