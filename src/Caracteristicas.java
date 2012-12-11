import java.io.IOException;
import java.util.Vector;
import javax.microedition.amms.control.ImageFormatControl;
import javax.microedition.amms.control.camera.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;

/**
 * 
 * @author CATALINA
 *
 */
public class Caracteristicas extends Canvas
        implements CommandListener {

    static final int MINIMUM_LINES_IN_DISPLAY = 9;

    /**
     * Caracteristicas que soporta la camara del dispositivo
     * 
     * @param cam
     */
    public Caracteristicas(Camara cam) {
        lineaSalida = 0;
        midlet = cam;
        player = null;
        back = new Command("Atras", 2, 0);
        key = new Command("Key", 8, 0);
        addCommand(key);
        addCommand(back);
        setCommandListener(this);
        black = 0;
        white = 0xffffff;
        blue = 255;
        teal = 39321;
       

        neutral = black;
        supported = teal;
        notSupported = red;

        offset = getWidth() * 0.10000000000000001D;
        subOffset = (int) (offset * 1.1000000000000001D);
        sub2Offset = (int) ((double) subOffset * 1.3D);
        initText();

        lineaAltura = (int) (2 * Font.SIZE_SMALL);
        if (System.getProperty("microedition.platform").indexOf("sw_platform=S60") > -1) {
            lineaAltura = 3 * Font.SIZE_SMALL;
        }

        lastPointerY = -1;
    }

    private void initText() {
        txt = new Vector();

        imprimirTexto(1, "--- Orientacion Camara --- ", neutral);
        imprimirResultadoPropiedadSeparado("camera.orientations");

        imprimirTexto(1, "--- Resolucuin Camara --- ", neutral);
        imprimirResultadoPropiedadSeparado("camera.resolutions");

        imprimirTexto(1, "--- Caracteristicas --- ", neutral);
        try {
            player = Manager.createPlayer(midlet.obtenerTipoReproductor());
            player.realize();
            player.prefetch();
            CaracteristicasControlCamara(player);
            player.close();
        }
        catch (IOException ioexception) {
        }
        catch (MediaException mediaexception) {
        }
    }

    /**
     * Caracteristicas del control de la camara del dispositivo
     * 
     * @param player
     */
    public void CaracteristicasControlCamara(Player player) {
        CameraControl cc = (CameraControl) player.getControl("javax.microedition.amms.control.camera.CameraControl");
        if (cc == null) {
            imprimirTexto(1, "Camara", notSupported);
        }
        else {
            imprimirTexto(1, "Camara", supported);

            int camaraRotacion = cc.getCameraRotation();
            if (camaraRotacion == CameraControl.ROTATE_LEFT) {
                imprimirTexto(2, "Rotacion de la Camara: Rotar a la Izquierda", neutral);
            }
            else if (camaraRotacion == CameraControl.ROTATE_NONE) {
                imprimirTexto(2, "Rotacion de la Camara: No rotar", neutral);
            }
            else if (camaraRotacion == CameraControl.ROTATE_RIGHT) {
                imprimirTexto(2, "Rotacion de la Camara: Rotar a la Derecha", neutral);
            }
            else if (camaraRotacion == CameraControl.UNKNOWN) {
                imprimirTexto(2, "Rotacion de la Camara: Desconocido", neutral);
            }

            String expModos[] = cc.getSupportedExposureModes();
            if (expModos.length > 0) {
                imprimirTexto(2, "Soporta modos:", neutral);
                for (int i = 0; i < expModos.length; i++) {
                    imprimirTexto(3, expModos[i], supported);
                }
            }

            int stillResolucion[] = cc.getSupportedStillResolutions();
            if (stillResolucion.length > 0) {
                imprimirTexto(2, "Resoluciones compatibles:", neutral);
                for (int i = 0; i < stillResolucion.length; i += 2) {
                    String str = String.valueOf(stillResolucion[i]) + "x" + String.valueOf(stillResolucion[i + 1]);
                    imprimirTexto(3, str, supported);
                }
            }

            int vidResolucion[] = cc.getSupportedVideoResolutions();
            if (vidResolucion.length > 0) {
                imprimirTexto(2, "Resoluciones de video:", neutral);
                for (int i = 0; i < vidResolucion.length; i += 2) {
                    String str = String.valueOf(vidResolucion[i]) + "x" + String.valueOf(vidResolucion[i + 1]);
                    imprimirTexto(3, str, supported);
                }
            }
        }
    }
   
    /**
     * Obtiene las caracterisitcas del control de la camara del dispositivo
     * @param p
     */
    public void obtenerImagenControlCaracteristicas(Player p) {
        ImageFormatControl sc = (ImageFormatControl) p.getControl("javax.microedition.amms.control.ImageFormatControl");
        Texto t = new Texto("Formato control de imagen");
        if (sc == null) {
            t.SetColor(red);
            txt.addElement(t);
        }
        else {
            t.SetColor(blue);
            txt.addElement(t);
        }
    }

    /**
     * Imprime el resultado de las propiedades de la camara
     * 
     * @param propiedad
     */
    public void imprimirResultadoPropiedadSeparado(String propiedad) {
        String resultado = System.getProperty(propiedad) + " ";
        if (resultado.indexOf(":") == -1) {
            imprimirTexto(2, "No soportado", notSupported);
        }
        else {
            while (resultado.indexOf(":") != -1) {
                String tmp = resultado.substring(0, resultado.indexOf(" "));
                resultado = resultado.substring(resultado.indexOf(" ") + 1);
                //System.out.println("tempt: " + tmp);
                //System.out.println("orient: " + result);
                imprimirTexto(2, tmp, supported);
            }

        }
    }

    /**
     * Imprime la capacidad de soporte que tiene la camara del dispositivo
     * 
     * @param capacidadSoporte
     */
    public void imprimirCapacidadSoporte(String capacidadSoporte) {
        String printableMediaCap = "j.m.a.c.c." + capacidadSoporte;
        if (System.getProperty("supports.mediacapabilities").indexOf(capacidadSoporte) != -1) {
            imprimirTexto(2, printableMediaCap, supported);
        }
        else {
            imprimirTexto(2, printableMediaCap, notSupported);
        }
    }

    /**
     * Imprime las caracterisitcas
     * 
     * @param nivelTexto
     * @param texto
     * @param color
     */
    protected void imprimirTexto(int nivelTexto, String texto, int color) {
        Texto t = new Texto(texto);
        t.SetColor(color);
        switch (nivelTexto) {
            case 2:
                t.setOffset(subOffset);
                break;
            case 3:
                t.setOffset(sub2Offset);
                break;
            case 1:
            default:
                t.setOffset(0);
                break;
        }
        txt.addElement(t);
    }

    /**
     * Zoom permitido por el dispositivo
     * 
     * @param z
     * @return
     */
    public String ZoomToStr(int z) {
        if (z % 100 == 0) {
            return String.valueOf(z / 100) + "x";
        }
        else {
            return String.valueOf(z / 100) + "." + String.valueOf(z % 100) + "x";
        }
    }

    protected void paint(Graphics g) {
        g.setColor(white);
        g.fillRect(0, 0, getWidth(), getHeight());
        Font f = Font.getFont(0, 0, Font.SIZE_SMALL);
        g.setFont(f);
        System.out.println("linestart: " + lineaSalida + ", txtsize: " + txt.size());
        for (int i = lineaSalida; i < txt.size(); i++) {
            int effectiveLinenumber = i - lineaSalida + 1;
            Texto str = (Texto) txt.elementAt(i);
            g.setColor(str.getColor());
            g.drawString(str.getTexto(), (int) (offset + (double) str.getOffset()),
                    effectiveLinenumber * lineaAltura, 68);
        }

    }

    /**
     * Puntero que esta siendo presionado
     * @param x
     * @param y
     */
    protected void pointerPressed(int x, int y) {
        lastPointerY = y;
    }

    /**
     * Puntero que fue presionado
     * @param x
     * @param y
     */
    protected void pointerReleased(int x, int y) {
        lastPointerY = -1;
    }

    /**
     * Puntero arrastrado
     * @param x
     * @param y
     */
    protected void pointerDragged(int x, int y) {
        if (lastPointerY >= 0) {
            while (lastPointerY + lineaAltura < y) {
                addToLinestart(-1);
                lastPointerY = lastPointerY + lineaAltura;
            }
            while (lastPointerY - lineaAltura > y) {
                addToLinestart(1);
                lastPointerY = lastPointerY - lineaAltura;
            }
        }
        repaint();
    }

    // Acción de apoyo a los dispositivos no táctiles
    public void keyPressed(int keycode) {
        int action = getGameAction(keycode);
        if (action == UP) {
            addToLinestart(-1);
        }
        else if (action == DOWN) {
            addToLinestart(1);
        }
        repaint();
    }

    public void keyRepeated(int keycode) {
        keyPressed(keycode);
    }
    
	/**
	 * Muestra el numero de la primera linea 
	 * @param incremento
	 */
    // Cambiar el número de la primera línea para mostrar
    private void addToLinestart(int incremento) {
        lineaSalida = lineaSalida + incremento;
        if (lineaSalida < 0) {
            lineaSalida = 0;
        }
        else if (lineaSalida > txt.size() - MINIMUM_LINES_IN_DISPLAY) {
            lineaSalida = txt.size() - MINIMUM_LINES_IN_DISPLAY;
        }
    }

   public void commandAction(Command command, Displayable displayable) {
        if (command == back) {
            if (player != null) {
                player.close();
            }
            else {
                System.out.println("No oli se player edelleen null");
            }
            Camara.getDisplay().setCurrent(midlet.openMenu);
        }
        else if (command == key) {
            midlet.mostrarAlerta("Color Key", "Green = supported\nRed = not supported", this);
        }
    }
    int red;
    int blue;
    int black;
    int white;
    int teal;
    int supported;
    int notSupported;
    int neutral;
    double offset;
    Vector txt;
    int lineaSalida;
    int subOffset;
    int sub2Offset;
    int lastPointerY;
    int lineaAltura;
    Command back;
    Command key;
    Camara midlet;
    Player player;
}