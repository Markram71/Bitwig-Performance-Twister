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

import java.util.LinkedList;

import com.bitwig.extension.controller.api.ControllerHost;

import de.drMartinKramer.MFT_Configuration;


public class OSC_MixerModule extends AbstractMFT_OSC_Module{

    
    public OSC_MixerModule(String name, ControllerHost host){
        super(name, host);
        

        for (int i= 0;i<NO_OF_ENCODERS;i++){
            this.encoderState[i] = new EncoderState();
            this.encoderState[i].clickFunction = "Select track";
            this.encoderState[i].turnFunction = "Track volume";
                        
            if(MFT_Configuration.isMixerLongButtonActionArm())          this.encoderState[i].longClickFunction = "Record arm";
            else if(MFT_Configuration.isMixerLongButtonActionMute())    this.encoderState[i].longClickFunction = "Mute";
            else if(MFT_Configuration.isMixerLongButtonActionSolo())    this.encoderState[i].longClickFunction = "Solo";
           
            if(MFT_Configuration.isMixerShiftClickActionCueMarker())    this.encoderState[i].shiftClickFunction = "Launch cue marker " + (i+1);
            else if(MFT_Configuration.isMixerShiftClickActionScene())   this.encoderState[i].shiftClickFunction = "Launch scene " + (i+1);
            
            if(MFT_Configuration.isMixerClickAdnTurnFunctionPan())                  this.encoderState[i].pushAndTurnFunction = "Pan";
            else if(MFT_Configuration.isMixerClickAdnTurnFunctionSend1())           this.encoderState[i].pushAndTurnFunction = "Send 1 level";
            else if(MFT_Configuration.isMixerClickAdnTurnFunctionTrackRemote1())    this.encoderState[i].pushAndTurnFunction = "Track remote 1";
            
        }
        //Override the last two encoder clicks as we need them for the shift button: 
        this.encoderState[NO_OF_ENCODERS-1].shiftClickFunction = "Shift button";
        this.encoderState[NO_OF_ENCODERS-2].shiftClickFunction = "Stop";
    } //end of constructor

    public void setLongClickFunction(String longClickFunction){
        for (int i = 0;i<NO_OF_ENCODERS;i++){
            this.encoderState[i].longClickFunction = longClickFunction;

        }
    }
    
    public void setShiftClickFunction(String shiftClickFunction){
        for(int i=0;i<NO_OF_ENCODERS-2;i++){
            this.encoderState[i].shiftClickFunction = shiftClickFunction + " " + (i+1);
            //Todo hier geht's weiter, die Nachricht an OSC senden
            sendOSC_Message(ENCODER + i + "/shiftClickFunction" ,  encoderState[i].shiftClickFunction, false);
        }
    }

    public void setEncoderName(int encoderNo, String encoderName){
        //todo: auch OSC informieren
        this.encoderState[encoderNo].name = encoderName;
        sendOSC_Message(ENCODER + encoderNo + "/name" ,  encoderState[encoderNo].name, false);
    }

    
    public String[] getSupportedCommands() {
        //Todo das müssen wir nochmal anschauen
        return new String[] { "name", "color", "value", "turnFunction", "pushAndTurnFunction", "clickFunction", "longClickFunction", "shiftClickFunction", "isSelected" };
    }

    public void execute(String command, LinkedList<String> path, Object value){
        //TODO hier geht's weiter 
    }

   


    
}
