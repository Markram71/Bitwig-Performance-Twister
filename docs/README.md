# Bitwig Performance Twister <!-- omit in toc -->

This is an extension for the Bitwig DAW (Digital Audio Workstation). 
With this extension you will be able to use the midi controller 
"Midi Fighter Twister" by DJ TechTools (see https://www.midifighter.com/#Twister). 

## Table of Contents <!-- omit in toc -->
1. [Introduction](#introduction)
1. [Overview of available features](#overview-of-the-available-features)
1. [Implemented Banks](#implemented-banks)
    1. [Bank 1 Tracks and Mixer](#bank-1-tracks-and-mixer)
    1. [Bank 2 Channel Strip](#bank-2-channel-strip) 
    1. [Bank 3 Device and Project-wide Remote Controls](#bank-3-device-and-project-wide-remote-controls) 
    1. [Bank 4 EQ](#bank-4-eq)  
    1. [Bank 5 Global Parameters](#bank-5-global-parameters) 
    1. [Bank 6 User defined Controls](#bank-6-user-defined-controls) 
       
1. [Installation](#installation)
    1. [Setup of the Midi Fighter Twister](#setup-of-the-midi-fighter-twister)
    1. [Setup in Bitwig](#setup-in-bitwig)

1. [Implementation Notes](#implementation-notes)


## Introduction

lorem ipsum

## Overview of the available features 
Bitwig performance Twister offers the following features 

* **Banks:** The Bitwig Performance Twister offers six different banks or modes for the 16 encoders of the midi controller. Each bank can be accessed by clicking on one of the buttons on the side.
  The encoders for the Midi Figher Twister then behave differently
on each bank, see further details below. You also have the possibility of a temporary bank switch. For that press on the side button for the bank you temporaririly want to switch to and hold the side button pressed. You can turn and click on the encoders of the newly selected bank. Once you release the side button, you immediately return to the previous bank. You can use this, for instance to quickly adjust the volume of a track in the mixer and directly return a device control. 

* **Long clicks:** By clicking and keeping the encoder pressed down for a short time, you can get access to a secondary action for some of the encoders
* **Click & turn:** Click down an encoder button and turn the encoder while it's pressed down. This gives access to a secondary paramter which can be controlled with this same encoder.  

## Implemented Banks

### Bank 1 Tracks and Mixer
This bank or mode contains a basic mixer for 16 channels in Bitwig. Every encoder of the Midi Fighter Twister is associated to one track in Bitwig. 

| Encoder | Turn (Twisting)      | Click & turn | Click           | Long Click          | RGB Light                   |
|:-------:|----------------------|--------------|-----------------|---------------------|-----------------------------|
|1-16     |Volume of track 1-16  |panning       |Select track 1-16|arm, mute, or solo** |color of the track in Bitwig

### Bank 2 Channel Strip
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


### Bank 3 Device and Project-wide Remote Controls
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
|10       |Project remote  2       |tbd           |go to previous deviece    |n/a                  | color on Bitwig parameter page |
|11       |Project remote  3       |tbd           |go to next device         |n/a                  | color on Bitwig parameter page |
|12       |Project remote  4       |tbd           |go to last device         |n/a                  | color on Bitwig parameter page |
|13       |Project remote  5       |tbd           |go to first param bank    |n/a                  | color on Bitwig parameter page |
|14       |Project remote  6       |tbd           |go to previous param bank |n/a                  | color on Bitwig parameter page |
|15       |Project remote  7       |tbd           |go to next param bank     |n/a                  | color on Bitwig parameter page |
|16       |Project remote  8       |tbd           |go to last param bank     |n/a                  | color on Bitwig parameter page |



### Bank 4 EQ

### Bank 5 Global Parameters

### Bank 6 User defined controls

## Installation

### Setup of the Midi Fighter Twister

### Setup in Bitwig

## Controller Configuration

## Implementation Notes
how are the 4 banks of the MFT mapped to the 6 banks of the BPT...

## License
