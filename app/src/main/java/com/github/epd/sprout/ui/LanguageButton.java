
package com.github.epd.sprout.ui;

import com.github.epd.sprout.Assets;
import com.github.epd.sprout.messages.Messages;
import com.github.epd.sprout.windows.WndLangs;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

public class LanguageButton extends Button {

	private Image image;

	public LanguageButton() {
		super();

		width = image.width;
		height = image.height;
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		image = Icons.get(Icons.LANGS);
		add(image);
		updateIcon();
	}

	private void updateIcon() {
		switch (Messages.lang().status()) {
			case INCOMPLETE:
				image.tint(1, 0, 0, .5f);
				break;
			case UNREVIEWED:
				image.tint(1, .5f, 0, .5f);
				break;
		}
	}

	@Override
	protected void layout() {
		super.layout();

		image.x = x;
		image.y = y;
	}

	@Override
	protected void onTouchDown() {
		image.brightness(1.5f);
		Sample.INSTANCE.play(Assets.SND_CLICK);
	}

	@Override
	protected void onTouchUp() {
		image.resetColor();
		updateIcon();
	}

	@Override
	protected void onClick() {
		parent.add(new WndLangs());
	}

}
