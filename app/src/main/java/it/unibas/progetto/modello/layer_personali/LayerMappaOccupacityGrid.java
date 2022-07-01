package it.unibas.progetto.modello.layer_personali;

import android.graphics.Bitmap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.android.view.visualization.TextureBitmap;
import org.ros.android.view.visualization.VisualizationView;
import org.ros.android.view.visualization.layer.OccupancyGridLayer;
import org.ros.android.view.visualization.layer.SubscriberLayer;
import org.ros.android.view.visualization.layer.TfLayer;
import org.ros.internal.message.MessageBuffers;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.rosjava_geometry.Transform;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import nav_msgs.OccupancyGrid;

public class LayerMappaOccupacityGrid extends SubscriberLayer<OccupancyGrid> implements TfLayer{
    private ConvertiOccupacyGrid convertiOccupacyGrid;
    private Bitmap bitmap;
    private int[] arrayPixel;
    private final TextureBitmap textureBitmapT = new TextureBitmap();

    public LayerMappaOccupacityGrid(String topic) {
        this(GraphName.of(topic));
    }

    public LayerMappaOccupacityGrid(GraphName topic) {
        super(topic, nav_msgs.OccupancyGrid._TYPE);
        tiles = Lists.newCopyOnWriteArrayList();
        convertiOccupacyGrid = new ConvertiOccupacyGrid();
        ready = false;
    }

    /**
     * Color of occupied cells in the map.
     */
    private static final int COLOR_OCCUPIED = 0xff111111;

    /**
     * Color of free cells in the map.
     */
    private static final int COLOR_FREE = 0xffffffff;

    /**
     * Color of unknown cells in the map.
     */
    private static final int COLOR_UNKNOWN = 0xffdddddd;

    /**
     * Color of transparent cells in the map.
     */
    private static final int COLOR_TRANSPARENT = 0x00000000;

    /**
     * In order to draw maps with a size outside the maximum size of a texture,
     * we split the map into multiple tiles and draw one texture per tile.
     */
    private class Tile{

        private final ChannelBuffer pixelBuffer = MessageBuffers.dynamicBuffer();
        private final TextureBitmap textureBitmap = new TextureBitmap();

        /**
         * Resolution of the {@link nav_msgs.OccupancyGrid}.
         */
        private final float resolution;

        /**
         * Points to the top left of the {@link OccupancyGridLayer.Tile}.
         */
        private Transform origin;

        /**
         * Width of the {@link OccupancyGridLayer.Tile}.
         */
        private int stride;

        /**
         * {@code true} when the {@link OccupancyGridLayer.Tile} is ready to be drawn.
         */
        private boolean ready;

        public Tile(float resolution) {
            this.resolution = resolution;
            ready = false;
        }

        public void draw(VisualizationView view, GL10 gl) {
            if (ready) {
                textureBitmap.draw(view, gl);

            }
        }

        public void clearHandle() {
            textureBitmap.clearHandle();
        }

        public void writeInt(int value) {
            pixelBuffer.writeInt(value);
        }

        public void update() {
            Preconditions.checkNotNull(origin);
            Preconditions.checkNotNull(stride);

            textureBitmap.updateFromPixelBuffer(pixelBuffer, stride, resolution, origin, COLOR_TRANSPARENT);
            pixelBuffer.clear();
            ready = true;
        }

        public void setOrigin(Transform origin) {
            this.origin = origin;
        }

        public void setStride(int stride) {
            this.stride = stride;
        }
    }

    private final List<Tile> tiles;

    private boolean ready;
    private GraphName frame;
    private GL10 previousGl;


    @Override
    public void draw(VisualizationView view, GL10 gl) {
        if (previousGl != gl) {
            textureBitmapT.clearHandle();
            previousGl = gl;
        }
        if (ready) {
            textureBitmapT.draw(view, gl);
        }
        ready= false;
    }
    /*
    @Override
    public void draw(VisualizationView view, GL10 gl) {
        if (previousGl != gl) {
            for (LayerMappaOccupacityGrid.Tile tile : tiles) {
                tile.clearHandle();
            }
            previousGl = gl;
        }
        if (ready) {
            for (LayerMappaOccupacityGrid.Tile tile : tiles) {
                tile.draw(view, gl);
            }
        }
    }*/

    @Override
    public GraphName getFrame() {
        return frame;
    }

    @Override
    public void onStart(VisualizationView view, ConnectedNode connectedNode) {
        super.onStart(view, connectedNode);
        previousGl = null;
        getSubscriber().addMessageListener(new MessageListener<OccupancyGrid>() {
            @Override
            public void onNewMessage(nav_msgs.OccupancyGrid message) {
                update2(message);
            }
        });
    }

    private void update2(nav_msgs.OccupancyGrid message){
        final Transform origin = Transform.fromPoseMessage(message.getInfo().getOrigin());//da vedere cosa ritorna
        //bitmap = convertiOccupacyGrid.convertiInBitman(message);
        arrayPixel= convertiOccupacyGrid.convertiInPixelArray(message);
        //textureBitmapT.updateFromPixelArray(arrayPixel,TextureBitmap.STRIDE, message.getInfo().getResolution(),origin,Costanti.COLORE_TRANSPARENTE);
        frame = GraphName.of(message.getHeader().getFrameId());
        ready = true;
    }

    private void update(nav_msgs.OccupancyGrid message) {
        final float resolution = message.getInfo().getResolution();
        final int width = message.getInfo().getWidth();
        final int height = message.getInfo().getHeight();
        final int numTilesWide = (int) Math.ceil(width / (float) TextureBitmap.STRIDE);
        final int numTilesHigh = (int) Math.ceil(height / (float) TextureBitmap.STRIDE);
        final int numTiles = numTilesWide * numTilesHigh;

        final Transform origin = Transform.fromPoseMessage(message.getInfo().getOrigin()); //da vedere cosa ritorna

        while (tiles.size() < numTiles) {
            tiles.add(new Tile(resolution));
        }

        for (int y = 0; y < numTilesHigh; ++y) {
            for (int x = 0; x < numTilesWide; ++x) {
                final int tileIndex = y * numTilesWide + x;
                /*
                tiles.get(tileIndex).setOrigin(origin.multiply(new Transform(
                        new Vector3(x * resolution * TextureBitmap.STRIDE,
                                    y * resolution * TextureBitmap.HEIGHT,
                                    0.), Quaternion.identity())));

                 */
                tiles.get(tileIndex).setOrigin(origin);
                tiles.get(tileIndex).setStride(TextureBitmap.STRIDE);
            }
        }

        int x = 0;
        int y = 0;

        final ChannelBuffer buffer = message.getData();
        while (buffer.readable()) {
            Preconditions.checkState(y < height);
            final int tileIndex = (y / TextureBitmap.STRIDE) * numTilesWide + x / TextureBitmap.STRIDE;
            final byte pixel = buffer.readByte();
            if (pixel == -1) {
                tiles.get(tileIndex).writeInt(COLOR_UNKNOWN);
            } else {
                if (pixel < 50) {
                    tiles.get(tileIndex).writeInt(COLOR_FREE);
                } else {
                    tiles.get(tileIndex).writeInt(COLOR_OCCUPIED);
                }
            }

            ++x;
            if (x == width) {
                x = 0;
                ++y;
            }
        }

        for (LayerMappaOccupacityGrid.Tile tile : tiles) {
            tile.update();
        }

        frame = GraphName.of(message.getHeader().getFrameId());
        ready = true;
    }


}
