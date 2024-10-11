package experimental.map;

import assets.AssetMissingException;
import assets.ImageBuffer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MapData {
    public ArrayList<Entity> entitys;
    public int[][] mapArray;
    public int width, height;

    public MapData() {
//        entitys = new ArrayList<>();
//        mapArray = new int[2048][2048];
    }

    public int tile(int x, int y) {
        return mapArray[x][y];
    }

    public static int[][] tileRotate(int[][] tiles) {
        if (tiles.length == 0 || tiles[0].length == 0) return null;
        int[][] rotated = new int[tiles[0].length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                rotated[tiles.length - 1 - i][j] = tiles[j][i];
            }
        }
        return rotated;
    }

    public static void printTiles(int[][] tiles, String name) {
        if (tiles.length == 0 || tiles[0].length == 0) return;
        BufferedImage bi = new BufferedImage(tiles.length, tiles[0].length, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j] != 0) {
                    int color = getIntColor(tiles[i][j]);
                    bi.setRGB(i, j, color);
                }
            }
        }

        try {
            ImageIO.write(bi, "PNG", new File(name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getIntColor(int id) {
        try {
            float[] cc = ImageBuffer.getColor(id);
            if(cc == null) return 0;
            int r = (int) (cc[0] * 255.0);
            int g = (int) (cc[1] * 255.0);
            int b = (int) (cc[2] * 255.0);
            int a = (int) (cc[3] * 255.0);
            return a << 24 | r << 16 | g << 8 | b;
        } catch (AssetMissingException e) {
            throw new RuntimeException(e);
        }
    }
}
