
package com.deconware.ops.fft;

import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

import com.deconware.algorithms.fft.filters.FrequencyFilter;
import com.deconware.algorithms.fft.filters.IterativeFilter;
import com.deconware.algorithms.fft.filters.IterativeFilter.ConvolutionStrategy;
import com.deconware.algorithms.fft.filters.IterativeFilter.AccelerationStrategy;


@Plugin(type = Op.class, name = "convolution",
	priority = Priority.NORMAL_PRIORITY)
public abstract class IterativeFilterOpRaiRai<T extends RealType<T>, S extends RealType<S>>
	extends FrequencyFilterOpRaiRai<T, S>
{
	@Parameter
	int iterations;
	
	@Parameter(required=false)
	protected AccelerationStrategy accelerationStrategy=AccelerationStrategy.NONE;
	
	@Parameter(required=false)
	protected ConvolutionStrategy convolutionStrategy=ConvolutionStrategy.CIRCULANT;
	
	@Parameter(required=false)
	protected long imageWindowX=-1;
	
	@Parameter(required=false)
	protected long imageWindowY=-1;
	
	@Parameter(required=false)
	protected long imageWindowZ=-1;
	
	@Parameter
	OpService ops;
	
	protected FrequencyFilter<T,S> createAlgorithm()
	{
		System.out.println("Convolution Strategy is: "+convolutionStrategy);
		System.out.println("Acceleration Strategy is: "+accelerationStrategy);
		System.out.println("Image Window X: "+imageWindowX);
		System.out.println("Image Window Y: "+imageWindowY);
		System.out.println("Image Window Z: "+imageWindowZ);
		
		IterativeFilter<T,S> iterativeFilter=createIterativeAlgorithm();
		
		iterativeFilter.setMaxIterations(iterations);
		iterativeFilter.setAccelerationType(accelerationStrategy);
		
		if ( (this.convolutionStrategy.equals(ConvolutionStrategy.SEMI_NONCIRCULANT)) ||
				(this.convolutionStrategy.equals(ConvolutionStrategy.NON_CIRCULANT)) )
		{
			System.out.println("seminoncirculant deconvolution strategy");
			
			System.out.println("image window x "+imageWindowX);
			System.out.println("image window y "+imageWindowY);
			System.out.println("image window z "+imageWindowZ);
			
			long[] k=new long[3];
			k[0]=imageWindowX;
			k[1]=imageWindowY;
			k[2]=imageWindowZ;
			
			
		
			iterativeFilter.setSemiNonCirculantConvolutionStrategy(k);
		}
		
		return iterativeFilter;
	}
	
	protected abstract IterativeFilter<T,S> createIterativeAlgorithm();
	
}
