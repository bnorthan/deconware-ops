package com.deconware.ops;

import net.imagej.ops.ComputerOp;

/**
 * Base interface for "slicewise" operations.
 * <p>
 * Implementing classes should be annotated with:
 * </p>
 * 
 * <pre>
 * @Plugin(type = Op.class, name = Slicewise.NAME,
 *   attrs = { @Attr(name = "aliases", value = Slicewise.ALIASES) })
 * </pre>
 * 
 * @author Christian Dietz
 * @author Martin Horn
 */
public interface SpatialWise<I, O> extends ComputerOp<I, O> {

	// NB: Marker interface.
	public static final String NAME = "spatialwise";
}