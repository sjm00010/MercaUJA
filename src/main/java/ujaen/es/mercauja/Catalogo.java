package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que simula el catálogo
 * @author sjm00010
 */
public class Catalogo {
    public List<Producto> catalogo;
    
    // Excusión mutua
    private final ReentrantLock lock;    

    public Catalogo() {
        this.catalogo = new ArrayList<>();
        this.lock = new ReentrantLock();
    }
    
    /**
     * Función para añadir nuevos productos al catálogo
     * @param newProducto Producto a añadir
     */
    public void addProducto(Producto newProducto){
        lock.lock();  // Bloquea mientras se edita el catalogo
        try {
            catalogo.add(newProducto);
        } finally {
            lock.unlock();
        }
    }
}
