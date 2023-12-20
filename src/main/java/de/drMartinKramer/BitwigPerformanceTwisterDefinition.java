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
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class BitwigPerformanceTwisterDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("782c549e-52ca-44e5-8135-50fb6a8dfcf7");
   
   public BitwigPerformanceTwisterDefinition()
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
      return "0.3";
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
      return "Midi Fighter Twister";
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

  @Override 
  /**
   * returns the URL to the readme file on github
   */
   public String getHelpFilePath()
   {
      return "https://github.com/Markram71/Bitwig-Performance-Twister/tree/main/docs";
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
   public BitwigPerformanceTwister createInstance(final ControllerHost host)
   {
      return new BitwigPerformanceTwister(this, host);
   }
}
