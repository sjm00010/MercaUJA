package ujaen.es.mercauja;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Tarea para pujar por un producto del catálogo
 * @author sjm00010
 */
public class TareaPujar implements Runnable {
    // Variables
    private final List<String> registro;
    private final Comprador comprador;
    private final Producto producto;
    private final int precio;
    
    // Excusión mutua
    private final ReentrantLock lockRegistro;
    
    public TareaPujar(  List<String> registro, Comprador comprador, 
                        Producto producto, int precio, ReentrantLock lockRegistro
                        ) {
        this.registro = registro;
        this.comprador = comprador;
        this.producto = producto;
        this.precio = precio;
        this.lockRegistro = lockRegistro;
    }

    @Override
    public void run() {
        // Añade el nuevo producto al catálogo
        producto.pujar(comprador, precio);
        
        lockRegistro.lock();  // Bloquea mientras se edita el registro
        try {
            // Añade al registro la operación realizada
            registro.add("PUJA : "+comprador.getName()+" | "+producto.toString());
        } finally {
            lockRegistro.unlock();
        }
    }
}
