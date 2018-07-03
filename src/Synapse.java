
/**
 * The synapse connects 2 neurons. It contains a weight and a bias. The weights are randomly initialized by the Network.java .
 * @author WEN HONG
 *
 */public class Synapse {
	public Neuron before;
	public Neuron after;
	public double weight;
	
	public double bias;
	
	/**
	 * Constructor for the Synapse, called by Network.java
	 * @param a neuron closer to raw input
	 * @param b neuron closer to output
	 * @param weightz the weight to be initially held by the synapse.
	 */
	public Synapse(Neuron a, Neuron b, double weightz){
		before = a;
		after = b;
		
		weight = weightz;
		//bias = 0D;
		
		
		a.forward.add(this);
		b.back.add(this);
	}
	
	/**
	 * This method is called along with the previous neuron's activation function in Neuron.transform .
	 * The output from previous neuron is multiplied by Synapse weight and passed on to next neuron.
	 */
	public void channel(){
		after.weights += weight;
		after.input += before.output*weight;

	}
	
}
