/**
 * Copyright (c) 2017, Wasiq Bhamla.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.wasiqb.coteafs.appium.android.vodqa.activities;

import org.openqa.selenium.By;

import com.github.wasiqb.coteafs.appium.android.AndroidDevice;
import com.github.wasiqb.coteafs.appium.device.DeviceElement;

/**
 * @author wasiq.bhamla
 * @since Jan 24, 2018 5:17:36 PM
 */
public class ChainedViewActivity extends DefaultActivity {
	/**
	 * @author wasiq.bhamla
	 * @since Jan 24, 2018 5:17:36 PM
	 * @param device
	 */
	public ChainedViewActivity (final AndroidDevice device) {
		super (device);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.wasiqb.coteafs.appium.device.DeviceActivity#prepare()
	 */
	@Override
	protected DeviceElement prepare () {
		final DeviceElement main = super.prepare ();

		final DeviceElement scroll = DeviceElement.create ("Main")
			.parent (main)
			.using (By.id ("scrollView"));

		final DeviceElement view1 = DeviceElement.create ("View1")
			.parent (scroll)
			.using (By.id ("viewgroup1"));
		final DeviceElement view2 = DeviceElement.create ("View2")
			.parent (scroll)
			.using (By.id ("viewgroup2"));
		final DeviceElement view3 = DeviceElement.create ("View3")
			.parent (scroll)
			.using (By.id ("viewgroup3"));

		DeviceElement.create ("Text1")
			.parent (view1)
			.using (By.className ("android.widget.TextView"));
		DeviceElement.create ("Text2")
			.parent (view2)
			.using (By.className ("android.widget.TextView"));
		DeviceElement.create ("Text3")
			.parent (view3)
			.using (By.className ("android.widget.TextView"));

		return main;
	}
}