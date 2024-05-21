# FighterDroid2P

This Android app allows to use the second joystick (and its buttons) on the Arcade1up Fighter Droid cabinets.
It works by associating the joystick and its buttons to the keys of a virtual keyboard.

It has been tested on the Yoga Flame and on the Marvel vs Capcom 2 cabinets. Please let me know if it works on the other cabinets too. In theory, the Marvel vs Capcom, the Big Blue, and The X-Men vs Street Fighter, should all work with the Yoga Flame apk: it would be great to have some feedback on this!

This is literally my first Android app, written in my spare time. The first version took ~1 week. So, it is very possible that something better could be done! For this reason, I release the code so that people more expert than me can improve and/or build on it.

To report bugs, please be as specific as possible and remember that I only have the Yoga Flame cabinet. So, for other cabinets, you'll have to know how to use adb to gather the logs, otherwise I will not be able to know what is going on.


# Disclaimer

This software is provided without any warranties, express or implied, and their usage carries inherent risks. By using the software, you accept full responsibility for any potential damage to your system or device. The developers, distributors, and contributors of this software explicitly disclaim liability for any data loss, system malfunctions, or adverse effects resulting from its installation or usage.

Additionally, please be aware that this software is intended solely for educational and experimental purposes. It is not intended for commercial use and is not supported by any warranty or technical assistance. Installing the app may void your device's warranty, and you should proceed with caution, ensuring you have backups and are aware of potential security risks. By using the app, you agree to these terms and acknowledge that the developers and contributors are not liable for any consequences arising from its use.


# Installation

The signed apps can be found in the Releases to the right of the screen. Just install the appropriate version as any other app on your cabinet and run it. If asked, give the permission to access the files on the device.

Video tutorials for the installation (not by me!):
- for the Yoga Flame, [YouTube video](https://www.youtube.com/watch?v=m1gkwDe9c7w)
- for the MvC2, [YouTube video](https://www.youtube.com/watch?v=oFziMSZgte0). (The video is for the version 1.1.0, but it would be the same for the other versions as well.)

After the first run, the version that auto starts should automatically start itself at each boot. Also, in some cases the autostart version has to be in the internal memory and not on the SD card, otherwise it does not autostart. Instead, the version that does not auto start must be executed at each boot when needed.

Given that the app emulates a virtual keyboard, it should not interfere with the stock apps, so it is reasonably safe to leave it running. However, if you are a professional player, you might be worried about having a useless process in the background consuming CPU cycles. For this reason, I also created the version that does not auto start.

Note that the app does not modify your system in any way, so, if it does not work or you do not like, at any moment you can just uninstall it and your system will be the same as before.

Important: If you received one of the beta test versions, unistall it before installing the new version.

This app should work with RetroArch, Mame4Droid, and RetroX. For RetroX, select the RetroX version, and for the others select the RetroArch version.


# Use with RetroArch

RetroArch might have some problems with games that requires 2 or 3 buttons to be pressed at the same time. This is a problem of RetroArch, not of this app, in fact you could experience the same problem with the Player One Joystick without this app.
Luckily, there is an easy fix found by calwinarlo on Reddit: Open the RetroArch Quick Menu and go to Latency, find "Input Block Timeout" and set it to any number other than 0 and 1 (it seems that 1 fixes the issue for the left stick but not the right, 2 fixes both, but you can experiment with different numbers).


# History

1.0.0 (beta): First beta for Yoga Flame cabinet  
1.0.1 (beta): Mapped all buttons (2nd player too) to numeric pad keys  
1.0.2 (beta): Cleaned up code, changed minsdk so that it runs on MvC2 too  
1.0.3 (beta): Switched to Instrumentation to send keyevents, added start at boot  
1.0.4 (beta): Found the platform key for MvC2  
1.1.0: First public version  
1.2.0: Added support for RetroX, way smaller delay using reflections and non-locking injection of events, adaptive sleep  
1.2.3: Now the RetroX version uses the umidokey2 device on the Yoga Flame and the rk29-keypad on the MvC2, while the RetroArch versions uses the virtual in all cases  
1.2.4: Now the RetroX version uses the ACCDET device on the Yoga Flame, so it is compatible with Big Blue  



# Technical Details

I found really painful to gather the necessary knowledge to write this app. A community does not grow without sharing knowledge. So, I tried to comment the code explaining what is going on, even adding the links to the websites I used. In addition, let me add here a high level explanation of the code.

The app works by starting a foreground service that reads the internal device associated to the joysticks and transform them into key presses. Foreground services are not killed by Android, but should this happen the code asks Android to restart the service (unless you kill it on purpose). The auto start version automatically starts the service at the end of each boot.

The app works using only the second joystick outputs because the first one is already treated as a gamepad called "robot" by the Android system.

The emulated keys corresponds to numeric keypads keys (and gamepad direction keys for the RetroX version). This particular choice is motivated by the fact that we want to use keys that are not commonly associated with emulators, to facilitate the configuration by the users, while in RetroX we are forced to use gamepad keys for the directions. However, in principle any set of keys could be emulated.

Android keeps track of each time a key is pressed and released, so we have to do the same in the app: We keep track of the status of the keys so that we inject the correct key pressed or key released event. In order to have a very minimal delay of 1-2ms, we do not wait for the injection to be succesfully received.

A note on the 2nd player button: this key is already assigned to the virtual gamepad of the first joystick. However, some software (e.g., RetroArch) do not allow to use keys from a gamepad for different players. So, I decided to emulate a key for that button too. This effectively means that when you press that button, two Android events will be generated: one from the native driver for the virtual gamepad and one from this app. This does not seem to be an issue, in the sense that the RetroArch, Mame4Droid, and RetroX still register the event correctly.

We inject key press and key release events as if they were generated by input devices. Now, RetroArch and Mame4Droid are happy with events generated by a virtual keyboard, that is present on any Android device, while RetroX seems to have a bug: it will allow you to set the keys if the device is the virtual keyboard, but then it won't work in the games! (I did report the bug to the RetroX developer.) So, now I still use the virtual keyboard for the RetroArch version, while for the RetroX version I use the id of the ACCDET device on the Yoga Flame/Big Blue and the rk29-keypad device on the MvC2. Note that the id of the devices might change with each reboot and if other devices (e.g., keyboard, mouse) are connected. So, I go over the devices and I find the id corresponding to ACCDET/rk29-keypad. In this way, you do not have to reconfigure RetroX each time. Note that the id of the virtual keyboard instead is always the same and it is equal to -1.

For reference, Here is the names of the devices for each cab:

- Yoga Flame:
ACCDET, umidokey2, robot, mtk-kpd

- MVC2:
rk29-keypad, rk-headset, robot

- Big Blue:
ACCDET, robot, mtk-kpd

VIRTUAL is always present as well, with keyboard type 2. The others are all keyboard type 1.

Normally Android does not allow to emulate key presses, because this would allow apps to interfere among them and this could pose a security threat. So, the app needs the special INJECT_EVENTS permission. Moreover, the access to the internal device of the joystick is also given only to system apps.
So, for both things above, we need to write a system app. The way to do it in Android is to sign the app using a 'platform' key. The platform keys for the Yoga Flame and the MvC2 are different and both are publicly available online. For the moment I will not disclose where to find them because the others-in-the-know also decided not to disclose it. However, at least now you know how it works! Suffices to say that if I found them, anyone sufficiently motivated can do the same. This also means that without the keys you will not be able to make changes to the code and run it on your arcade machines.

The code in this repository refers to the auto-start version. The non-auto-start version is obtained by just removing the auto starting code in the manifest file.

# Future Work

I suspect a better way would be to directly read the serial port for the status of the joystick and transform that directly into Android events. This would remove the delay of having two processes in a row: one from the system and the other from the app. I think this should be possible just using a system app, without the need to hack the Linux permissions. One would also to take care of the first joystick as well. However, studying from scratch how the serial port works and implementing the above would take too much time, that unfortunately I do not currently have.

~~Another thing to improve is the exact timing to use to poll the device. I use a simple sleep with a fixed time, but I strongly suspect something better can be done.~~ Adaptive sleep added from version 1.2.0.


# Acknowledgments

None of the above would have been possible without studying the documentation and the code by Team Encoder to add a second joystick, that can be found at https://github.com/Team-Encoder/A1AndroidControlFix.
Given that they did not release a version of their software for the Yoga Flame and the Marvel vs Capcom 2, I wanted to do a port. However, while their approach might be more direct, it requires too much background knowledge on Linux and Android that I do not have. In fact, emulating a gamepad in their approach requires to access a Linux device that apparently not even an Android system app can access, so they need to go around the Linux permissions.
So, I decided to take an easier route, that might be enough for casual players (like myself!).

Another very important source of information for me were the videos and software by The Code Always Wins, https://www.youtube.com/c/thecodealwayswins.

Last but not the least, I want to thank the beta testers:

- Martin Biener for the first test on the Yoga Flame
- chrisalddin for the many tests of the Yoga Flame version and for the idea to use numbers for the mapping
- Amelia Celeste Burgos for the many tests of the MvC2 version
- calwinarlo for finding a solution to the RetroArch multiple button issue
- Janice Wilson for finding a bug in version 1.2.0 and helping with the best testing
- Carlos Osegueda and Marco Tramontano for the tests on the MvC2 for the version 1.2.3
- DryExpression704 for the tests with RetroX on Big Blue
- All the other people from Reddit, Facebook, Discord that encouraged me, even just with an emoticon :)
