package com.deconware.ops.fft;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

import com.deconware.algorithms.fft.filters.IterativeFilter.FirstGuessType;

abstract public class IterativeFilterOp<T extends RealType<T>, S extends RealType<S>> extends FrequencyFilterOp
{
	@Parameter
	protected int iterations;
	
	@Parameter(type = ItemIO.INPUT, required=false)
	protected FirstGuessType firstGuessType;
}
