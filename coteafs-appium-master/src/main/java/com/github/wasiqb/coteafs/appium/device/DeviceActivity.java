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
package com.github.wasiqb.coteafs.appium.device;

import static com.github.wasiqb.coteafs.appium.constants.ErrorMessage.SERVER_STOPPED;
import static com.github.wasiqb.coteafs.appium.utils.ErrorUtils.fail;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElementsLocatedBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.wasiqb.coteafs.appium.checker.ServerChecker;
import com.github.wasiqb.coteafs.appium.config.PlatformType;
import com.github.wasiqb.coteafs.appium.config.PlaybackSetting;
import com.github.wasiqb.coteafs.appium.error.AppiumSelectorNotImplementedError;
import com.github.wasiqb.coteafs.appium.error.AppiumServerStoppedError;
import com.github.wasiqb.coteafs.appium.error.DeviceElementFindTimedOutError;
import com.github.wasiqb.coteafs.appium.error.DeviceElementNameNotFoundError;
import com.github.wasiqb.coteafs.appium.error.DeviceElementNotFoundError;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

/**
 * @author wasiq.bhamla
 * @param <D>
 * @param <E>
 * @since 26-Apr-2017 4:31:24 PM
 */
public abstract class DeviceActivity <D extends AppiumDriver <MobileElement>, E extends Device <D>> {
	private static final Logger log;

	static {
		log = LogManager.getLogger (DeviceActivity.class);
	}

	protected final E							device;
	protected final Map <String, DeviceElement>	deviceElements;
	private final PlaybackSetting				setting;
	private final WebDriverWait					wait;

	/**
	 * @author wasiq.bhamla
	 * @param device
	 * @since 26-Apr-2017 4:32:45 PM
	 */
	public DeviceActivity (final E device) {
		this.device = device;
		this.deviceElements = new HashMap <> ();
		this.setting = device.getSetting ()
			.getPlayback ();
		this.wait = new WebDriverWait (device.getDriver (), this.setting.getWaitForElementUntil ());
	}

	/**
	 * @author wasiq.bhamla
	 * @since Feb 2, 2018 1:44:52 PM
	 * @param name
	 * @return element
	 */
	public MobileElement getElement (final String name) {
		load ();
		final String msg = "Getting element with name [%s]...";
		log.trace (String.format (msg, name));
		return findElements (getDeviceElement (name));
	}

	/**
	 * @author wasiq.bhamla
	 * @since 26-Apr-2017 8:41:07 PM
	 * @return device actions
	 */
	@SuppressWarnings ("unchecked")
	public DeviceActions <D, E> onDevice () {
		return (DeviceActions <D, E>) this.device.action ();
	}

	/**
	 * @author wasiq.bhamla
	 * @since 26-Apr-2017 6:45:09 PM
	 * @param name
	 * @return element actions
	 */
	public DeviceElementActions <D, E> onElement (final String name) {
		ServerChecker.checkServerRunning (this.device.server);
		final String msg = "Preparing to perform actions on device element [%s]...";
		log.trace (String.format (msg, name));
		return new DeviceElementActions <> (this.device, name, getElement (name));
	}

	/**
	 * @author wasiq.bhamla
	 * @since Jul 5, 2017 6:55:54 AM
	 * @param name
	 * @param index
	 * @return actions
	 */
	public DeviceElementActions <D, E> onElement (final String name, final int index) {
		ServerChecker.checkServerRunning (this.device.server);
		final String msg = "Preparing to perform actions on dynamic device element [%s] on index [%d]...";
		log.trace (String.format (msg, name, index));
		final DeviceElement element = getDeviceElement (name).index (index);
		return new DeviceElementActions <> (this.device, name, findElements (element));
	}

	/**
	 * @author wasiq.bhamla
	 * @return element
	 * @since 02-May-2017 4:38:00 PM
	 */
	protected abstract DeviceElement prepare ();

	private void captureScreenshotOnError () {
		if (this.setting.isScreenshotOnError ()) {
			onDevice ().captureScreenshot ();
		}
	}

	private MobileElement find (final D deviceDriver, final DeviceElement parent, final By locator, final int index,
			final WaitStrategy strategy) {
		try {
			wait (locator, strategy);
			List <MobileElement> result = null;
			if (parent != null) {
				final String message = "Finding child element of [%s] parent using [%s] at index [%d]...";
				log.trace (String.format (message, parent.name (), locator, index));
				final MobileElement mobileElement = getElement (parent.name ());
				result = mobileElement.findElements (locator);
			}
			else {
				final String message = "Finding root element using [%s] at index [%d]...";
				log.trace (String.format (message, locator, index));
				result = deviceDriver.findElements (locator);
			}
			return result.get (index);
		}
		catch (final TimeoutException e) {
			captureScreenshotOnError ();
			final String message = "[%s] locator timed out.";
			fail (DeviceElementFindTimedOutError.class, String.format (message, locator), e);
		}
		catch (final NoSuchSessionException e) {
			fail (AppiumServerStoppedError.class, SERVER_STOPPED, e);
		}
		catch (final InvalidSelectorException e) {
			fail (AppiumSelectorNotImplementedError.class, "Selector not supported", e);
		}
		catch (final Exception e) {
			captureScreenshotOnError ();
			String message = "";
			if (parent == null) {
				message = "Error occured while finding root device element with locator [%s] at index [%d].";
				fail (DeviceElementNotFoundError.class, String.format (message, locator, index), e);
			}
			else {
				message = "Error occured while finding device element with locator [%s] at index [%d] under parent %s.";
				fail (DeviceElementNotFoundError.class, String.format (message, locator, index, parent.name ()), e);
			}
		}
		return null;
	}

	private MobileElement findElements (final DeviceElement element) {
		final DeviceElement parent = element.parent ();
		final By locator = element.locator ();
		final int index = element.index ();
		final WaitStrategy strategy = element.waitStrategy ();
		return find (this.device.getDriver (), parent, locator, index, strategy);
	}

	private DeviceElement getDeviceElement (final String name) {
		if (this.deviceElements.containsKey (name)) {
			return this.deviceElements.get (name);
		}
		final String msg = "DeviceElement with name [%s] not found.";
		fail (DeviceElementNameNotFoundError.class, String.format (msg, name));
		return null;
	}

	private void load () {
		if (this.deviceElements.size () == 0) {
			final PlatformType platform = this.device.setting.getPlatformType ();
			final String msg = "Loading elements on [%s] activity...";
			log.trace (String.format (msg, platform));
			loadElements (prepare ());
		}
	}

	private void loadElements (final DeviceElement rootElement) {
		ServerChecker.checkServerRunning (this.device.server);
		if (!this.deviceElements.containsKey (rootElement.name ())) {
			this.deviceElements.put (rootElement.name (), rootElement);
		}
		final List <DeviceElement> childs = rootElement.childs ();
		for (final DeviceElement child : childs) {
			loadElements (child);
		}
	}

	/**
	 * @author wasiq.bhamla
	 * @since Jan 30, 2018 7:33:47 PM
	 * @param locator
	 * @param waitStrategy
	 */
	private void wait (final By locator, final WaitStrategy waitStrategy) {
		switch (waitStrategy) {
			case ENABLED:
				this.wait.until (ExpectedConditions.elementToBeClickable (locator));
				break;
			case PRESENT:
				this.wait.until (ExpectedConditions.presenceOfAllElementsLocatedBy (locator));
				break;
			case VISIBLE:
			default:
				this.wait.until (visibilityOfAllElementsLocatedBy (locator));
				break;
		}
	}
}