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

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * @author leon_nicholls
 * 
 */
public class Playback {

	private static final String LOG_TAG = "Playback";

	private static final String VLC_MAC = "/Applications/VLC.app/Contents/MacOS/lib";
	private static final String VLC_WINDOWS1 = "C:\\Program Files\\VideoLAN\\VLC";
	private static final String VLC_WINDOWS2 = "C:\\Program Files (x86)\\VideoLAN\\VLC﻿";

	public static final String TRANSCODING_PARAMETERS = "vcodec=VP80,vb=1000,vfilter=canvas{width=640,height=360},acodec=vorb,ab=128,channels=2,samplerate=44100,threads=2";

	private PlaybackListener playbackListener;
	private RampClient rampClient;
	private String appId;
	private DialServer dialServer;
	private Platform platform;

	public Playback(Platform platform, String appId, DialServer dialServer, PlaybackListener playbackListener) {
		this.platform = platform;
		this.appId = appId;
		this.dialServer = dialServer;
		this.playbackListener = playbackListener;
		this.rampClient = new RampClient(this, playbackListener);
	}

	public void stream(final String u) {
		Log.d(LOG_TAG, "stream: " + rampClient);
		if (!rampClient.isClosed()) {
			rampClient.closeCurrentApp(dialServer);
		}
		if (dialServer != null) {
			rampClient.launchApp(appId, dialServer, null);
			// wait for socket to be ready...
			new Thread(new Runnable() {
				public void run() {
					while (!rampClient.isStarted() && !rampClient.isClosed()) {
						try {
							// make less than 3 second ping time
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
					}
					if (!rampClient.isClosed()) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						rampClient.load(u);
					}
				}
			}).start();
		} else {
			Log.d(LOG_TAG, "stream: dialserver null");
		}
	}
	

	/**
	 * Start a web server to serve the videos to the media player on the
	 * ChromeCast device
	 */

	public void doStop() {
		if (rampClient != null) {
			rampClient.closeCurrentApp(dialServer);
			//rampClient = null;
		}
	}

	public void doPlay() {
		if (rampClient != null) {
			rampClient.play();
		}
	}

	public void doPause() {
		Log.d(LOG_TAG, "doPause: " + rampClient);
		if (rampClient != null) {
			rampClient.pause();
		}
	}

	public DialServer getDialServer() {
		return dialServer;
	}

	public void setDialServer(DialServer dialServer) {
		this.dialServer = dialServer;
		if (rampClient != null) {
			rampClient.setDialServer(dialServer);
		}
	}

}
