package ujaen.es.mercauja;

import java.util.List;

/**
 * Clase para devolver los resultados de la ejecución de un Vendedor o un Comprador
 * @author sjm00010
 */
public class Resultado{
    private final String nombre;
    private final List<Producto> productos;

    public Resultado(String nombre, List<Producto> productos) {
        this.nombre = nombre;
        this.productos = productos;
    }

    /**
     * @return El nombre del Vendedor/Comprador
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Obtiene el dinero total conseguido/gastado
     * @return Dinero total conseguido/gastado
     */
    public int getDinero(){
        int dinero = 0;
        dinero = productos.stream()
                          .map(producto -> producto.getPrecioActual())
                          .reduce(dinero, Integer::sum);
        return dinero;
    }
    
    /**
     * Obtiene el total de productos comprados/vendidos
     * @return Total de productos comprados/vendidos
     */
    public int getTotalProductos(){
        long total = 0;
        total = productos.stream()
                         .filter(producto -> (producto.isVendido()))
                         .count();
        return Math.toIntExact(total);
    }
    
    /**
     * Función que compara dos elementos
     * @return True si el elemento que llama es mayor, False en caso contrario
     */
    public boolean esVendedor(){
        return nombre.contains("VENDEDOR");
    }


}
