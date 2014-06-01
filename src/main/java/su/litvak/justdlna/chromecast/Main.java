/*
 * Copyright (C) 2013 ENTERTAILION, LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package su.litvak.justdlna.chromecast;

import java.net.InetAddress;

public class Main {

	private static final String LOG_TAG = "Main";

	// TODO Add your own app id here
	private static final String APP_ID = "YOUR_APP_ID";

	public static final String VERSION = "0.4";

	private static Platform platform = new Platform();
	private static String appId = APP_ID;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
        String deviceName = "";

        Playback playback = new Playback(platform, appId, new DialServer(InetAddress.getByName(deviceName)), new PlaybackListener() {
            private int time;
            private int duration;
            private int state;

            @Override
            public void updateTime(Playback playback, int time) {
                Log.d(LOG_TAG, "updateTime: " + time);
                this.time = time;
            }

            @Override
            public void updateDuration(Playback playback, int duration) {
                Log.d(LOG_TAG, "updateDuration: " + duration);
                this.duration = duration;
            }

            @Override
            public void updateState(Playback playback, int state) {
                Log.d(LOG_TAG, "updateState: " + state);
                // Stop the app if the video reaches the end
                if (time > 0 && time == duration && state == 0) {
                    playback.doStop();
                    System.exit(0);
                }
            }

            public int getTime() {
                return time;
            }

            public int getDuration() {
                return duration;
            }

            public int getState() {
                return state;
            }

        });
        String streamURL = "";
        playback.stream(streamURL);
	}

}
