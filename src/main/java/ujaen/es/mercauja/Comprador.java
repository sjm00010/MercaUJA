package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ujaen.es.mercauja.Constantes.TipoProducto;

/**
 * Clase runnable que representa al comprador de la subasta
 * @author sjm00010
 */
public class Comprador implements Runnable {
    // Variables
    private final String name;
    private final List<Producto> productosDeseados; // Lista de productos deseados
    private final int dinero;
    private final Mercado mercado; // Acceso al catálogo
    
    public Comprador(String name, Mercado mercado) {
        this.name = name;
        this.dinero = Constantes.DINERO;
        this.mercado = mercado;
        
        // Inicializo la lista de productos deseados
        this.productosDeseados = new ArrayList<>();
        for (int i = 0; i < Constantes.NUM_PRODUCTOS; i++)
            this.productosDeseados.add(new Producto(TipoProducto.getProducto()));
    }
    
    /**
     * Obtiene los productos por los que esta interesado del catálogo actual
     */
    private void buscarProductos(){
        Set<Integer> indices = new HashSet();
        
        productosDeseados.forEach(productoDeseado -> {
            int posibleProducto = mercado.getProductoCatalogo(productoDeseado.getTipo());
            if (posibleProducto != -1 && !indices.contains(posibleProducto)) {
                productoDeseado.setIndice(posibleProducto);
                indices.add(posibleProducto);
            }
        });
    }
    
    /**
     * Función para pujar por un producto
     */
    private void pujar(){
        for (Producto productoDeseado : productosDeseados) {
            if( productoDeseado.getIndice() != -1 &&
                !productoDeseado.isVendido() &&
                dinero >= productoDeseado.getPrecioActual() ){
                    mercado.pujaProducto(productoDeseado.getIndice(), productoDeseado.getPrecioActual()+1, this, productoDeseado.getTipo());
                    break; // Solo se puja por un producto a la vez
            }
        }
    }
    
    /**
     * Cambia un producto deseado por el producto adquirido
     * @param comprado Producto adquirido
     */
    public void notificarCompra(Producto comprado){
        for (Producto productoDeseado : getProductosDeseados()) {
            if(productoDeseado.getTipo() == comprado.getTipo() && 
                    !productoDeseado.isVendido()){
                productoDeseado.copiaProducto(comprado);
                // Necesito romper el bucle for, ya que un comprador puede tener más de un producto deseado del mismo tipo
                break;
            }
        }
    }

    @Override
    public void run() {
        System.out.println("COMPRADOR(" + getName() + ") inicia su ejecución");
        
        // Lo primero que hace el comprador es buscar productos deseados en el catalago
        System.out.println("COMPRADOR(" + getName() + ") buscar productos en el catálogo");
        buscarProductos();
        
        
        
        System.out.println("COMPRADOR(" + getName() + ") finaliza su ejecución");
    }

    /**
     * @return Nombre del comprador
     */
    public String getName() {
        return name;
    }

    /**
     * @return Lista de productos deseados
     */
    public List<Producto> getProductosDeseados() {
        return productosDeseados;
    }

    /**
     * @return Dinero actual del comprador
     */
    public int getDinero() {
        return dinero;
    }
    
}
