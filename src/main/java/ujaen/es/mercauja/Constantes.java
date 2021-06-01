package ujaen.es.mercauja;

import java.util.Random;

/**
 * Interface con las constantes necesarias para la resolución del ejercico
 * @author sjm00010
 */
public interface Constantes {
    // Generador aleatorio
    public static final Random aleatorio = new Random();
    
    // Tipos de productos
    public enum TipoProducto {
        ALIMENTACION(19), ELECTRONICA(299), ROPA(149), OTROS(499);
        
        // Precio del producto
        private final int variacion;

        private TipoProducto(int variacion) {
            this.variacion = variacion;
        }

        /**
         * Nos devuelve un tipo de producto aleatorio
         * @return Tipo de producto generado 
         */
        public static TipoProducto getProducto() {
            int tipoAleatorio = aleatorio.nextInt(TIPOS.length);
            return TIPOS[tipoAleatorio];
        }
        
        /**
         * Nos devuelve un precio aleatorio dependiendo de su tipo
         * @param tipo Tipo de producto del que se desea obtener un precio
         * @return Precio
         */
        public int getPrecio(TipoProducto tipo) {
            // Identifico el producto
            int producto = 0;
            for (int i = 0; i < TIPOS.length; i++)
                if (TIPOS[i].equals(tipo)) 
                    producto = i;
            
           return aleatorio.nextInt(TIPOS[producto].variacion) + PRECIO_MINIMO;
        }
        
        /**
         * Nos devuelve un tiempo aleatorio dependiendo de su tipo
         * @return Precio
         */
        public long getTiempo() {            
           return aleatorio.nextInt(VARIACION_PUJAS) + TIEMPO_PUJAS;
        }
    }
    
    // Tipos de producto
    public static final TipoProducto[] TIPOS = TipoProducto.values();
    
    // Constantes del problema
    public static final int PRECIO_MINIMO = 1;
    public static final int TIEMPO_PUJAS = 1000; // milisegundos
    public static final int VARIACION_PUJAS = 2000; // milisegundos
    public static final int ESPERA_COMPRADOR = 100; // milisegundos
    public static final int TIEMPO_SUBASTA = 50000; // milisegundos
    public static final int NUM_VENDEDORES = 10;
    public static final int NUM_COMPRADORES = 15;
    public static final int NUM_PRODUCTOS = 5;
    public static final int DINERO = 800;
    public static final int NUM_PROCESOS = 100;
}
