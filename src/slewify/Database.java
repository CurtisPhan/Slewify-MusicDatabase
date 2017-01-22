package slewify;

import java.util.ArrayList;


/**	The Database Class
 * 
 * 	Stores the lists of all the playlists and songs registered in the  system and execute most of the functions required 
 * 	to do with changing the various playlists/songs (eg creating/deleting, modifying their information, adding/removing songs from playlists
 * 
 */
public abstract class Database {
	private static ArrayList <Song> songList= new ArrayList <Song> ();
	private static ArrayList <Playlist> playlistList = new ArrayList <Playlist> (); 

	/**
	 * @return the songList
	 */
	public static ArrayList<Song> getSongList() {
		return songList;
	}

	/**
	 * @return the playlistList
	 */
	public static ArrayList<Playlist> getPlaylistList() {
		return playlistList;
	}

	/**Determine if song to be created is already in the system.
	 * Creates the song if it is not in the system.
	 */
	public static boolean isDuplicateSong(String name, String artist,  String albumName, int year,String genre){
		Song s = new Song(name, artist, albumName, year, genre);
		for (int x=0; x<songList.size(); x++)
		{
			if (songList.get(x).equals(s))
			{
				return true;
			}
		}
		return false;
	}

	/**Determine if song to be added to the playlist is already in the system.
	 * Creates the song if it is not in the system, add the original song to the playlist if it is.
	 * Keeps star status of original song when adding original song to a playlist without creating
	 * 2 separate objects with the same parameters.
	 */
	private static Song checkDuplicateSong(Song s){
		for (int x=0; x<songList.size(); x++)
		{
			if (songList.get(x).equals(s))
			{
				return songList.get(x);
			}
		}
		createSong(s);
		return s;
	}

	/**Determines if the specified song can be deleted from the system.
	 * Cannot delete song if the song is a part of any playlist.
	 */
	public static boolean canDeleteSong (Song s)
	{
		//return false if song is in any playlist (does not count the Starred Playlist if song is in it)
		if (s.getNumOfTimesInPLs() > 1 && s.isStarred() == true || s.getNumOfTimesInPLs() > 0 && s.isStarred() == false)
				return false;
		return true;
	}
	
	/** Creates a playlist with the specified name and adds it to the system*/
	public static void createPlaylist(String name){
		playlistList.add(new Playlist(name));
	}

	/** Creates a song with the specified parameters and adds it to the system*/
	public static void createSong(String name, String artist,  String albumName, int year,String genre){
		songList.add(new Song(name, artist, albumName, year, genre));
	}

	/** Adds a song (created as a temp song in one of the addSong methods) to the system*/
	public static void createSong(Song s){
		songList.add(s);
	}

	/** Deletes the specified playlist from the system*/
	public static void deletePlaylist (Playlist p)
	{
		p.clearList();
		playlistList.remove(p);
	}

	/** Deletes the specified song from the system.*/
	public static boolean deleteSong (Song s)
	{
		//remove song from Starred Playlist if it is in it
		if (s.isStarred() == true)	
			changeStarredList(s);
		songList.remove(s);
		return true;
	}

	/** Add a song with the specified parameters to the end of the specified playlist.
	 * Creates and adds the song to the system if it is not already present.
	 */
	public static void addSong (Playlist p, String name, String artist,  String albumName, int year,String genre){
		Song s = new Song(name, artist,  albumName, year, genre);
		s = checkDuplicateSong(s);
		p.addSong(s);
	}

	/** Add a song with the specified parameters to the specified playlist at the specified index.
	 * Creates and adds the song to the system if it is not already present.
	 */
	public static void addSong(Playlist p, int index, String name, String artist, String albumName, int year,String genre){
		Song s = new Song(name, artist,  albumName, year, genre);
		s = checkDuplicateSong(s);
		p.addSong(index, s);
	}

	/** Removes the song at the specified index from the specified playlist.*/
	public static void removeSong (Playlist p, int index){
		p.removeSong(index);
	}

	/** Removes the first song with the specified name/title from the specified playlist.*/
	public static void removeSong(Playlist p, String name){
		p.removeSong(name);
	}

	/**Change the star status of the given song and add or remove it from the Starred Playlist.*/
	public static void changeStarredList(Song s){
		if (s.isStarred() == true)				//remove from Starred Playlist
		{
			s.setStarred(false);
			playlistList.get(0).removeSong(s);
		}
		else													//add to Starred Playlist
		{
			s.setStarred(true);
			playlistList.get(0).addSong(s);
		}
	}

	/**Rename the specified playlist to the inputted name.*/
	public static void renamePlaylist (Playlist p, String name){
		p.setName(name);
	}

	/**Change the information of the specified song to the inputted parameters.*/
	public static void modifySong (Song s, String name, String artist, String albumName, int year, String genre){
		s.setName(name);
		s.setArtist(artist);
		s.setAlbumName(albumName);
		s.setYear(year);
		s.setGenre(genre);
	}
}
