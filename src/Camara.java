import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * 
 * @author CATALINA
 *
 */
public class Camara extends MIDlet
        implements CommandListener {

	public Camara() {
        display = Display.getDisplay(this);
        
        salircmd = new Command("Salir", Command.EXIT, 0);
        selcmd = new Command("Seleccionar", Command.OK, 0);
        
        openMenu = new List("CAMARA", 3);
        openMenu.deleteAll();
        openMenu.append("Caracteristicas Compatibles", null);
        openMenu.append("Camara", null);
        openMenu.addCommand(salircmd);
        openMenu.setSelectCommand(selcmd);
        openMenu.setCommandListener(this);
        
        caracteristicas = 0;
        cameracontrol = 1;
        allcontrols = 7;
        
        System.out.println(System.getProperty("microedition.platform"));
    }

	protected void destroyApp(boolean flag)
            throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }

    protected void startApp()
            throws MIDletStateChangeException {
        display.setCurrent(openMenu);
    }

    /**
     * Obtiene la Pantalla
     * @return
     */
    public static Display getDisplay() {
        return display;
    }

    /**
     * Muestra las alertas
     * 
     * @param titulo
     * @param mensaje
     * @param siguienteDisp
     */
    public void mostrarAlerta(String titulo, String mensaje, Displayable siguienteDisp) {
        alerta = new Alert(titulo);
        alerta.setString(mensaje);
        alerta.setTimeout(-2);
        if (siguienteDisp != null) {
            getDisplay().setCurrent(alerta, siguienteDisp);
        }
        else {
            getDisplay().setCurrent(alerta);
            alerta.setCommandListener(this);
        }
    }

    /**
     * Diferentes tipos de series S40 and Symbian. El tipo de dispositivo puede
     * determinarse por la comprobación de un soporte para la salida de imagen de la cámara. 
     * Para obtener más información al respecto, consulte la página wiki siguiente:
     * http://www.developer.nokia.com/Community/Wiki/CS001259_-_Taking_a_picture_in_Java_ME
     *
     * @return "capture://image" para S40, "capture://video" para Symbian
     */
    public String obtenerTipoReproductor() {
        if (comprobarCompatibilidad()) {
            // Dispositivo Symbian 
            return "capture://video";
        }
        else {
            // Dispositivo Series 40 
            return "capture://image";
        }
    }

    /**
     * Comprobación de codificaciones para cámara instantánea. 
     * S60 y Symbian tienen apoyo png y codificaciones image / bmp mientras que la Serie 40
     * no los admite.
     * El soporte de codificación se utiliza para la determinación de la plataforma específica
     * 
     * @return
     */
    private boolean comprobarCompatibilidad() {
        String codificacion = System.getProperty("video.snapshot.encodings");
        return (codificacion != null) && (codificacion.indexOf("png") != -1) && 
                (codificacion.indexOf("image/bmp") != -1);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == salircmd) {
            notifyDestroyed();
        }
        else if (c == selcmd && d == openMenu) {
            int index = openMenu.getSelectedIndex();
            if (index == caracteristicas) {
                display.setCurrent(new Caracteristicas(this));
            }
            else if (index == cameracontrol) {
                ControlCamara testcc = new ControlCamara(this);
                if (testcc.esSoportado()) {
                    getDisplay().setCurrent(((Control) (testcc)).canvas);
                }
                else {
                    mostrarAlerta("No soporta la Camara", 
                            "Este movil no soporta el control de la camara", 
                            openMenu);
                }
            }
            
            
            else if (index == zoomcontrol) {
                TestZoomControl testzoom = new TestZoomControl(this);
                if (testzoom.esSoportado()) {
                    getDisplay().setCurrent(((Control) (testzoom)).canvas);
                }
                else {
                    mostrarAlerta("No soporta Zoom", 
                            "Este movil no soporta el control del zoom", 
                            openMenu);
                }
            }
            
            
        }
    }
    
    private static Display display = null;
    Alert alerta;
    List openMenu;
    Command salircmd;
    Command selcmd;
    int caracteristicas;
    int cameracontrol;
    int zoomcontrol;
    int allcontrols;
}