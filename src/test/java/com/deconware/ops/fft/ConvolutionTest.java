package com.deconware.ops.fft;

import com.deconware.ops.AbstractOpsTest;
import com.deconware.ops.phantom.PhantomTest;

import net.imagej.ops.Contingent;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.slicer.CroppedIterableInterval;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.meta.ImgPlus;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.junit.Test;
import org.junit.Assert;

public class ConvolutionTest extends AbstractOpsTest
{
	@Test
	public void FrequencyFilterMapTest()
	{
		int xSize=100;
		int ySize=100;
		int zSize=100;
		int channels=5;
		
		long totalsize=xSize*ySize*zSize*channels;
		
		int[] size=new int[]{xSize,ySize,zSize,channels};
		
		final int[] array =
				new int[(int) totalsize];

		Img<UnsignedIntType> image1=ArrayImgs.unsignedInts(array, xSize, ySize, zSize, channels);
		
		Op fmap=new FrequencyFilterMap<FloatType>();
		
		int[] axisIndices=new int[]{0,1,2};
		
		try
		{
			ops.run(fmap, new CroppedIterableInterval(ops, image1,
					axisIndices) );
		}
		catch(Exception e)
		{
			int stop=5;
		}
	}
	
	//@Test
	public void Convolution3DTest()
	{
		int xSize=100;
		int ySize=100;
		int zSize=100;
		
		long totalsize=xSize*ySize*zSize;
		
		int[] size=new int[]{xSize,ySize,zSize};
		
		final int[] array =
				new int[(int) totalsize];

		Img<UnsignedIntType> image1=ArrayImgs.unsignedInts(array, xSize, ySize, zSize);
		Img<UnsignedIntType> image2=ArrayImgs.unsignedInts(array, xSize, ySize, zSize);
		
		
		final int[] outArray = new int[(int)50*50*50];
		Img<UnsignedIntType> out=ArrayImgs.unsignedInts(array, 50, 50, 50);
		
		int[] location=new int[]{size[0]/2, size[1]/2, size[2]/2};
		
		ops.run("addsphere",  image1, location, 1.0, 5);
		ops.run("addsphere",  image2, location, 1.0, 5);
		
		Img<UnsignedIntType> convolved=(Img<UnsignedIntType>)ops.run("convolution", image1, image2, out);
		
		Float sum1=(Float)ops.run("sum", image1);
		Float sum2=(Float)ops.run("sum", image2);
		Float sum3=(Float)ops.run("sum", convolved);
		
		Assert.assertEquals(sum1*sum2, sum3, 0.00001);
	}
	
	//@Test
	public void Convolution4DTest()
	{
		int numChannels=5;
		
		Img<UnsignedIntType> image1=PhantomTest.makeMultiChannelPhantom(ops, 128, 128, 64, numChannels, 5, new UnsignedIntType());
		Img<UnsignedIntType> image2=PhantomTest.makeMultiChannelPhantom(ops, 128, 128, 64, numChannels, 5, new UnsignedIntType());

		// confirm we have a 4D image
		Assert.assertEquals(image1.numDimensions(), 4);
		Assert.assertEquals(image2.numDimensions(), 4);
		
		Assert.assertEquals(image1.dimension(3), numChannels);
		Assert.assertEquals(image2.dimension(3), numChannels);
	
		ops.run("spatial", image1);
		
		// wrap as image plus
		AxisType[] axes=new AxisType[4];
		axes[0]=Axes.X;
		axes[1]=Axes.Y;
		axes[2]=Axes.Z;
		axes[3]=Axes.CHANNEL;
		
		ImgPlus<UnsignedIntType> ip1=new ImgPlus(image1, "", axes);
		ImgPlus<UnsignedIntType> ip2=new ImgPlus(image1, "", axes);
		
		// convolve
		Img<UnsignedIntType> convolved=(Img<UnsignedIntType>)ops.run("convolution", ip1, ip2);
		
		System.out.println("convolved number dimensions: "+convolved.numDimensions());
		// confirm we have a 4D image
		Assert.assertEquals(4, convolved.numDimensions());
		Assert.assertEquals(convolved.dimension(3), numChannels);
		
		int[] axisIndices=new int[]{0,1,2};
		
		CroppedIterableInterval hsImage1= new CroppedIterableInterval(ops, image1,
				axisIndices);
		CroppedIterableInterval hsImage2= new CroppedIterableInterval(ops, image2,
				axisIndices);
		CroppedIterableInterval hsConvolved= new CroppedIterableInterval(ops, convolved,
				axisIndices);
		
		Cursor<RandomAccessibleInterval<?>> ci1=hsImage1.cursor();
		Cursor<RandomAccessibleInterval<?>> ci2=hsImage2.cursor();
		Cursor<RandomAccessibleInterval<?>> cc=hsConvolved.cursor();
			
		int numHyperSlices=0;
		while(cc.hasNext())
		{	
			ci1.fwd();
			ci2.fwd();
			cc.fwd();
			
			RandomAccessibleInterval<?> i1=ci1.get();
			RandomAccessibleInterval<?> i2=ci2.get();
			RandomAccessibleInterval<?> c=cc.get();
			
			Float sum1=(Float)ops.run("sum", i1);
			Float sum2=(Float)ops.run("sum", i2);
			Float sum3=(Float)ops.run("sum", c);
			
			Assert.assertEquals(sum1*sum2, sum3, 0.00001);
		}
	}
	
	
}
