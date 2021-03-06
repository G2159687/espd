
package com.github.epd.sprout.levels.rooms.special;

import com.github.epd.sprout.actors.mobs.npcs.RatKingDen;
import com.github.epd.sprout.items.bombs.ActiveMrDestructo2;
import com.github.epd.sprout.items.Egg;
import com.github.epd.sprout.items.Generator;
import com.github.epd.sprout.items.Gold;
import com.github.epd.sprout.items.Heap;
import com.github.epd.sprout.items.Item;
import com.github.epd.sprout.items.bombs.SeekingClusterBombItem;
import com.github.epd.sprout.levels.Level;
import com.github.epd.sprout.levels.Terrain;
import com.github.epd.sprout.levels.painters.Painter;
import com.github.epd.sprout.levels.rooms.Room;
import com.github.epd.sprout.levels.rooms.standard.EmptyRoom;
import com.github.epd.sprout.plants.Phaseshift;
import com.github.epd.sprout.plants.Starflower;
import com.watabou.utils.Random;

public class RatKingRoom2 extends SpecialRoom {

	@Override
	public boolean canConnect(Room r) {
		//never at the end of a connection room, or at the entrance
		return r instanceof EmptyRoom && super.canConnect(r);
	}

	//reduced max size to limit chest numbers.
	// normally would gen with 8-28, this limits it to 8-16
	@Override
	public int maxHeight() {
		return 7;
	}

	public int maxWidth() {
		return 7;
	}

	public void paint(Level level) {

		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY_SP);

		Door entrance = entrance();
		entrance.set(Door.Type.HIDDEN);
		int door = entrance.x + entrance.y * level.getWidth();

		for (int i = left + 1; i < right; i++) {
			addChest(level, (top + 1) * level.getWidth() + i, door);
			addChest(level, (bottom - 1) * level.getWidth() + i, door);
		}

		for (int i = top + 2; i < bottom - 1; i++) {
			addChest(level, i * level.getWidth() + left + 1, door);
			addChest(level, i * level.getWidth() + right - 1, door);
		}

		RatKingDen king = new RatKingDen();
		king.pos = level.pointToCell(random(2));
		level.mobs.add(king);
	}

	private static void addChest(Level level, int pos, int door) {

		if (pos == door - 1 || pos == door + 1 || pos == door - level.getWidth()
				|| pos == door + level.getWidth()) {
			return;
		}

		Item prize;
		switch (Random.Int(8)) {
			case 0:
				prize = new Egg();
				break;
			case 1:
				prize = new Phaseshift.Seed();
				break;
			case 2:
				prize = Generator.random(Generator.Category.BERRY);
				break;
			case 3:
				prize = new Starflower.Seed();
				break;
			case 5:
				prize = new ActiveMrDestructo2();
				break;
			case 6:
				prize = new SeekingClusterBombItem();
				break;
			default:
				prize = new Gold(Random.IntRange(100, 500));
				break;
		}

		level.drop(prize, pos).type = Heap.Type.CHEST;
	}
}

