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
public abstract class Control
        implements CommandListener, PlayerListener {

	/**
	 * Control de la Camara
	 * @param c
	 */
    public Control(Camara c) {
        snaps = 10;
        zoomlevel = 100;
        midlet = c;
        player = null;
        opcionesFormato = new Form("");
        cmdCaptura = new Command("Captura", Command.OK, 0);
        cmdOpciones = new Command("Opciones", Command.SCREEN, 0);
        cmdBack = new Command("Atras", Command.BACK, 1);
    }

    /**
     * 
     * @param supp
     */
    public void setSoportado(boolean supp) {
        suportado = supp;
        if (!suportado) {
            player.deallocate();
            player = null;
        }
    }

    /**
     * 
     * @return
     */
    public boolean esSoportado() {
        return suportado;
    }

    /**
     * 
     * @return
     */
    public boolean esInicializado() {
        return initializado;
    }

    /**
     * Crea el formulario de la camara con todas las opciones que esta soporta
     * @param key
     */
    public void crearFormatoCamara(int key) {
        id = key;
        try {
            player = Manager.createPlayer(midlet.obtenerTipoReproductor());
            player.addPlayerListener(this);
            player.realize();
            player.prefetch();
            videoControl = (VideoControl) player.getControl("VideoControl");
            if (videoControl == null) {
                midlet.mostrarAlerta("Error", "Video Control es nulo", canvas);
            }
            canvas = new Video(this, videoControl, id);
            if (id == midlet.cameracontrol) {
                cameraControl = (CameraControl) player.getControl("javax.microedition.amms.control.camera.CameraControl");
                setSoportado(cameraControl != null);
                canvas.setTitle("Prueba de control de camara");
            }
            
            else if (id == midlet.zoomcontrol) {
                zoomControl = (ZoomControl) player.getControl("javax.microedition.amms.control.camera.ZoomControl");
                setSoportado(zoomControl != null);
                canvas.setTitle("Prueba de control de zoom");
            }
            if (player.getState() == 300) {
                player.start();
            }
        }
        catch (IOException ioexception) {
        }
        catch (MediaException mediaexception) {
        }
        catch (NullPointerException nullpointerexception) {
        }
    }

    /**
     * Captura la imagen
     * @return
     */
    public boolean capturaImagen() {
        try {
            imagenDato = videoControl.getSnapshot("encoding=jpeg");
            imagenCapturada = Image.createImage(imagenDato, 0, imagenDato.length);
        }
        catch (MediaException exc) {
            midlet.mostrarAlerta("Error", exc.getMessage(), canvas);
            return false;
        }
        catch (SecurityException secExc) {
            midlet.mostrarAlerta("Error de seguridad", secExc.getMessage(), canvas);
            return false;
        }
        catch (NullPointerException exc) {
            midlet.mostrarAlerta("Error de puntero nulo", "Puntero nulo - videoControl no funciona ... ", canvas);
            return false;
        }
        catch (Exception e) {
            midlet.mostrarAlerta("Error", e.getMessage(), canvas);
            return false;
        }
        return true;
    }

    /**
     * Muestra la imagen despues de que ha sido capturada correctamente
     * @return
     */
    public boolean mostrarImagenCapturadaDespuesDeCaptura() {
        return true;
    }

    public void mostrarImagenCapturada() {
        crearFormatoImagen();
        if (imagenCapturada != null) {
            formatoImagen.append(escalaImagen(imagenCapturada, formatoImagen));
        }
        formatoImagen.setTitle("Imagen Capturada");
        Camara.getDisplay().setCurrent(formatoImagen);
    }

  
    public void crearFormatoImagen() {
        formatoImagen = new Form("Imagen capturada");
        cmdSave = new Command("Guardar", Command.OK, 0);
        formatoImagen.addCommand(cmdBack);
        formatoImagen.addCommand(cmdSave);
        formatoImagen.setCommandListener(this);
    }

    /**
     * Cambia el tamaño de la imagen 
     * 
     * @param fuentemagen a escala
     * @param nuevoAnchoImagen es el nuevo ancho de la imagen
     * @param nuevoAltoImagen is la nueva altura de la imagen
     */
    private Image escalaImagen(Image fuentemagen, int nuevoAnchoImagen, int nuevoAltoImagen) {
        try {
            int srcWidth = fuentemagen.getWidth();
            int srcHeight = fuentemagen.getHeight();

            // Una matriz RGB, con el tamaño original de la imagen)  
            int rgbSource[] = new int[srcWidth * srcHeight];
            // Una matriz  RGB, con el tamaño de la imagen a escala)  
            int rgb2Scaled[] = new int[nuevoAnchoImagen * nuevoAltoImagen];

            // Obtiene la matriz RGB de la imagen origen
            fuentemagen.getRGB(rgbSource, 0, srcWidth, 0, 0, srcWidth, srcHeight);

            int tempScaleRatioWidth;
            int tempScaleRatioHeight;

            // cálculos y operaciones de bits de desplazamiento para optimizar el bucle for
            tempScaleRatioWidth = ((srcWidth << 16) / nuevoAnchoImagen);
            tempScaleRatioHeight = ((srcHeight << 16) / nuevoAltoImagen);

            int i = 0;
            for (int y = 0; y < nuevoAltoImagen; y++) {
                for (int x = 0; x < nuevoAnchoImagen; x++) {
                    rgb2Scaled[i++] = rgbSource[(srcWidth
                            * ((y * tempScaleRatioHeight) >> 16))
                            + ((x * tempScaleRatioWidth) >> 16)];
                }
            }

            //Crea una matriz RGB imagen de la matriz rgbScaled 
            return Image.createRGBImage(rgb2Scaled,
                    nuevoAnchoImagen, nuevoAltoImagen, true);

        }
        catch (ArithmeticException e) {
            // si newImageWidth o newImageHeight es 0, devuelve el orig. image
            return fuentemagen;
        }
    }

    /**
     * 
     * @param fuenteImagen
     * @param objetivoForm
     * @return
     */
    private Image escalaImagen(Image fuenteImagen, Form objetivoForm) {

        float targetFormWidth = (float) objetivoForm.getWidth();
        float targetFormHeight = (float) objetivoForm.getHeight() * 0.85f;

        float scaleFactor1 = targetFormWidth / (float) fuenteImagen.getWidth();
        float scaleFactor2 = targetFormHeight / (float) fuenteImagen.getHeight();
        float scaleFactor = (scaleFactor1 < scaleFactor2) ? scaleFactor1 : scaleFactor2;

        int targetWidth = (int) (fuenteImagen.getWidth() * scaleFactor);
        int targetHeight = (int) (fuenteImagen.getHeight() * scaleFactor);

        return escalaImagen(fuenteImagen, targetWidth, targetHeight);

    }

    public void commandAction(Command command, Displayable display) {
        if (display.equals(opcionesFormato) && command.equals(cmdBack)) {
            SaveOptions();
            Camara.getDisplay().setCurrent(canvas);

        }
        else if (display.equals(formatoImagen) && command.equals(cmdBack)) {
            Camara.getDisplay().setCurrent(canvas);

        }
        else if (display.equals(formatoImagen) && command.equals(cmdSave)) {
            new Guardar(midlet, imagenDato, "jpeg", canvas, formatoImagen);
        }
    }

    public abstract void SaveOptions();

    public void playerUpdate(Player p, String event, Object obj) {
        switch (p.getState()) {
            case 400:
                initializado = true;
                break;
        }
    }

    public void zoomIn() {
        if (zoomControl != null) {
            if (-1001 > zoomControl.getMaxDigitalZoom()) {
                midlet.mostrarAlerta("Control de Zoom", "Cantidad maxima de zoom", canvas);
            }
            else {
                zoomlevel = zoomControl.setDigitalZoom(-1001);
            }
        }
        else {
            midlet.mostrarAlerta("Control de zoom", "Zoom No soportado.", canvas);
        }
        canvas.repaint();
    }

    protected void zoomOut() {
        if (zoomControl != null) {
            zoomlevel = zoomControl.setDigitalZoom(-1002);
        }
        else {
            midlet.mostrarAlerta("Control de zoom", "Zoom No soportado.", canvas);
        }
        canvas.repaint();
    }

    /**
     * 
     * @param s
     */
    public void setSnapshotNbr(int s) {
        snaps = s;
    }
    boolean suportado;
    Camara midlet;
    Player player;
    VideoControl videoControl;
    Command cmdCaptura;
    Command cmdOpciones;
    Command cmdBack;
    Command cmdSave;
    Canvas canvas;
    Form formatoImagen;
    Form opcionesFormato;
    byte imagenDato[];
    Image imagenCapturada;
    boolean initializado;
    int snaps;
    int zoomlevel;
    int id;
    CameraControl cameraControl;
    ZoomControl zoomControl;
    int width;
    int height;
    int zoomModes[];
}