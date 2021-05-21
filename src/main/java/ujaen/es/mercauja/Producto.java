package ujaen.es.mercauja;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import ujaen.es.mercauja.Constantes.TipoProducto;

/**
 * Clase que representa los productos que se venden en el mercado
 * @author sjm00010
 */
public class Producto {
    // Variables
    private TipoProducto tipo;
    private int indice; // Para el comprador
    private int precioActual;
    private Vendedor vendedor;
    private Comprador comprador;
    private boolean vendido;
    
    // Excusión mutua para el precio
    private final ReentrantLock lock;
    
    // Sinconización con Vendedor
    private final CountDownLatch esperaVentas;
    
    
    /**
     * Constructor de producto para vendedor
     * @param tipo Tipo de producto a crear
     * @param vendedor Nombre del vendedor
     * @param esperaVentas CountDownLatch para la sinconización con Vendedor
     */
    public Producto( TipoProducto tipo, Vendedor vendedor, CountDownLatch esperaVentas) {
        this.tipo = tipo;
        this.indice = -1;
        this.precioActual = tipo.getPrecio(tipo);
        this.vendedor = vendedor;
        this.comprador = null;
        this.vendido = false;
        this.lock = new ReentrantLock();
        this.esperaVentas = esperaVentas;
    }

    /**
     * Constructor de producto para comprador
     * @param tipo Tipo de producto a crear
     */
    public Producto(TipoProducto tipo) {
        this.tipo = tipo;
        this.indice = -1;
        this.vendedor = null;
        this.lock = null;
        this.esperaVentas = null;
    }

    /**
     * Copia la información del producto original
     * @param original Producto del que se va a copiar la información
     */
    public void copiaProducto(Producto original){
        this.tipo = original.tipo;
        this.indice = original.indice;
        this.precioActual = original.precioActual;
        this.vendedor = original.vendedor;
        this.comprador = original.comprador;
        this.vendido = original.vendido;
    }
    
    /**
     * @return Tipo del producto
     */
    public TipoProducto getTipo() {
        return tipo;
    }

    /**
     * @return Índice del producto original que se desea comprar
     */
    public int getIndice() {
        return indice;
    }
    
    /**
     * Establece el índice del producto original que se desea comprar
     * @param indice Índice del producto original que se desea comprar
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return Precio de la puja mas alta del producto
     */
    public int getPrecioActual() {
        lock.lock();  // Bloquea mientras se edita el precio
        try {
            return precioActual;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Establece el precio actual para el producto en función de la puja más alta
     * @param precioActual Valor de la puja mas alta actual
     * @param comprador Comprador que realiza la puja
     */
    public void pujar(int precioActual, Comprador comprador) {
        lock.lock();  // Bloquea mientras se edita el precio
        try {
            this.precioActual = precioActual;
            this.comprador = comprador;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return Indica si el producto ha sido vendido o no
     */
    public boolean isVendido() {
        lock.lock();  // Bloquea mientras se edita el precio
        try {
            return vendido;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Establece el producto como vendido
     */
    public void setVendido() {
        lock.lock();  // Bloquea mientras se edita el precio
        try {
            this.vendido = true;
        } finally {
            if(esperaVentas != null) 
                esperaVentas.countDown();
            lock.unlock();
        }
    }
    
}
