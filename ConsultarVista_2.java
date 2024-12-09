/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package nutricionistaVista;


import conexion.Conexion;

import conexion.pacienteData;
import entidades.Paciente;
import java.awt.event.ItemEvent;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTextField;


public class ConsultarVista_2 extends javax.swing.JFrame {

    private java.sql.Connection con = null;
    private Object Inactivo;

    public ConsultarVista_2() {
        initComponents();
        String DATABASE_URL = null;
        
        con = Conexion.getConexion(DATABASE_URL);
        cargarPacientes();
        agregarListeners();
    

    }

    // ComboBox
    private void cargarPacientes() {
        try {
            String sql = "SELECT nombre FROM paciente";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                jComboBox1.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar pacientes: " + e.getMessage());
        }
    }

    // listeners ComboBox y los botones
    private void agregarListeners() {
        // seleccionar un paciente
        jComboBox1.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedPaciente = (String) jComboBox1.getSelectedItem();
                cargarDatosPaciente(selectedPaciente);
            }
        });

        //  botón Guardar
        Guardar.addActionListener(e -> guardarCambiosPaciente());

        //  botón Limpiar
        Limpiar.addActionListener(e -> limpiarCampos());

        //  botón Salir
        Salir.addActionListener(e -> this.dispose());
    }
    private void cargarDatosPaciente(String selectedPaciente) {
    String sql = "SELECT * FROM paciente WHERE nombre = ?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, selectedPaciente);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            jTextField1.setText(rs.getString("nombre"));
            jTextField2.setText(rs.getString("apellido"));
         jTextField9.setText(rs.getString("Edad"));
            jTextField4.setText(rs.getString("altura"));
            jTextField5.setText(rs.getString("pesoActual"));
            jTextField6.setText(rs.getString("pesoBuscado"));

            String genero = rs.getString("sexo");
            Hombre.setSelected("Hombre".equalsIgnoreCase(genero));
            Mujer.setSelected("Mujer".equalsIgnoreCase(genero));

            String condicion = rs.getString("condicionEspecial");
            Vegetariano.setSelected(condicion.contains("Vegetariano"));
            Vegano.setSelected(condicion.contains("Vegano"));
            Celiaco.setSelected(condicion.contains("Celiaco"));
            IntolerantealaLactosa.setSelected(condicion.contains("Intolerante a la Lactosa"));
        } else {
            JOptionPane.showMessageDialog(this, "Paciente no encontrado.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar los datos del paciente: " + e.getMessage());
    }
}
private boolean validarCamposGenerales() {
    // Validar los campos de texto usando un arreglo de pares (campo, nombreCampo)
    Object[][] campos = {
        {jTextField1, "Nombre del paciente"},
        {jTextField2, "Apellido del paciente"},
        {jTextField9, "Edad del paciente"},
        {jTextField4, "Altura del paciente"},
        {jTextField5, "Peso actual del paciente"},
        {jTextField6, "Peso buscado del paciente"}
    };

    for (Object[] campo : campos) {
        JTextField textField = (JTextField) campo[0];
        String nombreCampo = (String) campo[1];

        // Si el campo está vacío, muestra un mensaje de error
        if (textField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El " + nombreCampo + " no está definido o está vacío.");
            return false; // Si algún campo está vacío, no continúa
        }
    }

    // Validar los botones de radio
    if (!(Hombre.isSelected() || Mujer.isSelected())) {
        JOptionPane.showMessageDialog(this, "Debes seleccionar un género.");
        return false;
    }

    return true; // Todos los campos son válidos
}

private void guardarCambiosPaciente() {
    // Consulta SQL para actualizar los datos del paciente
    String sql = "UPDATE paciente SET apellido = ?, edad = ?, altura = ?, pesoActual = ?, pesoBuscado = ?, sexo = ?, condicionEspecial = ?, inactivo = ? WHERE nombre = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        // Asignar los valores de los campos de texto
        ps.setString(1, jTextField2.getText()); // Apellido
        ps.setInt(2, Integer.parseInt(jTextField9.getText())); // Edad
        ps.setDouble(3, Double.parseDouble(jTextField4.getText())); // Altura
        ps.setDouble(4, Double.parseDouble(jTextField5.getText())); // Peso Actual
        ps.setDouble(5, Double.parseDouble(jTextField6.getText())); // Peso Buscado

        // Determinar el género seleccionado
        String genero = Hombre.isSelected() ? "Hombre" : "Mujer";
        ps.setString(6, genero);

        // Construir la cadena de condiciones especiales
        StringBuilder condicion = new StringBuilder();
        if (Vegetariano.isSelected()) condicion.append("Vegetariano, ");
        if (Vegano.isSelected()) condicion.append("Vegano, ");
        if (Celiaco.isSelected()) condicion.append("Celiaco, ");
        if (IntolerantealaLactosa.isSelected()) condicion.append("Intolerante a la Lactosa");
int inactivo = jinactivo.isSelected() ? 1 : 0; // 1 para Inactivo, 0 para Activo
ps.setInt(8, inactivo); 
        // Eliminar la última coma y espacio, si existen
        if (condicion.length() > 0) {
            condicion.setLength(condicion.length() - 2);
        }
        ps.setString(7, condicion.toString());

     

        // Asignar el nombre (campo clave)
        ps.setString(9, jTextField1.getText());

        // Ejecutar la actualización
        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el paciente.");
        }
    } catch (NumberFormatException e) {
        // Manejar errores de conversión de número
        JOptionPane.showMessageDialog(this, "Error en los datos numéricos: " + e.getMessage());
    } catch (SQLException e) {
        // Manejar errores de SQL
        JOptionPane.showMessageDialog(this, "Error al guardar los cambios: " + e.getMessage());
    }
}


    private void limpiarCampos() {
    jTextField1.setText("");
    jTextField2.setText("");
    jTextField9.setText("");
    jTextField4.setText("");
    jTextField5.setText("");
    jTextField6.setText("");

    Hombre.setSelected(false);
    Mujer.setSelected(false);
    Vegetariano.setSelected(false);
    Vegano.setSelected(false);
    Celiaco.setSelected(false);
    IntolerantealaLactosa.setSelected(false);
    jinactivo.setSelected(false);
}

 
 @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        Mujer = new javax.swing.JRadioButton();
        Nombre = new javax.swing.JLabel();
        Apellido = new javax.swing.JLabel();
        ed = new javax.swing.JLabel();
        Altura = new javax.swing.JLabel();
        CM = new javax.swing.JLabel();
        PesoActual = new javax.swing.JLabel();
        KG2 = new javax.swing.JLabel();
        PesoBuscado = new javax.swing.JLabel();
        KG = new javax.swing.JLabel();
        Genero = new javax.swing.JLabel();
        Hombre = new javax.swing.JRadioButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        CondicionAlimenticia = new javax.swing.JLabel();
        Limpiar = new javax.swing.JButton();
        Guardar = new javax.swing.JButton();
        Salir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        IntolerantealaLactosa = new javax.swing.JRadioButton();
        Vegano = new javax.swing.JRadioButton();
        Celiaco = new javax.swing.JRadioButton();
        Vegetariano = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jinactivo = new javax.swing.JRadioButton();
        jTextField9 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "", "", "" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Buscar paciente");

        Mujer.setText("Mujer");
        Mujer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MujerActionPerformed(evt);
            }
        });

        Nombre.setText("Nombre");

        Apellido.setText("Apellido");

        ed.setText("Edad");

        Altura.setText("Altura");

        CM.setText("CM");

        PesoActual.setText("Peso Actual");

        KG2.setText("KG");

        PesoBuscado.setText("Peso Buscado");

        KG.setText("KG");

        Genero.setText("Genero");

        Hombre.setText("Hombre");
        Hombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HombreActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        CondicionAlimenticia.setText("Condicion Alimenticia");

        Limpiar.setText("Eliminar Paciente");
        Limpiar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Limpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LimpiarActionPerformed(evt);
            }
        });

        Guardar.setText("Guardar");
        Guardar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarActionPerformed(evt);
            }
        });

        Salir.setText("Salir");
        Salir.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel2.setBackground(new java.awt.Color(229, 235, 238));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        IntolerantealaLactosa.setText("Intolerante a la lactosa");
        IntolerantealaLactosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IntolerantealaLactosaActionPerformed(evt);
            }
        });

        Vegano.setText("Vegano");

        Celiaco.setText("Celiaco");

        Vegetariano.setText("Vegetariano");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Celiaco, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Vegetariano, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Vegano, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(IntolerantealaLactosa, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IntolerantealaLactosa)
                    .addComponent(Celiaco))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Vegano)
                    .addComponent(Vegetariano))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jLabel13.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel13.setText("Consultar Datos del Paciente");

        jinactivo.setText("Inactivo");
        jinactivo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jinactivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jinactivoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(Hombre, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Mujer, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(PesoBuscado, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(KG, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Altura, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(30, 30, 30)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(CM, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Genero, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(PesoActual)
                                    .addComponent(ed, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(KG2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(CondicionAlimenticia, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(Salir, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jinactivo, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(231, 231, 231)))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(Limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jinactivo)
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Apellido)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Nombre)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CM, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ed)
                        .addComponent(Altura)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PesoActual)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(KG2)
                    .addComponent(PesoBuscado)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(KG))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Genero)
                    .addComponent(Hombre)
                    .addComponent(Mujer))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(CondicionAlimenticia))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Limpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(Salir, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void HombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HombreActionPerformed
        // TODO add your handling code here:
          if (Hombre.isSelected()) {
        Mujer.setSelected(false);
          }
    }//GEN-LAST:event_HombreActionPerformed

    private void MujerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MujerActionPerformed
        // TODO add your handling code here:
         if (Mujer.isSelected()) {
        Hombre.setSelected(false);
    }
    }//GEN-LAST:event_MujerActionPerformed

    private void IntolerantealaLactosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IntolerantealaLactosaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IntolerantealaLactosaActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void LimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LimpiarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LimpiarActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed

    }//GEN-LAST:event_GuardarActionPerformed

    private void jinactivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jinactivoActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jinactivoActionPerformed
 public static void main(String args[]) {
          java.awt.EventQueue.invokeLater(() -> {
            new ConsultarVista_2().setVisible(true);
        });
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Altura;
    private javax.swing.JLabel Apellido;
    private javax.swing.JLabel CM;
    private javax.swing.JRadioButton Celiaco;
    private javax.swing.JLabel CondicionAlimenticia;
    private javax.swing.JLabel Genero;
    private javax.swing.JButton Guardar;
    private javax.swing.JRadioButton Hombre;
    private javax.swing.JRadioButton IntolerantealaLactosa;
    private javax.swing.JLabel KG;
    private javax.swing.JLabel KG2;
    private javax.swing.JButton Limpiar;
    private javax.swing.JRadioButton Mujer;
    private javax.swing.JLabel Nombre;
    private javax.swing.JLabel PesoActual;
    private javax.swing.JLabel PesoBuscado;
    private javax.swing.JButton Salir;
    private javax.swing.JRadioButton Vegano;
    private javax.swing.JRadioButton Vegetariano;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JLabel ed;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JRadioButton jinactivo;
    // End of variables declaration//GEN-END:variables

}

   

   
