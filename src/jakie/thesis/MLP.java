package jakie.thesis;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import android.util.Log;

public class MLP<T> implements IBackPropagation<T>,Serializable{

	private static final long serialVersionUID = 6460486878068302623L;
	
	private int InputNum;
    private int HiddenNum;
    private int OutputNum;

    private Input[] InputLayer;
    private Hidden[] HiddenLayer;
    private Output<T>[] OutputLayer;

    private double learningRate = 0.3;
    private double alpha = 0.1;

    private T MatchedHigh;
	private double OutputValueHight;
	private T MatchedLow;
	private double OutputValueLow;
    
    @SuppressWarnings("unchecked")
	public MLP(int inputNum, int hiddenNum, int outputNum){
    	Log.d("MLP.Class", "Init MLP");
    	
    	InputNum = inputNum;
        HiddenNum = hiddenNum;
        OutputNum = outputNum;

        InputLayer = new Input[InputNum];
        for(int i=0;i<InputNum;i++)
        	InputLayer[i]=new Input();
        
        HiddenLayer = new Hidden[HiddenNum];
        for(int i=0;i<HiddenNum;i++)
        	HiddenLayer[i]=new Hidden();
        
        Output<T> x=new Output<T>();
        Output<T>[] temp=(Output<T>[])Array.newInstance(x.getClass(),1);
        OutputLayer = (Output<T>[])Array.newInstance(temp.getClass().getComponentType(),OutputNum);
               
        for(int i=0;i<OutputNum;i++)
        	OutputLayer[i]=new Output<T>();
    }
    
	@Override
	public void backPropagate() {
		int i, j;
        double total;
        
      //Fix HiddenNum Layer's Error
        for (i = 0; i < HiddenNum; i++){
            total = 0.0;
            for (j = 0; j < OutputNum; j++){
                total += HiddenLayer[i].Weights[j] * OutputLayer[j].Error;
            }
            HiddenLayer[i].Error = total * HiddenLayer[i].Output * (1 - HiddenLayer[i].Output);
        }
		
      //Update The Input Layer's Weights
        for (i = 0; i < HiddenNum; i++){
            for (j = 0; j < InputNum; j++){
            	InputLayer[j].Weights[i] += alpha * InputLayer[j].preDwt[i];
            	InputLayer[j].preDwt[i]=learningRate * HiddenLayer[i].Error * InputLayer[j].Value;
            	InputLayer[j].Weights[i] += InputLayer[j].preDwt[i];
            }
            HiddenLayer[i].Bias += alpha * HiddenLayer[i].preBias;
            HiddenLayer[i].preBias=learningRate * HiddenLayer[i].Error;
            HiddenLayer[i].Bias += HiddenLayer[i].preBias;
        }
        
      //Update The Hidden Layer's Weights
        for (i = 0; i < OutputNum; i++){
            for (j = 0; j < HiddenNum; j++){
            	HiddenLayer[j].Weights[i] +=
                    learningRate * OutputLayer[i].Error * HiddenLayer[j].Output;
            }
            OutputLayer[i].Bias += alpha * OutputLayer[i].preBias;
            OutputLayer[i].preBias=learningRate * OutputLayer[i].Error;
            OutputLayer[i].Bias += OutputLayer[i].preBias;
        }
	}

	@Override
	public double f(double x) {
		return (1 / (1 + Math.exp(-x)));
	}

	@Override
	public void forwardPropagate(double[] pattern, T output) {
		int i, j;
        double total = 0.0;
        String S1;
        String S2 = output.toString().substring(3);
		
      //Apply input to the network
        for (i = 0; i < InputNum; i++){
        	InputLayer[i].Value = pattern[i];
        }
        
      //Calculate The Hidden Layer's Inputs and Outputs
        for (i = 0; i < HiddenNum; i++){
            total = 0.0;
            for (j = 0; j < InputNum; j++){
                total += InputLayer[j].Value * InputLayer[j].Weights[i];
            }

            HiddenLayer[i].InputSum = total + HiddenLayer[i].Bias;
            HiddenLayer[i].Output = f(total);
        }
        
      //Calculate The Output Layer's Inputs, Outputs, Targets and Errors
        for (i = 0; i < OutputNum; i++){
            total = 0.0;
            for (j = 0; j < HiddenNum; j++)
            {
                total += HiddenLayer[j].Output * HiddenLayer[j].Weights[i];
            }

            OutputLayer[i].InputSum = total + OutputLayer[i].Bias;
            OutputLayer[i].output = f(total);

            S1 = OutputLayer[i].Value.toString().substring(3);


            OutputLayer[i].Target = S1.compareTo(S2) == 0 ? 1.0 : 0.0;
            OutputLayer[i].Error = (OutputLayer[i].Target - OutputLayer[i].output) * (OutputLayer[i].output) * (1 - OutputLayer[i].output);
        } 
	}

	@Override
	public double getError() {
		double total = 0.0;
        for (int j = 0; j < OutputNum; j++){
            total += Math.pow((OutputLayer[j].Target - OutputLayer[j].output), 2) / 2;
        }
        return total;
	}

	@Override
	public void initializeNetwork(Hashtable<T, double[]> TrainingSet) {
		int i, j;
        Random rand = new Random();
        for (i = 0; i < InputNum; i++){
            InputLayer[i].Weights = new double[HiddenNum];
            InputLayer[i].preDwt = new double[HiddenNum];
            for (j = 0; j < HiddenNum; j++){
            	InputLayer[i].Weights[j] = (double)(2*rand.nextInt(32767)) / 32767 - 1;
            	InputLayer[i].preDwt[j] = (double)0.0;
            }
        }
		
        for (i = 0; i < HiddenNum; i++)
        {
        	HiddenLayer[i].Weights = new double[OutputNum];
        	HiddenLayer[i].preDwt = new double[OutputNum];

        	HiddenLayer[i].Bias = (double)(2 * rand.nextInt(32767)) / 32767 - 1;
        	HiddenLayer[i].preBias = (double)0.0;
            for (j = 0; j < OutputNum; j++)
            {
            	HiddenLayer[i].Weights[j] = (double)(2 * rand.nextInt(32767)) / 32767 - 1;
            	HiddenLayer[i].preDwt[j] = (double)0.0;
            }
        }
        
        int k = 0;
        ArrayList<String> temp = new ArrayList<String>();
        String t;
        T key;
        for (Enumeration<T> e = TrainingSet.keys(); e.hasMoreElements();){
        	key =e.nextElement();
        	t = key.toString().substring(3);
        	
        	if (!temp.contains(t)){
                temp.add(t);
                OutputLayer[k].Value = key;
                OutputLayer[k].Bias = (double)(2 * rand.nextInt(32767)) / 32767 - 1;
                OutputLayer[k].preBias = (double)0.0;
                k++;
            }
        }
 	}

	@Override
	public void recognize(double[] Input) {
		int i, j;
        double total = 0.0;
        double max = -1;

        //Apply input to the network
        for (i = 0; i < InputNum; i++)
        {
            InputLayer[i].Value = Input[i];
        }
		
      //Calculate Input Layer's Inputs and Outputs
        for (i = 0; i < HiddenNum; i++)
        {
            total = 0.0;
            for (j = 0; j < InputNum; j++)
            {
                total += InputLayer[j].Value * InputLayer[j].Weights[i];
            }
            HiddenLayer[i].InputSum = total + HiddenLayer[i].Bias;
            HiddenLayer[i].Output = f(total);
        }
        
      //Find the [Two] Highest Outputs   
        for (i = 0; i < OutputNum; i++)
        {
            total = 0.0;
            for (j = 0; j < HiddenNum; j++)
            {
                total += HiddenLayer[j].Output * HiddenLayer[j].Weights[i];
            }
            OutputLayer[i].InputSum = total + OutputLayer[i].Bias;
            OutputLayer[i].output = f(total);
            if (OutputLayer[i].output > max)
            {
                MatchedLow = MatchedHigh;
                OutputValueLow = max;
                max = OutputLayer[i].output;
                MatchedHigh = OutputLayer[i].Value;
                OutputValueHight = max;
            }
        }
	}

	public void reset(){
		MatchedHigh=null;
		OutputValueHight=0;
		MatchedLow=null;
		OutputValueLow=0;
	}
	
	public T getMatchedHigh() {
		return MatchedHigh;
	}

	public double getOutputValueHight() {
		return OutputValueHight;
	}

	public T getMatchedLow() {
		return MatchedLow;
	}

	public double getOutputValueLow() {
		return OutputValueLow;
	}
}
