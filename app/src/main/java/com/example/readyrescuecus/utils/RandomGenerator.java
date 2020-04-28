package com.example.readyrescuecus.utils;

import java.util.Date;
import java.util.Random;

public class RandomGenerator {

	public String randomdata() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		String generatedString = buffer.toString();

		System.out.println(generatedString + random.nextInt(1000000));
		return generatedString + random.nextInt(1000000);
	}

	public String timeStamp() {

		Date date = new Date();

		long time = date.getTime();

		String timeS = Long.toString(time);

		System.out.println("Time in Milliseconds: " + timeS);

		return timeS;

	}

}
