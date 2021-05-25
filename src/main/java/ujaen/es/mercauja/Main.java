package ujaen.es.mercauja;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Hilo principal
 * @author sjm00010
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Variables
        ExecutorService ejecutor;      
        ExecutorService ejecutorCompletionService;
        CompletionService<Resultado> service;
        Catalogo catalogo;

        // Ejecución del hilo principal
        System.out.println("Hilo(PRINCIPAL) comienza la ejecución");
        
        // Inicialización de las variables para la prueba
        ejecutor = Executors.newCachedThreadPool();
        ejecutorCompletionService = Executors.newCachedThreadPool();        
        service = new ExecutorCompletionService<Resultado>(ejecutorCompletionService);
        catalogo = new Catalogo();
        
        // Creo los vendedores
        for(int i = 0; i < Constantes.NUM_VENDEDORES; i++) {
            Vendedor vendedor = new Vendedor(Integer.toString(i), catalogo);
            service.submit(vendedor);
        }
        
        // Creo los vendedores
        for(int i = 0; i < Constantes.NUM_COMPRADORES; i++) {
            Comprador comprador = new Comprador(Integer.toString(i), catalogo);
            service.submit(comprador);
        }
        
        Mercado mercado = new Mercado(ejecutorCompletionService, service, catalogo);
        ejecutor.execute(mercado);
        
        // Espera la finalización
        System.out.println("Hilo(PRINCIPAL) espera a la finalización");
        ejecutor.awaitTermination(Constantes.TIEMPO_SUBASTA, TimeUnit.HOURS);
		
        // Finalización del hilo principal
        System.out.println("Hilo(PRINCIPAL) ha finalizado la ejecución");
    }
}
