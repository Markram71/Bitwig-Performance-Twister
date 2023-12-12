package de.drMartinKramer;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class MidiFighterTwisterExtensionExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("782c549e-52ca-44e5-8135-50fb6a8dfcf7");
   
   public MidiFighterTwisterExtensionExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "Midi Fighter Twister";
   }
   
   @Override
   public String getAuthor()
   {
      return "Martin Kramer";
   }

   @Override
   public String getVersion()
   {
      return "0.2";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "DJ Techtools";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "Midi Fighter Twister (Java)";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 18;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 1;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 1;
   }

   
   /** {@inheritDoc} */
   @Override
   public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
       // Note: Only an example on my system. If you use the MOXF with USB replace these with tzhe
       // correct names

       list.add (new String []
       {
           "Midi Fighter Twister"
       }, new String []
       {
           "Midi Fighter Twister"
       });
   }
   
   
   @Override
   public MidiFighterTwisterExtensionExtension createInstance(final ControllerHost host)
   {
      return new MidiFighterTwisterExtensionExtension(this, host);
   }
}
