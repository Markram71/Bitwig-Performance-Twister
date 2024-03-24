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
 */


package de.drMartinKramer.osc;

import com.bitwig.extension.controller.api.ControllerHost;

import de.drMartinKramer.handler.ModeHandler;

public class OSC_ModeModule extends AbstractMFT_OSC_Module{

    private String MODE_ADDRESS = "/bpt_mode/";

    public OSC_ModeModule(ControllerHost host){
        super(host);
    }

    public void sendMode(int mode){
        sendOSC_Message(MODE_ADDRESS + "mixerMode",(mode == ModeHandler.MFT_MODE_MIXER ? 1:0) ,true);
        sendOSC_Message(MODE_ADDRESS + "eqMode",(mode == ModeHandler.MFT_MODE_EQ ? 1:0) ,true);
        sendOSC_Message(MODE_ADDRESS + "channelStripMode",(mode == ModeHandler.MFT_MODE_CHANNEL_STRIP ? 1:0) ,true);
        sendOSC_Message(MODE_ADDRESS + "deviceMode",(mode == ModeHandler.MFT_MODE_DEVICE ? 1:0) ,true);
        sendOSC_Message(MODE_ADDRESS + "globalParameterMode",(mode == ModeHandler.MFT_MODE_GLOBAL ? 1:0) ,true);
        sendOSC_Message(MODE_ADDRESS + "userMode",(mode == ModeHandler.MFT_MODE_USER ? 1:0) ,true);

        final String modeMessage;  
        switch(mode){
            case ModeHandler.MFT_MODE_MIXER:
                modeMessage = "Mixer mode: In this mode you can control the volume of 16 tracks. With a click on the controller " + 
                "you select the specific track. Pushing and turning let's add another function, e. g. the send to " + 
                "effect channel 1. You can also start scene and stop play mode by using the shift button (encoder 16)";
                break;
            case ModeHandler.MFT_MODE_EQ:
                modeMessage = "Equalizer Mode: Allows you to create an EQ+ at the end of the device chain. " +
                "When all encoders are out, no EQ+ exists, yet. With a click on any encoder you can add an EQ+. "+
                "Each column represents a frequency band. Thus, you can control up to four frequency bands. "+
                "The first row is to control the gain, the second is for the frequency, the third row changes the Q value, and  "+
                "the last row is for the type of the band. ";
                break;
            case ModeHandler.MFT_MODE_CHANNEL_STRIP:
                modeMessage = "Channel Strip mode: With this mode you can control many parameters of the selected track. "+
                "Use the first encoder to select the track. The second encoder is for the volume, the third for the pan, "+
                "encoder 5 to 8 are used for up to four send channels. The last two rows of encoders are used to control "+ 
                "the track remote controls";
                break;
            case ModeHandler.MFT_MODE_DEVICE:
                modeMessage = "Device mode: This is to control the currently selected device. The first two rows of encoders "+ 
                "control eight parameters of the device. Use the encoder clicks to move between devices and paramter banks for "+ 
                "each device. The last two rows of encoders are used to control the project remote controls." ;
                break;
            case ModeHandler.MFT_MODE_GLOBAL:
                modeMessage = "Global parameter mode: This mode is to control different global parameters. See the "+ 
                "info on encoders for more information on what each encoder does. ";
                break;
            case ModeHandler.MFT_MODE_USER:
                modeMessage = "User mode: This mode if to allow you to use the Bitwig Midi Mapping. Here CC messages of the "+ 
                "Midi Fighter Twister are directly routed to Bitwig. ";
                break;
            default: 
                modeMessage = "";                
        }
        sendOSC_Message(MODE_ADDRESS + "modeDescription",modeMessage,  true);
    }
    
}
