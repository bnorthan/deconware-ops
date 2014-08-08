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
import net.imagej.ops.slicer.CroppedIterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import com.deconware.algorithms.StaticFunctions;

@Plugin(type = Op.class, name = "frequencyfilter", priority = Priority.NORMAL_PRIORITY)
public class FrequencyFilterOpImgPlusImgPlus<T extends RealType<T>, S extends RealType<S>> implements Op, Contingent {

	@Parameter(type = ItemIO.INPUT)
	ImgPlus<T> input;
	
	@Parameter(type = ItemIO.INPUT)
	ImgPlus<S> kernel;
	
	@Parameter(type=ItemIO.INPUT)
	FrequencyFilterOpRaiRai<T, S> filter;
	
	@Parameter(type = ItemIO.OUTPUT)
	ImgPlus<T> output;
	
	@Parameter
	private OpService opService;
	
	//abstract protected FrequencyFilterOpRaiRai<T,S> createOp();
		
	public void run() {
		
		// create the output based on input
		
		Img<T> outputImg=StaticFunctions.CreateNdImage(input);
		output=new ImgPlus<T>(outputImg, input);
		
		// this is a little tricky -- need to loop over all spatial volumes of the input
		//   need to match each spatial input with the correct psf
		
		// PSF and image different dimensionality 
		
		// get a iterable and a cursor to loop through volumes over the input 
		CroppedIterableInterval inputIterable=SpatialIterableInterval.getSpatialIterableInterval(opService, input);
		CroppedIterableInterval outputIterable=SpatialIterableInterval.getSpatialIterableInterval(opService, output);		
	
		Cursor inputCursor=inputIterable.cursor();
		Cursor outputCursor=outputIterable.cursor();
		
		// loop through the volumes over the input
		while (inputCursor.hasNext()) {
			inputCursor.fwd();
			outputCursor.fwd();
				
			// get the index (dimension number) for channel
			int inputChannelIndex=input.dimensionIndex(Axes.CHANNEL);
			int kernelChannelIndex=kernel.dimensionIndex(Axes.CHANNEL);
			
			// get the channel position of the input
			int currentChannelPosition=inputCursor.getIntPosition(inputChannelIndex);
			
			RandomAccessibleInterval<S> kernelRAI=null;
			
			// if the input has channels then extract a hyperslice at the current channel
			if (kernelChannelIndex!=-1)
			{
				kernelRAI=Views.hyperSlice(kernel, kernelChannelIndex, currentChannelPosition );
			}
			// otherwise there is only one kernel so just use that
			else
			{
				kernelRAI=kernel;
			}
					
			// get input and output RAI
			RandomAccessibleInterval<T> inputRAI=(RandomAccessibleInterval<T>)inputCursor.get();
			RandomAccessibleInterval<T> outputRAI=(RandomAccessibleInterval<T>)outputCursor.get();
			
/*			System.out.println();
			System.out.print("input dimensions");
			for (int d=0;d<inputRAI.numDimensions();d++)
			{
				System.out.print(": "+inputRAI.dimension(d));
			}
			
			System.out.println();
			System.out.print("kernel dimensions");
			for (int d=0;d<kernelRAI.numDimensions();d++)
			{
				System.out.print(": "+kernelRAI.dimension(d));
			}*/
			
			// create the op to process the Random Accessible Intervals
		//	FrequencyFilterOpRaiRai<T,S> op=createOp();
			
			// run it
			opService.run(filter, inputRAI, kernelRAI, outputRAI);
			
			// get cursor RAI
			//System.out.println();
			//System.out.println("GETTING KERNEL");
			//RandomAccessibleInterval<T> kernelRAI=getKernelIntervalFromCursor(cursor);
			//System.out.println("=======================================");
			//System.out.println();
			
		}
	}
			
	public boolean conforms() {
			return true;
		}
	
}
