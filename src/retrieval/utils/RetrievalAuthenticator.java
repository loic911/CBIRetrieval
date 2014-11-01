/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 *
 * @author lrollus
 */
public class RetrievalAuthenticator extends Authenticator {
    // This method is called when a password-protected URL is accessed
         // Get the username from the user...
        String username;
        // Get the password from the user...
        String password;

    public RetrievalAuthenticator(String login, String pass) {
        this.username = login;
        this.password = pass;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        // Return the information
        return new PasswordAuthentication(username, password.toCharArray());
    }
}
