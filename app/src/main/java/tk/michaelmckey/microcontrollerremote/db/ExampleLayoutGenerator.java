/*
 * MIT License
 *
 * Copyright (c) 2021 Michael McKey (michaelmckey123@protonmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tk.michaelmckey.microcontrollerremote.db;

import java.util.HashMap;

import androidx.annotation.NonNull;
import tk.michaelmckey.microcontrollerremote.R;

/**
 * Generates data for the example layouts
 * @author Michael Mckey
 * @version 1.0.0
 */
class ExampleLayoutGenerator {
    /**
     * Generates the data for the example Serial layout.
     * Links the view's id to the message to be sent when it is clicked.
     * @return the example data
     */
    @NonNull
    public static HashMap<Integer, String> generateSerialExample(){
        HashMap<Integer, String> exampleIdToMessage = new HashMap<>();
        exampleIdToMessage.put(R.id.button_power, "A05F58A7");
        exampleIdToMessage.put(R.id.button_mute, "A05F708F");

        exampleIdToMessage.put(R.id.button_1, "0xA05FC837");
        exampleIdToMessage.put(R.id.button_2, "0xA05F08F7");
        exampleIdToMessage.put(R.id.button_3, "0xA05F8877");
        exampleIdToMessage.put(R.id.button_fav, "A05F609F");

        exampleIdToMessage.put(R.id.button_4, "0xA05FF00F");
        exampleIdToMessage.put(R.id.button_5, "0xA05F30CF");
        exampleIdToMessage.put(R.id.button_6, "0xA05FB04F");
        exampleIdToMessage.put(R.id.button_subtitle, "A05F28D7");

        exampleIdToMessage.put(R.id.button_7, "0xA05FD02F");
        exampleIdToMessage.put(R.id.button_8, "0xA05F10EF");
        exampleIdToMessage.put(R.id.button_9, "0xA05F906F");
        exampleIdToMessage.put(R.id.button_info, "A05F7887");

        exampleIdToMessage.put(R.id.button_list, "A05FF20D");
        exampleIdToMessage.put(R.id.button_0, "0x90B92");
        exampleIdToMessage.put(R.id.button_recall, "A05F42BD");
        exampleIdToMessage.put(R.id.button_ttx, "A05FE01F");

        exampleIdToMessage.put(R.id.button_lang, "A05F4AB5");
        exampleIdToMessage.put(R.id.button_timeshift, "A05FA857");
        exampleIdToMessage.put(R.id.button_tv_radio, "A05FAA55");

        exampleIdToMessage.put(R.id.button_epg, "A05FCA35");
        exampleIdToMessage.put(R.id.button_up, "0xA05F22DD");
        exampleIdToMessage.put(R.id.button_timer, "A05FE817");

        exampleIdToMessage.put(R.id.button_left, "0xA05F38C7");
        exampleIdToMessage.put(R.id.button_ok, "0x80B92");
        exampleIdToMessage.put(R.id.button_right, "0xA05F12ED");

        exampleIdToMessage.put(R.id.button_menu, "A05FF807");
        exampleIdToMessage.put(R.id.button_down, "0xA05FB847");
        exampleIdToMessage.put(R.id.button_exit, "A05F50AF");

        exampleIdToMessage.put(R.id.button_pvr, "A05F00FF");
        exampleIdToMessage.put(R.id.button_v_format, "A05F807F");
        exampleIdToMessage.put(R.id.button_aspect, "A05F6897");

        exampleIdToMessage.put(R.id.button_red, "A05F1AE5");
        exampleIdToMessage.put(R.id.button_green, "A05F48B7");
        exampleIdToMessage.put(R.id.button_yellow, "A05F2AD5");
        exampleIdToMessage.put(R.id.button_blue, "A05F40BF");

        exampleIdToMessage.put(R.id.button_play, "A05FBA45");
        exampleIdToMessage.put(R.id.button_pause, "A05F32CD");
        exampleIdToMessage.put(R.id.button_stop, "A05F8A75");

        exampleIdToMessage.put(R.id.button_rewind, "A05F0AF5");
        exampleIdToMessage.put(R.id.button_fast_forward, "A05F02FD");
        exampleIdToMessage.put(R.id.button_previous, "A05FB24D");
        return exampleIdToMessage;
    }

    /**
     * Generates the data for the example Bluetooth layout.
     * Links the view's id to the message to be sent when it is clicked.
     * @return the example data
     */
    @NonNull
    public static HashMap<Integer, String> generateBluetoothExample(){
        HashMap<Integer, String> exampleIdToMessage = new HashMap<>();
        exampleIdToMessage.put(R.id.button_volume_up, "r");
        exampleIdToMessage.put(R.id.button_volume_down, "b");
        return exampleIdToMessage;
    }

    /**
     * Generates the data for the example toy layout.
     * Links the view's id to the message to be sent when it is clicked.
     * @return the example data
     */
    @NonNull
    public static HashMap<Integer, String> generateToyExample(){
        HashMap<Integer, String> exampleIdToMessage = new HashMap<>();
        exampleIdToMessage.put(R.id.button_power, "button_power");
        exampleIdToMessage.put(R.id.button_mute, "button_mute");

        exampleIdToMessage.put(R.id.button_volume_up, "button_volume_up");
        exampleIdToMessage.put(R.id.button_volume_down, "button_volume_down");

        exampleIdToMessage.put(R.id.button_brightness_up, "button_brightness_up");
        exampleIdToMessage.put(R.id.button_brightness_down, "button_brightness_down");

        exampleIdToMessage.put(R.id.button_english, "button_english");
        exampleIdToMessage.put(R.id.button_spanish, "button_spanish");
        exampleIdToMessage.put(R.id.button_french, "button_french");

        exampleIdToMessage.put(R.id.button_calibrate, "button_calibrate");
        return exampleIdToMessage;
    }
}
