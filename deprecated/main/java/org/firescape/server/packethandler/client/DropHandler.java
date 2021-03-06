package org.firescape.server.packethandler.client;

import org.apache.mina.common.IoSession;
import org.firescape.server.event.DelayedEvent;
import org.firescape.server.model.InvItem;
import org.firescape.server.model.Item;
import org.firescape.server.model.Player;
import org.firescape.server.model.World;
import org.firescape.server.net.Packet;
import org.firescape.server.packethandler.PacketHandler;
import org.firescape.server.states.Action;

public class DropHandler implements PacketHandler {

  /**
   * World instance
   */
  public static final World world = World.getWorld();

  public void handlePacket(Packet p, IoSession session) throws Exception {
    Player player = (Player) session.getAttachment();
    if (player.isBusy()) {
      player.resetPath();
      return;
    }
    player.resetAll();
    int idx = (int) p.readShort();
    if (idx < 0 || idx >= player.getInventory().size()) {
      player.setSuspiciousPlayer(true);
      return;
    }
    InvItem item = player.getInventory().get(idx);
    if (item == null) {
      player.setSuspiciousPlayer(true);
      return;
    }
    player.setStatus(Action.DROPPING_GITEM);
    world.getDelayedEventHandler().add(new DelayedEvent(player, 500) {
      public void run() {
        if (owner.isBusy() || !owner.getInventory().contains(item) || owner.getStatus() != Action.DROPPING_GITEM) {
          running = false;
          return;
        }
        if (owner.hasMoved()) {
          return;
        }
        owner.getActionSender().sendSound("dropobject");
        owner.getInventory().remove(item);
        owner.getActionSender().sendInventory();
        DelayedEvent.world.registerItem(new Item(item.getID(), owner.getX(), owner.getY(), item.getAmount(), owner));
        running = false;
      }
    });
  }
}