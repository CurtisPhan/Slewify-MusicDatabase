package slewify;


/**	The Playlist Class
 * 
 *		Creates a Playlist object, a linked list that stores Songs in Node objects
 *		Playlist can add/remove songs from itself and keep track of which song in the playlist is currently being played
 */

public class Playlist {

	private Node head;										//Stores the reference to a playlists head
	private Node tail;											//Stores the reference to a playlists tail
	private String name;										//Stores the playlists name
	private int sizeOfList;									//Stores the number of songs a playlist has
	private Node currentSearchNode;				//Stores the reference to the current node while iterating through a list
	private Node currentPlayedNode;				//Stores the reference to the current node containing the song being played

	/**The constructor*/
	public Playlist (String listName)
	{
		name = listName;
	}


	/**Adds a new song to the end of the list.*/
	public boolean addSong (Song s)
	{
		Node newNode;
		//If there are no nodes yet, the node appended will be both the head and the tail
		//Else, update the old tail Node
		if(head == null) 
		{
			newNode = new Node (s, null, null);
			head = newNode;
		}
		else
		{
			newNode = new Node (s, tail, null);
			tail.setNext(newNode); 
		}
		tail = newNode; //specify a new tailNode
		sizeOfList++;
		s.setNumOfTimesInPLs(s.getNumOfTimesInPLs()+1);				//Increase the added song's number of times in playlist by 1 
		return true;
	}


	/**Adds a new song to the list at the specified index.
	 * Throws an exception if index is outside the scope of the playlist.
	 */
	public void addSong (int index, Song s)
	{
		if (index >= sizeOfList||index < 0)		//index out of bounds
		{
			IndexOutOfBoundsException e = new IndexOutOfBoundsException();
			throw e;
		}
		else
		{
			currentSearchNode=head;
			int i = 0;

			//Iterate through the playlist until it reaches the index of where the new node will be
			while(i<index)
			{
				currentSearchNode=currentSearchNode.getNext();
				i++;
			}
			Node newNode = new Node(s, currentSearchNode.getPrevious(), currentSearchNode);

			//Slot the new node into the playlist
			if (currentSearchNode.equals(head) == true)		//if index is 0, set the head to the new node
				head=newNode;
			else
				currentSearchNode.getPrevious().setNext(newNode);
			currentSearchNode.setPrevious(newNode);

			sizeOfList++;
			s.setNumOfTimesInPLs(s.getNumOfTimesInPLs()+1);			//Increase the added song's number of times in playlist by 1 
		}
	}


	/**Removes a song from this playlist at the specified index.
	 * Throws an exception if index is outside the scope of the playlist.
	 */
	public void removeSong (int index)
	{
		if (index >= sizeOfList||index < 0)		//index out of bounds
		{
			IndexOutOfBoundsException e = new IndexOutOfBoundsException();
			throw e;
		}
		else
		{
			currentSearchNode=head;
			int i = 0;

			//Iterate through the playlist until it reaches the index of node to be deleted/song to be removed
			while(i<index)
			{
				currentSearchNode=currentSearchNode.getNext();
				i++;
			}

			//adjust playlist by linking the nodes before and after the removed song
			if (currentSearchNode.equals(head) == true)	//redefine head if the old head was removed
				head = currentSearchNode.getNext();
			else
				currentSearchNode.getPrevious().setNext(currentSearchNode.getNext());

			if (currentSearchNode.equals(tail) == true)		//redefine tail if the old tail was removed
				tail = currentSearchNode.getPrevious();
			else
				currentSearchNode.getNext().setPrevious(currentSearchNode.getPrevious());

			//if current played song is being removed, switch current played song to next song
			if (currentSearchNode.equals(currentPlayedNode))
				setCurrentSong(getNextSong());

			sizeOfList--;
			//Decrease the removed song's number of times in playlist by 1 
			currentSearchNode.getSong().setNumOfTimesInPLs(currentSearchNode.getSong().getNumOfTimesInPLs()-1);
		}
	}


	/**Removes a song from this playlist with this song name. 
	 * Throws an exception if song was not found (to stay consistent with the index based removal method)
	 */
	public void removeSong (String name)
	{
		currentSearchNode=head;

		//Iterate through list looking for song with matching name.
		//If song is found, remove it from the list and return.
		while(currentSearchNode!=null)
		{
			if (currentSearchNode.getSong().getName().equalsIgnoreCase(name)){
				//adjust playlist by linking the nodes before and after the removed song
				if (currentSearchNode.equals(head) == true)	//redefine head if the old head was removed
					head = currentSearchNode.getNext();
				else
					currentSearchNode.getPrevious().setNext(currentSearchNode.getNext());

				if (currentSearchNode.equals(tail) == true)		//redefine tail if the old tail was removed
					tail = currentSearchNode.getPrevious();
				else
					currentSearchNode.getNext().setPrevious(currentSearchNode.getPrevious());

				//if current played song is being removed, switch current played song to next song
				if (currentSearchNode.equals(currentPlayedNode))
					setCurrentSong(getNextSong());

				sizeOfList--;
				//Decrease the removed song's number of times in playlist by 1 
				currentSearchNode.getSong().setNumOfTimesInPLs(currentSearchNode.getSong().getNumOfTimesInPLs()-1);
				return;
			}
			else{
				currentSearchNode=currentSearchNode.getNext();
			}
		}
		SongNotFoundException e = new SongNotFoundException();	//song not found
		throw e;
	}

	/**Remove the specified Song object from the playlist. 
	 * Throws an exception if song was not found (to stay consistent with the index based removal method)
	 */
	public void removeSong (Song s)
	{
		currentSearchNode=head;

		//Iterate through list looking for matching song
		//If song is found, remove it from the list and return
		while(currentSearchNode!=null)
		{
			if (currentSearchNode.getSong().equals(s)){
				//adjust playlist by linking the nodes before and after the removed song
				if (currentSearchNode.equals(head) == true)	//redefine head if the old head was removed
					head = currentSearchNode.getNext();
				else
					currentSearchNode.getPrevious().setNext(currentSearchNode.getNext());

				if (currentSearchNode.equals(tail) == true)		//redefine tail if the old tail was removed
					tail = currentSearchNode.getPrevious();
				else
					currentSearchNode.getNext().setPrevious(currentSearchNode.getPrevious());

				//if current played song is being removed, switch current played song to next song
				if (currentSearchNode.equals(currentPlayedNode))
					setCurrentSong(getNextSong());

				sizeOfList--;
				//Decrease the removed song's number of times in playlist by 1 
				currentSearchNode.getSong().setNumOfTimesInPLs(currentSearchNode.getSong().getNumOfTimesInPLs()-1);
				return;
			}
			else{
				currentSearchNode=currentSearchNode.getNext();
			}
		}
		SongNotFoundException e = new SongNotFoundException();	//song not found
		throw e;
	}

	/**Gets all of the songs in the playlist, adds them to an array and return the array*/
	public Song[] getListOfSongs ()
	{
		currentSearchNode = head;
		Song[] songList = new Song[sizeOfList];
		int i=0;

		//Iterate through playlist, adding the songs to the array
		while (currentSearchNode!=null)
		{
			songList[i]=currentSearchNode.getSong();
			currentSearchNode=currentSearchNode.getNext();
			i++;
		}
		return songList;
	}

	/** Clear all the songs from a playlist*/
	public void clearList ()
	{
		Song[] songList = getListOfSongs();

		//adjust the numOfTimesInPLs counter for each song in the playlist
		for (int x = 0; x < songList.length; x++)
			songList[x].setNumOfTimesInPLs(songList[x].getNumOfTimesInPLs() - 1);

		head = null;
		sizeOfList=0;
	}

	/**Return the name of this playlist*/
	public String getName ()
	{
		return name;
	}

	/**Return the name of this playlist*/
	public void setName (String name)
	{
		this.name=name;
	}

	/**Get the size of a given playlist*/
	public int getListSize ()
	{
		return sizeOfList;
	}

	/**Returns a reference to the head/first song of the playlist*/
	public Node getHead ()
	{
		return head;
	}

	/**Return the reference to the tail/last song in the list*/
	public Node getTail ()
	{
		return tail;
	}

	/**Return the previous song in the list. Return the last song in the list if current song being played is the first song in the list.*/
	public Node getPreviousSong ()
	{
		if (currentPlayedNode.equals(head) == true)
			return tail;
		return currentPlayedNode.getPrevious();
	}

	/**Return the next song in the list. Return the first song in the list if current song being played is the last song in the list.*/
	public Node getNextSong (){
		if (currentPlayedNode.equals(tail) == true)
			return head;
		return currentPlayedNode.getNext();
	}

	/**Return the current Node in the list here*/
	public Node getCurrentSong ()
	{
		return currentPlayedNode;
	}

	/**Set the current song being played in the playlist*/
	public void setCurrentSong (Node n)
	{
		currentPlayedNode=n;
	}

	/**Set the current song being played in the playlist to the song at the specified index in the playlist
	 * Throws an exception if index is outside the scope of the playlist.
	 */
	public void setCurrentSong (int index){
		if (index >= sizeOfList||index < 0)
		{
			IndexOutOfBoundsException e = new IndexOutOfBoundsException();
			throw e;
		}
		else
		{
			currentSearchNode=head;
			int i = 0;

			//Iterate through the playlist until it reaches specified index
			while(i<index)
			{
				currentSearchNode=currentSearchNode.getNext();
				i++;
			}
		}
		currentPlayedNode = currentSearchNode;
	}


	//Unused Methods
	/**
	//A method that finds the song required using the song's title. Returns the position number
	public int findPos (String name)
	{
		currentSearchNode=head;
		int i = 0;
		//Iterate through playlist looking for matching song
		//return the position of the song if found
		while(currentSearchNode!=null)
		{
			if (currentSearchNode.getSong().getName().equalsIgnoreCase(name))
				return i + 1;
			else{
				currentSearchNode=currentSearchNode.getNext();
				i++;
			}
		}
		return -1;			//return -1 if song not found
	}


	//Return the number of times a song occurs in a given list - lists can have multiples of the same song
	public int numTimes (String name)
	{
		currentSearchNode = head;
		int count = 0;
		//Iterate through playlist looking for matching song
		//increase counter whenever song is found
		while(currentSearchNode!=null)
		{
			if (currentSearchNode.getSong().getName().equalsIgnoreCase(name))
				count++;
			currentSearchNode=currentSearchNode.getNext();
		}
		return count;		//return counter
	}
	 */

}
