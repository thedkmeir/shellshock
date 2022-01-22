import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Draw extends JPanel {
	// dir to content
	static String dir = System.getProperty("user.dir") + "\\src\\sprites";

	/// sprites load
	// map
	private static Image map_sprite;
	private static Image background;
	// public static int[] map;
	// tank arrows
	private static Image arrow_sprite;
	private static Image full_arrow_sprite;

	File imageFile = new File(dir + "\\Misc\\tank_arrowFull.png");
	BufferedImage bufferedImage;

	// font
	public static Font font;
	public static Font end_font;
	// explosion animation
	static Image[] explosion2anim = new Image[5];
	static Image[] explosion3anim = new Image[3];

	// load all the sprites...
	public Draw() {
		super.setDoubleBuffered(true);

		// map setup
		Map.rndmap();
		map_sprite = new ImageIcon(dir + "\\Objects\\map.jpg").getImage();
		// map = Map.Maphights();
		background = new ImageIcon(dir + "\\Misc\\space.png").getImage();

		// user setup
		Random rnd = new Random();
		Main.Human = new Player(rnd.nextInt(500 - 20 + 1) + 20, 1);
		Main.Bot = new Second_Player(rnd.nextInt(1240 - 780 + 1) + 780);
		Main.Human.tank.setPosy(Map.get_y(Main.Human.tank.getPosx()) - 17);
		Main.Human.tank.setTankangle((Map.get_angle(Main.Human.tank.getPosx() - 10, Main.Human.tank.getPosx() + 10,
				Map.get_y((int) Main.Human.tank.getPosx() - 10), Map.get_y((int) Main.Human.tank.getPosx() + 10))));
		Main.Bot.tank.setPosy(Map.get_y(Main.Bot.tank.getPosx()) - 17);
		Main.Bot.tank.setTankangle((Map.get_angle(Main.Bot.tank.getPosx() - 10, Main.Bot.tank.getPosx() + 10,
				Map.get_y((int) Main.Bot.tank.getPosx() - 10), Map.get_y((int) Main.Bot.tank.getPosx() + 10))));
		// arrow setup
		arrow_sprite = new ImageIcon(dir + "\\Misc\\tank_arrowEmpty.png").getImage();
		full_arrow_sprite = new ImageIcon(dir + "\\Misc\\tank_arrowFull.png").getImage();

		// fonts
		font = new Font("Monospaced", Font.BOLD, 28);
		end_font = new Font("Monospaced", Font.BOLD, 90);
		// Weapon_Menu_Ele.back_mon = new ImageIcon(dir +
		// "\\Player_Bar\\Weapon_Bar_Mon.jpg").getImage();
		Weapon_Menu_Ele.font = new Font("Monospaced", Font.BOLD, 18);

		Main.menu.LoadContent(dir);
		Player_Bar.LoadContent(dir);
		Tanks.LoadContent(dir);
		Mon_Info.LoadContent(dir);

		Shot.LoadContent(dir);
		Split_Shot.LoadContent(dir);
		Ghost_Bomb.LoadContent(dir);
		Bouncer.LoadContent(dir);

		Crates.LoadContent(dir);

		// all explosion sprites load
		explosion2anim[0] = new ImageIcon(dir + "\\Explosions\\tank_explosion5.png").getImage();
		explosion2anim[1] = new ImageIcon(dir + "\\Explosions\\tank_explosion6.png").getImage();
		explosion2anim[2] = new ImageIcon(dir + "\\Explosions\\tank_explosion7.png").getImage();
		explosion2anim[3] = new ImageIcon(dir + "\\Explosions\\tank_explosion8.png").getImage();
		explosion2anim[4] = new ImageIcon(dir + "\\Explosions\\tank_explosion9.png").getImage();

		explosion3anim[0] = new ImageIcon(dir + "\\Explosions\\tank_explosion10.png").getImage();
		explosion3anim[1] = new ImageIcon(dir + "\\Explosions\\tank_explosion11.png").getImage();
		explosion3anim[2] = new ImageIcon(dir + "\\Explosions\\tank_explosion12.png").getImage();

		try {
			bufferedImage = ImageIO.read(imageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static BufferedImage cropImage(BufferedImage bufferedImage, int width, int height) {
		BufferedImage croppedImage = bufferedImage.getSubimage(0, 0, width, height);
		return croppedImage;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// activate anti aliasing
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform backup = g2d.getTransform();

		// draw background
		g.drawImage(background, 0, 0, null);
		g.setColor(Color.white);

		// draw all map
		for (int i = 0; i < Map.maphights.length; i++) {
			g.drawImage(map_sprite, i, Map.get_y(i) - Main.obj.getInsets().top, null);
		}

		// draw tanks
		for (Tanks tn : Tanks.tanks) {
			tn.Draw(g);
		}

		// draw all crates
		for (Crates cr : Crates.crates) {
			cr.Draw(g);
		}

		// draw all animations
		for (AnimatedSprites ani : AnimatedSprites.animes) {
			ani.Draw(g);
		}

		// draw gui, arrows for angle and power
		if (Main.playstate != Main.PlayState.midturn && Main.gamestate != Main.GameState.mainmenu) {
			g.setFont(font);
			if (Main.playstate == Main.PlayState.turn1) {
				AffineTransform tx = AffineTransform.getRotateInstance(-Main.Human.tank.getShotangle(),
						Main.Human.tank.getPosx(), Main.Human.tank.getPosy() - Main.obj.getInsets().top);
				g2d.setTransform(tx);
				g2d.drawImage(arrow_sprite, Main.Human.tank.getPosx(),
						Main.Human.tank.getPosy() - Main.obj.getInsets().top - 24, null);
				BufferedImage Buffered_Arrow = cropImage(bufferedImage,
						(int) ((full_arrow_sprite.getWidth(null) - 36) * (Main.Human.tank.getPower() / 100) + 36),
						full_arrow_sprite.getHeight(null));
				g2d.drawImage(Buffered_Arrow, null, Main.Human.tank.getPosx(),
						Main.Human.tank.getPosy() - Main.obj.getInsets().top - 24);

				g2d.setTransform(backup);
				g.setColor(Color.white);
				Main.Human.player_bar.Draw(g);
				g.setFont(font);
				g.drawString(Main.Human.name + "r Turn", 5, Main.obj.getInsets().top + 38);
			}
			if (Main.playstate == Main.PlayState.turn2) {
				g.setFont(font);
				g.drawString(Main.Bot.name + "'s Turn", 5, Main.obj.getInsets().top + 38);
			}

			g2d.setTransform(backup);
			g.drawString("Its the " + Main.turn_count + "th Turn", 5, Main.obj.getInsets().top - 5);
			g.drawString("Time: " + (int) Main.roundtimer,
					(int) ((1280 / 2) - g.getFontMetrics().stringWidth("Time: " + (int) Main.roundtimer) / 2),
					Main.obj.getInsets().top - 5);
		}

		// draw all the shots
		if (Main.playstate == Main.PlayState.midturn) {
			for (Shot sh : Shot.shots) {
				if (sh.is_ready) {
					Image tex = sh.get_texture();
					AffineTransform tx = AffineTransform.getRotateInstance(sh.angle, sh.posx,
							sh.posy - Main.obj.getInsets().top);
					g2d.setTransform(tx);
					g2d.drawImage(tex, (int) sh.get_posx(), (int) sh.get_posy() - Main.obj.getInsets().top, null);
					g2d.setTransform(backup);
				}
			}
		}

		// draw tha main menu
		if (Main.gamestate == Main.GameState.mainmenu) {
			Main.menu.Draw(g);
			if (Main.playstate == Main.PlayState.midturn) {
				g.setFont(end_font);
				if (Main.Bot.tank.getHealth() == 0 && Main.Human.tank.getHealth() == 0) {
					g.setColor(Color.BLACK);
					g.drawString("HMMMMMM THATS WEIRD",
							(1280 / 2) - (g.getFontMetrics().stringWidth("HMMMMMM THATS WEIRD") / 2), 200 - 20);
					g.drawString("I GUESS ITS A TIE THEN?",
							(1280 / 2) - (g.getFontMetrics().stringWidth("I GUESS ITS A TIE THEN?") / 2), 250 + 20);
					g.drawString("¯\\_(ツ)_/¯", (1280 / 2) - (g.getFontMetrics().stringWidth("¯\\_(ツ)_/¯") / 2), 460);
				} else if (Main.Bot.tank.getHealth() == 0) {
					g.setColor(Color.GREEN);
					g.drawString("HUMANITY WILL LIVE",
							(1280 / 2) - (g.getFontMetrics().stringWidth("HUMANITY WILL LIVE") / 2), 200 - 20);
					g.drawString("TO SEE TOMORROW",
							(1280 / 2) - (g.getFontMetrics().stringWidth("TO SEE TOMORROW") / 2), 250 + 20);
					g.drawString("(YOU WIN!!! :D)",
							(1280 / 2) - (g.getFontMetrics().stringWidth("(YOU WIN!!! :D)") / 2), 460);
				} else if (Main.Human.tank.getHealth() == 0) {
					g.setColor(Color.RED);
					g.drawString("THE BOTS WILL RISE",
							(1280 / 2) - (g.getFontMetrics().stringWidth("THE BOTS WILL RISE") / 2), 200 - 20);
					g.drawString("OVER HUMANITY'S ASHES",
							(1280 / 2) - (g.getFontMetrics().stringWidth("OVER HUMANITY'S ASHES") / 2), 250 + 20);
					g.drawString("(YOU LOST!!!)", (1280 / 2) - (g.getFontMetrics().stringWidth("(YOU LOST!!!)") / 2),
							460);
				}

				g.setFont(font);
				g.setColor(Color.white);
			}
		}
	}
}
