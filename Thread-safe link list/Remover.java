public class Remover implements Runnable{
	
	private ConcurrentSearcherList<Integer> l = new ConcurrentSearcherList<Integer>();
	private int t;
	
	public Remover(ConcurrentSearcherList<Integer> l, int t){
		this.l = l;
		this.t = t;
	}
	
	@Override
	public void run(){
		try{
			l.remove(t);
		}catch(InterruptedException ingore){};
	}

}