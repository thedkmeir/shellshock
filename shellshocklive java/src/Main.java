import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JFrame;

public class Main extends JFrame {
	// main JFrame
	public static JFrame obj;

	// dir to sprites
	static public String dir = System.getProperty("user.dir") + "\\src\\sprites";

	// gametimer
	public static double delta = 0;
	public static double frametime = 1.0 / 60.0;
	public static long lastTime = System.nanoTime();
	public static long timer = System.currentTimeMillis();
	public static int frames = 0;
	public static long roundtimer = 60;

	// turn counter
	public static int turn_count = 1;

	public static enum PlayState {
		midturn, turn1, turn2
	}

	public static enum GameState {
		mainmenu, play
	}

	// game state vars
	public static PlayState playstate = PlayState.turn1;
	public static PlayState lastturn = PlayState.turn1;
	public static GameState gamestate = GameState.mainmenu;

	// players....
	static public Random rnd = new Random();
	static public Player Human;
	static public Second_Player Bot;

	static MainMenu menu = new MainMenu();

	// reset all the game
	public static void reset() {
		Map.rndmap();

		// reset menu and gamestate
		MainMenu.main.clear();
		MainMenu.pause.clear();
		menu = new MainMenu();
		Main.menu.LoadContent(dir);
		playstate = PlayState.turn1;
		lastturn = PlayState.turn1;
		gamestate = GameState.mainmenu;

		// clearing all lists
		AnimatedSprites.animes.clear();
		Crates.crates.clear();
		Tanks.tanks.clear();
		Human.player_bar.weapon_menu.elelist.clear();

		for (Shot s : Shot.shots) {
			s.stop_thread = "t";
		}
		Shot.shots.clear();

		// reset the main timer
		roundtimer = 60;
		turn_count = 1;
		delta = 0;
		lastTime = System.nanoTime();
		timer = System.currentTimeMillis();
		frames = 0;

		// reset the players and tanks
		Human = new Player(rnd.nextInt(500 - 20 + 1) + 20, 1);
		Bot = new Second_Player(rnd.nextInt(1240 - 780 + 1) + 780);

		Human.tank.setPosy(Map.get_y((int) Human.tank.getPosx()) - 17);

		Human.tank.setTankangle((Map.get_angle(Human.tank.getPosx() - 10, Human.tank.getPosx() + 10,
				Map.get_y((int) Human.tank.getPosx() - 10), Map.get_y((int) Human.tank.getPosx() + 10))));

		Bot.tank.setPosy(Map.get_y((int) Bot.tank.getPosx()) - 17);

		Bot.tank.setTankangle((Map.get_angle(Bot.tank.getPosx() - 10, Bot.tank.getPosx() + 10,
				Map.get_y((int) Bot.tank.getPosx() - 10), Map.get_y((int) Bot.tank.getPosx() + 10))));
	}

	public static void main(String[] args) {
		//
		// setting up game window
		//
		obj = new JFrame();
		Draw draw = new Draw();
		obj.add(draw);
		obj.setBounds(0, 0, 1280, 720);
		obj.setTitle("ShellShock Live");
		obj.setResizable(false);
		obj.setVisible(true);
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// MOUSE LISTENER
		obj.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (gamestate == GameState.mainmenu && MainMenu.pause.size() > 0) {
					menu.M_Update(e);
				} else {
					if (playstate == PlayState.turn1) {
						Human.M_Update(e);
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (gamestate == GameState.mainmenu) {
					menu.click_switch();
				}
				if (playstate == PlayState.turn1) {
					Human.set_isshotm(e);
				}
			}
		});

		// KEY LISTENER
		obj.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (playstate == PlayState.turn1) {
					Human.K_Update(k);
				}
			}

			public void keyReleased(KeyEvent k) {
				if (playstate == PlayState.turn1) {
					Human.set_isshotk(k);
				}

				// re-build map cheat
				if (k.getKeyCode() == KeyEvent.VK_R && gamestate == GameState.mainmenu
						&& MainMenu.getstate() == MainMenu.menustate.menu) {
					Map.rndmap();
					Human.tank.setPosy(Map.get_y(Human.tank.getPosx()) - 17);
					Human.tank.setTankangle((Map.get_angle(Human.tank.getPosx() - 10, Human.tank.getPosx() + 10,
							Map.get_y((int) Human.tank.getPosx() - 10), Map.get_y((int) Human.tank.getPosx() + 10))));
					Bot.tank.setPosy(Map.get_y(Bot.tank.getPosx()) - 17);
					Bot.tank.setTankangle((Map.get_angle(Bot.tank.getPosx() - 10, Bot.tank.getPosx() + 10,
							Map.get_y((int) Bot.tank.getPosx() - 10), Map.get_y((int) Bot.tank.getPosx() + 10))));
				}

				// chet to kill yourself
				if (k.getKeyCode() == KeyEvent.VK_K && gamestate == GameState.play) {
					Human.tank.setHealth(0);
					Bot.tank.setHealth(0);
					menu.setstate(MainMenu.menustate.pause);
				}

				// esc key to pause
				if (k.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (gamestate != GameState.mainmenu) {
						MainMenu.setstate(MainMenu.menustate.pause);
						gamestate = GameState.mainmenu;

						// stopping all bullets mid air
						for (Shot sh : Shot.shots) {
							sh.stop_thread = "p";
						}
					} else {
						gamestate = GameState.play;

						// resuming bullets
						for (Shot sh : Shot.shots) {
							sh.stop_thread = "";
						}
					}
				}
			}
		});

		//
		// update
		//

		// setting up game timer
		lastTime = System.nanoTime();
		timer = System.currentTimeMillis();
		frames = 0;

		// game loop
		while (true) {
			// draw averything
			draw.repaint();

			// update all timer vars
			long now = System.nanoTime();
			long passedTime = now - lastTime;
			lastTime = now;
			delta += passedTime / 1000000000.0;

			if (delta > frametime) {
				//
				frames++;

				if (gamestate == GameState.mainmenu) {
					menu.Update();

					// when someone loses the game goes to a state of main menu and midturn together
					if (playstate == PlayState.midturn && MainMenu.pause.size() == 1) {
						if (Shot.shots.size() > 0) {
							// clear all shots
							for (Shot s : Shot.shots) {
								s.stop_thread = "t";
							}
							Shot.shots.clear();
						}

						for (AnimatedSprites ani : AnimatedSprites.animes) {
							ani.Update();
						}
					}
				} else {
					// turn update
					if (playstate != PlayState.midturn) {

						// if a player or bot is ready, cut the timer
						if (playstate == PlayState.turn1 && Human.ready) {
							roundtimer = 0;
						}
						if (playstate == PlayState.turn2 && Bot.ready) {
							roundtimer = 0;
						}

						// update timer
						if (roundtimer > 0) {
							if (frames >= 60) {
								frames = 0;
								roundtimer--;
							}
						} else {
							// after both tanks are ready we switch to midturn mode where bullets fly
							lastturn = playstate;
							playstate = PlayState.midturn;
							for (Tanks tn : Tanks.tanks) {
								tn.setFuel(70);
							}
						}
					} else {
						// switch turn after all shots has landed
						if (Shot.shots.size() == 0 && AnimatedSprites.animes.size() == 0) {
							// reset ready flags for players
							Main.Bot.ready = false;
							Main.Human.ready = false;

							// update turn counter and timer
							turn_count++;
							roundtimer = 60;

							if (turn_count % 5 == 0) {
								// clear all crates
								Crates.crates.clear();

								// randomize a new map
								Map.rndmap();

								// spawn crates
								for (int i = rnd.nextInt(4) + 2; i > 0; i--) {
									Crates.crates.add(new Crates());
								}
							}

							// relocate all crates
							for (Crates crt : Crates.crates) {
								if (crt.crateloc == Crates.Crateloc.floor) {
									crt.locy = Map.get_y(crt.locx);
									crt.angle = (double) Map.get_angle(crt.locx - crt.radius, crt.locx + crt.radius,
											Map.get_y((int) crt.locx - crt.radius),
											Map.get_y((int) crt.locx + crt.radius));
								}
							}

							// re-locate players
							Human.tank.setPosy(Map.get_y(Human.tank.getPosx()) - 17);
							Human.tank.setTankangle((Map.get_angle(Human.tank.getPosx() - 10, Human.tank.getPosx() + 10,
									Map.get_y((int) Human.tank.getPosx() - 10),
									Map.get_y((int) Human.tank.getPosx() + 10))));

							Bot.tank.setPosy(Map.get_y(Bot.tank.getPosx()) - 17);
							Bot.tank.setTankangle((Map.get_angle(Bot.tank.getPosx() - 10, Bot.tank.getPosx() + 10,
									Map.get_y((int) Bot.tank.getPosx() - 10),
									Map.get_y((int) Bot.tank.getPosx() + 10))));

							if (lastturn == PlayState.turn1) {
								playstate = PlayState.turn2;

								// need to activate bot Start_routine
								Bot.Start_routine();
							} else {
								playstate = PlayState.turn1;
							}
						}
					}

					if (playstate == PlayState.turn1) {
						Human.update();
					}
					if (playstate == PlayState.turn2) {
						// for testing reasons bot is not gonna be active for now
						//
						//
						Bot.Update();
					}

					// bullets handeling
					if (playstate == PlayState.midturn) {
						// stop all bullets threads
						//
						for (Shot sh : Shot.shots) {
							if (sh.stop_thread != "t") {
								sh.stop_thread = "p";
							}
						}

						// check if any tanks are hit by a bullet and update thair health
						for (Shot sh : Shot.shots) {
							for (Tanks tn : Tanks.tanks) {
								int sum = sh.radius + tn.getRadius();
								if (Math.abs(sh.posx - tn.getPosx()) <= sum
										&& Math.abs(sh.posy - tn.getPosy()) <= sum) {

									// if the bullet was shot from this tank we need to check that the bullet
									// actually got out of the tank first
									if (tn.getPl_name() != sh.Last_Hit) {
										if (tn.getHealth() > 0) {
											tn.setHealth(tn.getHealth() - sh.damage);
										} else {
											tn.setHealth(0);
										}
										// if the bullat is a bouncer the done hit
										if (sh.t.getName() != "Bouncer") {
											sh.hit = true;
										}
										sh.Last_Hit = tn.getPl_name();
										// sh.set_posy((int) sh.get_posy() + 10);
									}
								} else {
									if (tn.getPl_name() == sh.Last_Hit) {
										sh.Last_Hit = "";
									}
								}
							}
						}

						// remove all the shots that landed from the list
						Shot.shots.removeIf(s -> s.hit == true);

						// resume all threads
						//
						for (Shot sh : Shot.shots) {
							if (sh.stop_thread != "t") {
								sh.stop_thread = "";
							}
						}
					}

					// update all tanks and if someone is dead add explosion
					for (Tanks tn : Tanks.tanks) {
						tn.Update();
						if (tn.getHealth() == 0 && tn.getDeadexplosion() == false) {
							tn.setDeadexplosion(true);
							AnimatedSprites.animes
									.add(new AnimatedSprites(Tanks.explosion, tn.getPosx(), tn.getPosy()));
							gamestate = GameState.mainmenu;
							playstate = PlayState.midturn;
							MainMenu.pause.get(0).MoveElement(1380, 740);
							MainMenu.pause.get(1).SetElementLoc_y(600);
							menu.setstate(MainMenu.menustate.pause);
						}
					}

					// update all animations

					for (Shot sh : Shot.shots) {
						if (sh.stop_thread != "t") {
							sh.stop_thread = "p";
						}
					}

					for (AnimatedSprites ani : AnimatedSprites.animes) {
						ani.Update();
					}
					AnimatedSprites.animes.removeIf(a -> a.States() == AnimatedSprites.state.done);

					for (Shot sh : Shot.shots) {
						if (sh.stop_thread != "t") {
							sh.stop_thread = "";
						}
					}

					// update all crates
					for (Crates cr : Crates.crates) {
						cr.update();
					}
					Crates.crates.removeIf(c -> c.hit == true);

				}
				delta -= frametime;
				// in delta check
			}
			// out of delta chec

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
			}

			// for (Shot sh : Shot.shots) {
			// sh.t.suspend();
			// sh.stop_thread = "p";
			// }

			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
