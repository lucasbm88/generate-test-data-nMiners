import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
	
	static List<String> products;
	static Map<Integer, List<String>> associations = Collections.synchronizedMap(new HashMap<Integer, List<String>>());
	
	static class MyEx implements Runnable{
		int i;
		int numeroTheProdutos;
		
		public MyEx(int i, int p) {
			this.i = i;
			this.numeroTheProdutos = p;
		}
		public void run() {
			System.out.println(this.i);
			while(this.numeroTheProdutos > 0){
				if(createAssociation(this.i)){
					this.numeroTheProdutos--;
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		products = Files.readAllLines(Paths.get("/home/lucas/mestrado/CODE/generate-test-data/products_with_id.csv"));
		List<Thread> ts = new ArrayList<Thread>();
		for(int i=0; i<1000000; i++){
			Random rand = new Random(System.currentTimeMillis()+i);
			int numeroDeProdutos = rand.nextInt(5)+1;
			MyEx m = new MyEx(i, numeroDeProdutos);
			Thread t = new Thread(m);
			t.start();
			ts.add(t);
		}
		
		while(ts.size() > 0){
			if(ts.get(ts.size()-1).getState().equals(Thread.State.TERMINATED)){
				ts.remove(ts.size()-1);
			}
		}
		System.out.println("==========++> FINISHED ALL THREADS");
		
		List<String> linesToWrite = new ArrayList<String>();
		
		for(int i=0; i<1000000; i++){
			System.out.println(i);
			for(String productLine : associations.get(i)){
				linesToWrite.add(i+","+productLine);
			}
		}
		
		Files.write(Paths.get("/home/lucas/mestrado/CODE/generate-test-data/generated-input.csv"), linesToWrite, StandardOpenOption.CREATE);
		
	}
	
	public static boolean createAssociation(int i){
		Random rand = new Random(System.currentTimeMillis()+i);
		String productLine = products.get(rand.nextInt(products.size())); //get the product
		String productId = productLine.split(",")[0];
		if(null == associations.get(i)){
			List<String> usrPrd = new ArrayList<String>();
			usrPrd.add(productId);
			associations.put(i, usrPrd);
		} else {
			//check if the product is already in the user list
			if(associations.get(i).contains(productId)){
				//it already exists
				return false;
			} else {
				associations.get(i).add(productId);
			}
		}
		return true;
	}

}
