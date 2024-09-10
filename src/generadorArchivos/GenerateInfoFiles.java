package generadorArchivos;

import java.io.*;  // Input/output file management
import java.util.*; 

public class GenerateInfoFiles {

    public static void main(String[] args) {
        try {
            // Create test files
            createSalesManInfoFile();  // Generate information from predefined salesman
            createProductsFile(10);    // Generate information on 10 products
            generateSalesReport();     // Generate the sales report
            System.out.println("Archivos generados exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al generar archivos: " + e.getMessage());  // Shows input/output errors
        }
    }

    // Method to create the file with seller information
    public static void createSalesManInfoFile() throws IOException {
        // Create data folder if it does not exist
        File dataDir = new File("data"); // Directory where files will be saved
        if (!dataDir.exists()) {
            dataDir.mkdir();  // Create the directory if it does not exist
        }

        // Datos de vendedor
        String[][] salesmenData = {
            {"CC", "1115738329", "Carmen Yaneth", "Trujillo Lozada"},
            {"CC", "987654321", "Jeferson Andres", "Carvajal Martinez"},
            {"CC", "112233445", "Jose Gabriel", "Delgado Lopez"},
            {"CC", "556677889", "Duvan Camilo", "Ayala Muñoz"}
        };

        // Create seller information file
        FileWriter infoFileWriter = new FileWriter("data/Vendedores.txt");  // File to store seller data

        // Iterar sobre los datos de vendedores
        for (String[] salesman : salesmenData) {
            String documentType = salesman[0];  // Document type
            String documentNumber = salesman[1];  // Document number
            String name = salesman[2];  // Name
            String surname = salesman[3];  // Last name

            // Write information to the seller file
            infoFileWriter.write(documentType + ";" + documentNumber + ";" + name + ";" + surname + "\n");

            // Create folder for each seller
            String folderName = "data/" + documentType + "_" + documentNumber;  
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdir();  // Create the seller folder if it does not exist
            }

            // Generate random sales for the seller
            createSalesManFile(3, name, documentType, documentNumber, folderName);
        }

        infoFileWriter.close();  
    }

    // Method to generate sales files for each salesperson
    public static void createSalesManFile(int randomSalesCount, String name, String documentType, String documentNumber, String folderName) throws IOException {
        // Create sales file in seller folder
        FileWriter writer = new FileWriter(folderName + "/" + name.replaceAll(" ", "_") + "_sales.txt");
        Random random = new Random();  // 

        // Generate random sales
        for (int i = 0; i < randomSalesCount; i++) {
            int productId = random.nextInt(10) + 1;  // Random product ID between 1 and 10
            int quantity = random.nextInt(10) + 1;  // Random quantity between 1 and 10
            writer.write(productId + ";" + quantity + ";\n");  // Write product ID and quantity to file
        }

        writer.close();  
    }

    // Método para generar un archivo de productos
    public static void createProductsFile(int productsCount) throws IOException {
        FileWriter writer = new FileWriter("data/products.txt");  // Archivo para almacenar información de productos
        Random random = new Random();  // Generador de números aleatorios

        // Method to generate a product file
        for (int i = 1; i <= productsCount; i++) {
            String productName = "Producto" + i;  // Product name
            double price = random.nextDouble() * 100000;  // Random price between 0 and 100000
            writer.write(i + ";" + productName + ";" + String.format("%.2f", price).replace(".", ",") + "\n");  
        }

        writer.close();  
    }

    // Method to generate a sales report for sellers
    public static void generateSalesReport() throws IOException {
        // Read products and their prices from the product archive
        Map<Integer, Double> productPrices = new HashMap<>();  // Store product ID and price
        BufferedReader productReader = new BufferedReader(new FileReader("data/products.txt"));
        String line;
        while ((line = productReader.readLine()) != null) {
            String[] parts = line.split(";");  // 
            int productId = Integer.parseInt(parts[0]);  // Product ID
            String priceString = parts[2].replace(",", ".");  
            double price = Double.parseDouble(priceString);  
            productPrices.put(productId, price);  // Store the price on the map
        }
        productReader.close();  

        // Read sales and calculate total by salesperson
        Map<String, Double> salesBySalesman = new HashMap<>();  
        File dataDir = new File("data");  
        for (File file : dataDir.listFiles()) {
            if (file.isDirectory()) {  
                for (File salesFile : file.listFiles()) {
                    BufferedReader salesReader = new BufferedReader(new FileReader(salesFile));
                    double totalSales = 0.0;  // Total sales for this seller
                    while ((line = salesReader.readLine()) != null) {
                        String[] parts = line.split(";");  
                        int productId = Integer.parseInt(parts[0]);  // ID del producto
                        int quantity = Integer.parseInt(parts[1]);  // Cantidad vendida
                        totalSales += productPrices.get(productId) * quantity;  // Total sales for this seller
                    }
                    salesReader.close();  
                    salesBySalesman.put(file.getName(), totalSales);  // Store total sales on the map
                }
            }
        }

        // Sort sellers by sales total from highest to lowest
        List<Map.Entry<String, Double>> sortedSalesmen = new ArrayList<>(salesBySalesman.entrySet());
        sortedSalesmen.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));  

        // Crear archivo de reporte
        FileWriter reportWriter = new FileWriter("data/ReporteVentas.csv");  // CSV Reporte
        for (Map.Entry<String, Double> entry : sortedSalesmen) {
            reportWriter.write(entry.getKey() + ";" + String.format("%.2f", entry.getValue()) + "\n");  
        }
        reportWriter.close(); 
    }
}
