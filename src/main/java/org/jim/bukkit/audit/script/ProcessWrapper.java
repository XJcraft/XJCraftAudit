package org.jim.bukkit.audit.script;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ProcessWrapper {
	
	private Process process;

	public ProcessWrapper(Process process) {

		this.process = process;
	}

	public Process getRawProcess() {
		return this.process;
	}

	public void start() throws IOException {
		new Thread(new ProcessMonitor(this)).start();
	}

	public void log(String msg){
		
	}
	public void stop() {
		if (process != null)
			this.process.destroy();
	}

	public boolean isRunning() {
		try {
			this.process.exitValue();
		} catch (IllegalThreadStateException ex) {
			return true;
		}
		return false;
	}
}
