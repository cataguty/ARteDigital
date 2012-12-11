public class Texto {

	/**
	 * 
	 * @param str
	 */
    public Texto(String str) {
        contenido = str;
        color = 0;
        xoffset = 0;
    }

    /**
     * 
     * @param c
     */
    public void SetColor(int c) {
        color = c;
    }

    /**
     * 
     * @return
     */
    public String getTexto() {
        return contenido.trim();
    }

    /**
     * 
     * @return
     */
    public int getColor() {
        return color;
    }

    /**
     * 
     * @param o
     */
    public void setOffset(int o) {
        xoffset = o;
    }

    /**
     * 
     * @return
     */
    public int getOffset() {
        return xoffset;
    }
    String contenido;
    int color;
    int xoffset;
}