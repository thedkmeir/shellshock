
import java.awt.Image;

public class Splited_Shots extends Shot {

	static public String threadName = "Splited_Shots";

	public Splited_Shots(int posx, int posy, int power, double angle, boolean isready, String name) {
		super(posx, posy, power, angle, isready, name);
		damage = 10;
		is_ready = isready;
		radius = 2;
	}

	@Override
	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	// public void update() {
	@Override
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

	@Override
	public Image get_texture() {
		return bullet;
	}

	@Override
	public Image[] get_exploaion() {
		return explosion;
	}
}
