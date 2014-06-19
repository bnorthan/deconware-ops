package com.deconware.ops;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

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
		setUpDimensions(2);
	}
	
	@Test
	public void TestDeconvolutionProcess3D()
	{
		setUpDimensions(3);
	
		// create kernel
		Img<FloatType> psf=(Img<FloatType>)ops.run("psf", size[0], size[2], spacing, emw, NA, RI_lens_actual, RI_specimen_actual, depth);
		
		Object sum=ops.run("sum", psf);
		
		System.out.println("PSF sum is: "+sum+" "+sum.getClass());
		
/*		ops.convolve(out, in, kernel);
		
		result = ops.run("RichardsonLucy", out, in, kernel);
		
		result = ops.run("RichardsonLucy", in, kernel);
		
		result = ops.run("Convolution", in, kernel);
		
		Img<FloatType> imgOut=(Img<FloatType>)result;
	*/			
	}
	
	private void setUpDimensions(int dimensions)
	{
		// size in pixels
		size=new int[dimensions];
		
		// spacing in nanos
		spacing=new float[dimensions];
		
		// emw in nanos
		emw=new float[dimensions];
		
		for (int d=0;d<dimensions;d++)
		{
			size[d]=100;
			spacing[d]=100;
			emw[d]=400+100*d;
			
		}
	}

}
