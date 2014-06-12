package com.deconware.ops.metadata;

import net.imagej.ops.Op;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Op.class, name = "metadata", priority = Priority.HIGH_PRIORITY)
public class PrintMetaDataOp<T extends RealType<T>> implements Op 
{
	@Parameter
	Img<T> img;
	
	@Override
	public void run()
	{
		System.out.println("num dimensions: "+img.numDimensions());
		
		for (int d=0;d<img.numDimensions();d++)
		{
			System.out.println("dimensions "+d+" size: "+img.dimension(d));
		}
	}

}
