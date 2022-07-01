package it.unibas.progetto.modello.utilita;

import it.unibas.progetto.persistenza.DAOException;

public class CoefficientiRetta {
    private double m;
    private double q;

    public CoefficientiRetta(Punto2D a, Punto2D b){
        this(a.getX(), a.getY(), b.getX(), b.getY());
    }

    public CoefficientiRetta(double x1, double y1, double x2, double y2){
        if(x2 == x1){
            throw new DAOException("divisione per zero");
        }
        this.m = (y2-y1)/(x2-x1);
        this.q = y1-m*x1;
        //System.out.println("dati per coefficienti--> x1: "+x1+"  y1: "+y1+"  x2: "+x2+"  y2: "+y2);
        //System.out.println("m:"+m+" q:"+q);
    }

    public CoefficientiRetta(double m, double q) {
        this.m = m;
        this.q = q;
    }

    public double getM() {
        return m;
    }

    public double getQ() {
        return q;
    }
}
