package gne;

public final class Utils {
	public static int[] randomIntList(int max) {
		int[] ret = new int[max];
		for (int i = 0;i< ret.length;i++) {
			ret[i] = -1;
		}
		ret[0] = (int) (Math.random()*max);
		for (int i = 1;i<max;i++) {
			boolean run = true;
			while (run) {
				run = false;
				int newValue = (int) (Math.random()*max);
				for (int i2 = 0;i2<max;i2++) {
					if (ret[i2]==newValue) run = true;
				}
				ret[i] = newValue;
			}
		}
		return ret;
	}
}
