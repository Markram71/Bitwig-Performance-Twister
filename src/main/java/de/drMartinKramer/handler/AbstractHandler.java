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

package de.drMartinKramer.handler;


import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.NoteInput;
import com.bitwig.extension.controller.api.Project;
import com.bitwig.extension.controller.api.Transport;
import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.MFT_ColorMapper;
import de.drMartinKramer.hardware.MFT_Hardware;
import de.drMartinKramer.osc.AbstractMFT_OSC_Module;
import de.drMartinKramer.support.MidiMessageWithContext;
import de.mossgrabers.controller.osc.protocol.OSCWriter; 

public class AbstractHandler {

    protected ControllerHost host = null;
    protected Transport transport = null; 
    private MidiOut outPort = null; //let's make this private to channel all midi messages through the methods defined in this handler
    protected Project project = null; //get access to the project in Bitwig
    protected boolean isActive = false; //is this handler currently active or not   
    private boolean isShiftPressed = false; //is the shift button pressed or not 
    private boolean isShiftConsumed = false; //has the shift button been consumed or not
    private boolean isShiftPressed_left = false; //is the left shift button pressed or not 
    private boolean isShiftConsumed_left = false; //has the left shift button been consumed or not
    
    protected MidiIn midiIn = null; //the midi in port
    protected static NoteInput noteInput = null; //the note input port
    
    /** An OSC Handler to also update a GUI surface via OSC */
    protected AbstractMFT_OSC_Module oscModule = null;
   
    
    /**
     * Constructor. Takes the Bitwig 
     * @param host the Bitwig controller host
     */
    public AbstractHandler(ControllerHost host ){
        this.host = host;
        this.transport = host.createTransport();
        this.outPort = host.getMidiOutPort(0);
        this.project = host.getProject(); 
        this.midiIn = host.getMidiInPort(0);
        //the noteInput is used to pass CC message from the MFT's bank four into Bitwig
        if(AbstractHandler.noteInput==null) AbstractHandler.noteInput = midiIn.createNoteInput("Midi Fighter Twister", "B4????", "B6????");
    }

    
    /**
     * This message is called when an OSC writer is ready and we can inject it into this handler
     * @param writer
     */
    public void setOSC_Writer(OSCWriter writer){
        if (this.oscModule!=null)this.oscModule.setOSC_Writer(writer); 
    }

    /**
     * This method is called from BankHanlder to activate or deactivate this handler.
     * @param exists true if this handler should be active, false otherwise
     */
    public void setActive(boolean  newActiveState){
        this.isActive = newActiveState;
        if(this.oscModule!=null) this.oscModule.setActive(newActiveState);        
    }

    /**
     * If we have an OSC surface assiocated this method calls for an update of the surface
     */
    public void refreshOSC_Surface(){
        if(this.oscModule!=null) this.oscModule.refreshOSC_Surface();
    }

    protected boolean isActive(){
        return this.isActive;
    }

    protected boolean isShiftPressed(){
        return this.isShiftPressed;
    }   
       
    protected boolean isShiftConsumed(){
        return this.isShiftConsumed;
    }

    protected boolean isShiftPressed_left(){
        return this.isShiftPressed_left;
    }   
       
    protected boolean isShiftConsumed_left(){
        return this.isShiftConsumed_left;
    }

    public static void sendMidiToBitwig(int status, int data1, int data2){
         AbstractHandler.noteInput.sendRawMidiEvent(status, data1, data2);
    }


    /**
     * Convinience method to easily print to the Bitwig Console
     * @param msg
     */
    protected void println(String msg){
        host.println(msg);
    }

    /**
     * Convinience method to easily create a pop up message in Bitwig
     * @param msg the string message to be shown
     */
    protected void showPopupNotification(String msg){
        if(MFT_Configuration.showPupupNotifications()) host.showPopupNotification(msg);
    }

    /**
     * Convienience method to write an error to the Bitwig console (which is printed out in red)
     * @param msg message to be printed (in red)
     */
    protected void errorln(String msg){
        host.errorln(msg);
    }



    /**
     * Send a midi message to the MFT. We make this a private method, since we want to encapsulate the midi interface to the MFT
     * @param status Midi status 
     * @param data1 Midi data 1      
     * @param data2 Midi data 2 //the parameter value
     */
    protected void sendMidi(int status, int data1, int data2){
        outPort.sendMidi(status, data1, data2);
    }   

    /**
     * Set the color of an encoder
     * @param encoderBase the first encoder in this mode
     * @param encoderIndex which encoder should be set (0-15). This is added to encoderBase to address the final MFT encoder 
     * @param color the color of the encoder (as Bitwig-Color)
     */
    protected void setEncoderColor(int encoderBase, int encoderIndex, Color color){
        final int colorIndexB = MFT_ColorMapper.findNearestColorIndex(color);
        sendMidi(0xB1, encoderBase + encoderIndex, colorIndexB);
        if(this.oscModule!=null) this.oscModule.sendEncoderColor(encoderIndex, color);
    }


    /**
     * Sends a midi value on the special channel 3 which is used to change the brightness of the encoder
     * or change to a special effect
     * @param encoder the encoder ID
     * @param value which special value should be set, e.g. 17 for low brightness, see the MFT manual
     */
    protected void setEncoderSpecialColor(int encoder, int value){
        sendMidi(0xB2, encoder, value);
    }

    /**
     * Stes the encoder brightness
     * @param encoder ID of the encoder (0-127)
     * @param value from 0 (very low brightness) to 30 (highest brightness)
     */
    protected void setEncoderBrightness(int encoder, int value){
        assert (value >= 0 && value <= 30) : "Brightness value is not between 0 and 30";
        sendMidi(0xB2, encoder, MFT_Hardware.MFT_SPECIAL_ENCODER_COLOR_BRIGHTNESS_MESSAGE + value);        
    }
    

    /**
     * Update the encoder ring value (if this handler is active)
     * @param encoder for which encoder should there be a change (Encoder CC value)
     * @param value the value for the ring (0-127)
     */
    protected void setEncoderValue(int encoderBase, int encoderIndex, int value){
        if(this.isActive){
            sendMidi(0xB0, encoderBase+encoderIndex, value);
            if(this.oscModule!=null) this.oscModule.sendEncoderValue(encoderIndex, value);
        }        
    }

    /** 
     * Check if the incoming midi message for a click on a shift button. A shift button
     * is the encoder 16 on each bank. We also need to be careful, because we also want to 
     * use the shift button as a normal button for short clicks and long clicks. 
     * In case we want to use the shift button as shift button, we don't want to trigger the 
     * action which is set as a long or short click. So, in the actual handler implementation
     * we need to check if the Shift button has been "consumed". In that case, it was used as a 
     * shift button and we should not trigger the action for a short or long click.
     * In this method we only set the "consumed" flag.
     *  @param msg the incoming midi message      
     */
    private void checkShiftButton(MidiMessageWithContext msg){
        if(msg.isControlChange() && msg.getChannel()==1){ //we have a click on an encoder
            //if shift is already pressed and we have another encoder click, then the
            //then we "consume" the shift button, i.e. we actually use the shift button as a shift button
            //In case we consume the shift button, we should not use the button for a short or long click
            if(this.isShiftPressed && !msg.isShiftButton()) this.isShiftConsumed = true;  
            if(msg.isShiftButton())
                {
                    if(msg.getData2()==127) this.isShiftPressed = true;
                    else this.isShiftPressed = false;
                }              
        }        
    }

     /** 
     * Check if the incoming midi message for a click on a LEFT shift button. A LEFT shift button
     * is the encoder 13 on each bank. We also need to be careful, because we also want to 
     * use the shift button as a normal button for short clicks and long clicks. 
     * In case we want to use the shift button as shift button, we don't want to trigger the 
     * action which is set as a long or short click. So, in the actual handler implementation
     * we need to check if the Shift button has been "consumed". In that case, it was used as a 
     * left shift button and we should not trigger the action for a short or long click.
     * In this method we only set the "consumed" flag.
     *  @param msg the incoming midi message      
     */
    private void checkShiftButton_left(MidiMessageWithContext msg){
        if(msg.isControlChange() && msg.getChannel()==1){ //we have a click on an encoder
            //if shift is already pressed and we have another encoder click, then the
            //then we "consume" the shift button, i.e. we actually use the shift button as a shift button
            //In case we consume the shift button, we should not use the button for a short or long click
            if(this.isShiftPressed_left && !msg.isShiftButton_left()) this.isShiftConsumed_left = true;  
            if(msg.isShiftButton_left())
                {
                    if(msg.getData2()==127) this.isShiftPressed_left = true;
                    else this.isShiftPressed_left = false;
                }              
        }        
    }
    
    /**
     * Handle incoming midi message from the MFT
     * @param msg the enriched midi message
     * @return true if the message was handled, false otherwise
     */
    public boolean handleMidi (MidiMessageWithContext msg){
        boolean isHandled = false;
        checkShiftButton(msg);        
        checkShiftButton_left(msg);        
        
        if(isShiftConsumed()&&msg.isShiftButton())return true; //shift is consumed, so we do not need to do anything
        if(isShiftConsumed_left()&&msg.isShiftButton_left())return true; //shift is consumed, so we do not need to do anything
		
        if (msg.isButtonReleaseMessage() &&  msg.isValidClick()) isHandled = handleButtonClick(msg);
	    else if (msg.isEncoderTurnMessage()) isHandled =  handleEncoderTurn(msg);        
        
        if(msg.isShiftButton()&&msg.getData2()==0) this.isShiftConsumed = false;
        if(msg.isShiftButton_left()&&msg.getData2()==0) this.isShiftConsumed_left = false;

        return isHandled; //we did not handle any incoming midi        
    }
    
    /**
     * Should be overwritten by the concrete handler
     */
    public boolean handleEncoderTurn(MidiMessageWithContext msg){
        return false;
    }

    /**
     * Should be overwritten by the concrete handler
     * @param msg
     * @return
     */
    public boolean handleButtonClick(MidiMessageWithContext msg){
        return false;
    }   


    /**
     * Convienince method to send a global command to the MFT. 
     * @param encoderID which encoder should be effected
     * @param value the value for the  command
     */
    protected void sendMFT_Global_Command(int encoderID, int value){
        sendMidi(0xB3, encoderID, value);
    }

    protected void sendMidiCC(int channel, int cc, int value){
        sendMidi(0xB0+channel, cc, value);
    }
}
