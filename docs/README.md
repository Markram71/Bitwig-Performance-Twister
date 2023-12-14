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



## Introduction

lorem ipsum

## Overview of the available features 

## Implemented Banks

### Bank 1 Tracks and Mixer
This bank or mode contains a basic mixer for 16 channels in Bitwig. Every encoder of the Midi Fighter Twister is associated to one track in Bitwig. 

| Encoder | Turn (Twisting)      | Click & turn | Click           | Long Click          | RGB Light                   |
|:-------:|----------------------|--------------|-----------------|---------------------|-----------------------------|
|1-16     |Volume of track 1-16  |tbd           |Select track 1-16|arm, mute, or solo** |color of the track in Bitwig

### Bank 2 Channel Strip
This bank focuses on a single selected track. The first encoder is used to select a track. The other encoders are
can be used to change parameters for this selected track. 

Obviously you can use other controllers or Bitwig itself to change the selected track. The encoder in this bank 
always control the parameters of the selected track. This is not always the desired state, i.e. you might want to 
keep the controls fixed on a certain track. This is what the *pin functionality* is for. Bitwig has a feature to 
pin a controller to a certain track (or device, see below). This can be done with a long click on Encoder 1. Once the 
Midi Fighter Twister is pinned to the currently selected track, the first encoder turn green and you cannot change the selected
track with encoder 1 any more. 

| Encoder | Turn (Twisting)        | Click & turn | Click           | Long Click          | RGB Light                   |
|:-------:|------------------------|--------------|-----------------|---------------------|-----------------------------|
|1        |select a track          |tbd           |toggle arm       |pin the track        | green if track is selected |
|2        |volume of selected track|tbd           |toggle solo      |n/a                  | yellow if track is soloed





### Bank 3 Device and Project-wide Remote Controls

### Bank 4 EQ

### Bank 5 Global Parameters

### Bank 6 User defined controls

## Installation

### Setup of the Midi Fighter Twister

### Setup in Bitwig
