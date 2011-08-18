package me.locksmyth.visualcontrol;

import java.util.ArrayList;
import java.util.List;

import org.getspout.spoutapi.gui.Color;

public class VisualControlColor extends Color {

	public VisualControlColor() {
		super(Float.parseFloat("-1"), Float.parseFloat("-1"), Float.parseFloat("-1"));
	}

	public VisualControlColor(final float r, final float g, final float b) {
		super(r, g, b);
	}

	public String asHex() {
		return toHex(getRedF()) + toHex(getGreenF()) + toHex(getBlueF());
	}

	public VisualControlColor fromHex(final String h) {
		final List<Float> floats = new ArrayList<Float>();
		if (null != h) {
			for (final String value : letterPairs(h)) {
				final int dec = Integer.decode("0x" + value);
				floats.add((float) (dec / 255));
			}
			setRed(floats.get(0));
			setGreen(floats.get(1));
			setBlue(floats.get(2));
		}
		return this;
	}

	private String[] letterPairs(final String str) {
		final int numPairs = str.length() - 1;
		final String[] pairs = new String[numPairs];
		for (int i = 0; i < numPairs; i++) {
			pairs[i] = str.substring(i, i + 2);
		}
		return pairs;
	}

	private String toHex(final float n) {
		return Integer.toHexString((int) (255 * n));
	}

}
