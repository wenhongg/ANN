import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * This class repeatedly creates new networks, saves the best implementation of a network and further tests it.
 * Call Stater class to test a fixed number of networks. The best network is saved as modelnet.
 * Call save method to save the model.  
 * @author WEN HONG
 */

public class Stater {
	/**
	 * The network with best results
	 */
	Network modelnet;
	Data data;
	List<Double> results;
	HashMap<Double,Integer> map;
	
	/**
	 * Class constructor
	 * @param a number of input features (input neurons)
	 * @param b number of hidden layers of neurons
	 * @param c number of neurons in each hidden layer
	 * @param d number of output features (output neurons)
	 * @param e number of networks to create, train and test
	 * @param filename location of the file
	 * @param dropout dropout regularization
	 */
	public Stater(int a, int b, int c, int d, int e, String filename, boolean dropout) {
		results = new ArrayList<Double>();
		HashMap<Double,Integer> map = new HashMap<Double,Integer>();
		data = new Data(filename);
		data.splitData(50, 50, 000);
		double highacc=0;
	
		for(int i=0;i<e;i+=1) {
			Network nn = new Network(a,b,c,d);
			nn.train(100000, 1, data, dropout);
			double result = nn.test(data.valdata);
			results.add(result);
			if(result>highacc) {
				modelnet=nn;
				highacc = result;
				
			}
			if(map.containsKey(result)) {
				map.put(result, map.get(result) + 1);
			} else {
				map.put(result, 1);
			}
		}
		for (Map.Entry<Double, Integer> entry : map.entrySet()) {
		    System.out.println(entry.getKey()+ " : " +entry.getValue());
		}
	}
	
	/**
	 * Saves the model network. Work in progress. 
	 */
	public void save() {
		
	}
	
	public static void main(String[] args) {
		Stater op = new Stater(8,3,3,1,10,"testtenyn.csv",true);
		op.modelnet.test(op.data.valdata);
		
		
		
		System.out.println("Network closing");
	}
}
