// AuthDialog.java
import javax.swing.*;

public class AuthDialog {

    private static final String USERNAME = "priyaraya";
    private static final String PASSWORD = "1008";

    public static boolean showLogin(JFrame parent) {
        JPanel panel = new JPanel();
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(12);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(12);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(userLabel); panel.add(userField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(passLabel); panel.add(passField);

        int option = JOptionPane.showConfirmDialog(parent, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            return username.equals(USERNAME) && password.equals(PASSWORD);
        }
        return false;
    }
}
