import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

public class Player_Bar {
	public enum Wep_Menu {
		open, close
	}

	public Wep_Menu wep_menu = Wep_Menu.close;

	// all sprites and fonts
	public static Image background;
	public static Image bar;
	public static Image health_sprite;
	public static Image fuel_sprite;
	public static Image icon;
	public static Font font;

	public static int back_height = 200;

	// weapone button that activates weapone menu
	// fire button that triggers fire
	public static GUIElement Weapones = new GUIElement("Player_Bar/Weapons", "p");
	public static GUIElement Fire = new GUIElement("Player_Bar/Fire", "p");

	public Weapon_Menu weapon_menu;

	private int health;
	private int fuel;
	private double angle;
	private int power;

	public Player_Bar(int health, int fuel, double angle, int power, Player pl) {
		this.health = health;
		this.fuel = fuel;
		this.angle = angle;
		this.power = power;
		weapon_menu = new Weapon_Menu(pl);
	}

	public static void LoadContent(String dir) {
		// load all textures and fonts
		Weapones.LoadContent(dir);
		Weapones.MoveElement((int) (1280 * (0.6)), 720 - (back_height / 2) - (Weapones.get_height() / 2));

		Fire.LoadContent(dir);
		Fire.MoveElement((int) (1280 * (0.8)), 720 - (back_height / 2) - (Weapones.get_height() / 2));

		background = new ImageIcon(dir + "\\Player_Bar\\Background.jpg").getImage();
		bar = new ImageIcon(dir + "\\Player_Bar\\bar.png").getImage();
		health_sprite = new ImageIcon(dir + "\\Player_Bar\\life_bar_full.png").getImage();
		fuel_sprite = new ImageIcon(dir + "\\Player_Bar\\fuel_bar_full.png").getImage();
		font = new Font("Monospaced", Font.BOLD, 28);
	}

	public void Draw(Graphics g) {
		Fire.Draw(g);
		Weapones.Draw(g);

		g.drawImage(bar, 100, (int) (720 - (back_height / 3) - (bar.getHeight(null) / 2) - Main.obj.getInsets().top),
				null);
		g.drawImage(health_sprite, 100, 720 - (back_height / 3) - (bar.getHeight(null) / 2) - Main.obj.getInsets().top,
				(int) (health_sprite.getWidth(null) * (((double) health / 2) / 100)), health_sprite.getHeight(null),
				null);
		g.drawImage(bar, 100, 720 - ((back_height / 3) * 2) - (bar.getHeight(null) / 2) - Main.obj.getInsets().top,
				null);
		g.drawImage(fuel_sprite, 100,
				720 - ((back_height / 3) * 2) - (bar.getHeight(null) / 2) - Main.obj.getInsets().top,
				(int) (fuel_sprite.getWidth(null) * ((double) fuel / 70)), fuel_sprite.getHeight(null), null);

		g.setFont(font);
		g.setColor(Color.white);
		int half = 6;
		g.drawString("HP:", 10, (int) (720 - (back_height / 3) + half - Main.obj.getInsets().top));
		g.drawString(String.valueOf(health), (int) (100 + (bar.getWidth(null) / 2) + half - 24),
				(int) (720 - (back_height / 3) + half - Main.obj.getInsets().top));
		g.drawString("Fuel:", 10, (int) (720 - (back_height / 3 * 2) + half - Main.obj.getInsets().top));
		g.drawString(String.valueOf(fuel), (int) ((100 + (bar.getWidth(null) / 2) + half) - 24),
				(int) (720 - (back_height / 3 * 2) + half - Main.obj.getInsets().top));
		g.drawString("Angle: " + (int) (angle * 180 / Math.PI), 100, 720 - 40);
		g.drawString("Power: " + power, 100 + (bar.getWidth(null) / 2), 720 - 40);

		int length = 0;
		for (Weapon_Menu_Ele ele : weapon_menu.elelist) {
			if (ele.id == Main.Human.wep_select) {
				length = ele.icon.getWidth(null);
				icon = ele.icon;
			}
		}

		g.drawString("Selected: ", 1280 - g.getFontMetrics().stringWidth("Selected: ") - length,
				Main.obj.getInsets().top - 5);
		g.drawImage(icon, 1280 - length - 13, 0, null);

		if (wep_menu == Wep_Menu.open) {
			weapon_menu.Draw(g);
		}
	}

	public void Update(int health, int fuel, double angle, int power) {
		this.health = health;
		this.fuel = fuel;
		this.angle = angle;
		this.power = power;
		Weapones.update();
		Fire.update();
		if (wep_menu == Wep_Menu.open) {
			weapon_menu.update();
		}
	}

	public void M_Update(MouseEvent e) {
		Weapones.M_Update(e);
		Fire.M_Update(e);

		if (wep_menu == Wep_Menu.open) {
			weapon_menu.M_Update(e);
		}
	}

	public static void OnClick(String assetname) {
		if (Main.playstate == Main.PlayState.turn1) {
			if (assetname == "Player_Bar/Fire") {
				Main.Human.go = true;
			}
			if (assetname == "Player_Bar/Weapons") {
				if (Main.Human.player_bar.wep_menu == Wep_Menu.close) {
					Main.Human.player_bar.wep_menu = Wep_Menu.open;
				} else {
					Main.Human.player_bar.wep_menu = Wep_Menu.close;
				}
			}
		}
	}
}
