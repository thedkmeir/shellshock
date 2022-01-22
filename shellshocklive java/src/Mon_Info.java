import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;

import javax.swing.ImageIcon;

public class Mon_Info {
	private static int width = 160;
	private static int height = 75;

	// all sprites
	public static Image background;
	public static Image bar;
	public static Image health_bar;
	public static Font font;

	private int health;
	private String name;
	public static boolean show = false;

	public Mon_Info(String name) {
		this.name = name;
	}

	// load all sprites
	public static void LoadContent(String dir) {
		// GUITexture = new ImageIcon(dir + "\\" + assetname + "_Butt").getImage();
		// load all textures
		background = new ImageIcon(dir + "\\Mon_Info\\Background.png").getImage();
		health_bar = new ImageIcon(dir + "\\Mon_Info\\Life_Bar.png").getImage();
		bar = new ImageIcon(dir + "\\Mon_Info\\Bar.png").getImage();
		font = new Font("Monospaced", Font.BOLD, 26);
	}

	public void Update(int health) {
		this.health = health;
	}

	public void Draw(Graphics g) {
		int tx = MouseInfo.getPointerInfo().getLocation().x - Main.obj.getLocation().x;
		if (tx < width) {
			tx += width - tx + 5;
		}
		tx -= width;
		int ty = MouseInfo.getPointerInfo().getLocation().y - Main.obj.getLocation().y;
		int spacey = 5;
		ty -= height;
		ty += font.getSize();

		// g.drawImage(background, tx, ty - Main.obj.getInsets().top, null);
		g.setFont(font);
		g.setColor(Color.black);
		g.drawString(name, (tx + (width / 2) - (g.getFontMetrics().stringWidth(name) / 2)), ty);
		ty += font.getSize() + spacey;

		g.drawString("HP: " + health, (tx + (width / 2) - (g.getFontMetrics().stringWidth("HP: " + health) / 2)), ty);

		g.setColor(Color.black);
		g.drawImage(health_bar, tx + 5, ty + 10, (int) ((double) ((health_bar.getWidth(null) - 10) * (health * 0.005))),
				health_bar.getHeight(null), null);
		g.drawImage(bar, tx + 2, ty + 10, bar.getWidth(null) - 10, bar.getHeight(null), null);
	}
}
