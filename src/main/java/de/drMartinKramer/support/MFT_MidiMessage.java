/*
 * Copyright 2023 Dr. Martin Kramer
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
 * You should have received a copy of the GNU Lesser General Public License along with Twister
 * Sister. If not, see <https://www.gnu.org/licenses/>.
 *
 */


package de.drMartinKramer.support;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;

import de.drMartinKramer.hardware.MFT_Hardware;

public class MFT_MidiMessage {
    ShortMidiMessage msg = null;  //the core midi message from the MFT
    private int encoderID = -1; //the ID of the encoder that sent the message
    private boolean longClick = false; //true if the encoder was clicked for a long time
    private boolean isButtonCurrentlyDown = false; //true if the encoder button is currently down
    


    public MFT_MidiMessage(ShortMidiMessage msg){
        this.msg = msg;
         if(this.isEncoderMessage()){
            this.encoderID = this.msg.getData1();
        } else {
            this.encoderID = -1;
        }
    }

    public boolean isEncoderMessage(){
        if(this.msg.isControlChange() && (this.msg.getChannel() == 0 || this.msg.getChannel() == 1)) return true;
        return false;        
    }

    public boolean isButtonClickMessage(){
        if(this.msg.isControlChange() && this.msg.getChannel() == MFT_Hardware.MFT_BUTTON_CLICK_MIDI_CHANNEL) return true;
        return false;
    }

    public boolean isEncoderTurnkMessage(){
        if(this.msg.isControlChange() && this.msg.getChannel() == MFT_Hardware.MFT_ENCODER_TURN_MIDI_CHANNEL) return true;
        return false;
    }
    
    public boolean isGlobalMessage(){
        if(this.msg.isControlChange() && this.msg.getChannel() == MFT_Hardware.MFT_GLOBAL_MIDI_CHANNEL) return true;
        return false;
    } 

    public void setLongClick(boolean longClick){
        this.longClick = longClick;
    }
    public boolean isLongClick(){
        return this.longClick;
    }

    public int getEncoderID(){       
        return this.encoderID;
    }

    public  void setButtonCurrentlyDown(boolean isButtonCurrentlyDown){
        this.isButtonCurrentlyDown = isButtonCurrentlyDown;
    }      
    public boolean isButtonCurrentlyDown(){
        return this.isButtonCurrentlyDown;
    }    


    //*****  Access to the core Midi Message  */
    public boolean isControlChange(){
        return this.msg.isControlChange();
    }
    public int getData1(){
        return this.msg.getData1();
    }
    public int getData2(){
        return this.msg.getData2();
    }
    public int getChannel(){
        return this.msg.getChannel();
    }

    //******* SHIFT BUTTON methods */

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
