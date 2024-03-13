# Bitwig Performance Twister - OSC Extension<!-- omit in toc -->

This is the description of the OSC extension to Bitwig Performance Twister. Please go here for the general description of what Bitwig Performance Twister can do for you. 
  

## Table of Contents <!-- omit in toc -->
1. [Introduction to the OSC extension](#introduction)
1. [Available OSC Messages](#available-osc-messages)

## Introduction
Bitwig Performance Twister is an extension for the DAW Bitwig to use the Midi controller "Midi Fighter Twister". I have added additional (optional) functionality to this script so 
that you can see what you are controlling with the Midi controller. You can use a any OSC capable device, that could be an iPad which is running TouchOSC, for instance. And that is 
exactly the setup I am using (and also the only OSC setup which I have tested). 
With the OSC extension you are able to 
* see the functionality of the Midi Fighter Twister, e.g. see on the OSC surface which encoder is controlling which parameter in Bitwig
* see which of the six modes you are currently in
* add additional controls which send and receive OSC messages
* add command buttons to trigger actions in Bitwig
* use the full OSC implementation provided by the Bitwig extension *Driven by Moss* 


## Available OSC Messages
First of all, all OSC message that come with Jürgen Mossgräber's (aka MOSS) OSC implementation are avaialabe. For further information see his[web site for Bitwig](https://www.mossgrabers.de/Software/Bitwig/Bitwig.html) or his [DriveByMoss documentation](https://github.com/git-moss/DrivenByMoss/blob/master/DrivenByMoss-Manual.pdf). 

Additionally, I have implemented the following messages. These are all commands which means that these messages can be send from an OSC surface to Bitwig. There is not message from Bitwig to the OSC device.  


| Message/Command                  | Value                                        | Comment                                                                                         | 
|:---------------------------------|----------------------------------------------|-------------------------------------------------------------------------------------------------|
|Commands/addTrack                 | n/a                                          | adds a new empty instrument track                                                               |
|Commands/addFX                    | n/a                                          | adds a new empty FX track                                                                       |
|Commands/addAudio                 | n/a                                          | adds a new empty audio track                                                                    |
|Commands/showArranger             | n/a                                          | switches to show the Arranger in the main window                                                |
|Commands/showMixer                | n/a                                          | switches to show the Mixer in the main window                                                   |
|Commands/showEditor               | n/a                                          | switches to show the editor in the main window                                                  |
|Commands/toggleDevice             | n/a                                          | toggles the device window in the lower part of the Bitwig window                                |
|Commands/toggleMixer              | n/a                                          | toggles the mixer window in the lower part of the Bitwig window                                 |
|Commands/toggleInspector          | n/a                                          | toggles the Inspector on and off                                                                |
|Commands/toggleFullScreen         | n/a                                          | toggle Bitwig to full screen and back                                                           |
|Commands/bounce                   | n/a                                          | executes the bounce command (if possible)                                                       |
|Commands/bounceInPlace            | n/a                                          | exectues bounce in place (if possible)                                                          |
|addElements/Instrument/VST2       | Bitwig Code of the VST2 instrument (Integer) | add a new track and put the referenced VST2 instrument at the beginning of the device chain     |
|addElements/Instrument/VST3       | Bitwig Code of the VST3 instrument (String)  | add a new track and put the referenced VST3 instrument at the beginning of the device chain     |
|addElements/Instrument/CLAP       |  Bitwig Code of the CLAP instrument (String)  | add a new track and put the referenced CLAP instrument at the beginning of the device chain    |
|addElements/Instrument/Bitwig     |  Bitwig Code of the Bitwig instrument (String)  | add a new track and put the referenced Bitwig instrument at the beginning of the device chain    |
|addElements/FX/VST2               | Bitwig Code of the VST2 Effect (Integer)    | add the referenced VST2 effect at the end  of the device chain                                      |
|addElements/FX/VST3               | Bitwig Code of the VST3 Effect (String)  | add the referenced VST3 effect at the end  of the device chain                                      |
|addElements/FX/CLAP              | Bitwig Code of the Clap Effect (String)  | add the referenced VST2 effect at the end  of the device chain                                      |
|addElements/FX/Bitwig              | Bitwig Code of the Bitwig Effect (String)  | add the referenced Bitwig effect at the end  of the device chain                                      |
|addElements/Instrumet/Patch        | n/a  | not yet implemented                                      |
