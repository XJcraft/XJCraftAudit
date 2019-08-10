package org.jim.bukkit.audit.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jim.bukkit.audit.AuditPlugin;

public abstract class Task implements Runnable {
    private final JavaPlugin plugin;
    private int id = -1;

    public Task() {
        this(AuditPlugin.getPlugin());
    }

    public Task(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public final JavaPlugin getPlugin() {
        return this.plugin;
    }

    public boolean isRunning() {
        return (this.id != -1) && (Bukkit.getServer().getScheduler()
                .isCurrentlyRunning(this.id));
    }

    public boolean isQueued() {
        return Bukkit.getServer().getScheduler().isQueued(this.id);
    }

    public static boolean stop(Task task) {
        if (task == null)
            return false;
        task.stop();
        return true;
    }

    public Task stop() {
        if (this.id != -1) {
            Bukkit.getServer().getScheduler().cancelTask(this.id);
            this.id = -1;
        }
        return this;
    }

    public Task start() {
        this.id = this.plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(this.plugin, this);
        return this;
    }

    public Task startAsync() {
        BukkitTask task = this.plugin.getServer().getScheduler()
                .runTaskAsynchronously(getPlugin(), this);
        this.id = task.getTaskId();
        return this;
    }

    public Task start(long delay) {
        this.id = this.plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(this.plugin, this, delay);
        return this;
    }

    public Task start(long delay, long interval) {
        this.id = this.plugin.getServer().getScheduler()
                .scheduleSyncRepeatingTask(this.plugin, this, delay, interval);
        return this;
    }
}
