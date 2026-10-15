package org.ezhik.authTG.captcha;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
public final class TextCaptchaRenderer extends MapRenderer {
 private final BufferedImage image;
 private boolean rendered;
 public TextCaptchaRenderer(String code) {
  image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
  Graphics2D g = image.createGraphics();
  Random random = new Random();
  g.setColor(new Color(238, 238, 220)); g.fillRect(0, 0, 128, 128);
  for (int i = 0; i < 90; i++) { g.setColor(new Color(random.nextInt(180), random.nextInt(180), random.nextInt(180))); g.drawLine(random.nextInt(128), random.nextInt(128), random.nextInt(128), random.nextInt(128)); }
  g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
  for (int i = 0; i < code.length(); i++) { g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100))); g.drawString(String.valueOf(code.charAt(i)), 13 + i * 19, 70 + random.nextInt(15) - 7); }
  g.dispose();
 }
 @Override public void render(MapView view, MapCanvas canvas, Player player) {
  if (!rendered) { canvas.drawImage(0, 0, image); rendered = true; }
 }
}
