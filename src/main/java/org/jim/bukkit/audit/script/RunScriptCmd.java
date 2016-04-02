package org.jim.bukkit.audit.script;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.cmds.ICmd;
import org.jim.bukkit.audit.util.Task;

public class RunScriptCmd extends ICmd {

	private RunScript runScript;

	public RunScriptCmd(RunScript runScript) {
		super("runscript", "<file>", "执行datafolder内的脚本");
		minParam = 1;
		this.runScript = runScript;
	}

	@Override
	public boolean onCommand(final CommandSender sender, String[] args) {
		final Logger log = runScript.getPlugin().getLogger();
		final File f = new File(runScript.getPlugin().getDataFolder(), args[0]);
		if (!f.isFile()) {
			String msg = ChatColor.RED + "文件不存在: " + f.getAbsolutePath();
			sender.sendMessage(msg);
			log.info(msg);
			return true;
		}
		new Task() {

			@Override
			public void run() {
				sender.sendMessage("执行: " + f.getAbsolutePath() + "...");
				log.info("running script: " + f);
				FileExecutor fe = new FileExecutor(new Writer() {

					@Override
					public void write(char[] cbuf, int off, int len)
							throws IOException {
						String text = new String(cbuf, off, len);
						sender.sendMessage(text);
						log.info(text);
					}

					@Override
					public void flush() throws IOException {

					}

					@Override
					public void close() throws IOException {

					}
				});
				fe.exec(f);
				sender.sendMessage("执行完毕！");
				log.info("run completed.");
			}
		}.startAsync();
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.runscript";
	}

}
