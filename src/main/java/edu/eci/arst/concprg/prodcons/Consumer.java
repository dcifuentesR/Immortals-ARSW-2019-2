/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			try {
				consume();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException ex) {
//				Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
//			}

		
	}

	/**
	 * @throws InterruptedException 
	 * 
	 */
	public void consume() throws InterruptedException {
		synchronized (queue) {
			while (queue.isEmpty()) {
				System.out.println("queue empty");
				queue.wait();
			}
			int elem = queue.poll();
			System.out.println("Consumer consumes " + elem);
			queue.notifyAll();
		}
	}
}
