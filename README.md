# Archivist
This is the core plugin used for the anarchived minecraft server, for generating several worlds, containing sorted region files of bases, automatically, with no collisions.
The purpose of this is pretty clear; reducing maintenance with managing region files.

# Usage
## Browser compass
The browser compass is used to list all servers of the archive in a GUI ingame. Once logged in, you will receive the compass item. 
To open the GUI, right or left click the air with the compass in your hand.

### Preview
<img src="https://github.com/anarchived/archivist/assets/78901876/6a7c8317-e42c-423c-8e02-9809a3bd6432" width="400">

### Usage
You can:
- Browse, by left clicking items in the GUI. This will open the content, or if you clicked on a specific save, it will teleport you there.
- Read, by right clicking items in the GUI. If they have an index.md file associated, you will be able to read all of the provided information easily.
- Change page, by clicking the page changer arrow in the bottom right. To see the next page, left click it. To see the previous, right click.
- Go back, by clicking the back-arrow in the bottom left. This will open the previous window in the GUI.
- Exit, with the escape key.

## Find command
To list all bases on the entire archive, this command can be used. Usage:
```sh
/find [search]
# Example:
# /find waterloo # opens the browser and searches across the entire archive related to "waterloo"
```

## Go command
To list all bases of a server, this command can be used. Usage:
```sh
/go <server-name> [search]
# Example:
# /go 0b0t          # opens the browser and shows all bases of 0b0t
# /go 0b0t waterloo # opens the browser and searches for bases of 0b0t related to "waterloo"
```

## Goto command
To explicitly teleport to a specific base found in the Server Index, use the /goto command:
```sh
/goto <server-name> <save-name> <variant>
# Example:
# /goto 0b0t Waterloo_2 04-04-2024
# Note: To type spaces in the server, save or variant name, use underscores as a replacement.
# Note: The variant is usually a date, formatted in MM-DD-YYYY.
```

# Usage for archive owners
This is an example archive, a directory located in the minecraft server directory root, and it has the following structure:
```
archives
└─ base-archive
   └─ 0b0t.org
      └─ index.md
      └─ Waterloo 2
         └─ index.md
         └─ 04-04-2024
            └─ r.X.Z.mca
            └─ ...
         └─ 04-05-2024
            └─ ...
         └─ ...
      └─ Exodus
         └─ ...
   └─ 2b2t.org
      └─ ... same structure as the other servers
   └─ Constantiam.net
      └─ ...
   └─ Oldfrog.org
      └─ ...
   └─ ...
└─ map-archive
   └─ 0b0t.org
      └─ map_10.dat
      └─ map_2641.dat
      └─ ...
   └─ ...
```
#### See the [public anarchived archive](https://github.com/anarchived/public-archive) for a more in-depth example.
When the server starts, The Archivist plugin will use the given archive, for 
associating servers with saves and variants with specific locations and world files on the server.
During this stage, the browser GUI will also be automatically generated.

## Extending the archive / deleting saves
To modify the server archive in any way, simply edit your archive folder with the given files.
When you're finished, run the `reindex` command on your minecraft server. This will delete the cached worlds and restart.
On the next restart, the fresh archive will be reindexed.

## Do NOT use the "/reload" command
Since archives directly modify region files on the server, make sure to NEVER reload your server, as it can cause corruptions or other
errors unexpectedly. Always fully restart your server, or be prepared for failures. If your server archive does become 
corrupted, stop your server as soon as possible, then re-index the archive using the steps mentioned above.

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
Of course, the server index is also used to automatically generate the ingame browser GUI.
