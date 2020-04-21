package ru.nukkitx;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.plugin.PluginBase;

public class Dimensions extends PluginBase implements Listener {
    @Override
    public void onEnable() {
        Server.getInstance().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDataPacketSend(DataPacketSendEvent event) {
        DataPacket packet = event.getPacket();
        Player player = event.getPlayer();

        if (packet instanceof StartGamePacket) {
            StartGamePacket startGamePacket = (StartGamePacket) packet;
            startGamePacket.dimension = (byte) player.getLevel().getDimension();
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
    	if(event.getEntity().getLocation().getLevel().getDimension() != Level.DIMENSION_OVERWORLD) {
    		 Player player = event.getEntity();
    	     player.teleport(player.getSpawn(), TeleportCause.PLUGIN);
    	}       
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLevelChange(EntityLevelChangeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;

            ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();
            changeDimensionPacket.dimension = event.getTarget().getDimension();
            changeDimensionPacket.respawn = true;

            PlayStatusPacket playStatusPacket = new PlayStatusPacket();
            playStatusPacket.status = PlayStatusPacket.PLAYER_SPAWN;

            player.dataPacket(changeDimensionPacket);
            player.directDataPacket(playStatusPacket);
        }
    }
}