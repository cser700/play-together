package com.cser.play.common.utils;

public class MathRandomUtil {
	/**
	 * 1出现的概率为 79%
	 */
	public static double rate0 = 0.79;
	/**
	 * 1出现的概率为 20%
	 */
	public static double rate1 = 0.20;
	/**
	 * 2出现的概率为 1%
	 */
	public static double rate2 = 0.01;

	/**
	 * Math.random()产生一个double型的随机数，判断一下 例如0出现的概率为%50，则介于0到0.50中间的返回0
	 * 
	 * @return int
	 * 
	 */
	public static int random() {
		double randomNumber;
		randomNumber = Math.random();
		if (randomNumber >= 0 && randomNumber <= rate0) {
			return 1;
		} else if (randomNumber >= rate0 / 100 && randomNumber <= rate0 + rate1) {
			return 2;
		} else if (randomNumber >= rate0 + rate1 && randomNumber <= rate0 + rate1 + rate2) {
			return 10;
		}
		return -1;
	}

}
