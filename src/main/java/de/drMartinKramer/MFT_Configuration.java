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


package de.drMartinKramer;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;

/**
 * This is the access to the configration of the controller script within Bitwig. 
 * We encapsulate the configuration and the access to Bitwig to read and set it
 * within this class here. 
 */
public class MFT_Configuration {

    private static ControllerHost host = null;
    private static Preferences preferences = null;


    

    //GLOBAL CONFIGURATION -----------------------------------------------------
    // Global configuration for long clicks 
    public static final double GLOBAL_LONG_CLICK_MILLIS_MAX = 2000;
    private static SettableRangedValue globalLongClickMillis  = null;
    
    // Configuration for the long clicks in the mixer bank
    private static SettableEnumValue mixerLongButtonSetting  = null;
    public static final String MIXER_LONG_BUTTON_ACTION_SOLO = "Solo";
    public static final String MIXER_LONG_BUTTON_ACTION_ARM = "RecArm";
    public static final String MIXER_LONG_BUTTON_ACTION_MUTE = "Mute";
    private final static String[] mixerLongButtonClickActions = {
        MIXER_LONG_BUTTON_ACTION_SOLO,
        MIXER_LONG_BUTTON_ACTION_ARM,
        MIXER_LONG_BUTTON_ACTION_MUTE       
    }; 
   
    // MIXER CONFIGURATION -----------------------------------------------------
    // Configuration for making the newly clicked tracks visible in the mixer and the arranger
    private static SettableEnumValue mixerMakeVisibleSetting  = null;
    public static final String MIXER_MAKE_VISIBLE_YES = "yes";
    public static final String MIXER_MAKE_VISIBLE_NO = "no";
    private final static String[] mixerMakeVisibleSettingStrings = {
        MIXER_MAKE_VISIBLE_YES,
        MIXER_MAKE_VISIBLE_NO       
    }; 
    
    
    // Button 4 im Mixer: Master volume
    private static SettableEnumValue channelStripEncoder4Setting  = null;
    public static final String CHANNEL_STRIP_ENCODER_4_MASTERVOLUME = "Master volume";
    public static final String CHANNEL_STRIP_ENCODER_4_CROSSFADER = "Crossfader";
    public static final String CHANNEL_STRIP_ENCODER_4_CUE_VOLUME = "Cue volume";    
    private final static String[] channelStripEncoder4SettingStrings = {
        CHANNEL_STRIP_ENCODER_4_MASTERVOLUME,
        CHANNEL_STRIP_ENCODER_4_CROSSFADER, 
        CHANNEL_STRIP_ENCODER_4_CUE_VOLUME      
    };


    /**
     * Constructor. Takes the Bitwig controller host. 
     * It is not really need to create an object of this class, but it is needed to initialize the static variables
     * @param controllerHost the Bitwig controller host is stored in a static variable
     */
    public MFT_Configuration(ControllerHost controllerHost){
        MFT_Configuration.host = controllerHost;  
        MFT_Configuration.preferences = MFT_Configuration.host.getPreferences();
        
        //Global Configurations
        MFT_Configuration.mixerMakeVisibleSetting = preferences.getEnumSetting("Make tracks visible", "Global", mixerMakeVisibleSettingStrings, mixerMakeVisibleSettingStrings[0]);
        MFT_Configuration.globalLongClickMillis = preferences.getNumberSetting("Long click duration in milliseconds", "Global", 0, GLOBAL_LONG_CLICK_MILLIS_MAX, 10, "ms", 500);

        //Mixer / Track Bank Configurations
        MFT_Configuration.mixerLongButtonSetting = preferences.getEnumSetting("Long Click Action", "Mixer", mixerLongButtonClickActions, mixerLongButtonClickActions[0]);
        
        //Channel Strip Configurations
        MFT_Configuration.channelStripEncoder4Setting = preferences.getEnumSetting("Channel strip Encoder #4 function", "Channel Strip", channelStripEncoder4SettingStrings, channelStripEncoder4SettingStrings[0]);
    }  

    public static boolean isMixerLongButtonActionSolo(){
        return MFT_Configuration.mixerLongButtonSetting.get().equals(MIXER_LONG_BUTTON_ACTION_SOLO);
    }
    public static boolean isMixerLongButtonActionArm(){
        return MFT_Configuration.mixerLongButtonSetting.get().equals(MIXER_LONG_BUTTON_ACTION_ARM);
    }
    public static boolean isMixerLongButtonActionMute(){
        return MFT_Configuration.mixerLongButtonSetting.get().equals(MIXER_LONG_BUTTON_ACTION_MUTE);
    }
    public static boolean mixerMakeVisible(){
        return MFT_Configuration.mixerMakeVisibleSetting.get().equals(MIXER_MAKE_VISIBLE_YES);
    }
    public static double getGlobalLongClickMillis(){
        return MFT_Configuration.globalLongClickMillis.getAsDouble() * GLOBAL_LONG_CLICK_MILLIS_MAX;
    }
    
    //Channel Strip Encoder 4
    public static boolean isChannelStripEncoder4_MasterVolume(){
        return MFT_Configuration.channelStripEncoder4Setting.get().equals(CHANNEL_STRIP_ENCODER_4_MASTERVOLUME);
    }
    public static boolean isChannelStripEncoder4_Crossfader(){
        return MFT_Configuration.channelStripEncoder4Setting.get().equals(CHANNEL_STRIP_ENCODER_4_CROSSFADER);
    }
    public static boolean isChannelStripEncoder4_CueVolume(){
        return MFT_Configuration.channelStripEncoder4Setting.get().equals(CHANNEL_STRIP_ENCODER_4_CUE_VOLUME);
    }


    public static void println(String msg){
        host.println(msg);
    }
}
