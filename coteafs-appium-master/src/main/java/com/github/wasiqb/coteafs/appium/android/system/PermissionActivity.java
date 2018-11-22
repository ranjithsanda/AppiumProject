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
package com.github.wasiqb.coteafs.appium.android.system;

import org.openqa.selenium.By;

import com.github.wasiqb.coteafs.appium.android.AndroidActivity;
import com.github.wasiqb.coteafs.appium.android.AndroidDevice;
import com.github.wasiqb.coteafs.appium.device.DeviceElement;

/**
 * @author wasiq.bhamla
 * @since 10-May-2017 7:53:52 PM
 */
public class PermissionActivity extends AndroidActivity {
	/**
	 * @author wasiq.bhamla
	 * @since 10-May-2017 7:53:57 PM
	 * @param device
	 */
	public PermissionActivity (final AndroidDevice device) {
		super (device);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.wasiqb.coteafs.appium.device.DeviceActivity#build()
	 */
	@Override
	protected DeviceElement prepare () {
		final DeviceElement container = DeviceElement.create ("Permission Window")
			.using (By.id ("com.android.packageinstaller:id/dialog_container"));
		DeviceElement.create ("Message")
			.parent (container)
			.using (By.id ("com.android.packageinstaller:id/permission_message"));
		DeviceElement.create ("Allow")
			.parent (container)
			.using (By.id ("com.android.packageinstaller:id/permission_allow_button"));
		DeviceElement.create ("Deny")
			.parent (container)
			.using (By.id ("com.android.packageinstaller:id/permission_deny_button"));
		return container;
	}
}