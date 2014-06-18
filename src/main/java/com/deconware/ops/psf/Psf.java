package com.deconware.ops.psf;

import net.imagej.ops.Op;
import net.imglib2.type.numeric.RealType;

public interface Psf<T extends RealType<T>> extends Op
{
	String NAME = "psf";
}

