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


import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.Project;
import com.bitwig.extension.controller.api.Transport;

import de.drMartinKramer.MFT_Configuration;

public class AbstractHandler {

    protected ControllerHost host = null;
    protected Transport transport = null; 
    private MidiOut outPort = null;
    protected Project project = null; //get access to the project in Bitwig

    //the following variables are used to detect a long click on an encoder
    private boolean longClicked = false; //store if the last click was a long click
    private int lastEncoder = -1; //which encoder was part of the last click
    private long downClickTime = -1; //store the time when the last click happened
    
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
        host.showPopupNotification(msg);
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
     * Here we handle incoming Midi from the MFT Controller. This abstract method 
     * should be called to handle long clicks, i.e. when a button is clicked and hold for a specified
     * time in order to trigger a second action with this button.
     * We will also check if the button is clicked and turned at the same time. 
     * The concrete handler implementation are freed from this task. 
     * @param msg the incoming mdid message from the MFT
     * @return true if we were able to handle the message, false if not
     */
    public boolean handleMidi (ShortMidiMessage msg){
        //first check for a downclick (which is the case when data2 is 127)
        if (msg.isControlChange() && msg.getChannel()==1 && msg.getData2()==127){
            lastEncoder = msg.getData1(); //store the number of the encoder has just been clicked
            downClickTime = System.currentTimeMillis(); //record when the click happened
        }else if (msg.isControlChange() && msg.getChannel()==1 && msg.getData2()==0){
            //now check for the upclick (which is the case when data2 is 0)
            if (lastEncoder == msg.getData1()){
                //we have a click on the same encoder as before
                long duration = System.currentTimeMillis() - downClickTime;
                if(duration > MFT_Configuration.getGlobalLongClickMillis()) longClicked = true; //we have a long click
                else longClicked = false;
            }else{
                //we have a click on a different encoder, so let's reset the last encoder
                lastEncoder = -1; //reset the last encoder
                longClicked = false;   
                downClickTime = -1;             
            }
        }        
        return false;
    }

    /**
     * Has the last click on an encoder been a long click or not
     * @return
     */
    public boolean isLongClicked(){
        return longClicked;
    }
    /**
     * Get the id of the last controller which has been clicked
     * @return ID of the last encoder
     */
    public int getLastEncoder(){
        return lastEncoder;
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
