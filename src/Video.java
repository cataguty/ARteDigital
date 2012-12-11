import javax.microedition.lcdui.*;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.VideoControl;

/**
 * 
 * @author CATALINA
 *
 */
public class Video extends Canvas
        implements CommandListener {

	/**
	 * Caracterisitcas de la captura de video
	 * 
	 * @param tControl
	 * @param vControl
	 * @param key
	 */
    public Video(Control tControl, VideoControl vControl, int key) {
        setFullScreenMode(false);
        testControl = tControl;
        videoControl = vControl;

        capturaCmd = new Command("Captura", Command.OK, 0);
        opcionesCmd = new Command("Opciones", Command.SCREEN, 0);
        backCmd = new Command("Atras", Command.BACK, 1);
        addCommand(capturaCmd);
        addCommand(opcionesCmd);
        addCommand(backCmd);

        setCommandListener(this);

        id = key;
        actualizar = false;
        try {
            videoControl.initDisplayMode(1, this);
            videoControl.setDisplayFullScreen(true);
        }
        catch (IllegalArgumentException e) {
            testControl.midlet.mostrarAlerta("Excepcion Argumento Ilegal", e.getMessage(), this);
        }
        catch (Exception e) {
            testControl.midlet.mostrarAlerta("Excepcion", e.getMessage(), this);
        }
        videoControl.setVisible(true);
    }

    public void setDisplay() {
        if (id != testControl.midlet.zoomcontrol) {
            setFullScreenMode(false);
            try {
                videoControl.setDisplayFullScreen(true);
            }
            catch (MediaException e) {
                e.printStackTrace();
            }
        }
    }

    protected void paint(Graphics g) {
        if (videoControl != null) {
            actualizar = false;
        }
    }

    protected void pointerPressed(int x, int y) {
        captura();
    }

    public void commandAction(Command c, Displayable d) {
        if (c.equals(backCmd)) {
            salirVideoCanvas();
        }
        else if (c.equals(capturaCmd)) {
            captura();
        }
        else if (c.equals(opcionesCmd)) {
            mostrarOpciones();
        }
    }

    protected void salirVideoCanvas() {
        if (testControl.player != null) {
            testControl.player.close();
        }
        Camara.getDisplay().setCurrent(testControl.midlet.openMenu);
    }

    protected void captura() {
        if (testControl.capturaImagen() && testControl.mostrarImagenCapturadaDespuesDeCaptura()) {
            testControl.mostrarImagenCapturada();
        }
    }

    protected void mostrarOpciones() {
        Camara.getDisplay().setCurrent(testControl.opcionesFormato);
    }

    protected void zoomIn() {
        testControl.zoomIn();
        repaint();
    }

    protected void zoomOut() {
        testControl.zoomOut();
        repaint();
    }
    Control testControl;
    VideoControl videoControl;
    int id;
    boolean actualizar;
    Command capturaCmd;
    Command opcionesCmd;
    Command backCmd;
}