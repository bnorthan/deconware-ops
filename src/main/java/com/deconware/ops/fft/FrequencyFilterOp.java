package com.deconware.ops.fft;

import net.imagej.ops.Contingent;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.RealType;
import net.imagej.ops.Op;  

import com.deconware.algorithms.fft.filters.FrequencyFilter;

/**
 * 
 * @author bnorthan
 * base class for frequency filter operations
 * @param <T>
 */
public abstract class FrequencyFilterOp<T extends RealType<T>, S extends RealType<S>> implements Op, Contingent
{
	@Parameter
	Img<T> input;
	
	@Parameter
	Img<S> kernel;
	
	@Parameter(type = ItemIO.OUTPUT, required=false)
	Img<T> output;
	
	/**
	 * abstract function used to create the algorithm that will be applied
	 * @param region
	 * @return
	 */
	abstract protected FrequencyFilter<T,S> createAlgorithm();
	
	@Override
	public void run()
	{
		System.out.println("Convolution True North Wrapper");
			
		System.out.println("input dim: "+input.dimension(0)+" "+input.dimension(1)+" "+input.dimension(2));
		System.out.println("kernel dim: "+kernel.dimension(0)+" "+kernel.dimension(1)+" "+kernel.dimension(2));
			
		output = input.copy();
			
		System.out.println("output dim: "+output.dimension(0)+" "+output.dimension(1)+" "+output.dimension(2));
		
		FrequencyFilter<T,S> filter=createAlgorithm();
		
		filter.process();
		
		// check type of input
		if (input instanceof ImgPlus )
		{    
			output=new ImgPlus(filter.getResult(), "convolved");
		}
		else
		{
			output=filter.getResult();
		}
	}
	
	@Override
	public boolean conforms() 
	{
		return true;
	}
}
