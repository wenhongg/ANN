import java.util.Random;

/**
	SIGMOID BASED Neural network which supports a fixed number of hidden nodes per layer. 
	Layer size and number of layers can be specified.

	Data class parses CSV into 2D array. (Removes header first.)
	Neuron and synapse classes are called to create the elements of the network.
	
	The network is able to return good accuracy. It has a good probability of doing so given good hyperparameters.
	However, no optimization or regularization is implemented in this version which limits its success.
	
	Apart from the above methods, to improve the accuracy one can:
	1. Tweak the learning rate
	2. Modify number of hidden layers and the nodes per layer
	3. Normalize data in Network.class's feedforward function
	4. Modify number of steps.
	
	@author WEN HONG
*/

public class Network {
	/**
	 * Number of layers in network
	 */
	int layers;
	/**
	 * Number of neurons in each layer
	 */
	int layersize;
	
	int syncount;
	int neucount;
	
	/**
	 * Input layer of neurons
	 */
	Neuron[] input;
	/**
	 * Output layer of neurons
	 */
	Neuron[] output;
	/**
	 * Neurons in the intermediate layers. Each layer has a standard size. 
	 */
	Neuron[][] hidden;
	
	/**
	 * Labels (correct answers) go here.
	 */
	double[] labelexpect;
	
	Random random;
	
	/**
	 * Construct the neural network and produce the links between neurons and synapses.
	 * @param inputnodes number of input nodes (usually number of input features)
	 * @param layerz number of HIDDEN layers to use
	 * @param node number of neurons in each layer
	 * @param outputnodes number of output nodes (usually the number of labels to predict)
	 */
	public Network(int inputnodes, int layerz,int node, int outputnodes) {
		syncount = 0; 
		neucount = 0;
		
		random = new Random();
		layers = layerz;
		layersize = node;
		input = new Neuron[inputnodes];
		output = new Neuron[outputnodes];
		hidden = new Neuron[layerz][node];
		
		labelexpect = new double[outputnodes];
		
		//Initialize all neurons & synapses
		//Synapse weights are initialized with bias = 0 and -1<weight<1.
		for(int i=0; i<layers; i+=1) {
			for(int j=0;j<layersize;j+=1) {
				hidden[i][j] = new Neuron();
				neucount +=1;
			}
		}
		
		for(int j=0;j<inputnodes;j+=1) {
			input[j] = new Neuron();
			neucount +=1;
			for(int i=0; i<layersize;i+=1) {
				double inn = (random.nextInt(100)-50)/50D;
				System.out.println(inn);
				//System.out.println(inn);
				Synapse x = new Synapse(input[j],hidden[0][i], inn);
				syncount +=1;
			}
			
		}
		for(int j=0;j<outputnodes;j+=1) {
			output[j] = new Neuron();
			neucount +=1;
			for(int i=0; i<layersize;i+=1) {
				double inn = (random.nextInt(100)-50)/50D;
				//System.out.println(inn);
				Synapse x = new Synapse(hidden[layers-1][i],output[j], inn);
				syncount +=1;
			}
			
		}

		System.out.println("Output: " + output[0].input);
		for(int i=0; i<layers-1; i+=1) {
			for(int j=0;j<layersize;j+=1) {
				for(int k=0; k<layersize; k+=1) {
					double inn = (random.nextInt(100)-50)/50D;
					Synapse x = new Synapse(hidden[i][j],hidden[i+1][k],inn);
					syncount +=1;
					
				}
				
			}
		}
		// Must have minimum 1 hidden layer.

		System.out.println("Network created.");
		System.out.println(neucount + " neurons created; " + syncount + " synapses formed.");
		
	}
	/**
	 * Coordinates the feedforward process. Predicted output is produced.
	 * @param dropout dropout regularization takes place if true.
	 */
	public void feedforward(boolean dropout) {
		
		for(Neuron neu: input) {
			// No activation for input neurons.
			// Can use below function to normalize data. (As I have divided all raw data input by 50.)
			neu.output = neu.input/50;
			for(Synapse np: neu.forward) {
				np.channel();
			}
			 
		}
		for(int i=0; i<layers; i+=1) {
			int dropped=0;
			boolean dropoutlayer = dropout;
			for(int j=0;j<layersize;j+=1) {
				Integer a = random.nextInt(5);
				
				if(dropoutlayer == true && a.equals(1)) {
					dropped+=1;
					if(dropped>1) {
						dropoutlayer = false;
					}
				} else {
					hidden[i][j].transform();
				}
				
			}
			for(int j=0;j<layersize;j+=1) {
				double a = (layersize)/(layersize - dropped);
				for(Synapse p : hidden[i][j].forward) {
					p.after.input = p.after.input*a;
				}
			}
		}
		for(Neuron neu: output) {
			neu.transform();
		}
	}
	
	/**
	 * Coordinates backpropagation.
	 * @param learningrate learning rate
	 */
	public void backpropagate(double learningrate) {
		int check = 0;
		// Give error feedback to output neurons
		for(int i=0;i<labelexpect.length;i+=1) {
			output[i].error = (labelexpect[i] - output[i].output)*(1-output[i].output)*output[i].output;
			losscheck(output[i].output,labelexpect[i]);
			
		}
		for(Neuron neu : output) {
			neu.revise(learningrate);
			check+=1;
		}
		
		for(int i=layers-1; i>-1; i-=1 ) {
			for(int j=0;j<layersize;j+=1) {
				hidden[i][j].revise(learningrate);
				check+=1;
			}
			//System.out.println("Layer " + i + " backpropagation complete.");
		}
		
		//System.out.println("Backpropagation complete.");
		//System.out.println(check + " neurons revised.");
	}
	
	/**
	 * Top level method to train the model.
	 * @param steps number of training steps to take
	 * @param learningrate learning rate
	 * @param t data in the form of Data class
	 * @param dropout inclusion of dropout regularization
	 */
	public void train(int steps, double learningrate, Data t, boolean dropout) {
		System.out.println("Training beginning.");
		int counter = 0;
		while(counter<steps) {
			
			// Use of SGD.
			int a = random.nextInt(t.size);
			
			//Fill in test features.
			for(int i=0; i<t.featurecount-1;i+=1) {
				input[i].input = t.dataset[a][i];
			}
			
			//Fill in label.
			labelexpect[0] = t.dataset[a][t.featurecount-1];
			
			// Print to check.
			System.out.println("Expected:" + labelexpect[0]);
			
			//Cycle of fowardfeeding and then backpropagating for a single step.
			feedforward(dropout);

			System.out.println("Received:" + output[0].output);
			
			backpropagate(learningrate);

			counter += 1;
		}
	}
	
	private void reset() {
		//Resets all input to 0. This is for test function.
		for(int i=layers-1; i>-1; i-=1 ) {
			for(int j=0;j<layersize;j+=1) {
				hidden[i][j].input =0D;
				hidden[i][j].output =0D;
			}
		}
		for(Neuron neu : output) {
			neu.input=0D;
		}

		for(Neuron neu : input) {
			neu.input=0D;
		}
	}
	
	/**
	 * To test the accuracy of the model on test data
	 * @param t the test Data (Data class, choose testdata option)
	 * @return number of correct answers.
	 */
	public double test(Data t) {
		// Tests model against all entries in Data t.
		
		// acc is number of accurate results.
		int acc = 0;
		
		for(int j=0;j<t.size;j+=1) {
			// Feed in test features.
			for(int i=0; i<t.featurecount-1;i+=1) {
				input[i].input = t.dataset[j][i];
			}
			
			// Fill in the label
			labelexpect[0] = t.dataset[j][t.featurecount-1];
			System.out.println("Test: Expected - " + labelexpect[0]);
			System.out.println("Test: Received - " + output[0].output);
			feedforward(false);
			
			double b;
			// Right now classification threshold is 0.5 . Change as you wish.
			if(output[0].output<0.6) {
				b=0D;
			}
			else { b = 1D; }
			
			if(b==labelexpect[0]) {
				acc+=1;
				//System.out.println("Accurate.");
			}
			reset();
		}
		System.out.println("Accuracy is " + acc + " out of " + t.size);
		
		// Note: function returns raw score and not percentage. Add /t.size just before semicolon to obtain probability of correctness.
		return acc;
		
	}
	
	/**
	 * The loss is computed and printed in console. This method is used at the output neuron.
	 * @param predicted the result predicted by network
	 * @param actual the actual result
	 */
	public void losscheck(double predicted, double actual) {
		double loss = -(actual)*Math.log(predicted)-(1-actual)*Math.log(1-predicted);
		System.out.println("Cross-entropy loss is " + loss);
	}
	
	public static void main(String[] args) {
		// Read data from csv. Input your filename here:
		Data data = new Data("testtenyn.csv");
		// Partition data (if required).
		data.splitData(50, 50, 000);
		
		// 
		Network nn = new Network(8,3,3,1);
		nn.train(100000, 1, data, true);
		nn.test(data);
		nn.test(data.valdata);
		
		System.out.println("Run completed, program will terminate.");
		
		
	}
}
