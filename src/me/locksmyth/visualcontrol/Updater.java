package me.locksmyth.visualcontrol;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Updater {
	private static VisualControl plugin;
	private final String address;

	public Updater(final VisualControl visualControl, final String string) {
		plugin = visualControl;
		address = string;
	}

	public void download() {
		OutputStream out = null;
		URLConnection conn = null;
		InputStream in = null;
		try {
			final URL url = new URL(address);
			out = new BufferedOutputStream(new FileOutputStream("update./"));
			conn = url.openConnection();
			in = conn.getInputStream();
			final byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public Boolean updateCheck() {
		URLConnection conn = null;
		try {
			final URL url = new URL(address);
			conn = url.openConnection();
			final File localfile = new File("plugin" + File.separator + plugin.getClass().getSimpleName());
			final long lastmodifiedurl = conn.getLastModified();
			final long lastmodifiedfile = localfile.lastModified();
			if (lastmodifiedurl > lastmodifiedfile) {
				plugin.console.sendMessage("Updating...");
				download();
				return true;
			} else {
				plugin.console.sendMessage("No updates available :)");
				return false;
			}
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		plugin.getServer().notify();
		return false;
	}
}