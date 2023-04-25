import javax.swing.*;
import java.awt.*;

public class Button extends JButton{

	private Image img;
	
	public Button(Image img)
	{
		this.img=img;
	}
	
	public Image getImg()
	{
		return img;
	}
	
	public void setImg(Image img) 
	{
		this.img = img;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(),null);
	}
}
