package jakie.thesis;

import java.io.Serializable;

public class Hidden implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6488121715231233575L;
	public double InputSum;
    public double Bias;
    public double preBias;
    public double Output;
    public double Error;
    public double[] Weights;
    public double[] preDwt;
}
