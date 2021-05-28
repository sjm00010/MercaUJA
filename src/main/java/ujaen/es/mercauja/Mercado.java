package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import ujaen.es.mercauja.Constantes.TipoProducto;

/**
 * Clase runnable que representa la subasta
 * @author sjm00010
 */
public class Mercado implements Runnable {
    // Variables
    private final List<Producto> catalogo;
    private final List<String> registro;
    
    // Sinconización
    private final CountDownLatch avisaFinalizacion;
    
    // Ejecución de los servicios
    private final ExecutorService ejecutor;
    
    // Exclusión mutua para el catálogo
    private final ReentrantLock lockCatalogo;
    private final ReentrantLock lockRegistro;
    
    public Mercado(CountDownLatch avisaFinalizacion) {
        this.catalogo = new ArrayList<>();
        this.registro = new ArrayList<>();
        this.ejecutor = Executors.newCachedThreadPool();
        this.lockCatalogo = new ReentrantLock();
        this.lockRegistro = new ReentrantLock();
        this.avisaFinalizacion = avisaFinalizacion;
    }
    
    /**
     * Función para añadir nuevos productos al catálogo
     * @param newProducto Producto a añadir
     */
    public void addProducto(Producto newProducto){
        TareaAddCatalogo anadirCatalogo = new TareaAddCatalogo(catalogo, registro, newProducto, lockCatalogo, lockRegistro);
        ejecutor.execute(anadirCatalogo);
    }
    
    /**
     * Busca los productos del catálogo que sean del tipo deseado y se ajusten al precio
     * @param tipoBuscado Tipo de producto buscado
     * @param precioMax Precio maximo del producto (sin incluir)
     * @return Lista de productos que cumplen las condiciones
     */
    public List<Producto> buscarProductos(TipoProducto tipoBuscado, int precioMax){
        return catalogo.stream()
                       .filter( p -> !p.isVendido() && 
                                      p.getTipo().equals(tipoBuscado) &&
                                      p.getPrecioActual() < precioMax)
                       .toList();
    }
    
    /**
     * Función para pujar por un producto del catálogo
     * @param comprador Comprador que realiza la puja
     * @param producto Producto por el que se puja
     * @param precio Precio de la puja
     */
    public void pujar(Comprador comprador, Producto producto, int precio){
        TareaPujar puja = new TareaPujar(registro, comprador, producto, precio, lockRegistro);
        ejecutor.execute(puja);
    }

    /**
     * @return String con la información del registro concatenada
     */
    public String getRegistro() {
        return registro.stream()
                       .map(entrada -> entrada + " \n ")
                       .reduce(String::concat)
                       .orElse("");
    }
    
    /**
     * Funcián que calcula los datos para el ranking de los vendedores
     */
    private void rankingVendedores(){
        Map<String, List<String>> vendedores = new HashMap<>();
        
        catalogo.stream()
                .filter( producto -> producto.isVendido())
                .forEach(producto -> {
                    String nombre = producto.getVendedor().getName();
                    if(!vendedores.containsKey(nombre)){
                        List<String> aux = new ArrayList<>();
                        aux.add("1");// Productos
                        aux.add(Integer.toString(producto.getPrecioActual()));// Productos
                        vendedores.put(nombre, aux);
                    }else{
                        String productos = vendedores.get(nombre).get(0);
                        String dinero = vendedores.get(nombre).get(1);

                        productos = Integer.toString(Integer.parseInt(productos) + 1);
                        dinero = Integer.toString(Integer.parseInt(dinero) + producto.getPrecioActual());

                        vendedores.get(nombre).set(0, productos);
                        vendedores.get(nombre).set(1, dinero);
                    }
        });
        
        List<List<String>> resultado = new ArrayList();
        
        vendedores.forEach((clave, valor) -> {
            List<String> aux = new ArrayList<>();
            aux.add(clave);
            aux.addAll(valor);
            resultado.add(aux);
        });
        
        resultado.sort((o1, o2) -> {
            int sol = 0;
            if(Integer.parseInt(o1.get(1)) != Integer.parseInt(o2.get(1))){
                if(Integer.parseInt(o1.get(1)) > Integer.parseInt(o2.get(1)))
                    sol = -1;
                else
                    sol = 1;
            }else if(Integer.parseInt(o1.get(2)) != Integer.parseInt(o2.get(2))){
                if(Integer.parseInt(o1.get(2)) > Integer.parseInt(o2.get(2)))
                    sol = -1;
                else
                    sol = 1;  
            }
            
            return sol;
        });
        
        muestraRanking("VENDEDORES", resultado);
    }
    
    /**
     * Funcián que calcula los datos para el ranking de los compradores
     */
    private void rankingCompradores(){
        Map<String, List<String>> compradores = new HashMap<>();
        
        catalogo.stream()
                .filter( producto -> producto.isVendido())
                .forEach(producto -> {
                    String nombre = producto.getComprador().getName();
                    if(!compradores.containsKey(nombre)){
                        List<String> aux = new ArrayList<>();
                        aux.add("1");// Productos
                        aux.add(Integer.toString(producto.getPrecioActual()));// Productos
                        compradores.put(nombre, aux);
                    }else{
                        String productos = compradores.get(nombre).get(0);
                        String dinero = compradores.get(nombre).get(1);

                        productos = Integer.toString(Integer.parseInt(productos) + 1);
                        dinero = Integer.toString(Integer.parseInt(dinero) + producto.getPrecioActual());

                        compradores.get(nombre).set(0, productos);
                        compradores.get(nombre).set(1, dinero);
                    }
        });
        
        List<List<String>> resultado = new ArrayList();
        
        compradores.forEach((clave, valor) -> {
            List<String> aux = new ArrayList<>();
            aux.add(clave);
            aux.addAll(valor);
            resultado.add(aux);
        });
        
        resultado.sort((o1, o2) -> {
            int sol = 0;
            if(Integer.parseInt(o1.get(1)) != Integer.parseInt(o2.get(1))){
                if(Integer.parseInt(o1.get(1)) > Integer.parseInt(o2.get(1)))
                    sol = -1;
                else
                    sol = 1;
            }else if(Integer.parseInt(o1.get(2)) != Integer.parseInt(o2.get(2))){
                if(Integer.parseInt(o1.get(2)) > Integer.parseInt(o2.get(2)))
                    sol = -1;
                else
                    sol = 1;  
            }
            
            return sol;
        });
        
        muestraRanking("COMPRADORES", resultado);
    }
    
    /**
     * Función que muestra por pantalla una tabla con el ranking
     * @param cabecera Cabecera del ranking
     * @param resultados Resultados a mostrar
     */
    private void muestraRanking(String cabecera, List<List<String>> resultados){
        String center = "|            %-13s                   |%n";
        String leftAlignFormat = "| %-16s | %-11s | %-9s € |%n";

        System.out.format("+--------------------------------------------+%n");
        System.out.format(center, cabecera);
        System.out.format("+------------------+------------+------------+%n");
        System.out.format("| Nombre           | Productos  | Dinero     |%n");
        System.out.format("+------------------+------------+------------+%n");
        resultados.forEach(resultado -> {
            System.out.format(leftAlignFormat, resultado.get(0), resultado.get(1), resultado.get(2));
        });
        System.out.format("+---------------------------------------------+%n");
    }
        
    @Override
    public void run() {
        // Variables
        CountDownLatch cerrar;
        
        // Ejecución del hilo
        System.out.println("MERCADO comienza la ejecución");
        
        // Inicialización de las variables
        cerrar = new CountDownLatch(1); // Paraesèrar la tarea de cerrar el mercado
        
        try {
            // Creo la tarea de cancelación
            TareaCerrarMercado tareaCancelar = new TareaCerrarMercado(cerrar);
            
            // La añado al ejecutor
            ejecutor.execute(tareaCancelar);
            
            // Espero hasta que la tarea de cancelación avise de que se debe cerrar
            cerrar.await(Constantes.TIEMPO_SUBASTA, TimeUnit.MINUTES);
            
            System.out.println("MERCADO ha CERRADO, va a cancelar las operaciones restantes");
            ejecutor.shutdownNow();
            
            System.out.println("MERCADO va ha presentar los rankins");
            rankingVendedores();
            rankingCompradores();
		
            // Finalización del hilo
            System.out.println("MERCADO ha finalizado la ejecución");
        } catch (InterruptedException ex) {
            System.out.println("MERCADO ha CANCELADO su ejecución");
        }finally{
            avisaFinalizacion.countDown();
        }
    }
}
