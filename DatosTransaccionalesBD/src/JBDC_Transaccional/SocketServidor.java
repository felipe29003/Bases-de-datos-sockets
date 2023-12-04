package JBDC_Transaccional;

// Se importan todas las librerias correspondientes al manejo de datos con SQL
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * La clase SocketServidor implementa un Servidor a través de un socket
 * el cual espera la conexión de un Socket Cliente en el puerto 5000. Cuando
 * el Cliente se conecta se muestra la IP desde donde realiza la conexión.
 * */
public class SocketServidor {
    private Socket clienteSocket;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Esperando que un cliente se conecte");

            // Bucle encargado de aceptar las conexiones de los clientes
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clienteSocket.getInetAddress().getHostAddress());

                // Crea un objeto que maneja la conexión del cliente
                SocketServidor servidor = new SocketServidor(clienteSocket);
                servidor.procesarConexion();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recibe el socket cliente y lo asigna al miembro privado clienteSocket
     *
     * @param clienteSocket Es el socket del cliente que se conecta al servidor.
     * */
    public SocketServidor(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    /**
     * Este método se encarga de procesar la conexión con el cliente.
     *
     * En este caso como el Servidor realiza distintas operaciones sobre
     * la base de datos, las cuales son solicitudes hechas por el Cliente,
     * el método permite realizar una u otra acción según lo que pida el Cliente.
     * */
    private void procesarConexion() {
        try {

            // Se establece la conexión con la BD de Oracle a través del driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//localhost:1521/XE", "c##Poli01", "contraseña");

            // Muestra un mensaje por consola que confirma la conexión con éxito
            PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true);
            salida.println("Conexión exitosa a la base de datos desde el servidor");

            // Lee la solicitud del Cliente y ejecuta la operación en la BD
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            String solicitud = entrada.readLine();

            /*
            * Este conjunto de condicionales define la operación que se va a ejecutar
            * sobre la BD. Esto se define segú la solicitud hecha por el Cliente.
            * */
            if ("CONSULTAR_PAIS".equals(solicitud)) {
                consultarPais(con, salida);
            } else if ("CREAR_PAIS".equals(solicitud)) {
                crearPais(con, entrada, salida);
            } else if ("CREAR_CIUDAD".equals(solicitud)) {
                crearCiudad(con, entrada, salida);
            } else if ("CREAR_LOCALIZACION".equals(solicitud)) {
                crearLocalizacion(con, entrada, salida);
            } else if ("CREAR_DEPARTAMENTO".equals(solicitud)) {
                crearDepartamento(con, entrada, salida);
            } else if ("CREAR_CARGO".equals(solicitud)) {
                crearCargo(con, entrada, salida);
            } else if ("CREAR_EMPLEADO".equals(solicitud)) {
                insertarEmpleado(con, entrada, salida);
            } else if ("CONSULTAR_EMPLEADO".equals(solicitud)) {
                consultarEmpleado(con, entrada, salida);
            } else if ("ACTUALIZAR_CIUDAD".equals(solicitud)) {
                actualizarCiudad(con, entrada, salida);
            } else if ("ACTUALIZAR_LOCALIZACION".equals(solicitud)) {
                actualizarLocalizacion(con, entrada, salida);
            } else if ("INSERTAR_HISTORICO".equals(solicitud)) {
                insertarHistorico(con, entrada, salida);
            } else if ("CONSULTAR_HISTORICO".equals(solicitud)) {
                consultarHistorico(con, entrada, salida);
            } else if ("ACTUALIZAR_ESTADO_ACTIVO".equals(solicitud)) {
                actualizarEstadoActivo(con, entrada, salida);
            } else {
                salida.println("Solicitud no válida");
            }

            // Cierra la conexion la BD y el Cliente
            con.close();
            clienteSocket.close();
        } catch (Exception e) {

            // Maneja las excepciones y muestra el mensaje de error
            System.out.println("Error en la conexión: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Consulta sobre la BD, específicamente la tabla Pais y envía el resultado al Cliente
     * a través de consola.
     *
     * @param con Conexión a la base de datos Oracle.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void consultarPais(Connection con, PrintWriter salida) {
        try {

            // Crea el Statement para ejecutar la consulta SQL
            Statement statement = con.createStatement();

            // Ejecuta la consulta SQL para obtener información sobre la tabla Pais.
            ResultSet resultSet = statement.executeQuery("SELECT * FROM PAIS");

            // Envía la información al Cliente. En este caso el ID y el nombre del Pais
            while (resultSet.next()) {
                salida.println("ID: " + resultSet.getInt("pais_ID") + ", Nombre: " + resultSet.getString("nombrePais"));
            }

            // Cierra el Statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al consultar países: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Inserción en la tabla PAIS de la BD en base
     * a la información recibida por parte del Cliente.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     */
    private void crearPais(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Lee el nombre del pais digitado por el Cliente
            String nombrePais = entrada.readLine();

            // Prepara la sentencia SQL para hacer la inserción en la tabla Pais
            String sql = "INSERT INTO PAIS (nombrePais) VALUES (?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nombrePais);

            /*
            * Ejecuta la sentencia y obtiene el número de filas afectadas para
            * enviar la confirmación de que la inserción fue exitosa.
            *
            * En caso de que las filas afectadas sean mayores a 0 se índica que la
            * inserción fue hecha correctamente en la tabla, de lo contario se muestra
            * que hubo un error en el proceso.
            * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("País creado exitosamente");
            } else {
                salida.println("Error al crear el país");
            }

            // Cierra el Statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el país: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Inserción en la tabla CIUDAD de la BD con la
     * información suministrada por el Cliente.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void crearCiudad(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Lee el nombre de la ciudad y ID del pais recibidos por el Cliente
            String nombreCiudad = entrada.readLine();
            int idPais = Integer.parseInt(entrada.readLine());

            // Prepara la sentencia SQL para realizar la inserción en la tabla Ciudad
            String sql = "INSERT INTO CIUDAD (ciud_pais_ID, ciud_nombre) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, idPais);
            statement.setString(2, nombreCiudad);

            /*
            * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
            * para definir si la operación sobre la BD fue exitosa o no
            * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Ciudad creada exitosamente");
            } else {
                salida.println("Error al crear la ciudad");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear la ciudad: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Inserción en la tabla LOCALIZACIONES de la BD con la
     * información recibida por parte del Cliente.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void crearLocalizacion(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Lee el nombre de la Localización digitada por el Cliente y el ID de la ciudad a la que pertenece
            String direccion = entrada.readLine();
            int idCiudad = Integer.parseInt(entrada.readLine());

            // Prepara la sentencia para realizar la inserción en la tabla Localizaciones
            String sql = "INSERT INTO LOCALIZACIONES (Localiza_Direccion, Localiza_ciudad_id) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, direccion);
            statement.setInt(2, idCiudad);

            /*
             * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
             * para definir si la operación sobre la BD fue exitosa o no
             * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Localización creada exitosamente");
            } else {
                salida.println("Error al crear la localización");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear la localización: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Inserción en la tabla DEPARTAMENTO de la BD con la
     * información recibida por parte del Cliente.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void crearDepartamento(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            // Lee el nombre del departamento a insertar y el ID de su localización
            String nombreDepartamento = entrada.readLine();
            int idLocalizacion = Integer.parseInt(entrada.readLine());

            // Prepara la sentencia para insertar la fila en la tabla Departamento
            String sql = "INSERT INTO DEPARTAMENTO (dpto_nombre, ID_dpto_localizacion) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nombreDepartamento);
            statement.setInt(2, idLocalizacion);

            /*
             * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
             * para definir si la operación sobre la BD fue exitosa o no
             * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Departamento creado exitosamente");
            } else {
                salida.println("Error al crear el departamento");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el departamento: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Inserción en la tabla CARGOS de la BD con la
     * información recibida por parte del Cliente.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void crearCargo(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Lee el ID, nombre, sueldo mínimo y máximo del cargo, los datos son recibidos del Cliente.
            String idCargo = entrada.readLine();
            String nombreCargo = entrada.readLine();
            int sueldoMinimo = Integer.parseInt(entrada.readLine());
            int sueldoMaximo = Integer.parseInt(entrada.readLine());

            // Define la sentencia a utilizar dependiendo de si se proporcióno o no un ID para el cargo
            String sql;
            if (idCargo.isEmpty()) {
                sql = "INSERT INTO CARGOS (cargo_id, cargo_nombre, cargo_sueldo_minimo, cargo_sueldo_maximo) VALUES (SEQ_CARGOS.nextval, ?, ?, ?)";
            } else {
                sql = "INSERT INTO CARGOS (cargo_id, cargo_nombre, cargo_sueldo_minimo, cargo_sueldo_maximo) VALUES (?, ?, ?, ?)";
            }

            // Prepara la sentencia para realizar la inserción según que condición se haya cumplido
            PreparedStatement statement = con.prepareStatement(sql);
            if (!idCargo.isEmpty()) {
                statement.setInt(1, Integer.parseInt(idCargo));
            }
            statement.setString(2, nombreCargo);
            statement.setInt(3, sueldoMinimo);
            statement.setInt(4, sueldoMaximo);

            /*
             * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
             * para definir si la operación sobre la BD fue exitosa o no
             * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Cargo creado exitosamente");
            } else {
                salida.println("Error al crear el cargo");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el cargo: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Inserción en la tabla EMPLEADOS de la BD con la
     * información recibida por parte del Cliente.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void insertarEmpleado(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            /*
            * Se leen los datos del empleado digitados por el Cliente, estos son:
            *
            * - ID, primer y segundo nombre, email, fecha de nacimiento, sueldo, comision, ID del gerente,
            * ID del departamento, ID del cargo y estado del empleado.
            * */
            int idEmpleado = Integer.parseInt(entrada.readLine());
            String primerNombre = entrada.readLine();
            String segundoNombre = entrada.readLine();
            String email = entrada.readLine();
            String fechaNacimiento = entrada.readLine();
            int sueldo = Integer.parseInt(entrada.readLine());
            int comision = Integer.parseInt(entrada.readLine());
            int gerenteID = Integer.parseInt(entrada.readLine());
            int departamentoID = Integer.parseInt(entrada.readLine());
            int cargoID = Integer.parseInt(entrada.readLine());
            String Activo = entrada.readLine();

            // Prepara la sentencia SQL para insertar los valores suministrados en la tabla Empleados
            String sql = "INSERT INTO EMPLEADOS (empl_ID, empl_primer_nombre, empl_segundo_nombre, empl_email, empl_fecha_nac, empl_sueldo, empl_comision, empl_gerente_ID, empl_dpto_ID, empl_cargo_ID,Activo) " +
                    "VALUES (?, ?, ?, ?, TO_DATE(?,'DD-MM-YYYY'), ?, ?, ?, ?, ?,?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, idEmpleado);
            statement.setString(2, primerNombre);
            statement.setString(3, segundoNombre);
            statement.setString(4, email);
            statement.setString(5, fechaNacimiento);
            statement.setInt(6, sueldo);
            statement.setInt(7, comision);
            statement.setInt(8, gerenteID);
            statement.setInt(9, departamentoID);
            statement.setInt(10, cargoID);
            statement.setString(11, Activo);

            /*
             * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
             * para definir si la operación sobre la BD fue exitosa o no
             * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Empleado creado exitosamente");
            } else {
                salida.println("Error al crear el empleado");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el empleado: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Consulta sobre la tabla Empleados de la BD.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void consultarEmpleado(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Se lee el ID del empleado sobre el cual se quiere consultar la informacion.
            int idEmpleado = Integer.parseInt(entrada.readLine());

            // Prepara la sentencia SQL para seleccionar todos los datos correspondientes a dicho empleado
            String sql = "SELECT E.*, L.Localiza_Direccion, C.ciud_nombre, D.dpto_nombre, CG.cargo_nombre " +
                    "FROM EMPLEADOS E " +
                    "JOIN DEPARTAMENTO D ON E.empl_dpto_ID = D.dptoID " +
                    "JOIN LOCALIZACIONES L ON D.ID_dpto_localizacion = L.Localiza_ciudad_id " +
                    "JOIN CIUDAD C ON L.Localiza_ciudad_id = C.ciudID " +
                    "JOIN CARGOS CG ON E.empl_cargo_ID = CG.cargo_id " +
                    "WHERE E.empl_ID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, idEmpleado);

            // Ejecuta la sentencia y obtiene el resultado
            ResultSet resultSet = statement.executeQuery();

            // Le envía al usuario el resultado de la consulta
            while (resultSet.next()) {
                salida.println("ID Empleado: " + resultSet.getInt("empl_ID"));
                salida.println("Primer Nombre: " + resultSet.getString("empl_primer_nombre"));
                salida.println("Segundo Nombre: " + resultSet.getString("empl_segundo_nombre"));
                salida.println("Email: " + resultSet.getString("empl_email"));
                salida.println("Fecha de Nacimiento: " + resultSet.getString("empl_fecha_nac"));
                salida.println("Sueldo: " + resultSet.getInt("empl_sueldo"));
                salida.println("Comisión: " + resultSet.getInt("empl_comision"));
                salida.println("ID Gerente: " + resultSet.getInt("empl_gerente_ID"));
                salida.println("ID Departamento: " + resultSet.getInt("empl_dpto_ID"));
                salida.println("ID Cargo: " + resultSet.getInt("empl_cargo_ID"));
                salida.println("Dirección: " + resultSet.getString("Localiza_Direccion"));
                salida.println("Ciudad: " + resultSet.getString("ciud_nombre"));
                salida.println("Departamento: " + resultSet.getString("dpto_nombre"));
                salida.println("Cargo: " + resultSet.getString("cargo_nombre"));
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al consultar empleado: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Actualización sobre los datos de la tabla Ciudad de la BD.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void actualizarCiudad(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Lee el ID de la ciudad a reemplazar y el nuevo nombre que va a tener.
            int idCiudad = Integer.parseInt(entrada.readLine());
            String nuevoNombreCiudad = entrada.readLine();

            // Prepara la sentencia para actualizar el valor del campo de la tabla Ciudad
            String sql = "UPDATE CIUDAD SET ciud_nombre = ? WHERE ciudID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nuevoNombreCiudad);
            statement.setInt(2, idCiudad);

            /*
             * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
             * para definir si la operación sobre la BD fue exitosa o no
             * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Ciudad actualizada exitosamente");
            } else {
                salida.println("Error al actualizar la ciudad. Verifica que el ID de la ciudad sea válido.");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al actualizar la ciudad: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Actualización sobre los datos de la tabla Localizaciones de la BD.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void actualizarLocalizacion(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Lee el ID de la localización a reemplazar y la nueva dirección que tendrá.
            int idLocalizacion = Integer.parseInt(entrada.readLine());
            String nuevaDireccion = entrada.readLine();

            // Prepara la sentencia SQL para realizar la actualización en la tabla Localizaciones
            String sql = "UPDATE LOCALIZACIONES SET Localiza_Direccion = ? WHERE localiz_ID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nuevaDireccion);
            statement.setInt(2, idLocalizacion);

            /*
             * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
             * para definir si la operación sobre la BD fue exitosa o no
             * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Localización actualizada exitosamente");
            } else {
                salida.println("Error al actualizar la localización. Verifica que el ID de la localización sea válido.");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al actualizar la localización: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Inserción en la tabla Histórico de la BD en base a los datos suministrados por el Cliente.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void insertarHistorico(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            /*
            * Lee el ID del empleado que se va retirar para agregarlo al histórico junto con el ID del cargo, el ID
            * del departamento y la fecha de retiro.
            * */
            int idEmpleado = Integer.parseInt(entrada.readLine());
            String fechaRetiro = entrada.readLine();
            int idCargo = Integer.parseInt(entrada.readLine());
            int idDepartamento = Integer.parseInt(entrada.readLine());

            // Prepara la sentencia para insertar los datos en la tabla Historico
            String sql = "INSERT INTO HISTORICO (emphist_empl_ID, emphist_fecha_retiro, emphist_cargo_ID, emphist_dpto_ID) " +
                    "VALUES (?, TO_DATE(?,'DD-MM-YYYY'), ?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, idEmpleado);
            statement.setString(2, fechaRetiro);
            statement.setInt(3, idCargo);
            statement.setInt(4, idDepartamento);

            /*
             * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
             * para definir si la operación sobre la BD fue exitosa o no
             * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Registro histórico creado exitosamente");
            } else {
                salida.println("Error al crear el registro histórico");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el registro histórico: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Consulta sobre la tabla Historico de la BD.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void consultarHistorico(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Lee el ID del empleado perteneciente a la tabla de Historico
            int idEmpleado = Integer.parseInt(entrada.readLine());

            // Prepara la sentecia SQL con los datos de las tablas Historico, Empleado, Departamento y Cargos
            String sql = "SELECT " +
                    "e.empl_ID, " +
                    "e.empl_primer_nombre, " +
                    "e.empl_segundo_nombre, " +
                    "e.empl_email, " +
                    "d.dpto_nombre AS departamento, " +
                    "c.cargo_nombre AS cargo, " +
                    "e.empl_sueldo, " +
                    "'Inactivo' AS estado, " +
                    "h.emphist_fecha_retiro AS fecha_retiro " +
                    "FROM EMPLEADOS e " +
                    "JOIN HISTORICO h ON e.empl_ID = h.emphist_empl_ID " +
                    "JOIN DEPARTAMENTO d ON e.empl_dpto_ID = d.dptoID " +
                    "JOIN CARGOS c ON e.empl_cargo_ID = c.cargo_id " +
                    "WHERE h.emphist_fecha_retiro IS NOT NULL AND e.empl_ID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, idEmpleado);

            // Ejecuta la sentencia y obtiene el resultado
            ResultSet resultSet = statement.executeQuery();

            // Le muestra al usuario por consola los datos obtenidos de la consulta
            while (resultSet.next()) {
                salida.println("ID Empleado: " + resultSet.getInt("empl_ID"));
                salida.println("Primer Nombre: " + resultSet.getString("empl_primer_nombre"));
                salida.println("Segundo Nombre: " + resultSet.getString("empl_segundo_nombre"));
                salida.println("Email: " + resultSet.getString("empl_email"));
                salida.println("Departamento: " + resultSet.getString("departamento"));
                salida.println("Cargo: " + resultSet.getString("cargo"));
                salida.println("Sueldo: " + resultSet.getInt("empl_sueldo"));
                salida.println("Estado: " + resultSet.getString("estado"));
                salida.println("Fecha de Retiro: " + resultSet.getString("fecha_retiro"));
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al consultar historial: " + e.getMessage());
        }
    }

    /**
     * Permite realizar una Actualización del estado de un empleado en la tabla Empleados de la BD.
     *
     * @param con Conexión con la BD.
     * @param entrada BufferedReader recibe los datos de entrada del Cliente.
     * @param salida PrintWriter envía los datos de salida al Cliente.
     * */
    private void actualizarEstadoActivo(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {

            // Lee el ID del empleado al cual se le quiere actualizar el estado y el nuevo estado que tendrá.
            int idEmpleado = Integer.parseInt(entrada.readLine());
            String nuevoEstado = entrada.readLine();

            // Prepara la sentencia SQL para actualizar el campo de la tabla Empleados
            String sql = "UPDATE EMPLEADOS SET Activo = ? WHERE empl_ID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nuevoEstado);
            statement.setInt(2, idEmpleado);

            /*
             * Se ejecuta la sentencia SQL y se obtiene el número de filas afectadas
             * para definir si la operación sobre la BD fue exitosa o no
             * */
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Estado 'Activo' del empleado actualizado exitosamente");
            } else {
                salida.println("Error al actualizar el estado 'Activo' del empleado. Verifica que el ID del empleado sea válido.");
            }

            // Cierra el statement
            statement.close();
        } catch (Exception e) {
            System.out.println("Error al actualizar el estado 'Activo' del empleado: " + e.getMessage());
        }
    }
}