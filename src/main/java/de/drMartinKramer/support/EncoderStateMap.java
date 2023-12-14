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

import java.util.HashMap;

/**
 * Simple convinience class to store the state of our encoders
 * Basically it's hashmap that stores encoder states
 *
 */
public class EncoderStateMap {
    private HashMap<Integer, EncoderState> encoderStateMap = null;

    public EncoderStateMap(){
        this.encoderStateMap = new HashMap<>();        
    }

    /**
     * Records a new click on an encoder
     * @param encoderID records that the encoder with this ID was clicked
     */
    public void encoderClickDown(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            this.encoderStateMap.get(encoderID).clickDown();            
        } else {
            this.encoderStateMap.put(encoderID, new EncoderState(encoderID));
        }
    }

    /**
     * Records a new click (up) on an encoder
     * @param encoderID records that the encoder with this ID was clicked
     */
    public void encoderClickUp(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            this.encoderStateMap.get(encoderID).clickUp();            
        } else {
            EncoderState newState = new EncoderState(encoderID);
            newState.clickUp();
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
            return this.encoderStateMap.get(encoderID).isCurrentlyClickedDown();
        } else {
            return false;
        }
    }


    public boolean isLongClick(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            return this.encoderStateMap.get(encoderID).isLongClick();
        } else {
            return false;
        }
    }

    /**
     * Message is called if the encoder is turned while beeing pressed down
     * In this case we don't have a long click any more
     * @param encoderID
     */
    public void resetLongClick(int encoderID){
        if (this.encoderStateMap.containsKey(encoderID)){
            this.encoderStateMap.get(encoderID).resetLongClick();
        }        
    }
}
