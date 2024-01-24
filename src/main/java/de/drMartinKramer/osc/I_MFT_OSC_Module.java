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

import de.mossgrabers.controller.osc.protocol.OSCWriter;

/**
 * Interface for module which implement OSC handling for the Midi Fighter Twister.
 */
public interface I_MFT_OSC_Module {

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
    

    /**
     * Sets the name of the encoder, e.g. the track name in the mixer mode
     * @param encoderNo the number of the encoder (0-15)
     * @param name the new name of the encoder
     */
    public void setEncoderName(int encoderNo, String name);

    /**
     * Every handler is associated to a mode, e.g. a mixer. This function returns
     * the name of this mode
     * @return the name of the for which the handler is used for
     */
    public String getModeName();


     /**
     * Sets the selected state of an encoder. In some of our modes, an encoder can be selected, 
     * e.g. in the mixer mode, the currently selected encoder reflects the currently selected track.
     *
     * @param encoderNo The number of the encoder (0-15).
     * @param isSelected The selected state to set.
     */
    public void setEncoderSelected(int encoderNo, boolean isSelected);

    /**
     * Sets the active state of the specified encoder.
     *
     * @param encoderNo the number of the encoder
     * @param isActive  the active state of the encoder
     */
    public void setEncoderActive(int encoderNo, boolean isActive);

    /**
     * Message is used to inject an OSC writer (once it's ready) into this module
     * so that we can use this writer to send out OSC messages
     * @param writer
     */
    public void setOSC_Writer(OSCWriter writer);

}
