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


package de.drMartinKramer;

import com.bitwig.extension.callback.EnumValueChangedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;

import de.drMartinKramer.hardware.MFT_Hardware;

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
    
    private static double GLOBAL_TURN_SPEED_UP_MAX = 10;
    private static SettableRangedValue globalTurnFactor  = null;
    private static SettableRangedValue globalClickDownTurnFactor  = null;


    // Global config: show messages, or not
    private static SettableEnumValue globalShowMessagesSetting  = null;
    public static final String GLOBAL_SHOW_MESSAGES_YES = "yes";
    public static final String GLOBAL_SHOW_MESSAGES_NO = "no";
    private final static String[] globalShowMessagesStrings = {
        GLOBAL_SHOW_MESSAGES_YES,
        GLOBAL_SHOW_MESSAGES_NO       
    }; 

    // Global config: show messages, or not
    private static SettableEnumValue globalFirstHandlerSetting  = null;
    public static final String GLOBAL_FIRST_HANDLER_MIXER = "Mixer";
    public static final String GLOBAL_FIRST_HANDLER_CHANNEL_STRIP = "Channel Strip";
    public static final String GLOBAL_FIRST_HANDLER_EQ = "EQ";
    public static final String GLOBAL_FIRST_HANDLER_DEVICE = "Device and Global Remote Controls";
    public static final String GLOBAL_FIRST_HANDLER_GLOBAL_PARAMETERS = "Global Parameters";
    public static final String GLOBAL_FIRST_HANDLER_USER = "User";
    private final static String[] globalFirstHandlerStrings = {
        GLOBAL_FIRST_HANDLER_MIXER,
        GLOBAL_FIRST_HANDLER_CHANNEL_STRIP,
        GLOBAL_FIRST_HANDLER_EQ,
        GLOBAL_FIRST_HANDLER_DEVICE,
        GLOBAL_FIRST_HANDLER_GLOBAL_PARAMETERS,
        GLOBAL_FIRST_HANDLER_USER    
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
    

    // Configuration for the click and turn function in the mixer
    private static SettableEnumValue mixerClickAndTurnFunctionSetting  = null;
    public static final String MIXER_CLICK_AND_TURN_FUNCTION_PAN  = "Pan";
    public static final String MIXER_CLICK_AND_TURN_FUNCTION_SEND1  = "Send to fx 1";
    public static final String MIXER_CLICK_AND_TURN_FUNCTION_TRACK_REMOTE1  = "Track remote 1";
    
    private final static String[] mixerClickAndTurnFunctionStrings = {
        MIXER_CLICK_AND_TURN_FUNCTION_PAN,
        MIXER_CLICK_AND_TURN_FUNCTION_SEND1,    
        MIXER_CLICK_AND_TURN_FUNCTION_TRACK_REMOTE1
    }; 

    // Configuration for the shift click in the mixer
    static SettableEnumValue mixerShiftClickActionSetting  = null;
    public static final String MIXER_SHIFT_CLICK_ACTION_SCENE  = "Scene";
    public static final String MIXER_SHIFT_CLICK_ACTION_CUE_MARKER  = "Cue Marker";
    
    private final static String[] mixerShiftClickActionStrings = {
        MIXER_SHIFT_CLICK_ACTION_SCENE, 
        MIXER_SHIFT_CLICK_ACTION_CUE_MARKER
    }; 


    //----------CHANNEL STRIP ----------------------------------------------------------

    // Button 4 im Channel Strip: Master volume or cross fader of cue volumen
    private static SettableEnumValue channelStripEncoder4Setting  = null;
    public static final String CHANNEL_STRIP_ENCODER_4_MASTERVOLUME = "Master volume";
    public static final String CHANNEL_STRIP_ENCODER_4_CROSSFADER = "Crossfader";
    public static final String CHANNEL_STRIP_ENCODER_4_CUE_VOLUME = "Cue volume";    
    private final static String[] channelStripEncoder4SettingStrings = {
        CHANNEL_STRIP_ENCODER_4_MASTERVOLUME,
        CHANNEL_STRIP_ENCODER_4_CROSSFADER, 
        CHANNEL_STRIP_ENCODER_4_CUE_VOLUME      
    };

    // Configuration for the shift click in the mixer
    static SettableEnumValue channelStripShiftClickActionSetting  = null;
    public static final String CHANNEL_STRIP_SHIFT_CLICK_ACTION_SCENE  = "Scene";
    public static final String CHANNEL_STRIP_SHIFT_CLICK_ACTION_CUE_MARKER  = "Cue Marker";
    
    private final static String[] channelStripShiftClickActionStrings = {
        CHANNEL_STRIP_SHIFT_CLICK_ACTION_SCENE, 
        CHANNEL_STRIP_SHIFT_CLICK_ACTION_CUE_MARKER
    }; 


    //------ Global Parameter  -----------------------------------------------------
    // Configuration for the shift click in the mixer
    static SettableEnumValue globalParameterBankSelectSetting  = null;
    public static final String GLOBAL_PARAMETER_BANK_SELECT_NO  = "no";
    public static final String GLOBAL_PARAMETER_BANK_SELECT_YES  = "yes";
    
    private final static String[] globalParameterBankSelectStrings = {
        GLOBAL_PARAMETER_BANK_SELECT_NO, 
        GLOBAL_PARAMETER_BANK_SELECT_YES
    }; 



    //--------- OSC - Preferences --------------------------------------------------
    private static SettableRangedValue oscPortToReceiveOnSetting  = null;
    private static SettableRangedValue oscPortToSendToSetting  = null;
    private static SettableStringValue oscHostToSendToSetting  = null;

    /**
     * Constructor. Takes the Bitwig controller host. 
     * It is not really need to create an object of this class, but it is needed to initialize the static variables
     * @param controllerHost the Bitwig controller host is stored in a static variable
     */
    public MFT_Configuration(ControllerHost controllerHost){
        MFT_Configuration.host = controllerHost;  
        MFT_Configuration.preferences = MFT_Configuration.host.getPreferences();
        
        //Global Configurations
        MFT_Configuration.mixerMakeVisibleSetting = preferences.getEnumSetting( "Make tracks visible", 
                                                                                "Mixer", 
                                                                                mixerMakeVisibleSettingStrings, 
                                                                                mixerMakeVisibleSettingStrings[0]);
         MFT_Configuration.globalShowMessagesSetting = preferences.getEnumSetting( "Show pop up notifications", 
                                                                                "Global", 
                                                                                globalShowMessagesStrings, 
                                                                                globalShowMessagesStrings[0]);
        
        MFT_Configuration.globalFirstHandlerSetting = preferences.getEnumSetting( "First Mode", 
                                                                                "Global", 
                                                                                globalFirstHandlerStrings, 
                                                                                globalFirstHandlerStrings[0]);
       
        
        MFT_Configuration.globalTurnFactor = preferences.getNumberSetting("Encoder turn speedup factor ", 
                                                                                "Global", 0, 
                                                                                GLOBAL_TURN_SPEED_UP_MAX, 0.1, 
                                                                                "", 1);


        MFT_Configuration.globalClickDownTurnFactor = preferences.getNumberSetting("Encoder turn speedup factor when clicked down", 
                                                                                "Global", 0, 
                                                                                GLOBAL_TURN_SPEED_UP_MAX, 0.1, 
                                                                                "", 3);
// 
        //Mixer / Track Mode  Configurations
        MFT_Configuration.mixerLongButtonSetting = preferences.getEnumSetting("Long Click Action", "Mixer", mixerLongButtonClickActions, mixerLongButtonClickActions[0]);
        
        MFT_Configuration.mixerClickAndTurnFunctionSetting = preferences.getEnumSetting( "Click&Turn function", 
                                                                                "Mixer", 
                                                                                mixerClickAndTurnFunctionStrings, 
                                                                                mixerClickAndTurnFunctionStrings[2]);
        MFT_Configuration.mixerShiftClickActionSetting = preferences.getEnumSetting( "Shift click action", 
                                                                                "Mixer", 
                                                                                mixerShiftClickActionStrings, 
                                                                                mixerShiftClickActionStrings[0]);
       
       
        MFT_Configuration.globalLongClickMillis = preferences.getNumberSetting("Long click duration in milliseconds", 
                                                                                "Global", 0, 
                                                                                GLOBAL_LONG_CLICK_MILLIS_MAX, 10, 
                                                                                "ms", 500);



        //Channel Strip Configurations
        MFT_Configuration.channelStripEncoder4Setting = preferences.getEnumSetting("Channel strip Encoder #4 function", "Channel Strip", channelStripEncoder4SettingStrings, channelStripEncoder4SettingStrings[0]);
    
        MFT_Configuration.channelStripShiftClickActionSetting = preferences.getEnumSetting( "Shift click action", 
                                                                                "Channel Strip", 
                                                                                channelStripShiftClickActionStrings, 
                                                                                channelStripShiftClickActionStrings[1]);
    
    
    
        MFT_Configuration.globalParameterBankSelectSetting = preferences.getEnumSetting( "Send bank select MSB/LSB", 
                                                                                "Global Parameters", 
                                                                                globalParameterBankSelectStrings, 
                                                                                globalParameterBankSelectStrings[0]);
    
       
        /* MFT_Configuration.oscPortToReceiveOnSetting = preferences.getNumberSetting("Port to receive on", 
                                                                                "OSC Settings", 0, 
                                                                                64000, 1.0, 
                                                                                "", 8000);
       
        MFT_Configuration.oscHostToSendToSetting = preferences.getStringSetting("Host to send to (requires restart)","OSC Settings",20,"127.0.0.1" );
        
        MFT_Configuration.oscPortToSendToSetting = preferences.getNumberSetting("Port to send to (requires restart)", 
                                                                                "OSC Settings", 0, 
                                                                                64000, 1.0, 
                                                                                "", 9000);
                                                                                
        */
    }  //end of constructor

    // GLBOAL FUNCTIONS --------------------------------------------------------

    
    public static double getGlobalLongClickMillis(){
        return MFT_Configuration.globalLongClickMillis.getAsDouble() * GLOBAL_LONG_CLICK_MILLIS_MAX;
    }

    public static boolean showPupupNotifications(){
        return MFT_Configuration.globalShowMessagesSetting.get().equals(GLOBAL_SHOW_MESSAGES_YES);
    }
    
    public static int getFirstMode()
    {
        if(globalFirstHandlerSetting == null) return MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_1; //default, the mixer, on the first left button
        
        String firstMode = MFT_Configuration.globalFirstHandlerSetting.get();
        if(firstMode.equals(GLOBAL_FIRST_HANDLER_MIXER)) return MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_1;
        else if(firstMode.equals(GLOBAL_FIRST_HANDLER_CHANNEL_STRIP)) return MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_1;
        else if(firstMode.equals(GLOBAL_FIRST_HANDLER_EQ)) return MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_2;
        else if(firstMode.equals(GLOBAL_FIRST_HANDLER_DEVICE)) return MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_2;
        else if(firstMode.equals(GLOBAL_FIRST_HANDLER_GLOBAL_PARAMETERS)) return MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_3;
        else if(firstMode.equals(GLOBAL_FIRST_HANDLER_USER)) return MFT_Hardware.MFT_SIDE_BUTTON_CC_RIGHT_3;
        else return MFT_Hardware.MFT_SIDE_BUTTON_CC_LEFT_1; //default, the mixer, on the first left button
    }

    //------- MIXER --------------------------
    /**
     * Function to let us know what should happen when the user clicks a button in the mixer bank for a long time.
     * @return true, if within the mixer bank, the long click action is set to "Solo"
     */
    public static boolean isMixerLongButtonActionSolo(){
        return MFT_Configuration.mixerLongButtonSetting.get().equals(MIXER_LONG_BUTTON_ACTION_SOLO);
    }

    public static boolean isMixerLongButtonActionArm(){
        return MFT_Configuration.mixerLongButtonSetting.get().equals(MIXER_LONG_BUTTON_ACTION_ARM);
    }
    public static boolean isMixerLongButtonActionMute(){
        return MFT_Configuration.mixerLongButtonSetting.get().equals(MIXER_LONG_BUTTON_ACTION_MUTE);
    }

    /**
     * Method allows to set a callback for the setting of the long button click in the mixer. 
     * The OSC module will call this method to stay informed potential changes so that it 
     * can update the OSC surface
     * @param callback a callback method that is called when the setting is changed. 
     */
    public static void addValueObserver_MixerLongButton(EnumValueChangedCallback callback){
        MFT_Configuration.mixerLongButtonSetting.addValueObserver(callback);
    }

    
    public static boolean mixerMakeVisible(){
        return MFT_Configuration.mixerMakeVisibleSetting.get().equals(MIXER_MAKE_VISIBLE_YES);
    }


    public static boolean isMixerPushAndTurnFunctionPan(){
        return MFT_Configuration.mixerClickAndTurnFunctionSetting.get().equals(MIXER_CLICK_AND_TURN_FUNCTION_PAN);
    }
    public static boolean isMixerPushAndTurnFunctionSend1(){
        return MFT_Configuration.mixerClickAndTurnFunctionSetting.get().equals(MIXER_CLICK_AND_TURN_FUNCTION_SEND1);
    }
    public static boolean isMixerPushAndTurnFunctionTrackRemote1(){
        return MFT_Configuration.mixerClickAndTurnFunctionSetting.get().equals(MIXER_CLICK_AND_TURN_FUNCTION_TRACK_REMOTE1);
    }
    
    /**
     * Method allows to set a callback for the setting of the click and turn function in the mixer. 
     * The OSC module will call this method to stay informed potential changes so that it 
     * can update the OSC surface
     * @param callback a callback method that is called when the setting is changed. 
     */
    public static void addValueObserver_MixerPushAndTurnFunction(EnumValueChangedCallback callback){
        MFT_Configuration.mixerClickAndTurnFunctionSetting.addValueObserver(callback);
    }
    
    
    public static boolean isMixerShiftClickActionScene(){
        return MFT_Configuration.mixerShiftClickActionSetting.get().equals(MIXER_SHIFT_CLICK_ACTION_SCENE);
    }
    public static boolean isMixerShiftClickActionCueMarker(){
        return MFT_Configuration.mixerShiftClickActionSetting.get().equals(MIXER_SHIFT_CLICK_ACTION_CUE_MARKER);
    }

    /**
     * Method allows to set a callback for the setting of the shift click in the mixer. 
     * The OSC module will call this method to stay informed potential changes so that it 
     * can update the OSC surface
     * @param callback a callback method that is called when the setting is changed. 
     */
    public static void addValueObserver_MixerShiftClick(EnumValueChangedCallback callback){
        MFT_Configuration.mixerShiftClickActionSetting.addValueObserver(callback);
    }

    
    //------- CHANNEL STRIP ------------------------

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

    /**
     * Method allows to set a callback for the setting of the shift click in the mixer. 
     * The OSC module will call this method to stay informed potential changes so that it 
     * can update the OSC surface
     * @param callback a callback method that is called when the setting is changed. 
     */
    public static void addValueObserver_ChannelStripEncoder4(EnumValueChangedCallback callback){
        MFT_Configuration.channelStripEncoder4Setting.addValueObserver(callback);
    }

    public static boolean isChannelStripShiftClickActionScene(){
        return MFT_Configuration.channelStripShiftClickActionSetting.get().equals(CHANNEL_STRIP_SHIFT_CLICK_ACTION_SCENE);
    }
    public static boolean isChannelStripShiftClickActionCueMarker(){
        return MFT_Configuration.channelStripShiftClickActionSetting.get().equals(CHANNEL_STRIP_SHIFT_CLICK_ACTION_CUE_MARKER);
    }

    /**
     * Method allows to set a callback for the setting of the shift click in the channel strip. 
     * The OSC module will call this method to stay informed potential changes so that it 
     * can update the OSC surface
     * @param callback a callback method that is called when the setting is changed. 
     */
    public static void addValueObserver_ChannelStripShiftClick(EnumValueChangedCallback callback){
        MFT_Configuration.channelStripShiftClickActionSetting.addValueObserver(callback);
    }

    /**
     * A factor to increase or decrease the turning speed in normal case when the encoder is not clicked down.
     * @return a factor to multiply the turn speed. 
     */
    public static double getNormalTurnFactor(){
        return MFT_Configuration.globalTurnFactor.getAsDouble() *GLOBAL_TURN_SPEED_UP_MAX;
    }

    /**
     *  A factor to increase or decrease the turning speed when the encoder is clicked down.
     * @return the factor by which the encoder turn is multiplied to get the actual turn value
     */
    public static double getClickTurnFactor(){
        return MFT_Configuration.globalClickDownTurnFactor.getAsDouble() *GLOBAL_TURN_SPEED_UP_MAX;
    }
  
    // Global Parameter Moder ---------------------------------------------------
    public static boolean isGlobalParameterBankSelectSend(){
        return MFT_Configuration.globalParameterBankSelectSetting.get().equals(GLOBAL_PARAMETER_BANK_SELECT_YES);
    }


    /**
     * Static convinience method to print a message to the console from anywhere in the code. 
     * @param msg Message to be printed to the Bitwig console
     */
    public static void println(String msg){
        host.println(msg);
    }



    // OSC Settings ------------------------------------------------------------
    public static int getOscPortToReceiveOn(){
        final  int result = (int)MFT_Configuration.oscPortToReceiveOnSetting.getAsDouble();
        //Todo hier läuft es etwas schief....
        println("OSCPort to receive on: " + result );
        return 8000;
    } 
    public static int getOscPortToSendTo(){
        final int result = (int)MFT_Configuration.oscPortToSendToSetting.getAsDouble();
        println("OSCPort to send to: " + result );
        
        return 9000;
    }
    public static String getOscHostToSendTo(){
        final String result = MFT_Configuration.oscHostToSendToSetting.get();
        return result;
    }
}
