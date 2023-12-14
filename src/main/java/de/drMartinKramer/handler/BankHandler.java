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

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.hardware.*;
import de.drMartinKramer.support.MFT_MidiMessage;

/**
 * The BankHandler is used to handle the 4 different banks of the MFT. It keeps track of the current state, it sends a feedback (as a 
 * pop up text message in Bitwig when a button on the left and right was clicked.  
 * it also reacts to the two buttons on the left that are connect to a transport function. 
 */
public class BankHandler  extends AbstractHandler
{
	
	public  static final int MFT_BANK_1 = 1;
	public static final int MFT_BANK_2 = 2;
	public static final int MFT_BANK_3 = 3;
	public static final int MFT_BANK_4 = 4;
	private int bankMode = MFT_BANK_1; //we start on bank 1
	private int lastEncoderID = MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_1;
	private double lastBankDownClickTime = -1;
   
	
	public BankHandler(ControllerHost host)
	{    
		super(host);
	    //we want the initialize the MFT to bank 1 in order to sync the MFT with the bank mode in this handler object
		sendMFT_Global_Command( 0, 127); // in order to set the MFT to bank 1 we need to send CC0 on channel four with velocity of 127, see MFT manual
	}
	
	/**
	 * Returns the mode that the MFT is currently in
	 * @return the current mode of the MFT
	 */
	public int getBankMode() {
		return this.bankMode;
	}

	/**
	 * Change the MFT to one of the four banks
	 * @param newBank new bank to change to (0-3)
	 */
	private  void changeMFT_Bank(int newBank){
		sendMFT_Global_Command( newBank, 127); // in order to set the MFT to bank 1 we need to send CC0 on channel four with velocity of 127, see MFT manual
	}
	
	/**
	 * Message is called from the main event handler to allow this method to react to any buttons clicked on the left or the right.
	 * When any of the left or right button is clicked it results to a bank change or a transport command in Bitwig
	 * @param The incoming Midi message
	 * @return true if the Midi message was handled by the bank handler, false if no bank midi message was sent. 
	 */
	public boolean handleMidi (MFT_MidiMessage msg){
	   //check for CC message on channel 4 (which is here 3 and left/right button clicked which is indicated by value (data2) = 127)
	    int encoderId = msg.getData1();	 
		if (msg.isControlChange() && msg.getChannel()==3 && msg.getData2()==127) 
	    {	
			//the button is clicked down, we change to a new bank            
			lastBankDownClickTime = System.currentTimeMillis(); //record when the side click happened
			return handleBankChange(encoderId);				
		}else if (msg.isControlChange() && msg.getChannel()==3 && msg.getData2()==0) 
		{
			//the consecutive up click 
			double duration = System.currentTimeMillis()-lastBankDownClickTime;
			if(duration > MFT_Configuration.getGlobalLongClickMillis())
			{
				
				//we have a long click, i.e. we need to change back to the original bank
				return handleBankChange(lastEncoderID);
			}else 
			{ //we have a short click, i.e. we will not return to the original bank
				lastEncoderID = encoderId; //store the last encoder ID
				return true;
			}
		}
		return false;	    
	}//end of handleMidi	


	/**
	 * Manage the main action to change the bank to a new bank as a result of a click on one
	 * of the side buttons
	 * @param buttonID The ID of the button that is delivered via a Midi CC message. 
	 * @return if a bank transfer was successfully handled or not. 
	 */
	private boolean handleBankChange(int buttonID){
	// Click on a button on the left or the right
		switch (buttonID) //data1 contains the button number, we use this to differentiate the different encoders
		{   
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_1:
				showPopupNotification("Midi Fighter Twister: Mixer Mode");
				bankMode = MFT_BANK_1;	                
				changeMFT_Bank(0);
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_2: 
				showPopupNotification("Midi Fighter Twister: EQ Mode");
				changeMFT_Bank(0);				
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_3: 
				showPopupNotification("Midi Fighter Twister: Global Parameters");
				changeMFT_Bank(0);
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_1: 
				showPopupNotification("Midi Fighter Twister: Channel strip mode");
				bankMode = MFT_BANK_2;
				changeMFT_Bank(1);
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_2: 
				showPopupNotification("Midi Fighter Twister: Device mode");
				bankMode = MFT_BANK_3;
				changeMFT_Bank(2);
				return true;
			case MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_3: 
				showPopupNotification("Midi Fighter Twister: User mode");
				bankMode = MFT_BANK_4;
				changeMFT_Bank(3);
				return true;
			default: 
				errorln("no left/right button identiified");
				return false; //no midi was handled
		}//end of switch
	}//end of handleBankChange
}
