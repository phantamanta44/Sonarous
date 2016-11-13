Sonarous
=====
Sonarous is a [Discord](https://discordapp.com) music bot written in Java. It uses the [Discord4J](https://github.com/austinv11/Discord4J) library to communicate with Discord, as well as a number of [other libraries](https://github.com/phantamanta44/Sonarous/blob/master/pom.xml#L58) to decode and manipulate audio.

#### [Want this bot in your server?](https://discordapp.com/oauth2/authorize?client_id=176842635346968576&scope=bot&permissions=0) ####

## Command Overview ##
The following is a short overview of the commands offered by Sonarous.

### Basic Bot Commands ###
| Command     | Usage                     | Description
| ----------- | ------------------------- | -----------
| help        | help                      | Provides a link to this document.
| setprefix   | setprefix `prefix`        | Sets the command prefix in a specific server.
| halt        | halt reason               | Kills the bot.
| bind        | bind                      | Binds the bot to a voice channel.
| unbind      | unbind                    | Unbinds the bot from whatever voice channel it's in.

### Music Player Commands ###
| Command     | Usage                     | Description
| ----------- | ------------------------- | -----------
| skip        | skip                      | Votes to skip the current song. Requires a majority to successfully skip.
| forceskip   | forceskip                 | Forcefully skips the current song.
| pause       | pause                     | Temporarily pauses playback.
| unpause     | unpause                   | Resumes paused playback.
| volume      | volume decibels           | Sets the bot's playback volume in decibels. Ranges from -2147483648 to 6 dB.
| playing     | playing                   | Gets the currently playing song.

### Queue Manipulation Commands ###
| Command     | Usage                     | Description
| ----------- | ------------------------- | -----------
| play        | play songUrl              | Attempts to queue a song from the given link.
| search      | search provider query     | Searches for a song provided by a given provider.
| result      | result index              | Adds a search result to the queue.
| lsqueue     | lsqueue                   | Lists currently queued songs.
| unqueue     | unqueue index             | Removes the queued song at the given index.

### Provider Manipulation Commands ###
| Command     | Usage                     | Description
| ----------- | ------------------------- | -----------
| lssop       | lssop                     | Lists available song providers.
| lssep       | lssep                     | Lists available search providers.

## Resource Providers ##
In order to parse songs and searches, Sonarous exposes elements called "song providers" and "search providers". Essentially, they function to parse your input and convert them to songs and searches for a given song service. For example, a SoundCloud song provider parses SoundCloud URLs and tries to grab music tracks for playback. Similarly, a SoundCloud search provider searches SoundCloud for songs using user-provided search queries. In order to use a particular music service with Sonarous, you need a song provider and/or search provider for that service.

## Bot Configuration ##
In order to run the bot, you'll need a file called `sonarous_cfg.json` in the working directory. The file should look something like this:
```json
{
    "token": "abcdefghijklmnopqrstuvwxyz",
    "prefix": "!",
    "admin": "1234567890123456",
    "providers": {
        ...
    },
    "ffmpeg": {
        "ffmpegPath": "path/to/ffmpeg",
        "ffprobePath": "path/to/ffprobe"
    }
}
```
The parameters seem pretty self-explanatory, but here's a table of them anyways:

| Parameter      | Type     | Function
| -------------- | -------- | --------
| token          | String   | The bot's oauth token.
| prefix         | String   | The bot's default command prefix.
| admin          | String   | The bot owner's user ID.
| providers      | Map      | Provider configuration. See below.
| ffmpeg         | Map      | FFmpeg configuration.
| ffmpegPath     | Path     | Path to the ffmpeg executable.
| ffprobePath    | Path     | Path to the ffprobe executable.

## Available Providers ##
### YouTube Provider ###
Requires an FFmpeg installation to be configured, as well as (ytdl)[https://github.com/rylio/ytdl]. The configuration should look like this:
```json
"providers": {
    "youtube": {
        "ytdlPath": "path/to/ytdl/executable"
    }
}
```
### SoundCloud Provider ###
Requires a SoundCloud client ID. The configuration should look like this:
```json
"providers": {
    "soundcloud": {
        "apiKey": "clientId12345678"
    }
}
```