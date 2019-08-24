# butterfly
An e-AMUSEMENT server emulator, targeting Dance Dance Revolution A.

## What is this?

This is **butterfly**, an e-AMUSEMENT server targeting Dance Dance Revolution A. This is a mostly-fully-featured server, intended for local usage.

### Features:
* Full support for profile creation, score saving, options saving, rivals, etc.
  * Carding in works as expected, and any number of profiles is supported
* Forced full unlock (currently event progress is not tracked / everything is fully unlocked already)
* Can run on Windows/Mac/Linux

### How do I use it?

Requirements:
* Java 8 or above needs to be installed. Most users should already have this, but if not, download the latest JRE for your platform

#### Usage:

Put the following in a file called `run_server.bat` if on Windows, or `run_server.sh` if on Linux/Mac. The path, including `YOUR_USERNAME`, can be anything, as this is where the database file will be saved. Save and run the file:

`java -Ddb_path="C:\Users\YOUR_USERNAME\Desktop\db.sqlite" -jar butterfly-1.1.0.jar`

At this point, the server should be running. Connect your game and play! Set the `services` URL to `http://localhost` and turn off `url_slash`.

### How do I change webUI-only options (dancer, rivals, fast/slow judgement, etc.)?

Unfortunately, I did not get around to making a web UI for this server. I might in the future. In the meantime, you'll need to manually edit the database to change these options... any SQLite database browser will work.

To change your options, find your user under the `ddr_16_profiles` table and edit whichever columns you'd like. Below are the valid options for each column you probably care about:

**dancer_character**:
```
    RANDOM,
    RANDOM_MALE,
    RANDOM_FEMALE,
    YUNI,
    RAGE,
    AFRO,
    JENNY,
    EMI,
    BABYLON,
    GUS,
    RUBY,
    ALICE,
    JULIO,
    BONNIE,
    ZERO,
    RINON,
    RYUSEI_EMI,
    RYUSEI_ALICE,
    RYUSEI_RINON
```

**option_arrow_skin**:
```
    NORMAL,
    X,
    CLASSIC,
    CYBER,
    MEDIUM,
    SMALL,
    DOT,
    BUTTERFLY
```

**option_screen_filter**:
```
    OFF,
    LIGHT,
    MEDIUM,
    DARK
```

**option_guidelines**:
```    OFF,
    ARROW_TOP,
    ARROW_CENTER
```

**option_judgement_layer**:
```
    FOREGROUND,
    BACKGROUND
```

**show_fast_slow_results**:
```
    0,
    1
```

If you'd like to set your weight, enter your weight in **kilograms** in the weight column.

If you'd like to set your **rivals**, you can edit the `rival_1_id`, `rival_2_id`, or `rival_3_id` columns to contain the **ID** of the rival user, meaning the value from the `id` column of the user.

### What's next?

Probably nothing. I'm mainly releasing this because I don't see myself working more on it in the future, but I thought it'd be helpful for others. I mainly coded it as a learning exercise, and I suspect that the "juicy" bits of this code won't be relevant for much longer anyway. Maybe I'll make a web UI in the future but don't hold me to that.

### Credits
* **skogaby**: main author
* **dogelition_man** (https://github.com/ledoge): author of Kotlin kbinxml and card number conversion code
* Various other devs for tips/pointers
* Various other projects that I reversed for figuring out packet encryption/compression