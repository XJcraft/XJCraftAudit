package org.jim.bukkit.audit.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ProcessMonitor implements Runnable {

	// private static final Logger log = AuditPlugin.getPlugin().getLogger();
	private ProcessWrapper process;

	ProcessMonitor(ProcessWrapper p) {
		this.process = p;
	}

	@Override
	public void run() {
		InputStreamReader reader = null;
		InputStreamReader errorReader = null;
		try {
			reader = new InputStreamReader(
					process.getRawProcess().getInputStream(),
					System.getProperty("sun.jnu.encoding"));
			errorReader = new InputStreamReader(
					process.getRawProcess().getErrorStream(),
					System.getProperty("sun.jnu.encoding"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		BufferedReader buf = new BufferedReader(reader);
		BufferedReader bufError = new BufferedReader(errorReader);
		String line = null;
		try {
			while ((line = buf.readLine()) != null && process.isRunning()) {
				// log.info("XJ Client> " + line);
				process.log(line);
			}
			while ((line = bufError.readLine()) != null
					&& process.isRunning()) {
				process.log(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			process.log("Error: " + e.getMessage());
		} finally {
			// Streams.safeClose(buf);
			try {
				buf.close();
				bufError.close();
			} catch (Exception e) {
			}
		}
	}

}
