import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;

public class Crates {

	// enum that says if the box is in the sky or ground
	public enum Crateloc {
		sky, floor
	}

	public Crateloc crateloc;

	// enum that says if the box is health or ammo
	public enum Cratetype {
		health, ammo
	}

	public Cratetype cratetype;

	// all crates
	public static java.util.List<Crates> crates = new ArrayList<Crates>();
	public static Image health_crate_sprite;
	public static Image ammo_crate_sprite;
	public static Image parachute;

	Random rnd = new Random();

	// animation....

	// sky
	// timer vars for sky box to slightly move
	public int animtimer = 0;
	public int stopper = 200;
	public int updown = 0;
	public int addr = 0;

	// floor
	public int locx = -1;
	public int locy = -1;
	public int radius = (health_crate_sprite.getWidth(null) / 2);
	public Double angle;

	public Image texture;
	public boolean hit = false;

	public Crates() {
		// randomize health or ammo
		int hora = rnd.nextInt(100 - 1 + 1) + 1;

		// randomize floor or sky
		int fors = rnd.nextInt(100 - 1 + 1) + 1;

		// assign the right locations and sprites to the randomized box
		if (fors % 2 == 0) {
			crateloc = Crateloc.sky;
			if (hora % 2 == 0) {
				cratetype = Cratetype.health;
				texture = health_crate_sprite;
			} else {
				cratetype = Cratetype.ammo;
				texture = ammo_crate_sprite;
			}

			locx = rnd.nextInt((1155 - 100) + 1) + 100;
			while (locy >= 70) {
				locy = rnd.nextInt(((Map.get_y(locx) - 150) + 1) + 75);
				if (Map.get_y(locx) < 75) {
					locy = 100;
				}
			}
		} else {
			crateloc = Crateloc.floor;
			if (hora % 2 == 0) {
				cratetype = Cratetype.health;
				texture = health_crate_sprite;
			} else {
				cratetype = Cratetype.ammo;
				texture = ammo_crate_sprite;
			}
			locx = rnd.nextInt((1234 - 1) + 1) + 1;
			locy = Map.get_y((int) locx) - radius;
			angle = (double) Map.get_angle(locx - radius, locx + radius, Map.get_y((int) locx - radius),
					Map.get_y((int) locx + radius));
		}
	}

	// load all sprites
	public static void LoadContent(String dir) {
		health_crate_sprite = new ImageIcon(dir + "\\Objects\\Health_Crate.png").getImage();
		ammo_crate_sprite = new ImageIcon(dir + "\\Objects\\Ammo_Crate.png").getImage();
		parachute = new ImageIcon(dir + "\\Objects\\Parachute.png").getImage();
	}

	// check if any shot or tank are near the box and give them the reward
	public void update() {
		if (crateloc == Crateloc.sky) {
			for (Shot sh : Shot.shots) {
				if (Math.abs(locx - sh.posx) <= radius && Math.abs(locy - sh.posy) <= radius) {
					hit = true;
					if (cratetype == Cratetype.health) {

						if (sh.pl_name == "Bot") {
							int morehp = rnd.nextInt((30) + 1) + 10;
							Main.Bot.tank.setHealth(Main.Bot.tank.getHealth() + morehp);
							if (Main.Bot.tank.getHealth() > 200) {
								Main.Bot.tank.setHealth(200);
							}
						} else {
							int morehp = rnd.nextInt((60) + 1) + 10;
							Main.Human.tank.setHealth(Main.Human.tank.getHealth() + morehp);
							if (Main.Human.tank.getHealth() > 200) {
								Main.Human.tank.setHealth(200);
							}
							if (morehp <= 25) {
								Main.Human.player_bar.weapon_menu.make(1);
							}
						}
					}
					if (cratetype == Cratetype.ammo && sh.pl_name == "You") {
						Main.Human.player_bar.weapon_menu.make(3);
					}
				}
			}
		} else {
			for (Tanks tn : Tanks.tanks) {
				int sum = tn.getRadius() + radius;
				if (Math.abs(locx - tn.getPosx()) <= sum && Math.abs(locy - tn.getPosy()) <= sum && hit == false) {
					hit = true;
					if (cratetype == Cratetype.health) {
						int morehp = rnd.nextInt((70 - 10) + 1) + 10;
						if (tn.getPl_name() == "Bot") {
							Main.Bot.tank.setHealth(Main.Human.tank.getHealth() + morehp);
							if (Main.Bot.tank.getHealth() > 200) {
								Main.Bot.tank.setHealth(200);
							}
						} else {
							Main.Human.tank.setHealth(Main.Human.tank.getHealth() + morehp);
							if (Main.Human.tank.getHealth() > 200) {
								Main.Human.tank.setHealth(200);
							}
							if (morehp <= 25) {
								Main.Human.player_bar.weapon_menu.make(1);
							}
						}
					}
					if (cratetype == Cratetype.ammo && tn.getPl_name() == "You") {
						Main.Human.player_bar.weapon_menu.make(3);
					}
				}
			}
		}
	}

	public void Draw(Graphics g) {
		if (crateloc == Crateloc.floor) {
			// Vector2 origin = new Vector2(radius, radius);
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform backup = g2d.getTransform();
			AffineTransform tx = AffineTransform.getRotateInstance(angle, locx, locy - Main.obj.getInsets().top);
			g2d.setTransform(tx);
			g.drawImage(texture, locx - radius, locy - 2 * radius - Main.obj.getInsets().top, null);
			g2d.setTransform(backup);
		} else {
			if (updown == 0) {
				animtimer++;
				if (animtimer % stopper == 0) {
					addr--;
					if (animtimer == stopper * 5) {
						animtimer = 0;
						updown = 1;
					}
				}
			} else {
				animtimer++;
				if (animtimer % stopper == 0) {
					addr++;
					if (animtimer == stopper * 5) {
						animtimer = 0;
						updown = 0;
					}
				}
			}
			g.drawImage(texture, locx - radius, locy - radius - Main.obj.getInsets().top + addr, null);
			g.drawImage(parachute, locx - 17, locy - radius - 35 - Main.obj.getInsets().top + addr, null);
		}
	}

}
