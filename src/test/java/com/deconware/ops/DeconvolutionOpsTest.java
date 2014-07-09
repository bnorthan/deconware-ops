package com.deconware.ops;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;
import org.junit.Assert;

public class DeconvolutionOpsTest extends AbstractOpsTest
{
	int[] size;
	float[] spacing;
	
	//emission wavelenth in nanos
	float[] emw;

	// numerical aperture
	float NA=1.4f;

	// actual oil refractive index
	float RI_lens_actual=1.51f;

	// actual specimen layer refractive index
	float RI_specimen_actual=1.51f;

	// depth below coverslip in microns
	float depth=10;

	@Test
	public void TestDeconvolutionProcess2D()
	{
		setUpVolumeDimensions(2);
	}
	
	@Test
	public void TestDeconvolutionProcess3D()
	{
		setUpVolumeDimensions(3);
		
		float emw=500;
	
		// create kernel
		Img<FloatType> psf=(Img<FloatType>)ops.run("psf", size[0], size[2], spacing, emw, NA, RI_lens_actual, RI_specimen_actual, depth);
		
		Float sum=(Float)ops.run("sum", psf);
		
		System.out.println("PSF sum is: "+sum+" "+sum.getClass());
		
		// assert the total sum of the PSF is 1.0
		Assert.assertEquals(1.0, sum, 0.001);
		
		Img<FloatType> image=(Img<FloatType>)ops.run("create", size,  new FloatType());
		
		// confirm we have a 3D image
		Assert.assertEquals(image.numDimensions(), 3);
		
		
/*		ops.convolve(out, in, kernel);
		
		result = ops.run("RichardsonLucy", out, in, kernel);
		
		result = ops.run("RichardsonLucy", in, kernel);
		
		result = ops.run("Convolution", in, kernel);
		
		Img<FloatType> imgOut=(Img<FloatType>)result;
	*/			
	}
	
	@Test
	public void TestDeconvolutionProcess4D()
	{
		setUpVolumeDimensions(3);
		
		int numChannels=3;
		
		// set up total dimensions
		int[] dimensions=new int[4];
		// first 3 dimensions are just the size
		dimensions[0]=size[0];
		dimensions[1]=size[1];
		dimensions[2]=size[2];
		// and the last one is number of channels
		dimensions[3]=numChannels;
		
		emw=new float[numChannels];
		
		float wavelength=400.0f;
		
		for (int c=0;c<numChannels;c++)
		{
			emw[c]=wavelength;
			wavelength+=100;
		}
		
		// create kernel
		Img<FloatType> psf=(Img<FloatType>)ops.run("psf", size[0], size[2], spacing, emw, NA, RI_lens_actual, RI_specimen_actual, depth);
		
		Float sum=(Float)ops.run("sum", psf);
		
		System.out.println("PSF sum is: "+sum+" "+sum.getClass());
		
		// assert the PSF has 4 dimensions and numChannels channels
		Assert.assertEquals(psf.numDimensions(), 4);
		Assert.assertEquals(psf.dimension(3), numChannels);
		
		// assert the total sum of the PSF is equal to numChannels (each PSF should have a sum of 1)
		Assert.assertEquals(sum, numChannels, 0.001);
		
		Img<FloatType> image=(Img<FloatType>)ops.run("create", dimensions,  new FloatType());
		
		// confirm we have a 4D image
		Assert.assertEquals(image.numDimensions(), 4);
		
		// convolve
		Img<FloatType> convolved=(Img<FloatType>)ops.run("convolution", image, psf);
		
		System.out.println("convolved number dimensions: "+convolved.numDimensions());
		// confirm we have a 4D image
		Assert.assertEquals(4, convolved.numDimensions());
	}
	
	@Test
	public void TestDeconvolutionProcess5D()
	{
		
	}
	
	private void setUpVolumeDimensions(int dimensions)
	{
		// size in pixels
		size=new int[dimensions];
		
		// spacing in nanos
		spacing=new float[dimensions];
		
		for (int d=0;d<dimensions;d++)
		{
			size[d]=100;
			spacing[d]=100;
		}
		
	}

}
