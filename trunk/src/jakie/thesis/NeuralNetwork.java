package jakie.thesis;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import android.util.Log;

public class NeuralNetwork<T> {
	private MLP<T> neuralNet;
    private double maximumError = 0.0005;
    private int maximumIteration = 10000;
    Hashtable<T, double[]> TrainingSet=null;
    
    public NeuralNetwork(MLP<T> IBackPro, Hashtable<T, double[]> trainingSet){
        neuralNet = IBackPro;
        TrainingSet = trainingSet;
        neuralNet.initializeNetwork(TrainingSet);
    }
    
    public NeuralNetwork(MLP<T> IBackPro){
    	neuralNet = IBackPro;
    }
    
    //Not use this method in android
    public boolean train(){
        double currentError = 0;
        int currentIteration = 0;
        
        do
        {
            currentError = 0;
            T key;
            for(Enumeration<T> e=TrainingSet.keys();e.hasMoreElements();){
            	key=e.nextElement();
            	neuralNet.forwardPropagate(TrainingSet.get(key), key);
            	neuralNet.backPropagate();
            	currentError += neuralNet.getError();
            }
            
            currentIteration++;
            if(currentIteration % 5==0)
            	Log.d("NeutralNetwork.Class","Iteration: "+currentIteration+". Error: "+currentError);
            
        } while (currentError > maximumError && currentIteration < maximumIteration);

        if (currentIteration >= maximumIteration){
        	Log.d("NeutralNetwork.Class","Training Not Successful");
        	return false;//Training Not Successful
        }
        Log.d("NeutralNetwork.Class","Training Successful");
        return true;
    }
    
    public void recognize(double[] Input){
    	neuralNet.reset();	
        neuralNet.recognize(Input);
        
        Log.i("NeutralNetwork.Class","High: " + neuralNet.getMatchedHigh()+". Value: "+neuralNet.getOutputValueHight());
        Log.i("NeutralNetwork.Class","Low: " + neuralNet.getMatchedLow()+". Value: "+neuralNet.getOutputValueLow());
    }

    //Not use this method for android app
    public void saveNetwork(String path)throws Exception{
    	FileOutputStream fs=new FileOutputStream(path);
    	ObjectOutputStream os=new ObjectOutputStream(fs);
    	os.writeObject(this.neuralNet);
    	fs.close();os.close();
    }
    
    //Not use this method for android app
    @SuppressWarnings("unchecked")
	public void loadNetwork(String path) throws Exception{
    	FileInputStream fi=new FileInputStream(path);
    	ObjectInputStream ois=new ObjectInputStream(fi);
    	this.neuralNet=(MLP<T>) ois.readObject();
    	 	fi.close();ois.close();
    }
    
    //Use this method for android app
    @SuppressWarnings("unchecked")
	public void loadNetwork(InputStream in) throws Exception{
    	ObjectInputStream ois=new ObjectInputStream(in);
    	this.neuralNet=(MLP<T>) ois.readObject();
    	in.close();ois.close();
    }
}
