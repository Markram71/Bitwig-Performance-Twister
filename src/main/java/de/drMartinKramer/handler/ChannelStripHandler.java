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
 *  */

package de.drMartinKramer.handler;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CueMarkerBank;
import com.bitwig.extension.controller.api.RemoteControlsPage;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.*;
import de.drMartinKramer.support.MidiMessageWithContext;

public class ChannelStripHandler extends AbstractHandler
{
	public static final int SIZE_OF_SEND_BANK = 4;
	
	private CursorTrack cursorTrack = null;
	private boolean isMuted = false;
	private boolean isSolo = false;
	private boolean isArmed = false;
	private int moveTrackDelay = 0;
	private boolean isPinned = false; //is the cursortrack pinned or not?
	private MasterTrack masterTrack = null;
	private RemoteControlsPage trackRemoteControlsPage = null;
	private static final int COLOR_PINK = 100; 

	//for shift clicks we need access to the sceneBank or a cue marker bank
	private SceneBank sceneBank = null;
	private CueMarkerBank cueMarkerBank = null;
	
	public  ChannelStripHandler(ControllerHost host) 
	{
		super(host);
		//the cursorTrack will help us to have access to the 
		this.cursorTrack = host.createCursorTrack ("CHANNEL_STRIP_CURSOR_TRACK", "MFT Channel Strip Cursor Track", SIZE_OF_SEND_BANK, 0, true);
		this.cursorTrack.solo ().markInterested ();
	    this.cursorTrack.mute ().markInterested ();
	    this.cursorTrack.arm ().markInterested ();
	    
	    //we are also interested in the SENDs of this channel: 
	    for (int i= 0;i<SIZE_OF_SEND_BANK;i++) {	    	
	    	this.cursorTrack.sendBank().getItemAt(i).markInterested(); //we are interested to change this value
	    	this.cursorTrack.sendBank().getItemAt(i).value().markInterested();
	    	final int sendIndex = i;
	    	//this is creates a callback for the send value of a track
		    this.cursorTrack.sendBank().getItemAt(i).value().addValueObserver((newValue)->reactToTrackSendChange(sendIndex, newValue));	
		    
			//and this here creates the callback for the indication if the send channel actually exists (or not)
		    this.cursorTrack.sendBank().getItemAt(i).exists().addValueObserver((sendExists)-> reactToTrackSendExists(sendIndex, sendExists));		    
			this.cursorTrack.sendBank().getItemAt(i).isEnabled().addValueObserver((isEnabled) -> reactToTrackSendEnabled(sendIndex,isEnabled));
		}	    
	    
	    //get the status of the current track
	    this.isMuted = this.cursorTrack.mute().get();
	    this.isSolo = this.cursorTrack.solo().get();
	    this.isArmed = this.cursorTrack.arm().getAsBoolean();  //the arm flag
	    
	    //add an observer to the Solo flag of the currently selected track
	    this.cursorTrack.solo().addValueObserver((toggleValue) -> setSoloValue(toggleValue));
	    this.cursorTrack.mute().addValueObserver((toggleValue) -> setMutedValue(toggleValue)); //and the same for the Muted flag
		this.cursorTrack.arm().addValueObserver((toggleValue) -> setArmedValue(toggleValue));  //also for the armed flag
	    
	    this.cursorTrack.volume().value().addValueObserver((newVolume) -> reactToTrackVolumeChange(newVolume)); //new volume of the current track
	    this.cursorTrack.pan().value().addValueObserver((newPanning) -> reactToTrackPanningChange(newPanning)); //new panning of the current track
	    
	    this.cursorTrack.isPinned().addValueObserver((isPinned)->reactToIsPinned(isPinned));
		transport.isFillModeActive().addValueObserver((isFillModeActive)->reactToFillModeActive(isFillModeActive));
		
		//Crossfader on Encoder 4
		this.masterTrack = host.createMasterTrack(0);
		this.masterTrack.volume().value().addValueObserver((newVolume)-> reactToMasterTrackVolumeChange(newVolume));

		transport.crossfade().markInterested();
		transport.crossfade().value().addValueObserver((newValue) -> reactToCrossfadeValueChange(newValue));
		project.cueVolume().value().addValueObserver((newValue) -> reactToCueVolumeChange(newValue));
		
		this.trackRemoteControlsPage =  this.cursorTrack.createCursorRemoteControlsPage(8);
		for (int i=0;i<8;i++) {
			final int remoteIndex = i;
			this.trackRemoteControlsPage.getParameter(remoteIndex).markInterested();
			this.trackRemoteControlsPage.getParameter(remoteIndex).exists().addValueObserver((exists)->reactToRemoteExists(remoteIndex, exists));	
		}		

		//for shift clicks we need access to the sceneBank or a cue marker bank
		this.sceneBank = host.createSceneBank(MFT_Hardware.MFG_NUMBER_OF_ENCODERS-2); //-1 for the shift button and another one for the stop button on Encoder 15  
		//we need a cue marker bank to be able to jump to the cue markers
		this.cueMarkerBank = host.createArranger().createCueMarkerBank(MFT_Hardware.MFG_NUMBER_OF_ENCODERS-2); 
		
	}//end of constructor
	
	
	private void setSoloValue(boolean toggleValue) {
		this.isSolo = toggleValue;
		final int encoderColorIndex = toggleValue ? 127 : 0;
		setEncoderColor(MFT_Hardware.MFT_BANK2_BUTTON_02, encoderColorIndex);  		
	}
	
	private void setMutedValue(boolean toggleValue) {
		this.isMuted = toggleValue;
		final int encoderColorIndex = toggleValue ? 127 : 0;
		setEncoderColor(MFT_Hardware.MFT_BANK2_BUTTON_03, encoderColorIndex);
	}
	
	private void setArmedValue(boolean toggleValue) {
		this.isArmed = toggleValue;
		final int encoderColorIndex = toggleValue ? 127 : 0;
		setEncoderColor(MFT_Hardware.MFT_BANK2_BUTTON_04, encoderColorIndex);
	}
	
	/**
	 * Callback function that is called whenever the current track changes its volume
	 * @param newValue new volume of the currently selected track
	 */
	private void reactToTrackVolumeChange(double newValue) {
    	setEncoderRingValue(MFT_Hardware.MFT_BANK2_BUTTON_02, (int) Math.round(newValue*127));
    }
	/**
	 * Callback function that is called whenever the current track changes its volume
	 * @param newValue new volume of the currently selected track
	 */
	private void reactToMasterTrackVolumeChange(double newValue) {
    	if(MFT_Configuration.isChannelStripEncoder4_MasterVolume()){
			setEncoderRingValue(MFT_Hardware.MFT_BANK2_BUTTON_04, (int) Math.round(newValue*127));
		}
	}
	private void reactToCrossfadeValueChange(double newValue){
		if(MFT_Configuration.isChannelStripEncoder4_Crossfader()){
			setEncoderRingValue(MFT_Hardware.MFT_BANK2_BUTTON_04, (int) Math.round(newValue*127));
		}	
	}
	private void reactToCueVolumeChange(double newValue){
		if(MFT_Configuration.isChannelStripEncoder4_CueVolume	()){
			setEncoderRingValue(MFT_Hardware.MFT_BANK2_BUTTON_04, (int) Math.round(newValue*127));
		}
	}

	
	/**
	 * Callback function that is called whenever the panning of the current track is changed
	 * @param newValue New panning of the currently selected track
	 */
	private void reactToTrackPanningChange(double newValue) {
    	setEncoderRingValue(MFT_Hardware.MFT_BANK2_BUTTON_03, (int) Math.round(newValue*127));
    }
	
	/**
	 * Callback function that is called whenever the the send value of a track is changed
	 * @param newValue the new send value of the track that is indicated by the track-index
	 */
	private void reactToTrackSendChange(int index, double newValue) {
    	setEncoderRingValue(MFT_Hardware.MFT_BANK2_BUTTON_05+index, (int) Math.round(newValue*127));		
    }
	/**
	 * Callback function that is called to indicate if the send channel exists. 
	 * We use this to light up the colored led, in case the send channel exists
	 * @param newValue does the send channel exist or not
	 */
	private void reactToTrackSendExists(int index, boolean sendExists) {
		//switch the colored light on or off, depending on the sendExists status
		boolean enabled = this.cursorTrack.sendBank().getItemAt(index).isEnabled().get();
		if (sendExists && enabled){
			setEncoderColor( MFT_Hardware.MFT_BANK2_BUTTON_05+index, 127);	
		}
		else if(sendExists && !enabled){
			//set the encoder color to pink to indicate the FX channel exists but is disabled
			setEncoderColor( MFT_Hardware.MFT_BANK2_BUTTON_05+index, COLOR_PINK);
		}else 
			setEncoderColor( MFT_Hardware.MFT_BANK2_BUTTON_05+index, 0); //no FX channel, color off
			
    }	
	/**
	 * Callback function that is called to indicate if the send channel isenabled. 
	 * We use this to light up the colored led, in case the send channel is disabled, for example
	 * @param newValue indidaes if the send channel is enabled or not
	 */
	 private void reactToTrackSendEnabled(int index, boolean isEnabled) {
		//switch the colored light to gray or on
		boolean exists = this.cursorTrack.sendBank().getItemAt(index).exists().get();
		if(exists)setEncoderColor( MFT_Hardware.MFT_BANK2_BUTTON_05+index, isEnabled ? 127 : COLOR_PINK);	
		else setEncoderColor( MFT_Hardware.MFT_BANK2_BUTTON_05+index, isEnabled ? 127 : 0);
    }	
	
	/**
	 * Callback function that is called to indicate if the cursortrack is pinned or not
	 * This is important to know since we don't want to move the track with the first encoder
	 * We also use this callback to liight up the first encoder in case the track is pinned
	 * @param isPinned Indicate if the cursorTrack is pinned or not
	 */
	private void reactToIsPinned(boolean isPinned) {
		this.isPinned = isPinned; //update our internal flag to the status Bitwigs cursortrack
		//switch the colored light on or off, depending on the sendExists status
		setEncoderColor( MFT_Hardware.MFT_BANK2_BUTTON_01, isPinned ? 127 : 0);	
    }	
	/**
	 * Callback function that is called to indicate if a remote control for a track exists or not. 
	 * In case there is a remote control we can light up the LED for the remote control
	 * Otherwise we want to shut off the LED. 
	 * @param remoteIndex the index of the remote control that we want to check (from 0 to 7)
	 * @param exists indicates if the remote control exists or not
	 */
	private void reactToRemoteExists(int remoteIndex, boolean exists) {
		setEncoderColor( MFT_Hardware.MFT_BANK2_BUTTON_09+remoteIndex, exists ? 127 : 0);	
    }	

	/**
	 * Callback method that is called to incidate if the fill mode is active or not (this is used by operators)
	 * @param isFillModeActive indicates if the fill mode is active or not
	 */
	private void reactToFillModeActive(boolean isFillModeActive){
		setEncoderColor(MFT_Hardware.MFT_BANK2_BUTTON_04, isFillModeActive ? 127 : 0);
	}
	
	@Override
	public boolean handleButtonClick(MidiMessageWithContext msg)
	{
		//first handle the shift clicks
		if(isShiftConsumed()&&msg.isShiftButton())return true; //shift is consumed, so we do not need to do anything
		if(isShiftPressed())
		{
			if(msg.getData1()==MFT_Hardware.MFT_BANK2_BUTTON_15)transport.stop();
			else 
			{
				int index = msg.getData1()-MFT_Hardware.MFT_BANK2_BUTTON_01;
				if(MFT_Configuration.isChannelStripShiftClickActionScene()) this.sceneBank.launchScene(index);
				if(MFT_Configuration.isChannelStripShiftClickActionCueMarker()) this.cueMarkerBank.getItemAt(index).launch(true);				
			}
			return true;
		}

		//now the normal clicks
		switch (msg.getData1()) //data1 contains the controller number, we use this to differentiate the different encoders
	    {	
			case MFT_Hardware.MFT_BANK2_BUTTON_01:	    
				if(msg.isLongClick()){ //pin on long clicks
					this.isPinned = !this.isPinned; //let's change the pinned status and also tell Bitwig about it...
					this.cursorTrack.isPinned().set(this.isPinned);
				} else {
					this.isArmed = !this.isArmed; //toggle the arm flag
					this.cursorTrack.arm().set(isArmed); //and write the flag back
				}  
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_02:
				this.isSolo = !this.isSolo; //toggle the flag
				this.cursorTrack.solo().set(isSolo); //and write the flag back
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_03:
				this.isMuted = !this.isMuted; //toggle the flag
				this.cursorTrack.mute().set(isMuted); //and write the flag back
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_04:
				//let's toggle the fill mode, so that we can start and stop the fills
				transport.isFillModeActive().toggle();
				return true;     
			//2. row: Sends ----------
			case MFT_Hardware.MFT_BANK2_BUTTON_05:
				this.cursorTrack.sendBank().getItemAt(0).isEnabled().toggle();	                
				return true;   
			case MFT_Hardware.MFT_BANK2_BUTTON_06:
				this.cursorTrack.sendBank().getItemAt(1).isEnabled().toggle();
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_07:
				this.cursorTrack.sendBank().getItemAt(2).isEnabled().toggle();
				return true;  
			case MFT_Hardware.MFT_BANK2_BUTTON_08:
				this.cursorTrack.sendBank().getItemAt(3).isEnabled().toggle();
				return true; 
			//3. row: Track Remotes ----------
			case MFT_Hardware.MFT_BANK2_BUTTON_09:
				this.trackRemoteControlsPage.getParameter(0).reset();
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_10:
				this.trackRemoteControlsPage.getParameter(1).reset();return true;  
			case MFT_Hardware.MFT_BANK2_BUTTON_11:
				this.trackRemoteControlsPage.getParameter(2).reset();
				return true;  
			case MFT_Hardware.MFT_BANK2_BUTTON_12:
				this.trackRemoteControlsPage.getParameter(3).reset();
				return true;                                                  
			case MFT_Hardware.MFT_BANK2_BUTTON_13:
				this.trackRemoteControlsPage.getParameter(4).reset();
				return true;  
			case MFT_Hardware.MFT_BANK2_BUTTON_14:
				this.trackRemoteControlsPage.getParameter(5).reset();
				return true;  
			case MFT_Hardware.MFT_BANK2_BUTTON_15:
				this.trackRemoteControlsPage.getParameter(6).reset();
				return true;  
			case MFT_Hardware.MFT_BANK2_BUTTON_16:
				this.trackRemoteControlsPage.getParameter(7).reset();
				return true;  
			default:	                
				return false;
		}
	}
	    
	@Override
	public boolean handleEncoderTurn(MidiMessageWithContext msg)
	{
		switch (msg.getData1()) 
		{
			//1: Select Track
			case MFT_Hardware.MFT_BANK2_BUTTON_01:
				//The first encoder is turned, this should move the currently selected track up or down
				// We receive relative values from the MFT, either 65 (if turned clockwise) or 63 if turned counterclockwise
				//in order to slow down the button, let's not move with any message, but only every few
				if(this.isPinned)return true; //in case the cursor track is pinned, we do nothing here
				if(this.moveTrackDelay++ > 4) {
					if(msg.getData2()>=64) cursorTrack.selectNext();
					else cursorTrack.selectPrevious();
					this.moveTrackDelay = 0; //reset the delay
				}
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_02:  
				//this encoder is used to set the volume of the currently selected track
				this.cursorTrack.volume().inc (msg.getData2()-64, 128); 
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_03:     
				//we use the third encoder to change the panning
				this.cursorTrack.pan().inc (msg.getData2()-64, 128); 
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_04:
				if(MFT_Configuration.isChannelStripEncoder4_MasterVolume()){
					masterTrack.volume().inc(msg.getData2()-64, 128);
				}else if (MFT_Configuration.isChannelStripEncoder4_Crossfader()){
					transport.crossfade().inc(msg.getData2()-64, 128);						
				}else if(MFT_Configuration.isChannelStripEncoder4_CueVolume()){
					project.cueVolume().inc(msg.getData2()-64, 128);
				}
				return true;
			//2. row: Sends
			case MFT_Hardware.MFT_BANK2_BUTTON_05: //let's change the send of this track, and the same for the next four tracks              
				this.cursorTrack.sendBank().getItemAt(0).inc(msg.getData2()-64, 128);
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_06:                
				this.cursorTrack.sendBank().getItemAt(1).inc(msg.getData2()-64, 128);
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_07:                
				this.cursorTrack.sendBank().getItemAt(2).inc(msg.getData2()-64, 128);
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_08:                
				this.cursorTrack.sendBank().getItemAt(3).inc(msg.getData2()-64, 128);
				return true;
			//3. row: Track Remotes ----------
			case MFT_Hardware.MFT_BANK2_BUTTON_09:  
				this.trackRemoteControlsPage.getParameter(0).inc(msg.getData2()-64, 128);
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_10:                
				this.trackRemoteControlsPage.getParameter(1).inc(msg.getData2()-64, 128);					
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_11:                
				this.trackRemoteControlsPage.getParameter(2).inc(msg.getData2()-64, 128);					
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_12:                
				this.trackRemoteControlsPage.getParameter(3).inc(msg.getData2()-64, 128);
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_13:                
				this.trackRemoteControlsPage.getParameter(4).inc(msg.getData2()-64, 128);
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_14:                
				this.trackRemoteControlsPage.getParameter(5).inc(msg.getData2()-64, 128);
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_15:                
				this.trackRemoteControlsPage.getParameter(6).inc(msg.getData2()-64, 128);
				return true;
			case MFT_Hardware.MFT_BANK2_BUTTON_16:                
				this.trackRemoteControlsPage.getParameter(7).inc(msg.getData2()-64, 128);					
				return true;   
			default:
				return false; //false = no midi handled here
		} //switch
	}	//handle encoder turn		
} //class
