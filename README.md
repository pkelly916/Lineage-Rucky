# Rucky 
[![GitHub](https://img.shields.io/github/license/mayankmetha/Rucky)](https://github.com/mayankmetha/Rucky/blob/master/LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/mayankmetha/Rucky)](https://github.com/mayankmetha/Rucky/releases/latest)
[![Crowdin](https://badges.crowdin.net/rucky/localized.svg)](https://mayankmetha.github.io/Rucky/)
[![Android](https://img.shields.io/badge/android-6.x%2B-lightgrey)](https://github.com/mayankmetha/Rucky)
[![Architecture](https://img.shields.io/badge/architecture-Independent-blueviolet)](https://github.com/mayankmetha/Rucky)

A USB HID Rubber Ducky Script All-In-One tool, modified to enable/disable HID gadget in Lineage 16.1 Kernel.

## Fork
Rucky was modified to interact with a custom HID gadget implementation. Two patch files are included that update the Lineage kernel to include an HID keyboard device that can be enabled/disabled from the commandline. The patches were adapted from [Peyla's work](https://github.com/pelya/android-keyboard-gadget). The patches in this repository only support Lineage 16.1 on the Google Pixel (codename Sailfish), however they should be easily modifiable to any other device. Superspeed (USB 3.0) support has been included. Rucky itself has been modified to enable/disable this device from the app itself. The app is otherwise unchanged from the upstream code. 

## Extensions Repo

[![RPi](https://img.shields.io/badge/Raspberry%20Pi-0%20W-maroon)](https://github.com/mayankmetha/Rucky-Ext-RPi)

## Legacy Work

[![HID](https://img.shields.io/badge/Project-Legacy%20HID-lightgreen)](https://github.com/mayankmetha/Rucky-Legacy-HID)
[![Android](https://img.shields.io/badge/android-4.4.x-green)](https://github.com/mayankmetha/Rucky/releases/tag/1.9)
[![Android](https://img.shields.io/badge/android-5.x-green)](https://github.com/mayankmetha/Rucky/releases/tag/1.9)
