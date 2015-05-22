package main;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class testBed {

	ObjectInputStream reader;
	public static void main(String[] args) throws Exception {
		testBed t = new testBed();
		t.outputStreamTest();
	}


	public void scannerTest() {
		Scanner sc = new Scanner(System.in);
		String out = sc.nextLine();
		System.out.println(out);
	}

	public void nonStatic() throws Exception {
		while (true) {
			System.out.println("Loop start");
			reader = new ObjectInputStream
					(new ObjectInputStream(System.in));
			System.out.println("Loop end");
		}

	}

	public void outputStreamTest() {
		Object o = new Object();
		new LoginWindow(o);
		MainWindow m = new MainWindow(o, "");

		while (true) {
			try {
				synchronized(o) {
					o.wait();
					System.out.println(m.getMessage());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void nonStaticDebugger() {
		while (true) {
			int x = 5;
			System.out.println(x);
		}
	}
}
