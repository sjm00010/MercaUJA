package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import ujaen.es.mercauja.Constantes.TipoProducto;

/**
 * Clase runnable que representa al comprador de la subasta
 * @author sjm00010
 */
public class Comprador implements Callable<Resultado> {
    // Variables
    private final String name;
    private final List<TipoProducto> productosDeseados; // Lista de productos deseados
    private int pujaActual;
    private final List<Producto> productosComprados; // Lista de productos deseados
    private int dinero;
    private final Catalogo memoria;
    
    public Comprador(String name, Catalogo memoria) {
        this.name = "COMPRADOR("+name+")";
        this.dinero = Constantes.DINERO;
        this.pujaActual = -1;
        
        // Inicializo la lista de productos deseados
        this.productosDeseados = new ArrayList<>();
        for (int i = 0; i < Constantes.NUM_PRODUCTOS; i++)
            this.productosDeseados.add(TipoProducto.getProducto());
        
        this.productosComprados = new ArrayList<>();
        this.memoria = memoria;
    }
    
    /**
     * Obtiene los productos por los que esta interesado del catálogo actual
     */
    private void buscarProductos(){
        productosDeseados.stream()
                         .forEach(productoDeseado -> {
                            if(pujaActual == -1)
                                pujaActual = memoria.catalogo.indexOf(memoria.catalogo.stream()
                                            .filter(producto -> ( !producto.isVendido() && 
                                                                   productoDeseado == producto.getTipo() && 
                                                                   producto.getPrecioActual() < getDinero()))
                                            .findFirst().orElse(null));
                                }); // Asigna -1 si no hay productos disponibles
    }
    
    /**
     * Función para pujar por un producto
     */
    private void pujar(){
        if(!memoria.catalogo.isEmpty()){
            if(pujaActual == -1)
                buscarProductos();
            
            while(pujaActual != -1 && !memoria.catalogo.get(pujaActual).pujar(this)){ // Para si no hay productos disponibles
                buscarProductos(); // Se debe buscar otro producto
            }
        }else{
            System.out.println( name + " no tiene productos por los que pujar" );
        }
    }
    
    /**
     * Cambia un producto deseado por el producto adquirido
     * @param comprado Producto adquirido
     */
    public void notificarCompra(Producto comprado){
        int indice = productosDeseados.indexOf(comprado.getTipo());
        if(indice != -1){ 
            productosDeseados.remove(indice); // Elimino el producto de la lista de deseados
            productosComprados.add(comprado);
            dinero -= comprado.getPrecioActual();
        }else{ // No deberia de producirse nunca
            System.out.println( name + " ERROR AL NOTIFICAR LA COMPRA");
        }
    }
    
    /**
     * Presenta el informe de operaciones completadas y canceladas
     */
    private void presentaInforme(){
        System.out.println( name + " presenta su informe");
        
        if(productosComprados.size() == Constantes.NUM_PRODUCTOS)
            System.out.println( name + " ha comprado TODOS los productos deseados");
        else if(!productosComprados.isEmpty())
            System.out.println( name + " ha comprado "+ productosComprados.size() + " productos deseados");
        else
            System.out.println( name + " no ha comprado NINGUN producto deseado");
        
        if(!productosDeseados.isEmpty())
            System.out.println( name + " ha cancelado la compra de "+ productosDeseados.size() + " productos deseados");

    }
    
    /**
     * @return Nombre del comprador
     */
    public String getName() {
        return name;
    }

    /**
     * @return Dinero actual
     */
    public int getDinero() {
        return dinero;
    }
    
    @Override
    public Resultado call() {
        System.out.println( name + " inicia su ejecución");

        // Inicia un bucle para pujar por los productos
        try {
            while(!productosDeseados.isEmpty()){
                System.out.println( name + " realiza una puja" );
                pujar();
                System.out.println( name + " termina de pujar, espera hasta la siguiente puja");

                // En este caso realizo una espera ocupada entre pujas para dar tiempo a que el producto cambie de precio
                TimeUnit.MILLISECONDS.sleep(Constantes.ESPERA_COMPRADOR);
            }

            System.out.println( name + " se finaliza su ejecución");
        }catch (InterruptedException ex) {
            System.out.println( name + " se CANCELA su ejecución");
        }

        presentaInforme();
        
        return new Resultado( name, productosComprados);
    }    
}
