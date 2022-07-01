package it.unibas.progetto.modello.utilita;

public class Segmento{

    protected static double xDaEquazioneRetta(double y, CoefficientiRetta coefficientiRetta){
        return xDaEquazioneRetta(y, coefficientiRetta.getM(), coefficientiRetta.getQ());
    }

    protected static double xDaEquazioneRetta(double y, double m, double q){
        return (y-q)/m;
    }

    protected static double yDaEquazioneRetta(double x, CoefficientiRetta coefficientiRetta){
        return yDaEquazioneRetta(x, coefficientiRetta.getM(), coefficientiRetta.getQ());
    }

    protected static double yDaEquazioneRetta(double x, double m, double q){
        return m*x+q;
    }

    //// togliere da questa classe ////////////
    protected static double metriPercosi(Punto2D a, Punto2D b){
        return metriPercosi(a.getX(), a.getY(), b.getX(), b.getY());
    }

    protected static double metriPercosi(double t1, double v1, double t2, double v2){
        //System.out.println("t1: "+t1+"  v1: "+v1+"  t2: "+t2+"  v2: "+v2);
        return (v1+v2)*(t2-t1)*0.5;
    }
    ///////////////////
    protected static double metriPercorsiCompleto(double vel, double velF, double acc){
        return (vel*vel+velF*velF)/(2*acc);
    }

    public static double distanzaTraDuePunti2D(double x1, double y1, double x2, double y2){
        double diffX = x1-x2;
        double diffY = y1-y2;
        return Math.sqrt((diffX*diffX)+(diffY*diffY));
    }

    public static double velocitaMediaNsec(double x1, double y1, long t1, double x2, double y2, long t2){
        if(t1 != t2){
            return (distanzaTraDuePunti2D(x1, y1, x2, y2)/(t2-t1))*1.0E9D; //approssimarlo con Precision.round(millisecondiOriginali, 0);
        }
        return 0;
    }

    public static double angoloSegmento(double x1, double y1, double x2, double y2 ){
        double angolo = 0;
        if(x1 == x2){
            angolo = Math.PI/2;
            if(y1 > y2){
                angolo = 1.5*Math.PI;// = 3/2*Math.PI;
            }
        } else {
            if(x1 > x2){
                angolo = Math.PI;
            }
            double tan = (y2-y1)/(x2-x1);
            angolo = angolo+Math.atan(tan);
        }
        if(angolo < 0.0){
            angolo = angolo+2*Math.PI;
        }
        return angolo;
    }

}
