package slewify;


/**	The Node Class
 * 
 *		Creates a Node object, a single element in a Playlist object
 *		Stores a Song object and a reference to the preceding and following nodes in the list
 *
 */
public class Node {
	private Song storedSong;		//Stores the reference to this nodes song
	private Node previous;			//Stores the reference to the node that precedes this one in the list
	private Node next;					//Stores the reference to the node that follows this one in the list


	/**The constructor*/
	public Node (Song s, Node prv, Node nxt)
	{
		storedSong = s;
		previous = prv;
		next = nxt;
	}

	/**Returns the reference to this nodes song*/
	public Song getSong ()
	{
		return storedSong;
	}


	/**Returns the reference to the previous node in the list*/
	public Node getPrevious ()
	{
		return previous;
	}


	/**Set this node's previous node in the list to the specfied node*/
	public void setPrevious (Node n)
	{
		previous = n;
	}

	/**Returns the reference to the next node in the list*/
	public Node getNext ()
	{
		return next;
	}


	/**Set this node's next node in the list to the specfied node*/
	public void setNext (Node n)
	{
		next = n;
	}
}
