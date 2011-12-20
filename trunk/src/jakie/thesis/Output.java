package jakie.thesis;

import java.io.Serializable;

public class Output<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4190293966725680975L;
	public double InputSum;
    public double Bias;
    public double preBias;
    public double output;
    public double Error;
    public double Target;
    public T Value;
}
