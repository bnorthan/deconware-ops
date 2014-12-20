
package com.deconware.ops.fft;

import org.scijava.ItemIO;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.ops.SpatialIterableInterval;

import net.imglib2.img.Img;
import net.imglib2.Cursor;
import net.imglib2.meta.ImgPlus;
import net.imglib2.meta.Axes;
import net.imagej.ops.Contingent;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.slicer.Hyperslice;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import com.deconware.algorithms.StaticFunctions;

@Plugin(type = Op.class, name = "frequencyfilter",
	priority = Priority.NORMAL_PRIORITY)
public class FrequencyFilterOpImgPlusImgPlus<T extends RealType<T>, S extends RealType<S>>
	implements Op, Contingent
{

	@Parameter(type = ItemIO.INPUT)
	ImgPlus<T> input;

	@Parameter(type = ItemIO.INPUT)
	ImgPlus<S> kernel;

	@Parameter(type = ItemIO.INPUT)
	FrequencyFilterOpRaiRai<T, S> filter;

	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<T> output;

	@Parameter
	private OpService opService;

	public void run() {

		// create the output based on input

		Img<T> outputImg = StaticFunctions.CreateNdImage(input);
		output = new ImgPlus<T>(outputImg, input);

		// get an iterable and a cursor to loop through volumes over the input
		Hyperslice inputIterable =
			SpatialIterableInterval.getSpatialIterableInterval(opService, input);
		Hyperslice outputIterable =
			SpatialIterableInterval.getSpatialIterableInterval(opService, output);

		Cursor inputCursor = inputIterable.cursor();
		Cursor outputCursor = outputIterable.cursor();

		// loop through the volumes over the input
		while (inputCursor.hasNext()) {
			inputCursor.fwd();
			outputCursor.fwd();

			// get the index (dimension number) for channel
			int inputChannelIndex = input.dimensionIndex(Axes.CHANNEL);
			int kernelChannelIndex = kernel.dimensionIndex(Axes.CHANNEL);

			RandomAccessibleInterval<S> kernelRAI = null;

			// if the kernel has channels then extract a hyperslice at the current
			// channel
			if ( (kernelChannelIndex != -1) && (inputChannelIndex!=-1) ) {
			// get the channel position of the input
				int currentChannelPosition =
					inputCursor.getIntPosition(inputChannelIndex);

				kernelRAI =
					Views.hyperSlice(kernel, kernelChannelIndex, currentChannelPosition);
			}
			// otherwise there is only one kernel so just use that
			else {
				kernelRAI = kernel;
			}

			// get input and output RAI
			RandomAccessibleInterval<T> inputRAI =
				(RandomAccessibleInterval<T>) inputCursor.get();
			RandomAccessibleInterval<T> outputRAI =
				(RandomAccessibleInterval<T>) outputCursor.get();

			// run it
			opService.run(filter, inputRAI, kernelRAI, outputRAI);

		}
	}

	public boolean conforms() {
		// check to make sure the input is an ImgPlus
		if (input instanceof ImgPlus) {
			return true;
		}
		
		return false;
	}

}
