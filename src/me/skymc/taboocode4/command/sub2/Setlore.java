package me.skymc.taboocode4.command.sub2;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.skymc.taboocode4.TabooCode4;
import me.skymc.taboocode4.manager.ItemFilter;
import me.skymc.taboocode4.manager.ItemManager;
import me.skymc.taboocode4.type.SoundPack;
import me.skymc.taboocode4.utils.SubCommandExecutor;
import net.md_5.bungee.api.ChatColor;

public class Setlore implements SubCommandExecutor {

	@Override
	public boolean command(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)) {
			TabooCode4.send(sender, "该指令不能由后台输入");
			return false;
		}
		
		if (args.length < 2) {
			TabooCode4.send(sender, "缺少必要变量");
			return false;
		}
		
		Player player = (Player) sender;
		if (ItemFilter.isNull(player.getInventory().getItemInMainHand())) {
			TabooCode4.send(sender, "你不能修改空气");
			return false;
		}
		
		int line = 0;
		try {
			line = Integer.valueOf(args[1]);
		}
		catch (Exception e) {
			TabooCode4.send(sender, "&f" + args[1] + " &7不是一个有效的数字");
			return false;
		}
		
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		
		String value = "";
		
		for (int j = 2; j < args.length; j++) {
			value += args[j] + " ";
		}
		
		value = ChatColor.translateAlternateColorCodes('&', value.substring(0, value.length() - 1));
			
		List<String> lore = new ArrayList<>();
		if (meta.hasLore()) {
			lore = meta.getLore();
			
			int size = lore.size();
			for (int i = 0; i < line - size ; i++) {
				lore.add("");
			}
			
			lore.set(line - 1, value);
		}
		else {
			
			int size = lore.size();
			for (int i = 0; i < line - size ; i++) {
				lore.add("");
			}
			
			lore.set(line - 1, value);
		}
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		TabooCode4.send(sender, "设置第 &f" + line + " &7行描述: &f" + value);
		new SoundPack(Sound.BLOCK_ANVIL_USE, 1F, 1F).play(player);
		return true;
	}
	
}