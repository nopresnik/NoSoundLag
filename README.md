When playing multiplayer Minecraft, are you annoyed that when you place a block,
it takes a second for the sound to happen? Install this mod and the sound lag
will disappear.

## Download

Grab the latest binary linked from [the official thread on minecraftforum.net](http://www.minecraftforum.net/topic/1414774-nosoundlag/).
Alternately, you can browse binaries for [previous versions](http://download.bencvt.com/minecraft/nosoundlag/)
or refer to `README-dev.md` to build from source.

## Installation

Works exactly like any other Minecraft client mod.

Patch the contents of the zip file into your minecraft.jar, being sure to remove
remove the META-INF folder. I recommend a utility like
[Magic Launcher](http://www.minecraftforum.net/topic/939149-magiclauncher/);
manually copying .class files is for the birds.

This mod does *not* require ModLoader or any other mod management system.

## Compatibility

Should be compatible with every mod that does not overwrite:

- `fk.class` (Packet15Place)
- `dv.class` (Packet62LevelSound)

## More info

Minecraft 1.3 involved a lot of internal changes to reduce the amount of server
code duplicated by the client. One unfortunate side effect is that if you're on
a noticeably laggy connection, your block placement sounds will lag as well. The
server controls those sounds now, not the client.

A proper fix will involve changing both the server and the client. Mojang
partially fixed this issue in 1.4.3, but we're still wait on a fix for block
placement sounds. The issue is in the Mojira bug tracker:
**[MC-55](https://mojang.atlassian.net/browse/MC-55)**

Once Minecraft's sound lag issues are completely fixed, NoSoundLag will die a
happy death! Until then, it's a useful stop-gap.

NoSoundLag is open source! Check the official GitHub project for the changelog,
build instructions, and full source code:
[github.com/bencvt/NoSoundLag](https://github.com/bencvt/NoSoundLag)
