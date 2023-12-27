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


import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.Transport;

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.MFT_Hardware;
import de.drMartinKramer.support.MFT_MidiMessage;

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
		
		
     } //end of constructor
    
     /**
      * We react to a tempo change and change the color and the ring of the encoder accordingly. 
      * Note: we only show the range from 20 to 200bpm on the encoder
      * @param tempo the new tempo from 0-1 as a double. 0 means 20bpm, 1 means 666bpm
      */
    private void reactToTempoChange(double tempo)
    {
        int tempoAsInt = (int)Math.round(1+tempo/0.28*125);
        tempoAsInt = tempoAsInt >125 ? 125 : tempoAsInt; //cut of values over 125
        setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_08,7,  tempoAsInt);
        setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_08,7, tempoAsInt);
    }
    

    private void reactToIsPlaying(boolean isPlaying)
    {
        if(isPlaying)
		{
			//set the first button to green
			setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,0,  50);			
			//and have it flash in quarter notes (when the MFT receives midi clock)
			setEncoderSpecialFXCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 0, 6);
		}else 
		{	
			//turn off the first button
			setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_01,0,  0); 
			//and reset the special FX, i.e. blinking of the LED  			
			setEncoderSpecialFXCached(MFT_Hardware.MFT_BANK1_BUTTON_01, 0, 0);	
		}
    }
    private void reactToIsRecordEnabled(boolean isEnabled){
        if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_03,2,  80);            
        else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_03,2,  0);  
    }

	private void reactToIsLoopEnabled(boolean isEnabled)
	{
		if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_04,3,  20);            
		else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_04,3,  0);  
	}

	private void reactToIsFillModeEnabled(boolean isEnabled){
		if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_05,4,  75);            
		else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_05,4,  0);
	}	

	private void reactToIsOverdubEnabled(boolean isEnabled){
		if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_06,5,  85);            
		else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_06,5,  0);
	}

	private void reactToIsMetronomeEnabled(boolean isEnabled){
		if(isEnabled)setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_07,6,  10);            
		else setEncoderColorCached(MFT_Hardware.MFT_BANK1_BUTTON_07,6,  0);
	}

	private void reactToCueVolumeChange(double volume){
		int volumeAsInt = (int)Math.round(volume*127);
		setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_07,6, volumeAsInt);
	}

	private void reactToMasterTrackVolumeChange(double volume){
		int volumeAsInt = (int)Math.round(volume*127);
		setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_04,3, volumeAsInt);
	}

	private void reactToCrossfadeValueChange(double value){
		int valueAsInt = (int)Math.round(value*127);
		setEncoderRingValueCached(MFT_Hardware.MFT_BANK1_BUTTON_03,2, valueAsInt);
	}

	public boolean handleButtonClick(MFT_MidiMessage msg)
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


	public boolean handleEncoderTurn(MFT_MidiMessage msg)
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
				this.masterTrack.volume().value().inc((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 128);
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
				turnedEncoder(10, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_11:                
				turnedEncoder(12, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_12:                
				turnedEncoder(13, msg);
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
					if(msg.getData2()>64) sendMidi(0xB0, 98, 127);
					else sendMidi(0xB0, 99, 127);
				}
				return true;   
			default:
				return false; //false = no midi handled
		}
	} //end of handleEncoderTurn
	 
    private void turnedEncoder(int encoder, MFT_MidiMessage msg)
    {
        //println("Turned Encoder " + encoder + " to value " + msg.toString());
    }
}
