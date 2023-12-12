package de.drMartinKramer;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.ControllerExtension;

import de.drMartinKramer.handler.BankHandler;
import de.drMartinKramer.handler.ChannelStripHandler;
import de.drMartinKramer.handler.DeviceHandler;
import de.drMartinKramer.handler.TrackHandler;


public class MidiFighterTwisterExtensionExtension extends ControllerExtension
{
	private ControllerHost host = null;
	private BankHandler bankHandler = null;
	private TrackHandler trackHandler = null; //Bank 1
	private ChannelStripHandler channelStripHandler = null; // Bank 2
   private DeviceHandler deviceHandler = null; // Bank 3
	private MidiFighterTwisterExtensionExtensionDefinition definition = null;
   private MFT_Configuration configuration = null;
	
   protected MidiFighterTwisterExtensionExtension(final MidiFighterTwisterExtensionExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
      this.definition = definition;
      this.host = host; 
   }

   /**
    * The init method is called to by Bitwig to initialize the controller script. Some calls are only possible here
    */
@Override
   
   public void init() 
   {
      final ControllerHost host = getHost();   
      
      host.getMidiInPort(0).setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi0(msg));
      this.bankHandler = new BankHandler(host);  
      this.trackHandler = new TrackHandler(host);
      this.channelStripHandler = new ChannelStripHandler(host);
      this.deviceHandler = new DeviceHandler(host);
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

   /** Called when we receive short MIDI message on port 0. */
   private void onMidi0(ShortMidiMessage msg) 
   {
	   
	   try {
		   if(bankHandler.handleMidi(msg)) return;  //let's first check if a bank has changed		   
		   if(trackHandler.handleMidi(msg)) return;
		   if(channelStripHandler.handleMidi(msg)) return;	
         if(deviceHandler.handleMidi(msg)) return;	   		
		   
	   }catch(Exception e) {
		   host.errorln("Something went wrong after the MFT sent a Midi message. The script could not handle this message correctly. ");
		   host.errorln("Exception thrown: " + e.getLocalizedMessage());
	   }   	   
   }   
}
