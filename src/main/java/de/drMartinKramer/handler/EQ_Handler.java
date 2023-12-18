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

import java.util.UUID;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceMatcher;
import com.bitwig.extension.controller.api.Parameter;

import de.drMartinKramer.support.MFT_MidiMessage;

public class EQ_Handler  extends AbstractHandler
{

    private final String BITWIG_EQ_PLUS_DEVICE_ID =  "e4815188-ba6f-4d14-bcfc-2dcb8f778ccb"; 
    private Device eqPlusDevice = null;

    public EQ_Handler(ControllerHost host) {
        super(host);
        CursorTrack cursorTrack = host.createCursorTrack(0, 0);
        DeviceBank eqPlusFilterDeviceBank = cursorTrack.createDeviceBank(1);
        UUID eqUUID = UUID.fromString(BITWIG_EQ_PLUS_DEVICE_ID);
        DeviceMatcher eqPlusFilterDeviceMatcher = host.createBitwigDeviceMatcher(eqUUID);
        eqPlusFilterDeviceBank.setDeviceMatcher(eqPlusFilterDeviceMatcher);

        this.eqPlusDevice = eqPlusFilterDeviceBank.getItemAt(0);
        eqPlusDevice.exists().addValueObserver((exists)-> reactToEQ_Exists(exists));
        Parameter myParameter1 = null ; //TODO: here geht's weiter
  


        println("EQ Handler created");

    }

    private void reactToEQ_Exists(boolean exists) {
        println("EQ Exists: " + exists);
    }
    

    public boolean handleMidi (MFT_MidiMessage msg){
        if(this.eqPlusDevice.exists().get()){
            println("EQ device exists");
            return false;
        }else println("No EQ Plus Device");
        return false;
    }
    
}
