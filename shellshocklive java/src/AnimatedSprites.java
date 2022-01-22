import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class AnimatedSprites {
	// list of all animation
	public static List<AnimatedSprites> animes = new ArrayList<AnimatedSprites>();

	// enum that says if hte animation is over
	public enum state {
		done, play
	}

	private state states = state.play;
	private static Image[] textures;
	private int currentFrame;
	private int totalFrames;
	private double ogtimer;
	private double timer;

	// also vector position
	private int posx;
	private int posy;

	public state States() {
		return states;
	}

	public AnimatedSprites(Image[] texture, int x, int y) {
		textures = texture;
		currentFrame = 0;
		totalFrames = texture.length;
		timer = 0.15;
		ogtimer = timer;
		posx = x;
		posy = y;
	}

	public AnimatedSprites(Image[] texture, int x, int y, double timer) {
		textures = texture;
		currentFrame = 0;
		totalFrames = texture.length;
		this.timer = timer;
		ogtimer = timer;
		posx = x;
		posy = y;
	}

	// the update func updates a times and swaps to the next image
	public void Update() {
		timer -= Main.delta;
		if (timer <= 0) {
			currentFrame++;
			if (currentFrame >= totalFrames) {
				states = state.done;
			}
			timer = ogtimer;
		}
	}

	public void Draw(Graphics g) {
		if (states == state.play) {
			g.drawImage(textures[currentFrame], (int) posx - (textures[currentFrame].getWidth(null) / 2),
					(int) posy - (textures[currentFrame].getHeight(null) / 2) - Main.obj.getInsets().top, null);
		}
	}
}
