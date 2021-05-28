package ujaen.es.mercauja;

import static java.lang.Thread.sleep;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Tarea para añadir un producto al catálogo y venderlo despues de un tiempo
 * @author sjm00010
 */
public class TareaAddCatalogo implements Runnable {
    // Variables
    private final List<Producto> catalogo;
    private final List<String> registro;
    private final Producto producto;
    
    // Excusión mutua
    private final ReentrantLock lockCatalogo;
    private final ReentrantLock lockRegistro;
    
    public TareaAddCatalogo(List<Producto> catalogo, List<String> registro, Producto producto, ReentrantLock lockCatalogo, ReentrantLock lockRegistro) {
        this.catalogo = catalogo;
        this.registro = registro;
        this.producto = producto;
        this.lockCatalogo = lockCatalogo;
        this.lockRegistro = lockRegistro;
    }
    
    private void registrarAccion(){
        lockRegistro.lock();  // Bloquea mientras se edita el registro
        try {
            // Añade al registro la operación realizada
            registro.add("AÑADIDO : "+producto.toString());
        } finally {
            lockRegistro.unlock();
        }
    }
    
    private void addProducto(){
        lockCatalogo.lock();  // Bloquea mientras se edita el catalogo
        try {
            // Añade el nuevo producto al catálogo
            catalogo.add(producto);
            
            // Añade la operación al registro
            registrarAccion();
        } finally {
            lockCatalogo.unlock();
        }
    }

    @Override
    public void run() {
        addProducto();
        
        try {
            // Espera a que se agote el tiempo de puja por el producto
            sleep(producto.getTiempo());
            
            // Da el producto como vendido
            producto.setVendido();
        } catch (InterruptedException ex) {
            System.out.println("CANCELADA la unión al catálogo del "+producto.toString());
        }
    }
}
