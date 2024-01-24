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

import de.mossgrabers.controller.osc.protocol.OSCWriter;


public abstract class AbstractMFT_OSC_Module implements I_MFT_OSC_Module{
    protected final String modeName;  
    protected final ControllerHost host;
    
    public static final int NO_OF_ENCODERS = 16;
    public static final String ENCODER = "/encoder/";

    protected OSCWriter oscWriter = null;
    

    protected class EncoderState {
        String name = "";
        int value = 0;
        int color = 0;
        String turnFunction = "";
        String pushAndTurnFunction = "";
        String clickFunction = "";
        String longClickFunction = "";
        String shiftClickFunction = ""; 
        boolean isSelected = false;
        boolean isActive = false;
    }
    protected EncoderState[] encoderState = new EncoderState[NO_OF_ENCODERS];


    public AbstractMFT_OSC_Module(String modeName, ControllerHost host){
        this.modeName = modeName;
        this.host = host;
    }

    public void setOSC_Writer(OSCWriter oscWriter){
        this.oscWriter = oscWriter;
    }

    protected void sendOSC_Message(String address, String value, boolean dump){
        oscWriter.sendOSC(address, value, dump);
    }
    protected void sendOSC_Message(String address, int value, boolean dump){
        oscWriter.sendOSC(address, value, dump);
    }
    protected void sendOSC_Message(String address, boolean value, boolean dump){
        oscWriter.sendOSC(address, value, dump);
        
    }
    protected void sendOSC_Message(String address, double value, boolean dump){
        oscWriter.sendOSC(address, value, dump);        
    }
    protected void sendOSC_Color(String address, double red, double green, double blue, boolean dump){
        oscWriter.sendOSCColor(address, red, green, blue, dump);        
    }


    

    /** {@inheritDoc} */
    public void refreshOSC_Surface(){
        sendOSC_Message("modeName/", this.modeName, false);
        for (int i = 0;i<NO_OF_ENCODERS;i++){
            sendOSC_Message(ENCODER + i + "/name", encoderState[i].name, false);
            sendOSC_Message(ENCODER + i + "/color", encoderState[i].color, false);                        
            sendOSC_Message(ENCODER + i + "/value",  encoderState[i].value, false);
            sendOSC_Message(ENCODER + i + "/turnFunction",  encoderState[i].turnFunction, false);
            sendOSC_Message(ENCODER + i + "/pushAndTurnFunction", encoderState[i].pushAndTurnFunction, false);
            sendOSC_Message(ENCODER + i + "/clickFunction",  encoderState[i].clickFunction, false);
            sendOSC_Message(ENCODER + i + "/longClickFunction",  encoderState[i].longClickFunction, false);
            sendOSC_Message(ENCODER + i + "/shiftClickFunction",  encoderState[i].shiftClickFunction, false);
            sendOSC_Message(ENCODER + i + "/isSelected",  (encoderState[i].isSelected ? "1":"0"), false);
        }
    }

    /** {@inheritDoc} */    
    public void setEncoderColor(int encoderNo, int color){
        sendOSC_Message(ENCODER + encoderNo +  "/color",  color, false);
        this.encoderState[encoderNo].color = color;
    }

    /** {@inheritDoc} */
    public void setEncoderValue(int encoderNo, int value){
        sendOSC_Message(ENCODER + encoderNo + "/value" , value, false);
        this.encoderState[encoderNo].value = value;
    }

    /** {@inheritDoc} */
    public void setEncoderName(int encoderNo, String name){
        sendOSC_Message(ENCODER + encoderNo + "/name",  name, false);
        this.encoderState[encoderNo].name = name;
    }
    
    /** {@inheritDoc} */
    public String getModeName(){
        return this.modeName;
    }

    /** {@inheritDoc} */
    public void setEncoderSelected(int encoderNo, boolean isSelected){
        sendOSC_Message(ENCODER + encoderNo + "/isSelected" , (isSelected ? "1" : "0"), false);
        this.encoderState[encoderNo].isSelected = isSelected;
    }    

    /** {@inheritDoc} */
    public void setEncoderActive(int encoderNo, boolean isActive){
        sendOSC_Message(ENCODER + encoderNo + "/isActive" ,  (isActive ? "1" : "0"), false);
        this.encoderState[encoderNo].isActive = isActive;
    }
}
