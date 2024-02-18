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

import de.drMartinKramer.hardware.MFT_Hardware;

public class OSC_DeviceModule extends AbstractMFT_OSC_Module{

    public OSC_DeviceModule(ControllerHost host){
        super(host);

        for(int i= 0;i<8;i++){
            encoderState[i].turnFunction = "Device Param " + (i+1);
            encoderState[8+i].turnFunction = "Prj Remote " + (i+1);
        }

        for(int i=0;i<MFT_Hardware.MFT_NUMBER_OF_ENCODERS;i++){
            encoderState[i].isSelected = false;
        }

        encoderState[0].clickFunction = "first device";
        encoderState[1].clickFunction = "prev. device";
        encoderState[2].clickFunction = "next device";
        encoderState[3].clickFunction = "last device";
        encoderState[4].clickFunction = "1. param bank";
        encoderState[5].clickFunction = "prev p.  bank";
        encoderState[6].clickFunction = "next p. bank";
        encoderState[7].clickFunction = "last p. bank";
        encoderState[8].clickFunction = "";
        encoderState[9].clickFunction = "";
        encoderState[10].clickFunction = "";
        encoderState[11].clickFunction = "";
        encoderState[12].clickFunction = "1. prj.p. bank";
        encoderState[13].clickFunction = "prev prj.p. bank";
        encoderState[14].clickFunction = "next prj.p. bank";
        encoderState[15].clickFunction = "last prj.p. bank";

        encoderState[0].longClickFunction = "toggle dev. on/off";
    }
    
}
