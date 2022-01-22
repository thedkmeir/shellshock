import java.util.Random;

public class Map {
	public static int[] maphights = new int[1281];

	public static int[] Maphights() {
		return maphights;
	}

	// re-align all the hight spaces
	public static void build_map(float m, float b, int start, int stop) {
		int rise = 0;
		if (start != 0) {
			rise = maphights[start - 1] - (int) ((m * start) + b);
		}
		for (int x = start; x <= stop; x++) {
			maphights[x] = (int) ((m * x) + b) + rise;
			if (maphights[x] < 250 || maphights[x] > 500) {
				maphights[x] = maphights[x - 1];
			}
		}
	}

	// randomize a linear function copy y values to the array
	public static void rndmap() {
		Random rnd = new Random();
		int start = 0;
		int stop = 0;
		int num;
		float m;
		float prvm = 0;
		float b;
		while (start + 50 < 1280) {
			stop = rnd.nextInt(50) + start;
			num = rnd.nextInt(240) - 120;
			m = (float) num / (float) 100;
			if (prvm == 0 && m == 0) {
				while (m != 0) {
					m = (rnd.nextInt(2400) - 1200) / 1000;
				}
			}
			prvm = m;
			b = rnd.nextInt(16) + 301;
			build_map(m, b, start, stop);
			start = stop;
		}
		stop = 1280;
		num = rnd.nextInt(240) - 120;
		m = (float) num / (float) 100;
		if (prvm == 0 && m == 0) {
			while (m != 0) {
				m = (rnd.nextInt(2400) - 1200) / 1000;
			}
		}
		b = rnd.nextInt(16) + 301;
		build_map(m, b, start, stop);
	}

	public static int get_y(int x) {
		if (x < 0) {
			x = 1;
		}
		if (x > 1280) {
			x = 1279;
		}
		return maphights[x];
	}

	// caculate angle from two points
	public static float get_angle(float x1, float x2, float y1, float y2) {
		float X = x2 - x1;
		float Y = y2 - y1;
		float Z = (float) Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
		return (float) (Math.asin(Y / Z));
	}

}
