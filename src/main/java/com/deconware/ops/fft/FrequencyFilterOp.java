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
