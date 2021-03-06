package me.skymc.taboocode4.attribute.sub;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.skymc.taboocode4.TabooCode4;
import me.skymc.taboocode4.attribute.SpecialAttribute;
import me.skymc.taboocode4.attribute.SpecialAttributeData;
import me.skymc.taboocode4.attribute.manager.AttributesManager;

public class AdditionalSpeed implements SpecialAttribute {

	private FileConfiguration file;
	
	private Pattern Primary;
	private Pattern Regular;
	private Pattern Percent;
	
	private Integer group_Regular;
	private Integer group_Percent;
	
	private String Name = this.getClass().getSimpleName();
	
	@Override
	public boolean register(FileConfiguration conf, boolean first) {
		file = conf;
		if (first) {
			conf.set("Settings.Regular", "[$]\\* Walk Speed");
			conf.set("Settings.Percent", "[$]\\% Walk Speed");
			
			conf.set("Settings.GroupLocation.Primary", 1);
			conf.set("Settings.GroupLocation.Regular", 1);
			conf.set("Settings.GroupLocation.Percent", 1);
		}

		Regular = Pattern.compile(conf.getString("Settings.Regular").replace("[$]", "((?:\\-|\\+)?\\d+(?:\\.\\d+)?)").replace("[?]", "(?:\\s|\\S)"));
		Percent = Pattern.compile(conf.getString("Settings.Percent").replace("[$]", "((?:\\-|\\+)?\\d+(?:\\.\\d+)?)").replace("[?]", "(?:\\s|\\S)"));
		
		group_Regular = conf.getInt("Settings.GroupLocation.Regular");
		group_Percent = conf.getInt("Settings.GroupLocation.Percent");

		return true;
	}
	
	@Override
	public int getPriority() {
		return file.getInt("Settings.Priority");
	}

	@Override
	public DamageType getType() {
		return DamageType.UPDATE;
	}

	@Override
	public String getHoloSpecies() {
		return null;
	}

	@Override
	public SpecialAttributeData getAttribute(LivingEntity entity, ItemStack item, List<String> uncoloredlores) {
		
		Double[] primary = new Double[] { 0D, 0D };
		Double regular = 0D;
		Double percent = 0D;
		
		for (String lore : uncoloredlores) {
			Matcher m2 = Regular.matcher(lore);
			if (m2.find()) {
				regular += Double.valueOf(m2.group(group_Regular));
			}
			
			Matcher m3 = Percent.matcher(lore);
			if (m3.find()) {
				percent += Double.valueOf(m3.group(group_Percent));
			}
		}
		
		return new SpecialAttributeData(primary, regular, percent);
	}
	
	@Override
	public double execute(LivingEntity attacker, LivingEntity victim, HashMap<String, SpecialAttributeData> attackerattr, HashMap<String, SpecialAttributeData> victimattr, Event event) {
		
		if (!(attacker instanceof Player)) {
			return 0;
		}
		
		Double value = AttributesManager.getRegularValue(attacker, Name)/100;
		Double value2 = TabooCode4.getBaseSpeed() + value + (value * AttributesManager.getPercentValue(attacker, Name));
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (value2 < 0f) {
					((Player) attacker).setWalkSpeed(1f);
				}
				if (value2 > 1f) {
					((Player) attacker).setWalkSpeed(1f);
				}
				else {
					((Player) attacker).setWalkSpeed(value2.floatValue());
				}
			}
		}.runTask(TabooCode4.getInst());
		return 0;
	}


}