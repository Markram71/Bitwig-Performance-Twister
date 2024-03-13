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

import de.drMartinKramer.MFT_Configuration;
import de.drMartinKramer.handler.AbstractHandler;
import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.controller.osc.module.AbstractModule;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import java.util.LinkedList;

 
 
 public class OSC_CommandsModule extends AbstractModule{

    final private IApplication application;
     
     /**
      * Constructor.
      *
      * @param host The host
      * @param model The model
      * @param writer The writer
      */
     public OSC_CommandsModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
     {
         super (host, model, writer);
         this.application = model.getApplication();
         //model.getApplication().
     }
 
 
 
     /** {@inheritDoc} */
     @Override
     public String [] getSupportedCommands ()
     {
         return new String []
         {
            "Commands",            
            "setClipLength", 
            "XY"          
         };
     }
 
 
     /** {@inheritDoc} */
     @Override
     public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
     {
        switch(command){
            case "Commands":
                executeCommands(path, value);
                break;
            case "setClipLength":
                executeSetClipLength(path, value);
                break;
            case "XY":
                executeXY_Message(path, value);
                break;
            default:
                throw new UnknownCommandException (command);
        }   
     }

     private void executeCommands(final LinkedList<String> path, final Object value) throws UnknownCommandException{
        final IArranger arrange = this.model.getArranger ();        
        switch (path.get(0))
         {
             case "addTrack":
                this.model.getApplication().addInstrumentTrack(); 
                break; 
             case "addAudio":
             this.model.getApplication().addAudioTrack(); 
                break;
             case "addFX":
                application.addEffectTrack();
                break;
            case "showArranger":
                application.setPanelLayout("ARRANGE");
                break;
            case "showMixer":
                application.setPanelLayout("MIX");
                break;
            case "showEditor":
                application.setPanelLayout("EDIT");
                break;
            case "toggleDevice":
                application.toggleDevices();
                break;
            case "toggleMixer":
                application.toggleMixer();
                break;
            case "toggleNoteEditor":
                application.toggleNoteEditor();
                break;            
            case "toggleArranger":
                host.println("From OSC: "+ path.get(0));
                break;
            case "toggleInspector":
                application.toggleInspector();
                break;
            case "toggleFullScreen":
                application.toggleFullScreen();
                break;            
            case "bounce":
                application.invokeAction("bounce");                
                break;
            case "bounceInPlace":
            application.invokeAction("bounce_in_place");                
                break;
            case "quantize":
                application.invokeAction("quantize_again");                
                break;
            case "incTrackHeight":
                application.incTrackHeight();
                break;
            case "decTrackHeight":
                application.decTrackHeight();
                break;
            case "toggleCueMarker":
                arrange.toggleCueMarkerVisibility ();
                break;
            case "togglePlaybackFollow":
                arrange.togglePlaybackFollow ();
                break;
            case "toggleTrackRowHeight":
                arrange.toggleTrackRowHeight ();
                break;
            case "toggleClipLauncher":
                arrange.toggleClipLauncher ();
                break;            
            case "toggleTimeLine":
                arrange.toggleTimeLine ();
                break;
            case "toggleIoSection":
                arrange.toggleIoSection ();
                break;
            case "toggleEffectTracks":
                arrange.toggleEffectTracks ();
                break;
            case "BitwigAction": 
                final String action = value.toString();
                application.invokeAction(action);
                break;
            
             default:
                 throw new UnknownCommandException (path.get(0));
         }
     }

     private void executeSetClipLength(final LinkedList<String> path, final Object value){
        final String lengthString = path.get(0);
        final Double clipLength = Double.parseDouble(lengthString);
        model.getCursorClip().setLoopLength(clipLength.doubleValue());         
     }

     private void executeXY_Message(final LinkedList<String> path, final Object value){
        try{ //we need to make a whole lot of risky stuff here (casting, parsing, etc.)
            //first parse the value array from OSC
            final Object[] valueArray = (Object[]) value;
            final int x = toInteger(valueArray[0]);
            final int y = toInteger(valueArray[1]);
            final int MidiChannel = toInteger(valueArray[2]);
            final int xCC = toInteger(valueArray[3]);
            final int yCC = toInteger(valueArray[4]); 
            
            //then send CC messages to Bitwig, one for x and one for y
            AbstractHandler.sendMidiToBitwig(0xB0 + MidiChannel, xCC, x);
            AbstractHandler.sendMidiToBitwig(0xB0 + MidiChannel, yCC, y); 
        }catch(Exception e){
            host.println("Error while parsing XY element: " + e.getLocalizedMessage());
        }
     }
}
     

 
 
 
 
 
 
 