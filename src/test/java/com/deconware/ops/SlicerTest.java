package com.deconware.ops;

import net.imagej.ops.slicewise.Hyperslice;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

import org.junit.Assert;

public class SlicerTest extends AbstractOpsTest
{
	@Test
	public void LoopThroughHyperSlicesTest()
	{
		int xSize=40;
		int ySize=50;
		int numChannels=3;
		int numSlices=25;
		int numTimePoints=5;
		
		Img<UnsignedByteType> testImage = generateUnsignedByteTestImg(true,
				xSize, ySize, numChannels, numSlices, numTimePoints);
				
		int[] axisIndices=new int[3];
		
		// set up the axis so the resulting hyperslices are x,y,z and 
		// we loop through channels and time
		axisIndices[0]=0;
		axisIndices[1]=1;
		axisIndices[2]=3;
		
		Hyperslice hyperSlices= new Hyperslice(ops, testImage,
				axisIndices);
		
		Cursor<RandomAccessibleInterval<?>> c=hyperSlices.cursor();
			
		int numHyperSlices=0;
		while(c.hasNext())
		{
			
			c.fwd();
			numHyperSlices++;
			try
			{
				RandomAccessibleInterval<?> hyperSlice=c.get();
				
				Assert.assertEquals(3, hyperSlice.numDimensions());
				Assert.assertEquals(hyperSlice.dimension(0), xSize);
				Assert.assertEquals(hyperSlice.dimension(1), ySize);
				Assert.assertEquals(hyperSlice.dimension(2), numSlices);
				
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}
	
		Assert.assertEquals(numChannels*numTimePoints, numHyperSlices);
        
	}
}
