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
    private final TipoProducto tipo;
    private int precioActual;
    private Comprador comprador;
    private final Vendedor vendedor;
    private boolean vendido;
    private final long tiempo;
    
    // Excusión mutua para el precio
    private final ReentrantLock lock;
    
    // Sinconización con Vendedor
    private final CountDownLatch esperaVentas;
    
    /**
     * Constructor de producto para vendedor
     * @param tipo Tipo de producto a crear
     * @param vendedor Vendedor que oferta el producto
     * @param esperaVentas CountDownLatch para la sinconización con Vendedor
     */
    public Producto( TipoProducto tipo, Vendedor vendedor, CountDownLatch esperaVentas) {
        this.tipo = tipo;
        this.precioActual = tipo.getPrecio(tipo);
        this.comprador = null;
        this.vendedor = vendedor;
        this.vendido = false;
        this.lock = new ReentrantLock();
        this.esperaVentas = esperaVentas;
        this.tiempo = tipo.getTiempo();
    }
    
    /**
     * @return Tipo del producto
     */
    public TipoProducto getTipo() {
        return tipo;
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
     * @param comprador Comprador que realiza la puja
     * @param precio Nuevo precio del producto
     * @return True si la puja se realizo correctamente, False en caso contrario
     */
    public boolean pujar(Comprador comprador, int precio) {
        boolean Ok = false;
        lock.lock();  // Bloquea mientras se edita el precio
        try {
            if( this.comprador.getName().equals(comprador.getName()) )
                Ok = true;
            else if(comprador.getDinero() >= precio && precio > precioActual){
                precioActual = precio;
                this.comprador = comprador;
                Ok = true;
            }
        } finally {
            lock.unlock();
        }
        return Ok;
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
        if(comprador != null){
            lock.lock();  // Bloquea mientras se edita el precio
            try {
                this.vendido = true;
                if(this.comprador != null)
                    this.comprador.notificarCompra(this);
            } finally {
                if(esperaVentas != null) 
                    esperaVentas.countDown();
                lock.unlock();
            }
        }
    }

    /**
     * @return Último comprador que pujo
     */
    public Comprador getComprador() {
        return comprador;
    }

    /**
     * @return Vendedor del producto
     */
    public Vendedor getVendedor() {
        return vendedor;
    }

    /**
     * @return Tiempo de puja
     */
    public long getTiempo() {
        return tiempo;
    }
    
    @Override
    public String toString(){
        return "PRODUCTO DEL "+getVendedor().getName()+" : "+precioActual+" € | "+ (vendido ? "VENDIDO" : "CANCELADO");
    }
}
