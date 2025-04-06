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


package de.drMartinKramer.hardware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitwig.extension.api.Color;

import de.drMartinKramer.MFT_Configuration;

public class MFT_ColorMapper {

    /** A static Array of 127 entries of colors of the Midi Fighter Twister color spectrum */
    private static final List<Color> mftColorList = new ArrayList<Color>();
    /** A static array of 127 entries of colors from the Bitwig EQ spectrum */
    private static final List<Color> eqColorList = new ArrayList<Color>();
    /**
     * This constructore creates a list of 127 colors that are used by the Midi Fighter Twister.
     * I have asked a friendly AI to analyze the spectrum provided by the Midi Fighter Twister Manual and to create a list of 127 colors.
     * This constructor needs to be called once. Then the list of colors is available for the whole application via static calls. 
     */
    public MFT_ColorMapper(){

        /* first, let's  create an array with 127 samples of the color spectrum from the Midi Fighter Twister Manual. 
           A friendly AI has done this for me.
        */
        mftColorList.clear();
        mftColorList.add(Color.fromRGB255(0, 0, 0)); //first add black
        //mftColorList.add(Color.fromRGB255(49, 49, 227));
        mftColorList.add(Color.fromRGB255(49, 56, 225));
        mftColorList.add(Color.fromRGB255(49, 65, 224));
        mftColorList.add(Color.fromRGB255(48, 74, 225));
        mftColorList.add(Color.fromRGB255(49, 83, 226));
        mftColorList.add(Color.fromRGB255(48, 91, 226));
        mftColorList.add(Color.fromRGB255(49, 99, 223));
        mftColorList.add(Color.fromRGB255(47, 108, 225));
        mftColorList.add(Color.fromRGB255(48, 116, 224));
        mftColorList.add(Color.fromRGB255(49, 124, 225));
        mftColorList.add(Color.fromRGB255(50, 133, 226));
        mftColorList.add(Color.fromRGB255(49, 141, 223));
        mftColorList.add(Color.fromRGB255(48, 150, 226));
        mftColorList.add(Color.fromRGB255(48, 158, 224));
        mftColorList.add(Color.fromRGB255(48, 166, 225));
        mftColorList.add(Color.fromRGB255(49, 174, 225));
        mftColorList.add(Color.fromRGB255(48, 182, 224));
        mftColorList.add(Color.fromRGB255(48, 191, 226));
        mftColorList.add(Color.fromRGB255(49, 202, 226));
        mftColorList.add(Color.fromRGB255(48, 208, 224));
        mftColorList.add(Color.fromRGB255(48, 216, 223));
        mftColorList.add(Color.fromRGB255(49, 223, 226));
        mftColorList.add(Color.fromRGB255(48, 226, 217));
        mftColorList.add(Color.fromRGB255(49, 225, 209));
        mftColorList.add(Color.fromRGB255(48, 224, 199));
        mftColorList.add(Color.fromRGB255(48, 225, 193));
        mftColorList.add(Color.fromRGB255(49, 224, 185));
        mftColorList.add(Color.fromRGB255(50, 224, 174));
        mftColorList.add(Color.fromRGB255(48, 225, 166));
        mftColorList.add(Color.fromRGB255(48, 225, 157));
        mftColorList.add(Color.fromRGB255(48, 225, 150));
        mftColorList.add(Color.fromRGB255(48, 224, 142));
        mftColorList.add(Color.fromRGB255(49, 225, 135));
        mftColorList.add(Color.fromRGB255(49, 225, 125));
        mftColorList.add(Color.fromRGB255(48, 226, 116));
        mftColorList.add(Color.fromRGB255(49, 225, 108));
        mftColorList.add(Color.fromRGB255(48, 224, 99));
        mftColorList.add(Color.fromRGB255(48, 224, 91));
        mftColorList.add(Color.fromRGB255(48, 223, 85));
        mftColorList.add(Color.fromRGB255(48, 225, 74));
        mftColorList.add(Color.fromRGB255(48, 224, 65));
        mftColorList.add(Color.fromRGB255(49, 224, 58));
        mftColorList.add(Color.fromRGB255(49, 224, 49));
        mftColorList.add(Color.fromRGB255(53, 225, 48));
        mftColorList.add(Color.fromRGB255(63, 223, 47));
        mftColorList.add(Color.fromRGB255(73, 224, 49));
        mftColorList.add(Color.fromRGB255(80, 225, 48));
        mftColorList.add(Color.fromRGB255(89, 225, 48));
        mftColorList.add(Color.fromRGB255(98, 224, 48));
        mftColorList.add(Color.fromRGB255(105, 225, 49));
        mftColorList.add(Color.fromRGB255(114, 224, 48));
        mftColorList.add(Color.fromRGB255(121, 224, 49));
        mftColorList.add(Color.fromRGB255(130, 224, 48));
        mftColorList.add(Color.fromRGB255(139, 225, 48));
        mftColorList.add(Color.fromRGB255(148, 226, 48));
        mftColorList.add(Color.fromRGB255(155, 225, 48));
        mftColorList.add(Color.fromRGB255(164, 224, 48));
        mftColorList.add(Color.fromRGB255(172, 225, 48));
        mftColorList.add(Color.fromRGB255(182, 226, 49));
        mftColorList.add(Color.fromRGB255(188, 225, 48));
        mftColorList.add(Color.fromRGB255(198, 225, 48));
        mftColorList.add(Color.fromRGB255(205, 225, 48));
        mftColorList.add(Color.fromRGB255(213, 224, 48));
        mftColorList.add(Color.fromRGB255(223, 226, 49));
        mftColorList.add(Color.fromRGB255(224, 220, 48));
        mftColorList.add(Color.fromRGB255(224, 213, 48));
        mftColorList.add(Color.fromRGB255(227, 202, 49));
        mftColorList.add(Color.fromRGB255(226, 195, 49));
        mftColorList.add(Color.fromRGB255(225, 186, 48));
        mftColorList.add(Color.fromRGB255(224, 177, 48));
        mftColorList.add(Color.fromRGB255(224, 171, 47));
        mftColorList.add(Color.fromRGB255(226, 162, 49));
        mftColorList.add(Color.fromRGB255(227, 152, 49));
        mftColorList.add(Color.fromRGB255(226, 146, 49));
        mftColorList.add(Color.fromRGB255(224, 137, 48));
        mftColorList.add(Color.fromRGB255(225, 127, 48));
        mftColorList.add(Color.fromRGB255(224, 120, 48));
        mftColorList.add(Color.fromRGB255(224, 110, 50));
        mftColorList.add(Color.fromRGB255(224, 102, 48));
        mftColorList.add(Color.fromRGB255(223, 96, 49));
        mftColorList.add(Color.fromRGB255(223, 86, 48));
        mftColorList.add(Color.fromRGB255(225, 77, 47));
        mftColorList.add(Color.fromRGB255(224, 70, 49));
        mftColorList.add(Color.fromRGB255(224, 60, 47));
        mftColorList.add(Color.fromRGB255(225, 52, 49));
        mftColorList.add(Color.fromRGB255(226, 48, 51));
        mftColorList.add(Color.fromRGB255(224, 49, 59));
        mftColorList.add(Color.fromRGB255(225, 50, 70));
        mftColorList.add(Color.fromRGB255(223, 49, 77));
        mftColorList.add(Color.fromRGB255(223, 48, 85));
        mftColorList.add(Color.fromRGB255(225, 49, 95));
        mftColorList.add(Color.fromRGB255(224, 49, 100));
        mftColorList.add(Color.fromRGB255(225, 49, 109));
        mftColorList.add(Color.fromRGB255(225, 47, 118));
        mftColorList.add(Color.fromRGB255(225, 48, 128));
        mftColorList.add(Color.fromRGB255(224, 49, 135));
        mftColorList.add(Color.fromRGB255(225, 49, 146));
        mftColorList.add(Color.fromRGB255(223, 49, 151));
        mftColorList.add(Color.fromRGB255(223, 48, 159));
        mftColorList.add(Color.fromRGB255(225, 48, 170));
        mftColorList.add(Color.fromRGB255(223, 48, 177));
        mftColorList.add(Color.fromRGB255(224, 48, 187));
        mftColorList.add(Color.fromRGB255(224, 48, 194));
        mftColorList.add(Color.fromRGB255(224, 49, 203));
        mftColorList.add(Color.fromRGB255(224, 49, 210));
        mftColorList.add(Color.fromRGB255(224, 49, 221));
        mftColorList.add(Color.fromRGB255(222, 48, 225));
        mftColorList.add(Color.fromRGB255(214, 48, 224));
        mftColorList.add(Color.fromRGB255(206, 48, 223));
        mftColorList.add(Color.fromRGB255(197, 48, 224));
        mftColorList.add(Color.fromRGB255(191, 49, 225));
        mftColorList.add(Color.fromRGB255(182, 48, 226));
        mftColorList.add(Color.fromRGB255(174, 48, 224));
        mftColorList.add(Color.fromRGB255(162, 48, 225));
        mftColorList.add(Color.fromRGB255(156, 49, 224));
        mftColorList.add(Color.fromRGB255(147, 48, 224));
        mftColorList.add(Color.fromRGB255(139, 49, 226));
        mftColorList.add(Color.fromRGB255(131, 48, 225));
        mftColorList.add(Color.fromRGB255(123, 49, 226));
        mftColorList.add(Color.fromRGB255(114, 48, 226));
        mftColorList.add(Color.fromRGB255(106, 49, 225));
        mftColorList.add(Color.fromRGB255(98, 49, 226));
        mftColorList.add(Color.fromRGB255(90, 48, 226));
        mftColorList.add(Color.fromRGB255(81, 48, 225));
        mftColorList.add(Color.fromRGB255(72, 48, 224));
        mftColorList.add(Color.fromRGB255(64, 49, 226));
        mftColorList.add(Color.fromRGB255(55, 48, 224));
        
        //MFT_Configuration.println("Size of the MFT Color List: " + mftColorList.size() + " colors");


        eqColorList.add(Color.fromRGB255(91, 23, 20));
        eqColorList.add(Color.fromRGB255(95, 24, 20));
        eqColorList.add(Color.fromRGB255(98, 25, 21));
        eqColorList.add(Color.fromRGB255(102, 26, 22));
        eqColorList.add(Color.fromRGB255(105, 27, 23));
        eqColorList.add(Color.fromRGB255(109, 28, 24));
        eqColorList.add(Color.fromRGB255(113, 29, 24));
        eqColorList.add(Color.fromRGB255(116, 30, 25));
        eqColorList.add(Color.fromRGB255(120, 31, 27));
        eqColorList.add(Color.fromRGB255(123, 32, 27));
        eqColorList.add(Color.fromRGB255(127, 33, 28));
        eqColorList.add(Color.fromRGB255(130, 34, 29));
        eqColorList.add(Color.fromRGB255(134, 35, 30));
        eqColorList.add(Color.fromRGB255(137, 36, 30));
        eqColorList.add(Color.fromRGB255(141, 37, 31));
        eqColorList.add(Color.fromRGB255(145, 38, 32));
        eqColorList.add(Color.fromRGB255(149, 39, 33));
        eqColorList.add(Color.fromRGB255(152, 40, 34));
        eqColorList.add(Color.fromRGB255(156, 41, 34));
        eqColorList.add(Color.fromRGB255(159, 42, 36));
        eqColorList.add(Color.fromRGB255(163, 43, 36));
        eqColorList.add(Color.fromRGB255(167, 44, 37));
        eqColorList.add(Color.fromRGB255(170, 46, 38));
        eqColorList.add(Color.fromRGB255(173, 46, 39));
        eqColorList.add(Color.fromRGB255(177, 48, 40));
        eqColorList.add(Color.fromRGB255(181, 48, 40));
        eqColorList.add(Color.fromRGB255(184, 50, 41));
        eqColorList.add(Color.fromRGB255(188, 53, 43));
        eqColorList.add(Color.fromRGB255(192, 54, 43));
        eqColorList.add(Color.fromRGB255(196, 56, 45));
        eqColorList.add(Color.fromRGB255(199, 58, 45));
        eqColorList.add(Color.fromRGB255(203, 60, 46));
        eqColorList.add(Color.fromRGB255(207, 68, 48));
        eqColorList.add(Color.fromRGB255(210, 75, 49));
        eqColorList.add(Color.fromRGB255(214, 85, 52));
        eqColorList.add(Color.fromRGB255(219, 95, 53));
        eqColorList.add(Color.fromRGB255(223, 104, 56));
        eqColorList.add(Color.fromRGB255(227, 115, 58));
        eqColorList.add(Color.fromRGB255(231, 126, 61));
        eqColorList.add(Color.fromRGB255(233, 135, 63));
        eqColorList.add(Color.fromRGB255(235, 147, 65));
        eqColorList.add(Color.fromRGB255(239, 158, 68));
        eqColorList.add(Color.fromRGB255(241, 169, 71));
        eqColorList.add(Color.fromRGB255(244, 180, 73));
        eqColorList.add(Color.fromRGB255(246, 191, 76));
        eqColorList.add(Color.fromRGB255(247, 201, 78));
        eqColorList.add(Color.fromRGB255(248, 210, 81));
        eqColorList.add(Color.fromRGB255(249, 221, 83));
        eqColorList.add(Color.fromRGB255(250, 231, 86));
        eqColorList.add(Color.fromRGB255(252, 241, 89));
        eqColorList.add(Color.fromRGB255(253, 251, 91));
        eqColorList.add(Color.fromRGB255(246, 253, 91));
        eqColorList.add(Color.fromRGB255(238, 252, 91));
        eqColorList.add(Color.fromRGB255(228, 252, 90));
        eqColorList.add(Color.fromRGB255(220, 252, 90));
        eqColorList.add(Color.fromRGB255(212, 252, 89));
        eqColorList.add(Color.fromRGB255(203, 251, 88));
        eqColorList.add(Color.fromRGB255(195, 251, 88));
        eqColorList.add(Color.fromRGB255(186, 251, 88));
        eqColorList.add(Color.fromRGB255(179, 251, 87));
        eqColorList.add(Color.fromRGB255(171, 251, 87));
        eqColorList.add(Color.fromRGB255(164, 250, 86));
        eqColorList.add(Color.fromRGB255(158, 250, 86));
        eqColorList.add(Color.fromRGB255(151, 250, 86));
        eqColorList.add(Color.fromRGB255(145, 250, 86));
        eqColorList.add(Color.fromRGB255(140, 250, 85));
        eqColorList.add(Color.fromRGB255(135, 250, 85));
        eqColorList.add(Color.fromRGB255(130, 250, 85));
        eqColorList.add(Color.fromRGB255(127, 250, 85));
        eqColorList.add(Color.fromRGB255(124, 250, 85));
        eqColorList.add(Color.fromRGB255(123, 250, 89));
        eqColorList.add(Color.fromRGB255(123, 250, 94));
        eqColorList.add(Color.fromRGB255(123, 250, 99));
        eqColorList.add(Color.fromRGB255(122, 250, 105));
        eqColorList.add(Color.fromRGB255(122, 250, 113));
        eqColorList.add(Color.fromRGB255(122, 250, 119));
        eqColorList.add(Color.fromRGB255(122, 250, 127));
        eqColorList.add(Color.fromRGB255(121, 250, 135));
        eqColorList.add(Color.fromRGB255(121, 250, 143));
        eqColorList.add(Color.fromRGB255(121, 250, 153));
        eqColorList.add(Color.fromRGB255(121, 250, 162));
        eqColorList.add(Color.fromRGB255(121, 250, 171));
        eqColorList.add(Color.fromRGB255(121, 250, 181));
        eqColorList.add(Color.fromRGB255(122, 250, 191));
        eqColorList.add(Color.fromRGB255(122, 251, 201));
        eqColorList.add(Color.fromRGB255(122, 251, 210));
        eqColorList.add(Color.fromRGB255(123, 251, 221));
        eqColorList.add(Color.fromRGB255(123, 251, 232));
        eqColorList.add(Color.fromRGB255(124, 251, 241));
        eqColorList.add(Color.fromRGB255(123, 244, 242));
        eqColorList.add(Color.fromRGB255(124, 239, 244));
        eqColorList.add(Color.fromRGB255(125, 234, 245));
        eqColorList.add(Color.fromRGB255(126, 227, 247));
        eqColorList.add(Color.fromRGB255(128, 222, 248));
        eqColorList.add(Color.fromRGB255(129, 216, 250));
        eqColorList.add(Color.fromRGB255(133, 212, 251));
        eqColorList.add(Color.fromRGB255(137, 209, 250));
        eqColorList.add(Color.fromRGB255(141, 207, 250));
        eqColorList.add(Color.fromRGB255(146, 203, 250));
        eqColorList.add(Color.fromRGB255(152, 200, 250));
        eqColorList.add(Color.fromRGB255(157, 197, 250));
        eqColorList.add(Color.fromRGB255(162, 196, 250));
        eqColorList.add(Color.fromRGB255(168, 197, 249));
        eqColorList.add(Color.fromRGB255(174, 198, 248));
        eqColorList.add(Color.fromRGB255(179, 199, 248));
        eqColorList.add(Color.fromRGB255(186, 201, 248));
        eqColorList.add(Color.fromRGB255(192, 202, 247));
        eqColorList.add(Color.fromRGB255(197, 204, 246));
        eqColorList.add(Color.fromRGB255(201, 206, 243));
        eqColorList.add(Color.fromRGB255(206, 209, 239));
        eqColorList.add(Color.fromRGB255(209, 212, 236));
        eqColorList.add(Color.fromRGB255(213, 215, 232));
        eqColorList.add(Color.fromRGB255(218, 218, 229));
        eqColorList.add(Color.fromRGB255(222, 221, 225));
        eqColorList.add(Color.fromRGB255(219, 218, 223));
        eqColorList.add(Color.fromRGB255(216, 215, 219));
        eqColorList.add(Color.fromRGB255(212, 212, 214));
        eqColorList.add(Color.fromRGB255(209, 209, 211));
        eqColorList.add(Color.fromRGB255(206, 206, 207));
        eqColorList.add(Color.fromRGB255(203, 203, 203));
        eqColorList.add(Color.fromRGB255(199, 199, 199));
        eqColorList.add(Color.fromRGB255(195, 195, 195));
        eqColorList.add(Color.fromRGB255(191, 191, 191));
        eqColorList.add(Color.fromRGB255(188, 188, 188));
        eqColorList.add(Color.fromRGB255(184, 184, 184));
        eqColorList.add(Color.fromRGB255(180, 180, 180));
        eqColorList.add(Color.fromRGB255(83, 83, 83));
        
        //MFT_Configuration.println("Size of the Bitwig Color List: " + eqColorList.size() + " colors");
    }

    /** 
     *  Method to calculate the distance between two colors
     *  It takes two Color variables and create the square root of the sum of the squares of the differences of the RGB values
     */
    private static double distance(Color colorA, Color colorB) {
        double dr = colorA.getRed255() - colorB.getRed255();
        double dg = colorA.getGreen255() - colorB.getGreen255();
        double db = colorA.getBlue255() - colorB.getBlue255();
        return Math.sqrt(dr * dr + dg * dg + db * db);  
    }

    // Cache to store previously calculated nearest color indices
    private static Map<Integer, Integer> colorCache = new HashMap<>();
    
    /**
     *   Method to find the index of the nearest color in the given ArrayList
     *  It takes a Color variable and iterates over the colors in the ArrayList to find the nearest color
     * */
    public static int findNearestColorIndex(Color targetColor) {
        if(targetColor.getRed255()+targetColor.getGreen255()+targetColor.getBlue255() < 20) return 0; //black is always 0 (in MFT language, 0 turns the light off)
        
        int key = (int)targetColor.getRed255() << 16 | (int)targetColor.getGreen255() << 8 | (int)targetColor.getBlue255();
        if (colorCache.containsKey(key)) {
            return colorCache.get(key);
        }
        
        double minDistance = Double.MAX_VALUE;
        int nearestIndex = -1;
        
        // Iterate over the colors in the ArrayList and find the nearest color
        for (int i = 0; i < mftColorList.size(); i++) {
            Color color = mftColorList.get(i);
            double distance = distance(color, targetColor);
            if (distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }
        
        // Cache the result
        colorCache.put(key, nearestIndex);        
        return nearestIndex;
    }

    /**
     * Method returns a list of Colors that are used by Bitwig for devices and remote controls
     * @return List of 8 color objects
     */
    public static List<Color> parameterColorList()
	  {
	    
	    return Arrays.asList(
            mftColorList.get(86), 
            mftColorList.get(70), 
	        mftColorList.get(64), 
            mftColorList.get(51), 
            mftColorList.get(37), 
            mftColorList.get(14), 
            mftColorList.get(111),
            mftColorList.get(97)); 
	  }
    
    /**
     * Method returns a Color object that represents the color of the frequency in Bitwig's EQ
     */
    public static Color getEQColor(double frequency){ 
        int index = (int)Math.round(frequency * 127);
        if(index < 0) index = 0;
        if(index >= eqColorList.size()) index = eqColorList.size()-1;
        return eqColorList.get(index);
    }    
}
