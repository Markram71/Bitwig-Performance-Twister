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

import java.util.UUID;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceMatcher;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SpecificBitwigDevice;

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.MFT_Hardware;
import de.drMartinKramer.support.MFT_MidiMessage;

public class EQ_Handler  extends AbstractCachingHandler
{

    private final String BITWIG_EQ_PLUS_DEVICE_ID =  "e4815188-ba6f-4d14-bcfc-2dcb8f778ccb"; 
    private Device eqPlusDevice = null;
    SpecificBitwigDevice bitwigEQPlusDevice = null;
    Parameter[] gainParameter = null;
    Parameter[] frequencyParameter = null;
    Parameter[] qParameter = null;
    Parameter[] typeParameter = null;

    private int typeChangeDelay = 0; //we need to slow down the changes for the type Parameter
    //Some EQ Type constants: 
    private static final double EQ_TYPE_LOW_CUT_24_DB = 0.142;
    private static final double EQ_TYPE_HIGH_SHELF = 0.89;
    private static final double EQ_TYPE_BELL = 0.48;
    private static final double EQ_TYPE_OFF = 0.0;
    private final double[] eqBankCache = new double[4]; 

    // Color related constants
    private static final int COLOR_OFF = 0;    //the color when the band if off
    private static final int COLOR_FREQ_MIN = 88;  //the color for the lowest frequency
    private static final int COLOR_FREQ_MAX = 20;   //the color for the highest frequency

    public EQ_Handler(ControllerHost host) {
        super(host);
        CursorTrack cursorTrack = host.createCursorTrack(0, 0);
        DeviceBank eqPlusFilterDeviceBank = cursorTrack.createDeviceBank(1);
        UUID eqUUID = UUID.fromString(BITWIG_EQ_PLUS_DEVICE_ID);
        DeviceMatcher eqPlusFilterDeviceMatcher = host.createBitwigDeviceMatcher(eqUUID);
        eqPlusFilterDeviceBank.setDeviceMatcher(eqPlusFilterDeviceMatcher);

        this.eqPlusDevice = eqPlusFilterDeviceBank.getItemAt(0);
        eqPlusDevice.exists().addValueObserver((exists)-> reactToEQ_Exists(exists));
        
        this.bitwigEQPlusDevice = eqPlusDevice.createSpecificBitwigDevice(eqUUID);
        
        this.gainParameter = new Parameter[4];
        this.frequencyParameter = new Parameter[4];
        this.qParameter = new Parameter[4];
        this.typeParameter = new Parameter[4];

        for(int i=0;i<4;i++){
            final int column = i;             
            gainParameter[column] = bitwigEQPlusDevice.createParameter("GAIN"+(column+1)) ; 
            gainParameter[column].value().addValueObserver(newValue -> reactToGainChange(column, newValue));
            gainParameter[column].value().markInterested();
            frequencyParameter[column] = bitwigEQPlusDevice.createParameter("FREQ"+(column+1)) ; 
            frequencyParameter[column].value().addValueObserver(newValue -> reactToFrequencyChange(column, newValue));
            frequencyParameter[column].value().markInterested();
            qParameter[column] = bitwigEQPlusDevice.createParameter("Q"+(column+1)) ; 
            qParameter[column].value().addValueObserver(newValue -> reactToQ_Change(column, newValue));
            qParameter[column].value().markInterested();
            typeParameter[column] = bitwigEQPlusDevice.createParameter("TYPE"+(column+1)) ; 
            typeParameter[column].value().addValueObserver(newValue -> reactToTypeChange(column, newValue));
            typeParameter[column].value().markInterested();
        }
    }//Constructor

    
    private void reactToGainChange(int column, double newValue) {
        println("Gain Change: " + column + "to " + newValue);
        final int newIntValue = (int)Math.round(newValue * 127.0);
        setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_01 +  column,column, newIntValue);
        
    }
    private void reactToFrequencyChange(int column, double newValue) {
       final int newIntValue = (int)Math.round(newValue * 127.0);
       setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_05 +  column, 4+column,newIntValue); 
       setBandColor(column, newValue);      
    }

    private void reactToQ_Change(int column, double newValue) {
        final int newIntValue = (int)Math.round(newValue * 127.0);
        setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_09 +  column,8+column, newIntValue);
    }

    private void reactToTypeChange(int column, double newValue) {
        println("Type Change: " + column + "to " + newValue);
        final int newIntValue = (int)Math.round(newValue * 127.0);
        setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_13 +  column,12+column,  newIntValue);
        setBandColor(column);    //also set the color of the encoder
    }   

    private void reactToEQ_Exists(boolean exists) {
        println("EQ Exists: " + exists);
    }    

    private void setBandColor(int column, double frequency)
    {
        int newColor = 0;
        double bandType = typeParameter[column].value().get();
        if(bandType==EQ_TYPE_OFF) newColor = COLOR_OFF;
        else{
            newColor = (int)Math.round(COLOR_FREQ_MIN + frequency*(COLOR_FREQ_MAX-COLOR_FREQ_MIN));
        } 
        println("Set Band Color for band "+ column + ":" + bandType+  " to " + newColor   );

        setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01 +  column, column, newColor);
        setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_05 +  column, 4+column, newColor);
        setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_09 +  column, 8+column, newColor);
        setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_13 +  column, 12+column, newColor);
    }

    private void setBandColor(int column){
        setBandColor(column, frequencyParameter[column].value().get());
    }

    @Override
    public boolean handleMidi (MFT_MidiMessage msg)
	{   
		super.handleMidi(msg);//we first need to check for long clicks
		
        //then let's check if we actually have an EQ (or nor)
        if(!this.eqPlusDevice.exists().get()){
            println("No EQ device exists");
            return false;
            //we might want to create on.... //todo for later
        }

        //at this point we know we have an active EQ device
        //check for CC message on channel 2 (which is here 1 and button clicked which is indicated by value (data2) = 127)
	    if (msg.isControlChange() && msg.getChannel()==1 && msg.getData2()==0)
	    {
	        // Message came on Channel two (==1) -> CLICK ON THE ENCODER 
	        switch (msg.getData1()) //data1 contains the controller number, we use this to differentiate the different encoders
	        {   
	            case MFT_Hardware.MFT_BANK1_BUTTON_01:
	                toggleEQ_on_off(0);	                
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_02:
	            	toggleEQ_on_off(1);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_03:
	            	toggleEQ_on_off(2);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_04:
	            	toggleEQ_on_off(3);	                
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
	            	toggleEQType(0);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_14:
	            	toggleEQType(1);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_15:
	            	toggleEQType(2);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_16:
	            	toggleEQType(3);
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
	                changeGain(0, msg);					
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_02:                
	                changeGain(1, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_03:                
	                changeGain(2, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_04:
	                changeGain(3, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_05:                
	                changeFrequency(0, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_06:                
	                changeFrequency(1, msg);               
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_07:                
	                changeFrequency(2, msg); 
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_08:                
	                changeFrequency(3, msg); 
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_09:                
	                changeQ(0, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_10:                
	                changeQ(1, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_11:                
	                changeQ(2, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_12:                
	                changeQ(3, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_13:                
	                changeType(0, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_14:                
	                changeType(1, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_15:                
	                changeType(2, msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_16:                
	                changeType(3, msg);
	                return true;   
	            default:
	                return false; //false = no midi handled
	        }
	    }
	    return false; //we did not handle any incoming midi   
	}	//end of handleMidi

    private void clickedOnEncoder(int encoderNr, MFT_MidiMessage msg)
    {
        println("Clicked on encoder: " + encoderNr + "..." + msg.toString());
    }

    private void changeGain(int column, MFT_MidiMessage msg)
    {
        if(gainParameter[column]!=null){
            gainParameter[column].value().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 512);
        }     
    }

    private void changeFrequency(int column, MFT_MidiMessage msg)
    {
        if(frequencyParameter[column]!=null){
            frequencyParameter[column].value().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 512);
        }
    }
    private void changeQ(int column, MFT_MidiMessage msg)
    {
        if(qParameter[column]!=null){
            qParameter[column].value().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 512);
        }
    }
    private void changeType(int column, MFT_MidiMessage msg)
    {
       if(typeParameter[column]!=null){
            if(typeChangeDelay++>5){ //let's slow down the changes a bit
                typeParameter[column].value().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 512);
                typeChangeDelay=0;                
            }
            
        }
    }

    /**
     * This method returns a good default value for a given EQ band (0-3)
     * @param column which band or column of the MFT
     * @return a double value that reflects the EQ band type for Bitwig
     */
    private double getDefaultValueForBand(int column)
    {
        if(column == 1 || column == 2) return EQ_TYPE_BELL;
        if(column==0)return EQ_TYPE_LOW_CUT_24_DB;
        if(column==3)return EQ_TYPE_HIGH_SHELF;
        return EQ_TYPE_OFF;
    }

    /**
     * toggle the an EQ bank (1-4) on or off. This is called when an encoder in the last row is clicked. 
     * This is special because it always set a specific EQ type, see code below
     * @param column which EQ bank should be toggled (0-3)
     */
    private void toggleEQType(int column)
    {
        Parameter typeParameter = this.typeParameter[column];
        if(typeParameter!=null){
            if( typeParameter.value().get()>EQ_TYPE_OFF){
                typeParameter.value().set(EQ_TYPE_OFF); //toggle off                
            }else typeParameter.value().set(getDefaultValueForBand(column));                  
        }
    }
    /**
     * toggle the an EQ bank (1-4) on or off. This is called when an encoder in the last row is clicked. 
     * This is special because it always set a specific EQ type, see code below
     * @param column which EQ bank should be toggled (0-3)
     */
    private void toggleEQ_on_off(int column)
    {
        Parameter typeParameter = this.typeParameter[column];
        if(typeParameter!=null){
            double currentValue = typeParameter.value().get();
            double oldValue = eqBankCache[column];
            if(currentValue>EQ_TYPE_OFF) 
            {
                //the bank is active: 
                typeParameter.value().set(EQ_TYPE_OFF); //thus, switch it off 
                eqBankCache[column] = currentValue;     // and store the value from when it was on       
            }else{
                //the band is off, thus we need to switch it on again
                if(oldValue==EQ_TYPE_OFF) //but the old value is also off, so let's create a good default
                {
                    oldValue = getDefaultValueForBand(column);
                }
                typeParameter.value().set(oldValue); //thus, switch it on again with the value from the cache
            }                   
        }
    }


}
