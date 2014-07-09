package com.deconware.ops;

import java.util.Iterator;

import net.imagej.ops.AbstractFunction;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.ImgPlus;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Op.class, name = "index", priority=Priority.HIGH_PRIORITY+1)
public class IndexOpImgPlusImgPlus <T extends RealType<T>> extends
AbstractFunction<ImgPlus<T>, ImgPlus<T>>  {
	
	@Parameter
	OpService ops;
		
	public ImgPlus<T> compute(ImgPlus<T> input, ImgPlus<T> output)
	{
		ExtendOpRaiRai extend= new ExtendOpRaiRai();
		
		extend.setExtension(extension);
		extend.setBoundaryType(boundaryType);
		extend.setFFTTarget(fftTarget);
		
		if (output==null)
		{
			ExtendImageUtility utility=new ExtendImageUtility(extension, input, boundaryType, fftTarget);

			long[] newDimensions=utility.getNewDimensions();
			AxisType[] axes=new AxisType[input.numDimensions()];
			
			for (int d=0;d<input.numDimensions();d++)
			{
				axes[d]=input.axis(d).type();
			}
			
			Img<T> outputImg=input.factory().create(newDimensions, input.firstElement());
			
			output=new ImgPlus<T>(outputImg, "", axes);
		}
		
		ops.run("spatialmapper", input, output, extend);
		
		return output; 
	}