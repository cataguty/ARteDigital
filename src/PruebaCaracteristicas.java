import java.io.IOException;
import javax.microedition.amms.control.camera.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.VideoControl;

/**
 * 
 * @author CATALINA
 *
 */
public class PruebaCaracteristicas
        implements CommandListener {

	/**
	 * Prueba las caracteristicas de la camara del dispositivo
	 * @param cam
	 */
    public PruebaCaracteristicas(Camara cam) {
        camara = null;
        midlet = cam;
        capturaCmd = new Command("Captura", 4, 0);
        opcionesCmd = new Command("Opciones", 8, 0);
        atrasCmd = new Command("Atras", 2, 1);
        seleccionarCmd = new Command("Seleccionar", 4, 0);
        listaOpciones = new List("Cambiar: ", 3);
        camaraForm = new Form("Prueba de todas las caracteristicas");
        crearFormatoCamara();
    }

   public void initOptions() {
        
        listaOpciones.append("Desactivar Comentarios del obturador", null);
        listaOpciones.append("Activar macro", null);
        listaOpciones.append("Modo Camara ->", null);
        listaOpciones.append("Resolucion ->", null);
        
        for (int i = 0; i < listaOpciones.size(); i++) {
            listaOpciones.setFont(i, Font.getFont(0, 0, 8));
        }

        listaOpciones.addCommand(atrasCmd);
        listaOpciones.setSelectCommand(seleccionarCmd);
        listaOpciones.setCommandListener(this);
    }

   public void crearFormatoCamara() {
        camaraForm.addCommand(opcionesCmd);
        camaraForm.addCommand(capturaCmd);
        camaraForm.addCommand(atrasCmd);
        camaraForm.setCommandListener(this);
        try {
            player = Manager.createPlayer(midlet.obtenerTipoReproductor());
            player.realize();
            player.prefetch();
            videoControl = (VideoControl) player.getControl("Control de Video");
            if (videoControl == null) {
                camaraForm.append("Control de video es nulo");
            }
            camaraForm.append((Item) videoControl.initDisplayMode(0, null));
            videoControl.setDisplayFullScreen(true);
            camara = (CameraControl) player.getControl("javax.microedition.amms.control.camera.CameraControl");
            if (player.getState() == 300) {
                player.start();
            }
        }
        catch (IOException e) {
            midlet.mostrarAlerta("Error", "IO Excepcion\n" + e.getMessage(), camaraForm);
        }
        catch (MediaException e) {
            midlet.mostrarAlerta("Error", "Media Excepcion\n" + e.getMessage(), camaraForm);
        }
        catch (NullPointerException e) {
            midlet.mostrarAlerta("Error", "Excepcion de puntero nulo +\n" + e.getMessage(), camaraForm);
        }
    }

   public void initCamaraModo() {
        camaraModoForm = new Form("Modo de Camara");
        String expModes[] = camara.getSupportedExposureModes();
        camaraModoCG = new ChoiceGroup("Modos de Camara:", 1);
        if (expModes.length == 0) {
            camaraModoCG.append("No soportado", null);
            camaraModoCG.setFont(0, Font.getFont(0, 0, 8));
        }
        else {
            for (int i = 0; i < expModes.length; i++) {
                camaraModoCG.append(expModes[i], null);
                camaraModoCG.setFont(i, Font.getFont(0, 0, 8));
            }

        }
        camaraModoForm.append(camaraModoCG);
        camaraModoForm.addCommand(seleccionarCmd);
        camaraModoForm.addCommand(atrasCmd);
        camaraModoForm.setCommandListener(this);
    }

    public void initOpcionesResolucion() {
        resolucionForm = new Form("Resoluciones");
        resolucionCG = new ChoiceGroup("Elija la resolucion:", 1);
        int stillRes[] = camara.getSupportedStillResolutions();
        if (stillRes.length == 0) {
            resolucionCG.append("No soportado", null);
            resolucionCG.setFont(0, Font.getFont(0, 0, 8));
        }
        else {
            for (int i = 0; i < stillRes.length; i += 2) {
                resolucionCG.append(String.valueOf(stillRes[i]) + "x" + String.valueOf(stillRes[i + 1]), null);
            }

        }
        resolucionForm.append(resolucionCG);
        resolucionForm.addCommand(atrasCmd);
        resolucionForm.addCommand(seleccionarCmd);
        resolucionForm.setCommandListener(this);
    }

    public void initOpcionesExposicion() {
        ajusteExposicionForm = new Form("Ajustes de exposicion");
        ajusteExposicionForm.addCommand(atrasCmd);
        ajusteExposicionForm.addCommand(seleccionarCmd);
        ajusteExposicionForm.setCommandListener(this);
    }

    /**
     * Posibles excepciones de la captura de una imagen
     * @return
     */
    public boolean capturaImagen() {
        try {
            imagenDato = videoControl.getSnapshot(null);
            imagenCapturada = Image.createImage(imagenDato, 0, imagenDato.length);
        }
        catch (MediaException exc) {
            midlet.mostrarAlerta("Error", exc.getMessage(), camaraForm);
            return false;
        }
        catch (SecurityException secExc) {
            midlet.mostrarAlerta("Error de seguridad", secExc.getMessage(), camaraForm);
            return false;
        }
        catch (NullPointerException exc) {
            midlet.mostrarAlerta("Error de puntero nulo", "Puntero nulo - videoControl no funciona ... ", camaraForm);
            return false;
        }
        catch (Exception e) {
            midlet.mostrarAlerta("Error", e.getMessage(), camaraForm);
            return false;
        }
        return true;
    }

    public void mostrarImagenCapturada() {
        crearFormatoImagen();
        if (imagenCapturada != null) {
            imageForm.append(imagenCapturada);
        }
        imageForm.setTitle("Imagen capturada");
        Camara.getDisplay().setCurrent(imageForm);
    }

    public void crearFormatoImagen() {
        imageForm = new Form("Imagen capturada");
        guardarCmd = new Command("Save", 8, 0);
        imageForm.addCommand(atrasCmd);
        imageForm.addCommand(guardarCmd);
        imageForm.setCommandListener(this);
    }

   public void commandAction(Command c, Displayable d) {
        if (c == capturaCmd) {
            if (capturaImagen()) {
                mostrarImagenCapturada();
            }
        }
        
        else if (c == seleccionarCmd && d == listaOpciones) {
            if (!listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Desactivar comentarios")) {
                if (listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Activar macro")) {
                    
                }
                else if (listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Desactivar macro")) {
                    
                }
                else if (listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Modo camara ->")) {
                    if (camara != null) {
                        initCamaraModo();
                        Camara.getDisplay().setCurrent(camaraModoForm);
                    }
                    else {
                        midlet.mostrarAlerta("Control de Camara", "Contyrol de camara no soportado.", listaOpciones);
                    }
                }
                else if (listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Resolucion ->")) {
                    if (camara != null) {
                        initOpcionesResolucion();
                        Camara.getDisplay().setCurrent(resolucionForm);
                    }
                    else {
                        midlet.mostrarAlerta("Control de camara", "Control de camara no soportado.", listaOpciones);
                    }
                }
                else if (listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Ajustes de exposicion ->")) {
                    
                }
                else if (!listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Modo flash -> ") && !listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Configuracion de Instantaneas ->")) {
                    listaOpciones.getString(listaOpciones.getSelectedIndex()).equals("Ajustes de zoom ->");
                }
            }
        }
        else if (c == atrasCmd && d != camaraForm) {
            Camara.getDisplay().setCurrent(camaraForm);
        }
        else if (c == atrasCmd) {
            Camara.getDisplay().setCurrent(midlet.openMenu);
        }
        else if (c == seleccionarCmd) {
            if (d == camaraModoForm) {
                camara.setExposureMode(camaraModoCG.getString(camaraModoCG.getSelectedIndex()));
            }
            else if (d == resolucionForm) {
                camara.setStillResolution(resolucionCG.getSelectedIndex());
            }
        }
    }
    boolean soportado;
    Camara midlet;
    Player player;
    VideoControl videoControl;
    Command capturaCmd;
    Command opcionesCmd;
    Command atrasCmd;
    Command guardarCmd;
    Command seleccionarCmd;
    Form camaraForm;
    Form imageForm;
    Form camaraModoForm;
    Form resolucionForm;
    Form ajusteExposicionForm;
    ChoiceGroup camaraModoCG;
    ChoiceGroup resolucionCG;
    List listaOpciones;
    byte imagenDato[];
    Image imagenCapturada;
    CameraControl camara;
    
}