package com.deconware.ops.psf;

import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.slicer.Hyperslice;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import net.imglib2.meta.Axes;

import org.scijava.ItemIO;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.psf.PsfGenerator;
import com.deconware.algorithms.psf.FlipPsfQuadrants;
import com.deconware.algorithms.psf.PsfGenerator.PsfType;
import com.deconware.algorithms.psf.PsfGenerator.PsfModel;

@Plugin(type = Op.class, name = Psf.NAME, priority = Priority.HIGH_PRIORITY + 10)
public class CosmPsfWrapperOp implements Psf
{
	@Parameter
	private OpService ops;
	
	 // TODO: because of limitation of COSMOS library PSFs need to be the same size
	 // in x and y for now.  This is OK.  Padding can be done to make the arrays the 
	 // same size
	 @Parameter 
	 long xySize;
	 
	 @Parameter
	 long zSize;
	 
	 /**
	  * x, y, and z spacing in nanometer
	  */
	 @Parameter
	 float[] spacing;
	 
	 /**
	  * emission wavelength in nanometer
	  */
	 @Parameter
	 float[] emissionWavelength;
	 
	 /**
	  * numerical aperture
	  */
	 @Parameter
	 float numericalAperture;
	 
	 /**
	  * actual immersion oil refractive index
	  */
	 @Parameter
	 float actualImmersionOilRefractiveIndex;
	 
	 /**
	  * actual specimen layer refractive index
	  */
	 @Parameter
	 float actualSpecimenLayerRefractiveIndex;
	 
	 /**
	  * depth in specimen layer in microns
	  */
	 @Parameter
	 float actualPointSourceDepthInSpecimenLayer;
	
	 /**
	  * PSF type (WIDEFIELD, TWO_PHOTON, CONFOCAL_CIRCULAR, CONFOCAL_LINE)
	  * 
	  * assume widefield if not specified
	  */
	 @Parameter(required=false)
	 PsfType psfType=PsfType.WIDEFIELD;
	 
	 @Parameter(required=false)
	 boolean centerPsf=true;
	 
	 /**
	  * PSF model (GIBSON_LANI, HAEBERLE)
	  * 
	  * assume Gibson Lanni if not specified
	  */
	 @Parameter(required=false)
	 PsfModel psfModel=PsfModel.GIBSON_LANI;
	
	 /**
	  * design immersion oil refractive index
	  * (optional, assume 1.51 if not specified)
	  */
	 @Parameter(required=false)
	 float designImmersionOilRefractiveIndex=1.51f;
	 
	 /**
	  * design specimen layer refracive index
	  * (optional, assume 1.51 if not specified) 
	  */
	 @Parameter(required=false)
	 float designSpecimenLayerRefractiveIndex=1.51f;
	 
	 
	 @Parameter(type = ItemIO.OUTPUT, required=false)
	 Img<FloatType> output;
	 
	 @Override
	 public void run()
	 {
		 System.out.println("size: "+xySize+" "+zSize);
		 System.out.println("spacing: "+spacing[0]+spacing[1]+spacing[2]);
		 System.out.println("emissionWavelength "+emissionWavelength[0]);
		 System.out.println("numericalAperture "+numericalAperture);
		 System.out.println("actualImmersionOilRefractiveIndex "+actualImmersionOilRefractiveIndex);
		 System.out.println("actualSpecimenLayerRefractiveIndex "+actualSpecimenLayerRefractiveIndex);
		 System.out.println("actualPointSourceDepthInSpecimenLayer "+actualPointSourceDepthInSpecimenLayer);
		 System.out.println("PSF type "+psfType);
		 System.out.println("PSF model "+psfModel);
		 System.out.println("designImmersionOilRefractiveIndex "+actualImmersionOilRefractiveIndex);
		 System.out.println("designSpecimenLayerRefractiveIndex "+actualSpecimenLayerRefractiveIndex);
		
		 if ( (output==null) && (emissionWavelength.length>1) )
		 {
			// create a planer image factory
			//ImgFactory<FloatType> imgFactory = new CellImgFactory<FloatType>();
			ImgFactory<FloatType> imgFactory = new ArrayImgFactory<FloatType>();
				
			// 4 dimensionsal image
			long[] dims=new long[4];
			dims[0]=xySize;
			dims[1]=xySize;
			dims[2]=zSize;
			dims[3]=emissionWavelength.length;
			
			// use the image factory to create an img
			output = imgFactory.create(dims, new FloatType());
			
			AxisType[] axes=new AxisType[4];
			
			axes[0]=Axes.X;
			axes[1]=Axes.Y;
			axes[2]=Axes.Z;
			axes[3]=Axes.CHANNEL;
			
			output = new ImgPlus<FloatType>(output, "psf", axes);
		 }
		 else if ( (output==null) && (emissionWavelength.length==1))
		 {
			// create a planer image factory
			ImgFactory<FloatType> imgFactory = new CellImgFactory<FloatType>();
				
			// 3 dimensionsal image
			long[] dims=new long[3];
			dims[0]=xySize;
			dims[1]=xySize;
			dims[2]=zSize;
			
			// use the image factory to create an img
			output = imgFactory.create(dims, new FloatType());
				
			AxisType[] axes=new AxisType[3];
				
			axes[0]=Axes.X;
			axes[1]=Axes.Y;
			axes[2]=Axes.Z;
				
			output = new ImgPlus<FloatType>(output, "psf", axes);
		 }
		 
		 
		 // set up the axis so the resulting hyperslices are x,y,z and 
		 // we loop through channels and time
		 int[] axisIndices=new int[]{0,1,2};  
		 	
		 Hyperslice hyperSlices= new Hyperslice(ops, output,
				 axisIndices);

		 Cursor<RandomAccessibleInterval<?>> c=hyperSlices.cursor();

		 int channel=0;
 
		 while(c.hasNext())
		 {
			 c.fwd();
			 
			 RandomAccessibleInterval<FloatType> interval=
					 (RandomAccessibleInterval<FloatType>)c.get();
			 
			

			 IterableInterval iterable=Views.iterable(interval);
		
			 Img<FloatType> psf=PsfGenerator.CallGeneratePsf(new int[]{(int)xySize,(int)xySize,(int)zSize}, 
					 spacing, 
					 emissionWavelength[channel], 
					 numericalAperture, 
					 designImmersionOilRefractiveIndex, 
					 designSpecimenLayerRefractiveIndex, 
					 actualImmersionOilRefractiveIndex,
					 actualSpecimenLayerRefractiveIndex,
					 actualPointSourceDepthInSpecimenLayer,
					 psfType ,
					 psfModel);
 
			 channel++;
 
			 if (centerPsf)
			 {
				 FlipPsfQuadrants.flip(psf, interval, new int[]{(int)xySize,(int)xySize,(int)zSize});
			 }
		 }	 
	 }
}
