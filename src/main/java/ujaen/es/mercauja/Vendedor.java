package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Clase runnable que representa al vendedor de la subasta
 * @author sjm00010
 */
public class Vendedor implements Runnable {
    // Variables
    private final String name;
    private final List<Producto> listaProductos;
    private final Mercado mercado; // Acceso al catálogo
    private final CountDownLatch esperaVentas;

    public Vendedor(String name, Mercado mercado) {
        this.name = name;
        this.mercado = mercado;
        
        // Sicronización con la venta de todos los productos
        this.esperaVentas = new CountDownLatch(Constantes.NUM_PRODUCTOS);
        
        // Inicializo la lista de productos a la venta
        this.listaProductos = new ArrayList<>();
        for (int i = 0; i < Constantes.NUM_PRODUCTOS; i++)
            this.listaProductos.add(new Producto(Constantes.TipoProducto.getProducto(), this, esperaVentas));
    }

    /**
     * Añade los productos creados al catalogo del mercado
     */
    private void addProductos(){
        mercado.addProductosCatalogo(listaProductos);
    }
    
    @Override
    public void run() {
        System.out.println("VENDEDOR(" + getName() + ") inicia su ejecución");
        
        // Lo primero que hace el vendedor es añadir los productos que quiere vender
        System.out.println("VENDEDOR(" + getName() + ") añade sus productos al catálogo");
        addProductos();
        
        try {
            // Tras eso, espera a que todos los productos sean vendidos
            System.out.println("VENDEDOR(" + getName() + ") espera la venta sus productos");
            esperaVentas.await();
        } catch (InterruptedException ex) {
            System.out.println("VENDEDOR(" + getName() + ") se CANCELA su ejecución");
        }
        
        System.out.println("VENDEDOR(" + getName() + ") finaliza su ejecución");
    }

    /**
     * @return El nombre del vendedor
     */
    public String getName() {
        return name;
    }
    
}
