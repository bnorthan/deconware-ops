
package com.deconware.ops.fft;

import net.imagej.ops.Op;
import net.imglib2.type.numeric.RealType;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.fft.filters.RichardsonLucyFilter;
import com.deconware.algorithms.fft.filters.IterativeFilter;

@Plugin(type = Op.class, name = "richardsonlucy",
	priority = Priority.NORMAL_PRIORITY)
public class RichardsonLucyOpRaiRai<T extends RealType<T>, S extends RealType<S>>
	extends IterativeFilterOpRaiRai<T, S>
{

	public IterativeFilter<T, S> createIterativeAlgorithm() {
		try {
			// create a RichardsonLucy filter
			return new RichardsonLucyFilter<T, S>(input, kernel, output);
		}
		catch (Exception ex) {
			return null;
		}
	}
}
