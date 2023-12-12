package de.drMartinKramer.handler;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.*;


public class TrackHandler extends AbstractHandler
{
	private TrackBank trackBank = null;
	private int updateDelay = 0; //how often should the Bitwig mixer and arranger be updated?
		
	public TrackHandler (ControllerHost host)
	{		
		super(host);
	    this.trackBank = host.createMainTrackBank(MFT_Hardware.MFG_NUMBER_OF_ENCODERS, 0, 0);
	       
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
            
	        //COLOR
	        track.color().markInterested();	
            track.color().addValueObserver((colorRed,colorGreen,colorBlue)->this.reactToColorChange(trackIndex,colorRed,colorGreen,colorBlue) );			
	    }  
		this.host.println("TrackHandler created");
	}// end of trackHandler Constructor
	
	/**
	 * This is called as a reaction to a changed track volume in Bitwig. We send a message to the MFT to update the track volume on the encoder 	
	 * @param index the number of the track on which the volume change occured, from 0 to 15
	 * @param newValue the new value of of the track volume
	 */
    private void reactToTrackVolumeChange(int trackIndex, double newValue) {
    	updateEncoderRingValue(trackIndex, (int) Math.round(newValue*127));
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
    	setEncoderColor(trackIndex, colorIndex);    	
    }
    
    /**
     * Method is called as a response to an encoder click MFT. 
     * @param track which track is affected
     * @param msg the incoming midi message from the MFT
     */
	private void updateMFT_Volume (Track track, ShortMidiMessage msg)
	{
	    track.volume().inc (msg.getData2()-64, 128); 
	    updateDelay++;
	    if(updateDelay > 20 && MFT_Configuration.mixerMakeVisible()){ //only every 20th time and only if we should update the view 
	    	track.makeVisibleInArranger();
	        track.makeVisibleInMixer();
	        updateDelay=0;
	    }		
	}
	
/**
     * Method is called as a response to an encoder value change on the MFT. We update Bitwig by either increasing of decreasing its current value
     * @param track which track is affected
     * @param msg the incoming midi message from the MFT
     */
	private void clickedOnEncoder (int index)
	{
	    Track myNewTrack = this.trackBank.getItemAt (index);
	    if(isLongClicked()){
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

	public boolean handleMidi (ShortMidiMessage msg)
	{   
		super.handleMidi(msg);//we first need to check for long clicks
		//check for CC message on channel 2 (which is here 1 and button clicked which is indicated by value (data2) = 127)
	    if (msg.isControlChange() && msg.getChannel()==1 && msg.getData2()==0)
	    {
	        // Message came on Channel two (==1) -> CLICK ON THE ENCODER -> SELECT a TRACK *********
	        switch (msg.getData1()) //data1 contains the controller number, we use this to differentiate the different encoders
	        {
	            
	            case MFT_Hardware.MFT_BANK1_BUTTON_01:
	                clickedOnEncoder(0);	                
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_02:
	            	clickedOnEncoder(1);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_03:
	            	clickedOnEncoder(2);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_04:
	            	clickedOnEncoder(3);	                
	                return true;     
	            case MFT_Hardware.MFT_BANK1_BUTTON_05:
	            	clickedOnEncoder(4);	                
	                return true;   
	            case MFT_Hardware.MFT_BANK1_BUTTON_06:
	            	clickedOnEncoder(5);                
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_07:
	            	clickedOnEncoder(6);                
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_08:
	            	clickedOnEncoder(7);	                
	                return true; 
	            case MFT_Hardware.MFT_BANK1_BUTTON_09:
	            	clickedOnEncoder(8);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_10:
	            	clickedOnEncoder(9);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_11:
	            	clickedOnEncoder(10);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_12:
	            	clickedOnEncoder(11);
	                return true;                                                  
	            case MFT_Hardware.MFT_BANK1_BUTTON_13:
	            	clickedOnEncoder(12);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_14:
	            	clickedOnEncoder(13);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_15:
	            	clickedOnEncoder(14);
	                return true;  
	            case MFT_Hardware.MFT_BANK1_BUTTON_16:
	            	clickedOnEncoder(15);
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
	                this.updateMFT_Volume(this.trackBank.getItemAt (0), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_02:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (1), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_03:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (2), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_04:
	                this.updateMFT_Volume(this.trackBank.getItemAt (3), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_05:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (4), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_06:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (5), msg);               
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_07:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (6), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_08:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (7), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_09:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (8), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_10:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (9), msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_11:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (10),msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_12:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (11),msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_13:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (12),msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_14:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (13),msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_15:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (14),msg);
	                return true;
	            case MFT_Hardware.MFT_BANK1_BUTTON_16:                
	                this.updateMFT_Volume(this.trackBank.getItemAt (15),msg);
	                return true;   
	            default:
	                return false; //false = no midi handled
	        }
	    }
	    return false; //we did not handle any incoming midi   
	}	
	
}