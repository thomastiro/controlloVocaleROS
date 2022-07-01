package it.unibas.progetto.modello.utilita;

public class Target {
    private double alfa;
    private double x;
    private double y;
    private String nome;

    public Target(double alfa, double x, double y, String nome) {
        this.alfa = alfa;
        this.x = x;
        this.y = y;
        this.nome = nome;
    }

    public double getAlfa() {
        return alfa;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "nome = "+ nome+"\nx = "+ x+";\ny = "+y+";\nalfa = "+alfa+";";
    }
}
