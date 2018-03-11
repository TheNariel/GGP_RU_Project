package galp.ggp.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Variance {
	double mean = 0, M2 = 0, variance = 0;
	int nOfPlayouts = 0;
	List<Integer> depths = new ArrayList<Integer>();

	public Variance() {

	}

	public void update(int value) {
		double delta = value - mean;
		mean = mean + (delta / nOfPlayouts);
		double delta2 = value - mean;
		M2 = M2 + (delta * delta2);
		variance = M2 / (nOfPlayouts - 1);

	}

	public Set<Integer> mode() {
		int maxFrequency = 0;
		boolean modeFound = false;
		Set<Integer> modeSet = new HashSet<>();
		Collections.sort(depths);
		for (int i = 0; i < depths.size(); i++) {
			int number = depths.get(i);
			int count = 1;
			for (; (i + count) < depths.size() && depths.get(i + count) == number; count++) {
			}
			i += (count - 1);
			if (maxFrequency != 0 && count != maxFrequency) {
				modeFound = true;
			}
			if (count > maxFrequency) {
				modeSet.clear();
				modeSet.add(number);
				maxFrequency = count;
			} else if (count == maxFrequency) {
				modeSet.add(number);
			}
		}
		if (!modeFound) {
			modeSet.clear();
		}
		return modeSet;
	}

	public HashMap<Integer,Integer> getsss() {
		HashMap<Integer,Integer> ret = new HashMap<Integer,Integer>();
		Set<Integer> foo = new HashSet<Integer>(depths);
		for (final Integer integer : foo) {
			ret.put(integer, (int) depths.stream().filter(new Predicate<Integer>() {
				@Override
				public boolean test(Integer p) {
					return p.equals(integer);
				}
			}).count());
		}
		return ret;
	}
}
