package slewify;
/**	The Song Class
 * 
 *		Creates a Song object that stores all of the information required for a song
 *
 */
public class Song {
	//Stores the value for the information on the song.
	private String name;						//the name/title of the song
	private String artist;						//the artist that made the song
	private String albumName;			//the name of the album the song is in
	private int year;							//the year the song was released
	private String genre;						//the genre the song falls under
	private boolean starred;				//whether the song is starred/favourited or not
	private int numOfTimesInPLs;		//the total number of times the song appears in every playlist


	/**The constructor*/
	public Song (String name, String artist,  String albumName, int year, String genre)
	{
		this.name = name;
		this.artist = artist;
		this.albumName = albumName;
		this.year = year;
		this.genre = genre;
	}

	/** Determines if this song equals another song obeject based on their parameters*/
	public boolean equals (Song s){
		if (name.equalsIgnoreCase(s.getName()) && artist.equalsIgnoreCase(s.getArtist()) && year == s.getYear() && 
				albumName.equalsIgnoreCase(s.getAlbumName()) && genre.equalsIgnoreCase(s.getGenre()))
			return true;
		return false;
	}

	/**Returns the song's title*/
	public String getName (){
		return name;
	}


	/**Returns the song's artist */
	public String getArtist (){
		return artist;
	}
	
	/**Returns the song's album name */
	public String getAlbumName() {
		return albumName;
	}

	/**Returns the song's year*/
	public int getYear (){
		return year;
	}

	/**Returns the song's genre*/
	public String getGenre (){
		return genre;
	}

	/**Returns how many times the song appears in every playlist */
	public int getNumOfTimesInPLs() {
		return numOfTimesInPLs;
	}

	/**Returns whether the song is starred or not*/
	public boolean isStarred() {
		return starred;
	}

	/** Set the song's name */
	public void setName(String name) {
		this.name = name;
	}

	/** Set the song's artist*/
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/** Set the song's album name*/
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	
	/** Set the song's year*/
	public void setYear(int year) {
		this.year = year;
	}

	/** Set the song's genre*/
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/** Set the song's number of appearances in every playlist*/
	public void setNumOfTimesInPLs(int numOfTimesInPLs) {
		this.numOfTimesInPLs = numOfTimesInPLs;
	}

	/** Set the song's starred status*/
	public void setStarred(boolean starred) {
		this.starred = starred;
	}
}
