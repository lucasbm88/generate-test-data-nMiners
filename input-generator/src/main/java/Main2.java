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

public class Main2 {
	
	static List<String> products;
	static Map<String, List<String>> associations = Collections.synchronizedMap(new HashMap<String, List<String>>());
	
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
	
	public static void mainzz(String[] args) throws IOException {
		products = Files.readAllLines(Paths.get("/home/lucas/mestrado/CODE/generate-test-data/generated-input.csv"));
		Map<String, Integer> productsToId = new HashMap<String, Integer>();
		int i=0;
		for (String produto : products) {
			productsToId.put(produto, i);
			i++;
		}
		System.out.println("CRIOU MAP"+products.size());
		Map<Integer, String> inverted = new HashMap<Integer, String>();
		
		for(String id: productsToId.keySet()){
			inverted.put(productsToId.get(id), id);
		}
		System.out.println("INVERTEU");
		for(int j=products.size(); j>0; j--){
			inverted.get(j);
		}
		System.out.println("FIM!");
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		products = Files.readAllLines(Paths.get(args[0]+"/products.list"));
		List<Thread> ts = new ArrayList<Thread>();
		for(int i=0; i<1500000; i++){
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
		
		for(int i=0; i<1500000; i++){
			System.out.println("U"+i);
			for(String productLine : associations.get("U"+i)){
				linesToWrite.add("U"+i+","+productLine);
			}
		}
		
		Files.write(Paths.get(args[0]+"/generated-input.csv"), linesToWrite, StandardOpenOption.CREATE);
		
	}
	
	public static boolean createAssociation(int i){
		Random rand = new Random(System.currentTimeMillis()+i);
		String product = products.get(rand.nextInt(products.size())); //get the product
		if(null == associations.get("U"+i)){
			List<String> usrPrd = new ArrayList<String>();
			usrPrd.add(product);
			associations.put("U"+i, usrPrd);
		} else {
			//check if the product is already in the user list
			if(associations.get("U"+i).contains(product)){
				//it already exists
				return false;
			} else {
				associations.get("U"+i).add(product);
			}
		}
		return true;
	}

}
