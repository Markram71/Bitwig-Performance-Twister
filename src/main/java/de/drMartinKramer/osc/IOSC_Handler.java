/*
 * Copyright 2024 Dr. Martin Kramer
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

/**
 * Interface for module which implement OSC handling for the Midi Fighter Twister.
 */
public interface IOSC_Handler {

    /**
     * Method is called to refresh the OSC surface. I.e. all update messages are sent via
     * OSC to the respective listener that should then redraw the surface 
     */
    public void refreshOSC_Surface();

    /**
     * Set the color of an encoder
     * @param encoder which encoder should be set
     * @param color the color of the encoder (0-127)
     */
    public  void setEncoderColor(int encoder, int color);

    /**
     * Set the value of an encoder
     * @param encoder the number of the encoder (0-15)
     * @param value the new value of the encoder (0-127)
     */
    public void setEncoderValue(int encoder, int value);
    
}
