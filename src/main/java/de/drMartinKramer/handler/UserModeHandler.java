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

package de.drMartinKramer.handler;

import com.bitwig.extension.controller.api.ControllerHost;

import de.drMartinKramer.osc.OSC_UserModule;

/**
 * This is the handler for the user mode. And as you can see it does not really do anything. 
 * But we need a handler for consistency so that the mode changes, e.g. with the info to OSC
 * will be done correctly
 */
public class UserModeHandler extends AbstractHandler{
    public  UserModeHandler(ControllerHost host) 
	{
		super(host);

        this.oscModule = new OSC_UserModule(host);
    }
	
}
