package algorithm;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public interface AbstractMSCAlgorithm {
	public ArrayList<Long> getMSCforMDS(LinkedHashSet<Long> universum,
			ArrayList<RepresentedSet> sets);
}
