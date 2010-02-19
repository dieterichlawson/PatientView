package net.frontlinesms.plugins.patientview.ui.dialogs.imagechooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageChooser extends JPanel{
	
	private static JFileChooser fileChooser;
	private BufferedImage image;
	private String extension;
	public ImageChooser(){
		   //Set up the file chooser.
        if (fileChooser == null) {
            fileChooser = new JFileChooser();

	    //Add a custom file filter and disable the default
	    //(Accept All) file filter.
            fileChooser.addChoosableFileFilter(new ImageFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
	    //Add the preview pane.
            fileChooser.setAccessory(new ImagePreview(fileChooser));
        }

        //Show it.
        int returnVal = fileChooser.showDialog(this,
                                      "Attach");

        image = null;
        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            extension = file.getPath().substring(file.getPath().lastIndexOf(".") +1);
            System.out.println(file.getPath());
            System.out.println(file.getAbsolutePath());
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
            	System.out.println("Error loading image");
            	e.printStackTrace();
            }
        } else {
        	//user don't want nuthin
        }
        //Reset the file chooser for the next time it's shown.
        fileChooser.setSelectedFile(null);
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public String getExtension(){
		return extension;
	}
	
	
}
