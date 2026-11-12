package com.example.onemoredish.net;

public final class Config {
    private Config() {}

    /**
     * URL base de tu servidor, SIEMPRE terminada en "/".
     *
     * - Emulador de Android Studio + Tomcat en tu PC:  http://10.0.2.2:8080/one_more_dish-1.0-SNAPSHOT/
     *   (10.0.2.2 es el "localhost" de tu PC visto desde el emulador; el último tramo es el
     *    nombre con el que Tomcat/NetBeans despliega tu .war)
     * - Celular real en la misma red Wi-Fi:           http://IP-DE-TU-PC:8080/one_more_dish-1.0-SNAPSHOT/
     * - Azure (producción, recomendado):              https://TU-APP.azurewebsites.net/
     */
    public static final String BASE_URL = "http://10.0.2.2:8080/one_more_dish-1.0-SNAPSHOT/";
    /** true = entra sin servidor (solo para probar pantallas). Ponlo en false cuando conectes los servlets. */
    public static final boolean MODO_DEMO = true;
}