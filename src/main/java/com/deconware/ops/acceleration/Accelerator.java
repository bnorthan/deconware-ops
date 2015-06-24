package com.deconware.ops.acceleration;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.img.Img;

public interface Accelerator <T extends RealType<T>>
{
	public Img<T> Accelerate(RandomAccessibleInterval<T> estimate);

}
