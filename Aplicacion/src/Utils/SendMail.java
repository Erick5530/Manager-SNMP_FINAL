/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import snmpmanager.vista.ViewRendimiento;


/**
 *
 * @author Isaac
 */
public class SendMail {

    public static void enviarEmail(String correoDestinatario,
            String asunto,
            String textoCorreo) {
        try {
            Properties p = new Properties();
            p.setProperty("mail.smtp.host", "smtp.gmail.com");
            p.setProperty("mail.smtp.starttls.enable", "true");
            //La documentacion dice que se debe indicar el usuario pero no es indispensable
            //p.setProperty("mail.smtp.user", "silentghostphantom@gmail.com");
            p.setProperty("mail.smtp.port", "587");
            p.setProperty("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(p);
//            MimeMessage mensaje = new MimeMessage(session);
//            //revisar el api para el set from
//
//            mensaje.addRecipient(Message.RecipientType.TO,
//                    //new InternetAddress("tanibet.escom@gmail.com"));
//                    new InternetAddress("iserdur96@gmail.com"));
//            mensaje.setSubject(asunto);
//            mensaje.setText(textoCorreo);
        
            BodyPart texto = new MimeBodyPart();
            texto.setText(textoCorreo);

            BodyPart adjunto = new MimeBodyPart();
            adjunto.setDataHandler(new DataHandler(new FileDataSource(ViewRendimiento.pathImagen)));
            adjunto.setFileName("Grafica.gif");

            MimeMultipart multiParte = new MimeMultipart();

            multiParte.addBodyPart(texto);
            multiParte.addBodyPart(adjunto);

            MimeMessage message = new MimeMessage(session);

// Se rellena el From
            message.setFrom(new InternetAddress("erickerick.553030@gmail.com"));

// Se rellenan los destinatarios
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("erickerick.553030@gmail.com"));

// Se rellena el subject
            message.setSubject(asunto);

// Se mete el texto y la foto adjunta.
            message.setContent(multiParte);

            Transport t = session.getTransport("smtp");
            t.connect("erickerick.553030@gmail.com", "ipncecyt7");
            t.sendMessage(message, message.getAllRecipients());
            t.close();

//            Transport transporte
//                    = session.getTransport("smtp");
//            transporte.connect("iserdur96@gmail.com",
//                    "Serisaac196$");
//            transporte.sendMessage(mensaje,
//                    mensaje.getAllRecipients());
            System.out.println("Mensaje Enviado");
        } catch (AddressException ex) {
            Logger.getLogger(SendMail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(SendMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
