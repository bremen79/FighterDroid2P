# FighterDroid2P

This Android app allows to use the second joystick (and its buttons) on the Arcade1up Fighter Droid cabinets.
It works by associating the joystick and its buttons to the keys of a virtual keyboard.

It has been tested (but not too much ;) ) on the Yoga Flame and on the Marvel vs Capcom 2 cabinets. Please let me know if it works on the other cabinets too. In theory, the Marvel vs Capcom, the Big Blue, and The X-Men vs Street Fighter, should all work with the Yoga Flame apk: it would be great to have some feedback on this!

This is literally my first Android app, written in my spare time, in just ~1 week. So, it is very possible that something better could be done! For this reason, I release the code so that people more expert than me can improve and/or build on it. In fact, I might have reached the best I can do, at least in my spare time: if you want to propose changes, you will have to suggest the actual changes to the code! 

To report bugs, please be as specific as possible and remember that I only have the Yoga Flame cabinet. So, for other cabinets, you'll have to know how to use adb to gather the logs, otherwise I will not be able to know what is going on.


# Disclaimer

This software is provided without any warranties, express or implied, and their usage carries inherent risks. By using the software, you accept full responsibility for any potential damage to your system or device. The developers, distributors, and contributors of this software explicitly disclaim liability for any data loss, system malfunctions, or adverse effects resulting from its installation or usage.

Additionally, please be aware that this software is intended solely for educational and experimental purposes. It is not intended for commercial use and is not supported by any warranty or technical assistance. Installing the app may void your device's warranty, and you should proceed with caution, ensuring you have backups and are aware of potential security risks. By using the app, you agree to these terms and acknowledge that the developers and contributors are not liable for any consequences arising from its use.


# Installation

The signed apps can be found in the signed_apks directory. Just install the appropriate version as any other app on your cabinet and run it. If asked, give the permission to access the files on the device.
After the first run, the version that auto starts should automatically start itself at each boot. Also, it seems that the autostart version as to be in the internal memory and not on the SD card, otherwise it does not autostart. Instead, the version that does not auto start must be executed at each boot when needed.

Given that the app emulates a virtual keyboard, it should not interfere with the stock apps, so it is reasonably safe to leave it running. However, if you are a professional player, you might be worried about having a useless process in the background consuming CPU cycles. For this reason, I also created the version that does not auto start.

Note that the app does not modify your system in any way, so, if it does not work or you do not like, at any moment you can just uninstall it and your system will be the same as before.

Important: If you received one of the beta test versions, unistall it before installing the new version.


# History

Beta versions:  
1.0.0: First beta for Yoga Flame cabinet  
1.0.1: Mapped all buttons (2nd player too) to numeric pad keys  
1.0.2: Cleaned up code, changed minsdk so that it runs on MvC2 too  
1.0.3: Switched to Instrumentation to send keyevents, added start at boot  
1.0.4: Found the platform key for MvC2  


Public versions  
1.1.0: First public version


# Technical Details

I found really painful to gather the necessary knowledge to write this app. A community does not grow without sharing knowledge. So, I tried to comment the code explaining what is going on, even adding the links to the websites I used. In addition, let me add here a high level explanation of the code.

The app works by starting a foreground service that reads the internal device associated to the joysticks and transform them into virtual key presses. Foreground services are not killed by Android, but should this happen the code asks Android to restart the service (unless you kill it in some weird way). The auto start version automatically starts the service at the end of each boot.

The app works using only the second joystick outputs because the first one is already treated as a gamepad by the Android system.

The emulated keys corresponds to numeric keypads keys. This particular choice is motivated by the fact that we want to use keys that are not commonly associated with emulators, to facilitate the configuration by the users. However, in principle any set of keys could be emulated.

Android keeps track of each time a key is pressed and released, so we have to do the same in the app: We keep track of the status of the keys so that we inject the correct key pressed or key released event.
A better way would be to emulate a gamepad, but I am not sure how to do it. The virtual keyboard was just the easiest thing for me.

A note on the 2nd player button: this key is already assigned to the virtual gamepad of the first joystick. However, some software (e.g., RetroArch) do not allow to use keys from a gamepad for two players. So, I decided to emulate a key for that button too. This effectively means that when you press that button, two Android events will be generated: one from the native driver for the virtual gamepad and one from the app for the virtual key press. This does not seem to be an issue, in the sense that the RetroArch and Mame4Droid still register only one of the two events.

Normally Android does not allow to emulate key presses, because this would allow apps to interfere among them and this could pose a security threat. So, the app needs the special INJECT_EVENTS permission. Moreover, the access to the internal device of the joystick is also given only to system apps.
So, for both things above, we need to write a system app. The way to do it in Android is to sign the app using a 'platform' key. The platform keys for the Yoga Flame and the MvC2 are different and both are publicly available online. For the moment I will not disclose where to find them because the others-in-the-know also decided not to disclose it. However, at least now you know how it works! Suffices to say that if I found them, anyone sufficiently motivated can do the same. This also means that without the keys you will not be able to make changes to the code.

The code refers to the auto-start version. The non-auto-start version is obtained by just removing the auto starting code in the manifest file.

# Future Work

I suspect a better way would be to directly read the serial port for the status of the joystick and transform that directly into Android events. This would remove the delay of having two processes in a row: one from the system and the other from the app. I think this should be possible just using a system app, without the need to hack the Linux permissions. One would also to take care of the first joystick as well. However, as I said above, I already reached the best I can do. Studying from scratch how the serial port works and implementing the above would take too much time, that unfortunately I do not have.

Another thing to improve is the exact timing to use to poll the device. I use a simple sleep with a fixed time, but I strongly suspect something better can be done.


# Acknowledgments

None of the above would have been possible without studying the documentation and the code by Team Encoder to add a second joystick, that can be found at https://github.com/Team-Encoder/A1AndroidControlFix.
Given that they did not release a version of their software for the Yoga Flame and the Marvel vs Capcom 2, I wanted to do a port. However, while their approach might be better (that is with a smaller input lag because they rewrite the native drivers in C), it requires too much background knowledge on Linux and Android that I do not have. In fact, emulating a gamepad in their approach requires to access a device that apparently not even an Android system app can access, so they need to go around the Linux permissions.
So, I decided to take an easier route, that might be enough for casual players (like myself!).

Another very important source of information for me were the videos and software by The Code Always Wins, https://www.youtube.com/c/thecodealwayswins.

Last but not the least, I want to thank the beta testers:

- chrisalddin for the many tests of the Yoga Flame version and for the idea to use numbers for the mapping
- Amelia Celeste Burgos for the many tests of the Marvel vs Capcom 2 version
- All the other people from Reddit, Facebook, Discord that encouraged me, even just with an emoticon :)
