package JBDC_Transaccional;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketCliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            String mensaje = entrada.readLine();
            System.out.println("Mensaje del servidor: " + mensaje);

            System.out.println("Seleccione una opción:");
            System.out.println("1. Consultar países");
            System.out.println("2. Crear un nuevo país");
            System.out.println("3. Crear una nueva ciudad");
            System.out.println("4. Crear una nueva localización");
            System.out.println("5. Crear un nuevo departamento");
            System.out.println("6. Crear un nuevo cargo");
            System.out.println("7. Crear un nuevo empleado");
            System.out.println("8. Consultar Empleado");
            System.out.println("9. Actualizar ciudad");
            System.out.println("10. Actualizar localizacion");
            System.out.println("11. Insertar en el historico");
            System.out.println("12. Consultar en el historico");
            System.out.println("13. Actualizar estado 'Activo' de un empleado");

            BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
            String opcion = lector.readLine();

            if ("1".equals(opcion)) {
                salida.println("CONSULTAR_PAIS");

                String resultadoConsulta;
                while ((resultadoConsulta = entrada.readLine()) != null) {
                    System.out.println(resultadoConsulta);
                }
            } else if ("2".equals(opcion)) {
                crearPais(lector, salida, entrada);
            } else if ("3".equals(opcion)) {
                crearCiudad(lector, salida, entrada);
            } else if ("4".equals(opcion)) {
                crearLocalizacion(lector, salida, entrada);
            } else if ("5".equals(opcion)) {
                crearDepartamento(lector, salida, entrada);
            } else if ("6".equals(opcion)) {
                crearCargo(lector, salida, entrada);
            } else if ("7".equals(opcion)) {
                crearEmpleado(lector, salida, entrada);
            } else if ("8".equals(opcion)) {  // Agregar una nueva opción para la consulta de empleado
                consultarEmpleado(lector, salida, entrada);
            } else if ("9".equals(opcion)) {  // Nueva opción para actualizar ciudad
                actualizarCiudad(lector, salida, entrada);
            } else if ("10".equals(opcion)) {  // Nueva opción para actualizar localización
                actualizarLocalizacion(lector, salida, entrada);
            } else if ("11".equals(opcion)) {  // Nueva opción para actualizar localización
                insertarHistorico(lector, salida, entrada);
            } else if ("12".equals(opcion)) {  // Nueva opción para consultar historial
                consultarHistorial(lector, salida, entrada);
            } else if ("13".equals(opcion)) {
                actualizarEstadoActivo(lector, salida, entrada);
            } else {
                System.out.println("Opción no válida");
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void crearPais(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el nombre del nuevo país: ");
        String nombrePais = lector.readLine();

        salida.println("CREAR_PAIS");
        salida.println(nombrePais);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void crearCiudad(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el nombre de la nueva ciudad: ");
        String nombreCiudad = lector.readLine();

        System.out.print("Ingrese el ID del país al que pertenece la ciudad: ");
        int idPais = Integer.parseInt(lector.readLine());

        salida.println("CREAR_CIUDAD");
        salida.println(nombreCiudad);
        salida.println(idPais);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void crearLocalizacion(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese la dirección de la nueva localización: ");
        String direccionLocalizacion = lector.readLine();

        System.out.print("Ingrese el ID de la ciudad de la nueva localización: ");
        int idCiudadLocalizacion = Integer.parseInt(lector.readLine());

        salida.println("CREAR_LOCALIZACION");
        salida.println(direccionLocalizacion);
        salida.println(idCiudadLocalizacion);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void crearDepartamento(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el nombre del nuevo departamento: ");
        String nombreDepartamento = lector.readLine();

        System.out.print("Ingrese el ID de la localización del nuevo departamento: ");
        int idLocalizacionDepartamento = Integer.parseInt(lector.readLine());

        salida.println("CREAR_DEPARTAMENTO");
        salida.println(nombreDepartamento);
        salida.println(idLocalizacionDepartamento);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void crearCargo(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el ID del nuevo cargo (deje vacío para generación automática): ");
        String idCargo = lector.readLine();

        System.out.print("Ingrese el nombre del nuevo cargo: ");
        String nombreCargo = lector.readLine();

        System.out.print("Ingrese el sueldo mínimo del cargo: ");
        int sueldoMinimo = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese el sueldo máximo del cargo: ");
        int sueldoMaximo = Integer.parseInt(lector.readLine());

        salida.println("CREAR_CARGO");
        salida.println(idCargo);
        salida.println(nombreCargo);
        salida.println(sueldoMinimo);
        salida.println(sueldoMaximo);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void crearEmpleado(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el ID del nuevo empleado: ");
        int idEmpleado = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese el primer nombre del nuevo empleado: ");
        String primerNombre = lector.readLine();

        System.out.print("Ingrese el segundo nombre del nuevo empleado: ");
        String segundoNombre = lector.readLine();

        System.out.print("Ingrese el email del nuevo empleado: ");
        String email = lector.readLine();

        System.out.print("Ingrese la fecha de nacimiento del nuevo empleado (en formato DD-MM-YYYY): ");
        String fechaNacimiento = lector.readLine();

        System.out.print("Ingrese el sueldo del nuevo empleado: ");
        int sueldo = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese la comisión del nuevo empleado: ");
        int comision = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese el ID del gerente del nuevo empleado: ");
        int idGerente = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese el ID del departamento del nuevo empleado: ");
        int idDepartamento = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese el ID del cargo del nuevo empleado: ");
        int idCargo = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese si el empleado se encuentra activo o inactivo: ");
        String Activo = lector.readLine();

        salida.println("CREAR_EMPLEADO");
        salida.println(idEmpleado);
        salida.println(primerNombre);
        salida.println(segundoNombre);
        salida.println(email);
        salida.println(fechaNacimiento);
        salida.println(sueldo);
        salida.println(comision);
        salida.println(idGerente);
        salida.println(idDepartamento);
        salida.println(idCargo);
        salida.println(Activo);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void consultarEmpleado(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el ID del empleado a consultar: ");
        int idEmpleado = Integer.parseInt(lector.readLine());

        salida.println("CONSULTAR_EMPLEADO");
        salida.println(idEmpleado);

        String respuesta;
        while ((respuesta = entrada.readLine()) != null) {
            System.out.println(respuesta);
        }
    }

    private static void actualizarCiudad(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el ID de la ciudad a actualizar: ");
        int idCiudad = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese el nuevo nombre de la ciudad: ");
        String nuevoNombreCiudad = lector.readLine();

        salida.println("ACTUALIZAR_CIUDAD");
        salida.println(idCiudad);
        salida.println(nuevoNombreCiudad);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void actualizarLocalizacion(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el ID de la localización a actualizar: ");
        int idLocalizacion = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese la nueva dirección de la localización: ");
        String nuevaDireccion = lector.readLine();

        salida.println("ACTUALIZAR_LOCALIZACION");
        salida.println(idLocalizacion);
        salida.println(nuevaDireccion);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void insertarHistorico(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el ID del empleado: ");
        int idEmpleado = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese la fecha de retiro (en formato DD-MM-YYYY): ");
        String fechaRetiro = lector.readLine();

        System.out.print("Ingrese el ID del cargo: ");
        int idCargo = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese el ID del departamento: ");
        int idDepartamento = Integer.parseInt(lector.readLine());

        salida.println("INSERTAR_HISTORICO");
        salida.println(idEmpleado);
        salida.println(fechaRetiro);
        salida.println(idCargo);
        salida.println(idDepartamento);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }

    private static void consultarHistorial(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el ID del empleado para consultar el historial: ");
        int idEmpleado = Integer.parseInt(lector.readLine());

        salida.println("CONSULTAR_HISTORICO");
        salida.println(idEmpleado);

        String respuesta;
        while ((respuesta = entrada.readLine()) != null) {
            System.out.println(respuesta);
        }
    }
    private static void actualizarEstadoActivo(BufferedReader lector, PrintWriter salida, BufferedReader entrada) throws IOException {
        System.out.print("Ingrese el ID del empleado para actualizar el estado 'Activo': ");
        int idEmpleado = Integer.parseInt(lector.readLine());

        System.out.print("Ingrese el nuevo estado 'Activo' (Activo/Inactivo): ");
        String nuevoEstado = lector.readLine();

        salida.println("ACTUALIZAR_ESTADO_ACTIVO");
        salida.println(idEmpleado);
        salida.println(nuevoEstado);

        String respuesta = entrada.readLine();
        System.out.println(respuesta);
    }
}

