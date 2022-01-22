import java.awt.Image;

import javax.swing.ImageIcon;

public class Ghost_Bomb extends Shot {

	// all shots sprites
	public static Image bullet;
	public static Image bulletfly;
	public static Image icon;
	public static String name = "Ghost Bomb";
	public static Image[] explosion = new Image[6];

	static public String threadName = "Ghost_Bomb";

	public Ghost_Bomb(int posx, int posy, int power, double angle, boolean isready, String name) {
		super(posx, posy, power, angle, isready, name);
		damage = 0;
		radius = 0;
	}

	// load all shots
	public static void LoadContent(String dir) {
		Ghost_Bomb.bullet = new ImageIcon(dir + "\\Bullets\\ghost_bomb.png").getImage();
		Ghost_Bomb.bulletfly = Ghost_Bomb.bullet;
		Ghost_Bomb.icon = new ImageIcon(dir + "\\icons\\GhostBomb_icon.png").getImage();

		// explosion
		explosion[0] = new ImageIcon(dir + "\\Bullet_Explosion\\ghost_bomb_0.png").getImage();
		explosion[1] = new ImageIcon(dir + "\\Bullet_Explosion\\ghost_bomb_1.png").getImage();
		explosion[2] = new ImageIcon(dir + "\\Bullet_Explosion\\ghost_bomb_1s.png").getImage();
		explosion[3] = new ImageIcon(dir + "\\Bullet_Explosion\\ghost_bomb_2.png").getImage();
		explosion[4] = new ImageIcon(dir + "\\Bullet_Explosion\\ghost_bomb_2s.png").getImage();
		explosion[5] = new ImageIcon(dir + "\\Bullet_Explosion\\ghost_bomb_3.png").getImage();
	}

	// start thread
	@Override
	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	@Override
	// public void update() {
	public void run() {
		// getting system time ant timers
		double delta = 0;
		double frametime = 1.0 / 60.0;
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();

		// goes forever
		while (true) {
			// check if thread is terminated
			if (stop_thread == "t") {
				hit = true;
				return;
			}

			// check if hit
			if (hit) {
				for (Tanks tn : Tanks.tanks) {
					int sum = 75 + tn.getRadius();
					if (Math.abs(posx - tn.getPosx()) <= sum && Math.abs(posy - tn.getPosy()) <= sum
							&& tn.getPl_name() != pl_name) {
						if (tn.getHealth() > 0) {
							tn.setHealth(tn.getHealth() - 30);
						} else {
							tn.setHealth(0);
						}
					}
				}
				AnimatedSprites.animes.add(new AnimatedSprites(explosion, (int) posx, (int) posy, 0.05));
				hit = true;
				return;
			}

			// timer stuff
			long now = System.nanoTime();
			long passedTime = now - lastTime;
			lastTime = now;
			delta += passedTime / 1000000000.0;

			if (delta > frametime) {
				// if the thread is not paused
				if (stop_thread != "p") {

					// save prev positions
					prev_posx = posx;
					prev_posy = posy;

					// get next position
					posx += x_speed * dt;
					posy -= y_speed * dt;
					y_speed -= gravity;

					// run checks on next positions

					// if bullet hit bounds
					if (posx > 1280) {
						posx = 1280;
						x_speed = -x_speed;
					}
					if (posx < 0) {
						posx = 0;
						x_speed = -x_speed;
					}

					// if bullet under the map search for close tank give damage
					if (Map.get_y((int) posx) <= posy) {
						for (Tanks tn : Tanks.tanks) {
							int sum = 75 + tn.getRadius();
							if (Math.abs(posx - tn.getPosx()) <= sum && Math.abs(posy - tn.getPosy()) <= sum
									&& tn.getPl_name() != pl_name) {
								if (tn.getHealth() > 0) {
									tn.setHealth(tn.getHealth() - 30);
								} else {
									tn.setHealth(0);
								}
							}
						}
						AnimatedSprites.animes.add(new AnimatedSprites(explosion, (int) posx, (int) posy, 0.05));
						hit = true;
						return;
					}

					// fix angle
					if (posx > prev_posx) {
						angle = Map.get_angle((int) prev_posx, (int) posx, (int) prev_posy, (int) posy);
					} else {
						angle = Map.get_angle((int) posx, (int) prev_posx, (int) posy, (int) prev_posy)
								+ (float) Math.PI;
					}
				}
				delta -= frametime;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
			}
		}
	}

	@Override
	public Image get_texture() {
		return bullet;
	}

	@Override
	public Image[] get_exploaion() {
		return explosion;
	}
}
