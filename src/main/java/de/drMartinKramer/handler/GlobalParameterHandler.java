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


import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.Transport;

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.MFT_Hardware;
import de.drMartinKramer.osc.OSC_GlobalParameterModule;
import de.drMartinKramer.support.MidiMessageWithContext;


public class GlobalParameterHandler extends AbstractCachingHandler{

    Transport transport = null;
	MasterTrack masterTrack = null;
	CursorTrack cursorTrack = null;
	Application application = null;
	/** We need to slow down the selection of the next/previous track by some delay. This here is the counter */
	private int moveTrackDelay = 0;
	/** delay the send of next patch messages */
	private int nextPatchDelay = 0;
	private int arrangerWindowDelay = 0;
	/** delay for changing the zoom level */
	private int zoomDelay = 0;

	private int loopStartDelay = 0;
	private int loopDurationDelay = 0; 

	private int bankSelectDelay = 0;
	private int programChangeDelay = 0;
	private int programChangeValue = 0;
	private int bankSelectMSB = 0;
	private int bankSelectLSB = 0;

	private static final Color COLOR_PLAYING = 		Color.fromRGB255(0, 255, 0);
	private static final Color COLOR_METRONOM = 	Color.fromRGB255(50, 200, 255);
	private static final Color COLOR_RECORD = 		Color.fromRGB255(193, 10, 10);
	private static final Color COLOR_FILL = 		Color.fromRGB255(170, 94, 20);
	private static final Color COLOR_LOOP = 		Color.fromRGB255(0, 20, 255);
	private static final Color COLOR_OVERDUP = 		Color.fromRGB255(170, 94, 20);

	private static final Color COLOR_BLUE = 		Color.fromRGB255(10, 10, 200);
	private static final Color COLOR_PURPLE = 	Color.fromRGB255(220, 0, 255);
	
	
    public GlobalParameterHandler(ControllerHost host)
    {
        super(host);
        this.transport = host.createTransport();
		this.masterTrack = host.createMasterTrack(0);
		this.cursorTrack = host.createCursorTrack ("GLOBAL_PARAMETER_CURSOR_TRACK", "MFT Global Parameter Cursor Track", 0, 0, true);
		this.application = host.createApplication();

        this.transport.tempo().value().addValueObserver(tempo->reactToTempoChange(tempo));
        this.transport.isPlaying().addValueObserver(isPlaying->reactToIsPlaying(isPlaying));  
        this.transport.isArrangerRecordEnabled().addValueObserver(isEnabled->reactToIsRecordEnabled(isEnabled)); 
        this.transport.isArrangerLoopEnabled().addValueObserver(isEnabled->reactToIsLoopEnabled(isEnabled));
		this.transport.isFillModeActive().addValueObserver(isEnabled->reactToIsFillModeEnabled(isEnabled));
        this.transport.isArrangerOverdubEnabled().addValueObserver(isEnabled->reactToIsOverdubEnabled(isEnabled));
		this.transport.isMetronomeEnabled().addValueObserver(isEnabled->reactToIsMetronomeEnabled(isEnabled));
		this.project.cueVolume().value().addValueObserver(volume->reactToCueVolumeChange(volume));
		this.masterTrack.volume().value().addValueObserver((newVolume)-> reactToMasterTrackVolumeChange(newVolume));
		this.transport.crossfade().value().addValueObserver((newValue) -> reactToCrossfadeValueChange(newValue));
		
		//Create a an OSC Module to send updates to the OSC surface
		this.oscModule = new OSC_GlobalParameterModule(host);

		//Set the color for the first two start Cursor encoder
		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,1,  COLOR_RECORD);
		
		//Initialize BankChange Color
		Color bankChangeColor = Color.fromRGB255(250,10,10);
		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 10, bankChangeColor);
		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 11, bankChangeColor);
		
		//and the tempo color
		Color tempoColor = Color.fromRGB255(50, 50, 255);
        setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,7,  tempoColor);

		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,8,  COLOR_FILL);
		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,9,  COLOR_FILL);

		//and some color for the view encoders in the last row
		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,12,  COLOR_BLUE);
		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,13,  COLOR_BLUE);
		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,14,  COLOR_BLUE);
		setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,15,  COLOR_BLUE);

        
     } //end of constructor
    
     /**
      * We react to a tempo change and change the color and the ring of the encoder accordingly. 
      * Note: we only show the range from 20 to 200bpm on the encoder
      * @param tempo the new tempo from 0-1 as a double. 0 means 20bpm, 1 means 666bpm
      */
    private void reactToTempoChange(double tempo)
    {
        int tempoAsInt = (int)Math.round(1+tempo/0.28*127);
		if(tempoAsInt>127)tempoAsInt=127;
		Color tempoColor = Color.fromRGB255(50, tempoAsInt*2, 240);
        setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,7,  tempoColor);
        setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_01,7, tempoAsInt);
    }
    

    private void reactToIsPlaying(boolean isPlaying)
    {
        if(isPlaying)
		{
			//set the first button to green
			setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,0,  COLOR_PLAYING);			
			//and have it flash in quarter notes (when the MFT receives midi clock)
			setEncoderSpecialFXCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 0, 6);
		}else 
		{	
			//turn off the first button
			setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,0,  COLOR_PLAYING); 
			//and reset the special FX, i.e. blinking of the LED  			
			setEncoderSpecialFXCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 0, 0);	
		}
    }
    private void reactToIsRecordEnabled(boolean isEnabled){
        if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,2,  COLOR_RECORD);            
        else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,2,  COLOR_PURPLE);  
    }

	private void reactToIsLoopEnabled(boolean isEnabled)
	{
		if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,3,  COLOR_LOOP);            
		else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,3,  COLOR_PURPLE);  
	}

	private void reactToIsFillModeEnabled(boolean isEnabled){
		if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,4,  COLOR_FILL);            
		else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,4,  COLOR_PURPLE);
	}	

	private void reactToIsOverdubEnabled(boolean isEnabled){
		if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,5,  COLOR_OVERDUP);            
		else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,5,  COLOR_PURPLE);
	}

	private void reactToIsMetronomeEnabled(boolean isEnabled){
		if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_07,6,  COLOR_METRONOM);            
		else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,6,  COLOR_PURPLE);
	}

	private void reactToCueVolumeChange(double volume){
		int volumeAsInt = (int)Math.round(volume*127);
		setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_01,6, volumeAsInt);
	}

	private void reactToMasterTrackVolumeChange(double volume){
		int volumeAsInt = (int)Math.round(volume*127);
		setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_01,9, volumeAsInt);
	}

	private void reactToCrossfadeValueChange(double value){
		int valueAsInt = (int)Math.round(value*127);
		setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_01,2, valueAsInt);
	}

	public boolean handleButtonClick(MidiMessageWithContext msg)
	{
		
		switch (msg.getData1()) //data1 contains the controller number, we use this to differentiate the different encoders
		{	
			case MFT_Hardware.MFT_BANK1_BUTTON_01:
				this.transport.play();	                
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_02:
				this.transport.stop();
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_03:
				this.transport.record();
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_04:
				println(msg.getEncoderID() + ", Loop Button Clicked: " + msg.isValidClick() + ", long:" + msg.isLongClick());
				this.transport.isArrangerLoopEnabled().toggle();	                
				return true;     
			case MFT_Hardware.MFT_BANK1_BUTTON_05:
				this.transport.isFillModeActive().toggle();	                
				return true;   
			case MFT_Hardware.MFT_BANK1_BUTTON_06:
				this.transport.isArrangerOverdubEnabled().toggle();      
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_07:
				this.transport.isMetronomeEnabled().toggle();          
				return true;  
			case MFT_Hardware.MFT_BANK1_BUTTON_08:
				this.transport.tapTempo();	                
				return true; 
			case MFT_Hardware.MFT_BANK1_BUTTON_09:
				this.application.previousProject();
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_10:
				this.application.nextProject();
				return true;  
			case MFT_Hardware.MFT_BANK1_BUTTON_11:
				this.application.activateEngine();					
				return true;  
			case MFT_Hardware.MFT_BANK1_BUTTON_12:
				//nothing happening here
				return true;                                                  
			case MFT_Hardware.MFT_BANK1_BUTTON_13:
				if(msg.isLongClick()) this.application.toggleDevices();
				else this.application.toggleInspector();
				return true;  
			case MFT_Hardware.MFT_BANK1_BUTTON_14:
				if(msg.isLongClick()) this.application.toggleMixer();
				else this.application.setPanelLayout(Application.PANEL_LAYOUT_ARRANGE);
				return true;  
			case MFT_Hardware.MFT_BANK1_BUTTON_15:
				if(msg.isLongClick()) this.application.toggleNoteEditor();
				else this.application.setPanelLayout(Application.PANEL_LAYOUT_MIX);
				return true; 			
			case MFT_Hardware.MFT_BANK1_BUTTON_16:
				if(msg.isLongClick()) this.application.toggleFullScreen() ;
				else this.application.setPanelLayout(Application.PANEL_LAYOUT_EDIT);					
				return true;  		
			default:	                
				return false; //no midi handled here
		}//switch
	} //end of handleButtonClick


	public boolean handleEncoderTurn(MidiMessageWithContext msg)
	{
		switch (msg.getData1()) 
		{
			// We receive relative values from the MFT, either 65 (if turned clockwise) or 63 if turned counterclockwise
			//thus, data2-64 gives us either +1 or -1 and we can use this value to increment (or decrement) the volum
			case MFT_Hardware.MFT_BANK1_BUTTON_01:
				this.transport.playStartPosition().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor());	
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_02: 
				this.transport.playStartPosition().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor()*0.1);					
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_03:                
				this.transport.crossfade().value().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 128);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_04:
				if(msg.isButtonCurrentlyDown()) {
					if(loopStartDelay++>4){
						loopStartDelay = 0;
						transport.arrangerLoopStart().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor()*0.5);
					}
				}else{
					if(loopDurationDelay++>4){
						loopDurationDelay = 0;
						transport.arrangerLoopDuration().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor()*0.5);
					}
				} 
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_05:                
				turnedEncoder(4, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_06:                
				turnedEncoder(5, msg);               
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_07: 
				this.project.cueVolume().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 127);               
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_08:    
				if(msg.isButtonCurrentlyDown()){
					//click&turn                        
					this.transport.tempo().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 10000);
				} else {                        
					this.transport.tempo().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 1000);
				}return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_09:                
				if(this.moveTrackDelay++ > 4) {
					if(msg.getData2()>=64) cursorTrack.selectNext();
					else cursorTrack.selectPrevious();
					this.moveTrackDelay = 0; //reset the delay
				}
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_10:                
				this.masterTrack.volume().value().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 128);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_11:
				//send program change message
				turnedEncoder(12, msg);
				//todo hier geht's weiter
				if(msg.isButtonCurrentlyDown()){ //Click turn: MSB
					if(bankSelectDelay++>4){
						bankSelectDelay = 0;
						if(msg.getData2()>64) bankSelectMSB++;
						else bankSelectMSB--;
						if(bankSelectMSB<0) bankSelectMSB = 0;
						if(bankSelectMSB>127) bankSelectMSB = 127;
						sendMidiToBitwig(0xB0, 0, bankSelectMSB);
						showPopupNotification("Bank Select MSB: " + bankSelectMSB);
					}	 
					
				} else {       //normal turn: LSB
					if(bankSelectDelay++>4){
						bankSelectDelay = 0;
						if(msg.getData2()>64) bankSelectLSB++;
						else bankSelectLSB--;
						if(bankSelectLSB<0) bankSelectLSB = 0;
						if(bankSelectLSB>127) bankSelectLSB = 127;
						sendMidiToBitwig(0xB0, 32, bankSelectLSB);
						setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_11, 10, bankSelectLSB);
						Color bankChangeColor = Color.fromRGB255(250,10,bankSelectLSB*2);
						setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 11, bankChangeColor);
						showPopupNotification("Bank Select LSB: " + bankSelectLSB);
					}
				}                   
					
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_12:   //send program change message				
				int changeSpeedup = msg.isButtonCurrentlyDown() ? 10 : 1;				
				if(programChangeDelay++>4){
					programChangeDelay = 0;
					if(msg.getData2()>64) programChangeValue+=changeSpeedup;
					else programChangeValue-=changeSpeedup;
					if(programChangeValue<0) programChangeValue = 0;
					if(programChangeValue>127) programChangeValue = 127;
					if(MFT_Configuration.isGlobalParameterBankSelectSend())
					{	//also send the MSB and LSB before sending the program change
						sendMidiToBitwig(0xB0, 0, bankSelectMSB);
						sendMidiToBitwig(0xB0, 32, bankSelectLSB);

					}
					sendMidiToBitwig(0xC0, programChangeValue, 0);
					setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 11, programChangeValue);
					Color prgmChangeColor = Color.fromRGB255(255,10,programChangeValue*2);
					setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 11, prgmChangeColor);
					showPopupNotification("Program Change: " + programChangeValue);
				}		
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_13:  
				if(zoomDelay++>4){
					zoomDelay = 0;
					if(msg.getData2()>64) application.zoomIn();
					else application.zoomOut();
				}
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_14:  
				if(arrangerWindowDelay++>4){
					arrangerWindowDelay = 0;
					//this.application.
				}
				turnedEncoder(14, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_15:                
				turnedEncoder(15, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_16:                
				if(nextPatchDelay++>4){
					nextPatchDelay = 0;
					if(msg.getData2()>64) sendMidiToBitwig(0xB0, 75, 127);
					else sendMidiToBitwig(0xB0, 76, 127);
				}
				return true;   
			default:
				return false; //false = no midi handled
		}
	} //end of handleEncoderTurn
	 
    
	private void turnedEncoder(int encoder, MidiMessageWithContext msg)
    {
        //println("Turned Encoder " + encoder + " to value " + msg.toString());
    }
}
