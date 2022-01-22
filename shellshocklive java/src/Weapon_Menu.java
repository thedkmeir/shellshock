import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

public class Weapon_Menu {
	public List<Weapon_Menu_Ele> elelist = new ArrayList<Weapon_Menu_Ele>();

	public static Player player;
	public int num;

	public int max_height;

	public Weapon_Menu(Player ply) {
		player = ply;
		elelist.add(new Weapon_Menu_Ele(0, 0, Shot.icon, Shot.name, 1, true));
		make(5);
	}

	public void update() {
		for (Weapon_Menu_Ele we : elelist) {
			if (we.get_amount() != 0) {
				we.update();
			}
		}
	}

	public void M_Update(MouseEvent e) {
		for (Weapon_Menu_Ele we : elelist) {
			if (we.get_amount() != 0) {
				we.M_Update(e);
			}
		}
	}

	public void make(int count) {
		spawn(count);
		build();
	}

	Random rnd = new Random();

	public int Bullet_Rollete() {
		int roll = rnd.nextInt(100);
		if (roll % 10 == 1 && roll > 50) {
			return 6;
		}
		if (roll % 10 == 1 && roll < 50) {
			return 5;
		}
		if (roll < 33) {
			return 2;
		}
		if (roll < 66) {
			return 3;
		}
		if (roll <= 99) {
			return 4;
		}
		return -1;
	}

	public void spawn(int count) {
		for (int i = 0; i < count; i++) {
			boolean found = false;

			// roll the rollete!!!!!!!
			int wep = Bullet_Rollete();

			// search for existing place in list and adding
			for (Weapon_Menu_Ele we : elelist) {
				if (we.id == wep) {
					found = true;
					we.add_same();
				}
			}

			// if the itam is not in the list add it
			if (!found) {
				switch (wep) {
				case 2:
					elelist.add(new Weapon_Menu_Ele(0, 0, Split_Shot.icon, Split_Shot.name, wep));
					break;
				case 3:
					elelist.add(new Weapon_Menu_Ele(0, 0, Ghost_Bomb.icon, Ghost_Bomb.name, wep));
					break;
				case 4:
					elelist.add(new Weapon_Menu_Ele(0, 0, Ghost_Bomb.icon, "Bullet Storm", wep));
					break;
				case 5:
					elelist.add(new Weapon_Menu_Ele(0, 0, Ghost_Bomb.icon, "Ghost Attack", wep));
					break;
				case 6:
					elelist.add(new Weapon_Menu_Ele(0, 0, Bouncer.icon, "Bouncer", wep));
				default:
					break;
				}
			}
		}
	}

	// set the locations of all the items in the inventory
	public void build() {
		int spacey = 10;
		int spacex = 20;
		int rows = 1 + (elelist.size() / 6);
		int rowloc = 720 - 200 - (rows * (spacey + 50) - 50);
		int letter_sapce = 5;
		max_height = rowloc;
		int count = 0;
		for (int i = 0; i < rows; i++) {
			int colloc = spacex;
			for (int j = 0; j < 5; j++) {
				if (count >= elelist.size()) {
					break;
				}
				if (elelist.get(count).get_amount() != 0) {
					elelist.get(count).rect.x = colloc;
					elelist.get(count).rect.y = rowloc;
					colloc += elelist.get(count).icon.getWidth(null)
							+ ((elelist.get(count).name.length() + letter_sapce) * 11) + spacex + 20;
					letter_sapce = 4;
				}
				count++;
			}
			rowloc += 50 + spacey;
		}
	}

	public void Draw(Graphics g) {
		for (Weapon_Menu_Ele we : elelist) {
			we.Draw(g);
		}
	}

	public static void OnClick(int id) {
		// give buttons actions
		player.wep_selected(id);
	}
}

class Weapon_Menu_Ele {
	private enum state {
		reg, mon
	}

	private state stat = state.reg;

	// public static Image back_mon;
	public static Font font;

	public Rectangle rect;
	public Image icon;
	public String name;
	public int id;
	private int amount = 0;
	public boolean infi = false;

	public boolean click_switch = false;

	public Weapon_Menu_Ele(int x, int y, Image tex, String name, int id) {
		this.name = name;
		this.id = id;
		amount++;

		switch (name) {
		case "Splitter":
			icon = new ImageIcon(Main.dir + "\\icons\\Splitter_icon.png").getImage();
			break;

		case "Ghost Bomb":
			icon = new ImageIcon(Main.dir + "\\icons\\GhostBomb_icon.png").getImage();
			break;

		case "Bullet Storm":
			icon = new ImageIcon(Main.dir + "\\icons\\Bullet_Storm_icon.png").getImage();
			break;

		case "Ghost Attack":
			icon = new ImageIcon(Main.dir + "\\icons\\Ghost_Attack_icon.png").getImage();
			break;

		case "Bouncer":
			icon = new ImageIcon(Main.dir + "\\icons\\Bouncer.png").getImage();
			break;
		}

		rect = new Rectangle(x, y, icon.getWidth(null) + ((name.length() + 4) * 11) + 20, 50);
	}

	public Weapon_Menu_Ele(int x, int y, Image tex, String name, int id, boolean inf) {
		icon = new ImageIcon(Main.dir + "\\icons\\Shot_icon.png").getImage();
		this.name = name;
		this.id = id;
		amount++;
		infi = inf;
		rect = new Rectangle(x, y, icon.getWidth(null) + ((name.length() + 5) * 11) + 20, 50);
	}

	public void add_same() {
		amount++;
	}

	public void rem_same() {
		if (!infi) {
			amount--;
		}
	}

	public int get_amount() {
		return amount;
	}

	public void update() {
		if (rect.contains(new Point(MouseInfo.getPointerInfo().getLocation().x - Main.obj.getLocation().x,
				MouseInfo.getPointerInfo().getLocation().y - Main.obj.getLocation().y))
				&& Main.Human.player_bar.wep_menu == Player_Bar.Wep_Menu.open) {
			stat = state.mon;
		} else {
			stat = state.reg;
		}

	}

	public void M_Update(MouseEvent e) {
		if (rect.contains(new Point(MouseInfo.getPointerInfo().getLocation().x - Main.obj.getLocation().x,
				MouseInfo.getPointerInfo().getLocation().y - Main.obj.getLocation().y))
				&& Main.Human.player_bar.wep_menu == Player_Bar.Wep_Menu.open) {
			// if (e.getButton() == MouseEvent.MOUSE_RELEASED && click_switch) {
			// click_switch = false;
			Weapon_Menu.OnClick(id);
			// }
			// if (e.getButton() == MouseEvent.MOUSE_PRESSED) {
			// click_switch = true;
			// }
		} else {
			stat = state.reg;
		}
	}

	public void Draw(Graphics g) {
		if (amount > 0) {
			g.setFont(font);
			switch (stat) {
			case mon:
				// g.drawImage(back_mon, rect.x, rect.y - Main.obj.getInsets().top, rect.width,
				// rect.height,
				// new Color(255, 255, 255, 150), null);
				g.setColor(Color.white);
				break;
			default:
				g.setColor(Color.black);
				break;
			}
			// JOptionPane.showMessageDialog(null, name);
			g.drawImage(icon, rect.x + 5,
					(int) (rect.y + (rect.getHeight() / 2) - (icon.getHeight(null) / 2) - Main.obj.getInsets().top),
					null);
			if (infi) {
				amount = 99;
			}
			// g.drawString(name + " (" + amount + ")", rect.x + 15 + icon.getWidth(null),
			// (int) (rect.y + (rect.getHeight() / 2) - 6));
			g.drawString(name + " (" + amount + ")", rect.x + 15 + icon.getWidth(null),
					(int) (rect.y + (rect.getHeight() / 2) + 6 - Main.obj.getInsets().top));
			g.setColor(Color.black);
		}
	}
}