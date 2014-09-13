package com.deconware.ops.dimensions;

import net.imagej.ops.AbstractFunction;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.Axes;
import net.imglib2.meta.ImgPlus;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.dim.ExtendImageUtility;
import com.deconware.algorithms.dim.ExtendImageUtility.ExtensionType;
import com.deconware.algorithms.dim.ExtendImageUtility.BoundaryType;
import com.deconware.algorithms.fft.SimpleFFTFactory.FFTTarget;

@Plugin(type = Op.class, name = Extend.NAME, priority=Priority.HIGH_PRIORITY+1)
public class ExtendOpImgPlusImgPlus <T extends RealType<T>> extends
AbstractFunction<ImgPlus<T>, ImgPlus<T>>  {
	
	@Parameter
	OpService ops;
	
	@Parameter
	int extensionX;
	
	@Parameter
	int extensionY;
	
	@Parameter
	int extensionZ;
	
	@Parameter(required = false)
	ExtensionType extensionType;
	
	@Parameter(required = false)
	BoundaryType boundaryType;
	
	@Parameter(required = false)
	FFTTarget fftTarget;
	@Override
	
	public ImgPlus<T> compute(ImgPlus<T> input, ImgPlus<T> output)
	{
		int[] extension=new int[3];
		int[] axis=new int[3];
		
		int xPos=input.dimensionIndex(Axes.X);
		extension[0]=extensionX;
		axis[0]=xPos;
		
		int yPos=input.dimensionIndex(Axes.Y);
		extension[1]=extensionY;
		axis[1]=yPos;
		
		int zPos=input.dimensionIndex(Axes.Z);
		extension[2]=extensionZ;
		axis[2]=zPos;

		if (output==null)
		{
			ExtendImageUtility<T> utility=new ExtendImageUtility<T>(axis, extension, input, extensionType, boundaryType, fftTarget);

			long[] newDimensions=utility.getNewDimensions();
			AxisType[] axes=new AxisType[input.numDimensions()];
			
			for (int d=0;d<input.numDimensions();d++)
			{
				axes[d]=input.axis(d).type();
			}
			
			Img<T> outputImg=input.factory().create(newDimensions, input.firstElement());
			
			output=new ImgPlus<T>(outputImg, "", axes);
		}
		
		ExtendOpRaiRai<T> extend= new ExtendOpRaiRai<T>();
		
		extend.setExtension(extension);
		extend.setExtensionType(extensionType);
		extend.setBoundaryType(boundaryType);
		extend.setFFTTarget(fftTarget);
		
		ops.run("spatialwise", output, input, extend);
		
		return output; 
	}

}
