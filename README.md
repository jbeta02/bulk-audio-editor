# Bulk Audio Editor

## Summary

Bulk Audio Editor or BAE is a command line tool written in Java for editing and managing audio files in bulk. 
Users can edit names of files, edit metadata, organize the files in folders, display file data, and normalize the loudness of files.

Currently supports with mp3 files. 

## Usage

To use BAE, users can download the "bulk.audio.editor.zip" Zip file from the release page 
[Bulk Audio Editor - release](https://github.com/jbeta02/bulk-audio-editor/releases). After unzipping, 
run the "Bulk Audio Editor.exe" to launch the program.

<br />
The EXE file needs to be alongside the temp, ffmpeg, and jar folders so
if the tool needs to be moved it is recommended to move the tool from the top folder "Bulk Audio Editor".
<br />

#### Provide Initial Path

When the application is ran it will ask for the path to the target files. This path can be a file or a folder. 
BAE will only see supported files. 

#### General Command Syntax

```
command [command input] -o [output path]
```

#### Output

- The output modifier can be used on all commands except display commands, the new command, the help command, and the quit command. 
- Use the following syntax to add an output path: `-o [output path]`
- Note: When an output path is specified the target file/folder will change to the given output after the command is ran. 

example of command with output modifier: `ab new- -o C:\a-folder\folder2`

## Command List

Commands can be organized into **7 categories**: **Edit Name**, **Edit Metadata**, **Edit Loudness**, **Convert Files**, **Create Folders**, **Display Data**, and **Program Utility**

#### Edit Name
`ab [text]` Add to beginning command will add [text] to beginning of file name

`ae [text]` Add to end command will add [text] to end of file name

`r [text]` Remove command will remove [text] from file name

#### Edit Metadata
`T [title text]` Title command will add [title text] to title metadata of files

`Ar [artist text]` Artist command will add [artist text] to artist metadata of files

`A [album text]` Album command will add [album text] to album metadata of files

`G [genre text]` Genre command will add [genre text] to genre metadata of files

`Art [path to art]` Art command will add art in [path to art] to art metadata of files

#### Edit Loudness
`LN` Loudness Normalize command will make loudness of files similar.
This will allow user to listen to music without needing to change the volume. Will set loudness to -16 LU, 
true peak set to -2 dBFS and loudness range set to match file's current range. 

`LNN [LUFS value]` Loudness Normalize command (same as -LN but with custom loudness) 
will take a value in LU and bring the loudness of files to that target. This will allow user to listen to music without needing to change the volume. 
Recommended LU values are -24 to -14 (numbers closer to 0 are louder). True peak set to -2 dBFS and loudness range set to match file's current range.

#### Conversion
`ToMP3` To MP3 command will convert supported files to mp3

#### Create Folders
`ffA` Folders for album command will create folder for files based on their album then put the files in those folders

`ffAr` Folders for artist command will create folder for files based on their artist then put the files in those folders

#### Display Data
`DN` Display by name command will display the metadata of the files organized by name

`DAr` Display by artist command will display the metadata of the files organized by artist name

`DA` Display by album command will display the metadata of the files organized by album

`DG` Display by genre command will display the metadata of the files organized by genre

`DL` Display by loudness command will display the metadata and loudness data of the files organized by loudest to quietest in LU

#### Program Utility
`n [path to files]` New command will select new set of files or file to target

`h` Help command will display all command options and give their descriptions

`q` Quit command will terminate program


## Examples

#### Edit Name

![add-to-name](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/0be2d3df-4cc8-49d0-9fb6-be6e7d1d7432)

![Screenshot 2023-05-22 212959](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/d951d0a2-a456-4d47-aabd-2009500824f3)

![Screenshot 2023-05-22 214235](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/0df5f3ab-4e2f-48a3-866e-95e0ac1a4fed)

The first image shows the commands that were on BAE. After the target path was given, the files were found (see second image). 
The user wants to add "new_" to the beginning of the target files. To do this they use the command `ab` with the `-o` modifier to 
put the modifiers in a different folder and leave the original files alone. Notice that the fourth arrow shows that the target path
changed after the command was ran, this due to the behavior of BAE after the `-o` modifier is used. The second and third image shows the 
results after the command is ran. 

#### Edit Metadata

![edit-metadata](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/019a2817-0595-40aa-beeb-8c17a8ea0ce7)

![Screenshot 2023-05-22 212959](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/dbf04bba-d8f5-486c-b07b-fc2d01878077)

![Screenshot 2023-05-22 224855](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/ea31a18d-e5bf-4c25-849a-dfa250a89a54)

The user wants to change to album name of the files in a given folder. After giving BAE the target folder that use the `DA` command to 
display the current data of the files organized by album name (ascending order) (see first for command and second arrow for results). 
Next the user runs the `A` command to change the album text to "new-alb" (see third arrow). Finally the user uses the display by album 
command again to see the result of the previous command. The results can also be verified by using the file explorers (see second and third picture).

#### Edit Loudness

![edit-loudness](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/6b6609a5-f749-4488-b2f1-e19e74182fe6)

The user wants all their music to have a similar loudness so they can listen without changing the volume between songs. 
They use the `DN` command to see the files organised by name that will be changed (see first and second arrow). The user then uses the `DL`
command to display the loudness data in addition to the metadata, notice that the integrated loudness of all the files is different
(see fourth arrow). The `LN` command is used to perform the loudness normalization process. The `LN` command sets the integrated loudness to -16 LU, 
true peak to -2 dBFS, and matches the loudness range that already exists within the file. The user in this case does not use the output modifier, however,
it is recommended to use the output modifier to keep the original files safe in case you are not satisfied with the loudness change. Finally, the user uses the 
`DL` to see the results of the operation, notice that the integrated loudness of the files are -16 (see sixth arrow).

#### Create Folders

![folder-creation](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/81b9c526-d1ef-432e-aaea-43600e5bb652)

![Screenshot 2023-05-23 152349](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/bfb6c4f0-50af-4d22-838b-35425f8c74d3)

![Screenshot 2023-05-23 151806](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/2b8356fd-2f4e-4439-8ff3-3363e244d2cc)


The user wants to create folders for their audio files based on the album of the files. They first check the albums of the files using display name command 
(`DN`) a better option would be to use the `DA` command which displays the files based on the album name. Look at second arrow to see the albums of the files. 
The user uses the `ffA` (folders for album) command to create a folder for each unique album found among the target files. Notice that the third arrow shows that the after the
folders were created there were no more target files in the input path specified. To continue working on the files use the new command (`n`) and specify a new path to keep 
wokring on the files. The third image shows the folders created, notice that the files were placed in the folders since the output modifier was not used. If the output modifier was used
then copies of the files would be placed in folders created in the output path specified. 

## Notes

#### Loudness Normalization Usage

It is recommended to run the `DL` command before running `LN` or `LNN` so you can see the current loudness
of the files. Unless you know the loudness you would like to target it is recommended to just use `LN` command
which will set the integrated loudness to -16 LU, true peak to -2, and match the loudness range to the current target audio file.
For reference, platforms such as Spotify use an integrated loudness value of -14 LU which is also common on other platforms. 
If you are unsure, create a test folder with a few files then run the command and examine the results to see
if they meet your needs. Short explanation of loudness normalization: https://en.wikipedia.org/wiki/Audio_normalization

#### Reversing actions

Actions can NOT be undone using ctrl z in the target folder. To reverse an action by BAE use the same command or the command inverse. 
For example, to undo an add name command use the remove command. To undo a modify album command use the modify album command
again but specify the correct album name. 

Revering or changing the affects of the loudness normalization command should be used with caution since constant
changing of the file's loudness could result in generation loss after numerous loudness changes. 

#### Override Protections

![overrride-protection](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/2e8e4430-4f42-42a1-a2a4-91d007eb4eea)

![Screenshot 2023-05-22 212959](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/86a20a7d-3325-4a4e-8ca2-9bcb1db78f11)

![Screenshot 2023-05-23 151206](https://github.com/jbeta02/bulk-audio-editor/assets/55860847/de518abb-1dbf-46ff-baaf-a706e6bc4aa0)


When the output modifier is used an there is a potential conflict when one or more files having being the same, BAE will ask the user 
if overriding is acceptable. For example, If two folders hold the same three files and the user wants to change the album name of their
files they can do so without worrying about conflicts. Looking at the above example, the user displays the files in the input folder then attempts to 
change the album name to "z" using the output modifier to direct the output. They find  the output folder already contains the files. The user decides
that they only want to modify "Song A.mp3" and leave the others untouched. 

#### Adapt To User Changes

BAE will update its targets if the user changes any of the files or its contents between command runs. BAE will check if 
there were any changes before every command is ran, so it is always up-to-date. 

#### Target Changes To Output

When the output modifier is used on a command, the target will change from the input path initially set to the output path set through the output modifer. 
This should make it easier to follow the flow of the work being done on the files. 


## How It Works

- This project uses jaudiotagger for interacting with mp3 and flac files and editing metadata
- FFmpeg is used for normalizing loudness and displaying loudness data
- Launch4j is used to create the executable

For those who wish to clone the host machine will need to have Java 1.8 to run jaudiotagger
