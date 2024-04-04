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

import java.util.LinkedList;
import java.util.UUID;

import com.bitwig.extension.controller.api.InsertionPoint;

import de.drMartinKramer.handler.AbstractHandler;
import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.controller.osc.module.AbstractModule;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;

/**
 * 
 */
public class OSC_ProgramChangeModule extends AbstractModule{
   
    private int lastMidiChannel = 1;
    private int lastProgramChangeNumber = 1;
   
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public OSC_ProgramChangeModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "programChange"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "programChange":
                executeProgramChangeMessage(command, path, value);
                break;
            default:
                throw new UnknownCommandException (command);
        }
    }

    private void executeProgramChangeMessage(final String command, final LinkedList<String> path, final Object value){
        String subCommand = path.get(0);
        switch (subCommand) {
            case "set":
                setProgramChangeMessage(value);
                break;
            case "+": 
                incProgramChangeMessage(value);
                break;
            case "-": 
                decProgramChangeMessage(value);
                break;        
            default:
                break;
        }
    }

    private void incProgramChangeMessage(final Object value){
        try {
            final int delta = toInteger(value);
            lastProgramChangeNumber+=delta;
            if(lastProgramChangeNumber>128)lastProgramChangeNumber=1;
            AbstractHandler.sendMidiToBitwig(0xC0 + lastMidiChannel, lastProgramChangeNumber-1, 0);
            host.showNotification("Program Change " + (lastProgramChangeNumber-1));
        }catch(Exception e){
            host.println("Error while parsing Program Change + Message : " + e.getLocalizedMessage());
        }
    }

    private void decProgramChangeMessage(final Object value){
        try{
            final int delta = toInteger(value);
            lastProgramChangeNumber-=delta;
            if(lastProgramChangeNumber<1)lastProgramChangeNumber=128;
            AbstractHandler.sendMidiToBitwig(0xC0 + lastMidiChannel, lastProgramChangeNumber-1, 0);
            host.showNotification("Program Change " + (lastProgramChangeNumber-1));
        }catch(Exception e){
            host.println("Error while parsing Program Change - Message : " + e.getLocalizedMessage());
        }
    }

    private void setProgramChangeMessage(final Object value){    
        try{
            //first let's parse the program Change infos from OSC
            final Object[] valueArray = (Object[]) value;
            final int midiChannel = toInteger(valueArray[0]);
            this.lastMidiChannel = midiChannel; //store the last MidiChannel
            final int programChangeNumber = toInteger(valueArray[1]);
            this.lastProgramChangeNumber = programChangeNumber;

            //then send CC messages to Bitwig, one for x and one for y
            AbstractHandler.sendMidiToBitwig(0xC0 + midiChannel, programChangeNumber-1, 0);
            host.showNotification("Program Change " + (programChangeNumber-1));
        }catch(Exception e){
            host.println("Error while parsing Program Change Message : " + e.getLocalizedMessage());
        }
    }

  

}
