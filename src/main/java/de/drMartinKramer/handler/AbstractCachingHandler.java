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

import com.bitwig.extension.controller.api.ControllerHost; 
import de.drMartinKramer.hardware.MFT_Hardware;


/**
 * This is an AbstractHandler, but with a Caching functionality. It caches the values of the 
 * color and the ring position of the encoders. It can also restore the cached values. 
 *  
 * It collaborates with the BankHandler, which is responsible for activating and deactivating
 */
public class AbstractCachingHandler extends AbstractHandler{

    
    /** A cache for the color of the encoder */
    private int[] encoderColorCache = new int[MFT_Hardware.MFG_NUMBER_OF_ENCODERS];
    private int[] encoderSpecialFXCache = new int[MFT_Hardware.MFG_NUMBER_OF_ENCODERS];
    /** A cache for the ring status  */
    private int[] encoderRingCache = new int[MFT_Hardware.MFG_NUMBER_OF_ENCODERS];


    AbstractCachingHandler(ControllerHost host)
    {
        super(host);

        //Let's initialize the brightness cache to the highest brightness
        for(int i=0;i<MFT_Hardware.MFG_NUMBER_OF_ENCODERS;i++){
            this.encoderSpecialFXCache[i] = MFT_Hardware.MFT_SPECIAL_ENCODER_COLOR_BRIGHTNESS_MESSAGE + MFT_Hardware.MFT_SPECIAL_ENCODER_MAX_BRIGHTNESS ;
        }
    }

    
    /**
     * This method is called from BankHanlder to activate or deactivate this handler.
     * @param isActive true if this handler should be active, false otherwise
     */
    @Override
     public void setActive(boolean  newActiveState){
        if(this.isActive != newActiveState){ //the active mode is changing, so we should redraw the surface
            this.isActive = newActiveState;
            if(this.isActive)resetMFT_SurfaceAfterModeChange();
        }
    }

    /**
     * Sets the encoder color and caches the color so that we can easily restore it
     * @param encoder the Midi CC number of the encoder
     * @param encoderNumber the number of the encoder (0-15)
     * @param value new value for the color (0-127)
     */
    protected void setEncoderColorCached(int encoderID, int encoderNumber,int color){
        if(isActive) setEncoderColor(encoderID, color);
        this.encoderColorCache[encoderNumber] = color; //update the cache even when the handler is not active
    }

    /**
     * Sets the encoder brightness and caches the brighness so that we can easily restore it
     * @param encoder the Midi CC number of the encoder
     * @param encoderNumber the number of the encoder (0-15)
     * @param value brightness (0-30)
     */
    protected void setEncoderSpecialFXCached(int encoderID, int encoderNumber,int fxID){
        if(isActive)setEncoderSpecialColor(encoderNumber, fxID);
        this.encoderSpecialFXCache[encoderNumber] = fxID; //update the cache even when the handler is not active
    }

    /**
     * Sets the encoder ring value and caches the value so that we can easily restore it
     * @param encoderID the Midi CC number of the encoder
     * @param encoderNumber the number of the encoder (0-15)
     * @param value new value for the ring (0-127)
     */
    protected void setEncoderRingValueCached(int encoderID, int encoderNumber, int value){
        if(isActive) setEncoderRingValue(encoderID, value);
        /* we need to update the cache even if the handler is not active, since changes might occur 
         * in the background, e.g. a track is changed
        */ 
        this.encoderRingCache[encoderNumber] = value;        
    }


    /** 
     * Important function to reset the surface of the MFT after a mode change has happened. 
     * Usually we do not need to change anything, as in many cases the bank of the MFT is also changed. 
     * In these cases, the MFT automatically resets the surface (to the next internal MFT bank).
     * But in case an MFT bank is shared (e.g. between the mixer, the EQ, and the global parameters), 
     * we need to take care of the surface reset ourselves. 
     * 
     */
    private void resetMFT_SurfaceAfterModeChange(){
        for(int i=0;i<MFT_Hardware.MFG_NUMBER_OF_ENCODERS;i++){
            setEncoderColor(MFT_Hardware.MFT_BANK1_BUTTON_01 +  i, this.encoderColorCache[i]);
            setEncoderRingValue(MFT_Hardware.MFT_BANK1_BUTTON_01 +  i, this.encoderRingCache[i]);  
            setEncoderSpecialFXCached(MFT_Hardware.MFT_BANK1_BUTTON_01 + i, i,this.encoderSpecialFXCache[i]);          
        } 
    }

    
    
}
