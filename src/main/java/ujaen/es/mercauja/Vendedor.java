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
    private final CountDownLatch esperaVentas;
    private final Mercado mercado;

    public Vendedor(String name, Mercado mercado) {
        this.name = "VENDEDOR("+name+")";
        
        // Sicronización con la venta de todos los productos
        this.esperaVentas = new CountDownLatch(Constantes.NUM_PRODUCTOS);
        
        // Inicializo la lista de productos a la venta
        this.listaProductos = new ArrayList<>();
        for (int i = 0; i < Constantes.NUM_PRODUCTOS; i++)
            this.listaProductos.add(new Producto(Constantes.TipoProducto.getProducto(), this, esperaVentas));
        
        this.mercado = mercado;
    }

    /**
     * Añade los productos creados al catalogo del mercado
     */
    private void addProductos(){
        listaProductos.forEach(producto -> {
            mercado.addProducto(producto);
        });
    }
    
    /**
     * Presenta el informe de operaciones completadas y canceladas
     */
    private void presentaInforme(){
        System.out.println(name + " : INFORME");
        
        int vendidos = 0, cancelados = 0, ganancia = 0;
        for (Producto producto : listaProductos) {
            if(!producto.isVendido())
                cancelados++;
            else{
                vendidos++;
                ganancia += producto.getPrecioActual();
            }
        }
        
        if(vendidos == 0)
            System.out.println(name + " PRODUCTOS VENDIDOS : NADA | GANANCIAS : "+ganancia+" €");
        else
            System.out.println(name + " PRODUCTOS VENDIDOS : "+ (vendidos == Constantes.NUM_PRODUCTOS ? "TODOS" : vendidos) +" | GANANCIAS : "+ganancia+" €");
        
        if(cancelados != 0)
            System.out.println(name + " PRODUCTOS CANCELADOS : "+ (cancelados == Constantes.NUM_PRODUCTOS ? "TODOS" : cancelados));
    }
    
    @Override
    public void run() {
        System.out.println(getName() + " inicia su ejecución");
        
        // Lo primero que hace el vendedor es añadir los productos que quiere vender
        System.out.println(getName() + " añade sus productos al catálogo");
        addProductos();
        
        try {
            /**
             * Si hubiera varios mercados en este punto debería realizarse una 
             * acción para maximizar la ganancia del vendedor
             **/
                    
            // Espera a que todos los productos sean vendidos
            System.out.println(getName() + " espera la venta sus productos");
            esperaVentas.await();
            
            System.out.println(getName() + " finaliza su ejecución");
        } catch (InterruptedException ex) {
            System.out.println(getName() + " se CANCELA su ejecución");
        }finally{
            presentaInforme();
        }
    }
    
    /**
     * @return Nombre del vendedor
     */
    public String getName() {
        return name;
    }
}
