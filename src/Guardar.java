import java.io.*;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.IllegalModeException;
import javax.microedition.lcdui.*;

/**
 * 
 * @author CATALINA
 *
 */
public class Guardar
        implements CommandListener {

	
    private class SaverThread
            implements Runnable {

    	
        public void run() {
            try {
                FileConnection fc = (FileConnection) Connector.open(System.getProperty("fileconn.dir.photos") + nombreImagen + "." + formato);
                if (!fc.exists()) {
                    fc.create();
                    DataOutputStream dout = new DataOutputStream(fc.openDataOutputStream());
                    dout.write(dato);
                    dout.close();
                    fc.close();
                    mostrarGuardar("Guardar Imagen", nombreImagen + " se ha guardado correctamente en la carpeta Imagenes \"Images\"!");
                }
                else if (fc.exists() && !sobrescribir) {
                    mostrarMensaje("Reemplazar Imagen", nombreImagen + " ya existe.\nDesea reempazarla \"" + nombreImagen + "\"?");
                }
                else {
                    DataOutputStream dout = new DataOutputStream(fc.openDataOutputStream());
                    dout.write(dato);
                    dout.close();
                    fc.close();
                    mostrarGuardar("Reemplazar Imagen", nombreImagen + " fue reemplazado con exito en la carpeta Imagenes \"Images\"!");
                }
            }
            catch (IOException e) {
                //e.printStackTrace();
                if (sobrescribir) {
                    mostrarAlerta("Reemplazar Imagen", nombreImagen + " no puede reemplazarce por " + e.getMessage());
                }
                else {
                    mostrarAlerta("Guardar Imagen", nombreImagen + " no puede guardarse por " + e.getMessage());
                }
            }
            catch (IllegalModeException ime) {
                if (sobrescribir) {
                    mostrarAlerta("Reemplazar Imagen", nombreImagen + " no puede reemplazarse por " + ime.getMessage());
                }
                else {
                    mostrarAlerta("Guardar Imagen", nombreImagen + " no puede guardarse por " + ime.getMessage());
                }
            }
            catch (SecurityException se) {
                if (sobrescribir) {
                    mostrarAlerta("Reemplazar Imagen", "Por favor permitir que el movil reemplace " + nombreImagen + " en la carpeta Imagenes \"Images\" y vuelva a intentarlo!");
                }
                else {
                    mostrarAlerta("Guardar Imagen", "Por favor permitir que el movil guarde " + nombreImagen + " en la carpeta Imagenes \"Images\" y vuelva a intentarlo!");
                }
            }
            catch (IllegalArgumentException iae) {
                if (sobrescribir) {
                    mostrarAlerta("Reemplace Imagen", nombreImagen + " no se puede reemplazar por " + iae.getMessage());
                }
                else {
                    mostrarAlerta("Guardar Imagen", nombreImagen + " no se puede guardar por " + iae.getMessage());
                }
            }
            catch (OutOfMemoryError me) {
                if (sobrescribir) {
                    if (me.getMessage().equals("")) {
                        mostrarAlerta("Reemplazar Imagen", nombreImagen + " no se puede reemplazar porque no hay suficiente espacio en la memoria");
                    }
                    else {
                        mostrarAlerta("Remplazar Imagen", nombreImagen + " no se puede reemplazar por " + me.getMessage());
                    }
                }
                else if (me.getMessage().equals("")) {
                    mostrarAlerta("Guardar Imagen", nombreImagen + " no se puede guardar porque no hay suficiente espacio en la memoria");
                }
                else {
                    mostrarAlerta("Guardar Imagen", nombreImagen + " no se puede guardar por " + me.getMessage());
                }
            }
        }

        private SaverThread() {
        }

        SaverThread(SaverThread saverthread) {
            this();
        }
    }

    /**
     * Guarda la imagen que ha sido capturada en el administrador de imagenes que 
     * tiene el dispositivo movil por defecto
     * 
     * @param midlet
     * @param dato
     * @param formato
     * @param cf
     * @param imgf
     */
    public Guardar(Camara midlet, byte dato[], String formato, Displayable cf, Displayable imgf) {
        ok = new Command("OK", 4, 1);
        OK = new Command("OK", 4, 1);
        yes = new Command("Yes", 4, 1);
        no = new Command("No", 2, 1);
        back = new Command("Back", 2, 1);
        this.midlet = midlet;
        this.dato = dato;
        this.formato = formato;
        CamaraForm = cf;
        ImagenForm = imgf;
        mostrarNombreArchivo();
    }

    private void mostrarNombreArchivo() {
        sobrescribir = false;
        mensaje = new TextBox("Por favor introduzca el nombre de la imagen:", "", 15, 0);
        mensaje.addCommand(ok);
        mensaje.addCommand(back);
        mensaje.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(mensaje);
    }
    
	/**
	 *  Obtiene el nombre del archivo de la imagen 
	 * @return
	 */
    public String obtenerNombreArchivo() {
        return nombreImagen;
    }
    
	/**
	 * 
	 * @param titulo
	 * @param texto
	 */
    private void esperandoMostrarFormulario(String titulo, String texto) {
        Form esperando = new Form(titulo);
        Gauge medir = new Gauge(null, false, -1, 2);
        esperando.append(texto);
        esperando.append(medir);
        Display.getDisplay(midlet).setCurrent(esperando);
    }

    /**
     * Muestra que la imagen ha sido gurdada correctamente
     * @param titulo
     * @param texto
     */
    private void mostrarGuardar(String titulo, String texto) {
        Alert guardar = new Alert(titulo, texto, null, AlertType.CONFIRMATION);
        guardar.setTimeout(-2);
        guardar.addCommand(OK);
        guardar.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(guardar);
    }

    /**
     * Muestra el mensaje con la informacion de la imagen guardada
     * @param titulo
     * @param texto
     */
    private void mostrarMensaje(String titulo, String texto) {
        Alert alerta = new Alert(titulo, texto, null, AlertType.INFO);
        alerta.setTimeout(-2);
        alerta.addCommand(yes);
        alerta.addCommand(no);
        alerta.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(alerta);
    }

    /**
     *  Muestra un mensaje de alerta en caso que suceda algun error
     * @param titulo
     * @param texto
     */
    private void mostrarAlerta(String titulo, String texto) {
        Alert alerta = new Alert(titulo, texto, null, AlertType.INFO);
        alerta.setTimeout(-2);
        alerta.addCommand(Alert.DISMISS_COMMAND);
        alerta.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(alerta);
    }

    /**
     * Obtiene el titulo de la imagen guardada
     * @return
     */
    public String obtenerTituloImagen() {
        return obtenerNombreArchivo() + ".jpeg";
    }

    public void commandAction(Command c, Displayable s) {
        if (c == ok) {
            nombreImagen = mensaje.getString();
            esperandoMostrarFormulario("Guardar Imagen", "Guardando " + nombreImagen + " a las Imagenes...");
            (new Thread(new SaverThread(null))).start();
        }
        if (c == back || c == Alert.DISMISS_COMMAND) {
            Display.getDisplay(midlet).setCurrent(CamaraForm);
        }
        if (c == OK) {
            ImagenForm.setTitle(obtenerTituloImagen());
            Display.getDisplay(midlet).setCurrent(ImagenForm);
        }
        if (c == yes) {
            sobrescribir = true;
            esperandoMostrarFormulario("Reemplazar Imagen", "Reemplazando " + nombreImagen + " en Imagenes...");
            (new Thread(new SaverThread(null))).start();
        }
        if (c == no) {
            sobrescribir = false;
            mensaje.setString(nombreImagen);
            Display.getDisplay(midlet).setCurrent(mensaje);
        }
    }
    Camara midlet;
    private TextBox mensaje;
    String nombreImagen;
    String formato;
    byte dato[];
    private boolean sobrescribir;
    private Command ok;
    private Command OK;
    private Command yes;
    private Command no;
    private Command back;
    Displayable CamaraForm;
    Displayable ImagenForm;
}