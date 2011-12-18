package jakie.thesis;

import java.util.Hashtable;

public interface IBackPropagation<T> {
	void backPropagate();
    double f(double x);
    void forwardPropagate(double[] pattern, T output);
    double getError();
    void initializeNetwork(Hashtable<T, double[]> TrainingSet);
    void recognize(double[] Input);
}
