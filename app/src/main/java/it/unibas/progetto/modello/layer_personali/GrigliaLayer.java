package it.unibas.progetto.modello.layer_personali;

import android.content.res.AssetManager;
import android.util.Log;

import org.ros.android.view.visualization.Color;
import org.ros.android.view.visualization.Vertices;
import org.ros.android.view.visualization.VisualizationView;
import org.ros.android.view.visualization.XYOrthographicCamera;
import org.ros.android.view.visualization.layer.DefaultLayer;
import org.ros.android.view.visualization.layer.TfLayer;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import it.unibas.progetto.Applicazione;
import it.unibas.progetto.Costanti;
import it.unibas.progetto.modello.Modello;
import it.unibas.progetto.modello.utilita.Punto2D;
import uk.co.blogspot.fractiousg.texample.GLText;

public class GrigliaLayer extends DefaultLayer implements TfLayer {
    private final String TAG = GrigliaLayer.class.getName();

    private String nomeFileTTF ="Roboto-Italic.ttf";
    private float pixelForMeter = 50;
    private float dimensioneCellaGriglia = 1.0f;
    private float spessoreLinee = 1.0f;
    private int celleAltezza = 20;
    private int celleLarghezza = 20;
    private final Color coloreGriglia;
    private final Color coloreAssi;
    private final Color coloreCerchioObbiettivo;
    private Punto2D obbiettivo=null;

    private final Lock lock;
    private GraphName frame;
    private boolean ready;
    private boolean puoiModificareReady = false;
    //private boolean inizializzati=false;
    private boolean obbiettivoAggiornato = true;
    private boolean inizializzati = true;

    private GLTextNuovo glTextNuovo;
    private List<GLTextNuovo> listaNumeriPerAssiXY=null;
    private FloatBuffer floatBufferGriglia;
    private FloatBuffer floatBufferAssi;
    private FloatBuffer floatBufferFrecceAssi;
    private FloatBuffer floatBufferObbiettivo;//=Vertices.toFloatBuffer(new float[0]);

    private int numeroVerticiGriglia = 0;
    private int numeroVerticiAssi = 0;
    private int numeroVerticiFrecce = 0;
    private int numeroVerticiObbiettivo = 0;
    private float lunghezzaAsseXGriglia = 20.0f;
    private float lunghezzaAsseYGriglia = 20.0f;
    private Modello modello;

    public GrigliaLayer(){
        this(Color.fromHexAndAlpha("a0a0a4",0.6f),Color.fromHexAndAlpha("ff0000",0.6f),Color.fromHexAndAlpha("4CAF50",0.4f),2.0f,null);
    }

    public GrigliaLayer(Color coloreGriglia, Color coloreAssi,Color coloreCerchioObbiettivo, float spessoreLinee, String nomeFileTTF) {
        this.coloreGriglia = coloreGriglia;
        this.coloreAssi = coloreAssi;
        this.coloreCerchioObbiettivo = coloreCerchioObbiettivo;
        this.lock = new ReentrantLock();
        this.ready = false;

        this.modello = Applicazione.getInstance().getModello();
        if(nomeFileTTF != null && !nomeFileTTF.trim().isEmpty()){
            this.nomeFileTTF = nomeFileTTF;
        }
        if(spessoreLinee >= 0){
            this.spessoreLinee = spessoreLinee;
        }
        try {
            Field field = XYOrthographicCamera.class.getDeclaredField("PIXELS_PER_METER");
            field.setAccessible(true);
            pixelForMeter = (float)field.getDouble(null); //è un valore private final static double
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG,"FALLITO recupero valore PIXEL_PER_METER. Di Default sarà 100."+e.getMessage());
        }
    }

    @Override
    public GraphName getFrame() {
        if(frame == null){
            frame = GraphName.of("map");
        }
        return frame;
    }

    @Override
    public void onSurfaceCreated(VisualizationView view, GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(view, gl, config);
        //inutili
        /*textShapeFactory=new TextShapeFactory(view, gl);
        textShapeFactory.loadFont("Roboto-Italic.ttf", 50,2,2);*/
        glTextNuovo = newGlTextNuovoInizializzato(gl, view, nomeFileTTF); //dove faccio il load
        Log.e(TAG,"onSurfaceCreated");
    }

    @Override
    public void draw(VisualizationView view, GL10 gl) {
        super.draw(view, gl);
        if(!ready){
            return;
        }
        //this.lock.lock();
        if(inizializzati){
            floatBufferGriglia = floatBufferLineeGriglia(celleAltezza, celleLarghezza, dimensioneCellaGriglia);
            floatBufferAssi = floatBufferAssi();
            floatBufferFrecceAssi = floatBufferFrecceAssi();
            listaNumeriPerAssiXY = listaNumeriCoordinateXY();
            inizializzati = false;
            Log.e(TAG,"BUFFER INIZIALIZZATI");
        }
        if(obbiettivoAggiornato){
            floatBufferObbiettivo = floatBufferCerchio(obbiettivo,360);
            obbiettivoAggiornato = false;
            Log.e(TAG,"BUFFER CERCHIO INIZIALIZZATO");
        }
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, floatBufferGriglia);
        coloreGriglia.apply(gl);
        gl.glLineWidth(spessoreLinee);
        gl.glDrawArrays(GL10.GL_LINES, 0,numeroVerticiGriglia);
        coloreAssi.apply(gl);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, floatBufferAssi);
        gl.glDrawArrays(GL10.GL_LINES, 0, numeroVerticiAssi);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, floatBufferFrecceAssi);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, numeroVerticiFrecce);
        coloreCerchioObbiettivo.apply(gl);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, floatBufferObbiettivo);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, numeroVerticiObbiettivo);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);//
        //textShape.draw(view,gl);
        drawNumeriCoordinateN(gl, listaNumeriPerAssiXY);
        //this.lock.unlock();
    }

    public void aggiornaObbiettivo(Punto2D obbiettivo){
        this.obbiettivo = obbiettivo;
        obbiettivoAggiornato = true;
    }

    public void aggiornaDimensioniGriglia(Integer celleAltezza,Integer celleLarghezza){
        if(celleAltezza != null){
            this.celleAltezza = celleAltezza;
        }
        if(celleLarghezza != null){
            this.celleLarghezza = celleLarghezza;
        }
        inizializzati = true;
    }

    public int getCelleAltezza(){
        return celleAltezza;
    }

    public int getCelleLarghezza(){
        return celleLarghezza;
    }

    private List<GLTextNuovo> listaNumeriCoordinateXY(){
        if(glTextNuovo == null){
            return null;
        }
        /*utilizzo un'oggetto glTextNuovo che è stato già creato ed sono state inizializzate alcune proprietà comuni.
        Per poi clonarlo(motivo della classe privata) per tutte le volte necessarie e settare i restanti valori singolarmente.
        Altrimenti, se avessi usato TextShape(inutile) o direttamente GLText, avrei dovuto
        effetturare il load("##.ttf") per ognuno, compromettendo le prestazioni.
         */
        List<GLTextNuovo> lista = new ArrayList<>();
        //per x
        for(int i = -(int)lunghezzaAsseXGriglia+1; i <= lunghezzaAsseXGriglia-1; i++){
            String sNumero = ""+i;
            /*
            TextShape nuovo = new TextShape((GLText) glTextNuovo.clone(),sNumero);
            nuovo.setColor(coloreGriglia);
            nuovo.setOffset(i*pixelForMeter,0.0f);
            lista.add(nuovo);*/
            GLTextNuovo nuovo = (GLTextNuovo)glTextNuovo.clone();
            nuovo.setOffset((float)i/*pixelForMeter*/,0.0f);
            nuovo.setText(sNumero);
            lista.add(nuovo);
        }//per y
        for(int i = -(int)lunghezzaAsseYGriglia+1; i <= lunghezzaAsseYGriglia-1; i++){
            String sNumero=""+i;
            GLTextNuovo nuovo = (GLTextNuovo)glTextNuovo.clone();
            nuovo.setOffset(0.0f, (float)i);//*pixelForMeter);
            if(i == 0){
                nuovo.setColor(coloreAssi);
            }
            nuovo.setText(sNumero);
            lista.add(nuovo);
            /*
            TextShape nuovo = new TextShape((GLText) glTextNuovo.clone(),sNumero);
            nuovo.setColor(coloreGriglia);
            nuovo.setOffset(0.0f,i*pixelForMeter);
            lista.add(nuovo);
            */
        }
        GLTextNuovo nuovo = (GLTextNuovo)glTextNuovo.clone();
        nuovo.setColor(Color.fromHexAndAlpha("0000ff",1.0f));
        nuovo.setOffset(-lunghezzaAsseXGriglia+1,-lunghezzaAsseYGriglia+1);
        nuovo.setText("THOMAS ANTONIO TIRONE");
        lista.add(nuovo);
        return lista;
    }

    private void drawNumeriCoordinateN(GL10 gl, List<GLTextNuovo> lista){
        if(lista != null){
            gl.glEnable(GL10.GL_TEXTURE_2D);
            for(GLTextNuovo t:lista){
                t.drawNuovo();
            }
            gl.glDisable(GL10.GL_TEXTURE_2D);
        }
    }

    private FloatBuffer floatBufferLineeGriglia(int numeroRighe, int numeroColonne, float dimCella){
        if(dimCella <= 0){
            return Vertices.toFloatBuffer(new float[0]);
        }
        float maxX = numeroRighe/2*dimCella;
        float maxY = numeroColonne/2*dimCella;
        float minX = -maxX;
        float minY = -maxY;
        lunghezzaAsseXGriglia = maxX+1;
        lunghezzaAsseYGriglia = maxY+1;

        int numeroVerticiX = (numeroRighe+1)*2;
        int numeroVerticiY = (numeroColonne+1)*2;
        numeroVerticiGriglia = numeroVerticiX+numeroVerticiY;
        float [] arrayGriglia = new float[numeroVerticiGriglia*2];//*3 se inserissi anche la z

        float cont = minX;
        //GRIGLIA PRIMA PARTE
        for(int i = 0; i < numeroVerticiX*2; i = i+4){//(int i=0;i<numeroVerticiX*3;i=i+6){
            arrayGriglia[i] = cont;
            arrayGriglia[i+1] = maxY;
            arrayGriglia[i+2] = cont;
            arrayGriglia[i+3] = minY;
            /*
            arrayGriglia[i]=cont;
            arrayGriglia[i+1]=maxY;
            arrayGriglia[i+2]=0.0f;
            arrayGriglia[i+3]=cont;
            arrayGriglia[i+4]=minY;
            arrayGriglia[i+5]=0.0f;
            */
            cont = cont+dimCella;
            //Log.e(TAG, "vertice 1 |X: i:"+i+ " valore:"+arrayGriglia[i]+" |Y: i:"+(i+1)+ " valore:"+arrayGriglia[i+1]+" |");
            //Log.e(TAG, "vertice 2 |X: i:"+(i+2)+ " valore:"+arrayGriglia[i+2]+" |Y: i:"+(i+3)+ " valore:"+arrayGriglia[i+3]+" |"+"\n--");
        }
        cont=minY;
        //GRIGLIA SECONDA PARTE
        for(int i = numeroVerticiX*2; i < arrayGriglia.length; i = i+4){//for(int i=numeroVerticiX*3;i<dimensioneArrayGriglia;i=i+6){ //per 3 dimensioni
            arrayGriglia[i] = maxX;
            arrayGriglia[i+1] = cont;
            arrayGriglia[i+2] = minX;
            arrayGriglia[i+3] = cont;
            /*
            arrayGriglia[i]=maxX;
            arrayGriglia[i+1]=cont;
            arrayGriglia[i+2]=0.0f;
            arrayGriglia[i+3]=minX;
            arrayGriglia[i+4]=cont;
            arrayGriglia[i+5]=0.0f;*/
            cont=cont+dimCella;
            //Log.e(TAG, "vertice 1 |X: i:"+i+ " valore:"+arrayGriglia[i]+ " |Y: i:"+(i+1)+ " valore:"+arrayGriglia[i+1]+" |");
            //Log.e(TAG, "vertice 2 |X: i:"+(i+2)+ " valore:"+arrayGriglia[i+2]+" |Y: i:"+(i+3)+ " valore:"+arrayGriglia[i+3]+" |"+"\n--");
        }
        return Vertices.toFloatBuffer(arrayGriglia);
    }

    private FloatBuffer floatBufferAssi(){
        numeroVerticiAssi = 14;
        float [] origineAssi = new float[numeroVerticiAssi*2];
        float distanzaYFreccia = lunghezzaAsseYGriglia+0.5f;
        float distanzaXfreccia = lunghezzaAsseXGriglia+0.5f;
        float dVerCen = 0.25f;
        //linee per gli assi 4 vertici
        origineAssi[0] = lunghezzaAsseXGriglia;
        origineAssi[1] = 0.0f;
        origineAssi[2] = -lunghezzaAsseXGriglia;
        origineAssi[3] = 0.0f;
        origineAssi[4] = 0.0f;
        origineAssi[5] = lunghezzaAsseYGriglia;
        origineAssi[6] = 0.0f;
        origineAssi[7] = -lunghezzaAsseYGriglia;
        //simbolo X 4 vertici
        origineAssi[8] = distanzaXfreccia-dVerCen;
        origineAssi[9] = dVerCen;
        origineAssi[10] = distanzaXfreccia+dVerCen;
        origineAssi[11] = -dVerCen;
        origineAssi[12] = distanzaXfreccia+dVerCen;
        origineAssi[13] = dVerCen;
        origineAssi[14] = distanzaXfreccia-dVerCen;
        origineAssi[15] = -dVerCen;
        //simbolo Y 6 vertici
        origineAssi[16] = -dVerCen;
        origineAssi[17] = distanzaYFreccia;
        origineAssi[18] = 0.0f;
        origineAssi[19] = distanzaYFreccia;   //quello centrale, da ripetere
        origineAssi[20] = dVerCen;
        origineAssi[21] = distanzaYFreccia+dVerCen;
        origineAssi[22] = origineAssi[18];
        origineAssi[23] = origineAssi[19];
        origineAssi[24] = dVerCen;
        origineAssi[25] = distanzaYFreccia-dVerCen;
        origineAssi[26] = origineAssi[18];
        origineAssi[27] = origineAssi[19];
        return Vertices.toFloatBuffer(origineAssi);
    }

    private FloatBuffer floatBufferFrecceAssi(){
        numeroVerticiFrecce=6;
        float [] frecceAssi = new float[numeroVerticiFrecce*2];
        float distanza = 0.2f;
        frecceAssi[0]= lunghezzaAsseXGriglia;
        frecceAssi[1]= 0.0f;
        frecceAssi[2]= lunghezzaAsseXGriglia-distanza;
        frecceAssi[3]= distanza;
        frecceAssi[4]= lunghezzaAsseXGriglia-distanza;
        frecceAssi[5]= -distanza;
        frecceAssi[6]= 0.0f;
        frecceAssi[7]= lunghezzaAsseYGriglia;
        frecceAssi[8]= -distanza;
        frecceAssi[9]= lunghezzaAsseYGriglia-distanza;
        frecceAssi[10]= distanza;
        frecceAssi[11]= lunghezzaAsseYGriglia-distanza;
        return Vertices.toFloatBuffer(frecceAssi);
    }

    private GLTextNuovo newGlTextNuovoInizializzato(GL10 gl, VisualizationView view,String nomeFileInAssets){
        GLTextNuovo glTextNuovo = new GLTextNuovo(gl, view.getContext().getAssets());
        glTextNuovo.load(nomeFileInAssets, 50,2,2);
        glTextNuovo.setColor(coloreGriglia);
        glTextNuovo.setSpace(1.0F/(float)view.getCamera().getZoom());
        glTextNuovo.setScale(1.0F/(float)view.getCamera().getZoom());
        return glTextNuovo;
    }

    private FloatBuffer floatBufferCerchio(Punto2D centroCerchio, int lati){
        if(centroCerchio == null){
            return Vertices.toFloatBuffer(new float[numeroVerticiObbiettivo = 0]);
        }
        float x = centroCerchio.getX();
        float y = centroCerchio.getY();
        float raggioCerchio = (float)Costanti.PRECISIONE_LINEARE_M;
        numeroVerticiObbiettivo = lati+2;
        int dimensioniArrayVertici = numeroVerticiObbiettivo*2; //fatto per 2 dimensioni (x,y)
        float radPPCirconferenza = (float)(2*Math.PI)/lati;
        float[] vertici = new float[dimensioniArrayVertici];
        vertici[0] = x;
        vertici[1] = y;
        int j = 1;
        for(int i = 2; i<dimensioniArrayVertici; i = i+2){
            double alfa = j*radPPCirconferenza;
            vertici[i]  = x+(raggioCerchio*(float)Math.cos(alfa));
            vertici[i+1] = y+(raggioCerchio*(float)Math.sin(alfa));
            j++;
        }
        /*
        es. lati=6
        numeroVertici=8;                        numeroVertici=8;
        numeroVertici*2=16;
        ______                                  ______
        i=2;                                    i=1
        j=1;                                    vX=cos(1)
        v[2]=cos(1)                             vY=sin(1)
        v[3]=sin(1)                             ________
        _____                                   i=2
        i=4                                     vX=cos(2)
        j=2                                       vY=sin(2)
        v[4]=cos(2)
        v[5]=sin(2)
        ------------------------------------------------------
        i=6                                     i=3
        j=3                                     vX=cos(3)
        v[6]=cos(3)                             vY=cos(3)
        v[7]=sin(3)
        --------------------------------------------------------
        i=8                                     i=4
        j=4                                     vX=cos(4)
        v[8]=cos(4)                             vY=cos(4)
        v[9]=sin(4)
        --------------------------------------------------------
        i=10                                     i=5
        j=5                                     vX=cos(5)
        v[10]=cos(5)                             vY=cos(5)
        v[11]=sin(5)
        --------------------------------------------------------
        i=12                                     i=6
        j=6                                     vX=cos(6)
        v[12]=cos(6)                             vY=cos(6)
        v[13]=sin(6)
        --------------------------------------------------------
        i=14                                     i=7
        j=7                                     vX=cos(7)
        v[14]=cos(7)                             vY=cos(7)
        v[15]=sin(7)
        --------------------------------------------------------
        stop ==16                                stop==8
        * */
        return Vertices.toFloatBuffer(vertici);
    }

    private class GLTextNuovo extends GLText implements Cloneable{
        //classe fatta per evitare di dover fare il load() del file ttf per ogni oggetto GLText ;) clonandolo dopo aver fatto il load
        private String text;
        private Float x;
        private Float y;
        private Color color;

        public GLTextNuovo(GL10 gl, AssetManager assets) {
            super(gl, assets);
        }

        public void setText(String text){
            if(text != null && !text.trim().isEmpty()){
                this.text = text;
            }
        }

        public void setOffset(Float x, Float y){
            if(x != null && y != null){
                this.x = x;
                this.y = y;
            }
        }

        private void setColor(Color color){
            if(color != null){
                this.color = color;
            }
        }

        public void drawNuovo() throws NoSuchElementException{
            if(text != null && x != null && y != null && color != null){
                begin(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                draw(text, x, y);
                end();
            } else{
                throw new NoSuchElementException();
            }
        }

        @Override
        public Object clone(){
            try{
                return super.clone();
            }catch (CloneNotSupportedException e){
                Log.e(TAG,"impossibile clonare");
                return null;
            }
        }
    }

    @Override
    public void onStart(VisualizationView view, ConnectedNode connectedNode) {
        super.onStart(view, connectedNode);
        this.ready = true;
        this.puoiModificareReady = true;
        Log.e(TAG, "GRIGLIA ON START");
    }

    public void setReady(boolean value){
        if(!puoiModificareReady){
            Log.e(TAG, "valore ready non modificato");
            return;
        }
        this.ready = value;
        Log.e(TAG, "valore ready modificato");
    }
}
