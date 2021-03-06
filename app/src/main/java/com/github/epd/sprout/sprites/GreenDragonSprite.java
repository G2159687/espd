
package com.github.epd.sprout.sprites;

import com.github.epd.sprout.Assets;
import com.github.epd.sprout.ShatteredPixelDungeon;
import com.github.epd.sprout.actors.Char;
import com.github.epd.sprout.actors.mobs.pets.GreenDragon;
import com.github.epd.sprout.effects.Lightning;
import com.github.epd.sprout.scenes.GameScene;
import com.github.epd.sprout.ui.HealthBar;
import com.watabou.noosa.TextureFilm;

public class GreenDragonSprite extends MobSprite {

	public HealthBar hpBar;

	//Frames 1-4 are idle, 5-8 are moving, 9-12 are attack and the last are for death RBVG

	public GreenDragonSprite() {
		super();

		texture(Assets.PETDRAGON);

		TextureFilm frames = new TextureFilm(texture, 16, 16);

		idle = new Animation(2, true);
		idle.frames(frames, 48, 49, 50, 51);

		run = new Animation(8, true);
		run.frames(frames, 52, 53, 54, 55);

		attack = new Animation(8, false);
		attack.frames(frames, 56, 57, 58, 59);

		zap = attack.clone();

		die = new Animation(8, false);
		die.frames(frames, 60, 61, 62, 63);

		play(idle);
	}

	@Override
	public void zap(int pos) {

		parent.add(new Lightning(ch.pos, pos, (GreenDragon) ch));

		turnTo(ch.pos, pos);
		play(zap);
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		if (ch instanceof GreenDragon) {
			final Char finalCH = ch;
			hpBar = new HealthBar() {
				@Override
				public synchronized void update() {
					super.update();
					hpBar.setRect(finalCH.sprite.x, finalCH.sprite.y - 3, finalCH.sprite.width, hpBar.height());
					hpBar.level(finalCH);
					visible = finalCH.sprite.visible;
				}
			};
			((GameScene) ShatteredPixelDungeon.scene()).ghostHP.add(hpBar);
		}
	}

	@Override
	public int blood() {
		return 0xFFcdcdb7;
	}

	@Override
	public void die() {
		super.die();

		if (hpBar != null) {
			hpBar.killAndErase();
		}
	}

	@Override
	public void killAndErase(){

		if (hpBar != null) {
			hpBar.killAndErase();
		}

		super.killAndErase();
	}
}
