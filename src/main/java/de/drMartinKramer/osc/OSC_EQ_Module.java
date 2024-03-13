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
 
 public class OSC_EQ_Module extends AbstractMFT_OSC_Module{
 
    private static final String NO_FUNCTION = "---";
    /**
     * Constructor, sets the different labels for this OSC module
     * Which is used in the EQ Hanlder
     * @param host the controller host
     */ 
    public OSC_EQ_Module(ControllerHost host){
        super(host);        

        for (int i= 0;i<4;i++){ //go through four columns
            this.encoderState[i].turnFunction           = "Gain";
            this.encoderState[i+4].turnFunction         = "Frequency";
            this.encoderState[i+8].turnFunction         = "Q";
            this.encoderState[i+12].turnFunction        = "Type";

            this.encoderState[i].name                   = "Gain";
            this.encoderState[i+4].name                 = "Frequency";
            this.encoderState[i+8].name                 = "Q";
            this.encoderState[i+12].name                = "Type";

            this.encoderState[i].clickFunction          = "tgl. band on/off";
            this.encoderState[i+4].clickFunction        = NO_FUNCTION;
            this.encoderState[i+8].clickFunction        = NO_FUNCTION;
            this.encoderState[i+12].clickFunction       = "Tgl./res. type";

            this.encoderState[i].longClickFunction      = NO_FUNCTION;
            this.encoderState[i+4].longClickFunction    = NO_FUNCTION;
            this.encoderState[i+8].longClickFunction    = NO_FUNCTION;
            this.encoderState[i+12].longClickFunction   = NO_FUNCTION;

            this.encoderState[i].shiftClickFunction      = NO_FUNCTION;
            this.encoderState[i+4].shiftClickFunction    = NO_FUNCTION;
            this.encoderState[i+8].shiftClickFunction    = NO_FUNCTION;
            this.encoderState[i+12].shiftClickFunction   = NO_FUNCTION;

            this.encoderState[i].pushTurnFunction       = NO_FUNCTION;
            this.encoderState[i+4].pushTurnFunction     = NO_FUNCTION;
            this.encoderState[i+8].pushTurnFunction     = NO_FUNCTION;
            this.encoderState[i+12].pushTurnFunction    = NO_FUNCTION;  
            
            this.encoderState[i].isSelected         = false;
            this.encoderState[i+4].isSelected       = false;
            this.encoderState[i+8].isSelected       = false;
            this.encoderState[i+12].isSelected      = false;             
        }        

        this.encoderState[0].longClickFunction = "toggle EQ"; 
        this.encoderState[0].longClickFunction = "toggle window"; 
        
    } //end of constructor 
     
 }
 