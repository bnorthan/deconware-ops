
package com.deconware.ops.fft;

import com.deconware.ops.AbstractOpsTest;
import com.deconware.ops.SpatialIterableInterval;
import com.deconware.ops.phantom.PhantomTest;

import net.imagej.ops.Op;
import net.imagej.ops.slicewise.Hyperslice;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.meta.ImgPlus;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;

public class ConvolutionTest extends AbstractOpsTest {

	@Test
	public void FrequencyFilterMapTest() {
		// define dimensions for a multi-channel dataset
		int xSize = 100;
		int ySize = 100;
		int zSize = 100;
		int channels = 5;
		int timePoints = 10;

		// create a 5d input image
		Img<UnsignedIntType> input5D =
			PhantomTest.makeMultiChannelMultiTimePointPhantom(ops, xSize, ySize,
				zSize, channels, timePoints, 10, new UnsignedIntType());

		// create a 4d input image (channels but no timepoints)
		Img<UnsignedIntType> input4D =
			PhantomTest.makeMultiChannelPhantom(ops, xSize, ySize, zSize, channels,
				5, new UnsignedIntType());

		// create a 4d kernel
		Img<UnsignedIntType> kernel =
			PhantomTest.makeMultiChannelPhantom(ops, xSize, ySize, zSize, channels,
				20, new UnsignedIntType());

		// define the axis types for the 5d data
		AxisType[] axInput = new AxisType[5];
		axInput[0] = Axes.X;
		axInput[1] = Axes.Y;
		axInput[2] = Axes.Z;
		axInput[3] = Axes.CHANNEL;
		axInput[4] = Axes.TIME;

		// define the axis types for the 4d data
		AxisType[] axKernel = new AxisType[4];
		axKernel[0] = Axes.X;
		axKernel[1] = Axes.Y;
		axKernel[2] = Axes.Z;
		axKernel[3] = Axes.CHANNEL;

		// wrap Imgs with ImgPlus, pass in the axis info.
		ImgPlus<UnsignedIntType> inputPlus4D =
			new ImgPlus<UnsignedIntType>(input4D, "", axInput);
		ImgPlus<UnsignedIntType> inputPlus5D =
			new ImgPlus<UnsignedIntType>(input5D, "", axInput);
		ImgPlus<UnsignedIntType> kernelPlus =
			new ImgPlus<UnsignedIntType>(kernel, "", axKernel);

		System.out.println("Test 4D:==========================");

		ImgPlus<UnsignedIntType> output = null;

		Hyperslice inputIterable =
			SpatialIterableInterval.getSpatialIterableInterval(ops, inputPlus4D);
		Hyperslice kernelIterable =
			SpatialIterableInterval.getSpatialIterableInterval(ops, kernelPlus);

		Cursor<RandomAccessibleInterval<?>> inputCursor = inputIterable.cursor();
		Cursor<RandomAccessibleInterval<?>> kernelCursor = kernelIterable.cursor();

		int numberVolumes = channels * timePoints;
		long[] inSums = new long[numberVolumes];

		LongType inSum = new LongType();
		LongType outSum = new LongType();

		// calculate sum of input and kernel
		int i = 0;
		while (inputCursor.hasNext()) {
			inputCursor.fwd();
			kernelCursor.fwd();

			RandomAccessibleInterval<UnsignedIntType> inputRAI =
				(RandomAccessibleInterval<UnsignedIntType>) inputCursor.get();
			RandomAccessibleInterval<UnsignedIntType> kernelRAI =
				(RandomAccessibleInterval<UnsignedIntType>) kernelCursor.get();

			ops.run("sum", inSum, inputRAI);
			inSums[i] = inSum.getIntegerLong();
			
			System.out.println(inSum);
			i++;
		}

		// run the test op
		try {
			output =
				(ImgPlus<UnsignedIntType>) (ops.run("frequencyfilter", inputPlus4D,
					kernelPlus, new FFTTestOpRaiRai()));
		}
		catch (Exception e) {
		}

		
		Hyperslice outputIterable =
			SpatialIterableInterval.getSpatialIterableInterval(ops, output);
		Cursor<RandomAccessibleInterval<?>> outputCursor = outputIterable.cursor();

		// the test op just multiplies the fft by 2 so verify output is doubled
		i = 0;
		while (outputCursor.hasNext()) {
			outputCursor.fwd();

			RandomAccessibleInterval<UnsignedIntType> outRAI =
				(RandomAccessibleInterval<UnsignedIntType>) outputCursor.get();

			ops.run("sum", outSum, outRAI);
			
			System.out.println(outSum);

			assertEquals(2 * inSums[i], outSum.getIntegerLong());
			i++;

		}

	}

	@Test
	public void Convolution3DTest() {
		int xSize = 100;
		int ySize = 100;
		int zSize = 100;

		long totalsize = xSize * ySize * zSize;

		int[] size = new int[] { xSize, ySize, zSize };

		final int[] array1 = new int[(int) totalsize];
		final int[] array2 = new int[(int) totalsize];

		Img<UnsignedIntType> image1 =
			ArrayImgs.unsignedInts(array1, xSize, ySize, zSize);
		Img<UnsignedIntType> image2 =
			ArrayImgs.unsignedInts(array2, xSize, ySize, zSize);

		final int[] outArray = new int[(int) xSize * ySize * zSize];
		Img<UnsignedIntType> out =
			ArrayImgs.unsignedInts(outArray, xSize, ySize, zSize);

		int[] location = new int[] { size[0] / 2, size[1] / 2, size[2] / 2 };

		ops.run("addsphere", image1, location, 1.0, 5);
		ops.run("addsphere", image2, location, 1.0, 5);
		// ops.run("addsphere", image2, location, 1.0, 5);

		// Img<UnsignedIntType> convolved =
		ops.run("convolution", image1, image2, out);

		Float sum1 = (Float) ops.run("sum", image1);
		Float sum2 = (Float) ops.run("sum", image2);
		Float sum3 = (Float) ops.run("sum", out);

		System.out.println(sum1 + "::::" + sum2 + "::::" + sum3);

		Assert.assertEquals(sum1 * sum2, sum3, 0.00001);
	}

	@Test
	public void Convolution4DTest() {
		int numChannels = 5;

		Img<UnsignedIntType> image1 =
			PhantomTest.makeMultiChannelPhantom(ops, 128, 128, 64, numChannels, 5,
				new UnsignedIntType());
		Img<UnsignedIntType> image2 =
			PhantomTest.makeMultiChannelPhantom(ops, 128, 128, 64, numChannels, 5,
				new UnsignedIntType());

		// confirm we have a 4D image
		Assert.assertEquals(image1.numDimensions(), 4);
		Assert.assertEquals(image2.numDimensions(), 4);

		Assert.assertEquals(image1.dimension(3), numChannels);
		Assert.assertEquals(image2.dimension(3), numChannels);

		// wrap as image plus
		AxisType[] axes = new AxisType[4];
		axes[0] = Axes.X;
		axes[1] = Axes.Y;
		axes[2] = Axes.Z;
		axes[3] = Axes.CHANNEL;

		ImgPlus<UnsignedIntType> ip1 = new ImgPlus(image1, "", axes);
		ImgPlus<UnsignedIntType> ip2 = new ImgPlus(image1, "", axes);

		// convolve
		Img<UnsignedIntType> convolved =
			(ImgPlus<UnsignedIntType>) (ops.run("frequencyfilter", ip1, ip2,
				new ConvolutionRaiRai()));

		System.out.println("convolved number dimensions: " +
			convolved.numDimensions());
		// confirm we have a 4D image
		Assert.assertEquals(4, convolved.numDimensions());
		Assert.assertEquals(convolved.dimension(3), numChannels);

		int[] axisIndices = new int[] { 0, 1, 2 };

		Hyperslice hsImage1 =
			new Hyperslice(ops, image1, axisIndices);
		Hyperslice hsImage2 =
			new Hyperslice(ops, image2, axisIndices);
		Hyperslice hsConvolved =
			new Hyperslice(ops, convolved, axisIndices);

		Cursor<RandomAccessibleInterval<?>> ci1 = hsImage1.cursor();
		Cursor<RandomAccessibleInterval<?>> ci2 = hsImage2.cursor();
		Cursor<RandomAccessibleInterval<?>> cc = hsConvolved.cursor();

		int numHyperSlices = 0;
		while (cc.hasNext()) {
			ci1.fwd();
			ci2.fwd();
			cc.fwd();

			RandomAccessibleInterval<?> i1 = ci1.get();
			RandomAccessibleInterval<?> i2 = ci2.get();
			RandomAccessibleInterval<?> c = cc.get();

			Float sum1 = (Float) ops.run("sum", i1);
			Float sum2 = (Float) ops.run("sum", i2);
			Float sum3 = (Float) ops.run("sum", c);

			System.out.println("sums " + ":" + sum1 + ":" + sum2 + ":" + sum3);

			Assert.assertEquals(sum1 * sum2, sum3, 0.00001);
		}
	}
}
