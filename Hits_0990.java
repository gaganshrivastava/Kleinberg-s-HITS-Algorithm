

import static java.lang.Math.abs;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Hits_0990 {

	static double errorrate = 0.00001; // default error rate as per Prp
	static int numOfVertices;
	static int numOfEdges;
	static int[][] adjcancyMatrix;
	static int iterations;
	static int initialvalue;
//	static String filename;
	static double[] hub0;
	static double[] auth0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			 if (args.length != 3) {
			 System.out.println("Please give the proper running cmd");
			 return;
			 }
			// Parsing the command-line arguments
			  iterations = Integer.parseInt(args[0]);
			  initialvalue = Integer.parseInt(args[1]);
			 String filename = args[2];
		/*	 iterations = 15;
			 initialvalue = -1;
			String filename = "samplegraph2.txt";*/

			if (!(initialvalue >= -2 && initialvalue <= 1)) {
				System.out.println("Initialization value must be between -2 and 1");
				return;
			}

			File file = new File(filename);
			Scanner fileData = new Scanner(file);

			// ---------------------------------------
			// Getting the number of vertices and edges in the graph
			try {
				if (fileData.hasNextLine()) {
					String[] parts = fileData.nextLine().split(" "); // taking
																		// the
																		// first
																		// row
					numOfVertices = Integer.parseInt(parts[0]);
					numOfEdges = Integer.parseInt(parts[1]);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Invalid number of vertices and edges");
			}
		//	System.out.println("number of vertices = " + numOfVertices);
		//	System.out.println("number of edges = " + numOfEdges);

			// ---------------------------------------
			// Making the adjacency Matrix
			adjcancyMatrix = new int[numOfVertices][numOfVertices];

			try {
				String[] parts = new String[2]; // to store the values of file
				while (fileData.hasNext()) {
					String data = fileData.nextLine();
					parts = data.split(" ", 2);

					int n = Integer.parseInt(parts[0]);
					int m = Integer.parseInt(parts[1]);
					adjcancyMatrix[n][m] = 1;

				}
			} catch (Exception e) {
				System.out.println("Invalid data");
			}

			// printing adjaceancy matrix
	/*		for (int a[] : adjcancyMatrix) {
				for (int x : a) {
					System.out.print(x + " ");
				}
				System.out.println();
			}*/

			// ---------------------------------------
			// Making Hub and Authority
			hub0 = new double[numOfVertices];
			auth0 = new double[numOfVertices];

			switch (initialvalue) { // init the hub and auth values for 0th
									// iteration

			case -1:
				for (int i = 0; i < numOfVertices; i++) {
					hub0[i] = 1.0 / numOfVertices;
					auth0[i] = 1.0 / numOfVertices; // 1/n
				}
				break;

			case -2:
				for (int i = 0; i < numOfVertices; i++) {
					hub0[i] = 1.0 / Math.sqrt(numOfVertices); // 1/root(n)
					auth0[i] = 1.0 / Math.sqrt(numOfVertices);
				}
				break;

			case 1:
				for (int i = 0; i < numOfVertices; i++) {
					hub0[i] = 1;
					auth0[i] = 1;
				}
				break;

			case 0:
				for (int i = 0; i < numOfVertices; i++) {
					hub0[i] = 0;
					auth0[i] = 0;
				}
				break;
			}

			Kleinberg_HITS();
			
			
			
		} catch (Exception e) {
			System.out.println("File Not found");
		}

	}
	
	
	
	public static void Kleinberg_HITS(){
		
		double[] auth = new double[numOfVertices];
		double[] hub = new double[numOfVertices];
		
		//priivuos values to used to calculate the next value
		double[] authPre = new double[numOfVertices];  
		double[] hubPre = new double[numOfVertices]; 
											
		//If the graph has N GREATER than
		//10, then the values for iterations, initialvalue are automatically set to 0 and -1 respectively
		if (numOfVertices > 10) {
			iterations = 0;
			for (int i = 0; i < numOfVertices; i++) {
				hub[i] = 1.0 / numOfVertices;
				auth[i] = 1.0 / numOfVertices;
				authPre[i] = auth[i];
				hubPre[i] = hub[i];
				
			}
			// declaring and initializing a counter i
			int x = 0;
			do {
				for (int i = 0; i < numOfVertices; i++) {
					authPre[i] = auth[i];
					hubPre[i] = hub[i];
				}
				// calculate hub, scaling,authoritative
				auth_hubFind(auth, hub);
				// incrementing counter
				x++;
			} while ( false == cmpErrorRate(hub, hubPre) || false == cmpErrorRate(auth, authPre));
			System.out.println("Iter:    " + x);
			for (int l = 0; l < numOfVertices; l++) {
				System.out.printf(" A/H[%d]=%.7f/%.7f\n",l,Math.round(auth[l]*10000000.0)/10000000.0,Math.round(hub[l]*10000000.0)/10000000.0);
			}
			return;
		}
		
		
		// if num of vertices are less than and equal to  10
		
		// init new hub and auth value form previous iteration
		for (int i = 0; i < numOfVertices; i++) {
			auth[i] = auth0[i];
			hub[i] = hub0[i];
			authPre[i] = auth[i];
			hubPre[i] = hub[i];
		}
		
		// Base iteration
		System.out.print("Base:    0 :");
		for (int i = 0; i < numOfVertices; i++) {
			System.out.printf(" A/H[%d]=%.7f/%.7f", i,auth0[i],
					hub0[i]);
		}
		
		if (iterations > 0) { 
			for (int i = 0; i < iterations; i++) { 
				// Computation of authoritative, hub and scaling
				auth_hubFind(auth, hub); 

				System.out.println();
				System.out.print("Iter:    " + (i + 1) + " :");
				for (int l = 0; l < numOfVertices; l++) {
					System.out.printf(" A/H[%d]=%.7f/%.7f",l,Math.round(auth[l]*10000000.0)/10000000.0,Math.round(hub[l]*10000000.0)/10000000.0);
				}

			} 
		}else{ //  for the error check, stopping condition

			//iterations equal to 0 corresponds to a default errorrate of 10^5. A -1, -2, etc , -6 for iterations
			//becomes an errorrate of 10^1;10^2; : : : ;10^6 respectively.
			
			
			if (iterations == 0) {
				errorrate = Math.pow(10, -5);//   default errorrate of 10^-5
			}else{
				errorrate = Math.pow(10, iterations);
			}
			
			int x = 0;
			do { // seting the auth and hub from preious iteration
				for (int i = 0; i < numOfVertices; i++) {
					authPre[i] = auth[i];
					hubPre[i] = hub[i];
				}
				
				// Computation of authoritative, hub and scaling
				auth_hubFind(auth, hub);
				x++;
				System.out.println();
				System.out.print("Iter:    " + x + " :");
				for (int l = 0; l < numOfVertices; l++) {
					System.out.printf(" A/H[%d]=%.7f/%.7f",l,Math.round(auth[l]*10000000.0)/10000000.0,Math.round(hub[l]*10000000.0)/10000000.0);
				}
			} while (false == cmpErrorRate(hub, hubPre) || false == cmpErrorRate(auth, authPre) );
		
			
		}
	
	}
	
	
	public static boolean cmpErrorRate(double []curr, double []pre) {
		
		// comparing the differecne between the prev and curr for each edges
		for (int i = 0; i < numOfVertices; i++) {
			if (abs(curr[i] - pre[i]) > errorrate)
				return false;
		}
		return true;
	}
	
	
	
	public static void auth_hubFind(double[] auth, double[] hub) {
			
		//setting auth arrray
		for (int i = 0; i < numOfVertices; i++) {
			auth[i] = 0.0;
		}
		// getting auth value from hub value
		for (int j = 0; j < numOfVertices; j++) {
			for (int i= 0; i < numOfVertices; i++) {
				if (adjcancyMatrix[i][j] == 1) {
					auth[j] += hub[i];
				}
			}
		}

		// setting  hub
		for (int p = 0; p < numOfVertices; p++) {
			hub[p] = 0.0;
		}
		//getting hub  value from auth value
		for (int j = 0; j < numOfVertices; j++) {
			for (int i = 0; i < numOfVertices; i++) {
				if (adjcancyMatrix[j][i] == 1) {
					hub[j] += auth[i];
				}
			}
		}
		
		double authTotal = 0.0 ;
		double authritySclFac = 0.0;
				
		// To scale auth values
		for (int t = 0; t < numOfVertices; t++) {
			authTotal += auth[t] * auth[t];
		}
		authritySclFac = Math.sqrt(authTotal);
		for (int t = 0; t < numOfVertices; t++) {
			auth[t] = auth[t] / authritySclFac;
		}

		// To scale hub values
		double hubScling = 0.0 ;
		double hubTotal = 0.0 ;
		for (int l = 0; l < numOfVertices; l++) {
			hubTotal += hub[l] * hub[l];
		}
		hubScling = Math.sqrt(hubTotal);
		for (int l = 0; l < numOfVertices; l++) {
			hub[l] = hub[l] / hubScling;
		}
	}
	
	
	

}
