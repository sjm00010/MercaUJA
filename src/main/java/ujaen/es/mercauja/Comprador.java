package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import ujaen.es.mercauja.Constantes.TipoProducto;

/**
 * Clase runnable que representa al comprador de la subasta
 * @author sjm00010
 */
public class Comprador implements Runnable {
    // Variables
    private final String name;
    private final List<TipoProducto> productosDeseados; // Lista de productos deseados
    private final List<Producto> productosComprados; // Lista de productos deseados
    private int dinero;
    private final Mercado mercado;
    
    public Comprador(String name, Mercado mercado) {
        this.name = "COMPRADOR("+name+")";
        this.dinero = Constantes.DINERO;
        
        // Inicializo la lista de productos deseados
        this.productosDeseados = new ArrayList<>();
        for (int i = 0; i < Constantes.NUM_PRODUCTOS; i++)
            this.productosDeseados.add(TipoProducto.getProducto());
        
        this.productosComprados = new ArrayList<>();
        this.mercado = mercado;
    }
    
    /**
     * Busca el producto mas barato de entre todos los deseados
     */
    private Producto buscarProductos(){
        List<Producto> posiblesProductos = new ArrayList<>();
        productosDeseados.stream()
                         .distinct()
                         .forEach(productoDeseado -> {
                            int masBarato = 0;
                            List<Producto> productos = mercado.buscarProductos(productoDeseado, dinero);
                            for (int i = 1; i < productos.size(); i++) {
                                if(productos.get(masBarato).getPrecioActual() < productos.get(i).getPrecioActual()){
                                    masBarato = i;
                                }
                            }
                            
                            if(!productos.isEmpty())
                                posiblesProductos.add(productos.get(masBarato));
                        });
        
        int masBarato = 0;
        for (int i = 1; i < posiblesProductos.size(); i++) {
            if(posiblesProductos.get(masBarato).getPrecioActual() < posiblesProductos.get(i).getPrecioActual()){
                masBarato = i;
            }
        }
        
        return !posiblesProductos.isEmpty() ? posiblesProductos.get(masBarato) : null;
    }
    
    /**
     * Función para pujar por un producto
     */
    private void pujar(){
        Producto puja = buscarProductos();
        if(puja != null)
            mercado.pujar(this, puja, puja.getPrecioActual()+1);
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
        }else{ // No deberia de producirse nunca
            System.out.println( name + " ERROR AL NOTIFICAR LA COMPRA");
        }
    }
    
    /**
     * Presenta el informe de operaciones completadas y canceladas
     */
    private void presentaInforme(){
        System.out.println(name + " : INFORME");
        
        int comprados = productosComprados.size(), cancelados = productosDeseados.size();
        
        if(productosComprados.isEmpty())
            System.out.println(name + " PRODUCTOS COMPRADOS : NADA | DINERO RESTANTE : "+Constantes.DINERO+" €");
        else{
            int ganancia = 0;
            ganancia = productosComprados.stream()
                                         .map(producto -> producto.getPrecioActual())
                                         .reduce(ganancia, Integer::sum);
            System.out.println(name + " PRODUCTOS COMPRADOS : "+(comprados == Constantes.NUM_PRODUCTOS ? "TODOS" : comprados)+" | DINERO GASTADO : "+ganancia+" €");
        }
        
        if(cancelados != 0)
            System.out.println(name + " COMPRAS CANCELADAS : "+ (cancelados == Constantes.NUM_PRODUCTOS ? "TODAS" : cancelados));
    }
    
    @Override
    public void run() {
        System.out.println( name + " inicia su ejecución");

        // Inicia un bucle para pujar por los productos
        try {
            System.out.println( name + " va a pujar por productos");
            while(!productosDeseados.isEmpty()){
                pujar();

                // En este caso realizo una espera ocupada entre pujas para dar tiempo a que el producto cambie de precio
                TimeUnit.MILLISECONDS.sleep(Constantes.ESPERA_COMPRADOR);
            }

            System.out.println( name + " se finaliza su ejecución");
        }catch (InterruptedException ex) {
            System.out.println( name + " se CANCELA su ejecución");
        }finally{
            presentaInforme();
        }
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

    /**
     * Función que retira el dinero como deposito al realizar una puja
     * @param deposito Cantidad de dinero pujado
     */
    public void realizarDeposito(int deposito) {
        if(this.dinero >= deposito)
            this.dinero -= deposito;
        else
            System.out.println( name + " ERROR AL REALIZAR UN DEPÓSITO PARA UNA PUJA");
    }
    
    /**
     * Función que devuelve el dinero como deposito de realizar una puja
     * @param deposito Cantidad de dinero devuelto
     */
    public void devolverDeposito(int deposito) {
        this.dinero += deposito;
    }
}
