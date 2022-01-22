import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

public class GUIElement {

	// enum that says if mouse on button or not
	private enum state {
		reg, mon
	}

	private state stat = state.reg;

	// textures
	private Image GUITexture;
	private Image GUITexture_Mon;

	private Rectangle GUIRect;
	private String assetname;
	private String belong;

	public boolean click_switch = false;

	public int get_height() {
		return GUIRect.height;
	}

	public GUIElement(String assetname, String belong) {
		this.assetname = assetname;
		this.belong = belong;
	}

	public void LoadContent(String dir) {
		GUITexture = new ImageIcon(dir + "\\" + assetname + "_Butt.png").getImage();
		GUITexture_Mon = new ImageIcon(dir + "\\" + assetname + "_Butt_Mon.png").getImage();
		GUIRect = new Rectangle(0, 0, GUITexture.getWidth(null), GUITexture.getHeight(null));
	}

	public void Draw(Graphics g) {
		switch (stat) {
		case reg:
			g.drawImage(GUITexture, GUIRect.x, GUIRect.y - Main.obj.getInsets().top, null);
			break;
		case mon:
			g.drawImage(GUITexture_Mon, GUIRect.x, GUIRect.y - Main.obj.getInsets().top, null);
			break;
		}
	}

	// checks if mouse on/off button and updates it
	public void update() {
		if (GUIRect.contains(new Point(MouseInfo.getPointerInfo().getLocation().x - Main.obj.getLocation().x,
				MouseInfo.getPointerInfo().getLocation().y - Main.obj.getLocation().y))) {
			stat = state.mon;
		} else {
			stat = state.reg;
		}
	}

	public void click_switch() {
		click_switch = true;
	}

	// mouse event trigger
	public void M_Update(MouseEvent e) {
		if (GUIRect.contains(e.getPoint())) {
			stat = state.mon;
			click_switch = false;
			if (belong == "m") {
				MainMenu.OnClick(assetname);
			} else {
				Player_Bar.OnClick(assetname);
			}
		} else {
			stat = state.reg;
		}
	}

	public void MoveElement(int x, int y) {
		GUIRect = new Rectangle(GUIRect.x += x, GUIRect.y += y, GUIRect.width, GUIRect.height);
	}

	public void SetElementLoc_y(int y) {
		GUIRect = new Rectangle(GUIRect.x, y, GUIRect.width, GUIRect.height);
	}

	public void CenterElement() {
		GUIRect = new Rectangle((1280 / 2) - (this.GUITexture.getWidth(null) / 2),
				(720 / 2) - (this.GUITexture.getHeight(null) / 2), this.GUITexture.getWidth(null),
				this.GUITexture.getHeight(null));
	}
}
