package server.handlers;

import enums.ServiceMethod;

public class Handlers {

    public static void log(ServiceMethod serviceMethod) {
        System.out.println("Received " + serviceMethod.toString() + " request");
    }

}
