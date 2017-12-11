package org.firescape.server.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.BelongsToParents;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.Table;

@BelongsToParents({
  @BelongsTo(foreignKeyName = "player_id", parent = Player.class),
  @BelongsTo(foreignKeyName = "friend_id", parent = Player.class)
})
@Table("player_friends")
@DbName("firescape")
public class PlayerFriend extends Model {}
