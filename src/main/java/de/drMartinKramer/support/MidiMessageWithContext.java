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


package de.drMartinKramer.support;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;

import de.drMartinKramer.hardware.MFT_Hardware;

/**
 * This class represents Midi message that have additional context information. E.g. We need to know
 * if an encoder of the MFT has been clicked before it was turned. There this is a basic 
 * ShortMidiMessage (the superclass) with some additional information.
 */
public class MidiMessageWithContext extends ShortMidiMessage{
    private boolean longClick = false; //true if the encoder was clicked for a long time
    private boolean isButtonCurrentlyDown = false; //true if the encoder button is currently down
    private boolean isValidClick = true; //true if the click is valid, e.g. if the encoder was not turned after the click down


    public MidiMessageWithContext(final int status, final int data1, final int data2){
        
        super(status,data1,data2);                 
    }

    public MidiMessageWithContext(ShortMidiMessage msg){
        super(msg.getStatusByte(), msg.getData1(), msg.getData2());        
    }

    /**
     * Returns the encoder ID in case this message is sent by an encoder
     * @return the ID of the encoder (in case it's an encoder ID) or -1 otherwise
     */
    public int getEncoderID(){       
        if(isEncoderMessage()) return getData1();
        else return -1;
    }

    /**
     * The encoderIndex is different from the encoderID. While the encoderID is the 
     * specific ID (data1) of the encoder midi message and can have any number between 0 and 127, the 
     * encoderIndex is the number of the MFT encoder in the range of 0 to 16.  
     * @return
     */
    public int getEncoderIndex(){
        final int encoderID = getEncoderID();
        if(isEncoderMessage()){
            if      (encoderID >= MFT_Hardware.MFT_BANK1_BUTTON_01 && encoderID <= MFT_Hardware.MFT_BANK1_BUTTON_16) return encoderID - MFT_Hardware.MFT_BANK1_BUTTON_01;
            else if (encoderID >= MFT_Hardware.MFT_BANK2_BUTTON_01 && encoderID <= MFT_Hardware.MFT_BANK2_BUTTON_16) return encoderID - MFT_Hardware.MFT_BANK2_BUTTON_01;
            else if (encoderID >= MFT_Hardware.MFT_BANK3_BUTTON_01 && encoderID <= MFT_Hardware.MFT_BANK3_BUTTON_16) return encoderID - MFT_Hardware.MFT_BANK3_BUTTON_01;
            else if (encoderID >= MFT_Hardware.MFT_BANK4_BUTTON_01 && encoderID <= MFT_Hardware.MFT_BANK4_BUTTON_16) return encoderID - MFT_Hardware.MFT_BANK4_BUTTON_01;
            else return -1;

        }else return -1;
    }


    /**
     * We can differentiate encoder messages from other message by looking at the channel on which the message is sent.
     * @return true if this is a message that is sent on an encoder channel
     */
    public boolean isEncoderMessage(){
        if(isControlChange() && (getChannel() == 0 || getChannel() == 1)) return true;
        else return false;        
    }

    /**
     * Is this a message that is sent because an encoder was clicked down 
     * @return true if an encoder was clicked down, false otherwise
     */
    public boolean isButtonClickMessage(){
        if(isControlChange() && getChannel() == MFT_Hardware.MFT_BUTTON_CLICK_MIDI_CHANNEL) return true;
        else return false;
    }

    /**
     * Returns true if this is a message that represents a button up (or release) message 
     * @return
     */
    public boolean isButtonReleaseMessage(){
        if(isButtonClickMessage() && getData2() == 0) return true;
        else return false;
    }

    /**
     * Returns true if this is a message that represents a button click down message 
     * @return
     */
    public boolean isButtonClickDownMessage(){
        if(isButtonClickMessage() && getData2() == 127) return true;
        else return false;
    }

    /**
     * Is this a encoder turn message
     * @return true if the encoder was turned, false otherwise
     */
    public boolean isEncoderTurnMessage(){
        if(isControlChange() && getChannel() == MFT_Hardware.MFT_ENCODER_TURN_MIDI_CHANNEL) return true;
        else return false;
    }
    
    /**
     * The Midi Fighter Twister can also send global messages, e.g. if a bank was changed.
     * @return true if this is a global message, false otherwise
     */
    public boolean isGlobalMessage(){
        if(isControlChange() && getChannel() == MFT_Hardware.MFT_GLOBAL_MIDI_CHANNEL) return true;
        return false;
    } 

    // **** Methods that handle CONTEXT information for this Midi message ****

    /**
     * Sets the long click flag
     * @param longClick true, if the encoder was clicked some some duration already, false otherwise
     */
    public void setLongClick(boolean longClick){
        this.longClick = longClick;
    }

    /**
     * Returns true if the encoder was clicked for a long time
     * @return true if the encoder was clicked for a long time, false otherwise
     */
    public boolean isLongClick(){
        return this.longClick;
    }


    /**
     * Sometimes we need to check if the encoder is currently hold down, e.g. if it was clicked and then 
     * subsequently turned.
     * @param isButtonCurrentlyDown set the flag that indicated if the button is currently clicked down
     */
    public  void setButtonCurrentlyDown(boolean isButtonCurrentlyDown){
        this.isButtonCurrentlyDown = isButtonCurrentlyDown;
    }      

    /**
     * Returns true if the encoder button is currently clicked down
     * @return true if the encoder button is currently clicked down, false otherwise
     */
    public boolean isButtonCurrentlyDown(){
        return this.isButtonCurrentlyDown;
    }    

    /**
     * Only valid messages should be handled
     * @return true if this is a valid midi message, false otherwise. 
     */
    public boolean isValidClick(){
        return this.isValidClick;
    }

    /**
     * This message is called to invalidate a midi message, i.e. this message should not be handled any more. 
     * A reason for this is that an encoder was clicked down, then turned and is then released. 
     * In this case the resulting click up message should not be handled any more. Reason: The user
     * wanted to perform a click turn and then there should not be another click anymore. 
     */
    public void invalidateClick(){
        this.isValidClick = false;
    }


    /**
     * returns true if this message is triggered by a click on a potential shift button
     * @return true if the 16th encoder is clicked or released 
     */
    public boolean isShiftButton(){
        if(isButtonClickMessage()) {
            final int encoderId = this.getData1();
            if(encoderId == MFT_Hardware.MFT_BANK1_BUTTON_16 ||
                encoderId == MFT_Hardware.MFT_BANK2_BUTTON_16 ||
                encoderId == MFT_Hardware.MFT_BANK3_BUTTON_16 ||
                encoderId == MFT_Hardware.MFT_BANK4_BUTTON_16)
                {
                    return true;
                }              
        }
        return false;
    }


}
