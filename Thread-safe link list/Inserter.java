public class Inserter implements Runnable{
	
	private ConcurrentSearcherList<Integer> l = new ConcurrentSearcherList<Integer>();
	private int t;
	
	public Inserter(ConcurrentSearcherList<Integer> l, int t){
		this.l = l;
		this.t = t;
	}
	
	@Override
	public void run(){
		try{
			l.insert(t);
		}catch(InterruptedException ingore){};
	}

}