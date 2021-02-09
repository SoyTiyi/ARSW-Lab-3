package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback = null;

    private AtomicInteger health;

    private AtomicBoolean bool;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean stop;

    public Immortal(String name, List<Immortal> immortalsPopulation, AtomicInteger health, int defaultDamageValue,
            ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue = defaultDamageValue;
        bool = new AtomicBoolean(true);
    }

    public void run() {

        while (bool.get()) {
            synchronized (this) {
                while (stop) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        System.out.println("F");
                    }
                }
            }
            Immortal im;
            synchronized (immortalsPopulation){
                if(health.get()==0){
                    bool.getAndSet(false);
                }
                int myIndex = immortalsPopulation.indexOf(this);
                int nextFighterIndex = r.nextInt(immortalsPopulation.size());
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }
                im = immortalsPopulation.get(nextFighterIndex);
                this.fight(im);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void fight(Immortal i2) {

        if (i2.getHealth().get() > 0 && bool.get()) {
            AtomicInteger atomic = new AtomicInteger(i2.getHealth().get() - defaultDamageValue);
            i2.changeHealth(atomic);
            health.addAndGet(defaultDamageValue);
            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }

    }

    public synchronized void changeHealth(AtomicInteger v) {
        health = v;
    }

    public AtomicInteger getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public void setBool(boolean bool){
        this.bool = new AtomicBoolean(bool);
    }

    public synchronized void setStop(boolean stop){
        this.stop = stop;
        if(stop==false){
            this.notifyAll();
        }
    }

}
