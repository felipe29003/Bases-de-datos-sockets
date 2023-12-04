package JBDC_Transaccional;

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

public class SocketServidor {
    private Socket clienteSocket;


    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Esperando que un cliente se conecte");

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clienteSocket.getInetAddress().getHostAddress());

                SocketServidor servidor = new SocketServidor(clienteSocket);
                servidor.procesarConexion();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketServidor(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    private void procesarConexion() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//localhost:1521/XE", "c##Poli01", "contraseña");

            PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true);
            salida.println("Conexión exitosa a la base de datos desde el servidor");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            String solicitud = entrada.readLine();

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

            con.close();
            clienteSocket.close();
        } catch (Exception e) {
            System.out.println("Error en la conexión: " + e.getMessage());
        }
    }

    private void consultarPais(Connection con, PrintWriter salida) {
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM PAIS");

            while (resultSet.next()) {
                salida.println("ID: " + resultSet.getInt("pais_ID") + ", Nombre: " + resultSet.getString("nombrePais"));
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al consultar países: " + e.getMessage());
        }
    }

    private void crearPais(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            String nombrePais = entrada.readLine();

            String sql = "INSERT INTO PAIS (nombrePais) VALUES (?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nombrePais);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("País creado exitosamente");
            } else {
                salida.println("Error al crear el país");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el país: " + e.getMessage());
        }
    }

    private void crearCiudad(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            String nombreCiudad = entrada.readLine();
            int idPais = Integer.parseInt(entrada.readLine());

            String sql = "INSERT INTO CIUDAD (ciud_pais_ID, ciud_nombre) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, idPais);
            statement.setString(2, nombreCiudad);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Ciudad creada exitosamente");
            } else {
                salida.println("Error al crear la ciudad");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear la ciudad: " + e.getMessage());
        }
    }

    private void crearLocalizacion(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            String direccion = entrada.readLine();
            int idCiudad = Integer.parseInt(entrada.readLine());

            String sql = "INSERT INTO LOCALIZACIONES (Localiza_Direccion, Localiza_ciudad_id) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, direccion);
            statement.setInt(2, idCiudad);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Localización creada exitosamente");
            } else {
                salida.println("Error al crear la localización");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear la localización: " + e.getMessage());
        }
    }

    private void crearDepartamento(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            String nombreDepartamento = entrada.readLine();
            int idLocalizacion = Integer.parseInt(entrada.readLine());

            String sql = "INSERT INTO DEPARTAMENTO (dpto_nombre, ID_dpto_localizacion) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nombreDepartamento);
            statement.setInt(2, idLocalizacion);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Departamento creado exitosamente");
            } else {
                salida.println("Error al crear el departamento");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el departamento: " + e.getMessage());
        }
    }

    private void crearCargo(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            String idCargo = entrada.readLine();
            String nombreCargo = entrada.readLine();
            int sueldoMinimo = Integer.parseInt(entrada.readLine());
            int sueldoMaximo = Integer.parseInt(entrada.readLine());

            String sql;
            if (idCargo.isEmpty()) {
                sql = "INSERT INTO CARGOS (cargo_id, cargo_nombre, cargo_sueldo_minimo, cargo_sueldo_maximo) VALUES (SEQ_CARGOS.nextval, ?, ?, ?)";
            } else {
                sql = "INSERT INTO CARGOS (cargo_id, cargo_nombre, cargo_sueldo_minimo, cargo_sueldo_maximo) VALUES (?, ?, ?, ?)";
            }

            PreparedStatement statement = con.prepareStatement(sql);
            if (!idCargo.isEmpty()) {
                statement.setInt(1, Integer.parseInt(idCargo));
            }
            statement.setString(2, nombreCargo);
            statement.setInt(3, sueldoMinimo);
            statement.setInt(4, sueldoMaximo);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Cargo creado exitosamente");
            } else {
                salida.println("Error al crear el cargo");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el cargo: " + e.getMessage());
        }
    }

    private void insertarEmpleado(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
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
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Empleado creado exitosamente");
            } else {
                salida.println("Error al crear el empleado");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el empleado: " + e.getMessage());
        }
    }

    private void consultarEmpleado(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            int idEmpleado = Integer.parseInt(entrada.readLine());

            String sql = "SELECT E.*, L.Localiza_Direccion, C.ciud_nombre, D.dpto_nombre, CG.cargo_nombre " +
                    "FROM EMPLEADOS E " +
                    "JOIN DEPARTAMENTO D ON E.empl_dpto_ID = D.dptoID " +
                    "JOIN LOCALIZACIONES L ON D.ID_dpto_localizacion = L.Localiza_ciudad_id " +
                    "JOIN CIUDAD C ON L.Localiza_ciudad_id = C.ciudID " +
                    "JOIN CARGOS CG ON E.empl_cargo_ID = CG.cargo_id " +
                    "WHERE E.empl_ID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, idEmpleado);

            ResultSet resultSet = statement.executeQuery();

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

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al consultar empleado: " + e.getMessage());
        }
    }

    private void actualizarCiudad(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            int idCiudad = Integer.parseInt(entrada.readLine());
            String nuevoNombreCiudad = entrada.readLine();

            String sql = "UPDATE CIUDAD SET ciud_nombre = ? WHERE ciudID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nuevoNombreCiudad);
            statement.setInt(2, idCiudad);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Ciudad actualizada exitosamente");
            } else {
                salida.println("Error al actualizar la ciudad. Verifica que el ID de la ciudad sea válido.");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al actualizar la ciudad: " + e.getMessage());
        }
    }

    private void actualizarLocalizacion(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            int idLocalizacion = Integer.parseInt(entrada.readLine());
            String nuevaDireccion = entrada.readLine();

            String sql = "UPDATE LOCALIZACIONES SET Localiza_Direccion = ? WHERE localiz_ID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nuevaDireccion);
            statement.setInt(2, idLocalizacion);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Localización actualizada exitosamente");
            } else {
                salida.println("Error al actualizar la localización. Verifica que el ID de la localización sea válido.");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al actualizar la localización: " + e.getMessage());
        }
    }

    private void insertarHistorico(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            int idEmpleado = Integer.parseInt(entrada.readLine());
            String fechaRetiro = entrada.readLine();
            int idCargo = Integer.parseInt(entrada.readLine());
            int idDepartamento = Integer.parseInt(entrada.readLine());

            String sql = "INSERT INTO HISTORICO (emphist_empl_ID, emphist_fecha_retiro, emphist_cargo_ID, emphist_dpto_ID) " +
                    "VALUES (?, TO_DATE(?,'DD-MM-YYYY'), ?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, idEmpleado);
            statement.setString(2, fechaRetiro);
            statement.setInt(3, idCargo);
            statement.setInt(4, idDepartamento);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Registro histórico creado exitosamente");
            } else {
                salida.println("Error al crear el registro histórico");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al crear el registro histórico: " + e.getMessage());
        }
    }

    private void consultarHistorico(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            int idEmpleado = Integer.parseInt(entrada.readLine());

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

            ResultSet resultSet = statement.executeQuery();

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

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al consultar historial: " + e.getMessage());
        }
    }
    private void actualizarEstadoActivo(Connection con, BufferedReader entrada, PrintWriter salida) {
        try {
            int idEmpleado = Integer.parseInt(entrada.readLine());
            String nuevoEstado = entrada.readLine();

            String sql = "UPDATE EMPLEADOS SET Activo = ? WHERE empl_ID = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, nuevoEstado);
            statement.setInt(2, idEmpleado);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                salida.println("Estado 'Activo' del empleado actualizado exitosamente");
            } else {
                salida.println("Error al actualizar el estado 'Activo' del empleado. Verifica que el ID del empleado sea válido.");
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Error al actualizar el estado 'Activo' del empleado: " + e.getMessage());
        }
    }
}


