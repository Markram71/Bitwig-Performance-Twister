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

import de.drMartinKramer.MFT_Configuration;

/**
 * Simple convinience class to store the state of our encoders
 * This class is basically a record and it's only used by the EncoderStateMap. 
 * The EncoderStateMap stores objects of this class in it's hashmap to manage that state of the encoders. 
 * E.g. it's important to remember is an encoder was clicked before it's turned or not. /**
 * The time when the down click occurred.
 */

 
public class EncoderState {
    
    /** When did the user click down an encoder, 0 means it currently not clicked down */
    private long downClickTime = 0;

    /** Not every first turn of the encoder will reset the long click*/
    private int turnMessageCounter = 0;

    /** the state if the encoder, e.g. if it's clicked down or not*/
    private boolean isCurrentlyClickedDown = false;
   
    /** We can invalid a click by turning the know when click down, then we don't want consume the click any more */
    private boolean isValidClick = true;

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
    public void clickDown(){
        this.downClickTime = System.currentTimeMillis();
        this.isCurrentlyClickedDown = true;
        this.turnMessageCounter = 0;
        this.isValidClick = true;
    }

    /**
     * Record that the encoder button was released
     */
    public void clickUp(){
        this.isCurrentlyClickedDown = false;
        this.downClickTime = 0;
        this.turnMessageCounter = 0;
    }

    public boolean isCurrentlyClickedDown(){
        return this.isCurrentlyClickedDown;
    }   

    /**
     * Calculates the time between the first down click and a subsequent release of the encoder button
     * @return true if the click was a long click, false otherwise
     */
    public boolean isLongClick(){
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

    
}
