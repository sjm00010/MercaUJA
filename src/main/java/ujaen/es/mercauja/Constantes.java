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
            for (int i = 0; i < 10; i++)
                if (TIPOS[i].equals(tipo)) 
                    producto = i;
            
           return aleatorio.nextInt(TIPOS[producto].variacion) + PRECIO_MINIMO;
        }
    }
    
    // Tipos de producto
    public static final TipoProducto[] TIPOS = TipoProducto.values();
    
    // Constantes del problema
    public static final int PRECIO_MINIMO = 1;
    public static final int TIEMPO_SIMULADO = 1; // segundos
    public static final int TIEMPO_ESPERA = 30; // segundos
    public static final int NUM_VENDEDORES = 20;
    public static final int NUM_COMPRADORES = 25;
    public static final int NUM_PRODUCTOS = 7;
    public static final int DINERO = 700;
}
