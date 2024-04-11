# Archivist
This is the core plugin used for the anarchived minecraft server, for generating several worlds, containing sorted region files of bases, automatically, with no collisions.
The purpose of this is pretty clear; reducing maintenance with managing region files.

# Usage
## Goto command
To explicitly teleport to a specific base found in the Server Index, use the /goto command:
```sh
/goto <server-name> <save-name> <variant>
# Example:
# /goto 0b0t Waterloo_2 04-04-2024
# Note: To type spaces in the server, save or variant name, use underscores as a replacement.
# Note: The variant is usually a date, formatted in MM-DD-YYYY.
```
## GUI
The GUI is currently not implemented yet.

# Usage for archive owners
This is an example archive, a directory located in the minecraft server directory root, and it has the following structure:
```
archive
└─ index.md
└─ servers
   └─ index.md
   └─ 0b0t
      └─ index.md
      └─ saves
         └─ Waterloo 2
            └─ index.md
            └─ 04-04-2024
               └─ region
                  └─ r.X.Z.mca
                  └─ ...
            └─ 04-05-2024
               └─ ...
            └─ ...
         └─ Exodus
         └─ ...
   └─ 2b2t
      └─ ... same structure as the other servers
   └─ Constantiam
      └─ ...
   └─ Oldfrog
      └─ ...
   └─ ...
```
#### See the public anarchived archive for a more in-depth example (coming soon).


# How it works internally
## Blobs and Blob Fields
Using the archive folder, the Archivist plugin will automatically generate multiple world files, housing every save from every server, socalled blobs.
Every server will be firstly assigned it's own blob field, containing stacks of blobs for all three dimensions.
For context, a blob field may look something like this (note that this is only a representation, blobs are never actually stored in json):
```json
"0b0t": {
    "overworld": {
        "blobs": [
            [
                "r.1.4.mca",
                "r.1.3.mca",
                "r.0.3.mca"
            ],
            [
                "r.1.3.mca",
                "r.1.2.mca"
            ]
        ]
    }
    "nether": {
        "blobs": [
            [
                "r.0.1.mca",
                "r.0.0.mca"
            ],
            [
                "r.-1.0.mca",
                "r.-1.-1.mca",
                "r.0.1.mca"
            ]
        ]
    }
}
```
Blob fields are temporarily created while indexing the archive, to copy region files into their own worlds, ensuring there are no collisions.
Collision checking is important due to there potentially being multiple variant of the same save, at the same location, differing only in date of archiving.
If any collision occurs while indexing region files, the current variant of the base to add will be moved to a new blob with a higher index. 
For efficiency and less clutter, the plugin will try to store as many region files in a single world file as possible.

## Server Index
In the end, every save and it's variant will be mapped to it's corresponding blob and location, then added to the server index. 
The user of the archive can then find any save and variant using the /goto command or the GUI, where the Archivist plugin internally uses it's server index to find it.
Of course, the server index is also used to automatically generate the ingame save finder GUI.
