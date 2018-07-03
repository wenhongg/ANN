/**
 *  Reinventing the wheel. 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
	Parses CSV files into 2D array. Assumes there is a header, which is removed. 
	If there is no header... the first data entry will be discarded. 


*/

public class Data {
	
	public double[][] dataset;
	public int size;
	public int featurecount;
	public Data valdata;
	public Data testdata;
	
	/**
	 * Constructor with  
	 * @param filename location of the CSV file
	 */
	public Data(String filename) {
		readData(filename);
	}
	
	
	private Data() {}
	
	/**
	 * Uses Scanner to read CSV file.  
	 * @param place location of the CSV file
	 */
	public void readData(String place) {
		List<String[]> data = new ArrayList<>();
		int counter=0;
		try {
			
		Scanner scanner = new Scanner(new File(place));
		scanner.useDelimiter("\\r?\\n");
		System.out.println(scanner.next());
		
		while(scanner.hasNext()) {
			counter+=1;
			String line = scanner.next();
			String[] values = line.split(",");
			data.add(values);
			}
		
		scanner.close();
		
		} 
		catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		size = counter;
		featurecount = data.get(0).length;
		System.out.println("Size: " + size);
		System.out.println("Features: " + featurecount);
		
		double[][] data1 = new double[size][featurecount];
		for(int i=0;i<size;i+=1) {
			
			String[] str = data.get(i);
			
			for(int j=0;j<featurecount;j+=1) {
				Double d = Double.parseDouble(str[j]);
				data1[i][j] = d;
			} 
			
		}
		dataset = data1;
		System.out.println("Data successfully parsed.");
		// Note that dataset is in the format [example-number][feature]
		
	}
	
	/**
	 * A method to obtain the average number of each feature.
	 * @return an array of doubles of the average of each column/feature
	 */
	public double[] avgs() {
		double[] avg = new double[featurecount];
		for(int i=0; i<featurecount; i+=1) {
			double sum = 0D;
			for(int j=0; j<size;j+=1) {
				sum += dataset[j][i];
			}
			avg[i] = (sum/size);
		}
		return avg;
	}
	
	/**
	 * Partitions data into training data, validation data and test data. The numbers are required to add up to the total number of data entries.
	 * @param train number of data entries in training set
	 * @param valid number of data entries in validation set
	 * @param test number of data entries in test set
	 */
	public void splitData(int train, int valid, int test) {
		valdata = new Data();
		testdata = new Data();
		
		if(train+valid+test!=size) {
			throw new IllegalArgumentException("Partitions don't add up.");
		}
		
		double[][] temp = new double[train][featurecount];
		for(int i=0; i<train;i+=1) {
			for(int j=0; j<featurecount;j+=1) {
				temp[i][j] = dataset[i][j];
			}
		}
		
		valdata.dataset = new double[valid][featurecount];
		for(int i=train; i<train+valid;i+=1) {
			for(int j=0; j<featurecount;j+=1) {
				valdata.dataset[i-train][j] = dataset[i][j];
			}
		}
		valdata.featurecount = featurecount;
		valdata.size = valid;
		

		testdata.dataset = new double[test][featurecount];
		for(int i=train+valid; i<size;i+=1) {
			for(int j=0; j<featurecount;j+=1) {
				testdata.dataset[i-train-valid][j] = dataset[i][j];
			}
		}
		testdata.featurecount = featurecount;
		testdata.size = test;
		
		
		dataset = temp;
		size = train;
	}
	
	/**
	 * Still a work in progress.
	 */
	public void normalize() {
		// Need to tweak normalize function.
		// Future version will automate this process.
		for(int i=0; i<featurecount-1; i+=1) {
			for(int j=0; j<size; j+=1) {
				dataset[j][i]= dataset[j][i]/100;
			}
		}
	}
	
	
		
	
	
	
}
