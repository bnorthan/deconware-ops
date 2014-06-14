package com.deconware.ops.dimensions;

import net.imagej.ops.Op;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;
import org.junit.Assert;

import com.deconware.ops.AbstractOpsTest;

import com.deconware.algorithms.dim.ExtendImageUtility.BoundaryType;
import com.deconware.algorithms.fft.SimpleFFTFactory.FFTTarget;

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
		
	}
}
