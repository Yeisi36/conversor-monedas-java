package com.lad.alura.conversormoneda;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Conversor {

    private static final int LIMITE_HISTORIAL = 10;
    private static List<String> historial = new ArrayList<>();

    private static final String[][] MONEDAS = {
            {"USD", "Dólar estadounidense"},
            {"COP", "Peso colombiano"},
            {"ARS", "Peso argentino"},
            {"BRL", "Real brasileño"},
            {"CLP", "Peso chileno"},
            {"BOB", "Boliviano"}
    };

    public static void eleccionUserMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n🌍 CONVERSOR DE MONEDAS 🌍");
            System.out.println("1) Convertir moneda");
            System.out.println("2) Ver historial");
            System.out.println("3) Salir");
            System.out.print("Elige una opción: ");

            int opcion;
            try {
                opcion = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("❌ Debes ingresar un número.");
                scanner.nextLine();
                continue;
            }

            if (opcion == 3) {
                System.out.println("👋 Gracias por usar el conversor.");
                break;
            }

            switch (opcion) {
                case 1 -> convertir(scanner);
                case 2 -> mostrarHistorial();
                default -> System.out.println("❌ Opción inválida.");
            }
        }
    }

    private static void convertir(Scanner scanner) {

        try {
            System.out.println("\nMoneda de ORIGEN:");
            for (int i = 0; i < MONEDAS.length; i++) {
                System.out.println((i + 1) + ") " + MONEDAS[i][1]);
            }
            int origen = scanner.nextInt() - 1;

            System.out.println("\nMoneda de DESTINO:");
            for (int i = 0; i < MONEDAS.length; i++) {
                System.out.println((i + 1) + ") " + MONEDAS[i][1]);
            }
            int destino = scanner.nextInt() - 1;

            System.out.print("Ingrese el valor a convertir: ");
            double valor = scanner.nextDouble();

            String base = MONEDAS[origen][0];
            String target = MONEDAS[destino][0];

            double tasa = obtenerTasa(base, target);
            double resultado = valor * tasa;

            System.out.printf("Resultado: %.2f %s = %.2f %s%n",
                    valor, base, resultado, target);

            registrarConversion(valor, base, resultado, target);

        } catch (Exception e) {
            System.out.println("❌ Error durante la conversión.");
        }
    }

    private static double obtenerTasa(String base, String target) throws Exception {

        String apiKey = "66c4cc9704fb4125108498de";
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + base + "/" + target;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("conversion_rate").getAsDouble();
    }

    private static void registrarConversion(double valor, String base,
                                            double resultado, String target) {

        if (historial.size() == LIMITE_HISTORIAL) {
            historial.remove(0);
        }

        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String registro = String.format("[%s] %.2f %s → %.2f %s",
                ahora.format(formato), valor, base, resultado, target);

        historial.add(registro);
    }

    private static void mostrarHistorial() {

        if (historial.isEmpty()) {
            System.out.println("📭 No hay conversiones registradas.");
            return;
        }

        System.out.println("\n📜 HISTORIAL:");
        for (String r : historial) {
            System.out.println(r);
        }
    }
}



