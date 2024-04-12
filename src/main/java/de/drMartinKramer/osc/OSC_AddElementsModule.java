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

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.controller.osc.module.AbstractModule;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import java.util.LinkedList;
import java.util.UUID;

import com.bitwig.extension.controller.api.InsertionPoint;


public class OSC_AddElementsModule extends AbstractModule{
    
    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    public OSC_AddElementsModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        super (host, model, writer);
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "addElements"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "addElements":
                executeAddElements (path, value);
                break;
            default:
                throw new UnknownCommandException (command);
        }
    }

    private void executeAddElements(LinkedList<String> path, Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (path.get(0))
        {
            case "Instrument":
                addInstrument(path, value);
                break;
            case "FX":
                addFX(path, value);
                break;            
            default:
                throw new UnknownCommandException (path.get(0));
        }
    }

    private void addInstrument(LinkedList<String> path, Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (path.get(1))
        {
            case "VST3":
                addVST3_Instrument(path, value);
                break;
            case "VST2":
                addVST2_Instrument(path, value);
                break;
            case "Bitwig":
                addBitwigInstrument(path, value);
                break;
            case "CLAP":
                addCLAP_Instrument(path, value);
                break;
            case "presetByNumber":
                addInstrumentPresetbyNumber(path, value);
                break;
            case "presetByName":
                addInstrumentPresetbyName(path, value);
                break;
            default:
                throw new UnknownCommandException (path.get(1));
        }
    }

    private void addFX(LinkedList<String> path, Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException{
        
        switch (path.get(1))
        {
            case "VST3":
                addVST3_FX(path, value);
                break;
            case "VST2":
                addVST2_FX(path, value);
                break;
            case "Bitwig":
                addBitwigFX(path, value);
                break;
            case "CLAP":
                addCLAP_FX(path, value);
                break;
            default:
                throw new UnknownCommandException (path.get(1));
        }
    }


    private void addVST3_Instrument(LinkedList<String> path, Object value) {
        this.model.getApplication().addInstrumentTrack();

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ()){ 
            InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
            endOfDeviceChainInsertionPoint.insertVST3Device(value.toString());  
        }
    }

    private void addVST2_Instrument(LinkedList<String> path, Object value) {
        this.model.getApplication().addInstrumentTrack();
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ()){ 
            InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
            endOfDeviceChainInsertionPoint.insertVST2Device(Integer.parseInt(value.toString()));  
        }
    }
        

    
    private void addBitwigInstrument(LinkedList<String> path, Object value) {
        this.model.getApplication().addInstrumentTrack();        
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ()){ 
            InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
            endOfDeviceChainInsertionPoint.insertBitwigDevice(UUID.fromString(value.toString()));  
        }
    }
    private void addCLAP_Instrument(LinkedList<String> path, Object value) {
        this.model.getApplication().addInstrumentTrack();
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ()){ 
            InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
            endOfDeviceChainInsertionPoint.insertCLAPDevice(value.toString());  
        }
    }

    private void addInstrumentPresetbyName(LinkedList<String> path, Object value) {
        try{
            this.model.getApplication().addInstrumentTrack();final ITrack cursorTrack = this.model.getCursorTrack ();
            final Object[] valueArray = (Object[]) value;
            final String filePath = valueArray[0].toString();
            final String instrumentName = valueArray[1].toString();
            
            host.println("Load Instrument: " + filePath + instrumentName);
            if (cursorTrack.doesExist ()){ 
                InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
                endOfDeviceChainInsertionPoint.insertFile(filePath + "/" + instrumentName);  
            }
            
        }catch(Exception e){
            host.println("Error while parsing XY element: " + e.getLocalizedMessage());
        }
    }



    private void addInstrumentPresetbyNumber(LinkedList<String> path, Object value) {
        try{
            this.model.getApplication().addInstrumentTrack();final ITrack cursorTrack = this.model.getCursorTrack ();
            final Object[] valueArray = (Object[]) value;
            
            if (cursorTrack.doesExist ()){ 
                InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
                endOfDeviceChainInsertionPoint.insertBitwigDevice(UUID.fromString(valueArray[0].toString()));  
            }
            final int position = toInteger(valueArray[1]);
            final IBrowser browser = this.model.getBrowser ();
            browser.replace (this.model.getCursorDevice ());
            
            final int browserDelay = 200;
            int delayTime = browserDelay;
            for (int i = 0;i<position; i++){
                host.scheduleTask((Runnable)()->{browser.selectNextResult ();}, delayTime);
                delayTime += browserDelay; 
            }
            host.scheduleTask((Runnable)()->{browser.stopBrowsing (true);}, delayTime);
            
        }catch(Exception e){
            host.println("Error while parsing XY element: " + e.getLocalizedMessage());
        }
    }

    private void addBitwigFX(LinkedList<String> path, Object value) {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ()){ 
            InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
            endOfDeviceChainInsertionPoint.insertBitwigDevice(UUID.fromString(value.toString()));  
        }
    }
    private void addVST3_FX(LinkedList<String> path, Object value) {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ()){ 
            InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
            endOfDeviceChainInsertionPoint.insertVST3Device(value.toString());  
        }
    }
    private void addVST2_FX(LinkedList<String> path, Object value) {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ()){ 
            InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
            endOfDeviceChainInsertionPoint.insertVST2Device(Integer.parseInt(value.toString()));  
        }
    }
    private void addCLAP_FX(LinkedList<String> path, Object value) {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack.doesExist ()){ 
            InsertionPoint endOfDeviceChainInsertionPoint = cursorTrack.endOfDeviceChainInsertionPoint();
            endOfDeviceChainInsertionPoint.insertCLAPDevice(value.toString());  
        }
    }
}






