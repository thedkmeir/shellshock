import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Player {
	// public static List<Player> players = new ArrayList<Player>();
	public Player_Bar player_bar;
	public Tanks tank;
	public int team;

	// player num might be useless
	public int playernum;
	public boolean ready = false;
	public int wep_select = 1;

	// is_bot is probably useless
	public boolean is_bot = false;
	public String name = "You";

	public boolean go = false;

	public Player(int x, int team) {
		tank = new Tanks(x, team, name);
		Tanks.tanks.add(tank);
		this.team = team;
		playernum = 1;
		player_bar = new Player_Bar(tank.getHealth(), tank.getFuel(), tank.getAngle(), (int) tank.getPower(), this);
	}

	public void M_Update(MouseEvent e) {
		player_bar.M_Update(e);
	}

	public void update() {
		player_bar.Update(tank.getHealth(), tank.getFuel(), tank.getAngle(), (int) tank.getPower());

		if (go) {
			go = false;

			if (tank.getIsshot()) {
				switch (wep_select) {
				// regular shot
				case 1:
					Shot t = new Shot(tank.getPosx(), tank.getPosy(), (int) tank.getPower(), tank.getShotangle(), true,
							this.name);
					Shot.shots.add(t);
					t.start();
					break;

				// split shot
				case 2:
					Splited_Shots t1 = new Splited_Shots(tank.getPosx(), tank.getPosy(), (int) tank.getPower(),
							tank.getShotangle(), false, name);
					Splited_Shots t2 = new Splited_Shots(tank.getPosx(), tank.getPosy(), (int) tank.getPower(),
							tank.getShotangle(), false, name);
					Splited_Shots.shots.add(t1);
					Splited_Shots.shots.add(t2);
					Split_Shot t0 = new Split_Shot(tank.getPosx(), tank.getPosy(), (int) tank.getPower(),
							tank.getShotangle(), true, t1, t2, name);
					Split_Shot.shots.add(t0);
					t0.start();
					break;

				// ghost bomb
				case 3:
					Ghost_Bomb g1 = new Ghost_Bomb(tank.getPosx(), tank.getPosy(), (int) tank.getPower(),
							tank.getShotangle(), true, name);
					Shot.shots.add(g1);
					g1.start();
					break;

				// bullet storm
				case 4:
					double dec = 0.01745329252;
					double angle = tank.getShotangle() - (2 * dec);
					for (int i = 0; i < 5; i++) {
						Shot s = new Shot(tank.getPosx(), tank.getPosy(), (int) tank.getPower(), angle, true,
								this.name);
						Shot.shots.add(s);
						s.start();
						angle += dec;
					}
					break;

				// ghost attack
				case 5:
					for (int i = 0; i < 3; i++) {
						double rand = (Main.rnd.nextInt(11) - 5) * 0.01745329252;
						Ghost_Bomb gx = new Ghost_Bomb(tank.getPosx(), tank.getPosy(), (int) tank.getPower(),
								tank.getShotangle() + rand, true, name);
						Shot.shots.add(gx);
						gx.start();
					}
					break;

				// bouncer
				case 6:
					Bouncer b = new Bouncer(tank.getPosx() - 25, tank.getPosy() - 15, (int) tank.getPower(),
							tank.getShotangle(), true, name);
					Shot.shots.add(b);
					b.start();
					break;

				default:
					Shot temp = new Shot(tank.getPosx(), tank.getPosy(), (int) tank.getPower(), tank.getShotangle(),
							true, name);
					Shot.shots.add(temp);
					temp.start();
					break;
				}

				for (Weapon_Menu_Ele l : player_bar.weapon_menu.elelist) {
					if (l.id == wep_select) {
						l.rem_same();
						if (l.get_amount() == 0) {
							wep_select = 1;
						}
						player_bar.weapon_menu.build();
					}
				}

				tank.setIsshot(false);
				ready = true;
			}
		}
	}

	public void K_Update(KeyEvent k) {
		if (tank.getHealth() > 0) {
			// long dt = Main.Elapsedgametime() / 10000;
			// movement
			if (!ready) {
				// cheats to bet a specific shot
				if (k.getKeyCode() == KeyEvent.VK_1) {
					wep_select = 1;
				}
				if (k.getKeyCode() == KeyEvent.VK_2) {
					wep_select = 2;
				}
				if (k.getKeyCode() == KeyEvent.VK_3) {
					wep_select = 3;
				}
				if (k.getKeyCode() == KeyEvent.VK_4) {
					wep_select = 4;
				}

				if (k.getKeyCode() == KeyEvent.VK_5) {
					wep_select = 5;
				}

				if (k.getKeyCode() == KeyEvent.VK_6) {
					wep_select = 6;
				}

				// movement a & d
				if (k.getKeyCode() == KeyEvent.VK_D && tank.getFuel() != 0) {
					// tank.setPosx((int)(tank.getPosx() + tank.getMovespeed() * dt));
					tank.setPosx(tank.getPosx() + 3);
					if (tank.getPosx() > 1258) {
						tank.setPosx(1258);
					} else {
						tank.setFuel(tank.getFuel() - 1);
					}
					tank.setPosy(Map.get_y((int) tank.getPosx()) - 17);

					tank.setTankangle((Map.get_angle(tank.getPosx() - 10, tank.getPosx() + 10,
							Map.get_y((int) tank.getPosx() - 10), Map.get_y((int) tank.getPosx() + 10))));
				}
				if (k.getKeyCode() == KeyEvent.VK_A && tank.getFuel() != 0) {
					// tank.setPosx((int)(tank.getPosx() - tank.getMovespeed() * dt));
					tank.setPosx(tank.getPosx() - 3);
					if (tank.getPosx() < 22) {
						tank.setPosx(22);
					} else {
						tank.setFuel(tank.getFuel() - 1);
					}
					tank.setPosy(Map.get_y((int) tank.getPosx()) - 17);

					tank.setTankangle((Map.get_angle(tank.getPosx() - 10, tank.getPosx() + 10,
							Map.get_y((int) tank.getPosx() - 10), Map.get_y((int) tank.getPosx() + 10))));
				}

				// power of shot up & down
				if (k.getKeyCode() == KeyEvent.VK_UP) {
					if (tank.getPower() < 100) {
						// tank.setPower(tank.getPower() + tank.getAimspeed() * dt);
						tank.setPower(tank.getPower() + 1);
					}
				}

				if (k.getKeyCode() == KeyEvent.VK_DOWN) {
					if (tank.getPower() > 1) {
						// tank.setPower(tank.getPower() - tank.getAimspeed() * dt);
						tank.setPower(tank.getPower() - 1);
					}
				}

				// angle of shot //works int 360o right & left
				if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
					// tank.setShotangle((tank.getShotangle() - tank.getAimspeed() * dt) * Math.PI /
					// 180);
					tank.setShotangle(tank.getShotangle() - 0.01745329252);
				}

				if (k.getKeyCode() == KeyEvent.VK_LEFT) {
					// tank.setShotangle((tank.getAimspeed() + tank.getAimspeed() * dt) * Math.PI /
					// 180);
					tank.setShotangle(tank.getShotangle() + 0.01745329252);
				}

				// shot //space
				if (k.getKeyCode() == KeyEvent.VK_SPACE) {
					go = true;
				}
			}

		}
	}

	public void set_isshotk(KeyEvent k) {
		if (k.getKeyCode() == KeyEvent.VK_SPACE) {
			tank.setIsshot(true);
		}
	}

	public void set_isshotm(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			tank.setIsshot(true);
		}
	}

	public void wep_selected(int id) {
		this.wep_select = id;
		player_bar.wep_menu = Player_Bar.Wep_Menu.close;
	}
}
