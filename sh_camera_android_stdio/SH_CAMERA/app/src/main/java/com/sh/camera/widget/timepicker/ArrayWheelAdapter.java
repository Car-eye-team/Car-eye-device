/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.sh.camera.widget.timepicker;

/**
 * The simple Array wheel adapter
 * 
 * @param <T>
 *            the element type
 */
/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */


public class ArrayWheelAdapter<T> implements WheelAdapter {

	/** The default items length */
	public static final int DEFAULT_LENGTH = -1;

	// items
	private T items[];
	// length
	private int length;

	/**
	 * Constructor
	 * 
	 * @param items
	 *            the items
	 * @param length
	 *            the max items length
	 */
	public ArrayWheelAdapter(T items[], int length) {
		this.items = items;
		this.length = length;
	}

	/**
	 * Contructor
	 * 
	 * @param items
	 *            the items
	 */
	public ArrayWheelAdapter(T items[]) {
		this(items, DEFAULT_LENGTH);
	}

	@Override
	public String getItem(int index) {
		if (index >= 0 && index < items.length) {
			return items[index].toString();
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return items.length;
	}

	@Override
	public int getMaximumLength() {
		return length;
	}

}
