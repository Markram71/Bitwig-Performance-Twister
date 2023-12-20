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
import com.bitwig.extension.controller.api.Transport;

import de.drMartinKramer.hardware.MFT_Hardware;
import de.drMartinKramer.support.MFT_MidiMessage;

public class GlobalParameterHandler extends AbstractCachingHandler{

    Transport transport = null;

    public GlobalParameterHandler(ControllerHost host)
    {
        super(host);
        this.transport = host.createTransport();

        for(int i=0;i<16;i++)
        {
            
            setEncoderColorCached(i, i, 19+i);
            setEncoderRingValueCached(i, i, 50+i*2);
        }

        println("Global Parameter Handler initialized");
    }   

    public boolean handleMidi (MFT_MidiMessage msg)
	{   
		super.handleMidi(msg);//we first need to check for long clicks
		//check for CC message on channel 2 (which is here 1 and button clicked which is indicated by value (data2) = 127)
        if (msg.isControlChange() && msg.getChannel()==1 && msg.getData2()==0)
	    {
	        // Message came on Channel two (==1) -> CLICK ON THE ENCODER -> SELECT a TRACK *********
	        switch (msg.getData1()) //data1 contains the controller number, we use this to differentiate the different encoders
	        {
	            
	            case MFT_Hardware.MFT_BANK1_BUTTON_01:
	                clickedOnEncoder(0, msg);	                
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_02:
	            	clickedOnEncoder(1, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_03:
	            	clickedOnEncoder(2, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_04:
	            	clickedOnEncoder(3, msg);	                
	                return true;     
	            case MFT_Hardware.MFT_BANK1_BUTTON_05:
	            	clickedOnEncoder(4, msg);	                
	                return true;   
	            case MFT_Hardware.MFT_BANK1_BUTTON_06:
	            	clickedOnEncoder(5, msg);                
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_07:
	            	clickedOnEncoder(6, msg);                
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_08:
	            	clickedOnEncoder(7, msg);	                
	                return true; 
	            case MFT_Hardware.MFT_BANK1_BUTTON_09:
	            	clickedOnEncoder(8, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_10:
	            	clickedOnEncoder(9, msg);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_11:
	            	clickedOnEncoder(10, msg);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_12:
	            	clickedOnEncoder(11, msg);
	                return true;                                                  
	            case MFT_Hardware.MFT_BANK1_BUTTON_13:
	            	clickedOnEncoder(12, msg);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_14:
	            	clickedOnEncoder(13, msg);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_15:
	            	clickedOnEncoder(14, msg);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_16:
	            	clickedOnEncoder(15, msg);
	                return true;  
	            default:	                
	                return false; //no midi handled here
	        }
	    } else if (msg.isControlChange()  && msg.getChannel()==0)
	    {
	        // Message sent on channel 1 (==1) -> TURNED THE ENCODER *********
	        //this here is the case when we turn the encoder, i.e. a CC message on channel 1 (which is 0 here)
	        switch (msg.getData1()) 
	        {
	            // We receive relative values from the MFT, either 65 (if turned clockwise) or 63 if turned counterclockwise
	            //thus, data2-64 gives us either +1 or -1 and we can use this value to increment (or decrement) the volum
	            case MFT_Hardware.MFT_BANK1_BUTTON_01:
	                turnedEncoder(0, msg);					
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_02:                
	                turnedEncoder(1, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_03:                
	                turnedEncoder(2, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_04:
	                turnedEncoder(3, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_05:                
	                turnedEncoder(4, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_06:                
	                turnedEncoder(5, msg);               
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_07:                
	                turnedEncoder(6, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_08:                
	                turnedEncoder(7, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_09:                
	                turnedEncoder(8, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_10:                
	                turnedEncoder(9, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_11:                
	                turnedEncoder(10, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_12:                
	                turnedEncoder(11, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_13:                
	                turnedEncoder(12, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_14:                
	                turnedEncoder(13, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_15:                
	                turnedEncoder(14, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_16:                
	                turnedEncoder(15, msg);
	                return true;   
	            default:
	                return false; //false = no midi handled
	        }
	    }
	    return false; //we did not handle any incoming midi   
	}	//end of handleMidi

    private void clickedOnEncoder(int encoder, MFT_MidiMessage msg)
    {
        println("Clicked on Encoder " + encoder);
    }

    private void turnedEncoder(int encoder, MFT_MidiMessage msg)
    {
        println("Turned Encoder " + encoder + " to value " + msg.toString());
    }
}
