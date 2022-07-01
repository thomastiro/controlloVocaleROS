package it.unibas.progetto.modello.utilita;

import java.util.Objects;

public class Punto2D {
    //creata perché le altre sono trooopo per quello che serve a me.
    private float x;
    private float y;

    public Punto2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Punto2D punto2D = (Punto2D) o;
        return Float.compare(punto2D.x, x) == 0 && Float.compare(punto2D.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
