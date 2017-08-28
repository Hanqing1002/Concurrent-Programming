
public class Searcher implements Runnable{
	
	private ConcurrentSearcherList<Integer> l = new ConcurrentSearcherList<Integer>();
	private int t;
	
	public Searcher(ConcurrentSearcherList<Integer> l, int t){
		this.l = l;
		this.t = t;
	}
	
	@Override
	public void run(){
		try{
			boolean b = l.search(t);
			System.out.println(b);
		}catch(InterruptedException ingore){};
	}

}
