package me.locksmyth.visualcontrol;

import org.getspout.spoutapi.gui.Align;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;

public class VisualControlGui extends GenericPopup {
	private static VisualControl instance;

	@SuppressWarnings("deprecation")
	public VisualControlGui(final String world) {
		VisualControlPlayerManager.worldData.get(world);
		setBgVisible(true);
		setVisible(true);
		this.attachWidget(instance, ((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.FIRST)).setAlignX(Align.FIRST).setX(0).setY(0)
				.setHeight(240).setWidth(427));
	}

	public VisualControlGui(final VisualControl instance) {
		VisualControlGui.instance = instance;
	}
}
