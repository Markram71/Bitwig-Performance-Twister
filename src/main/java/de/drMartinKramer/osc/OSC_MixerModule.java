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


public class OSC_MixerModule extends AbstractMFT_OSC_Module{

    public OSC_MixerModule(ControllerHost host){
        super(host);
        
        for (int i= 0;i<NO_OF_ENCODERS;i++){
            this.encoderState[i].clickFunction = "Select track";
            this.encoderState[i].turnFunction = "Track volume";
                        
            if(MFT_Configuration.isMixerLongButtonActionArm())          this.encoderState[i].longClickFunction = "Record arm";
            else if(MFT_Configuration.isMixerLongButtonActionMute())    this.encoderState[i].longClickFunction = "Mute";
            else if(MFT_Configuration.isMixerLongButtonActionSolo())    this.encoderState[i].longClickFunction = "Solo";
           
            if(MFT_Configuration.isMixerShiftClickActionCueMarker())    this.encoderState[i].shiftClickFunction = "Launch cue marker " + (i+1);
            else if(MFT_Configuration.isMixerShiftClickActionScene())   this.encoderState[i].shiftClickFunction = "Launch scene " + (i+1);
            
            if(MFT_Configuration.isMixerPushAndTurnFunctionPan())                  this.encoderState[i].pushTurnFunction = "Pan";
            else if(MFT_Configuration.isMixerPushAndTurnFunctionSend1())           this.encoderState[i].pushTurnFunction = "Send 1 level";
            else if(MFT_Configuration.isMixerPushAndTurnFunctionTrackRemote1())    this.encoderState[i].pushTurnFunction = "Track remote 1";
            
        }
        //Override the last two encoder clicks as we need them for the shift button: 
        this.encoderState[NO_OF_ENCODERS-1].shiftClickFunction = "Shift button";
        this.encoderState[NO_OF_ENCODERS-2].shiftClickFunction = "Stop";

        //We need to add listeners to stay informed about changes in the configuration
        MFT_Configuration.addValueObserver_MixerLongButton(newConfig -> setLongClickFunction(newConfig));
        MFT_Configuration.addValueObserver_MixerShiftClick(newConfig -> setShiftClickFunction(newConfig));
        MFT_Configuration.addValueObserver_MixerPushAndTurnFunction(newConfig -> setClickAndTurnFunction(newConfig));

    } //end of constructor

    
    private void setLongClickFunction(String longClickFunction){
        for (int i = 0;i<NO_OF_ENCODERS;i++){
            sendEncoderLongClickFunction(i, longClickFunction);
        }
    }
    
    private void setShiftClickFunction(String shiftClickFunction){
        for(int i=0;i<NO_OF_ENCODERS-2;i++){ //don't set it for the last two encoders
            sendEncoderShiftClickFunction(i, shiftClickFunction + " " + (i+1));            
        }
    }
    
    private void setClickAndTurnFunction(String shiftClickFunction){
        for(int i=0;i<NO_OF_ENCODERS;i++){
            sendEncoderPushTurnFunction(i, shiftClickFunction);            
        }
    }
}
