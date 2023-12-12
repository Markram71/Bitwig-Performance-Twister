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



package de.drMartinKramer.hardware;

public class MFT_Hardware
{

	public static final int MFT_No_ENCODER = 16;
	public static final int MFG_NUMBER_OF_ENCODERS = 16;
	//Bank1: The CC number for each of the  encoders in this bank
	public static final int MFT_BANK1_BUTTON_01 = 0x00; 
	public static final int MFT_BANK1_BUTTON_02 = 0x01; 
	public static final int MFT_BANK1_BUTTON_03 = 0x02; 
	public static final int MFT_BANK1_BUTTON_04 = 0x03; 
	public static final int MFT_BANK1_BUTTON_05 = 0x04; 
	public static final int MFT_BANK1_BUTTON_06 = 0x05; 
	public static final int MFT_BANK1_BUTTON_07 = 0x06; 
	public static final int MFT_BANK1_BUTTON_08 = 0x07; 
	public static final int MFT_BANK1_BUTTON_09 = 0x08; 
	public static final int MFT_BANK1_BUTTON_10 = 0x09; 
	public static final int MFT_BANK1_BUTTON_11 = 0x0A; 
	public static final int MFT_BANK1_BUTTON_12 = 0x0B; 
	public static final int MFT_BANK1_BUTTON_13 = 0x0C; 
	public static final int MFT_BANK1_BUTTON_14 = 0x0D; 
	public static final int MFT_BANK1_BUTTON_15 = 0x0E; 
	public static final int MFT_BANK1_BUTTON_16 = 0x0F; 

	//Bank2: The CC number for each of the  encoders in this bank
	public static final int MFT_BANK2_BUTTON_01 = 0x10; 
	public static final int MFT_BANK2_BUTTON_02 = 0x11; 
	public static final int MFT_BANK2_BUTTON_03 = 0x12; 
	public static final int MFT_BANK2_BUTTON_04 = 0x13; 
	public static final int MFT_BANK2_BUTTON_05 = 0x14; 
	public static final int MFT_BANK2_BUTTON_06 = 0x15; 
	public static final int MFT_BANK2_BUTTON_07 = 0x16; 
	public static final int MFT_BANK2_BUTTON_08 = 0x17; 
	public static final int MFT_BANK2_BUTTON_09 = 0x18; 
	public static final int MFT_BANK2_BUTTON_10 = 0x19; 
	public static final int MFT_BANK2_BUTTON_11 = 0x1A; 
	public static final int MFT_BANK2_BUTTON_12 = 0x1B; 
	public static final int MFT_BANK2_BUTTON_13 = 0x1C; 
	public static final int MFT_BANK2_BUTTON_14 = 0x1D; 
	public static final int MFT_BANK2_BUTTON_15 = 0x1E; 
	public static final int MFT_BANK2_BUTTON_16 = 0x1F; 

	//Bank2: The CC number for each of the  encoders in this bank
	public static final int MFT_BANK3_BUTTON_01 = 0x20; 
	public static final int MFT_BANK3_BUTTON_02 = 0x21; 
	public static final int MFT_BANK3_BUTTON_03 = 0x22; 
	public static final int MFT_BANK3_BUTTON_04 = 0x23; 
	public static final int MFT_BANK3_BUTTON_05 = 0x24; 
	public static final int MFT_BANK3_BUTTON_06 = 0x25; 
	public static final int MFT_BANK3_BUTTON_07 = 0x26; 
	public static final int MFT_BANK3_BUTTON_08 = 0x27; 
	public static final int MFT_BANK3_BUTTON_09 = 0x28; 
	public static final int MFT_BANK3_BUTTON_10 = 0x29; 
	public static final int MFT_BANK3_BUTTON_11 = 0x2A; 
	public static final int MFT_BANK3_BUTTON_12 = 0x2B; 
	public static final int MFT_BANK3_BUTTON_13 = 0x2C; 
	public static final int MFT_BANK3_BUTTON_14 = 0x2D; 
	public static final int MFT_BANK3_BUTTON_15 = 0x2E; 
	public static final int MFT_BANK3_BUTTON_16 = 0x2F; 

	//Bank2: The CC number for each of the  encoders in this bank
	public static final int MFT_BANK4_BUTTON_01 = 0x30; 
	public static final int MFT_BANK4_BUTTON_02 = 0x31; 
	public static final int MFT_BANK4_BUTTON_03 = 0x32; 
	public static final int MFT_BANK4_BUTTON_04 = 0x13; 
	public static final int MFT_BANK4_BUTTON_05 = 0x34; 
	public static final int MFT_BANK4_BUTTON_06 = 0x35; 
	public static final int MFT_BANK4_BUTTON_07 = 0x36; 
	public static final int MFT_BANK4_BUTTON_08 = 0x37; 
	public static final int MFT_BANK4_BUTTON_09 = 0x38; 
	public static final int MFT_BANK4_BUTTON_10 = 0x39; 
	public static final int MFT_BANK4_BUTTON_11 = 0x3A; 
	public static final int MFT_BANK4_BUTTON_12 = 0x3B; 
	public static final int MFT_BANK4_BUTTON_13 = 0x3C; 
	public static final int MFT_BANK4_BUTTON_14 = 0x3D; 
	public static final int MFT_BANK4_BUTTON_15 = 0x3E; 
	public static final int MFT_BANK4_BUTTON_16 = 0x3F;  


	/* old implementation of the side buttons
	//The side buttons on the left and right also send Midi CC
	//They send on Channel 4, First the last Button is set to value (data2) 0
	//Then a second CC message is sent with the new mode and value (data2) of 127
	//For buttons 2 and 3 on the left side the order seems to be reversed
	//So let's concentrate on the "on"-messages with value (data2) == 127
	public static final int MFT_SIDE_BUTTON_CC_LEFT_1 = 0;
	public static final int MFT_SIDE_BUTTON_CC_LEFT_2 = 9;
	public static final int MFT_SIDE_BUTTON_CC_LEFT_3 = 10;
	public static final int MFT_SIDE_BUTTON_CC_RIGHT_1 = 1;
	public static final int MFT_SIDE_BUTTON_CC_RIGHT_2 = 2; 
	public static final int MFT_SIDE_BUTTON_CC_RIGHT_3 = 3;

	public static final int MFT_SIDE_BUTTON_CC_LEFT_2_ALT1 = 15;
	public static final int MFT_SIDE_BUTTON_CC_LEFT_2_ALT2 = 21;
	public static final int MFT_SIDE_BUTTON_CC_LEFT_2_ALT3 = 27;

	public static final int MFT_SIDE_BUTTON_CC_LEFT_3_ALT1 = 16;
	public static final int MFT_SIDE_BUTTON_CC_LEFT_3_ALT2 = 22;
	public static final int MFT_SIDE_BUTTON_CC_LEFT_3_ALT3 = 28;
	*/
	/* New side button implementation - all go only to CC message, we need to provide the bank switch ourself */
	//The side buttons on the left and right also send Midi CC
	//They send on Channel 4, just like the encoders, first a downclick with 127 in data2, 
	//then followed by an upclick with 0 in data2
	public static final int MFT_SIDE_BUTTON_CC_LEFT_1 = 8;
	public static final int MFT_SIDE_BUTTON_CC_LEFT_2 = 9;
	public static final int MFT_SIDE_BUTTON_CC_LEFT_3 = 10;
	public static final int MFT_SIDE_BUTTON_CC_RIGHT_1 = 11;
	public static final int MFT_SIDE_BUTTON_CC_RIGHT_2 = 12; 
	public static final int MFT_SIDE_BUTTON_CC_RIGHT_3 = 13;
}
