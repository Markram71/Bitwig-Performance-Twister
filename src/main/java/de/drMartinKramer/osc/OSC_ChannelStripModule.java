/*
 * Copyright 2023-2024 Dr. Martin Kramer
 *
 * This file is part of the Extension "Bitwig Performance Twister".
 *
 * Bitwig Performance Twister (BPT) is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or any later version.
 *
 * I have written Bitwig Performance Twister for my own use and hope that it can be of some value for you. 
 * Nevertheless, BPT does come WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * Bitwig Performance Twister
 * If not, see https://www.gnu.org/licenses/.
 *
 */


package de.drMartinKramer.osc;

import com.bitwig.extension.controller.api.ControllerHost;

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.MFT_Hardware;

public class OSC_ChannelStripModule extends AbstractMFT_OSC_Module{

    public OSC_ChannelStripModule(ControllerHost host){
        super(host);

        
        for(int i=0;i<MFT_Hardware.MFT_NUMBER_OF_ENCODERS;i++){
            encoderState[i].isSelected = false;
            encoderState[i].exists = true;
        }

        encoderState[0].turnFunction = "Select a track";
        encoderState[0].name = "Track";
        encoderState[1].turnFunction = "volume";
        encoderState[1].name = "Volume";
        encoderState[2].turnFunction = "panning";
        encoderState[2].name = "Panning";

        if(MFT_Configuration.isChannelStripEncoder4_MasterVolume()){
            encoderState[3].turnFunction = "master volume";
            encoderState[3].name = "Master";
        }
        else if(MFT_Configuration.isChannelStripEncoder4_CueVolume()) {
            encoderState[3].turnFunction = "cue volume";
            encoderState[3].name = "Cue";
        }
        else if(MFT_Configuration.isChannelStripEncoder4_Crossfader()) {
            encoderState[3].turnFunction = "crossfader";
            encoderState[3].name = "X-Fader";
        }

        MFT_Configuration.addValueObserver_ChannelStripEncoder4(newValue -> setEncoder4Function(newValue));

        encoderState[4].turnFunction = "Send FX 1";
        encoderState[4].name = "FX 1";
        encoderState[5].turnFunction = "Send FX 2";
        encoderState[5].name = "FX 2";
        encoderState[6].turnFunction = "Send FX 3";
        encoderState[6].name = "FX 3";
        encoderState[7].turnFunction = "Send FX 4";
        encoderState[7].name = "FX 4";



        for (int i= 0;i<8;i++){ //go through all eight track remotes
            encoderState[8+i].turnFunction = "Track remote " + (i+1);
        }

        final String function = (MFT_Configuration.isChannelStripShiftClickActionCueMarker() ? "launch cue marker " : "launch scene ");
        for(int i=0;i<14;i++){
            encoderState[i].shiftClickFunction = function + (i+1);
        }
        encoderState[14].shiftClickFunction = "stop";
        encoderState[15].shiftClickFunction = "shift button";
        MFT_Configuration.addValueObserver_ChannelStripShiftClick(newValue -> setShiftClickFunction(newValue));
        
        //CLICK 
        encoderState[0].clickFunction = "toggle arm";
        encoderState[1].clickFunction = "toggle solo";
        encoderState[2].clickFunction = "toggle mute";
        encoderState[3].clickFunction = "toggle fill";

        for(int i=0;i<4;i++){
            encoderState[4+i].clickFunction = "toggle send " + (i+1) + " enabled";
        }

        encoderState[0].longClickFunction = "pin track";    

        //---- Track Remotes
        encoderState[8].turnFunction =  "Device Param 1";
        encoderState[9].turnFunction =  "Device Param 2";
        encoderState[10].turnFunction = "Device Param 3";
        encoderState[11].turnFunction = "Device Param 4";
        encoderState[12].turnFunction = "Device Param 5";
        encoderState[13].turnFunction = "Device Param 6";
        encoderState[14].turnFunction = "Device Param 7";
        encoderState[15].turnFunction = "Device Param 8";

    } // end of constructor
 
    private void setEncoder4Function(String encoder4Function){
        sendEncoderTurnFunction(3, encoder4Function);
    }

    private void setShiftClickFunction(String shiftClickFunction){
        for(int i=0;i<NO_OF_ENCODERS-2;i++){ //don't set it for the last two encoders
            sendEncoderShiftClickFunction(i, shiftClickFunction + " " + (i+1));            
        }
    }

}
