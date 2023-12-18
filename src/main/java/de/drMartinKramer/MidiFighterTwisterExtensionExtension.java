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


package de.drMartinKramer;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.ControllerExtension;

import de.drMartinKramer.handler.ModeHandler;
import de.drMartinKramer.handler.ChannelStripHandler;
import de.drMartinKramer.handler.DeviceHandler;
import de.drMartinKramer.handler.EQ_Handler;
import de.drMartinKramer.handler.TrackHandler;
import de.drMartinKramer.support.EncoderStateMap;
import de.drMartinKramer.support.MFT_MidiMessage;


public class MidiFighterTwisterExtensionExtension extends ControllerExtension
{
	private ControllerHost host = null;
	private ModeHandler modeHandler = null;
	private TrackHandler trackHandler = null; //Bank 1
	private ChannelStripHandler channelStripHandler = null; // Bank 2
   private DeviceHandler deviceHandler = null; // Bank 3
   private EQ_Handler eq_Handler = null; // Bank 4
	private MidiFighterTwisterExtensionExtensionDefinition definition = null;
   private MFT_Configuration configuration = null;
   private EncoderStateMap encoderStateMap = null; //a hashmap that stores the current state of the encoders  

	
   protected MidiFighterTwisterExtensionExtension(final MidiFighterTwisterExtensionExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
      this.definition = definition;
      this.host = host; 
      this.encoderStateMap = new EncoderStateMap();
   }

   /**
    * The init method is called to by Bitwig to initialize the controller script. 
    * Some calls to Bitwig are only possible here. E.g. calls to register callbacks or 
    * markInterested. 
    * So, it's important to create the Handler objects right nere and not in the constructor.
    */
@Override
   
   public void init() 
   {
      final ControllerHost host = getHost();   
      
      host.getMidiInPort(0).setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi0(msg));
      this.modeHandler = new ModeHandler(host);  
      this.trackHandler = new TrackHandler(host);
      this.channelStripHandler = new ChannelStripHandler(host);
      this.deviceHandler = new DeviceHandler(host);
      this.eq_Handler = new EQ_Handler(host);
      this.configuration = new MFT_Configuration(host);
      		  
      // For now just show a pop up notification for verification that it is running.
      host.showPopupNotification("Midi Fighter Twister initialized, Version " + definition.getVersion());     
   }

   public MFT_Configuration getConfiguration() {
      return this.configuration;
   }

   @Override
   public void exit()
   {
      getHost().showPopupNotification("Midi Fighter Twister exited");
   }

   @Override
   public void flush()
   {
      // currently nothing to do here.
   }

   private MFT_MidiMessage parseMidiMessage(ShortMidiMessage msg) {
      MFT_MidiMessage mftMessage = new MFT_MidiMessage(msg); 
      if(mftMessage.isButtonClickMessage()){ //button click message
         if(mftMessage.getData2()==127){
            this.encoderStateMap.encoderClickDown(mftMessage.getEncoderID()); //click down
         }
         else if(mftMessage.getData2()==0) //this is click up
         {
            if(encoderStateMap.isLongClick(mftMessage.getEncoderID())) mftMessage.setLongClick(true); //button up
            else mftMessage.setLongClick(false); 
            this.encoderStateMap.encoderClickUp(mftMessage.getEncoderID()); //button up
         }
      } else if(mftMessage.isEncoderTurnkMessage()){
         if(encoderStateMap.isEncoderCurrentlyClickedDown(mftMessage.getEncoderID())) {
            mftMessage.setButtonCurrentlyDown(true);
            this.encoderStateMap.resetLongClickByTurn(mftMessage.getEncoderID());            
         }
         else mftMessage.setButtonCurrentlyDown(false);
      }
      return mftMessage;
   }

   /** Called when we receive short MIDI message on port 0. */
   private void onMidi0(ShortMidiMessage msg) 
   {
	   
	   try {
         //an important first step is to add context to the midi message, e.g. let us know that an encoder was clicked shortly before
         MFT_MidiMessage mftMessage = parseMidiMessage(msg);

         //Now let's select a handler based on the mode
		   if(mftMessage.isGlobalMessage()){ //but let's first check if a bank has changed, these are sent by global messages
            modeHandler.handleMidi(mftMessage);
         }else if(modeHandler.getMode() == ModeHandler.MFT_MODE_MIXER){
            trackHandler.handleMidi(mftMessage);
         } else if(modeHandler.getMode() == ModeHandler.MFT_MODE_EQ){
            eq_Handler.handleMidi(mftMessage);
         } else if(modeHandler.getMode() == ModeHandler.MFT_MODE_CHANNEL_STRIP){
            channelStripHandler.handleMidi(mftMessage); 
         } else if (modeHandler.getMode() == ModeHandler.MFT_MODE_DEVICE){
            deviceHandler.handleMidi(mftMessage);
         }      
      }catch(Exception e) {
		   host.errorln("Something went wrong after the MFT sent a Midi message. The script could not handle this message correctly. ");
		   host.errorln("Exception thrown: " + e.getLocalizedMessage());
	   }   	   
   }   
}
