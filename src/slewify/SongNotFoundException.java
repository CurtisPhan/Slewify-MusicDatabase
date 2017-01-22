package slewify;
/**	The SongNotFoundException Class
 * 
 * 	Creates an exception to be thrown when a search for a specific song turns up no results.
 * 	Main purpose is to keep the remove methods in Playlist class consistent with the remove by index method
 * 	in throwing exceptions when removal is unsuccessful.
 * 
 */
public class SongNotFoundException extends RuntimeException {
	/**The constructor*/
	public SongNotFoundException ()
	{
	}
}
