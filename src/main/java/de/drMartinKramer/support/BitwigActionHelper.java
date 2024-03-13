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


package de.drMartinKramer.support;

import java.util.HashMap;

import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ActionCategory;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;

import de.drMartinKramer.MFT_Configuration;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class BitwigActionHelper {

    final ControllerHost host;
    final private String [] categoryNames;
    final Application application;
    final ActionCategory [] categories;
   


    class ActionRecord{
        String actionCategory;
        String actioName;
        String actionId;
    }

    private HashMap<String, ActionRecord> actionRecords = new HashMap<String, ActionRecord>();

    public BitwigActionHelper (ControllerHost host)
    {
        this.host = host;
        application = this.host.createApplication ();
        categories = application.getActionCategories ();        
        categoryNames = new String [categories.length];
        for (int i = 0; i < categories.length; i++)
        {
            final String categoryName = categories[i].getName ();
            categoryNames[i] = categoryName;

            final Action [] actions = categories[i].getActions ();
            final String [] actionNames = new String [actions.length];

            for (int j = 0; j < actions.length; j++)
            {
                actionNames[j] = actions[j].getName ();
                final String actionId = actions[j].getId ();
                ActionRecord record = new ActionRecord();
                record.actionCategory = categoryName;
                record.actioName = actionNames[j];
                record.actionId = actionId; 
                actionRecords.put(actionId, record);
                //MFT_Configuration.println(record.actionCategory + " : " + record.actionId + " : " + record.actioName); 
            }            
        }
    }

    
    public void saveActionList(){
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("BitwigActions.txt", true));
            for (String key : actionRecords.keySet()) {
                ActionRecord record = actionRecords.get(key);
                writer.println(record.actionCategory + ";" + record.actionId + ";" + record.actioName);
            }
            writer.close();
            MFT_Configuration.println("Action List saved successfully.");
        } catch (IOException e) {
            MFT_Configuration.println("Error saving Action List: " + e.getMessage());
        }
    }

    
    
}
