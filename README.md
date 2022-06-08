# Nova AntiCheat
This anti-cheat is the partial culmination of my knowledge about Minecraft anti-cheating.
Partial, because well, this is open sourced and as a result did not provide 100% accurate methods or all of the useful checks.
You'll probably notice that there are very few checks. I did not intend on making Nova a fully-fledged production anti-cheat with all
the bells and whistles. 

Nova should be used as a resource to the community. Nova introduces checks unheard of in an open sourced, public anti-cheat system, such as:
- Aim checks which simulate kill-aura rotations serverside and use statistical analysis to identity patterns which represent such.
- Aim checks which utilize an accurate sensitivity tracker to flag rotations which bypass GCD checks.
- Reach and hit-box check which utilizes transaction-confirmed tracking and interpolation.

Nova also introduces packet listening on the main thread, through a modified PlayerConnection class. The packet wrapper system itself
uses reflection. Why would you want to be on the main thread?

![image](https://user-images.githubusercontent.com/62041141/172733095-ffc9eee0-eded-4a11-b621-62f7d7c84686.png)

Nova also gives a clean base with utilities to add your own checks. While this base is certainly not performant, your formatting 
will be kept clean. I encourage thoroughly optimizing the base before using this on a production server.

### Base overview
Nova - Helper class used to cleanly access fields in the NovaPlugin class.__
NovaPlugin - Main class of Nova.__
PacketManager - Packet listening for players. This uses a modified player connection class to feed the packets to the wrapper system
off of the main thread.__
PlayerListener - Listens for players joining or leaving. We inject the packet manager on this events. This means some of the packets 
will be lost in the beginning, so be careful with this in your checks.__
PlayerData - Stores only a few fields, such as the managers which are initialized per player.__
TrackerManager - Stores each tracker initialized per player. Register new trackers here.__
CheckManager - Stores each check initialized per player. Register new checks here.__

### Check handlers
Most anti-cheats use a single method or multiple abstract class types for check handling. Nova uses an interface handler system, so you
do not have to be limited to just handling one type of check. You can combine these handlers to make your life easier.

### Tracker handlers
Nova gives handlers for the trackers which can allow you to listen to packets before and after checks are executed, as well as Bukkit events.

### Check buffers
Many checks in Nova are not accurate, as I already stated above. Buffers are commonly used throughout to reduce false positive rate.
Be careful using buffers, use them appropriately, and sparingly. Ensure you increase and reduce the buffer in an appropriate way which leads
to the least bypasses and false positives.

### Changing alert format
View the check class and change the format field. The immutable format placeholders are cached during check initialization to reduce performance
impact.

### Conventions
When working on Nova, please stick to the project conventions. For example, please do not spam final modifiers because I do not use them here.
When submitting a feature, please make your own branch, do not work on master.
