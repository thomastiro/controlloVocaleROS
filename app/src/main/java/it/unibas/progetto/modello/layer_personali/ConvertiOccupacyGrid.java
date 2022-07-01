package it.unibas.progetto.modello.layer_personali;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.jboss.netty.buffer.ChannelBuffer;

public class ConvertiOccupacyGrid {

    public int[] convertiInPixelArray(nav_msgs.OccupancyGrid occupancyGrid){
        ChannelBuffer buffer = occupancyGrid.getData();
        byte[] dataAll = buffer.array();
        int dataOffset = buffer.arrayOffset();
        int dataLength = buffer.readableBytes();
        byte[] data = new byte[dataLength];
        for (int i=0; i<dataLength; i++) {
            data[i] = dataAll[dataOffset + i];
        }
        // Get the pixel color, TODO: Maybe different grayscales depending on thee occupancy value
        int[] pixels = new int[dataLength];
        for (int i = 0; i < dataLength; i++) {
            // Pixels are ARGB packed ints.
            if (data[i] == -1) {
                pixels[i] = -1;
            } else if (data[i] < 50) {
                pixels[i] = 50;
            } else {
                pixels[i] = 100;
            }
        }
        return pixels;
    }
    public Bitmap convertiInBitman(nav_msgs.OccupancyGrid occupancyGrid){
        // Get the data from the occupancy grid message
        int[] pixels = convertiInPixelArray(occupancyGrid);
        // Generate Bitmap
        Bitmap mapFlipped = Bitmap.createBitmap(pixels,
                                                occupancyGrid.getInfo().getWidth(),
                                                occupancyGrid.getInfo().getHeight(),
                                                Bitmap.Config.ARGB_8888);
        // Flip Bitmap
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        return Bitmap.createBitmap(mapFlipped, 0, 0, mapFlipped.getWidth(), mapFlipped.getHeight(), matrix, true);

    }
}
