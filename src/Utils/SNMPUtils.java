package Utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Isaac
 */
public class SNMPUtils {
    public static void freeMem(){
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().freeMemory();
                Runtime.getRuntime().gc();
                System.gc();
                System.out.println("Memoria Liberada. Memoria Actual Disp " + Runtime.getRuntime().freeMemory() / (1024*1024) + " MB");
            }
        }).start();
    }
}
