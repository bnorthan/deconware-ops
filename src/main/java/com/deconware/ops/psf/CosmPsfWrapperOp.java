package com.deconware.ops.psf;

import net.imagej.ops.Op;
import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.ItemIO;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.psf.PsfGenerator;
import com.deconware.algorithms.psf.FlipPsfQuadrants;
import com.deconware.algorithms.psf.PsfGenerator.PsfType;
import com.deconware.algorithms.psf.PsfGenerator.PsfModel;
import com.deconware.ops.math.dot.DotProduct;

@Plugin(type = Op.class, name = Psf.NAME, priority = Priority.HIGH_PRIORITY + 10)
public class CosmPsfWrapperOp implements Psf
{
	 @Parameter 
	 long[] size;
	 
	 /**
	  * x, y, and z spacing in nanometer
	  */
	 @Parameter
	 float[] spacing;
	 
	 /**
	  * emission wavelength in nanometer
	  */
	 @Parameter
	 float emissionWavelength;
	 
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
		 System.out.println("size: "+size[0]+" "+size[1]+" "+size[2]);
		 System.out.println("spacing: "+spacing[0]+spacing[1]+spacing[2]);
		 System.out.println("emissionWavelength "+emissionWavelength);
		 System.out.println("numericalAperture "+numericalAperture);
		 System.out.println("actualImmersionOilRefractiveIndex "+actualImmersionOilRefractiveIndex);
		 System.out.println("actualSpecimenLayerRefractiveIndex "+actualSpecimenLayerRefractiveIndex);
		 System.out.println("actualPointSourceDepthInSpecimenLayer "+actualPointSourceDepthInSpecimenLayer);
		 System.out.println("PSF type "+psfType);
		 System.out.println("PSF model "+psfModel);
		 System.out.println("designImmersionOilRefractiveIndex "+actualImmersionOilRefractiveIndex);
		 System.out.println("designSpecimenLayerRefractiveIndex "+actualSpecimenLayerRefractiveIndex);
		

		 output=PsfGenerator.CallGeneratePsf(new int[]{(int)size[0],(int)size[1],(int)size[2]}, 
				 spacing, 
				 emissionWavelength, // multiply by 1000 because COSM expect nanos
				 numericalAperture, 
				 designImmersionOilRefractiveIndex, 
				 designSpecimenLayerRefractiveIndex, 
				 actualImmersionOilRefractiveIndex,
				actualSpecimenLayerRefractiveIndex,
				actualPointSourceDepthInSpecimenLayer,
				psfType ,
				psfModel);
		 
		if (centerPsf)
		{
			output=FlipPsfQuadrants.flip(output, output.factory(), new int[]{(int)size[0],(int)size[1],(int)size[2]});
		}
		 
		output=new ImgPlus(output, "psf");
		
	 }
}
