
package com.github.epd.sprout.items.food;

import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.actors.blobs.Blob;
import com.github.epd.sprout.actors.blobs.Fire;
import com.github.epd.sprout.actors.buffs.Hunger;
import com.github.epd.sprout.actors.hero.Hero;
import com.github.epd.sprout.actors.mobs.Mob;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.scenes.GameScene;
import com.github.epd.sprout.sprites.ItemSpriteSheet;
import com.github.epd.sprout.utils.GLog;
import com.watabou.utils.Random;

public class JackOLantern extends Food {

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.MUSHROOM_LANTERN;
		energy = (Hunger.STARVING - Hunger.HUNGRY) / 10;
		message = Messages.get(BlueMilk.class, "eat");
		bones = false;
	}

	private static final String TXT_PREVENTING = Messages.get(BlueMilk.class, "prevent");
	private static final String TXT_EFFECT = Messages.get(JackOLantern.class, "effect");

	@Override
	public void execute(Hero hero, String action) {

		if (action.equals(AC_EAT)) {

			if (Dungeon.bossLevel()) {
				GLog.w(TXT_PREVENTING);
				return;
			}

		}

		if (action.equals(AC_EAT)) {


			GLog.w(TXT_EFFECT);

			switch (Random.Int(10)) {
				case 1:
					for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
						if (mob.hostile)
							GameScene.add(Blob.seed(mob.pos, 3, Fire.class));
					}
					//GameScene.add(Blob.seed(hero.pos, 1, Fire.class));
					break;
				case 0:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
						if (mob.hostile)
							if (Random.Int(2) == 0) {
								GameScene.add(Blob.seed(mob.pos, 3, Fire.class));
							}
					}
					if (Random.Int(5) == 0) {
						GameScene.add(Blob.seed(hero.pos, 2, Fire.class));
					}
					break;
			}
		}

		super.execute(hero, action);
	}

	@Override
	public String info() {
		return Messages.get(this, "desc");
	}

	@Override
	public int price() {
		return 20 * quantity;
	}

	public JackOLantern() {
		this(1);
	}

	public JackOLantern(int value) {
		this.quantity = value;
	}
}
