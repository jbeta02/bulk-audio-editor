# Bulk Audio Editor

## Summary

Bulk Audio Editor or BAE is a command line tool written in Java for editing audio files such as mp3 in bulk. 
Users can edit metadata, organize the files in folders, display file data, and normalize the loudness of files.

Currently only works with mp3 files. 

## Usage

To use BAE, users can download the "bulk-audio-editor-FND" repository then run the "Bulk Audio Editor.exe"
inside the downloaded repository. see [Bulk Audio Editor-FND](https://github.com/jbeta02/bulk-audio-editor-FND) for details.

<br />
The EXE file needs to be alongside the temp, ffmpeg, and jar folders so
if the tool needs to be moved it is recommended to move the tool from the top folder "Bulk Audio Editor".
<br />


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
