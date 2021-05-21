package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import ujaen.es.mercauja.Constantes.TipoProducto;

/**
 * Clase runnable que representa la subasta
 * @author sjm00010
 */
public class Mercado implements Runnable {
    // Variables
    private List<Producto> catalogo;
    private List<String> registro;
    
    // Excusión mutua para la modificación del catalogo
    private final ReentrantLock lock;

    public Mercado() {
        this.catalogo = new ArrayList<>();
        this.registro = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    /**
     * Devuelve un Producto del tipo por el que esta interesado el comprador
     * @param pInteresado Tipo de producto por el que esta interesado
     * @return Indice del producto del tipo interesado, -1 si no hay ninguno
     */
    public Integer getProductoCatalogo(TipoProducto pInteresado) {
        return catalogo.indexOf(catalogo.stream()
                       .filter( (p) -> p.getTipo() == pInteresado )
                       .findFirst()
                       .orElse(null));
    }
    
    /**
     * Permite obtener el precio de un producto
     * @param producto Índice del producto
     * @param tipoEsperado Tipo de producto esperado
     * @return Precio del producto, 0 si ya fue vendido
     */
    public Integer getPrecioProducto(int producto, TipoProducto tipoEsperado){
        int precio = 0;
        if(tipoEsperado == catalogo.get(producto).getTipo()){
            precio = catalogo.get(producto).getPrecioActual();
        }
        return precio;
    }
    
    /**
     * Función para pujar por un producto
     * @param producto Índice del producto por el que se quiere pujar
     * @param newPrecio Nuevo precio de la puja
     * @param comprador Comprador que realiza la puja
     * @param tipoEsperado Tipo de producto esperado
     * @return True si se pujo correctamente, False si el producto ya fue vendido
     */
    public boolean pujaProducto(int producto ,int newPrecio, Comprador comprador, TipoProducto tipoEsperado){
        boolean resultado = tipoEsperado == catalogo.get(producto).getTipo();
        if(resultado){
            catalogo.get(producto).pujar(newPrecio, comprador);
        }
        return resultado;
    }

    /**
     * Añade al catalogo los productos de un nuevo vendedor
     * @param nuevosProductos Lista de nuevos productos a la venta
     */
    public void addProductosCatalogo(List<Producto> nuevosProductos) {
        lock.lock();  // Bloquea mientras se edita el catalogo
        try {
            catalogo.addAll(nuevosProductos);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return String con la información del registro concatenada
     */
    public String getRegistro() {
        return registro.stream()
                       .map(entrada -> entrada + " \n ")
                       .reduce(String::concat).orElse("");
    }

    /**
     * Registra una interacción en el mercado
     * @param entrada Entrada que se desea añadir al registro
     */
    public void addRegistro(String entrada) {
        this.registro.add(entrada);
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
