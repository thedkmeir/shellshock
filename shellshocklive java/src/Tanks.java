import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class Tanks {
	public static Image tank1;
	public static Image tank2;
	public static Image tank1_body;
	public static Image tank2_body;
	public static Image turret;
	public static Image[] explosion = new Image[4];

	public static List<Tanks> tanks = new ArrayList<Tanks>();

	private Mon_Info mon_info;

	private int posx;
	private int posy;
	private int health = 200;
	private int movespeed = 200;
	// private double shotangle = 0.795398;
	private double shotangle = 0;
	private double tankangle = 0;
	private double power = 100;
	private int aimspeed = 10;
	private boolean isshot = true;
	private int radius = 21;
	private int team;
	private boolean deadexplosion = false;
	private int fuel = 70;
	private String pl_name;

	public String getPl_name() {
		return pl_name;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int h) {
		health = h;
	}

	public int getMovespeed() {
		return movespeed;
	}

	public void setMovespeed(int h) {
		movespeed = h;
	}

	public double getAngle() {
		return shotangle;
	}

	public void setAngle(double h) {
		shotangle = h;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double h) {
		power = h;
	}

	public int getAimspeed() {
		return aimspeed;
	}

	public void setAimspeed(int h) {
		aimspeed = h;
	}

	public int getPosx() {
		return posx;
	}

	public void setPosx(int h) {
		posx = h;
	}

	public int getPosy() {
		return posy;
	}

	public void setPosy(int h) {
		posy = h;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int h) {
		radius = h;
	}

	public double getTankangle() {
		return tankangle;
	}

	public void setTankangle(double h) {
		tankangle = h;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int h) {
		team = h;
	}

	public double getShotangle() {
		return shotangle;
	}

	public void setShotangle(double h) {
		shotangle = h;
	}

	public boolean getIsshot() {
		return isshot;
	}

	public void setIsshot(boolean h) {
		isshot = h;
	}

	public boolean getDeadexplosion() {
		return deadexplosion;
	}

	public void setDeadexplosion(boolean h) {
		deadexplosion = h;
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int h) {
		fuel = h;
	}

	public Tanks(int x, int team, String name) {
		posx = x + 21;
		posy = Map.get_y(x) - 17;
		this.team = team;
		tankangle = Map.get_angle(posx - 10, posx + 10, Map.get_y(posx - 10), Map.get_y(posx + 10));
		mon_info = new Mon_Info(name);
		this.pl_name = name;
	}

	public static void LoadContent(String dir) {
		tank1 = new ImageIcon(dir + "\\Tanks\\tanks_tankDesert1.png").getImage();
		tank1_body = new ImageIcon(dir + "\\Tanks\\tanks_tankDesert_body1.png").getImage();
		tank2 = new ImageIcon(dir + "\\Tanks\\tanks_tankNavy1.png").getImage();
		tank2_body = new ImageIcon(dir + "\\Tanks\\tanks_tankNavy_body1.png").getImage();
		turret = new ImageIcon(dir + "\\Tanks\\tanks_turret1.png").getImage();
		explosion[0] = new ImageIcon(dir + "\\Explosions\\tank_explosion1.png").getImage();
		explosion[1] = new ImageIcon(dir + "\\Explosions\\tank_explosion2.png").getImage();
		explosion[2] = new ImageIcon(dir + "\\Explosions\\tank_explosion3.png").getImage();
		explosion[3] = new ImageIcon(dir + "\\Explosions\\tank_explosion4.png").getImage();
	}

	public void Update() {
		mon_info.Update(this.health);
	}

	public void Draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform backup = g2d.getTransform();
		if (team == 1) {
			if (health > 0) {
				AffineTransform tx = AffineTransform.getRotateInstance(-shotangle, posx,
						posy - Main.obj.getInsets().top);
				g2d.setTransform(tx);
				// g2d.drawImage(turret, posx, posy + 17 - Main.obj.getInsets().top, null);
				g2d.drawImage(turret, posx, posy - Main.obj.getInsets().top, null);
				tx = AffineTransform.getRotateInstance(tankangle, posx, posy - Main.obj.getInsets().top);
				g2d.setTransform(tx);
				// g2d.drawImage(tank1, posx -21 + originx, posy - 17 + originy -
				// Main.obj.getInsets().top, null);
				g2d.drawImage(tank1, posx - 21, posy - 17 - Main.obj.getInsets().top, null);
			} else {
				AffineTransform tx = AffineTransform.getRotateInstance(tankangle, posx,
						posy - Main.obj.getInsets().top);
				g2d.setTransform(tx);
				g2d.drawImage(tank1_body, posx - 21, posy - 7 - Main.obj.getInsets().top, null);
			}
		} else {
			if (health > 0) {
				AffineTransform tx = AffineTransform.getRotateInstance(-shotangle, posx,
						posy - Main.obj.getInsets().top);
				g2d.setTransform(tx);
				g2d.drawImage(turret, posx, posy - Main.obj.getInsets().top, null);
				tx = AffineTransform.getRotateInstance(tankangle, posx, posy - Main.obj.getInsets().top);
				g2d.setTransform(tx);
				g2d.drawImage(tank2, posx - 21, posy - 17 - Main.obj.getInsets().top, null);
			} else {
				AffineTransform tx = AffineTransform.getRotateInstance(tankangle, posx,
						posy - Main.obj.getInsets().top);
				g2d.setTransform(tx);
				g2d.drawImage(tank2_body, posx - 21, posy - 7 - Main.obj.getInsets().top, null);
			}
		}
		g2d.setTransform(backup);
		if (Math.abs(posx - (MouseInfo.getPointerInfo().getLocation().x - Main.obj.getLocation().x)) <= radius
				&& Math.abs(posy - (MouseInfo.getPointerInfo().getLocation().y - Main.obj.getLocation().y)) <= radius) {
			mon_info.Draw(g);
		}
	}
}
