package algorithm.chapter7;

import java.util.LinkedHashMap;

public class Algorithm34Task implements Runnable {
	private Algorithm34 alg;
	private Algorithm34State state;
	private LinkedHashMap<Integer, Algorithm34State> vertices;
	private boolean finished;

	public Algorithm34Task(Algorithm34 alg) {
		this.alg = alg;
		this.vertices = alg.getAllVertices();
		this.finished = false;
	}

	private void saveState() {
		alg.saveState(state);
		setState(null);
		return;
	}

	private void setNextState() {
		setState(alg.chooseNextVertex());
		if (state == null) {
			finished = true;
		}
		return;
	}

	private boolean hasWhiteNeighbours(Integer v) {
		boolean result;
		synchronized (state.W) {
			state.W.remove(v);
			result = !state.W.isEmpty();
			state.W.add(v);
		}
		return result;
	}

	public Integer computeSpan(Integer v) {
		Integer result;
		synchronized (state.W) {
			result = Integer.valueOf(state.W.size());
		}
		return result;
	}

	public boolean recievedFromAll() {
		boolean b = false;
		synchronized (state.dist2NotSorG) {
			synchronized (state.spans) {
				b = state.spans.keySet().containsAll(state.dist2NotSorG);
			}
		}
		return b;
	}

	private boolean finalTest(boolean isBiggest) {
		boolean b = isBiggest && hasWhiteNeighbours(state.v);
		synchronized (state.canJoinLock) {
			b = b && (state.canJoin == 0);
		}
		return b;
	}

	private void joinS() {
		synchronized (alg.joinLock) {
			LinkedHashMap<Integer, Algorithm34State> states = alg.getAllVertices();
			synchronized (state.dist2NotSorG) {
				for (Integer v : state.dist2NotSorG) {
					states.get(v).takeABreakePlease();
				}
				for (Integer v : state.dist2NotSorG) {
					Algorithm34State s = states.get(v);
					s.recieveRemoveFromW(state.v);
					if (!v.equals(state.v)) {
						s.recieveRemoveFromDist2(state.v);
					}
				}
				alg.joinS(state.v);
				for (Integer v : state.dist2NotSorG) {
					states.get(v).thankYou();
				}
			}
		}
		return;
	}

	private void joinG() {
		synchronized (alg.joinLock) {
			LinkedHashMap<Integer, Algorithm34State> states = alg.getAllVertices();
			synchronized (state.dist2NotSorG) {
				for (Integer v : state.dist2NotSorG) {
					states.get(v).takeABreakePlease();
				}
				for (Integer v : state.dist2NotSorG) {
					Algorithm34State s = alg.getAllVertices().get(v);
					s.recieveRemoveFromW(state.v);
					if (!v.equals(state.v)) {
						s.recieveRemoveFromDist2(state.v);
					}
				}
				for (Integer v : state.dist2NotSorG) {
					states.get(v).thankYou();
				}
			}
		}
		return;
	}

	@Override
	public void run() {
		/*
		 * try { synchronized (alg.waitForStart) { alg.waitForStart.wait(); } }
		 * catch (InterruptedException e1) { e1.printStackTrace(); }
		 */
		setNextState();
		while (!finished) {
			if (hasWhiteNeighbours(state.v)) {
				state.w = computeSpan(state.v);
				synchronized (state.dist2NotSorG) {
					for (Integer v2 : state.dist2NotSorG) {
						vertices.get(v2).recieveSpan(state.v, state.w);
					}
				}
				if (recievedFromAll()) {
					state.w = computeSpan(state.v);

					boolean isBiggest = true;
					synchronized (state.dist2NotSorG) {
						for (Integer v2 : state.dist2NotSorG) {
							Integer getV2 = state.spans.get(v2);
							if ((getV2 > state.w)
									|| (getV2.equals(state.w) && (v2 < state.v))) {
								isBiggest = false;
							}
						}
					}
					if (finalTest(isBiggest)) {
						joinS();
						// System.out.println("koniec+ " + state.v);
						setNextState();
						if (finished)
							return;
					}
					//state.spans.clear();
				}
				try {
					Thread.sleep(0, 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				joinG();
				// System.out.println("koniec- " + state.v);
				setNextState();
				if (finished)
					return;
			}
			saveState();
			setNextState();
			if (finished)
				return;
		}
		return;
	}

	public Algorithm34State getState() {
		return state;
	}

	public void setState(Algorithm34State state) {
		this.state = state;
	}

}
