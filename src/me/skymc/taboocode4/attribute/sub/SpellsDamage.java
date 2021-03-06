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

import me.skymc.taboocode4.TabooCode4;
import me.skymc.taboocode4.attribute.SpecialAttribute;
import me.skymc.taboocode4.attribute.SpecialAttributeData;
import me.skymc.taboocode4.attribute.manager.AttributesManager;

public class SpellsDamage implements SpecialAttribute {

	private FileConfiguration file;
	
	private Pattern Primary;
	private Pattern Regular;
	private Pattern Percent;
	
	private Integer group_Primary1;
	private Integer group_Primary2;
	private Integer group_Regular;
	private Integer group_Percent;
	
	private String HoloSpecies;
	
	private String Name = this.getClass().getSimpleName();
	
	@Override
	public boolean register(FileConfiguration conf, boolean first) {
		file = conf;
		if (first) {
			conf.set("Settings.Primary", "Spells Damage: [$][?][$]");
			conf.set("Settings.Regular", "[$]\\* Spells Damage");
			conf.set("Settings.Percent", "[$]\\% Spells Damage");
			
			conf.set("Settings.GroupLocation.Primary-min", 1);
			conf.set("Settings.GroupLocation.Primary-max", 2);
			conf.set("Settings.GroupLocation.Regular", 1);
			conf.set("Settings.GroupLocation.Percent", 1);
			
			conf.set("Settings.HoloSpecies", "default");
		}
		
		HoloSpecies = conf.getString("Settings.HoloSpecies");

		Primary = Pattern.compile(conf.getString("Settings.Primary").replace("[$]", "((?:\\-|\\+)?\\d+(?:\\.\\d+)?)").replace("[?]", "(?:\\s|\\S)"));
		Regular = Pattern.compile(conf.getString("Settings.Regular").replace("[$]", "((?:\\-|\\+)?\\d+(?:\\.\\d+)?)").replace("[?]", "(?:\\s|\\S)"));
		Percent = Pattern.compile(conf.getString("Settings.Percent").replace("[$]", "((?:\\-|\\+)?\\d+(?:\\.\\d+)?)").replace("[?]", "(?:\\s|\\S)"));
		
		group_Primary1 = conf.getInt("Settings.GroupLocation.Primary-min");
		group_Primary2 = conf.getInt("Settings.GroupLocation.Primary-max");
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
		return DamageType.ATTACK;
	}

	@Override
	public String getHoloSpecies() {
		return HoloSpecies;
	}

	@Override
	public SpecialAttributeData getAttribute(LivingEntity entity, ItemStack item, List<String> uncoloredlores) {
		
		Double[] primary = new Double[] { 0D, 0D };
		Double regular = 0D;
		Double percent = 0D;
		
		for (String lore : uncoloredlores) {
			Matcher m = Primary.matcher(lore);
			if (m.find()) {
				primary[0] = Double.valueOf(m.group(group_Primary1));
				primary[1] = Double.valueOf(m.group(group_Primary2));
			}
			
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
		
		EntityDamageByEntityEvent e = ((EntityDamageByEntityEvent) event);
		
		Double damage = AttributesManager.getPrimaryValue(attacker, Name) + AttributesManager.getRegularValue(attacker, Name);
		Double damage2 = damage + (damage * AttributesManager.getPercentValue(attacker, Name));
		
		if (victimattr != null) {
			Double defense = AttributesManager.getPrimaryValue(victim, "SpellsDefense") + AttributesManager.getRegularValue(victim, "SpellsDefense");
			Double defense2 = defense + (defense * AttributesManager.getPercentValue(victim, "SpellsDefense"));
			
			e.setDamage(e.getDamage() + damage2 - defense2);
			return damage2 - defense2;
		}
		else {
			e.setDamage(e.getDamage() + damage2);
			return damage2;
		}
	}

}