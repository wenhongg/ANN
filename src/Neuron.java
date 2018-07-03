import java.util.ArrayList;
import java.util.List;

/**
 * The neurons in this basic ANN hold the SIGMOID activation function.  
 * @author WEN HONG
 *
 */
public class Neuron {
	public ArrayList<Synapse> forward;
	public ArrayList<Synapse> back;
	
	public double input;
	public double output;
	
	//Sum of input synapse weights
	public double weights;
	public double error;
	
	/**
	 * Neurons are generated in this constructor with error and bias all set at 0.
	 */
	public Neuron() {
		forward = new ArrayList<Synapse>();
		back = new ArrayList<Synapse>();
		weights = 0D;
		error = 0D;
		input = 0D;
		output = 0D;
	}
	
	private double sgm(double input) {
		// Activation function
		double ans = 1/(1+ Math.pow(Math.E, -input));
		return ans;
	}
	
	/**
	 * 
	 * This method transforms the input using the activation function.
	 * It also calls the synapse channeling method to multiply the weight and transfer new value on to next neuron.
	 */
	public void transform(){
		output = sgm(input);
		for(Synapse np: forward) {
			np.channel();
		}
	}
	
	/**
	 * Backpropagation taking place in a single unit. This method is coordinated by the Network.backpropagate method.
	 * @param learningrate 
	 */
	public void revise(double learningrate){
		//Updates the errors for previous layer neurons and update synapse weights
		// The math behind backpropagation takes place here.
		for(Synapse np : back) {
			np.before.error += np.before.output*(1-np.before.output)*error*np.weight;
			np.weight += learningrate*error*np.before.output;
			//np.bias += learningrate*output*(1-output)*error;
		}
		
		
		//Reset input/output to 0
		input = 0D;
		output = 0D;
		error = 0D;
		weights = 0D;
	}
}
