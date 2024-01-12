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
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.PinnableCursorDevice;

import de.drMartinKramer.hardware.*;
import de.drMartinKramer.support.MidiMessageWithContext;

public class DeviceHandler extends AbstractHandler 
{
	
	final public static int BITWIG_SIZE_OF_PARAMTER_PAGE = 8; //the number of parameters that are shown on a parameter page in Bitwig
	private DeviceBank deviceBank = null; //A window to look over the device chain
	private CursorTrack cursorTrack = null; //the track that is currently selected
    //private MasterTrack masterTrack= null;
	PinnableCursorDevice cursorDevice = null;
	CursorRemoteControlsPage myDeviceParameterPage = null; //this is a page of 8 device parameters
	int[] deviceParameterColorArray = {82,75,64,50,40,15,110,105};
	boolean deviceButtonClicked = false;
	boolean parameterPageButtonClicked = false; //indicates if the use clicked on a parameter page button, i.e. we need to change the device parameter page
	boolean projectParameterPageClicked = false; //the same for the project-wide parameter 
	CursorRemoteControlsPage projectControlsPage = null;
	
	public  DeviceHandler(ControllerHost host) 
	{
		super(host);
		this.cursorTrack = host.createCursorTrack ("DEVICE_HANDLER_CURSOR_TRACK", "MFT_DEVICE_HANDLER_CURSOR_TRACK", 0, 0,true);
		this.deviceBank = this.cursorTrack.createDeviceBank(MFT_Hardware.MFT_No_ENCODER);
		this.deviceBank.cursorIndex().markInterested();

		this.cursorDevice = this.cursorTrack.createCursorDevice("DEVICE_HANDLER_CURSOR_DEVICE", "MFT Device Handler Cursor Device", 0, CursorDeviceFollowMode.FOLLOW_SELECTION);
		this.cursorDevice.name().markInterested();
		this.cursorDevice.name().addValueObserver((name) -> reactToDeviceNameChange(name));
		myDeviceParameterPage = cursorDevice.createCursorRemoteControlsPage(MFT_Hardware.MFT_No_ENCODER);
		myDeviceParameterPage.getName().markInterested();
		myDeviceParameterPage.getName().addValueObserver((name) -> reactToParameterPageNameChange(name));

		//**** Access to the project remote controls */
		this.projectControlsPage = this.project.getRootTrackGroup().createCursorRemoteControlsPage(BITWIG_SIZE_OF_PARAMTER_PAGE);
		this.projectControlsPage.getName().markInterested();
		this.projectControlsPage.getName().addValueObserver((name) -> reactToProjectParameterPageNameChange(name));
		
		for(int i=0; i<BITWIG_SIZE_OF_PARAMTER_PAGE; i++)
		{
			//* First we handle the device parameter group, i.e. the first two rows of the MFT are for the device */
			//set the indication that we are interested in the value of the parameter, and add an observer with a callback function
			final int myParameter = i; //"final" is very important otherwise the callback is always called for the last encoder 
			myDeviceParameterPage.getParameter(myParameter).value().markInterested();
			myDeviceParameterPage.getParameter(myParameter).setIndication (true);
			//register the callback: 
			myDeviceParameterPage.getParameter(i).value().addValueObserver((newValue)->reactToDeviceParameterChange(myParameter, newValue));
			//register callback for the existence of a device parameter
			myDeviceParameterPage.getParameter(myParameter).exists().addValueObserver((exists)->reactToDeviceParameterExists(myParameter, exists));

			/** Now we do the same for the project-wide project controls. That is for row 3 and 4 */
			final int myProjectParameter = i;
			projectControlsPage.getParameter(myProjectParameter).value().markInterested();
			projectControlsPage.getParameter(myProjectParameter).setIndication (true);
			//register the callback: 
			projectControlsPage.getParameter(i).value().addValueObserver((newValue)->reactToProjectParameterChange(myParameter, newValue));
			//register callback for the existence of a device parameter
			projectControlsPage.getParameter(myParameter).exists().addValueObserver((exists)->reactToProjectParameterExists(myParameter, exists));
		}		
    }//end of constructor
	
	/**
	 * Callback function that is called whenever a device parameter change happens
	 * @param index the index (encoder) of the device parameter that changed
	 * @param newValue the new value of the device parameter
	 */
	private void reactToDeviceParameterChange(int index, double newValue) {
    	setEncoderRingValue(MFT_Hardware.MFT_BANK3_BUTTON_01+index, (int) Math.round(newValue*127));		
    }
		
	/**
	 * Callback function that is called to indicate if a device control exists or not.  
	 * In case there is a device control exists, we can light up the LED for the remote control
	 * Otherwise we want to shut off the LED. 
	 * @param remoteIndex the index of the device parameter (from 0 to 7)
	 * @param exists indicates if the device control exists or not
	 */
	private void reactToDeviceParameterExists(int remoteIndex, boolean exists) {
		if(remoteIndex<8){

			int newColor = deviceParameterColorArray[remoteIndex];
			setEncoderColor(MFT_Hardware.MFT_BANK3_BUTTON_01+remoteIndex, exists ? newColor : 0);				
		}
    }

/**
	 * Callback function that is called whenever a project-wide parameter change happens
	 * @param index the index (encoder) of the project parameter that changed
	 * @param newValue the new value of the project-wide parameter
	 */
	private void reactToProjectParameterChange(int index, double newValue) {
    	setEncoderRingValue(MFT_Hardware.MFT_BANK3_BUTTON_01+BITWIG_SIZE_OF_PARAMTER_PAGE+index, (int) Math.round(newValue*127));		
    }
		
	/**
	 * Callback function that is called to indicate if a project-wide control exists or not.  
	 * In case when a project-wide control exists, we can light up the LED for the remote control
	 * Otherwise we want to shut off the LED. 
	 * @param remoteIndex the index of the project-wide parameter (from 0 to 7)
	 * @param exists indicates if the project-wide control exists or not
	 */
	private void reactToProjectParameterExists(int remoteIndex, boolean exists) {
		if(remoteIndex<8){

			int newColor = deviceParameterColorArray[remoteIndex];
			setEncoderColor(MFT_Hardware.MFT_BANK3_BUTTON_01+BITWIG_SIZE_OF_PARAMTER_PAGE+remoteIndex, exists ? newColor : 0);				
		}
    }

	/**
	 * Callback function that is called whenever the name of the device changes. 
	 * We use this callback to show a popup notification with the name of the device. 
	 * The paraemter name is not required. 
	 * @param name not required
	 */
	private void reactToDeviceNameChange(String name) {
		if(deviceButtonClicked){
			deviceButtonClicked = false;
			showPopupNotification("Active Device: " + cursorDevice.name().get());
		}	
    }	

	/**
	 * Callback function that is called whenever the parameter page of a device is changed (e.g. by click on the button) 
	 * We use this callback to show a popup notification with the name of new parameter page. 
	 * The paraemter name is not required. 
	 * @param name not required
	 */
	private void reactToParameterPageNameChange(String name) {
		if(parameterPageButtonClicked){
			parameterPageButtonClicked = false;
			showPopupNotification("Parameter Page: " + myDeviceParameterPage.getName().get());
		}
	}

	/**
	 * Callback function that is called whenever the parameter page of a project-wide remote controls is changed (e.g. by click on the button) 
	 * We use this callback to show a popup notification with the name of new parameter page. 
	 * The paraemter name is not required. 
	 * @param name the name of the new parameter page
	 */
	private void reactToProjectParameterPageNameChange(String name) {
		if(this.projectParameterPageClicked){
			parameterPageButtonClicked = false; //reset 
			showPopupNotification("Project-wide Parameter Page: " + projectControlsPage.getName().get());
		}
	}

	@Override
	public boolean handleButtonClick(MidiMessageWithContext msg) 
	{			 
		switch (msg.getData1()) //data1 contains the controller number, we use this to differentiate the different encoders
		{	            
			//Row 1-2: Device Controls ----------
			//first encoder button: toggle the device on/off			
			case MFT_Hardware.MFT_BANK3_BUTTON_01:	 
				if(msg.isLongClick()) { //long click -> toggle the device window open/closed
					this.cursorDevice.isEnabled().toggle();						
				}else { //short click
					this.cursorDevice.selectFirst();
					deviceButtonClicked = true;
				}return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_02:
				this.cursorDevice.selectPrevious();  
				deviceButtonClicked = true;
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_03:
				this.cursorDevice.selectNext();  
				deviceButtonClicked = true;                  
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_04:
				this.cursorDevice.selectLast();  
				deviceButtonClicked = true;	            	                
				return true;  
			case MFT_Hardware.MFT_BANK3_BUTTON_05:
				if(msg.isLongClick()) { //long click -> toggle the device window open/closed
					this.cursorDevice.isWindowOpen().toggle(); //toggle the device window open/closed
				}else { //short click
					myDeviceParameterPage.selectFirst(); //select the first parameter page 
					parameterPageButtonClicked = true;
				}
				return true; 
			case MFT_Hardware.MFT_BANK3_BUTTON_06:
				myDeviceParameterPage.selectPreviousPage(false); //select the previous parameter page, do not wrap around						                
				parameterPageButtonClicked = true;
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_07:	            	
				myDeviceParameterPage.selectNextPage(false);					       
				parameterPageButtonClicked = true;
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_08:	            	
				myDeviceParameterPage.selectLast();					       
				parameterPageButtonClicked = true;
				return true;
			//row 4-4: Device Control ----------
			case MFT_Hardware.MFT_BANK3_BUTTON_09:
				//no action right now	                
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_10:
				//no action right now	                 
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_11:
				//no action right now	                
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_12:
				//no action right now	                
				return true;                                                
			case MFT_Hardware.MFT_BANK3_BUTTON_13:
				projectControlsPage.selectFirst();					       
				this.projectParameterPageClicked = true;	                
				return true; 
			case MFT_Hardware.MFT_BANK3_BUTTON_14:
				projectControlsPage.selectPreviousPage(false);				       
				this.projectParameterPageClicked = true;                
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_15:
				projectControlsPage.selectNextPage(false);				       
				this.projectParameterPageClicked = true;            	                
				return true; 
			case MFT_Hardware.MFT_BANK3_BUTTON_16:
				projectControlsPage.selectLast();					       
				this.projectParameterPageClicked = true;	                
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
			case MFT_Hardware.MFT_BANK3_BUTTON_01:
				myDeviceParameterPage.getParameter(0).inc(msg.getData2()-64, 128);             
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_02:  
				myDeviceParameterPage.getParameter(1).inc(msg.getData2()-64, 128);             
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_03:     
				myDeviceParameterPage.getParameter(2).inc(msg.getData2()-64, 128);           
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_04:
				myDeviceParameterPage.getParameter(3).inc(msg.getData2()-64, 128);            
				return true;				
			case MFT_Hardware.MFT_BANK3_BUTTON_05:             
				myDeviceParameterPage.getParameter(4).inc(msg.getData2()-64, 128);             
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_06:                
				myDeviceParameterPage.getParameter(5).inc(msg.getData2()-64, 128);             
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_07:                
				myDeviceParameterPage.getParameter(6).inc(msg.getData2()-64, 128);              
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_08:                
				myDeviceParameterPage.getParameter(7).inc(msg.getData2()-64, 128);             
				return true;
			//Row 3-4 Global Remote Controls
			case MFT_Hardware.MFT_BANK3_BUTTON_09:  
				projectControlsPage.getParameter(0).inc(msg.getData2()-64, 128);             	 
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_10:                
				projectControlsPage.getParameter(1).inc(msg.getData2()-64, 128); 	
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_11:                
				projectControlsPage.getParameter(2).inc(msg.getData2()-64, 128); 	                
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_12:                
				projectControlsPage.getParameter(3).inc(msg.getData2()-64, 128);                 
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_13:                
				projectControlsPage.getParameter(4).inc(msg.getData2()-64, 128); 	                
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_14:                
				projectControlsPage.getParameter(5).inc(msg.getData2()-64, 128); 	                
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_15:                
				projectControlsPage.getParameter(6).inc(msg.getData2()-64, 128);                 
				return true;
			case MFT_Hardware.MFT_BANK3_BUTTON_16:                
				projectControlsPage.getParameter(7).inc(msg.getData2()-64, 128);                 
				return true;
			default:
				return false; //false = no midi handled here
		} // end of switch	
	} //handle encoder turn
	
}
