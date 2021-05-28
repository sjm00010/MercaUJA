package ujaen.es.mercauja;

import static java.lang.Thread.sleep;
import java.util.concurrent.CountDownLatch;

/**
 * Tarea para cerrar el mercado
 * @author sjm00010
 */
public class TareaCerrarMercado implements Runnable {
    private final CountDownLatch cerrar;

    public TareaCerrarMercado(CountDownLatch cerrar) {
        this.cerrar = cerrar;
    }

    @Override
    public void run() {
        try {
            sleep(Constantes.TIEMPO_SUBASTA);
        } catch (InterruptedException ex) {
            System.out.println("Hilo(TareaCerrarMercado) CANCELA la ejecución");
        }finally{
            cerrar.countDown();
        }
    }
}
