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

import java.util.List;

import de.mossgrabers.controller.osc.module.IModule;
import de.mossgrabers.controller.osc.protocol.OSCWriter;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;

/**
 * Simple helper class. We use this to initialize the OSC modules. 
 */
public class OSC_Helper {
    
    /**
     * Only message required here. It is called from Moss' OSC implementation to initialize the OSC Modules
     * @param modules a list of modueles
     * @param host the Bitwig Controller host 
     * @param model 
     * @param writer an OSC writer to write messages to OSC
     */
    public static void initOSC_Modules(List<IModule> modules, IHost host, IModel model, OSCWriter writer ){

        modules.add (new OSC_CommandsModule(host, model, writer));
        modules.add (new OSC_AddElementsModule(host, model, writer));
        modules.add (new OSC_ProgramChangeModule(host, model, writer));
    }


}
