package com.deconware.ops.dimensions;

import net.imagej.ops.Op;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.Axes;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;
import org.junit.Assert;

import com.deconware.ops.AbstractOpsTest;
import com.deconware.algorithms.dim.ExtendImageUtility.BoundaryType;
import com.deconware.algorithms.fft.SimpleFFTFactory.FFTTarget;

import edu.mines.jtk.sgl.Axis;

public class ExtensionTest extends AbstractOpsTest
{
	@Test
	public void ExtendHyperSlicesTest()
	{
		int xSize=40;
		int ySize=50;
		int numChannels=3;
		int numSlices=25;
		int numTimePoints=5;
		
		Img<UnsignedByteType> testImage = generateUnsignedByteTestImg(true,
				xSize, ySize, numChannels, numSlices, numTimePoints);
				
		int[] axisIndices=new int[3];
		int[] extensions=new int[3];
		
		// set up the axis so the resulting hyperslices are x,y,z and 
		// we loop through channels and time
		axisIndices[0]=0;
		axisIndices[1]=1;
		axisIndices[2]=3;
		
		extensions[0]=20;
		extensions[1]=20;
		extensions[2]=10;
		
		Op extend=new ExtendOp();
		
		RandomAccessibleInterval<UnsignedByteType> out=
				(RandomAccessibleInterval<UnsignedByteType>)ops.run(extend, null, testImage, 
						axisIndices, extensions, BoundaryType.ZERO, FFTTarget.MINES_SPEED);
		
		Assert.assertEquals(out.dimension(0), xSize+2*extensions[0]);
		Assert.assertEquals(out.dimension(1), ySize+2*extensions[1]);
		Assert.assertEquals(out.dimension(2), numChannels);
		Assert.assertEquals(out.dimension(3), numSlices+2*extensions[2]);
		Assert.assertEquals(out.dimension(4), numTimePoints);
		
		AxisType[] axes=new AxisType[testImage.numDimensions()];
		
		axes[0]=Axes.X;
		axes[1]=Axes.Y;
		axes[2]=Axes.CHANNEL;
		axes[3]=Axes.Z;
		axes[4]=Axes.TIME;
		
		ImgPlus<UnsignedByteType> testPlus=new ImgPlus<UnsignedByteType>(testImage, "test", axes);
		
		Op extendSpatial=new ExtendOpImgPlusImgPlus();
		
		ImgPlus<UnsignedByteType> outPlus=null;
		
		outPlus=(ImgPlus<UnsignedByteType>)ops.run(extendSpatial, null, testPlus, extensions[0], extensions[2], BoundaryType.ZERO, FFTTarget.MINES_SPEED); 
		
		Assert.assertEquals(outPlus.dimension(0), xSize+2*extensions[0]);
		Assert.assertEquals(outPlus.dimension(1), ySize+2*extensions[0]);
		Assert.assertEquals(outPlus.dimension(2), numChannels);
		Assert.assertEquals(outPlus.dimension(3), numSlices+2*extensions[2]);
		Assert.assertEquals(outPlus.dimension(4), numTimePoints);
	}
}
