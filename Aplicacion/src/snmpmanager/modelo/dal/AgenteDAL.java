/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snmpmanager.modelo.dal;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import snmpmanager.modelo.entidades.AgenteSNMP;

/**
 *
 * @author Dell
 */
public class AgenteDAL {

    public AgenteDAL() {
    }

    private static String obtenerNodoValor(String strTag, Element element) {
        Node nValor = (Node) element.getElementsByTagName(strTag).item(0).getFirstChild();
        if (nValor == null) {
            return "";
        } else {
            return nValor.getNodeValue() + "";
        }
    }

    public ArrayList<AgenteSNMP> obtenerAgentes(){
        ArrayList<AgenteSNMP> listaAgentes = new ArrayList<>();
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("src/snmpmanager/db/DBAgentes.xml");
            doc.normalize();
            NodeList nodosAgente = doc.getElementsByTagName("Agente");
            for (int i = 0; i < nodosAgente.getLength(); i++) {
                Node agenteNodo = nodosAgente.item(i);
                if (agenteNodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element unElemento = (Element)agenteNodo;
                    AgenteSNMP objAExtraer = new AgenteSNMP();
                    try{
                    objAExtraer.setComunidad(obtenerNodoValor("comunidad", unElemento));
                    objAExtraer.setInfoContacto(obtenerNodoValor("contacto", unElemento));
                    objAExtraer.setIp(obtenerNodoValor("ip", unElemento) + "");
                    objAExtraer.setNoInterfacesDeRed(obtenerNodoValor("interfaces", unElemento));
                    objAExtraer.setNombre(obtenerNodoValor("nombre", unElemento));
                    objAExtraer.setPuerto(obtenerNodoValor("puerto", unElemento));
                    objAExtraer.setSO(obtenerNodoValor("so", unElemento));
                    objAExtraer.setUbicacion(obtenerNodoValor("ubicacion", unElemento));
                    objAExtraer.setUltimoReinicio(obtenerNodoValor("reinicio", unElemento));
                    objAExtraer.setVersion(obtenerNodoValor("version", unElemento));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    listaAgentes.add(objAExtraer);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaAgentes;
    }
    
    public AgenteSNMP obtenerUnAgente(String ip){
        ArrayList<AgenteSNMP> listaAgentes = obtenerAgentes();
        for(AgenteSNMP j : listaAgentes){
            if (j.getIp().equals(ip)) {
                return j;
            }
        }
        return null;
    }
    
    public int obtenerUltimoIDAgente(){
        ArrayList<AgenteSNMP> listaAgentes = obtenerAgentes();
        int numeroMax = 0;
        for(AgenteSNMP j : listaAgentes){
            if (j.getId() > numeroMax) {
                numeroMax = j.getId();
            }
        }
        return numeroMax;
    }
    
    public boolean agregarAgente(AgenteSNMP agente) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse("src/snmpmanager/db/DBAgentes.xml");

            doc.getDocumentElement().normalize();
            
            NodeList items = doc.getElementsByTagName("Agente");
            for (int i = 0; i < items.getLength(); i++) {
                Element element = (Element) items.item(i);
                try {
                    if (obtenerNodoValor("ip", element).equals(agente.getIp())) {
                        System.out.println("Agente Repetido, removiendo...");
                        element.getParentNode().removeChild(element);
                        System.out.println("Removida OK");
                    }
                } catch (Exception e) {
                    System.out.println("No encuentro mas Agentes con la misma IP" + e.getMessage());
                }
            }

            System.out.println("Agregando nuevo Agente");
            Node nodoRaiz = doc.getDocumentElement();
            Element nuevoAgente = doc.createElement("Agente");
            
            Element nuevoID = doc.createElement("comunidad");
            nuevoID.setTextContent(String.valueOf(agente.getComunidad()));
            Element nuevoContact = doc.createElement("contacto");
            nuevoContact.setTextContent(agente.getInfoContacto());
            Element nuevaIP = doc.createElement("ip");
            nuevaIP.setTextContent(agente.getIp());
            Element nuevInter = doc.createElement("interfaces");
            nuevInter.setTextContent(agente.getNoInterfacesDeRed());
            Element nuevoNombre = doc.createElement("nombre");
            nuevoNombre.setTextContent(agente.getNombre());
            Element nuevPuerto = doc.createElement("puerto");
            nuevPuerto.setTextContent(agente.getPuerto());
            Element nuevSO = doc.createElement("so");
            nuevSO.setTextContent(agente.getSO());
            Element nuevUbic = doc.createElement("ubicacion");
            nuevUbic.setTextContent(agente.getUbicacion());
            Element nuevReinicio = doc.createElement("reinicio");
            nuevReinicio.setTextContent(agente.getUltimoReinicio());
            Element nuevVersion = doc.createElement("version");
            nuevVersion.setTextContent(agente.getVersion());            


            nuevoAgente.appendChild(nuevoID);
            nuevoAgente.appendChild(nuevoContact);
            nuevoAgente.appendChild(nuevaIP);
            nuevoAgente.appendChild(nuevInter);
            nuevoAgente.appendChild(nuevoNombre);
            nuevoAgente.appendChild(nuevPuerto);
            nuevoAgente.appendChild(nuevSO);
            nuevoAgente.appendChild(nuevUbic);
            nuevoAgente.appendChild(nuevReinicio);
            nuevoAgente.appendChild(nuevVersion);
            
            nodoRaiz.appendChild(nuevoAgente);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("src/snmpmanager/db/DBAgentes.xml"));
            t.transform(source, result);
            System.out.println("Agregado Agente Correctamente");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminaAgente(String ip) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse("src/snmpmanager/db/DBAgentes.xml");

            doc.getDocumentElement().normalize();

            NodeList items = doc.getElementsByTagName("Agente");
            for (int i = 0; i < items.getLength(); i++) {
                Element element = (Element) items.item(i);
                try {
                    if (obtenerNodoValor("ip", element).equals(ip)) {
                        System.out.println("Agente Encontrado, removiendo...");
                        element.getParentNode().removeChild(element);
                        System.out.println("Removida OK");
                    }
                } catch (Exception e) {
                    System.out.println("No encuentro mas Agentes con la misma IP" + e.getMessage());
                    return false;
                }
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("src/snmpmanager/db/DBAgentes.xml"));
            t.transform(source, result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
