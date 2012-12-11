import javax.microedition.lcdui.*;

/**
 * 
 * @author CATALINA
 *
 */
public class TestZoomControl extends Control {

	/**
	 * Prueba del control de zoom del dispositivo
	 * @param cam
	 */
    public TestZoomControl(Camara cam) {
        super(cam);
        super.opcionesFormato.setTitle("Opciones de control de zoom");
        crearFormatoCamara(super.midlet.zoomcontrol);
        if (esSoportado()) {
            CrearOpcionesFormato();
        }
    }

    public void CrearOpcionesFormato() {
        String zoomDigSuppModes[] = new String[super.zoomControl.getDigitalZoomLevels()];
        int zoomDigModesInt[] = new int[super.zoomControl.getDigitalZoomLevels()];
        super.zoomControl.setDigitalZoom(100);
        for (int i = 0; i < zoomDigSuppModes.length; i++) {
            zoomDigModesInt[i] = super.zoomControl.getDigitalZoom();
            zoomDigSuppModes[i] = ZoomToStr(zoomDigModesInt[i]);
            super.zoomControl.setDigitalZoom(-1001);
        }

        super.zoomControl.setDigitalZoom(100);
        digZoomCG = new ChoiceGroup("Nivel de zoom digital", 1);
        for (int i = 0; i < zoomDigSuppModes.length; i++) {
            if (zoomDigModesInt[i] % 100 == 0) {
                digZoomCG.append(zoomDigSuppModes[i], null);
            }
        }

        String zoomOptSuppModes[] = new String[super.zoomControl.getOpticalZoomLevels()];
        int zoomOptModesInt[] = new int[zoomOptSuppModes.length];
        super.zoomControl.setOpticalZoom(100);
        for (int i = 0; i < zoomOptSuppModes.length; i++) {
            zoomOptModesInt[i] = super.zoomControl.getOpticalZoom();
            zoomOptSuppModes[i] = ZoomToStr(zoomOptModesInt[i]);
            super.zoomControl.setOpticalZoom(-1001);
        }

        optZoomCG = new ChoiceGroup("Zoom optico ", 1, zoomOptSuppModes, null);
        String note = "***\n Para acercar y alejar entre dig. niveles de zoom en el modo de cámara, presione las teclas programables izquierda y derecha \n***";
        super.opcionesFormato.append(digZoomCG);
        super.opcionesFormato.append(optZoomCG);
        super.opcionesFormato.addCommand(super.cmdBack);
        super.opcionesFormato.append(note);
        super.opcionesFormato.setCommandListener(this);
    }

    public String ZoomToStr(int z) {
        String ret = String.valueOf(z);
        if (z % 100 == 0) {
            ret = String.valueOf(z / 100) + "x";
        }
        else {
            ret = String.valueOf(z / 100) + "." + String.valueOf(z % 100) + "x";
        }
        return ret;
    }

    public int ZoomToInt(String str) {
        int tmp = str.indexOf("x");
        return Integer.parseInt(str.substring(0, tmp)) * 100;
    }

    public void SaveOptions() {
        try {
            if (digZoomCG.size() > 0) {
                super.zoomControl.setDigitalZoom(ZoomToInt(digZoomCG.getString(digZoomCG.getSelectedIndex())));
            }
            if (optZoomCG.size() > 0) {
                super.zoomControl.setOpticalZoom(ZoomToInt(optZoomCG.getString(optZoomCG.getSelectedIndex())));
            }
            super.midlet.mostrarAlerta("Opciones guardado", "Opciones han sido guardadas.", super.canvas);
        }
        catch (IllegalArgumentException e) {
            super.midlet.mostrarAlerta("IllegalArgumentException", "No se admite el modo elejido", super.opcionesFormato);
        }
    }
    ChoiceGroup digZoomCG;
    ChoiceGroup optZoomCG;
}