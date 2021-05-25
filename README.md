# MercaUJA
Mercado de subastas, como Ebay, donde los vendedores publican anuncios del producto que quieren vender y que podrán adquirir los compradores.

## Análisis
A continuación, detallaré como he dedicidido que se comportan cada una de las clases.

### Producto
El producto se compone de:
- *tipo* : tipo de producto, es un enum que puede ser `ALIMENTACION`, `ELECTRONICA`, `ROPA` y `OTROS`;
- *precioActual* : indica el precio del producto, ira variando con las pujas. El precio inicial es un valor aleatorio dependiendo del tipo de producto, siendo el precio minimo 1
- *comprador* : es una referencia al último comprador que realizo la puja mas alta, si no se ha pujado por el producto vale `null`.
- *vendido* : indica si el producto ya esta vendido.
- *puestoEnVenta* : tiempo en milisegundos en el que se inscribió el objeto en el catálogo.
- *tiempo* : tiempo que esta el producto a la venta, valor aleatorio entre 1000 y 3000 ms.

Una vez explicadas las variables que componen un producto pasemos a explicar como funciona un producto. Tras inicializarse se puede establecer el tiempo de puesto en venta, pujar por él (indicando el comprador que realiza la puja) y dar el producto como vendido. Se da por hecho que las pujas van de 1 en 1 sobre el precio actual, es decir, que si el producto vale 100 la puja del nuevo comprador será de 101.

Para obtener el precio actual, pujar, consultar si el producto ha sido vendido y establecer el producto como vendido se utiliza un *ReentrantLock* para garantizar la excusión mutua en esas operaciones.

### Vendedor
El vendedor se compone de:
- *name* : nombre del vendedor.
- *listaProductos* : lista de productos a la venta, se genera de manera aleatoria cuando se crea el vendedor.
- *esperaVentas* : elemento de sincronización (CountDownLatch) con los productos, cada uno de ello tiene una referencia a este elemento.
- *memoria* : acceso al catálogo.

Una vez inicializado, el vendedor añade sus productos al catálogo. Tras esto espera hasta que todos los productos son vendidos (mediante el CountDownLatch) o hasta que es interrumpido. Después presenta el informe sobre de los productos vendidos, y por último devuelve un resultado (nombre del vendedor y su lista de productos).

### Comprador
El comprador se compone de:
- *name* : nombre del comprador.
- *productosDeseados* : lista con los tipos productos que se desea comprar, se genera de manera aleatoria cuando se crea el comprador.
- *pujaActual* : índice del producto del catálogo por el que se está pujando actualmente, -1 si no se está pujando por ningún producto.
- *productosComprados* : lista con los productos comprados, se van incluyendo los productos cuando termina su tiempo de puja y se adquieren.
- *memoria* : acceso al catálogo.

Una vez inicializado, el comprador intenta pujar. Si hay productos en el catálogo, se busca un producto de alguno de los tipos por los que está interesado el comprador y que tenga dinero para pujar. Tras intentar pujar espera un breve instante para que se realicen nuevas pujas, tras lo cual vuelve a intentar pujar. Por último, cuando compra todos los productos que deseaba o lo cancelan, presenta su informe y devuelve un resultado (nombre del comprador y su lista de productos comprados).

### Mercado
El mercado se compone de:
- *registro* : lista de entradas con las operaciones realizadas (venta de los productos).
- *ranking* : lista de resultados.
- *service* y *ejecutor* : CompletionService y ExecutorService donde se ejecutan los procesos de los vendedores y los compradores.
- *memoria* : acceso al catálogo.

Cuando el mercado se inicia obtiene el tiempo de inicio, ya que está activo durante un tiempo, y empieza a comprobar los productos del catálogo para establecerlos como vendidos si ha cumplido su tiempo. Tras el cierre del mercado cancela los procesos restantes (vendedores y compradores), obtiene sus resultados y presenta el ranking de vendedores y compradores.
