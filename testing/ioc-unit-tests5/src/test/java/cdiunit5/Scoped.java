package cdiunit5;

import jakarta.enterprise.inject.Vetoed;


@Vetoed
public class Scoped {

    private Runnable disposeListener;

    public Scoped() {

    }

    public void setDisposedListener(Runnable disposeListener) {
        this.disposeListener = disposeListener;

    }


    public void dispose() {
        disposeListener.run();
    }
}
