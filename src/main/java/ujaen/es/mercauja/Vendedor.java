package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Clase runnable que representa al vendedor de la subasta
 * @author sjm00010
 */
public class Vendedor implements Callable<Resultado> {
    // Variables
    private final String name;
    private final List<Producto> listaProductos;
    private final CountDownLatch esperaVentas;
    private final Catalogo memoria;

    public Vendedor(String name, Catalogo memoria) {
        this.name = "VENDEDOR("+name+")";
        
        // Sicronización con la venta de todos los productos
        this.esperaVentas = new CountDownLatch(Constantes.NUM_PRODUCTOS);
        
        // Inicializo la lista de productos a la venta
        this.listaProductos = new ArrayList<>();
        for (int i = 0; i < Constantes.NUM_PRODUCTOS; i++)
            this.listaProductos.add(new Producto(Constantes.TipoProducto.getProducto(), esperaVentas));
        
        this.memoria = memoria;
    }

    /**
     * Añade los productos creados al catalogo del mercado
     */
    private void addProductos(){
        listaProductos.forEach(producto -> {
            memoria.addProducto(producto);
            producto.setPuestoEnVenta();
        });
    }
    
    /**
     * Presenta el informe de operaciones completadas y canceladas
     */
    private void presentaInforme(){
        System.out.println( name + " presenta su informe");
        
        int vendidos = 0, cancelados = 0;
        for (Producto producto : listaProductos) {
            if(!producto.isVendido())
                cancelados++;
            else
                vendidos++;
        }
        
        if(vendidos == Constantes.NUM_PRODUCTOS)
            System.out.println( name + " ha vendido TODOS sus productos");
        else if(vendidos != 0)
            System.out.println( name + " ha vendido "+ vendidos + " productos");
        else
            System.out.println( name + " no ha vendido NINGUN producto");
        
        if(cancelados != 0)
            System.out.println( name + " ha cancelado la venta de "+ cancelados + " productos");

    }
    
    @Override
    public Resultado call() {
        System.out.println( name + " inicia su ejecución");
        
        // Lo primero que hace el vendedor es añadir los productos que quiere vender
        System.out.println( name + " añade sus productos al catálogo");
        addProductos();
        
        try {
            // Tras eso, espera a que todos los productos sean vendidos
            System.out.println( name + " espera la venta sus productos");
            esperaVentas.await();
            
            System.out.println( name + " finaliza su ejecución");
        } catch (InterruptedException ex) {
            System.out.println( name + " se CANCELA su ejecución");
        }finally{
            presentaInforme();
        }
        
        return new Resultado( name, listaProductos);
    }
    
}
