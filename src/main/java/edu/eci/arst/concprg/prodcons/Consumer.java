/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread {

    private Queue<Integer> queue;

    public Consumer(Queue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            /* Se bajo el consumo "DUERMIENDO" al thread por 1.5 segundos */
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                System.out.println("F");
            }
            synchronized (queue) {
                if (queue.size() > 0) {
                    int elem = queue.poll();
                    System.out.println("Consumer consumes " + elem);
                }
            }
        }
    }
}
