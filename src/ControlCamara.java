import javax.microedition.lcdui.*;
import javax.microedition.media.MediaException;

/**
 * 
 * @author CATALINA
 *
 */
public class ControlCamara extends Control
        implements ItemStateListener {

	/**
	 * Hace un test de la camara
	 * 
	 * @param cam
	 */
    public ControlCamara(Camara cam) {
        super(cam);
        imgw = 120;
        imgh = 160;
        super.opcionesFormato.setTitle("Opciones de la Camara");
        crearFormatoCamara(super.midlet.cameracontrol);
        if (esSoportado()) {
            CrearOpcionesForm();
        }
    }

    private void CrearOpcionesForm() {
        comentario = new ChoiceGroup("Comentarios", 1);
        exposicionCG = new ChoiceGroup("Exposicion: ", 1);
        resolucionCG = new ChoiceGroup("Resolucion", 1);
        resolucionVideoCG = new ChoiceGroup("Resolucion de Video", 1);
        comentario.append("On", null);
        comentario.append("Off", null);
        if (super.cameraControl.isShutterFeedbackEnabled()) {
            comentario.setSelectedIndex(0, true);
            comentario.setSelectedIndex(1, false);
        }
        else {
            comentario.setSelectedIndex(1, true);
            comentario.setSelectedIndex(0, false);
        }
        String expModos[] = super.cameraControl.getSupportedExposureModes();
        if (expModos.length == 0) {
            exposicionCG.append("No es soportado", null);
            exposicionCG.setFont(0, Font.getFont(0, 0, 8));
        }
        else {
            for (int i = 0; i < expModos.length; i++) {
                exposicionCG.append(expModos[i], null);
                exposicionCG.setFont(i, Font.getFont(0, 0, 8));
            }

        }
        int stillResolucion[] = super.cameraControl.getSupportedStillResolutions();
        if (stillResolucion.length == 0) {
            resolucionCG.append("No es soportado", null);
            resolucionCG.setFont(0, Font.getFont(0, 0, 8));
        }
        else {
            for (int i = 0; i < stillResolucion.length; i += 2) {
                resolucionCG.append(String.valueOf(stillResolucion[i]) + "x" + String.valueOf(stillResolucion[i + 1]), null);
            }

        }
        int vidResolucion[] = super.cameraControl.getSupportedVideoResolutions();
        if (vidResolucion.length == 0) {
            resolucionVideoCG.append("No es soportado", null);
            resolucionVideoCG.setFont(0, Font.getFont(0, 0, 8));
        }
        else {
            for (int i = 0; i < vidResolucion.length; i += 2) {
                resolucionVideoCG.append(String.valueOf(vidResolucion[i]) + "x" + String.valueOf(vidResolucion[i + 1]), null);
            }

        }
        for (int i = 0; i < resolucionCG.size(); i++) {
            resolucionCG.setFont(i, Font.getFont(0, 0, 8));
        }

        for (int i = 0; i < resolucionVideoCG.size(); i++) {
            resolucionVideoCG.setFont(i, Font.getFont(0, 0, 8));
        }

        for (int i = 0; i < comentario.size(); i++) {
            comentario.setFont(i, Font.getFont(0, 0, 8));
        }

        super.opcionesFormato.setItemStateListener(this);
        super.opcionesFormato.append(comentario);
        super.opcionesFormato.append(exposicionCG);
        super.opcionesFormato.append(resolucionCG);
        super.opcionesFormato.append(resolucionVideoCG);
        super.opcionesFormato.addCommand(super.cmdBack);
        super.opcionesFormato.setCommandListener(this);
    }

   public void SaveOptions() {
        try {
            super.cameraControl.enableShutterFeedback(comentario.getSelectedIndex() == 0);
            super.cameraControl.setExposureMode(exposicionCG.getString(exposicionCG.getSelectedIndex()));
            if (!resolucionCG.getString(resolucionCG.getSelectedIndex()).equals("No es soportado")) {
                super.cameraControl.setStillResolution(resolucionCG.getSelectedIndex());
            }
            if (!resolucionVideoCG.getString(resolucionVideoCG.getSelectedIndex()).equals("No es soportado")) {
                super.cameraControl.setVideoResolution(resolucionVideoCG.getSelectedIndex());
            }
        }
        catch (MediaException e) {
            super.midlet.mostrarAlerta("Media Excepcion", "No se puede guardar la configuracion.", null);
            Camara.getDisplay().setCurrent(super.canvas);
        }
    }

   /**
    * Captura Imagen
    * @return
    */
    public boolean capturaImagen() {
        try {
            super.imagenDato = super.videoControl.getSnapshot("encoding=jpeg&width=" + imgw + "&height=" + imgh);
            super.imagenCapturada = Image.createImage(super.imagenDato, 0, super.imagenDato.length);
        }
        catch (MediaException exc) {
            super.midlet.mostrarAlerta("Error", exc.getMessage(), super.canvas);
            return false;
        }
        catch (SecurityException secExc) {
            super.midlet.mostrarAlerta("Error de seguridad", secExc.getMessage(), super.canvas);
            return false;
        }
        catch (NullPointerException exc) {
            super.midlet.mostrarAlerta("Error de puntero nulo", "Puntero nulo - videoControl no funciona ... ", super.canvas);
            return false;
        }
        catch (Exception e) {
            super.midlet.mostrarAlerta("Error", e.getMessage(), super.canvas);
            return false;
        }
        return true;
    }

    public void itemStateChanged(Item item) {
        if (item != comentario) {
            if (item == exposicionCG) {
                super.cameraControl.setExposureMode(super.cameraControl.getSupportedExposureModes()[exposicionCG.getSelectedIndex()]);
                super.midlet.mostrarAlerta("Exoposicion cambaiada", "Nuevo modo de exposicion: " + super.cameraControl.getExposureMode(), super.opcionesFormato);
            }
            else if (item == resolucionCG) {
                if (!resolucionCG.getString(resolucionCG.getSelectedIndex()).equals("No soportado")) {
                    super.cameraControl.setStillResolution(resolucionCG.getSelectedIndex() / 2);
                    String resStr = resolucionCG.getString(resolucionCG.getSelectedIndex());
                    imgh = Integer.parseInt(resStr.substring(0, resStr.indexOf("x")));
                    imgw = Integer.parseInt(resStr.substring(resStr.indexOf("x") + 1));
                    super.midlet.mostrarAlerta("Resolucion cambiada", "Nueva resolucion: " + imgh + "x" + imgw, super.opcionesFormato);
                }
            }
            else if (item == resolucionVideoCG && !resolucionVideoCG.getString(resolucionVideoCG.getSelectedIndex()).equals("No soportado")) {
                super.cameraControl.setVideoResolution(resolucionVideoCG.getSelectedIndex());
            }
        }
    }
    ChoiceGroup exposicionCG;
    ChoiceGroup resolucionCG;
    ChoiceGroup resolucionVideoCG;
    ChoiceGroup comentario;
    int imgw;
    int imgh;
}