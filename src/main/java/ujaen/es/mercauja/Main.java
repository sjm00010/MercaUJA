package ujaen.es.mercauja;

import java.util.concurrent.CountDownLatch;
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
        
        // Sinconización con Vendedor
        CountDownLatch esperaMercado;

        // Ejecución del hilo principal
        System.out.println("Hilo(PRINCIPAL) comienza la ejecución");
        
        // Inicialización de las variables para la prueba
        ejecutor = Executors.newCachedThreadPool();
        esperaMercado = new CountDownLatch(1); // Numero de mercados
        
        // Creo el mercado
        Mercado mercado = new Mercado(esperaMercado);
        ejecutor.execute(mercado);
        
        // Creo los vendedores
        for(int i = 0; i < Constantes.NUM_VENDEDORES; i++) {
            Vendedor vendedor = new Vendedor(Integer.toString(i), mercado);
            ejecutor.submit(vendedor);
        }
        
        // Creo los vendedores
        for(int i = 0; i < Constantes.NUM_COMPRADORES; i++) {
            Comprador comprador = new Comprador(Integer.toString(i), mercado);
            ejecutor.submit(comprador);
        }
        
        
        // Espera la finalización
        System.out.println("Hilo(PRINCIPAL) espera a la finalización del mercado");
        esperaMercado.await();
        
        // Cancela el resto de procesos
        System.out.println("Hilo(PRINCIPAL) cancela el resto de procesos");
        ejecutor.shutdownNow();
		
        // Finalización del hilo principal
        System.out.println("Hilo(PRINCIPAL) ha finalizado la ejecución");
    }
}
