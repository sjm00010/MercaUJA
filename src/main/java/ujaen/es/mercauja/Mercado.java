package ujaen.es.mercauja;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Clase runnable que representa la subasta
 * @author sjm00010
 */
public class Mercado implements Runnable {
    // Variables
    private final List<String> registro;
    private final List<Resultado> ranking;
    private final CompletionService<Resultado> service;
    private final ExecutorService ejecutor;
    private final Catalogo memoria;

    public Mercado(ExecutorService ejecutorCompletionService, CompletionService<Resultado> service, Catalogo memoria) {
        this.registro = new ArrayList<>();
        this.ranking = new ArrayList<>();
        this.ejecutor = ejecutorCompletionService;
        this.service = service;
        this.memoria = memoria;
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
     * Registra una interacción en el mercado
     * @param entrada Entrada que se desea añadir al registro
     */
    public void addRegistro(String entrada) {
        this.registro.add(entrada);
    }
    
    /**
     * Obtiene los resultados de los Vendedores y Compradores tras cerrar el mercado
     * @throws InterruptedException Excepción lanzada por la interrupción del hilo
     * @throws ExecutionException Excepción lanzada por la interrupción del hilo
     */
    private void obtenerResultados() throws InterruptedException, ExecutionException {
        Resultado resultado;
        resultado = service.take().get();
        ranking.add(resultado);               
    }
    
    /**
     * Función que presenta el ranking de los vendedores
     */
    private void rankingVendedores(){
        List<Resultado> listaVendedores = 
            ranking.stream()
                   .filter( r -> r.esVendedor())
                   .sorted((o1, o2) -> {
                        int resultado = 0;
                        if(o1.getTotalProductos() == o2.getTotalProductos()){
                            resultado = o1.getDinero() > o2.getDinero() ? -1 : 1;
                        }else{
                            resultado = o1.getTotalProductos() > o2.getTotalProductos() ? -1 : 1;
                        }

                        return resultado;
                    })
                    .toList();
        System.out.println("MERCADO : Ranking de los vendedores");
        for (int i = 0; i < listaVendedores.size(); i++) {
            System.out.println("Posición "+(i+1)+" : "+ listaVendedores.get(i).getNombre() + 
                    " | Productos vendidos: "+ listaVendedores.get(i).getTotalProductos() +
                    " | Dinero ganado: "+ listaVendedores.get(i).getDinero());
        }
    }
    
    /**
     * Función que presenta el ranking de los compradores
     */
    private void rankingCompradores(){
        List<Resultado> listaCompradores = 
            ranking.stream()
                   .filter( r -> !r.esVendedor())
                   .sorted((o1, o2) -> {
                        int resultado = 0;
                        if(o1.getTotalProductos() == o2.getTotalProductos()){
                            resultado = o1.getDinero() > o2.getDinero() ? -1 : 1;
                        }else{
                            resultado = o1.getTotalProductos() > o2.getTotalProductos() ? -1 : 1;
                        }

                        return resultado;
                    })
                    .toList();
        System.out.println("MERCADO : Ranking de los compradores");
        for (int i = 0; i < listaCompradores.size(); i++) {
            System.out.println("Posición "+(i+1)+" : "+ listaCompradores.get(i).getNombre() + 
                    " | Productos comprados: "+ listaCompradores.get(i).getTotalProductos() +
                    " | Dinero gastado: "+ listaCompradores.get(i).getDinero());
        }
    }
    
    @Override
    public void run() {
        // Variables
        long inicio;
        
        // Ejecución del hilo
        System.out.println("MERCADO comienza la ejecución");
        
        // Inicialización de las variables
        inicio = System.currentTimeMillis();
        
        try {
            
            // Inicio el bucle para ir comprobando el estado de los productos del catalogo
            while(System.currentTimeMillis()-inicio < Constantes.TIEMPO_SUBASTA){
                memoria.catalogo.stream()
                                .filter(p -> !p.isVendido())
                                .toList()
                                .forEach(p -> {
                                    if(p.tiempoVencido()){
                                        p.setVendido();
                                        addRegistro("Se ha vendido el producto: "+p.toString());
                                    }
                });
            }
        
            System.out.println("MERCADO ha CERRADO, va a cancelar las operaciones restantes");
            ejecutor.shutdownNow();
                    
            for (int i = 0; i < Constantes.NUM_COMPRADORES + Constantes.NUM_VENDEDORES; i++) {
                obtenerResultados();
            }
            
            System.out.println("MERCADO va ha presentar los rankins");
            rankingVendedores();
            rankingCompradores();
		
            // Finalización del hilo
            System.out.println("MERCADO ha finalizado la ejecución");
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println("MERCADO ha CANCELADO su ejecución");
        }
    }
}
