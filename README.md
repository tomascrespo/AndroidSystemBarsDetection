# AndroidSystemBarsDetection
How to detect Android system bars visibility in Inmersive Mode

# Description
Android has Inmersive (normal) mode and Inmersive Sticky mode. In Inmersive Sticky mode you can *NOT* detect if user has make a gesture to display system bars or not. It is a design feature, Android has decide that in Inmersive *Sticky* mode the system bars are always not visible, even when they are visible because is a transient state.

# Solution
If you want to use Inmersive Sticky mode and you need to detect when system bars are visible or hidden the only option you have is to user Inmersive (normal, not sticky) mode and make some adjustments:
1. System bars have to not modify your layout
2. System bars should auto-hidden after a few seconds

This way you will emulate sticky mode, and listeners could be used to detect when systems bars are visible or not.

# Code
In this example app you can see all this stuff, inmersive (normal) mode emulating inmersive sticky mode, and system bars detection
