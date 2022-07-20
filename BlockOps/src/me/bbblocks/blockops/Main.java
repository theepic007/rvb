package me.bbblocks.blockops;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener
{
	List<UUID> playersInGame = new ArrayList<UUID>();
	boolean isLoopGoing = false;
	
	@Override
	public void onEnable()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	private void startLoop()
	{
		isLoopGoing = true;
	}
	
	private void stopLoop()
	{
		isLoopGoing = false;
	}
	
	private void arrowLoop(final Arrow arrow, final Entity entity)
	{
		new BukkitRunnable()
		{
			private int counter = 100;
			
			@Override
			public void run()
			{
				if(!isLoopGoing)
				{
					cancel();
					return;
				}
				
				if(counter < 3 || arrow.isOnGround())
				{
					arrow.remove();
					stopLoop();
					return;
				}
				
				
			}
		}.runTaskTimer(this, 0, 1);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		e.getPlayer().getInventory().clear();
		
		List<String> lore = new ArrayList<String>();
		ItemStack c1 = new ItemStack(Material.BOW);
		lore.add("Gives Trick Bow and Small Knife");
		lore.add("Low Health and High Damage with Permanent Speed 1");
		createMeta(c1, lore);
		e.getPlayer().getInventory().addItem(c1);
		lore.clear();
		
		ItemStack c2 = new ItemStack(Material.NETHERITE_SWORD);
		lore.add("Gives a Great Sword and Medium Shield");
		lore.add("Medium Health and High Damage but Permanent Slow 2 and Swing Speed is Reduced");
		createMeta(c2,lore);
		e.getPlayer().getInventory().addItem(c2);
		lore.clear();
		
		ItemStack c3 = new ItemStack(Material.OBSIDIAN);
		lore.add("Gives Tower Shield and Defence Magic");
		lore.add("Tower Shield gives you resistance 4 but Slowness 5");
		lore.add("Defence Magic gives teammates Resistance 2 for 5 seconds");
		lore.add("Defence Magic makes enemies Take 10% more damage for 3 seconds");
		lore.add("Defence Magic has a 20 second cooldown and a 5 block radius");
		createMeta(c3,lore);
		e.getPlayer().getInventory().addItem(c3);
		lore.clear();
		
		ItemStack c4 = new ItemStack(Material.APPLE);
		lore.add("");
		lore.add("");
		lore.add("");
		lore.add("");
		createMeta(c4,lore);
		e.getPlayer().getInventory().addItem(c4);
		lore.clear();
	}
	
	@EventHandler
	public void invClick(InventoryClickEvent e)
	{
		if(e.getInventory().getHolder() instanceof Player && e.getClickedInventory() != null)
		{
			Player p = (Player) e.getClickedInventory().getHolder();
			UUID uuid = p.getUniqueId();
			
			if(!playersInGame.contains(uuid))
			{
				List<String> lore = new ArrayList<String>();
				
				e.setCancelled(true);
				if(e.getSlot() == 0)
				{
					p.getInventory().clear();
					ItemStack tBow = new ItemStack(Material.BOW);
					ItemStack knife = new ItemStack(Material.IRON_SWORD);
					ItemMeta im = knife.getItemMeta();
			        AttributeModifier am_ad = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
			        im.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, am_ad);
			        knife.setItemMeta(im);
			        p.getInventory().addItem(knife);
			        lore.add("Trick Bow can shoot other arrows mid-air to make shots target enemies!");
			        lore.add("Homing shots deal double damage and can be activated by your own arrows");
			        createMeta(tBow, lore);
			        tBow.getItemMeta().setDisplayName("Trick Bow");
			        p.getInventory().addItem(tBow);
				}
			}
			else
			{
				
			}
		}
	}
	
	@EventHandler
	public void onArrowShoot(ProjectileLaunchEvent e)
	{
		if(e.getEntity().getShooter() instanceof Player)
		{
			Player p = (Player) e.getEntity().getShooter();
			
			if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Trick Bow"))
			{
				Entity ent = p.getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.PIG);
				PotionEffect pe = new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 1, false, false);
				PotionEffect pe2 = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100000, 10, false, false);
				((LivingEntity) ent).addPotionEffect(pe);
				((LivingEntity) ent).addPotionEffect(pe2);
				
				arrowLoop((Arrow)e.getEntity(), ent);
				startLoop();
			}
		}
	}
	
	private ItemStack createMeta(ItemStack i, List<String> l)
	{
		ItemMeta im = i.getItemMeta();
		List<String> il = new ArrayList<String>();
		il.addAll(l);
		im.setLore(il);
		i.setItemMeta(im);
		return i;
	}
}