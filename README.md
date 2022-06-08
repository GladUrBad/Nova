# Nova AntiCheat
This anti-cheat is the partial culmination of my knowledge about Minecraft anti-cheating.
Partial, because well, this is open sourced and as a result did not provide 100% accurate methods or all of the useful checks.
You'll probably notice that there are very few checks. I did not intend on making Nova a fully-fledged production anti-cheat with all
the bells and whistles. 

Nova should be used as a resource to the community. Nova introduces checks unheard of in a public anti-cheat system, such as:
- Aim checks which simulate kill-aura rotations serverside and use statistical analysis to identity patterns which represent such.
- Aim checks which utilize an accurate sensitivity tracker to flag rotations which bypass GCD checks.
- Reach and hit-box check which utilizes transaction-confirmed tracking and interpolation.

Nova also introduces packet listening on the main thread, through a modified PlayerConnection class. The packet wrapper system itself
uses reflection. 

Nova also gives a clean base with utilities to add your own checks. While this base is certainly not performant, your formatting 
will be kept clean. I encourage thoroughly optimizing the base before using this on a production server.
