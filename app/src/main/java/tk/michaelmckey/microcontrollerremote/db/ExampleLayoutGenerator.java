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
 * @version 1.2.2
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
        exampleIdToMessage.put(R.id.button_power, "E51AFA05");
        exampleIdToMessage.put(R.id.button_mute, "F10EFA05");

        exampleIdToMessage.put(R.id.button_1, "EC13FA05");
        exampleIdToMessage.put(R.id.button_2, "EF10FA05");
        exampleIdToMessage.put(R.id.button_3, "EE11FA05");
        exampleIdToMessage.put(R.id.button_fav, "F906FA05");

        exampleIdToMessage.put(R.id.button_4, "F00FFA05");
        exampleIdToMessage.put(R.id.button_5, "F30CFA05");
        exampleIdToMessage.put(R.id.button_6, "F20DFA05");
        exampleIdToMessage.put(R.id.button_subtitle, "EB14FA05");

        exampleIdToMessage.put(R.id.button_7, "F40BFA05");
        exampleIdToMessage.put(R.id.button_8, "F708FA05");
        exampleIdToMessage.put(R.id.button_9, "F609FA05");
        exampleIdToMessage.put(R.id.button_info, "E11EFA05");

        exampleIdToMessage.put(R.id.button_list, "B04FFA05");
        exampleIdToMessage.put(R.id.button_0, "B847FA05");
        exampleIdToMessage.put(R.id.button_recall, "BD42FA05");
        exampleIdToMessage.put(R.id.button_ttx, "F807FA05");

        exampleIdToMessage.put(R.id.button_epg, "AC53FA05");
        exampleIdToMessage.put(R.id.button_up, "BB44FA05");
        exampleIdToMessage.put(R.id.button_timer, "E817FA05");

        exampleIdToMessage.put(R.id.button_left, "E31CFA05");
        exampleIdToMessage.put(R.id.button_ok, "A35CFA05");
        exampleIdToMessage.put(R.id.button_right, "B748FA05");

        exampleIdToMessage.put(R.id.button_menu, "E01FFA05");
        exampleIdToMessage.put(R.id.button_down, "E21DFA05");
        exampleIdToMessage.put(R.id.button_exit, "F50AFA05");

        exampleIdToMessage.put(R.id.button_pvr, "FF00FA05");
        exampleIdToMessage.put(R.id.button_v_format, "FE01FA05");
        exampleIdToMessage.put(R.id.button_aspect, "E916FA05");

        exampleIdToMessage.put(R.id.button_play, "A25DFA05");
        exampleIdToMessage.put(R.id.button_pause, "B34CFA05");
        exampleIdToMessage.put(R.id.button_stop, "AE51FA05");

        exampleIdToMessage.put(R.id.button_rewind, "A05F0AF5");
        exampleIdToMessage.put(R.id.button_fast_forward, "AF50FA05");
        exampleIdToMessage.put(R.id.button_previous, "B24DFA05");
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

    /**
     * Generates the data for the example lights layout.
     * Links the view's id to the message to be sent when it is clicked.
     * @return the example data
     */
    @NonNull
    public static HashMap<Integer, String> generateLightsExample(){
        HashMap<Integer, String> exampleIdToMessage = new HashMap<>();
        exampleIdToMessage.put(R.id.button_power, "BA45FF00");
        exampleIdToMessage.put(R.id.button_timer, "B946FF00");
        exampleIdToMessage.put(R.id.button_off, "B847FF00");


        exampleIdToMessage.put(R.id.button_1, "BB44FF00");
        exampleIdToMessage.put(R.id.button_2, "BC43FF00");

        exampleIdToMessage.put(R.id.button_3, "F807FF00");
        exampleIdToMessage.put(R.id.button_4, "F609FF00");

        exampleIdToMessage.put(R.id.button_5, "E916FF00");
        exampleIdToMessage.put(R.id.button_6, "F20DFF00");

        exampleIdToMessage.put(R.id.button_7, "F30CFF00");
        exampleIdToMessage.put(R.id.button_8, "A15EFF00");

        exampleIdToMessage.put(R.id.button_brightness_down, "F708FF00");
        exampleIdToMessage.put(R.id.button_brightness_up, "A55AFF00");

        return exampleIdToMessage;
    }
}
