package com.deconware.ops.fft;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Cursor;
import net.imagej.ops.Contingent;
import net.imagej.ops.Op;

@Plugin(type = Op.class, name = "frequencymap", priority = Priority.NORMAL_PRIORITY+1)
public class FrequencyFilterMap<T> implements Op, Contingent 
{
	@Parameter
	private IterableInterval<RandomAccessibleInterval<T>> input;
	
	public void run()
	{
		Cursor<RandomAccessibleInterval<T>> c=input.cursor();
		
		while (c.hasNext())
		{
			c.fwd();
			
			System.out.println("---VOTE--");
			System.out.println("---NORRIS--");
				
			System.out.println("----------");
			
			for (int d=0;d<input.numDimensions();d++)
			{
				System.out.print(c.getIntPosition(d)+": ");
			}
			
			System.out.println("----------");
			
			RandomAccessibleInterval<T> ra=c.get();
			
			for (int d=0;d<ra.numDimensions();d++)
			{
				System.out.print(ra.dimension(d)+": ");
			}
			
			System.out.println();
			
			System.out.println("--VOTE---");
			
			System.out.println("---EE----");
		}
		
	}
	
	public boolean conforms()
	{
		return true;
	}

}
