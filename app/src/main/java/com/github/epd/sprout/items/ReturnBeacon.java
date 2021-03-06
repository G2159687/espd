
package com.github.epd.sprout.items;

import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.actors.buffs.Buff;
import com.github.epd.sprout.actors.buffs.Invisibility;
import com.github.epd.sprout.actors.hero.Hero;
import com.github.epd.sprout.actors.mobs.Mob;
import com.github.epd.sprout.items.artifacts.DriedRose;
import com.github.epd.sprout.items.artifacts.TimekeepersHourglass;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.scenes.InterlevelScene;
import com.github.epd.sprout.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class ReturnBeacon extends Item {

	{
		defaultAction = AC_RETURN;
	}


	private static final String TXT_INFO = Messages.get(ReturnBeacon.class, "desc");

	public static final float TIME_TO_USE = 1;

	//public static final String AC_SET = "SET";
	public static final String AC_RETURN = Messages.get(ReturnBeacon.class, "ac_return");

	//private int returnDepth = -1;
	//private int returnPos;

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.BEACON;

		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_RETURN);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		if (action == AC_RETURN) {


			Buff buff = Dungeon.hero
					.buff(TimekeepersHourglass.timeFreeze.class);
			if (buff != null)
				buff.detach();

			Buff buffinv = Dungeon.hero.buff(Invisibility.class);
			if (buffinv != null)
				buffinv.detach();
			Invisibility.dispel();
			Dungeon.hero.invisible = 0;

			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
				if (mob instanceof DriedRose.GhostHero)
					mob.destroy();

			InterlevelScene.mode = InterlevelScene.Mode.RETURNSAVE;
			InterlevelScene.returnDepth = 1;
			InterlevelScene.returnPos = 1;
			Game.switchScene(InterlevelScene.class);


		} else {

			super.execute(hero, action);

		}
	}


	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}


	@Override
	public String info() {
		return TXT_INFO;
	}
}
