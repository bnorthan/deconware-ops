package com.deconware.ops;

import net.imglib2.img.Img;
import net.imagej.ops.AbstractFunction;

public abstract class AbstractFunctionWithOutputInializer<I, O> extends AbstractFunction<I, O>{
		
	@Override
	public void run()
	{
		//if ( (in instanceof Img) && (out==null) )
		
		super.run();
	}
}
