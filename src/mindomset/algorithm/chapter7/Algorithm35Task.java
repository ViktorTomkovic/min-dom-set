package mindomset.algorithm.chapter7;

import java.util.LinkedHashMap;

public class Algorithm35Task implements Runnable {
	private Algorithm35 alg;
	private Algorithm35State state;
	private LinkedHashMap<Integer, Algorithm35State> vertices;
	private boolean finished;

	public Algorithm35Task(Algorithm35 alg) {
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

	public Integer computeSpan() {
		int w = 0;
		synchronized (state.W) {
			w = state.W.size();
		}
		Double a = Math.pow(2, Math.floor(Math.log(w) / Math.log(2)));
		int result = a.intValue();
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

	private void joinS() {
		synchronized (alg.joinLock) {
			LinkedHashMap<Integer, Algorithm35State> states = alg.getAllVertices();
			synchronized (state.dist2NotSorG) {
				for (Integer v : state.dist2NotSorG) {
					states.get(v).takeABreakePlease();
				}
				for (Integer v : state.dist2NotSorG) {
					Algorithm35State s = states.get(v);
					s.recieveRemoveFromW(state.v);
					if (!state.v.equals(v)) {
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
			LinkedHashMap<Integer, Algorithm35State> states = alg.getAllVertices();
			synchronized (state.dist2NotSorG) {
				for (Integer v : state.dist2NotSorG) {
					states.get(v).takeABreakePlease();
				}
				for (Integer v : state.dist2NotSorG) {
					Algorithm35State s = alg.getAllVertices().get(v);
					s.recieveRemoveFromW(state.v);
					if (!state.v.equals(v)) {
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
		setNextState();
		while (!finished) {
			if (hasWhiteNeighbours(state.v)) {
				state.w = computeSpan();
				synchronized (state.dist2NotSorG) {
					for (Integer v2 : state.dist2NotSorG) {
						vertices.get(v2).recieveSpan(state.v, state.w);
					}
				}
				if (recievedFromAll()) {
					state.w = computeSpan();

					long maxOtherSpan = maxFromOthers();
					if (maxOtherSpan <= state.w) {
						synchronized (state.isCandidateLock) {
							state.isCandidate = true;
						}
					}
					state.c = computeC();
					if (joinTest()) {
						joinS();
						setNextState();
						if (finished)
							return;
					}
					state.spans.clear();
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

	private boolean joinTest() {
		boolean b = state.isCandidate;
		long sum = 0L;
		synchronized (state.W) {
			for (Integer v : state.W) {
				Algorithm35State s = vertices.get(v);
				synchronized (s.cLock) {
					sum = sum + s.c;
				}
			}
		}
		boolean c;
		synchronized (state.wLock) {
			c = sum <= 3 * state.w;
		}
		return b && c;
	}

	private Integer computeC() {
		int c = 0;
		synchronized (state.N) {
			for (Integer v : state.N) {
				Algorithm35State s = vertices.get(v);
				synchronized (s.isCandidateLock) {
					if (s.isCandidate)
						c++;
				}
			}
		}
		return c;
	}

	private long maxFromOthers() {
		long max = 0L;
		synchronized (state.spans) {
			for (Integer l : state.spans.values()) {
				if (l > max) {
					max = l;
				}
			}
		}
		return max;
	}

	public Algorithm35State getState() {
		return state;
	}

	public void setState(Algorithm35State state) {
		this.state = state;
	}

}
