
import java.awt.Image;

import javax.swing.ImageIcon;

public class Split_Shot extends Shot {
	public static Image bullet;
	public static Image bulletfly;
	public static Image icon;
	public static String name = "Splitter";
	static public String threadName = "Splitter";
	// num 2

	// setup splited shots
	public Splited_Shots t1;
	public Splited_Shots t2;

	public Split_Shot(int posx, int posy, int power, double angle, boolean isready, Splited_Shots temp1,
			Splited_Shots temp2, String name) {
		super(posx, posy, power, angle, isready, name);
		damage = 20;
		t1 = temp1;
		t2 = temp2;
		radius = 2;
	}

	public static void LoadContent(String dir) {
		Split_Shot.bullet = new ImageIcon(dir + "\\Bullets\\tank_bullet5.png").getImage();
		Split_Shot.bulletfly = new ImageIcon(dir + "\\Bullets\\tank_bullet5.png").getImage();
		Split_Shot.icon = new ImageIcon(dir + "\\icons\\Splitter_icon.png").getImage();
	}

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
				t1.hit = true;
				t2.hit = true;
				hit = true;
				return;
			}

			// check if hit
			if (hit) {
				t1.hit = true;
				t2.hit = true;
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
						t1.hit = true;
						t2.hit = true;
						hit = true;
						AnimatedSprites.animes.add(new AnimatedSprites(explosion, (int) posx, (int) posy));
						return;
					}

					// check if we can split the shot
					if (Map.get_y((int) posx) - posy < 150 && y_speed < 0) {
						// setup splited shot
						t1.posx = posx;
						t1.posy = posy;
						t1.x_speed = x_speed - ((x_speed / 2));
						t1.y_speed = y_speed;
						t2.posx = posx;
						t2.posy = posy;
						t2.x_speed = x_speed + ((x_speed / 2));
						t2.y_speed = y_speed;
						if ((int) x_speed == 0) {
							t1.x_speed -= (start_pow / 2);
							t2.x_speed += (start_pow / 2);
						}
						t1.is_ready = true;
						t1.start();
						t2.is_ready = true;
						t2.start();
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

	@Override
	public Image get_texture() {
		return bullet;
	}
}