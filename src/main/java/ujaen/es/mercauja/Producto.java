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
    private boolean vendido;
    private long puestoEnVenta;
    private long tiempo;
    
    // Excusión mutua para el precio
    private final ReentrantLock lock;
    
    // Sinconización con Vendedor
    private final CountDownLatch esperaVentas;
    
    /**
     * Constructor de producto para vendedor
     * @param tipo Tipo de producto a crear
     * @param esperaVentas CountDownLatch para la sinconización con Vendedor
     */
    public Producto( TipoProducto tipo, CountDownLatch esperaVentas) {
        this.tipo = tipo;
        this.precioActual = tipo.getPrecio(tipo);
        this.comprador = null;
        this.vendido = false;
        this.lock = new ReentrantLock();
        this.esperaVentas = esperaVentas;
        this.tiempo = tipo.getTiempo();
    }

    /**
     * Constructor de producto para comprador
     * @param tipo Tipo de producto a crear
     */
    public Producto(TipoProducto tipo) {
        this.tipo = tipo;
        this.lock = null;
        this.esperaVentas = null;
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
     * @return True si la puja se realizo correctamente, False en caso contrario
     */
    public boolean pujar(Comprador comprador) {
        boolean Ok = false;
        lock.lock();  // Bloquea mientras se edita el precio
        try {
            if( equalComprador(comprador))
                Ok = true;
            else if(comprador.getDinero() > precioActual){
                precioActual++;
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
    
    /**
     * Funcion que compara dos compradores para determinar si son el mismo
     * @param comprador Nuevo comprador
     * @return True si son el mismo, false en caso contrario
     */
    public boolean equalComprador(Comprador comprador){
        return  this.comprador != null ? this.comprador.equals(comprador) : false;
    }

    /**
     * Establece el tiempo actual como el tiempo en que se puso a la venta
     */
    public void setPuestoEnVenta() {
        this.puestoEnVenta = System.currentTimeMillis();
    }
    
    public boolean tiempoVencido(){
        return tiempo <= System.currentTimeMillis()-puestoEnVenta;
    }

    /**
     * @return the comprador
     */
    public Comprador getComprador() {
        return comprador;
    }
}
