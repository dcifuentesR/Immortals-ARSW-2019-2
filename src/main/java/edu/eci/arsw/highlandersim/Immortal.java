package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

	private boolean paused;
	
	private boolean stop;

	private ImmortalUpdateReportCallback updateCallback = null;

	private AtomicInteger health;

	private int defaultDamageValue;

	private final List<Immortal> immortalsPopulation;

	private final String name;

	private final Random r = new Random(System.currentTimeMillis());

	public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue,
			ImmortalUpdateReportCallback ucb) {
		super(name);
		this.updateCallback = ucb;
		this.name = name;
		this.immortalsPopulation = immortalsPopulation;
		this.health = new AtomicInteger(health);
		this.defaultDamageValue = defaultDamageValue;
	}

	public void run() {

		while (!stop) {
			if(health.get() == 0) {
				immortalsPopulation.remove(this);
				break;
			}
			
			synchronized (immortalsPopulation) {
				while (paused) {
					try {
						immortalsPopulation.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				immortalsPopulation.notifyAll();

				Immortal im;

				int myIndex = immortalsPopulation.indexOf(this);

				int nextFighterIndex = r.nextInt(immortalsPopulation.size());

				// avoid self-fight
				if (nextFighterIndex == myIndex) {
					nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
				}

				im = immortalsPopulation.get(nextFighterIndex);

				this.fight(im);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		

	}

	public void fight(Immortal i2) {

		if (i2.getHealth() > 0) {
			i2.changeHealth(i2.getHealth() - defaultDamageValue);
			this.health.addAndGet(defaultDamageValue);
			updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
		} else {
			updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
		}
	}

	public void pause() {
		paused = true;
	}

	public void resumeImmortal() {
		paused = false;
	}

	public void changeHealth(int v) {
		health.set(v);
	}

	public int getHealth() {
		return health.get();
	}
	
	public void stopImmortal() {
		stop = true;
	}

	@Override
	public String toString() {

		return name + "[" + health + "]";
	}

}
