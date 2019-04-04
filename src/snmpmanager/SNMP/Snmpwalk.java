/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snmpmanager.SNMP;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeUtils;
import org.snmp4j.util.TreeEvent;

public class Snmpwalk {
    public String targetAddr;
    public String oidStr;
    public String commStr;
    public int snmpVersion;
    public String portNum;
    public String usage;
    
    //Lista para ejercicio de uso de CPU
    public static ArrayList<Double> listaUsoCPU;    
    

    public Snmpwalk() throws IOException {
        // Set default value.
        targetAddr = "192.168.1.80";
        oidStr = ".1.3.6.1.2.1.25.3.3";
        commStr = "ASREjercicio";
        snmpVersion = SnmpConstants.version2c;
        portNum =  "161";
        usage = "snmpwalk -v2c -c ASREjercicio 192.168.1.80 .1.3.6.1.2.1.2.2.1";
    }

    public Snmpwalk(String targetAddr, String oidStr, String commStr, int snmpVersion, String portNum) {
        this.targetAddr = targetAddr;
        this.oidStr = oidStr;
        this.commStr = commStr;
        this.snmpVersion = snmpVersion;
        this.portNum = portNum;
    }
    
    

    private void execSnmpwalk() throws IOException {
        Address targetAddress = GenericAddress.parse("udp:"+ targetAddr + "/" + portNum);
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        // setting up target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(commStr));
        target.setAddress(targetAddress);
        target.setRetries(3);
        target.setTimeout(1000 * 3);
        target.setVersion(snmpVersion);
        OID oid;
        try {
            oid = new OID(translateNameToOID(oidStr));
        } catch (Exception e) {
            System.err.println("Failed to understand the OID or object name.");
            throw e;
        }

        // Get MIB data.
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, oid);
        //System.out.println("Tamaño de lista " + events.size());
        if (events == null || events.size() == 0) {
            System.out.println("No result returned.");
            System.exit(1);
        }

        // Handle the snmpwalk result.
        for (TreeEvent event : events) {
            if (event == null) {
                continue;
            }
            if (event.isError()) {
                System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                continue;
            }
            VariableBinding[] varBindings = event.getVariableBindings();
            System.out.println("Tamaño de las variables ligadas " + varBindings.length);
            if (varBindings == null || varBindings.length == 0) {
                System.out.println("Sin variables ligadas");
                continue;
            }
            for (VariableBinding varBinding : varBindings) {
                if (varBinding == null) {
                    continue;
                }
                System.out.println(
                        varBinding.getOid()
                        + " : "
                        + varBinding.getVariable().getSyntaxString()
                        + " : "
                        + varBinding.getVariable());
            }
        }
        snmp.close();
    }

    public double getCPUUsage() throws IOException{
        Address targetAddress = GenericAddress.parse("udp:" + targetAddr + "/" + portNum);
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();
        //Lista de uso de cpu
        listaUsoCPU = new ArrayList<>();

        // setting up target
        double contadorCarga = 0;
        double contadorTotal = 0;
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(commStr));
        target.setAddress(targetAddress);
        target.setRetries(3);
        target.setTimeout(500);
        target.setVersion(snmpVersion);
        OID oid;
        try {
            oid = new OID(".1.3.6.1.2.1.25.3.3");
        } catch (Exception e) {
            System.err.println("Failed to understand the OID or object name.");
            throw e;
        }

        // Get MIB data.
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, oid);
        //System.out.println("Tamaño de lista " + events.size());
        if (events == null || events.size() == 0) {
            System.out.println("No result returned.");
            System.exit(1);
        }
        // Handle the snmpwalk result.
        String in = "100", out = "100";
        try {
            for (TreeEvent event : events) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                //System.out.println("Tamaño de las variables ligadas " + varBindings.length);
                if (varBindings == null || varBindings.length == 0) {
                    System.out.println("Sin variables ligadas");
                    continue;
                }
                int contadorBinding = 0;
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    in = varBinding.getVariable().toString();
                    if (!in.equals("0.0")) {
                        contadorTotal += 100;
                        contadorCarga += Double.parseDouble(in);
                        listaUsoCPU.add(Double.parseDouble(in));
                    }
                    System.out.println(
                            varBinding.getOid()
                            + " : "
                            + varBinding.getVariable().getSyntaxString()
                            + " : "
                        + varBinding.getVariable());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (contadorCarga*100)/contadorTotal;
    }
    
    //TODO: Por resolver disco duro
    public double getHDD() throws IOException{
        Address targetAddress = GenericAddress.parse("udp:" + targetAddr + "/" + portNum);
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();
        
        
        double totalHDD = 0;
        double hddUsado = 0;
        // setting up target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(commStr));
        target.setAddress(targetAddress);
        target.setRetries(3);
        target.setTimeout(500);
        target.setVersion(snmpVersion);
        OID oid;
        try {
            oid = new OID(".1.3.6.1.2.1.25.2.3.1.5");
        } catch (Exception e) {
            System.err.println("Failed to understand the OID or object name.");
            throw e;
        }

        // Get MIB data.
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, oid);
        //System.out.println("Tamaño de lista " + events.size());
        if (events == null || events.size() == 0) {
            System.out.println("No result returned.");
            System.exit(1);
        }
        // Handle the snmpwalk result.
        
        try {
            for (TreeEvent event : events) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                //System.out.println("Tamaño de las variables ligadas " + varBindings.length);
                if (varBindings == null || varBindings.length == 0) {
                    System.out.println("Sin variables ligadas");
                    continue;
                }
                int contadorBinding = 0;
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    totalHDD += Double.parseDouble(varBinding.getVariable().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            oid = new OID(".1.3.6.1.2.1.25.2.3.1.6");
        } catch (Exception e) {
            System.err.println("Failed to understand the OID or object name.");
            throw e;
        }
        
        // Get MIB data.
        treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        events = treeUtils.getSubtree(target, oid);
        //System.out.println("Tamaño de lista " + events.size());
        if (events == null || events.size() == 0) {
            System.out.println("No result returned.");
            System.exit(1);
        }

        try {
            for (TreeEvent event : events) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                if (varBindings == null || varBindings.length == 0) {
                    System.out.println("Sin variables ligadas");
                    continue;
                }
                int contadorBinding = 0;
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    hddUsado += Double.parseDouble(varBinding.getVariable().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("TOTAL HDD " + totalHDD);
        System.out.println("USED HDD " + hddUsado);
        System.out.println("HDD " + (((hddUsado)*100)/(totalHDD)) + " %");
        return (((hddUsado)*100)/(totalHDD));
    }
    
    public double getRAM() throws IOException{
        
        Address targetAddress = GenericAddress.parse("udp:" + targetAddr + "/" + portNum);
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();
        
        double ramDisponible = 0;
        double ramUsada = 0;
        double ramTotal = 0;
        // setting up target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(commStr));
        target.setAddress(targetAddress);
        target.setRetries(3);
        target.setTimeout(500);
        target.setVersion(snmpVersion);
        OID oid;
        //Obtener la cantidad de ram disponible
        try {
            oid = new OID(".1.3.6.1.4.1.2021.4.6");
        } catch (Exception e) {
            System.err.println("Failed to understand the OID or object name.");
            throw e;
        }

        // Get MIB data.
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, oid);
        //System.out.println("Tamaño de lista " + events.size());
        if (events == null || events.size() == 0) {
            System.out.println("No result returned.");
            System.exit(1);
        }
        try {
            for (TreeEvent event : events) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                //System.out.println("Tamaño de las variables ligadas " + varBindings.length);
                if (varBindings == null || varBindings.length == 0) {
                    System.out.println("Sin variables ligadas");
                    continue;
                }
                int contadorBinding = 0;
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    ramDisponible += Double.parseDouble(varBinding.getVariable().toString());
                    System.out.println("Ram Disponible " + varBinding.getVariable().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            oid = new OID(".1.3.6.1.4.1.2021.4.5");
        } catch (Exception e) {
            System.err.println("Failed to understand the OID or object name.");
            throw e;
        }
        
        // Obtener la RAM total
        treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        events = treeUtils.getSubtree(target, oid);
        //System.out.println("Tamaño de lista " + events.size());
        if (events == null || events.size() == 0) {
            System.out.println("No result returned.");
            System.exit(1);
        }
        try {
            for (TreeEvent event : events) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                if (varBindings == null || varBindings.length == 0) {
                    System.out.println("Sin variables ligadas");
                    continue;
                }
                int contadorBinding = 0;
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    ramTotal += Double.parseDouble(varBinding.getVariable().toString());
                    System.out.println("Ram Total " + varBinding.getVariable().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ramUsada = ramTotal - ramDisponible;
        System.out.println("RAM Usada " + ramUsada);
        System.out.println("Porcentaje de Ram usada " + ((ramUsada*100)/ramTotal));
        
        return ((ramUsada*100)/ramTotal);
    }

    public ArrayList<String> getInOutOctetsFromIndex3() throws IOException {
        Address targetAddress = GenericAddress.parse("udp:" + targetAddr + "/" + portNum);
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        // setting up target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(commStr));
        target.setAddress(targetAddress);
        target.setRetries(3);
        target.setTimeout(500);
        target.setVersion(snmpVersion);
        OID oid;
        try {
            oid = new OID(translateNameToOID(oidStr));
        } catch (Exception e) {
            System.err.println("Failed to understand the OID or object name.");
            throw e;
        }

        oid = new OID("1.3.6.1.2.1.2.2.1.10");
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, oid);
        //System.out.println("Tamaño de lista " + events.size());
        if (events == null || events.size() == 0) {
            System.out.println("No result returned.");
            System.exit(1);
        }
        // Handle the snmpwalk result.
        ArrayList<String> inout = new ArrayList<>();
        String in = "100", out = "100";
        try {
            for (TreeEvent event : events) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                //System.out.println("Tamaño de las variables ligadas " + varBindings.length);
                if (varBindings == null || varBindings.length == 0) {
                    System.out.println("Sin variables ligadas");
                    continue;
                }
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    if (Double.parseDouble(varBinding.getVariable().toString()) != 0) {
                        //System.out.println("Ha salido " + varBinding.getVariable().toString());
                        in = varBinding.getVariable().toString();
                    }
                    //System.out.println("Entrado "  + varBinding.getVariable().toString());
                }
                //System.out.println("out " + varBindings[indexToReview].getVariable().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            inout.add("1000");
        }
        oid = new OID("1.3.6.1.2.1.2.2.1.16");
        events = treeUtils.getSubtree(target, oid);
        // Handle the snmpwalk result.
        try {
            for (TreeEvent event : events) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                //System.out.println("Tamaño de las variables ligadas " + varBindings.length);
                if (varBindings == null || varBindings.length == 0) {
                    System.out.println("Sin variables ligadas");
                    continue;
                }
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    if (Double.parseDouble(varBinding.getVariable().toString()) != 0) {
                        //System.out.println("Ha salido " + varBinding.getVariable().toString());
                        out = varBinding.getVariable().toString();
                    }
                    //System.out.println("Salido "  + varBinding.getVariable().toString());
                }
                //System.out.println("out " + varBindings[indexToReview].getVariable().toString());
            }
        }catch(Exception e){
            e.printStackTrace();
            inout.add("1010");
        }
        snmp.close();
        inout.add(in);
        inout.add(out);
        return inout;
    }

    public String getDevicesInitialStatus() {
        try {
            ArrayList<String> listaIndices = new ArrayList<>();
            ArrayList<String> listaNombre = new ArrayList<>();
            ArrayList<String> listaStatus = new ArrayList<>();
            Address targetAddress = GenericAddress.parse("udp:" + targetAddr + "/" + portNum);
            TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            // setting up target
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(commStr));
            target.setAddress(targetAddress);
            target.setRetries(3);
            target.setTimeout(500);
            target.setVersion(snmpVersion);
            OID oid;

            //************** OBTENER LOS DATOS DE LAS INTERFACES DE RED ********************************
            //Obteniendo El indice de interfaz del sipositivo----------------------------------
            oid = new OID("1.3.6.1.2.1.2.2.1.1");
            TreeUtils treeUtilsIndex = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> eventsIndex = treeUtilsIndex.getSubtree(target, oid);
            if (eventsIndex == null || eventsIndex.size() == 0) {
                System.out.println("No result returned.");
                System.exit(1);
            }
            // Handle the snmpwalk result.
            for (TreeEvent event : eventsIndex) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                if (varBindings == null || varBindings.length == 0) {
                    continue;
                }
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    //System.out.println(varBinding.getVariable());
                    listaIndices.add(varBinding.getVariable().toString());
                }
            }
            //------------------------ Obteniendo el nombre -------------------------------
            oid = new OID("1.3.6.1.2.1.2.2.1.2");
            TreeUtils treeUtilsName = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> eventsName = treeUtilsName.getSubtree(target, oid);
            if (eventsName == null || eventsName.size() == 0) {
                System.out.println("No result returned.");
                System.exit(1);
            }
            // Handle the snmpwalk result.
            for (TreeEvent event : eventsName) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                if (varBindings == null || varBindings.length == 0) {
                    continue;
                }
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    //System.out.println(varBinding.getVariable());
                    listaNombre.add(varBinding.getVariable().toString());
                }
            }
            //------------------------ Obteniendo el Estado -------------------------------
            oid = new OID("1.3.6.1.2.1.2.2.1.7");
            TreeUtils treeUtilsStat = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> eventsStat = treeUtilsStat.getSubtree(target, oid);
            if (eventsStat == null || eventsStat.size() == 0) {
                System.out.println("No result returned.");
                System.exit(1);
            }
            // Handle the snmpwalk result.
            for (TreeEvent event : eventsStat) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }
                VariableBinding[] varBindings = event.getVariableBindings();
                if (varBindings == null || varBindings.length == 0) {
                    continue;
                }
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    //System.out.println(varBinding.getVariable());
                    listaStatus.add(varBinding.getVariable().toString());
                }
            }
            //----------------- Acomodando todo el contenido de las listas en orden --------------------
            StringBuilder listaFinal = new StringBuilder();
            for (int i = 0; i < listaIndices.size(); i++) {
                listaFinal.append("Numero:").append(listaIndices.get(i)).append("\n>Descripcion: ").append(listaNombre.get(i)).append("\n>Estado: ").append(listaStatus.get(i)).append("\n");
            }
            System.out.println(listaFinal);
            snmp.close();
            return listaFinal.toString();
        } catch (Exception ex) {
            Logger.getLogger(Snmpwalk.class.getName()).log(Level.SEVERE, null, ex);
             return ">NO CONECTADO<";
        }
       
    }

    private String translateNameToOID(String oidStr) {
        switch (oidStr) {
            case "mib-2":
                oidStr = ".1.3.6.1.2.1";
                break;
            case "mib2":
                oidStr = ".1.3.6.1.2.1";
                break;
            case "system":
                oidStr = ".1.3.6.1.2.1.1";
                break;
            case "interfaces":
                oidStr = ".1.3.6.1.2.1.2";
                break;
            case "at":
                oidStr = ".1.3.6.1.2.1.3";
                break;
            case "ip":
                oidStr = ".1.3.6.1.2.1.4";
                break;
            case "icmp":
                oidStr = ".1.3.6.1.2.1.5";
                break;
            case "tcp":
                oidStr = ".1.3.6.1.2.1.6";
                break;
            case "udp":
                oidStr = ".1.3.6.1.2.1.7";
                break;
            case "egp":
                oidStr = ".1.3.6.1.2.1.8";
                break;
            case "transmission":
                oidStr = ".1.3.6.1.2.1.10";
                break;
            case "snmp":
                oidStr = ".1.3.6.1.2.1.11";
                break;
        }
        return oidStr;
    }

    private void setArgs(String[] args) {
        if(args.length < 2) {
            System.err.println(usage);
            System.exit(1);
        }

        for (int i=0; i<args.length; i++) {
            if("-c".equals(args[i])) {
                commStr = args[++i];
            }
            else if ("-v".equals(args[i])) {
                if(Integer.parseInt(args[++i]) == 1) {
                    snmpVersion = SnmpConstants.version1;
                }
                else {
                    snmpVersion = SnmpConstants.version2c;
                }
            }
            else if ("-p".equals(args[i])) {
                portNum = args[++i];
            }
            else{
                targetAddr = args[i++];
                oidStr = args[i];
            }
        }
        if(targetAddr == null || oidStr == null) {
            System.err.println(usage);
            System.exit(1);
        }
    }

    // Delegate main function to Snmpwalk.
    public static void main(String[] args) {
        try{
            Snmpwalk snmpwalk = new Snmpwalk();
            //snmpwalk.getDevicesInitialStatus();
            //snmpwalk.execSnmpwalk();
            //snmpwalk.getInOutOctetsFromIndex3();
            //snmpwalk.getCPUUsage();
            //snmpwalk.getHDD();
            snmpwalk.getRAM();
        }
        catch(Exception e) {
            System.err.println("----- An Exception happened as follows. Please confirm the usage etc. -----");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
