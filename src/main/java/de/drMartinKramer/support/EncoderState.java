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

public class EncoderState {
    
    private long downClickTime = 0;
    private final int encoderID;
    private boolean isCurrentlyClickedDown = false;
    private boolean lastClickWasLong = false;

    
    /*
     * Constructor. 
     * This constructor should beused when the encoderState is created as a response to the downclick 
     * @param encoderID the ID of the encoder
     */
    EncoderState(int encoderID){
        this.encoderID = encoderID;
        this.downClickTime = System.currentTimeMillis();
        this.isCurrentlyClickedDown = true;        
    }

    /**
     * Record that the encoder button is pressed down
     */
    public void clickDown(){
        this.downClickTime = System.currentTimeMillis();
        this.isCurrentlyClickedDown = true;
    }

    /**
     * Record that the encoder button was released
     */
    public void clickUp(){
        this.isCurrentlyClickedDown = false;
        this.downClickTime = 0;
    }

    public boolean isCurrentlyClickedDown(){
        return this.isCurrentlyClickedDown;
    }   

    /**
     * Returns true if the click was a long click
     * @return true if the click was a long click, false otherwise
     */
    public boolean isLongClick(){
        if(this.downClickTime==0)return false;
        return (System.currentTimeMillis() - this.downClickTime) > MFT_Configuration.getGlobalLongClickMillis();
    } 

    public void resetLongClick(){
        this.downClickTime =0;                
    }
    
    public int getEncoderID(){
        return this.encoderID;
    }   
    
}
