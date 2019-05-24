# FadelandTilePadder
This utility was made for Fadeland, but can be used by anyone for whatever they need. It takes in a tile sheet and some settings laid out in JSON, and pads the sheet accordingly. This is commonly used to fix tile bleeding in games.

# Windows Usage
To use this utility right off the bat, download the zip here:

https://github.com/BambooBandit/FadelandTilePadder/releases/tag/0.1.0

Unzip the zip, and create your own input_{tile name} folder with a settings.json. Refer to the input_example folder as an example. Put your own {tile name}.png in the input folder as well. Double click run.bat and enter {tile name}. The resulting padded tilesheet will be in the output folder.

# Non Windows Usage
I have not yet written an out of the box working zip for non-Windows OS, so you will have to resort to using the command line. You can still download the windows zip and take the jar from it here:

https://github.com/BambooBandit/FadelandTilePadder/releases/tag/0.1.0

Instead of using the run.bat, use the commandline with arguments [input path] [output path] [name].
