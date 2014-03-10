package io.github.TheRoboDC;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class MineSlamKillStreaks extends JavaPlugin implements Listener {

	Logger log = Logger.getLogger("Minecraft.LudusTravel");
	Map<String, Integer> killstreak = new HashMap<String, Integer>();

	@Override
	public void onEnable() {
		// Registers the events in the class.
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("kd").setExecutor(this);
		// Tells the console that the plugin has been enabled.
		getLogger().info("MineSlamKillStreaks has been enabled!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("MineSlamKillStreaks has been disabled!");
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if(command.getName().equalsIgnoreCase("kd")){ //Is the command /kd?
			if(sender instanceof Player){ //Is the sender a Player?
				String name = sender.getName(); //Name of the sender.
				if(killstreak.containsKey(name)){
					//Player already has killstreak.
					int kills = killstreak.get(name);
					sender.sendMessage(ChatColor.WHITE + "You have " + ChatColor.GREEN + Integer.toString(kills) + ChatColor.WHITE + " kills without dying!");
					} else {
						sender.sendMessage(ChatColor.RED + "You don't currently have a killstreak.");
					}
			}
		}
		return false;
	}
	@EventHandler
	public void playerdeath (PlayerDeathEvent ev){
		Player p = ev.getEntity();
		if(p.getKiller() instanceof Player){
			Player killer = p.getKiller();
			p.sendMessage(ChatColor.GREEN + "Your killstreak was lost by " + ChatColor.RED + killer.getName());
			addtokillstreak(killer);
		}
		return;
		}

	public void addtokillstreak(Player killer) {
		String name = killer.getName();
		if(killstreak.containsKey(name)){
			//Already have a killstreak - add one to it
			int kills = killstreak.get(name);
			kills++;
			killstreak.put(name, kills);
			killer.sendMessage(ChatColor.GREEN  + "You now have " + ChatColor.RED + Integer.toString(kills) + ChatColor.WHITE + " kills! Good job!");
			// run another method to run console commands
			runcommands(name,killstreak.get(name));
		}else{
			//first kill of a new streak
			killstreak.put(name, 1);
			killer.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.RED + "1" + ChatColor.WHITE + " kills! Good job!");
			// run another method to run console commands
			runcommands(name,1);
		}
	}

	public void runcommands(String name, int kills){
		String numofkills = Integer.toString(kills);
		boolean rancommands = false;
		int commandnumber = 0;
		while (rancommands == false){
			commandnumber++;
			if(this.getConfig().getString(numofkills+"."+commandnumber) != null){
				//there is a valid command to be run
				String command = this.getConfig().getString(numofkills+"."+commandnumber).replaceAll("%name%", name);
				String command1 = command.replaceAll("%killstreak%", numofkills);
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command1);
			}
			if(this.getConfig().getString(numofkills+"."+commandnumber) == null){
				return;
			}
		}
	}
}
