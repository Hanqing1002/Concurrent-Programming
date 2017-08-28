
import java.lang.Thread;

public class test_List{

	public static void main(String[] args) {
		
		
		ConcurrentSearcherList<Integer> l = new ConcurrentSearcherList<Integer>(); 
		
		int i = 0;
		int num_insertThreads = 20;
		int num_searchThreads = 40;
		int num_removeThreads = 10;
		
		for(i=0;i<25;i++){
			try{
				l.insert(i*2+5);
			}catch(InterruptedException ingore){};
		}
		
		

		for (i=0;i<num_insertThreads || i<num_searchThreads || i<num_searchThreads;i++){
			final int t = i;
			
			if(i<num_insertThreads){
				Inserter in = new Inserter(l,t);
				Thread temp = new Thread(in);
				temp.start();
			}
			//System.out.println(threads[index]);
			//threads[index].start();
			//index++;
			
			if(i<num_searchThreads){
				Searcher s = new Searcher(l,t);
				Thread temp= new Thread(s);
				temp.start();
			}
			
			
			if(i<num_removeThreads){
				Remover r = new Remover(l,t);
				Thread temp = new Thread(r);
				temp.start();
			}	
			
		}
		/*
		for(i=0;i<70;i++){
			try{
				threads[i].join();
			}catch(InterruptedException ingore){};
			
		}
		*/
		
	}
	
}
