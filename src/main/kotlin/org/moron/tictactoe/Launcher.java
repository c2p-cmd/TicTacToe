package org.moron.tictactoe;

public class Launcher implements Runnable {
    private static String[] ARGS;
    @Override
    public void run() {
        System.out.println("Starting " + Thread.currentThread().getName());
        GameMain.main(ARGS);
    }

    public static void main(String[] args) {
        ARGS = args;
        new Thread(new Launcher(), "Launcher Thread").start();
    }
}
