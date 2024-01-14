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

public class AbstractOSC_Handler implements IOSC_Handler{
    protected ControllerHost host = null;    

    public AbstractOSC_Handler(ControllerHost host){
        this.host = host;
    }

    public void refreshOSC_Surface(){
        //do nothing
    }

    public void setEncoderColor(int encoder, int color){
        //do nothing
    }
    
}
