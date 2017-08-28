import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Thread;
//????import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentSearcherList<T> {

	/*
	 * Three kinds of threads share access to a singly-linked list:
	 * searchers, inserters and deleters. Searchers merely examine the list;
	 * hence they can execute concurrently with each other. Inserters add
	 * new items to the front of the list; insertions must be mutually exclusive
	 * to preclude two inserters from inserting new items at about
	 * the same time. However, one insert can proceed in parallel with
	 * any number of searches. Finally, deleters remove items from anywhere
	 * in the list. At most one deleter process can access the list at
	 * a time, and deletion must also be mutually exclusive with searches
	 * and insertions.
	 * 
	 * Make sure that there are no data races between concurrent inserters and searchers!
	 */

	private static class Node<T>{
		final T item;
		Node<T> next;
		
		Node(T item, Node<T> next){
			this.item = item;
			this.next = next;
		}
	}
	

	volatile private Node<T> first;  // safely published
	
	private int num_insert = 0; 
	private int num_search = 0;  
	private int num_remove = 0; 
	private int num_waiting_remove = 0;
	//invariant:
	// num_search >=0 && 
	// ((num_remove ==0 && num_insert == 0) || 
	// (num_insert == 1 && num_remove == 0) || 
	// (num_remove == 1 && num_insert == 0 && num_search == 0))
	
	private final ReentrantLock lock;
	private final Condition searchCond;
	private final Condition insertCond; // num_insert == num_remove== 0 
	private final Condition removeCond; // num_insert == num_search == num_remove == 0
	

	
	public ConcurrentSearcherList() {
		first = null;	
		lock = new ReentrantLock();
		searchCond = lock.newCondition();
		insertCond = lock.newCondition();
		removeCond = lock.newCondition();
	}
	
	/**
	 * Inserts the given item into the list.  
	 * 
	 * Precondition:  item != null
	 * 
	 * @param item
	 * @throws InterruptedException
	 */
	public void insert(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to insert null";
		start_insert();
		try{
			first = new Node<T>(item, first);
		}
		finally{
			end_insert();
		}
	}
	
	/**
	 * Determines whether or not the given item is in the list
	 * 
	 * Precondition:  item != null
	 * 
	 * @param item
	 * @return  true if item is in the list, false otherwise.
	 * @throws InterruptedException
	 */
	public boolean search(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to search for null";
		start_search();
		try{
			for(Node<T> curr = first;  curr != null ; curr = curr.next){
				if (item.equals(curr.item)) return true;
			}
			return false;
		}
		finally{
			end_search();
		}
	}
	
	/**
	 * Removes the given item from the list if it exists.  Otherwise the list is not modified.
	 * The return value indicates whether or not the item was removed.
	 * 
	 * Precondition:  item != null.
	 * 
	 * @param item
	 * @return  whether or not item was removed from the list.
	 * @throws InterruptedException
	 */
	// give priority to remove
	public boolean remove(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to remove null";
		start_remove();
		try{
			if(first == null) return false;
			if (item.equals(first.item)){first = first.next; return true;}
			for(Node<T> curr = first;  curr.next != null ; curr = curr.next){
				if (item.equals(curr.next.item)) {
					curr.next = curr.next.next;
					return true;
				}
			}
			return false;			
		}
		finally{
			end_remove();
		}
	}
	
	private void start_insert() throws InterruptedException{
		lock.lock();
		try{
			while(num_insert != 0 || num_remove != 0)
			{
				insertCond.await();
			}
			num_insert++;
			System.out.printf("Number of Search: %d; Number of insert: %d; Number of remove: %d.\n", num_search, num_insert, num_remove);
			long temp = Thread.currentThread().getId();
			System.out.printf("Thread %d starts inserting.\n", temp);
		}
		finally{lock.unlock();}
	}

	private void end_insert(){
		lock.lock();
		try{
			num_insert--;
			System.out.printf("Number of Search: %d; Number of insert: %d; Number of remove: %d.\n", num_search, num_insert, num_remove);
			insertCond.signalAll();
			long temp = Thread.currentThread().getId();
			System.out.printf("Thread %d ends inserting.\n", temp);
		}finally{lock.unlock();}
	}
	
	private void start_search() throws InterruptedException{
		lock.lock();
		try{
			
			while(num_waiting_remove != 0){
				searchCond.await();
			}
			num_search++;
			System.out.printf("Number of Search: %d; Number of insert: %d; Number of remove: %d.\n", num_search, num_insert, num_remove);
			long temp = Thread.currentThread().getId();
			System.out.printf("Thread %d starts searching.\n", temp);
		}finally{lock.unlock();}
	}
	
	private void end_search(){
		lock.lock();
		try{
			num_search--;
			
			System.out.printf("Number of Search: %d; Number of insert: %d; Number of remove: %d.\n", num_search, num_insert, num_remove);
			if(num_search == 0 && num_insert == 0 && num_remove == 0){
				removeCond.signalAll();
			}
			long temp = Thread.currentThread().getId();
			System.out.printf("Thread %d ends searching.\n", temp);
		}finally{lock.unlock();}

	}
	
	private void start_remove() throws InterruptedException{
		lock.lock();
		num_waiting_remove++;
		try{
			while(num_search != 0 || num_insert != 0 || num_remove != 0){
				removeCond.await();
			}
			num_remove++;
			num_waiting_remove--;
			System.out.printf("Number of Search: %d; Number of insert: %d; Number of remove: %d.\n", num_search, num_insert, num_remove);
			long temp = Thread.currentThread().getId();
			System.out.printf("Thread %d starts removing.\n", temp);
			if(num_waiting_remove == 0){
				searchCond.signalAll();
			}
		}finally{lock.unlock();}
	}
	
	private void end_remove() {
		lock.lock();
		try{
			num_remove--;
			System.out.printf("Number of Search: %d; Number of insert: %d; Number of remove: %d.\n", num_search, num_insert, num_remove);
			if(num_insert == 0){
				insertCond.signalAll();
				if(num_search == 0){
					removeCond.signalAll();
				}
			}
			long temp = Thread.currentThread().getId();
			System.out.printf("Thread %d ends removing.\n", temp);
		}finally{lock.unlock();}
		
	}
}
