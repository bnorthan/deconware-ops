
package com.deconware.ops.acceleration;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import com.deconware.algorithms.StaticFunctions;

/**
 * Vector Accelerator implements acceleration scheme described in Acceleration
 * of iterative image restoration algorithms David S.C. Biggs and Mark Andrews
 * Applied Optics, Vol. 36, Issue 8, pp. 1766-1775 (1997)
 * 
 * @author bnorthan
 * @param <T>
 */
//@Plugin(type = Op.class, name = "vectorAccelerate",
//	priority = Priority.NORMAL_PRIORITY)
public class VectorAccelerator<T extends RealType<T>> implements Accelerator<T>
{

	// TODO: should accelerator be an Op?? If so how do we keep track of current
	// state?
	// @Parameter
	// private OpService ops;

	// @Parameter
	// private RandomAccessibleInterval<T> yk_iterated_;

	/**
	 * The ImgFactory used to create images
	 */
	@Parameter
	private ImgFactory<T> imgFactory;

	Img<T> xkm1_previous = null;
	Img<T> yk_prediction = null;
	Img<T> hk_vector = null;

	Img<T> gk;
	Img<T> gkm1;

	double accelerationFactor = 0.0f;

	public VectorAccelerator(ImgFactory<T> imgFactory) {
		this.imgFactory = imgFactory;
		// initialize();
	}

/*	@Override
	public void run() {

		Accelerate(yk_iterated_);
	}*/

	public void initialize(RandomAccessibleInterval<T> yk_iterated) {
		if (yk_prediction == null) {
			long[] dimensions =
				new long[] { yk_iterated.dimension(0), yk_iterated.dimension(1),
					yk_iterated.dimension(2) };

			Type<T> type = Util.getTypeFromInterval(yk_iterated);
			yk_prediction = imgFactory.create(dimensions, type.createVariable());
			xkm1_previous = imgFactory.create(dimensions, type.createVariable());
			gk = imgFactory.create(dimensions, type.createVariable());
			hk_vector = imgFactory.create(dimensions, type.createVariable());

		}

	}

	@Override
	public Img<T> Accelerate(RandomAccessibleInterval<T> yk_iterated) {

		StaticFunctions.showStats(yk_iterated);
		// StaticFunctions.showStats(yk_prediction);
		
		boolean reset=false;

		// use the iterated prediction and the previous value of the prediction
		// to calculate the acceleration factor
		if (yk_prediction != null) {
			 
			System.out.println("PREDICTION------------------------------");
			System.out.println("stats yk_iterated");
			StaticFunctions.showStats(yk_iterated);
			
			System.out.println("stats yk_prediciton");
			StaticFunctions.showStats(yk_prediction);

			System.out.println("PREDICTION OVER------------------------------");
			
			accelerationFactor =
				computeAccelerationFactor(yk_iterated, yk_prediction);

			System.out.println("Acceleration Factor: " + accelerationFactor);

			if ((accelerationFactor < 0)) {
				// xkm1_previous = null;
				reset=true;
				gkm1 = null;
				accelerationFactor = 0.0;
			}

			if ((accelerationFactor > 1.0f)) {
				accelerationFactor = 1.0f;
			}
		}

		// current estimate for x is yk_iterated
		RandomAccessibleInterval<T> xk_estimate = yk_iterated;

		// calculate the change vector between x and x previous
		// if (xkm1_previous != null) {
		if ( (reset==false) && (xkm1_previous!=null) )  {
			// hk_vector=StaticFunctions.Subtract(xk_estimate, xkm1_previous);
			Subtract(xk_estimate, xkm1_previous, hk_vector);

			// make the next prediction
			yk_prediction =
				AddAndScale(xk_estimate, hk_vector, (float) accelerationFactor);
		}
		else {
			// can't make a prediction yet
			// yk_prediction=xk_estimate.copy();

			// TODO: Revisit where initialization should be done
			initialize(yk_iterated);
			
			System.out.println("RESET------------------------------");
			
			System.out.println("xk_estimate: stats");	
			StaticFunctions.showStats(xk_estimate);
			System.out.println("yk_prediction: stats");
			StaticFunctions.showStats(yk_prediction);
			

			Copy(xk_estimate, yk_prediction);
			System.out.println("yk_prediction: stats");
			StaticFunctions.showStats(yk_prediction);
			
			System.out.println("RESET_OVER------------------------------");
		}
		
		System.out.println("STATS------------------------------");
		
		// make a copy of the estimate to use as previous next time
		// xkm1_previous=xk_estimate.copy();
		Copy(xk_estimate, xkm1_previous);
		
		System.out.println("yk_iterated: stats");	
		StaticFunctions.showStats(yk_iterated);
		
		System.out.println("xk_estimate: stats");	
		StaticFunctions.showStats(xk_estimate);
		
		System.out.println("yk_prediction: stats");
		StaticFunctions.showStats(yk_prediction);

		System.out.println("STATS_OVER------------------------------");
		
		// HACK: TODO: look over how to transfer the memory
		// copy prediction
		Copy(yk_prediction, yk_iterated);

		// return the prediction
		return yk_prediction.copy();

	}

	double computeAccelerationFactor(RandomAccessibleInterval<T> yk_iterated,
		Img<T> yk)
	{
		// gk=StaticFunctions.Subtract(yk_iterated, yk_prediction);
		Subtract(yk_iterated, yk_prediction, gk);
		
		

		if (gkm1 != null) {
			System.out.println("Acceleration------------------");
			System.out.println("GK1 stats");
			StaticFunctions.showStats(gk);
			System.out.println("GKM1 stats");
			
			StaticFunctions.showStats(gkm1);
			
			double numerator = DotProduct(gk, gkm1);
			double denominator = DotProduct(gkm1, gkm1);

			gkm1 = gk.copy();
			
			System.out.println("numerator: "+numerator);
			System.out.println("denominator: "+numerator);
			
			
			System.out.println("END-- Acceleration------------------");
			

			return numerator / denominator;

		}
		

		gkm1 = gk.copy();

		return 0.0;

	}

	/*
	 * multiply inputOutput by input and place the result in input
	 */
	public double DotProduct(final Img<T> image1, final Img<T> image2) {
		final Cursor<T> cursorImage1 = image1.cursor();
		final Cursor<T> cursorImage2 = image2.cursor();

		double dotProduct = 0.0d;

		while (cursorImage1.hasNext()) {
			cursorImage1.fwd();
			cursorImage2.fwd();

			float val1 = cursorImage1.get().getRealFloat();
			float val2 = cursorImage2.get().getRealFloat();

			dotProduct += val1 * val2;
		}

		return dotProduct;
	}

	// TODO replace with Op
	// copy a into b
	protected void Copy(RandomAccessibleInterval<T> a,
		RandomAccessibleInterval<T> b)
	{
		
	

		final Cursor<T> cursorA = Views.iterable(a).cursor();
		final Cursor<T> cursorB = Views.iterable(b).cursor();
		
		int i=0;

		while (cursorA.hasNext()) {
			i++;
			cursorA.fwd();
			cursorB.fwd();

			cursorB.get().set(cursorA.get());
		}		
	}

	// TODO replace with op.
	protected void Subtract(RandomAccessibleInterval<T> a,
		RandomAccessibleInterval<T> b, RandomAccessibleInterval<T> output)
	{

		final Cursor<T> cursorA = Views.iterable(a).cursor();
		final Cursor<T> cursorB = Views.iterable(b).cursor();
		final Cursor<T> cursorOutput = Views.iterable(output).cursor();

		while (cursorA.hasNext()) {
			cursorA.fwd();
			cursorB.fwd();
			cursorOutput.fwd();

			cursorOutput.get().set(cursorA.get());
			cursorOutput.get().sub(cursorB.get());
		}
	}

	public Img<T> AddAndScale(final RandomAccessibleInterval<T> img1,
		final Img<T> img2, final float a)
	{
		// Img<T> out = img1.factory().create(img1, img1.firstElement());

		Type<T> outType = Util.getTypeFromInterval(img1);

		Img<T> out = imgFactory.create(img1, outType.createVariable());

		final Cursor<T> cursor1 = Views.iterable(img1).cursor();
		final Cursor<T> cursor2 = img2.cursor();
		final Cursor<T> cursorOut = out.cursor();

		while (cursor1.hasNext()) {
			cursor1.fwd();
			cursor2.fwd();
			cursorOut.fwd();

			float val1 = cursor1.get().getRealFloat();
			float val2 = cursor2.get().getRealFloat();

			float val3 = Math.max(val1 + a * val2, 0.0001f);

			cursorOut.get().setReal(val3);
		}

		return out;
	}
}
