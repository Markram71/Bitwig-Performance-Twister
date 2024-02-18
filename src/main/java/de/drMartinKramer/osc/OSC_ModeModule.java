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
    }
    
}
