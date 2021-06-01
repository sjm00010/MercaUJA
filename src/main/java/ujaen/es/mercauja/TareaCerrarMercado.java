package ujaen.es.mercauja;

import java.util.concurrent.ExecutorService;

/**
 * Tarea para cerrar el mercado
 * @author sjm00010
 */
public class TareaCerrarMercado implements Runnable {
    private final ExecutorService cerrar;

    public TareaCerrarMercado(ExecutorService cerrar) {
        this.cerrar = cerrar;
    }

    @Override
    public void run() {
        System.out.println("Hilo(TareaCerrarMercado) : el MERCADO va ha CERRAR, CANCELANDO las tareas pendientes");
        cerrar.shutdown();
    }
}
