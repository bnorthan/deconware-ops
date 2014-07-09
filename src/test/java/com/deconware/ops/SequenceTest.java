package com.deconware.ops;

import net.imglib2.img.Img;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.integer.UnsignedIntType;

import org.junit.Test;
import org.junit.Assert;

import com.deconware.ops.phantom.PhantomTest;

import net.imagej.ops.slicer.SlicewiseRAI2RAI;

public class SequenceTest extends AbstractOpsTest
{
	@Test
	public void SpatialTest()
	{
		int numChannels=5;
		
		Img<UnsignedIntType> image1=PhantomTest.makeMultiChannelPhantom(ops, 128, 128, 64, numChannels, 5, new UnsignedIntType());
		Img<UnsignedIntType> image2=PhantomTest.makeMultiChannelPhantom(ops, 128, 128, 64, numChannels, 5, new UnsignedIntType());

		// confirm we have a 4D image
		Assert.assertEquals(image1.numDimensions(), 4);
		Assert.assertEquals(image2.numDimensions(), 4);
		
		Assert.assertEquals(image1.dimension(3), numChannels);
		Assert.assertEquals(image2.dimension(3), numChannels);
		
		int[] axes=new int[]{0,1,2};
	
		IndexOpRaiRai index=new IndexOpRaiRai();
		
		try
		{
			ops.run("slicemapper", image1, image2, index, axes);
		}
		catch (Exception ex)
		{
			int stop=5;
		}
		
		System.out.println("----------------------------");
		
		AxisType[] ax = new AxisType[4];
    ax[0]=Axes.X;
    ax[1]=Axes.Y;
    ax[2]=Axes.Z;
    ax[3]=Axes.TIME;
    
    ImgPlus<UnsignedIntType> plus1=new ImgPlus<UnsignedIntType>(image1, "", ax);
    ImgPlus<UnsignedIntType> plus2=new ImgPlus<UnsignedIntType>(image2, "", ax);
		
		try
		{
			ops.run("spatialmapper", plus1, plus2, index);
		}
		catch (Exception ex)
		{
			int stop=5;
		}
		
	}
}
