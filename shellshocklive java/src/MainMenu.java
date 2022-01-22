import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainMenu {

	// enum that says if menu is main menu or pause
	public enum menustate {
		menu, pause
	}

	private static menustate state = menustate.menu;

	// all gui bottons for main menu and pause
	public static List<GUIElement> main = new ArrayList<GUIElement>();
	public static List<GUIElement> pause = new ArrayList<GUIElement>();
	private static int space = 80;

	public static menustate getstate() {
		return state;
	}

	public static void setstate(menustate val) {
		state = val;
	}

	public MainMenu() {
		// add new buttond

		// main menu
		GUIElement play_butt = new GUIElement("Menu_Assets\\Play", "m");
		main.add(play_butt);

		GUIElement exit_butt = new GUIElement("Menu_Assets\\Exit", "m");
		main.add(exit_butt);

		// pause menu
		GUIElement resume_butt = new GUIElement("Menu_Assets\\Resume", "m");
		pause.add(resume_butt);

		GUIElement exit_butt2 = new GUIElement("Menu_Assets\\Exit", "m");
		pause.add(exit_butt2);
	}

	public void LoadContent(String dir) {
		// load buttons
		int heightM = 180 + (space * (main.size() / 2));

		for (GUIElement ele : main) {
			ele.LoadContent(dir);
			ele.CenterElement();
			ele.SetElementLoc_y(heightM);
			heightM += space;
		}

		int heightP = 180 - (space * main.size() / 2);

		for (GUIElement ele : pause) {
			ele.LoadContent(dir);
			ele.CenterElement();
			ele.SetElementLoc_y(heightM);
			heightM += space;
		}
	}

	public void Draw(Graphics g) {
		if (state == menustate.menu) {
			for (GUIElement ele : main) {
				ele.Draw(g);
			}
		} else {
			for (GUIElement ele : pause) {
				ele.Draw(g);
			}
		}
	}

	// update all buttons
	public void Update() {
		// check if mouse on buttons
		if (state == menustate.menu) {
			for (GUIElement ele : main) {
				ele.update();
			}
		} else {
			for (GUIElement ele : pause) {
				ele.update();
			}
		}
	}

	// mouse trigger to all buttons
	public void M_Update(MouseEvent e) {
		if (state == menustate.menu) {
			for (GUIElement ele : main) {

				ele.M_Update(e);
			}
		} else {
			for (GUIElement ele : pause) {
				ele.M_Update(e);
			}
		}
	}

	public void click_switch() {
		if (state == menustate.menu) {
			for (GUIElement ele : main) {

				ele.click_switch();
			}
		} else {
			for (GUIElement ele : pause) {
				ele.click_switch();
			}
		}
	}

	// if one of the buttons get clicked this function is triggered
	public static void OnClick(String assetname) {
		// give buttons actions
		if (assetname == "Menu_Assets\\Play" || assetname == "Menu_Assets\\Resume") {
			Main.gamestate = Main.GameState.play;
		}

		if (assetname == "Menu_Assets\\Exit") {
			if (state == menustate.menu) {
				System.exit(0);
			} else {
				// reset game!!!
				state = menustate.menu;
				Main.reset();
			}
		}
	}
}
