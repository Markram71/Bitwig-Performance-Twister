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
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.RemoteControlsPage;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.*;
import de.drMartinKramer.support.MFT_MidiMessage;


public class MixerHandler extends AbstractCachingHandler
{
	private TrackBank trackBank = null;
	private int updateDelay = 0; //how often should the Bitwig mixer and arranger be updated?

	
	private RemoteControlsPage[] remoteControlsPage = null;
		
	public MixerHandler (ControllerHost host)
	{		
		super(host);
		
		this.trackBank = host.createMainTrackBank(MFT_Hardware.MFG_NUMBER_OF_ENCODERS, 1, 0);
	    this.remoteControlsPage = new RemoteControlsPage[MFT_Hardware.MFG_NUMBER_OF_ENCODERS];  
		
		for (int i = 0; i < this.trackBank.getSizeOfBank (); i++)
	    {
	        Track track = this.trackBank.getItemAt (i);	
	        track.pan().markInterested ();
	        track.pan().setIndication (true);	          
	        
	        // VOLUME: Register an observer for each track's volume
	        final int trackIndex = i; //the final is very important, otherwise we cannot lock the trackIndex for each track
	        track.volume().markInterested ();
	        track.volume().setIndication (true);
	        track.volume().value().addValueObserver( (newValue)->this.reactToTrackVolumeChange(trackIndex,newValue) );
            
			//Get access to one send and one remote parameter for click&turn		
			this.remoteControlsPage[i] =  track.createCursorRemoteControlsPage(1);

			//COLOR
	        track.color().markInterested();	
            track.color().addValueObserver((colorRed,colorGreen,colorBlue)->this.reactToColorChange(trackIndex,colorRed,colorGreen,colorBlue) );
			
			track.addIsSelectedInMixerObserver(isActive->reactToIsSelected(trackIndex, isActive));
		}
	}// end of trackHandler Constructor
	


	/**
	 * This is called as a reaction to a changed track volume in Bitwig. We send a message to the MFT to update the track volume on the encoder 	
	 * @param index the number of the track on which the volume change occured, from 0 to 15
	 * @param newValue the new value of of the track volume
	 */
    private void reactToTrackVolumeChange(int trackIndex, double newValue) {
    	setEncoderRingValueCached(trackIndex,trackIndex,  (int) Math.round(newValue*127));
    }
    
    /**
     * This callback method is called when a color of a track in Bitwig is changed. We receive the trackIndex and the 
     * color in form of an RGB value, as red, green, blue. 
     * As a result we need to change the color in the device
     * @param trackIndex The index of the track for which the color was changed. 
     * @param red the amount of red in the color
     * @param green the amount of green in the color
     * @param blue the amount of blue in the color
     */
    private void reactToColorChange(int trackIndex, float red, float green, float blue) {
    	int colorIndex = MFT_Colors.getClosestMFT_Color(red,green,blue);
    	setEncoderColorCached(trackIndex, trackIndex,colorIndex);    	
    }

	/**
	 * Callback method that is called when a track is selected or not. We then change the 
	 * brightness of the encoder light to indicate the selection state
	 * @param trackIndex which track (or encoder) is affected 
	 * @param isSelected is the track/encoder selected or not
	 */
	private void reactToIsSelected(int trackIndex, boolean isSelected){
		//set the encoder brightness to high (if selected) or low (if not selected)
		setEncoderSpecialFXCached(trackIndex, trackIndex, 
			isSelected ? 
				MFT_Hardware.MFT_SPECIAL_ENCODER_COLOR_BRIGHTNESS_MESSAGE + MFT_Hardware.MFT_SPECIAL_ENCODER_MAX_BRIGHTNESS : 
				MFT_Hardware.MFT_SPECIAL_ENCODER_COLOR_BRIGHTNESS_MESSAGE + MFT_Hardware.MFT_SPECIAL_ENCODER_LOW_BRIGHTNESS);
	}
    
    /**
     * Method is called as a response to an encoder click MFT. 
     * @param track which track is affected
     * @param msg the incoming midi message from the MFT
     */
	private void updateTrackParameterAfterTurn (int encoderNr, MFT_MidiMessage msg)
	{
		Track track = this.trackBank.getItemAt (encoderNr);
	    if(!msg.isButtonCurrentlyDown()){ //the button is currently not pressed
			track.volume().inc ((msg.getData2()-64)*MFT_Configuration.getNormalTurnFactor(), 128); 
			updateDelay++;
			if(updateDelay > 20 && MFT_Configuration.mixerMakeVisible()){ //only every 20th time and only if we should update the view 
				track.makeVisibleInArranger();
				track.makeVisibleInMixer();
				updateDelay=0;
			}	
		}else{
			if(MFT_Configuration.isMixerClickAdnTurnFunctionPan()){
				track.pan().inc((msg.getData2()-64)*MFT_Configuration.getClickTurnFactor(), 128);
			}else if(MFT_Configuration.isMixerClickAdnTurnFunctionSend1()){	
				track.sendBank().getItemAt(0).value().inc((msg.getData2()-64)*MFT_Configuration.getClickTurnFactor(), 128);				
			}else if(MFT_Configuration.isMixerClickAdnTurnFunctionTrackRemote1()){
				Parameter parameter = remoteControlsPage[encoderNr].getParameter(0);
				if(parameter!=null)parameter.inc((msg.getData2()-64)*MFT_Configuration.getClickTurnFactor(), 128);
			}
		}
	}
	
	/**
     * Method is called as a response to an encoder value change on the MFT. We update Bitwig by either increasing of decreasing its current value
     * @param track which track is affected
     * @param msg the incoming midi message from the MFT
     */
	private void clickedOnEncoder (int index, MFT_MidiMessage msg)
	{
	    Track myNewTrack = this.trackBank.getItemAt (index);
	    if(msg.isLongClick()){
			if(MFT_Configuration.isMixerLongButtonActionArm())
				myNewTrack.arm().toggle();
			else if(MFT_Configuration.isMixerLongButtonActionMute())
				myNewTrack.mute().toggle();
			else if(MFT_Configuration.isMixerLongButtonActionSolo())
				myNewTrack.solo().toggle();
		}else{ //short click
			myNewTrack.selectInEditor();
			myNewTrack.selectInMixer();
	    	if(MFT_Configuration.mixerMakeVisible()){
				myNewTrack.makeVisibleInMixer();
	    		myNewTrack.makeVisibleInArranger();
			}			
		}
	}

	@Override
	public boolean handleButtonClick (MFT_MidiMessage msg)
	{   
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
	} //handle button click 
		
	/**
	 * Handle the encoder turn message from the MFT
	 */
	@Override
	public boolean handleEncoderTurn (MFT_MidiMessage msg)
	{
		switch (msg.getData1()) 
	    {
			case MFT_Hardware.MFT_BANK1_BUTTON_01:
				this.updateTrackParameterAfterTurn(0, msg);					
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_02:                
				this.updateTrackParameterAfterTurn(1, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_03:                
				this.updateTrackParameterAfterTurn(2, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_04:
				this.updateTrackParameterAfterTurn(3, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_05:                
				this.updateTrackParameterAfterTurn(4, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_06:                
				this.updateTrackParameterAfterTurn(5, msg);               
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_07:                
				this.updateTrackParameterAfterTurn(6, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_08:                
				this.updateTrackParameterAfterTurn(7, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_09:                
				this.updateTrackParameterAfterTurn(8, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_10:                
				this.updateTrackParameterAfterTurn(9, msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_11:                
				this.updateTrackParameterAfterTurn(10,msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_12:                
				this.updateTrackParameterAfterTurn(11,msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_13:                
				this.updateTrackParameterAfterTurn(12,msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_14:                
				this.updateTrackParameterAfterTurn(13,msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_15:                
				this.updateTrackParameterAfterTurn(14,msg);
				return true;
			case MFT_Hardware.MFT_BANK1_BUTTON_16:                
				this.updateTrackParameterAfterTurn(15,msg);
				return true;   
			default:
				return false; //false = no midi handled
		}	
	}	//end of handle encoder turn

} //of class