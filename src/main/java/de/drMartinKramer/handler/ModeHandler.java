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

import java.util.HashMap;

import com.bitwig.extension.controller.api.ControllerHost;

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.*;
import de.drMartinKramer.support.MidiMessageWithContext;

/**
 * The ModeHandler is used to handle sixe different modes of the Bitwig Performance Twister
 * It keeps track of the current mode, it sends a feedback (as a 
 * pop up text message in Bitwig when a button on the left and right has been pressed.  
 * We need to differentiate two concepts: 1. the MFT Banks of which there are four. And 2. the six 
 * different modes that this controller script offers. 
 * With the six buttons on the side, 3 on the left, 3 on the right, we now have the possibility
 * to implement six different modes and each mode is directly available by a click on the associated
 * button on the left of the right. The MFT device has only four mode banks though. So two of the 
 * modes need to reuse a bank from another mode. Specifically all three buttons on the left 
 * are associated to bank 1 of the MFT.    
 * We have configured the MFT device in such a way that the buttons on the side only send
 * CC messages and no bank changes on the MFT devices are initiated by the device itself.
 * We nevertheless need to change the banks and this is what this ModeHandler does. 
 * So you will see two notions here: 1. "Mode" and 2. "Bank". The differentiation should now be clear.  
 */
public class ModeHandler  extends AbstractHandler
{
	
	public static final int MFT_MODE_MIXER = MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_1;
	public static final int MFT_MODE_CHANNEL_STRIP = MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_1;
	public static final int MFT_MODE_EQ = MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_2;
	public static final int MFT_MODE_DEVICE = MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_2;
	public static final int MFT_MODE_GLOBAL = MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_3;
	public static final int MFT_MODE_USER = MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_3;
	private  int mode = MFT_MODE_MIXER; //
	private  int lastMode = mode;
	private  int lastSideButtonID = MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_1;
	private static double lastModekDownClickTime = -1;
	HashMap<Integer, AbstractHandler> handlerMap = null;
   
	
	public ModeHandler(ControllerHost host, HashMap<Integer, AbstractHandler> handlerMap)	
	{    
		super(host);
		this.handlerMap = handlerMap;
	}
	
	/**
	 * Returns the mode that the MFT is currently in
	 * @return the current mode of the MFT
	 */
	public int getMode() {
		return this.mode;
	}

	/**
	 * Change the MFT to one of the four internal (hardware) banks
	 * @param newBank new bank to change to (0-3)
	 */
	private void changeMFT_Bank(int newBank){
		sendMFT_Global_Command( newBank, 127); // in order to set the MFT to bank 1 we need to send CC0 on channel four with velocity of 127, see MFT manual
	}
	
	/**
	 * Message is called from the main event handler to allow this method to react to any buttons clicked on the left or the right.
	 * When any of the left or right button is clicked it results to a bank change or a transport command in Bitwig
	 * @param The incoming Midi message
	 * @return true if the Midi message was handled by the bank handler, false if no bank midi message was sent. 
	 */
	@Override
	public boolean handleMidi (MidiMessageWithContext msg){
	   //check for CC message on channel 4 (which is here 3 and left/right button clicked which is indicated by value (data2) = 127)
	    int sideButtonID = msg.getData1();	 
		if (msg.isGlobalMessage() && msg.getData2()==127) 
	    {	
			//the button is clicked down, we change to a new bank            
			lastModekDownClickTime = System.currentTimeMillis(); //record when the side click happened
			return handleModeChange(sideButtonID);				
		}else if (msg.isGlobalMessage() && msg.getData2()==0) 
		{
			//the consecutive up click 
			double duration = System.currentTimeMillis()-lastModekDownClickTime;
			if(duration > MFT_Configuration.getGlobalLongClickMillis())
			{				
				//we have a long click, i.e. we need to change back to the last mode 
				return handleModeChange(lastSideButtonID);
			}else 
			{ //we have a short click, i.e. we will not return to the original bank
				lastSideButtonID = sideButtonID; //store the last encoder ID
				return true;
			}
		}
		return false;	    
	}//end of handleMidi	

	public void changeToMode(int buttonID){		
		handleModeChange(buttonID);
	}	

	/**
	 * Manage the main action to change the bank to a new bank as a result of a click on one
	 * of the side buttons
	 * @param buttonID The ID of the button that is delivered via a Midi CC message. 
	 * @return if a bank transfer was successfully handled or not. 
	 */
	private boolean handleModeChange(int buttonID){
	// Click on a button on the left or the right		
		switch (buttonID) //data1 contains the button number, we use this to differentiate the different side buttons
		{   
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_1:
				handleModeChange(MFT_MODE_MIXER, 0, "Midi Fighter Twister: Mixer mode");				
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_2: 
				handleModeChange(MFT_MODE_EQ, 0, "Midi Fighter Twister: EQ mode");				
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_3: 
				handleModeChange(MFT_MODE_GLOBAL, 0, "Midi Fighter Twister: Global parameters");				
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_1: 
				handleModeChange(MFT_MODE_CHANNEL_STRIP, 1, "Midi Fighter Twister: Channel strip mode");				
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_2: 
				handleModeChange(MFT_MODE_DEVICE, 2, "Midi Fighter Twister: Device mode");
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_3: 
				handleModeChange(MFT_MODE_USER, 3, "Midi Fighter Twister: User mode");
				return true;
			default: 
				errorln("no left/right button identiified");
				return false; //no midi was handled
		}//end of switch
	}//end of handleBankChange

	/**
	 * this messages handles all the internal changes and shows a pop up message in Bitwig
	 * @param newMode the new mode to be changed to
	 * @param newBank the new bank to be changed to
	 * @param popUpMessage the message to be shown in Bitwig (if pop up messages are enabled)
	 */
	private void handleModeChange(int newMode, int newBank, String popUpMessage)
	{
		showPopupNotification(popUpMessage);

		this.lastMode = this.mode;
		this.mode = newMode;
		changeMFT_Bank(newBank);
		AbstractHandler newHandler = handlerMap.get(newMode);
		if(newHandler != null) newHandler.setActive(true);
		if(newMode != lastMode) {
			AbstractHandler oldHandler = handlerMap.get(lastMode);
			if(oldHandler!=null) oldHandler.setActive(false);
		}
	}
}
