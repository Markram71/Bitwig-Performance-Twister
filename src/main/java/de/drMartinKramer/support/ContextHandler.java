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

import java.util.HashMap;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;

import de.drMartinKramer.MFT_Configuration;

/**
 * An object that manages the context of encoder of the MFT
 * For that it uses a hashMap to keep track of the different states that encoders can have. 
 * E.g. they might have been pushed down, they might have been turned after being pushed down, 
 * they might have been pressed down for longer to create a long click.
 * This is important in order to create different types of clicks and turns for the Midi Figher Twister 
 *
 */
public class ContextHandler {

    //***** First we need a private inner class for storing information about encoders */
            private class EncoderState {
    
                /** When did the user click down an encoder, 0 means it currently not clicked down */
                long downClickTime = 0;
                /** Not every first turn of the encoder will reset the long click*/
                int turnMessageCounter = 0;
                /** the state if the encoder, e.g. if it's clicked down or not*/
                boolean isCurrentlyClickedDown = false;   
                /** We can invalid a click by turning the know when click down, then we don't want consume the click any more */
                boolean isValidClick = true;
                /*
                * This constructor should beused when the encoderState is created as a response to the downclick 
                * @param encoderID the ID of the encoder
                */
                EncoderState(int encoderID){
                    this.downClickTime = System.currentTimeMillis();
                    this.isCurrentlyClickedDown = true;  
                    this.turnMessageCounter = 0;             
                }

                /**
                 * Record that the encoder button is pressed down
                 */
                public void setEncoderClickDown(){
                    this.downClickTime = System.currentTimeMillis();
                    this.isCurrentlyClickedDown = true;
                    this.turnMessageCounter = 0;
                    this.isValidClick = true;
                }

                /**
                 * Record that the encoder button was released
                 */
                public void recordButtonReleased(){
                    this.isCurrentlyClickedDown = false;
                    this.downClickTime = 0;
                    this.turnMessageCounter = 0;
                }

                public boolean isEncoderCurrentlyClickedDown(){
                    return this.isCurrentlyClickedDown;
                }   

                /**
                 * Calculates the time between the first down click and a subsequent release of the encoder button
                 * @return true if the click was a long click, false otherwise
                 */
                public boolean isEncoderLongClick(){
                    if(this.downClickTime==0)return false;
                    return (System.currentTimeMillis() - this.downClickTime) > MFT_Configuration.getGlobalLongClickMillis();
                } 

                public void invalidateClickByTurn(){
                    if(this.turnMessageCounter++>4){
                        this.downClickTime =0; 
                        this.turnMessageCounter = 0;
                        this.isValidClick = false;
                    }             
                }     
                
                public boolean isValidClick(){
                    return this.isValidClick;
                }   
            }//end of private inner class EncoderState       
                
    
    
    
    //**************************************************** */
    
    /**
     * This Hashmap is at the core of this context handler. It stores records of the EncoderState
     * to keep track of the different states that encoders can have.
     */
    private HashMap<Integer, EncoderState> encoderStateMap = null;

    public ContextHandler(){
        this.encoderStateMap = new HashMap<>();        
    }

    /**
     * Records a new click on an encoder
     * @param encoderID records that the encoder with this ID was clicked
     */
    public void recordEncoderClickDown(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            this.encoderStateMap.get(encoderID).setEncoderClickDown();            
        } else {
            this.encoderStateMap.put(encoderID, new EncoderState(encoderID));
        }
    }

    /**
     * Records a new click (up) on an encoder
     * @param encoderID records that the encoder with this ID was clicked
     */
    public void recordButtonReleased(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            this.encoderStateMap.get(encoderID).recordButtonReleased();            
        } else {
            EncoderState newState = new EncoderState(encoderID);
            newState.recordButtonReleased();
            this.encoderStateMap.put(encoderID, newState);
        }
    }

    /**
     * Methods returns true if the encoder specified by the encoderID is currently pressed down
     * @param encoderID ID of the encoder to be checked
     * @return
     */
    public boolean isEncoderCurrentlyClickedDown(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            return this.encoderStateMap.get(encoderID).isEncoderCurrentlyClickedDown();
        } else {
            return false;
        }
    }


    public boolean isLongClick(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            return this.encoderStateMap.get(encoderID).isEncoderLongClick();
        } else {
            return false;
        }
    }

    public boolean isValidClick(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            return this.encoderStateMap.get(encoderID).isValidClick();
        } else {
            return true;
        }
    }

    /**
     * Message is called if the encoder is turned while beeing pressed down
     *  In this case we don't have a long click any more, but we don't swith directly, so there is 
     * a bit of buffer to let a few turn messagees come through. 
     * 
     * @param encoderID
     */
    public void resetLongClickByTurn(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){            
            this.encoderStateMap.get(encoderID).invalidateClickByTurn();
        }        
    }


    /**
    * Important and a bit complex method in order to create a new MFT-specific Midi message that 
    * takes the current state of the encoder into account. I.e. it could be that a prior message
    * would invalid a current click message. In that specific case it could be that the encoder was 
    * held down and the encoder was turned. 
    * The main task of this method is to set up the new Midi message (with context) from the 
    * information we have stored in the Hashmap.
    * @param msg The incoming Midi message from the Midi Figther Twister
    * @return a MFT-specific midi message that contains further information on the context
    */
   public MidiMessageWithContext createMidiMessageWithContext(ShortMidiMessage incomingMessage) {
      MidiMessageWithContext msg = new MidiMessageWithContext(incomingMessage); 
      final int encoderID = msg.getEncoderID();
      if(msg.isButtonClickDownMessage())recordEncoderClickDown(encoderID); //click down
      else if(msg.isButtonReleaseMessage())
      {
            msg.setLongClick( isLongClick(encoderID));
            if(!isValidClick(encoderID)) msg.invalidateClick(); 
            recordButtonReleased(encoderID); //button released
       
      } else if(msg.isEncoderTurnMessage())
      {
         if(isEncoderCurrentlyClickedDown(encoderID)) {
            msg.setButtonCurrentlyDown(true);
            resetLongClickByTurn(encoderID);                        
         }
         else msg.setButtonCurrentlyDown(false);
      }
      return msg;
   }
}
