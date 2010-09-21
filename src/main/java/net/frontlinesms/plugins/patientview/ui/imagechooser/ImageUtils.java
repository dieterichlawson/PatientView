package net.frontlinesms.plugins.patientview.ui.imagechooser;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageUtils {

	
	public static BufferedImage getImageFromByteArray(byte[] imageContent) {
	     InputStream in = new ByteArrayInputStream(imageContent);
	     try {
			return ImageIO.read(in);
	     } catch (IOException e) {
	    	 System.out.println("Error loading image from byte array");
			return null;
	     }
	 }

	 public static byte[] getByteArrayForImage(BufferedImage image, String type) {
	     ByteArrayOutputStream out = new ByteArrayOutputStream();
	     try {
			ImageIO.write(image,type,out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return out.toByteArray();
	 }
	
	/**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public static BufferedImage scale(BufferedImage img,
                                           int targetWidth,
                                           int targetHeight,
                                           Object hint,
                                           boolean higherQuality)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        
        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
    
    public static BufferedImage getThumbnailImage(BufferedImage img){
    	int height = img.getHeight() < 115? img.getHeight():115;
    	int width = (int) ((height / (double) img.getHeight()) * (double) img.getWidth());
    	boolean highQuality = img.getHeight() > 115*3;
    	return scale(img, width , height,RenderingHints.VALUE_INTERPOLATION_BILINEAR,highQuality);
    }
    
    public static BufferedImage getLargeImage(BufferedImage img){
    	int height = img.getHeight() < 650? img.getHeight():650;
    	int width = (int) ((height / (double) img.getHeight()) * (double) img.getWidth());
    	boolean highQuality = img.getHeight() > 650*2;
    	return scale(img, width , height,RenderingHints.VALUE_INTERPOLATION_BILINEAR,highQuality);
    }
}
