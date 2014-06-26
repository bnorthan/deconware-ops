package com.deconware.ops.psf;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

import org.junit.Test;
import org.junit.Assert;

import com.deconware.ops.AbstractOpsTest;

public class PsfTest extends AbstractOpsTest
{
	@Test
	public void psfTest()
	{
		float[] spacing=new float[]{100f, 100f, 300f};
		
		//emission wavelenth in nanos
		float[] emw=new float[]{300, 400, 500};

		// numerical aperture
		float NA=1.4f;

		// actual oil refractive index
		float RI_lens_actual=1.51f;

		// actual specimen layer refractive index
		float RI_specimen_actual=1.51f;

		// depth below coverslip in microns
		float depth=10;
		
		int xySize=128;
		int zSize=64;
		
		// create single channel kernel
		Img<FloatType> psf=(Img<FloatType>)ops.run("psf", xySize, zSize, spacing, 300, NA, RI_lens_actual, RI_specimen_actual, depth);
		
		// should have 3 dimensions
		Assert.assertEquals(psf.numDimensions(), 3);
		
		FloatType maxx=new FloatType();
		
		ops.run("max", maxx, psf);
		
		System.out.println("maxx: "+maxx);
		
		FloatType max=new FloatType();
				
		// create multi channel kernel (pass in 3 element array of for emw)
		Img<FloatType> psf2=(Img<FloatType>)ops.run("psf", 128, 64, spacing, emw, NA, RI_lens_actual, RI_specimen_actual, depth);
		
		Assert.assertEquals(psf2.numDimensions(), 4);
		Assert.assertEquals(psf2.dimension(3), 3);
	}
}

