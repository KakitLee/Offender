package com.qingyangli.offender;




/**
 * <b>Covariance Singularity Exception</b>
 *
 * <p>Description: </p>
 * This class indicates that during the EM training algorithem of a Gaussian
 * Mixture Model one of the components got singular. A corrected list of points
 * meight be passed through this exception, such that one can rerun the EM
 * algorithm with the corrected list of points.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class CovarianceSingularityException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4866648642097556689L;
	PointList list = null;


	/**
	 * Constructs a  <code>CovarianceSingularityException</code>.
	 *
	 * @param list PointList the correcte list of points, or null if there is no
	 *                       corrected list
	 */
	public CovarianceSingularityException(PointList list)
	{
		super("Covariance matrix got singular;");
		this.list = list;
	}


	/**
	 * Returns the corrected list of points, or null if no corrected list of
	 * points is available.
	 *
	 * @return PointList the corrected list of points
	 */
	public PointList getCorrectedPointList()
	{
		return list;
	}
}
