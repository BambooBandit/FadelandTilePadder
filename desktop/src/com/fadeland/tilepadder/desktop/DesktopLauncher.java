package com.fadeland.tilepadder.desktop;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.fadeland.tilepadder.TilePadder;

public class DesktopLauncher {
	public static void main (String[] args) {
		HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
		new HeadlessApplication(new TilePadder(args), config);
	}
}
