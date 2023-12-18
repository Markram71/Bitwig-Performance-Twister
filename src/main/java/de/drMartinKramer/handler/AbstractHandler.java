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

package de.drMartinKramer.handler;


import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.Project;
import com.bitwig.extension.controller.api.Transport;

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.support.MFT_MidiMessage;

public class AbstractHandler {

    protected ControllerHost host = null;
    protected Transport transport = null; 
    private MidiOut outPort = null;
    protected Project project = null; //get access to the project in Bitwig
    
    
    /**
     * Constructor. Takes the Bitwig 
     * @param host the Bitwig controller host
     */
    public AbstractHandler(ControllerHost host){
        this.host = host;
        this.transport = host.createTransport();
        this.outPort = host.getMidiOutPort(0);
        this.project = host.getProject();         
    }

    /**
     * Convinience method to easily print to the Bitwig Console
     * @param msg
     */
    protected void println(String msg){
        host.println(msg);
    }

    /**
     * Convinience method to easily create a pop up message in Bitwig
     * @param msg
     */
    protected void showPopupNotification(String msg){
        if(MFT_Configuration.showPupupNotifications()) host.showPopupNotification(msg);
    }

    /**
     * Convienience method to write an error to the Bitwig console (which is printed out in red)
     * @param msg message to be printed (in red)
     */
    protected void errorln(String msg){
        host.errorln(msg);
    }

    /**
     * Send a midi message to the MFT 
     * @param status Midi status 
     * @param data1 Midi data 1      
     * @param data2 Midi data 2 //the parameter value
     */
    protected void sendMidi(int status, int data1, int data2){
        outPort.sendMidi(status, data1, data2);
    }   

    /**
     * Set the color of an encoder
     * @param encoder which encoder should be set
     * @param color the color of the encoder (0-127)
     */
    protected void setEncoderColor(int encoder, int color){
        sendMidi(0xB1, encoder, color);
    }

    /**
     * Update the encoder ring value
     * @param encoder for which encoder should there be a change
     * @param value the value for the ring (0-127)
     */
    protected void updateEncoderRingValue(int encoder, int value){
        sendMidi(0xB0, encoder, value);
    }



    /**
     * Handle incoming midi message from the MFT
     * @param msg
     * @return
     */
    public boolean handleMidi (MFT_MidiMessage msg){
        return false;
    }

    /**
     * Convienince method to send a global command to the MFT. 
     * @param encoderID which encoder should be effected
     * @param value the value for the  command
     */
    protected void sendMFT_Global_Command(int encoderID, int value){
        sendMidi(0xB3, encoderID, value);
    }
}
