package theDeque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Deque {

	private int maxSize;
	   private long[] dequeArray;
	   private int front;
	   private int rear;
	   private int nItems;
	public Deque(int i) {
		// TODO Auto-generated constructor stub
		maxSize = i;
	      dequeArray = new long[maxSize];
	      front = 0;
	      rear = -1;
	      nItems = 0;
	}

	public long removeRight() {
		// TODO Auto-generated method stub
		if(isEmpty())
		{
			System.out.println("dequeue empty. no item removed, will return 0");
			return 0;
		}
		long temp = dequeArray[rear--];
		if(rear==-1)
		{
			rear=maxSize-1;
		}
		nItems--; 
		
	    return temp;
	}

	public long removeLeft() {
		// TODO Auto-generated method stub
		if(isEmpty())
		{
			System.out.println("dequeue empty. no item removed, will return 0");
			return 0;
		}
		long temp = dequeArray[front++]; // get value and incr front
	      if(front == maxSize)           // deal with wraparound
	         front = 0;
	      nItems--;                      // one less item
	      return temp;
	}

	public void insertRight(long value) {
		// TODO Auto-generated method stub
		if(isFull())
		{
			System.out.println("dequeue full");
			return;
		}
		
		if(rear == maxSize-1)         // deal with wraparound
	         rear = -1;
	      dequeArray[++rear] = value;         // increment rear and insert
	      nItems++;                     // one more item
	}

	public void insertLeft(long value) {
		// TODO Auto-generated method stub
		if(isFull())
		{
			System.out.println("dequeue full");
			return;
		}
		if(front==0)
			front=maxSize;
		dequeArray[--front] = value;
		nItems++;
		
	}

	public void display() {
		// TODO Auto-generated method stub
		if (isEmpty()) 
	        System.out.println("Empty dequeue");
	    else if (rear < front) 
	    {
	        for (int i = front; i < maxSize; i++) 
	            System.out.print(" "+dequeArray[i]);
	        for (int i = 0; i <= rear; i++) 
	            System.out.print(" "+dequeArray[i]);
	    } 
	    else if (front <= rear) 
	        for (int i = front; i <= rear; i++)
	            System.out.print(" "+dequeArray[i]);
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return (nItems==0);
	}

	public boolean isFull() {
		// TODO Auto-generated method stub
		return (nItems==maxSize);
	}

}
///////////// END deque CLASS//////////////////////////////////////////////////////
//driver
class DequeApp
{
public static void main(String[] args) throws IOException
{
Deque theDeque = new Deque(10);
while(true)
{
long value;
System.out.println("");
if( theDeque.isFull() )
System.out.println(
"*** Deque is full. No insertions. ***");
if( theDeque.isEmpty() )
System.out.println(
"*** Deque is empty. No deletions. ***");
System.out.print("Enter first letter of ");
System.out.println("insertLeft, InsertRight, ");
System.out.print("removeLeft, RemoveRight, or display: ");
int choice = getChar();
switch(choice)
{
case 'd':
theDeque.display();
break;
case 'i':
System.out.print("Enter value to insert left: ");
value = getLong();
theDeque.insertLeft(value);
break;
case 'I':
System.out.print("Enter value to insert right: ");
value = getLong();
theDeque.insertRight(value);
break;
case 'r':
value = theDeque.removeLeft();
System.out.println("Removed left: " + value);
break;
case 'R':
value = theDeque.removeRight();
System.out.println("Removed right: " + value);
break;
default:
System.out.print("Invalid entry\n");
} // end switch
} // end while
} // end main()
//-------------------------------------------------------------
public static String getString() throws IOException
{
InputStreamReader isr = new InputStreamReader(System.in);
BufferedReader br = new BufferedReader(isr);
String s = br.readLine();
return s;
}
//-------------------------------------------------------------
public static char getChar() throws IOException
{
String s = getString();
return s.charAt(0);
}
//-------------------------------------------------------------
public static long getLong() throws IOException
{
String s = getString();
return (long)Integer.parseInt(s);
}
////////////////////////////////////////////////////////////////
} // end class DequeApp