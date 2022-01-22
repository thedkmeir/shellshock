import java.awt.Image;

import javax.swing.ImageIcon;

public class Bouncer extends Shot {

	// data about this shot.....
	public static Image bullet;
	public static Image bulletfly;
	public static Image icon;
	public static String name = "Bouncer";
	public static Image[] explosion = new Image[6];

	static public String threadName = "Bouncer";
	int bulletpower = 100;

	public Bouncer(int posx, int posy, int power, double angle, boolean isready, String name) {
		super(posx, posy, power, angle, isready, name);
		damage = 20;
		radius = 20;
	}

	// loading all images
	public static void LoadContent(String dir) {
		Bouncer.bullet = new ImageIcon(dir + "\\Bullets\\bouncer.png").getImage();
		Bouncer.icon = new ImageIcon(dir + "\\icons\\bouncer.png").getImage();

		explosion[0] = new ImageIcon(dir + "\\Explosions\\Bouncer_exp_1.png").getImage();
		explosion[1] = new ImageIcon(dir + "\\Explosions\\Bouncer_exp_2.png").getImage();
		explosion[2] = new ImageIcon(dir + "\\Explosions\\Bouncer_exp_3.png").getImage();
		explosion[3] = new ImageIcon(dir + "\\Explosions\\Bouncer_exp_4.png").getImage();
		explosion[4] = new ImageIcon(dir + "\\Explosions\\Bouncer_exp_5.png").getImage();
		explosion[5] = new ImageIcon(dir + "\\Explosions\\Bouncer_exp_6.png").getImage();
	}

	// start the update thread
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
					// if (posy < 0) {
					// posy = -posy;
					// }

					// if bullet under the map
					if (Map.get_y((int) posx + radius) <= posy + radius) {
						if (bulletpower > 39) {
							double hita = Map.get_angle((int) prev_posx, (int) posx, (int) prev_posy, (int) posy);
							double a = (Map.get_angle((int) posx + radius, (int) posx + radius + 1,
									Map.get_y((int) posx + radius), Map.get_y((int) posx + radius + 1) * 2) - hita);
							bulletpower -= 10;
							y_speed = (bulletpower * power_shot_multi) * Math.sin(a);
							x_speed = (bulletpower * power_shot_multi) * Math.cos(a);
							posx += x_speed * dt;
							posy -= y_speed * dt;
							y_speed -= gravity;
						} else {
							hit = true;
							AnimatedSprites.animes
									.add(new AnimatedSprites(explosion, (int) posx + radius, (int) posy + radius));
							return;
						}
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
