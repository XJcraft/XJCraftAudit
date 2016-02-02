package org.jim.bukkit.audit.script;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;

import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.AuditPlugin;

public class FileExecutor {
	
	public Writer logger;

	public FileExecutor(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					name = name.toLowerCase();
					return name.endsWith(".bat") && name.endsWith(".sh");
				}
			});
			for (File f : files) {
				exec(f);
			}
		}
	}
	//private CommandSender sender;
	public FileExecutor(Writer writer) {
		this.logger = writer;
	}

	public void exec(File f) {
	/*	if (!isLinux()) {
			log("非linux环境...不运行脚本");
			return;
		}
		*/
		try {
			Process p = null;
			if(isWindows())
				p= Runtime.getRuntime().exec(new String[]{
						"cmd.exe","/C",f.getAbsolutePath().toString()
				});
			else if(isLinux())
				p= Runtime.getRuntime().exec("sh " + f.getAbsolutePath());
			if(p == null){
				log("不支持当前环境!");
				return;
			}
			new ProcessWrapper(p){
				public void log(String msg) {
					FileExecutor.this.log(msg);
				};
			}.start();
		} catch (IOException e) {
			log("Error: "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}
	
	public boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
	private void log(String msg){
		if(logger!=null){
			try {
				logger.append(" >"+msg+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			//sender.sendMessage(" >"+msg);
		}
	}

}
