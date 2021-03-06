package it.unibas.progetto.modello.layer_personali;

import androidx.core.view.GestureDetectorCompat;
///import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.google.common.base.Preconditions;

import org.ros.android.view.visualization.RotateGestureDetector;
import org.ros.android.view.visualization.VisualizationView;
import org.ros.android.view.visualization.layer.CameraControlListener;
import org.ros.android.view.visualization.layer.DefaultLayer;
import org.ros.concurrent.ListenerGroup;
import org.ros.concurrent.SignalRunnable;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMainExecutor;

/**
 * Provides gesture control of the camera for translate, rotate, and zoom.
 *
 * @author damonkohler@google.com (Damon Kohler)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public class LayerGesture extends DefaultLayer {

    private ListenerGroup<CameraControlListener> listeners;
    private GestureDetectorCompat translateGestureDetector;
    private RotateGestureDetector rotateGestureDetector;
    private ScaleGestureDetector zoomGestureDetector;

    @Override
    public void init(NodeMainExecutor nodeMainExecutor) {
        listeners = new ListenerGroup<CameraControlListener>(nodeMainExecutor.getScheduledExecutorService());
    }

    public void addListener(CameraControlListener listener) {
        Preconditions.checkNotNull(listeners);
        listeners.add(listener);
    }

    @Override
    public boolean onTouchEvent(VisualizationView view, MotionEvent event) {
        if (translateGestureDetector == null || rotateGestureDetector == null
                || zoomGestureDetector == null) {
            return false;
        }
        final boolean translateGestureHandled = translateGestureDetector.onTouchEvent(event);
        final boolean rotateGestureHandled = rotateGestureDetector.onTouchEvent(event);
        final boolean zoomGestureHandled = zoomGestureDetector.onTouchEvent(event);
        return translateGestureHandled || rotateGestureHandled || zoomGestureHandled || super.onTouchEvent(view, event);
    }

    @Override
    public void onStart(final VisualizationView view, ConnectedNode connectedNode) {
        view.post(new Runnable() {
            @Override
            public void run() {
                translateGestureDetector = new GestureDetectorCompat(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        // This must return true in order for onScroll() to trigger.
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent event1, MotionEvent event2, final float distanceX, final float distanceY) {
                        Log.e("punto onScroll", "inizio");
                        view.getCamera().translate(-distanceX, distanceY);

                        SignalRunnable<CameraControlListener> signalRunnable = new SignalRunnable<CameraControlListener>(){
                            @Override
                            public void run(CameraControlListener listener) {
                                Log.e("punto onScroll", "run");
                                listener.onTranslate(-distanceX, distanceY);
                            }
                        };
                        Log.e("punto onScroll", "Signal creato: "+ signalRunnable + " listeners: "+ listeners);
                        listeners.signal(signalRunnable);
                        Log.e("punto onScroll", "Signal usato");
                        /*
                        listeners.signal(new SignalRunnable<CameraControlListener>() {
                            @Override
                            public void run(CameraControlListener listener) {
                                listener.onTranslate(-distanceX, distanceY);
                            }
                        });*/
                        return true;
                    }

                    @Override
                    public boolean onDoubleTap (final MotionEvent e) {
                        listeners.signal(new SignalRunnable<CameraControlListener>() {
                            @Override
                            public void run(CameraControlListener listener) {
                                listener.onDoubleTap(e.getX(), e.getY());
                            }
                        });
                        return true;
                    }
                });
                rotateGestureDetector = new RotateGestureDetector(new RotateGestureDetector.OnRotateGestureListener() {
                    @Override
                    public boolean onRotate(MotionEvent event1, MotionEvent event2, final double deltaAngle) {
                        final float focusX = (event1.getX(0) + event1.getX(1)) / 2;
                        final float focusY = (event1.getY(0) + event1.getY(1)) / 2;
                        view.getCamera().rotate(focusX, focusY, deltaAngle);
                        listeners.signal(new SignalRunnable<CameraControlListener>() {
                            @Override
                            public void run(CameraControlListener listener) {
                                listener.onRotate(focusX, focusY, deltaAngle);
                            }
                        });
                        return true;
                    }
                });
                zoomGestureDetector = new ScaleGestureDetector(view.getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        if (!detector.isInProgress()) {
                            return false;
                        }
                        final float focusX = detector.getFocusX();
                        final float focusY = detector.getFocusY();
                        final float factor = detector.getScaleFactor();
                        view.getCamera().zoom(focusX, focusY, factor);
                        listeners.signal(new SignalRunnable<CameraControlListener>() {
                            @Override
                            public void run(CameraControlListener listener) {
                                listener.onZoom(focusX, focusY, factor);
                            }
                        });
                        return true;
                    }
                });
            }
        });
    }
}