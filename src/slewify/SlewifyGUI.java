package slewify;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *	@author 		Curtis Phan
 *	@course			ICS-4U0-A
 *	@instructor 	Mr. A. Sayed 
 *	@date 			December 8, 2015
 *
 *	Purpose: This program simulates a music streaming system similar to spotify.  It is able to create, delete, modify and store playlists
 *					and songs, and display them on screen. Songs can be added to playlists at a given position or at the end of the list, or 
 *					removed from playlists given the name of the song or the position of the song in the list. The program keeps track of 
 *					which song is being played and allows the user to pause/play it or switch to a different song. If the song being played is
 *					being played from a playlist, it also keeps track of which song is next in the playlist and allows the user to go forwards and
 *					backwards.
 *
 *	Known Bugs: - playlist/song fields that are String type (e.g. name, artist, genre) can break out of their position in the tables
 *							  displaying list of playlists/songs if they are too long (very unlikely to occur for playlists, somewhat likely for songs)
 *									- could be fixed by putting a character limit on these fields or programming in word wrapping
 *							- some flickering when switching between the  radio buttons determining whether song should be added to or 
 *							  removed from the playlist as components like other radio buttons are added/removed from the screen
 *							- clicking the play button on the played song but in a different playlist from when it was initially clicked does not 
 *							  switch from the first playlist over to the second playlist. Playlists only switch over when a different song is clicked.
 *							- comments are displayed differently depending on computer/settings so formatting will likely be off
 */

public class SlewifyGUI extends Applet implements ActionListener, ItemListener, MouseListener, MouseMotionListener{

	private final int MAX_OBJECTS_ONSCREEN=15;			//the number of playlists/songs able to be displayed on 1 page of a list

	int numOfPages;				//the number of pages the current list takes up
	int pageNum;						//the page number of the list the program is presently displaying

	Font normalFont;				//font for regular text
	Font numberFont;			//font for numbers
	Font bigFont;						//font for big text
	Font headerFont;				//font for header text
	Font subheaderFont;		//font for subheader text
	Font logoFont;					//font for the logo

	Color mainBGC;					//background color 
	Color sideBarBGC;				//background ground color for the side bar
	Color bottomBarBGC;		//background ground color for the bottom bar
	Color logoColor1;				//color for outside circle of logo
	Color logoColor2;				//color for inside circle of logo

	//file path for images
	private final String LYNX_PICTURE_PATH = "lynx.png";
	private final String EMPTY_STAR_PICTURE_PATH = "empty_star.png";
	private final String FILLED_STAR_PICTURE_PATH = "filled_star.png";
	private final String PAUSE_BUTTON_PICTURE_PATH = "pause_button.png";
	private final String PLAY_BUTTON_PICTURE_PATH = "play_button.png";
	private final String NEXT_SONG_BUTTON_PICTURE_PATH = "next_song_button.png";
	private final String PREVIOUS_SONG_BUTTON_PICTURE_PATH = "previous_song_button.png";

	Image lynxImage;									//image for lynx on the logo
	Image emptyStarImage;						//image of an empty star
	Image filledStarImage;							//image of a filled star
	Image pauseButtonImage;					//image of a pause button
	Image playButtonImage;						//image of a play button
	Image nextSongButtonImage;				//image of a next song button
	Image previousSongButtonImage;		//image of a previous song button

	// declare two instance variables at the head of the program
	private Image dbImage;
	private Graphics dbg;

	ArrayList <Playlist> currentListOfPlaylists;          	//the current list of playlists in the system displayed on the screen
	ArrayList <Song> currentListOfSongs;	   				//the current list of songs in the system displayed on the screen
	ArrayList <Song> playlistListOfSongs;					//the list of songs in the selected playlist, used so a new ArrayList object doesn't need to be created each time
	//the user switches between displaying all songs and displaying only the playlist's song while editing a playlist

	Playlist playedPlaylist;					//the playlist that the song that is currently playing a part of
	Song playedSong;							//the song that is currently being played

	Playlist selectedPlaylist;				//the playlist selected by the user for renaming/deletion/etc
	Song selectedSong;						//the song selected by the user for renaming/deletion/etc


	//booleans that determine which screen the program is on so the appropriate components and text can be placed on-screen
	boolean mainScreen=true;								//the main menu
	boolean viewPlaylistsScreen=false;				//screen for viewing all playlists in the system
	boolean viewSongsScreen=false;					//screen for viewing all songs in the system
	boolean viewPlaylistSongsScreen=false;		//screen for viewing all songs in the selected playlist
	boolean alterPlaylistScreen=false;					//screen for choosing a playlist to add/remove songs from
	boolean alterSelectedPlaylistScreen=false;	//screen for adding/removing songs from the selected playlist
	boolean crtDelPlaylistScreen=false;				//screen for creating/deleting playlists
	boolean crtDelSongScreen=false;					//screen for creating/deleting songs

	boolean modifyingInfo = false;		//add components/text to the screen related to renaming playlists/modifying songs when true
	boolean deleting = false;					//add text to the screen related to deleting playlists/songs when true

	boolean songPlaying = false;			//keep track of whether the current song is paused or playing
	//keeps track of whether to print a list of playlists or a list of songs
	boolean printPlaylists = false;			
	boolean printSongs = false;

	//determines if the mouse is hovering over the nth row of a list where n is the index of the boolean in the array
	boolean[]  mouseOnRow = new boolean[MAX_OBJECTS_ONSCREEN];		

	//error checkers that will display error messages on screen
	boolean successful = false;						//displays success message when a function has been successfully executed
	boolean emptyTextFields = false;			//displays error message when the TextFields on the current screen are empty
	boolean duplicate = false;						//displays error message when the song to be created is already in the system
	boolean cannotDelete = false;					//displays error message when a song cannot be deleted because it is in one or more playlists
	boolean stringToNumErrorYear = false;	//displays error message when text in the yearField cannot be converted to a int
	boolean stringToNumErrorPos = false;	//displays error message when text in the positionField cannot be converted to a int
	boolean invalidYear = false;						//displays error message when value for the year taken in is invalid 
	boolean invalidPosition = false;				//displays error message when value for the position entered in is invalid 
	boolean songNotInPlaylist = false;			//displays error message when song searched for is not in the playlist

	//Screen Buttons:  buttons that changes the screen the program is currently on
	Button returnToMainButton;			//goes back to main menu
	Button returnToPreviousButton;		//goes back to previous screen

	Button toCrtDelPlaylistButton;		//goes to screen for creating/deleting playlists
	Button toCrtDelSongButton;			//goes to screen for creating/deleting songs
	Button toAlterPlaylistButton;			//goes to screen for adding/removing songs from playlists
	Button toViewPlaylistsButton;		//goes to screen for viewing playlists
	Button toViewSongsButton;			//goes to screen for viewing songs

	Button seePlaylistSongsButton;		//switches to display list of songs in the selected in the system on alterSelectedPlaylistScreen
	Button seeAllSongsButton;				//switches to display list of all songs in the system on alterSelectedPlaylistScreen

	Button createButton;						//create a new playlist/song object
	Button saveChangesButton;			//update playlist/song object with the new information entered by the user
	Button addButton;							//add the song with the given parameters to the selected playlist
	Button removeButton;						//remove the song with the name/position from the selected playlist
	Button nextPageButton;					//switches to next page of current list
	Button previousPageButton;			//switches to previous page of current list

	//List Buttons: arrays of button that correspond to a certain playlist/song when a list of playlist/song is displayed on screen
	// first element of array corresponds first playlist/song on page, second to second, etc	
	Button[] modifyInfoButton = new Button[MAX_OBJECTS_ONSCREEN];					//fill in textfields used to modify playlist/song with information from the selected song/playlist
	Button[] viewPlaylistSongsButton = new Button[MAX_OBJECTS_ONSCREEN];			//print list of songs in the selected playlist
	Button[] editPlaylistButton = new Button[MAX_OBJECTS_ONSCREEN]; 					//goes to screen for adding/removing songs from selected playlist
	Button[] addToPlaylistButton = new Button[MAX_OBJECTS_ONSCREEN];  				//adds the information for the selected song into the TextFields used to add a song to the playlist
	Button[] removeFromPlaylistButton = new Button[MAX_OBJECTS_ONSCREEN]; 	//adds the position of the selected song to the TextField used to remove a song from a playlist
	Button[] deleteButton = new Button[MAX_OBJECTS_ONSCREEN]; 							//deletes selected playlist/song

	TextField nameField;				//takes in name of playlist/song
	TextField artistField;				//takes in artist of song
	TextField albumNameField;	//takes in name of album song is part of
	TextField yearField;				//takes in year song was released
	TextField genreField;				//takes in genre of song
	TextField positionField;			//takes in position of song in playlist

	CheckboxGroup locatingRadioGroup;	//determines where song will be added in playlist or where song to be removed is in playlist 
	Checkbox toEnd;												//song will be added to end
	Checkbox byPosition;										//song will be added to/removed from specified position in the playlist
	Checkbox byName;											//first song with specified name will be removed 

	CheckboxGroup addRemoveRadioGroup;		//determines whether song is being added to or removed from playlist
	Checkbox adding;														//song is being added
	Checkbox removing;													//song is being removed


	public void init (){
		resize (1300, 900);
		setLayout (null);

		//Hard code some playlists and songs in
		Database.createPlaylist("Starred Playlist");

		Database.createPlaylist("Derek's Slow Jams");
		Database.createPlaylist("Mohid’s Master Playlist");
		Database.createPlaylist("Yasmine’s Party Remix");

		Database.addSong(Database.getPlaylistList().get(1),"Alpha", "Grecian Pillars",  "Alphabetical Noise",1900, "a");
		Database.addSong(Database.getPlaylistList().get(1),"Beta", "Grecian Pillars",  "Alphabetical Noise", 1900,"a");
		Database.addSong(Database.getPlaylistList().get(1),"Chi", "Grecian Pillars",  "Alphabetical Noise", 1900,"a");
		Database.addSong(Database.getPlaylistList().get(1),"Delta", "Grecian Pillars",  "Alphabetical Noise", 1900,"a");
		Database.addSong(Database.getPlaylistList().get(1),"Epsilon", "Grecian Pillars",  "Alphabetical Noise", 1900,"a");
		Database.addSong(Database.getPlaylistList().get(1),"Gamma", "Grecian Pillars",  "Alphabetical Noise", 1900,"a");

		Database.addSong(Database.getPlaylistList().get(2),"Alpha", "Grecian Pillars",  "Alphabetical Noise",1900, "a");
		Database.addSong(Database.getPlaylistList().get(2),"Zeta", "Grecian Pillars",  "Alphabetical Noise",1900, "a");
		Database.addSong(Database.getPlaylistList().get(2),"Alpha", "Grecian Pillars",  "Alphabetical Noise",1900, "a");
		Database.addSong(Database.getPlaylistList().get(3),"Alpha", "Grecian Pillars",  "Alphabetical Noise",1900, "a");

		Database.createSong("Omega", "Grecian Pillars",  "Alphabetical Noise", 1900,"a");
		Database.createSong("4", "Aphex Twin",  "Richard D. James Album", 1996,"IDM");
		Database.createSong("Fingerbib", "Aphex Twin",  "Richard D. James Album", 1996,"IDM");
		Database.createSong("Girl/Boy Song", "Aphex Twin",  "Richard D. James Album", 1996,"IDM");
		Database.createSong("Yellow Submarine", "The Beatles",  "Yellow Submarine", 1969,"Psychedelic Rock");
		Database.createSong("All You Need Is Love", "The Beatles",  "Yellow Submarine", 1969,"Psychedelic Rock");
		Database.createSong("Hey Bulldog", "The Beatles",  "Yellow Submarine", 1969,"Psychedelic Rock");
		Database.createSong("Visionz", "Wu-Tang Clan",  "Wu-Tang Foever", 1997,"Hip Hop");
		Database.createSong("Triumph", "Wu-Tang Clan",  "Wu-Tang Foever", 1997,"Hip Hop");
		Database.createSong("Impossible", "Wu-Tang Clan",  "Wu-Tang Foever", 1997,"Hip Hop");
		Database.createSong("Deadly Melody", "Wu-Tang Clan",  "Wu-Tang Foever", 1997,"Hip Hop");
		Database.changeStarredList(Database.getSongList().get(3));

		//initialize current list of playlists/songs
		currentListOfPlaylists=Database.getPlaylistList();
		currentListOfSongs=Database.getSongList();

		//initialize fonts and colours
		normalFont = new Font("Shruti",Font.PLAIN,16);
		numberFont = new Font(Font.SANS_SERIF,Font.PLAIN,14);
		bigFont = new Font("Shruti",Font.PLAIN,18);
		headerFont = new Font("Calibri Light", Font.PLAIN,40);
		subheaderFont = new Font("Calibri Light", Font.PLAIN,22);
		logoFont = new Font("Rockwell", Font.ITALIC,90);

		mainBGC = new Color(20, 20, 20);
		sideBarBGC = new Color(40, 40, 40);
		bottomBarBGC = new Color(55, 55, 55);
		logoColor1 = new Color(100, 100, 100);
		logoColor2 = new Color(200, 200, 200);


		///////////////////////////////////////////////////
		//initiate labels, position, etc of every component//
		///////////////////////////////////////////////////

		returnToMainButton = new Button ("Main Menu");
		returnToMainButton.setBounds (17,20,200,50);

		returnToPreviousButton =  new Button ("Return");
		returnToPreviousButton.setBounds (127,20,90,50);

		//main screen
		toViewPlaylistsButton = new Button ("Play / View Playlists");
		toViewPlaylistsButton.setBounds (17,150,200,50);

		toViewSongsButton = new Button ("Play / View Songs");
		toViewSongsButton.setBounds (17,250,200,50);

		toAlterPlaylistButton = new Button ("Add / Remove Song from Playlist");
		toAlterPlaylistButton.setBounds (17,350,200,50);

		toCrtDelPlaylistButton = new Button ("Create / Delete Playlist");
		toCrtDelPlaylistButton.setBounds (17,450,200,50);

		toCrtDelSongButton = new Button ("Create / Delete Song");
		toCrtDelSongButton.setBounds (17,550,200,50);

		//edit playlist screen
		seePlaylistSongsButton = new Button ("View Playlist's Songs");
		seePlaylistSongsButton.setBounds (935,20,150,50);

		seeAllSongsButton = new Button ("View All Songs");
		seeAllSongsButton.setBounds (1135,20,150,50);

		//major buttons
		createButton = new Button ("Create");
		createButton.setBounds (30,715,170,50);

		saveChangesButton = new Button ("Save Changes");
		saveChangesButton.setBounds (30,715,170,50);

		addButton = new Button ("Add Song To Playlist");
		addButton.setBounds (30,715,170,50);

		removeButton = new Button ("Remove Song From Playlist");
		removeButton.setBounds (30,715,170,50);

		nextPageButton = new Button ("Next Page");
		nextPageButton.setBounds(820,770,200,50);

		previousPageButton = new Button ("Previous Page");
		previousPageButton.setBounds(520,770,200,50);

		// Attach actions to the components
		returnToMainButton.addActionListener(this);
		returnToPreviousButton.addActionListener(this);
		toViewPlaylistsButton.addActionListener(this);
		toViewSongsButton.addActionListener(this);
		toAlterPlaylistButton.addActionListener(this);
		toCrtDelPlaylistButton.addActionListener(this);
		toCrtDelSongButton.addActionListener(this);

		createButton.addActionListener(this);
		saveChangesButton.addActionListener(this);
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		seePlaylistSongsButton.addActionListener(this);
		seeAllSongsButton.addActionListener(this);
		nextPageButton.addActionListener(this);
		previousPageButton.addActionListener(this);

		//Initiate labels and position, and attach actions to the components in arrays
		for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
		{
			modifyInfoButton[x] = new Button();
			modifyInfoButton[x].setBounds(1190,150+x*40,90,30);

			viewPlaylistSongsButton[x] =  new Button("See Songs");
			viewPlaylistSongsButton[x].setBounds(1060,150+x*40,90,30);

			editPlaylistButton[x] = new Button("Edit");
			editPlaylistButton[x].setBounds(1190,150+x*40,90,30);

			addToPlaylistButton[x] = new Button("Add"); 
			addToPlaylistButton[x].setBounds(1190,150+x*40,90,30);

			removeFromPlaylistButton[x] = new Button("Remove"); 
			removeFromPlaylistButton[x].setBounds(1190,150+x*40,90,30);

			deleteButton[x] = new Button("Delete");
			deleteButton[x].setBounds(1190,150+x*40,90,30);

			modifyInfoButton[x].addActionListener(this);
			viewPlaylistSongsButton[x].addActionListener(this);
			editPlaylistButton[x].addActionListener(this);
			addToPlaylistButton[x].addActionListener(this);
			removeFromPlaylistButton[x].addActionListener(this);
			deleteButton[x].addActionListener(this);
		}

		//Text fields
		nameField = new TextField (30);
		nameField.setBounds (15, 220, 200, 30);

		artistField = new TextField (30);
		artistField.setBounds (15, 300, 200, 30);

		albumNameField = new TextField (30);
		albumNameField.setBounds (15, 380, 200, 30);

		yearField = new TextField (30);
		yearField.setBounds (15, 460, 200, 30);

		genreField = new TextField (30);
		genreField.setBounds (15, 540, 200, 30);

		positionField = new TextField (30);
		positionField.setBounds(15, 640, 200, 30);

		//Radio buttons
		//Determine where song will be added/which song will be removed in a playist
		locatingRadioGroup = new CheckboxGroup ();
		toEnd = new Checkbox ("Add to end", locatingRadioGroup, true);
		byPosition = new Checkbox ("Add to position: ", locatingRadioGroup, false);
		byName = new Checkbox("Remove song w/ title", locatingRadioGroup, false);

		toEnd.setBounds (10, 580, 200, 30);
		toEnd.setFont (normalFont);
		toEnd.setForeground(Color.lightGray);
		toEnd.setBackground(sideBarBGC);
		byPosition.setBounds (10, 610, 200, 30);
		byPosition.setFont (normalFont);
		byPosition.setForeground(Color.lightGray);
		byPosition.setBackground(sideBarBGC);
		byName.setBounds (10, 190, 200, 30);
		byName.setFont (normalFont);
		byName.setForeground(Color.lightGray);
		byName.setBackground(sideBarBGC);

		//determine whether to add or remove song to/from playlist
		addRemoveRadioGroup = new CheckboxGroup ();
		adding = new Checkbox ("Add Song", addRemoveRadioGroup, true);
		removing = new Checkbox ("Remove Song", addRemoveRadioGroup, false);

		adding.setBounds (50, 770, 130, 30);
		adding.setFont(bigFont);
		adding.setForeground(Color.lightGray);
		adding.setBackground(sideBarBGC);
		removing.setBounds (50, 800, 130, 30);
		removing.setFont(bigFont);
		removing.setForeground(Color.lightGray);
		removing.setBackground(sideBarBGC);

		//Detect changes in radio buttons
		adding.addItemListener(this);
		removing.addItemListener(this);


		add(toCrtDelPlaylistButton);
		add(toCrtDelSongButton);
		add(toAlterPlaylistButton);
		add(toViewPlaylistsButton);
		add(toViewSongsButton);

		/*This is what I use if I want to put a picture on the screen*/
		lynxImage = loadImage (LYNX_PICTURE_PATH);
		emptyStarImage = loadImage (EMPTY_STAR_PICTURE_PATH);
		filledStarImage = loadImage (FILLED_STAR_PICTURE_PATH);
		pauseButtonImage = loadImage (PAUSE_BUTTON_PICTURE_PATH);
		playButtonImage = loadImage (PLAY_BUTTON_PICTURE_PATH);
		nextSongButtonImage = loadImage (NEXT_SONG_BUTTON_PICTURE_PATH);
		previousSongButtonImage = loadImage (PREVIOUS_SONG_BUTTON_PICTURE_PATH);
		prepareImage (lynxImage, this);
		prepareImage (emptyStarImage, this);
		prepareImage (filledStarImage, this);
		prepareImage (pauseButtonImage, this);
		prepareImage (playButtonImage, this);
		prepareImage (nextSongButtonImage, this);
		prepareImage (previousSongButtonImage, this);

		// Now, it can actually take some time to load the image, and
		// it could fail (image not found, etc).  The following checks for
		// all that.
		MediaTracker tracker = new MediaTracker (this);
		// Add the picture to the list of images to be tracked
		tracker.addImage (lynxImage, 0);
		tracker.addImage (emptyStarImage, 0);
		tracker.addImage (filledStarImage, 0);
		tracker.addImage (pauseButtonImage, 0);
		tracker.addImage (playButtonImage, 0);
		tracker.addImage (nextSongButtonImage, 0);
		tracker.addImage (previousSongButtonImage, 0);
		// Wait until all the images are loaded.  This can throw an
		// InterruptedException although it's not likely, so we ignore
		// it if it occurs.
		try
		{
			tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}
		// If there were any errors loading the image, then abort the
		// program with a message.
		if (tracker.isErrorAny ())
		{
			showStatus ("Couldn't load ");
			return;
		}

		//Detect mouse inputs
		addMouseListener (this);
		addMouseMotionListener(this); 
	}

	private static BufferedImage loadImage(String imgPath){
		BufferedInputStream imgStream = new BufferedInputStream(SlewifyGUI.class.getResourceAsStream("/"+imgPath));
		if(imgStream != null){
			try {
				return javax.imageio.ImageIO.read(imgStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		return null;
	}

	/** Update - Method, implements double buffering */
	public void update (Graphics g){

		// initialize buffer
		if (dbImage == null)
		{
			dbImage = createImage (this.getSize ().width, this.getSize ().height);
			dbg = dbImage.getGraphics ();
		}

		// clear screen in background
		dbg.setColor (getBackground ());
		dbg.fillRect (0, 0, this.getSize ().width, this.getSize ().height);

		// draw elements in background
		dbg.setColor (getForeground ());
		paint (dbg);

		// draw image on the screen
		g.drawImage (dbImage, 0, 0, this);

	}

	/**	Check parameters entered into the TextFields by user for any errors or invalid values and display the appropriate error message
	 *		Encompasses the non-unique errors, more specific ones will be checked for in the calling method
	 */
	private void dummyProofing ()
	{
		//reset error checkers to false so error messages aren't displayed until an error comes up
		emptyTextFields=false;
		stringToNumErrorYear=false;
		invalidYear=false;
		duplicate=false;
		successful=false;

		//check playlist related errors
		if (printPlaylists==true)
		{
			//displays error message if any of the text fields are not filled in
			if (nameField.getText().equals(""))
				emptyTextFields=true;
		}
		//check song related errors
		else if (printSongs==true)
		{
			int year=0;

			//displays error message if any of the text fields are not filled in
			if (nameField.getText().equals("")||artistField.getText().equals("")||albumNameField.getText().equals("")
					||yearField.getText().equals("")||genreField.getText().equals(""))
				emptyTextFields=true;

			//converts year entry into a int, display error message if not possible
			try
			{
				year = Integer.parseInt(yearField.getText());
			}
			catch (NumberFormatException e)
			{
				stringToNumErrorYear=true;
				yearField.setText("");
			}

			//displays error message if year was converted properly, but are invalid values 
			if (year<=0 || year>=10000)
			{
				invalidYear=true;
				yearField.setText("");
			}
		}

		repaint();
	}

	/**determines number of pages of current list of playlist/song and adds all appropriate list buttons when list is first added onto the screen
	 * i.e. page number of list is 1, next/previous page buttons have not been pressed
	 */
	private void addListButtons ()
	{
		//determine number of pages of current list 
		//the number of pages is the size of the current playlist/song list divided by 15, rounded up
		if (printPlaylists==true)					//number of pages based on current user list
		{
			numOfPages=currentListOfPlaylists.size()/MAX_OBJECTS_ONSCREEN;
			if (currentListOfPlaylists.size()%MAX_OBJECTS_ONSCREEN!=0)
				numOfPages+=1;
		}
		else if (printSongs==true)				//number of pages based on current book list
		{
			numOfPages=currentListOfSongs.size()/MAX_OBJECTS_ONSCREEN;
			if (currentListOfSongs.size()%MAX_OBJECTS_ONSCREEN!=0)
				numOfPages+=1;
		}


		pageNum=1;				//display first page of list

		//remove all of the list buttons currently on screen
		for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
		{
			remove(modifyInfoButton[x]);
			remove(viewPlaylistSongsButton[x]);
			remove(editPlaylistButton[x]);
			remove(addToPlaylistButton[x]);
			remove(removeFromPlaylistButton[x]);
			remove(deleteButton[x]);
		}

		remove(nextPageButton);
		remove(previousPageButton);

		//determines how many list buttons need to be added on screen
		//If 1st page # of buttons added = size of list, if greater than 1,  # of buttons added = 15
		int end = 0;							//the end of the for loop
		if (numOfPages == 1)
		{
			if (printPlaylists == true)
				end = currentListOfPlaylists.size();
			else if (printSongs == true)
				end = currentListOfSongs.size();
		}
		else if (numOfPages > 1)
		{
			//adds next page button if there is more than 1 page
			add(nextPageButton);
			end = MAX_OBJECTS_ONSCREEN;
		}

		//adds list buttons until x reaches end
		for (int x=0;x<end;x++)
		{
			//add list buttons that have to with playlists
			if (printPlaylists==true) 					
			{
				if (viewPlaylistsScreen == true)
					add(viewPlaylistSongsButton[x]);
				//does not allow user to rename, edit, or delete Starred Playlist
				if (x > 0)
				{
					if (viewPlaylistsScreen == true)
						add(modifyInfoButton[x]);
					if (alterPlaylistScreen == true)
						add(editPlaylistButton[x]);
					if (crtDelPlaylistScreen == true)
						add(deleteButton[x]);
				}
			}
			//add list buttons that have to with songs
			else if (printSongs==true);		
			{
				if (viewSongsScreen == true)
					add(modifyInfoButton[x]);
				//when removing songs, only allow selecting songs in the playlist for removal from the playlist
				//when adding songs, only allow selecting songs from system's general list of songs for addition to the playlist
				if (alterSelectedPlaylistScreen == true)
				{
					if (currentListOfSongs.equals(playlistListOfSongs) && removing.getState() == true)
						add(removeFromPlaylistButton[x]);
					else if (currentListOfSongs.equals(Database.getSongList()) && adding.getState() == true)
						add(addToPlaylistButton[x]);
				}
				if (crtDelSongScreen == true)
					add(deleteButton[x]);
			}
		}
	}


	/**	Prints list of users or books in system meant to be displayed on screen
	 * 	Which playlist/song are displayed is based on search parameters and page number of current list
	 */
	private void displayList (Graphics g)
	{
		int listIndex;			//index of playlist/song to be displayed on screen in current list of playlist/song
		int yPos;					//the y-coordinate of the playlist/song to be displayed on screen

		//prints list of playlists if the current screen is playlist related
		if (printPlaylists==true)
		{
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("#",240, 125);
			g.drawString("PLAYLIST",270, 125);

			g.setColor(Color.darkGray);
			g.drawLine(237, 145, 1278, 145);

			//prints each playlist's information row by row in a table
			for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
			{
				listIndex = x + (pageNum-1)*MAX_OBJECTS_ONSCREEN;
				yPos=170 + x*40;

				//break out of loop when there are no more playlists to be printed
				try 
				{
					g.setFont(normalFont);
					g.setColor(Color.white);
					g.drawString(currentListOfPlaylists.get(listIndex).getName(),270,yPos);

					g.setFont(numberFont);
					g.setColor(Color.lightGray);
					g.drawString(Integer.toString(listIndex+1),240,yPos);
					g.setColor(Color.darkGray);
					g.drawLine(237, yPos +15, 1278, yPos+15);
				}
				catch (IndexOutOfBoundsException e)
				{
					break;
				}
			}


		}
		//prints list of songs if the current screen is song related
		else if (printSongs==true)
		{
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("#",310, 125);
			g.drawString("TITLE",340, 125);
			g.drawString("ARTIST",550, 125);
			g.drawString("ALBUM",760, 125);
			g.drawString("YEAR",960, 125);
			g.drawString("GENRE",1030, 125);

			g.setColor(Color.darkGray);
			g.drawLine(237, 145, 1278, 145);

			//prints each Book's information row by row in a table
			for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
			{
				listIndex = x + (pageNum-1)*MAX_OBJECTS_ONSCREEN;
				yPos=170 + x*40;

				//break out of loop when there are no more songs to be printed
				try 
				{
					g.setFont(normalFont);
					g.setColor(Color.white);
					g.drawString(currentListOfSongs.get(listIndex).getName(),340,yPos);
					g.drawString(currentListOfSongs.get(listIndex).getArtist(),550,yPos);
					g.drawString(currentListOfSongs.get(listIndex).getAlbumName(),760,yPos);
					g.drawString(currentListOfSongs.get(listIndex).getGenre(),1030,yPos);

					//if the mouse is on the row the song at listIndex is on, show a pause or play button 
					//depending on whether song is currently playing or not
					if (mouseOnRow[x] == true)
					{
						if (currentListOfSongs.get(listIndex) == playedSong && songPlaying == true)
							g.drawImage(pauseButtonImage,235, yPos - 19,30,30,null);
						else
							g.drawImage(playButtonImage,235, yPos - 19,30,30,null);
					}
					//draw filled star if song is starred, empty star if it is not
					if (currentListOfSongs.get(listIndex).isStarred()==true)
						g.drawImage(filledStarImage,270,yPos - 20,30,30,null);
					else 
						g.drawImage(emptyStarImage,270,yPos - 20,30,30,null);

					//pad year with leading zeros until it is 4 digits long		
					g.setFont(numberFont);
					g.drawString(String.format("%04d",currentListOfSongs.get(listIndex).getYear()),965,yPos);

					g.setColor(Color.lightGray);
					g.drawString(Integer.toString(listIndex+1),310,yPos);
					g.setColor(Color.darkGray);
					g.drawLine(237, yPos +15, 1278, yPos+15);

				}
				catch (IndexOutOfBoundsException e)
				{
					break;
				}
			}


		}
	}

	/**	Prints all the non-unique labels for the data entry fields and the associated error messages
	 */
	private void displayDataEntryLabels (Graphics g){
		//Print labels showing what to enter in each TextField and playlist related errors
		if (printPlaylists == true)
		{
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("NAME OF PLAYLIST", 15, 215);

			//error messages
			g.setColor(Color.red);
			if (emptyTextFields==true)
				g.drawString("Fill in every field", 52, 280);
		}
		//Print labels showing what to enter in each TextField and song related errors
		if (printSongs == true)
		{
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("TITLE", 15, 215);
			g.drawString("ARTIST", 15, 295);
			g.drawString("ALBUM", 15, 375);
			g.drawString("YEAR (0-9999)", 15, 455);
			g.drawString("GENRE", 15, 535);

			//error messages
			g.setColor(Color.red);
			if (emptyTextFields==true)
				g.drawString("Fill in every field", 52, 700);

			//yearField related errors
			if (stringToNumErrorYear==true)
				g.drawString("Enter in a whole number", 17, 505);
			else if (invalidYear==true)
				g.drawString("Enter in a valid year", 37, 505);
		}
	}


	/**Display text and images on screen*/
	public void paint (Graphics g){
		//ALWAYS DRAW START//

		g.setColor(mainBGC);
		g.fillRect(0, 0, 1300, 900);
		g.setColor(sideBarBGC);
		g.fillRect(0, 0, 230, 900);
		g.setColor(bottomBarBGC);
		g.fillRect(0, 840, 1300, 60);
		//draw big play/pause button depending on whether a song is playing or not
		if (songPlaying == true)
			g.drawImage(pauseButtonImage, 50,838, null);
		else
			g.drawImage(playButtonImage, 50,838, null);
		g.drawImage(nextSongButtonImage, 120,855, null);
		g.drawImage(previousSongButtonImage, 20,855, null);

		g.setFont(bigFont);
		g.setColor(Color.lightGray);
		//Display which song is currently playing
		//Show next song in playlist if the song currently being played is being played from a playlist
		if (playedSong == null)
			g.drawString("Current Song: None", 200, 875);
		else if (playedPlaylist == null)
			g.drawString("Current Song: " + playedSong.getName() + " - " + playedSong.getArtist(), 200, 875);
		else
		{
			g.drawString("Current Song: " + playedPlaylist.getCurrentSong().getSong().getName() + " - " + playedPlaylist.getCurrentSong().getSong().getArtist(), 200, 875);
			g.drawString("Next Song: " + playedPlaylist.getNextSong().getSong().getName() + " - " +playedPlaylist.getCurrentSong().getSong().getArtist(), 750, 875);
		}

		//ALWAYS DRAW END//


		//Draw headings and subheadings on screen when viewing a list
		if (printPlaylists == true)			//Headings for playlist list
		{
			g.setColor(Color.white);
			g.setFont(headerFont);
			g.drawString("Playlists", 245, 45);
		}
		else if (printSongs == true)		//Headings for song list
		{
			g.setColor(Color.white);
			g.setFont(headerFont);
			if (currentListOfSongs == Database.getSongList())		//Heading for general song list
				g.drawString("Songs", 245, 45);
			if (currentListOfSongs == playlistListOfSongs)				//Heading for a playlist's list of songs
			{
				g.drawString(selectedPlaylist.getName(), 245, 45);								//playlist name
				g.setColor(Color.lightGray);
				g.setFont(subheaderFont);
				g.drawString(selectedPlaylist.getListSize() + " song(s)", 255, 65);		//# of songs in playlist
				//Display first and last song in playlist if playlist is not empty
				if (selectedPlaylist.getListSize() > 0)
				{
					g.setFont(bigFont);
					g.drawString("First Song:", 405, 65);
					g.drawString("Last Song:", 405, 85);
					g.drawString(selectedPlaylist.getHead().getSong().getName() + " - " + selectedPlaylist.getHead().getSong().getArtist(), 500, 65);
					g.drawString(selectedPlaylist.getTail().getSong().getName() + " - " + selectedPlaylist.getTail().getSong().getArtist(), 500, 85);
				}
			}
		}


		if (mainScreen==true)
		{
			//draw logo
			g.setColor(logoColor1);
			g.fillOval(595, 300, 340, 340);
			g.setColor(logoColor2);
			g.fillOval(685, 390, 160, 160);
			g.setColor(Color.white);
			g.fillOval(695, 400, 140, 140);
			g.setColor(Color.black);
			g.drawOval(675, 380, 180, 180);
			g.setColor(Color.darkGray);
			g.drawOval(600, 305, 330, 330);
			g.drawOval(615, 320, 300, 300);
			g.drawOval(635, 340, 260, 260);
			g.drawOval(645, 350, 240, 240);
			g.drawOval(655, 360, 220, 220);
			g.drawImage(lynxImage, 715, 420,100,100,null);
			g.setFont(logoFont);
			g.setColor(Color.red);
			g.drawString("Slewify", 630, 240);
		}

		if (viewPlaylistsScreen==true)
		{
			displayList(g);
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("RENAME",1200, 125);
			g.drawString("PLAYLIST",1195, 140);

			if (successful == false && modifyingInfo == true)				//display instructions for renaming playlists
			{
				displayDataEntryLabels(g);

				g.setFont(bigFont);
				g.setColor(Color.lightGray);
				g.drawString("Enter in a new name ", 33,120);
				g.drawString("for the playlist", 60,140);
			}
			else if (successful==true && modifyingInfo==true)			//display success message
			{
				g.setFont(bigFont);
				g.setColor(Color.lightGray);
				g.drawString("Playlist successfully renamed", 6, 110);
			}
		}

		if (viewSongsScreen==true)
		{
			displayList(g);
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("MODIFY",1205, 125);
			g.drawString("SONG",1210, 140);
			if (successful == false && modifyingInfo == true)				//display instructions for modifying songs
			{
				displayDataEntryLabels(g);

				g.setFont(bigFont);
				g.setColor(Color.lightGray);
				g.drawString("Type in the song's ", 44,100);
				g.drawString("new information below", 28,120);

				//display error message
				g.setColor(Color.red);
				if (duplicate==true)															
				{
					g.drawString("The new information cannot", 10, 610);
					g.drawString("match a song already", 35, 630);
					g.drawString("in the system", 60, 650);
				}
			}
			else if (successful==true && modifyingInfo==true)				//display success message
			{
				g.setFont(bigFont);
				g.setColor(Color.lightGray);
				g.drawString("Song successfully modified", 13, 110);
			}
		}

		if (viewPlaylistSongsScreen == true)
		{
			displayList(g);
		}

		if (alterPlaylistScreen==true)
		{
			displayList(g);
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("ADD/REMOVE",1177, 125);
			g.drawString("SONGS ",1205, 140);
		}

		if (alterSelectedPlaylistScreen==true)
		{
			displayList(g);
			if (adding.getState() == true)													//display instructions for adding songs to a playlist
			{
				displayDataEntryLabels(g);

				g.setFont(bigFont);
				g.setColor(Color.lightGray);
				g.drawString("Enter a song's information", 10,100);
				g.drawString("below to add it to the playlist", 5,115);
				g.drawString("OR select a song from the ", 5, 130);
				g.drawString("list to the right to fill them", 5, 145);
				g.drawString("in automatically.", 5, 160);
				//Display only when looking at system's general list of songs
				if (currentListOfSongs.equals(Database.getSongList()))
				{
					g.drawString("ADD TO",1200, 125);
					g.drawString("PLAYLIST",1195, 140);
				}

				//display success message
				if (successful==true)
					g.drawString("Song added to Playlist", 27, 700);

				//display error messages
				g.setColor(Color.red);
				if (stringToNumErrorPos==true)
					g.drawString("Enter in a whole number", 17, 685);
				else if (invalidPosition==true)
				{
					g.drawString("Enter in a position", 47, 685); 
					g.drawString("within the existing playlist", 17, 700);
				}
			}
			else if (removing.getState())														//display instructions for removing songs from a playlist
			{
				g.setFont(bigFont);
				g.setColor(Color.lightGray);
				g.drawString("Enter the name or position ", 10,100);
				g.drawString("of the song to be deleted ", 5,115);
				g.drawString("below OR select a song from", 5, 130);
				g.drawString("the list to the right to fill the", 5, 145);
				g.drawString("position in automatically", 5, 160);
				//Display only when looking at playlist list of songs
				if (currentListOfSongs.equals(playlistListOfSongs))
				{
					g.drawString("REMOVE FROM",1175, 125);
					g.drawString("PLAYLIST",1200, 140);
				}
				g.setFont(normalFont);
				g.drawString("Note: only the first song with" , 15, 265);
				g.drawString("this title will be removed" , 57, 280);

				//display success message
				if (successful==true)
					g.drawString("Song removed from Playlist", 12, 390);

				//display error messages
				g.setColor(Color.red);
				if (emptyTextFields==true)
					g.drawString("Fill in the required field", 52, 390);

				//positionField related errors
				if (stringToNumErrorPos == true)
					g.drawString("Enter in a whole number", 17, 375);
				else if (invalidPosition == true)
				{
					g.drawString("Enter in a position", 47, 375); 
					g.drawString("within the existing playlist", 17, 390);
				}

				if (songNotInPlaylist == true)
				{
					g.drawString("Song with the entered title", 17, 375); 
					g.drawString("was not found in playlist", 17, 390);
				}

			}
		}

		if (crtDelPlaylistScreen==true)
		{
			displayList(g);
			displayDataEntryLabels(g);

			//display instructions for creating and deleting playlist
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("Create a new playlist ", 33,120);
			g.drawString("DELETE FROM",1175, 125);
			g.drawString("SYSTEM",1200, 140);

			//display success message
			if (successful==true && deleting == false)	
				g.drawString("Playlist Creation Successful!", 12, 280);
			else if (successful==true && deleting == true)			
				g.drawString("Playlist successfully deleted", 10, 280);
		}

		if (crtDelSongScreen==true)
		{
			displayList(g);
			displayDataEntryLabels(g);

			//display instructions for creating and deleting playlist
			g.setFont(bigFont);
			g.setColor(Color.lightGray);
			g.drawString("Create a song and add it to", 10,100);
			g.drawString("the system by entering in all", 10,120);
			g.drawString("of the song's information", 10, 140);
			g.drawString("DELETE FROM",1175, 125);
			g.drawString("SYSTEM",1200, 140);

			//display success message
			if (successful==true && deleting == false)
				g.drawString("Song Creation Successful!", 17, 610);
			else if (successful==true && deleting == true)
				g.drawString("Song successfully deleted", 16, 610);

			//display error messages
			g.setColor(Color.red);
			if (duplicate==true)
			{
				g.drawString("This song already exists", 20, 610);
				g.drawString("in the system", 60, 630);
			}

			if (cannotDelete==true)
			{
				g.drawString("This song cannot be deleted", 7, 610);
				g.drawString("because it is in one or more", 7, 630);
				g.drawString("playlists. You must remove", 7, 650);
				g.drawString("this song from every playlist", 7, 670);
				g.drawString("before it can be deleted.", 7, 690);
			}
		}

	}

	@Override
	/**This method will be called when the mouse has been dragged i.e. pressed and moved*/
	public void mouseDragged(MouseEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	/**This method will be called when the mouse has been moved*/
	public void mouseMoved(MouseEvent me) {
		// TODO Auto-generated method stub
		if (printSongs==true)
		{
			int yPos;

			//determine if mouse is on one of the rows in the song list so pause/play button can be displayed
			for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
			{
				yPos=170 + x*40;

				if (me.getX() >= 230 && me.getX() < 1300 && me.getY() >= yPos-25 && me.getY() < yPos+15) 
					mouseOnRow[x] = true;
				else 
					mouseOnRow[x] = false;
			}
		}
		repaint();
	}

	@Override
	/**This method will be called when the mouse has been clicked i.e. pressed and released*/
	public void mouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub
	}

	@Override
	/**This method will be called when the mouse has entered the applet*/
	public void mouseEntered(MouseEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	/**This method will be called when the mouse has entered the applet*/
	public void mouseExited(MouseEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	/**This method will be called when the mouse has been pressed*/
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub

		//pause or play current song, if there is a current song to play or pause
		if (me.getX() >= 52 && me.getX() <= 112 && me.getY() >= 840 && me.getY() <= 900)
		{
			if (playedSong != null)
			{
				if (songPlaying == false)										//play current song if song is paused
					songPlaying = true;
				else if (songPlaying == true)								//pause current song if song is being played
					songPlaying = false;
			}
		}

		//switch song to the previous or next song in the list, if the current song being played is being played out of a playlist
		else if (me.getY() >= 855 && me.getY() <= 880)
		{
			if (playedPlaylist != null)
			{
				if (me.getX() >= 20 && me.getX() <= 45)														//previous song
					playedPlaylist.setCurrentSong(playedPlaylist.getPreviousSong());
				if (me.getX() >= 120 && me.getX() <= 145)													//next song
					playedPlaylist.setCurrentSong(playedPlaylist.getNextSong());
			}
		}


		//only take action when screen is displaying a list of songs
		if (printSongs==true)
		{
			int listIndex;			//index of song in the clicked row in current list of songs
			int yPos;					//the y-coordinate of the clicked row

			//determine if there was a click in each row of the list and take action based on where in the row mouse was clicked
			for (int x=0;x<MAX_OBJECTS_ONSCREEN;x++)
			{
				listIndex = x + (pageNum-1)*MAX_OBJECTS_ONSCREEN;
				yPos=150 + x*40;

				//break out of loop when there are no rows with songs in the list
				try 
				{
					//clicked on pause/play play, pauses or plays the song in that row
					if (me.getX() >= 235 && me.getX() <= 265 && me.getY() >= yPos && me.getY() <= yPos+30) 
					{
						//if the song clicked is the song currently being played or paused, play the song if paused, pause the song if playing
						//else, play the song clicked
						if (currentListOfSongs.get(listIndex) == playedSong && songPlaying == true)
							songPlaying = false;
						else if (currentListOfSongs.get(listIndex) == playedSong && songPlaying == false)
							songPlaying = true;
						else
						{
							songPlaying = true;
							//set current song of the previous playlist to null
							if (playedPlaylist != null)
								playedPlaylist.setCurrentSong(null);
							//if song was clicked from a playlist, switch current played playlist to the playlist song was clicked from
							//if clicked from the system's general song list, switch current playlist to none
							if (currentListOfSongs.equals(playlistListOfSongs))
							{
								playedPlaylist = selectedPlaylist;
								playedPlaylist.setCurrentSong(listIndex);								//keep track of which song is playing in the playlist
								playedSong = playedPlaylist.getCurrentSong().getSong();
							}
							else if (currentListOfSongs.equals(Database.getSongList()))
							{
								playedPlaylist = null;
								playedSong = currentListOfSongs.get(listIndex);
							}
						}
						break;		//break out of loop after action is performed
					}
					//change the starred status of the song in that row
					else if (me.getX() >= 270 && me.getX() <= 300 && me.getY() >= yPos && me.getY() <= yPos+30)
					{
						Database.changeStarredList(currentListOfSongs.get(listIndex));
						break;		//break out of loop after action is performed
					}
				}
				catch (IndexOutOfBoundsException e)
				{
					break;
				}
			}
		}
		repaint();

	}

	@Override
	/**This method will be called when the mouse has been released*/
	public void mouseReleased(MouseEvent me) {
		// TODO Auto-generated method stub

	}


	@Override
	/**This method was detect changes in the readio buttons that are listened to*/
	public void itemStateChanged(ItemEvent ie) {
		// TODO Auto-generated method stub
		//reset error checkers to false so error messages aren't displayed until an error comes up
		emptyTextFields = false;
		successful = false;
		stringToNumErrorYear = false;	
		stringToNumErrorPos = false;	
		invalidYear = false;	
		invalidPosition = false;
		songNotInPlaylist = false;

		//clear textfields
		nameField.setText("");
		artistField.setText("");
		albumNameField.setText("");
		yearField.setText("");
		genreField.setText("");
		positionField.setText("");

		//add, remove, and reposition components when state changed
		if (adding.getState() == true)				//change components to ones related to adding songs to playlists
		{
			remove(removeButton);
			remove(byName);
			add(artistField);
			add(albumNameField);
			add(yearField);
			add(genreField);
			add(addButton);
			add(toEnd);
			add(byPosition);

			toEnd.setState(true);
			byPosition.setLabel("Add to position:");
			byPosition.setBounds (10, 610, 200, 30);
			positionField.setBounds(15, 640, 200, 30);
		}
		else if (removing.getState() == true)		//change components to ones related to removing songs from playlists
		{
			remove(artistField);
			remove(albumNameField);
			remove(yearField);
			remove(genreField);
			remove(addButton);
			remove(toEnd);
			add(removeButton);
			add(byPosition);
			add(byName);

			byName.setState(true);
			byPosition.setLabel("Remove at position:");
			byPosition.setBounds (10, 300, 200, 30);
			positionField.setBounds(15, 330, 200, 30);
		}
		addListButtons();
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub



		if (evt.getSource() == toViewPlaylistsButton || evt.getSource() == returnToPreviousButton && viewPlaylistSongsScreen == true)
		{
			//set booleans to display appropriate screen
			mainScreen = false;
			viewPlaylistsScreen = true;
			viewPlaylistSongsScreen = false;
			printPlaylists = true;
			printSongs = false;
			//make current list the full list of playlists in the system
			currentListOfPlaylists=Database.getPlaylistList();

			removeAll();

			//change bounds and labels of buttons to the appropriate values for the screen
			returnToMainButton.setBounds (17,20,200,50);
			for (int x = 0; x < MAX_OBJECTS_ONSCREEN; x++)
				modifyInfoButton[x].setLabel("Rename");

			//add components to screen
			add(returnToMainButton);

			addListButtons();

			repaint();
		}

		if (evt.getSource() == toViewSongsButton)
		{
			//set booleans to display appropriate screen
			mainScreen = false;
			viewSongsScreen = true;
			printPlaylists = false;
			printSongs = true;
			//make current list the full list of songs in the system
			currentListOfSongs=Database.getSongList();

			removeAll();

			//change bounds and labels of buttons to the appropriate values for the screen
			for (int x = 0; x < MAX_OBJECTS_ONSCREEN; x++)
				modifyInfoButton[x].setLabel("Edit Info");

			//add components to screen
			add(returnToMainButton);

			addListButtons();

			repaint();
		}

		if (evt.getSource() == toAlterPlaylistButton || evt.getSource() == returnToPreviousButton && alterSelectedPlaylistScreen == true)
		{
			//set booleans to display appropriate screen
			mainScreen = false;
			alterPlaylistScreen = true;
			alterSelectedPlaylistScreen = false;
			printPlaylists = true;
			printSongs = false;
			//make current list the full list of playlists in the system
			currentListOfPlaylists=Database.getPlaylistList();

			removeAll();

			//change bounds and labels of buttons to the appropriate values for the screen
			returnToMainButton.setBounds (17,20,200,50);

			//add components to screen
			add(returnToMainButton);

			addListButtons();

			repaint();
		}

		if (evt.getSource() == toCrtDelPlaylistButton)
		{
			//set booleans to display appropriate screen
			mainScreen = false;
			crtDelPlaylistScreen = true;
			printPlaylists = true;
			printSongs = false;
			//make current list the full list of playlists in the system
			currentListOfPlaylists=Database.getPlaylistList();

			removeAll();

			//add components to screen
			add(returnToMainButton);
			add(createButton);
			add(nameField);

			addListButtons();

			repaint();
		}

		if (evt.getSource() == toCrtDelSongButton)
		{
			//set booleans to display appropriate screen
			mainScreen = false;
			crtDelSongScreen = true;
			printPlaylists = false;
			printSongs = true;
			//make current list the full list of songs in the system
			currentListOfSongs = Database.getSongList();

			removeAll();

			//add components to screen
			add(returnToMainButton);
			add(createButton);
			add(nameField);
			add(artistField);
			add(albumNameField);
			add(yearField);
			add(genreField);

			addListButtons();

			repaint();
		}

		if (evt.getSource () == seePlaylistSongsButton)
		{
			//make current list the full list of songs in the current selected playlist
			currentListOfSongs = playlistListOfSongs;

			addListButtons();
			repaint();
		}

		if (evt.getSource () == seeAllSongsButton)
		{
			//make current list the full list of songs in the system
			currentListOfSongs = Database.getSongList();

			addListButtons();
			repaint();
		}


		////////////////////////////////////////////
		///////////IMPORTANT BUTTONS///////////
		////////////////////////////////////////////



		//switches to next page of the current list
		if (evt.getSource () == nextPageButton)
		{
			pageNum+=1;

			//reset error checkers to false so error messages aren't displayed until an error comes up
			successful=false;
			modifyingInfo=false;
			deleting=false;

			remove(saveChangesButton);		//remove button because user is no longer modifying info

			//adds list buttons that would have been missing from the first page of playlist list b/c they corresponded to Starred Playlist
			if (pageNum > 1 && printPlaylists == true)
			{
				if (viewPlaylistsScreen==true)
					add(modifyInfoButton[0]);
				if (alterPlaylistScreen==true)
					add(editPlaylistButton[0]);
				if (crtDelPlaylistScreen==true)
					add(deleteButton[0]);
			}

			/**	When switching to the last page of list, removes next page button and 
			 * 	remove list buttons on screen to match number of playlists/songs on page.
			 * 	(ex. 10 playlists/songs on page = 10 of each list button on screen, so 5 list buttons are removed)
			 */
			if (pageNum>=numOfPages)
			{
				remove(nextPageButton);
				if (printPlaylists==true)					//remove playlist related list buttons
				{
					for (int x=MAX_OBJECTS_ONSCREEN-1;x>=currentListOfPlaylists.size()-(pageNum-1)*MAX_OBJECTS_ONSCREEN;x--)
					{
						remove(modifyInfoButton[x]);
						remove(viewPlaylistSongsButton[x]);
						remove(editPlaylistButton[x]);
						remove(deleteButton[x]);
					}
				}
				else if (printSongs==true)			//remove song related list button
				{
					for (int x=MAX_OBJECTS_ONSCREEN-1;x>=currentListOfSongs.size()-(pageNum-1)*MAX_OBJECTS_ONSCREEN;x--)
					{
						remove(modifyInfoButton[x]);
						remove(addToPlaylistButton[x]);
						remove(removeFromPlaylistButton[x]);
						remove(deleteButton[x]);
					}
				}
			}
			add(previousPageButton);

			repaint();
		}

		//switches to previous page of the current list
		if (evt.getSource () == previousPageButton)
		{
			pageNum-=1;

			//reset error checkers to false so error messages aren't displayed until an error comes up
			successful = false;
			modifyingInfo = false;
			deleting = false;

			remove(saveChangesButton);		//remove button because user is no longer modifying info

			//removes list buttons if it is switching to the first page of playlist list b/c they correspond to Starred Playlist
			if (pageNum == 1 && printPlaylists == true)
			{
				remove(modifyInfoButton[0]);
				remove(editPlaylistButton[0]);
				remove(deleteButton[0]);
			}

			/**	When switching from the last page of the list, add list buttons on screen to match max number of playlists/songs on page 
			 * 	(ex. 10 playlists/songs on last page = 10 list buttons on screen, so 5 list buttons added to make 15)
			 */
			if (pageNum+1==numOfPages)
			{
				if (printPlaylists==true)				//add playlist related list buttons
				{
					for (int x=MAX_OBJECTS_ONSCREEN-1;x>=currentListOfPlaylists.size()%MAX_OBJECTS_ONSCREEN;x--)
					{
						//adds list buttons depending on which screen program is on
						if (viewPlaylistsScreen==true)
						{
							add(viewPlaylistSongsButton[x]);
							add(modifyInfoButton[x]);
						}
						if (alterPlaylistScreen==true)
							add(editPlaylistButton[x]);
						if (crtDelPlaylistScreen==true)
							add(deleteButton[x]);
					}
				}
				else if (printSongs==true)			//add song related list buttons
				{
					for (int x=MAX_OBJECTS_ONSCREEN-1;x>=currentListOfSongs.size()%MAX_OBJECTS_ONSCREEN;x--)
					{
						//adds list buttons depending on which screen program is on
						if (viewSongsScreen==true)
							add(modifyInfoButton[x]);
						if (alterSelectedPlaylistScreen == true)
						{
							if (currentListOfSongs.equals(playlistListOfSongs) && removing.getState() == true)
								add(removeFromPlaylistButton[x]);
							else if (currentListOfSongs.equals(Database.getSongList()) && adding.getState() == true)
								add(addToPlaylistButton[x]);
						}
						if (crtDelSongScreen==true)
							add(deleteButton[x]);
					}
				}
			}
			//when switching to the first page of the list, remove previous page button 
			if (pageNum<=1)
				remove(previousPageButton);
			add(nextPageButton);

			repaint();
		}

		//creates a playlist or song object and adds it to the system
		if (evt.getSource () == createButton)
		{
			dummyProofing();
			deleting = false;			//program is creating, so it's not deleting
			//creates a playlist and adds it to the system if there aren't any errors
			if (printPlaylists==true)
			{
				/**If there aren't any errors, adds a Playlist object to the database using information entered into the Text Fields 
				 * Does not check for duplicates
				 */
				if (emptyTextFields==false)
				{
					Database.createPlaylist(nameField.getText());
					successful=true;
					//clear TextFields
					nameField.setText("");
					//update list
					currentListOfPlaylists=Database.getPlaylistList();
					addListButtons();
				}
			}
			//creates a book and adds it to the system if there aren't any errors
			else if (printSongs==true)
			{
				/**If there aren't any errors, adds a Song object to the database using information entered into the Text Fields 
				 * Displays error message if duplicate
				 * Does not check for duplicates if they are no Songs in the Database
				 */
				if (emptyTextFields==false && invalidYear==false)
				{
					if (Database.getSongList().isEmpty()==false && Database.isDuplicateSong(nameField.getText(), artistField.getText(),
							albumNameField.getText(), Integer.parseInt(yearField.getText()), genreField.getText()))
						duplicate = true;
					else
					{
						Database.createSong(nameField.getText(), artistField.getText(), albumNameField.getText(), 
								Integer.parseInt(yearField.getText()), genreField.getText());
						successful=true;
						//clear TextFields
						nameField.setText("");
						artistField.setText("");
						albumNameField.setText("");
						yearField.setText("");
						genreField.setText("");
						//update list
						currentListOfSongs=Database.getSongList();
						addListButtons();
					}
				}
			}
			repaint();
		}

		//modifies the selected playlist or song
		if (evt.getSource () == saveChangesButton)
		{
			dummyProofing();
			//renames playlist
			if (printPlaylists==true)
			{
				/**If there aren't any errors, renames playlist using information entered into the Text Fields 
				 * Does not check for duplicates
				 */
				if (emptyTextFields==false)
				{
					Database.renamePlaylist(selectedPlaylist, nameField.getText());
					successful=true;
					//remove the components related to modification
					remove(saveChangesButton);
					remove(nameField);
				}
			}
			//changes songs' information
			else if (printSongs==true)
			{
				/**If there aren't any errors, changes the song's information using information entered into the Text Fields
				 * If song is unchanged, treat it as though the change was successful
				 * Displays error message if new information matches another song in the system
				 */
				if (emptyTextFields==false && invalidYear==false)
				{
					if (selectedSong.equals(new Song (nameField.getText(), artistField.getText(),albumNameField.getText(), 
							Integer.parseInt(yearField.getText()), genreField.getText())))
					{
						successful=true;
						//remove the components related to modification
						remove(saveChangesButton);
						remove(nameField);
						remove(artistField);
						remove(albumNameField);
						remove(yearField);
						remove(genreField);
					}
					else if (Database.isDuplicateSong(nameField.getText(), artistField.getText(),albumNameField.getText(), 
							Integer.parseInt(yearField.getText()), genreField.getText()))
						duplicate = true;
					else
					{
						Database.modifySong(selectedSong, nameField.getText(), artistField.getText(), albumNameField.getText(), 
								Integer.parseInt(yearField.getText()), genreField.getText());
						successful=true;
						//remove the components related to modification
						remove(saveChangesButton);
						remove(nameField);
						remove(artistField);
						remove(albumNameField);
						remove(yearField);
						remove(genreField);
					}
				}
			}
			repaint();
		}

		//adds a song to the selected playlist
		if (evt.getSource () == addButton)
		{
			dummyProofing();
			invalidPosition=false;
			songNotInPlaylist = false;

			//error checking for position field
			if (byPosition.getState()==true)
			{
				if (positionField.getText().isEmpty())				//display error message if textfield is empty
					emptyTextFields=true;
				try
				{
					Integer.parseInt(positionField.getText());
				}
				catch (NumberFormatException e)					//display error message if textfield is not an int
				{
					stringToNumErrorPos=true;
					positionField.setText("");
				}
			}

			/**If there aren't any errors, add song to playlist using information entered into the Text Fields
			 * Creates song and adds it to the system if it's not already in the system
			 * Displays error message if position entered was outside of the playlist
			 */
			if (emptyTextFields==false && stringToNumErrorPos == false && invalidYear==false)
			{
				try
				{
					if (toEnd.getState()==true)										//add song to end
						Database.addSong(selectedPlaylist, nameField.getText(), artistField.getText(), albumNameField.getText(), 
								Integer.parseInt(yearField.getText()), genreField.getText());
					else if (byPosition.getState()==true)						//add song to specified position
						Database.addSong(selectedPlaylist, Integer.parseInt(positionField.getText()) - 1, nameField.getText(), artistField.getText(),
								albumNameField.getText(), Integer.parseInt(yearField.getText()), genreField.getText());

					successful = true;
					//clear TextFields
					nameField.setText("");
					artistField.setText("");
					albumNameField.setText("");
					yearField.setText("");
					genreField.setText("");
					positionField.setText("");
					//update list
					playlistListOfSongs = new ArrayList <Song> (Arrays.asList(selectedPlaylist.getListOfSongs()));
					currentListOfSongs = playlistListOfSongs;
					addListButtons();
				}
				catch (IndexOutOfBoundsException e)		//display error message
				{
					invalidPosition = true;
				}

			}
			repaint();
		}

		if (evt.getSource () == removeButton)
		{
			successful = false;
			emptyTextFields = false;
			stringToNumErrorPos = false;
			invalidPosition = false;
			songNotInPlaylist = false;

			//error checking for name Field
			if (byName.getState()==true)						
			{
				if (nameField.getText().isEmpty())				//display error message if textfield is empty
					emptyTextFields = true;
			}
			//error checking for positionField
			if (byPosition.getState()==true)
			{
				if (positionField.getText().isEmpty())			//display error message if textfield is empty
					emptyTextFields = true;
				try
				{
					Integer.parseInt(positionField.getText());
				}
				catch (NumberFormatException e)				//display error message if textfield is not an int
				{
					stringToNumErrorPos=true;
					positionField.setText("");
				}
			}

			/**If there aren't any errors, removes song from playlist using information entered into the Text Fields
			 * Displays error message if position entered was outside of the playlist or song entered was not found in the playlist
			 */
			if (emptyTextFields==false && stringToNumErrorPos == false)
			{
				try
				{
					if (byName.getState()==true)					//remove first song with matching name
						Database.removeSong(selectedPlaylist, nameField.getText());
					else if (byPosition.getState()==true)		//remove song at specified position
						Database.removeSong(selectedPlaylist, Integer.parseInt(positionField.getText()) - 1);

					//TODO
					if (selectedPlaylist.equals(playedPlaylist))
					{
						if (playedSong != selectedPlaylist.getCurrentSong().getSong())
							playedSong = selectedPlaylist.getCurrentSong().getSong();
					}
					successful = true;
					//clear TextFields
					nameField.setText("");
					positionField.setText("");
					//update list
					playlistListOfSongs = new ArrayList <Song> (Arrays.asList(selectedPlaylist.getListOfSongs()));
					currentListOfSongs = playlistListOfSongs;
					addListButtons();
				}
				catch (IndexOutOfBoundsException e)		//display error message
				{
					invalidPosition = true;
					positionField.setText("");
				}
				catch (SongNotFoundException e)				//display error message
				{
					songNotInPlaylist = true;
					nameField.setText("");
				}

			}
			repaint();
		}




		////////////////////////////////////
		/////////BUTTON ARRAYS//////////
		///////////////////////////////////




		int index;		//the index  of the selected playlist/song in the current list of playlists/songs

		//checks if a button from the various arrays of buttons is pressed
		//break out of the loop when a button is pressed
		for (int n=0;n<MAX_OBJECTS_ONSCREEN;n++)
		{
			//determine the index of the playlist/song corresponding to the button that was pressed
			index = n+MAX_OBJECTS_ONSCREEN*(pageNum-1);


			//selects playlist/song to be modified, adds textfields and enters its information into the textfields
			if (evt.getSource() == modifyInfoButton[n])
			{
				successful=false;
				modifyingInfo=true;
				add(saveChangesButton);
				add(nameField);

				if (viewPlaylistsScreen==true)				//select playlist and fill in the information
				{
					selectedPlaylist=currentListOfPlaylists.get(index);
					nameField.setText(selectedPlaylist.getName());
				}
				else if (viewSongsScreen==true)			//select song and fill in the information
				{
					add(artistField);
					add(albumNameField);
					add(yearField);
					add(genreField);

					selectedSong=currentListOfSongs.get(index);
					nameField.setText(selectedSong.getName());
					artistField.setText(selectedSong.getArtist());
					albumNameField.setText(selectedSong.getAlbumName());
					yearField.setText(Integer.toString(selectedSong.getYear()));
					genreField.setText(selectedSong.getGenre());
				}
				repaint();
				break;
			}


			//TODO
			//displays list of songs in the selected playlist
			if (evt.getSource() == viewPlaylistSongsButton[n])
			{
				//set booleans to display appropriate screen
				viewPlaylistsScreen=false;
				viewPlaylistSongsScreen=true;
				printPlaylists = false;
				printSongs = true;
				//store the selected playlist
				selectedPlaylist=currentListOfPlaylists.get(index);
				//make current list the list of songs in the selected playlist
				playlistListOfSongs = new ArrayList <Song> (Arrays.asList(selectedPlaylist.getListOfSongs()));
				currentListOfSongs = playlistListOfSongs;

				removeAll();

				//change bounds and labels of buttons to the appropriate values for the screen
				returnToMainButton.setBounds (17,20,90,50);

				//add components to the screen
				add(returnToMainButton);
				add(returnToPreviousButton);

				addListButtons();

				repaint();
				break;
			}


			//selects the playlist to be altered/edited
			if (evt.getSource() == editPlaylistButton[n])
			{
				//set booleans to display appropriate screen
				alterPlaylistScreen=false;
				alterSelectedPlaylistScreen=true;
				printPlaylists = false;
				printSongs = true;
				//store the selected playlist
				selectedPlaylist=currentListOfPlaylists.get(index);
				//make current list the list of songs in the selected playlist
				playlistListOfSongs = new ArrayList <Song> (Arrays.asList(selectedPlaylist.getListOfSongs()));
				currentListOfSongs = playlistListOfSongs;

				removeAll();

				//reset error checkers to false so error messages aren't displayed until an error comes up
				emptyTextFields = false;
				successful = false;
				stringToNumErrorYear = false;	
				stringToNumErrorPos = false;	
				invalidYear = false;	
				invalidPosition = false;
				songNotInPlaylist = false;

				//clear TextFields
				nameField.setText("");
				artistField.setText("");
				albumNameField.setText("");
				yearField.setText("");
				genreField.setText("");
				positionField.setText("");

				//change bounds and labels of buttons to the appropriate values for the screen
				returnToMainButton.setBounds (17,20,90,50);

				//add components to the screen
				add(returnToMainButton);
				add(returnToPreviousButton);
				add(seePlaylistSongsButton);
				add(seeAllSongsButton);
				add(adding);
				add(removing);
				add(nameField);
				add(positionField);
				if (adding.getState() == true)					//add components related to adding songs to the playlist
				{
					add(artistField);
					add(albumNameField);
					add(yearField);
					add(genreField);
					add(addButton);
					add(toEnd);
					add(byPosition);
				}
				else if (removing.getState() == true)		//add components related to removing songs from the playlist
				{
					add(removeButton);
					add(byPosition);
					add(byName);
				}

				addListButtons();

				repaint();
				break;
			}


			//add the selected song's information into the textfields so song will be added when user presses addButton
			if (evt.getSource() == addToPlaylistButton[n])
			{
				selectedSong = currentListOfSongs.get(index);
				nameField.setText(selectedSong.getName());
				artistField.setText(selectedSong.getArtist());
				albumNameField.setText(selectedSong.getAlbumName());
				yearField.setText(Integer.toString(selectedSong.getYear()));
				genreField.setText(selectedSong.getGenre());

				repaint();
				break;
			}

			//add the selected song's position into the textfield so song will be added when user presses rermoveButton
			if (evt.getSource() == removeFromPlaylistButton[n])
			{
				selectedSong = currentListOfSongs.get(index);
				positionField.setText(Integer.toString(index +1));

				repaint();
				break;
			}


			//delete the selected playlist/song
			if (evt.getSource() == deleteButton[n])
			{
				//reset error checkers to false so error messages aren't displayed until an error comes up
				successful=false;
				cannotDelete=false;
				deleting=true;

				if (crtDelPlaylistScreen==true)					//delete playlist
				{
					//if the playlist being deleted is the playlist being played, set current song and playlist to none
					if (currentListOfPlaylists.get(index).equals(playedPlaylist))
					{
						playedPlaylist = null;
						playedSong = null;
					}

					Database.deletePlaylist(currentListOfPlaylists.get(index));

					successful=true;
					//update list
					currentListOfPlaylists=Database.getPlaylistList();
					addListButtons();
				}
				else if (crtDelSongScreen==true)				//delete song
				{
					//delete song if it can be deleted (i.e. it isn't in any playlist)
					if (Database.canDeleteSong(currentListOfSongs.get(index)) == true)
					{
						//if the song being deleted is the song being played, set current song to none
						if (currentListOfSongs.get(index) == playedSong)
							playedSong=null;

						Database.deleteSong(currentListOfSongs.get(index));

						successful = true;
						//update list
						currentListOfSongs=Database.getSongList();
						addListButtons();
					}
					else 
						cannotDelete=true;
				}
				repaint();
				break;
			}
		}

		if (evt.getSource() == returnToMainButton)
		{
			//set booleans to display appropriate screen
			mainScreen = true;
			viewPlaylistsScreen = false;
			viewSongsScreen = false;
			viewPlaylistSongsScreen = false;
			alterPlaylistScreen = false;
			alterSelectedPlaylistScreen = false;
			crtDelPlaylistScreen = false;
			crtDelSongScreen = false;
			modifyingInfo = false;
			deleting = false;

			printPlaylists=false;
			printSongs=false;

			//reset error checkers to false so error messages aren't displayed until an error comes up
			emptyTextFields = false;
			duplicate = false;		
			successful = false;
			cannotDelete = false;
			stringToNumErrorYear = false;	
			stringToNumErrorPos = false;	
			invalidYear = false;	
			invalidPosition = false;
			songNotInPlaylist = false;

			//clear TextFields
			nameField.setText("");
			artistField.setText("");
			albumNameField.setText("");
			yearField.setText("");
			genreField.setText("");
			positionField.setText("");

			removeAll();

			//change bounds and labels of buttons to the appropriate values for the screen
			returnToMainButton.setBounds (17,20,200,50);

			//add components to screen
			add(toCrtDelPlaylistButton);
			add(toCrtDelSongButton);
			add(toAlterPlaylistButton);
			add(toViewPlaylistsButton);
			add(toViewSongsButton);

			repaint();
		}
	}
}
