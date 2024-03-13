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


import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.ControllerHost;
import de.mossgrabers.controller.osc.protocol.OSCWriter;


public abstract class AbstractMFT_OSC_Module {
    protected final ControllerHost host;
    protected boolean isActive = false; //it's super important that this is not active from the beginning

    public static final int NO_OF_ENCODERS = 16;
    
    //Constants that defined the different addresses for OSC message
    public static final String ENCODER_ADDRESS =            "/bpt_encoder/";
    public static final String COLOR_ADDRESS =              "color";
    public static final String VALUE_ADDRESS =              "value";
    public static final String EXISTS_ADDRESS =             "exists";
    public static final String NAME_ADDRESS =               "name";
    
    public static final String CLICK_FUNCTION_ADDRESS =      "clickFunction";
    public static final String LONG_CLICK_FUNCTION_ADDRESS =  "longClickFunction";
    public static final String SHIFT_CLICK_FUNCTION_ADDRESS =  "shiftClickFunction";
    
    public static final String TURN_FUNCTION_ADDRESS =       "turnFunction";
    public static final String PUSH_TURN_FUNCTION_ADDRESS =   "pushTurnFunction";  

    public static final String IS_SELECTED_ADDRESS =        "isSelected";  

    /** This is the OSC writer from MOSS. We can use this to send messages to an OSC surface */
    protected OSCWriter oscWriter = null;    

    protected class EncoderState {
        String name = "";
        int value = 0;
        Color color = Color.blackColor();
        String turnFunction = "";
        String pushTurnFunction = "";
        String clickFunction = "";
        String longClickFunction = "";
        String shiftClickFunction = ""; 
        boolean isSelected = false;
        boolean exists = false;
    }
    protected EncoderState[] encoderState = new EncoderState[NO_OF_ENCODERS];


    /**
     * Constructs a new instance of the AbstractMFT_OSC_Module class.
     * 
     * @param modeName the name of the mode
     * @param host the controller host
     */
    public AbstractMFT_OSC_Module(ControllerHost host){
        this.host = host;
        for (int i= 0;i<NO_OF_ENCODERS;i++){
            this.encoderState[i] = new EncoderState();
        }
    }
    
    /**
     * Method is called from the associated Handler to indicate if the Module is active or not.
     * When the module is not active, it should not send any Encoder Updates
     * When this module is activated, also the OSC Surface should be refreshed, i.e. 
     * refreshOSC Surface is called. 
     * @param active 
     */
    public void setActive(boolean active){
        this.isActive = active;
        if(isActive) refreshOSC_Surface(); 
    }

     /**
     * Message is used to inject an OSC writer (once it's ready) into this module
     * so that we can use this writer to send out OSC messages
     * @param writer
     */    
    public void setOSC_Writer(OSCWriter oscWriter){
        this.oscWriter = oscWriter;
    }

    // --- Gneral methods to write to OSC ----

    protected void sendOSC_Message(String address, String value, boolean dump){
        if(oscWriter!=null)oscWriter.sendOSC(address, value, dump);
    }
    protected void sendOSC_Message(String address, int value, boolean dump){
        if(oscWriter!=null)oscWriter.sendOSC(address, value, dump);
    }
    protected void sendOSC_Message(String address, boolean value, boolean dump){
        if(oscWriter!=null)oscWriter.sendOSC(address, value, dump);
        
    }
    protected void sendOSC_Message(String address, double value, boolean dump){
        if(oscWriter!=null)oscWriter.sendOSC(address, value, dump);        
    }
    protected void sendOSC_Color(String address, double red, double green, double blue, boolean dump){
        if(oscWriter!=null)oscWriter.sendOSCColor(address, red, green, blue, dump);         
    }


    /**
     * Sends an OSC message for an encoder with Encoder specific information. 
     * This will only be used for encoder message
     *
     * @param encoderNo The number of the encoder.
     * @param address The OSC Address of the encoder parameter
     * @param value The value to send.
     * @param dump Whether to dump the message.
     */
    protected void sendOSC_EncoderMessage(int encoderNo, String address, String value, boolean dump){
        if(isActive) sendOSC_Message(ENCODER_ADDRESS + (encoderNo+1) + "/"+  address, value, dump);
    }


    /**
     * Method is called to refresh the OSC surface. I.e. all update messages are sent via
     * OSC to the respective listener that should then redraw the surface
     * This method takes the values fromt the internal cache 
     */
    
    public void refreshOSC_Surface(){
        for (int i = 0;i<NO_OF_ENCODERS;i++){
            sendEncoderExists(i, encoderState[i].exists);
            sendEncoderName(i, encoderState[i].name);
            sendEncoderColor(i, encoderState[i].color);                        
            sendEncoderValue(i,  encoderState[i].value);
            sendEncoderTurnFunction(i,  encoderState[i].turnFunction);
            sendEncoderPushTurnFunction(i,  encoderState[i].pushTurnFunction);
            sendEncoderClickFunction(i,   encoderState[i].clickFunction);
            sendEncoderLongClickFunction(i,   encoderState[i].longClickFunction);
            sendEncoderShiftClickFunction(i,   encoderState[i].shiftClickFunction);
            sendEncoderSelected(i,  encoderState[i].isSelected);
        }
    }


    // ----- Set and Send OSC Messages for specific encoder functions -----
    

    /**
     * Sends the color of the encoder to the OSC surfacer
     * @param encoderNo the encoder No (0-15)
     * @param color the new color of the encoder
     */    
    public void sendEncoderColor(int encoderNo, Color color){
        this.encoderState[encoderNo].color = color;
        if(isActive) sendOSC_Color(ENCODER_ADDRESS + (encoderNo+1) + "/"+  COLOR_ADDRESS, color.getRed(), color.getGreen(), color.getBlue(), false);
    }

    /**
     * Send the value of the encoder to the OSC surface
     * @param encoderNo the number of the encoder (0-15)
     * @param value the new value of the encoder
     */
    public void sendEncoderValue(int encoderNo, int value){
        if(isActive) sendOSC_EncoderMessage(encoderNo, VALUE_ADDRESS , String.valueOf(value), false);
        this.encoderState[encoderNo].value = value;
    }

    /**
     * Sends the name of an encoder to the OSC surface 
     *
     * @param encoderNo The number of the encoder.
     * @param name The name of the encoder.
     */
    public void sendEncoderName(int encoderNo, String name){
        if(isActive) sendOSC_EncoderMessage(encoderNo, NAME_ADDRESS, name, false);
        this.encoderState[encoderNo].name = name;
    }
    

    /**
     * Sets the selected state of an encoder. In some of our modes, an encoder can be selected, 
     * e.g. in the mixer mode, the currently selected encoder reflects the currently selected track.
     *
     * @param encoderNo The number of the encoder (0-15).
     * @param isSelected The selected state to set.
     */
    public void sendEncoderSelected(int encoderNo, boolean isSelected){
        if(isActive) sendOSC_EncoderMessage(encoderNo, IS_SELECTED_ADDRESS , (isSelected ? "1" : "0"), false);
        this.encoderState[encoderNo].isSelected = isSelected;
    }    

    /**
     * Sets a message that tells the OSC surface if an encoder exists or not
     *
     * @param encoderNo the number of the encoder
     * @param exists  true if the encoder exists
     */
    
    public void sendEncoderExists(int encoderNo, boolean exists){
        if(isActive) sendOSC_EncoderMessage(encoderNo,  EXISTS_ADDRESS ,  (exists ? "1" : "0"), false);
        this.encoderState[encoderNo].exists = exists;
    }


    public void sendEncoderTurnFunction(int encoderNo,  String function){
        if(isActive) sendOSC_EncoderMessage(encoderNo,TURN_FUNCTION_ADDRESS, function, false);
        this.encoderState[encoderNo].turnFunction = function;

    }
    public void sendEncoderPushTurnFunction(int encoderNo, String function ){
        if(isActive) sendOSC_EncoderMessage(encoderNo, PUSH_TURN_FUNCTION_ADDRESS, function, false);
        this.encoderState[encoderNo].pushTurnFunction = function;

    }
    public void sendEncoderClickFunction(int encoderNo,  String function ){
        if(isActive) sendOSC_EncoderMessage(encoderNo, CLICK_FUNCTION_ADDRESS, function, false);
        this.encoderState[encoderNo].clickFunction = function;

    }
    public void sendEncoderLongClickFunction(int encoderNo, String function){
        if(isActive) sendOSC_EncoderMessage(encoderNo, LONG_CLICK_FUNCTION_ADDRESS, function, false);
        this.encoderState[encoderNo].longClickFunction = function;

    }
    public void sendEncoderShiftClickFunction(int encoderNo, String function  ){
        if(isActive) sendOSC_EncoderMessage(encoderNo, SHIFT_CLICK_FUNCTION_ADDRESS, function, false);
        this.encoderState[encoderNo].shiftClickFunction = function;
    }


     
}


