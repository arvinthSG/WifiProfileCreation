This funcitonality of the module is to create a Wi-Fi network connection within the application. With each Android OS updates the Network Creation has become more and more difficult.
From Android 4 to Android 8 google has made several changes to how to create and delete Wi-Fi connections.

Starting With Lollipop(API:21) - which has the most unreliable Wifi stack. Maninting a network connection within a local network which has no internet access is extremely unstable. This module tries to address this issue by disabling auto-reconnect with all other networks. Wi-Fi scan doesn't return results if the location is not turned on.

Marshmellow(API:23) - Android decide to not give the permission of modifying the network profile to anyone(any application) who hasn't created the profile. Every app can use it by cannot modify or delete it. Even a different instance of the same application cannot modify the network profile.

