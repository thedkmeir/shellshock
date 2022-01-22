import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class Shot implements Runnable {
	public static List<Shot> shots = new ArrayList<Shot>();

	public static Image bullet;
	public static Image bulletfly;
	public static Image[] explosion = new Image[4];
	public static Image icon;
	public static String name = "Shot";
	// num 1

	public double posx;
	public double posy;
	public double prev_posx;
	public double prev_posy;
	public double y_speed;
	public double x_speed;
	static public double gravity = 4;
	static public int power_shot_multi = 5;
	static public double dt = 0.02;
	public boolean hit = false;
	public int damage = 10;
	public int radius = 2;
	public boolean is_ready = true;
	public int start_pow = 0;
	public float angle = 0;
	public String pl_name;
	public String Last_Hit = "";

	//
	public Thread t;
	static public String threadName = "Shot";
	public String stop_thread = "";
	//

	public double get_posx() {
		return posx;
	}

	public void set_posx(int g) {
		posx = g;
	}

	public double get_posy() {
		return posy;
	}

	public void set_posy(int g) {
		posy = g;
	}

	public Shot(int posx, int posy, int power, double angle, boolean isready, String pl_name) {
		this.posx = posx + (bullet.getWidth(null) / 2);
		this.posy = posy - (bullet.getHeight(null) / 2);
		y_speed = (power * power_shot_multi) * Math.sin(angle);
		x_speed = (power * power_shot_multi) * Math.cos(angle);
		if ((int) (angle * 180 / Math.PI) == 180) {
			x_speed = -(power * Shot.power_shot_multi);
		}
		if ((int) (angle * 180 / Math.PI) == 0) {
			x_speed = (power * Shot.power_shot_multi);
		}
		is_ready = isready;
		start_pow = power;
		this.pl_name = pl_name;
		this.Last_Hit = pl_name;
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public static void LoadContent(String dir) {
		Shot.bullet = new ImageIcon(dir + "\\Bullets\\tank_bullet1.png").getImage();
		Shot.bulletfly = new ImageIcon(dir + "\\Bullets\\tank_bulletFly1.png").getImage();
		Shot.icon = new ImageIcon(dir + "\\icons\\Shot_icon.png").getImage();
		explosion[0] = new ImageIcon(dir + "\\Explosions\\tank_explosion1.png").getImage();
		explosion[1] = new ImageIcon(dir + "\\Explosions\\tank_explosion2.png").getImage();
		explosion[2] = new ImageIcon(dir + "\\Explosions\\tank_explosion3.png").getImage();
		explosion[3] = new ImageIcon(dir + "\\Explosions\\tank_explosion4.png").getImage();
	}

	public Image get_texture() {
		return bullet;
	}

	public Image[] get_exploaion() {
		return explosion;
	}

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
				hit = true;
				AnimatedSprites.animes.add(new AnimatedSprites(explosion, (int) posx, (int) posy));
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

					// if bullet under the map
					if (Map.get_y((int) posx) <= posy) {
						hit = true;
						AnimatedSprites.animes.add(new AnimatedSprites(explosion, (int) posx, (int) posy));
						return;
					}

					// fix bullet angle
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
}
