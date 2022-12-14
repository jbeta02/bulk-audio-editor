# Bulk Audio Editor

## Summary

Bulk Audio Editor or BAE is a command line tool written in Java for editing audio files such as mp3 in bulk. 
Users can edit metadata, organize the files in folders, display file data, and normalize the loudness of files.

Currently only works with mp3 files. 

## Usage

To use BAE, users can download the "bulk.audio.editor.zip" Zip file from the release page 
[Bulk Audio Editor - release](https://github.com/jbeta02/bulk-audio-editor/releases). After unzipping, 
run the "Bulk Audio Editor.exe" to launch the program.

<br />
The EXE file needs to be alongside the temp, ffmpeg, and jar folders so
if the tool needs to be moved it is recommended to move the tool from the top folder "Bulk Audio Editor".
<br />

#### Loudness Normalization Usage

It is recommended to run the `DL` command before running `LN` or `LNN` so you can see the current loudness
of the files. Unless you know the loudness you would like to target it is recommended to just use `LN` command
which will set the integrated loudness to -16 LU, true peak to -2, and match the loudness range to the current target audio file.
For reference, platforms such as Spotify use an integrated loudness value of -14 LU which is also common on other platforms. 
If you are unsure, create a test folder with a few files then run the command and examine the results to see
if they meet your needs. 

#### Reversing actions

Actions can NOT be undone using ctrl z in the target folder. To reverse an action by BAE use the same command or the command inverse. 
For example, to under an add name command use the remove command. To undo a modify album command use the modify album command
again but specify the correct album name. 

Revering or changing the affects of the loudness normalization command should be used with caution since constant
changing of the file's loudness could result in generation loss after numerous changes. 


## Command Details

#### General Command Syntax

```
command [command input] -o [output path]
```

#### Initial Run

When the program is ran it will initial ask for a path to the target files. The path can lead to a folder full of mp3 files or 
lead to a single mp3 file.

#### Output

- User can specify an output path for any command. 
- Use the following syntax to add an output path: `-o [output path]`


example: add text to end of files and save result in output folder


command: `ab new- -o C:\a-folder\folder2`


result: 



![result](https://user-images.githubusercontent.com/55860847/208598946-78a4ce65-46fb-4c76-b1c2-7de26bda0a5a.PNG)

<br />

#### Command List

`ab [text]` Add to beginning command will add [text] to beginning of file name

`ae [text]` Add to end command will add [text] to end of file name

`ab [text]` Remove command will remove [text] from file name

`Ar [artist text]` Artist command will add [artist text] to artist metadata of files

`A [album text]` Album command will add [album text] to album metadata of files

`G [genre text]` Genre command will add [genre text] to genre metadata of files

`Art [path to art]` Art command will add art in [path to art] to art metadata of files

`LN` Loudness Normalize command will make loudness of files similar.
This will allow user to listen to music without needing to change the volume. Will set loudness to -16 LU, 
true peak set to -2 dBFS and loudness range set to match file's current range. 

`LNN [LUFS value]` Loudness Normalize command (same as -LN but with custom loudness) 
will take a value in LU and bring the loudness of files to that target. This will allow user to listen to music without needing to change the volume. 
Recommended LU values are -24 to -14 (numbers closer to 0 are louder). True peak set to -2 dBFS and loudness range set to match file's current range.

`ffA` Folders for album command will create folder for files based on their album then put the files in those folders

`ffAr` Folders for artist command will create folder for files based on their artist then put the files in those folders

`DN` Display by name command will display the metadata of the files organized by name

`DAr` Display by artist command will display the metadata of the files organized by artist name

`DA` Display by album command will display the metadata of the files organized by album

`DG` Display by genre command will display the metadata of the files organized by genre

`DL` Display by loudness command will display the metadata and loudness data of the files organized by loudest to quietest in LU

`n [path to files]` New command will select new set of files or file to target

`h` Help command will display all command options and give their descriptions

`q` Quit command will terminate program


## For developers
This project uses jaudiotagger for interacting for mp3 files and editing metadata. FFmpeg is used for
normalizing loudness and displaying loudness data. The project can be ran as a JAR file as long as the temp and 
ffmpeg folder and alongside the JAR file. The host machine will also need to have JRE 1.8.0

#### In the future BAE will also work with FLAC and other audio file types
