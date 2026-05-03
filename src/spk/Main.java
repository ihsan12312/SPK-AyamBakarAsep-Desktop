package spk;

import spk.ui.LoginFrame;
import spk.ui.UITheme;
import javax.swing.*;
import java.awt.*;

/**
 * Main.java
 * Entry point aplikasi SPK Ayam Bakar Asep – Desktop (Swing)
 * Jalankan dari NetBeans: Run Project (F6)
 */
public class Main {

    public static void main(String[] args) {
        // Premium font rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Nimbus LookAndFeel dengan overrides warna merah premium
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Override Nimbus defaults untuk tampilan bersih & modern
            UIManager.put("control",               new Color(0xF8FAFC));
            UIManager.put("nimbusLightBackground",  Color.WHITE);
            UIManager.put("nimbusBase",             new Color(0xE2E8F0)); 
            UIManager.put("nimbusFocus",            new Color(0x818CF8)); // Indigo-400 focus
            UIManager.put("nimbusSelection",        new Color(0xE0E7FF)); // Indigo-100 selection
            UIManager.put("nimbusSelectedText",     UITheme.TEXT_PRIMARY);
            UIManager.put("nimbusOrange",           new Color(0x4F46E5)); // Primary Indigo for progress bars/highlights
            UIManager.put("text",                   UITheme.TEXT_PRIMARY);
            UIManager.put("TextField.font",         UITheme.fontPlain(13));
            UIManager.put("Label.font",             UITheme.fontPlain(13));
            UIManager.put("Button.font",            UITheme.fontBold(13));
            UIManager.put("Table.font",             UITheme.fontPlain(13));
            UIManager.put("TableHeader.font",       UITheme.fontBold(11));
            UIManager.put("OptionPane.messageFont", UITheme.fontPlain(13));
            UIManager.put("OptionPane.buttonFont",  UITheme.fontBold(12));
        } catch (Exception e) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
        }

        // Launch di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
