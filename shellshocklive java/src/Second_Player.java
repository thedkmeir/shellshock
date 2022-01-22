import java.util.List;
import java.util.Random;

public class Second_Player {

	Random rnd = new Random();

	int playernum = 2;
	int prev_health = 200;
	public Tanks tank;
	public boolean ready = false;
	public int wep_select = 1;
	public String name = "Bot";

	public boolean go = false;

	// action vars
	int mov_target = -1;
	String lorr = "";
	double target_radian = -1;
	double target_power = -1;

	// is ready to shoot?
	boolean power_ready = false;
	boolean radian_ready = false;
	boolean move_ready = false;

	public Second_Player(int x) {
		tank = new Tanks(x, 2, name);
		Tanks.tanks.add(tank);

		// try to find location to move to
		// Move();
	}

	// activates when its the bots turn
	public void Start_routine() {
		// check if we need to move...
		if ((prev_health > tank.getHealth() && mov_target == -1) || Main.turn_count <= 2 || Main.turn_count % 5 == 0
				|| Main.turn_count % 5 == 1) {
			Move();
		}
		// update prevhealth to know if we got hit....
		prev_health = tank.getHealth();

		// after movement is done
		// auto aim for enemy tank or health box or both
		if (mov_target == -1) {
			Auto_aim(Main.Human.tank.getPosx(), Main.Human.tank.getPosy(), tank.getPosx(), tank.getPosy(),
					Crates.crates);
		} else {
			if (Math.abs(tank.getPosx() - mov_target) >= tank.getFuel() * 3) {
				if (lorr == "r") {
					Auto_aim(Main.Human.tank.getPosx(), Main.Human.tank.getPosy(), tank.getPosx() + tank.getFuel() * 3,
							Map.get_y(tank.getPosx() + tank.getFuel() * 3) - 17, Crates.crates);
				}
				if (lorr == "l") {
					Auto_aim(Main.Human.tank.getPosx(), Main.Human.tank.getPosy(), tank.getPosx() - tank.getFuel() * 3,
							Map.get_y(tank.getPosx() - tank.getFuel() * 3) - 17, Crates.crates);
				}
			} else {
				Auto_aim(Main.Human.tank.getPosx(), Main.Human.tank.getPosy(), mov_target, Map.get_y(mov_target) - 17,
						Crates.crates);
			}
		}
	}

	// i might add code to take the most accurate shot
	public void Auto_aim(int enemy_locx, int enemy_locy, int my_locx, int my_locy, List<Crates> crates) {
		int o = tank.getPosx();

		o++;
		o--;

		// enemy is in the right
		double high = 1.5807964999999966, low = 3.1415935000000015, degree = 0.01745329252;
		if (enemy_locx > my_locx) {
			// enemy is in the left
			high = 0;
			low = 1.5807964999999966;
		}

		if (tank.getHealth() < 100) {
			high = 0;
			low = 3.1415935000000015;
		}

		double y_speed, x_speed, posx, posy, deg = -1;
		int power = -1;
		boolean done = false, hcratehit = false, acratehit = false, tankhit = false;

		for (double j = high; j <= low; j += degree) {
			for (int i = 100; i > 1; i--) {
				y_speed = (i * Shot.power_shot_multi) * Math.sin(j);
				x_speed = (i * Shot.power_shot_multi) * Math.cos(j);
				if ((int) (j * 180 / Math.PI) == 180) {
					x_speed = -(power * Shot.power_shot_multi);
				}
				if ((int) (j * 180 / Math.PI) == 0) {
					x_speed = (power * Shot.power_shot_multi);
				}
				posx = my_locx;
				posy = my_locy;
				// going through the bullet course
				while (!done) {
					posx += x_speed * Shot.dt;
					posy -= y_speed * Shot.dt;
					y_speed -= Shot.gravity;

					// check if bullet out of bounds and redirect them
					if (posx > 1280 || posx < 0) {
						if (posx > 1280) {
							posx = 1280;
						}
						if (posx < 0) {
							posx = 0;
						}
						x_speed = -x_speed;
					}

					// check if bullet is touching the map
					if (Map.get_y((int) posx) <= posy) {
						done = true;
					}

					// check if bullet hit crates on his way
					if (!done) {
						for (Crates c : crates) {
							if (Math.abs(c.locx + c.radius - posx) <= c.radius * 0.8
									&& Math.abs(c.locy + c.radius - posy) <= c.radius * 0.8
									&& c.crateloc == Crates.Crateloc.sky) {
								if (c.cratetype == Crates.Cratetype.health) {
									hcratehit = true;
								} else {
									acratehit = true;
								}
							}
						}

						// check if bullet hit enemy
						if (Math.abs(posx - enemy_locx) <= 3 && Math.abs(posy - enemy_locy) <= 3) {
							tankhit = true;
							done = true;
							deg = j;
							power = i;
						}
					}
				}

				if (tankhit && hcratehit && tank.getHealth() < 100) {
					target_radian = deg;
					target_power = power;
					return;
				}

				if (tankhit && !hcratehit && !acratehit && tank.getHealth() > 100) {
					target_radian = deg;
					target_power = power;
					return;
				}

				done = false;
				hcratehit = false;
				acratehit = false;
				tankhit = false;
			}
		}
		if (deg == -1 && power == -1) {
			target_radian = 1.5708;
			target_power = 100;
		} else {
			target_radian = deg;
			target_power = power;
		}
	}

	public void Update() {
		// need to update movement
		if (mov_target != -1) {
			if (tank.getFuel() > 0) {
				if (lorr == "r") {
					tank.setPosx(tank.getPosx() + 3);
					tank.setFuel(tank.getFuel() - 1);
				} else if (lorr == "l") {
					tank.setPosx(tank.getPosx() - 3);
					tank.setFuel(tank.getFuel() - 1);
				}
				tank.setPosy(Map.get_y((int) tank.getPosx()) - 17);

				tank.setTankangle((Map.get_angle(tank.getPosx() - 10, tank.getPosx() + 10,
						Map.get_y((int) tank.getPosx() - 10), Map.get_y((int) tank.getPosx() + 10))));

				if (Math.abs(tank.getPosx() - mov_target) <= 2) {
					mov_target = -1;
					lorr = "";
					move_ready = true;
				}
			} else {
				move_ready = true;
			}
		} else {
			move_ready = true;
		}

		if (target_radian != -1) {
			if (target_radian < tank.getAngle()) {
				tank.setShotangle(tank.getShotangle() - 0.0174533);
			} else {
				tank.setShotangle(tank.getShotangle() + 0.0174533);
			}

			if (Math.abs(target_radian - tank.getShotangle()) <= 0.01) {
				target_radian = -1;
				radian_ready = true;
			}
		}

		if (target_power != -1) {
			if (target_power < tank.getPower()) {
				tank.setPower(tank.getPower() - 1);
			} else {
				tank.setPower(tank.getPower() + 1);
			}

			if (target_power == tank.getPower()) {
				target_power = -1;
				power_ready = true;
			}
		}

		if (radian_ready && power_ready && move_ready) {
			radian_ready = false;
			power_ready = false;
			move_ready = false;

			Shot t = new Shot(tank.getPosx(), tank.getPosy(), (int) tank.getPower(), tank.getShotangle(), true,
					this.name);
			Shot.shots.add(t);
			t.start();
			ready = true;
		}
	}

	public void Move() {
		// if we below 100 try to find a box
		if (tank.getHealth() < 100) {
			for (Crates crt : Crates.crates) {
				if ((Math.abs(crt.locx - tank.getPosx()) < 200) && crt.crateloc == Crates.Crateloc.floor
						&& crt.cratetype == Crates.Cratetype.health) {
					if (crt.locx < tank.getPosx()) {
						mov_target = crt.locx;
						lorr = "l";
						return;
					} else {
						mov_target = crt.locx;
						lorr = "r";
						return;
					}
				}
			}
		}

		// try to find mountain in left or right..
		int left = Find_Mountain("l");
		int right = Find_Mountain("r");

		if (left > tank.getPosx()) {
			if (right == -1) {
				mov_target = rnd.nextInt((200 + 200) + 1) - 200;
				if (Math.abs(mov_target) > 35) {
					if (mov_target > 0) {
						lorr = "r";
					} else {
						lorr = "l";
					}
					mov_target += tank.getPosx();
					return;
				}
			} else {
				mov_target = right;
				lorr = "r";
				return;
			}
		}

		if (right < tank.getPosx()) {
			if (left == -1) {
				mov_target = rnd.nextInt((200 + 200) + 1) - 200;
				if (Math.abs(mov_target) > 35) {
					if (mov_target > 0) {
						lorr = "r";
					} else {
						lorr = "l";
					}
					mov_target += tank.getPosx();
					return;
				}
			} else {
				mov_target = left;
				lorr = "l";
				return;
			}
		}

		if (left != -1 && right != -1) {
			// checking the human distance and location
			if (Math.abs(tank.getPosx() - Main.Human.tank.getPosx()) <= 150) {
				if (Main.Human.tank.getPosx() >= tank.getPosx()) {
					mov_target = left;
					lorr = "l";
					return;
				}

				if (Main.Human.tank.getPosx() < tank.getPosx()) {
					mov_target = right;
					lorr = "r";
					return;
				}
			}

			if (right - tank.getPosx() < tank.getPosx() - left) {
				mov_target = right;
				lorr = "r";
				return;
			} else {
				mov_target = left;
				lorr = "l";
				return;
			}
		} else if (left == -1 && right == -1) {
			while (true) {
				mov_target = rnd.nextInt((200 + 200) + 1) - 200;
				if (Math.abs(mov_target) > 35) {
					if (mov_target > 0) {
						lorr = "r";
					} else {
						lorr = "l";
					}
					mov_target += tank.getPosx();
					return;
				}
			}
		} else if (right == -1) {
			mov_target = left;
			lorr = "l";
			return;
		} else if (left == -1) {
			mov_target = right;
			lorr = "r";
			return;
		} else {
			mov_target = -1;
			lorr = "";
			return;
		}
	}

	// lorr is left or right
	public int Find_Mountain(String lorr) {
		int top = -1;
		int bottom = -1;

		int move;
		if (lorr == "r") {
			move = 3;
		} else {
			move = -3;
		}

		int first = tank.getPosx();
		int second = first;

		// finding a clue for a hill
		while (true) {
			boolean found = false;
			// finding a clue for a hill
			while (!found) {
				second = first;
				first += move;
				if (first > 1258 || first < 22) {
					return -1;
				}

				if (Map.get_y(first) > Map.get_y(second)) {
					top = second;
					found = true;
				}
			}

			// finding depth of hill
			found = false;
			first = second;
			while (!found) {
				second = first;
				first += 3;
				if (first > 1258 || first < 22) {
					bottom = second;
					found = true;
				}
				if (Map.get_y(first) < Map.get_y(second)) {
					bottom = second;
					found = true;
				}
			}

			// checking if the crater is deeper then 35
			if (Map.get_y(bottom) - Map.get_y(top) > 35) {
				if (lorr == "l" && tank.getPosx() < bottom) {
					first = top - 3;
					second = first;
				} else {
					return bottom;
				}
			} else {
				if (lorr == "r") {
					first = bottom;
					second = bottom;
				} else {
					first = top - 3;
					second = first;
				}
			}
		}
	}
}
