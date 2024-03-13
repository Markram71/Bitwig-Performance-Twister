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

public class OSC_GlobalParameterModule extends AbstractMFT_OSC_Module{

    public OSC_GlobalParameterModule(ControllerHost host){
        super(host);

        for(int i=0;i<MFT_Hardware.MFT_NUMBER_OF_ENCODERS;i++){
            encoderState[i].exists = true;
            encoderState[i].isSelected = false;
        }
 
        this.encoderState[0].name = "Start/Pause";
        this.encoderState[0].turnFunction = "move (coarse)";
        this.encoderState[0].clickFunction = "start/pause";
        
        this.encoderState[1].name = "Stop";
        this.encoderState[1].turnFunction = "move (fine)";
        this.encoderState[1].clickFunction = "stop";

        this.encoderState[2].name = "X-Fader";
        this.encoderState[2].turnFunction = "crossfader";
        this.encoderState[2].clickFunction = "toggle record";

        this.encoderState[3].name = "Loop";
        this.encoderState[3].turnFunction = "loop start";
        this.encoderState[3].pushTurnFunction = "loop duration";        
        this.encoderState[3].clickFunction = "toggle loop";

        this.encoderState[4].name = "Fill";
        this.encoderState[4].turnFunction = "---";
        this.encoderState[4].clickFunction = "toggle fill";

        this.encoderState[5].name = "Overdub";
        this.encoderState[5].turnFunction = "---";
        this.encoderState[5].clickFunction = "t. clip overdub";
        this.encoderState[5].longClickFunction = "t. arr. overdub";

        this.encoderState[6].name = "Metronom";
        this.encoderState[6].turnFunction = "---";
        this.encoderState[6].clickFunction = "tgl. metronom";

        this.encoderState[7].name = "Tempo";
        this.encoderState[7].turnFunction = "change tempo";
        this.encoderState[7].pushTurnFunction = "change fine";
        this.encoderState[7].clickFunction = "tap tempo";

        this.encoderState[8].name = "Track";
        this.encoderState[8].turnFunction = "change track";
        this.encoderState[8].clickFunction = "prev. project";

        this.encoderState[9].name = "Master Vol.";
        this.encoderState[9].turnFunction = "---";
        this.encoderState[9].clickFunction = "next project";

        this.encoderState[10].name = "Bank";
        this.encoderState[10].turnFunction = "Bank LSB";
        this.encoderState[10].pushTurnFunction = "Bank MSB";        
        this.encoderState[10].clickFunction = "activate audio";

        this.encoderState[11].name = "Prgm Change";
        this.encoderState[11].turnFunction = "change prgm.";
        this.encoderState[11].pushTurnFunction = "change fast";        
        this.encoderState[11].clickFunction = "---";

        this.encoderState[12].name = "Zoom";
        this.encoderState[12].turnFunction = "zoom in/out";
        this.encoderState[12].clickFunction = "tgl. inspector";
        this.encoderState[12].longClickFunction = "toggle device";

        this.encoderState[13].name = "Arranger";
        this.encoderState[13].turnFunction = "---";
        this.encoderState[13].clickFunction = "arranger view";
        this.encoderState[13].longClickFunction = "toggle mixer";
        
        this.encoderState[14].name = "Mix";
        this.encoderState[14].turnFunction = "---";
        this.encoderState[14].clickFunction = "mix view";
        this.encoderState[14].longClickFunction = "tgl.  note editor";

        this.encoderState[15].name = "Patch";
        this.encoderState[15].turnFunction = "change patch";
        this.encoderState[15].clickFunction = "edit view";
        this.encoderState[15].longClickFunction = "tgl. full screen";

    }
    
}
