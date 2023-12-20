# Bitwig Performance Twister <!-- omit in toc -->

This is an extension for the Bitwig DAW (Digital Audio Workstation). 
With this extension you will be able to use the midi controller 
"Midi Fighter Twister" by DJ TechTools (see https://www.midifighter.com/#Twister). 

## Table of Contents <!-- omit in toc -->
1. [Introduction](#introduction)
1. [Overview of available features](#overview-of-the-available-features)
1. [Implemented Modes](#implemented-modes)
    1. [Mode 1 Mixer](#mode-1-mixer)
    1. [Mode 2 Channel Strip](#mode-2-channel-strip) 
    1. [Mode 3 Device and Project-wide Remote Controls](#mode-3-device-and-project-wide-remote-controls) 
    1. [Mode 4 EQ](#mode-4-eq)  
    1. [Mode 5 Global Parameters](#mode-5-global-parameters) 
    1. [Mode 6 User defined Controls](#mode-6-user-defined-controls) 
       
1. [Installation](#installation)
    1. [Setup of the Midi Fighter Twister](#setup-of-the-midi-fighter-twister)
    1. [Setup in Bitwig](#setup-in-bitwig)

1. [Implementation Notes](#implementation-notes)


## Introduction

lorem ipsum

## Overview of the available features 
Bitwig performance Twister offers the following features 

* **Modes:** The Bitwig Performance Twister offers six different modes for the 16 encoders of the midi controller. Each mode can be accessed by clicking on one of the buttons on the side.
  The encoders for the Midi Figher Twister then behave differently
on each mode, see further details below. You also have the possibility of a temporary mode switch. For that press on the side button for the bank you temporaririly want to switch to and hold the side button pressed. You can turn and click on the encoders of the newly selected mode. Once you release the side button, you immediately return to the previous mode. You can use this, for instance to quickly adjust the volume of a track in the mixer and directly return a device control. 

* **Long clicks:** By clicking and keeping the encoder pressed down for a short time, you can get access to a secondary action for some of the encoders
* **Click & turn:** Click down an encoder button and turn the encoder while it's pressed down. This gives access to a secondary paramter which can be controlled with this same encoder.  

## Implemented Modes

### Mode 1 Mixer
This bank or mode contains a basic mixer for 16 channels in Bitwig. Every encoder of the Midi Fighter Twister is associated to one track in Bitwig. 

| Encoder | Turn (Twisting)      | Click & turn | Click           | Long Click          | RGB Light                   |
|:-------:|----------------------|--------------|-----------------|---------------------|-----------------------------|
|1-16     |Volume of track 1-16  |panning       |Select track 1-16|arm, mute, or solo** |color of the track in Bitwig

### Mode 2 Channel Strip
This bank focuses on a single selected track. The first encoder is used to select a track. The other encoders are
can be used to change parameters for this selected track. 

Obviously you can use other controllers or Bitwig itself to change the selected track. The encoder in this bank 
always control the parameters of the selected track. This is not always the desired state, i.e. you might want to 
keep the controls fixed on a certain track. This is what the *pin functionality* is for. Bitwig has a feature to 
pin a controller to a certain track (or device, see below). This can be done with a long click on Encoder 1. Once the 
Midi Fighter Twister is pinned to the currently selected track, the first encoder turn green and you cannot change the selected
track with encoder 1 any more. 

| Encoder | Turn (Twisting)              | Click & turn | Click             | Long Click          | RGB Light                   |
|:-------:|------------------------------|--------------|-------------------|---------------------|-----------------------------|
|1        |select a track                |tbd           |toggle arm         |pin the track        | green if track is selected  |
|2        |volume of selected track      |tbd           |toggle solo        |n/a                  | yellow if track is soloed   |
|3        |panning selected track        |tbd           |toggle mute        |n/a                  | orange if track is muted    |
|4        |volume: master,cue, x-fader(1)|tbd           |toggle fill (2)    |n/a                  | tbd                         |
|5-8      |send to fx channel 1-4        |tbd           |toggle send enabled|n/a                  | blue, dark blue if send disabled|
|9-16     |track remote control 1-8      |tbd           |toggle send enabled|n/a                  | color on Bitwig parameter page|

(1) Encoder #4 on this bank can be configured to control the
* master volume 
* cue volume
* crossfader volume  

The control depends on the setting of the controller configuration. See [Controller Configuration](#controller-configuration).


(2) A click on encoder #4 on this bank toggle the global Bitwig fill flag which effects operators


### Mode 3 Device and Project-wide Remote Controls
| Encoder | Turn (Twisting)        | Click & turn | Click                    | Long Click          | RGB Light                      |
|:-------:|------------------------|--------------|--------------------------|---------------------|--------------------------------|
|1        |device parameter 1      |tbd           |go to first device        |toggle device on/off | color on Bitwig parameter page |
|2        |device parameter 2      |tbd           |go to previous deviece    |n/a                  | color on Bitwig parameter page |
|3        |device parameter 3      |tbd           |go to next device         |n/a                  | color on Bitwig parameter page |
|4        |device parameter 4      |tbd           |go to last device         |n/a                  | color on Bitwig parameter page |
|5        |device parameter 5      |tbd           |go to first param bank    |n/a                  | color on Bitwig parameter page |
|6        |device parameter 6      |tbd           |go to previous param bank |n/a                  | color on Bitwig parameter page |
|7        |device parameter 7      |tbd           |go to next param bank     |n/a                  | color on Bitwig parameter page |
|8        |device parameter 8      |tbd           |go to last param bank     |n/a                  | color on Bitwig parameter page |
|9        |Project remote  1       |tbd           |go to first device        |toggle device on/off | color on Bitwig parameter page |
|10       |Project remote  2       |tbd           |go to previous device     |n/a                  | color on Bitwig parameter page |
|11       |Project remote  3       |tbd           |go to next device         |n/a                  | color on Bitwig parameter page |
|12       |Project remote  4       |tbd           |go to last device         |n/a                  | color on Bitwig parameter page |
|13       |Project remote  5       |tbd           |go to first param bank    |n/a                  | color on Bitwig parameter page |
|14       |Project remote  6       |tbd           |go to previous param bank |n/a                  | color on Bitwig parameter page |
|15       |Project remote  7       |tbd           |go to next param bank     |n/a                  | color on Bitwig parameter page |
|16       |Project remote  8       |tbd           |go to last param bank     |n/a                  | color on Bitwig parameter page |



### Mode 4 EQ
This mode is there to help you twist and turn the prarameters of the Bitwig EQ+. In case there is an EQ+ installed in the device chain of the currently selected track, the encoders of the Midi Fighter Twister are automatically mapped to this EQ device. In case there is no EQ available you can easily create a new EQ+ device by simply clicking on any encoder button. This installs the  new EQ+ at the end of the device chain. 

| Encoder | Turn (Twisting)     | Click & turn | Click                     | Long Click           | RGB Light                 |
|:-------:|---------------------|--------------|---------------------------|----------------------|---------------------------|
|1        |gain for band 1      |n/a           |toggle band on/off         |toggle device on/off (1) | Bitwig EQ color of band 1 (2) |
|2        |gain for band 2      |n/a           |toggle band on/off         |toggle window         | Bitwig EQ color of band 2 |
|3        |gain for band 3      |n/a           |toggle band on/off         |n/a                   | Bitwig EQ color of band 3 |
|4        |gain for band 4      |n/a           |toggle band on/off         |n/a                   | Bitwig EQ color of band 4 |
|5        |frequency of bank 1  |n/a           |reset frequency to default |n/a                   | Bitwig EQ color of band 1 |
|6        |frequency of bank 2  |n/a           |reset frequency to default |n/a                   | Bitwig EQ color of band 2 |
|7        |frequency of bank 3  |n/a           |reset frequency to default |n/a                   | Bitwig EQ color of band 3 |
|8        |frequency of bank 4  |n/a           |reset frequency to default |n/a                   | Bitwig EQ color of band 4 |
|9        |Q valaue for band 1  |n/a           |reset Q to default         |n/a                   | Bitwig EQ color of band 1 |
|10       |Q valaue for band 2  |n/a           |reset Q to default         |n/a                   | Bitwig EQ color of band 2 |
|11       |Q valaue for band 3  |n/a           |reset Q to default         |n/a                   | Bitwig EQ color of band 3 |
|12       |Q valaue for band 4  |n/a           |reset Q to default         |n/a                   | Bitwig EQ color of band 4 |
|13       |EQ type for band 1   |n/a           |toggle band on/off (3)     |n/a                   | Bitwig EQ color of band 1 |
|14       |EQ type for band 2   |n/a           |toggle band on/off (3)     |n/a                   | Bitwig EQ color of band 2 |
|15       |EQ type for band 3   |n/a           |toggle band on/off (3)     |n/a                   | Bitwig EQ color of band 3 |
|16       |EQ type for band 4   |n/a           |toggle band on/off (3)     |n/a                   | Bitwig EQ color of band 4 |

* (1) A long click on the first encoder toggles the EQ+ device on an off. 
* (2) The color of the encoders in a column reflect (somehow closely) the color of the band in the EQ+ decive in bitwig. All encoders for a band (i.e. in a colum) have the same color. The colored led is switched off when the respective band is deactivated. You can still turn the parameters and see their value on the out ring though.
* (3) A click on the encoder of the last row toggles the band on or off. This toggle switch is different to the toggle switch of the first row as it resets the band to the following useful band types: band 0: 24db low cut, band 1-2: bell curve, band 3: high shelf



### Mode 5 Global Parameters

### Mode 6 User defined controls

## Installation

### Setup of the Midi Fighter Twister

### Setup in Bitwig

## Controller Configuration

## Implementation Notes
* Although the Midi Performance Twister (this script) has six modes, the Midi Fighter Twister (the hardware device) has only four banks. The modes which are associated to the left side buttons (mixer mode, EQ mode, global mode) are mapped to the first bank of the hardware device. The channel strip mode is mapped to bank two. The device mode is mapped to bank three. The user assignable mode on bank four. This is also the bank that you can freely assign to your liking. Changing any parameter on banks one to three of the hardware device will cause this script to malfunction.

## Acknowledgements
I am very thankful to Jürgen Moßgräber (MOSS) for his support of the midi controller API. I am using several of his scripts for other controllers and the youtube videos on how to use the Bitwig controller API have been a tremendous help. I could not have implemented this without his contributions. 


## License
This Bitwig extension is alvailable under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3. 
