
package com.github.epd.sprout.levels;

import com.github.epd.sprout.Assets;
import com.github.epd.sprout.Bones;
import com.github.epd.sprout.Dungeon;
import com.github.epd.sprout.actors.Actor;
import com.github.epd.sprout.actors.Char;
import com.github.epd.sprout.actors.mobs.Mob;
import com.github.epd.sprout.actors.mobs.SkeletonHand1;
import com.github.epd.sprout.actors.mobs.SkeletonHand2;
import com.github.epd.sprout.actors.mobs.SkeletonKing;
import com.github.epd.sprout.items.Heap;
import com.github.epd.sprout.items.Item;
import com.github.epd.sprout.items.keys.SkeletonKey;
import com.github.epd.sprout.items.potions.PotionOfLiquidFlame;
import com.github.epd.sprout.levels.painters.Painter;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.scenes.GameScene;
import com.watabou.noosa.Scene;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SkeletonBossLevel extends Level {

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
		cleared = true;
		viewDistance = 8;
	}

	private static final int TOP = 2;
	private static final int HALL_WIDTH = 13;
	private static final int HALL_HEIGHT = 15;
	private static final int CHAMBER_HEIGHT = 3;

	private static final int LEFT = (48 - HALL_WIDTH) / 2;
	private static final int CENTER = LEFT + HALL_WIDTH / 2;

	private int arenaDoor;
	private boolean enteredArena = false;
	private boolean keyDropped = false;

	@Override
	public String tilesTex() {
		return Assets.TILES_SKELETON;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_PRISON;
	}

	private static final String DOOR = "door";
	private static final String ENTERED = "entered";
	private static final String DROPPED = "droppped";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DOOR, arenaDoor);
		bundle.put(ENTERED, enteredArena);
		bundle.put(DROPPED, keyDropped);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		arenaDoor = bundle.getInt(DOOR);
		enteredArena = bundle.getBoolean(ENTERED);
		keyDropped = bundle.getBoolean(DROPPED);
	}

	@Override
	protected boolean build() {

		setSize(48, 48);

		Painter.fill(this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY);
		Painter.fill(this, CENTER, TOP, 1, HALL_HEIGHT, Terrain.EMPTY);

		int y = TOP + 1;
		while (y < TOP + HALL_HEIGHT) {
			map[y * getWidth() + CENTER - 2] = Terrain.STATUE;
			map[y * getWidth() + CENTER + 2] = Terrain.STATUE;
			y += 2;
		}

		exit = (TOP - 1) * getWidth() + CENTER;
		map[exit] = Terrain.LOCKED_EXIT;

		arenaDoor = (TOP + HALL_HEIGHT) * getWidth() + CENTER;
		map[arenaDoor] = Terrain.DOOR;

		Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH,
				CHAMBER_HEIGHT, Terrain.EMPTY);
		Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT,
				Terrain.WATER);
		Painter.fill(this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1,
				CHAMBER_HEIGHT, Terrain.WATER);

		entrance = (TOP + HALL_HEIGHT + 2 + Random.Int(CHAMBER_HEIGHT - 1))
				* getWidth() + LEFT + (/* 1 + */Random.Int(HALL_WIDTH - 2));
		map[entrance] = Terrain.PEDESTAL;

		map[exit] = Terrain.WALL;
		decorate();

		return true;
	}


	protected void decorate() {

		for (int i = 0; i < getLength(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
				map[i] = Terrain.EMPTY_DECO;
			} else if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}

		int shrub1 = arenaDoor + getWidth();
		int shrub2 = arenaDoor + getWidth() + 1;
		int shrub3 = arenaDoor + getWidth() - 1;
		int potionpos = arenaDoor + 2 * getWidth();
		map[shrub1] = Terrain.SHRUB;
		map[shrub2] = Terrain.SHRUB;
		map[shrub3] = Terrain.SHRUB;
		drop(new PotionOfLiquidFlame(), potionpos);

		for (int i = 0; i < getLength(); i++) {
			if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
			if (map[i] == Terrain.ENTRANCE) {
				map[i] = Terrain.EMPTY;
			}
			if (map[i] == Terrain.EMPTY && heaps.get(i) == null && Random.Float() < .20) {
				map[i] = Terrain.HIGH_GRASS;
			}
			if (map[i] == Terrain.EMPTY && heaps.get(i) == null && Random.Float() < .25) {
				map[i] = Terrain.GRASS;
			}
			if (map[i] == Terrain.EMPTY && heaps.get(i) == null && Random.Float() < .30) {
				map[i] = Terrain.SHRUB;
			}
		}

	}

	public static int pedestal(boolean left) {
		if (left) {
			return (TOP + HALL_HEIGHT / 2) * Dungeon.level.getWidth() + CENTER - 2;
		} else {
			return (TOP + HALL_HEIGHT / 2) * Dungeon.level.getWidth() + CENTER + 2;
		}
	}

	@Override
	protected void createMobs() {
	}

	@Override
	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos = Random.IntRange(LEFT + 1, LEFT + HALL_WIDTH - 2)
						+ Random.IntRange(TOP + HALL_HEIGHT + 1, TOP
						+ HALL_HEIGHT + CHAMBER_HEIGHT) * getWidth();
			} while (pos == entrance);
			drop(item, pos).type = Heap.Type.REMAINS;
		}
	}

	@Override
	public int randomRespawnCell() {
		return -1;
	}

	@Override
	public void press(int cell, Char hero) {

		super.press(cell, hero);

		if (!enteredArena && outsideEntraceRoom(cell) && hero == Dungeon.hero) {

			enteredArena = true;
			//locked = true;

			Mob boss = new SkeletonKing();
			Mob hand1 = new SkeletonHand1();
			Mob hand2 = new SkeletonHand2();
			boss.state = boss.WANDERING;
			hand1.state = hand1.HUNTING;
			hand2.state = hand2.HUNTING;
			int count = 0;
			do {
				boss.pos = Random.Int(getLength());
				hand1.pos = (TOP + 1) * getWidth() + CENTER;
				hand2.pos = (TOP + 1) * getWidth() + CENTER + 1;

			} while (!passable[boss.pos]
					|| !outsideEntraceRoom(boss.pos)
					|| (Dungeon.visible[boss.pos] && count++ < 20));

			GameScene.add(boss);

			GameScene.add(hand1);
			GameScene.add(hand2);


			if (Dungeon.visible[boss.pos]) {
				boss.notice();
				boss.sprite.alpha(0);
				boss.sprite.parent.add(new AlphaTweener(boss.sprite, 1, 0.1f));
			}

			//set(arenaDoor, Terrain.WALL);
			//GameScene.updateMap(arenaDoor);
			Dungeon.observe();
		}
	}

	@Override
	public Heap drop(Item item, int cell) {

		if (!keyDropped && item instanceof SkeletonKey) {

			keyDropped = true;
			locked = false;

			set(arenaDoor, Terrain.DOOR);
			GameScene.updateMap(arenaDoor);
			Dungeon.observe();
		}

		return super.drop(item, cell);
	}

	private boolean outsideEntraceRoom(int cell) {
		return cell / getWidth() < arenaDoor / getWidth();
	}

	public String tileName(int tile) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(SewerLevel.class, "water_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(SkeletonBossLevel.class, "high_grass_name");
			default:
				return super.tileName(tile);
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(SkeletonBossLevel.class, "empty_deco_desc");
			case Terrain.HIGH_GRASS:
				return Messages.get(SkeletonBossLevel.class, "high_grass_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(SewerLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc(tile);
		}
	}

	@Override
	public void addVisuals(Scene scene) {
		CityLevel.addVisuals(this, scene);
	}
}
