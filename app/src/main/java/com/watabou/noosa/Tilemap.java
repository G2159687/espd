
package com.watabou.noosa;

import android.graphics.RectF;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.utils.Rect;

import java.nio.FloatBuffer;

public class Tilemap extends Visual {

	protected SmartTexture texture;
	protected TextureFilm tileset;

	protected int[] data;
	protected int mapWidth;
	protected int mapHeight;
	protected int size;

	private float cellW;
	private float cellH;

	protected float[] vertices;
	protected short[] bufferPositions;
	protected short bufferLength;
	protected FloatBuffer quads;
	protected Vertexbuffer buffer;

	private volatile Rect updated;
	private boolean fullUpdate;
	private Rect updating;
	private int topLeftUpdating;
	private int bottomRightUpdating;

	public Tilemap(Object tx, TextureFilm tileset) {

		super(0, 0, 0, 0);

		this.texture = TextureCache.get(tx);
		this.tileset = tileset;

		RectF r = tileset.get(0);
		cellW = tileset.width(r);
		cellH = tileset.height(r);

		vertices = new float[16];

		updated = new Rect();
	}

	public void map(int[] data, int cols) {

		this.data = data;

		mapWidth = cols;
		mapHeight = data.length / cols;
		size = mapWidth * mapHeight;
		bufferPositions = new short[size];
		for (int i = 0; i < bufferPositions.length; i++)
			bufferPositions[i] = -1;
		bufferLength = 0;

		width = cellW * mapWidth;
		height = cellH * mapHeight;

		quads = Quad.createSet(size);

		updateMap();
	}


	public synchronized void updateMap() {
		updated.set(0, 0, mapWidth, mapHeight);
		fullUpdate = true;
		camX = null;
	}

	public synchronized void updateMapCell(int cell) {
		updated.union(cell % mapWidth, cell / mapWidth);
	}

	private synchronized void moveToUpdating() {
		updating = new Rect(updated);
		updated.setEmpty();
	}

	protected void updateVertices() {

		moveToUpdating();

		float y1 = cellH * updating.top;
		float y2 = y1 + cellH;

		for (int i = updating.top; i < updating.bottom; i++) {

			float x1 = cellW * updating.left;
			float x2 = x1 + cellW;

			int pos = i * mapWidth + updating.left;

			for (int j = updating.left; j < updating.right; j++) {

				//Currently if a none-rendered tile becomes rendered it will mess with culling in draw()
				//However shifting the whole array is expensive, even with selective updating
				//So right now I'm accepting this as an engine limitation, but support could be added.

				//It's also worth noting that nothing is stopping the game from rendering tiles
				//which will need to be visible in future as transparent, and accepting the small
				//performance cost of rendering them before they become visible
				if (needsRender(pos) || bufferPositions[pos] != -1) {
					int bufferPos = bufferPositions[pos];
					if (bufferPos == -1) {
						bufferPos = bufferPositions[pos] = bufferLength;
						bufferLength++;
					}

					if (topLeftUpdating == 0)
						topLeftUpdating = bufferPos;

					bottomRightUpdating = bufferPos + 1;

					quads.position(bufferPos * 16);
					RectF uv = tileset.get(data[pos]);

					vertices[0] = x1;
					vertices[1] = y1;

					vertices[2] = uv.left;
					vertices[3] = uv.top;

					vertices[4] = x2;
					vertices[5] = y1;

					vertices[6] = uv.right;
					vertices[7] = uv.top;

					vertices[8] = x2;
					vertices[9] = y2;

					vertices[10] = uv.right;
					vertices[11] = uv.bottom;

					vertices[12] = x1;
					vertices[13] = y2;

					vertices[14] = uv.left;
					vertices[15] = uv.bottom;

					quads.put(vertices);
				}

				pos++;
				x1 = x2;
				x2 += cellW;

			}

			y1 = y2;
			y2 += cellH;
		}

	}

	int topLeft, bottomRight, length;

	//check these before updating, allows for cached values when the camera isn't moving.
	Integer camX, camY, camW, camH;

	@Override
	public void draw() {

		super.draw();

		if (!updated.isEmpty()) {
			updateVertices();
			quads.limit(bufferLength * 16);
			if (buffer == null)
				buffer = new Vertexbuffer(quads);
			else {
				if (fullUpdate) {
					buffer.updateVertices(quads);
					fullUpdate = false;
				} else {
					buffer.updateVertices(quads,
							topLeftUpdating * 16,
							bottomRightUpdating * 16);
				}
			}
			topLeftUpdating = 0;
			updating.setEmpty();
		}

		Camera c = Camera.main;

		if (camX == null || camY == null || camW == null || camH == null
				|| camX != (int) c.scroll.x / 16
				|| camY != (int) c.scroll.y / 16
				|| camW != (int) Math.ceil(c.width / cellW)
				|| camH != (int) Math.ceil(c.height / cellH)) {
			camX = (int) c.scroll.x / 16;
			camY = (int) c.scroll.y / 16;
			camW = (int) Math.ceil(c.width / cellW);
			camH = (int) Math.ceil(c.height / cellH);

			if (camX >= mapWidth
					|| camY >= mapHeight
					|| camW + camW <= 0
					|| camH + camH <= 0)
				return;

			//determines the top-left visible tile, the bottom-right one, and the buffer length
			//between them, this culls a good number of none-visible tiles while keeping to 1 draw
			topLeft = Math.max(camX, 0)
					+ Math.max(camY * mapWidth, 0);
			while (topLeft < bufferPositions.length && bufferPositions[topLeft] == -1)
				topLeft++;

			bottomRight = Math.min(camX + camW, mapWidth - 1)
					+ Math.min((camY + camH) * mapWidth, (mapHeight - 1) * mapWidth);
			while (bottomRight >= topLeft && bufferPositions[bottomRight] == -1)
				bottomRight--;

			if (topLeft >= bufferPositions.length || bottomRight <= 0)
				length = 0;
			else
				length = bufferPositions[bottomRight] - bufferPositions[topLeft] + 1;
		}

		if (camX >= mapWidth
				|| camY >= mapHeight
				|| camW + camW <= 0
				|| camH + camH <= 0
				|| length <= 0)
			return;

		NoosaScript script = NoosaScriptNoLighting.get();

		texture.bind();

		script.uModel.valueM4(matrix);

		script.camera(camera);
		script.drawQuadSet(buffer, length, bufferPositions[topLeft]);

	}

	@Override
	public void destroy() {
		super.destroy();
		if (buffer != null)
			buffer.delete();
	}

	protected boolean needsRender(int pos) {
		return true;
	}
}
